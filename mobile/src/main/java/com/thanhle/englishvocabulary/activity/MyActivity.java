package com.thanhle.englishvocabulary.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.squareup.picasso.Picasso;
import com.thanhle.englishvocabulary.MyApplication;
import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.adapter.DictionaryAdapterChildResource;
import com.thanhle.englishvocabulary.adapter.DictionaryAdapterResource;
import com.thanhle.englishvocabulary.billing.util.IabHelper;
import com.thanhle.englishvocabulary.billing.util.IabResult;
import com.thanhle.englishvocabulary.billing.util.Inventory;
import com.thanhle.englishvocabulary.billing.util.Purchase;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.dialog.AddCardDialog;
import com.thanhle.englishvocabulary.dialog.AppDialog;
import com.thanhle.englishvocabulary.dialog.ConfirmDialog;
import com.thanhle.englishvocabulary.dialog.LibraryListDialog;
import com.thanhle.englishvocabulary.dialog.RateMeDialog;
import com.thanhle.englishvocabulary.dialog.SearchLibraryDialog;
import com.thanhle.englishvocabulary.dialog.YesNoDialog;
import com.thanhle.englishvocabulary.fragment.DictionaryDrawerFragment;
import com.thanhle.englishvocabulary.fragment.MainFragment;
import com.thanhle.englishvocabulary.fragment.UserDrawerFragment;
import com.thanhle.englishvocabulary.requestmanagement.RequestErrorListener;
import com.thanhle.englishvocabulary.resource.ListLibraryResource;
import com.thanhle.englishvocabulary.resource.UserResource;
import com.thanhle.englishvocabulary.utils.Actions;
import com.thanhle.englishvocabulary.utils.AppUtils;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.DataUtils;
import com.thanhle.englishvocabulary.utils.SharePrefs;
import com.thanhle.englishvocabulary.view.circluarimageview.CircularImageView;
import com.thanhle.englishvocabulary.view.circluarimageview.RoundTransform;

import java.io.InputStream;
import java.util.ArrayList;

public class MyActivity extends BaseActivity
        implements DictionaryDrawerFragment.NavigationDrawerCallbacks, RateMeDialog.RateMeDialogListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener, NodeApi.NodeListener, AddCardDialog.AddCardDialogListener, ConfirmDialog.ConfirmDialogListener, LibraryListDialog.LibraryListDialogListener, YesNoDialog.YesNoDialogListener, SearchLibraryDialog.SearchLibraryDialogListener {
    private static final String TAG = MyActivity.class.getSimpleName();
    private static final String DIALOG_TAG_RATE = "DIALOG_TAG_RATE";
    private static final String DIALOG_ADD_CARD = "DIALOG_ADD_CARD";
    private static final String DIALOG_TAG_CARD_PICKER = "DIALOG_TAG_CARD_PICKER";
    private static final String DIALOG_TAG_LEARN_ALL = "DIALOG_TAG_LEARN_ALL";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DictionaryDrawerFragment mDictionaryDrawerFragment;
    private UserDrawerFragment mUserDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     * Request code for launching the Intent to resolve Google Play services
     * errors.
     */
    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError;
    private IabHelper mHelper;
    private RateMeDialog mDialogRate;
    private AddCardDialog mDialogAddCard;
    private ConfirmDialog mDialogLearnAll, mDialogCardPicker;
    private MainFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView1 = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView1.getMenu().getItem(0).setCheckable(false);
        mNavigationView1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                menuItem.setChecked(true);
                menuItem.setCheckable(false);
                switch (menuItem.getItemId()) {
                    //change user profile
                    case R.id.navigation_item_1:
                        Intent updateProfile = new Intent(MyActivity.this, UpdateProfileActivity.class);
                        startActivity(updateProfile);
                        closeDrawer();
                        return true;
                    //change library
                    case R.id.navigation_item_2:
//                        ArrayList<LibraryTable> libraries = new ArrayList<LibraryTable>();
//                        for (int j = 0; j < Consts.LIST_LIBRARY.size(); j++) {
//                            LibraryTable libraryTable = new LibraryTable(Consts.LIST_LIBRARY.get(j).getName(), Consts.LIST_LIBRARY.get(j).getName());
//                            libraries.add(libraryTable);
//                        }
//
//                        LibraryListDialog.newInstance(libraries, MyActivity.this).show(
//                                getSupportFragmentManager(), null);

                        ArrayList<LibraryTable> libraries = database.getListMyLibrary();
                        libraries.add(0, new LibraryTable(Consts.MY_LIBRARY, getString(R.string.my_library)));
                        LibraryListDialog.newInstance(libraries, MyActivity.this).show(
                                getSupportFragmentManager(), null);

                        return true;

                    // library online
                    case R.id.navigation_item_3:
//                        SearchLibraryDialog.newInstance(MyActivity.this, "Search Library", "").show(getSupportFragmentManager(), null);

                        if (isConnectInternet()) {
                            Intent libraryOnline = new Intent(MyActivity.this, ListLibraryActivity.class);
                            startActivity(libraryOnline);
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            Toast.makeText(MyActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                        return true;

                    //logout
                    case R.id.navigation_item_4:
                        mSharePrefs.clear();
                        database.clearAllWords();
                        database.clearAllCards();
                        database.clearAllLibrary();
                        finish();
                        Intent i = new Intent(MyActivity.this, SplashActivity.class);
                        startActivity(i);
                        return true;
                    default:
                        return true;
                }

            }
        });

        setUpToolbar();

        CircularImageView imgUserAvatar = (CircularImageView) findViewById(R.id.imgUserAvatar);
        String url = "";
        if (mSharePrefs.getUserInfo().provider.equalsIgnoreCase("google")) {
            url = convertLinkImage(mSharePrefs.get(Consts.LINK_PICTURE, "http://imgur.com/Q54I5IC.png"));
        } else {
            url = mSharePrefs.get(Consts.LINK_PICTURE, "http://i.imgur.com/Q54I5IC.png");
        }
        Picasso.with(this).load(url).placeholder(R.drawable.img_icon_user_avatar).transform(new RoundTransform()).into(imgUserAvatar);
//        new LoadProfileImage(imgUserAvatar).execute();

        TextView tvUserName = (TextView) findViewById(R.id.tv_username_layout_user_drawer_header);
        UserResource user = mSharePrefs.getUserInfo();
        if (user != null) {
            if (user.fullname != null && !user.fullname.equalsIgnoreCase("")) {
                tvUserName.setText(user.fullname);
            } else {
                tvUserName.setText(user.username);
            }
        }

        //get list library from server
        if (Consts.LIST_LIBRARY.size() == 0 && isConnectInternet()) {
            Bundle bundle = new Bundle();
            bundle.putString(Consts.PARAMConsts.TOKEN, mSharePrefs.getToken());
            mRequestManager.request(Actions.GETLISTLIBRARY, bundle, mGetListLibrarySuccessListener, mGetListLibraryErrorListener);
        }

        initialiseBilling();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        // get screen width and set width of Navigation Drawer layout width is 4/5 screen width
        int screenWidth = AppUtils.getScreenSize(this)[0];
        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) findViewById(R.id.dictionary_drawer).getLayoutParams();
//        DrawerLayout.LayoutParams lp2 = (DrawerLayout.LayoutParams) findViewById(R.id.user_drawer).getLayoutParams();
        lp.width = screenWidth / 5 * 4;
//        lp2.width = screenWidth / 5 * 3;

        mDictionaryDrawerFragment = (DictionaryDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.dictionary_drawer);
//        mUserDrawerFragment = (UserDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.user_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mDictionaryDrawerFragment.setUp(
                R.id.dictionary_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
//        mUserDrawerFragment.setUp(
//                R.id.user_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState != null) {
            mDialogRate = (RateMeDialog) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_TAG_RATE);
            mDialogAddCard = (AddCardDialog) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_ADD_CARD);
            mDialogCardPicker = (ConfirmDialog) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_TAG_CARD_PICKER);
            mDialogLearnAll = (ConfirmDialog) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_TAG_LEARN_ALL);
            mFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        } else {
            mFragment = new MainFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, mFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check studying data
        checkData();

        int appRunCount = mSharePrefs.getCountAppRun();
        // check show rate it
        if (appRunCount >= Consts.COUNT_SHOW_RATE) {
            // check show rate or not
            if (mSharePrefs.getShowRateMe()) {
                if (mDialogRate == null) {
                    mDialogRate = RateMeDialog.newInstance(this);
                }
                if (!mDialogRate.isShowing() && !isFinishing()) {
                    mDialogRate.show(getSupportFragmentManager(),
                            DIALOG_TAG_RATE);
                }
            }
        } else if (appRunCount > 0) {
            mSharePrefs.saveCountAppRun(appRunCount + 1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Consts.updateprofile) {
            Consts.updateprofile = false;
            TextView tvUserName = (TextView) findViewById(R.id.tv_username_layout_user_drawer_header);
            UserResource user = mSharePrefs.getUserInfo();
            if (user != null) {
                if (user.fullname != null && !user.fullname.equalsIgnoreCase("")) {
                    tvUserName.setText(user.fullname);
                } else {
                    tvUserName.setText(user.username);
                }
            }
        }
        if (Consts.install_new_library) {
            Consts.install_new_library = false;
            mSharePrefs.saveIsWearSync(false);
            checkData();
            syncCardData();
            mFragment.loadDataAndRefresh();
        }
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mResolvingError) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDictionaryDrawerFragment.isDrawerOpen()) {
            mDictionaryDrawerFragment.closeDrawer();
        } else if (mUserDrawerFragment != null && mUserDrawerFragment.isDrawerOpen()) {
            mUserDrawerFragment.closeDrawer();
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            YesNoDialog.newInstance(getString(R.string.msg_ask_exit_app), null, this)
                    .show(getSupportFragmentManager(), null);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mDictionaryDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
//            restoreActionBar();
//            return true;
//        } else if (mUserDrawerFragment != null && !mUserDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
//            restoreActionBar();
//            return true;
//        }
//        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_library:
                ArrayList<LibraryTable> libraries = database.getListMyLibrary();
                libraries.add(0, new LibraryTable(Consts.MY_LIBRARY, getString(R.string.my_library)));
                LibraryListDialog.newInstance(libraries, this).show(
                        getSupportFragmentManager(), null);
                break;
            case android.R.id.home:
//                if (mUserDrawerFragment != null && mUserDrawerFragment.isDrawerOpen()) {
//                    mUserDrawerFragment.closeDrawer();
//                } else {
//                    mUserDrawerFragment.openDrawer();
//                }

                return false;
            case R.id.action_share_fb: {
                Intent i = new Intent(this, TestDictionaryActivity.class);
                startActivity(i);
                break;
            }
            case R.id.action_share_google: {
                Intent i = new Intent(this, MyActivity.class);
                startActivity(i);
                break;
            }
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_rate:
                if (mDialogRate == null) {
                    mDialogRate = RateMeDialog.newInstance(this);
                }
                if (!mDialogRate.isShowing() && !isFinishing()) {
                    mDialogRate.show(getSupportFragmentManager(),
                            DIALOG_TAG_RATE);
                }
                break;
            case R.id.action_remove_ads:
                if (mHelper != null) {
                    mHelper.launchPurchaseFlow(this, Consts.PURCHASE_REMOVE_ADS, 10001, mPurchaseFinishedListener, "");
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Log.e(TAG, "Connection to Google API client has failed");
            mResolvingError = false;
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Google API Client was connected");
        mResolvingError = false;
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
        if (!mSharePrefs.getIsWearSync())
            syncCardData();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Connection to Google API client was suspended");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived() A message from watch was received:"
                + messageEvent.getRequestId() + " " + messageEvent.getPath());
        if (messageEvent.getPath().equals(Consts.WearConsts.DATA_ITEM_RECEIVED_PATH)) {
            mSharePrefs.saveIsWearSync(true);
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        Log.d(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "onPeerDisconnected: " + peer);
    }

    @Override
    public void onRateYes(RateMeDialog f) {
        AppUtils.showAppPlayStore(this);
        // save not show rate
        mSharePrefs.saveShowRateMe(false);
        traceRate("rate yes");
    }

    @Override
    public void onRateNo(RateMeDialog f) {
        // save not show rate again
        mSharePrefs.saveShowRateMe(false);
        traceRate("rate no");
    }

    @Override
    public void onRateCancel(RateMeDialog f) {
        // reset app running count is 1, prepare for next show rate app after 5
        // times run
        mSharePrefs.saveCountAppRun(1);
        traceRate("rate cancel");
    }

    @Override
    public void onDictionarySelect(DictionaryAdapterResource group, DictionaryAdapterChildResource ite) {
        CardTable card = null;
        switch (group.dictionaryType) {
            case DictionaryAdapterResource.TYPE_WORD:
            case DictionaryAdapterResource.TYPE_WORD_SPECIFIC:
                card = new CardTable(group.word, group.phonetically, group.type, "", ite.mean, (ite.exams != null && ite.exams.size() > 0 ? ite.exams.get(0) : ""), Consts.MY_LIBRARY);
                break;
            case DictionaryAdapterResource.TYPE_WORD_PHARSE:
                card = new CardTable(ite.wordPharseOrField, "", "", "", ite.mean, "", Consts.MY_LIBRARY);
                break;
        }
        if (mDialogAddCard == null) {
            mDialogAddCard = AddCardDialog.newInstance(this);
        }
        if (!mDialogAddCard.isShowing() && !isFinishing()) {
            mDialogAddCard.setCard(card);
            mDialogAddCard.show(getSupportFragmentManager(), DIALOG_ADD_CARD);
        }
    }

    @Override
    public void onAddCardYes(AddCardDialog f, CardTable card) {
        database.insertCard(card);
        showCenterToast((R.string.msg_add_card_success));
    }

    @Override
    public void onAddCardCancel(AddCardDialog f) {

    }


    @Override
    public void onOk(AppDialog<?> f) {
        if (DIALOG_TAG_CARD_PICKER.equals(f.getTag())) {
            Intent i = new Intent(this, CardPickerActivity.class);
            startActivity(i);
        }
    }


    @Override
    public void onLibraryCancel(AppDialog<?> f) {
    }

    @Override
    public void onLibraryChoose(AppDialog<?> f, String library, String library_name) {
//        if (Consts.show_result_get_list_library) {
//            Toast.makeText(this, library, Toast.LENGTH_SHORT).show();
//            Consts.show_result_get_list_library = false;
//        } else if (Consts.show_result_search_list_library) {
//            Toast.makeText(this, library, Toast.LENGTH_SHORT).show();
//            Consts.show_result_search_list_library = false;
//        } else {}
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        if (database.checkLibraryExists(library)) {
            mSharePrefs.saveCurrentLibrary(library);
            mSharePrefs.saveIsWearSync(false);
            checkData();
            syncCardData();
            mFragment.loadDataAndRefresh();
        } else {
            Toast.makeText(MyActivity.this, "Thư viện: \"" + library_name + "\" hiện tại chưa có dữ liệu.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * call when card change in Main Fragment
     *
     * @param card current card
     */
    public void changeCard(CardTable card) {
        if (mDictionaryDrawerFragment != null) {
            mDictionaryDrawerFragment.setDictionaryWord(card.word);
        }
    }

    /**
     * show finish guide dialog
     */
    public void showFinishGuide() {
        // show dialog finish guideline
        ConfirmDialog.newInstance(getString(R.string.guide_finish),
                null, this).show(getSupportFragmentManager(), null);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    private void syncCardData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<CardTable> mListCard = DataUtils.getStudyingCardList(database);
                PutDataMapRequest putDataMapRequest = PutDataMapRequest
                        .create(Consts.WearConsts.SYNC_PATH);
                putDataMapRequest.getDataMap()
                        .putString(Consts.WearConsts.LIBRARY_KEY,
                                mSharePrefs.getCurrentLibrary());
                putDataMapRequest.getDataMap().putStringArrayList(
                        Consts.WearConsts.CARDS_KEY,
                        DataUtils.convertListCardToWearDataString(mListCard));
                PutDataRequest request = putDataMapRequest.asPutDataRequest();
                if (!mGoogleApiClient.isConnected()) {
                    return;
                }
                Log.d(TAG, "Generating Sync DataItem: " + request);
                Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                        .setResultCallback(
                                new ResultCallback<DataApi.DataItemResult>() {
                                    @Override
                                    public void onResult(
                                            DataApi.DataItemResult dataItemResult) {
                                        if (!dataItemResult.getStatus()
                                                .isSuccess()) {
                                            Log.e(TAG,
                                                    "ERROR: failed to putDataItem, status code: "
                                                            + dataItemResult
                                                            .getStatus()
                                                            .getStatusCode());
                                        }
                                    }
                                });
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the connected nodes and wait for results
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi
                        .getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    // Send a message and wait for result
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(),
                            Consts.WearConsts.DATA_ITEM_RECEIVED_PATH, new byte[0])
                            .await();
                    if (result.getStatus().isSuccess()) {
                        Log.v("myTag",
                                "Message sent to : " + node.getDisplayName());
                    } else {
                        // Log an error
                        Log.v("myTag", "MESSAGE ERROR: failed to send Message");
                    }
                }
            }
        }).start();
    }

    private void checkData() {
        if (mSharePrefs.getCurrentLibrary().equals(Consts.MY_LIBRARY)) return;
        int newWord = database.countNewWord(mSharePrefs.getCurrentLibrary());
        int count = database.countWord(mSharePrefs.getCurrentLibrary());
        int cardCount = database.countCard(mSharePrefs.getCurrentLibrary());

        if (count == cardCount && newWord == 0 && cardCount > 0) {
            // you learn all of words
            if (mDialogLearnAll == null) {
                mDialogLearnAll = ConfirmDialog.newInstance(
                        getString(R.string.msg_learn_all_words), null, this);
            }
            if (!mDialogLearnAll.isShowing() && !isFinishing()) {
                mDialogLearnAll.show(getSupportFragmentManager(),
                        DIALOG_TAG_LEARN_ALL);
            }
        } else if (count == 0
                || (newWord == 0 && mSharePrefs.getShowAlertPickCard())) {
            // first, we must check do we have any new word, if not, show
            // word picker activity
            if (mDialogCardPicker == null) {
                mDialogCardPicker = ConfirmDialog.newInstance(
                        getString(R.string.select_card_hint), null, count != 0,
                        R.string.ok, this);
            }
            if (!mDialogCardPicker.isShowing() && !isFinishing()) {
                mSharePrefs.saveShowAlertPickCard(false);
                mDialogCardPicker.show(getSupportFragmentManager(),
                        DIALOG_TAG_CARD_PICKER);
            }
        }
    }

    private void traceRate(String event) {
        Tracker t = MyApplication.getTracker();
        if (t != null) {
            t.send(new HitBuilders.EventBuilder().setCategory("Rate")
                    .setAction(event).build());
        }
    }

    private void initialiseBilling() {
        if (mHelper != null) {
            return;
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, Consts.PURCHASE_LICENSE_KEY);

        // Enable debug logging (for a production application, you should set this to false).
        // mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

                // Something went wrong
                if (!result.isSuccess()) {
                    Log.e(TAG, "Problem setting up in-app billing: "
                            + result.getMessage());
                    return;
                }

                Log.d(TAG, "init google billing successful");

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                if (!mSharePrefs.getIsCheckPurchased()) {
                    mHelper.queryInventoryAsync(iabInventoryListener());
                }
            }
        });
    }

    /**
     * Listener that's called when we finish querying the items and subscriptions we own
     */
    private IabHelper.QueryInventoryFinishedListener iabInventoryListener() {
        return new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

                // Something went wrong
                if (!result.isSuccess()) {
                    return;
                }

                Log.d(TAG, "has fullversion purchase: " + inventory.hasPurchase(Consts.PURCHASE_REMOVE_ADS));
                mSharePrefs.saveIsCheckPurchased(true);
                // Do we have the premium upgrade?
                Purchase purchasePro = inventory.getPurchase(Consts.PURCHASE_REMOVE_ADS);
                if (purchasePro != null) {
                    mSharePrefs.saveIsPurchased(true);
                } else {
                    mSharePrefs.saveIsPurchased(false);
                }
            }
        };
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                return;
            } else if (purchase.getSku().equals(Consts.PURCHASE_REMOVE_ADS)) {
                // save setting
                mSharePrefs.saveIsPurchased(true);
                // show toast
                showCenterToast((R.string.msg_has_purchase));
            }
        }
    };

    @Override
    public void onDialogYes(AppDialog<?> f) {
        finish();
    }

    @Override
    public void onDialogNo(AppDialog<?> f) {
    }

    private RequestErrorListener mGetListLibraryErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            Log.e("MyActivity", "status: " + status + ", code: " + code + ", message: " + message);
        }
    };

    private Response.Listener<ListLibraryResource> mGetListLibrarySuccessListener = new Response.Listener<ListLibraryResource>() {
        @Override
        public void onResponse(ListLibraryResource response) {
            Log.e("MyActivity", "mGetListLibrarySuccessListener");
        }
    };

    @Override
    public void onSearchLibraryCancel() {

    }

    @Override
    public void onSearchLibraryStart(String key) {
        if (!"".equalsIgnoreCase(key)) {
            Log.e("KeySearch ", key);
            Bundle bundle = new Bundle();
            bundle.putString(Consts.PARAMConsts.TOKEN, mSharePrefs.getToken());
            bundle.putString(Consts.PARAMConsts.KEY_SEARCH, key);
            showProgress(getString(R.string.processing));
            mRequestManager.request(Actions.SEARCHLIBRARY, bundle, mSearchLibrarySuccessListener, mSearchLibraryErrorListener);
        }
    }

    private RequestErrorListener mSearchLibraryErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            Log.e("UserDrawerFragment", " | mSearchLibraryErrorListener" + "status: " + status + ", code: " + code + ", message: " + message);
            dismissProgress();
        }
    };

    private Response.Listener<ListLibraryResource> mSearchLibrarySuccessListener = new Response.Listener<ListLibraryResource>() {
        @Override
        public void onResponse(ListLibraryResource response) {
            Log.e("UserDrawerFragment", " | mSearchLibrarySuccessListener ");
            dismissProgress();
            if (Consts.LIST_LIBRARY_SEARCH.size() > 0) {
                ArrayList<LibraryTable> libraries = new ArrayList<LibraryTable>();
                for (int j = 0; j < Consts.LIST_LIBRARY_SEARCH.size(); j++) {
                    LibraryTable libraryTable = new LibraryTable(Consts.LIST_LIBRARY_SEARCH.get(j).getName(), Consts.LIST_LIBRARY_SEARCH.get(j).getName());
                    libraries.add(libraryTable);
                }
                LibraryListDialog.newInstance(libraries, MyActivity.this).show(
                        getSupportFragmentManager(), null);
            } else {
                SearchLibraryDialog.newInstance(MyActivity.this, "Search Library", "Cannot find library").show(getSupportFragmentManager(), null);
            }
        }
    };

    //load photo user
    private class LoadProfileImage extends AsyncTask<Void, Void, Bitmap> {
        CircularImageView downloadedImage;

        public LoadProfileImage(CircularImageView image) {
            this.downloadedImage = image;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            String url = convertLinkImage(SharePrefs.getInstance().get(Consts.LINK_PICTURE, ""));

            Bitmap icon = null;

            try {
                InputStream in = new java.net.URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return icon;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                downloadedImage.setImageBitmap(bitmap);
            } else {
                downloadedImage.setImageResource(R.drawable.img_icon_user_avatar);
            }

        }
    }

    private String convertLinkImage(String link) {

        String[] items = link.split("=");
        String link_ouput = "";


        return link_ouput = items[0] + "=128";
    }
    /*
    custom tool bar
     */

    private Toolbar mToolbar;
    public static DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView1;

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mToolbar.setTitle(getString(R.string.app_name));
            mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    public static DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    private void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
