package com.example.videostreamingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.item.ItemPaymentSetting;
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
import com.tuyenmonkey.textdecorator.TextDecorator;
import com.tuyenmonkey.textdecorator.callback.OnTextClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SelectPlanActivity extends AppCompatActivity {

    String planId, planName, planPrice, planDuration;
    TextView textPlanName, textPlanPrice, textPlanDuration, textChangePlan, textPlanCurrency, textNoPaymentGateway, tvPlanDesc, tvCurrentPlan;
    LinearLayout lytProceed;
    RadioButton radioPayPal, radioStripe, radioRazorPay, radioPayStack, radioInstaMojo, radioPayU, radioPayTM, radioCashFree, radioFlutterWave;
    MyApplication myApplication;
    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    RelativeLayout lytDetails;
    ItemPaymentSetting paymentSetting;
    RadioGroup radioGroup;
    ImageView imageClose;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarBlack(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_plan);
        IsRTL.ifSupported(this);

        myApplication = MyApplication.getInstance();
        paymentSetting = new ItemPaymentSetting();

        final Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planName = intent.getStringExtra("planName");
        planPrice = intent.getStringExtra("planPrice");
        planDuration = intent.getStringExtra("planDuration");

        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        lytDetails = findViewById(R.id.lytDetails);
        textPlanName = findViewById(R.id.textPackName);
        textPlanPrice = findViewById(R.id.textPrice);
        textPlanCurrency = findViewById(R.id.textCurrency);
        textPlanDuration = findViewById(R.id.textDay);
        tvPlanDesc = findViewById(R.id.tvPlanDesc);
        textChangePlan = findViewById(R.id.changePlan);
        tvCurrentPlan = findViewById(R.id.textCurrentPlan);
        lytProceed = findViewById(R.id.lytProceed);
        radioPayPal = findViewById(R.id.rdPaypal);
        radioStripe = findViewById(R.id.rdStripe);
        radioRazorPay = findViewById(R.id.rdRazorPay);
        radioPayStack = findViewById(R.id.rdPayStack);
        radioInstaMojo = findViewById(R.id.rdInstaMojo);
        radioPayTM = findViewById(R.id.rdPayTM);
        radioPayU = findViewById(R.id.rdPayUMoney);
        radioCashFree = findViewById(R.id.rdCashFree);
        radioFlutterWave = findViewById(R.id.rdFlutterWave);
        textNoPaymentGateway = findViewById(R.id.textNoPaymentGateway);
        radioGroup = findViewById(R.id.radioGrp);
        imageClose = findViewById(R.id.imageClose);

        textPlanName.setText(planName);
        tvCurrentPlan.setText(planName);
        textPlanPrice.setText(planPrice);
        textPlanDuration.setText(getString(R.string.plan_day_for, planDuration));

        textChangePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lytProceed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View view) {
                int radioSelected = radioGroup.getCheckedRadioButtonId();
                if (radioSelected != -1) {
                    switch (radioSelected) {
                        case R.id.rdPaypal:
                            goPayPal();
                            break;
                        case R.id.rdStripe:
                            goStripe();
                            break;
                        case R.id.rdRazorPay:
                            goRazorPay();
                            break;
                        case R.id.rdPayStack:
                            goPayStack();
                            break;
                        case R.id.rdInstaMojo:
                            goInstaMojo();
                            break;
                        case R.id.rdPayUMoney:
                            goPayUMoney();
                            break;
                        case R.id.rdPayTM:
                            goPayTm();
                            break;
                        case R.id.rdCashFree:
                            goCashFree();
                            break;
                        case R.id.rdFlutterWave:
                            goFlutterWave();
                            break;
                    }
                } else {
                    Toast.makeText(SelectPlanActivity.this, getString(R.string.select_gateway), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (NetworkUtils.isConnected(SelectPlanActivity.this)) {
            getPaymentSetting();
        } else {
            Toast.makeText(SelectPlanActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buildPlanDesc();
    }

    private void getPaymentSetting() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.PAYMENT_SETTING_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytDetails.setVisibility(View.GONE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytDetails.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    paymentSetting.setCurrencyCode(mainJson.getString(Constant.CURRENCY_CODE));

                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objJson = jsonArray.getJSONObject(i);
                            String gatewayName = objJson.getString("gateway_name");
                            String gatewayId = objJson.getString("gateway_id");
                            boolean status = objJson.getBoolean("status");
                            JSONObject gatewayInfoJson = objJson.getJSONObject("gateway_info");
                            switch (gatewayId) {
                                case "1":
                                    radioPayPal.setText(gatewayName);
                                    paymentSetting.setPayPal(status);
                                    paymentSetting.setPayPalSandbox(gatewayInfoJson.getString(Constant.PAYMENT_MODE).equals("sandbox"));
                                    paymentSetting.setPayPalClientId(gatewayInfoJson.getString(Constant.PAY_PAL_CLIENT));
                                    break;
                                case "2":
                                    radioStripe.setText(gatewayName);
                                    paymentSetting.setStripe(status);
                                    paymentSetting.setStripePublisherKey(gatewayInfoJson.getString(Constant.STRIPE_PUBLISHER));
                                    break;
                                case "3":
                                    radioRazorPay.setText(gatewayName);
                                    paymentSetting.setRazorPay(status);
                                    paymentSetting.setRazorPayKey(gatewayInfoJson.getString(Constant.RAZOR_PAY_KEY));
                                    break;
                                case "4":
                                    radioPayStack.setText(gatewayName);
                                    paymentSetting.setPayStack(status);
                                    paymentSetting.setPayStackPublicKey(gatewayInfoJson.getString(Constant.PAY_STACK_KEY));
                                    break;
                                case "5":
                                    radioInstaMojo.setText(gatewayName);
                                    paymentSetting.setInstaMojo(status);
                                    paymentSetting.setInstaMojoSandbox(gatewayInfoJson.getString(Constant.PAYMENT_MODE).equals("sandbox"));
                                    break;
                                case "6":
                                    radioPayU.setText(gatewayName);
                                    paymentSetting.setPayUMoney(status);
                                    paymentSetting.setPayUMoneySandbox(gatewayInfoJson.getString(Constant.PAYMENT_MODE).equals("sandbox"));
                                    paymentSetting.setPayUMoneyMerchantId(gatewayInfoJson.getString(Constant.PAY_U_MERCHANT_ID));
                                    paymentSetting.setPayUMoneyMerchantKey(gatewayInfoJson.getString(Constant.PAY_U_MERCHANT_KEY));
                                    break;
                                case "8":
                                    radioFlutterWave.setText(gatewayName);
                                    paymentSetting.setFlutterWave(status);
                                    paymentSetting.setFwPublicKey(gatewayInfoJson.getString(Constant.FW_PUBLIC_KEY));
                                    paymentSetting.setFwEncryptionKey(gatewayInfoJson.getString(Constant.FW_ENCRYPTION_KEY));
                                    break;
                                case "9":
                                    radioPayTM.setText(gatewayName);
                                    paymentSetting.setPayTM(status);
                                    paymentSetting.setPayTMSandbox(gatewayInfoJson.getString(Constant.PAYMENT_MODE).equals("sandbox"));
                                    paymentSetting.setPayTMMid(gatewayInfoJson.getString(Constant.PAYTM_MID));
                                    break;
                                case "10":
                                    radioCashFree.setText(gatewayName);
                                    paymentSetting.setCashFree(status);
                                    paymentSetting.setCashFreeSandbox(gatewayInfoJson.getString(Constant.PAYMENT_MODE).equals("sandbox"));
                                    paymentSetting.setCashFreeAppId(gatewayInfoJson.getString(Constant.CASHFREE_APPID));
                                    break;
                            }
                        }
                        displayData();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytDetails.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);
                    lytDetails.setVisibility(View.GONE);
                    lyt_not_found.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                mProgressBar.setVisibility(View.GONE);
                lytDetails.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        textPlanCurrency.setText(paymentSetting.getCurrencyCode());

        radioPayPal.setVisibility(paymentSetting.isPayPal() ? View.VISIBLE : View.GONE);
        radioStripe.setVisibility(paymentSetting.isStripe() ? View.VISIBLE : View.GONE);
        radioRazorPay.setVisibility(paymentSetting.isRazorPay() ? View.VISIBLE : View.GONE);
        radioPayStack.setVisibility(paymentSetting.isPayStack() ? View.VISIBLE : View.GONE);
        radioInstaMojo.setVisibility(paymentSetting.isInstaMojo() ? View.VISIBLE : View.GONE);
        radioPayU.setVisibility(paymentSetting.isPayUMoney() ? View.VISIBLE : View.GONE);
        radioPayTM.setVisibility(paymentSetting.isPayTM() ? View.VISIBLE : View.GONE);
        radioCashFree.setVisibility(paymentSetting.isCashFree() ? View.VISIBLE : View.GONE);
        radioFlutterWave.setVisibility(paymentSetting.isFlutterWave() ? View.VISIBLE : View.GONE);

        if (!paymentSetting.isPayPal() && !paymentSetting.isStripe()
                && !paymentSetting.isRazorPay() && !paymentSetting.isPayStack()
                && !paymentSetting.isInstaMojo() && !paymentSetting.isPayUMoney()
                && !paymentSetting.isPayTM() && !paymentSetting.isCashFree() && !paymentSetting.isFlutterWave()) {
            textNoPaymentGateway.setVisibility(View.VISIBLE);
            lytProceed.setVisibility(View.GONE);
        }
    }

    private void buildPlanDesc() {
        TextDecorator
                .decorate(tvPlanDesc, getString(R.string.choose_plan, myApplication.getUserEmail()))
                .setTextColor(R.color.highlight, planName, myApplication.getUserEmail(), getString(R.string.menu_logout))
                .makeTextClickable(new OnTextClickListener() {
                    @Override
                    public void onClick(View view, String text) {
                        logOut();
                    }
                }, false, getString(R.string.menu_logout))
                .setTextColor(R.color.highlight, getString(R.string.menu_logout))
                .build();
    }

    private void logOut() {
        new AlertDialog.Builder(SelectPlanActivity.this)
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myApplication.saveIsLogin(false);
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.putExtra("isLogout", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_logout)
                .show();
    }

    private void goPayPal() {
        Intent intentPayPal = new Intent(SelectPlanActivity.this, PayPalActivity.class);
        intentPayPal.putExtra("planId", planId);
        intentPayPal.putExtra("planPrice", planPrice);
        intentPayPal.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentPayPal.putExtra("planGateway", "Paypal");
        intentPayPal.putExtra("planGatewayText", getString(R.string.paypal));
        intentPayPal.putExtra("isSandbox", paymentSetting.isPayPalSandbox());
        intentPayPal.putExtra("payPalClientId", paymentSetting.getPayPalClientId());
        startActivity(intentPayPal);
    }

    private void goStripe() {
        Intent intentStripe = new Intent(SelectPlanActivity.this, StripeActivity.class);
        intentStripe.putExtra("planId", planId);
        intentStripe.putExtra("planPrice", planPrice);
        intentStripe.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentStripe.putExtra("planGateway", "Stripe");
        intentStripe.putExtra("planGatewayText", getString(R.string.stripe));
        intentStripe.putExtra("stripePublisherKey", paymentSetting.getStripePublisherKey());
        startActivity(intentStripe);
    }

    private void goRazorPay() {
        Intent intentRazor = new Intent(SelectPlanActivity.this, RazorPayActivity.class);
        intentRazor.putExtra("planId", planId);
        intentRazor.putExtra("planName", planName);
        intentRazor.putExtra("planPrice", planPrice);
        intentRazor.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentRazor.putExtra("planGateway", "Razorpay");
        intentRazor.putExtra("planGatewayText", getString(R.string.razor_pay));
        intentRazor.putExtra("razorPayKey", paymentSetting.getRazorPayKey());
        startActivity(intentRazor);
    }

    private void goPayStack() {
        Intent intentPayStack = new Intent(SelectPlanActivity.this, PayStackActivity.class);
        intentPayStack.putExtra("planId", planId);
        intentPayStack.putExtra("planPrice", planPrice);
        intentPayStack.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentPayStack.putExtra("planGateway", "Paystack");
        intentPayStack.putExtra("planGatewayText", getString(R.string.pay_stack));
        intentPayStack.putExtra("payStackPublicKey", paymentSetting.getPayStackPublicKey());
        startActivity(intentPayStack);
    }

    private void goInstaMojo() {
        Intent intentInstaMojo = new Intent(SelectPlanActivity.this, InstaMojoActivity.class);
        intentInstaMojo.putExtra("planId", planId);
        intentInstaMojo.putExtra("planName", planName);
        intentInstaMojo.putExtra("planPrice", planPrice);
        intentInstaMojo.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentInstaMojo.putExtra("planGateway", "Instamojo");
        intentInstaMojo.putExtra("planGatewayText", getString(R.string.insta_mojo));
        intentInstaMojo.putExtra("isSandbox", paymentSetting.isInstaMojoSandbox());
        startActivity(intentInstaMojo);
    }

    private void goPayUMoney() {
        Intent intentPayU = new Intent(SelectPlanActivity.this, PayUProActivity.class);
        intentPayU.putExtra("planId", planId);
        intentPayU.putExtra("planName", planName);
        intentPayU.putExtra("planPrice", planPrice);
        intentPayU.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentPayU.putExtra("planGateway", "PayUMoney");
        intentPayU.putExtra("planGatewayText", getString(R.string.pay_u));
        intentPayU.putExtra("isSandbox", paymentSetting.isPayUMoneySandbox());
        intentPayU.putExtra("payUMoneyMerchantId", paymentSetting.getPayUMoneyMerchantId());
        intentPayU.putExtra("payUMoneyMerchantKey", paymentSetting.getPayUMoneyMerchantKey());
        startActivity(intentPayU);
    }

    private void goPayTm() {
        Intent intentPayTm = new Intent(SelectPlanActivity.this, PayTMActivity.class);
        intentPayTm.putExtra("planId", planId);
        intentPayTm.putExtra("planName", planName);
        intentPayTm.putExtra("planPrice", planPrice);
        intentPayTm.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentPayTm.putExtra("planGateway", "Paytm");
        intentPayTm.putExtra("planGatewayText", getString(R.string.paytm));
        intentPayTm.putExtra("isSandbox", paymentSetting.isPayTMSandbox());
        intentPayTm.putExtra("paytmMid", paymentSetting.getPayTMMid());
        startActivity(intentPayTm);
    }

    private void goCashFree() {
        Intent intentCashFree = new Intent(SelectPlanActivity.this, CashFreeActivity.class);
        intentCashFree.putExtra("planId", planId);
        intentCashFree.putExtra("planName", planName);
        intentCashFree.putExtra("planPrice", planPrice);
        intentCashFree.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentCashFree.putExtra("planGateway", "Cashfree");
        intentCashFree.putExtra("planGatewayText", getString(R.string.cash_free));
        intentCashFree.putExtra("isSandbox", paymentSetting.isCashFreeSandbox());
        intentCashFree.putExtra("cashFreeAppId", paymentSetting.getCashFreeAppId());
        startActivity(intentCashFree);
    }

    private void goFlutterWave() {
        Intent intentFlutterWave = new Intent(SelectPlanActivity.this, FlutterWaveActivity.class);
        intentFlutterWave.putExtra("planId", planId);
        intentFlutterWave.putExtra("planName", planName);
        intentFlutterWave.putExtra("planPrice", planPrice);
        intentFlutterWave.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentFlutterWave.putExtra("planGateway", "Flutterwave");
        intentFlutterWave.putExtra("planGatewayText", getString(R.string.flutter_wave));
        intentFlutterWave.putExtra("fwPublicKey", paymentSetting.getFwPublicKey());
        intentFlutterWave.putExtra("fwEncryptionKey", paymentSetting.getFwEncryptionKey());
        startActivity(intentFlutterWave);
    }
}
