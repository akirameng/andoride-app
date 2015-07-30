package com.brass.votosurveytaker;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import com.android.volley.VolleyError;

/**
 * error message class to deal with every type of error for server and
 * application
 * 
 */
public class ErrorMessenger {
	private static String usrError;
	private static String detailError;

	// static method to display the error message for app
	public static void showErrorMessage(final Activity context,
			VolleyError error) {
		usrError = getErrorMessage(error, context);
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
		final AlertDialog.Builder moreBuilder = new AlertDialog.Builder(context);

		dlgBuilder.setTitle(context.getString(R.string.an_error_has_occured_));
		dlgBuilder.setMessage(usrError);
		dlgBuilder.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dlgBuilder.setNegativeButton(context.getString(R.string.more),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						buildMore(moreBuilder, context);
					}
				});
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setCancelable(true); // This allows the 'BACK' button
		dlgBuilder.create().show();
	}

	// helper class to get more detailed error from server
	private static void buildMore(Builder moreBuilder, final Activity context) {
		moreBuilder.setTitle(context.getString(R.string.detailed_error));
		moreBuilder.setMessage(context.getString(R.string.details_)
				+ detailError);
		moreBuilder.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		moreBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		moreBuilder.setCancelable(true);
		moreBuilder.create().show();
	}

	// helper class to get error message to pass to showErrorMessage function
	private static String getErrorMessage(VolleyError error,
			final Activity context) {

		String errorMessage = "";
		if (error == null) {
			errorMessage = context
					.getString(R.string.something_went_wrong_accessing_the_server_but_no_details_available_);
		} else if (error.getMessage() != null) {
			detailError = error.getMessage();
			errorMessage = error.getClass().toString()
					.replaceAll("com.android.volley.", "");
		} else if (error.networkResponse == null) {
			errorMessage = context.getString(R.string.error_of_type_)
					+ error.getClass().toString() + ".";
		} else {
			// Try finding message in response data (JSON from VOTO server)
			String msg = "";
			try {
				String contents = new String(error.networkResponse.data,
						"UTF-8");
				JSONObject response = new JSONObject(contents);
				msg = response.getString("message");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (msg.contains("subscriber")) {
				msg = context
						.getString(R.string.could_not_find_subscriber_with_this_phone);
			}
			errorMessage = context.getString(R.string.server_says_) + msg;
			detailError = error.toString();
		}
		// parse connection error
		if (errorMessage.contains("NoConnectionError")) {
			errorMessage = context.getString(R.string.no_connection_error_)
					+ context
							.getString(R.string.please_connect_to_the_internet_and_try_again_);
		}

		return errorMessage;
	}
}
