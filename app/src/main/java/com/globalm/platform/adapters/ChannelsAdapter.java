package com.globalm.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.activities.MessageActivity;
import com.globalm.platform.models.ChannelModel;

import java.util.ArrayList;
import java.util.List;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ViewHolder> {

    private ArrayList<ChannelModel> mChannelsList;
    private Context mContext;

    public ChannelsAdapter(Context context) {
        mContext = context;
        mChannelsList = new ArrayList<>();
    }

    public void setData(List<ChannelModel> mContactItems) {
        mChannelsList.clear();
        mChannelsList.addAll(mContactItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextChannelName.setText(getItem(position).getName());
        holder.mTextMembersCount.setText(String.valueOf(getItem(position).getMemberCount()) + " " + "members");
    }

    @Override
    public int getItemCount() {
        return mChannelsList.size();
    }

    private ChannelModel getItem(int position) {
        return mChannelsList.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextChannelName;
        private TextView mTextMembersCount;
        private ImageView mButtonAccept;
//        private ImageView mButtonOptions;
        private TextView mNotification;

        ViewHolder(View itemView) {
            super(itemView);
            mTextChannelName = itemView.findViewById(R.id.text_channel_name);
            mTextMembersCount = itemView.findViewById(R.id.text_members_count);
            mButtonAccept = itemView.findViewById(R.id.button_accept);
//            mButtonOptions = itemView.findViewById(R.id.button_options);
            mNotification = itemView.findViewById(R.id.image_notification);
            itemView.setOnClickListener(this);
            mButtonAccept.setOnClickListener(this);
//            mButtonOptions.setOnClickListener(this);
            itemView.setClipToOutline(true);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.button_accept:
                    Toast.makeText(itemView.getContext(), "Not implemented yet (message)", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.button_options:
                    Toast.makeText(itemView.getContext(), "Not implemented yet (accept)", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    mContext.startActivity(new Intent(itemView.getContext(), MessageActivity.class));
                    break;
            }
        }
    }
}