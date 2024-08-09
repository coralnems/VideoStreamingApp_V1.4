package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.adapter.ShowAdapter;
import com.example.item.ItemShow;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.ShowDetailsActivity;
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

public class ShowFragment extends Fragment {

    private ArrayList<ItemShow> mListItem;
    private RecyclerView recyclerView;
    private ShowAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    private String Id;
    private boolean isFirst = true, isOver = false;
    private boolean isLanguage = true;
    private int pageIndex = 1;
    private String mFilter = Constant.FILTER_NEWEST;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        if (getArguments() != null) {
            Id = getArguments().getString("Id");
            isLanguage = getArguments().getBoolean("isLanguage", true);
        }
        mListItem = new ArrayList<>();
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case 0:
                        return 2;
                    default:
                        return 1;
                }
            }
        });

        if (NetworkUtils.isConnected(getActivity())) {
            getShow(mFilter);
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }


        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pageIndex++;
                            getShow(mFilter);
                        }
                    }, 1000);
                } else {
                    adapter.hideHeader();
                }
            }
        };

        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);

        return rootView;
    }

    private void getShow(String mFilter) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty(isLanguage ? "lang_id" : "genre_id", Id);
        jsObj.addProperty("filter", mFilter);
        params.put("data", API.toBase64(jsObj.toString()));
        params.put("page", pageIndex);
        client.post(isLanguage ? Constant.SHOW_BY_LANGUAGE_URL : Constant.SHOW_BY_GENRE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isFirst)
                    showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (isFirst)
                    showProgress(false);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                ItemShow objItem = new ItemShow();
                                objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                                objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                                objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                                objItem.setPremium(objJson.getString(Constant.SHOW_ACCESS).equals("Paid"));
                                mListItem.add(objItem);
                            }
                        }
                    } else {
                        isOver = true;
                        if (adapter != null) { // when there is no data in first time
                            adapter.hideHeader();
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
            if (isFirst) {
                isFirst = false;
                adapter = new ShowAdapter(getActivity(), mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String showId = mListItem.get(position).getShowId();
                    Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                    intent.putExtra("Id", showId);
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

    public void selectFilter(String filterTag) {
        endlessRecyclerViewScrollListener.resetState();
        mListItem.clear();
        isFirst = true;
        isOver = false;
        mFilter = filterTag;
        pageIndex = 1;

        if (NetworkUtils.isConnected(getActivity())) {
            getShow(mFilter);
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
    }
}

