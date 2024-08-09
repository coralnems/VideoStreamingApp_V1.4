package com.example.videostreamingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.PlanAdapter;
import com.example.item.ItemPlan;
import com.example.util.API;
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

public class PlanActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    RecyclerView rvPlan;
    NestedScrollView nestedScrollView;
    LinearLayout lytProceed;
    ArrayList<ItemPlan> mListItem;
    PlanAdapter adapter;
    int selectedPlan = 0;
    ImageView imageClose;
    TextView tvCouponCode;
    MyApplication myApplication;
    ProgressDialog pDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarBlack(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        IsRTL.ifSupported(this);

        myApplication = MyApplication.getInstance();
        mListItem = new ArrayList<>();
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        lytProceed = findViewById(R.id.lytProceed);
        imageClose = findViewById(R.id.imageClose);
        tvCouponCode = findViewById(R.id.tvCouponCode);
        pDialog = new ProgressDialog(this);

        rvPlan = findViewById(R.id.rv_plan);
        rvPlan.setHasFixedSize(true);
        rvPlan.setLayoutManager(new LinearLayoutManager(PlanActivity.this, LinearLayoutManager.VERTICAL, false));
        rvPlan.setFocusable(false);
        rvPlan.setNestedScrollingEnabled(false);

        if (NetworkUtils.isConnected(PlanActivity.this)) {
            getPlan();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvCouponCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponCodeDialog();
            }
        });

    }

    private void getPlan() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.PLAN_LIST_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
                lytProceed.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
                lytProceed.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);

                            ItemPlan objItem = new ItemPlan();
                            objItem.setPlanId(objJson.getString(Constant.PLAN_ID));
                            objItem.setPlanName(objJson.getString(Constant.PLAN_NAME));
                            objItem.setPlanPrice(objJson.getString(Constant.PLAN_PRICE));
                            objItem.setPlanDuration(objJson.getString(Constant.PLAN_DURATION));
                            objItem.setPlanCurrencyCode(objJson.getString(Constant.CURRENCY_CODE));
                            mListItem.add(objItem);

                        }
                        displayData();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.GONE);
                        lytProceed.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                lytProceed.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        adapter = new PlanAdapter(PlanActivity.this, mListItem);
        rvPlan.setAdapter(adapter);
        adapter.select(0);

        adapter.setOnItemClickListener(new RvOnClickListener() {
            @Override
            public void onItemClick(int position) {
                selectedPlan = position;
                adapter.select(position);
            }
        });

        lytProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemPlan itemPlan = mListItem.get(selectedPlan);
                String isFreePlan = itemPlan.getPlanPrice();
                if (isFreePlan.equals("0.00")) {
                    if (NetworkUtils.isConnected(PlanActivity.this)) {
                        new Transaction(PlanActivity.this).purchasedItem(itemPlan.getPlanId(), "-", "N/A");
                    } else {
                        Toast.makeText(PlanActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(PlanActivity.this, SelectPlanActivity.class);
                    intent.putExtra("planId", itemPlan.getPlanId());
                    intent.putExtra("planName", itemPlan.getPlanName());
                    intent.putExtra("planPrice", itemPlan.getPlanPrice());
                    intent.putExtra("planDuration", itemPlan.getPlanDuration());
                    startActivity(intent);
                }
            }
        });
    }

    private void couponCodeDialog() {
        final Dialog mDialog = new Dialog(PlanActivity.this, R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.dialog_coupon);
        Button buttonCancel = mDialog.findViewById(R.id.btn_cancel);
        Button btnSubmit = mDialog.findViewById(R.id.btn_submit);
        EditText edtCouponCode = mDialog.findViewById(R.id.edt_refCode);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String couponCode = edtCouponCode.getText().toString();
                if (!couponCode.isEmpty()) {
                    if (NetworkUtils.isConnected(PlanActivity.this)) {
                        addCouponCode(couponCode);
                    } else {
                        showToast(getString(R.string.conne_msg1));
                    }
                    mDialog.dismiss();
                }

            }
        });
        mDialog.show();
    }

    private void addCouponCode(String couponCode) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("coupon_code", couponCode);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.APPLY_COUPON_URL, params, new AsyncHttpResponseHandler() {

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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        showToast(objJson.getString(Constant.MSG));
                        if (Constant.GET_SUCCESS_MSG == 1) {
                            ActivityCompat.finishAffinity(PlanActivity.this);

                            Intent intentDashboard = new Intent(PlanActivity.this, DashboardActivity.class);
                            intentDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentDashboard.putExtra("isPurchased", true);
                            startActivity(intentDashboard);
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
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showToast(String msg) {
        Toast.makeText(PlanActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
