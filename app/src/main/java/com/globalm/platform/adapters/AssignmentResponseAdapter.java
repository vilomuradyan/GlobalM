package com.globalm.platform.adapters;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.models.Owner;
import com.globalm.platform.models.assingments.Response;
import com.globalm.platform.utils.PicassoUtil;
import com.globalm.platform.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AssignmentResponseAdapter extends RecyclerView.Adapter<AssignmentResponseAdapter.ResponseViewHolder> {

    private List<Response> responses = new ArrayList<>();
    private Utils.InvokeCallback<Integer> onMessageListener;

    public AssignmentResponseAdapter(Utils.InvokeCallback<Integer> onMessageListener) {
        this.onMessageListener = onMessageListener;
    }

    @NonNull
    @Override
    public ResponseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.item_response, viewGroup, false);
        return new ResponseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseViewHolder viewHolder, int position) {
        Response response = responses.get(position);
        if (response.getUser() != null
                && response.getUser().getProfile() != null) {
            Owner.Profile profile = response.getUser().getProfile();
            setupAvatar(profile, viewHolder);
            setupUserData(response.getUser(), viewHolder);
        } else {
            viewHolder.avatar.setImageResource(getDefaultAvatarDrawableId());
            viewHolder.fullName.setText("");
            viewHolder.country.setText("");
        }
        if (response.getMessage() != null) {
            viewHolder.message.setText(response.getMessage());
        } else {
            viewHolder.message.setText("");
        }
        viewHolder.sendMessage.setOnClickListener((v) -> onMessageListener.invoke(response.getId()));
    }

    private void setupUserData(Owner user, ResponseViewHolder viewHolder) {
        if (user.getFullName() != null) {
            viewHolder.fullName.setText(user.getFullName());
        }
        if (user.getProfile().getCountry() != null) {
            viewHolder.country.setText(user.getProfile().getCountry());
        }
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    public void addResponses(List<Response> responses) {
        int startPos = this.responses.size();
        this.responses.addAll(responses);
        notifyItemRangeInserted(startPos, this.responses.size());
    }

    public void clearResponses() {
        responses.clear();
        notifyDataSetChanged();
    }

    private int getDefaultAvatarDrawableId() {
        return R.drawable.selector_rounded_button_dark_grey;
    }

    private void setupAvatar(Owner.Profile profile, ResponseViewHolder viewHolder) {
        if (profile.getThumb() != null) {
            PicassoUtil.loadCircleImageIntoImageView(
                    profile.getThumb(),
                    viewHolder.avatar,
                    getDefaultAvatarDrawableId());
        } else {
            viewHolder.avatar.setImageResource(getDefaultAvatarDrawableId());
        }
    }

    public static class ResponseViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView message;
        private TextView country;
        private TextView fullName;
        private ImageView sendMessage;

        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = findView(R.id.avatar);
            message = findView(R.id.message);
            country = findView(R.id.country);
            fullName = findView(R.id.full_name);
            sendMessage = findView(R.id.send_message);
        }

        private <T extends View> T findView(@IdRes int id) {
            return itemView.findViewById(id);
        }
    }
}
