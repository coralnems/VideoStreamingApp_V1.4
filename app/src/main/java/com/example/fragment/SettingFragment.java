package com.example.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.videostreamingapp.MainActivity;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
import com.onesignal.OneSignal;


public class SettingFragment extends Fragment {

    private MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        myApplication = MyApplication.getInstance();

        LinearLayout lytRate = rootView.findViewById(R.id.lytRateApp);
        LinearLayout lytMore = rootView.findViewById(R.id.lytMoreApp);
        LinearLayout lytShare = rootView.findViewById(R.id.lytShareApp);
        LinearLayout lytPrivacy = rootView.findViewById(R.id.lytPrivacy);
        LinearLayout lytAbout = rootView.findViewById(R.id.lytAbout);
        Switch notificationSwitch = rootView.findViewById(R.id.switch_notification);

        notificationSwitch.setChecked(myApplication.getNotification());

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myApplication.saveIsNotification(isChecked);
              //  OneSignal.setSubscription(isChecked);
            }
        });

        lytRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApp();
            }
        });

        lytMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.play_more_apps))));
            }
        });

        lytShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });

        lytAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String about = getString(R.string.about);
                AboutFragment aboutFragment = new AboutFragment();
                changeFragment(aboutFragment, about);
            }
        });

        lytPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privacy = getString(R.string.privacy_policy);
                PrivacyFragment privacyFragment = new PrivacyFragment();
                changeFragment(privacyFragment, privacy);
            }
        });

        return rootView;
    }

    private void changeFragment(Fragment fragment, String Name) {
        FragmentManager fm = getFragmentManager();
        assert fm != null;
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(SettingFragment.this);
        ft.add(R.id.Container, fragment, Name);
        ft.addToBackStack(Name);
        ft.commit();
        ((MainActivity) requireActivity()).setToolbarTitle(Name);
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg) + requireActivity().getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void rateApp() {
        final String appName = requireActivity().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id="
                            + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + appName)));
        }
    }
}
