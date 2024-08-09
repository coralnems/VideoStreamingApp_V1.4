package com.example.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.util.Constant;
import com.example.util.FilterDialogListener;
import com.example.videostreamingapp.R;

public class FilterFragment extends DialogFragment {
    private int selectedFilter = 1;
    private RelativeLayout lytNew, lytOld, lytAtoZ, lytRandom;
    private TextView txtNew, txtOld, txtAtoZ, txtRandom;
    private ImageView imgNewTick, imgOldTick, imgAtoZTick, imgRandomTick;
    FilterDialogListener filterDialogListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dailog_filter, container, false);
        if (getArguments() != null) {
            selectedFilter = getArguments().getInt("selectedFilter");
        }

        lytNew = rootView.findViewById(R.id.lytNew);
        lytOld = rootView.findViewById(R.id.lytOld);
        lytAtoZ = rootView.findViewById(R.id.lytAtoZ);
        lytRandom = rootView.findViewById(R.id.lytRandom);

        txtNew = rootView.findViewById(R.id.txtNew);
        txtOld = rootView.findViewById(R.id.txtOld);
        txtAtoZ = rootView.findViewById(R.id.txtAtoZ);
        txtRandom = rootView.findViewById(R.id.txtRandom);

        imgNewTick = rootView.findViewById(R.id.imgNewTick);
        imgOldTick = rootView.findViewById(R.id.imgOldTick);
        imgAtoZTick = rootView.findViewById(R.id.imgAtoZTick);
        imgRandomTick = rootView.findViewById(R.id.imgRandomTick);

        lytNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialogListener.confirm(Constant.FILTER_NEWEST, 1);
                dismiss();
            }
        });

        lytOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialogListener.confirm(Constant.FILTER_OLDEST, 2);
                dismiss();
            }
        });

        lytAtoZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialogListener.confirm(Constant.FILTER_ALPHA, 3);
                dismiss();
            }
        });

        lytRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialogListener.confirm(Constant.FILTER_RANDOM, 4);
                dismiss();
            }
        });

        selectedFilter();
        return rootView;
    }

    private void selectedFilter() {
        switch (selectedFilter) {
            case 1:
                lytNew.setBackgroundColor(getResources().getColor(R.color.highlight));
                txtNew.setTextColor(getResources().getColor(R.color.white));
                imgNewTick.setVisibility(View.VISIBLE);
                break;
            case 2:
                lytOld.setBackgroundColor(getResources().getColor(R.color.highlight));
                txtOld.setTextColor(getResources().getColor(R.color.white));
                imgOldTick.setVisibility(View.VISIBLE);
                break;
            case 3:
                lytAtoZ.setBackgroundColor(getResources().getColor(R.color.highlight));
                txtAtoZ.setTextColor(getResources().getColor(R.color.white));
                imgAtoZTick.setVisibility(View.VISIBLE);
                break;
            case 4:
                lytRandom.setBackgroundColor(getResources().getColor(R.color.highlight));
                txtRandom.setTextColor(getResources().getColor(R.color.white));
                imgRandomTick.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
