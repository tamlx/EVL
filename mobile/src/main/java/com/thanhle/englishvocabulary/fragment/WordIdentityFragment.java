package com.thanhle.englishvocabulary.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.activity.BaseActivity;
import com.thanhle.englishvocabulary.activity.TestActivity;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.DataUtils;
import com.thanhle.englishvocabulary.utils.SharePrefs;
import com.thanhle.englishvocabulary.view.TypingTextView;

public class WordIdentityFragment extends TestFragment implements
		OnClickListener {
	protected static final int ANSWER_COUNT = 4;
	protected static final int DELAY_CHECK_ANSWER = 1000;
	protected static final int DELAY_CHANGE_ANSWER = 1000;
	protected static final int DELAY_CHANGE_ANSWER_END_TIME = 2000;
	protected static final int DELAY_SPEAK_WORD = 500;
	protected static final int DELAY_BLINK = 150;
	protected TextView tvWord;
	protected TypingTextView tvPosition;
	protected View btnSpeak;
	protected TextView[] tvAnswer = new TextView[ANSWER_COUNT];
	protected View[] rlAnswer = new View[ANSWER_COUNT];
	protected int mCurrentCorrectPos;
	protected Handler mHandler = new Handler();
	protected BlinkRunnable mBlinkRunnable;
	protected boolean mAnswerClicking = false;
	protected int mAnswerSelect;
	protected boolean mIsQuestionMean = false;
	protected CardTable[] mAnotherAnswer;

	private Runnable mSpeakWordRunnable = new Runnable() {

		@Override
		public void run() {
			if (isRunning()) {
				String word = mListWord.get(mCurrentWord).word;
				tts.speak(word);
			}
		}
	};

	private Runnable mChangeWordRunnable = new Runnable() {

		@Override
		public void run() {
			if (isRunning()) {
				// reset answer click
				mAnswerClicking = false;
				// remove blink runnable
				if (mBlinkRunnable != null) {
					mHandler.removeCallbacks(mBlinkRunnable);
				}
				mCurrentWord++;
				if (mCurrentWord < mListWord.size()) {
					gotoWord(mCurrentWord);
				} else {
					// change to next question type
					((TestActivity) getActivity()).checkResult();
				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_word_identity,
				container, false);
		
		// init ads
		((BaseActivity) getActivity()).initAdBanner(root, Consts.ADS_TEST_ACTIVITY);

		// find view
		tvWord = (TextView) root.findViewById(R.id.tvWord);
		tvTimer = (TextView) root.findViewById(R.id.tvTimer);
		tvPosition = (TypingTextView) root.findViewById(R.id.tvPosition);
		btnSpeak = root.findViewById(R.id.btnSpeak);
		rlAnswer[0] = root.findViewById(R.id.rlAnswer1);
		rlAnswer[1] = root.findViewById(R.id.rlAnswer2);
		rlAnswer[2] = root.findViewById(R.id.rlAnswer3);
		rlAnswer[3] = root.findViewById(R.id.rlAnswer4);

		tvWord.setOnClickListener(this);
		btnSpeak.setOnClickListener(this);
		for (int i = 0; i < ANSWER_COUNT; i++) {
			tvAnswer[i] = (TextView) rlAnswer[i].findViewById(R.id.tvAnswer);
			TextView tvAlphabet = (TextView) rlAnswer[i]
					.findViewById(R.id.tvAlphabet);
			tvAlphabet.setText(String.valueOf((char) (65 + i)));
			rlAnswer[i].setOnClickListener(this);
		}

		// show first word
		gotoWord(mCurrentWord);

		showTestDescription();

		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mAnotherAnswer = (CardTable[]) savedInstanceState
					.getParcelableArray(EXTRA_ANOTHER_ANSWER);
			mCurrentCorrectPos = savedInstanceState
					.getInt(EXTRA_CORRECT_POSITION);
			mAnswerSelect = savedInstanceState.getInt(EXTRA_ANSWER_SELECT);
			mIsQuestionMean = savedInstanceState
					.getBoolean(EXTRA_QUESTION_MEAN);
			mAnswerClicking = savedInstanceState
					.getBoolean(EXTRA_ANSWER_CLICKING);
		}
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArray(EXTRA_ANOTHER_ANSWER, mAnotherAnswer);
		outState.putInt(EXTRA_CORRECT_POSITION, mCurrentCorrectPos);
		outState.putInt(EXTRA_ANSWER_SELECT, mAnswerSelect);
		outState.putBoolean(EXTRA_QUESTION_MEAN, mIsQuestionMean);
		outState.putBoolean(EXTRA_ANSWER_CLICKING, mAnswerClicking);
	}

	@Override
	public void resetTest() {
		super.resetTest();
		mCurrentWord = 0;
		mAnswerClicking = false;
		gotoWord(mCurrentWord);
	}

	@Override
	public void onResumeAfterPause() {
		super.onResumeAfterPause();
		if (mAnswerClicking) {// show answer again;
			// post show answer
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mAnswerSelect == mCurrentCorrectPos) {
						playRightAnswer();
						// blink current answer
						mBlinkRunnable = new BlinkRunnable(
								rlAnswer[mAnswerSelect]);
					} else {
						playWrongAnswer();
						// set enable false to current answer (wrong answer)
						rlAnswer[mAnswerSelect].setEnabled(false);
						// blink correct answer
						mBlinkRunnable = new BlinkRunnable(
								rlAnswer[mCurrentCorrectPos]);
					}
					doAfterShowAnswer();

					// post blinking runnable
					mHandler.post(mBlinkRunnable);
					// post change word after 1s
					mHandler.postDelayed(mChangeWordRunnable,
							DELAY_CHANGE_ANSWER);
				}
			}, DELAY_CHECK_ANSWER);
		} else if (isResume()) {
			// start timer again if answer button has not clicked yet
			startTimer(tvTimer);
		}
	}

	protected void showTestDescription() {
		if (SharePrefs.getInstance().getLanguage().startsWith("en")) {
			tts.speak(getString(R.string.test_part_1));
		}
		// start description text animation
		tvPosition.runTypingAnimation(getString(R.string.test_part_1));
	}

	/**
	 * show card intent into view
	 * 
	 * @param pos
	 */
	protected void gotoWord(int pos) {
		String word = mListWord.get(pos).word;
		CardTable card = database
				.getCard(mSharePrefs.getCurrentLibrary(), word);
		tvPosition.runTypingAnimation(getString(R.string.question_position,
				pos + 1));

		if (card != null) {
			// resume state skip it
			if (!isResume()) {
				// reset clicking state
				mAnswerClicking = false;
				// random show word or mean
				mIsQuestionMean = DataUtils.getRandomBoolean();
				// random correct answer position
				mCurrentCorrectPos = DataUtils.getRandomNumber(4, 0) - 1;
				// random answer from other card
				mAnotherAnswer = DataUtils.get3RandomCard(database,
						mSharePrefs.getCurrentLibrary(), mListWord, card);
				// start timer
				startTimer(tvTimer);
			}

			if (!mIsQuestionMean) {
				// set word
				tvWord.setText(card.word);
				btnSpeak.setVisibility(View.VISIBLE);
			} else {
				tvWord.setText(card.mean);
				btnSpeak.setVisibility(View.GONE);
			}

			int idx = 0;
			for (int i = 0; i < ANSWER_COUNT; i++) {
				// reset view state
				rlAnswer[i].setEnabled(true);
				rlAnswer[i].setSelected(false);
				// tag is answer position
				rlAnswer[i].setTag(i);

				if (i == mCurrentCorrectPos) {
					// correct position
					if (!mIsQuestionMean) {
						tvAnswer[i].setText(card.mean);
					} else {
						tvAnswer[i].setText(card.word);
					}
				} else {
					// incorrect position, load from another answer
					if (mAnotherAnswer[idx] != null) {
						if (!mIsQuestionMean) {
							tvAnswer[i].setText(mAnotherAnswer[idx].mean);
						} else {
							tvAnswer[i].setText(mAnotherAnswer[idx].word);
						}
					}
					// increase answer index
					idx++;
				}
			}
		}
	}

	protected void doAfterShowAnswer() {
		// speak correct answer
		mHandler.postDelayed(mSpeakWordRunnable, DELAY_SPEAK_WORD);
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.tvWord:
		case R.id.btnSpeak:
			if (!mIsQuestionMean) {
				String word = mListWord.get(mCurrentWord).word;
				tts.speak(word);
			}
			break;
		case R.id.rlAnswer1:
		case R.id.rlAnswer2:
		case R.id.rlAnswer3:
		case R.id.rlAnswer4:
			if (!mAnswerClicking) {
				// change clicking state
				mAnswerClicking = true;
				// stop timer
				stopTimer();

				// change background to select
				v.setSelected(true);
				mAnswerSelect = (Integer) (v.getTag());
				final boolean correct = (mAnswerSelect == mCurrentCorrectPos);
				if (correct) {
					((TestActivity) getActivity()).addCorrectWord(mListWord
							.get(mCurrentWord).word);
				} else {
					((TestActivity) getActivity()).addIncorrectWord(mListWord
							.get(mCurrentWord).word);
				}
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (isRunning()) {
							if (correct) {
								playRightAnswer();
								// blink current answer
								mBlinkRunnable = new BlinkRunnable(v);
							} else {
								playWrongAnswer();
								// set enable false to current answer (wrong
								// answer)
								v.setEnabled(false);
								// blink correct answer
								mBlinkRunnable = new BlinkRunnable(
										rlAnswer[mCurrentCorrectPos]);
							}
							doAfterShowAnswer();

							// post blinking runnable
							mHandler.post(mBlinkRunnable);
							// post change word after 1s
							mHandler.postDelayed(mChangeWordRunnable,
									DELAY_CHANGE_ANSWER);
						}
					}
				}, DELAY_CHECK_ANSWER);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onEndTime() {
		// disable answer click
		mAnswerClicking = true;
		// play wrong answer sound
		playWrongAnswer();
		// blink correct answer
		mBlinkRunnable = new BlinkRunnable(rlAnswer[mCurrentCorrectPos]);
		doAfterShowAnswer();
		// post blinking runnable
		mHandler.post(mBlinkRunnable);
		// post change word after 1s
		mHandler.postDelayed(mChangeWordRunnable, DELAY_CHANGE_ANSWER_END_TIME);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mBlinkRunnable);
		mHandler.removeCallbacks(mChangeWordRunnable);
		mHandler.removeCallbacks(mSpeakWordRunnable);
	};

	private class BlinkRunnable implements Runnable {
		private final View v;
		private boolean blink;

		public BlinkRunnable(View v) {
			this.v = v;
		}

		@Override
		public void run() {
			if (blink) {
				v.setSelected(true);
			} else {
				v.setSelected(false);
			}
			blink = !blink;
			mHandler.postDelayed(this, DELAY_BLINK);
		}
	}
}
