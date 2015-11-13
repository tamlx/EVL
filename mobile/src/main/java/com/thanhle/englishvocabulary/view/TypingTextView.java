package com.thanhle.englishvocabulary.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class TypingTextView extends TextView {
	private static final int DELAY_BLINK_DASH = 500;
	private static final int TYPING_SPEECH = 60;
	private static final int TYPING_WAIT_SPEECH = 800;
	private Thread mThread;
	private String mCurrentText = "";
	private String mTypingText;
	private int mCurrentPos = 0;
	private boolean mBlinked = false;
	private TypingAnimationListener mListener;
	private boolean mStartAnimation = false;
	private int mSpeed = TYPING_SPEECH;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				setText(mCurrentText);
			} else if (msg.what == 1) {
				if (mListener != null)
					mListener.onTypingAnimationEnd();
			}
		}
	};

	public TypingTextView(Context context) {
		super(context);
	}

	public TypingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TypingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSpeed(int speed) {
		mSpeed = speed;
	}

	/**
	 * run typing animation with custom text
	 * 
	 * @param text
	 */
	public void runTypingAnimation(String text) {
		mCurrentText = "";
		this.mTypingText = text;
		mCurrentPos = 0;
		mBlinked = false;
		if (mThread == null) {
			mThread = new Thread(new TypingAnimationRunnable());
			mThread.start();
		}
	}

	/**
	 * run typing animation with current text of text view
	 */
	public void runTypingAnimation() {
		runTypingAnimation((String) getText());
		setText("");
	}

	public void setTypingAnimationListener(TypingAnimationListener listener) {
		mListener = listener;
	}

	private class TypingAnimationRunnable implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (mCurrentPos < mTypingText.length()) {
					mStartAnimation = true;
					mCurrentPos++;
					mCurrentText = mTypingText.subSequence(0, mCurrentPos)
							+ " _";
					mHandler.sendEmptyMessage(0);
					try {
						if (mCurrentPos < 0)
							mCurrentPos = 1;
						if (mTypingText.charAt(mCurrentPos - 1) == ':') {
							Thread.sleep(TYPING_WAIT_SPEECH);
						} else {
							Thread.sleep(mSpeed);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				} else {
					if (mStartAnimation && mListener != null) {
						mHandler.sendEmptyMessage(1);
					}
					mStartAnimation = false;
					if (mBlinked) {
						mCurrentText = mTypingText + "  ";
						mHandler.sendEmptyMessage(0);
					} else {
						mCurrentText = mTypingText + " _";
						mHandler.sendEmptyMessage(0);
					}
					mBlinked = !mBlinked;
					try {
						Thread.sleep(DELAY_BLINK_DASH);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public interface TypingAnimationListener {
		public void onTypingAnimationEnd();
	}
}
