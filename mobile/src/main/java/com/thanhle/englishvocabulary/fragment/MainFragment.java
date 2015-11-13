package com.thanhle.englishvocabulary.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.activity.CardPickerActivity;
import com.thanhle.englishvocabulary.activity.MyActivity;
import com.thanhle.englishvocabulary.activity.TestActivity;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.WordTable;
import com.thanhle.englishvocabulary.popupmenu.ActionItem;
import com.thanhle.englishvocabulary.popupmenu.QuickAction;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.DataUtils;
import com.thanhle.englishvocabulary.utils.ProgressAnimationRunnable;
import com.thanhle.englishvocabulary.utils.ProgressAnimationRunnable.ProgressAnimationListener;
import com.thanhle.englishvocabulary.view.CardView;
import com.thanhle.englishvocabulary.view.FlipView;
import com.thanhle.englishvocabulary.view.ProgressWheel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainFragment extends CardListFragment implements OnClickListener, ProgressAnimationListener, CardView.OnChangeCardCallback
{
    private static final String TAG = MainFragment.class.getSimpleName();

    private static final float SIZE_PER_FORGET = 0.5f;
    private static final float MAX_SIZE_FORGET = 3f;
    private TextView mTvWordBoard, mBtnTest;
    private View rlCardView, rlWordBoard, mBtnPickCard, mBtnGuideNext;
    private SlidingUpPanelLayout rlCardBoard;
    private ImageView imgBoardArrow;
    private ProgressWheel mProgressBar1, mProgressBar2;
    private boolean mProgressRunning = false;
    private Thread mProgress1Thread, mProgress2Thread;
    private boolean mShowWordBoard = false;
    private ImageButton btn_user, btn_menu;
    private QuickAction quickAction;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mRoot = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        mBtnTest = (TextView) mRoot.findViewById(R.id.btnTest);
        mBtnTest.setOnClickListener(this);
        mBtnPickCard = mRoot.findViewById(R.id.btnPickCard);
        mBtnPickCard.setOnClickListener(this);
        rlCardView = mRoot.findViewById(R.id.rlCardView);
        rlCardBoard = (SlidingUpPanelLayout) mRoot.findViewById(R.id.rlCardBoard);
        rlWordBoard = mRoot.findViewById(R.id.rlWordBoard);
        imgBoardArrow = (ImageView) mRoot.findViewById(R.id.imgBoardArrow);
        imgBoardArrow.setOnClickListener(this);
        mTvWordBoard = (TextView) mRoot.findViewById(R.id.tvWordBoard);
        mProgressBar1 = (ProgressWheel) mRoot.findViewById(R.id.progressBar1);
        mProgressBar2 = (ProgressWheel) mRoot.findViewById(R.id.progressBar2);
        mBtnGuideNext = mRoot.findViewById(R.id.btnGuideNext);
        mBtnGuideNext.setOnClickListener(this);

        btn_user = (ImageButton) mRoot.findViewById(R.id.btn_user_actionbar);
        btn_menu = (ImageButton) mRoot.findViewById(R.id.btn_menu_actionbar);
        btn_user.setOnClickListener(this);
        btn_menu.setOnClickListener(this);

        // init animation
        initAnimation();

        // init for SlidingUpPanel
        rlCardBoard.setAnchorPoint(0.7f);
        rlCardBoard.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener()
        {
            @Override
            public void onPanelSlide(View panel, float slideOffset)
            {

            }

            @Override
            public void onPanelCollapsed(View panel)
            {
                mShowWordBoard = false;
                imgBoardArrow.setImageResource(R.drawable.img_board_top);
            }

            @Override
            public void onPanelExpanded(View panel)
            {
                mShowWordBoard = true;
                imgBoardArrow.setImageResource(R.drawable.img_board_bottom);
            }

            @Override
            public void onPanelAnchored(View panel)
            {
                mShowWordBoard = true;
                imgBoardArrow.setImageResource(R.drawable.img_board_bottom);
            }

            @Override
            public void onPanelHidden(View panel)
            {

            }
        });

        // init card view
        mCardView = (CardView) mRoot.findViewById(R.id.cardView);
        mCardView.setAnimationDuration(300);
        mCardView.setDirection(FlipView.DIRECTION_HORIZONTAL);
        mCardView.setPivot(FlipView.PIVOT_CENTER);
        mCardView.setOnCardMoveListener(this);
        mCardView.setCallback(this);

        // load word data
        loadData();

        ActionItem menu_setting = new ActionItem(0, getString(R.string.menu_setting), null);
        ActionItem menu_rate = new ActionItem(1, getString(R.string.menu_rate), null);
        ActionItem menu_share_fb = new ActionItem(2, getString(R.string.menu_share_fb), null);

        ActionItem menu_share_google = new ActionItem(3, getString(R.string.menu_share_google), null);

        ActionItem menu_remove_ads = new ActionItem(4, getString(R.string.menu_remove_ads), null);

        quickAction = new QuickAction(getActivity(), QuickAction.VERTICAL);

        quickAction.addActionItem(menu_setting);
        quickAction.addActionItem(menu_rate);
        quickAction.addActionItem(menu_share_fb);
        quickAction.addActionItem(menu_share_google);
        quickAction.addActionItem(menu_remove_ads);

        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
        {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId)
            {
                switch (actionId)
                {
                    case 0:
                        break;
                }
            }
        });

        return mRoot;
    }

    @Override
    public void loadData()
    {
        mListCard = DataUtils.getStudyingCardList(database);

        // fill random card index
        if (mListCard.size() > 0)
        {
            mCardView.setVisibility(View.VISIBLE);
            mCardIndex = DataUtils.getRandomNumber(mListCard.size());
            mCardView.fillCardData(mListCard.get(mCardIndex));
        }
        else
        {
            mCardView.setVisibility(View.GONE);
        }
    }

    /**
     * load data and refresh check data
     */
    public void loadDataAndRefresh()
    {
        loadData();
        checkData();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        checkData();
    }

    @Override
    public void onClick(View v)
    {
        Intent i;
        switch (v.getId())
        {
            case R.id.btn_menu_actionbar:
                quickAction.show(v);
                break;

            case R.id.btn_user_actionbar:

                if (MyActivity.getDrawerLayout() != null)
                {
                    if (!MyActivity.getDrawerLayout().isDrawerOpen(GravityCompat.START))
                    {
                        MyActivity.getDrawerLayout().openDrawer(GravityCompat.START);
                    }
                }

                break;
            case R.id.btnPickCard:
                i = new Intent(getActivity(), CardPickerActivity.class);
                startActivity(i);
                break;
            case R.id.btnTest:
                i = new Intent(getActivity(), TestActivity.class);
                startActivity(i);
                break;
            case R.id.imgBoardArrow:
                if (!mShowWordBoard)
                {
                    openWordBoard();
                }
                else
                {
                    closeWordBoard();
                }
                break;
            case R.id.btnGuideNext:
                int step = mSharePrefs.getGuideStep(TAG);
                if (step == 2)
                {
                    // final step, hide all guide
                    mBtnGuideNext.setVisibility(View.GONE);
                    mRoot.findViewById(R.id.viewOverlayCenter1).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.viewOverlayTop).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.viewOverlayBottom).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.viewOverlayCenter2).setVisibility(View.GONE);

                    // first gone all textview
                    mRoot.findViewById(R.id.tvGuideCenter).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.tvGuideBottom).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.tvGuideTop).setVisibility(View.GONE);
                    mRoot.findViewById(R.id.tvGuideDesc).setVisibility(View.GONE);

                    // reset count app run
                    mSharePrefs.saveCountAppRun(1);

                    if (getActivity() != null && getActivity() instanceof MyActivity)
                    {
                        ((MyActivity) getActivity()).showFinishGuide();
                    }
                }
                mSharePrefs.saveGuideStep(TAG, step + 1);
                loadGuideline();
            default:
                break;
        }
    }

    /**
     * check studying data, run progress bar animation, put onResume to check after resume fragment
     */
    private void checkData()
    {
        // load word board data
        loadWordBoard();

        int newWord = database.countNewWord(mSharePrefs.getCurrentLibrary());
        int count = database.countWord(mSharePrefs.getCurrentLibrary());
        int cardCount = database.countCard(mSharePrefs.getCurrentLibrary());

        // show, hide button test, pick word, check if new word is less than
        // MAX_WORD / 2
        if (count == 0)
        {
            mBtnTest.setVisibility(View.GONE);
        }
        else
        {
            mBtnTest.setVisibility(View.VISIBLE);
        }
        if (newWord == 0)
        {
            mBtnTest.setText(R.string.word_practice);
        }
        else
        {
            mBtnTest.setText(R.string.word_test);
        }
        if (newWord < Consts.MAX_WORD_SELECT / 2)
        {
            mBtnPickCard.setVisibility(View.VISIBLE);
        }
        else
        {
            mBtnPickCard.setVisibility(View.GONE);
        }

        // running progress animation
        if (!mProgressRunning)
        {
            mProgressRunning = true;
            mProgress1Thread = new Thread(new ProgressAnimationRunnable(mProgressBar1, count, cardCount, this));
            mProgress1Thread.start();
            mProgress2Thread = new Thread(new ProgressAnimationRunnable(mProgressBar2, newWord, count, this));
            mProgress2Thread.start();
        }

        // check and show guide
        if (cardCount > 0 && newWord > 0)
        {
            loadGuideline();
        }
    }

    /**
     * open word board, animation Card View and Word Board View
     */
    private void openWordBoard()
    {
        rlCardBoard.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        mShowWordBoard = true;
        imgBoardArrow.setImageResource(R.drawable.img_board_bottom);
    }

    /**
     * close word board, animation Card View and Word Board View
     */
    private void closeWordBoard()
    {
        rlCardBoard.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mShowWordBoard = false;
        imgBoardArrow.setImageResource(R.drawable.img_board_top);
    }

    private void loadGuideline()
    {
        int pickStep = mSharePrefs.getGuideStep(CardPickerFragment.class.getSimpleName());
        int step = mSharePrefs.getGuideStep(TAG);
        if (pickStep == 5 && step < 3)
        {
            // show next button
            mBtnGuideNext.setVisibility(View.VISIBLE);

            // show all overlay
            mRoot.findViewById(R.id.viewOverlayCenter1).setVisibility(View.VISIBLE);
            View overlayTop = mRoot.findViewById(R.id.viewOverlayTop);
            View overlayBottom = mRoot.findViewById(R.id.viewOverlayBottom);
            View overlayCenter2 = mRoot.findViewById(R.id.viewOverlayCenter2);
            overlayTop.setVisibility(View.VISIBLE);
            overlayBottom.setVisibility(View.VISIBLE);
            overlayCenter2.setVisibility(View.VISIBLE);
            overlayTop.setOnClickListener(this);
            overlayBottom.setOnClickListener(this);
            overlayCenter2.setOnClickListener(this);

            // first gone all textview
            View tvGuideCenter = mRoot.findViewById(R.id.tvGuideCenter);
            View tvGuideBottom = mRoot.findViewById(R.id.tvGuideBottom);
            View tvGuideTop = mRoot.findViewById(R.id.tvGuideTop);
            TextView tvGuideDesc = (TextView) mRoot.findViewById(R.id.tvGuideDesc);
            switch (step)
            {
                case 0:
                    // show guide center
                    overlayTop.setBackgroundResource(R.color.overlay_color);
                    overlayBottom.setBackgroundResource(R.color.overlay_color);
                    overlayCenter2.setBackgroundResource(R.drawable.img_background_overlay_guide);
                    tvGuideCenter.setVisibility(View.VISIBLE);
                    tvGuideBottom.setVisibility(View.GONE);
                    tvGuideTop.setVisibility(View.GONE);
                    tvGuideDesc.setVisibility(View.GONE);
                    break;
                case 1:
                    // show guide bottom
                    overlayTop.setBackgroundResource(R.color.overlay_color);
                    overlayBottom.setBackgroundResource(R.drawable.img_background_overlay_guide);
                    overlayCenter2.setBackgroundResource(R.color.overlay_color);
                    tvGuideCenter.setVisibility(View.GONE);
                    tvGuideBottom.setVisibility(View.VISIBLE);
                    tvGuideTop.setVisibility(View.GONE);
                    tvGuideDesc.setVisibility(View.VISIBLE);
                    tvGuideDesc.setText(R.string.guide_word_board_desc);
                    break;
                case 2:
                    // show guide top
                    overlayTop.setBackgroundResource(R.drawable.img_background_overlay_guide);
                    overlayBottom.setBackgroundResource(R.color.overlay_color);
                    overlayCenter2.setBackgroundResource(R.color.overlay_color);
                    tvGuideCenter.setVisibility(View.GONE);
                    tvGuideBottom.setVisibility(View.GONE);
                    tvGuideTop.setVisibility(View.VISIBLE);
                    tvGuideDesc.setVisibility(View.VISIBLE);
                    tvGuideDesc.setText(R.string.guide_top_information_desc);
                default:
                    break;
            }
        }
    }

    private void loadWordBoard()
    {
        // get forget word (forget count > 0)
        ArrayList<WordTable> wordForgets = database.getListWordByForget(mSharePrefs.getCurrentLibrary(), true);
        // get remember word (forget count = 0)
        ArrayList<WordTable> wordRemembers = database.getListWordByForget(mSharePrefs.getCurrentLibrary(), false);
        // merge 2 list
        ArrayList<WordTable> words = new ArrayList<WordTable>();
        words.addAll(wordForgets);
        words.addAll(wordRemembers);

        int max = database.getWordForgetHighest(mSharePrefs.getCurrentLibrary());
        int min = database.getWordForgetLowest(mSharePrefs.getCurrentLibrary());
        float sizePerForget = SIZE_PER_FORGET;
        if (max * sizePerForget > MAX_SIZE_FORGET)
        {
            // recalc size per forget
            sizePerForget = MAX_SIZE_FORGET / max;
        }

        mTvWordBoard.setMovementMethod(LinkMovementMethod.getInstance());
        mTvWordBoard.setLinkTextColor(Color.WHITE);
        String text = "";
        for (WordTable word : words)
        {
            text += word.word + "  ";
        }
        SpannableString spannable = new SpannableString(text);
        int start = 0, end = -2;
        for (WordTable word : words)
        {
            start = end + 2;
            end = start + word.word.length();
            spannable.setSpan(new RelativeSizeSpan(1 + (word.forgetCount - min) * sizePerForget), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            spannable.setSpan(new WordClickSpannable(word.word), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        mTvWordBoard.setText(spannable);
    }

    @Override
    public void onChangeCard(CardTable card)
    {
        if (getActivity() != null && getActivity() instanceof MyActivity)
        {
            ((MyActivity) getActivity()).changeCard(card);
        }
    }

    private class WordClickSpannable extends ClickableSpan
    {
        private final String word;

        public WordClickSpannable(String word)
        {
            this.word = word;
        }

        @Override
        public void updateDrawState(TextPaint ds)
        {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false); // set to false to remove underline
        }

        @Override
        public void onClick(View widget)
        {
            if (mShowWordBoard)
            {
                closeWordBoard();
            }
            mCardView.fillCardData(database.getCard(mSharePrefs.getCurrentLibrary(), word), true);
        }
    }

    /**
     * an animation for resizing the view.
     */
    private class ResizeAnimation extends Animation
    {
        private View mView;
        private View mView2;
        private float mToWeight;
        private float mFromWeight;
        private float mFromWeight2;

        public ResizeAnimation(View v, float fromWeight, float toWeight, View v2, float fromWeight2)
        {
            mFromWeight = fromWeight;
            mToWeight = toWeight;
            mFromWeight2 = fromWeight2;
            mView = v;
            mView2 = v2;
            setDuration(300);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            float weight = (mToWeight - mFromWeight) * interpolatedTime + mFromWeight;
            LayoutParams p = (LayoutParams) mView.getLayoutParams();
            p.weight = weight;
            mView.requestLayout();

            float weight2 = mFromWeight2 - (mToWeight - mFromWeight) * interpolatedTime;
            LayoutParams p2 = (LayoutParams) mView2.getLayoutParams();
            p2.weight = weight2;
            mView2.requestLayout();
        }
    }

    @Override
    public void onProgressAnimationFinish()
    {
        mProgressRunning = false;
    }
}
