package com.example.videostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.tenbis.library.consts.CardType;
import com.tenbis.library.listeners.OnCreditCardStateChanged;
import com.tenbis.library.models.CreditCard;
import com.tenbis.library.views.CompactCreditCardInput;

import org.jetbrains.annotations.NotNull;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PayStackActivity extends AppCompatActivity implements OnCreditCardStateChanged {

    String planId, planPrice, planCurrency, planGateway, planGateWayText, payStackPublicKey;
    Button btnPay;
    MyApplication myApplication;
    ProgressDialog pDialog;
    CompactCreditCardInput creditCardInput;
    CreditCard creditCard;
    boolean isCardValid = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarGradiant(this);
        super.onCreate(savedInstanceState);
        PaystackSdk.initialize(this);
        setContentView(R.layout.activity_pay_stack);
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
        payStackPublicKey = intent.getStringExtra("payStackPublicKey");

        PaystackSdk.setPublicKey(payStackPublicKey);

        btnPay = findViewById(R.id.btn_pay);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        creditCardInput = findViewById(R.id.compact_credit_card_input);
        creditCardInput.addOnCreditCardStateChangedListener(this);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isConnected(PayStackActivity.this)) {
                    if (isCardValid && creditCard != null) {
                        performCharge(myApplication.getUserEmail());
                    }
                } else {
                    Toast.makeText(PayStackActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void performCharge(String email) {
        showProgressDialog();
        Charge charge = new Charge();
        charge.setCard(loadCardFromForm());
        charge.setEmail(email);
        double amount = Double.parseDouble(planPrice);
        charge.setAmount((int) amount * 100);
        PaystackSdk.chargeCard(PayStackActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                dismissProgressDialog();
                if (NetworkUtils.isConnected(PayStackActivity.this)) {
                    new com.example.videostreamingapp.Transaction(PayStackActivity.this)
                            .purchasedItem(planId, transaction.getReference(), planGateway);
                } else {
                    showError(getString(R.string.conne_msg1));
                }
            }

            @Override
            public void beforeValidate(Transaction transaction) {

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                dismissProgressDialog();
                showError(error.getMessage());
            }
        });
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
        new AlertDialog.Builder(PayStackActivity.this)
                .setTitle(getString(R.string.pay_stack_error_1))
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

    private Card loadCardFromForm() {
        return new Card.Builder(creditCard.getCardNumber(), creditCard.getExpiryMonth(), creditCard.getExpiryYear(), creditCard.getCvv()).build();
    }

    @Override
    public void onCreditCardCvvValid(@NotNull String s) {

    }

    @Override
    public void onCreditCardExpirationDateValid(int i, int i1) {

    }

    @Override
    public void onCreditCardNumberValid(@NotNull String s) {

    }

    @Override
    public void onCreditCardTypeFound(@NotNull CardType cardType) {

    }

    @Override
    public void onCreditCardValid(@NotNull CreditCard creditCard) {
        isCardValid = true;
        this.creditCard = creditCard;
    }

    @Override
    public void onInvalidCardTyped() {
        isCardValid = false;
    }
}
