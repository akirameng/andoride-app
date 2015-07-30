package com.brass.votosurveytaker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Handle messages from the GCM server that new data is available.
 * @author Brian Fraser
 *
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "MyBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i(TAG, "Recieved action: " + action);
		if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
			String registartionId = intent.getStringExtra("registration_id");
			String error = intent.getStringExtra("error");
			String unregistered = intent.getStringExtra("unregistered");
			
			Log.i(TAG, "Reg ID: " + registartionId);
			Log.i(TAG, "Error: " + error);
			Log.i(TAG, "Unreg: " + unregistered);
			
		} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
			// Server/application specific data being received.
			String message = "";
			String invitationId = "";
			if (intent.hasExtra("message")) {
				message = intent.getStringExtra("message");
			}
			if (intent.hasExtra("invitation_id")) {
				invitationId = intent.getStringExtra("invitation_id");
			}
			
			String text = message + " (" + invitationId + ")";
			Log.i(TAG, text);
			
			setupNotifacation(context, text);
		}
	}

	private void setupNotifacation(Context context, String text) {
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
	            new Intent(context, WelcomeScreen.class), 0);

	    NotificationCompat.Builder mBuilder =
	            new NotificationCompat.Builder(context)
	            .setSmallIcon(R.drawable.notification)
	            .setContentTitle(context.getString(R.string.voto_survey_taker))
	            .setContentText(context.getString(R.string.voto_gcm_) + text);
	    mBuilder.setContentIntent(contentIntent);
	    mBuilder.setDefaults(Notification.DEFAULT_SOUND);
	    mBuilder.setAutoCancel(true);
	    NotificationManager mNotificationManager =
	        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    mNotificationManager.notify(1, mBuilder.build());
		
	}
}
