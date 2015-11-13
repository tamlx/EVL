package com.thanhle.englishvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.database.tables.WordTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "englishvocabulary_database";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + WordTable.TABLE_NAME + "("
                + "_id integer primary key," + WordTable.COLUMN_LIBRARY
                + " text," + WordTable.COLUMN_WORD + " text,"
                + WordTable.COLUMN_IS_NEW + " integer,"
                + WordTable.COLUMN_FORGET_COUNT + " integer,"
                + WordTable.COLUMN_IS_NEW_TESTING + " integer" + ")";
        db.execSQL(sql);

        sql = "CREATE TABLE " + CardTable.TABLE_NAME + "("
                + "_id integer primary key," + CardTable.COLUMN_WORD + " text,"
                + CardTable.COLUMN_PHONETICALLY + " text,"
                + CardTable.COLUMN_TYPE + " text," + CardTable.COLUMN_MEAN_ENG
                + " text," + CardTable.COLUMN_MEAN + " text,"
                + CardTable.COLUMN_EXAM + " text," + CardTable.COLUMN_LIBRARY
                + " text" + ")";
        db.execSQL(sql);

        sql = "CREATE TABLE " + LibraryTable.TABLE_NAME + "("
                + LibraryTable.COLUMN_CODE + " text primary key,"
                + LibraryTable.COLUMN_NAME + " text" + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * get list word order by forget count desc (using in word board)
     *
     * @param library
     * @return
     */
    public ArrayList<WordTable> getListWordByForget(String library,
                                                    boolean forget) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<WordTable> r = new ArrayList<WordTable>();
        String sql = "SELECT * FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                + WordTable.COLUMN_FORGET_COUNT + (forget ? ">0" : "=0")
                + " ORDER BY " + WordTable.COLUMN_WORD;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                WordTable log = new WordTable(cursor.getString(1),
                        cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                r.add(log);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return r;
    }

    /**
     * get range value of word forget (highest - lowest)
     *
     * @param library
     * @return
     */
    public int getWordForgetHighest(String library) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT max(" + WordTable.COLUMN_FORGET_COUNT + ") FROM "
                + WordTable.TABLE_NAME + " WHERE " + WordTable.COLUMN_LIBRARY
                + "='" + library + "'";
        int max = 0;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                max = cursor.getInt(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return max;
    }

    public int getWordForgetLowest(String library) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT min(" + WordTable.COLUMN_FORGET_COUNT + ") FROM "
                + WordTable.TABLE_NAME + " WHERE " + WordTable.COLUMN_LIBRARY
                + "='" + library + "'";
        int min = 0;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                min = cursor.getInt(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return min;
    }

    /**
     * get list new word (word forget, not remember)
     *
     * @param library
     * @param max     0 will return all
     * @return
     */
    public ArrayList<WordTable> getListNewWords(String library, int max) {
        return getListWords(library, max, 1, true);
    }

    /**
     * get list old word (word remember)
     *
     * @param library
     * @param max     max=0 will return all
     * @return
     */
    public ArrayList<WordTable> getListOldWords(String library, int max) {
        return getListWords(library, max, 0, false);
    }

    /**
     * get list of old and forget word
     *
     * @param library
     * @param max     max=0 will return all
     * @return
     */
    public ArrayList<WordTable> getListOldWordForget(String library, int max) {
        return getListWords(library, max, 0, true);
    }

    /**
     * get list of words
     *
     * @param library
     * @param max
     * @return
     */
    public ArrayList<WordTable> getListWords(String library, int max) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<WordTable> r = new ArrayList<WordTable>();
        String sql = "SELECT * FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' "
                + (max == 0 ? "" : " ORDER BY RANDOM() LIMIT " + max);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                WordTable log = new WordTable(cursor.getString(1),
                        cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                r.add(log);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return r;
    }

    /**
     * get list word in not testing state
     *
     * @param library
     * @param max      max=0 don't care
     * @param isNew
     * @param isForget isFOrget=-1 don't care
     * @return
     */
    private ArrayList<WordTable> getListWords(String library, int max,
                                              int isNew, boolean isForget) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<WordTable> r = new ArrayList<WordTable>();
        String sql = "SELECT * FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                + WordTable.COLUMN_IS_NEW + "=" + isNew + " AND "
                + WordTable.COLUMN_FORGET_COUNT + (isForget ? ">0" : "=0")
                + (max == 0 ? "" : " ORDER BY RANDOM() LIMIT " + max);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                WordTable log = new WordTable(cursor.getString(1),
                        cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                r.add(log);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return r;
    }

    /**
     * count total word in database
     *
     * @param library
     * @return
     */
    public int countWord(String library) {
        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0;
        String sql = "SELECT count(*) FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    /**
     * count new word in list
     *
     * @param library
     * @return
     */

    public int countWordLearn(String library) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT count(*) FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                + WordTable.COLUMN_IS_NEW + "= 0" + " AND "
                + WordTable.COLUMN_FORGET_COUNT + "= 0";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public int countWordForget(String library) {
        int count = 0, value1 = 0, value2 = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        String sql1 = "SELECT count(*) FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                + WordTable.COLUMN_IS_NEW + "= 0" + " AND "
                + WordTable.COLUMN_FORGET_COUNT + "> 0";
        Cursor cursor1 = db.rawQuery(sql1, null);
        if (cursor1 != null && cursor1.moveToFirst()) {
            value1 = cursor1.getInt(0);
            cursor1.close();
        }

        String sql2 = "SELECT count(*) FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                + WordTable.COLUMN_IS_NEW + "= 1" + " AND "
                + WordTable.COLUMN_FORGET_COUNT + "> 1";
        Cursor cursor2 = db.rawQuery(sql2, null);
        if (cursor2 != null && cursor2.moveToFirst()) {
            value2 = cursor2.getInt(0);
            cursor2.close();
        }

        db.close();
        return count = value1 + value2;
    }

    public int countNewWord(String library) {
        return countWordCondition(library, 1);
    }

    /**
     * count old word in list
     *
     * @param library
     * @return
     */
    public int countOldWord(String library) {
        return countWordCondition(library, 0);
    }

    private int countWordCondition(String library, int isNew) {
        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0;
        String sql = "SELECT count(*) FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                + WordTable.COLUMN_IS_NEW + "=" + isNew;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public int countForgetWord(String library) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        // select word and get forget count
        String sql = "SELECT count(*) FROM "
                + WordTable.TABLE_NAME + " WHERE " + WordTable.COLUMN_LIBRARY
                + "='" + library + "' AND " + WordTable.COLUMN_FORGET_COUNT + ">" + 0;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public synchronized void clearListWords(String library) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WordTable.TABLE_NAME, WordTable.COLUMN_LIBRARY + " = ?",
                new String[]{library});
        db.close();
    }

    public synchronized void clearAllWords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WordTable.TABLE_NAME, null, null);
        db.close();
    }

    public synchronized void insertWord(WordTable word) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(WordTable.COLUMN_LIBRARY, word.library);
        value.put(WordTable.COLUMN_WORD, word.word);
        value.put(WordTable.COLUMN_IS_NEW, word.isNew);
        value.put(WordTable.COLUMN_IS_NEW_TESTING, word.isNewTesting);
        value.put(WordTable.COLUMN_FORGET_COUNT, word.forgetCount);

        // insert row
        db.insert(WordTable.TABLE_NAME, null, value);
        db.close(); // close database connection
    }

    /**
     * increase forget count of word
     *
     * @param library
     * @param word
     */
    public synchronized void increaseWordForget(String library, String word) {
        SQLiteDatabase db = this.getReadableDatabase();

        // select word and get forget count
        String sql = "SELECT " + WordTable.COLUMN_FORGET_COUNT + " FROM "
                + WordTable.TABLE_NAME + " WHERE " + WordTable.COLUMN_LIBRARY
                + "='" + library + "' AND " + WordTable.COLUMN_WORD + "='"
                + word + "'";
        Cursor cursor = db.rawQuery(sql, null);
        int forgetCount = 0;
        if (cursor != null && cursor.moveToFirst()) {
            forgetCount = cursor.getInt(0);
            cursor.close();
        }
        db.close();

        // increase forget count
        forgetCount++;

        // save again
        ContentValues value = new ContentValues();
        value.put(WordTable.COLUMN_FORGET_COUNT, forgetCount);

        db = this.getWritableDatabase();
        db.update(WordTable.TABLE_NAME, value, WordTable.COLUMN_LIBRARY
                + "=? AND " + WordTable.COLUMN_WORD + "=?", new String[]{
                library, word});
        db.close();
    }

    /**
     * increase forget count of list word
     *
     * @param library
     * @param words
     */
    public synchronized void increaseWordForget(String library,
                                                List<String> words) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase dbWrite = this.getWritableDatabase();

        for (String word : words) {
            // select word and get forget count
            String sql = "SELECT " + WordTable.COLUMN_FORGET_COUNT + " FROM "
                    + WordTable.TABLE_NAME + " WHERE "
                    + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                    + WordTable.COLUMN_WORD + "='" + word + "'";
            Cursor cursor = db.rawQuery(sql, null);
            int forgetCount = 0;
            if (cursor != null && cursor.moveToFirst()) {
                forgetCount = cursor.getInt(0);
                cursor.close();
            }

            // increase forget count
            forgetCount++;

            // save again
            ContentValues value = new ContentValues();
            value.put(WordTable.COLUMN_FORGET_COUNT, forgetCount);

            dbWrite.update(WordTable.TABLE_NAME, value,
                    WordTable.COLUMN_LIBRARY + "=? AND "
                            + WordTable.COLUMN_WORD + "=?", new String[]{
                            library, word});
        }
        db.close();
        dbWrite.close();
    }

    /**
     * reset word forget count to 0
     *
     * @param library
     * @param word
     */
    public synchronized void resetWordForget(String library, String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(WordTable.COLUMN_FORGET_COUNT, 0);
        db.update(WordTable.TABLE_NAME, value, WordTable.COLUMN_LIBRARY
                + "=? AND " + WordTable.COLUMN_WORD + "=?", new String[]{
                library, word});
        db.close();
    }

    /**
     * reset list word forget count to 0
     *
     * @param library
     * @param words
     */
    public synchronized void resetWordForget(String library, List<String> words) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(WordTable.COLUMN_FORGET_COUNT, 0);
        for (String word : words) {
            db.update(WordTable.TABLE_NAME, value, WordTable.COLUMN_LIBRARY
                    + "=? AND " + WordTable.COLUMN_WORD + "=?", new String[]{
                    library, word});
        }
        db.close();
    }

    /**
     * change word new status
     *
     * @param library
     * @param word
     * @param isNew
     */
    public synchronized void changeWordNewStatus(String library, String word,
                                                 int isNew) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(WordTable.COLUMN_IS_NEW, isNew);
        db.update(WordTable.TABLE_NAME, value, WordTable.COLUMN_LIBRARY
                + "=? AND " + WordTable.COLUMN_WORD + "=?", new String[]{
                library, word});
        db.close();
    }

    /**
     * change list word to new status
     *
     * @param library
     * @param words
     * @param isNew
     */
    public synchronized void changeWordNewStatus(String library,
                                                 List<String> words, int isNew) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(WordTable.COLUMN_IS_NEW, isNew);
        for (String word : words) {
            db.update(WordTable.TABLE_NAME, value, WordTable.COLUMN_LIBRARY
                    + "=? AND " + WordTable.COLUMN_WORD + "=?", new String[]{
                    library, word});
        }
        db.close();
    }

    public synchronized void deleteLog(String library, String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WordTable.TABLE_NAME, WordTable.COLUMN_LIBRARY + " = ? AND "
                        + WordTable.COLUMN_WORD + " = ?",
                new String[]{library, word});
        db.close();
    }

    /**
     * change word to testing state (1)
     *
     * @param library
     * @param words
     */
    public synchronized void changeWordTesting(String library,
                                               List<WordTable> words) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();

        for (WordTable word : words) {
            // save again
            ContentValues value = new ContentValues();
            value.put(WordTable.COLUMN_IS_NEW_TESTING, 1);
            dbWrite.update(WordTable.TABLE_NAME, value,
                    WordTable.COLUMN_LIBRARY + "=? AND "
                            + WordTable.COLUMN_WORD + "=?", new String[]{
                            library, word.word});
        }
        dbWrite.close();
    }

    /**
     * change all word having testing state to tested (0)
     *
     * @param library
     */
    public synchronized void changeWordTested(String library) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        // save again
        ContentValues value = new ContentValues();
        value.put(WordTable.COLUMN_IS_NEW_TESTING, 0);
        dbWrite.update(WordTable.TABLE_NAME, value, WordTable.COLUMN_LIBRARY
                        + "=? AND " + WordTable.COLUMN_IS_NEW_TESTING + "=1",
                new String[]{library});
        dbWrite.close();
    }

    /**
     * count word having testing state
     *
     * @param library
     * @return
     */
    public int countTestingWord(String library) {
        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0;
        String sql = "SELECT count(*) FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                + WordTable.COLUMN_IS_NEW_TESTING + "=1";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    /**
     * get list of testing word
     *
     * @param library
     * @return
     */
    public ArrayList<WordTable> getTestingWord(String library) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<WordTable> r = new ArrayList<WordTable>();
        String sql = "SELECT * FROM " + WordTable.TABLE_NAME + " WHERE "
                + WordTable.COLUMN_LIBRARY + "='" + library + "' AND "
                + WordTable.COLUMN_IS_NEW_TESTING + "=1";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                WordTable log = new WordTable(cursor.getString(1),
                        cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                r.add(log);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return r;
    }

    /**
     * get card information base library and word
     *
     * @param library
     * @param word
     * @return
     */
    public CardTable getCard(String library, String word) {
        CardTable r = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + CardTable.TABLE_NAME + " WHERE "
                + CardTable.COLUMN_LIBRARY + " =?" + " AND "
                + CardTable.COLUMN_WORD + " =?";

        Cursor cursor = db.rawQuery(sql, new String[]{library, word});
        if (cursor != null && cursor.moveToFirst()) {
            r = new CardTable(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4),
                    cursor.getString(5), cursor.getString(6),
                    cursor.getString(7));
            cursor.close();
        }

        return r;
    }

    public CardTable getRandomCard(String library, int max) {
        CardTable r = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + CardTable.TABLE_NAME + " WHERE "
                + CardTable.COLUMN_LIBRARY + " =?"
                + (max == 0 ? "" : " ORDER BY RANDOM() LIMIT " + max);
        ;

        Cursor cursor = db.rawQuery(sql, new String[]{library});
        if (cursor != null && cursor.moveToFirst()) {
            r = new CardTable(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4),
                    cursor.getString(5), cursor.getString(6),
                    cursor.getString(7));
            cursor.close();
        }

        return r;
    }

    /**
     * insert list card
     *
     * @param cards
     */
    public synchronized void insertCard(ArrayList<CardTable> cards) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues value = new ContentValues();
        try {
            for (CardTable card : cards) {
                value.put(CardTable.COLUMN_LIBRARY, card.library);
                value.put(CardTable.COLUMN_WORD, card.word);
                value.put(CardTable.COLUMN_PHONETICALLY, card.phonetically);
                value.put(CardTable.COLUMN_TYPE, card.type);
                value.put(CardTable.COLUMN_MEAN_ENG, card.meanEng);
                value.put(CardTable.COLUMN_MEAN, card.mean);
                value.put(CardTable.COLUMN_EXAM, card.exam);

                // insert row
                db.insert(CardTable.TABLE_NAME, null, value);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close(); // close database connection
    }

    /**
     * insert new card
     *
     * @param card
     */
    public synchronized void insertCard(CardTable card) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(CardTable.COLUMN_LIBRARY, card.library);
        value.put(CardTable.COLUMN_WORD, card.word);
        value.put(CardTable.COLUMN_PHONETICALLY, card.phonetically);
        value.put(CardTable.COLUMN_TYPE, card.type);
        value.put(CardTable.COLUMN_MEAN_ENG, card.meanEng);
        value.put(CardTable.COLUMN_MEAN, card.mean);
        value.put(CardTable.COLUMN_EXAM, card.exam);

        // insert row
        db.insert(CardTable.TABLE_NAME, null, value);
        db.close(); // close database connection
    }

    /**
     * count total card in database
     *
     * @param library
     * @return
     */
    public int countCard(String library) {
        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0;
        String sql = "SELECT count(*) FROM " + CardTable.TABLE_NAME + " WHERE "
                + CardTable.COLUMN_LIBRARY + "='" + library + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public ArrayList<CardTable> getListCards(String library) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<CardTable> r = new ArrayList<CardTable>();
        String sql = "SELECT * FROM " + CardTable.TABLE_NAME + " WHERE "
                + CardTable.COLUMN_LIBRARY + "='" + library + "' "
                + " order by " + CardTable.COLUMN_WORD;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CardTable card = new CardTable(cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7));
                r.add(card);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return r;
    }

    public LinkedHashMap<String, CardTable> getListHashMapCards(String library) {
        SQLiteDatabase db = this.getReadableDatabase();

        LinkedHashMap<String, CardTable> r = new LinkedHashMap<String, CardTable>();
        String sql = "SELECT * FROM " + CardTable.TABLE_NAME + " WHERE "
                + CardTable.COLUMN_LIBRARY + "='" + library + "' "
                + " order by " + CardTable.COLUMN_WORD;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CardTable card = new CardTable(cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7));
                r.put(card.word, card);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return r;
    }

    /**
     * clear all card in library
     *
     * @param library library code
     */
    public synchronized void clearListCards(String library) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CardTable.TABLE_NAME, CardTable.COLUMN_LIBRARY + " = ?",
                new String[]{library});
        db.close();
    }

    public synchronized void clearAllLibrary() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LibraryTable.TABLE_NAME, null,
                null);
        db.close();
    }

    public synchronized void clearAllCards(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CardTable.TABLE_NAME, null,
                null);
        db.close();
    }

    /**
     * return list of library of user
     *
     * @return
     */
    public ArrayList<LibraryTable> getListMyLibrary() {
        ArrayList<LibraryTable> r = new ArrayList<LibraryTable>();
        String sql = "SELECT * FROM " + LibraryTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(LibraryTable.TABLE_NAME, null, null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                r.add(new LibraryTable(c.getString(0), c.getString(1)));
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return r;
    }

    public synchronized void insertLibrary(LibraryTable library) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(LibraryTable.COLUMN_CODE, library.code);
        value.put(LibraryTable.COLUMN_NAME, library.name);

        // insert row
        db.insert(LibraryTable.TABLE_NAME, null, value);
        db.close();
    }

    public synchronized boolean checkLibraryExists(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + LibraryTable.TABLE_NAME + " WHERE " + LibraryTable.COLUMN_CODE + "='" + code + "'";
        Cursor c = db.rawQuery(sql, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            return true;
        }

        return false;
    }
}