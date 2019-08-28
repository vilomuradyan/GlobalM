package com.globalm.platform.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipDrawable;
import android.support.design.chip.ChipGroup;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.models.Tag;
import com.globalm.platform.models.assingments.Assignment;
import com.globalm.platform.models.assingments.AssignmentStatus;
import com.globalm.platform.utils.PicassoUtil;
import com.globalm.platform.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AssignmentsAdapter extends RecyclerView.Adapter<AssignmentsAdapter.AssignmentViewHolder> {

    private List<Assignment> assignments;
    private Utils.InvokeCallback<Integer> itemClickListener;
    private Utils.InvokeCallback<Integer> menuClickListener;

    public AssignmentsAdapter(Utils.InvokeCallback<Integer> itemClickListener,
                              Utils.InvokeCallback<Integer> menuClickListener) {
        this.itemClickListener = itemClickListener;
        this.menuClickListener = menuClickListener;
        assignments = new ArrayList<>();
    }

    public void addAssignments(List<Assignment> assignments) {
        if (assignments == null) {
            return;
        }

        int posStart = this.assignments.size();
        this.assignments.addAll(assignments);
        notifyItemRangeInserted(posStart, assignments.size());
    }

    public void clearAllAssignments() {
        assignments.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = getLayoutInflater(viewGroup).inflate(R.layout.item_assignment, viewGroup, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder viewHolder, int position) {
        Assignment assignment = assignments.get(position);
        viewHolder.title.setText(assignment.getTitle());
        viewHolder.idTv.setText(getId(assignment.getId()));
        viewHolder.locationTv.setText(assignment.getTargetGeoData().getName());
        viewHolder.ratePriceTv.setText(getRateValue(assignment.getRateValue()));

        if (assignment.getSkills() != null) {
            setupSkills(
                    viewHolder.itemView.getContext(),
                    viewHolder.skillsGroup, assignment.getSkills());
        }

        if (assignment.getOrganization() != null) {
            viewHolder.organizationName.setText(assignment.getOrganization().getName());
        } else {
            viewHolder.organizationName.setText("");

        }
        if (assignment.getOrganization() != null) {
            PicassoUtil.loadCircleImageIntoImageView(
                    assignment.getOrganization().getThumb(),
                    viewHolder.organizationicon,
                    R.drawable.selector_rounded_button_dark_grey);
        }
        if (assignment.getUrgent() != null && assignment.getUrgent()) {
            viewHolder.urgentTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.urgentTv.setVisibility(View.GONE);
        }
        if (assignment.getStatus() != null) {
            processStatus(assignment.getStatus(), viewHolder);
        }
        viewHolder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.invoke(assignment.getId());
            }
        });
        String numberOfResponses = String.format(
                "%d %s",
                assignment.getResponses().size(),
                viewHolder.itemView.getContext().getString(R.string.responses));
        viewHolder.responsesTv.setText(numberOfResponses);
        viewHolder.respondMenu.setOnClickListener(v -> showRespondMenu(v, assignment.getId()));
    }

    private void showRespondMenu(View v, int assignmentId) {
        getPopupWindow(v, assignmentId)
                .showAsDropDown(v, -80, 0, Gravity.END);
    }

    private PopupWindow getPopupWindow(View view, int assignmentId) {
        Context context = view.getContext();
        PopupWindow popupWindow = new PopupWindow(context);

        LayoutInflater inflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View inflatedLayout = inflater.inflate(R.layout.item_menu_respond, null);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setContentView(inflatedLayout);
        popupWindow.setElevation(12f);

        inflatedLayout.setOnClickListener(v -> {
            if (menuClickListener != null) {
                menuClickListener.invoke(assignmentId);
            }
            popupWindow.dismiss();
        });

        return popupWindow;
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    private void processStatus(AssignmentStatus status, AssignmentViewHolder viewHolder) {
        switch (status) {
            case STATUS_ACCEPTED: {
                showWon(viewHolder);
                break;
            }
            default: {
                hideWon(viewHolder);
                break;
            }
        }
    }

    private void showWon(AssignmentViewHolder viewHolder) {
        viewHolder.wonTv.setVisibility(View.VISIBLE);
        viewHolder.globalMIcon.setVisibility(View.VISIBLE);
        viewHolder.ratePriceTv.setVisibility(View.GONE);
        viewHolder.rateHeader.setVisibility(View.GONE);
        viewHolder.assignmentIcon.setImageResource(R.drawable.assignment_won_placeholder);
    }

    private void hideWon(AssignmentViewHolder viewHolder) {
        viewHolder.wonTv.setVisibility(View.GONE);
        viewHolder.globalMIcon.setVisibility(View.GONE);
        viewHolder.ratePriceTv.setVisibility(View.VISIBLE);
        viewHolder.rateHeader.setVisibility(View.VISIBLE);
        viewHolder.assignmentIcon.setImageResource(R.drawable.assignment_placeholder);
    }

    private String getRateValue(Double rateValue) {
        return String.format("$%.2f", rateValue);
    }

    private String getId(Integer rateValue) {
        return String.format("ID: %d, ", rateValue);
    }

    private void setupSkills(Context context, ChipGroup skillsGroup, List<Tag> skills) {
        for (Tag skill : skills) {
            View chip = getChip(context, skill.getName());
            skillsGroup.addView(chip);
        }
    }

    private LayoutInflater getLayoutInflater(ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext());
    }

    private Chip getChip(Context context, String title) {
        Chip chip = new Chip(context);
        chip.setChipDrawable(ChipDrawable.createFromResource(context, R.xml.chip));
        chip.setText(title);
        chip.setTextColor(ContextCompat.getColor(context, R.color.color_main_blue));
        chip.setCloseIconVisible(false);
        chip.setTextAppearance(R.style.AssignmentChipTextStyle);
        chip.setEnabled(false);
        return chip;
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        private ImageView assignmentIcon;
        private TextView title;
        private TextView idTv;
        private TextView locationTv;
        private TextView ratePriceTv;
        private TextView rateHeader;
        private TextView responsesTv;

        private ChipGroup skillsGroup;

        private ImageView organizationicon;
        private TextView organizationName;
        private TextView urgentTv;

        private ImageView globalMIcon;
        private TextView wonTv;

        private ImageView respondMenu;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            assignmentIcon = findView(R.id.assignment_icon);
            title = findView(R.id.title);
            idTv = findView(R.id.id_tv);
            locationTv = findView(R.id.location_tv);
            ratePriceTv = findView(R.id.rate_price_tv);
            skillsGroup = findView(R.id.skills_chip_group);
            organizationicon = findView(R.id.organization_icon);
            organizationName = findView(R.id.organization_name);
            urgentTv = findView(R.id.urgent_tv);
            globalMIcon = findView(R.id.globalm_icon);
            wonTv = findView(R.id.won_tv);
            rateHeader = findView(R.id.rate_header_tv);
            respondMenu = findView(R.id.respond_menu);
            responsesTv = findView(R.id.responses_tv);
        }

        private <T extends View> T findView(@IdRes int id) {
            return itemView.findViewById(id);
        }
    }
}
