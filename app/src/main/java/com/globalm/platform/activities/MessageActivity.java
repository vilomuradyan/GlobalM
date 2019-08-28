package com.globalm.platform.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.adapters.MessageAdapter;
import com.globalm.platform.adapters.MessageChatAdapter;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.MessageChatModel;
import com.globalm.platform.models.MessageModel;
import com.globalm.platform.models.Owner;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.PicassoUtil;
import com.globalm.platform.utils.Utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.globalm.platform.fragments.AccountFragment.UPLOAD_REQUEST_CODE;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int OPEN_GALLERY = 1;
    private File avatarFile;
    private ImageView mImageUser;
    private ImageView mButtonBack;
    private RecyclerView mListContacts;
    private MessageAdapter mMessageAdapter;
    private ArrayList<MessageModel> mContactsList = new ArrayList<>();

    private RecyclerView mListChat;
    private MessageChatAdapter mMessageChatAdapter;
    private ArrayList<MessageChatModel> mMessageChatModels = new ArrayList<>();

    private EmojiconEditText mFiledMessage;
    private ImageView mImageSend;
    private EmojIconActions mEmojIconActions;
    private RelativeLayout mLayoutFieldMessage;
    private ImageView mButtonSmile;
    private LinearLayout mLayoutImageButton;
    private ImageView mButtonOpenGallery;
    private String accountName = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mButtonBack = findViewById(R.id.button_back);
        mListContacts = findViewById(R.id.list_contacts);
        mListChat = findViewById(R.id.list_message_chat);
        mFiledMessage = findViewById(R.id.field_message);
        mImageSend = findViewById(R.id.image_send);
        mLayoutFieldMessage = findViewById(R.id.layout_field_message);
        mButtonSmile = findViewById(R.id.button_smile);
        mLayoutImageButton = findViewById(R.id.layout_image_bottom);
        mButtonOpenGallery = findViewById(R.id.button_open_gallery);
        mImageUser = findViewById(R.id.image_user);
        mListContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mListChat.setLayoutManager(new LinearLayoutManager(this));
        mMessageChatAdapter = new MessageChatAdapter(this);
        mMessageAdapter = new MessageAdapter(this);
        mImageSend.setOnClickListener(this);
        mButtonOpenGallery.setOnClickListener(this);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }

        mListContacts.setAdapter(mMessageAdapter);
        mListChat.setAdapter(mMessageChatAdapter);
        mButtonBack.setOnClickListener(v -> onBackPressed());
        initDummyList();
        initDummyListChat();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getProfileData();
//        mEmojIconActions = new EmojIconActions(this, mLayoutImageButton, mFiledMessage, mButtonSmile);
//        mEmojIconActions.ShowEmojIcon();
    }

    public void initDummyList() {
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mContactsList.add(new MessageModel(R.drawable.ic_contact_selected, "John Smith"));
        mMessageAdapter.setData(mContactsList);
    }

    public void initDummyListChat() {
        mMessageChatModels.add(new MessageChatModel(R.drawable.ic_contact_selected, "13:00", "Helena Roberson", "Hi there, how are you doing today?"));
        mMessageChatModels.add(new MessageChatModel(R.drawable.ic_contact_selected, "13:00", "Lester Arnold", "Hi, I am good. What about you?"));
        mMessageChatModels.add(new MessageChatModel(R.drawable.ic_contact_selected, "13:00", "Helena Roberson", "Hi there, how are you doing today?"));
        mMessageChatModels.add(new MessageChatModel(R.drawable.ic_contact_selected, "13:00", "Lester Arnold", "Hi, I am good. What about you??"));
        mMessageChatModels.add(new MessageChatModel(R.drawable.ic_contact_selected, "13:00", "Helena Roberson", "Hi there, how are you doing today?"));
        mMessageChatModels.add(new MessageChatModel(R.drawable.ic_contact_selected, "13:00", "Lester Arnold", "Hi, I am good. What about you?"));
        mMessageChatAdapter.setData(mMessageChatModels);
        mListChat.scrollToPosition(mMessageChatModels.size() - 1);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        switch (v.getId()) {
            case R.id.image_send:
                writeMessage();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
                break;
            case R.id.button_open_gallery:
                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), OPEN_GALLERY);
                break;
        }
    }
    private void writeMessage() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(date);
        if (!mFiledMessage.getText().toString().equals("")) {
            mMessageChatModels.add(new MessageChatModel(R.drawable.ic_contact_selected, formattedDate, accountName, mFiledMessage.getText().toString()));
            mMessageChatAdapter.setData(mMessageChatModels);
            mListChat.scrollToPosition(mMessageChatModels.size() - 1);
            mFiledMessage.setText("");
        }
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }

    private void getProfileData() {
        getRequestManager().getProfileMe(getProfileCallback());
    }

    private CallbackListener<BaseResponseBody<
            List<Void>,
            GetContentDataModel<Owner>>> getProfileCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Owner>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<Owner>> o) {
                setupAccountFields(o.getData().getItem());
            }

            @Override
            public void onFailure(Throwable error) {
            }
        };
    }

    private void setupAccountFields(Owner item) {
        accountName = item.getFullName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUser.setVisibility(View.VISIBLE);
            setAvatarFromUri(data.getData());

        }
    }

    private void setAvatarFromUri(Uri uri) {
        setImageFromUri(mImageUser, uri, getFileNameAvatar(), file -> {
            if (isFileNull(file)) {
                avatarFile = file;
            }
        });
    }

    private void setImageFromUri(ImageView imageView,
                                 Uri uri,
                                 String fileName,
                                 Utils.InvokeCallback<File> callback) {
        Utils.copyFileToDir(
                uri,
                getFilesDirPath(),
                this,
                fileName,
                callback);
        PicassoUtil.loadCircleImageIntoImageView(uri, imageView, R.drawable.ic_contact_selected);
    }

    private String getFileNameAvatar() {
        return Utils.SHARED_FILE_TEMP_NAME + "_AVATAR";
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
        return getFilesDir().getAbsolutePath();
    }

    private Toast showMessage(String msg, int duration) {
        return Toast.makeText(this, msg, duration);
    }
}
