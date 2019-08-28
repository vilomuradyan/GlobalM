package com.globalm.platform.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.models.ContentHistoryModel;

import java.util.List;

public class ContentHistoryAdapter extends RecyclerView.Adapter<ContentHistoryAdapter.ContentDetailsViewHolder> {

    private Context mcontext;
    private List<ContentHistoryModel> models;

    public ContentHistoryAdapter(Context mcontext, List<ContentHistoryModel> models) {
        this.mcontext = mcontext;
        this.models = models;
    }

    @NonNull
    @Override
    public ContentDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history, viewGroup, false);
        return new ContentDetailsViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentDetailsViewHolder contentDetailsViewHolder, int i) {
        contentDetailsViewHolder.username.setText(models.get(i).getUserName());
        contentDetailsViewHolder.userExclusive.setText(models.get(i).getUserPrice());
        contentDetailsViewHolder.date.setText(models.get(i).getTime());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class ContentDetailsViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private TextView username;
        private TextView userExclusive;
        private TextView date;

        public ContentDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.image_history);
            username = itemView.findViewById(R.id.text_history_name);
            userExclusive = itemView.findViewById(R.id.text_history_exclusive);
            date = itemView.findViewById(R.id.text_history_date);
        }
    }
}
