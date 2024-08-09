package com.example.videostreamingapp;

import android.app.Activity;
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

import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {

    String planId, planPrice, planCurrency, planGateway, planGateWayText, razorPayKey, planName;
    Button btnPay;
    MyApplication myApplication;

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
        razorPayKey = intent.getStringExtra("razorPayKey");

        btnPay = findViewById(R.id.btn_pay);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        Checkout.preload(getApplicationContext());

        startPayment();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
            }
        });

    }

    public void startPayment() {
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setKeyID(razorPayKey);

        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.razor_pay_company));
            options.put("description", planName);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", planCurrency);
            double big = Double.valueOf(planPrice);
            int amount = (int) (big) * 100;
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", myApplication.getUserEmail());
            //     preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            showError("Error in payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String Title) {
        new AlertDialog.Builder(RazorPayActivity.this)
                .setTitle(getString(R.string.razor_payment_error_1))
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
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            if (NetworkUtils.isConnected(RazorPayActivity.this)) {
                new Transaction(RazorPayActivity.this)
                        .purchasedItem(planId, razorpayPaymentID, planGateway);
            } else {
                showError(getString(R.string.conne_msg1));
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            JSONObject jsonRes = new JSONObject(response);
            JSONObject jsonError = jsonRes.getJSONObject("error");
            showError("Payment failed: " + code + " " + jsonError.getString("description"));
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
