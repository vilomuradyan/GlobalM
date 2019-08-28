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
import com.globalm.platform.activities.MessageActivity;
import com.globalm.platform.models.ContactModel;
import com.globalm.platform.utils.AddContactModel;
import com.globalm.platform.utils.RemoveContactsModel;
import org.greenrobot.eventbus.EventBus;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapterMyContacts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;
    WeakReference<Context> mContextWeakReference;
    private ArrayList<ContactModel> mContactsList;

    public ContactsAdapterMyContacts(Context context) {
        mContactsList = new ArrayList<>();
        this.mContextWeakReference = new WeakReference<>(context);
    }

    public void setData(List<ContactModel> mContactItems) {
        mContactsList.clear();
        mContactsList.addAll(mContactItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mContactsList.get(position).isSection) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == SECTION_VIEW) {
            return new ContactsAdapterMyContacts.SectionHeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_header_title, viewGroup, false));
        }

        return new ContactsAdapterMyContacts.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Context context = mContextWeakReference.get();
        if (context == null) {
            return;
        }
        if (SECTION_VIEW == getItemViewType(i)) {

            ContactsAdapter.SectionHeaderViewHolder sectionHeaderViewHolder = (ContactsAdapter.SectionHeaderViewHolder) viewHolder;
            ContactModel sectionItem = mContactsList.get(i);
            sectionHeaderViewHolder.headerTitleTextview.setText(sectionItem.getName());
            return;
        }

        ContactsAdapterMyContacts.ViewHolder viewHolder1 = (ContactsAdapterMyContacts.ViewHolder) viewHolder;
        viewHolder1.mButtonMessage.setVisibility(getItem(i).isAccepted() ? View.VISIBLE : View.GONE);
        viewHolder1.mButtonDelete.setVisibility(getItem(i).isAccepted() ? View.VISIBLE : View.GONE);
        viewHolder1.mTextContactName.setText(getItem(i).getName());
        viewHolder1.mTextContactPosition.setText(getItem(i).getPosition());
        viewHolder1.mTextContactCompany.setText(getItem(i).getCompany());
        viewHolder1.mTextContactCountry.setText(getItem(i).getCountry());

    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    private ContactModel getItem(int position) {
        return mContactsList.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextContactName;
        private TextView mTextContactPosition;
        private TextView mTextContactCompany;
        private TextView mTextContactCountry;
        private ImageView mButtonMessage;
        private ImageView mButtonAccept;
        private ImageView mButtonDelete;
        private ImageView mButtonDecline;

        ViewHolder(View itemView) {
            super(itemView);
            mTextContactName = itemView.findViewById(R.id.text_contact_name);
            mTextContactPosition = itemView.findViewById(R.id.text_contact_position);
            mTextContactCompany = itemView.findViewById(R.id.text_contact_company);
            mTextContactCountry = itemView.findViewById(R.id.text_contact_country);
            mButtonMessage = itemView.findViewById(R.id.button_message);
            mButtonAccept = itemView.findViewById(R.id.button_accept);
            mButtonDelete = itemView.findViewById(R.id.button_delete);
            mButtonDecline = itemView.findViewById(R.id.button_decline);
            itemView.setOnClickListener(this);
            mButtonMessage.setOnClickListener(this);
            mButtonAccept.setOnClickListener(this);
            mButtonDelete.setOnClickListener(this);
            mButtonDecline.setOnClickListener(this);
            itemView.setClipToOutline(true);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.button_message:
                    mContextWeakReference.get().startActivity(new Intent(itemView.getContext(), MessageActivity.class));
                    break;

                case R.id.button_accept:
                    try {
                        EventBus.getDefault().post(new AddContactModel(mContactsList.get(position)));
                    } catch (Exception e) {
                        EventBus.getDefault().post(new AddContactModel(mContactsList.get(position)));
                    }

                    mContactsList.remove(position);
                    notifyItemRemoved(position);
                    break;

                case R.id.button_delete:

                    try {
                        EventBus.getDefault().post(new RemoveContactsModel(position));
                    } catch (Exception e) {
                        EventBus.getDefault().post(new RemoveContactsModel(position));
                    }

//                    mContactsList.remove(position);
//                    notifyItemRemoved(position);
//                    changeData();
                    break;

                case R.id.button_decline:
                    mContactsList.remove(position);
                    notifyItemRemoved(position);
                    break;

                default:
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), ContactActivity.class));
                    break;
            }
        }
    }

    public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitleTextview;

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
            headerTitleTextview = itemView.findViewById(R.id.headerTitleTextview);
        }
    }
}
