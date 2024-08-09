package com.example.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.applovin.mediation.ads.MaxAdView;
import com.example.videostreamingapp.R;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ixidev.gdpr.GDPRChecker;
import com.startapp.sdk.ads.banner.Banner;
import com.wortise.ads.banner.BannerAd;

public class BannerAds {
    public static void showBannerAds(Context context, LinearLayout mAdViewLayout) {
        if (Constant.isBanner) {
            switch (Constant.adNetworkType) {
                case Constant.admobAd:
                    AdView mAdView = new AdView(context);
                    mAdView.setAdSize(AdSize.BANNER);
                    mAdView.setAdUnitId(Constant.bannerId);
                    AdRequest.Builder builder = new AdRequest.Builder();
                    GDPRChecker.Request request = GDPRChecker.getRequest();
                    if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                    }
                    mAdView.loadAd(builder.build());
                    mAdViewLayout.addView(mAdView);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
                case Constant.startAppAd:
                    Banner startAppBanner = new Banner((Activity) context);
                    mAdViewLayout.addView(startAppBanner);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
                case Constant.facebookAd:
                    com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, Constant.bannerId, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                    adView.loadAd();
                    mAdViewLayout.addView(adView);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
                case Constant.appLovinMaxAd:
                    MaxAdView maxAdView = new MaxAdView(Constant.bannerId, context);
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = context.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height);
                    maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    maxAdView.loadAd();
                    mAdViewLayout.addView(maxAdView);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
                case Constant.wortiseAd:
                    BannerAd mBannerAd = new BannerAd(context);
                    mBannerAd.setAdSize(com.wortise.ads.AdSize.HEIGHT_50);
                    mBannerAd.setAdUnitId(Constant.bannerId);
                    mBannerAd.loadAd();
                    mAdViewLayout.addView(mBannerAd);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
            }
        } else {
            mAdViewLayout.setVisibility(View.GONE);
        }
    }
}
