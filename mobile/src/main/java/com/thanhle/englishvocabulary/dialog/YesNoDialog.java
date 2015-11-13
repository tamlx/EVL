package com.thanhle.englishvocabulary.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.dialog.YesNoDialog.YesNoDialogListener;

public class YesNoDialog extends AppDialog<YesNoDialogListener> {
	public static final String TAG = YesNoDialog.class.getSimpleName();

	public interface YesNoDialogListener {
		public void onDialogYes(AppDialog<?> f);

		public void onDialogNo(AppDialog<?> f);
	}

	public YesNoDialog() {
		setCancelable(false);
	}

	public static YesNoDialog newInstance(String message, String title, YesNoDialogListener listener) {
		YesNoDialog dialog = new YesNoDialog();
        dialog.setListener(listener);
		Bundle args = new Bundle();
		args.putString(AppDialog.EXTRA_MESSAGE, message);
		args.putString(AppDialog.EXTRA_TITLE, title);
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
		if (bundle != null) {
			message = bundle.getString(EXTRA_MESSAGE);
			title = bundle.getString(EXTRA_TITLE);
		}
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity())
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (getListener() != null)
									getListener().onDialogYes(YesNoDialog.this);
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (getListener() != null)
									getListener().onDialogNo(YesNoDialog.this);
							}
						}).setCancelable(false);

		return builder.create();
	}
}
