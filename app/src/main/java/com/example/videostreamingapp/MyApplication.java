package com.example.videostreamingapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.example.util.NetworkUtils;
import com.facebook.FacebookSdk;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    public SharedPreferences preferences;
    public String prefName = "VideoStreamingApp";

    public MyApplication() {
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/custom.otf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        OneSignal.initWithContext(this);
        OneSignal.setAppId(getString(R.string.onesignal_app_id));
        OneSignal.setNotificationOpenedHandler(new ExampleNotificationOpenedHandler());
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.apply();
        if (!flag) {
            saveLogin("", "", "", "");
        }
    }

    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedIn", false);
    }

    public void saveIsRemember(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedRemember", flag);
        editor.apply();
    }

    public boolean getIsRemember() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedRemember", false);
    }


    public void saveRemember(String email, String password) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("remember_email", email);
        editor.putString("remember_password", password);
        editor.apply();
    }

    public String getRememberEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("remember_email", "");
    }

    public String getRememberPassword() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("remember_password", "");
    }

    public void saveLogin(String user_id, String user_name, String email, String phone) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("user_id", user_id);
        editor.putString("user_name", user_name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();
    }

    public String getLoginType() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("login_type", "");
    }

    public void saveLoginType(String type) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("login_type", type);
        editor.apply();
    }


    public String getUserId() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_id", "");
    }

    public String getUserName() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_name", "");
    }

    public String getUserEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("email", "");
    }

    public String getUserPhone() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("phone", "");
    }

    public void saveIsNotification(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsNotification", flag);
        editor.apply();
    }

    public boolean getNotification() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsNotification", true);
    }

    public void saveIsIntroduction(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsIntroduction", flag);
        editor.apply();
    }

    public boolean getIsIntroduction() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsIntroduction", false);
    }


    public int getScreenWidth() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getInt("screen_width", NetworkUtils.getScreenWidth(MyApplication.this));
    }

    public void setScreenWidth(int screenWidth) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putInt("screen_width", screenWidth);
        editor.apply();
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            JSONObject data = result.getNotification().getAdditionalData();
            Log.e("data", "-->" + data);
            String isExternalLink, postId, postType;
            try {
                isExternalLink = data.getString("external_link");
                postId = data.getString("post_id");
                postType = data.getString("type");
                if (!postId.equals("null")) {
                    Class<?> aClass;
                    switch (postType) {
                        case "Movies":
                            aClass = MovieDetailsActivity.class;
                            break;
                        case "Shows":
                            aClass = ShowDetailsActivity.class;
                            break;
                        case "LiveTV":
                            aClass = TVDetailsActivity.class;
                            break;
                        default:
                            aClass = SportDetailsActivity.class;
                            break;
                    }
                    Intent intent = new Intent(MyApplication.this, aClass);
                    intent.putExtra("Id", postId);
                    intent.putExtra("isNotification", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    if (!isExternalLink.equals("false")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(isExternalLink));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MyApplication.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
