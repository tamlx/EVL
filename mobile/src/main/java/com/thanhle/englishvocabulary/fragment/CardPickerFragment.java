package com.thanhle.englishvocabulary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.activity.CardPickerActivity;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.WordTable;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.DataUtils;
import com.thanhle.englishvocabulary.utils.ProgressAnimationRunnable;
import com.thanhle.englishvocabulary.view.CardView;
import com.thanhle.englishvocabulary.view.FlipView;
import com.thanhle.englishvocabulary.view.ProgressWheel;

import java.util.ArrayList;

public class CardPickerFragment extends CardListFragment {
    private static final String TAG = CardPickerFragment.class.getSimpleName();
    private TextView tvWordBoard, btnAutoAdd;
    private ArrayList<CardTable> mListCardSelect = new ArrayList<CardTable>();
    private String mWordSelect = "";
    private Animation mMoveRightAnimation;
    private int mMaxCardSelect = 0;
    private boolean mAutoAdd = false;

    private Runnable mShowGuideRunnable = new Runnable() {

        @Override
        public void run() {
            int step = mSharePrefs.getGuideStep(TAG);

            switch (step) {
                case 1:
                    mRoot.findViewById(R.id.tvGuideSwipeUp).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.tvGuideSwipeDown).setVisibility(View.VISIBLE);
                    mSharePrefs.saveGuideStep(TAG, step + 1);
                    break;
                case 2:
                    mRoot.findViewById(R.id.tvGuideSwipeDown).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.tvGuideTouchCard).setVisibility(View.VISIBLE);
                    mSharePrefs.saveGuideStep(TAG, step + 1);
                    break;
                case 3:
                    mRoot.findViewById(R.id.tvGuideTouchCard).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.tvGuideSwipeRight)
                            .setVisibility(View.VISIBLE);
                    mSharePrefs.saveGuideStep(TAG, step + 1);
                    break;
                case 4:
                    mRoot.findViewById(R.id.tvGuideSwipeRight).setVisibility(View.GONE);
                    btnAutoAdd = (TextView) mRoot.findViewById(R.id.btnAutoAddGuide);
                    btnAutoAdd.setText(getResources().getQuantityString(
                            R.plurals.auto_add, mMaxCardSelect, mMaxCardSelect));
                    btnAutoAdd.setVisibility(View.VISIBLE);
                    btnAutoAdd.setOnClickListener(CardPickerFragment.this);
                    mRoot.findViewById(R.id.tvGuideTouchAdd).setVisibility(View.VISIBLE);
                    mSharePrefs.saveGuideStep(TAG, step + 1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_card_picker, container, false);
        // init animation
        initAnimation();

        // init card view
        mCardView = (CardView) mRoot.findViewById(R.id.cardView);
        tvWordBoard = (TextView) mRoot.findViewById(R.id.tvWordBoard);
        btnAutoAdd = (TextView) mRoot.findViewById(R.id.btnAutoAdd);
        btnAutoAdd.setOnClickListener(this);

        // load word data
        loadData();
        return mRoot;
    }

    @Override
    public void loadData() {
        // set library descrption
        loadLibraryDescription();
        // reset data
        mListCardSelect.clear();
        tvWordBoard.setText("");

        // get max card can select
        mMaxCardSelect = Consts.MAX_WORD_SELECT
                - database.countNewWord(mSharePrefs.getCurrentLibrary());
        if (mMaxCardSelect == 0)
            mMaxCardSelect = Consts.MAX_WORD_SELECT;

        // set auto add text
        btnAutoAdd.setText(getResources().getQuantityString(R.plurals.auto_add,
                mMaxCardSelect, mMaxCardSelect));

        if (database.countCard(mSharePrefs.getCurrentLibrary()) == 0 ) {
            // the number card of current library is 0, load card from resource
            // data to database
            showProgress(getString(R.string.msg_wait_load_card));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataUtils.readData(getActivity(),
                            mSharePrefs.getCurrentLibrary());
                    dismissProgress();
                    mCardView.post(new Runnable() {
                        @Override
                        public void run() {
                            loadCard();
                        }
                    });
                }
            }).start();
        } else {
            loadCard();
        }
    }

    @Override
    protected void initAnimation() {
        super.initAnimation();

        mMoveRightAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +2.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        mMoveRightAnimation.setDuration(300);
        mMoveRightAnimation.setInterpolator(new AccelerateInterpolator());
        mMoveRightAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addSelectWord();
                if (mListCard.size() != 0) {
                    mCardView.fillCardData(mListCard.get(mCardIndex), true);
                }
                endCardAnimation(ANIMATION_RIGHT);
                if (mAutoAdd) {
                    if (mMaxCardSelect > 0 && mListCard.size() != 0) {
                        cardMoveRight(mCardView);
                    } else {
                        mAutoAdd = false;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnAutoAdd:
            case R.id.btnAutoAddGuide:
                if (mListCardSelect.size() < mMaxCardSelect) {
                    v.setEnabled(false);
                    mAutoAdd = true;
                    cardMoveRight(mCardView);
                }
                break;
            default:
                break;
        }
    }

    private void loadCard() {
        if (mListCard==null) {
            mListCard = new ArrayList<CardTable>();
        } else {
            mListCard.clear();
        }
        mListCard.addAll(database.getListCards(mSharePrefs.getCurrentLibrary()));

        // remove all card is old
        ArrayList<WordTable> oldWordsList = database.getListWords(
                mSharePrefs.getCurrentLibrary(), 0);
        ArrayList<String> oldWords = new ArrayList<String>();
        for (WordTable word : oldWordsList) {
            oldWords.add(word.word);
        }
        for (int i = mListCard.size() - 1; i >= 0; i--) {
            if (oldWords.contains(mListCard.get(i).word)) {
                mListCard.remove(i);
            }
        }
        initCardView(mCardView);
        if (mListCard.size()>0) {
            loadGuideline();
        }
    }

    private void initCardView(CardView cardView) {
        cardView.setAnimationDuration(300);
        cardView.setDirection(FlipView.DIRECTION_HORIZONTAL);
        cardView.setPivot(FlipView.PIVOT_CENTER);
        cardView.setOnCardMoveListener(this);

        // fill first word
        if (mListCard.size() > 0) {
            cardView.fillCardData(mListCard.get(mCardIndex));
        }
    }

    private void addSelectWord() {

        // add card to select list
        mListCardSelect.add(mListCard.get(mCardIndex));
        mMaxCardSelect--;
        mWordSelect += " " + mListCard.get(mCardIndex).word;
        tvWordBoard.setText(mWordSelect);
        btnAutoAdd.setText(getResources().getQuantityString(R.plurals.auto_add,
                mMaxCardSelect, mMaxCardSelect));

        // remove list item
        mListCard.remove(mCardIndex);
        if (mListCard.size() != 0) {
            if (mAutoAdd) {
                // random card if auto add mode
                mCardIndex = DataUtils.getRandomNumber(mListCard.size());
            }
            // after remove, if current card index is last, go to previous card
            if (mCardIndex == mListCard.size()) {
                mCardIndex--;
            }
        }

        if (mMaxCardSelect == 0 || mListCard.size() == 0) {
            if (getActivity() instanceof CardPickerActivity) {
                ((CardPickerActivity) getActivity()).showFinishChooseCardDialog();
            }
        }
    }

    private void loadLibraryDescription() {
        int studiedWord = database.countWord(mSharePrefs.getCurrentLibrary());
        int cardCount = database.countCard(mSharePrefs.getCurrentLibrary());
        TextView tvDescription = (TextView) mRoot.findViewById(R.id.tvDescription2);
        TextView tvName = (TextView) mRoot.findViewById(R.id.tvDescription1);
        // set name
        tvName.setText(DataUtils.getLibraryName(getActivity(),
                mSharePrefs.getCurrentLibrary()));
        // set description
        ProgressWheel progress1 = (ProgressWheel) mRoot.findViewById(R.id.progressBar1);
        tvDescription.setText(getString(R.string.word_list_count, cardCount,
                studiedWord));
        new Thread(new ProgressAnimationRunnable(progress1, studiedWord,
                cardCount, null)).start();
    }

    private void loadGuideline() {
        int step = mSharePrefs.getGuideStep(TAG);
        if (step >= 0 && step <= 4) {
            mRoot.findViewById(R.id.overlay).setVisibility(View.VISIBLE);
            mCardView = (CardView) mRoot.findViewById(R.id.cardViewGuide);
            initCardView(mCardView);
            mCardView.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    endCardAnimation(ANIMATION_TOUCH);
                }
            });
            switch (step) {
                case 0:
                    mRoot.findViewById(R.id.tvGuideSwipeUp).setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mRoot.findViewById(R.id.tvGuideSwipeDown).setVisibility(View.VISIBLE);
                    break;
                case 2:
                    mRoot.findViewById(R.id.tvGuideTouchCard).setVisibility(View.VISIBLE);
                    break;
                case 3:
                    mRoot.findViewById(R.id.tvGuideSwipeRight)
                            .setVisibility(View.VISIBLE);
                    break;
                case 4:
                    btnAutoAdd = (TextView) mRoot.findViewById(R.id.btnAutoAddGuide);
                    btnAutoAdd.setText(getResources().getQuantityString(
                            R.plurals.auto_add, mMaxCardSelect, mMaxCardSelect));
                    btnAutoAdd.setVisibility(View.VISIBLE);
                    btnAutoAdd.setOnClickListener(CardPickerFragment.this);
                    mRoot.findViewById(R.id.tvGuideTouchAdd).setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            mSharePrefs.saveGuideStep(TAG, step + 1);
        }
    }

    @Override
    // we will select card by swipe to right
    public void cardMoveRight(CardView view) {
        if (mListCard == null || mListCard.size() == 0)
            return;
        // find card body (view inside)
        View rlCardBody = view.getCurrentFaceView().findViewById(
                R.id.rlCardBody);
        // start animation only card body
        rlCardBody.startAnimation(mMoveRightAnimation);
    }

    @Override
    protected void endCardAnimation(int animationType) {
        super.endCardAnimation(animationType);
        int step = mSharePrefs.getGuideStep(TAG);
        if (animationType == step) {
            mCardView.postDelayed(mShowGuideRunnable, 300);
        }
    }

    /**
     * get all list of selected card from user
     *
     * @return list of card (CardTable)
     */
    public ArrayList<CardTable> getListSelectedCard() {
        return mListCardSelect;
    }
}
