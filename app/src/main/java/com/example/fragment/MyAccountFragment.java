package com.example.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.item.ItemDashBoard;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.videostreamingapp.DashboardActivity;
import com.example.videostreamingapp.EditProfileActivity;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.PlanActivity;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.SignInActivity;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.material.button.MaterialButton;
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

public class MyAccountFragment extends Fragment {
    ProgressBar progressBar;
    LinearLayout lyt_not_found;
    RelativeLayout lytLogin;
    MaterialButton btnLogin;
    MyApplication myApplication;
    TextView tvLoginFirst, textName, textEmail, textCurrentPlan, textExpiresOn, textChangePlan, tvDashboard, tvEditProfile, tvLogout;
    NestedScrollView nestedScrollView;
    CircularImageView imageAvatar;
    ItemDashBoard itemDashBoard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        myApplication = MyApplication.getInstance();
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar1);
        lytLogin = rootView.findViewById(R.id.lytLogin);
        btnLogin = rootView.findViewById(R.id.btnLogin);
        tvLoginFirst = rootView.findViewById(R.id.text);
        tvLoginFirst.setText(getString(R.string.login_first_see_account));
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
        textName = rootView.findViewById(R.id.textName);
        textEmail = rootView.findViewById(R.id.textEmail);
        textCurrentPlan = rootView.findViewById(R.id.textCurrentPlan);
        textExpiresOn = rootView.findViewById(R.id.textExpiresOn);
        textChangePlan = rootView.findViewById(R.id.changePlan);
        tvDashboard = rootView.findViewById(R.id.tvDashboard);
        tvEditProfile = rootView.findViewById(R.id.tvEditProfile);
        tvLogout = rootView.findViewById(R.id.tvLogout);
        imageAvatar = rootView.findViewById(R.id.imageAvtar);

        itemDashBoard = new ItemDashBoard();

        textChangePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), PlanActivity.class);
                startActivity(intent);
            }
        });

        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        tvDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), DashboardActivity.class);
                startActivity(intent);
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignIn = new Intent(requireActivity(), SignInActivity.class);
                intentSignIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentSignIn);
                requireActivity().finish();
            }
        });
        nestedScrollView.setVisibility(View.GONE);
        if (myApplication.getIsLogin()) {
            if (NetworkUtils.isConnected(requireActivity())) {
                getDashboard();
            } else {
                Toast.makeText(requireActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
            }
        } else {
            nestedScrollView.setVisibility(View.GONE);
            lytLogin.setVisibility(View.VISIBLE);
        }

        return rootView;
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
                progressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
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
                        if (getActivity() != null) {
                            displayData();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.GONE);
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
    }

    private void logOut() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myApplication.saveIsLogin(false);
                        Intent intent = new Intent(requireActivity(), SignInActivity.class);
                        intent.putExtra("isLogout", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        requireActivity().finish();
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
}
