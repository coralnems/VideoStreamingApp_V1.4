package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videostreamingapp.SportDetailsActivity;
import com.example.videostreamingapp.TVDetailsActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.example.adapter.HomeContentAdapter;
import com.example.item.ItemHomeContent;
import com.example.videostreamingapp.MovieDetailsActivity;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.ShowDetailsActivity;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeContentMoreFragment extends Fragment {

    private ArrayList<ItemHomeContent> mListItem;
    private RecyclerView recyclerView;
    private HomeContentAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    private String Id, Type;
    GridLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        if (getArguments() != null) {
            Id = getArguments().getString("Id");
            Type = getArguments().getString("Type");
        }

        mListItem = new ArrayList<>();
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), Type.equals("Movie") ? 3 : 2);
        recyclerView.setLayoutManager(layoutManager);


        if (NetworkUtils.isConnected(getActivity())) {
            getHomeMore();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }


        return rootView;
    }

    private void getHomeMore() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("id", Id);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.HOME_MORE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                showProgress(false);
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject liveTVJson = mainJson.getJSONObject(Constant.ARRAY_NAME);
                    if (liveTVJson.has("home_sections")) {
                        JSONArray homeSecArray = liveTVJson.getJSONArray("home_sections");
                        for (int i = 0; i < homeSecArray.length(); i++) {
                            JSONObject objJson = homeSecArray.getJSONObject(i);
                            JSONArray homeContentArray = objJson.getJSONArray("home_content");
                            for (int j = 0; j < homeContentArray.length(); j++) {
                                JSONObject objJsonSec = homeContentArray.getJSONObject(j);
                                ItemHomeContent itemHomeContent = new ItemHomeContent();
                                itemHomeContent.setVideoId(objJsonSec.getString("video_id"));
                                itemHomeContent.setVideoTitle(objJsonSec.getString("video_title"));
                                itemHomeContent.setVideoImage(objJsonSec.getString("video_image"));
                                itemHomeContent.setVideoType(objJsonSec.getString("video_type"));
                                itemHomeContent.setHomeType(objJsonSec.getString("video_type"));
                                itemHomeContent.setPremium(objJsonSec.getString("video_access").equals("Paid"));
                                mListItem.add(itemHomeContent);
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showProgress(false);
                lyt_not_found.setVisibility(View.VISIBLE);
            }

        });
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);

            adapter = new HomeContentAdapter(getActivity(), mListItem, true);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    ItemHomeContent itemHomeContent = mListItem.get(position);
                    Class<?> aClass;
                    switch (itemHomeContent.getVideoType()) {
                        case "Movie":
                        default:
                            aClass = MovieDetailsActivity.class;
                            break;
                        case "Shows":
                            aClass = ShowDetailsActivity.class;
                            break;
                        case "Sports":
                            aClass = SportDetailsActivity.class;
                            break;
                        case "LiveTV":
                            aClass = TVDetailsActivity.class;
                            break;
                    }
                    Intent intent = new Intent(requireActivity(), aClass);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Id", itemHomeContent.getVideoId());
                    startActivity(intent);
                }
            });
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}