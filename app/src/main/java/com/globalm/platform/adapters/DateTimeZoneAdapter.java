package com.globalm.platform.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.globalm.platform.R;
import com.globalm.platform.listeners.OnDateTimeZoneSelectedListener;
import com.globalm.platform.models.ContactModel;

import java.util.ArrayList;

public class DateTimeZoneAdapter extends RecyclerView.Adapter<DateTimeZoneAdapter.ViewHolder> {

    private int mType;
    private String[] mDateTimeZoneList;
    private OnDateTimeZoneSelectedListener mOnDateTimeZoneSelectedListener;

    public DateTimeZoneAdapter(int type, String[] dateTimeZoneList, OnDateTimeZoneSelectedListener onDateTimeZoneSelectedListener) {
        mType = type;
        mDateTimeZoneList = dateTimeZoneList;
        mOnDateTimeZoneSelectedListener = onDateTimeZoneSelectedListener;
    }

    public void filterList(ArrayList<String> filter){
        mDateTimeZoneList = filter.toArray(new String[0]);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_time_zone, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextTimeZone.setText(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDateTimeZoneList.length;
    }

    private String getItem(int position) {
        return mDateTimeZoneList[position];
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextTimeZone;

        ViewHolder(View itemView) {
            super(itemView);
            mTextTimeZone = itemView.findViewById(R.id.text_time_zone);
            itemView.setOnClickListener(v -> mOnDateTimeZoneSelectedListener.onTimeZoneSelected(mType, getItem(getAdapterPosition())));
        }
    }
}