package com.thanhle.englishvocabulary.dialog;

import android.app.Dialog;
import android.os.Bundle;

import com.thanhle.englishvocabulary.dialog.ConfirmDialog.ConfirmDialogListener;

public class ProgressDialog extends AppDialog<ConfirmDialogListener> {
	public static final String TAG = ProgressDialog.class.getSimpleName();
	private static final String EXTRA_CANCELABLE = "EXTRA_CANCELABLE";
	protected android.app.ProgressDialog mProgressDialog;

	public ProgressDialog() {
		setCancelable(false);
	}

	public static ProgressDialog newInstance(String message) {
		return newInstance(message, false);
	}

	public static ProgressDialog newInstance(String message, boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog();
		Bundle args = new Bundle();
		args.putString(EXTRA_MESSAGE, message);
		args.putBoolean(EXTRA_CANCELABLE, cancelable);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	protected boolean isListenerOptional() {
		return true;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		String message = null;
		boolean cancelable = false;
		if (bundle != null) {
			message = bundle.getString(EXTRA_MESSAGE);
			cancelable = bundle.getBoolean(EXTRA_CANCELABLE);
		}
		setCancelable(cancelable);
		mProgressDialog = new android.app.ProgressDialog(getActivity());
		mProgressDialog.setMessage(message);
		return mProgressDialog;
	}
}
