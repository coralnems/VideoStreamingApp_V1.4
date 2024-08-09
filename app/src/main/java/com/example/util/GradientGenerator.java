package com.example.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;

import com.example.videostreamingapp.R;

public class GradientGenerator {
    public static GradientDrawable build(Context context, int position) {
        int[] gradientColor = new int[]{context.getResources().getColor(R.color.gradient_1),
                context.getResources().getColor(R.color.gradient_2),
                context.getResources().getColor(R.color.gradient_3),
                context.getResources().getColor(R.color.gradient_4),
                context.getResources().getColor(R.color.gradient_5)};
        int totalColor = gradientColor.length;
        int currentColor = position % totalColor;
        GradientDrawable gd = new GradientDrawable();
        gd.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(context.getResources().getDimension(R.dimen.langRadius));
        gd.setColors(new int[]{
                lightenColor(gradientColor[currentColor]),
                gradientColor[currentColor],
                darkenColor(gradientColor[currentColor]),
        });
        return gd;
    }

    @ColorInt
    static int darkenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    static int lightenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.2f;
        return Color.HSVToColor(hsv);
    }
}
