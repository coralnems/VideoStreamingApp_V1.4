package com.example.videostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.StatusBarUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PayTMActivity extends AppCompatActivity {

    String planId, planPrice, planCurrency, planGateway, planGateWayText,
            planName, paytmMid;
    Button btnPay;
    MyApplication myApplication;
    boolean isSandbox = false;
    ProgressDialog pDialog;
    String orderId = "", txnId = "";
    String callBackUrl, paymentUrl;
    Integer ActivityRequestCode = 2;

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
        paytmMid = intent.getStringExtra("paytmMid");
        isSandbox = intent.getBooleanExtra("isSandbox", false);

        btnPay = findViewById(R.id.btn_pay);
        pDialog = new ProgressDialog(this);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        if (isSandbox) {
            callBackUrl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
            paymentUrl = "https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage";
        } else {
            callBackUrl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";
            paymentUrl = "https://securegw.paytm.in/theia/api/v1/showPaymentPage";
        }

        initTransaction();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initTransaction();
            }
        });
    }

    public void startPayment() {
        PaytmOrder paytmOrder = new PaytmOrder(orderId, paytmMid, txnId, planPrice, callBackUrl + orderId);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle inResponse) {
                if (inResponse != null) {
                    fetchSuccessTransaction(inResponse.getString("STATUS"),
                            inResponse.getString("TXNID"),
                            inResponse.getString("RESPMSG"));
                }
            }

            @Override
            public void networkNotAvailable() {
                showError(getString(R.string.conne_msg1));
            }

            @Override
            public void onErrorProceed(String s) {
                showError(s);
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                showError(s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                showError(s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                showError(s);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                showError(getString(R.string.payment_cancel));
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                showError(s);
            }
        });

        transactionManager.setShowPaymentUrl(paymentUrl);
        transactionManager.startTransaction(this, ActivityRequestCode);

    }

    private void fetchSuccessTransaction(String status, String txnId, String message) {
        if (status.equals("TXN_SUCCESS")) {
            new Transaction(PayTMActivity.this)
                    .purchasedItem(planId, txnId, planGateway);
        } else {
            showError(message);
        }
    }

    private void initTransaction() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("user_id", myApplication.getUserId());
        params.put("amount", planPrice);
        client.post(Constant.PAYTM_TXN_URL, params, new AsyncHttpResponseHandler() {
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
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        if (Constant.GET_SUCCESS_MSG == 0) {
                            showError(getString(R.string.payment_token_error));
                        } else {
                            orderId = objJson.getString("order_id");
                            txnId = objJson.getString("txn_token");
                            startPayment();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                dismissProgressDialog();
                showError(getString(R.string.payment_token_error));
            }
        });
    }

    private void showError(String Title) {
        new AlertDialog.Builder(PayTMActivity.this)
                .setTitle(getString(R.string.paytm))
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            String inResponse = data.getStringExtra("response");
            if (inResponse != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(inResponse);
                    fetchSuccessTransaction(jsonResponse.getString("STATUS"),
                            jsonResponse.getString("TXNID"),
                            jsonResponse.getString("RESPMSG"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
