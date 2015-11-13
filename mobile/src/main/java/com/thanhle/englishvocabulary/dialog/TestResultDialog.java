package com.thanhle.englishvocabulary.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.dialog.TestResultDialog.TestResultDialogListener;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.ProgressAnimationRunnable;
import com.thanhle.englishvocabulary.view.ProgressWheel;

public class TestResultDialog extends AppDialog<TestResultDialogListener> {
	public static final String TAG = TestResultDialog.class.getSimpleName();
	private static final String EXTRA_FROM = "extra_from";
	private static final String EXTRA_VALUE = "extra_value";
	private static final String EXTRA_MAX = "extra_max";

	public interface TestResultDialogListener {
		public void onTestResultDialogOk(AppDialog<?> f, boolean next);
	}

	public TestResultDialog() {
		setCancelable(false);
	}

	public static TestResultDialog newInstance(int from, int value, int max, TestResultDialogListener listener) {
		TestResultDialog dialog = new TestResultDialog();
        dialog.setListener(listener);
		Bundle args = new Bundle();
		args.putInt(EXTRA_FROM, from);
		args.putInt(EXTRA_VALUE, value);
		args.putInt(EXTRA_MAX, max);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	protected boolean isListenerOptional() {
		return true;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		int from = 0, value = 0, max = 0;
		String buttonText, message;
		final boolean nextTest;
		if (bundle != null) {
			value = bundle.getInt(EXTRA_VALUE);
			from = bundle.getInt(EXTRA_FROM);
			max = bundle.getInt(EXTRA_MAX);
		}
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.layout_result_dialog, null);
		if (from + value == max * Consts.MAX_TESTING) {
			nextTest = true;
			buttonText = getString(R.string.test_done);
			message = getString(R.string.test_done_status);
		} else if (value == max) {
			nextTest = true;
			buttonText = getString(R.string.test_next);
			message = getString(R.string.test_next_status);
		} else {
			nextTest = false;
			buttonText = getString(R.string.test_again);
			message = getResources().getQuantityString(
					R.plurals.test_again_status, max - value, max - value);
		}
		ProgressWheel progress1 = (ProgressWheel) view
				.findViewById(R.id.progressBar1);
		ProgressWheel progress2 = (ProgressWheel) view
				.findViewById(R.id.progressBar2);
		TextView tvStatus = (TextView) view.findViewById(R.id.tvProgressBar1);
		tvStatus.setText(message);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity())
				.setView(view)
				.setTitle(R.string.test_result)
				.setPositiveButton(buttonText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (getListener() != null)
									getListener().onTestResultDialogOk(
											TestResultDialog.this, nextTest);
							}
						}).setCancelable(false);

		// run animation
		new Thread(new ProgressAnimationRunnable(progress1, from, value, max
				* Consts.MAX_TESTING, true, null)).start();
		new Thread(new ProgressAnimationRunnable(progress2, 0, value, max, true, null))
				.start();
		return builder.create();
	}
}
