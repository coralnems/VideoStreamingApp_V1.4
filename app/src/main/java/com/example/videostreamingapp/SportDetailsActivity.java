package com.example.videostreamingapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.HomeSportAdapter;
import com.example.adapter.InfoAdapter;
import com.example.cast.Casty;
import com.example.cast.MediaData;
import com.example.fragment.ChromecastScreenFragment;
import com.example.fragment.EmbeddedImageFragment;
import com.example.fragment.ExoPlayerFragment;
import com.example.fragment.PlayRippleFragment;
import com.example.fragment.PremiumContentFragment;
import com.example.item.ItemPlayer;
import com.example.item.ItemSport;
import com.example.item.ItemSubTitle;
import com.example.util.API;
import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
import com.example.util.ShareUtils;
import com.example.util.StatusBarUtil;
import com.example.util.WatchListClickListener;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SportDetailsActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    RelativeLayout lytParent;
    WebView webView;
    TextView textTitle;
    RecyclerView rvRelated, rvInfo;
    ItemSport itemSport;
    ArrayList<ItemSport> mListItemRelated;
    ArrayList<String> mListInfo;
    HomeSportAdapter homeSportAdapter;
    String Id;
    LinearLayout lytRelated, lytView;
    MyApplication myApplication;
    NestedScrollView nestedScrollView;
    Toolbar toolbar;
    private FragmentManager fragmentManager;
    private int playerHeight;
    FrameLayout frameLayout;
    boolean isFullScreen = false;
    boolean isFromNotification = false;
    LinearLayout mAdViewLayout;
    boolean isPurchased = false, isWatchList = false;
    MaterialButton btnDownload, btnWatchList;
    private Casty casty;
    ImageView imgFacebook, imgTwitter, imgWhatsApp;
    InfoAdapter infoAdapter;
    MediaRouteButton mediaRouteButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarBlack(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_sport_details);
        IsRTL.ifSupported(this);
        GlobalBus.getBus().register(this);
        mAdViewLayout = findViewById(R.id.adView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        myApplication = MyApplication.getInstance();
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        if (intent.hasExtra("isNotification")) {
            isFromNotification = true;
        }
        casty = Casty.create(this)
                .withMiniController();
        mediaRouteButton = findViewById(R.id.media_route_button);
        casty.setUpMediaRouteButton(mediaRouteButton);
        frameLayout = findViewById(R.id.playerSection);
        int columnWidth = NetworkUtils.getScreenWidth(this);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
        playerHeight = frameLayout.getLayoutParams().height;

        BannerAds.showBannerAds(this, mAdViewLayout);

        mListItemRelated = new ArrayList<>();
        mListInfo = new ArrayList<>();
        itemSport = new ItemSport();
        lytRelated = findViewById(R.id.lytRelated);
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        lytParent = findViewById(R.id.lytParent);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        webView = findViewById(R.id.webView);

        textTitle = findViewById(R.id.textTitle);
        rvInfo = findViewById(R.id.rv_info);
        rvRelated = findViewById(R.id.rv_related);
        btnDownload = findViewById(R.id.btnDownload);
        lytView = findViewById(R.id.lytView);
        imgFacebook = findViewById(R.id.imgFacebook);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgWhatsApp = findViewById(R.id.imgWhatsApp);
        btnWatchList = findViewById(R.id.btnWatchList);

        rvRelated.setHasFixedSize(true);
        rvRelated.setLayoutManager(new LinearLayoutManager(SportDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvRelated.setFocusable(false);
        rvRelated.setNestedScrollingEnabled(false);

        rvInfo.setHasFixedSize(true);
        rvInfo.setLayoutManager(new LinearLayoutManager(SportDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvInfo.setFocusable(false);
        rvInfo.setNestedScrollingEnabled(false);

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);
        if (NetworkUtils.isConnected(SportDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

    }

    private void getDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("sport_id", Id);
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.SPORT_DETAILS_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytParent.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    isPurchased = mainJson.getBoolean(Constant.USER_PLAN_STATUS);
                    JSONObject objJson = mainJson.getJSONObject(Constant.ARRAY_NAME);
                    if (objJson.length() > 0) {
                        if (objJson.has(Constant.STATUS)) {
                            lyt_not_found.setVisibility(View.VISIBLE);
                        } else {
                            isWatchList = objJson.getBoolean(Constant.USER_WATCHLIST_STATUS);
                            itemSport.setSportId(objJson.getString(Constant.SPORT_ID));
                            itemSport.setSportName(objJson.getString(Constant.SPORT_TITLE));
                            itemSport.setSportDescription(objJson.getString(Constant.SPORT_DESC));
                            itemSport.setSportImage(objJson.getString(Constant.SPORT_IMAGE));
                            itemSport.setSportCategory(objJson.getString(Constant.SPORT_CATEGORY));
                            itemSport.setSportUrl(objJson.getString(Constant.SPORT_URL));
                            itemSport.setSportType(objJson.getString(Constant.SPORT_TYPE));
                            itemSport.setSportDuration(objJson.getString(Constant.SPORT_DURATION));
                            itemSport.setSportDate(objJson.getString(Constant.SPORT_DATE));
                            itemSport.setPremium(objJson.getString(Constant.SPORT_ACCESS).equals("Paid"));
                            itemSport.setDownload(objJson.getBoolean(Constant.DOWNLOAD_ENABLE));
                            itemSport.setDownloadUrl(objJson.getString(Constant.DOWNLOAD_URL));
                            itemSport.setSportShareLink(objJson.getString(Constant.MOVIE_SHARE_LINK));
                            itemSport.setSportView(objJson.getString(Constant.MOVIE_VIEW));

                            itemSport.setQuality(objJson.getBoolean(Constant.IS_QUALITY));
                            itemSport.setSubTitle(objJson.getBoolean(Constant.IS_SUBTITLE));
                            itemSport.setQuality480(objJson.getString(Constant.QUALITY_480));
                            itemSport.setQuality720(objJson.getString(Constant.QUALITY_720));
                            itemSport.setQuality1080(objJson.getString(Constant.QUALITY_1080));

                            itemSport.setSubTitleLanguage1(objJson.getString(Constant.SUBTITLE_LANGUAGE_1));
                            itemSport.setSubTitleUrl1(objJson.getString(Constant.SUBTITLE_URL_1));
                            itemSport.setSubTitleLanguage2(objJson.getString(Constant.SUBTITLE_LANGUAGE_2));
                            itemSport.setSubTitleUrl2(objJson.getString(Constant.SUBTITLE_URL_2));
                            itemSport.setSubTitleLanguage3(objJson.getString(Constant.SUBTITLE_LANGUAGE_3));
                            itemSport.setSubTitleUrl3(objJson.getString(Constant.SUBTITLE_URL_3));

                            JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_SPORT_ARRAY_NAME);
                            if (jsonArrayChild.length() != 0) {
                                for (int j = 0; j < jsonArrayChild.length(); j++) {
                                    JSONObject objChild = jsonArrayChild.getJSONObject(j);
                                    ItemSport item = new ItemSport();
                                    item.setSportId(objChild.getString(Constant.SPORT_ID));
                                    item.setSportName(objChild.getString(Constant.SPORT_TITLE));
                                    item.setSportImage(objChild.getString(Constant.SPORT_IMAGE));
                                    item.setPremium(objChild.getString(Constant.SPORT_ACCESS).equals("Paid"));
                                    mListItemRelated.add(item);
                                }
                            }
                        }
                        displayData();

                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytParent.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        setTitle("");
        textTitle.setSelected(true);
        textTitle.setText(itemSport.getSportName());

        mListInfo.add(itemSport.getSportDate());
        if (!itemSport.getSportDuration().isEmpty() && !itemSport.getSportDuration().equals("null")) {
            mListInfo.add(itemSport.getSportDuration());
        }
        mListInfo.add(itemSport.getSportCategory());
        if (!itemSport.getSportView().isEmpty()) {
            mListInfo.add(getString(R.string.view, itemSport.getSportView()));
        }
        infoAdapter = new InfoAdapter(mListInfo);
        rvInfo.setAdapter(infoAdapter);

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = itemSport.getSportDescription();

        boolean isRTL = Boolean.parseBoolean(getResources().getString(R.string.isRTL));
        String direction = isRTL ? "rtl" : "ltr";

        String text = "<html dir=" + direction + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.otf\")}body{font-family: MyFont;color: #9c9c9c;font-size:14px;margin-left:0px;line-height:1.3}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        initRipplePlay();//initPlayer();
        initDownload();
        initWatchList();

        if (!mListItemRelated.isEmpty()) {
            homeSportAdapter = new HomeSportAdapter(SportDetailsActivity.this, mListItemRelated);
            rvRelated.setAdapter(homeSportAdapter);

            homeSportAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String sportId = mListItemRelated.get(position).getSportId();
                    Intent intent = new Intent(SportDetailsActivity.this, SportDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Id", sportId);
                    startActivity(intent);
                }
            });

        } else {
            lytRelated.setVisibility(View.GONE);
        }

        casty.setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
            @Override
            public void onConnected() {
                initCastPlayer();
            }

            @Override
            public void onDisconnected() {
                initPlayer();
            }
        });

        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareFacebook(SportDetailsActivity.this, itemSport.getSportName(), itemSport.getSportShareLink());
            }
        });

        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareTwitter(SportDetailsActivity.this, itemSport.getSportName(), itemSport.getSportShareLink(), "", "");
            }
        });

        imgWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareWhatsapp(SportDetailsActivity.this, itemSport.getSportName(), itemSport.getSportShareLink());
            }
        });
    }

    private void initWatchList() {
        btnWatchList.setIconResource(isWatchList ? R.drawable.ic_watch_list_remove : R.drawable.ic_watch_list_add);
        btnWatchList.setText(isWatchList ? getString(R.string.remove_from_watch_list) : getString(R.string.add_to_watch_list));
        btnWatchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getIsLogin()) {
                    if (NetworkUtils.isConnected(SportDetailsActivity.this)) {
                        WatchListClickListener watchListClickListener = new WatchListClickListener() {
                            @Override
                            public void onItemClick(boolean isAddWatchList, String message) {
                                isWatchList = isAddWatchList;
                                btnWatchList.setIconResource(isAddWatchList ? R.drawable.ic_watch_list_remove : R.drawable.ic_watch_list_add);
                                btnWatchList.setText(isAddWatchList ? getString(R.string.remove_from_watch_list) : getString(R.string.add_to_watch_list));
                            }
                        };
                        new WatchList(SportDetailsActivity.this).applyWatch(isWatchList, Id, "Sports", watchListClickListener);
                    } else {
                        showToast(getString(R.string.conne_msg1));
                    }
                } else {
                    showToast(getString(R.string.login_first_watch_list));
                    Intent intentLogin = new Intent(SportDetailsActivity.this, SignInActivity.class);
                    intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentLogin.putExtra("isOtherScreen", true);
                    intentLogin.putExtra("postId", Id);
                    intentLogin.putExtra("postType", "Sport");
                    startActivity(intentLogin);
                }
            }
        });
    }

    private void initRipplePlay() {
        PlayRippleFragment playRippleFragment = PlayRippleFragment.newInstance(itemSport.getSportImage());
        playRippleFragment.setOnSkipClickListener(new RvOnClickListener() {
            @Override
            public void onItemClick(int position) {
                toolbar.setVisibility(View.GONE);
                initPlayer();
            }
        });
        fragmentManager.beginTransaction().replace(R.id.playerSection, playRippleFragment).commitAllowingStateLoss();
    }

    private void initPlayer() {
        if (itemSport.isPremium()) {
            if (isPurchased) {
                setPlayer();
            } else {
                PremiumContentFragment premiumContentFragment = PremiumContentFragment.newInstance(Id, "Sport");
                fragmentManager.beginTransaction().replace(R.id.playerSection, premiumContentFragment).commitAllowingStateLoss();
            }
        } else {
            setPlayer();
        }
    }

    private void initCastPlayer() {
        if (itemSport.isPremium()) {
            if (isPurchased) {
                castScreen();
            } else {
                PremiumContentFragment premiumContentFragment = PremiumContentFragment.newInstance(Id, "Movies");
                fragmentManager.beginTransaction().replace(R.id.playerSection, premiumContentFragment).commitAllowingStateLoss();
            }
        } else {
            castScreen();
        }
    }

    private void initDownload() {
        if (itemSport.isDownload()) {
            if (itemSport.isPremium()) {
                if (isPurchased) {
                    btnDownload.setVisibility(View.VISIBLE);
                } else {
                    btnDownload.setVisibility(View.GONE);
                }
            } else {
                btnDownload.setVisibility(View.VISIBLE);
            }
        } else {
            btnDownload.setVisibility(View.GONE);
        }


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemSport.getDownloadUrl().isEmpty()) {
                    showToast(getString(R.string.download_not_found));
                } else {
                    try {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(itemSport.getDownloadUrl())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Toast.makeText(SportDetailsActivity.this, getString(R.string.invalid_download), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setPlayer() {
        if (itemSport.getSportUrl().isEmpty()) {
            EmbeddedImageFragment embeddedImageFragment = EmbeddedImageFragment.newInstance(itemSport.getSportUrl(), itemSport.getSportImage(), false);
            fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
        } else {
            switch (itemSport.getSportType()) { //URL Embed
                case "Local":
                case "URL":
                case "HLS":
                case "DASH":
                    if (casty.isConnected()) {
                        castScreen();
                    } else {
                        ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(getPlayerData());
                        fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
                    }
                    break;
                case "Embed":
                    EmbeddedImageFragment embeddedImageFragment = EmbeddedImageFragment.newInstance(itemSport.getSportUrl(), itemSport.getSportImage(), true);
                    fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
                    break;
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(SportDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScreen = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            gotoFullScreen();
        } else {
            gotoPortraitScreen();
        }
    }

    private void gotoPortraitScreen() {
        nestedScrollView.setVisibility(View.VISIBLE);
        //    toolbar.setVisibility(View.VISIBLE);
        mAdViewLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, playerHeight));
    }

    private void gotoFullScreen() {
        nestedScrollView.setVisibility(View.GONE);
        //     toolbar.setVisibility(View.GONE);
        mAdViewLayout.setVisibility(View.GONE);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            Events.FullScreen fullScreen = new Events.FullScreen();
            fullScreen.setFullScreen(false);
            GlobalBus.getBus().post(fullScreen);
        } else {
            if (isFromNotification) {
                Intent intent = new Intent(SportDetailsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //    casty.addMediaRouteMenuItem(menu);
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    private void playViaCast() {
        if (itemSport.getSportType().equals("Local") || itemSport.getSportType().equals("URL") || itemSport.getSportType().equals("HLS")) {
            casty.getPlayer().loadMediaAndPlay(createSampleMediaData(itemSport.getSportUrl(), itemSport.getSportName(), itemSport.getSportImage()));
        } else {
            showToast(getResources().getString(R.string.cast_youtube));
        }
    }

    private MediaData createSampleMediaData(String videoUrl, String videoTitle, String videoImage) {
        return new MediaData.Builder(videoUrl)
                .setStreamType(MediaData.STREAM_TYPE_BUFFERED)
                .setContentType(getType(videoUrl))
                .setMediaType(MediaData.MEDIA_TYPE_MOVIE)
                .setTitle(videoTitle)
                .setSubtitle(getString(R.string.app_name))
                .addPhotoUrl(videoImage)
                .build();
    }

    private String getType(String videoUrl) {
        if (videoUrl.endsWith(".mp4")) {
            return "videos/mp4";
        } else if (videoUrl.endsWith(".m3u8")) {
            return "application/x-mpegurl";
        } else {
            return "application/x-mpegurl";
        }
    }

    private void castScreen() {
        ChromecastScreenFragment chromecastScreenFragment = new ChromecastScreenFragment();
        fragmentManager.beginTransaction().replace(R.id.playerSection, chromecastScreenFragment).commitAllowingStateLoss();
        chromecastScreenFragment.setOnItemClickListener(new RvOnClickListener() {
            @Override
            public void onItemClick(int position) {
                playViaCast();
            }
        });
    }

    private ItemPlayer getPlayerData() {
        ItemPlayer itemPlayer = new ItemPlayer();
        itemPlayer.setDefaultUrl(itemSport.getSportUrl());
        if (itemSport.getSportType().equals("Local") || itemSport.getSportType().equals("URL")) {
            itemPlayer.setQuality(itemSport.isQuality());
            itemPlayer.setSubTitle(itemSport.isSubTitle());
            itemPlayer.setQuality480(itemSport.getQuality480());
            itemPlayer.setQuality720(itemSport.getQuality720());
            itemPlayer.setQuality1080(itemSport.getQuality1080());

            ArrayList<ItemSubTitle> itemSubTitles = new ArrayList<>();
            ItemSubTitle subTitleOff = new ItemSubTitle("0", getString(R.string.off_sub_title), "");
            itemSubTitles.add(subTitleOff);
            if (!itemSport.getSubTitleLanguage1().isEmpty()) {
                ItemSubTitle subTitle1 = new ItemSubTitle("1", itemSport.getSubTitleLanguage1(), itemSport.getSubTitleUrl1());
                itemSubTitles.add(subTitle1);
            }
            if (!itemSport.getSubTitleLanguage2().isEmpty()) {
                ItemSubTitle subTitle2 = new ItemSubTitle("2", itemSport.getSubTitleLanguage2(), itemSport.getSubTitleUrl2());
                itemSubTitles.add(subTitle2);
            }
            if (!itemSport.getSubTitleLanguage3().isEmpty()) {
                ItemSubTitle subTitle3 = new ItemSubTitle("3", itemSport.getSubTitleLanguage3(), itemSport.getSubTitleUrl3());
                itemSubTitles.add(subTitle3);
            }
            itemPlayer.setSubTitles(itemSubTitles);

            if (itemSport.getQuality480().isEmpty() && itemSport.getQuality720().isEmpty() && itemSport.getQuality1080().isEmpty()) {
                itemPlayer.setQuality(false);
            }

            if (itemSport.getSubTitleLanguage1().isEmpty() && itemSport.getSubTitleLanguage2().isEmpty() && itemSport.getSubTitleLanguage3().isEmpty()) {
                itemPlayer.setSubTitle(false);
            }
        } else {
            itemPlayer.setQuality(false);
            itemPlayer.setSubTitle(false);
        }
        return itemPlayer;
    }
}
