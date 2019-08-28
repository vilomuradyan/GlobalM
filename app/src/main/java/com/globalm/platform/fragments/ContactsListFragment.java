package com.globalm.platform.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.adapters.ContactsListPagerAdapter;

public class ContactsListFragment extends Fragment {
    String[] tabTitle = {"MY CONTACTS", "COMPANIES", "REQUESTS", "PENDING"};
    int[] unreadCount = {0, 0, 0};
    private TabLayout mTabLayoutContacts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        mTabLayoutContacts = rootView.findViewById(R.id.tab_layout_list_contacts);
        ViewPager mViewPagerContacts = rootView.findViewById(R.id.view_pager_list_contacts);
        mTabLayoutContacts.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
        ContactsListPagerAdapter mContactsListPagerAdapter = new ContactsListPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPagerContacts.setOffscreenPageLimit(4);
        MyContactsFragment myContactsFragment = new MyContactsFragment();
        RequestsFragment requestsFragment = new RequestsFragment();
        requestsFragment.setType(RequestsFragment.Type.REQUESTS);
        RequestsFragment pendingFragment = new RequestsFragment();
        pendingFragment.setType(RequestsFragment.Type.PENDING);
        mContactsListPagerAdapter.addFragment(myContactsFragment, "MY CONTACTS");
        mContactsListPagerAdapter.addFragment(new CompaniesFragment(), "COMPANIES");
        mContactsListPagerAdapter.addFragment(requestsFragment, "REQUESTS");
        mContactsListPagerAdapter.addFragment(pendingFragment, "PENDING");
        mViewPagerContacts.setAdapter(mContactsListPagerAdapter);
        mTabLayoutContacts.setupWithViewPager(mViewPagerContacts);
        try {
            setupTabIcons();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private View prepareTabView(int pos) {
        View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_count = view.findViewById(R.id.tv_count);
        tv_title.setText(tabTitle[pos]);
        if (unreadCount[pos] > 0) {
            tv_count.setVisibility(View.VISIBLE);
            tv_count.setText("" + unreadCount[pos]);
        } else {
            tv_count.setVisibility(View.GONE);
        }
        return view;
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabTitle.length; i++) {
            mTabLayoutContacts.getTabAt(i).setCustomView(prepareTabView(i));
        }
    }
}