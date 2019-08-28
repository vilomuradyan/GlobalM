package com.globalm.platform.adapters;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.activities.ContactActivity;
import com.globalm.platform.activities.ContentDetailsActivity;
import com.globalm.platform.activities.CreateContentActivity;
import com.globalm.platform.activities.MapViewActivity;
import com.globalm.platform.activities.MessageActivity;
import com.globalm.platform.fragments.CreateContentFragment;
import com.globalm.platform.models.Item;
import com.globalm.platform.models.Owner;
import com.globalm.platform.models.Tag;
import com.globalm.platform.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapterForApi extends RecyclerView.Adapter<ContentAdapterForApi.ViewHolder>{

    private Context mContext;
    private List<Item> mContentList;
    private ContentAdapterListener listener;
    private boolean shouldApplySize;

    public ContentAdapterForApi(Context context, ContentAdapterListener listener, boolean shouldApplySize) {
        mContext = context;
        mContentList = new ArrayList<>();
        this.listener = listener;
        this.shouldApplySize = shouldApplySize;
    }

    public void setData(List<Item> contentItems) {
        mContentList.clear();
        mContentList.addAll(contentItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }

    public Item getItem(int position) {
        return mContentList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_stream, viewGroup, false) ;
        if (shouldApplySize) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            params.width = view.getContext().getResources().getDimensionPixelOffset(R.dimen.item_stream_width);
            view.setLayoutParams(params);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Item model = getItem(i);
        viewHolder.mTextTitle.setText(model.getTitle());
        Picasso.get().load(model.getThumb()).into(viewHolder.mImagePreview);
        viewHolder.mTextDate.setText(model.getCreatedAt());
        viewHolder.mTextDescription.setText(model.getSubtitle());
        if (model.getTags().size() > 0) {
            viewHolder.mTextTag.setVisibility(View.VISIBLE);
            viewHolder.mTextTag.setText(model.getTags().get(0).getName());
            viewHolder.mTextTag.setOnClickListener((v) -> {
                if (listener != null) {
                    listener.onTagClicked(model.getTags().get(0));
                }
            });
        } else {
            viewHolder.mTextTag.setVisibility(View.GONE);
            viewHolder.mTextTag.setOnClickListener(null);
        }

        viewHolder.mImageUser.setVisibility(View.GONE);
        Owner owner = model.getOwner();
        if (owner != null) {
            Owner.Profile profile = owner.getProfile();
            if (profile != null) {
                if (profile.getThumb() != null) {
                    viewHolder.mImageUser.setVisibility(View.VISIBLE);
                    Picasso.get().load(profile.getThumb()).transform(new CircleTransformation()).into(viewHolder.mImageUser);
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextTag;
        private TextView mTextLive;
        private TextView mTextTitle;
        private TextView mTextDate;
        private TextView mTextDescription;
        private TextView mTextUserName;
        private TextView mTextUserSurname;
        private ImageView mImagePreview;
        private ImageView mImageUser;
        private LinearLayout mLayoutUsername;
        private ImageView mButtonDownload;
        private ImageView mButtonMessage;
        private ImageView mButtonLocation;
        private ImageView mButtonPopupMenu;
        private RelativeLayout mLayoutTop;
        private Dialog mDialog = getDialog();

        PopupWindow popupwindow_obj = popupDisplay();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextTag = itemView.findViewById(R.id.text_tag);
            mTextLive = itemView.findViewById(R.id.text_live);
            mTextTitle = itemView.findViewById(R.id.text_title);
            mTextDate = itemView.findViewById(R.id.text_date);
            mTextDescription = itemView.findViewById(R.id.text_description);
            mTextUserName = itemView.findViewById(R.id.text_user_name);
//            mTextUserSurname = itemView.findViewById(R.id.text_user_surname);
            mImagePreview = itemView.findViewById(R.id.image_preview);
            mImageUser = itemView.findViewById(R.id.image_user);
//            mLayoutUsername = itemView.findViewById(R.id.layout_user_name);
            mButtonDownload = itemView.findViewById(R.id.button_download);
            mButtonMessage = itemView.findViewById(R.id.button_message);
            mButtonLocation = itemView.findViewById(R.id.button_location);
            mButtonPopupMenu = itemView.findViewById(R.id.button_popup_menu);
            mLayoutTop = itemView.findViewById(R.id.layout_top);
            itemView.setOnClickListener(this);
            mImageUser.setOnClickListener(this);
            mButtonPopupMenu.setOnClickListener(this);
//            mLayoutUsername.setOnClickListener(this);
            mButtonDownload.setOnClickListener(this);
            mButtonMessage.setOnClickListener(this);
            mButtonLocation.setOnClickListener(this);
            itemView.setClipToOutline(true);
        }

        public Dialog getDialog(){
            Dialog dialog = new BottomSheetDialog(itemView.getContext());
            LayoutInflater inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_dialog, null);
            dialog.setContentView(view);
            dialog.setCancelable(true);
            Button edit = view.findViewById(R.id.dialog_edit);
            edit.setOnClickListener(v -> {
                CreateContentActivity.start(mContext, mContentList.get(getAdapterPosition()));
                mDialog.dismiss();
            });
            Button delete = view.findViewById(R.id.dialog_delete);
            delete.setOnClickListener(v -> {
                mContentList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                mDialog.dismiss();
            });
            Button cancel = view.findViewById(R.id.dialog_cancel);
            cancel.setOnClickListener(v -> mDialog.dismiss());
            return dialog;
        }

        public PopupWindow popupDisplay() {

            final PopupWindow popupWindow = new PopupWindow(itemView.getContext());

            // inflate your layout or dynamically add view
            LayoutInflater inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.rounded_layout, null);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(itemView.getContext().getDrawable(R.drawable.rounded_drawable));
//            popupWindow.setWidth(500);
//            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(view);

            TextView mTextDeleteContent = view.findViewById(R.id.text_delete_content);
            mTextDeleteContent.setOnClickListener(v -> {
                mContentList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                popupwindow_obj.dismiss();
            });
            LinearLayout buttonEdit = view.findViewById(R.id.button_edit);
            buttonEdit.setOnClickListener(v -> {
                CreateContentActivity.start(mContext, mContentList.get(getAdapterPosition()));
                popupwindow_obj.dismiss();
            });

            return popupWindow;
        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            switch (v.getId()){
//                case R.id.layout_user_name :
//                    itemView.getContext().startActivity(new Intent(itemView.getContext(), ContactActivity.class));
//                    break;
                case R.id.image_user:
                    context.startActivity(new Intent(context, ContactActivity.class));
                    break;
                case R.id.button_download:
                    DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse("http://www.nightout.xyz/companion/papers/nmo2014.pdf");
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    Long reference = downloadManager.enqueue(request);
                    break;

                case R.id.button_message:
                    mContext.startActivity(new Intent(context, MessageActivity.class));
                    break;

                case R.id.button_location:
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), MapViewActivity.class));
                    break;

                case R.id.button_popup_menu:
                    //    showPopupMenu(mTextNews, true, R.style.MyPopupStyle);
                //    popupwindow_obj.showAsDropDown(mLayoutTop, 0, 18, Gravity.CENTER);
                    mDialog.show();
                    break;
                default:
                    ContentDetailsActivity.start(context, mContentList.get(getAdapterPosition()));
                    break;
            }
        }
    }

    public interface ContentAdapterListener {
        void onTagClicked(Tag tag);
    }
}
