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
import com.globalm.platform.models.Contact;
import com.globalm.platform.models.ContactModelNew;
import com.globalm.platform.models.User;
import com.globalm.platform.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SECTION_VIEW = 0;
    private static final int CONTENT_VIEW = 1;
    private ArrayList<ContactModelNew> contactModels;
    private ContactActionListener contactActionListener;

    public ContactsAdapter(ContactActionListener contactActionListener) {
        this.contactActionListener = contactActionListener;
        contactModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SECTION_VIEW) {
            return new SectionHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_title, parent, false));
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (SECTION_VIEW == getItemViewType(i)) {
            SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) viewHolder;
            ContactModelNew sectionItem = contactModels.get(i);
            sectionHeaderViewHolder.headerTitleTextview.setText(sectionItem.getSectionName());
            return;
        }
        ViewHolder viewHolder1 = (ViewHolder) viewHolder;
        ContactModelNew model = getItem(i);
        Contact.Metadata metadata = model.getContact().getMetadata();
        boolean isConnected = false;
        boolean isRequested = false;
        if (metadata != null) {
            isConnected = metadata.isConnected();
            isRequested = metadata.isRequested();
        }
        viewHolder1.mButtonMessage.setVisibility(View.VISIBLE);
        viewHolder1.mButtonAccept.setVisibility(isRequested ? View.VISIBLE : View.GONE);
        viewHolder1.mButtonDecline.setVisibility(isRequested ? View.VISIBLE : View.GONE);
        viewHolder1.mButtonDelete.setVisibility(isConnected ? View.VISIBLE : View.GONE);
        viewHolder1.mButtonSendRequest.setVisibility(isConnected || isRequested ? View.GONE : View.VISIBLE);

        User user = model.getContact().getUser();

        if (user != null) {
            viewHolder1.mTextContactName.setText(user.getFullName());
            User.Profile profile = user.getProfile();
            if (profile != null) {
                viewHolder1.mTextContactPosition.setText(profile.getTitle());
                viewHolder1.mTextContactCompany.setText("");
                viewHolder1.mTextContactCountry.setText(profile.getCountry());
                if (profile.getThumb() != null) {
                    viewHolder1.mImageViewContact.setVisibility(View.VISIBLE);
                    Picasso.get().load(profile.getThumb()).transform(new CircleTransformation()).into(viewHolder1.mImageViewContact);
                } else {
                    viewHolder1.mImageViewContact.setVisibility(View.GONE);
                }
            } else {
                viewHolder1.mTextContactPosition.setText("");
                viewHolder1.mTextContactCountry.setText("");
                viewHolder1.mImageViewContact.setVisibility(View.GONE);
            }
        } else {
            viewHolder1.mTextContactName.setText("");
            viewHolder1.mTextContactPosition.setText("");
            viewHolder1.mTextContactCountry.setText("");
            viewHolder1.mImageViewContact.setVisibility(View.GONE);
        }
        viewHolder1.itemView.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contactModels.get(viewHolder.getAdapterPosition()).getContact();
                contactActionListener.onOpen(contact);
            }
        });
        viewHolder1.mButtonMessage.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contactModels.get(viewHolder.getAdapterPosition()).getContact();
                contactActionListener.onSendMessage(contact);
            }
        });
        viewHolder1.mButtonAccept.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contactModels.get(viewHolder.getAdapterPosition()).getContact();
                contactActionListener.onAccept(contact);
            }
        });
        viewHolder1.mButtonDelete.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contactModels.get(viewHolder.getAdapterPosition()).getContact();
                contactActionListener.onRemove(contact);
            }
        });
        viewHolder1.mButtonDecline.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contactModels.get(viewHolder.getAdapterPosition()).getContact();
                contactActionListener.onDecline(contact);
            }
        });
        viewHolder1.mButtonSendRequest.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contactModels.get(viewHolder.getAdapterPosition()).getContact();
                contactActionListener.onSendContactRequest(contact);
            }
        });
        viewHolder1.itemView.setOnClickListener(v -> viewHolder1.itemView.getContext().startActivity(
                new Intent(viewHolder1.itemView.getContext(),
                        ContactActivity.class)));
    }

    @Override
    public int getItemViewType(int position) {
        if (contactModels.get(position).isSection()) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    public void setData(List<ContactModelNew> mContactItems) {
        contactModels.clear();
        contactModels.addAll(mContactItems);
        notifyDataSetChanged();
    }

    private ContactModelNew getItem(int position) {
        return contactModels.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextContactName;
        private TextView mTextContactPosition;
        private TextView mTextContactCompany;
        private TextView mTextContactCountry;
        private ImageView mButtonMessage;
        private ImageView mButtonAccept;
        private ImageView mButtonDelete;
        private ImageView mButtonDecline;
        private ImageView mButtonSendRequest;
        private ImageView mImageViewContact;

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
            mButtonSendRequest = itemView.findViewById(R.id.button_send_request);
            mImageViewContact = itemView.findViewById(R.id.image_contact);
        }
    }

    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitleTextview;

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
            headerTitleTextview = itemView.findViewById(R.id.headerTitleTextview);
        }
    }

    public interface ContactActionListener {
        void onRemove(Contact contact);
        void onDecline(Contact contact);
        void onSendContactRequest(Contact contact);
        void onCancelContactRequest(Contact contact);
        void onAccept(Contact contact);
        void onSendMessage(Contact contact);
        void onOpen(Contact contact);
    }

}