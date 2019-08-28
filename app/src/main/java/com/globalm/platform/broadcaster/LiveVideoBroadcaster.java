package com.globalm.platform.broadcaster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import com.globalm.platform.broadcaster.encoder.AudioHandler;
import com.globalm.platform.broadcaster.encoder.CameraSurfaceRenderer;
import com.globalm.platform.broadcaster.encoder.TextureMovieEncoder;
import com.globalm.platform.broadcaster.encoder.VideoEncoderCore;
import com.globalm.platform.broadcaster.network.IMediaMuxer;
import com.globalm.platform.broadcaster.network.RTMPStreamer;
import com.globalm.platform.broadcaster.utils.Resolution;
import com.globalm.platform.broadcaster.utils.Utils;
import com.globalm.platform.utils.OrientationManager;
import com.globalm.platform.utils.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LiveVideoBroadcaster extends Service implements ILiveVideoBroadcaster, CameraHandler.ICameraViewer, SurfaceTexture.OnFrameAvailableListener {

    public static final int PERMISSIONS_REQUEST = 8954;
    public final static int SAMPLE_AUDIO_RATE_IN_HZ = 44100;
    private static final String TAG = LiveVideoBroadcaster.class.getSimpleName();
    private volatile static CameraProxy sCameraProxy;
    private volatile static boolean sCameraReleased;
    private static TextureMovieEncoder sVideoEncoder = new TextureMovieEncoder();
    private final IBinder mBinder = new LocalBinder();
    private IMediaMuxer mRtmpStreamer;
    private AudioRecorderThread audioThread;
    private boolean isRecording = false;
    private GLSurfaceView mGLView;
    private CameraSurfaceRenderer mRenderer;
    private CameraHandler mCameraHandler;
    private AudioHandler audioHandler;
    private Activity mActivity;
    private ArrayList<Resolution> choosenPreviewsSizeList;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int frameRate = 20;
    private Resolution previewSize;
    private AlertDialog mAlertDialog;
    private HandlerThread mRtmpHandlerThread;
    private HandlerThread audioHandlerThread;
    private ConnectivityManager connectivityManager;
    private boolean adaptiveStreamingEnabled = false;
    private Timer adaptiveStreamingTimer = null;
    private boolean misChangeOrientation = false;

    @Override
    public int getCameraId() {
        return currentCameraId;
    }

    public boolean isConnected() {
        if (mRtmpStreamer != null) {
            return mRtmpStreamer.isConnected();
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (!misChangeOrientation){
            sCameraProxy.setDisplayOrientation(0);
        }else {
            sCameraProxy.setDisplayOrientation(90);
        }
        misChangeOrientation = !misChangeOrientation;
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


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGLView.requestRender();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void pause() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }

        mGLView.setVisibility(View.GONE);
        stopBroadcasting();
        mGLView.queueEvent(() -> {
            mRenderer.notifyPausing();
            if (!sCameraReleased) {
                releaseCamera();
            }
        });

        mGLView.onPause();
        mGLView.setOnTouchListener(null);
    }

    public void setDisplayOrientation() {
        if (sCameraProxy != null) {
            sCameraProxy.setDisplayOrientation(getCameraDisplayOrientation());
            if (!isConnected()) {
                setRendererPreviewSize();
            }
        }
    }

    public ArrayList<Resolution> getPreviewSizeList() {
        return choosenPreviewsSizeList;
    }

    public Resolution getPreviewSize() {
        return previewSize;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        audioHandlerThread.quitSafely();
        mRtmpHandlerThread.quitSafely();
        mCameraHandler.invalidateHandler();
        super.onDestroy();
    }

    public void init(Activity activity, GLSurfaceView glView) {
        try {
            audioHandlerThread = new HandlerThread("AudioHandlerThread", Process.THREAD_PRIORITY_AUDIO);
            audioHandlerThread.start();
            audioHandler = new AudioHandler(audioHandlerThread.getLooper());
            mCameraHandler = new CameraHandler(this);
            this.mActivity = activity;
            mRenderer = new CameraSurfaceRenderer(mCameraHandler, sVideoEncoder);
            mGLView = glView;
            mGLView.setRenderer(mRenderer);
            mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            mRtmpHandlerThread = new HandlerThread("RtmpStreamerThread");
            mRtmpHandlerThread.start();
            mRtmpStreamer = new RTMPStreamer(mRtmpHandlerThread.getLooper());
            connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasConnection() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public boolean startBroadcasting(String rtmpUrl) {
        isRecording = false;

        if (sCameraProxy == null || sCameraProxy.isReleased()) {
            Log.w(TAG, "Camera should be opened before calling this function");
            return false;
        }

        if (!hasConnection()) {
            Log.w(TAG, "There is no active network connection");
        }

        if (Utils.doesEncoderWorks(mActivity) != Utils.ENCODER_WORKS) {
            Log.w(TAG, "This device does not have hardware encoder");
            Snackbar.make(mGLView, "This device does not have hardware encoder", Snackbar.LENGTH_LONG).show();
            return false;
        }

        try {
            boolean result = mRtmpStreamer.open(rtmpUrl);
            if (result) {
                final long recordStartTime = System.currentTimeMillis();
                mGLView.queueEvent(() -> {
                    mRenderer.setOptions(mRtmpStreamer);
                    setRendererPreviewSize();
                    mRenderer.startRecording(recordStartTime);
                });

                int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_AUDIO_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                audioHandler.startAudioEncoder(mRtmpStreamer, SAMPLE_AUDIO_RATE_IN_HZ, minBufferSize);
                audioThread = new AudioRecorderThread(SAMPLE_AUDIO_RATE_IN_HZ, recordStartTime, audioHandler);
                audioThread.start();
                isRecording = true;

                if (adaptiveStreamingEnabled) {
                    adaptiveStreamingTimer = new Timer();
                    adaptiveStreamingTimer.schedule(new TimerTask() {
                        int previousFrameCount;
                        int frameQueueIncreased;

                        @Override
                        public void run() {
                            int frameCountInQueue = mRtmpStreamer.getVideoFrameCountInQueue();
                            Log.d(TAG, "video frameCountInQueue : " + frameCountInQueue);

                            if (frameCountInQueue > previousFrameCount) {
                                frameQueueIncreased++;
                            } else {
                                frameQueueIncreased--;
                            }

                            previousFrameCount = frameCountInQueue;

                            if (frameQueueIncreased > 10) {
                                System.out.println("decrease bitrate");
                                mGLView.queueEvent(() -> {
                                    int frameRate = mRenderer.getFrameRate();
                                    if (frameRate >= 13) {
                                        frameRate -= 3;
                                        mRenderer.setFrameRate(frameRate);
                                    } else {
                                        int bitrate = mRenderer.getBitrate();
                                        if (bitrate > 200000) { //200kbit
                                            bitrate -= 100000;
                                            mRenderer.setBitrate(bitrate);
                                            mRenderer.recorderConfigChanged();
                                        }
                                    }
                                });

                                frameQueueIncreased = 0;
                            }

                            if (frameQueueIncreased < -10) {
                                System.out.println("//increase bitrate");
                                mGLView.queueEvent(() -> {
                                    int frameRate = mRenderer.getFrameRate();
                                    if (frameRate <= 27) {
                                        frameRate += 3;
                                        mRenderer.setFrameRate(frameRate);
                                    } else {
                                        int bitrate = mRenderer.getBitrate();
                                        if (bitrate < 2000000) {
                                            bitrate += 100000;
                                            mRenderer.setBitrate(bitrate);
                                            mRenderer.recorderConfigChanged();
                                        }
                                    }
                                });

                                frameQueueIncreased = 0;
                            }
                        }
                    }, 0, 500);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isRecording;
    }

    public void stopBroadcasting() {
        if (isRecording) {
            mGLView.queueEvent(() -> mRenderer.stopRecording());

            if (adaptiveStreamingTimer != null) {
                adaptiveStreamingTimer.cancel();
                adaptiveStreamingTimer = null;
            }

            if (audioThread != null) {
                audioThread.stopAudioRecording();
            }

            if (audioHandler != null) {
                audioHandler.sendEmptyMessage(AudioHandler.END_OF_STREAM);
            }

            int i = 0;
            while (sVideoEncoder.isRecording()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (i > 5) {
                    sVideoEncoder.stopRecording();
                    break;
                }

                i++;
            }
        }
    }

    @Override
    public void flash() {
        Camera.Parameters parameters = sCameraProxy.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode()) ? Camera.Parameters.FLASH_MODE_OFF : Camera.Parameters.FLASH_MODE_TORCH);
        sCameraProxy.setParameters(parameters);
    }

    public void setResolution(Resolution size) {
        Camera.Parameters parameters = sCameraProxy.getParameters();
        parameters.setPreviewSize(size.width, size.height);
        parameters.setRecordingHint(true);
        System.out.println("set resolution stop preview");
        sCameraProxy.stopPreview();
        sCameraProxy.setParameters(parameters);
        sCameraProxy.startPreview();
        previewSize = size;
        setRendererPreviewSize();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void setRendererPreviewSize() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            mGLView.queueEvent(() -> mRenderer.setCameraPreviewSize(previewSize.height, previewSize.width));
        } else {
            mGLView.queueEvent(() -> mRenderer.setCameraPreviewSize(previewSize.width, previewSize.height));
        }
    }

    @Override
    public void handleSetSurfaceTexture(SurfaceTexture st) {
        if (sCameraProxy != null && !mActivity.isFinishing() && st != null) {
            st.setOnFrameAvailableListener(this);
            sCameraProxy.stopPreview();
            sCameraProxy.setPreviewTexture(st);
            sCameraProxy.startPreview();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void openCamera(int cameraId) {
        if (!isPermissionGranted()) {
            requestPermission();
            return;
        }

        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        currentCameraId = cameraId;
        mGLView.setVisibility(View.GONE);
        new AsyncTask<Integer, Void, Camera.Parameters>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Camera.Parameters doInBackground(Integer... params) {
                Camera.Parameters parameters = null;
                sCameraReleased = false;
                System.out.println("--- releaseCamera call in doInBackground --- ");
                releaseCamera();

                try {
                    int tryCount = 0;
                    do {
                        sCameraProxy = new CameraProxy(params[0]);
                        if (sCameraProxy.isCameraAvailable()) {
                            break;
                        }

                        Thread.sleep(1000);
                        tryCount++;
                    } while (tryCount <= 3);
                    if (sCameraProxy.isCameraAvailable()) {
                        System.out.println("--- camera opened --- ");
                        parameters = sCameraProxy.getParameters();
                        if (parameters != null) {
                            setCameraParameters(parameters);
                            if (Utils.doesEncoderWorks(mActivity) == Utils.ENCODER_NOT_TESTED) {
                                boolean encoderWorks = VideoEncoderCore.doesEncoderWork(Settings.loadIntCameraSizeWidth("camera_size_width"), Settings.loadIntCameraSizeHeight("camera_size_height"), 300000, 20);
                                Utils.setEncoderWorks(mActivity, encoderWorks);
                            }
                        }
                    } else {
                        sCameraProxy = null;
                    }
                    Log.d(TAG, "onResume complete: " + this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return parameters;
            }

            @Override
            protected void onPostExecute(Camera.Parameters parameters) {
                if (mActivity.isFinishing()) {
                    releaseCamera();
                } else if (sCameraProxy != null && parameters != null) {
                    mGLView.setVisibility(View.VISIBLE);
                    mGLView.onResume();
                    setRendererPreviewSize();

                    if (Utils.doesEncoderWorks(mActivity) != Utils.ENCODER_WORKS) {
                        showEncoderNotExistDialog();
                    }

                } else {
                    Snackbar.make(mGLView, "Camera not running properly", Snackbar.LENGTH_LONG).show();
                }
            }
        }.execute(currentCameraId);
    }

    private void releaseCamera() {
        try {
            if (sCameraProxy != null) {
                System.out.println("releaseCamera stop preview");
                sCameraProxy.release();
                sCameraProxy = null;
                sCameraReleased = true;
                System.out.println("-- camera released --");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setAdaptiveStreaming(boolean enable) {
        this.adaptiveStreamingEnabled = enable;
    }

    private int setCameraParameters(Camera.Parameters parameters) {
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Collections.sort(previewSizeList, (lhs, rhs) -> {
            if (lhs.height == rhs.height) {
                return Integer.compare(lhs.width, rhs.width);
            } else if (lhs.height > rhs.height) {
                return 1;
            }
            return -1;
        });

        int preferredHeight = 720;

        choosenPreviewsSizeList = new ArrayList<>();

        int diff = Integer.MAX_VALUE;
        Resolution choosenSize = null;
        for (int i = 0; i < previewSizeList.size(); i++) {
            Camera.Size size = previewSizeList.get(i);

            if ((size.width % 16 == 0) && (size.height % 16 == 0)) {
                Resolution resolutionSize = new Resolution(size.width, size.height);
                choosenPreviewsSizeList.add(resolutionSize);
                int currentDiff = Math.abs(size.height - preferredHeight);
                if (currentDiff < diff) {
                    diff = currentDiff;
                    choosenSize = resolutionSize;
                }
            }
        }

        int[] requestedFrameRate = new int[]{frameRate * 1000, frameRate * 1000};
        int[] bestFps = findBestFrameRate(parameters.getSupportedPreviewFpsRange(), requestedFrameRate);
        parameters.setPreviewFpsRange(bestFps[0], bestFps[1]);
        int len = choosenPreviewsSizeList.size();
        int resolutionIndex = len - 1;

        if (choosenSize != null) {
            resolutionIndex = choosenPreviewsSizeList.indexOf(choosenSize);
        }

        if (resolutionIndex >= 0) {
            Resolution size = choosenPreviewsSizeList.get(resolutionIndex);
            parameters.setPreviewSize(Settings.loadIntCameraSizeWidth("camera_size_width"), Settings.loadIntCameraSizeHeight("camera_size_height"));
            parameters.setRecordingHint(true);
        }

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        sCameraProxy.setDisplayOrientation(getCameraDisplayOrientation());

        if (parameters.isVideoStabilizationSupported()) {
            parameters.setVideoStabilization(true);
        }

        sCameraProxy.setParameters(parameters);
        Camera.Size size = parameters.getPreviewSize();
        this.previewSize = new Resolution(Settings.loadIntCameraSizeWidth("camera_size_width"), Settings.loadIntCameraSizeHeight("camera_size_height"));
        return len;
    }

    public boolean isPermissionGranted() {
        boolean cameraPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean microPhonePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        return cameraPermissionGranted && microPhonePermissionGranted;
    }

    public void requestPermission() {
        boolean cameraPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean microPhonePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        final List<String> permissionList = new ArrayList();

        if (!cameraPermissionGranted) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!microPhonePermissionGranted) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (permissionList.size() > 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
                mAlertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("Permission")
                        .setMessage("Camera permission is required")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            String[] permissionArray = permissionList.toArray(new String[0]);
                            ActivityCompat.requestPermissions(mActivity, permissionArray, PERMISSIONS_REQUEST);
                        })
                        .show();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.RECORD_AUDIO)) {
                mAlertDialog = new AlertDialog.Builder(mActivity)
                        .setMessage("Microphone permission is required")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            String[] permissionArray = permissionList.toArray(new String[0]);
                            ActivityCompat.requestPermissions(mActivity, permissionArray, PERMISSIONS_REQUEST);
                        })
                        .show();
            } else {
                String[] permissionArray = permissionList.toArray(new String[0]);
                ActivityCompat.requestPermissions(mActivity, permissionArray, PERMISSIONS_REQUEST);
            }
        }
    }

    public int[] findBestFrameRate(List<int[]> frameRateList, int[] requestedFrameRate) {
        int[] bestRate = frameRateList.get(0);
        int requestedAverage = (requestedFrameRate[0] + requestedFrameRate[1]) / 2;
        int bestRateAverage = (bestRate[0] + bestRate[1]) / 2;
        int size = frameRateList.size();
        for (int i = 1; i < size; i++) {
            int[] rate = frameRateList.get(i);
            int rateAverage = (rate[0] + rate[1]) / 2;
            if (Math.abs(requestedAverage - bestRateAverage) >= Math.abs(requestedAverage - rateAverage)) {
                if ((Math.abs(requestedFrameRate[0] - rate[0]) <= Math.abs(requestedFrameRate[0] - bestRate[0])) || (Math.abs(requestedFrameRate[1] - rate[1]) <= Math.abs(requestedFrameRate[1] - bestRate[1]))) {
                    bestRate = rate;
                    bestRateAverage = rateAverage;
                }
            }
        }

        return bestRate;
    }

    public void showEncoderNotExistDialog() {
        mAlertDialog = new AlertDialog.Builder(mActivity)
                .setMessage("Not eligible for broadcast")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                })
                .show();
    }

    public int getCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraId, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    @SuppressLint("StaticFieldLeak")
    public void changeCamera() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Snackbar.make(mGLView, "Only one camera exists", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (sCameraProxy == null) {
            Snackbar.make(mGLView, "First call open camera", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        new AsyncTask<Void, Void, Camera.Parameters>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mGLView.queueEvent(() -> mRenderer.notifyPausing());
                mGLView.onPause();
                mGLView.setOnTouchListener(null);
            }

            @Override
            protected Camera.Parameters doInBackground(Void... voids) {
                releaseCamera();
                try {
                    sCameraProxy = new CameraProxy(currentCameraId);
                    Camera.Parameters parameters = sCameraProxy.getParameters();
                    if (parameters != null) {
                        setCameraParameters(parameters);
                        return parameters;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Camera.Parameters parameters) {
                super.onPostExecute(parameters);
                if (parameters != null) {
                    mGLView.onResume();
                    setRendererPreviewSize();
                } else {
                    Snackbar.make(mGLView, "Camera not running properly", Snackbar.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ILiveVideoBroadcaster getService() {
            return LiveVideoBroadcaster.this;
        }
    }
}
