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
import com.globalm.platform.models.TagsModel;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    private Context mContext;
    private List<TagsModel> mModels;

    public TagsAdapter(Context context) {
        mContext = context;
        mModels = new ArrayList<>();
    }

    public void setData(ArrayList<TagsModel> models){
        mModels.clear();
        mModels.addAll(models);
        notifyDataSetChanged();
    }

    public void addItem(TagsModel tagsModel){
        boolean isEquals = false;
        for (int i = 0; i < mModels.size(); i++) {
            if (mModels.get(i).getTagsName().equals(tagsModel.getTagsName())){
                isEquals = true;
                break;
            }
        }
        if (!isEquals){
            mModels.add(tagsModel);
        }

        notifyDataSetChanged();
    }

    public TagsModel getItem(int position){
        return mModels.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tags, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get().load(getItem(i).getImageDelete()).into(viewHolder.mImageDelete);
        viewHolder.mTextName.setText(getItem(i).getTagsName());
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageDelete;
        private TextView mTextName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageDelete = itemView.findViewById(R.id.image_delete_tags);
            mTextName = itemView.findViewById(R.id.text_tags);
            mImageDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()){
                case R.id.image_delete_tags:
                    mModels.remove(position);
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
