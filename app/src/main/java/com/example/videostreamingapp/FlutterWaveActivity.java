package com.example.videostreamingapp;

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

import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class FlutterWaveActivity extends AppCompatActivity {

    String planId, planPrice, planCurrency, planGateway, planGateWayText, fwPublicKey, fwEncryptionKey, planName;
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
        fwPublicKey = intent.getStringExtra("fwPublicKey");
        fwEncryptionKey = intent.getStringExtra("fwEncryptionKey");

        btnPay = findViewById(R.id.btn_pay);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        startPayment();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
            }
        });

    }

    public void startPayment() {
        RaveUiManager raveUiManager = new RaveUiManager(FlutterWaveActivity.this);
        raveUiManager.setAmount(Double.parseDouble(planPrice))
                //.setCountry(getFlutterWaveCountry())
                .setCurrency(planCurrency) //NGN
                .setEmail(myApplication.getUserEmail())
                .setfName(myApplication.getUserName())
                .setPublicKey(fwPublicKey)
                .setEncryptionKey(fwEncryptionKey)
                .setTxRef(getTransactionId())
              //  .setPhoneNumber("+2348090717512", true) //myApplication.getUserPhone()
                .acceptAccountPayments(true)
                .shouldDisplayFee(true)
                .acceptCardPayments(true)
                .acceptMpesaPayments(true)
                .acceptAchPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .acceptUgMobileMoneyPayments(true)
                .acceptZmMobileMoneyPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptSaBankPayments(true)
                .acceptUkPayments(true)
                .acceptBankTransferPayments(true)
                .acceptUssdPayments(true)
                .acceptBarterPayments(true)
                .acceptFrancMobileMoneyPayments(true, "NG")
                .allowSaveCardFeature(true)
                .onStagingEnv(false)
                .isPreAuth(true)
                .showStagingLabel(true);
        raveUiManager.initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                try {
                    assert message != null;
                    JSONObject jsonObject = new JSONObject(message);
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    String status = jsonData.getString("status");
                    if (status.equals("successful")) {
                        if (NetworkUtils.isConnected(FlutterWaveActivity.this)) {
                            new Transaction(FlutterWaveActivity.this)
                                    .purchasedItem(planId, jsonData.getString("flwRef"), planGateway);
                        }
                    } else {
                        showError("Failed " + jsonData.getString("vbvrespmessage"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                showError(getString(R.string.paypal_payment_error_1));
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                showError(getString(R.string.payment_cancel));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showError(String Title) {
        new AlertDialog.Builder(FlutterWaveActivity.this)
                .setTitle(getString(R.string.flutter_wave))
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

    public String getTransactionId() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        String orderId = String.format(Locale.getDefault(), "%06d", number);
        return "fW" + myApplication.getUserId() + orderId;
    }
}
