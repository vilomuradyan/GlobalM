package com.globalm.platform.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.fragments.RequestsFragment;
import com.globalm.platform.models.Contact;
import com.globalm.platform.models.User;
import com.globalm.platform.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Contact> contacts;
    private ContactsAdapter.ContactActionListener contactActionListener;
    private RequestsFragment.Type type;

    public RequestsAdapter(ContactsAdapter.ContactActionListener contactActionListener, RequestsFragment.Type type) {
        this.contactActionListener = contactActionListener;
        this.type = type;
        contacts = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder viewHolder1 = (ViewHolder) viewHolder;
        Contact model = getItem(i);
        viewHolder1.mButtonMessage.setVisibility(View.VISIBLE);
        User user;
        if (type == RequestsFragment.Type.REQUESTS) {
            viewHolder1.mButtonAccept.setVisibility(View.VISIBLE);
            user = model.getCreator();
        } else {
            viewHolder1.mButtonAccept.setVisibility(View.GONE);
            user = model.getTarget();
        }
        viewHolder1.mButtonDecline.setVisibility(View.VISIBLE);
        viewHolder1.mButtonDelete.setVisibility(View.GONE);
        viewHolder1.mButtonSendRequest.setVisibility(View.GONE);
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
                Contact contact = contacts.get(viewHolder.getAdapterPosition());
                contactActionListener.onOpen(contact);
            }
        });
        viewHolder1.mButtonMessage.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contacts.get(viewHolder.getAdapterPosition());
                contactActionListener.onSendMessage(contact);
            }
        });
        viewHolder1.mButtonAccept.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contacts.get(viewHolder.getAdapterPosition());
                contactActionListener.onAccept(contact);
            }
        });
        viewHolder1.mButtonDecline.setOnClickListener((v) -> {
            if (contactActionListener != null) {
                Contact contact = contacts.get(viewHolder.getAdapterPosition());
                if (type == RequestsFragment.Type.REQUESTS) {
                    contactActionListener.onDecline(contact);
                } else if(type == RequestsFragment.Type.PENDING) {
                    contactActionListener.onCancelContactRequest(contact);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setData(List<Contact> mContactItems) {
        contacts.clear();
        contacts.addAll(mContactItems);
        notifyDataSetChanged();
    }

    private Contact getItem(int position) {
        return contacts.get(position);
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

}