package com.globalm.platform.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalm.platform.R;
import com.globalm.platform.models.Item;

/**
 * A simple {@link Fragment} subclass.
 */
public class RootFragment extends Fragment {

    private Uri sharedFileUri = null;
    private Item mUploadData;

    public RootFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_root, container, false);

        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        /*
         * When this container fragment is created, we fill it with our first
         * "real" fragment
         */
        UploadContentFragment fragment = new UploadContentFragment();
        transaction.replace(R.id.root_frame, fragment, UploadContentFragment.TAG);
        transaction.runOnCommit(() -> {
            if (sharedFileUri != null) {
                fragment.setSharedFileUri(sharedFileUri);
            }
            if (mUploadData != null){
                fragment.setUploadData(mUploadData);
            }
        });
        transaction.commit();
        return view;
    }

    public void setSharedFileUri(Uri uri) {
        if (getFragmentManager() == null) {
            sharedFileUri = uri;
            return;
        }

        Fragment fragment = getFragmentManager().findFragmentByTag(UploadContentFragment.TAG);
        if (fragment instanceof UploadContentFragment) {
            ((UploadContentFragment)fragment).setSharedFileUri(uri);
        }
    }

    public void setUploadData(Item item){
        if (getFragmentManager() == null) {
            mUploadData = item;
            return;
        }

        Fragment fragment = getFragmentManager().findFragmentByTag(UploadContentFragment.TAG);
        if (fragment instanceof UploadContentFragment) {
            ((UploadContentFragment)fragment).setUploadData(item);
        }
    }
}
