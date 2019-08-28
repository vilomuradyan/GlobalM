package com.globalm.platform.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.globalm.platform.R;
import com.globalm.platform.activities.ContactActivity;
import com.globalm.platform.activities.ContentDetailsActivity;
import com.globalm.platform.activities.MapViewActivity;
import com.globalm.platform.activities.MessageActivity;
import com.globalm.platform.models.StreamModel;
import com.squareup.picasso.Picasso;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    private ArrayList<StreamModel> mContentList;
    private Context mContext;

    public ContentAdapter(Context context) {
        mContext = context;
        mContentList = new ArrayList<>();
    }


    public void setData(List<StreamModel> contentItems) {
        mContentList.clear();
        mContentList.addAll(contentItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream, parent, false));
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        params.width = holder.itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.item_stream_width);
        holder.itemView.setLayoutParams(params);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextLive.setVisibility(getItem(position).isLive() ? View.VISIBLE : View.GONE);
        holder.mTextTitle.setText(getItem(position).getTitle());
        holder.mTextDate.setText(getItem(position).getDate());
        holder.mTextDescription.setText(getItem(position).getDescription());
//        holder.mTextUserName.setText(getItem(position).getUserName());
//        holder.mTextUserSurname.setText(getItem(position).getUserSurname());
        Picasso.get().load(getItem(position).getUserImage()).into(holder.mImagePreview);
    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }

    private StreamModel getItem(int position) {
        return mContentList.get(position);
    }

    public void filterList(ArrayList<StreamModel> filter) {
        mContentList = filter;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextNews;
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

        PopupWindow popupwindow_obj = popupDisplay();

        ViewHolder(View itemView) {
            super(itemView);
            mTextNews = itemView.findViewById(R.id.text_tag);
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

//        public void showCustomPopupMenu(View view) {
//            showPopupMenu(view, true, R.style.MyPopupStyle);
//        }

        private void showPopupMenu(View anchor, boolean isWithIcons, int style) {
            //init the wrapper with style
            Context wrapper = new ContextThemeWrapper(itemView.getContext(), style);

            //init the popup
            PopupMenu popup = new PopupMenu(wrapper, anchor);

            /*  The below code in try catch is responsible to display icons*/
            if (isWithIcons) {
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //inflate menu
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

            //implement click events
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        mContentList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        break;
                    case R.id.action_edit:
                        break;

                }
                return true;
            });
            popup.show();

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

            return popupWindow;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_user:
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), ContactActivity.class));
                    break;

                case R.id.button_download:
                    DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse("http://www.nightout.xyz/companion/papers/nmo2014.pdf");
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    Long reference = downloadManager.enqueue(request);
                    break;

                case R.id.button_message:
                    mContext.startActivity(new Intent(itemView.getContext(), MessageActivity.class));
                    break;

                case R.id.button_location:
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), MapViewActivity.class));
                    break;

                case R.id.button_popup_menu:
                    //    showPopupMenu(mTextNews, true, R.style.MyPopupStyle);
                    popupwindow_obj.showAsDropDown(mLayoutTop, 0, 18, Gravity.CENTER);
                    break;
                default:
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), ContentDetailsActivity.class));
                    break;
            }
        }
    }
}