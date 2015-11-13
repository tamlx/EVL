package com.thanhle.englishvocabulary.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.DictionaryDatabase;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.dialog.LoadLibraryDialog;
import com.thanhle.englishvocabulary.requestmanagement.DownloadLibraryListenner;
import com.thanhle.englishvocabulary.requestmanagement.RequestErrorListener;
import com.thanhle.englishvocabulary.resource.DownloadLibraryInstallResource;
import com.thanhle.englishvocabulary.resource.LibraryInfosResource;
import com.thanhle.englishvocabulary.resource.ListLibraryResource;
import com.thanhle.englishvocabulary.resource.UserResource;
import com.thanhle.englishvocabulary.utils.Actions;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.FileUtils;
import com.thanhle.englishvocabulary.utils.SharePrefs;
import com.thanhle.englishvocabulary.utils.TTS;
import com.thanhle.englishvocabulary.view.TypingTextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class SplashActivity extends BaseActivity implements View.OnClickListener, DownloadLibraryListenner {
    private ProgressBar progressBar;
    private TextView tvLoading;
    private float mReadDictionaryCount = 0;
    private float increase;
    private TypingTextView tvApplicationName;
    DictionaryDatabase databaseDictionary;

    private ArrayList<DownloadLibraryInstallResource> listLibraryInstalled = new ArrayList<DownloadLibraryInstallResource>();

    private LoadLibraryDialog loadLibraryDialog;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Handler mHandlerInstallLibrarySuccess = new Handler();
    private Handler mHandlerTimeOut = new Handler();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mReadDictionaryCount += increase;
                if (progressBar != null) {
                    progressBar.setProgress((int) mReadDictionaryCount);
                }
            }
        }
    };

    private Response.Listener<UserResource> mLoginRequestSuccessListener = new Response.Listener<UserResource>() {
        @Override
        public void onResponse(UserResource response) {
            dismissProgress();
            if (response.libraries.length > 0) {
                for (int i = 0; i < response.libraries.length; i++) {
                    listLibraryInstalled.add(new DownloadLibraryInstallResource(response.libraries[i].getName(), response.libraries[i].get_id(), 1));
                }
                downloadLibrary();
            } else {
                listLibraryInstalled.add(new DownloadLibraryInstallResource("Bộ từ vựng 1000 từ cơ bản", "54c2834530fd0f09005e69ce", 1));
                downloadLibrary();

            }

        }
    };

    private void downloadLibrary() {
        if (database.checkLibraryExists(listLibraryInstalled.get(listLibraryInstalled.size() - 1).getLibraryId())) {
            Log.e("SplashActivity", "not first install");
            if (database.checkLibraryExists("54c2834530fd0f09005e69ce")) {
                mSharePrefs.saveCurrentLibrary("54c2834530fd0f09005e69ce");
            } else {
                mSharePrefs.saveCurrentLibrary(listLibraryInstalled.get(listLibraryInstalled.size() - 1).getLibraryId());
            }
            Intent i = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(i);
            finish();
        } else {
            if (loadLibraryDialog == null) {
                loadLibraryDialog = LoadLibraryDialog.newInstance(listLibraryInstalled);
            }
            if (!loadLibraryDialog.isShowing() && !isFinishing()) {
                loadLibraryDialog.show(getSupportFragmentManager(),
                        "load_library");
            }
            new DownloadLibrary(listLibraryInstalled.get(0).getLibraryId(), this).execute();
        }
    }

    private boolean checkAllLibraryInstalled() {

        if (listLibraryInstalled.size() > 0) {
            for (int i = 0; i < listLibraryInstalled.size(); i++) {
                if (!database.checkLibraryExists(listLibraryInstalled.get(i).getLibraryId())) {
                    return false;
                }
            }
        }
        return true;
    }


    private RequestErrorListener mLoginRequestErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            dismissProgress();
            Log.d("SplashActivity", "status: " + status + ", code: " + code + ", message: " + message);
            EditText tvLoginUsername = (EditText) findViewById(R.id.tvLoginUsername);
            EditText tvLoginPassword = (EditText) findViewById(R.id.tvLoginPassword);
            if (message != null && message.equals("username doesn't exists")) {
                tvLoginUsername.setError(getString(R.string.msg_err_username_not_exists));
                tvLoginUsername.requestFocus();
            } else if (message != null && message.equals("password isn't correct")) {
                tvLoginPassword.setError(getString(R.string.msg_err_password_incorrect));
                tvLoginPassword.requestFocus();
            }
        }
    };

    private Response.Listener<UserResource> mRegisterRequestSuccessListener = new Response.Listener<UserResource>() {
        @Override
        public void onResponse(UserResource response) {
            dismissProgress();
            Intent i = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(i);
            finish();
        }
    };

    private RequestErrorListener mRegisterRequestErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            dismissProgress();
            Log.d("SplashActivity", "status: " + status + ", code: " + code + ", message: " + message);
            EditText tvRegisterUsername = (EditText) findViewById(R.id.tvRegisterUsername);
            EditText tvRegisterEmail = (EditText) findViewById(R.id.tvRegisterEmail);
            if (message.equals("username is exists")) {
                tvRegisterUsername.setError(getString(R.string.msg_err_username_exists));
                tvRegisterUsername.requestFocus();
            } else if (message.equals("email is exists")) {
                tvRegisterEmail.setError(getString(R.string.msg_err_email_exists));
                tvRegisterEmail.requestFocus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        getKeyhash();
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        int i = 100 / 0;

        loginWithFacebook();
        initLoginWithGoogle();

        // init dictionary database
        databaseDictionary = new DictionaryDatabase(this);
        // find view
        tvApplicationName = (TypingTextView) findViewById(R.id.tvApplicationName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        tvLoading = (TextView) findViewById(R.id.tvLoading);
        boolean dataInited = extractData();
        if (!dataInited) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (tvApplicationName != null) {
            tvApplicationName.runTypingAnimation();
            if (dataInited) {
                tvApplicationName.setTypingAnimationListener(new TypingTextView.TypingAnimationListener() {
                    @Override
                    public void onTypingAnimationEnd() {
                        onLoadDataComplete();
                    }
                });
            }
        }
    }


    private boolean extractData() {
        // check and extract voice file
        final File f = new File(getCacheDir(), Consts.VOICE_FOLDER_NAME);

        // check dir is empty, extra voice zip file
        if (f.list().length == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvLoading.setText(R.string.loading_voice);
                            }
                        });
                        FileUtils.unzip(getAssets().open("voice.zip"),
                                f.getAbsolutePath());
                        TTS.getInstance().init(f.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return extractDictionaryData();
        } else {
            TTS.getInstance().init(f.getAbsolutePath());
            return extractDictionaryData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean extractDictionaryData() {
        // check database has data
        final int dictionaryCount = databaseDictionary.countDictionary();
        Log.e("DictionaryCount", "Dictionary count: " + dictionaryCount);
        if (dictionaryCount < Consts.DICTIONARY_COUNT) {
            if (Consts.DICTIONARY_COUNT - dictionaryCount < DictionaryDatabase.BUFFER) {
                increase = 100;
            } else {
                increase = (float) DictionaryDatabase.BUFFER / (Consts.DICTIONARY_COUNT - dictionaryCount) * 100;
            }
            progressBar.setIndeterminate(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvLoading.setText(R.string.loading_dictionary);
                            }
                        });
                        // get dictionary database folder
                        File dir = new File(getCacheDir(), Consts.EXTRACT_FOLDER_NAME);
                        FileUtils.unzip(getAssets().open("dict_en_vi.zip"),
                                dir.getAbsolutePath());
                        // after unzip, read dictionary data file to dictionary database
                        String[] fileList = dir.list();
                        if (fileList.length > 0) {
                            // get dictionary text input file
                            File file = new File(dir.getAbsolutePath() + "/" + fileList[0]);
                            InputStream in = new FileInputStream(file);
                            // reset count
                            mReadDictionaryCount = 0;
                            databaseDictionary.readDictionary(SplashActivity.this, in, dictionaryCount, mHandler);
                            file.delete();
                        }
                        onLoadDataComplete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gọi sau khi load dữ liệu thành công
     */
    private void onLoadDataComplete() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                // kiểm tra xem đã login chưa, nếu chưa login, hiển thị login
                if (TextUtils.isEmpty(mSharePrefs.getToken())) {
                    tvApplicationName.setVisibility(View.GONE);
                    View rlLogin = findViewById(R.id.rlLogin);
                    View rlRegister = findViewById(R.id.rlRegister);
                    View btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
                    btnLoginFacebook.setOnClickListener(SplashActivity.this);
                    View btnLoginWithGoogle = findViewById(R.id.btnLoginGoogle);
                    btnLoginWithGoogle.setOnClickListener(SplashActivity.this);

                    // set sự kiện click cho button login, gọi api login
                    rlLogin.findViewById(R.id.btnLoginButton).setOnClickListener(SplashActivity.this);
                    rlLogin.findViewById(R.id.btnLoginRegister).setOnClickListener(SplashActivity.this);
                    rlRegister.findViewById(R.id.btnRegisterLogin).setOnClickListener(SplashActivity.this);
                    rlRegister.findViewById(R.id.btnRegisterButton).setOnClickListener(SplashActivity.this);
                    // hiển thị khung Login
                    rlLogin.setVisibility(View.VISIBLE);
                    int top = rlLogin.getTop();
                    ObjectAnimator anim = ObjectAnimator.ofFloat(rlLogin, "translationY", 300, 0);
                    anim.setDuration(500);
                    anim.start();
                } else {
//                    if (Consts.LIST_LIBRARY.size() == 0 && isConnectInternet()) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString(Consts.PARAMConsts.TOKEN, mSharePrefs.getToken());
//                        mRequestManager.request(Actions.GETLISTLIBRARY, bundle, mGetListLibrarySuccessListener, mGetListLibraryErrorListener);
//
//                    } else {
                    Intent i = new Intent(SplashActivity.this, MyActivity.class);
                    startActivity(i);
                    finish();
//                    }
                }
            }
        }, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        database.close();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLoginButton: {
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                View rlLogin = findViewById(R.id.rlLogin);
                EditText tvLoginUsername = (EditText) rlLogin.findViewById(R.id.tvLoginUsername);
                EditText tvLoginPassword = (EditText) rlLogin.findViewById(R.id.tvLoginPassword);
                String username = tvLoginUsername.getText().toString().trim();
                String pass = tvLoginPassword.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    tvLoginUsername.setError(getString(R.string.msg_err_username_empty));
                    tvLoginUsername.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    tvLoginPassword.setError(getString(R.string.msg_err_password_empty));
                    tvLoginPassword.requestFocus();
                    return;
                }
                if (isConnectInternet()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.PARAMConsts.USERNAME, username);
                    bundle.putString(Consts.PARAMConsts.PASSWORD, pass);
                    showProgress(getString(R.string.processing));
                    mRequestManager.request(Actions.LOGIN, bundle, mLoginRequestSuccessListener, mLoginRequestErrorListener);

                    mHandlerTimeOut.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing() && mSharePrefs.getToken().equalsIgnoreCase("")) {
                                dismissProgress();
                                Toast.makeText(SplashActivity.this, getString(R.string.connect_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 20000);

                } else {
                    showCenterToast(getString(R.string.not_connect_internet));
                }
                break;
            }
            case R.id.btnRegisterButton: {
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                View rlLogin = findViewById(R.id.rlRegister);
                EditText tvRegisterUsername = (EditText) rlLogin.findViewById(R.id.tvRegisterUsername);
                EditText tvRegisterEmail = (EditText) rlLogin.findViewById(R.id.tvRegisterEmail);
                EditText tvRegisterPassword = (EditText) rlLogin.findViewById(R.id.tvRegisterPassword);
                EditText tvRegisterPasswordAgain = (EditText) rlLogin.findViewById(R.id.tvRegisterPasswordAgain);
                String username = tvRegisterUsername.getText().toString().trim();
                String email = tvRegisterEmail.getText().toString().trim();
                String pass = tvRegisterPassword.getText().toString();
                String pass2 = tvRegisterPasswordAgain.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    tvRegisterUsername.setError(getString(R.string.msg_err_username_empty));
                    tvRegisterUsername.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    tvRegisterEmail.setError(getString(R.string.msg_err_email_empty));
                    tvRegisterEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    tvRegisterPassword.setError(getString(R.string.msg_err_password_empty));
                    tvRegisterPassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(pass2)) {
                    tvRegisterPasswordAgain.setError(getString(R.string.msg_err_password_empty));
                    tvRegisterPasswordAgain.requestFocus();
                    return;
                }
                if (!validateEmail(email)) {
                    tvRegisterEmail.setError(getString(R.string.msg_err_email_invalid));
                    tvRegisterEmail.requestFocus();
                    return;
                }
                if (!pass.equals(pass2)) {
                    tvRegisterPassword.setError(getString(R.string.msg_err_password_not_match));
                    tvRegisterPassword.requestFocus();
                    return;
                }
                if (isConnectInternet()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.PARAMConsts.USERNAME, username);
                    bundle.putString(Consts.PARAMConsts.EMAIL, email);
                    bundle.putString(Consts.PARAMConsts.PASSWORD, pass);
                    showProgress(getString(R.string.processing));
                    mRequestManager.request(Actions.REGISTER, bundle, mRegisterRequestSuccessListener, mRegisterRequestErrorListener);
                } else {
                    showCenterToast(getString(R.string.not_connect_internet));
                }

                mHandlerTimeOut.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing() && mSharePrefs.getToken().equalsIgnoreCase("")) {
                            dismissProgress();
                            Toast.makeText(SplashActivity.this, getString(R.string.connect_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 20000);

                break;
            }
            case R.id.btnLoginRegister: {
                final View rlLogin = findViewById(R.id.rlLogin);
                View rlRegister = findViewById(R.id.rlRegister);
                rlRegister.setVisibility(View.VISIBLE);
                ObjectAnimator anim = ObjectAnimator.ofFloat(rlLogin, "translationY", 0, rlLogin.getHeight());
                anim.setDuration(500);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(rlRegister, "translationY", -rlLogin.getHeight(), 0);
                anim2.setDuration(500);
                anim.start();
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        rlLogin.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                anim2.start();

                break;
            }
            case R.id.btnRegisterLogin: {
                View rlLogin = findViewById(R.id.rlLogin);
                final View rlRegister = findViewById(R.id.rlRegister);
                rlLogin.setVisibility(View.VISIBLE);
                ObjectAnimator anim = ObjectAnimator.ofFloat(rlRegister, "translationY", 0, -rlRegister.getHeight());
                anim.setDuration(500);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(rlLogin, "translationY", rlRegister.getHeight(), 0);
                anim2.setDuration(500);
                anim.start();
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        rlRegister.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                anim2.start();
                break;
            }
            case R.id.btnLoginFacebook:

                if (loginButton != null) {
                    if (isConnectInternet()) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            GraphRequest request = GraphRequest.newMeRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            // Application code
                                            Log.e("SplashActivity", object.toString());
                                            try {
                                                Consts.USER_FB_ID = object.getString("id").toString();
                                                Consts.USERNAME_FB_GG = object.getString("name").toString();
                                                Consts.PERSON_PHOTO_URL = "https://graph.facebook.com/" + Consts.USER_FB_ID + "/picture";
                                                SharePrefs.getInstance().save(Consts.LINK_PICTURE, Consts.PERSON_PHOTO_URL);
                                                Bundle bundle = new Bundle();
                                                bundle.putString(Consts.PARAMConsts.USERNAME, Consts.USER_FB_ID);
                                                bundle.putString(Consts.PARAMConsts.EMAIL, object.getString("email").toString());
                                                bundle.putString(Consts.PARAMConsts.PASSWORD, Consts.USER_FB_ID);
                                                bundle.putString(Consts.PARAMConsts.PROVIDER, "facebook");
                                                showProgress(getString(R.string.processing));
                                                mRequestManager.request(Actions.REGISTER, bundle, mRegisterWithFacebookRequestSuccessListener, mRegisterWithFacebookRequestErrorListener);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {
                            loginButton.performClick();
                        }
                        mHandlerTimeOut.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(!isFinishing() && mSharePrefs.getToken().equalsIgnoreCase("")){
                                    dismissProgress();
                                    Toast.makeText(SplashActivity.this,getString(R.string.connect_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },20000);
                    } else {
                        showCenterToast(getString(R.string.not_connect_internet));
                    }
                }
                break;

            case R.id.btnLoginGoogle:
                if (isConnectInternet()) {
                    onSignInClicked();
                    mHandlerTimeOut.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing() && mSharePrefs.getToken().equalsIgnoreCase("")) {
                                dismissProgress();
                                Toast.makeText(SplashActivity.this, getString(R.string.connect_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 20000);
                } else {
                    showCenterToast(getString(R.string.not_connect_internet));
                }
                break;
        }
    }

    protected boolean validateEmail(String email) {
        boolean valid = false;
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            valid = true;
        return valid;
    }


    /*
        Login with facebook
     */
    private void getKeyhash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.thanhle.englishvocabulary",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loginWithFacebook() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("SplashActivity", "Facebook Login Successful!");
//                Log.e("SplashActivity", "Logged in user Details : " + loginResult);
                Log.e("SplashActivity", "--------------------------");
                Log.e("SplashActivity", "User ID  : " + loginResult.getAccessToken().getUserId());
//                Log.e("SplashActivity", "Authentication Token : " + loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.e("SplashActivity", object.toString());
                                try {
                                    Consts.USER_FB_ID = object.getString("id").toString();
                                    Consts.USERNAME_FB_GG = object.getString("name").toString();
                                    Consts.PERSON_PHOTO_URL = "https://graph.facebook.com/" + Consts.USER_FB_ID + "/picture";
                                    SharePrefs.getInstance().save(Consts.LINK_PICTURE, Consts.PERSON_PHOTO_URL);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Consts.PARAMConsts.USERNAME, Consts.USER_FB_ID);
                                    bundle.putString(Consts.PARAMConsts.EMAIL, object.getString("email").toString());
                                    bundle.putString(Consts.PARAMConsts.PASSWORD, Consts.USER_FB_ID);
                                    bundle.putString(Consts.PARAMConsts.PROVIDER, "facebook");
                                    showProgress(getString(R.string.processing));
                                    mRequestManager.request(Actions.REGISTER, bundle, mRegisterWithFacebookRequestSuccessListener, mRegisterWithFacebookRequestErrorListener);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(SplashActivity.this, "Login cancelled!", Toast.LENGTH_LONG).show();
                Log.e("SplashActivity", "Facebook Login failed!!");

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(SplashActivity.this, "Login unsuccessful!", Toast.LENGTH_LONG).show();
                Log.e("SplashActivity", "Facebook Login failed!!");
            }
        });
    }

    private Response.Listener<UserResource> mRegisterWithFacebookRequestSuccessListener = new Response.Listener<UserResource>() {
        @Override
        public void onResponse(UserResource response) {
            Log.e("SplashActivity", "mRegisterWithFacebookRequestSuccess!");
            dismissProgress();
            Intent i = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(i);
            finish();
        }
    };

    private RequestErrorListener mRegisterWithFacebookRequestErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            dismissProgress();
            Log.e("SplashActivity", "mRegisterWithFacebookRequestErrorListener!");
            Log.e("SplashActivity", "status: " + status + ", code: " + code + ", message: " + message);
            if (message.equalsIgnoreCase("username is exists")) {
                Bundle bundle = new Bundle();
                bundle.putString(Consts.PARAMConsts.USERNAME, Consts.USER_FB_ID);
                bundle.putString(Consts.PARAMConsts.PASSWORD, Consts.USER_FB_ID);
                bundle.putString(Consts.PARAMConsts.PROVIDER, "facebook");
                mRequestManager.request(Actions.LOGIN, bundle, mLoginWithFacebookRequestSuccessListener, mLoginWithFacebookRequestErrorListener);
            } else {
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Response.Listener<UserResource> mLoginWithFacebookRequestSuccessListener = new Response.Listener<UserResource>() {
        @Override
        public void onResponse(UserResource response) {
            dismissProgress();
            Log.e("SplashActivity", "mLoginWithFacebookRequestSuccessListener!");
            Intent i = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(i);
            finish();
        }
    };

    private RequestErrorListener mLoginWithFacebookRequestErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            Log.e("SplashActivity", "mLoginWithFacebookRequestErrorListener!");
            dismissProgress();
            Log.e("SplashActivity", "status: " + status + ", code: " + code + ", message: " + message);

        }
    };

    /*
        Login with Google
     */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private void initLoginWithGoogle() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFaildedListener)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
//                .addScope(new Scope(Scopes.PROFILE))
//                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }


    GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Log.e("SplashActivity", "onConnected:" + bundle);
            Log.e("SplashActivity", "User is connected!");
            mShouldResolve = false;
            getProfileInformation();
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }
    };

    GoogleApiClient.OnConnectionFailedListener mConnectionFaildedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {


            Log.e("SplasActivity", "onConnectionFailed:" + connectionResult);

            if (!mIsResolving && mShouldResolve) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(SplashActivity.this, 0);
                        mIsResolving = true;
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("SplasActivity", "Could not resolve ConnectionResult.", e);
                        mIsResolving = false;
                        mGoogleApiClient.connect();
                    }
                }
            }
        }
    };

    private void onSignInClicked() {
        mShouldResolve = true;
        showProgress(getString(R.string.processing));
        if (mGoogleApiClient == null) {
            initLoginWithGoogle();
        }
        mGoogleApiClient.connect();

    }


    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                Consts.USERNAME_FB_GG = currentPerson.getDisplayName();
                Consts.USER_GG_ID = currentPerson.getId();
                Consts.PERSON_PHOTO_URL = currentPerson.getImage().getUrl();
                SharePrefs.getInstance().save(Consts.LINK_PICTURE, Consts.PERSON_PHOTO_URL);
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e("SplashActivity", "UserID: " + Consts.USER_GG_ID + ", Name: " + Consts.USERNAME_FB_GG + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + Consts.PERSON_PHOTO_URL);

                Bundle bundle = new Bundle();
                bundle.putString(Consts.PARAMConsts.USERNAME, Consts.USER_GG_ID);
                bundle.putString(Consts.PARAMConsts.EMAIL, email);
                bundle.putString(Consts.PARAMConsts.PASSWORD, Consts.USER_GG_ID);
                bundle.putString(Consts.PARAMConsts.PROVIDER, "google");

                mRequestManager.request(Actions.REGISTER, bundle, mRegisterWithGoogleRequestSuccessListener, mRegisterWithGoogleRequestErrorListener);

            } else {
                dismissProgress();
                Log.e("SplashActivity", "Person information is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

    }

    private Response.Listener<UserResource> mRegisterWithGoogleRequestSuccessListener = new Response.Listener<UserResource>() {
        @Override
        public void onResponse(UserResource response) {
            Log.e("SplashActivity", "mRegisterWithGoogleRequestSuccess!");
            dismissProgress();
            Intent i = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(i);
            finish();
        }
    };

    private RequestErrorListener mRegisterWithGoogleRequestErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            dismissProgress();
            Log.e("SplashActivity", "mRegisterWithGoogleRequestErrorListener!");
            Log.e("SplashActivity", "status: " + status + ", code: " + code + ", message: " + message);
            if (message.equalsIgnoreCase("username is exists")) {
                Bundle bundle = new Bundle();
                bundle.putString(Consts.PARAMConsts.USERNAME, Consts.USER_GG_ID);
                bundle.putString(Consts.PARAMConsts.PASSWORD, Consts.USER_GG_ID);
                bundle.putString(Consts.PARAMConsts.PROVIDER, "google");
                mRequestManager.request(Actions.LOGIN, bundle, mLoginWithGoogleRequestSuccessListener, mLoginWithGoogleRequestErrorListener);
            } else {
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Response.Listener<UserResource> mLoginWithGoogleRequestSuccessListener = new Response.Listener<UserResource>() {
        @Override
        public void onResponse(UserResource response) {
            dismissProgress();
            Log.e("SplashActivity", "mLoginWithGoogleRequestSuccessListener!");
            Intent i = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(i);
            finish();
        }
    };

    private RequestErrorListener mLoginWithGoogleRequestErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            Log.e("SplashActivity", "mLoginWithGoogleRequestErrorListener!");
            dismissProgress();
            Log.e("SplashActivity", "status: " + status + ", code: " + code + ", message: " + message);

        }
    };


    private RequestErrorListener mGetListLibraryErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            Log.e("SplashActivity", "status: " + status + ", code: " + code + ", message: " + message);
            Intent i = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(i);
            finish();
        }
    };

    private Response.Listener<ListLibraryResource> mGetListLibrarySuccessListener = new Response.Listener<ListLibraryResource>() {
        @Override
        public void onResponse(ListLibraryResource response) {
            Log.e("SplashActivity", "mGetListLibrarySuccessListener");
            Intent i = new Intent(SplashActivity.this, MyActivity.class);
            startActivity(i);
            finish();
        }
    };
    private int totalLibraryDownlaod = 0;

    @Override
    public synchronized void onDownloadDone() {
        totalLibraryDownlaod++;
        if (totalLibraryDownlaod < listLibraryInstalled.size()) {
            new DownloadLibrary(listLibraryInstalled.get(totalLibraryDownlaod).getLibraryId(), this).execute();
        } else {
            if (listLibraryInstalled.size() == 1 && database.checkLibraryExists("54c2834530fd0f09005e69ce")) {
                mSharePrefs.saveCurrentLibrary("54c2834530fd0f09005e69ce");
            }
            mHandlerInstallLibrarySuccess.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissProgress();
                    Intent intent = new Intent(SplashActivity.this, MyActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }


    }


    public class DownloadLibrary extends AsyncTask<Void, Void, Void> {


        private DownloadLibraryListenner listenner;
        private String id;

        public DownloadLibrary(String id, DownloadLibraryListenner listenner) {
            this.listenner = listenner;
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bundle bundle = new Bundle();

            bundle.putString(Consts.PARAMConsts.KEY_LIBRARY_ID, id);
            bundle.putString(Consts.PARAMConsts.TOKEN, mSharePrefs.getToken());
            mRequestManager.request(Actions.GETLIBRARYINFO, bundle, mGetLibraryInfoSuccessListener, mGetLibraryInfoErrorListener);
            return null;
        }

    }


    private RequestErrorListener mGetLibraryInfoErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {

            Log.e("SplashActivity", " | mGetLibraryInfoErrorListener" + "status: " + status + ", code: " + code + ", message: " + message);
            onDownloadDone();

        }
    };

    private Response.Listener<LibraryInfosResource> mGetLibraryInfoSuccessListener = new Response.Listener<LibraryInfosResource>() {
        @Override
        public void onResponse(final LibraryInfosResource response) {

            Log.e("SplashActivity", " | mGetLibraryInfoSuccessListener ");
            final int sizeResponse = response.cards.length;

            if (!database.checkLibraryExists(response._id)) {
                Log.e("SplashActivity", " response size: " + sizeResponse);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < sizeResponse; i++) {
                            CardTable cardTable = new CardTable(response.cards[i].word.toString(), response.cards[i].phonetically.toString(), response.cards[i].type.toString(), response.cards[i].meanEng.toString(), response.cards[i].mean.toString(), response.cards[i].exam.toString(), response.cards[i].library.toString());
                            database.insertCard(cardTable);
                        }
                        LibraryTable libraryTable = new LibraryTable(response._id, response.name);
                        database.insertLibrary(libraryTable);
                        Log.e("SplashActivity", " insert data complete ");
                        mHandlerInstallLibrarySuccess.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response._id.equalsIgnoreCase("54c2834530fd0f09005e69ce")) {
                                    mSharePrefs.saveCurrentLibrary("54c2834530fd0f09005e69ce");
                                }
                                onDownloadDone();
                            }
                        });

                    }
                }).start();


            }
        }
    };

}
