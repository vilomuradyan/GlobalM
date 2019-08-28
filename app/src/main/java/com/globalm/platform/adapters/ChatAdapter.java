package com.globalm.platform.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.ChatModel;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.Owner;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ChatModel> mModels;

    public ChatAdapter(Context context) {
        mContext = context;
        mModels = new ArrayList<>();
    }

    public void setData(ArrayList<ChatModel> chatModels) {
        mModels.clear();
        mModels.addAll(chatModels);
        notifyDataSetChanged();
    }

    public ChatModel getItem(int position) {
        return mModels.get(position);
    }

    public void addItem(ChatModel chatModel){
        mModels.add(chatModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get().load(getItem(i).getImage()).into(viewHolder.mImageContact);
        viewHolder.mTextMessage.setText(getItem(i).getMessage());
        viewHolder.mTextName.setText(getItem(i).getName());
        viewHolder.mTextTime.setText(getItem(i).getTime());
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageContact;
        private TextView mTextTime;
        private TextView mTextName;
        private TextView mTextMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageContact = itemView.findViewById(R.id.image_contact);
            mTextTime = itemView.findViewById(R.id.text_time);
            mTextName = itemView.findViewById(R.id.text_message_name);
            mTextMessage = itemView.findViewById(R.id.text_message);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }
    }
}
