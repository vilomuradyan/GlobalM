package com.globalm.platform.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.models.Organization;
import com.globalm.platform.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrganizationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Organization> organizations;

    public OrganizationsAdapter() {
        organizations = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder viewHolder1 = (ViewHolder) viewHolder;
        Organization model = getItem(i);

        viewHolder1.mTextCompanyName.setText(model.getName());
        viewHolder1.mTextCompanyCode.setText(model.getCode());
        if (model.getThumb() != null) {
            viewHolder1.mImageViewCompany.setVisibility(View.VISIBLE);
            Picasso.get().load(model.getThumb()).transform(new CircleTransformation()).into(viewHolder1.mImageViewCompany);
        } else {
            viewHolder1.mImageViewCompany.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return organizations.size();
    }

    public void setData(List<Organization> organizations) {
        this.organizations.clear();
        this.organizations.addAll(organizations);
        notifyDataSetChanged();
    }

    private Organization getItem(int position) {
        return organizations.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextCompanyName;
        private TextView mTextCompanyCode;
        private ImageView mImageViewCompany;

        ViewHolder(View itemView) {
            super(itemView);
            mTextCompanyName = itemView.findViewById(R.id.text_company_name);
            mTextCompanyCode = itemView.findViewById(R.id.text_company_code);
            mImageViewCompany = itemView.findViewById(R.id.image_company);
        }
    }

}