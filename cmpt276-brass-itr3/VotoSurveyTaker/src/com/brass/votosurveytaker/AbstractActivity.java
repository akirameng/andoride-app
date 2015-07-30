package com.brass.votosurveytaker;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.NetworkActivityListener;
import com.android.volley.VolleyError;
import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Base class for activities to handle registering listeners, error handling,
 * and displaying if there is network activity.
 * 
 * Uses abstract method getManager(), which must be defined in derived classes
 * to get the manager (such as SurveyManager()) which that activity uses. If
 * derived activity uses more than one manager, it must register and unregister
 * the listener itself by overriding onResume() and onPause().
 * 
 * @author Brian Fraser
 * 
 */
public abstract class AbstractActivity extends Activity implements
		DataChangeListener, NetworkActivityListener {
	private ProgressDialog waitDialog;

	// Allow the base class to ask the derived class what manager to register
	// with.
	abstract protected AbstractManager getManager();

	/*
	 * Register as a listener for data-change and network activity.
	 */
	/**
	 * Register listeners for data and network-activity change.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getManager().addListener(this);
		getManager().addNetworkActivityListener(this);

		// Display network activity dialog when starting if there was
		// activity initiated before this activity was started.
		if (getManager().hasNetworkActivity()) {
			networkActivityStarted();
		}
	}

	/**
	 * Unregister listeners for data and network-activity change.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		getManager().removeListener(this);
		getManager().removeNetworkActivityListener(this);

		// Dismiss dialog if shown:
		networkActivityEnded();
	}

	/*
	 * DataChangeListener: Default network error handling.
	 */
	/**
	 * Default error handling.
	 */
	@Override
	public void networkError(VolleyError volleyError) {
		ErrorMessenger.showErrorMessage(this, volleyError);
	}

	/*
	 * Network Activity Listener: Default handling
	 */
	/**
	 * Display a progress box.
	 */
	@Override
	public void networkActivityStarted() {
		waitDialog = ProgressDialog.show(this, getString(R.string.voto_survey_taker),
				getString(R.string.loading_));
	}

	/**
	 * Hide the progress box.
	 */
	@Override
	public void networkActivityEnded() {
		if (waitDialog != null) {
			waitDialog.dismiss();
		}
	}

}
