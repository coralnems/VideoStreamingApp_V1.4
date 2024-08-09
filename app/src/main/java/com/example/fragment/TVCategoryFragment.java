package com.example.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.TVCategoryAdapter;
import com.example.item.ItemTVCategory;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
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


public class TVCategoryFragment extends Fragment {

    private ArrayList<ItemTVCategory> mListItem;
    private RecyclerView recyclerView;
    private TVCategoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView txtNoFound;
    private CardView lytRView;
    FragmentManager childFragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_language, container, false);

        mListItem = new ArrayList<>();
        progressBar = rootView.findViewById(R.id.progressBar);
        txtNoFound = rootView.findViewById(R.id.textView_mlm);
        lytRView = rootView.findViewById(R.id.lytRView);
        childFragmentManager = getChildFragmentManager();

        recyclerView = rootView.findViewById(R.id.recyclerView_mlm);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (NetworkUtils.isConnected(getActivity())) {
            getCategory();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }


        return rootView;
    }

    private void getCategory() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.TV_CATEGORY_URL, params, new AsyncHttpResponseHandler() {
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
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                txtNoFound.setVisibility(View.VISIBLE);
                            } else {
                                ItemTVCategory objItem = new ItemTVCategory();
                                objItem.setCategoryId(objJson.getString(Constant.CATEGORY_ID));
                                objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                                mListItem.add(objItem);
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
                txtNoFound.setVisibility(View.VISIBLE);
                lytRView.setVisibility(View.GONE);
            }

        });
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            txtNoFound.setVisibility(View.VISIBLE);
            lytRView.setVisibility(View.GONE);
        } else {
            txtNoFound.setVisibility(View.GONE);
            adapter = new TVCategoryAdapter(getActivity(), mListItem);
            recyclerView.setAdapter(adapter);
            listByCategory(0);
            adapter.select(0);


            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    listByCategory(position);
                    adapter.select(position);
                }
            });
        }
    }


    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            txtNoFound.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void listByCategory(int position) {
        String categoryName = mListItem.get(position).getCategoryName();
        String categoryId = mListItem.get(position).getCategoryId();
        Bundle bundle = new Bundle();
        bundle.putString("Id", categoryId);

        FragmentManager fm = childFragmentManager;
        FragmentTransaction ft = fm.beginTransaction();

        TVFragment tvFragment = new TVFragment();
        tvFragment.setArguments(bundle);
        ft.replace(R.id.framlayout_sub, tvFragment, categoryName);
        ft.addToBackStack(categoryName);
        ft.commitAllowingStateLoss();
        //    ((MainActivity) requireActivity()).setToolbarTitle(languageName);
    }
}
