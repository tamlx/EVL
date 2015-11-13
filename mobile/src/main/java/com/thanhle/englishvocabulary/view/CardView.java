package com.thanhle.englishvocabulary.view;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.utils.TTS;

/**
 * @author thanh.lecaominh
 */
public class CardView extends FlipView implements OnGestureListener,
        OnClickListener, OnDoubleTapListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 200;
    private static final int SWIPE_THRESHOLD_VELOCITY = 120;
    private TextView tvWord, tvPhonetically, tvType, tvMeanEng, tvMean, tvExam;
    private GestureDetectorCompat mDetector;
    private CardMoveListener mListener;
    private TTS tts = TTS.getInstance();
    private CardTable currentCard;
    private OnChangeCardCallback mCallback;

    public CardView(Context context) {
        super(context);
        init();
    }

    public CardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * init card, add front view and back view
     */
    private void init() {
        if (!isInEditMode()) {
            // front face
            RelativeLayout ff = (RelativeLayout) View.inflate(getContext(),
                    R.layout.layout_card_front, null);
            // back face
            RelativeLayout bf = (RelativeLayout) View.inflate(getContext(),
                    R.layout.layout_card_back, null);
            // find all view
            tvWord = (TextView) ff.findViewById(R.id.tvCardWord);
            tvPhonetically = (TextView) ff
                    .findViewById(R.id.tvCardPhonetically);
            tvType = (TextView) ff.findViewById(R.id.tvCardWordType);
            tvMeanEng = (TextView) ff.findViewById(R.id.tvCardMeanEng);
            tvMean = (TextView) bf.findViewById(R.id.tvCardMean);
            tvExam = (TextView) bf.findViewById(R.id.tvCardExam);

            // set on click to speak
//            tvWord.setOnClickListener(this);
//            tvMeanEng.setOnClickListener(this);
//            tvExam.setOnClickListener(this);
            ff.findViewById(R.id.btnCardSpeak).setOnClickListener(this);

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
        if (card == null) return;
        currentCard = card;
        tvWord.setText(card.word);
        tvPhonetically.setText(card.phonetically);
        tvType.setText(card.type);
        tvMeanEng.setText(card.meanEng);
        tvMean.setText(card.mean);
        tvExam.setText(card.exam);
        if (flipToFront)
            flipToFront();
        if (mCallback != null)
            mCallback.onChangeCard(currentCard);
    }

    public void fillCardData(CardTable card) {
        fillCardData(card, false);
    }

    public void setCallback(OnChangeCardCallback callback) {
        mCallback = callback;
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
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                // vertical swipe distance is more than horizontal swipe

                // swipe from bottom to top
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    if (mListener != null)
                        mListener.cardMoveUp(this);
                    return false;
                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    if (mListener != null)
                        mListener.cardMoveDown(this);
                    return false;
                }
            } else {
                // swipe from right to left
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (mListener != null)
                        mListener.cardMoveLeft(this);
                    return false;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (mListener != null)
                        mListener.cardMoveRight(this);
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return true;
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
        if (mListener != null) mListener.cardTouch(this);
        return false;
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
                if (m_IsFrontFacing) {
                    if (currentCard != null && currentCard.word != null)
                        tts.speak(currentCard.word);
                }
                break;
            case R.id.tvCardMeanEng:
            case R.id.tvCardExam:
                if (m_IsFrontFacing) {
                    tts.speak(currentCard.meanEng);
                } else {
                    tts.speak(currentCard.exam);
                }
                break;
            default:
                break;
        }
    }

    public interface OnChangeCardCallback {
        public void onChangeCard(CardTable card);
    }
}