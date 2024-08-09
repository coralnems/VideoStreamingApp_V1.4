package com.example.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.HomeAdapter;
import com.example.adapter.SliderAdapter;
import com.example.item.ItemHome;
import com.example.item.ItemHomeContent;
import com.example.item.ItemSlider;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.EnchantedViewPager;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.MainActivity;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
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

public class HomeFragment extends Fragment {

    private ProgressBar mProgressBar;
    private LinearLayout lyt_not_found;
    private NestedScrollView nestedScrollView;
    private EnchantedViewPager viewPager;
    private ArrayList<ItemHome> homeList;
    private ArrayList<ItemSlider> sliderList;
    private RecyclerView rvHome;
    private SliderAdapter sliderAdapter;
    private RelativeLayout lytSlider;
    private MyApplication myApplication;
    HomeAdapter homeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        myApplication = MyApplication.getInstance();

        homeList = new ArrayList<>();
        sliderList = new ArrayList<>();
        mProgressBar = rootView.findViewById(R.id.progressBar1);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
        viewPager = rootView.findViewById(R.id.viewPager);
        viewPager.useScale();
        viewPager.removeAlpha();
        lytSlider = rootView.findViewById(R.id.lytSlider);
        rvHome = rootView.findViewById(R.id.rv_home);

        recyclerViewProperty(rvHome);

        if (NetworkUtils.isConnected(getActivity())) {
            getHome();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }


        return rootView;
    }

    private void recyclerViewProperty(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
    }


    private void getHome() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.HOME_URL, params, new AsyncHttpResponseHandler() {
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


                    JSONArray sliderArray = liveTVJson.getJSONArray("slider");
                    for (int i = 0; i < sliderArray.length(); i++) {
                        JSONObject jsonObject = sliderArray.getJSONObject(i);
                        ItemSlider itemSlider = new ItemSlider();
                        itemSlider.setId(jsonObject.getString("slider_post_id"));
                        itemSlider.setSliderTitle(jsonObject.getString("slider_title"));
                        itemSlider.setSliderImage(jsonObject.getString("slider_image"));
                        itemSlider.setSliderType(jsonObject.getString("slider_type"));
                        itemSlider.setPremium(jsonObject.getString("video_access").equals("Paid"));
                        sliderList.add(itemSlider);
                    }

                    if (liveTVJson.has("recently_watched")) {
                        JSONArray recentArray = liveTVJson.getJSONArray("recently_watched");
                        if (recentArray.length() > 0) {
                            ItemHome objItem = new ItemHome();
                            objItem.setHomeId("-1");
                            objItem.setHomeTitle(getString(R.string.home_recently_watched));
                            objItem.setHomeType("Recent");

                            ArrayList<ItemHomeContent> homeContentList = new ArrayList<>();
                            for (int i = 0; i < recentArray.length(); i++) {
                                JSONObject jsonObject = recentArray.getJSONObject(i);
                                ItemHomeContent itemHomeContent = new ItemHomeContent();
                                itemHomeContent.setVideoId(jsonObject.getString("video_id"));
                                itemHomeContent.setVideoImage(jsonObject.getString("video_thumb_image"));
                                itemHomeContent.setVideoType(jsonObject.getString("video_type"));
                                itemHomeContent.setHomeType("Recent");
                                homeContentList.add(itemHomeContent);
                            }
                            objItem.setItemHomeContents(homeContentList);
                            homeList.add(objItem);
                        }
                    }

                    if (liveTVJson.has("upcoming_movies")) {
                        JSONArray upcomingMovieArray = liveTVJson.getJSONArray("upcoming_movies");
                        if (upcomingMovieArray.length() > 0) {
                            ItemHome objItem = new ItemHome();
                            objItem.setHomeId("-1");
                            objItem.setHomeTitle(getString(R.string.home_upcoming_movie));
                            objItem.setHomeType("Movie");

                            ArrayList<ItemHomeContent> homeContentList = new ArrayList<>();
                            for (int i = 0; i < upcomingMovieArray.length(); i++) {
                                JSONObject objJson = upcomingMovieArray.getJSONObject(i);
                                ItemHomeContent itemHomeContent = new ItemHomeContent();
                                itemHomeContent.setVideoId(objJson.getString(Constant.MOVIE_ID));
                                itemHomeContent.setVideoTitle(objJson.getString(Constant.MOVIE_TITLE));
                                itemHomeContent.setVideoImage(objJson.getString(Constant.MOVIE_POSTER));
                                itemHomeContent.setVideoType("Movie");
                                itemHomeContent.setHomeType("Movie");
                                itemHomeContent.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                                homeContentList.add(itemHomeContent);
                            }
                            objItem.setItemHomeContents(homeContentList);
                            homeList.add(objItem);
                        }
                    }

                    if (liveTVJson.has("upcoming_series")) {
                        JSONArray upcomingSeriesArray = liveTVJson.getJSONArray("upcoming_series");
                        if (upcomingSeriesArray.length() > 0) {
                            ItemHome objItem = new ItemHome();
                            objItem.setHomeId("-1");
                            objItem.setHomeTitle(getString(R.string.home_upcoming_show));
                            objItem.setHomeType("Shows");

                            ArrayList<ItemHomeContent> homeContentList = new ArrayList<>();
                            for (int i = 0; i < upcomingSeriesArray.length(); i++) {
                                JSONObject objJson = upcomingSeriesArray.getJSONObject(i);
                                ItemHomeContent itemHomeContent = new ItemHomeContent();
                                itemHomeContent.setVideoId(objJson.getString(Constant.SHOW_ID));
                                itemHomeContent.setVideoTitle(objJson.getString(Constant.SHOW_TITLE));
                                itemHomeContent.setVideoImage(objJson.getString(Constant.SHOW_POSTER));
                                itemHomeContent.setVideoType("Shows");
                                itemHomeContent.setHomeType("Shows");
                                itemHomeContent.setPremium(objJson.getString(Constant.SHOW_ACCESS).equals("Paid"));
                                homeContentList.add(itemHomeContent);
                            }
                            objItem.setItemHomeContents(homeContentList);
                            homeList.add(objItem);
                        }
                    }

                    if (liveTVJson.has("home_sections")) {
                        JSONArray homeSecArray = liveTVJson.getJSONArray("home_sections");
                        for (int i = 0; i < homeSecArray.length(); i++) {
                            JSONObject objJson = homeSecArray.getJSONObject(i);
                            ItemHome objItem = new ItemHome();
                            objItem.setHomeId(objJson.getString("home_id"));
                            objItem.setHomeTitle(objJson.getString("home_title"));
                            objItem.setHomeType(objJson.getString("home_type"));

                            JSONArray homeContentArray = objJson.getJSONArray("home_content");
                            ArrayList<ItemHomeContent> homeContentList = new ArrayList<>();
                            for (int j = 0; j < homeContentArray.length(); j++) {
                                JSONObject objJsonSec = homeContentArray.getJSONObject(j);
                                ItemHomeContent itemHomeContent = new ItemHomeContent();
                                itemHomeContent.setVideoId(objJsonSec.getString("video_id"));
                                itemHomeContent.setVideoTitle(objJsonSec.getString("video_title"));
                                itemHomeContent.setVideoImage(objJsonSec.getString("video_image"));
                                itemHomeContent.setVideoType(objJsonSec.getString("video_type"));
                                itemHomeContent.setHomeType(objJsonSec.getString("video_type"));
                                itemHomeContent.setPremium(objJsonSec.getString("video_access").equals("Paid"));
                                homeContentList.add(itemHomeContent);
                            }
                            objItem.setItemHomeContents(homeContentList);
                            if (!homeContentList.isEmpty()) // when there is not date in section
                                homeList.add(objItem);
                        }
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

        if (!sliderList.isEmpty()) {
            sliderAdapter = new SliderAdapter(requireActivity(), sliderList);
            viewPager.setAdapter(sliderAdapter);
        } else {
            lytSlider.setVisibility(View.GONE);
        }

        if (!homeList.isEmpty()) {
            homeAdapter = new HomeAdapter(getActivity(), homeList);
            rvHome.setAdapter(homeAdapter);
            homeAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    ItemHome itemHome = homeList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", itemHome.getHomeId());
                    bundle.putString("Type", itemHome.getHomeType());
                    HomeContentMoreFragment homeMovieMoreFragment = new HomeContentMoreFragment();
                    homeMovieMoreFragment.setArguments(bundle);
                    changeFragment(homeMovieMoreFragment, itemHome.getHomeTitle());

                }
            });
        } else {
            rvHome.setVisibility(View.GONE);
        }

    }

    private void changeFragment(Fragment fragment, String Name) {
        FragmentManager fm = getFragmentManager();
        assert fm != null;
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(HomeFragment.this);
        ft.add(R.id.Container, fragment, Name);
        ft.addToBackStack(Name);
        ft.commit();
        ((MainActivity) requireActivity()).setToolbarTitle(Name);
    }
}
