package com.globalm.platform.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.globalm.platform.R;
import com.globalm.platform.listeners.OnShareWithIndividualsListener;

import java.util.ArrayList;

public class ShareCopyRightAdapter extends RecyclerView.Adapter<ShareCopyRightAdapter.ViewHolder> {

    private int mType;
    private String[] mShareWithIndividuals;
    private OnShareWithIndividualsListener mOnShareWithIndividualsListener;

    public ShareCopyRightAdapter(int type, String[] shareWithIndividuals, OnShareWithIndividualsListener onShareWithIndividualsListener) {
        mType = type;
        mShareWithIndividuals = shareWithIndividuals;
        mOnShareWithIndividualsListener = onShareWithIndividualsListener;
    }

    public void filterList(ArrayList<String> filter){
        String[] filters = new String[filter.size()];
        for (int i = 0; i < filter.size(); i++) {
            filters[i] = filter.get(i);
        }
        mShareWithIndividuals = filters;
        notifyDataSetChanged();
    }

    private String getItem(int position) {
        return mShareWithIndividuals[position];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder((LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_share_copy_right, viewGroup, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mTextShareWithIndividuals.setText(getItem(i));
    }

    @Override
    public int getItemCount() {
        return mShareWithIndividuals.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextShareWithIndividuals;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextShareWithIndividuals = itemView.findViewById(R.id.txt_share_with_individuals);
            itemView.setOnClickListener(v -> mOnShareWithIndividualsListener.onSelected(mType, getItem(getAdapterPosition())));
        }
    }
}
