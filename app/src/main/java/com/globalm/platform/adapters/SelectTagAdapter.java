package com.globalm.platform.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.globalm.platform.R;
import com.globalm.platform.models.Tag;

import java.util.ArrayList;
import java.util.List;

public class SelectTagAdapter extends RecyclerView.Adapter<SelectTagAdapter.TagViewHolder> {

    private List<Tag> tags;
    private List<Tag> selectedTags;

    public SelectTagAdapter() {
        tags = new ArrayList<>();
        selectedTags = new ArrayList<>();
    }

    public void addTags(List<Tag> tags) {
        int posStart = this.tags.size();
        this.tags.addAll(tags);
        notifyItemRangeInserted(posStart, this.tags.size());
    }

    public void clearTags() {
        tags.clear();
    }

    public List<Tag> getSelectedTags() {
        return selectedTags;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_select_tag, viewGroup, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder tagViewHolder, int position) {
        Tag tag = tags.get(position);
        tagViewHolder.checkBox.setText(tag.getName());
        tagViewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedTags.add(tag);
            } else {
                selectedTags.remove(tag);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
        }
    }
}
