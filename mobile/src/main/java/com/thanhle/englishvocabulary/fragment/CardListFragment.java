package com.thanhle.englishvocabulary.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.utils.FlipAnimation;
import com.thanhle.englishvocabulary.utils.TTS;
import com.thanhle.englishvocabulary.view.CardView;
import com.thanhle.englishvocabulary.view.CardView.CardMoveListener;

import java.util.ArrayList;

public abstract class CardListFragment extends BaseFragment implements
		OnClickListener, CardMoveListener {
	protected static final int ANIMATION_UP = 1;
	protected static final int ANIMATION_DOWN = 2;
	protected static final int ANIMATION_TOUCH = 3;
	protected static final int ANIMATION_RIGHT = 4;
	protected TTS tts = TTS.getInstance();
	protected ArrayList<CardTable> mListCard;
	protected int mCardIndex = 0;
	protected CardView mCardView;
	protected Animation mFlipUpAnimation, mFlipDownAnimation;

    @Override
    public void onResume() {
        super.onResume();
        // change saved background
        int background = mSharePrefs.getCardTheme();
        refreshBackground(background);
    }

	@Override
	public void onClick(View v) {
	}

	private void refreshBackground(int pos) {
        if (mRoot==null) return;
		View view1 = mRoot.findViewById(R.id.cardViewFake1);
		View view2 = mRoot.findViewById(R.id.cardViewFake2);
		int resId;
		switch (pos) {
		case 0:
			resId = R.drawable.img_card_background;
			break;
		case 1:
			resId = R.drawable.img_card1_background;
			break;
		case 2:
			resId = R.drawable.img_card2_background;
			break;
		case 3:
			resId = R.drawable.img_card3_background;
			break;
		case 4:
			resId = R.drawable.img_card4_background;
			break;
		case 5:
			resId = R.drawable.img_card5_background;
			break;
		case 6:
			resId = R.drawable.img_card6_background;
			break;
		case 7:
			resId = R.drawable.img_card7_background;
			break;
		default:
			resId = R.drawable.img_card_background;
			break;
		}
		if (view1 != null)
			view1.setBackgroundResource(resId);
		if (view2 != null)
			view2.setBackgroundResource(resId);
		if (mCardView != null)
			mCardView.setCardBackground(resId);
	}

	/**
	 * load card list data
	 */
    public abstract void loadData();

	protected void initAnimation() {
		mFlipUpAnimation = new FlipAnimation(0, 270, 0, 0, false);
		mFlipUpAnimation.setDuration(300);
		mFlipUpAnimation.setInterpolator(new AccelerateInterpolator());
		mFlipUpAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mCardIndex++;
				// move to end card, move back to first card
				if (mCardIndex == mListCard.size()) {
					mCardIndex = 0;
				}
				mCardView.fillCardData(mListCard.get(mCardIndex), true);
				endCardAnimation(ANIMATION_UP);
			}
		});
		mFlipDownAnimation = new FlipAnimation(270, 0, 0, 0, false);
		mFlipDownAnimation.setDuration(300);
		mFlipDownAnimation.setInterpolator(new AccelerateInterpolator());
		mFlipDownAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mCardIndex--;
				// move to end card, move back to first card
				if (mCardIndex < 0) {
					mCardIndex = mListCard.size() - 1;
				}
				mCardView.fillCardData(mListCard.get(mCardIndex), true);
				endCardAnimation(ANIMATION_DOWN);
			}
		});
	}

	@Override
	public void cardMoveUp(CardView view) {
		if (mListCard.size() > 0) {
			// find card body (view inside)
			View rlCardBody = view.getCurrentFaceView().findViewById(
					R.id.rlCardBody);
			// start animation only card body
			rlCardBody.startAnimation(mFlipUpAnimation);
		}
	}

	@Override
	public void cardMoveDown(CardView view) {
		if (mListCard.size() > 0) {
			// find card body (view inside)
			View rlCardBody = view.getCurrentFaceView().findViewById(
					R.id.rlCardBody);
			// start animation only card body
			rlCardBody.startAnimation(mFlipDownAnimation);
		}
	}

	@Override
	public void cardMoveLeft(CardView view) {
	}

	@Override
	public void cardMoveRight(CardView view) {
	}

	@Override
	public void cardTouch(CardView view) {
	}

	protected void endCardAnimation(int animationType) {
	}
}
