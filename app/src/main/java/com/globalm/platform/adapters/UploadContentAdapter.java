package com.globalm.platform.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.globalm.platform.R;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.Item;
import com.globalm.platform.models.ProgressRequestBody;
import com.globalm.platform.models.RequestBodyBuilder;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.Utils;
import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class UploadContentAdapter extends RecyclerView.Adapter<UploadContentAdapter.UploadViewHolder> {

    private List<File> files;
    private RequestManager requestManager;
    public String title;
    private String subtitle;
    private String location;
    private String geolocationLng;
    private String geolocationLat;
    private boolean isFileFromFilesDir;
    private Utils.InvokeCallback<Void> onUploadEndedListener;

    public UploadContentAdapter(
            List<File> files,
            RequestManager requestManager,
            String title,
            String subtitle,
            String location,
            String geolocationLng,
            String geolocationLat,
            boolean isFileFromFilesDir,
            Utils.InvokeCallback<Void> onUploadEndedListener) {
        this.files = files;
        this.requestManager = requestManager;
        this.title = title;
        this.subtitle = subtitle;
        this.location = location;
        this.geolocationLng = geolocationLng;
        this.geolocationLat = geolocationLat;
        this.isFileFromFilesDir = isFileFromFilesDir;
        this.onUploadEndedListener = onUploadEndedListener;
    }


    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflateViewHolder(viewGroup.getContext(), viewGroup, R.layout.item_upload);
        return new UploadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadViewHolder viewHolder, int position) {
        File file = files.get(position);
        Context context = viewHolder.itemView.getContext();
        String errorMessage = context.getString(R.string.an_error_has_occured);

        viewHolder.filename.setText(file.getName());
        ProgressRequestBody fileBody = new ProgressRequestBody(
                file,
                "video",
                new ProgressRequestBody.UploadCallbacks() {
                    @Override
                    public void onProgressUpdate(int percentage) {
                        viewHolder.progress.setText(percentage + "%");
                        viewHolder.progressBar.setProgress(percentage);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(
                                viewHolder.itemView.getContext(),
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinish() {
                        viewHolder.progressBar.setProgress(100);
                        viewHolder.progressBar.setVisibility(View.GONE);
                        viewHolder.progress.setVisibility(View.GONE);
                    }
                });

        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "source",
                file.getName(),
                fileBody);
        Map<String, RequestBody> map = new RequestBodyBuilder()
                .setTitle(this.title)
                .setSubtitle(this.subtitle)
                .setGeoName(location)
                .setGeoLocationLat(this.geolocationLat)
                .setGeoLocationLng(this.geolocationLng)
                .build();

        requestManager.content(
                map,
                body,
                new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Item>>>() {
                    @Override
                    public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<Item>> response) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        viewHolder.progress.setVisibility(View.GONE);
                        Utils.clearFileFromFilesDir(isFileFromFilesDir, file);
                        viewHolder.round_background.setBackground(
                                ContextCompat.getDrawable(context, R.drawable.ic_check));
                        if (position == getItemCount() - 1) {
                            onUploadEndedListener.invoke(null);
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        viewHolder.progress.setVisibility(View.GONE);
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Utils.clearFileFromFilesDir(isFileFromFilesDir, file);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    private View inflateViewHolder(Context context, ViewGroup viewGroup, @LayoutRes int resId) {
        return LayoutInflater.from(context).inflate(resId, viewGroup, false);
    }

    static class UploadViewHolder extends RecyclerView.ViewHolder {
        private TextView progress;
        private ProgressBar progressBar;
        private TextView filename;
        private ImageView round_background;

        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progress);
            progressBar = itemView.findViewById(R.id.progress_bar);
            filename = itemView.findViewById(R.id.filename);
            round_background = itemView.findViewById(R.id.round_background);
        }
    }
}
