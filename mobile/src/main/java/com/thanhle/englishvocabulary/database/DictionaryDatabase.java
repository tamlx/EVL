package com.thanhle.englishvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.thanhle.englishvocabulary.database.tables.DictionaryTable;
import com.thanhle.englishvocabulary.utils.Consts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by thanhle on 9/20/2014.
 */
public class DictionaryDatabase {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "dictionary_database";
    public static final int BUFFER = 4000;
    private SQLiteDatabase db;

    public DictionaryDatabase(Context context) {
        String DB_PATH = getDatabaseFileName(context);
        db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        String sql = "CREATE TABLE IF NOT EXISTS " + DictionaryTable.TABLE_NAME + " ("
                + DictionaryTable.COLUMN_WORD + " text,"
                + DictionaryTable.COLUMN_MEAN + " text" + ")";
        db.execSQL(sql);
    }

    public static String getDatabaseFileName(Context context) {
        return context.getCacheDir() + "/" + Consts.DICT_FOLDER_NAME + "/dict_en_vi.db";
    }

    /**
     * check dictionary has data
     *
     * @return true if dictionary has data
     */
    public int countDictionary() {
        String sql = "SELECT COUNT(*) FROM " + DictionaryTable.TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        if (c != null && c.moveToFirst()) {
            int count = c.getInt(0);
            Log.e("DictionaryDatabase", "Dictionary size: " + count);
            c.close();
            return count;
        } else {
            return 0;
        }
    }

    public void clearDictionary() {
        db.delete(DictionaryTable.TABLE_NAME, null, null);
    }

    private static void insertWords(SQLiteDatabase db, ArrayList<DictionaryTable> dicts) {
        db.beginTransaction();
        try {
            ContentValues value = new ContentValues();
            for (DictionaryTable dict : dicts) {
                value.put(DictionaryTable.COLUMN_WORD, dict.word);
                value.put(DictionaryTable.COLUMN_MEAN, dict.mean);
                db.insert(DictionaryTable.TABLE_NAME, null, value);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * find word in dictionary
     *
     * @param word word want to find
     * @return
     */
    public DictionaryTable findWord(String word) {
        Cursor c = db.query(DictionaryTable.TABLE_NAME, null, DictionaryTable.COLUMN_WORD + "=?", new String[]{word}, null, null, null);
        if (c != null && c.moveToFirst()) {
            DictionaryTable r = new DictionaryTable(c);
            return r;
        } else {
            return null;
        }
    }

    /**
     * get list of word when user typing to auto complete
     *
     * @param word word want to find
     * @return
     */
    public ArrayList<String> getAutoCompleteWord(String word) {
        ArrayList<String> r = new ArrayList<String>();
        String sql = "SELECT " + DictionaryTable.COLUMN_WORD + " FROM " + DictionaryTable.TABLE_NAME + " WHERE " + DictionaryTable.COLUMN_WORD + " LIKE '" + word.toLowerCase(Locale.US) + "%' LIMIT 20";
        Cursor c = db.rawQuery(sql, null);
        if (c != null && c.moveToFirst()) {
            do {
                r.add(c.getString(0));
            } while (c.moveToNext());
            c.close();
        }
        return r;
    }

    /**
     * read dictionary from raw resource and insert to dictionary database
     *
     * @param context context
     * @return true if success
     */
    public static boolean readDictionary(Context context, InputStream in, int start, Handler handler) {
        String DB_PATH = getDatabaseFileName(context);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        String line;
        int lineCount = 0;
        ArrayList<DictionaryTable> cards = new ArrayList<DictionaryTable>();
        try {
            while ((line = bf.readLine()) != null) {
                lineCount++;
                if (lineCount > start) {
                    cards.add(new DictionaryTable(line));
                    if (lineCount % BUFFER == 0) {
                        insertWords(db, cards);
                        cards.clear();
                        handler.sendEmptyMessage(1);
                    }
                }
            }
            insertWords(db, cards);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.close();
        return true;
    }

    public void close() {
        db.close();
    }

}
