package com.example.videostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.WatchListClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WatchList {

    private final ProgressDialog pDialog;
    private final Context mContext;
    private final MyApplication myApplication;

    public WatchList(Context context) {
        this.mContext = context;
        pDialog = new ProgressDialog(mContext);
        myApplication = MyApplication.getInstance();
    }

    public void applyWatch(boolean isAdd, String postId, String postType, final WatchListClickListener watchListClickListener) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("post_id", postId);
        jsObj.addProperty("post_type", postType);
        jsObj.addProperty("user_id", myApplication.getUserId());
        params.put("data", API.toBase64(jsObj.toString())); // if add then remove url otherwise vice versa
        client.post(isAdd ? Constant.REMOVE_FROM_WATCHLIST_URL : Constant.ADD_TO_WATCHLIST_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            Toast.makeText(mContext, objJson.getString(Constant.MSG), Toast.LENGTH_SHORT).show();
                            watchListClickListener.onItemClick(!isAdd, objJson.getString(Constant.MSG)); //objJson.getString(Constant.SUCCESS).equals("1")
                            Events.WatchList watchList = new Events.WatchList();
                            watchList.setWatchListId(postId);
                            GlobalBus.getBus().post(watchList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    private void showProgressDialog() {
        pDialog.setMessage(mContext.getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}
