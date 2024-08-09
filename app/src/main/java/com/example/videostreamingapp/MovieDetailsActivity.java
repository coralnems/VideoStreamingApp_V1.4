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

import com.example.adapter.ActorDirectorAdapter;
import com.example.adapter.HomeMovieAdapter;
import com.example.adapter.InfoAdapter;
import com.example.cast.Casty;
import com.example.cast.MediaData;
import com.example.fragment.ChromecastScreenFragment;
import com.example.fragment.EmbeddedImageFragment;
import com.example.fragment.ExoPlayerFragment;
import com.example.fragment.PlayRippleFragment;
import com.example.fragment.PremiumContentFragment;
import com.example.fragment.TrailerExoPlayerFragment;
import com.example.item.ItemActor;
import com.example.item.ItemMovie;
import com.example.item.ItemPlayer;
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

public class MovieDetailsActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    RelativeLayout lytParent;
    WebView webView;
    TextView textTitle, tvImdb;
    RecyclerView rvRelated, rvActor, rvDirector, rvInfo;
    ItemMovie itemMovie;
    ArrayList<ItemMovie> mListItemRelated;
    ArrayList<ItemActor> mListItemActor, mListItemDirector;
    ArrayList<String> mListInfo, movieGenreList;
    HomeMovieAdapter homeMovieAdapter;
    ActorDirectorAdapter actorAdapter, directorAdapter;
    String Id;
    LinearLayout lytRelated, lytActor, lytDirector;
    MyApplication myApplication;
    NestedScrollView nestedScrollView;
    Toolbar toolbar;
    private FragmentManager fragmentManager;
    private int playerHeight;
    FrameLayout frameLayout;
    boolean isFullScreen = false;
    boolean isFromNotification = false;
    LinearLayout mAdViewLayout;
    boolean isPurchased = false, isWatchList = false, isUpcoming = false;
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
        setContentView(R.layout.activity_movie_details);
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
        mListItemActor = new ArrayList<>();
        mListItemDirector = new ArrayList<>();
        mListInfo = new ArrayList<>();
        movieGenreList = new ArrayList<>();
        lytActor = findViewById(R.id.lytActors);
        lytDirector = findViewById(R.id.lytDirector);
        rvActor = findViewById(R.id.rv_actor);
        rvDirector = findViewById(R.id.rv_director);
        rvInfo = findViewById(R.id.rv_info);

        itemMovie = new ItemMovie();
        lytRelated = findViewById(R.id.lytRelated);
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        lytParent = findViewById(R.id.lytParent);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        webView = findViewById(R.id.webView);
        textTitle = findViewById(R.id.textTitle);
        tvImdb = findViewById(R.id.tvImdb);
        rvRelated = findViewById(R.id.rv_related);
        btnDownload = findViewById(R.id.btnDownload);

        imgFacebook = findViewById(R.id.imgFacebook);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgWhatsApp = findViewById(R.id.imgWhatsApp);
        btnWatchList = findViewById(R.id.btnWatchList);

        rvRelated.setHasFixedSize(true);
        rvRelated.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvRelated.setFocusable(false);
        rvRelated.setNestedScrollingEnabled(false);

        rvActor.setHasFixedSize(true);
        rvActor.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvActor.setFocusable(false);
        rvActor.setNestedScrollingEnabled(false);

        rvDirector.setHasFixedSize(true);
        rvDirector.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvDirector.setFocusable(false);
        rvDirector.setNestedScrollingEnabled(false);

        rvInfo.setHasFixedSize(true);
        rvInfo.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvInfo.setFocusable(false);
        rvInfo.setNestedScrollingEnabled(false);

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);
        if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

    }

    private void getDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("movie_id", Id);
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.MOVIE_DETAILS_URL, params, new AsyncHttpResponseHandler() {
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
                            isUpcoming = objJson.getBoolean(Constant.UPCOMING_STATUS);
                            itemMovie.setMovieId(objJson.getString(Constant.MOVIE_ID));
                            itemMovie.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                            itemMovie.setMovieDescription(objJson.getString(Constant.MOVIE_DESC));
                            itemMovie.setMovieImage(objJson.getString(Constant.MOVIE_IMAGE));
                            itemMovie.setMovieLanguage(objJson.getString(Constant.MOVIE_LANGUAGE));
                            itemMovie.setMovieUrl(objJson.getString(Constant.MOVIE_URL));
                            itemMovie.setMovieType(objJson.getString(Constant.MOVIE_TYPE));
                            itemMovie.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                            itemMovie.setMovieDate(objJson.getString(Constant.MOVIE_DATE));
                            itemMovie.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                            itemMovie.setDownload(objJson.getBoolean(Constant.DOWNLOAD_ENABLE));
                            itemMovie.setDownloadUrl(objJson.getString(Constant.DOWNLOAD_URL));
                            itemMovie.setMovieRating(objJson.getString(Constant.IMDB_RATING));
                            itemMovie.setMovieShareLink(objJson.getString(Constant.MOVIE_SHARE_LINK));
                            itemMovie.setMovieTrailer(objJson.getString(Constant.MOVIE_TRAILER_URL));
                            itemMovie.setMovieContentRating(objJson.getString(Constant.MOVIE_CONTENT_RATING));
                            itemMovie.setMovieView(objJson.getString(Constant.MOVIE_VIEW));

                            itemMovie.setQuality(objJson.getBoolean(Constant.IS_QUALITY));
                            itemMovie.setSubTitle(objJson.getBoolean(Constant.IS_SUBTITLE));
                            itemMovie.setQuality480(objJson.getString(Constant.QUALITY_480));
                            itemMovie.setQuality720(objJson.getString(Constant.QUALITY_720));
                            itemMovie.setQuality1080(objJson.getString(Constant.QUALITY_1080));

                            itemMovie.setSubTitleLanguage1(objJson.getString(Constant.SUBTITLE_LANGUAGE_1));
                            itemMovie.setSubTitleUrl1(objJson.getString(Constant.SUBTITLE_URL_1));
                            itemMovie.setSubTitleLanguage2(objJson.getString(Constant.SUBTITLE_LANGUAGE_2));
                            itemMovie.setSubTitleUrl2(objJson.getString(Constant.SUBTITLE_URL_2));
                            itemMovie.setSubTitleLanguage3(objJson.getString(Constant.SUBTITLE_LANGUAGE_3));
                            itemMovie.setSubTitleUrl3(objJson.getString(Constant.SUBTITLE_URL_3));

                            JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_MOVIE_ARRAY_NAME);
                            if (jsonArrayChild.length() != 0) {
                                for (int j = 0; j < jsonArrayChild.length(); j++) {
                                    JSONObject objChild = jsonArrayChild.getJSONObject(j);
                                    ItemMovie item = new ItemMovie();
                                    item.setMovieId(objChild.getString(Constant.MOVIE_ID));
                                    item.setMovieName(objChild.getString(Constant.MOVIE_TITLE));
                                    item.setMovieImage(objChild.getString(Constant.MOVIE_POSTER));
                                    item.setMovieDuration(objChild.getString(Constant.MOVIE_DURATION));
                                    item.setPremium(objChild.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                                    mListItemRelated.add(item);
                                }
                            }

                            JSONArray jsonArrayActor = objJson.getJSONArray(Constant.ACTOR_ARRAY);
                            if (jsonArrayActor.length() != 0) {
                                for (int j = 0; j < jsonArrayActor.length(); j++) {
                                    JSONObject objChild = jsonArrayActor.getJSONObject(j);
                                    ItemActor item = new ItemActor();
                                    item.setActorId(objChild.getString(Constant.ACTOR_ID));
                                    item.setActorName(objChild.getString(Constant.ACTOR_NAME));
                                    item.setActorImage(objChild.getString(Constant.ACTOR_IMAGE));
                                    mListItemActor.add(item);
                                }
                            }

                            JSONArray jsonArrayDirector = objJson.getJSONArray(Constant.DIRECTOR_ARRAY);
                            if (jsonArrayDirector.length() != 0) {
                                for (int j = 0; j < jsonArrayDirector.length(); j++) {
                                    JSONObject objChild = jsonArrayDirector.getJSONObject(j);
                                    ItemActor item = new ItemActor();
                                    item.setActorId(objChild.getString(Constant.ACTOR_ID));
                                    item.setActorName(objChild.getString(Constant.ACTOR_NAME));
                                    item.setActorImage(objChild.getString(Constant.ACTOR_IMAGE));
                                    mListItemDirector.add(item);
                                }
                            }


                            JSONArray jsonArrayGenre = objJson.getJSONArray(Constant.GENRE_LIST);
                            if (jsonArrayGenre.length() != 0) {
                                for (int k = 0; k < jsonArrayGenre.length(); k++) {
                                    JSONObject objChild = jsonArrayGenre.getJSONObject(k);
                                    movieGenreList.add(objChild.getString(Constant.GENRE_NAME));
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
                    mProgressBar.setVisibility(View.GONE);
                    lytParent.setVisibility(View.GONE);
                    lyt_not_found.setVisibility(View.VISIBLE);
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
        setTitle("");//itemMovie.getMovieName()
        textTitle.setText(itemMovie.getMovieName());

        mListInfo.add(itemMovie.getMovieDate());
        if (!itemMovie.getMovieDuration().isEmpty() && !itemMovie.getMovieDuration().equals("null")) {
            mListInfo.add(itemMovie.getMovieDuration());
        }
        if (!itemMovie.getMovieContentRating().isEmpty()) {
            mListInfo.add(getString(R.string.content_rating, itemMovie.getMovieContentRating()));
        }
        if (!itemMovie.getMovieView().isEmpty()) {
            mListInfo.add(getString(R.string.view, itemMovie.getMovieView()));
        }

        mListInfo.add(itemMovie.getMovieLanguage());
        mListInfo.addAll(movieGenreList);

        infoAdapter = new InfoAdapter(mListInfo);
        rvInfo.setAdapter(infoAdapter);

        if (!itemMovie.getMovieRating().isEmpty() && !itemMovie.getMovieRating().equals("0")) {
            tvImdb.setText(getString(R.string.imdb, itemMovie.getMovieRating()));
        } else {
            tvImdb.setVisibility(View.GONE);
        }

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = itemMovie.getMovieDescription();

        boolean isRTL = Boolean.parseBoolean(getResources().getString(R.string.isRTL));
        String direction = isRTL ? "rtl" : "ltr";

        String text = "<html dir=" + direction + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.otf\")}body{font-family: MyFont;color: #9c9c9c;font-size:14px;margin-left:0px;line-height:1.3}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        initRipplePlay();//initTrailerPlayer();//initPlayer();
        initDownload();
        initWatchList();

        if (!mListItemRelated.isEmpty()) {
            homeMovieAdapter = new HomeMovieAdapter(MovieDetailsActivity.this, mListItemRelated, false);
            rvRelated.setAdapter(homeMovieAdapter);

            homeMovieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = mListItemRelated.get(position).getMovieId();
                    Intent intent = new Intent(MovieDetailsActivity.this, MovieDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytRelated.setVisibility(View.GONE);
        }

        if (!mListItemActor.isEmpty()) {
            actorAdapter = new ActorDirectorAdapter(MovieDetailsActivity.this, mListItemActor);
            rvActor.setAdapter(actorAdapter);

            actorAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String adId = mListItemActor.get(position).getActorId();
                    String adName = mListItemActor.get(position).getActorName();
                    Intent intent = new Intent(MovieDetailsActivity.this, ActorDirectorDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("adId", adId);
                    intent.putExtra("adName", adName);
                    intent.putExtra("isActor", true);
                    startActivity(intent);
                }
            });

        } else {
            lytActor.setVisibility(View.GONE);
        }

        if (!mListItemDirector.isEmpty()) {
            directorAdapter = new ActorDirectorAdapter(MovieDetailsActivity.this, mListItemDirector);
            rvDirector.setAdapter(directorAdapter);

            directorAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String adId = mListItemDirector.get(position).getActorId();
                    String adName = mListItemDirector.get(position).getActorName();
                    Intent intent = new Intent(MovieDetailsActivity.this, ActorDirectorDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("adId", adId);
                    intent.putExtra("adName", adName);
                    intent.putExtra("isActor", false);
                    startActivity(intent);
                }
            });

        } else {
            lytDirector.setVisibility(View.GONE);
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
                ShareUtils.shareFacebook(MovieDetailsActivity.this, itemMovie.getMovieName(), itemMovie.getMovieShareLink());
            }
        });

        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareTwitter(MovieDetailsActivity.this, itemMovie.getMovieName(), itemMovie.getMovieShareLink(), "", "");
            }
        });

        imgWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareWhatsapp(MovieDetailsActivity.this, itemMovie.getMovieName(), itemMovie.getMovieShareLink());
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
                    if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                        WatchListClickListener watchListClickListener = new WatchListClickListener() {
                            @Override
                            public void onItemClick(boolean isAddWatchList, String message) {
                                isWatchList = isAddWatchList;
                                btnWatchList.setIconResource(isAddWatchList ? R.drawable.ic_watch_list_remove : R.drawable.ic_watch_list_add);
                                btnWatchList.setText(isAddWatchList ? getString(R.string.remove_from_watch_list) : getString(R.string.add_to_watch_list));
                            }
                        };
                        new WatchList(MovieDetailsActivity.this).applyWatch(isWatchList, Id, "Movies", watchListClickListener);
                    } else {
                        showToast(getString(R.string.conne_msg1));
                    }
                } else {
                    showToast(getString(R.string.login_first_watch_list));
                    Intent intentLogin = new Intent(MovieDetailsActivity.this, SignInActivity.class);
                    intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentLogin.putExtra("isOtherScreen", true);
                    intentLogin.putExtra("postId", Id);
                    intentLogin.putExtra("postType", "Movies");
                    startActivity(intentLogin);
                }
            }
        });
    }

    private void initRipplePlay() {
        PlayRippleFragment playRippleFragment = PlayRippleFragment.newInstance(itemMovie.getMovieImage());
        playRippleFragment.setOnSkipClickListener(new RvOnClickListener() {
            @Override
            public void onItemClick(int position) {
                toolbar.setVisibility(View.GONE);
                initTrailerPlayer();
            }
        });
        fragmentManager.beginTransaction().replace(R.id.playerSection, playRippleFragment).commitAllowingStateLoss();
    }

    private void initTrailerPlayer() {
        if (itemMovie.getMovieTrailer().isEmpty()) {
            initPlayer();
        } else {
            TrailerExoPlayerFragment trailerExoPlayerFragment = TrailerExoPlayerFragment.newInstance(itemMovie.getMovieTrailer());
            trailerExoPlayerFragment.setOnSkipClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    initPlayer();
                }
            });
            fragmentManager.beginTransaction().replace(R.id.playerSection, trailerExoPlayerFragment).commitAllowingStateLoss();
        }
    }

    private void initPlayer() {
        if (itemMovie.isPremium()) {
            if (isPurchased) {
                setPlayer();
            } else {
                PremiumContentFragment premiumContentFragment = PremiumContentFragment.newInstance(Id, "Movies");
                fragmentManager.beginTransaction().replace(R.id.playerSection, premiumContentFragment).commitAllowingStateLoss();
            }
        } else {
            setPlayer();
        }
    }

    private void initCastPlayer() {
        if (itemMovie.isPremium()) {
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
        if (itemMovie.isDownload()) {
            if (itemMovie.isPremium()) {
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
                if (itemMovie.getDownloadUrl().isEmpty()) {
                    showToast(getString(R.string.download_not_found));
                } else {
                    try {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(itemMovie.getDownloadUrl())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Toast.makeText(MovieDetailsActivity.this, getString(R.string.invalid_download), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setPlayer() {
        if (itemMovie.getMovieUrl().isEmpty() || isUpcoming) {
            EmbeddedImageFragment embeddedImageFragment = EmbeddedImageFragment.newInstance(itemMovie.getMovieUrl(), itemMovie.getMovieImage(), false);
            fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
        } else {
            switch (itemMovie.getMovieType()) { //URL Embed
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
                    EmbeddedImageFragment embeddedImageFragment = EmbeddedImageFragment.newInstance(itemMovie.getMovieUrl(), itemMovie.getMovieImage(), true);
                    fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
                    break;

            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(MovieDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        //  toolbar.setVisibility(View.VISIBLE);
        mAdViewLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, playerHeight));
    }

    private void gotoFullScreen() {
        nestedScrollView.setVisibility(View.GONE);
        //   toolbar.setVisibility(View.GONE);
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
                Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
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
        //   casty.addMediaRouteMenuItem(menu);
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    private void playViaCast() {
        if (itemMovie.getMovieType().equals("Local") || itemMovie.getMovieType().equals("URL") || itemMovie.getMovieType().equals("HLS")) {
            casty.getPlayer().loadMediaAndPlay(createSampleMediaData(itemMovie.getMovieUrl(), itemMovie.getMovieName(), itemMovie.getMovieImage()));
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
        itemPlayer.setDefaultUrl(itemMovie.getMovieUrl());
        if (itemMovie.getMovieType().equals("Local") || itemMovie.getMovieType().equals("URL")) {
            itemPlayer.setQuality(itemMovie.isQuality());
            itemPlayer.setSubTitle(itemMovie.isSubTitle());
            itemPlayer.setQuality480(itemMovie.getQuality480());
            itemPlayer.setQuality720(itemMovie.getQuality720());
            itemPlayer.setQuality1080(itemMovie.getQuality1080());

            ArrayList<ItemSubTitle> itemSubTitles = new ArrayList<>();
            ItemSubTitle subTitleOff = new ItemSubTitle("0", getString(R.string.off_sub_title), "");
            itemSubTitles.add(subTitleOff);
            if (!itemMovie.getSubTitleLanguage1().isEmpty()) {
                ItemSubTitle subTitle1 = new ItemSubTitle("1", itemMovie.getSubTitleLanguage1(), itemMovie.getSubTitleUrl1());
                itemSubTitles.add(subTitle1);
            }
            if (!itemMovie.getSubTitleLanguage2().isEmpty()) {
                ItemSubTitle subTitle2 = new ItemSubTitle("2", itemMovie.getSubTitleLanguage2(), itemMovie.getSubTitleUrl2());
                itemSubTitles.add(subTitle2);
            }
            if (!itemMovie.getSubTitleLanguage3().isEmpty()) {
                ItemSubTitle subTitle3 = new ItemSubTitle("3", itemMovie.getSubTitleLanguage3(), itemMovie.getSubTitleUrl3());
                itemSubTitles.add(subTitle3);
            }
            itemPlayer.setSubTitles(itemSubTitles);

            if (itemMovie.getQuality480().isEmpty() && itemMovie.getQuality720().isEmpty() && itemMovie.getQuality1080().isEmpty()) {
                itemPlayer.setQuality(false);
            }

            if (itemMovie.getSubTitleLanguage1().isEmpty() && itemMovie.getSubTitleLanguage2().isEmpty() && itemMovie.getSubTitleLanguage3().isEmpty()) {
                itemPlayer.setSubTitle(false);
            }
        } else {
            itemPlayer.setQuality(false);
            itemPlayer.setSubTitle(false);
        }
        return itemPlayer;
    }

}
