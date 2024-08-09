package com.example.videostreamingapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class DynamicGradientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_gradient);
        int[] gradientColor = new int[]{getResources().getColor(R.color.gradient_1),
                getResources().getColor(R.color.gradient_2),
                getResources().getColor(R.color.gradient_3),
                getResources().getColor(R.color.gradient_4),
                getResources().getColor(R.color.gradient_5)};
        LinearLayout lytGen = findViewById(R.id.lytGen);
        Button btnGen = findViewById(R.id.btnGenerate);
        btnGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int randomNumber = random.nextInt(gradientColor.length);
                GradientDrawable gd = new GradientDrawable();
                gd.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                gd.setShape(GradientDrawable.RECTANGLE);
                gd.setCornerRadius(50f);
                gd.setColors(new int[]{
                        lightenColor(gradientColor[randomNumber]),
                        gradientColor[randomNumber],
                        darkenColor(gradientColor[randomNumber]),
//                Color.YELLOW,
//                Color.CYAN
                });
                lytGen.setBackground(gd);
            }
        });

        // lytGen.setBackgroundColor(darkenColor(getResources().getColor(R.color.gradient_1)));
    }

    @ColorInt
    int darkenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    int lightenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.2f;
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    int darkenColor(@ColorInt int color, Float value) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] -= value;
        hsv[2] = Math.max(0f, Math.min(hsv[2], 1f));
        return Color.HSVToColor(hsv);
    }
}
