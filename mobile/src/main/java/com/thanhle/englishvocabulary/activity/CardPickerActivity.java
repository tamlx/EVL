package com.thanhle.englishvocabulary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.database.tables.WordTable;
import com.thanhle.englishvocabulary.dialog.AppDialog;
import com.thanhle.englishvocabulary.dialog.ConfirmDialog;
import com.thanhle.englishvocabulary.dialog.LibraryListDialog;
import com.thanhle.englishvocabulary.fragment.CardPickerFragment;
import com.thanhle.englishvocabulary.utils.DataUtils;

import java.util.ArrayList;

/**
 * Created by thanhle on 9/21/2014.
 */
public class CardPickerActivity extends BaseActivity implements View.OnClickListener, LibraryListDialog.LibraryListDialogListener, ConfirmDialog.ConfirmDialogListener {
    private CardPickerFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_picker);

        if (savedInstanceState == null) {
            mFragment = new CardPickerFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, mFragment);
            fragmentTransaction.commit();
        } else {
            mFragment = (CardPickerFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_pick, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_library:
                ArrayList<LibraryTable> libraries = DataUtils.getListLibrary(this);
                LibraryListDialog.newInstance(libraries, this).show(
                        getSupportFragmentManager(), null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLibraryCancel(AppDialog<?> f) {
    }

    @Override
    public void onLibraryChoose(AppDialog<?> f, String library, String library_name) {
        mSharePrefs.saveCurrentLibrary(library);
        mSharePrefs.saveIsWearSync(false);
        mFragment.loadData();
    }

    @Override
    public void onOk(AppDialog<?> f) {
        ArrayList<CardTable> mListCardSelect = mFragment.getListSelectedCard();
        // insert new word
        for (CardTable card : mListCardSelect) {
            database.insertWord(WordTable.convertCardToNewWord(card));
        }
        // reset sync flash to false
        mSharePrefs.saveIsWearSync(false);
        // start main activity
        Intent i = new Intent(this, MyActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /**
     * show confirm dialog after finish choose card
     */
    public void showFinishChooseCardDialog() {
        ConfirmDialog.newInstance(
                getString(R.string.msg_finish_choose_word), null, this).show(
                getSupportFragmentManager(), null);
    }
}
