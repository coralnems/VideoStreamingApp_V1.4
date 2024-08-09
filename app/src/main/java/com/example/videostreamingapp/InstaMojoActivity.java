package com.example.videostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.instamojo.android.Instamojo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class InstaMojoActivity extends AppCompatActivity implements Instamojo.InstamojoPaymentCallback {

    String planId, planPrice, planCurrency, planGateway, planGateWayText, planName;
    Button btnPay;
    MyApplication myApplication;
    boolean isSandbox = false;
    ProgressDialog pDialog;
    String orderId = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarGradiant(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.payment));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        myApplication = MyApplication.getInstance();

        Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planName = intent.getStringExtra("planName");
        planPrice = intent.getStringExtra("planPrice");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        planGateWayText = intent.getStringExtra("planGatewayText");
        isSandbox = intent.getBooleanExtra("isSandbox", false);

        btnPay = findViewById(R.id.btn_pay);
        pDialog = new ProgressDialog(this);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        Instamojo.getInstance().initialize(this, isSandbox ? Instamojo.Environment.TEST : Instamojo.Environment.PRODUCTION);

        if (myApplication.getUserPhone().isEmpty()) {
            showErrorPhone();
        } else {
            getOrderId();
        }

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getUserPhone().isEmpty()) {
                    showErrorPhone();
                } else {
                    if (orderId.isEmpty()) {
                        getOrderId();
                    } else {
                        startPayment();
                    }
                }
            }
        });
    }

    public void startPayment() {
        Instamojo.getInstance().initiatePayment(this, orderId, this);
    }

    private void getOrderId() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("user_id", myApplication.getUserId());
        params.put("plan_id", planId);
        params.put("amount", planPrice);
        params.put("purpose", planName);
        client.post(Constant.INSTA_MOJO_ORDER_URL, params, new AsyncHttpResponseHandler() {
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
                        orderId = objJson.getString("order_id");
                        Log.e("orderId", "" + orderId);
                        startPayment();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                dismissProgressDialog();
            }
        });
    }

    private void showError(String Title) {
        new AlertDialog.Builder(InstaMojoActivity.this)
                .setTitle(getString(R.string.insta_mojo))
                .setMessage(Title)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        if (NetworkUtils.isConnected(InstaMojoActivity.this)) {
            new Transaction(InstaMojoActivity.this)
                    .purchasedItem(planId, paymentID, planGateway);
        } else {
            showError(getString(R.string.conne_msg1));
        }
    }

    @Override
    public void onPaymentCancelled() {
        showError(getString(R.string.payment_cancel));
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        showError(errorMessage);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showErrorPhone() {
        new AlertDialog.Builder(InstaMojoActivity.this)
                .setTitle(getString(R.string.pay_u))
                .setMessage(getString(R.string.payment_need_phone))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(InstaMojoActivity.this, EditProfileActivity.class);
                        intent.putExtra("isFromPayU", true);
                        startActivityForResult(intent, 1118);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1118 && resultCode == 1187) {
            if (myApplication.getUserPhone().isEmpty()) {
                showErrorPhone();
            } else {
                getOrderId();
            }
        }
    }
}
