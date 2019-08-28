package com.globalm.platform.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.globalm.platform.R;
import com.globalm.platform.fragments.CreateContentFragment;
import com.globalm.platform.models.Item;
import java.io.Serializable;

public class CreateContentActivity extends BaseActivity {
    private static final String EXTRA_URI = "EXTRA_URI";
    private static final String EXTRA_ITEM = "EXTRA_ITEM";
    private static final String EXTRA_POSITION_TO_OPEN = "EXTRA_POSITION_TO_OPEN";
    private CreateContentFragment createContentFragment;

    public static void start(Context context, int position) {
        Intent i = new Intent(context, CreateContentActivity.class);
        i.putExtra(EXTRA_POSITION_TO_OPEN, position);
        context.startActivity(i);
    }

    public static void start(Context context, Uri uri) {
        Intent i = new Intent(context, CreateContentActivity.class);
        i.putExtra(EXTRA_URI, uri);
        i.putExtra(EXTRA_POSITION_TO_OPEN, CreateContentFragment.POSITION_UPLOAD);
        context.startActivity(i);
    }

    public static void start(Context context, Item item){
        Intent i = new Intent(context, CreateContentActivity.class);
        i.putExtra(EXTRA_ITEM, item);
        i.putExtra(EXTRA_POSITION_TO_OPEN, CreateContentFragment.POSITION_UPLOAD);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_blue));
        setContentView(R.layout.activity_create_content);
        createContentFragment = (CreateContentFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        createContentFragment.setPositionToOpen(getIntent().getIntExtra(EXTRA_POSITION_TO_OPEN, CreateContentFragment.POSITION_CREATE_EVENT));
        setup();
        Parcelable uri = getIntent().getParcelableExtra(EXTRA_URI);
        if (uri instanceof  Uri) {
            createContentFragment.setUploadFileUri((Uri) uri);
        }
        Serializable item = getIntent().getSerializableExtra(EXTRA_ITEM);
        if (item instanceof Item){
            createContentFragment.setUploadData((Item) item);
        }
    }

    private void setup() {
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });
        ((TextView)findViewById(R.id.text_title)).setText(R.string.title_create_content);
    }

}