package com.thanhle.englishvocabulary.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.activity.BaseActivity;
import com.thanhle.englishvocabulary.activity.TestActivity;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.DataUtils;
import com.thanhle.englishvocabulary.utils.SharePrefs;
import com.thanhle.englishvocabulary.view.TextViewAutoResizeSingleline;
import com.thanhle.englishvocabulary.view.TypingTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class WordWritingFragment extends TestFragment implements
        OnClickListener {
    protected static final int LETTER_LINE = 2;
    protected static int LETTER_PER_LINE = 5;
    protected static final int MAX_CHANGE = 5;

    protected static final int DELAY_CHANGE_ANSWER = 1000;
    protected static final int DELAY_CHANGE_ANSWER_END_TIME = 2000;
    protected static final int DELAY_BLINK = 150;
    protected static final int DELAY_REMOVE_BLINK = 1000;
    private static final int DELAY_SPEAK_WORD = 300;
    protected TextView tvWord, tvQuestion, tvChange;
    protected TypingTextView tvPosition;
    protected View btnSpeak;
    protected TextViewAutoResizeSingleline[] tvLetter;
    protected LinearLayout[] rlLetter = new LinearLayout[LETTER_LINE];
    protected int mCurrentLetter = 0;
    protected Handler mHandler = new Handler();
    protected BlinkRunnable mBlinkRunnable;
    protected boolean mAnswerClicking = false;
    protected boolean mIsQuestionMean = false;
    protected int mChangeLeft = MAX_CHANGE;
    protected ArrayList<Character> mLetters;

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
        View root = inflater.inflate(R.layout.fragment_word_writing, container,
                false);

        // init ads
        ((BaseActivity) getActivity()).initAdBanner(root,
                Consts.ADS_TEST_ACTIVITY);

        // find view
        tvWord = (TextView) root.findViewById(R.id.tvWord);
        tvTimer = (TextView) root.findViewById(R.id.tvTimer);
        tvQuestion = (TextView) root.findViewById(R.id.tvQuestion);
        tvPosition = (TypingTextView) root.findViewById(R.id.tvPosition);
        tvChange = (TextView) root.findViewById(R.id.tvChange);
        btnSpeak = root.findViewById(R.id.btnSpeak);
        rlLetter[0] = (LinearLayout) root.findViewById(R.id.rlLetter1);
        rlLetter[1] = (LinearLayout) root.findViewById(R.id.rlLetter2);

        for (int i = 0; i < mListWord.size(); i++) {
            if (mListWord.get(i).word.length() > 10 && mListWord.get(i).word.length() < 13) {
                switch (mListWord.get(i).word.length() % 2) {
                    case 0:
                        rlLetter[0].setWeightSum(Float.valueOf(mListWord.get(i).word.length() / 2));
                        rlLetter[1].setWeightSum(Float.valueOf(mListWord.get(i).word.length() / 2));
                        LETTER_PER_LINE = (int) mListWord.get(i).word.length() / 2;
                        break;
                    case 1:
                        rlLetter[0].setWeightSum(Float.valueOf((mListWord.get(i).word.length() + 1) / 2));
                        rlLetter[1].setWeightSum(Float.valueOf((mListWord.get(i).word.length() + 1) / 2));
                        LETTER_PER_LINE = (int) (mListWord.get(i).word.length() + 1) / 2;
                        break;
                }
                break;
            }
        }
        tvLetter = new TextViewAutoResizeSingleline[LETTER_LINE
                * LETTER_PER_LINE];
        tvWord.setOnClickListener(this);
        btnSpeak.setOnClickListener(this);

        addLettersView();

        // show first word
        gotoWord(mCurrentWord);

        showTestDescription();

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // load saved game
            mCurrentLetter = savedInstanceState.getInt(EXTRA_CURRENT_LETTER);
            mChangeLeft = savedInstanceState.getInt(EXTRA_CHANGE_LEFT);
            mIsQuestionMean = savedInstanceState
                    .getBoolean(EXTRA_QUESTION_MEAN);
            mAnswerClicking = savedInstanceState
                    .getBoolean(EXTRA_ANSWER_CLICKING);
            char[] letters = savedInstanceState
                    .getCharArray(EXTRA_ANOTHER_ANSWER);
            mLetters = new ArrayList<Character>();
            for (int i = 0; i < letters.length; i++) {
                mLetters.add(letters[i]);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // saved game
        outState.putInt(EXTRA_CURRENT_LETTER, mCurrentLetter);
        outState.putInt(EXTRA_CHANGE_LEFT, mChangeLeft);
        outState.putBoolean(EXTRA_QUESTION_MEAN, mIsQuestionMean);
        outState.putBoolean(EXTRA_ANSWER_CLICKING, mAnswerClicking);
        char[] letters = new char[mLetters.size()];
        for (int i = 0; i < mLetters.size(); i++) {
            letters[i] = mLetters.get(i);
        }
        outState.putCharArray(EXTRA_ANOTHER_ANSWER, letters);
    }

    @Override
    public void onResumeAfterPause() {
        super.onResumeAfterPause();
        if (isResume()) {
            // start timer
            startTimer(tvTimer);
        }
    }

    protected void showTestDescription() {
        if (SharePrefs.getInstance().getLanguage().startsWith("en")) {
            tts.speak(getString(R.string.test_part_3));
        }
        // start description text animation
        tvPosition.runTypingAnimation(getString(R.string.test_part_3));
    }

    @Override
    public void resetTest() {
        super.resetTest();
        mCurrentWord = 0;
        mAnswerClicking = false;
        gotoWord(mCurrentWord);
    }

    /**
     * programmatically add letter view
     */
    private void addLettersView() {
        int letterTextColor = getResources().getColor(R.color.white);
        int margin = getResources().getDimensionPixelSize(
                R.dimen.wordwriting_letter_margin);
        int padding = getResources().getDimensionPixelSize(
                R.dimen.wordwriting_letter_padding);
        int textSize = getResources().getDimensionPixelSize(
                R.dimen.wordwriting_letter_text_size);
        // create LayoutParam for letter text view
        LayoutParams lp;
        for (int i = 0; i < LETTER_LINE; i++) {
            // generate 10 letter TextView
            for (int j = 0; j < LETTER_PER_LINE; j++) {
                int idx = i * LETTER_PER_LINE + j;
                tvLetter[idx] = new TextViewAutoResizeSingleline(getActivity());
                if (j == (LETTER_PER_LINE - 1)) {
                    lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                    lp.weight = 1;
                    lp.setMargins(0, 0, 0, 0);
                } else {
                    lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                    lp.weight = 1;
                    lp.setMargins(0, 0, margin, 0);
                }
                tvLetter[idx].setLayoutParams(lp);
                tvLetter[idx].setPadding(padding, padding, padding, padding);
                tvLetter[idx].setBackgroundResource(R.drawable.letter_frame);
                tvLetter[idx].setTextColor(letterTextColor);
                tvLetter[idx].setGravity(Gravity.CENTER);
                tvLetter[idx].setMaxTextSize(textSize);
                tvLetter[idx].setTypeface(null, Typeface.BOLD);
                tvLetter[idx].setOnClickListener(this);
                rlLetter[i].addView(tvLetter[idx]);
            }
        }
    }

    /**
     * show card intent into view
     *
     * @param pos
     */
    protected void gotoWord(int pos) {
        String word = mListWordSplitSpecial.get(pos).toLowerCase(Locale.US);
        Log.e("Word", word);
        CardTable card = database
                .getCard(mSharePrefs.getCurrentLibrary(), mListWord.get(pos).word);

        Log.e("mListWord", mListWord.get(pos).word);
        tvPosition.runTypingAnimation(getString(R.string.question_position,
                pos + 1));

        if (card != null) {
            int i;
            int totalLetter = LETTER_LINE * LETTER_PER_LINE;
            // resume state skip it
            if (!isResume()) {
                // reset letter position
                mCurrentLetter = 0;
                // reset clicking state
                mAnswerClicking = false;
                // reset change text
                mChangeLeft = MAX_CHANGE;
                // random show word or mean
                mIsQuestionMean = DataUtils.getRandomBoolean();

                // random letter
                mLetters = new ArrayList<Character>();
                for (i = 0; i < word.length(); i++) {
                    if (!mLetters.contains(word.charAt(i))) {
                        mLetters.add(word.charAt(i));
                    }
                }
                char value = 97;
                for (i = mLetters.size(); i < totalLetter; i++) {
                    // random 25 letter, and not exists in letters list
                    while (mLetters.contains((value = (char) (DataUtils
                            .getRandomNumber(26) + 97))))
                        ;
                    mLetters.add((char) value);
                }
                Log.e("mLetters", mLetters.toString());
                // random sort
                long seed = System.nanoTime();
                Collections.shuffle(mLetters, new Random(seed));

                // set word is _ _ _
                String[] chars = new String[mListWord.get(pos).word.length()];
                Arrays.fill(chars, "_");

                for (int j = 0; j < mListWord.get(pos).word.length(); j++) {
                    if (String.valueOf(mListWord.get(pos).word.charAt(j)).equalsIgnoreCase("-")) {
                        chars[j] = "-";
                    }
                    if (String.valueOf(mListWord.get(pos).word.charAt(j)).equalsIgnoreCase(" ")) {
                        chars[j] = " ";
                    }
                }

                tvWord.setText(TextUtils.join(" ", chars));

                // start timer
                startTimer(tvTimer);
            } else {
                // show saved letters
                doAfterAnswer();
            }
            tvChange.setText(getString(R.string.change, mChangeLeft));

            if (mIsQuestionMean) {
                // set question
                tvQuestion.setVisibility(View.VISIBLE);
                btnSpeak.setVisibility(View.GONE);
                tvQuestion.setText(card.mean);
            } else {
                tvQuestion.setVisibility(View.GONE);
                btnSpeak.setVisibility(View.VISIBLE);
                // speak word
                mHandler.postDelayed(mSpeakWordRunnable, DELAY_SPEAK_WORD);
            }

            // set text to letter view
            for (i = 0; i < mLetters.size() && i < totalLetter; i++) {
                tvLetter[i].setEnabled(true);
                tvLetter[i].setSelected(false);
                tvLetter[i].setText(String.valueOf(mLetters.get(i))
                        .toUpperCase(Locale.US));
            }
        }
    }

    protected void doAfterAnswer() {
        String word = mListWord.get(mCurrentWord).word;
        // set word is _ _ _
        String[] chars = new String[word.length()];
        Arrays.fill(chars, "_");

        for (int j = 0; j < word.length(); j++) {
            if (String.valueOf(word.charAt(j)).equalsIgnoreCase("-")) {
                chars[j] = "-";
            }
            if (String.valueOf(word.charAt(j)).equalsIgnoreCase(" ")) {
                chars[j] = " ";
            }
        }

        // open correct letter
        for (int i = 0; i < mCurrentLetter; i++) {
            if (String.valueOf(word.charAt(i)).equalsIgnoreCase("-")) {
                chars[i] = "-";
                chars[i + 1] = String.valueOf(word.charAt(i + 1));
            } else if (String.valueOf(word.charAt(i)).equalsIgnoreCase(" ")) {
                chars[i] = " ";
                chars[i + 1] = String.valueOf(word.charAt(i + 1));
            } else {
                chars[i] = String.valueOf(word.charAt(i));
            }
        }
        tvWord.setText(TextUtils.join(" ", chars));
    }

    protected void doBeforeChangeWord() {
        String word = mListWord.get(mCurrentWord).word;
        tvWord.setText(word);
        if (mIsQuestionMean) {
            // speak word
            tts.speak(word);
        } else {
            // show meaning
            CardTable card = database.getCard(mSharePrefs.getCurrentLibrary(),
                    word);
            tvQuestion.setVisibility(View.VISIBLE);
            btnSpeak.setVisibility(View.GONE);
            tvQuestion.setText(card.mean);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mBlinkRunnable);
        mHandler.removeCallbacks(mChangeWordRunnable);
        mHandler.removeCallbacks(mSpeakWordRunnable);
    }

    ;

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
            default:
                if (v instanceof TextViewAutoResizeSingleline && !mAnswerClicking) {
                    String word = mListWord.get(mCurrentWord).word
                            .toUpperCase(Locale.US);
                    if (mCurrentLetter >= word.length()) return;
                    // change clicking state
                    if (String.valueOf(word.charAt(mCurrentLetter)).equalsIgnoreCase("-") || String.valueOf(word.charAt(mCurrentLetter)).equalsIgnoreCase(" ")) {
                        mCurrentLetter++;
                    }
                    mAnswerClicking = true;
                    boolean correct;
                    char letter = ((TextViewAutoResizeSingleline) v).getText().charAt(0);
                    correct = (word.charAt(mCurrentLetter) == letter);

                    // blink current answer
                    if (mBlinkRunnable != null)
                        mHandler.removeCallbacks(mBlinkRunnable);
                    if (correct) {
                        mCurrentLetter++;
                        mBlinkRunnable = new BlinkRunnable(v, true);
                        doAfterAnswer();
                    } else {
                        // play wrong answer sound
                        playWrongAnswer();
                        mChangeLeft--;
                        tvChange.setText(getString(R.string.change, mChangeLeft));
                        mBlinkRunnable = new BlinkRunnable(v, false);
                    }
                    mHandler.post(mBlinkRunnable);
                    mHandler.postDelayed(new RemoveBlinkRunnable(v),
                            DELAY_REMOVE_BLINK);

                    if (mChangeLeft == 0) {
                        // stop timer
                        stopTimer();

                        // wrong, change left = 0
                        ((TestActivity) getActivity()).addIncorrectWord(mListWordSplitSpecial
                                .get(mCurrentWord));
                        doBeforeChangeWord();
                        // post change word after 1s
                        mHandler.postDelayed(mChangeWordRunnable,
                                DELAY_CHANGE_ANSWER);
                    } else if (mCurrentLetter == word.length()) {
                        // stop timer
                        stopTimer();
                        // play right answer
                        playRightAnswer();

                        // end of word
                        ((TestActivity) getActivity()).addCorrectWord(mListWordSplitSpecial
                                .get(mCurrentWord));
                        doBeforeChangeWord();
                        // post change word after 1s
                        mHandler.postDelayed(mChangeWordRunnable,
                                DELAY_CHANGE_ANSWER);
                    }
                    mAnswerClicking = false;
                }
                break;
        }
    }

    @Override
    protected void onEndTime() {
        // disable answer click
        mAnswerClicking = true;
        // play wrong answer sound
        playWrongAnswer();
        doBeforeChangeWord();
        // post change word after 1s
        mHandler.postDelayed(mChangeWordRunnable, DELAY_CHANGE_ANSWER_END_TIME);
    }

    private class BlinkRunnable implements Runnable {
        private final View v;
        private final boolean correct;
        private boolean blink;

        public BlinkRunnable(View v, boolean correct) {
            this.v = v;
            this.correct = correct;
        }

        @Override
        public void run() {
            if (correct) {
                if (blink) {
                    v.setSelected(true);
                } else {
                    v.setSelected(false);
                }
            } else {
                if (blink) {
                    v.setEnabled(true);
                } else {
                    v.setEnabled(false);
                }
            }
            blink = !blink;
            mHandler.postDelayed(this, DELAY_BLINK);
        }
    }

    private class RemoveBlinkRunnable implements Runnable {
        private final View v;

        public RemoveBlinkRunnable(View v) {
            this.v = v;
        }

        @Override
        public void run() {
            if (mBlinkRunnable != null)
                mHandler.removeCallbacks(mBlinkRunnable);
            v.setEnabled(true);
            v.setSelected(false);
        }
    }
}
