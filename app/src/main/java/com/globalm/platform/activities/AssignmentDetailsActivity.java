package com.globalm.platform.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.adapters.SimpleFragmentViewPagerAdapter;
import com.globalm.platform.fragments.AssignmentDetailsFragment;
import com.globalm.platform.fragments.AssignmentResponsesFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssignmentDetailsActivity extends BaseActivity {
    public static final String ASSIGNMENT_ID_KEY = "ASSIGNMENT_ID_KEY";

    public static void start(Context context, Integer assignmentId) {
        Intent intent = new Intent(context, AssignmentDetailsActivity.class);
        intent.putExtra(ASSIGNMENT_ID_KEY, assignmentId);

        context.startActivity(intent);
    }

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SimpleFragmentViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_details);
        findView(R.id.back_button).setOnClickListener((v) -> finish());
        pagerAdapter = new SimpleFragmentViewPagerAdapter(getSupportFragmentManager(), getFragmentList());
        setupView();
    }

    private List<Pair<String, Fragment>> getFragmentList() {
        int assignmentId = getIntent().getIntExtra(ASSIGNMENT_ID_KEY, 0);
        return new ArrayList<>(Arrays.asList(
                new Pair<>(getString(R.string.details), AssignmentDetailsFragment.newInstance(assignmentId)),
                new Pair<>(getString(R.string.responses), AssignmentResponsesFragment.newInstance(assignmentId))));
    }

    private void setupView() {
        tabLayout = findView(R.id.tab_layout);
        viewPager = findView(R.id.view_pager);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        ((TextView) findView(R.id.text_title)).setText(R.string.assignment);
    }
}
