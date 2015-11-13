package com.thanhle.englishvocabulary.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.adapter.DictionaryAdapter;
import com.thanhle.englishvocabulary.database.DictionaryDatabase;
import com.thanhle.englishvocabulary.database.tables.DictionaryTable;

public class TestDictionaryActivity extends BaseActivity {
    private EditText etWord;
    private View btnLookup;
    private ExpandableListView mListView;
    private DictionaryDatabase database;
    private DictionaryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dictionary);
        database = new DictionaryDatabase(this);

        etWord = (EditText) findViewById(R.id.etWord);
        btnLookup = findViewById(R.id.btnLookup);
        mListView = (ExpandableListView) findViewById(R.id.listView);
        mListView.setGroupIndicator(null);
        mListView.setEmptyView(findViewById(R.id.empty_view));

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                showCenterToast(i + " " + i2);
                return false;
            }
        });

        btnLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DictionaryTable dict = database.findWord(etWord.getText().toString());
                if (dict == null) {
                    showCenterToast("Not found");
                } else {
                    mAdapter = new DictionaryAdapter(TestDictionaryActivity.this, dict.convertToDictionaryAdapterResource());
                    mListView.setAdapter(mAdapter);
                    for (int i=0; i < mAdapter.getGroupCount();i++) {
                        mListView.expandGroup(i);
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.close();
    }
}
