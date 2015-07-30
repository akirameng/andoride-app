package com.brass.votosurveytaker;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.DataChangeListener;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * class dealing with welcome screen handling log in and animation display
 * android activity class
 */
public class WelcomeScreen extends AbstractActivity implements
		DataChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_screen);
		checkStoredNum();
		setupWelcome();
	}

	private void checkStoredNum() {
		SharedPreferences numPref = getSharedPreferences("phoneNum",
				MODE_PRIVATE);
		SharedPreferences valPref = getSharedPreferences("Valid", MODE_PRIVATE);

		boolean valid = valPref.getBoolean("Valid", false);
		String phoneNumber = numPref.getString("Number", null);
		Log.i("Welcome bool", "" + valid);
		Log.i("Welcome number", "" + phoneNumber);
		if (valid && phoneNumber != null) {
			loginWithPhoneNum(phoneNumber);
		}
	}

	private void setupWelcome() {

		final TextView txtMessage = (TextView) findViewById(R.id.WelcomeTitle);
		final ImageView image = (ImageView) findViewById(R.id.imageView1Surveyimage);

		Animation fade = AnimationUtils.loadAnimation(this, R.animator.fade_in);
		Animation move = AnimationUtils.loadAnimation(this, R.animator.move);

		move.setAnimationListener(new AnimationListener() {

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				showDialog();
				final Button btn = (Button) findViewById(R.id.btnSkip);
				btn.setText(getString(R.string.re_enter_number));

			}

			public void onAnimationStart(Animation animation) {
			}
		});

		image.startAnimation(move);
		txtMessage.startAnimation(fade);

		final Button btn = (Button) findViewById(R.id.btnSkip);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				image.clearAnimation();
				txtMessage.clearAnimation();
				if (btn.getText() == getString(R.string.re_enter_number))
					showDialog();
				btn.setText(R.string.re_enter_number);
			}
		});
	}

	private void showDialog() {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder
				.setTitle(getString(R.string.no_login_details_found_please_enter_your_phone_number_));
		final EditText input = new EditText(this);

		SharedPreferences phoneNum = getSharedPreferences("phoneNum",
				MODE_PRIVATE);
		String phoneNumber = phoneNum.getString("Number", null);
		input.setText(phoneNumber);

		dialogBuilder.setView(input);
		dialogBuilder.setPositiveButton(getString(R.string.submit),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						loginWithPhoneNum(input);
					}

				}).setNegativeButton(getString(R.string.exit),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}

				});

		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setCancelable(false);
	}

	private void loginWithPhoneNum(final TextView phoneTxt) {

		String phoneNumber = phoneTxt.getText().toString();
		
		

		SharedPreferences phoneNum = getSharedPreferences("phoneNum",
				MODE_PRIVATE);
		
		if(phoneNumber!=phoneNum.getString("Number", null))
		{
			SharedPreferences Gcm = getSharedPreferences("GcmCheck", MODE_PRIVATE);
			Editor Gcmeditor = Gcm.edit();
			Gcmeditor.putBoolean("IsGcmCheck", true);
			Gcmeditor.commit();
		}
		Editor phoneEdit = phoneNum.edit();
		phoneEdit.putString("Number", phoneNumber);
		if (!phoneEdit.commit())
			phoneEdit.apply();

		// get api key and notify data change listener
		App.getRegistrationManager().getRegistrationId(phoneNumber);
	}

	private void loginWithPhoneNum(String phoneNumber) {
		App.getRegistrationManager().getRegistrationId(phoneNumber);
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome_screen, menu);
		return true;
	}

	// Listener support for data managers
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public void dataChanged() {
		String regId = App.getRegistrationManager().getApiKey();
//		Toast.makeText(this, getString(R.string.have_registration_id_) + regId,
//				Toast.LENGTH_SHORT).show();

		App.initializeDataManagers(regId);

		// data change listener will start the survey activity when api key is
		// received
		startActivity(new Intent(this, SurveyListActivity.class));
		finish();
	}

	@Override
	protected AbstractManager getManager() {
		return App.getRegistrationManager();
	}

}
