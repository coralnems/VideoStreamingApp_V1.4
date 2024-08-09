package com.example.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.item.ItemAbout;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.videostreamingapp.R;
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

public class AboutFragment extends Fragment {

    private TextView txtAppName, txtVersion, txtCompany, txtEmail, txtWebsite, txtContact;
    private ImageView imgAppLogo;
    private NestedScrollView mScrollView;
    private ProgressBar mProgressBar;
    private WebView webView;
    private ItemAbout itemAbout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
        txtAppName = rootView.findViewById(R.id.text_app_name);
        txtVersion = rootView.findViewById(R.id.text_version);
        txtCompany = rootView.findViewById(R.id.text_company);
        txtEmail = rootView.findViewById(R.id.text_email);
        txtWebsite = rootView.findViewById(R.id.text_website);
        txtContact = rootView.findViewById(R.id.text_contact);
        imgAppLogo = rootView.findViewById(R.id.image_app_logo);
        webView = rootView.findViewById(R.id.webView);
        itemAbout = new ItemAbout();
        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        webView.setBackgroundColor(Color.TRANSPARENT);
        mScrollView.setVisibility(View.GONE);

        if (NetworkUtils.isConnected(getActivity())) {
            getAboutUs();
        } else {
            showToast(getString(R.string.conne_msg1));
        }


        return rootView;
    }

    private void getAboutUs() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.APP_DETAIL_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                mScrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        itemAbout.setAppName(objJson.getString(Constant.APP_NAME));
                        itemAbout.setAppLogo(objJson.getString(Constant.APP_IMAGE));
                        itemAbout.setAppVersion(objJson.getString(Constant.APP_VERSION));
                        itemAbout.setAppAuthor(objJson.getString(Constant.APP_AUTHOR));
                        itemAbout.setAppEmail(objJson.getString(Constant.APP_EMAIL));
                        itemAbout.setAppWebsite(objJson.getString(Constant.APP_WEBSITE));
                        itemAbout.setAppContact(objJson.getString(Constant.APP_CONTACT));
                        itemAbout.setAppDescription(objJson.getString(Constant.APP_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
            }

        });
    }

    private void setResult() {
        txtAppName.setText(itemAbout.getAppName());
        txtVersion.setText(itemAbout.getAppVersion());
        txtCompany.setText(itemAbout.getAppAuthor());
        txtEmail.setText(itemAbout.getAppEmail());
        txtWebsite.setText(itemAbout.getAppWebsite());
        txtContact.setText(itemAbout.getAppContact());
        if (!itemAbout.getAppLogo().isEmpty()) {
            Picasso.get().load(itemAbout.getAppLogo()).into(imgAppLogo);
        }

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = itemAbout.getAppDescription();
        boolean isRTL = Boolean.parseBoolean(getResources().getString(R.string.isRTL));
        String direction = isRTL ? "rtl" : "ltr";
        String text = "<html dir=" + direction + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.otf\")}body{font-family: MyFont;color: #767676;text-align:justify;margin-left:0px;line-height:1.2}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);
    }


    private void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
