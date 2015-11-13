package com.thanhle.englishvocabulary.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.adapter.DictionaryAdapter;
import com.thanhle.englishvocabulary.adapter.DictionaryAdapterChildResource;
import com.thanhle.englishvocabulary.adapter.DictionaryAdapterResource;
import com.thanhle.englishvocabulary.database.DictionaryDatabase;
import com.thanhle.englishvocabulary.database.tables.DictionaryTable;
import com.thanhle.englishvocabulary.utils.AppUtils;
import com.thanhle.englishvocabulary.utils.TTS;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class DictionaryDrawerFragment extends BaseFragment implements View.OnClickListener {
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mListView;
    private View mFragmentContainerView;
    private AutoCompleteTextView etWord;
    private View btnLookup;
    private View emptyView;
    private TextView tvPhonetically;
    private View btnSpeak;
    private boolean mIsSetText = false;

    private DictionaryAdapter mAdapter;
    private DictionaryDatabase database;
    private AutoCompleteTask mAutoCompleteTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRoot = inflater.inflate(
                R.layout.fragment_dictionary_drawer, container, false);
        etWord = (AutoCompleteTextView) mRoot.findViewById(R.id.etWord);
        btnLookup = mRoot.findViewById(R.id.btnLookup);
        tvPhonetically = (TextView) mRoot.findViewById(R.id.tvPhonetically);
        btnSpeak = mRoot.findViewById(R.id.btnSpeak);
        mListView = (ExpandableListView) mRoot.findViewById(R.id.listView);
        emptyView = mRoot.findViewById(R.id.empty_view);

        // init auto complete text view
        etWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String word = (String) adapterView.getItemAtPosition(i);
                setDictionaryWord(word);
            }
        });
        etWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String word = charSequence.toString();
                if (TextUtils.isEmpty(word)) return;
                // cancel previous task
                if (mAutoCompleteTask != null) {
                    mAutoCompleteTask.cancel(true);
                }
                mAutoCompleteTask = new AutoCompleteTask();
                mAutoCompleteTask.execute(word);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    new FindWordAsyncTask().execute(etWord.getText().toString());
                    etWord.dismissDropDown();
                    AppUtils.hideKeyBoard(etWord);
                    return true;
                }
                return false;
            }
        });

        // init ListView
        emptyView.setVisibility(View.GONE);
        mListView.setGroupIndicator(null);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                }
                if (mCallbacks != null) {
                    mCallbacks.onDictionarySelect((DictionaryAdapterResource) mAdapter.getGroup(i), (DictionaryAdapterChildResource) mAdapter.getChild(i, i2));
                }
                return false;
            }
        });

        mRoot.setOnClickListener(this);
        btnLookup.setOnClickListener(this);
        mRoot.findViewById(R.id.btnDelete).setOnClickListener(this);
        btnSpeak.setOnClickListener(this);

        // request root focus
        mRoot.requestFocus();

        return mRoot;
    }

    public void setDictionaryWord(String word) {
        mIsSetText = true;
        etWord.setText(word);
        new FindWordAsyncTask().execute(word);
    }

    public void openDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
            database = new DictionaryDatabase(activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        database.close();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSpeak:
                if (mAdapter != null && mAdapter.getGroupCount() > 0) {
                    String word = ((DictionaryAdapterResource) mAdapter.getGroup(0)).word;
                    TTS.getInstance().speak(word);
                }
                break;
            case R.id.btnDelete:
                etWord.setText("");
                break;
            case R.id.btnLookup:
                if (etWord.getText() == null) return;
                new FindWordAsyncTask().execute(etWord.getText().toString());
                etWord.dismissDropDown();
                break;
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onDictionarySelect(DictionaryAdapterResource group, DictionaryAdapterChildResource ite);
    }

    private class FindWordAsyncTask extends AsyncTask<String, Void, DictionaryTable> {

        @Override
        protected DictionaryTable doInBackground(String... params) {
            if (TextUtils.isEmpty(params[0]) || TextUtils.isEmpty((params[0].trim()))) return null;
            return database.findWord(params[0].trim());
        }

        @Override
        protected void onPostExecute(DictionaryTable dictionaryTable) {
            if (dictionaryTable == null) {
                mListView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                mListView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                mAdapter = new DictionaryAdapter(getActivity(), dictionaryTable.convertToDictionaryAdapterResource());
                mListView.setAdapter(mAdapter);
                if (mAdapter.getGroupCount() > 0) {
                    mListView.expandGroup(0);
                    tvPhonetically.setText(((DictionaryAdapterResource) mAdapter.getGroup(0)).phonetically);
                }
            }
        }
    }

    private class AutoCompleteTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            if (mIsSetText) {
                return null;
            } else
                return database.getAutoCompleteWord(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (!isCancelled() && strings != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, strings);
                etWord.setAdapter(adapter);
            }
            if (mIsSetText) {
                etWord.dismissDropDown();
                mIsSetText = false;
            } else {
                etWord.showDropDown();
            }
        }

    }
}
