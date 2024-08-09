package com.example.videostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;

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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.tuyenmonkey.textdecorator.TextDecorator;
import com.tuyenmonkey.textdecorator.callback.OnTextClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class SignUpActivity extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty
    EditText edtFullName;
    @Email
    EditText edtEmail;
    @Password
    EditText edtPassword;
    @ConfirmPassword
    EditText edtPasswordConfirm;
    Button btnSignUp;
    String strName, strEmail, strPassword, strMessage;
    private Validator validator;
    TextView txtLogin;
    ProgressDialog pDialog;
    AppCompatTextView tvSignInAccept;
    AppCompatCheckBox checkBoxAgree;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarBlack(this);
        setContentView(R.layout.activity_sign_up);
        IsRTL.ifSupported(this);
        pDialog = new ProgressDialog(this);
        edtFullName = findViewById(R.id.editText_name_register);
        edtEmail = findViewById(R.id.editText_email_register);
        edtPassword = findViewById(R.id.editText_password_register);
        edtPasswordConfirm = findViewById(R.id.editText_confirm);
        tvSignInAccept = findViewById(R.id.textSignUpAccept);
        checkBoxAgree = findViewById(R.id.checkbox);

        btnSignUp = findViewById(R.id.button_submit);
        txtLogin = findViewById(R.id.textView_login_register);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
        setAcceptText();
    }

    @Override
    public void onValidationSucceeded() {
        if (NetworkUtils.isConnected(SignUpActivity.this)) {
            if (checkBoxAgree.isChecked()) {
                putSignUp();
            } else {
                showToast(getString(R.string.please_accept));
            }
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void putSignUp() {
        strName = edtFullName.getText().toString();
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("name", strName);
        jsObj.addProperty("email", strEmail);
        jsObj.addProperty("password", strPassword);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.REGISTER_URL, params, new AsyncHttpResponseHandler() {

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

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            edtEmail.setText("");
            edtEmail.requestFocus();
            showToast(strMessage);
        } else {
            showToast(strMessage);
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private void setAcceptText() {
        TextDecorator
                .decorate(tvSignInAccept, getString(R.string.sign_up_accept, getString(R.string.terms_of_service), getString(R.string.privacy_policy)))
                .setTextColor(R.color.highlight, getString(R.string.terms_of_service), getString(R.string.privacy_policy))
                .makeTextClickable(new OnTextClickListener() {
                    @Override
                    public void onClick(View view, String text) {
                        String pageId;
                        if (text.equals(getString(R.string.terms_of_service))) {
                            pageId = "1";
                        } else {
                            pageId = "2";
                        }
                        Intent intent = new Intent(SignUpActivity.this, AcceptActivity.class);
                        intent.putExtra("pageId", pageId);
                        startActivity(intent);
                    }
                }, true, getString(R.string.terms_of_service), getString(R.string.privacy_policy))
                .build();
    }
}
