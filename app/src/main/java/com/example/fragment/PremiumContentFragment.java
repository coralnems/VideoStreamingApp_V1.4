package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.PlanActivity;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.SignInActivity;

public class PremiumContentFragment extends Fragment {

    public static PremiumContentFragment newInstance(String postId, String postType) {
        PremiumContentFragment f = new PremiumContentFragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        args.putString("postType", postType);
        f.setArguments(args);
        return f;
    }

    private String postId, postType;
    private MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_premium_content, container, false);
        if (getArguments() != null) {
            postId = getArguments().getString("postId");
            postType = getArguments().getString("postType");
        }
        myApplication = MyApplication.getInstance();
        Button btnSubscribe = rootView.findViewById(R.id.btn_subscribe_now);

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getIsLogin()) {
                    Intent intentPlan = new Intent(getActivity(), PlanActivity.class);
                    intentPlan.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentPlan);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.login_first), Toast.LENGTH_SHORT).show();

                    Intent intentLogin = new Intent(getActivity(), SignInActivity.class);
                    intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentLogin.putExtra("isOtherScreen", true);
                    intentLogin.putExtra("postId", postId);
                    intentLogin.putExtra("postType", postType);
                    startActivity(intentLogin);
                }
            }
        });

        return rootView;
    }
}
