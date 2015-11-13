package com.thanhle.englishvocabulary.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import com.thanhle.englishvocabulary.database.DatabaseHandler;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.WordTable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	}

	public static void setAlarmCheckLocation(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, NotificationReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// get current time;
		Calendar calAlarm = GregorianCalendar.getInstance();
		calAlarm.add(Calendar.HOUR, 8);
		// set alarm to 12AM and repeat every 12h (12AM and 12PM)
		am.setRepeating(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), 8,
				pi);
	}
	
	public static void showNotificationNewWord(Context context) {
		DatabaseHandler database = new DatabaseHandler(context);
		String currentLibrary = SharePrefs.getInstance().getCurrentLibrary();
		ArrayList<WordTable> words = database
				.getListNewWords(currentLibrary, 0);
		if (words.size() > 0) {
			int rnd = new Random().nextInt(words.size());
			String word = words.get(rnd).word;
			CardTable card = database.getCard(currentLibrary, word);
			
		}		
	}

}
