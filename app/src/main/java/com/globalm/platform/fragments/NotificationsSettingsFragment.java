package com.globalm.platform.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.globalm.platform.R;

public class NotificationsSettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private Context mContext;
    private AppCompatCheckBox mCheckBoxEmail;
    private AppCompatCheckBox mCheckBoxIM;
    private AppCompatCheckBox mCheckBoxNever;
    private AppCompatRadioButton mRadioButtonImmediately;
    private AppCompatRadioButton mRadioButtonDaily;
    private AppCompatRadioButton mRadioButtonNever;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        mCheckBoxEmail = rootView.findViewById(R.id.checkbox_email);
        mCheckBoxIM = rootView.findViewById(R.id.checkbox_im);
        mCheckBoxNever = rootView.findViewById(R.id.checkbox_never);
        mRadioButtonImmediately = rootView.findViewById(R.id.radio_button_immediately);
        mRadioButtonDaily = rootView.findViewById(R.id.radio_button_daily);
        mRadioButtonNever = rootView.findViewById(R.id.radio_button_never);
        mCheckBoxEmail.setOnCheckedChangeListener(this);
        mCheckBoxIM.setOnCheckedChangeListener(this);
        mCheckBoxNever.setOnCheckedChangeListener(this);
        mRadioButtonImmediately.setOnCheckedChangeListener(this);
        mRadioButtonDaily.setOnCheckedChangeListener(this);
        mRadioButtonNever.setOnCheckedChangeListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(mContext, "Not implemented yet", Toast.LENGTH_SHORT).show();
        switch (buttonView.getId()) {
            case R.id.checkbox_email:

                break;

            case R.id.checkbox_im:

                break;

            case R.id.checkbox_never:

                break;

            case R.id.radio_button_immediately:

                break;

            case R.id.radio_button_daily:

                break;

            case R.id.radio_button_never:

                break;
        }
    }
}