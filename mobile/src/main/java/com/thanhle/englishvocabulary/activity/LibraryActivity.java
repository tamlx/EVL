package com.thanhle.englishvocabulary.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.adapter.ListCardWordAdapter;
import com.thanhle.englishvocabulary.resource.LibraryInfosResource;
import com.thanhle.englishvocabulary.utils.Consts;

public class LibraryActivity extends BaseActivity {

    private TextView tvDes1, tvDes2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Bundle bundle = getIntent().getExtras();
        LibraryInfosResource data = (LibraryInfosResource) bundle.getSerializable("list_card");
        if (data != null) {
            Log.e("LibraryActivity", " list card: " + data.cards.length);
            ListView lvCard = (ListView) findViewById(R.id.lvCardWords);
            ListCardWordAdapter listCardWordAdapter = new ListCardWordAdapter(LibraryActivity.this, data.cards);
            lvCard.setAdapter(listCardWordAdapter);
        } else {
            Log.e("LibraryActivity", " list card: null");
        }

        tvDes1 = (TextView) findViewById(R.id.tvDescription1);
        tvDes2 = (TextView) findViewById(R.id.tvDescription2);

        tvDes1.setText(Consts.LIBRARY_INFO.getName());

        tvDes2.setText("Number of words: " + Consts.LIBRARY_INFO.getCard_total() + "\n" + "Downloaded: " + Consts.LIBRARY_INFO.getDownloaded());


    }

}
