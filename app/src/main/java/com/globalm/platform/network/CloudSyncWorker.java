package com.globalm.platform.network;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.globalm.platform.R;
import com.globalm.platform.fragments.TechnicalPreferencesFragment;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.FileData;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.Item;
import com.globalm.platform.models.ItemFile;
import com.globalm.platform.models.PaginationData;
import com.globalm.platform.models.RequestBodyBuilder;
import com.globalm.platform.utils.NotificationUtil;
import com.globalm.platform.utils.Settings;
import com.globalm.platform.utils.SharedPreferencesUtil;
import com.globalm.platform.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class CloudSyncWorker extends Worker {
    private static final String SYNC_TAG = "SYNC_TAG";

    public static UUID createSyncTask() {
        clearSyncRequest();
        Constraints constraints = createTaskConstraints();
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest
                .Builder(CloudSyncWorker.class, 6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(SYNC_TAG)
                .build();
        WorkManager.getInstance().enqueue(syncRequest);
        return syncRequest.getId();
    }

    public static LiveData<WorkInfo> observeSyncWorkInfo(UUID uuid) {
        return WorkManager.getInstance().getWorkInfoByIdLiveData(uuid);
    }

    private static Constraints createTaskConstraints() {
        return new Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
    }

    public static void clearSyncRequest() {
        WorkManager.getInstance().cancelAllWorkByTag(SYNC_TAG);
    }

    public CloudSyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        setupSharedPrefs(getApplicationContext());

        if (!syncEnabled()) {
            return Result.success();
        }

        Result result;
        try {
            result = syncFiles();
        } catch (IOException e) {
            e.printStackTrace();
            result = Result.retry();
        }

        showNotificationBasedOnSyncResult(result);

        return result;
    }

    private void showNotificationBasedOnSyncResult(Result result) {
        if (result.equals(Result.success())) {
            showNotification(getApplicationContext().getString(R.string.sync_successful));
        } else if (result.equals(Result.failure())) {
            showNotification(getApplicationContext().getString(R.string.sync_failed));
        }
    }

    private void showNotification(String message) {
        NotificationUtil.showNotification(getApplicationContext(), message);
    }

    private Result syncFiles() throws IOException {
        RequestManager requestManager = getRequestManager();
        int userId = getUserId();
        if (userId == 0) {
            return Result.failure();
        }
        List<File> localFiles = getFileListFromSyncFolder();
        if (localFiles.size() == 0) {
            return Result.success();
        }

        Response<BaseResponseBody<List<Void>, PaginationData<ItemFile>>> response
                = requestManager.getUserContentList(userId);
        if (response == null || response.body() == null) {
            return Result.retry();
        }
        List<ItemFile> cloudFileItems = response.body().getData().getItems();

        List<File> syncedFiles = filterFiles(localFiles, cloudFileItems, true);
        List<File> notSyncedFiles = filterFiles(localFiles, cloudFileItems, false);
        Map<String, ItemFile> itemsMap = Utils.toMap(cloudFileItems, ItemFile::getTitle);

        boolean isUpdateSuccessful = updateSyncedFiles(syncedFiles, itemsMap);
        if (!isUpdateSuccessful) {
            return Result.retry();
        }
        boolean isUploadSuccessful = uploadNewFiles(notSyncedFiles);
        if (!isUploadSuccessful) {
            return Result.retry();
        }

        return Result.success();
    }

    private boolean uploadNewFiles(List<File> notSyncedFiles) throws IOException {
        for (File file : notSyncedFiles) {
            MultipartBody.Part body = prepareVideoFilePart(file);
            Map<String, RequestBody> partMan = new RequestBodyBuilder()
                    .setTitle(file.getName())
                    .build();
            Response<BaseResponseBody<List<Void>, GetContentDataModel<Item>>> response
                    = getRequestManager().syncedContentUpload(partMan, body);
            if (response == null || response.body() == null) {
                return false;
            }
        }

        return true;
    }

    private boolean updateSyncedFiles(List<File> localFiles, Map<String, ItemFile> cloudFileItemsMap)
            throws IOException {
        for (File file : localFiles) {
            ItemFile syncedItemFile = cloudFileItemsMap.get(file.getName());
            if (syncedItemFile == null) {
                continue;
            }
            Response<BaseResponseBody<List<Void>, GetContentDataModel<FileData>>> response
                    = getRequestManager().getSyncedFileContent(syncedItemFile.getId());
            if (response == null || response.body() == null) {
                return false;
            }

            String syncedFileHash = response.body().getData().getItem().getSource().getHash();
            String localFileHash = Utils.calculateMD5(file);

            if (syncedFileHash.equals(localFileHash)) {
                continue;
            }

            boolean syncSuccessful = syncFile(file, syncedItemFile.getId());
            if (!syncSuccessful) {
                return false;
            }
        }

        return true;
    }

    private boolean syncFile(File file, int contentId) throws IOException {

        MultipartBody.Part body = prepareVideoFilePart(file);

        Map<String, RequestBody> map = new RequestBodyBuilder()
                .setMethod(RequestBodyBuilder.METHOD_PATCH)
                .build();

        Response<BaseResponseBody<List<Void>, GetContentDataModel<Item>>> patchResponse
                = getRequestManager().syncedContentUpdate(map, body,contentId);

        return patchResponse != null && patchResponse.body() != null;
    }

    private MultipartBody.Part prepareVideoFilePart(File file) {
        MediaType type = MediaType.parse("video/*");
        RequestBody requestBody = RequestBody.create(type, file);
        return MultipartBody.Part.createFormData("source", file.getName(), requestBody);
    }

    private List<File> filterFiles(List<File> localFiles, List<ItemFile> syncedFiles, boolean syncedOnly) {
        List<String> syncedFileNames = Utils.map(syncedFiles, ItemFile::getTitle);
        return Utils.filter(localFiles, item -> {
            if (syncedOnly) {
                return syncedFileNames.contains(item.getName());
            } else {
                return !syncedFileNames.contains(item.getName());
            }
        });
    }

    private List<File> getFileListFromSyncFolder() {
        File syncFolderFile = getSyncFolderFile();

        if (!syncFolderFile.exists()) {
            return new ArrayList<>();
        }

        return Arrays.asList(syncFolderFile.listFiles());
    }

    private File getSyncFolderFile() {
        return new File(TechnicalPreferencesFragment.SYNC_FOLDER_PATH);
    }

    private boolean syncEnabled() {
        return SharedPreferencesUtil.getSyncFolderEnabledOption();
    }

    private int getUserId() {
        return SharedPreferencesUtil.getUserId();
    }

    private void setupSharedPrefs(Context appContext) {
        Settings.loadSettingsHelper(appContext);
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }
}
