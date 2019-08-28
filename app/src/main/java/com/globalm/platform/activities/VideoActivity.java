package com.globalm.platform.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.globalm.platform.R;

public class VideoActivity extends BaseActivity {
    private static final String VIDEO_URL_KEY = "VIDEO_URL_KEY";
    private static final String VIDEO_POSITION_KEY = "VIDEO_POSITION_KEY";

    private VideoView videoView;
    private String videoUrl;

    public static void start(Context context, String videoUrl) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(VIDEO_URL_KEY, videoUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getVideoUrl();
        findView(R.id.back_button).setOnClickListener((v) -> finish());

        Integer savedPos = getSavePos(savedInstanceState);
        setupVideoView(savedPos);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(VIDEO_POSITION_KEY, videoView.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    private void getVideoUrl() {
        if (getIntent().getExtras() != null) {
            videoUrl = getIntent().getExtras().getString(VIDEO_URL_KEY);
        } else {
            finish();
        }
    }

    private Integer getSavePos(@Nullable Bundle savedInstanceState) {
        Integer savedPos;
        if (savedInstanceState != null) {
            savedPos = savedInstanceState.getInt(VIDEO_POSITION_KEY);
        } else {
            savedPos = null;
        }
        return savedPos;
    }

    private void setupVideoView(Integer savedPos) {
        videoView = findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(getVideoUri(videoUrl));
        videoView.requestFocus();
        videoView.setOnErrorListener((mp, what, extra) -> {
            showMessage(getString(R.string.an_error_has_occured), Toast.LENGTH_SHORT).show();
            return true;
        });

        if (savedPos != null) {
            videoView.seekTo(savedPos);
        }
        videoView.start();
    }

    private Uri getVideoUri(String videoUrl) {
        return Uri.parse(videoUrl);
    }
}
