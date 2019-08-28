package com.globalm.platform.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipDrawable;
import android.support.design.chip.ChipGroup;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.BuildConfig;
import com.globalm.platform.R;
import com.globalm.platform.activities.LoginActivity;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.AccountStatusResponse;
import com.globalm.platform.models.AddSkillBody;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.Owner;
import com.globalm.platform.models.RequestBodyBuilder;
import com.globalm.platform.models.Tag;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.PicassoUtil;
import com.globalm.platform.utils.SocialUtil;
import com.globalm.platform.utils.SkillsDialogUtil;
import com.globalm.platform.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment implements View.OnClickListener {

    public static final int UPLOAD_REQUEST_CODE = 1;
    public static final int COVER_REQUEST_CODE = 2;
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private Uri imageFilePath;
    private Context mContext;
    private EditText mFieldFullName;
    private EditText mFieldPrimaryEmailAddress;
    private EditText mFieldPassword;
    private EditText mFieldConfirmPassword;
    private EditText mFieldBiography;
    private ImageView mImageUser;
    private ImageView mImageCover;
    private TextView mButtonTakePhoto;
    private TextView mButtonChangeCover;
    private TextView mButtonAddSkill;
    private Bitmap FixBitmap;

    private TextView twitterBtn;
    private TextView facebookBtn;
    private TextView googleBtn;
    private TextView twitterConnectStatus;
    private TextView facebookConnectStatus;
    private TextView googleConnectStatus;
    private ImageView twitterConnected;
    private ImageView facebookConnected;
    private ImageView googleConnected;

    private WebView webView;
    private ProgressBar webProgressBar;
    private ProgressBar profileLinearProgressBar;

    private File avatarFile;
    private File coverFile;
    private List<Tag> skills;
    private ChipGroup skillsChipGroup;
    private SkillsDialogUtil skillsDialogUtil;

    @Nullable
    private List<AccountStatusResponse.Data.Item> socialConnectionStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        skills = new ArrayList<>();
        skillsDialogUtil = new SkillsDialogUtil();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        mFieldFullName = rootView.findViewById(R.id.field_full_name);
        mFieldPrimaryEmailAddress = rootView.findViewById(R.id.field_primary_email_address);
        mFieldPassword = rootView.findViewById(R.id.field_password);
        mFieldConfirmPassword = rootView.findViewById(R.id.field_confirm_password);
        mFieldBiography = rootView.findViewById(R.id.field_biography);
        mImageUser = rootView.findViewById(R.id.image_user);
        mImageCover = rootView.findViewById(R.id.image_cover);
        mButtonTakePhoto = rootView.findViewById(R.id.button_take_photo);
        mButtonChangeCover = rootView.findViewById(R.id.button_change_cover);
        mButtonAddSkill = rootView.findViewById(R.id.button_add_new_skill);
        profileLinearProgressBar = rootView.findViewById(R.id.profile_progress_bar);


        twitterBtn = rootView.findViewById(R.id.connect_twitter_tv);
        facebookBtn = rootView.findViewById(R.id.connect_facebook_tv);
        googleBtn = rootView.findViewById(R.id.connect_google_tv);
        twitterConnectStatus = rootView.findViewById(R.id.twitter_connect_status);
        facebookConnectStatus = rootView.findViewById(R.id.facebook_connect_status);
        googleConnectStatus = rootView.findViewById(R.id.google_connect_status);
        twitterConnected = rootView.findViewById(R.id.twitter_connected_iv);
        facebookConnected = rootView.findViewById(R.id.facebook_connected_iv);
        googleConnected = rootView.findViewById(R.id.google_connected_iv);

        webView = rootView.findViewById(R.id.web_view);
        webProgressBar = rootView.findViewById(R.id.progress);

        mButtonTakePhoto.setOnClickListener(this);
        mButtonChangeCover.setOnClickListener(this);
        mButtonAddSkill.setOnClickListener(this);
        twitterBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        googleBtn.setOnClickListener(this);
        twitterConnectStatus.setOnClickListener(this);
        facebookConnectStatus.setOnClickListener(this);
        googleConnectStatus.setOnClickListener(this);
        rootView.findViewById(R.id.save_avatar_btn).setOnClickListener(this);
        rootView.findViewById(R.id.save_cover_btn).setOnClickListener(this);
        rootView.findViewById(R.id.button_upload_photo).setOnClickListener(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        skillsChipGroup = rootView.findViewById(R.id.skills_chip_group);
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_PERMISSION);
//        }
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }

        webView.getSettings().setUserAgentString(LoginActivity.USER_AGENT_FAKE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webProgressBar.setVisibility(View.GONE);
                webView.bringToFront();
                if (url.contains("/connect/ok?type=success")) {
                    setupSocialConnectionStatus();
                    webView.setVisibility(View.GONE);
                    webView.clearCache(true);
                } else if (url.contains("skip_api_login=1")) {
                    return;
                } else if (url.contains("error")) {
                    webView.setVisibility(View.GONE);
                    webView.clearCache(true);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                webProgressBar.setVisibility(View.VISIBLE);
                webProgressBar.bringToFront();
                super.onPageStarted(view, url, favicon);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setSupportZoom(false);
        setupSocialConnectionStatus();
        getProfileData();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        resetAvatarFile();
        resetCoverFile();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent gallery = new Intent();

        gallery.setType("image/*");

        gallery.setAction(Intent.ACTION_GET_CONTENT);
        switch (v.getId()) {
            case R.id.button_upload_photo:
                startActivityForResult(Intent.createChooser(gallery, "Select Image From Gallery"), UPLOAD_REQUEST_CODE);
                break;

            case R.id.button_take_photo:
                openCameraIntent();

                break;

            case R.id.button_change_cover:
                startActivityForResult(Intent.createChooser(gallery, "Select Image From Gallery"), COVER_REQUEST_CODE);
                break;
        }
        int id = v.getId();
        processSocialMediaClickListeners(id);
        processSaveClickListeners(id);
        processSkillsClickListeners(id);
    }

    private void processSkillsClickListeners(int id) {
        switch (id) {
            case R.id.button_add_new_skill: {
                skillsDialogUtil.showTagDialog(getContext(), getRequestManager(),  getDialogCallback());
                break;
            }
        }
    }

    private SkillsDialogUtil.DialogCallback<List<Tag>> getDialogCallback() {
        return new SkillsDialogUtil.DialogCallback<List<Tag>>() {
            @Override
            public void onOk(List<Tag> skills) {
                addSkills(skills);
            }

            @Override
            public void onCancel() { }

            @Override
            public void onApiFailure(Throwable error) {
                showMessage(error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void addSkills(List<Tag> skills) {
        List<Integer> mappedSkills = Utils.map(this.skills, Tag::getId);
        List<Tag> missingSkills = Utils.filter(skills, item -> !mappedSkills.contains(item.getId()));
        for (int pos = 0; pos < missingSkills.size(); pos++) {
            AddSkillBody body = new AddSkillBody(String.valueOf(missingSkills.get(pos).getId()));
            getRequestManager().addSkill(body,
                    getAddSkillCallback(pos, missingSkills.size() - 1));
        }
    }

    private CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>>>
    getAddSkillCallback(int currentPosition, int lastPos) {
        return new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>> o) {
                hideSkillsLoaderAndUpdateSkills(currentPosition, lastPos);
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(getString(R.string.error_while_adding_skill), Toast.LENGTH_LONG).show();
                hideSkillsLoaderAndUpdateSkills(currentPosition, lastPos);
            }
        };
    }

    private void hideSkillsLoaderAndUpdateSkills(int currentPosition, int lastPos) {
        if (currentPosition == lastPos) {
            getProfileData();
            showProfileProgress(false);
        }
    }

    private Chip getChip(String newChipText, int skillId) {
        if (getContext() == null) {
            return null;
        }

        Chip chip = new Chip(skillsChipGroup.getContext());
        chip.setChipDrawable(ChipDrawable.createFromResource(getContext(), R.xml.chip));
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(newChipText);
        chip.setOnCloseIconClickListener(v -> skillsChipGroup.removeView(chip));
        chip.setOnCloseIconClickListener(v ->
                getRequestManager().deleteSkill(skillId, getRemoveSkillCallback()));
        return chip;
    }

    private CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>>> getRemoveSkillCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>> o) {
                getProfileData();
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void processSocialMediaClickListeners(int id) {
        switch (id) {
            case R.id.connect_twitter_tv:
            case R.id.twitter_connect_status: {
                processConnectDisconnect(twitterConnected, AccountStatusResponse.Data.Item.TYPE_TWITTER);
                break;
            }
            case R.id.connect_facebook_tv:
            case R.id.facebook_connect_status: {
                processConnectDisconnect(facebookConnected, AccountStatusResponse.Data.Item.TYPE_FACEBOOK);
                break;
            }
            case R.id.connect_google_tv:
            case R.id.google_connect_status: {
                processConnectDisconnect(googleConnected, AccountStatusResponse.Data.Item.TYPE_GOOGLE);
                break;
            }
        }
    }

    private void processSaveClickListeners(int id) {
        switch (id) {
            case R.id.save_avatar_btn: {
                saveAvatar();
                break;
            }
            case R.id.save_cover_btn: {
                saveCover();
                break;
            }
        }
    }

    private void saveImageFile(
            File file,
            MultipartBody.Part part,
            CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Owner>>> callbackListener) {
        if (file == null) {
            return;
        }
        showProfileProgress(true);
        Map<String, RequestBody> map = new RequestBodyBuilder()
                .setMethod(RequestBodyBuilder.METHOD_PATCH)
                .build();
        getRequestManager().saveProfileImage(map, part, callbackListener);
    }

    private void saveCover() {
        if (coverFile == null) {
            return;
        }
        saveImageFile(coverFile, getCoverPart(coverFile), getCoverSaveCallback());
    }

    private void saveAvatar() {
        if (avatarFile == null) {
            return;
        }
        saveImageFile(avatarFile, getAvatarPart(avatarFile), getAvatarSaveCallback());
    }

    private MultipartBody.Part getAvatarPart(File file) {
        return Utils.getMultipartBody("image/*", file, "avatar");
    }

    private MultipartBody.Part getCoverPart(File file) {
        return Utils.getMultipartBody("image/*", file, "cover");
    }

    private CallbackListener<BaseResponseBody<
            List<Void>,
            GetContentDataModel<Owner>>> getAvatarSaveCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Owner>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<Owner>> o) {
                resetAvatarFile();
                showProfileProgress(false);
            }

            @Override
            public void onFailure(Throwable error) {
                resetAvatarFile();
                showMessage(error.getMessage(), Toast.LENGTH_LONG).show();
                showProfileProgress(false);
            }
        };
    }

    private CallbackListener<BaseResponseBody<
            List<Void>,
            GetContentDataModel<Owner>>> getCoverSaveCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Owner>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<Owner>> o) {
                resetCoverFile();
                showProfileProgress(false);
            }

            @Override
            public void onFailure(Throwable error) {
                resetCoverFile();
                showMessage(error.getMessage(), Toast.LENGTH_LONG).show();
                showProfileProgress(false);
            }
        };
    }

    private void resetAvatarFile() {
        deleteFile(avatarFile);
        avatarFile = null;
        imageFilePath = null;
    }

    private void resetCoverFile() {
        deleteFile(coverFile);
        coverFile = null;
    }

    private boolean deleteFile(File file) {
        if (file == null) {
            return true;
        }
        if (!file.exists()) {
            return true;
        }
        return file.delete();
    }

    private void processConnectDisconnect(
            ImageView socialConnectionStatus,
            @AccountStatusResponse.Data.Item.SocialType String socType) {
        if (isConnected(socialConnectionStatus)) {
            disconnect(socType);
        } else {
            connect(socType);
        }
    }

    private void connect(@AccountStatusResponse.Data.Item.SocialType String socType) {
        switch (socType) {
            case AccountStatusResponse.Data.Item.TYPE_TWITTER: {
                SocialUtil.connectSocialNetwork(
                        SocialUtil.SOCIAL_NETWORK_TWITTER,
                        getConnectSocialCallback());
                break;
            }
            case AccountStatusResponse.Data.Item.TYPE_FACEBOOK: {
                SocialUtil.connectSocialNetwork(
                        SocialUtil.SOCIAL_NETWORK_FACEBOOK,
                        getConnectSocialCallback());
                break;
            }
            case AccountStatusResponse.Data.Item.TYPE_GOOGLE: {
                SocialUtil.connectSocialNetwork(
                        SocialUtil.SOCIAL_NETWORK_GOOGLE,
                        getConnectSocialCallback());
                break;
            }
        }
    }

    private boolean isConnected(ImageView socConnectedIcon) {
        return socConnectedIcon.getVisibility() == View.VISIBLE;
    }

    private void disconnect(@AccountStatusResponse.Data.Item.SocialType String socType) {
        long id = findConnectedSocIdBySocType(socType);
        if (id == 0) return;

        getRequestManager().disconnectSocialAccount(id, processConnectionResponse());
    }

    private long findConnectedSocIdBySocType(@AccountStatusResponse.Data.Item.SocialType String socType) {
        if (socialConnectionStatus == null || socialConnectionStatus.size() == 0) {
            return 0;
        }

        for (AccountStatusResponse.Data.Item item : socialConnectionStatus) {
            if (item.getType().equals(socType)) {
                return item.getId();
            }
        }
        return 0;
    }

    private CallbackListener<BaseResponseBody<List<Void>, List<Void>>> processConnectionResponse() {
        return new CallbackListener<BaseResponseBody<List<Void>, List<Void>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, List<Void>> o) {
                setupSocialConnectionStatus();
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File imageFile = createPhotoImageFile();

            if (imageFile == null) {
                return;
            }

            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                showMessage(getString(R.string.an_error_has_occured), Toast.LENGTH_LONG).show();
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Context context = getContext();
                imageFilePath = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFile);
            } else {
                imageFilePath = Uri.fromFile(imageFile);
            }
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        } else {
            showMessage(getString(R.string.an_error_has_occured), Toast.LENGTH_SHORT).show();
        }
    }

    private File createPhotoImageFile() {
        if (getContext() == null) {
            return null;
        }

        File dir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir.getPath() + "/" + getAvatarCameraFileName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COVER_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            setCoverFromUri(data.getData());
        }

        if (requestCode == UPLOAD_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            setAvatarFromUri(data.getData());
        }

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                setAvatarFromUri(imageFilePath);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(mContext, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setImageFromUri(ImageView imageView,
                                 Uri uri,
                                 String fileName,
                                 Utils.InvokeCallback<File> callback) {
        Utils.copyFileToDir(
                uri,
                getFilesDirPath(),
                getContext(),
                fileName,
                callback);
        PicassoUtil.loadCircleImageIntoImageView(uri, imageView, R.drawable.ic_contact_selected);
    }

    private String getFileNameAvatar() {
        return Utils.SHARED_FILE_TEMP_NAME + "_AVATAR";
    }

    private String getFileNameCover() {
        return Utils.SHARED_FILE_TEMP_NAME + "_COVER";
    }

    private void setAvatarFromUri(Uri uri) {
        setImageFromUri(mImageUser, uri, getFileNameAvatar(), file -> {
            if (isFileNull(file)) {
                avatarFile = file;
            }
        });
    }

    private void setCoverFromUri(Uri uri) {
        setImageFromUri(mImageCover, uri, getFileNameCover(), file -> {
            if (isFileNull(file)) {
                coverFile = file;
            }
        });
    }

    private boolean isFileNull(File file) {
        if (file == null) {
            showMessage(getString(R.string.an_error_has_occured), Toast.LENGTH_LONG)
                    .show();
            return false;
        } else {
            return true;
        }
    }

    private String getFilesDirPath() {
        return getContext().getFilesDir().getAbsolutePath();
    }

    private void getProfileData() {
        showProfileProgress(true);
        getRequestManager().getProfileMe(getProfileCallback());
    }

    private CallbackListener<BaseResponseBody<
            List<Void>,
            GetContentDataModel<Owner>>> getProfileCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Owner>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<Owner>> o) {
                setupAccountFields(o.getData().getItem());
                showProfileProgress(false);
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(error.getMessage(), Toast.LENGTH_LONG).show();
                showProfileProgress(false);
            }
        };
    }

    private void showProfileProgress(boolean show) {
        if (show) {
            profileLinearProgressBar.setVisibility(View.VISIBLE);
        } else {
            profileLinearProgressBar.setVisibility(View.GONE);
        }
    }

    private String getAvatarCameraFileName() {
        return System.currentTimeMillis() + ".jpg";
    }

    private void setupAccountFields(Owner item) {
        mFieldFullName.setText(item.getFullName());
        mFieldPrimaryEmailAddress.setText(item.getEmail());
        if (item.getProfile() != null) {
            mFieldBiography.setText(item.getProfile().getBio());
            setupSkills(item.getProfile().getSkills());
            setupUserAvatar(item.getProfile().getThumb());
            if (item.getProfile().getCover() != null) {
                setupCover(item.getProfile().getCover().getUrl());
            }
        }
    }

    private void setupSkills(List<Tag> skills) {
        this.skills = skills;
        skillsChipGroup.removeAllViews();
        for (Tag skill : skills) {
            Chip newChip = getChip(skill.getName(), skill.getId());
            skillsChipGroup.addView(newChip);
        }
    }

    private void setupUserAvatar(String url) {
        PicassoUtil.loadCircleImageIntoImageView(
                url,
                mImageUser,
                R.drawable.ic_contact_selected);
    }

    private void setupCover(String url) {
        PicassoUtil.loadImageIntoImageView(
                url,
                mImageCover,
                R.drawable.rounded_drawable_white);
    }

    private void setupSocialConnectionStatus() {
        getRequestManager().getAccountStatus(10, 1, new CallbackListener<AccountStatusResponse>() {
            @Override
            public void onSuccess(AccountStatusResponse o) {
                resetSocialConnectonStatus();
                socialConnectionStatus = o.getData().getItems();
                processStatus(o.getData().getItems());
            }

            @Override
            public void onFailure(Throwable error) {
                resetSocialConnectonStatus();
                error.printStackTrace();
                showMessage(error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private Toast showMessage(String msg, int duration) {
        return Toast.makeText(getContext(), msg, duration);
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }

    private CallbackListener<String> getConnectSocialCallback() {
        return new CallbackListener<String>() {

            @Override
            public void onSuccess(String url) {
                webView.bringToFront();
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void processStatus(List<AccountStatusResponse.Data.Item> items) {
        for (AccountStatusResponse.Data.Item item: items) {
            String status = item.getStatus();
            if (status == null) {
                return;
            }
            setConnectionStatus(item.getType(), status);
        }
    }

    private void setConnectionStatus(
            @AccountStatusResponse.Data.Item.SocialType String type,
            String status) {
        switch (type) {
            case AccountStatusResponse.Data.Item.TYPE_TWITTER : {
                setConnectionStatusCheck(status, twitterConnected, twitterConnectStatus);
                break;
            }
            case AccountStatusResponse.Data.Item.TYPE_FACEBOOK : {
                setConnectionStatusCheck(status, facebookConnected, facebookConnectStatus);
                break;
            }
            case AccountStatusResponse.Data.Item.TYPE_GOOGLE : {
                setConnectionStatusCheck(status, googleConnected, googleConnectStatus);
                break;
            }
        }
    }

    private void setConnectionStatusCheck(
            String status,
            ImageView socConnected,
            TextView socConnectedStatus) {
        if (checkIfStatusConnected(status)) {
            setViewStatusConnected(socConnected, socConnectedStatus);
        } else {
            setViewStatusDisconnected(socConnected, socConnectedStatus);
        }
    }

    private void setViewStatusConnected(ImageView socConnected, TextView socConnectStatus) {
        socConnected.setVisibility(View.VISIBLE);
        socConnectStatus.setText(R.string.disconnect);
    }

    private void setViewStatusDisconnected(ImageView socConnected, TextView socConnectStatus) {
        socConnected.setVisibility(View.GONE);
        socConnectStatus.setText(R.string.connect);
    }

    private void resetSocialConnectonStatus() {
        twitterConnected.setVisibility(View.GONE);
        facebookConnected.setVisibility(View.GONE);
        googleConnected.setVisibility(View.GONE);
        twitterConnectStatus.setText(getString(R.string.connect));
        facebookConnectStatus.setText(getString(R.string.connect));
        googleConnectStatus.setText(getString(R.string.connect));
    }

    private boolean checkIfStatusConnected(String status) {
        return status.equals(AccountStatusResponse.Data.Item.STATUS_CONNECTED);
    }
}
