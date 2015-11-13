package com.thanhle.englishvocabulary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.thanhle.englishvocabulary.resource.UserResource;

public class SharePrefs {

    public static final String DEFAULT_BLANK = "";
    /**
     * Keys for saving data to shareprefs
     */
    private static final String PREF_TOKEN = "token";
    private static final String PREF_CURRENT_TEST_POSISTION = "current_test_position";
    private static final String PREF_CARD_BACKGROUND = "card_theme";
    private static final String PREF_LANGUAGE = "language";
    private static final String PREF_GUIDE_STEP = "guide_step";
    private static final String PREF_CURRENT_LIBRARY = "current_library";
    private static final String PREF_COUNT_APP_RUN = "count_app_run";
    private static final String PREF_SHOW_RATE_ME = "show_rate_me";
    private static final String PREF_SHOW_ALERT_PICK_CARD = "show_alert_pick_card";
    private static final String PREF_WEAR_SYNC = "wear_sync";
    private static final String PREF_NOTIFICATION_NEW_WORD = "notifications_new_word";
    private static final String PREF_NOTIFICATION_WEAR = "notifications_wear";
    private static final String PREF_IS_PURCHASED = "is_purchased";
    private static final String PREF_IS_CHECK_PURCHASED = "is_check_purchased";
    private static final String PREF_USER_INFO = "user_info";


    private static SharePrefs instance = new SharePrefs();
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public static SharePrefs getInstance() {
        return instance;
    }

    public void init(Context ctx) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(ctx);
        }
    }

    public void clear() {
        // clear all
        sharedPreferences.edit().clear().commit();
    }

    public void removeUserInfo() {
        //remove user info
        sharedPreferences.edit().remove(PREF_USER_INFO).commit();
    }

    public void save(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public void save(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public String get(String key, String _default) {
        return sharedPreferences.getString(key, _default);
    }

    public int get(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public void save(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean get(String key, boolean _default) {
        return sharedPreferences.getBoolean(key, _default);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(PREF_TOKEN, token).commit();
    }

    public String getToken() {
        return sharedPreferences.getString(PREF_TOKEN, DEFAULT_BLANK);
    }

    public void saveCurrentTestPosition(int position) {
        String library = getCurrentLibrary();
        sharedPreferences.edit().putInt(PREF_CURRENT_TEST_POSISTION + "." + library, position)
                .commit();
    }

    public int getCurrentTestPosition() {
        String library = getCurrentLibrary();
        return sharedPreferences.getInt(PREF_CURRENT_TEST_POSISTION + "." + library, 0);
    }

    public int getCardTheme() {
        return Integer.valueOf(sharedPreferences.getString(PREF_CARD_BACKGROUND, "0"));
    }

    public void saveLanguage(String language) {
        sharedPreferences.edit().putString(PREF_LANGUAGE, language).commit();
    }

    public String getLanguage() {
        return sharedPreferences.getString(PREF_LANGUAGE, "vi_VN");
    }

    public void saveGuideStep(String activity, int step) {
        sharedPreferences.edit().putInt(PREF_GUIDE_STEP + "_" + activity, step)
                .commit();
    }

    public int getGuideStep(String activity) {
        return sharedPreferences.getInt(PREF_GUIDE_STEP + "_" + activity, 0);
    }

    public void saveCurrentLibrary(String library) {
        sharedPreferences.edit().putString(PREF_CURRENT_LIBRARY, library)
                .commit();
    }

    public String getCurrentLibrary() {
        return sharedPreferences.getString(PREF_CURRENT_LIBRARY,
                Consts.DEFAULT_LIBRARY);
    }

    public void saveCountAppRun(int count) {
        sharedPreferences.edit().putInt(PREF_COUNT_APP_RUN, count).commit();
    }

    public int getCountAppRun() {
        return sharedPreferences.getInt(PREF_COUNT_APP_RUN, 0);
    }

    public void saveShowRateMe(boolean showed) {
        sharedPreferences.edit().putBoolean(PREF_SHOW_RATE_ME, showed).commit();
    }

    public boolean getShowRateMe() {
        return sharedPreferences.getBoolean(PREF_SHOW_RATE_ME, true);
    }

    public void saveShowAlertPickCard(boolean showed) {
        sharedPreferences.edit().putBoolean(PREF_SHOW_ALERT_PICK_CARD, showed)
                .commit();
    }

    public boolean getShowAlertPickCard() {
        return sharedPreferences.getBoolean(PREF_SHOW_ALERT_PICK_CARD, true);
    }

    public void saveIsWearSync(boolean sync) {
        sharedPreferences.edit().putBoolean(PREF_WEAR_SYNC, sync).commit();
    }

    public boolean getIsWearSync() {
        return sharedPreferences.getBoolean(PREF_WEAR_SYNC, false);
    }

    public boolean getIsNotificationNewWord() {
        return sharedPreferences.getBoolean(PREF_NOTIFICATION_NEW_WORD, true);
    }

    public boolean getIsNotificationWear() {
        return sharedPreferences.getBoolean(PREF_NOTIFICATION_WEAR, true);
    }

    public void saveIsPurchased(boolean purchased) {
        sharedPreferences.edit().putBoolean(PREF_IS_PURCHASED, purchased).commit();
    }

    public boolean getIsPurchased() {
        return sharedPreferences.getBoolean(PREF_IS_PURCHASED, false);
    }

    public void saveIsCheckPurchased(boolean check) {
        sharedPreferences.edit().putBoolean(PREF_IS_CHECK_PURCHASED, check).commit();
    }

    public boolean getIsCheckPurchased() {
        return sharedPreferences.getBoolean(PREF_IS_CHECK_PURCHASED, false);
    }

    /**
     * Lưu thông tin user
     *
     * @param user
     */
    public void saveUserInfo(UserResource user) {
        String json = gson.toJson(user);
        sharedPreferences.edit().putString(PREF_USER_INFO, json).commit();
    }

    /**
     * Lấy thông tin user
     *
     * @return
     */
    public UserResource getUserInfo() {
        String json = sharedPreferences.getString(PREF_USER_INFO, DEFAULT_BLANK);
        Log.d("abc", json);
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            UserResource user = gson.fromJson(json, UserResource.class);
            return user;
        }
    }
}
