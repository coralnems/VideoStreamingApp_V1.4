package com.example.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.util.Constant;
import com.example.videostreamingapp.R;

public class FilterDialog extends BaseDialog {

    private Activity activity;
    private FilterDialogListener filterDialogListener;
    private int selectedFilter = 1;
    private RelativeLayout lytNew, lytOld, lytAtoZ, lytRandom;
    private TextView txtNew, txtOld, txtAtoZ, txtRandom;
    private ImageView imgNewTick, imgOldTick, imgAtoZTick, imgRandomTick;
    private LinearLayout lytRoot;

    public FilterDialog(Activity activity, int selectedFilter) {
        super(activity, R.style.Theme_AppCompat_Translucent);
        this.activity = activity;
        this.selectedFilter = selectedFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailog_filter);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        lytRoot = findViewById(R.id.lytDialogRoot);
        lytNew = findViewById(R.id.lytNew);
        lytOld = findViewById(R.id.lytOld);
        lytAtoZ = findViewById(R.id.lytAtoZ);
        lytRandom = findViewById(R.id.lytRandom);

        txtNew = findViewById(R.id.txtNew);
        txtOld = findViewById(R.id.txtOld);
        txtAtoZ = findViewById(R.id.txtAtoZ);
        txtRandom = findViewById(R.id.txtRandom);

        imgNewTick = findViewById(R.id.imgNewTick);
        imgOldTick = findViewById(R.id.imgOldTick);
        imgAtoZTick = findViewById(R.id.imgAtoZTick);
        imgRandomTick = findViewById(R.id.imgRandomTick);

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

        lytRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        selectedFilter();
    }

    public interface FilterDialogListener {
        void confirm(String filterTag, int filterPosition);
    }

    public void setFilterDialogListener(FilterDialogListener filterDialogListener) {
        this.filterDialogListener = filterDialogListener;
    }

    private void selectedFilter() {
        switch (selectedFilter) {
            case 1:
                lytNew.setBackground(ContextCompat.getDrawable(activity, R.drawable.toolbar_statusbar));
                txtNew.setTextColor(activity.getResources().getColor(R.color.white));
                imgNewTick.setVisibility(View.VISIBLE);
                break;
            case 2:
                lytOld.setBackground(ContextCompat.getDrawable(activity, R.drawable.toolbar_statusbar));
                txtOld.setTextColor(activity.getResources().getColor(R.color.white));
                imgOldTick.setVisibility(View.VISIBLE);
                break;
            case 3:
                lytAtoZ.setBackground(ContextCompat.getDrawable(activity, R.drawable.toolbar_statusbar));
                txtAtoZ.setTextColor(activity.getResources().getColor(R.color.white));
                imgAtoZTick.setVisibility(View.VISIBLE);
                break;
            case 4:
                lytRandom.setBackground(ContextCompat.getDrawable(activity, R.drawable.toolbar_statusbar));
                txtRandom.setTextColor(activity.getResources().getColor(R.color.white));
                imgRandomTick.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
