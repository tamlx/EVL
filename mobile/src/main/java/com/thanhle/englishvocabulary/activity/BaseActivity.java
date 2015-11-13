package com.thanhle.englishvocabulary.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thanhle.englishvocabulary.MyApplication;
import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.DatabaseHandler;
import com.thanhle.englishvocabulary.dialog.ProgressDialog;
import com.thanhle.englishvocabulary.requestmanagement.RequestManager;
import com.thanhle.englishvocabulary.utils.SharePrefs;
import com.thanhle.englishvocabulary.utils.TTS;

public class BaseActivity extends ActionBarActivity {
    protected DatabaseHandler database;
    protected SharePrefs mSharePrefs = SharePrefs.getInstance();
    protected RequestManager mRequestManager = RequestManager.getInstance();
    protected TTS tts = TTS.getInstance();
    protected Toast exitToast;
    /**
     * The view to show the ad.
     */
    private AdView adView;
    protected Toast mToastCenter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new DatabaseHandler(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // init tracker screen name
        Tracker t = MyApplication.getTracker();
        if (t != null) {
            // Set screen name.
            t.setScreenName(this.getClass().getName());
            // Send a screen view.
            t.send(new HitBuilders.AppViewBuilder().build());
        }
        exitToast = Toast.makeText(this, "Press back again to quit", Toast.LENGTH_SHORT);
    }

    protected void initAdBanner(String adUnitId) {
        if (mSharePrefs.getIsPurchased()) return;
        LinearLayout layout = (LinearLayout) findViewById(R.id.viewAd);
        if (layout != null) {
            adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(adUnitId);
            // Add the AdView to the view hierarchy. The view will have no size
            // until the ad is loaded.
            layout.removeAllViews();
            layout.addView(adView);
            // Create an ad request. Check logcat output for the hashed device
            // ID to
            // get test ads on a physical device.
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(
                    AdRequest.DEVICE_ID_EMULATOR).build();

            // Start loading the ad in the background.
            adView.loadAd(adRequest);
        }
    }

    public void initAdBanner(View root, String adUnitId) {
        if (mSharePrefs.getIsPurchased()) return;
        LinearLayout layout = (LinearLayout) root.findViewById(R.id.viewAd);
        if (layout != null) {
            adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(adUnitId);
            // Add the AdView to the view hierarchy. The view will have no size
            // until the ad is loaded.
            layout.removeAllViews();
            layout.addView(adView);
            // Create an ad request. Check logcat output for the hashed device
            // ID to
            // get test ads on a physical device.
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(
                    AdRequest.DEVICE_ID_EMULATOR).build();

            // Start loading the ad in the background.
            adView.loadAd(adRequest);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /**
     * Called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        // Destroy the AdView.
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a toast in the center of screen. Fix: not show if toast is showed
     *
     * @param message message to be shown
     */
    public void showCenterToast(String message) {
        // check if toast not init
        if (mToastCenter == null) {
            mToastCenter = Toast.makeText(this, message, Toast.LENGTH_LONG);
            mToastCenter.setGravity(Gravity.CENTER, 0, 0);
            mToastCenter.show();
            return;
        }
        // set new message
        mToastCenter.setText(message);
        // Check if toast is NOT shown, show again
        if (!mToastCenter.getView().isShown()) {
            mToastCenter.show();
        }
    }

    /**
     * show center toast
     *
     * @param resId message resource id
     */
    public void showCenterToast(int resId) {
        showCenterToast(getString(resId));
    }

    /**
     * Show progress dialog with indeterminate loading icon, message and
     * cancelable
     *
     * @param message
     */
    public void showProgress(String message) {
        // Show progress dialog if it is null or not showing.
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.newInstance(message);
        }
        if (!mProgressDialog.isShowing() && !this.isFinishing()) {
            mProgressDialog.show(getSupportFragmentManager(), null);

        }
    }

    public void dismissProgress() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public boolean isConnectInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isVisible()) {
            display();
        } else {
            finish();
        }
    }


    private boolean isVisible() {
        return (exitToast.getView().getWindowVisibility() != View.VISIBLE);
    }

    private void display() {

        exitToast.show();
    }
}
