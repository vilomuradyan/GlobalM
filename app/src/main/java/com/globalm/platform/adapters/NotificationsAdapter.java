package com.globalm.platform.adapters;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.models.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private ArrayList<NotificationModel> mNotificationsList;

    public NotificationsAdapter() {
        mNotificationsList = new ArrayList<>();
    }

    public void setData(List<NotificationModel> mContactItems) {
        mNotificationsList.clear();
        mNotificationsList.addAll(mContactItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = getItem(position).getName();
        SpannableString spannableString = new SpannableString(name + " has send you a message");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.mTextName.setText(spannableString);
        holder.mTextTime.setText(getItem(position).getTime());
    }

    @Override
    public int getItemCount() {
        return mNotificationsList.size();
    }

    private NotificationModel getItem(int position) {
        return mNotificationsList.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextName;
        private TextView mTextTime;
        private ImageView mButtonOptions;

        ViewHolder(View itemView) {
            super(itemView);
            mTextName = itemView.findViewById(R.id.text_name);
            mTextTime = itemView.findViewById(R.id.text_time);
            mButtonOptions = itemView.findViewById(R.id.button_options);
            mButtonOptions.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.button_options:
                    Toast.makeText(itemView.getContext(), "Not implemented yet (options)", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(itemView.getContext(), "Not implemented yet (itemView)", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}