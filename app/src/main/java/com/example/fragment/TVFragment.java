package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.example.adapter.TVAdapter;
import com.example.dialog.FilterDialog;
import com.example.item.ItemTV;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.TVDetailsActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TVFragment extends Fragment implements FilterDialog.FilterDialogListener {

    private ArrayList<ItemTV> mListItem;
    private RecyclerView recyclerView;
    private TVAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    private String Id;
    private boolean isFirst = true, isOver = false;
    private int pageIndex = 1;
    private int selectedFilter = 1;
    private String mFilter = Constant.FILTER_NEWEST;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        if (getArguments() != null) {
            Id = getArguments().getString("Id");
        }
        setHasOptionsMenu(true);
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
            getSport(mFilter);
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
                            getSport(mFilter);
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

    private void getSport(String mFilter) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("category_id", Id);
        jsObj.addProperty("filter", mFilter);
        params.put("data", API.toBase64(jsObj.toString()));
        params.put("page", pageIndex);
        client.post(Constant.TV_BY_CATEGORY_URL, params, new AsyncHttpResponseHandler() {
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
                                ItemTV objItem = new ItemTV();
                                objItem.setTvId(objJson.getString(Constant.TV_ID));
                                objItem.setTvName(objJson.getString(Constant.TV_TITLE));
                                objItem.setTvImage(objJson.getString(Constant.TV_IMAGE));
                                objItem.setPremium(objJson.getString(Constant.TV_ACCESS).equals("Paid"));
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
                adapter = new TVAdapter(getActivity(), mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String sportId = mListItem.get(position).getTvId();
                    Intent intent = new Intent(getActivity(), TVDetailsActivity.class);
                    intent.putExtra("Id", sportId);
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

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_filter) {
            FilterDialog filterDialog = new FilterDialog(getActivity(), selectedFilter);
            filterDialog.setFilterDialogListener(this);
            filterDialog.show();
            filterDialog.setCancelable(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void confirm(String filterTag, int filterPosition) {
        Log.e("filterTag", filterTag);
        endlessRecyclerViewScrollListener.resetState();
        mListItem.clear();
        isFirst = true;
        isOver = false;
        mFilter = filterTag;
        selectedFilter = filterPosition;
        pageIndex = 1;

        if (NetworkUtils.isConnected(getActivity())) {
            getSport(mFilter);
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
    }
}

