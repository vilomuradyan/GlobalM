package com.globalm.platform.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.adapters.UploadContentAdapter;
import com.globalm.platform.network.RequestManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends BaseActivity {

    public static final String GEOLOCATION_LNG_KEY = "GEOLOCATION_LNG_KEY";
    public static final String GEOLOCATION_LAT_KEY = "GEOLOCATION_LAT_KEY";
    public static final String LOCATION_KEY = "LOCATION_KEY";
    public static final String SUBTITLE_KEY = "SUBTITLE_KEY";
    public static final String TITLE_KEY = "TITLE_KEY";
    public static final String FILE_ABSOLUTE_PATH_KEY = "FILE_ABSOLUTE_PATH_KEY";
    public static final String IS_FILE_FROM_FILES_DIR_KEY = "IS_FILE_FROM_FILES_DIR_KEY";
    public static final int REQUEST_CODE_UPLOAD = 9002;

    public static void startForResult(
            Fragment fragment,
            String geolocationLng,
            String geolocationLat,
            String location,
            String subtitle,
            String title,
            ArrayList<String> fileAbsolutePathList,
            boolean isFileFromFilesDir) {
        Intent intent = new Intent(fragment.getContext(), UploadActivity.class);
        intent.putExtra(GEOLOCATION_LNG_KEY, geolocationLng);
        intent.putExtra(GEOLOCATION_LAT_KEY, geolocationLat);
        intent.putExtra(LOCATION_KEY, location);
        intent.putExtra(SUBTITLE_KEY, subtitle);
        intent.putExtra(TITLE_KEY, title);
        intent.putStringArrayListExtra(FILE_ABSOLUTE_PATH_KEY, fileAbsolutePathList);
        intent.putExtra(IS_FILE_FROM_FILES_DIR_KEY, isFileFromFilesDir);

        fragment.startActivityForResult(intent, REQUEST_CODE_UPLOAD);
    }

    private RecyclerView recyclerView;
    private ImageView backButton;
    private UploadContentAdapter adapter;
    private boolean processed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || bundle.getStringArrayList(FILE_ABSOLUTE_PATH_KEY) == null) {
            return;
        }
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            setResult();
            finish();
        });
        ((TextView)findViewById(R.id.text_title)).setText(R.string.upload);
        List<String> filePathList = bundle.getStringArrayList(FILE_ABSOLUTE_PATH_KEY);
        adapter = new UploadContentAdapter(
                createFilesList(filePathList),
                RequestManager.getInstance(),
                bundle.getString(TITLE_KEY),
                bundle.getString(SUBTITLE_KEY),
                bundle.getString(LOCATION_KEY),
                bundle.getString(GEOLOCATION_LNG_KEY),
                bundle.getString(GEOLOCATION_LAT_KEY),
                bundle.getBoolean(IS_FILE_FROM_FILES_DIR_KEY),
                result -> processed = true);
        recyclerView = findViewById(R.id.upload_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }

    private List<File> createFilesList(List<String> filePathList) {
        List<File> list = new ArrayList<>();
        for (String filePath : filePathList) {
            list.add(new File(filePath));
        }
        return list;
    }

    private void setResult() {
        if (processed) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
    }
}
