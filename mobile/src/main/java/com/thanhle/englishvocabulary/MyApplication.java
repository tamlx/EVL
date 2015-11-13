package com.thanhle.englishvocabulary;

import android.app.Application;
import android.os.Handler;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thanhle.englishvocabulary.requestmanagement.RequestManager;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.SharePrefs;
import com.thanhle.englishvocabulary.utils.TTS;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.File;
import java.util.Locale;


@ReportsCrashes(

        // This is required for backward compatibility but not used
//        mailTo = "ndxuantam10@gmail.com",
        formUri = "http://www.bugsense.com/api/acra?api_key=1269a7fe",

        //show notification
        mode = ReportingInteractionMode.NOTIFICATION,
//        resToastText = R.string.crash_toast_text // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resNotifTickerText = R.string.crash_notif_ticker_text,
        resNotifTitle = R.string.crash_notif_title,
        resNotifText = R.string.crash_notif_text,
        resNotifIcon = android.R.drawable.stat_notify_error,
//
//        //show dialog with user comment
////        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
//        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast

        //Choosing which fields to be included in reports
//        mailTo = "ndxuantam10@gmail.com",
//        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
//        mode = ReportingInteractionMode.TOAST,
//        resToastText = R.string.crash_toast_text


)


public class MyApplication extends Application {
    private static Tracker mTracker;

    @Override
    public void onCreate() {

        super.onCreate();

//        Mint.initAndStartSession(this, "1269a7fe");

        SharePrefs.getInstance().init(this);

        RequestManager.getInstance().init(getApplicationContext());

        changeLang();

        // init google analytics
        initializeGa();

        TTS.getInstance().setHandler(new Handler());

        checkDataFolder();

        ACRA.init(this);


    }

    private void checkDataFolder() {
        // check dictionary folder
        File f = new File(getCacheDir(), Consts.DICT_FOLDER_NAME);
        if (!f.exists())
            f.mkdirs();

        // check extract folder
        f = new File(getCacheDir(), Consts.EXTRACT_FOLDER_NAME);
        // check and make dir
        if (!f.exists())
            f.mkdirs();

        // check voice folder
        f = new File(getCacheDir(), Consts.VOICE_FOLDER_NAME);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public void changeLang() {
        Locale myLocale = new Locale(SharePrefs.getInstance().getLanguage());
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void initializeGa() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        mTracker = analytics.newTracker(R.xml.app_tracker);
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Application")
                .setAction("start app").build());
    }

    public static Tracker getTracker() {
        return mTracker;
    }
}
