package com.thanhle.englishvocabulary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author thanh.lecaominh
 *
 */
public class CardView extends FlipView implements OnGestureListener,
		OnDoubleTapListener, OnClickListener {
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 80;
	private static final int SWIPE_THRESHOLD_VELOCITY = 80;
	private TextView tvWord, tvPhonetically, tvType, tvMean;
	private GestureDetectorCompat mDetector;
	private CardMoveListener mListener;
	private CardTable currentCard;
    private int cardType;

	public CardView(Context context) {
		super(context);
		init();
	}

	public CardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        parseAttributes(context.obtainStyledAttributes(attrs,
                R.styleable.CardView));
		init();
	}

	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
        parseAttributes(context.obtainStyledAttributes(attrs,
                R.styleable.CardView));
		init();
	}

    /**
     * Parse the attributes passed to the view from the XML
     *
     * @param a the attributes to parse
     */
    private void parseAttributes(TypedArray a) {
        cardType = (int) a.getInt(R.styleable.CardView_type,0);
        a.recycle();
    }

	/**
	 * init card, add front view and back view
	 */
	private void init() {
		if (!isInEditMode()) {
			// front face
            int resFront = R.layout.layout_card_front;
            int resBack = R.layout.layout_card_back;
            if (cardType==1) {
                resFront = R.layout.layout_card_front_round;
                resBack = R.layout.layout_card_back_round;
            }
			RelativeLayout ff = (RelativeLayout) View.inflate(getContext(),
                    resFront, null);
			// back face
			RelativeLayout bf = (RelativeLayout) View.inflate(getContext(),
                    resBack, null);
			// find all view
			tvWord = (TextView) ff.findViewById(R.id.tvCardWord);
			tvPhonetically = (TextView) ff
					.findViewById(R.id.tvCardPhonetically);
			tvType = (TextView) ff.findViewById(R.id.tvCardWordType);
			tvMean = (TextView) bf.findViewById(R.id.tvCardMean);
			ff.findViewById(R.id.btnCardSpeak).setOnClickListener(this);

			// set on click to speak
			// tvWord.setOnClickListener(this);
			// tvMeanEng.setOnClickListener(this);
			// tvExam.setOnClickListener(this);

			// set front, back view
			setFrontFace(ff);
			setBackFace(bf);
		}

		// Instantiate the gesture detector with the
		// application context and an implementation of
		// GestureDetector.OnGestureListener
		mDetector = new GestureDetectorCompat(getContext(), this);
		// Set the gesture detector as the double tap
		// listener.
		mDetector.setOnDoubleTapListener(this);
	}

	/**
	 * change front face background
	 *
	 * @param resId
	 */
	public void setCardBackground(int resId) {
		View rlCardBody = getFrontFace().findViewById(R.id.rlCardBody);
		rlCardBody.setBackgroundResource(resId);
	}

	public void setOnCardMoveListener(CardMoveListener listener) {
		mListener = listener;
	}

	/**
	 * fill information into card
	 *
	 * @param card
	 */
	public void fillCardData(CardTable card, boolean flipToFront) {
		currentCard = card;
		tvWord.setText(card.word);
		tvPhonetically.setText(card.phonetically);
		tvType.setText(card.type);
		tvMean.setText(card.mean);
		if (flipToFront)
			flipToFront();
	}

	public void fillCardData(CardTable card) {
		fillCardData(card, false);
	}

	/**
	 * flip to front face, skip animation
	 */
	public void flipToFront() {
		if (!m_IsFrontFacing) {
			applyRotation(0, -90, 0);
			m_IsFrontFacing = !m_IsFrontFacing;
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.d("abc", "onFling");
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
				// vertical swipe distance is more than horizontal swipe

				// swipe from bottom to top
				if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					if (mListener != null)
						mListener.cardMoveUp(this);
				} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					if (mListener != null)
						mListener.cardMoveDown(this);
				}
				return false;
			} else {
				// swipe from right to left
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (mListener != null)
						mListener.cardMoveLeft(this);
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (mListener != null)
						mListener.cardMoveRight(this);
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent event) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent event) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		flip();
		if (mListener != null)
			mListener.cardTouch(this);
		return true;
	}

	public interface CardMoveListener {
		/**
		 * Override this method when card swipe from bottom to top
		 */
		public void cardMoveUp(CardView view);

		/**
		 * Override this method when card swipe from top to bottom
		 */
		public void cardMoveDown(CardView view);

		/**
		 * Override this method when card swipe from right to left
		 */
		public void cardMoveLeft(CardView view);

		/**
		 * Override this method when card swipe from left to right
		 */
		public void cardMoveRight(CardView view);

		/**
		 * Override this method when single touch to view
		 *
		 * @param view
		 */
		public void cardTouch(CardView view);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCardSpeak:
			// TODO speak
			break;
		// case R.id.tvCardMeanEng:
		// case R.id.tvCardExam:
		// if (m_IsFrontFacing) {
		// tts.speak(currentCard.meanEng);
		// } else {
		// tts.speak(currentCard.exam);
		// }
		// break;
		default:
			break;
		}
	}
}