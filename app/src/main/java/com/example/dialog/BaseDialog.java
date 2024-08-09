package com.example.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public class BaseDialog extends Dialog {
    private View decorView;
    private Window window;

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.window = getWindow();
        this.decorView = getWindow().getDecorView();
    }

    @SuppressLint({"NewApi"})
    public void hideStatusBar() {
        if (VERSION.SDK_INT <= 15) {
            getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        } else if (VERSION.SDK_INT < 19) {
            this.decorView.setSystemUiVisibility(4);
        } else {
            this.decorView.setSystemUiVisibility(5380);
        }
    }
}
