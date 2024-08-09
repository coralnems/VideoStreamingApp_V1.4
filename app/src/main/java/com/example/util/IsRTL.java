package com.example.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import com.example.videostreamingapp.R;

public class IsRTL {
    public static void ifSupported(Activity mContext) {
        boolean isRTL = Boolean.parseBoolean(mContext.getString(R.string.isRTL));
        if (isRTL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mContext.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }
}
