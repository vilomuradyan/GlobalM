package com.globalm.platform.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.FlowLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.globalm.platform.R;
import com.globalm.platform.adapters.ChatAdapter;
import com.globalm.platform.adapters.DateTimeZoneAdapter;
import com.globalm.platform.broadcaster.ILiveVideoBroadcaster;
import com.globalm.platform.broadcaster.LiveVideoBroadcaster;
import com.globalm.platform.broadcaster.utils.Resolution;
import com.globalm.platform.fragments.CameraResolutionsFragment;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.listeners.OnDateTimeZoneSelectedListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.ChatModel;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.Owner;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.CustomVerticalSeekbar;
import com.globalm.platform.utils.OrientationManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.globalm.platform.utils.Settings.loadBoolean;
import static com.globalm.platform.utils.Settings.loadInt;
import static com.globalm.platform.utils.Settings.loadIntCameraSizeHeight;
import static com.globalm.platform.utils.Settings.loadIntCameraSizeWidth;
import static com.globalm.platform.utils.Settings.loadStringCameraSize;
import static com.globalm.platform.utils.Settings.saveBoolean;
import static com.globalm.platform.utils.Settings.saveStringCameraSize;
import static com.globalm.platform.utils.Utils.getTimeZoneList;

public class CameraActivity extends BaseActivity implements View.OnClickListener, OrientationManager.OrientationListener, OnDateTimeZoneSelectedListener {

    public static final String RTMP_BASE_URL = "rtmp://stream-dev.goglobalm.com:1935/live/";
    private static final String CAMERA_RESOLUTIONS = "CAMERA_RESOLUTIONS";
    private static final String SELECTED_SIZE_WIDTH = "SELECTED_SIZE_WIDTH";
    private static final String SELECTED_SIZE_HEIGHT = "SELECTED_SIZE_HEIGHT";
    private static final int TYPE_TIMEZONE = 202033;
    private static final int MAX_VOLUME_VALUE = 32767;
    public TimerHandler mTimerHandler;
    boolean mIsRecording = false;
    int checked = 5;
    private boolean mIsTorchOn;
    private Timer mTimer;
    //    private long mElapsedTime;
    private CameraResolutionsFragment mCameraResolutionsDialog;
    private Intent mLiveVideoBroadcasterServiceIntent;
    private ILiveVideoBroadcaster mLiveVideoBroadcaster;
    private ImageView mButtonStreamingBandwidth;
    private ImageView mButtonSettings;
    private ImageView mButtonConfigure;
    private Button mButtonCapture;
    private ImageView mButtonFlash;
    private ImageView mButtonChangeCamera;
    private GLSurfaceView mTextureView;
    private TextView mTextChatMessageCount;
    private RelativeLayout mLayoutCamera;
    private FrameLayout mButtonChat;
    private RotateAnimation mRotateAnimation;
    private Chronometer mchronometer;
    private long mstopTime = 0;
    private boolean misStart = true;

    @Nullable
    private MediaRecorder mediaRecorder;
    @Nullable
    private Handler audioInspectionHandler;
    private Runnable inspectionRunnable = new Runnable() {
        @Override
        public void run() {
            double amplitude = mediaRecorder.getMaxAmplitude();
            updateAmplitudeMeter(amplitude);
            audioInspectionHandler.postDelayed(this, 50);
        }
    };
    private int maxVolumeItems = 0;

    private String accountName;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LiveVideoBroadcaster.LocalBinder binder = (LiveVideoBroadcaster.LocalBinder) service;

            if (mLiveVideoBroadcaster == null) {
                mLiveVideoBroadcaster = binder.getService();
                mLiveVideoBroadcaster.init(CameraActivity.this, mTextureView);
                mLiveVideoBroadcaster.setAdaptiveStreaming(true);
            }

            mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLiveVideoBroadcaster = null;
        }
    };
    private OrientationManager orientationManager;
    private ImageView mImageVoiceLand;
    private RelativeLayout mLayoutVoice;
    //    private boolean misVoiceLayout = loadBoolean("voice_layout");
    private RelativeLayout mLayoutCameraImage;
    private ImageView mImageCamera;
    private boolean misCameraLayout = false;
    private RelativeLayout mButtonSecond;
    private RelativeLayout mLayoutSecond;
    //    private boolean misLayoutSeekBar = loadBoolean("second_layout");
    private ImageView mImageVoice;
    private ImageView mImageHeadSet;
    private ImageView mImageVideoSize1080P;
    private ImageView mImageVideoSize1080I;
    private ImageView mImageVideoSize720P;
    private ImageView mImageVideoSize576P;
    private ImageView mImageVideoSize576I;
    private CustomVerticalSeekbar mCustomVerticalSeekbar;
    private boolean mSizeColor1080P;
    private boolean mSizeColor1080I;
    private boolean mSizeColor720P;
    private boolean mSizeColor576P;
    private boolean mSizeColor576I;
    private boolean misVoice = true;
    private int mStateSeekBar = loadInt("state_seek_bar");
    private LinearLayout mLayoutStreamingBandwidth;
    private boolean misStreamingBandwidth = false;
    private Resolution mResolution;
    private TextView mTextVideoSize;
    private RelativeLayout mLayoutCameraButton;
    private RelativeLayout mLayoutConfigure;
    private LinearLayout mLayoutChangeCamera;
    private RelativeLayout mLayoutIcons;
    private RelativeLayout mLayoutCollection;
    private LinearLayout mLayoutMetaData;
    private boolean isMetaData;
    private ScrollView mScrollViewMetaData;
    private int mselectedSizeWidth = loadIntCameraSizeWidth("camera_size_width");
    private int mselectedSizeHeight = loadIntCameraSizeHeight("camera_size_height");
    private LinearLayout mLayoutChat;
    private RecyclerView mListChat;
    private ChatAdapter mChatAdapter;
    private ArrayList<ChatModel> mChatModels = new ArrayList<>();
    private EmojiconEditText mFieldComment;
    private ImageView mImageSend;
    private TextView mFiledNews;
    private EditText mFieldTitle;
    private Button mButtonDone;
    private boolean isButtonChat;
    private EditText mFiledSubtitle;
    private EditText mFieldDescription;
    private EditText mFieldRestriction;
    private EditText mFieldAddLocation;
    private ImageView mImageLocation;
    private EditText mFieldStreamPrice;

    private RecyclerView mListTimeZone;
    private DateTimeZoneAdapter mDateTimeZoneAdapter;
    private String[] mTimeZone;
    private ExpandableRelativeLayout mExpandableTimeZone;
    private int mMarginCollectionLayout;
    private RelativeLayout mLayoutWriteComments;
    private boolean isLandScapeOrientation = false;
    private View mViewChat;
    private EmojIconActions mEmojIconActions;
    private ImageView mImageSmile;

    private boolean isVoiceLayoutVisible = false;

    public static String getDurationString(int seconds) {
        if (seconds < 0 || seconds > 2000000) {
            seconds = 0;
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours == 0) {
            return twoDigitString(minutes) + " : " + twoDigitString(seconds);
        } else {
            return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
        }
    }

    public static String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mLiveVideoBroadcasterServiceIntent = new Intent(this, LiveVideoBroadcaster.class);
        startService(mLiveVideoBroadcasterServiceIntent);
        setContentView(R.layout.activity_camera);
        mTextureView = findViewById(R.id.texture_view);
        mButtonStreamingBandwidth = findViewById(R.id.button_streaming_bandwidth);
        mButtonSettings = findViewById(R.id.button_settings);
        mButtonChat = findViewById(R.id.button_chat);
        mTextChatMessageCount = findViewById(R.id.text_chat_message_count);
        mButtonConfigure = findViewById(R.id.button_configure);
        mButtonCapture = findViewById(R.id.button_capture_rec);
        mButtonFlash = findViewById(R.id.button_flash);
        mButtonChangeCamera = findViewById(R.id.button_change_camera);
        mLayoutCamera = findViewById(R.id.layout_camera);
//        mImageVoiceLand = findViewById(R.id.img_voice_land);
        mLayoutVoice = findViewById(R.id.layout_voice);
        mLayoutCameraImage = findViewById(R.id.layout_camera_image);
        mImageCamera = findViewById(R.id.img_camera);
        mButtonSecond = findViewById(R.id.layout_second);
        mLayoutSecond = findViewById(R.id.layout_seek_bar);
//        mCustomVerticalSeekbar = findViewById(R.id.customverticalseekbar);
//        mImageVoice = findViewById(R.id.image_voice);
//        mImageHeadSet = findViewById(R.id.image_head_set);
        mTextVideoSize = findViewById(R.id.text_video_size);
        mTextVideoSize.setText(loadStringCameraSize("camera_size"));
        mImageVideoSize1080P = findViewById(R.id.image_size_1088);
        mImageVideoSize1080I = findViewById(R.id.image_size_1024);
        mImageVideoSize720P = findViewById(R.id.image_size_720);
        mImageVideoSize576P = findViewById(R.id.image_size_640);
        mImageVideoSize576I = findViewById(R.id.image_size_320);
        mLayoutChangeCamera = findViewById(R.id.layout_change_camera);

        mLayoutStreamingBandwidth = findViewById(R.id.layout_streaming_bandwidth);
        mLayoutCameraButton = findViewById(R.id.layout_camera_buttons);
        mLayoutConfigure = findViewById(R.id.layout_configure);
        mLayoutIcons = findViewById(R.id.layout_icons);
        mLayoutCollection = findViewById(R.id.layout_collection);
        mLayoutMetaData = findViewById(R.id.layout_meta_data);
        mScrollViewMetaData = findViewById(R.id.scroll_view_meta_data);
        mListChat = findViewById(R.id.list_chat);
        mFieldComment = findViewById(R.id.field_comment);
        mImageSend = findViewById(R.id.image_send);
        mFiledNews = findViewById(R.id.text_news);
        mFieldTitle = findViewById(R.id.field_title);
        mButtonDone = findViewById(R.id.button_done);
        mFiledSubtitle = findViewById(R.id.field_subtitle);
        mFieldDescription = findViewById(R.id.field_stream_description);
        mFieldRestriction = findViewById(R.id.field_restriction);
        mFieldAddLocation = findViewById(R.id.field_add_location);
        mImageLocation = findViewById(R.id.image_location);
        mFieldStreamPrice = findViewById(R.id.field_stream_price);
        mLayoutWriteComments = findViewById(R.id.layout_write_comments);
        mLayoutChat = findViewById(R.id.layout_chat);
        mViewChat = findViewById(R.id.view_chat);
        mImageSmile = findViewById(R.id.image_smile);

        mListTimeZone = findViewById(R.id.list_timezone);
        mTimeZone = getTimeZoneList();
        mDateTimeZoneAdapter = new DateTimeZoneAdapter(TYPE_TIMEZONE, mTimeZone, this);
        mListTimeZone.setLayoutManager(new LinearLayoutManager(this));
        mListTimeZone.setAdapter(mDateTimeZoneAdapter);
        mExpandableTimeZone = findViewById(R.id.layout_expandable_timezone_list);

        mFieldAddLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mExpandableTimeZone.expand();
            } else
                mExpandableTimeZone.collapse();
            rotateArrow(TYPE_TIMEZONE, false);
        });
        mFieldAddLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (height < 2000){
            mMarginCollectionLayout = (width / 5);
        }else {
            mMarginCollectionLayout = (int) (width / 2.25);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mButtonSecond.setOnClickListener(this);
        mButtonStreamingBandwidth.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);
        mButtonChat.setOnClickListener(this);
        mButtonConfigure.setOnClickListener(this);
        mButtonCapture.setOnClickListener(this);
        mButtonFlash.setOnClickListener(this);
        mButtonChangeCamera.setOnClickListener(this);
//        mImageVoiceLand.setOnClickListener(this);
        mImageCamera.setOnClickListener(this);
//        mImageVoice.setOnClickListener(this);
//        mImageHeadSet.setOnClickListener(this);

        mImageVideoSize1080P.setOnClickListener(this);
        mImageVideoSize1080I.setOnClickListener(this);
        mImageVideoSize720P.setOnClickListener(this);
        mImageVideoSize576P.setOnClickListener(this);
        mImageVideoSize576I.setOnClickListener(this);
        mImageSend.setOnClickListener(this);
        mButtonDone.setOnClickListener(this);
        mFieldAddLocation.setOnClickListener(this);
        mImageLocation.setOnClickListener(this);

        mTimerHandler = new TimerHandler();
        orientationManager = new OrientationManager(this, SensorManager.SENSOR_DELAY_NORMAL, this);
        orientationManager.enable();
        mchronometer = findViewById(R.id.chronometer);

        if (mTextureView != null) {
            mTextureView.setEGLContextClientVersion(2);
        }

        cameraSizeChecked();
        getChatData();
        mEmojIconActions = new EmojIconActions(this, mLayoutChat, mFieldComment,mImageSmile);
        mEmojIconActions.ShowEmojIcon();
        getProfileData();
        setupVolumeMeter();
        setupVolumeBinding();
//        voiceChecked();
//        stateVoiceButton();
//        stateCameraButton();
//        stateSecondButton();
//        stateSeekBar();
//        stateStreamingBandwidth();

//        mCustomVerticalSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                saveInt("state_seek_bar", progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        // restoreState(savedInstanceState);
    }

    public void startTimers() {
        mchronometer.setBase(SystemClock.elapsedRealtime() + mstopTime);
        mchronometer.start();

    }

    public void stopTimers() {
        mstopTime = mchronometer.getBase() - SystemClock.elapsedRealtime();
        mchronometer.stop();
    }

    public void changeCamera() {
        if (mLiveVideoBroadcaster != null) {
            mLiveVideoBroadcaster.changeCamera();
        }
    }

    public void flash() {
        if (mLiveVideoBroadcaster != null) {
            mLiveVideoBroadcaster.flash();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mLayoutStreamingBandwidth.getVisibility() == View.VISIBLE) {
            startAudioInspection();
        }
        bindService(mLiveVideoBroadcasterServiceIntent, mConnection, 0);

//        mLayoutMetaData.setVisibility(View.INVISIBLE);
//        mScrollViewMetaData.setVisibility(View.INVISIBLE);
//        mLayoutIcons.setVisibility(View.VISIBLE);
//        mLayoutCollection.setVisibility(View.VISIBLE);
//        mTextureView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LiveVideoBroadcaster.PERMISSIONS_REQUEST: {
                if (mLiveVideoBroadcaster.isPermissionGranted()) {
                    mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                        mLiveVideoBroadcaster.requestPermission();
                    } else {
                        new AlertDialog.Builder(CameraActivity.this)
                                .setTitle("Permission")
                                .setMessage("App doesnot work without permissions")
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    try {
                                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                        startActivity(intent);

                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraResolutionsDialog != null && mCameraResolutionsDialog.isVisible()) {
            mCameraResolutionsDialog.dismiss();
        }

        mLiveVideoBroadcaster.pause();


//        mTextureView.setVisibility(View.INVISIBLE);
//        mLayoutMetaData.setVisibility(View.VISIBLE);
//        mScrollViewMetaData.setVisibility(View.VISIBLE);
//        mLayoutIcons.setVisibility(View.GONE);
//        mLayoutCollection.setVisibility(View.GONE);
//        isMetaData = false;

    }


    @Override
    protected void onStop() {
        super.onStop();
        stopAudioInspection();
        unbindService(mConnection);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            mLiveVideoBroadcaster.setDisplayOrientation();
//        }
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mLandscapeLayout.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "ORIENTATION_LANDSCAPE", Toast.LENGTH_SHORT).show();
//        }
//
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            mLandscapeLayout.setVisibility(View.GONE);
//            Toast.makeText(this, "ORIENTATION_PORTRAIT", Toast.LENGTH_SHORT).show();
//        }
    }

    public void showSetResolutionDialog(View v) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragmentDialog = getSupportFragmentManager().findFragmentByTag("dialog");
        if (fragmentDialog != null) {
            ft.remove(fragmentDialog);
        }

        ArrayList<Resolution> sizeList = mLiveVideoBroadcaster.getPreviewSizeList();
        if (sizeList != null && sizeList.size() > 0) {
            mCameraResolutionsDialog = new CameraResolutionsFragment();
            mCameraResolutionsDialog.setCameraResolutions(sizeList, mLiveVideoBroadcaster.getPreviewSize());
            mCameraResolutionsDialog.show(ft, "resolutiton_dialog");
        } else {
            Snackbar.make(mLayoutCamera, "No resolution available", Snackbar.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void toggleBroadcasting() {
        if (!mIsRecording) {
            if (mLiveVideoBroadcaster != null) {
                if (!mLiveVideoBroadcaster.isConnected()) {
//                    String streamName = mStreamNameEditText.getText().toString();
                    new AsyncTask<String, String, Boolean>() {
                        ContentLoadingProgressBar progressBar;

                        @Override
                        protected void onPreExecute() {
                            progressBar = new ContentLoadingProgressBar(CameraActivity.this);
                            progressBar.show();
                        }

                        @Override
                        protected Boolean doInBackground(String... url) {
                            return mLiveVideoBroadcaster.startBroadcasting(url[0]);
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            progressBar.hide();
                            mIsRecording = result;
                            if (result) {
//                                mStreamLiveStatus.setVisibility(View.VISIBLE);
//                                mBroadcastControlButton.setText("Stop broadcasting");
//                                mSettingsButton.setVisibility(View.GONE);
                                startTimer();//start the recording duration
                            } else {
                                Snackbar.make(mLayoutCamera, "Stream not started", Snackbar.LENGTH_LONG).show();
                                triggerStopRecording();
                            }
                        }
                    }.execute(RTMP_BASE_URL + "aaa");
                } else {
                    Snackbar.make(mLayoutCamera, "Streaming not finished", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(mLayoutCamera, "Shouldnt happen", Snackbar.LENGTH_LONG).show();
            }
        } else {
            triggerStopRecording();
        }
    }

    public void triggerStopRecording() {
        if (mIsRecording) {
//            mBroadcastControlButton.setText("Start broadcasting");
//            mStreamLiveStatus.setVisibility(View.GONE);
//            mStreamLiveStatus.setText("Live indicator");
            stopTimer();
            mLiveVideoBroadcaster.stopBroadcasting();
        }

        mIsRecording = false;
    }

    public void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
//        mElapsedTime = 0;
        mTimer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
//                mElapsedTime += 1; //increase every sec
                mTimerHandler.obtainMessage(TimerHandler.INCREASE_TIMER).sendToTarget();
                if (mLiveVideoBroadcaster == null || !mLiveVideoBroadcaster.isConnected()) {
                    mTimerHandler.obtainMessage(TimerHandler.CONNECTION_LOST).sendToTarget();
                }
            }
        }, 0, 1000);
    }

    public void stopTimer() {
        if (mTimer != null) {
            this.mTimer.cancel();
        }

        this.mTimer = null;
//        this.mElapsedTime = 0;
    }

    public void setResolution(Resolution size) {
        mLiveVideoBroadcaster.setResolution(size);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_streaming_bandwidth:
                misStreamingBandwidth = !misStreamingBandwidth;
                if (misStreamingBandwidth) {
                    mLayoutStreamingBandwidth.setVisibility(View.VISIBLE);
                    startAudioInspection();
                    fillSoundMeter(0);
                } else {
                    mLayoutStreamingBandwidth.setVisibility(View.INVISIBLE);
                    stopAudioInspection();
                }

                break;

            case R.id.button_done:

                if (mFieldTitle != null) {
                    mFiledNews.setText(mFieldTitle.getText().toString());
//                    mFieldTitle.setText("");
//                    mFiledSubtitle.setText("");
//                    mFieldDescription.setText("");
//                    mFieldRestriction.setText("");
//                    mFieldAddLocation.setText("");
//                    mFieldStreamPrice.setText("0");

                    mLayoutMetaData.setVisibility(View.INVISIBLE);
                    mScrollViewMetaData.setVisibility(View.INVISIBLE);
                    mLayoutIcons.setVisibility(View.VISIBLE);
                    mLayoutCollection.setVisibility(View.VISIBLE);
                    mTextureView.setVisibility(View.VISIBLE);

                    isMetaData = false;
                }
                break;

            case R.id.button_chat:
                isButtonChat = true;
                if (!isLandScapeOrientation) {
                    mLayoutIcons.setVisibility(View.INVISIBLE);
                }
                mLayoutChat.setVisibility(View.VISIBLE);
                break;

            case R.id.button_configure:

                if (!isButtonChat) {
                    if (isMetaData) {
                        mLayoutMetaData.setVisibility(View.INVISIBLE);
                        mScrollViewMetaData.setVisibility(View.INVISIBLE);
                        mLayoutIcons.setVisibility(View.VISIBLE);
                        mLayoutCollection.setVisibility(View.VISIBLE);
                        mTextureView.setVisibility(View.VISIBLE);
                    } else {
                        mTextureView.setVisibility(View.INVISIBLE);
                        mLayoutMetaData.setVisibility(View.VISIBLE);
                        mScrollViewMetaData.setVisibility(View.VISIBLE);
                        mLayoutIcons.setVisibility(View.GONE);
                        mLayoutCollection.setVisibility(View.GONE);
                    }
                    isMetaData = !isMetaData;
                }
                break;

            case R.id.button_capture_rec:
                toggleBroadcasting();
                if (misStart) {
                    startTimers();
                    misStart = false;
                } else {
                    stopTimers();
                    misStart = true;
                }
                break;

            case R.id.button_flash:
                if (mLiveVideoBroadcaster.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    flash();
                    mIsTorchOn = !mIsTorchOn;
                    mButtonFlash.setBackground(ContextCompat.getDrawable(this, mIsTorchOn ? R.drawable.selector_rounded_button_white : R.drawable.selector_rounded_button_stroke_white));
                    mButtonFlash.setColorFilter(ContextCompat.getColor(this, mIsTorchOn ? R.color.color_main_blue : android.R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                break;

            case R.id.button_change_camera:
                changeCamera();
                mRotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                mRotateAnimation.setDuration(300);
                mRotateAnimation.setInterpolator(new LinearInterpolator());
                mButtonChangeCamera.startAnimation(mRotateAnimation);
                break;
//            case R.id.img_voice_land:
//                misVoiceLayout = !misVoiceLayout;
//                saveBoolean("voice_layout", misVoiceLayout);
//                if (loadBoolean("voice_layout")) {
//                    mLayoutVoice.setVisibility(View.INVISIBLE);
//                } else {
//                    mLayoutVoice.setVisibility(View.VISIBLE);
//                }
//
//
//                break;
            case R.id.img_camera:
                misCameraLayout = !misCameraLayout;
                if (misCameraLayout) {
                    mLayoutCameraImage.setVisibility(View.VISIBLE);
                } else {
                    mLayoutCameraImage.setVisibility(View.INVISIBLE);
                }

                break;
            case R.id.layout_second:
//                misLayoutSeekBar = !misLayoutSeekBar;
//                saveBoolean("second_layout", misLayoutSeekBar);
//                if (loadBoolean("second_layout")) {
                if (mLayoutSecond.getVisibility() == View.VISIBLE) {
                    mLayoutSecond.setVisibility(View.INVISIBLE);
                } else {
                    mLayoutSecond.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.image_voice:
                ImageViewCompat.setImageTintList(mImageVoice, ContextCompat.getColorStateList(this, R.color.color_green));
                ImageViewCompat.setImageTintList(mImageHeadSet, ContextCompat.getColorStateList(this, android.R.color.white));

                misVoice = true;
                saveBoolean("state_voice", misVoice);

                break;
            case R.id.image_head_set:
                ImageViewCompat.setImageTintList(mImageHeadSet, ContextCompat.getColorStateList(this, R.color.color_green));
                ImageViewCompat.setImageTintList(mImageVoice, ContextCompat.getColorStateList(this, android.R.color.white));

                misVoice = false;
                saveBoolean("state_voice", misVoice);

                break;
            case R.id.image_size_1088:
                mResolution = new Resolution(1280, 720);
                setResolution(mResolution);
                mTextVideoSize.setText("1280");
                saveStringCameraSize("camera_size", "1280");
//                saveIntCameraSizeWidth("camera_size_width",1280);
//                saveIntCameraSizeHeight("camera_size_height",720);
//                mselectedSizeWidth = 1280;
//                mselectedSizeHeight = 720;
                ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, R.color.color_green));
                ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, android.R.color.white));

                mSizeColor1080P = true;
                mSizeColor1080I = false;
                mSizeColor720P = false;
                mSizeColor576P = false;
                mSizeColor576I = false;

                saveBoolean("size1080P", mSizeColor1080P);
                saveBoolean("size1080I", mSizeColor1080I);
                saveBoolean("size720P", mSizeColor720P);
                saveBoolean("size576P", mSizeColor576P);
                saveBoolean("size576I", mSizeColor576I);

                break;
            case R.id.image_size_1024:
                mResolution = new Resolution(960, 540);
                setResolution(mResolution);
                mTextVideoSize.setText("960");
                saveStringCameraSize("camera_size", "960");
//                saveIntCameraSizeWidth("camera_size_width",960);
//                saveIntCameraSizeHeight("camera_size_height",540);
//                mselectedSizeWidth = 960;
//                mselectedSizeHeight = 540;
                ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, R.color.color_green));
                ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, android.R.color.white));

                mSizeColor1080P = false;
                mSizeColor1080I = true;
                mSizeColor720P = false;
                mSizeColor576P = false;
                mSizeColor576I = false;

                saveBoolean("size1080P", mSizeColor1080P);
                saveBoolean("size1080I", mSizeColor1080I);
                saveBoolean("size720P", mSizeColor720P);
                saveBoolean("size576P", mSizeColor576P);
                saveBoolean("size576I", mSizeColor576I);

                break;
            case R.id.image_size_720:
                mResolution = new Resolution(960, 540);
                setResolution(mResolution);
                mTextVideoSize.setText("800");
                saveStringCameraSize("camera_size", "800");
//                saveIntCameraSizeWidth("camera_size_width",960);
//                saveIntCameraSizeHeight("camera_size_height",540);
//                mselectedSizeWidth = 960;
//                mselectedSizeHeight = 540;
                ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, R.color.color_green));
                ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, android.R.color.white));

                mSizeColor1080P = false;
                mSizeColor1080I = false;
                mSizeColor720P = true;
                mSizeColor576P = false;
                mSizeColor576I = false;

                saveBoolean("size1080P", mSizeColor1080P);
                saveBoolean("size1080I", mSizeColor1080I);
                saveBoolean("size720P", mSizeColor720P);
                saveBoolean("size576P", mSizeColor576P);
                saveBoolean("size576I", mSizeColor576I);

                break;
            case R.id.image_size_640:
//                mResolution = new Resolution(1280  ,960);
//                setResolution(mResolution);
                mTextVideoSize.setText("640");
                saveStringCameraSize("camera_size", "640");
//                saveIntCameraSizeWidth("camera_size_width",1280);
//                saveIntCameraSizeHeight("camera_size_height",960);
//                mselectedSizeWidth = 1280;
//                mselectedSizeHeight = 960;
                ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, R.color.color_green));
                ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, android.R.color.white));

                mSizeColor1080P = false;
                mSizeColor1080I = false;
                mSizeColor720P = false;
                mSizeColor576P = true;
                mSizeColor576I = false;

                saveBoolean("size1080P", mSizeColor1080P);
                saveBoolean("size1080I", mSizeColor1080I);
                saveBoolean("size720P", mSizeColor720P);
                saveBoolean("size576P", mSizeColor576P);
                saveBoolean("size576I", mSizeColor576I);

                break;
            case R.id.image_size_320:
//                mResolution = new Resolution(320,240);
//                setResolution(mResolution);
                mTextVideoSize.setText("320");
                saveStringCameraSize("camera_size", "320");
//                saveIntCameraSizeWidth("camera_size_width",320);
//                saveIntCameraSizeHeight("camera_size_height",240);
//                mselectedSizeWidth = 320;
//                mselectedSizeHeight = 240;
                ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, R.color.color_green));
                ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, android.R.color.white));
                ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, android.R.color.white));

                mSizeColor1080P = false;
                mSizeColor1080I = false;
                mSizeColor720P = false;
                mSizeColor576P = false;
                mSizeColor576I = true;

                saveBoolean("size1080P", mSizeColor1080P);
                saveBoolean("size1080I", mSizeColor1080I);
                saveBoolean("size720P", mSizeColor720P);
                saveBoolean("size576P", mSizeColor576P);
                saveBoolean("size576I", mSizeColor576I);

                break;
            case R.id.image_send:
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String formattedDate = dateFormat.format(date);
                if (!mFieldComment.getText().toString().equals("")) {
                    mChatModels.add(new ChatModel(R.drawable.ic_contact_selected, formattedDate, accountName, mFieldComment.getText().toString()));
                    mFieldComment.setText("");
                    mChatAdapter.setData(mChatModels);
                    mListChat.scrollToPosition(mChatModels.size() - 1);
                } else {
                    Toast.makeText(this, "line can not be empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.image_location:
                if (mExpandableTimeZone.isExpanded()) {
                    mExpandableTimeZone.collapse();
                } else {
                    mExpandableTimeZone.expand();
                }

                rotateArrow(TYPE_TIMEZONE, !mExpandableTimeZone.isExpanded());
                break;
            case R.id.field_add_location:
                mExpandableTimeZone.expand();
                rotateArrow(TYPE_TIMEZONE, false);
                break;
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
                showMessage(error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void setupAccountFields(Owner item) {
        accountName = item.getFullName();
    }

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {

        RelativeLayout.LayoutParams paramsLayoutCameraButton;
        FlowLayout.LayoutParams paramsButtonCapture;
        RelativeLayout.LayoutParams paramsLayoutConfigure;
        RelativeLayout.LayoutParams paramsLayoutChangeCamera;
        RelativeLayout.LayoutParams paramsButtonConfigure;
        LinearLayout.LayoutParams paramsChangeCamera;
        RelativeLayout.LayoutParams paramsLayoutIcons;
        RelativeLayout.LayoutParams paramsLayoutCollection;
        RelativeLayout.LayoutParams paramsButtonChat;
        RelativeLayout.LayoutParams paramsLayoutChat;
        RelativeLayout.LayoutParams paramsmScrollViewMetaData;
        RelativeLayout.LayoutParams paramsSettings = (RelativeLayout.LayoutParams) mButtonSettings.getLayoutParams();
        RelativeLayout.LayoutParams paramsStreamingBandWidth;


        switch (screenOrientation) {
            case PORTRAIT:

//                mButtonCapture.setRotation(0f);
//                mchronometer.setRotation(0f);
//                mButtonChangeCamera.setRotation(0f);
//                mButtonConfigure.setRotation(0f);
//                mLayoutCollection.setRotation(0f);
//                mButtonStreamingBandwidth.setRotation(0);
//                mButtonChat.setRotation(0);
//                mScrollViewMetaData.setRotation(0);
//
//                int pix = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
//
                isLandScapeOrientation = false;
                mButtonFlash.setVisibility(View.VISIBLE);

                paramsLayoutCameraButton = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getInteger(R.integer.mLayoutCameraButton));
                paramsLayoutCameraButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mLayoutCameraButton.setLayoutParams(paramsLayoutCameraButton);

                paramsButtonCapture = new FrameLayout.LayoutParams(getResources().getInteger(R.integer.mButtonCapture), getResources().getInteger(R.integer.mButtonCapture));
                mButtonCapture.setLayoutParams(paramsButtonCapture);
                mButtonFlash.setVisibility(View.VISIBLE);

                paramsLayoutConfigure = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsLayoutConfigure.addRule(RelativeLayout.START_OF, R.id.button_capture);
                mLayoutConfigure.setLayoutParams(paramsLayoutConfigure);

                paramsLayoutChangeCamera = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsLayoutChangeCamera.addRule(RelativeLayout.END_OF, R.id.button_capture);
                mLayoutChangeCamera.setLayoutParams(paramsLayoutChangeCamera);
                mLayoutChangeCamera.setGravity(Gravity.CENTER);

                paramsChangeCamera = new LinearLayout.LayoutParams(getResources().getInteger(R.integer.mButtonChangeCamera), getResources().getInteger(R.integer.mButtonChangeCamera));
                mButtonChangeCamera.setLayoutParams(paramsChangeCamera);

                paramsButtonConfigure = new RelativeLayout.LayoutParams(getResources().getInteger(R.integer.mButtonChangeCamera), getResources().getInteger(R.integer.mButtonChangeCamera));
                paramsButtonConfigure.addRule(RelativeLayout.CENTER_IN_PARENT);
                mButtonConfigure.setLayoutParams(paramsButtonConfigure);

                paramsLayoutIcons = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getInteger(R.integer.mLayoutIcons));
                paramsLayoutIcons.addRule(RelativeLayout.ABOVE, R.id.layout_camera_buttons);
                mLayoutIcons.setLayoutParams(paramsLayoutIcons);

                paramsLayoutCollection = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsLayoutCollection.addRule(RelativeLayout.ABOVE, R.id.layout_icons);
                mLayoutCollection.setLayoutParams(paramsLayoutCollection);

                paramsmScrollViewMetaData = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsmScrollViewMetaData.addRule(RelativeLayout.ABOVE, R.id.layout_camera_buttons);
                mScrollViewMetaData.setLayoutParams(paramsmScrollViewMetaData);
                if (mLayoutChat.getVisibility() == View.VISIBLE){
                    mLayoutIcons.setVisibility(View.INVISIBLE);
                }

////                mButtonSettings.setVisibility(View.VISIBLE);
//                paramsStreamingBandWidth = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                paramsStreamingBandWidth.setMargins(getResources().getInteger(R.integer.mButtonSettings), getResources().getInteger(R.integer.mButtonSettings)
//                        , getResources().getInteger(R.integer.mButtonSettings), getResources().getInteger(R.integer.mButtonSettings));
//                mButtonStreamingBandwidth.setLayoutParams(paramsStreamingBandWidth);
//                mButtonStreamingBandwidth.setPadding(getResources().getInteger(R.integer.mButtonStreamingBandwidth), getResources().getInteger(R.integer.mButtonStreamingBandwidth),
//                        getResources().getInteger(R.integer.mButtonStreamingBandwidth), getResources().getInteger(R.integer.mButtonStreamingBandwidth));
//
                paramsButtonChat = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsButtonChat.addRule(RelativeLayout.CENTER_VERTICAL);
                paramsButtonChat.setMargins(0, getResources().getInteger(R.integer.mButtonSettings), getResources().getInteger(R.integer.mButtonSettings), getResources().getInteger(R.integer.mButtonSettings));
                paramsButtonChat.addRule(RelativeLayout.END_OF, R.id.button_streaming_bandwidth);
                mButtonChat.setLayoutParams(paramsButtonChat);

                paramsLayoutChat = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsLayoutChat.addRule(RelativeLayout.ABOVE, R.id.layout_camera_buttons);
                mLayoutChat.setLayoutParams(paramsLayoutChat);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);

                break;
            case REVERSED_PORTRAIT:
//                mButtonCapture.setRotation(0f);
//                mchronometer.setRotation(0f);
//                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,360);
//                layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                mLayoutCameraButton.setLayoutParams(layoutParams1);
                break;
            case REVERSED_LANDSCAPE:
//                mButtonCapture.setRotation(90f);
//                mchronometer.setRotation(90f);
                break;
            case LANDSCAPE:
//
//                mButtonCapture.setRotation(90f);
//                mchronometer.setRotation(90f);
//                mButtonChangeCamera.setRotation(90f);
//                mButtonConfigure.setRotation(90f);
//                mLayoutCollection.setRotation(90f);
//                mButtonStreamingBandwidth.setRotation(90f);
//                mButtonChat.setRotation(90f);




//                mButtonStreamingBandwidth.setLayoutParams(paramsSettings);
//
//                mButtonSettings.setVisibility(View.GONE);
//
//                if (!isButtonChat) {
//                    if (isMetaData) {
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
//                        mButtonCapture.setRotation(0);
//                        mchronometer.setRotation(0);
//                        mButtonChangeCamera.setRotation(0);
//                        mButtonConfigure.setRotation(0);
//                    }
//                }
//

                isLandScapeOrientation = true;

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);

                paramsLayoutChat = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mLayoutChat.setLayoutParams(paramsLayoutChat);

                mButtonFlash.setVisibility(View.GONE);
                paramsLayoutCameraButton = new RelativeLayout.LayoutParams(getResources().getInteger(R.integer.mLayoutIcons), ViewGroup.LayoutParams.MATCH_PARENT);
                paramsLayoutCameraButton.addRule(RelativeLayout.ALIGN_PARENT_END);
                mLayoutCameraButton.setLayoutParams(paramsLayoutCameraButton);

                paramsButtonCapture = new FrameLayout.LayoutParams(getResources().getInteger(R.integer.mButtonCaptureLand), getResources().getInteger(R.integer.mButtonCaptureLand));
                mButtonCapture.setLayoutParams(paramsButtonCapture);


                paramsLayoutChangeCamera = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mLayoutChangeCamera.setLayoutParams(paramsLayoutChangeCamera);
                mLayoutChangeCamera.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

                paramsChangeCamera = new LinearLayout.LayoutParams(getResources().getInteger(R.integer.mButtonChangeCamera), getResources().getInteger(R.integer.mButtonChangeCamera));
                paramsChangeCamera.setMargins(0,0,0,getResources().getInteger(R.integer.mButtonSettings));
                mButtonChangeCamera.setLayoutParams(paramsChangeCamera);


                paramsLayoutConfigure = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsLayoutConfigure.addRule(RelativeLayout.ABOVE, R.id.button_capture);
                paramsLayoutConfigure.setMargins(0,getResources().getInteger(R.integer.mButtonSettings),0,0);
                mLayoutConfigure.setLayoutParams(paramsLayoutConfigure);
                mLayoutConfigure.setGravity(Gravity.CENTER_HORIZONTAL);

                paramsButtonConfigure = new RelativeLayout.LayoutParams(getResources().getInteger(R.integer.mButtonChangeCamera), getResources().getInteger(R.integer.mButtonChangeCamera));
                mButtonConfigure.setLayoutParams(paramsButtonConfigure);

                paramsLayoutIcons = new RelativeLayout.LayoutParams(getResources().getInteger(R.integer.mLayoutIcons),ViewGroup.LayoutParams.MATCH_PARENT);
                mLayoutIcons.setLayoutParams(paramsLayoutIcons);

                paramsButtonChat = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsButtonChat.addRule(RelativeLayout.CENTER_VERTICAL);
                paramsButtonChat.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                paramsButtonChat.setMargins(getResources().getInteger(R.integer.mButtonSettings), getResources().getInteger(R.integer.mButtonSettings),
                        getResources().getInteger(R.integer.mButtonSettings), getResources().getInteger(R.integer.mButtonSettings));
                mButtonChat.setLayoutParams(paramsButtonChat);

                paramsLayoutCollection = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsLayoutCollection.addRule(RelativeLayout.START_OF, R.id.layout_camera_buttons);
                paramsLayoutCollection.addRule(RelativeLayout.END_OF, R.id.layout_icons);
                mLayoutCollection.setLayoutParams(paramsLayoutCollection);

                paramsmScrollViewMetaData = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsmScrollViewMetaData.addRule(RelativeLayout.START_OF, R.id.layout_camera_buttons);
                mScrollViewMetaData.setLayoutParams(paramsmScrollViewMetaData);
                mLayoutIcons.setVisibility(View.VISIBLE);

                break;
        }
    }

    private void cameraSizeChecked() {
        if (loadBoolean("size1080P")) {
            //    mTextVideoSize.setText("1280");
            ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, R.color.color_green));
            ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, android.R.color.white));
        } else if (loadBoolean("size1080I")) {
            //  mTextVideoSize.setText("1024");
            ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, R.color.color_green));
            ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, android.R.color.white));
        } else if (loadBoolean("size720P")) {
            //  mTextVideoSize.setText("720");
            ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, R.color.color_green));
            ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, android.R.color.white));
        } else if (loadBoolean("size576P")) {
            // mTextVideoSize.setText("640");
            ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, R.color.color_green));
            ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, android.R.color.white));
        } else {
            // mTextVideoSize.setText("320");
            ImageViewCompat.setImageTintList(mImageVideoSize576I, ContextCompat.getColorStateList(this, R.color.color_green));
            ImageViewCompat.setImageTintList(mImageVideoSize1080P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize1080I, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize720P, ContextCompat.getColorStateList(this, android.R.color.white));
            ImageViewCompat.setImageTintList(mImageVideoSize576P, ContextCompat.getColorStateList(this, android.R.color.white));
        }
    }

//    private void voiceChecked() {
//        if (loadBoolean("state_voice")) {
//            ImageViewCompat.setImageTintList(mImageVoice, ContextCompat.getColorStateList(this, R.color.color_green));
//            ImageViewCompat.setImageTintList(mImageHeadSet, ContextCompat.getColorStateList(this, android.R.color.white));
//        } else {
//            ImageViewCompat.setImageTintList(mImageHeadSet, ContextCompat.getColorStateList(this, R.color.color_green));
//            ImageViewCompat.setImageTintList(mImageVoice, ContextCompat.getColorStateList(this, android.R.color.white));
//        }
//    }

//    public void stateVoiceButton() {
//        if (loadBoolean("voice_layout")) {
//            mLayoutVoice.setVisibility(View.INVISIBLE);
//        } else {
//            mLayoutVoice.setVisibility(View.VISIBLE);
//        }
//    }

//    public void stateCameraButton() {
//        if (loadBoolean("camera_layout")) {
//            mLayoutCameraImage.setVisibility(View.INVISIBLE);
//        } else {
//            mLayoutCameraImage.setVisibility(View.VISIBLE);
//        }
//    }
//    public void stateSecondButton() {
//        if (loadBoolean("second_layout")) {
//            mLayoutSecond.setVisibility(View.INVISIBLE);
//        } else {
//            mLayoutSecond.setVisibility(View.VISIBLE);
//        }
//    }

//    private void stateStreamingBandwidth(){
//        if (loadBoolean("streaming_bandwidth")) {
//            mLayoutStreamingBandwidth.setVisibility(View.INVISIBLE);
//        } else {
//            mLayoutStreamingBandwidth.setVisibility(View.VISIBLE);
//        }
//    }

//    private void stateSeekBar(){
//        mCustomVerticalSeekbar.setProgress(loadInt("state_seek_bar"));
//    }

    @Override
    public void onBackPressed() {
        mLayoutIcons.setVisibility(View.VISIBLE);
        if (isMetaData) {
            mLayoutMetaData.setVisibility(View.INVISIBLE);
            mScrollViewMetaData.setVisibility(View.INVISIBLE);
            mLayoutIcons.setVisibility(View.VISIBLE);
            mLayoutCollection.setVisibility(View.VISIBLE);
            mTextureView.setVisibility(View.VISIBLE);
            isMetaData = false;
        } else {

            isButtonChat = false;
            if (mLayoutChat.getVisibility() == View.VISIBLE) {
                mLayoutChat.setVisibility(View.INVISIBLE);
                mLayoutIcons.setVisibility(View.VISIBLE);
            } else {
                super.onBackPressed();
                isMetaData = true;
            }
        }
    }

    public void getChatData() {
        mListChat.setLayoutManager(new LinearLayoutManager(this));
        mChatAdapter = new ChatAdapter(this);
        mListChat.setAdapter(mChatAdapter);

        mChatModels.add(new ChatModel(R.drawable.ic_contact_selected, "13:11", "Sara Majd", "WOW, this lokks amazing."));
        mChatModels.add(new ChatModel(R.drawable.ic_contact_selected, "13:11", "Hrithik Williams", "Just as I thought"));
        mChatModels.add(new ChatModel(R.drawable.ic_contact_selected, "13:11", "Daniel Roshan", "Where is that?"));
        mChatAdapter.setData(mChatModels);
        mListChat.scrollToPosition(mChatModels.size() - 1);
    }

    private void rotateArrow(int type, boolean expand) {
        switch (type) {
            case TYPE_TIMEZONE:
                ObjectAnimator.ofFloat(mImageLocation, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
        }
    }

    @Override
    public void onTimeZoneSelected(int type, String s) {
        rotateArrow(type, false);
        switch (type) {
            case TYPE_TIMEZONE:
                mExpandableTimeZone.collapse();
                mFieldAddLocation.setText(s);
                break;

        }
    }

    private void filter(String text) {
        ArrayList<String> list = new ArrayList<>();
        for (String item : mTimeZone) {
            int index = item.lastIndexOf(")") + 2;
            String items = item.substring(index);
            if (items.toLowerCase().contains(text.toLowerCase())) {
                Log.d("TESTING", "Items = " + items);
                list.add(item);
            }
        }
        mDateTimeZoneAdapter.filterList(list);
    }

    @SuppressLint("HandlerLeak")
    private class TimerHandler extends Handler {
        static final int CONNECTION_LOST = 2;
        static final int INCREASE_TIMER = 1;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INCREASE_TIMER:
//                    mStreamLiveStatus.setText("Live indicator" + " - " + getDurationString((int) mElapsedTime));
                    break;
                case CONNECTION_LOST:
                    triggerStopRecording();
                    new AlertDialog.Builder(CameraActivity.this)
                            .setMessage("Broadcast connection lost")
                            .setPositiveButton(android.R.string.yes, null)
                            .show();

                    break;
            }
        }
    }

    private void setupVolumeBinding() {
        findView(R.id.layout_voice_icon).setOnClickListener(getOnVoiceIconClicked());
        findView(R.id.image_gopro_camera).setOnClickListener(getOnGoProClicked());
    }

    private View.OnClickListener getOnGoProClicked() {
        return (v) -> GoProStreamActivity.start(CameraActivity.this);
    }

    private View.OnClickListener getOnVoiceIconClicked() {
        return (v) -> {
            if (isVoiceLayoutVisible) {
                mLayoutVoice.setVisibility(View.GONE);
                isVoiceLayoutVisible = false;
            } else {
                mLayoutVoice.setVisibility(View.VISIBLE);
                isVoiceLayoutVisible = true;
            }
        };
    }

    private void startAudioInspection() {
        initMediaRecorder();
        startAudioRecord();
        processSoundQuality();
    }

    private void initMediaRecorder() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null");
        }
    }

    private void processSoundQuality() {
        if (mediaRecorder == null) {
            return;
        }

        if (audioInspectionHandler == null) {
            audioInspectionHandler = new Handler();
        }
        audioInspectionHandler.post(inspectionRunnable);
    }

    private void startAudioRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
                showMessage(e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void stopAudioInspection() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (audioInspectionHandler != null) {
            audioInspectionHandler.removeCallbacks(inspectionRunnable);
        }
    }

    private void updateAmplitudeMeter(double amplitude) {
        int correlation = getItemsToAmplitudeCorrelation(getMaxSoundItems());
        int totalVisibleItems = getTotalVisibleItems(amplitude, correlation);
        fillSoundMeter(totalVisibleItems);
    }

    private int getTotalVisibleItems(double amplitude, int correlation) {
        return (int) (amplitude / correlation);
    }

    private int getItemsToAmplitudeCorrelation(int maxItems) {
        if (maxItems == 0) {
            return 0;
        }
        return MAX_VOLUME_VALUE / maxItems;
    }

    private void fillSoundMeter(int totalVisible) {
        mLayoutStreamingBandwidth.removeAllViews();
        int maxSoundItems = getMaxSoundItems();
        for (int i = 0; i < maxSoundItems; i++) {
            View soundItem =  inflateSoundItem();
            setupSoundItemColor(soundItem, i, maxSoundItems, totalVisible);
            mLayoutStreamingBandwidth.addView(soundItem);
        }
    }

    private void setupSoundItemColor(View soundItem, int pos, int totalItems, int totalVisible) {
        int part = totalItems / 5;
        Drawable drawable;
        if (pos < totalItems - totalVisible) {
            drawable = new ColorDrawable(Color.TRANSPARENT);
        } else if (pos <= part) {
            drawable = getDrawable(R.drawable.rounded_red);
        } else if (pos <= totalItems - part) {
            drawable = getDrawable(R.drawable.rounded_yellow);
        } else {
            drawable = getDrawable(R.drawable.selector_rounded_button_stroke_grey_background_green);
        }
        soundItem.findViewById(R.id.item_1).setBackground(drawable);
        soundItem.findViewById(R.id.item_2).setBackground(drawable);
    }

    private View inflateSoundItem() {
        return getLayoutInflater().inflate(R.layout.item_sound, null);
    }

    private int getMaxSoundItems() {
        int parentHeight = getSoundMeterHeight();
        int soundItemHeight = getSoundItemHeightInPixels();

        return parentHeight / soundItemHeight;
    }

    private int getSoundMeterHeight() {
        return mLayoutStreamingBandwidth.getHeight();
    }

    private int getSoundItemHeightInPixels() {
        //14 => 12dp height of item + 2dp top margin
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 14,getResources().getDisplayMetrics()));
    }

    private void setupVolumeMeter() {
        mLayoutStreamingBandwidth.setVisibility(View.VISIBLE);
        fillSoundMeter(0);
        mLayoutStreamingBandwidth.setVisibility(View.INVISIBLE);
    }


    //    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable(CAMERA_RESOLUTIONS, mResolution);
//        outState.putInt(SELECTED_SIZE_WIDTH, mselectedSizeWidth);
//        outState.putInt(SELECTED_SIZE_HEIGHT, mselectedSizeHeight);
//    }
//
//    private void restoreState(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey(CAMERA_RESOLUTIONS)) {
//                this.mResolution = (Resolution) savedInstanceState.getSerializable(CAMERA_RESOLUTIONS);
//            }
//
//            if (savedInstanceState.containsKey(SELECTED_SIZE_WIDTH) &&
//                    savedInstanceState.containsKey(SELECTED_SIZE_WIDTH)) {
//                mselectedSizeWidth = savedInstanceState.getInt(SELECTED_SIZE_WIDTH);
//                mselectedSizeHeight = savedInstanceState.getInt(SELECTED_SIZE_HEIGHT);
//            }
//            mResolution = new Resolution(mselectedSizeWidth,mselectedSizeHeight);
//           // setResolution(mResolution);
//        }
//    }
}