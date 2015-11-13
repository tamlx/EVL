package com.thanhle.englishvocabulary.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.dialog.RateMeDialog.RateMeDialogListener;

public class RateMeDialog extends AppDialog<RateMeDialogListener> {
	public static final String TAG = RateMeDialog.class.getSimpleName();

	public interface RateMeDialogListener {
		public void onRateYes(RateMeDialog f);

		public void onRateNo(RateMeDialog f);

		public void onRateCancel(RateMeDialog f);
	}

	public RateMeDialog() {
		setCancelable(false);
	}

	public static RateMeDialog newInstance(RateMeDialogListener listener) {
		RateMeDialog dialog = new RateMeDialog();
        dialog.setListener(listener);
		return dialog;
	}

	@Override
	protected boolean isListenerOptional() {
		return false;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity())
				.setTitle(R.string.rate_dialog_title)
				.setMessage(R.string.rate_dialog_message)
				.setPositiveButton(R.string.rate_dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (getListener() != null)
									getListener().onRateCancel(
											RateMeDialog.this);
							}
						})
				.setNegativeButton(R.string.rate_dialog_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (getListener() != null)
									getListener().onRateYes(RateMeDialog.this);
							}
						})
				.setNeutralButton(R.string.rate_dialog_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (getListener() != null)
									getListener().onRateNo(RateMeDialog.this);
							}
						}).setCancelable(false);

		return builder.create();
	}
}