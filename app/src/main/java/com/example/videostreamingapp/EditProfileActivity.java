package com.example.videostreamingapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.GetPath;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class EditProfileActivity extends AppCompatActivity implements Validator.ValidationListener {

    private ProgressBar mProgressBar;
    private LinearLayout lyt_not_found;
    private NestedScrollView nestedScrollView;

    @NotEmpty
    private EditText edtName;
    @Email
    private EditText edtEmail;
    private EditText edtPassword;
    @Length(max = 14, min = 6, message = "Enter valid Phone Number")
    private EditText edtPhone;
    @NotEmpty
    private EditText edtAddress;

    private String strName, strEmail, strPassword, strMobi, strMessage, strAddress;

    private Button btnSubmit;
    private CircularImageView imageAvtar;
    private MyApplication myApplication;

    private ProgressDialog pDialog;

    private Validator validator;

    private File avtarFile;
    private boolean isImage = false;
    private boolean isFromPayU = false;
    private static final int SELECT_PHOTO = 100;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarGradiant(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.menu_profile));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("isFromPayU")) {
            isFromPayU = intent.getBooleanExtra("isFromPayU", false);
        }

        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtPhone = findViewById(R.id.edt_phone);
        edtAddress = findViewById(R.id.edt_address);
        imageAvtar = findViewById(R.id.imageAvtar);
        btnSubmit = findViewById(R.id.button_submit);
        myApplication = MyApplication.getInstance();


        pDialog = new ProgressDialog(EditProfileActivity.this);
        nestedScrollView.setVisibility(View.GONE);

        if (NetworkUtils.isConnected(EditProfileActivity.this)) {
            getUserProfile();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        imageAvtar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onValidationSucceeded() {
        if (NetworkUtils.isConnected(EditProfileActivity.this)) {
            strPassword = edtPassword.getText().toString();
            if (strPassword.length() >= 1 && strPassword.length() <= 5) {
                edtPassword.setError("Invalid Password");
            } else {
                putEditProfile();
            }

        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(EditProfileActivity.this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                showToast(message);
            }
        }
    }

    private void getUserProfile() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getUserId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.PROFILE_URL, params, new AsyncHttpResponseHandler() {

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
                    if (jsonArray.length() > 0) {
                        JSONObject objJson;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            edtName.setText(objJson.getString(Constant.USER_NAME));
                            edtEmail.setText(objJson.getString(Constant.USER_EMAIL));
                            edtPhone.setText(objJson.getString(Constant.USER_PHONE));
                            edtAddress.setText(objJson.getString(Constant.USER_ADDRESS));
                            String userImage = objJson.getString(Constant.USER_IMAGE);
                            if (!userImage.isEmpty()) {
                                Picasso.get().load(userImage).placeholder(R.mipmap.ic_launcher_round).into(imageAvtar);
                            }
                        }
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
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void putEditProfile() {
        strName = edtName.getText().toString();
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();
        strMobi = edtPhone.getText().toString();
        strAddress = edtAddress.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("name", strName);
        jsObj.addProperty("email", strEmail);
        if (strPassword.isEmpty()) {
            jsObj.addProperty("password", "");
        } else {
            jsObj.addProperty("password", strPassword);
        }
        jsObj.addProperty("phone", strMobi);
        jsObj.addProperty("user_address", strAddress);
        params.put("data", API.toBase64(jsObj.toString()));

        if (isImage) {
            try {
                params.put("user_image", avtarFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            params.put("user_image", "");
        }

        client.post(Constant.EDIT_PROFILE_URL, params, new AsyncHttpResponseHandler() {

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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    private void setResult() {
        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast(strMessage);
        } else {
            myApplication.saveLogin(myApplication.getUserId(), strName, strEmail, strMobi);
            showToast(strMessage);
            if (isFromPayU) {
                Intent intent = new Intent();
                setResult(1187, intent);
                finish();
            }
        }
    }


    private void showToast(String msg) {
        Toast.makeText(EditProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private void requestStoragePermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        chooseAvtarImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void chooseAvtarImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String filePath = GetPath.getPath(EditProfileActivity.this, selectedImage);
            if (filePath != null) {
                avtarFile = new File(filePath);
                Picasso.get().load(avtarFile).into(imageAvtar);
                isImage = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
