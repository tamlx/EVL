package com.thanhle.englishvocabulary.utils;

import com.thanhle.englishvocabulary.view.ProgressWheel;

public class ProgressAnimationRunnable implements Runnable {
	private static final int MAX_DELAY_ANIMATION = 1000;
	private ProgressWheel progress;
	private float reach;
	private float d = 1;
	private int value;
	private int max;
	private int delay;
	private int v;
	private boolean showPercent;
	private ProgressAnimationListener listener;

	public ProgressAnimationRunnable(ProgressWheel progress, int value,
			int max, ProgressAnimationListener listener) {
		init(progress, 0, value, max, false, listener);
	}

	public ProgressAnimationRunnable(ProgressWheel progress, int from,
			int value, int max, boolean showPercent,
			ProgressAnimationListener listener) {
		init(progress, from, value, max, showPercent, listener);
	}

	public void init(ProgressWheel progress, int from, int value, int max,
			boolean showPercent, ProgressAnimationListener listener) {
		this.listener = listener;
		this.progress = progress;
		this.max = max;
		this.value = from + value;
		this.reach = (float) this.value * 360 / max;
		this.showPercent = showPercent;
		if (reach < value) {
			this.reach = (float) this.value;
			this.d = (float) max / 360;
			this.max = 360;
		}
		this.delay = (int) (MAX_DELAY_ANIMATION / this.reach);
		this.v = from;
	}

	@Override
	public void run() {
		while (v <= reach) {
			showValue(false);
			v++;
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		showValue(true);
		if (listener != null)
			listener.onProgressAnimationFinish();
	}

	private void showValue(boolean last) {
		String text = "";
		if (!showPercent) {
			if (last) {
				text = String.valueOf(value);
			} else {
				text = String.valueOf(v * max / 360);
			}
		} else {
			text = String.valueOf((int) ((v / d) / 360 * 100)) + "%";
		}
		progress.setText(text);
		progress.setProgress((int) (v / d));
	}

	public interface ProgressAnimationListener {
		public void onProgressAnimationFinish();
	}
}
