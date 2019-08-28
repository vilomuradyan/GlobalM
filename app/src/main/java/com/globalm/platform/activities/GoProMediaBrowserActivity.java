package com.globalm.platform.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.adapters.GoProBrowserAdapter;
import com.globalm.platform.fragments.UploadContentFragment;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.gopro.hero4.GoProVideoDataContainer;
import com.globalm.platform.models.gopro.hero4.MediaResponse;
import com.globalm.platform.network.GoProRequestManager;
import com.globalm.platform.utils.GoProUtil;

import java.io.File;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;

public class GoProMediaBrowserActivity extends BaseActivity {

    public static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), GoProMediaBrowserActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    private ImageView backButton;
    private GoProBrowserAdapter adapter;
    private RecyclerView videosRecycler;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gopro_media_browser);
        setStatusBarColor();

        backButton = findViewById(R.id.button_back);
        videosRecycler = findViewById(R.id.videos_recycler);
        mProgressDialog = new ProgressDialog(this);
        setupView();

        showProgressDialog(R.string.loading_camera_files_list);
        GoProRequestManager.getInstance().getFilesList(new CallbackListener<MediaResponse>() {
            @Override
            public void onSuccess(MediaResponse mediaResponse) {
                hideProgressDialog();
                adapter.setVideoContainerList(GoProUtil.buildVideoDataContainers(mediaResponse));
            }

            @Override
            public void onFailure(Throwable error) {
                hideProgressDialog();
                if (error instanceof SocketTimeoutException) {
                    showMessage(getString(R.string.unable_to_connect_to_camera)).show();
                } else {
                    showMessage(error.getMessage()).show();
                }
            }
        });
    }

    private void setupView() {
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        adapter = new GoProBrowserAdapter(this::downloadVideoFile);
        videosRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        videosRecycler.setAdapter(adapter);
    }

    private Toast showMessage(String message) {
        return Toast.makeText(this, message, Toast.LENGTH_SHORT);
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    private void showProgressDialog(@StringRes int messageid) {
        mProgressDialog.show();
        mProgressDialog.setMessage(getString(messageid));
    }

    private void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_blue));
    }

    private void downloadVideoFile(GoProVideoDataContainer videoDataContainer) {
        showProgressDialog(R.string.please_wait);
        GoProRequestManager
                .getInstance()
                .getVideoResponseBody(
                        GoProUtil.buildVideoDownloadUrl(
                                videoDataContainer.getFolder(),
                                videoDataContainer.getFileName()
                        ),
                        getDownloadFileCallback()
                );
    }

    private CallbackListener<ResponseBody> getDownloadFileCallback() {
        return new CallbackListener<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                File file = GoProUtil.saveFile(GoProMediaBrowserActivity.this, responseBody);
                hideProgressDialog();
                if (file != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(
                            UploadContentFragment.KEY_GOPRO_CAMERA_FILE_PATH,
                            file.getAbsolutePath());
                    setResult(RESULT_OK, resultIntent);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }

            @Override
            public void onFailure(Throwable error) {
                hideProgressDialog();
                showMessage(error.getMessage()).show();
            }
        };
    }
}