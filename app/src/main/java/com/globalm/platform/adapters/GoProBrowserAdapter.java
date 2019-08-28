package com.globalm.platform.adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.globalm.platform.R;
import com.globalm.platform.models.gopro.hero4.GoProVideoDataContainer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GoProBrowserAdapter extends RecyclerView.Adapter<GoProBrowserAdapter.ThumbnailViewHolder> {

    private List<GoProVideoDataContainer> videoContainerList;
    private OnVideoSelectedListener listener;

    public GoProBrowserAdapter(OnVideoSelectedListener listener) {
        this.listener = listener;
        videoContainerList = new ArrayList<>();
    }

    public void setVideoContainerList(List<GoProVideoDataContainer> videoContainerList) {
        this.videoContainerList = videoContainerList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_thumbnail, viewGroup, false);
        return new ThumbnailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder thumbnailViewHolder, int position) {
        GoProVideoDataContainer container = videoContainerList.get(position);
        String url = container.getThumbnailUrl();
        if (url != null) {
            Picasso.get().load(url).into(thumbnailViewHolder.videoThumbnail);
        } else {
            thumbnailViewHolder.videoThumbnail.setBackgroundColor(
                    ContextCompat.getColor(
                            thumbnailViewHolder.itemView.getContext(),
                            R.color.color_grey_light)
            );
        }
        thumbnailViewHolder.videoThumbnail.setOnClickListener(v -> listener.onVideoSelected(container));
    }

    @Override
    public int getItemCount() {
        return videoContainerList.size();
    }

    public static class ThumbnailViewHolder extends RecyclerView.ViewHolder {
        private ImageView videoThumbnail;

        public ThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
        }
    }

    @FunctionalInterface
    public interface OnVideoSelectedListener {
        void onVideoSelected(GoProVideoDataContainer videoDataContainer);
    }
}
