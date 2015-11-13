package com.thanhle.englishvocabulary.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.activity.SplashActivity;
import com.thanhle.englishvocabulary.activity.UpdateProfileActivity;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.dialog.AppDialog;
import com.thanhle.englishvocabulary.dialog.LibraryListDialog;
import com.thanhle.englishvocabulary.dialog.SearchLibraryDialog;
import com.thanhle.englishvocabulary.requestmanagement.RequestErrorListener;
import com.thanhle.englishvocabulary.requestmanagement.RequestManager;
import com.thanhle.englishvocabulary.resource.ListLibraryResource;
import com.thanhle.englishvocabulary.resource.UserResource;
import com.thanhle.englishvocabulary.utils.Actions;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.SharePrefs;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class UserDrawerFragment extends BaseFragment implements View.OnClickListener, LibraryListDialog.LibraryListDialogListener, SearchLibraryDialog.SearchLibraryDialogListener {
    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;
    private static TextView tvUsername;
    private RequestManager mRequestManager = RequestManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRoot = inflater.inflate(
                R.layout.fragment_user_drawer, container, false);
        ImageView imgAvata = (ImageView) mRoot.findViewById(R.id.imgUserAvatar);
        new LoadProfileImage(imgAvata).execute();

        View btnLogout = mRoot.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        UserResource user = mSharePrefs.getUserInfo();
        if (user != null) {
            tvUsername = (TextView) mRoot.findViewById(R.id.tvUserName);
            if (user.fullname != null && !user.fullname.equalsIgnoreCase("")) {
                tvUsername.setText(user.fullname);
            } else {
                tvUsername.setText(user.username);
            }
        }
        ListView lvNavigationMenu = (ListView) mRoot.findViewById(R.id.lvNavigationMenu);
        final String[] menu;
        if (SharePrefs.getInstance().get(Consts.LOGINFACEBOOK, false) || SharePrefs.getInstance().get(Consts.LOGINGOOGLE, false)) {
            menu = new String[]{"Change Library", "Search Library", "Settings", "-"};
        } else {
            menu = getResources().getStringArray(R.array.array_navigation);
        }
        lvNavigationMenu.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, menu));
        lvNavigationMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!SharePrefs.getInstance().get(Consts.LOGINFACEBOOK, false) && !SharePrefs.getInstance().get(Consts.LOGINGOOGLE, false)) {
                    switch (i) {
                        case 0:
//                        showPopupUpdateProfile();
                            Intent updateProfile = new Intent(getActivity(), UpdateProfileActivity.class);
                            startActivity(updateProfile);
                            closeDrawer();
                            break;
                        case 1:
                            ArrayList<LibraryTable> libraries = new ArrayList<LibraryTable>();
                            for (int j = 0; j < Consts.LIST_LIBRARY.size(); j++) {
                                LibraryTable libraryTable = new LibraryTable(Consts.LIST_LIBRARY.get(j).getName(), Consts.LIST_LIBRARY.get(j).getName());
                                libraries.add(libraryTable);
                            }

                            LibraryListDialog.newInstance(libraries, UserDrawerFragment.this).show(
                                    getActivity().getSupportFragmentManager(), null);
                            break;
                        case 2:
                            SearchLibraryDialog.newInstance(UserDrawerFragment.this, "Search Library", "").show(getActivity().getSupportFragmentManager(), null);
                            break;
                        case 3:
                            Toast.makeText(getActivity(), "Click menu: " + menu[3], Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(getActivity(), "Click menu: " + menu[4], Toast.LENGTH_SHORT).show();
                            break;

                    }
                } else {
                    switch (i) {
                        case 0:
                            ArrayList<LibraryTable> libraries = new ArrayList<LibraryTable>();
                            for (int j = 0; j < Consts.LIST_LIBRARY.size(); j++) {
                                LibraryTable libraryTable = new LibraryTable(Consts.LIST_LIBRARY.get(j).getName(), Consts.LIST_LIBRARY.get(j).getName());
                                libraries.add(libraryTable);
                            }

                            LibraryListDialog.newInstance(libraries, UserDrawerFragment.this).show(
                                    getActivity().getSupportFragmentManager(), null);
                            break;
                        case 1:
                            SearchLibraryDialog.newInstance(UserDrawerFragment.this, "Search Library", "").show(getActivity().getSupportFragmentManager(), null);
                            break;
                        case 2:
                            Toast.makeText(getActivity(), "Click menu: " + menu[2], Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(getActivity(), "Click menu: " + menu[3], Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            }
        });
        // request root focus
        mRoot.requestFocus();

        return mRoot;
    }

    public static void setTvUsername(String name) {
        if (tvUsername != null) {
            tvUsername.setText(name);
        }
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
    public void onDetach() {
        super.onDetach();
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
            case R.id.btnLogout:
                mSharePrefs.clear();
                getActivity().finish();
                Intent i = new Intent(getActivity(), SplashActivity.class);
                startActivity(i);
                break;


        }
    }

    @Override
    public void onLibraryCancel(AppDialog<?> f) {

    }

    @Override
    public void onLibraryChoose(AppDialog<?> f, String library,String library_name) {
        Toast.makeText(getActivity(), library, Toast.LENGTH_SHORT).show();
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

    private class LoadProfileImage extends AsyncTask<Void, Void, Bitmap> {
        ImageView downloadedImage;

        public LoadProfileImage(ImageView image) {
            this.downloadedImage = image;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            String url = SharePrefs.getInstance().get(Consts.LINK_PICTURE, "");
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
                downloadedImage.setBackgroundResource(R.drawable.img_icon_user_avatar);
            }

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
                LibraryListDialog.newInstance(libraries, UserDrawerFragment.this).show(
                        getActivity().getSupportFragmentManager(), null);
            } else {
                SearchLibraryDialog.newInstance(UserDrawerFragment.this, "Search Library", "Cannot find library").show(getActivity().getSupportFragmentManager(), null);
            }
        }
    };


}
