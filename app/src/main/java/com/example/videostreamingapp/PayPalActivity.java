package com.example.videostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PayPalActivity extends AppCompatActivity {

    String planId, planPrice, planCurrency, planGateway, planGateWayText, payPalClientId;
    Button btnPay;
    boolean isSandbox = false;
    BraintreeFragment mBraintreeFragment;
    String authToken = "";
    ProgressDialog pDialog;

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

        Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planPrice = intent.getStringExtra("planPrice");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        planGateWayText = intent.getStringExtra("planGatewayText");
        payPalClientId = intent.getStringExtra("payPalClientId");
        isSandbox = intent.getBooleanExtra("isSandbox", false);

        pDialog = new ProgressDialog(this);
        btnPay = findViewById(R.id.btn_pay);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!authToken.isEmpty()) {
                    makePaymentFromBraintree();
                } else {
                    showError(getString(R.string.paypal_payment_error_3));
                }
            }
        });

        generateToken();
    }

    private void generateToken() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.BRAIN_TREE_TOKEN_URL, params, new AsyncHttpResponseHandler() {

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
                    JSONObject objJson = jsonArray.getJSONObject(0);
                    if (objJson.getString("success").equals("1")) {
                        authToken = objJson.getString("client_token");
                        initBraintree(authToken);
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

    private void initBraintree(String authToken) {
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, authToken);
            mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                    String nNonce = paymentMethodNonce.getNonce();
                    checkoutNonce(nNonce);
                    //   Log.e("ok Nonce=>", "" + nNonce);

                }
            });
            mBraintreeFragment.addListener(new BraintreeCancelListener() {
                @Override
                public void onCancel(int requestCode) {
                    showError(getString(R.string.paypal_payment_error_2));
                }
            });

            mBraintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {
                    showError(error.getMessage());
                }
            });
            Toast.makeText(PayPalActivity.this, getString(R.string.proceed_with_payment), Toast.LENGTH_SHORT).show();
        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
            showError(getString(R.string.paypal_payment_error_1));
        }
    }

    private void makePaymentFromBraintree() {
        PayPal.requestOneTimePayment(mBraintreeFragment, getPaypalRequest(planPrice));
    }

    private PayPalRequest getPaypalRequest(@Nullable String amount) {
        PayPalRequest request = new PayPalRequest(amount);
        request.currencyCode(planCurrency);
        request.intent(PayPalRequest.INTENT_SALE);
        return request;
    }

    private void checkoutNonce(String paymentNonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("payment_nonce", paymentNonce);
        jsObj.addProperty("payment_amount", planPrice);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.BRAIN_TREE_CHECK_OUT_URL, params, new AsyncHttpResponseHandler() {

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
                    JSONObject objJson = jsonArray.getJSONObject(0);
                    if (objJson.getString("success").equals("1")) {
                        String paymentId = objJson.getString("paypal_payment_id"); //objJson.getString("transaction_id")
                        if (NetworkUtils.isConnected(PayPalActivity.this)) {
                            new Transaction(PayPalActivity.this)
                                    .purchasedItem(planId, paymentId, planGateway);
                        } else {
                            showError(getString(R.string.conne_msg1));
                        }
                    } else {
                        showError(objJson.getString("msg"));
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

    public void showProgressDialog() {
        pDialog.setMessage(PayPalActivity.this.getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private void showError(String Title) {
        new AlertDialog.Builder(PayPalActivity.this)
                .setTitle(getString(R.string.paypal_payment_error_4))
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
