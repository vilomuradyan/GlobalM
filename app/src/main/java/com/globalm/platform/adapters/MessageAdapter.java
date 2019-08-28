package com.globalm.platform.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.globalm.platform.R;
import com.globalm.platform.models.MessageModel;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MessageModel> mContactsList;

    public MessageAdapter(Context context) {
        mContactsList = new ArrayList<>();
        mContext = context;
    }

    public void setData(List<MessageModel> mContactItems) {
        mContactsList.clear();
        mContactsList.addAll(mContactItems);
        notifyDataSetChanged();
    }

    private MessageModel getItem(int position) {
        return mContactsList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get().load(getItem(i).getImage()).into(viewHolder.mImageUser);
        viewHolder.mTextName.setText(getItem(i).getName());
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageUser;
        private TextView mTextName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageUser = itemView.findViewById(R.id.image_user);
            mTextName = itemView.findViewById(R.id.text_user_name);
            mImageUser.setOnClickListener(this);
            mTextName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()){
                case R.id.image_user:
                    Toast.makeText(itemView.getContext(), "No implemented yet (Image User)", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
