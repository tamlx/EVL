package com.thanhle.englishvocabulary.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.dialog.ConfirmDialog.ConfirmDialogListener;

public class ConfirmDialog extends AppDialog<ConfirmDialogListener> {
	public static final String TAG = ConfirmDialog.class.getSimpleName();

	public interface ConfirmDialogListener {
		public void onOk(AppDialog<?> f);
	}

	public ConfirmDialog() {
		setCancelable(false);
	}

	public static ConfirmDialog newInstance(String message, String title, ConfirmDialogListener listener) {
		return newInstance(message, title, false, R.string.ok, listener);
	}

	public static ConfirmDialog newInstance(String message, String title,
			boolean cancelable, int buttonTextResId, ConfirmDialogListener listener) {
		ConfirmDialog dialog = new ConfirmDialog();
        dialog.setListener(listener);
		Bundle args = new Bundle();
		args.putInt(EXTRA_BUTTON_TEXT, buttonTextResId);
		args.putString(EXTRA_MESSAGE, message);
		args.putString(EXTRA_TITLE, title);
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
		String message = null, title = null;
		boolean cancelable = false;
		int buttonText = R.string.ok;
		if (bundle != null) {
			buttonText = bundle.getInt(EXTRA_BUTTON_TEXT);
			message = bundle.getString(EXTRA_MESSAGE);
			title = bundle.getString(EXTRA_TITLE);
			cancelable = bundle.getBoolean(EXTRA_CANCELABLE);
		}
		setCancelable(cancelable);
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity())
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(buttonText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (getListener() != null)
									getListener().onOk(ConfirmDialog.this);
							}
						}).setCancelable(cancelable);

		return builder.create();
	}
}
