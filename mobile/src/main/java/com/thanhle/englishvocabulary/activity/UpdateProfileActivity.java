package com.thanhle.englishvocabulary.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.squareup.picasso.Picasso;
import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.adapter.ListWordLearnAdapter;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.requestmanagement.RequestErrorListener;
import com.thanhle.englishvocabulary.resource.UserResource;
import com.thanhle.englishvocabulary.resource.WordLearnResource;
import com.thanhle.englishvocabulary.utils.Actions;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.view.circluarimageview.CircularImageView;
import com.thanhle.englishvocabulary.view.circluarimageview.RoundTransform;

import java.util.ArrayList;

public class UpdateProfileActivity extends BaseActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private Handler mHandler = new Handler();
    private TextView btnDone, tv_userfullname, tv_change_password, tv_logout;
    private ArrayList<WordLearnResource> list = new ArrayList<WordLearnResource>();
    private ListView lv;
    private ListWordLearnAdapter listWordLearnAdapter;
    private ImageButton imb_back;
//    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setUpToolbar();
        String url = "";
        CircularImageView circularImageView = (CircularImageView) findViewById(R.id.imgUserAvatar);
        if (mSharePrefs.getUserInfo().provider.equalsIgnoreCase("google")) {
            url = convertLinkImage(mSharePrefs.get(Consts.LINK_PICTURE, "http://imgur.com/Q54I5IC.png"));
        } else {
            url = mSharePrefs.get(Consts.LINK_PICTURE, "http://i.imgur.com/Q54I5IC.png");
        }
        Picasso.with(this).load(url).placeholder(R.drawable.img_icon_user_avatar).transform(new RoundTransform()).into(circularImageView);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        tv_userfullname = (TextView) findViewById(R.id.tv_userfullname_updateprofile);
        tv_change_password = (TextView) findViewById(R.id.tv_change_password_updateprofile);
        tv_logout = (TextView) findViewById(R.id.btnLogout);
        tv_change_password.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        imb_back = (ImageButton) findViewById(R.id.btn_back_updateprofile);
        imb_back.setOnClickListener(this);

        if (!mSharePrefs.getUserInfo().provider.equalsIgnoreCase("vole")) {
            tv_change_password.setVisibility(View.GONE);
        }

        UserResource userResource = mSharePrefs.getUserInfo();

        if (userResource.fullname != null && !userResource.fullname.equalsIgnoreCase("")) {
            tv_userfullname.setText(userResource.fullname);
        } else {
            tv_userfullname.setText(userResource.username);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<LibraryTable> libraries = database.getListMyLibrary();
                for (int i = 0; i < libraries.size(); i++) {
                    WordLearnResource wordLearnResource = new WordLearnResource();

                    int wordLearn = database.countWordLearn(libraries.get(i).code);
                    int wordForget = database.countWordForget(libraries.get(i).code);
                    int wordTotal = database.countCard(libraries.get(i).code);

                    wordLearnResource.setLibraryName(libraries.get(i).name);
                    wordLearnResource.setWordLearn("" + wordLearn);
                    wordLearnResource.setWordForget("" + wordForget);
                    wordLearnResource.setTotalWord("" + wordTotal);
                    list.add(wordLearnResource);

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onLoadDataComplete();
                    }
                });


            }
        }).start();

    }

    private void onLoadDataComplete() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv = (ListView) findViewById(R.id.lv_library_updateprofile);
                listWordLearnAdapter = new ListWordLearnAdapter(UpdateProfileActivity.this, list);
                lv.setAdapter(listWordLearnAdapter);
            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_change_password_updateprofile:
                showDialogChangeProfile(getString(R.string.update_profile_dialog_title));
                break;
            case R.id.btnLogout:
                mSharePrefs.clear();
                database.clearAllWords();
                database.clearAllCards();
                database.clearAllLibrary();
                finish();
                Intent i = new Intent(UpdateProfileActivity.this, SplashActivity.class);
                startActivity(i);
                break;
            case R.id.btn_back_updateprofile:
                finish();
                break;
        }
    }

    private Response.Listener<UserResource> mUpdateProfileSuccessListener = new Response.Listener<UserResource>() {
        @Override
        public void onResponse(UserResource response) {
            dismissProgress();
            Toast.makeText(UpdateProfileActivity.this, "Update Profile Success", Toast.LENGTH_SHORT).show();
            Log.e("UserDrawerFragment: ", "Update Profile Success " + response.fullname);
//            UserDrawerFragment.setTvUsername(response.fullname);
            if (response.fullname != null) {
                tv_userfullname.setText(response.fullname);
            } else {
                tv_userfullname.setText("");
            }
        }
    };

    private RequestErrorListener mUpdateProfileErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            dismissProgress();
            if (message != null && !"".equalsIgnoreCase(message))
                Toast.makeText(UpdateProfileActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };


    private void showDialogChangeProfile(String title) {
        final View view = getLayoutInflater().inflate(R.layout.layout_user_change_profile, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(
                this)
                .setTitle(title);

        builder.setView(view);
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                    }
                })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        }).setCancelable(false);

        final AlertDialog changePassDialog = builder.create();
        changePassDialog.show();

        changePassDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt_old_pass = (EditText) view.findViewById(R.id.tvOldPassword);
                EditText edt_new_pass = (EditText) view.findViewById(R.id.tvNewPassword);
                EditText edt_confirm_pass = (EditText) view.findViewById(R.id.tvConfirmPassword);


                if (!TextUtils.isEmpty(edt_old_pass.getText())
                        && !TextUtils.isEmpty(edt_new_pass.getText())
                        && !TextUtils.isEmpty(edt_confirm_pass.getText())) {
                    if (edt_new_pass.getText().toString().equals(edt_confirm_pass.getText().toString())) {
                        String token = mSharePrefs.getToken();
                        String email = mSharePrefs.getUserInfo().email;
                        String fullname = mSharePrefs.getUserInfo().fullname;

                        Bundle data = new Bundle();
                        data.putString(Consts.PARAMConsts.FULLNAME, fullname);
                        data.putString(Consts.PARAMConsts.TOKEN, token);
                        data.putString(Consts.PARAMConsts.EMAIL, email);
                        data.putString(Consts.PARAMConsts.PASSWORD, edt_new_pass.getText().toString());
                        data.putString(Consts.PARAMConsts.OLDPASSWORD, edt_old_pass.getText().toString());
                        showProgress(getString(R.string.processing));
                        mRequestManager.request(Actions.UPDATEPROFILE, data, mUpdateProfileSuccessListener, mUpdateProfileErrorListener);
                        edt_old_pass.setText("");
                        edt_new_pass.setText("");
                        edt_confirm_pass.setText("");
                        changePassDialog.dismiss();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "The password and confirmation password do not match.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (TextUtils.isEmpty(edt_old_pass.getText())) {
                        edt_old_pass.setError("Invalid value");
                        edt_old_pass.requestFocus();
                    } else if (TextUtils.isEmpty(edt_new_pass.getText())) {
                        edt_new_pass.setError("Invalid value");
                        edt_new_pass.requestFocus();
                    } else if (TextUtils.isEmpty(edt_confirm_pass.getText())) {
                        edt_confirm_pass.setError("Invalid value");
                        edt_confirm_pass.requestFocus();
                    }
                }

            }
        });

        changePassDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassDialog.dismiss();
            }
        });
    }

    private void setUpToolbar() {
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        if (mToolbar != null) {
//            mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//            mToolbar.setTitle(getString(R.string.update_profile_title));
//            mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
//            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//        }
    }

    private String convertLinkImage(String link) {

        String[] items = link.split("=");
        String link_ouput = "";


        return link_ouput = items[0] + "=128";
    }
}
