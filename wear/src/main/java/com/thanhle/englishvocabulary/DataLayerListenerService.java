/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thanhle.englishvocabulary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerListenerService extends WearableListenerService implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = "DataLayerListenerServic";

	public class WearConsts {
		public static final String SYNC_PATH = "/sync";
		public static final String CARDS_KEY = "cards";
		public static final String LIBRARY_KEY = "library";
		public static final String DATA_ITEM_RECEIVED_PATH = "/data_item_received";
		public static final String START_ACTIVITY_PATH = "/start_activity";
        public static final String ACTION_DATA_FORWARDED = "action_data_forwarded";
	}

	private GoogleApiClient mGoogleApiClient;
	private DatabaseHandler database;

	@Override
	public void onCreate() {
		super.onCreate();
		database = new DatabaseHandler(this);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		mGoogleApiClient.connect();
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		Log.d(TAG, "onDataChanged: " + dataEvents);
		final List<DataEvent> events = FreezableUtils
				.freezeIterable(dataEvents);
		dataEvents.close();
		if (!mGoogleApiClient.isConnected()) {
			ConnectionResult connectionResult = mGoogleApiClient
					.blockingConnect(30, TimeUnit.SECONDS);
			if (!connectionResult.isSuccess()) {
				Log.e(TAG,
						"DataLayerListenerService failed to connect to GoogleApiClient.");
				return;
			}
		}

		// Loop through the events and send a message back to the node that
		// created the data item.
		for (DataEvent event : events) {
			Uri uri = event.getDataItem().getUri();
			String path = uri.getPath();
			if (WearConsts.SYNC_PATH.equals(path)) {
				// Get the node id of the node that created the data item from
				// the host portion of
				// the uri.
				String nodeId = uri.getHost();
				// Set the data of the message to be the bytes of the Uri.
				byte[] payload = uri.toString().getBytes();

				// Send the message back
				Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId,
						WearConsts.DATA_ITEM_RECEIVED_PATH, payload);

				// get DataMap item
				DataMapItem dataItem = DataMapItem.fromDataItem(event
						.getDataItem());
				String library = dataItem.getDataMap().getString(
						WearConsts.LIBRARY_KEY);
				ArrayList<String> cards = dataItem.getDataMap()
						.getStringArrayList(WearConsts.CARDS_KEY);
                if (cards!=null) {
                    Log.d(TAG, "library: " + library + ", cards: " + cards.size());
                    // convert to ArrayList CardTable
                    ArrayList<CardTable> r = new ArrayList<CardTable>();
                    for (String cardText : cards) {
                        r.add(new CardTable(cardText));
                    }
                    if (r.size() > 0) {
                        // clear old database
                        database.clearListCards(library);
                        // add all card list
                        database.insertCard(r);
                        Log.d(TAG, "sync card success");
                    }

                    // Broadcast message to wearable activity for display
                    Intent messageIntent = new Intent();
                    messageIntent.setAction(WearConsts.ACTION_DATA_FORWARDED);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                }
			}
		}
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		Log.d(TAG, "onMessageReceived: " + messageEvent);
		// Check to see if the message is to start an activity
		if (messageEvent.getPath().equals(WearConsts.START_ACTIVITY_PATH)) {
			Intent startIntent = new Intent(this, MainActivity.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startIntent);
		}
	}

	@Override
	public void onPeerConnected(com.google.android.gms.wearable.Node peer) {
		super.onPeerConnected(peer);
		Log.d(TAG, "onPeerConnected: " + peer);
	}

	@Override
	public void onPeerDisconnected(com.google.android.gms.wearable.Node peer) {
		super.onPeerDisconnected(peer);
		Log.d(TAG, "onPeerDisconnected: " + peer);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: "
				+ result);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(TAG, "onConnected(): Successfully connected to Google API client");
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.d(TAG,
				"onConnectionSuspended(): Connection to Google API client was suspended");
	}

}
