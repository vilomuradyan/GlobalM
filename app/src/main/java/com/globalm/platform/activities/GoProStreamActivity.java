package com.globalm.platform.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.globalm.platform.R;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.gopro.hero4.GoProStatusResponse;
import com.globalm.platform.network.GoProAPI;
import com.globalm.platform.network.GoProRequestManager;
import com.globalm.platform.utils.Utils;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import java.util.ArrayList;
import timber.log.Timber;

public class GoProStreamActivity extends BaseActivity implements IVLCVout.Callback {
    private static final String VIDEO_PATH = "udp://@:8555/gopro";
    private static final int STREAM_SESSION_RESTART_DELAY = 7000;
    private static final String[] FFMPEG_STREAM_COMMAND = {
            "-fflags", "nobuffer" , "-f:v", "mpegts", "-probesize", "8192" ,"-i",
            "udp://:8554", "-f", "mpegts","udp://127.0.0.1:8555/gopro?pkt_size=64"};

    public static void start(Context context) {
        Intent intent = new Intent(context, GoProStreamActivity.class);
        context.startActivity(intent);
    }

    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    @Nullable
    private LibVLC libvlc;
    @Nullable
    private MediaPlayer mediaPlayer = null;
    private GoProRequestManager requestManager;
    private Handler sessionRestartHandler;
    private FFmpeg fFmpeg;
    private Runnable cameraStreamingSession = new Runnable() {
        @Override
        public void run() {
            restartStream(null);
            startSessionRestartHandler(this);
        }
    };
    private MediaPlayer.EventListener mediaPlayerEventListener = event -> {
        if (event.type == MediaPlayer.Event.EndReached) {
            logMessage("MediaPlayerEndReached");
            releasePlayer();
        }
    };
    private int mVideoWidth;
    private int mVideoHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gopro_stream);

        sessionRestartHandler = new Handler();
        surfaceView = findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        fFmpeg = FFmpeg.getInstance(getApplicationContext());
        requestManager = GoProRequestManager.getInstance();
        mVideoHeight = 0;
        mVideoWidth = 0;
        loadFFmpegBinary(fFmpeg);
        setVideoHD();
        startStreaming(fFmpeg, FFMPEG_STREAM_COMMAND);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) { }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) { }

    private void loadFFmpegBinary(FFmpeg fFmpeg) {
        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    super.onFailure();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            handleException(e);
        }
    }

    private void startStreaming(FFmpeg fFmpeg, String[] command) {
        restartStream(result -> {
            try {
                executeFFmpegStreamRead(fFmpeg, command);
                createPlayer(VIDEO_PATH);
            } catch (FFmpegCommandAlreadyRunningException e) {
                handleException(e);
            }
        });
    }

    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if (holder == null || surfaceView == null)
            return;

        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        holder.setFixedSize(mVideoWidth, mVideoHeight);
        ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
        lp.width = w;
        lp.height = h;
        surfaceView.setLayoutParams(lp);
        surfaceView.invalidate();
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        setSize(mVideoWidth, mVideoHeight);
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        showStreamFailedMessage();
    }

    private void createPlayer(String videoPath) {
        releasePlayer();
        createLibVLC(createVLCStreamingOptions());
        createMediaPlayer();
        setupVideoOutput();

        Media media = new Media(libvlc, Uri.parse(videoPath));
        if (mediaPlayer != null) {
            mediaPlayer.setMedia(media);
            mediaPlayer.play();
        }
        setSize(mVideoWidth, mVideoHeight);
    }

    private void createLibVLC(ArrayList<String> options) {
        libvlc = new LibVLC(this, options);
        holder.setKeepScreenOn(true);
    }

    private ArrayList<String> createVLCStreamingOptions() {
        ArrayList<String> options = new ArrayList<>();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch");
        options.add("-vvv");
        return options;
    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer(libvlc);
        mediaPlayer.setEventListener(mediaPlayerEventListener);
    }

    private void setupVideoOutput() {
        if (mediaPlayer != null) {
            final IVLCVout vout = mediaPlayer.getVLCVout();
            vout.setVideoView(surfaceView);
            vout.addCallback(this);
            vout.attachViews();
        }
    }

    private void releasePlayer() {
        if (libvlc == null || mediaPlayer == null)
            return;

        mediaPlayer.stop();
        final IVLCVout vout = mediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        holder = null;
        libvlc.release();
        libvlc = null;
    }

    private void executeFFmpegStreamRead(FFmpeg fFmpeg, String[] command)
            throws FFmpegCommandAlreadyRunningException {

        fFmpeg.execute(command, new ExecuteBinaryResponseHandler() {

            @Override
            public void onStart() {
                //TODO SESSION PERION
                //USED TO MAKE SESSION LONGER OTHERWISE SESSION IS 25sec
                //startSessionRestartHandler(cameraStreamingSession);
            }

            @Override
            public void onProgress(String message) {
                logMessage(message);
            }

            @Override
            public void onFailure(String message) {
                Timber.e("onFailure");
                showStreamFailedMessage();
            }

            @Override
            public void onSuccess(String message) {
                logMessage(message);
            }

            @Override
            public void onFinish() {
                logMessage("onFinish");
            }
        });
    }

    private void restartStream(@Nullable Utils.InvokeCallback<Void> callback) {
        requestManager.restartStream(handleRestartStreamCallback(callback));
    }

    private void logMessage(String msg) {
        Timber.d(msg);
    }

    private void showStreamFailedMessage() {
    }

    private void startSessionRestartHandler(Runnable runnable) {
        sessionRestartHandler.postDelayed(runnable, STREAM_SESSION_RESTART_DELAY);
    }

    private void setVideoHD() {
        setGoProSettings(
                GoProAPI.GO_PRO_HERO4_VIDEO_RESOLUTION_PARAM,
                GoProAPI.GO_PRO_HERO4_HD_QUALITY);
        setGoProSettings(
                GoProAPI.GO_PRO_HERO4_BITRATE_PARAM,
                GoProAPI.GO_PRO_HERO4_VIDEO_BITRATE_4MPS_QUALITY);
    }

    private void setGoProSettings(String param, String value) {
        requestManager.config(param, value, new CallbackListener<Void>() {
            @Override
            public void onSuccess(Void o) {

            }

            @Override
            public void onFailure(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    private CallbackListener<GoProStatusResponse> handleRestartStreamCallback(@Nullable Utils.InvokeCallback<Void> callback) {
        return new CallbackListener<GoProStatusResponse>() {
            @Override
            public void onSuccess(GoProStatusResponse o) {
                if (callback != null)
                    callback.invoke(null);
            }

            @Override
            public void onFailure(Throwable error) {
            }
        };
    }
}
