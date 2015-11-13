package com.thanhle.englishvocabulary.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.adapter.ListLibraryAdapter;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.dialog.SearchLibraryDialog;
import com.thanhle.englishvocabulary.requestmanagement.RequestErrorListener;
import com.thanhle.englishvocabulary.resource.LibraryInfosResource;
import com.thanhle.englishvocabulary.resource.ListLibraryResource;
import com.thanhle.englishvocabulary.utils.Actions;
import com.thanhle.englishvocabulary.utils.Consts;

public class ListLibraryActivity extends BaseActivity implements SearchLibraryDialog.SearchLibraryDialogListener {

    private ProgressBar progressBar;
    private float mReadDictionaryCount = 0;
    private float increase;
    private TextView tvLoading;
    private ListView listLibrary;
    private ListLibraryAdapter listLibraryAdapter;
    private Toolbar mToolbar;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mReadDictionaryCount += 1;
                if (progressBar != null) {
                    progressBar.setProgress((int) mReadDictionaryCount);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_library);

        setUpToolbar();

        progressBar = (ProgressBar) findViewById(R.id.progressBarListLibrary);
        progressBar.setIndeterminate(true);
        tvLoading = (TextView) findViewById(R.id.tvLoadingListLibrary);

        listLibrary = (ListView) findViewById(R.id.list_library);
        listLibraryAdapter = new ListLibraryAdapter(this, Consts.LIST_LIBRARY);
        listLibrary.setAdapter(listLibraryAdapter);

        if (Consts.LIST_LIBRARY.size() == 0) {
            if (isConnectInternet()) {
                showProgress(getString(R.string.processing));
                Bundle bundle = new Bundle();
                bundle.putString(Consts.PARAMConsts.TOKEN, mSharePrefs.getToken());
                mRequestManager.request(Actions.GETLISTLIBRARY, bundle, mGetListLibrarySuccessListener, mGetListLibraryErrorListener);
            } else {
                showCenterToast(getString(R.string.getlist_library_error));
            }
        }

        listLibrary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showProgress(getString(R.string.processing));
                Bundle bundle = new Bundle();
                if (Consts.LIST_LIBRARY_SEARCH.size() > 0) {
                    for (int i = 0; i < Consts.LIST_LIBRARY.size(); i++) {
                        if (Consts.LIST_LIBRARY.get(i).get_id().equalsIgnoreCase(Consts.LIST_LIBRARY_SEARCH.get(position).get_id())) {
                            bundle.putString(Consts.PARAMConsts.KEY_LIBRARY_ID, Consts.LIST_LIBRARY.get(i).get_id());
                            break;
                        }
                    }
                } else {
                    bundle.putString(Consts.PARAMConsts.KEY_LIBRARY_ID, Consts.LIST_LIBRARY.get(position).get_id());
                }

                bundle.putString(Consts.PARAMConsts.TOKEN, mSharePrefs.getToken());
                mRequestManager.request(Actions.DOWNLOAD_LIBRARY, bundle, mDownloadLibrarySuccessListener, mDownloadLibraryErrorListener);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_library, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_library:
                if (isConnectInternet()) {
                    SearchLibraryDialog.newInstance(ListLibraryActivity.this, "Search Library", "").show(getSupportFragmentManager(), null);
                } else {
                    showCenterToast(getString(R.string.not_connect_internet));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            mToolbar.setTitle(getString(R.string.library_activity_title));
            mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private RequestErrorListener mDownloadLibraryErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            Log.e("UserDrawerFragment", " | mSearchLibraryErrorListener" + "status: " + status + ", code: " + code + ", message: " + message);
            dismissProgress();

        }
    };

    private Response.Listener<LibraryInfosResource> mDownloadLibrarySuccessListener = new Response.Listener<LibraryInfosResource>() {
        @Override
        public void onResponse(final LibraryInfosResource response) {
            Log.e("ListLibraryActivity", " | mGetLibraryInfoSuccessListener ");


            final int sizeResponse = response.cards.length;

            Log.e("ListLibraryActivity", " response size: " + sizeResponse);

            if (!database.checkLibraryExists(response._id)) {
//                progressBar.setVisibility(View.VISIBLE);
//                tvLoading.setVisibility(View.VISIBLE);
//                progressBar.setIndeterminate(false);
//                tvLoading.setText(R.string.loading_dictionary);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < sizeResponse; i++) {
                            CardTable cardTable = new CardTable(response.cards[i].word.toString(), response.cards[i].phonetically.toString(), response.cards[i].type.toString(), response.cards[i].meanEng.toString(), response.cards[i].mean.toString(), response.cards[i].exam.toString(), response.cards[i].library.toString());
                            database.insertCard(cardTable);
//                            if (i % ((float) sizeResponse / 100) == 0)
//                                mHandler.sendEmptyMessage(1);
                        }
                        LibraryTable libraryTable = new LibraryTable(response._id, response.name);
                        database.insertLibrary(libraryTable);
                        Log.e("ListLibraryActivity", " insert data complete ");
                        onLoadDataComplete(response);


                    }
                }).start();
            } else {
                onLoadDataComplete(response);
            }
        }
    };

    private void onLoadDataComplete(final LibraryInfosResource response) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
                tvLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                listLibraryAdapter.notifyDataSetChanged();

                Intent libraryInfo = new Intent(ListLibraryActivity.this, LibraryActivity.class);
                if (response != null) {
                    if (response.cards.length > 0) {
                        Consts.install_new_library = true;
                        mSharePrefs.saveCurrentLibrary(response._id);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list_card", response);
                    libraryInfo.putExtras(bundle);
                }
                startActivity(libraryInfo);

            }
        });
    }

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
            Toast.makeText(ListLibraryActivity.this, getString(R.string.error_search_library), Toast.LENGTH_SHORT).show();
        }
    };

    private Response.Listener<ListLibraryResource> mSearchLibrarySuccessListener = new Response.Listener<ListLibraryResource>() {
        @Override
        public void onResponse(ListLibraryResource response) {
            Log.e("UserDrawerFragment", " | mSearchLibrarySuccessListener ");
            dismissProgress();
            if (Consts.LIST_LIBRARY_SEARCH.size() > 0) {
//                ArrayList<LibraryTable> libraries = new ArrayList<LibraryTable>();
//                for (int j = 0; j < Consts.LIST_LIBRARY_SEARCH.size(); j++) {
//                    LibraryTable libraryTable = new LibraryTable(Consts.LIST_LIBRARY_SEARCH.get(j).get_id(), Consts.LIST_LIBRARY_SEARCH.get(j).getName());
//                    libraries.add(libraryTable);
//                }

                listLibraryAdapter = new ListLibraryAdapter(ListLibraryActivity.this, Consts.LIST_LIBRARY_SEARCH);
                listLibrary.setAdapter(listLibraryAdapter);
                listLibraryAdapter.notifyDataSetChanged();

                Log.e("ListLibraryActivity", " change list library (search)");

            } else {
                Toast.makeText(ListLibraryActivity.this, getString(R.string.error_search_library), Toast.LENGTH_SHORT).show();
                Log.e("ListLibraryActivity", " list library (search) null");

                listLibraryAdapter = new ListLibraryAdapter(ListLibraryActivity.this, Consts.LIST_LIBRARY);
                listLibrary.setAdapter(listLibraryAdapter);
                listLibraryAdapter.notifyDataSetChanged();
            }
        }
    };
    private RequestErrorListener mGetListLibraryErrorListener = new RequestErrorListener() {
        @Override
        public void onError(int status, String code, String message) {
            dismissProgress();
            Log.e("MyActivity", "status: " + status + ", code: " + code + ", message: " + message);
        }
    };

    private Response.Listener<ListLibraryResource> mGetListLibrarySuccessListener = new Response.Listener<ListLibraryResource>() {
        @Override
        public void onResponse(ListLibraryResource response) {
            Log.e("MyActivity", "mGetListLibrarySuccessListener");
            dismissProgress();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listLibraryAdapter.notifyDataSetChanged();
                }
            });

        }
    };

}
