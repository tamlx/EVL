package com.thanhle.englishvocabulary.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.tables.WordTable;
import com.thanhle.englishvocabulary.dialog.AppDialog;
import com.thanhle.englishvocabulary.dialog.ConfirmDialog;
import com.thanhle.englishvocabulary.dialog.ConfirmDialog.ConfirmDialogListener;
import com.thanhle.englishvocabulary.dialog.TestResultDialog;
import com.thanhle.englishvocabulary.dialog.TestResultDialog.TestResultDialogListener;
import com.thanhle.englishvocabulary.dialog.YesNoDialog;
import com.thanhle.englishvocabulary.dialog.YesNoDialog.YesNoDialogListener;
import com.thanhle.englishvocabulary.fragment.TestFragment;
import com.thanhle.englishvocabulary.fragment.WordIdentityFragment;
import com.thanhle.englishvocabulary.fragment.WordListeningFragment;
import com.thanhle.englishvocabulary.fragment.WordWritingFragment;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.DataUtils;

import java.util.ArrayList;

public class TestActivity extends BaseActivity implements
		ConfirmDialogListener, YesNoDialogListener, TestResultDialogListener {
	private static final int TEST_IDENTITY = 0;
	private static final int TEST_LISTENING = 1;
	private static final int TEST_WRITING = 2;
	private static final String DIALOG_TAG_RESUME = "DIALOG_TAG_RESUME";
	private static final String EXTRA_CORRECT_LIST = "EXTRA_CORRECT_LIST";
	private static final String EXTRA_INCORRECT_LIST = "EXTRA_INCORRECT_LIST";
	private static final String EXTRA_WORD_LIST = "EXTRA_WORD_LIST";
	private TestFragment mFragment;
	private ArrayList<WordTable> mListWord;
	private ArrayList<String> incorrectList = new ArrayList<String>();
	private ArrayList<String> correctList = new ArrayList<String>();
	private int mCurrentTestId = 0;
	private boolean mResume = false;
	private boolean mIsRunning = true;
	private ConfirmDialog mDialogResume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		mCurrentTestId = mSharePrefs.getCurrentTestPosition();
		if (savedInstanceState == null) {
			// generator word, prepare data for test
			generateWord();
			applyFragmentForTest();
		} else {
			mResume = true;
			correctList = savedInstanceState
					.getStringArrayList(EXTRA_CORRECT_LIST);
			incorrectList = savedInstanceState
					.getStringArrayList(EXTRA_INCORRECT_LIST);
			mListWord = savedInstanceState
					.getParcelableArrayList(EXTRA_WORD_LIST);
			mFragment = (TestFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragmentLayout);
			mDialogResume = (ConfirmDialog) getSupportFragmentManager()
					.findFragmentByTag(DIALOG_TAG_RESUME);
			mFragment.setWordList(mListWord, false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(EXTRA_WORD_LIST, mListWord);
		outState.putStringArrayList(EXTRA_CORRECT_LIST, correctList);
		outState.putStringArrayList(EXTRA_INCORRECT_LIST, incorrectList);
	}

	@Override
	public void onBackPressed() {
		YesNoDialog.newInstance(getString(R.string.msg_ask_exit_test), null, this)
				.show(getSupportFragmentManager(), null);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mIsRunning == false || mResume) {
			if (mDialogResume == null) {
				mDialogResume = ConfirmDialog.newInstance(null, null, false,
						R.string.resume_test,this);
			}
			if (!mDialogResume.isShowing() && !this.isFinishing()) {
				mDialogResume.show(getSupportFragmentManager(),
						DIALOG_TAG_RESUME);
			}
		} else {
			mIsRunning = true;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mResume = false;
		mIsRunning = false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initAdBanner(Consts.ADS_TEST_ACTIVITY);
	}

	/**
	 * prepare data for test
	 */
	private void generateWord() {
		mListWord = new ArrayList<WordTable>();
		// add testing word to word list
		DataUtils.checkAndGenerateNewTestingWord(database,
				mSharePrefs.getCurrentLibrary(), mListWord);

		// get list old and forget word if enough
		int oldWordCount = Consts.MAX_WORD_PER_TEST - mListWord.size();
		if (oldWordCount > 0) {
			ArrayList<WordTable> oldWords = database.getListOldWordForget(
					mSharePrefs.getCurrentLibrary(), oldWordCount);
			mListWord.addAll(oldWords);
		}

		// get list old word if enough
		oldWordCount = Consts.MAX_WORD_PER_TEST - mListWord.size();
		if (oldWordCount > 0) {
			ArrayList<WordTable> oldWords = database.getListOldWords(
					mSharePrefs.getCurrentLibrary(), oldWordCount);
			mListWord.addAll(oldWords);
		}
	}

	/**
	 * apply test framgnet for current test id
	 */
	private void applyFragmentForTest() {
		switch (mCurrentTestId) {
		case TEST_IDENTITY:
			mFragment = new WordIdentityFragment();
			break;
		case TEST_WRITING:
			mFragment = new WordWritingFragment();
			break;
		case TEST_LISTENING:
			mFragment = new WordListeningFragment();
			break;
		default:
			mCurrentTestId = 0;
			mSharePrefs.saveCurrentTestPosition(mCurrentTestId);
			mFragment = new WordIdentityFragment();
			break;
		}
		// set word list data
		mFragment.setWordList(mListWord, true);

		// add fragment to fragment transaction
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragmentLayout, mFragment);
		fragmentTransaction.commit();
	}

	/**
	 * go to next test
	 */
	public void gotoNextTest() {
		mCurrentTestId++;
		if (mCurrentTestId == Consts.MAX_TESTING) {
			// finish test, set result
			showFinalResult();
		} else {
			// save next current test
			mSharePrefs.saveCurrentTestPosition(mCurrentTestId);
			// reset correct count
			reset();
			// show next test
			applyFragmentForTest();
		}
	}

	/**
	 * show result for all test
	 */
	private void showFinalResult() {
		// save correct word to old list, incorrect word to new list
		database.changeWordNewStatus(mSharePrefs.getCurrentLibrary(),
				correctList, 0);

		// reset word remember
		database.resetWordForget(mSharePrefs.getCurrentLibrary(), correctList);

		// clear testing word
		database.changeWordTested(mSharePrefs.getCurrentLibrary());

		// reset test position
		mSharePrefs.saveCurrentTestPosition(0);

		// reset show alert pick card if new card is 0
		mSharePrefs.saveShowAlertPickCard(true);

        // reset sync flash to false
        mSharePrefs.saveIsWearSync(false);

		finish();
	}

	/**
	 * check result for current test
	 */
	public void checkResult() {
		// change status of incorrect word to new word
		if (incorrectList.size() > 0) {
			database.changeWordNewStatus(mSharePrefs.getCurrentLibrary(),
					incorrectList, 1);
		}
		TestResultDialog.newInstance(mCurrentTestId * mListWord.size(),
				correctList.size(), mListWord.size(), this).show(
				getSupportFragmentManager(), null);
	}

	/**
	 * Add correct word after test
	 * 
	 * @param word
	 */
	public void addCorrectWord(String word) {
		correctList.add(word);
	}

	/**
	 * Add incorrect word after test
	 * 
	 * @param word
	 */
	public void addIncorrectWord(String word) {
		// increase word forget
		incorrectList.add(word);
		database.increaseWordForget(mSharePrefs.getCurrentLibrary(), word);
	}

	private void reset() {
		incorrectList.clear();
		correctList.clear();
	}

	@Override
	public void onOk(AppDialog<?> f) {
		// resume test
		mFragment.onResumeAfterPause();
		// reset resume state
		mResume = false;
		mIsRunning = true;
	}

	@Override
	public void onDialogYes(AppDialog<?> f) {
		finish();
	}

	@Override
	public void onDialogNo(AppDialog<?> f) {
	}

	@Override
	public void onTestResultDialogOk(AppDialog<?> f, boolean next) {
		if (next) {
			gotoNextTest();
		} else {
			// reset
			reset();
			mFragment.resetTest();
		}
	}

	public boolean isResume() {
		return mResume;
	}

	public boolean isRunning() {
		return mIsRunning;
	}
}
