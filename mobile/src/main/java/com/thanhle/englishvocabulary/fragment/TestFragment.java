package com.thanhle.englishvocabulary.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.activity.TestActivity;
import com.thanhle.englishvocabulary.database.DatabaseHandler;
import com.thanhle.englishvocabulary.database.tables.WordTable;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.SharePrefs;
import com.thanhle.englishvocabulary.utils.TTS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TestFragment extends Fragment {
    protected static final String EXTRA_WORD_POSITION = "EXTRA_WORD_POSITION";
    protected static final String EXTRA_TIMER = "EXTRA_TIMER";
    protected static final String EXTRA_ANSWER_SELECT = "EXTRA_ANSWER_SELECT";
    protected static final String EXTRA_ANOTHER_ANSWER = "EXTRA_ANOTHER_ANSWER";
    protected static final String EXTRA_CORRECT_POSITION = "EXTRA_CORRECT_POSITION";
    protected static final String EXTRA_QUESTION_MEAN = "EXTRA_QUESTION_MEAN";
    protected static final String EXTRA_ANSWER_CLICKING = "EXTRA_ANSWER_CLICKING";
    protected static final String EXTRA_CURRENT_LETTER = "EXTRA_CURRENT_LETTER";
    protected static final String EXTRA_CHANGE_LEFT = "EXTRA_CHANGE_LEFT";
    protected DatabaseHandler database;
    protected SharePrefs mSharePrefs = SharePrefs.getInstance();
    protected TTS tts = TTS.getInstance();
    protected ArrayList<WordTable> mListWord;
    protected ArrayList<String> mListWordSplitSpecial;
    protected int mCurrentWord = 0;
    protected TextView tvTimer;
    private MediaPlayer mSlowTickPlayer, mFastTickPlayer, mRightPlayer,
            mWrongPlayer;
    private int mSavedTime = Consts.MAX_TEST_TIMER;
    private int mTime = Consts.MAX_TEST_TIMER;
    private Handler mHandler = new Handler();
    private Runnable mTickRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning()) {
                mTime--;
                tvTimer.setText(String.valueOf(mTime));
                if (mTime == 0) {
                    // stop slow tick
                    if (mFastTickPlayer != null && mFastTickPlayer.isPlaying()) {
                        mFastTickPlayer.pause();
                    }
                    // reset timer
                    mTime = 0;
                    mSavedTime = Consts.MAX_TEST_TIMER;

                    // call end time event
                    onEndTime();
                } else {
                    if (mTime == Consts.MAX_TEST_TIMER_CHANGE) {
                        // stop slow tick
                        if (mSlowTickPlayer != null
                                && mSlowTickPlayer.isPlaying()) {
                            mSlowTickPlayer.pause();
                        }
                        if (mFastTickPlayer != null) {
                            mFastTickPlayer.start();
                        }

                        // change text color
                        tvTimer.setTextColor(getResources().getColor(
                                R.color.bright_red_darker));
                    }
                    mHandler.postDelayed(this, 1000);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHandler(getActivity());
        initSound();
        if (savedInstanceState != null) {
            mCurrentWord = savedInstanceState.getInt(EXTRA_WORD_POSITION, 0);
            mSavedTime = savedInstanceState.getInt(EXTRA_TIMER,
                    Consts.MAX_TEST_TIMER);
            mTime = mSavedTime;
        } else {
            mSavedTime = Consts.MAX_TEST_TIMER;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_WORD_POSITION, mCurrentWord);
        outState.putInt(EXTRA_TIMER, mTime);
    }

    private void initSound() {

        // init slow tick player
        try {
            mSlowTickPlayer = MediaPlayer
                    .create(getActivity(), R.raw.slow_tick);
            mSlowTickPlayer.setLooping(true);
            mSlowTickPlayer.prepare();
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }

        // init fast tick player
        try {
            mFastTickPlayer = MediaPlayer
                    .create(getActivity(), R.raw.fast_tick);
            mFastTickPlayer.setLooping(true);
            mFastTickPlayer.prepare();
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }

        // init right answer sound player
        try {
            mRightPlayer = MediaPlayer
                    .create(getActivity(), R.raw.right_answer);
            mRightPlayer.prepare();
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }

        // init right answer sound player
        try {
            mWrongPlayer = MediaPlayer
                    .create(getActivity(), R.raw.wrong_answer);
            mWrongPlayer.prepare();
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // remove tick runanble
        mHandler.removeCallbacks(mTickRunnable);

        if (mSlowTickPlayer != null && mSlowTickPlayer.isPlaying()) {
            mSlowTickPlayer.pause();
        }
        if (mFastTickPlayer != null && mFastTickPlayer.isPlaying()) {
            mFastTickPlayer.pause();
        }
        if (mRightPlayer != null && mRightPlayer.isPlaying()) {
            mRightPlayer.pause();
        }
        if (mWrongPlayer != null && mWrongPlayer.isPlaying()) {
            mWrongPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // release all player
        if (mSlowTickPlayer != null) {
            mSlowTickPlayer.release();
            mSlowTickPlayer = null;
        }

        if (mFastTickPlayer != null) {
            mFastTickPlayer.release();
            mFastTickPlayer = null;
        }

        if (mRightPlayer != null) {
            mRightPlayer.release();
            mRightPlayer = null;
        }

        if (mWrongPlayer != null) {
            mWrongPlayer.release();
            mWrongPlayer = null;
        }
    }

    /**
     * set word list data, must call this function after create new fragment
     *
     * @param list
     */
    public void setWordList(ArrayList<WordTable> list, boolean shuffle) {
        mListWord = list;
        mListWordSplitSpecial = new ArrayList<String>();
        int size = mListWord.size();
        for (int i = 0; i < size; i++) {
            String words2[] = mListWord.get(i).word.trim().split("-");
            String words[] = mListWord.get(i).word.trim().split(" ");

            if (words2.length > 1) {
                switch (words2.length) {
                    case 2:
                        mListWordSplitSpecial.add(words2[0] + words2[1]);
                        break;
                    case 3:
                        mListWordSplitSpecial.add(words2[0] + words2[1] + words2[2]);
                        break;
                    case 4:
                        mListWordSplitSpecial.add(words2[0] + words2[1] + words2[2] + words2[3]);
                        break;
                }
            } else if (words.length > 1) {
                switch (words.length) {
                    case 2:
                        mListWordSplitSpecial.add(words[0] + words[1]);
                        break;
                    case 3:
                        mListWordSplitSpecial.add(words[0] + words[1] + words[2]);
                        break;
                    case 4:
                        mListWordSplitSpecial.add(words[0] + words[1] + words[2] + words[3]);
                        break;
                }
            } else {
                mListWordSplitSpecial.add(mListWord.get(i).word);
            }
        }
        if (shuffle) {
            // random array list
            long seed = System.nanoTime();
            Collections.shuffle(mListWord, new Random(seed));
            Collections.shuffle(mListWordSplitSpecial, new Random(seed));
        }
    }

    public void resetTest() {
        // random array list
        long seed = System.nanoTime();
        Collections.shuffle(mListWord, new Random(seed));
        Collections.shuffle(mListWordSplitSpecial, new Random(seed));
    }

    protected void onEndTime() {
    }

    protected void onEndChangeWordAnimation() {
    }

    public void onResumeAfterPause() {
        if (!isResume()) {
            if (mTime > Consts.MAX_TEST_TIMER_CHANGE && mSlowTickPlayer != null) {
                mSlowTickPlayer.start();
            } else if (mFastTickPlayer != null) {
                mFastTickPlayer.start();
            }
            tvTimer.setText(String.valueOf(mTime));
            mHandler.postDelayed(mTickRunnable, 1000);
        }
    }

    protected void startTimer(TextView tvTimer) {
        this.tvTimer = tvTimer;
        // reset timer
        mTime = mSavedTime;
        // set text
        tvTimer.setText(String.valueOf(mTime));

        if (mTime > Consts.MAX_TEST_TIMER_CHANGE && mSlowTickPlayer != null) {
            // set white text color
            tvTimer.setTextColor(getResources().getColor(R.color.white));
            mSlowTickPlayer.start();
        } else if (mFastTickPlayer != null) {
            // change text color
            tvTimer.setTextColor(getResources().getColor(
                    R.color.bright_red_darker));
            mFastTickPlayer.start();
        }
        mHandler.postDelayed(mTickRunnable, 1000);
    }

    protected void stopTimer() {
        // reset timer
        mSavedTime = Consts.MAX_TEST_TIMER;

        // stop sound player
        if (mFastTickPlayer != null && mFastTickPlayer.isPlaying()) {
            mFastTickPlayer.pause();
        }
        if (mSlowTickPlayer != null && mSlowTickPlayer.isPlaying()) {
            mSlowTickPlayer.pause();
        }

        // remove timer runnable
        mHandler.removeCallbacks(mTickRunnable);
    }

    protected void playRightAnswer() {
        if (mRightPlayer != null)
            mRightPlayer.start();
    }

    protected void playWrongAnswer() {
        if (mWrongPlayer != null)
            mWrongPlayer.start();
    }

    protected boolean isResume() {
        if (getActivity() != null) {
            return ((TestActivity) getActivity()).isResume();
        }
        return false;
    }

    protected boolean isRunning() {
        if (getActivity() != null) {
            return ((TestActivity) getActivity()).isRunning();
        }
        return false;
    }
}
