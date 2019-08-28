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
import com.globalm.platform.models.MessageChatModel;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MessageChatModel> mModels;

    public MessageChatAdapter(Context context) {
        mContext = context;
        mModels = new ArrayList<>();
    }

    public void setData(ArrayList<MessageChatModel> models){
        mModels.clear();
        mModels.addAll(models);
        notifyDataSetChanged();
    }

    public MessageChatModel getItem(int position){
        return mModels.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_chat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mTextTime.setText(getItem(i).getTime());
        viewHolder.mFieldMessage.setText(getItem(i).getMessage());
        viewHolder.mUserName.setText(getItem(i).getName());
        Picasso.get().load(getItem(i).getImage()).into(viewHolder.mImageUser);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mImageUser;
        private TextView mTextTime;
        private TextView mUserName;
        private TextView mFieldMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageUser = itemView.findViewById(R.id.image_contact);
            mTextTime = itemView.findViewById(R.id.text_message_time);
            mUserName = itemView.findViewById(R.id.text_user_name);
            mFieldMessage = itemView.findViewById(R.id.field_message_chat);
        }
    }
}
