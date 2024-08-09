package com.example.videostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.tuyenmonkey.textdecorator.TextDecorator;
import com.tuyenmonkey.textdecorator.callback.OnTextClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import cn.refactor.library.SmoothCheckBox;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class SignInActivity extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty
    @Email
    EditText edtEmail;
    @NotEmpty
    @Password
    EditText edtPassword;
    String strEmail, strPassword, strMessage, strName, strUserId, strPhoneNo;
    Button btnLogin;
    TextView btnForgotPass, btnRegister, btnSkip;
    private Validator validator;
    MyApplication myApplication;
    ProgressDialog pDialog;
    SmoothCheckBox checkBox;
    boolean isFromOtherScreen = false;
    String postId, postType;
    FrameLayout fbLoginBtn;
    LinearLayout googleLoginBtn;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;
    boolean isLogout = false;
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
        setContentView(R.layout.activity_sign_in);
        IsRTL.ifSupported(this);

        Intent intent = getIntent();
        if (intent.hasExtra("isOtherScreen")) {
            isFromOtherScreen = true;
            postId = intent.getStringExtra("postId");
            postType = intent.getStringExtra("postType");
        }

        if (intent.hasExtra("isLogout")) {
            isLogout = intent.getBooleanExtra("isLogout", false);
        }

        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fbLoginBtn = findViewById(R.id.frameLayout_login);
        googleLoginBtn = findViewById(R.id.linearLayout_google_login);
        pDialog = new ProgressDialog(this);
        myApplication = MyApplication.getInstance();
        edtEmail = findViewById(R.id.editText_email_login_activity);
        edtPassword = findViewById(R.id.editText_password_login_activity);
        btnLogin = findViewById(R.id.button_login_activity);
        btnSkip = findViewById(R.id.button_skip_login_activity);
        btnForgotPass = findViewById(R.id.textView_forget_password_login);
        btnRegister = findViewById(R.id.textView_signup_login);
        checkBox = findViewById(R.id.checkbox_login_activity);
        tvSignInAccept = findViewById(R.id.textSignUpAccept);
        checkBoxAgree = findViewById(R.id.checkbox);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });


        if (myApplication.getIsRemember()) {
            checkBox.setChecked(true);
            edtEmail.setText(myApplication.getRememberEmail());
            edtPassword.setText(myApplication.getRememberPassword());
        }
        validator = new Validator(this);
        validator.setValidationListener(this);

        myApplication.saveIsIntroduction(true);

        fbLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxAgree.isChecked()) {
                    if (v == fbLoginBtn) {
                        LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("email"));
                    }
                } else {
                    showToast(getString(R.string.please_accept));
                }
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getDetailsFromFacebook(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxAgree.isChecked()) {
                    googleSignIn();
                } else {
                    showToast(getString(R.string.please_accept));
                }
            }
        });

        if (isLogout) {
            logoutSocial(myApplication.getLoginType());
        }
        setAcceptText();
    }

    @Override
    public void onValidationSucceeded() {
        if (NetworkUtils.isConnected(SignInActivity.this)) {
            if (checkBoxAgree.isChecked()) {
                putSignIn();
            } else {
                showToast(getString(R.string.please_accept));
            }
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    public void putSignIn() {
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();

        if (checkBox.isChecked()) {
            myApplication.saveIsRemember(true);
            myApplication.saveRemember(strEmail, strPassword);
        } else {
            myApplication.saveIsRemember(false);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("email", strEmail);
        jsObj.addProperty("password", strPassword);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.LOGIN_URL, params, new AsyncHttpResponseHandler() {

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
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        if (Constant.GET_SUCCESS_MSG == 0) {
                            strMessage = objJson.getString(Constant.MSG);
                        } else {
                            strName = objJson.getString(Constant.USER_NAME);
                            strUserId = objJson.getString(Constant.USER_ID);
                            strPhoneNo = objJson.getString("phone");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult("normal");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
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

    public void setResult(String type) {

        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast(strMessage);
            logoutSocial(type);
        } else {
            myApplication.saveIsLogin(true);
            myApplication.saveLoginType(type);
            myApplication.saveLogin(strUserId, strName, strEmail, strPhoneNo);
            if (isFromOtherScreen) {
                Class<?> aClass;
                switch (postType) {
                    case "Movies":
                        aClass = MovieDetailsActivity.class;
                        break;
                    case "Shows":
                        aClass = ShowDetailsActivity.class;
                        break;
                    case "LiveTV":
                        aClass = TVDetailsActivity.class;
                        break;
                    default:
                        aClass = SportDetailsActivity.class;
                        break;
                }
                Intent intent = new Intent(SignInActivity.this, aClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Id", postId);
                startActivity(intent);
            } else {
                Intent i = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    //Google login
    private void googleSignIn() {
        if (NetworkUtils.isConnected(this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            socialLogin(id, name, email, "google");
        } catch (ApiException e) {
            Toast.makeText(SignInActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDetailsFromFacebook(LoginResult loginResult) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    socialLogin(id, name, email, "facebook");
                } catch (JSONException e) {
                    try {
                        String id = object.getString("id");
                        String name = object.getString("name");
                        socialLogin(id, name, "", "facebook");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email"); // Parameters that we ask for facebook
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void socialLogin(String authId, String name, String email, String type) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("social_id", authId);
        jsObj.addProperty("name", name);
        jsObj.addProperty("email", email);
        jsObj.addProperty("login_type", type);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.LOGIN_SOCIAL_URL, params, new AsyncHttpResponseHandler() {

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
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        if (Constant.GET_SUCCESS_MSG == 0) {
                            strMessage = objJson.getString(Constant.MSG);
                        } else {
                            strName = objJson.getString(Constant.USER_NAME);
                            strUserId = objJson.getString(Constant.USER_ID);
                            strEmail = objJson.getString(Constant.USER_EMAIL);
                            strPhoneNo = objJson.getString("phone");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult(type);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    private void logoutSocial(String type) {
        if (type.equals("google")) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            myApplication.saveIsLogin(false);
                        }
                    });
        } else if (type.equals("facebook")) {
            LoginManager.getInstance().logOut();
            myApplication.saveIsLogin(false);
        }
    }

    private void setAcceptText() {
        TextDecorator
                .decorate(tvSignInAccept, getString(R.string.sign_in_accept, getString(R.string.terms_of_service), getString(R.string.privacy_policy)))
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
                        Intent intent = new Intent(SignInActivity.this, AcceptActivity.class);
                        intent.putExtra("pageId", pageId);
                        startActivity(intent);
                    }
                }, true, getString(R.string.terms_of_service), getString(R.string.privacy_policy))
                .build();
    }
}
