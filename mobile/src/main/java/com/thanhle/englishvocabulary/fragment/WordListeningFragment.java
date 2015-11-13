package com.thanhle.englishvocabulary.fragment;

import android.view.View;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.utils.DataUtils;
import com.thanhle.englishvocabulary.utils.SharePrefs;

public class WordListeningFragment extends WordIdentityFragment {
	private boolean isAnswerMean = false;
	private static final int DELAY_SPEAK_WORD = 300;

	@Override
	protected void showTestDescription() {
		if (SharePrefs.getInstance().getLanguage().startsWith("en")) {
			tts.speak(getString(R.string.test_part_2));
		}
		// start description text animation
		tvPosition.runTypingAnimation(getString(R.string.test_part_2));
	}

	/*
	 * Override gotoWord function to hide word, speak word
	 * 
	 * @see
	 * com.thanhle.vocablearning.fragment.WordIdentityFragment#gotoWord(int)
	 */
	@Override
	protected void gotoWord(int pos) {
		String word = mListWord.get(pos).word;
		CardTable card = database
				.getCard(mSharePrefs.getCurrentLibrary(), word);
		tvPosition.runTypingAnimation(getString(R.string.question_position,
				pos + 1));

		if (card != null) {
			if (!isResume()) {
				// reset clicking state
				mAnswerClicking = false;
				// random show word or mean
				isAnswerMean = DataUtils.getRandomBoolean();
				// speak word
				mHandler.postDelayed(mSpeakWordRunnable, DELAY_SPEAK_WORD);
				// random correct answer position
				mCurrentCorrectPos = DataUtils.getRandomNumber(4, 0) - 1;
				// random answer from other card
				mAnotherAnswer = DataUtils.get3RandomCard(database,
						mSharePrefs.getCurrentLibrary(), mListWord, card);
				// start timer
				startTimer(tvTimer);
			}
			// set word ?
			tvWord.setText("?");
			btnSpeak.setVisibility(View.VISIBLE);

			int idx = 0;
			for (int i = 0; i < ANSWER_COUNT; i++) {
				// reset view state
				rlAnswer[i].setEnabled(true);
				rlAnswer[i].setSelected(false);
				// tag is answer position
				rlAnswer[i].setTag(i);

				if (i == mCurrentCorrectPos) {
					// correct position
					if (isAnswerMean) {
						tvAnswer[i].setText(card.mean);
					} else {
						tvAnswer[i].setText(card.word);
					}
				} else {
					if (mAnotherAnswer[idx] != null) {
						if (isAnswerMean) {
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

	@Override
	protected void doAfterShowAnswer() {
		// show word after choose answer
		String word = mListWord.get(mCurrentWord).word;
		tvWord.setText(word);
	}

	private Runnable mSpeakWordRunnable = new Runnable() {

		@Override
		public void run() {
			String word = mListWord.get(mCurrentWord).word;
			tts.speak(word);
		}
	};
}
