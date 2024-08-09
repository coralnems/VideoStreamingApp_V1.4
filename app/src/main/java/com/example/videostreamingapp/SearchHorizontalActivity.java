package com.example.videostreamingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.HomeMovieAdapter;
import com.example.adapter.HomeShowAdapter;
import com.example.adapter.HomeSportAdapter;
import com.example.adapter.HomeTVAdapter;
import com.example.item.ItemMovie;
import com.example.item.ItemShow;
import com.example.item.ItemSport;
import com.example.item.ItemTV;
import com.example.util.API;
import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
import com.example.util.StatusBarUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SearchHorizontalActivity extends AppCompatActivity {

    String search;
    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    NestedScrollView nestedScrollView;
    TextView movieViewAll, showViewAll, sportViewAll;
    RecyclerView rvMovie, rvShow, rvSport, rvTV;
    ArrayList<ItemMovie> movieList;
    ArrayList<ItemShow> showList;
    ArrayList<ItemSport> sportList;
    ArrayList<ItemTV> tvList;

    HomeMovieAdapter movieAdapter;
    HomeShowAdapter showAdapter;
    HomeSportAdapter sportAdapter;
    HomeTVAdapter tvAdapter;

    LinearLayout lytMovie, lytShow, lytSport, lytTV;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarGradiant(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_horizontal);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_search));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        search = intent.getStringExtra("search");

        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        BannerAds.showBannerAds(this, mAdViewLayout);

        movieList = new ArrayList<>();
        showList = new ArrayList<>();
        sportList = new ArrayList<>();
        tvList = new ArrayList<>();

        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        movieViewAll = findViewById(R.id.textLatestMovieViewAll);
        showViewAll = findViewById(R.id.textTVSeriesViewAll);
        sportViewAll = findViewById(R.id.textLatestChannelViewAll);

        lytMovie = findViewById(R.id.lytMovie);
        lytShow = findViewById(R.id.lytHomeTVSeries);
        lytSport = findViewById(R.id.lytHomeLatestChannel);
        lytTV = findViewById(R.id.lytSearchTV);


        rvMovie = findViewById(R.id.rv_latest_movie);
        rvShow = findViewById(R.id.rv_tv_series);
        rvSport = findViewById(R.id.rv_latest_channel);
        rvTV = findViewById(R.id.rv_tv);


        rvMovie.setHasFixedSize(true);
        rvMovie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMovie.setFocusable(false);
        rvMovie.setNestedScrollingEnabled(false);

        rvShow.setHasFixedSize(true);
        rvShow.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvShow.setFocusable(false);
        rvShow.setNestedScrollingEnabled(false);

        rvSport.setHasFixedSize(true);
        rvSport.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSport.setFocusable(false);
        rvSport.setNestedScrollingEnabled(false);


        rvTV.setHasFixedSize(true);
        rvTV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTV.setFocusable(false);
        rvTV.setNestedScrollingEnabled(false);

        if (NetworkUtils.isConnected(SearchHorizontalActivity.this)) {
            getSearchAll();
        } else {
            Toast.makeText(SearchHorizontalActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

    }

    private void getSearchAll() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("search_text", search);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.SEARCH_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject liveTVJson = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray showArray = liveTVJson.getJSONArray("shows");
                    for (int i = 0; i < showArray.length(); i++) {
                        JSONObject jsonObject = showArray.getJSONObject(i);

                        ItemShow itemShow = new ItemShow();
                        itemShow.setShowId(jsonObject.getString(Constant.SHOW_ID));
                        itemShow.setShowName(jsonObject.getString(Constant.SHOW_TITLE));
                        itemShow.setShowImage(jsonObject.getString(Constant.SHOW_POSTER));
                        itemShow.setPremium(jsonObject.getString(Constant.SHOW_ACCESS).equals("Paid"));
                        showList.add(itemShow);
                    }

                    JSONArray movieArray = liveTVJson.getJSONArray("movies");
                    for (int i = 0; i < movieArray.length(); i++) {
                        JSONObject jsonObject = movieArray.getJSONObject(i);

                        ItemMovie itemMovie = new ItemMovie();
                        itemMovie.setMovieId(jsonObject.getString(Constant.MOVIE_ID));
                        itemMovie.setMovieName(jsonObject.getString(Constant.MOVIE_TITLE));
                        itemMovie.setMovieImage(jsonObject.getString(Constant.MOVIE_POSTER));
                        itemMovie.setMovieDuration(jsonObject.getString(Constant.MOVIE_DURATION));
                        itemMovie.setPremium(jsonObject.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                        movieList.add(itemMovie);

                    }

                    JSONArray sportArray = liveTVJson.getJSONArray("sports");
                    for (int i = 0; i < sportArray.length(); i++) {
                        JSONObject jsonObject = sportArray.getJSONObject(i);

                        ItemSport objItem = new ItemSport();
                        objItem.setSportId(jsonObject.getString(Constant.SPORT_ID));
                        objItem.setSportName(jsonObject.getString(Constant.SPORT_TITLE));
                        objItem.setSportImage(jsonObject.getString(Constant.SPORT_IMAGE));
                        objItem.setPremium(jsonObject.getString(Constant.SPORT_ACCESS).equals("Paid"));
                        sportList.add(objItem);
                    }


                    JSONArray tvArray = liveTVJson.getJSONArray("live_tv");
                    for (int i = 0; i < tvArray.length(); i++) {
                        JSONObject jsonObject = tvArray.getJSONObject(i);

                        ItemTV objItem = new ItemTV();
                        objItem.setTvId(jsonObject.getString(Constant.TV_ID));
                        objItem.setTvName(jsonObject.getString(Constant.TV_TITLE));
                        objItem.setTvImage(jsonObject.getString(Constant.TV_IMAGE));
                        objItem.setPremium(jsonObject.getString(Constant.TV_ACCESS).equals("Paid"));
                        tvList.add(objItem);
                    }

                    displayData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    nestedScrollView.setVisibility(View.GONE);
                    lyt_not_found.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {

        if (!movieList.isEmpty()) {
            movieAdapter = new HomeMovieAdapter(SearchHorizontalActivity.this, movieList, false);
            rvMovie.setAdapter(movieAdapter);

            movieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = movieList.get(position).getMovieId();
                    Intent intent = new Intent(SearchHorizontalActivity.this, MovieDetailsActivity.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytMovie.setVisibility(View.GONE);
        }

        if (!showList.isEmpty()) {
            showAdapter = new HomeShowAdapter(SearchHorizontalActivity.this, showList, false);
            rvShow.setAdapter(showAdapter);

            showAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String seriesId = showList.get(position).getShowId();
                    Intent intent = new Intent(SearchHorizontalActivity.this, ShowDetailsActivity.class);
                    intent.putExtra("Id", seriesId);
                    startActivity(intent);
                }
            });

        } else {
            lytShow.setVisibility(View.GONE);
        }

        if (!sportList.isEmpty()) {
            sportAdapter = new HomeSportAdapter(SearchHorizontalActivity.this, sportList);
            rvSport.setAdapter(sportAdapter);

            sportAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String tvId = sportList.get(position).getSportId();
                    Intent intent = new Intent(SearchHorizontalActivity.this, SportDetailsActivity.class);
                    intent.putExtra("Id", tvId);
                    startActivity(intent);
                }
            });


        } else {
            lytSport.setVisibility(View.GONE);
        }


        if (!tvList.isEmpty()) {
            tvAdapter = new HomeTVAdapter(SearchHorizontalActivity.this, tvList);
            rvTV.setAdapter(tvAdapter);

            tvAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String tvId = tvList.get(position).getTvId();
                    Intent intent = new Intent(SearchHorizontalActivity.this, TVDetailsActivity.class);
                    intent.putExtra("Id", tvId);
                    startActivity(intent);
                }
            });


        } else {
            lytTV.setVisibility(View.GONE);
        }

        if (movieList.isEmpty() && showList.isEmpty() && tvList.isEmpty() && sportList.isEmpty()) {
            lyt_not_found.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
