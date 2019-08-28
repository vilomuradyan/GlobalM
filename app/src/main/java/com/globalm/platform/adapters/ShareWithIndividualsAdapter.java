package com.globalm.platform.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.models.ShareWithIndividualsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShareWithIndividualsAdapter extends RecyclerView.Adapter<ShareWithIndividualsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ShareWithIndividualsModel> mModels;
    private OnCountListener mOnCountListener;
    public ShareWithIndividualsAdapter(Context context, OnCountListener onCountListener) {
        mContext = context;
        mModels = new ArrayList<>();
        mOnCountListener = onCountListener;
    }

    public void setData(ArrayList<ShareWithIndividualsModel> models){
        mModels.clear();
        mModels.addAll(models);
        notifyDataSetChanged();
    }

    public void addItem(ShareWithIndividualsModel model){
        boolean isEquals = false;
        for (int i = 0; i < mModels.size(); i++) {
            if (mModels.get(i).getUserEmail().equals(model.getUserEmail())){
                isEquals = true;
                break;
            }
        }
        if (!isEquals){
            mModels.add(model);
            notifyDataSetChanged();
        }

        if (mModels.size() == 0){
            mOnCountListener.sendCount(true);
        }else {
            mOnCountListener.sendCount(false);
        }
    }

    private ShareWithIndividualsModel getItem(int position){
        return mModels.get(position);
    }

    public interface OnCountListener{
        void sendCount(boolean isNull);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_share_with_individuals, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get().load(getItem(i).getImageUser()).into(viewHolder.mImageUser);
        Picasso.get().load(getItem(i).getImageDelete()).into(viewHolder.mImageDelete);
        viewHolder.mTextEmail.setText(getItem(i).getUserEmail());
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageUser;
        private ImageView mImageDelete;
        private TextView mTextEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageUser = itemView.findViewById(R.id.image_user_share_with_individuals);
            mImageDelete = itemView.findViewById(R.id.image_delete_share_with_individuals);
            mTextEmail = itemView.findViewById(R.id.text_email_share_with_individuals);

            mImageDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()){
                case R.id.image_delete_share_with_individuals:
                    mModels.remove(position);
                    notifyDataSetChanged();
                    if (mModels.size() == 0){
                        mOnCountListener.sendCount(true);
                    }else {
                        mOnCountListener.sendCount(false);
                    }
                    break;
            }
        }
    }
}
