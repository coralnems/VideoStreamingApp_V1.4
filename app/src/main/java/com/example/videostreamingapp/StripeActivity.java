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

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.PaymentSheetResultCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class StripeActivity extends AppCompatActivity {

    String planId, planPrice, planCurrency, planGateway, planGateWayText, stripePublisherKey;
    Button btnPay;
    MyApplication myApplication;
    ProgressDialog pDialog;
    private String paymentIntentClientSecret = "";
    private PaymentSheet paymentSheet;
    private String customerId;
    private String ephemeralKeySecret;
    private String paymentIntentId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarGradiant(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.payment));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        pDialog = new ProgressDialog(this);
        myApplication = MyApplication.getInstance();

        Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planPrice = intent.getStringExtra("planPrice");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        planGateWayText = intent.getStringExtra("planGatewayText");
        stripePublisherKey = intent.getStringExtra("stripePublisherKey");

        PaymentConfiguration.init(this, stripePublisherKey);
        paymentSheet = new PaymentSheet((ComponentActivity) StripeActivity.this, new PaymentSheetResultCallback() {
            @Override
            public void onPaymentSheetResult(@NotNull PaymentSheetResult paymentSheetResult) {
                onPaymentSheetResult1(paymentSheetResult);
            }
        });

        btnPay = findViewById(R.id.btn_pay);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isConnected(StripeActivity.this)) {
                    getToken();
                } else {
                    Toast.makeText(StripeActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                new PaymentSheet.Configuration(
                        getString(R.string.stripe_company),
                        new PaymentSheet.CustomerConfiguration(
                                customerId,
                                ephemeralKeySecret
                        )
                )
        );

    }

    private void onPaymentSheetResult1(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            showError(getString(R.string.paypal_payment_error_2));
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
//            Log.e("App", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            showError(getString(R.string.paypal_payment_error_1));
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            if (NetworkUtils.isConnected(StripeActivity.this)) {
                new Transaction(StripeActivity.this)
                        .purchasedItem(planId, paymentIntentId, planGateway);
            } else {
                showError(StripeActivity.this.getString(R.string.conne_msg1));
            }
        }
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showError(String Title) {
        new AlertDialog.Builder(StripeActivity.this)
                .setTitle(getString(R.string.stripe_payment_error_1))
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

    private void getToken() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("amount", planPrice);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.STRIPE_TOKEN_URL, params, new AsyncHttpResponseHandler() {
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
                        paymentIntentClientSecret = objJson.getString("stripe_payment_token");
                        ephemeralKeySecret = objJson.getString("ephemeralKey");
                        customerId = objJson.getString("customer");
                        paymentIntentId = objJson.getString("id");
                        if (paymentIntentClientSecret.isEmpty() && ephemeralKeySecret.isEmpty() && customerId.isEmpty() && paymentIntentId.isEmpty()) {
                            showError(getString(R.string.stripe_token_error));
                        } else {
                            presentPaymentSheet();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showError(getString(R.string.stripe_token_error));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                dismissProgressDialog();
                showError(getString(R.string.stripe_token_error));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
