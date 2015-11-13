package com.thanhle.englishvocabulary.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AppDialog<Listener> extends DialogFragment {
	protected static final String EXTRA_BUTTON_TEXT = "button_text";
	protected static final String EXTRA_MESSAGE = "message";
	protected static final String EXTRA_TITLE = "title";
	protected static final String EXTRA_CANCELABLE = "EXTRA_CANCELABLE";
    protected Listener listener;

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener=listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public boolean isShowing() {
        final Dialog dialog = getDialog();
        return dialog != null && dialog.isShowing();
    }

    protected boolean isListenerOptional() {
        return true;
    }

    // Hack for android issue 17423 in the compatibility library
    @Override
    public void onDestroyView() {
        if ( getDialog() != null && getRetainInstance() )
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}