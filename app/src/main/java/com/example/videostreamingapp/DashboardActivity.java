package com.example.videostreamingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.example.item.ItemDashBoard;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class DashboardActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    NestedScrollView nestedScrollView;
    CircularImageView imageAvatar;
    TextView textName, textEmail, textEdit;
    TextView textCurrentPlan, textExpiresOn, textChangePlan;
    TextView textLsDate, textLsPlan, textLsAmount;
    ItemDashBoard itemDashBoard;
    MyApplication myApplication;
    boolean isPurchased = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarGradiant(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.menu_dashboard));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("isPurchased")) {
            isPurchased = true;
        }

        myApplication = MyApplication.getInstance();
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        imageAvatar = findViewById(R.id.imageAvtar);
        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        textEdit = findViewById(R.id.editProfile);

        textCurrentPlan = findViewById(R.id.textCurrentPlan);
        textExpiresOn = findViewById(R.id.textExpiresOn);
        textChangePlan = findViewById(R.id.changePlan);

        textLsDate = findViewById(R.id.textLsDate);
        textLsPlan = findViewById(R.id.textLsPlan);
        textLsAmount = findViewById(R.id.textLsAmount);

        itemDashBoard = new ItemDashBoard();

        if (NetworkUtils.isConnected(DashboardActivity.this)) {
            getDashboard();
        } else {
            Toast.makeText(DashboardActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        textChangePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, PlanActivity.class);
                startActivity(intent);
            }
        });

        textEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getDashboard() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getUserId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.DASH_BOARD_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        objJson = jsonArray.getJSONObject(0);

                        itemDashBoard.setUserName(objJson.getString("name"));
                        itemDashBoard.setUserEmail(objJson.getString("email"));
                        itemDashBoard.setUserImage(objJson.getString("user_image"));
                        itemDashBoard.setCurrentPlan(objJson.getString("current_plan"));
                        itemDashBoard.setExpiresOn(objJson.getString("expires_on"));
                        itemDashBoard.setLastInvoiceDate(objJson.getString("last_invoice_date"));
                        itemDashBoard.setLastInvoicePlan(objJson.getString("last_invoice_plan"));
                        itemDashBoard.setLastInvoiceAmount(objJson.getString("last_invoice_amount"));

                        displayData();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.GONE);
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
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        textName.setText(itemDashBoard.getUserName());
        textEmail.setText(itemDashBoard.getUserEmail());
        textEmail.setSelected(true);
        textName.setSelected(true);
        if (!itemDashBoard.getUserImage().isEmpty()) {
            Picasso.get().load(itemDashBoard.getUserImage()).into(imageAvatar);
        }

        if (itemDashBoard.getCurrentPlan().isEmpty()) {
            textCurrentPlan.setText(getString(R.string.n_a));
        } else {
            textCurrentPlan.setText(itemDashBoard.getCurrentPlan());
        }

        if (itemDashBoard.getExpiresOn().isEmpty()) {
            textExpiresOn.setText(getString(R.string.expire_on, getString(R.string.n_a)));
        } else {
            textExpiresOn.setText(getString(R.string.expire_on, itemDashBoard.getExpiresOn()));
        }

        if (itemDashBoard.getLastInvoiceDate().isEmpty()) {
            textLsDate.setText(getString(R.string.n_a));
        } else {
            textLsDate.setText(itemDashBoard.getLastInvoiceDate());
        }

        if (itemDashBoard.getLastInvoicePlan().isEmpty()) {
            textLsPlan.setText(getString(R.string.n_a));
        } else {
            textLsPlan.setText(itemDashBoard.getLastInvoicePlan());
        }

        if (itemDashBoard.getLastInvoiceAmount().isEmpty()) {
            textLsAmount.setText(getString(R.string.n_a));
        } else {
            textLsAmount.setText(itemDashBoard.getLastInvoiceAmount());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isPurchased) {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
