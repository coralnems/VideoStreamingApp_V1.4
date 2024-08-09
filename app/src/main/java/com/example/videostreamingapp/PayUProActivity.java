package com.example.videostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PayUProActivity extends AppCompatActivity {

    String planId, planPrice, planCurrency, planGateway, planGateWayText, payUMoneyMerchantId, payUMoneyMerchantKey, planName;
    Button btnPay;
    MyApplication myApplication;
    boolean isSandbox = false;
    public static final String SURL = BuildConfig.SERVER_URL + "app_payu_success";
    public static final String FURL = BuildConfig.SERVER_URL + "app_payu_failed";
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

        myApplication = MyApplication.getInstance();

        Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planName = intent.getStringExtra("planName");
        planPrice = intent.getStringExtra("planPrice");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        planGateWayText = intent.getStringExtra("planGatewayText");
        payUMoneyMerchantId = intent.getStringExtra("payUMoneyMerchantId");
        payUMoneyMerchantKey = intent.getStringExtra("payUMoneyMerchantKey");
        isSandbox = intent.getBooleanExtra("isSandbox", false);

        btnPay = findViewById(R.id.btn_pay);
        pDialog = new ProgressDialog(this);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        if (myApplication.getUserPhone().isEmpty()) {
            showErrorPhone();
        } else {
            startPayment();
        }

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getUserPhone().isEmpty()) {
                    showErrorPhone();
                } else {
                    startPayment();
                }

            }
        });

    }

    public void startPayment() {
        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(planPrice)
                .setIsProduction(!isSandbox)
                .setProductInfo(planName)
                .setKey(payUMoneyMerchantKey)
                .setTransactionId(System.currentTimeMillis() + "")
                .setFirstName(myApplication.getUserName())
                .setEmail(myApplication.getUserEmail())
                .setPhone(myApplication.getUserPhone())
                .setUserCredential(myApplication.getUserEmail())
                .setSurl(SURL)
                .setFurl(FURL);
        PayUPaymentParams payUPaymentParams = builder.build();
        PayUCheckoutProConfig payUCheckoutProConfig = new PayUCheckoutProConfig();
        payUCheckoutProConfig.setMerchantName(getString(R.string.app_name));
        payUCheckoutProConfig.setMerchantLogo(R.mipmap.ic_launcher);

        PayUCheckoutPro.open(PayUProActivity.this, payUPaymentParams, payUCheckoutProConfig, new PayUCheckoutProListener() {
            @Override
            public void onPaymentSuccess(@NonNull Object response) {
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                //  String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                try {
                    assert payuResponse != null;
                    JSONObject mainJson = new JSONObject(payuResponse);
                    String paymentId = mainJson.getString("txnid");
                    if (NetworkUtils.isConnected(PayUProActivity.this)) {
                        new Transaction(PayUProActivity.this)
                                .purchasedItem(planId, paymentId, planGateway);
                    } else {
                        showError(getString(R.string.conne_msg1));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPaymentFailure(@NonNull Object response) {
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                //    String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                try {
                    assert payuResponse != null;
                    JSONObject mainJson = new JSONObject(payuResponse);
                    String errorMessage = mainJson.getString("Error_Message");
                    showError(errorMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPaymentCancel(boolean b) {

            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                String errorMessage = errorResponse.getErrorMessage();
                Log.e("onError", "Yes");
                showError(errorMessage);
            }

            @Override
            public void generateHash(@NonNull HashMap<String, String> hashMap, @NonNull PayUHashGenerationListener payUHashGenerationListener) {
                String hashName = hashMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                String hashData = hashMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                    //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                    getHash(hashName, hashData, payUHashGenerationListener);
                }
            }

            @Override
            public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {

            }
        });
    }

    private void showError(String Title) {
        new AlertDialog.Builder(PayUProActivity.this)
                .setTitle(getString(R.string.pay_u))
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

    private void showErrorPhone() {
        new AlertDialog.Builder(PayUProActivity.this)
                .setTitle(getString(R.string.pay_u))
                .setMessage(getString(R.string.payment_need_phone))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PayUProActivity.this, EditProfileActivity.class);
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
                startPayment();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private void getHash(String hashName, String hashData, PayUHashGenerationListener payUHashGenerationListener) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("hashdata", hashData);
        client.post(Constant.PRO_PAY_U_HASH_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                String merchantHash = "";
                try {
                    JSONObject mainJson = new JSONObject(result);
                    merchantHash = mainJson.getString("result");
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put(hashName, merchantHash);
                    payUHashGenerationListener.onHashGenerated(dataMap);
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
}
