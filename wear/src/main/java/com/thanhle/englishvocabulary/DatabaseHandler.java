package com.thanhle.englishvocabulary;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "iwander_database";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + CardTable.TABLE_NAME + "("
				+ "_id integer primary key," + CardTable.COLUMN_WORD + " text,"
				+ CardTable.COLUMN_PHONETICALLY + " text,"
				+ CardTable.COLUMN_TYPE + " text," + CardTable.COLUMN_MEAN
				+ " text)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * get card information base word
	 * 
	 * @param word
	 * @return
	 */
	public CardTable getCard(String word) {
		CardTable r = null;
		SQLiteDatabase db = this.getReadableDatabase();

		String sql = "SELECT * FROM " + CardTable.TABLE_NAME + " WHERE "
				+ CardTable.COLUMN_WORD + " =?";

		Cursor cursor = db.rawQuery(sql, new String[] { word });
		if (cursor != null && cursor.moveToFirst()) {
			r = new CardTable(cursor.getString(1), cursor.getString(2),
					cursor.getString(3), cursor.getString(4));
			cursor.close();
		}

		return r;
	}

	/**
	 * insert list card
	 * 
	 * @param card
	 */
	public synchronized void insertCard(ArrayList<CardTable> cards) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues value = new ContentValues();
		for (CardTable card : cards) {
			value.put(CardTable.COLUMN_WORD, card.word);
			value.put(CardTable.COLUMN_PHONETICALLY, card.phonetically);
			value.put(CardTable.COLUMN_TYPE, card.type);
			value.put(CardTable.COLUMN_MEAN, card.mean);

			// insert row
			db.insert(CardTable.TABLE_NAME, null, value);
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
		value.put(CardTable.COLUMN_WORD, card.word);
		value.put(CardTable.COLUMN_PHONETICALLY, card.phonetically);
		value.put(CardTable.COLUMN_TYPE, card.type);
		value.put(CardTable.COLUMN_MEAN, card.mean);

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
	public int countCard() {
		SQLiteDatabase db = this.getReadableDatabase();

		int count = 0;
		String sql = "SELECT count(*) FROM " + CardTable.TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null && cursor.moveToFirst()) {
			count = cursor.getInt(0);
			cursor.close();
		}
		return count;
	}

	public ArrayList<CardTable> getListCards() {
		SQLiteDatabase db = this.getReadableDatabase();

		ArrayList<CardTable> r = new ArrayList<CardTable>();
		String sql = "SELECT * FROM " + CardTable.TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				CardTable card = new CardTable(cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4));
				r.add(card);
			} while (cursor.moveToNext());
			cursor.close();
		}
		return r;
	}

	public synchronized void clearListCards(String library) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(CardTable.TABLE_NAME, null, null);
		db.close();
	}
}