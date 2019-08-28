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
import com.globalm.platform.R;
import com.globalm.platform.activities.ContactActivity;
import com.globalm.platform.activities.ContentDetailsActivity;
import com.globalm.platform.models.ContactModel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ContactSuggestAdapter extends RecyclerView.Adapter<ContactSuggestAdapter.ViewHolder> {

    WeakReference<Context> mContextWeakReference;
    private ArrayList<ContactModel> mContactsList;

    public ContactSuggestAdapter(Context context) {
        mContactsList = new ArrayList<>();
        this.mContextWeakReference = new WeakReference<>(context);
    }

    public void setData(List<ContactModel> mContactItems) {
        mContactsList.clear();
        mContactsList.addAll(mContactItems);
        notifyDataSetChanged();
    }

    private ContactModel getItem(int position) {
        return mContactsList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_suggested_contacts, viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mTextContactName.setText(getItem(i).getName());
        viewHolder.mTextContactCountry.setText(getItem(i).getCountry());
        viewHolder.mTextContactPosition.setText(getItem(i).getPosition());
        viewHolder.mTextCompany.setText(getItem(i).getCompany());
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageContact;
        private ImageView mButtonMessage;
        private ImageView mButtonDecline;
        private TextView mTextContactName;
        private TextView mTextContactPosition;
        private TextView mTextContactCountry;
        private TextView mTextWorked;
        private TextView mTextCompany;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageContact = itemView.findViewById(R.id.image_contact);
            mButtonMessage = itemView.findViewById(R.id.button_message);
            mButtonDecline = itemView.findViewById(R.id.button_decline);
            mTextContactName = itemView.findViewById(R.id.text_contact_name);
            mTextContactPosition = itemView.findViewById(R.id.text_contact_position);
            mTextContactCountry = itemView.findViewById(R.id.text_contact_country);
            mTextWorked = itemView.findViewById(R.id.text_worked);
            mTextCompany = itemView.findViewById(R.id.text_contact_company);

            mImageContact.setOnClickListener(this);
            mButtonMessage.setOnClickListener(this);
            mButtonDecline.setOnClickListener(this);
            mTextContactName.setOnClickListener(this);
            mTextContactPosition.setOnClickListener(this);
            mTextContactCountry.setOnClickListener(this);
            mTextWorked.setOnClickListener(this);
            mTextCompany.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()){
                default:
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), ContactActivity.class));
                    break;
            }
        }
    }
}
