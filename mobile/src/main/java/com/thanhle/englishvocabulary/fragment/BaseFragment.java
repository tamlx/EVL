package com.thanhle.englishvocabulary.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.thanhle.englishvocabulary.activity.BaseActivity;
import com.thanhle.englishvocabulary.database.DatabaseHandler;
import com.thanhle.englishvocabulary.utils.SharePrefs;

/**
 * Created by thanhle on 9/21/2014.
 */
public class BaseFragment extends Fragment {
    protected SharePrefs mSharePrefs = SharePrefs.getInstance();
    protected View mRoot;
    protected DatabaseHandler database;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        database = new DatabaseHandler(activity);
    }

    protected void showProgress(String message) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showProgress(message);
        }
    }

    protected void dismissProgress() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).dismissProgress();
        }
    }

    protected void showCenterToast(String message) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showCenterToast(message);
        }
    }

    protected void showCenterToast(int resId) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showCenterToast(resId);
        }
    }

}
