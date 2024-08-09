package com.example.videostreamingapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.util.API;
import com.example.util.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Transaction {
    private ProgressDialog pDialog;
    private Activity mContext;
    MyApplication myApplication;

    public Transaction(Activity context) {
        this.mContext = context;
        pDialog = new ProgressDialog(mContext);
        myApplication = MyApplication.getInstance();
    }

    public void purchasedItem(String planId, String paymentId, String paymentGateway) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("plan_id", planId);
        jsObj.addProperty("payment_id", paymentId);
        jsObj.addProperty("payment_gateway", paymentGateway);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.TRANSACTION_URL, params, new AsyncHttpResponseHandler() {
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
                        objJson = jsonArray.getJSONObject(0);
                        Toast.makeText(mContext, objJson.getString(Constant.MSG), Toast.LENGTH_SHORT).show();
                        showPurchaseSuccess();
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

    private void showPurchaseSuccess() {
        Dialog mDialog = new Dialog(mContext, R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.dailog_purchased);
        Button btnDashboard = mDialog.findViewById(R.id.btnDashboard);
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                ActivityCompat.finishAffinity(mContext);

                Intent intentDashboard = new Intent(mContext, DashboardActivity.class);
                intentDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentDashboard.putExtra("isPurchased", true);
                mContext.startActivity(intentDashboard);
            }
        });
        mDialog.show();
    }
}
