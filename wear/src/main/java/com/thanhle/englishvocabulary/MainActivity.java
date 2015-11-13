package com.thanhle.englishvocabulary;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

import com.thanhle.englishvocabulary.CardView.CardMoveListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity implements OnClickListener,
        CardMoveListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<CardTable> mListCard = new ArrayList<CardTable>();
    private int mCardIndex = 0;
    private CardView mCardView;
    private Animation mFlipUpAnimation, mFlipDownAnimation;
    private DatabaseHandler database;
    //    private ProgressDialog dialog;
    private MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new DatabaseHandler(this);

        // init animation
        initAnimation();

        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                mCardView = (CardView) watchViewStub.findViewById(R.id.cardView);
                mCardView.setAnimationDuration(300);
                mCardView.setDirection(FlipView.DIRECTION_HORIZONTAL);
                mCardView.setPivot(FlipView.PIVOT_CENTER);
                mCardView.setOnCardMoveListener(MainActivity.this);
                if (watchViewStub.findViewById(R.id.rlWearRound)!=null) {
                    // round wear
                }

                // count card
                int count = database.countCard();
                if (count == 0) {
                    Toast.makeText(MainActivity.this, R.string.no_card, Toast.LENGTH_LONG).show();
                } else {
                    // load data
                    loadData();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register a local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(DataLayerListenerService.WearConsts.ACTION_DATA_FORWARDED);
        messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        mListCard = database.getListCards();

        // fill random card index
        if (mListCard.size() > 0) {
            mCardIndex = new Random().nextInt(mListCard.size());
            mCardView.fillCardData(mListCard.get(mCardIndex));
        }
    }

//    private void readData() {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                InputStream in = getResources().openRawResource(
//                        R.raw.data_1000words);
//                BufferedReader bf = new BufferedReader(
//                        new InputStreamReader(in));
//                String line;
//                int lineCount = 1;
//                try {
//                    // skip first line (name of words)
//                    bf.readLine();
//                    while ((line = bf.readLine()) != null) {
//                        String[] values = line.split("\t");
//                        CardTable card = new CardTable(values);
//                        if (card.word != null) {
//                            card.word = card.word.toLowerCase(Locale.US).trim();
//                            mListCard.add(card);
//                        } else {
//                            Log.w(TAG, "data line error: " + lineCount);
//                        }
//                        lineCount++;
//                    }
//                    database.insertCard(mListCard);
//                    in.close();
//
//                    // fill random card index
//                    if (mListCard.size() > 0) {
//                        mCardIndex = new Random().nextInt(mListCard.size());
//                        mCardView.fillCardData(mListCard.get(mCardIndex));
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//        }).start();
//    }

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

    @Override
    public void onClick(View v) {

    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "on receive data forward from data-layer");
            loadData();
        }
    }
}
