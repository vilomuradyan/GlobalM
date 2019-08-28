package com.globalm.platform.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.globalm.platform.R;
import com.globalm.platform.models.PricingModel;
import com.squareup.picasso.Picasso;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PricingAdapter extends RecyclerView.Adapter<PricingAdapter.ViewHolder> {

    private ArrayList<PricingModel> mPricingList;
    WeakReference<Context> mContextWeakReference;

   public PricingAdapter(Context context){
       mPricingList = new ArrayList<>();
       this.mContextWeakReference = new WeakReference<>(context);
   }

    public void setData(List<PricingModel> mPricingItems) {
        mPricingList.clear();
        mPricingList.addAll(mPricingItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pricing, viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
       viewHolder.mTitle.setText(getItem(i).getTitle());
       viewHolder.mPrice.setText(getItem(i).getPrice());
       Picasso.get().load(getItem(i).getImageRemove()).into(viewHolder.mImageRemove);
    }

    @Override
    public int getItemCount() {
        return mPricingList.size();
    }

    private PricingModel getItem(int position) {
        return mPricingList.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitle;
        private EditText mPrice;
        private ImageView mImageRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.text_price_title);
            mPrice = itemView.findViewById(R.id.field_price);
            mImageRemove = itemView.findViewById(R.id.image_price_remove);

            mTitle.setOnClickListener(this);
            mPrice.setOnClickListener(this);
            mImageRemove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()){
                case R.id.image_price_remove:
                    mPricingList.remove(position);
                    notifyItemRemoved(position);
                    break;
            }
        }
    }
}
