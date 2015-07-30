package com.brass.votosurveytaker;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.GcmManager;
import org.votomobile.datamodel.taker.LanguageManager;
import org.votomobile.datamodel.taker.RegistrationManager;
import org.votomobile.datamodel.taker.QuestionManager;
import org.votomobile.datamodel.taker.InvitationManager;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Singleton class for the application's running data.
 * 
 * Must add the "android:name" property to the manifest. Format: <application
 * android:name="com.xyz.App"> </application>
 */
public class App extends Application {
	public static final String TAG = "Voto API Demo";

	private static App instance;
	private static Context context;

	// Model's data managers
	private RegistrationManager registrationManager;
	private InvitationManager invitationManager;
	private QuestionManager questionManager;
	private LanguageManager languageManager;
	private GcmManager gcmManager;

	/**
	 * Handle application context (for networking and error display). This
	 * method is called by Android when the class is instantiated. Will be
	 * called before any Activity is created. This will be the one and only
	 * instance of this class created; and it is saved via a static field for
	 * future use by static methods to access the model.
	 */
	public void onCreate() {
		super.onCreate();
		instance = this;
		context = getApplicationContext();
		registrationManager = new RegistrationManager(context);
	}

	/**
	 * Initialize the model's data managers once we have the API key.
	 */
	public static void initializeDataManagers(String regId) {
		getInstance().invitationManager = new InvitationManager(regId, context);
		getInstance().questionManager = new QuestionManager(regId, context);
		getInstance().languageManager = new LanguageManager(regId, context);
		getInstance().gcmManager = new GcmManager(regId, context);
	}

	/*
	 * Get each of the model's managers.
	 */
	public static RegistrationManager getRegistrationManager() {
		return getInstance().registrationManager;
	}

	public static InvitationManager getInvitationManager() {
		failIfNull(getInstance().invitationManager);
		return getInstance().invitationManager;
	}

	public static QuestionManager getQuestionManager() {
		failIfNull(getInstance().questionManager);
		return getInstance().questionManager;
	}

	public static LanguageManager getLanguageManager() {
		failIfNull(getInstance().languageManager);
		return getInstance().languageManager;
	}

	public static GcmManager getGcmManager() {
		failIfNull(getInstance().gcmManager);
		return getInstance().gcmManager;
	}

	/*
	 * Private support methods.
	 */
	private static synchronized App getInstance() {
		return instance;
	}

	private static void failIfNull(AbstractManager manager) {
		if (manager == null) {
			Log.wtf("App",
					"Must call initializeDataManagers() before getting a manager!");
			throw new RuntimeException(
					"Must call initializeDataManagers() before getting a manager!");
		}
	}

}
