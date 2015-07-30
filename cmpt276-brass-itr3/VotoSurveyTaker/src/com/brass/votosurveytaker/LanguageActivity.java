package com.brass.votosurveytaker;

import java.util.ArrayList;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.proxy.taker.Language;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LanguageActivity extends AbstractActivity {

	private ArrayList<String> languageArray = new ArrayList<String>();
	private ArrayAdapter<String> languageAdapter;

	@Override
	protected AbstractManager getManager() {
		return App.getLanguageManager();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);

		setupListView();
		App.getLanguageManager().fetchAllLanguages();

		refreshCurrentLanguage();
		refreshLanguageList();
	}

	private void setupListView() {
		ListView languageListView = (ListView) findViewById(R.id.listLanguageList);
		languageAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, languageArray);
		languageListView.setAdapter(languageAdapter);

		languageListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							View viewClicked, int position, long id) {
						Language clickedLanguage = App.getLanguageManager()
								.getLanguageAt(position);
						App.getLanguageManager().setPreferredLanguage(
								clickedLanguage.getId());

						// Display info on language
						String message = "Selected preferred language of "
								+ getLanguageDescription(clickedLanguage);
						Toast.makeText(LanguageActivity.this, message,
								Toast.LENGTH_SHORT).show();
						finish();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.language, menu);
		return true;
	}

	@Override
	public void dataChanged() {
		refreshCurrentLanguage();
		refreshLanguageList();
	}

	private void refreshCurrentLanguage() {
		Language curLang = App.getLanguageManager().getPreferredLanguage();
		String langString = "not loaded...";
		if (curLang != null) {
			langString = getLanguageDescription(curLang);
		}

		TextView textView = (TextView) findViewById(R.id.lblPreferredLanguage);
		textView.setText(langString);
	}

	private String getLanguageDescription(Language language) {
		return language.getName() + " (" + language.getAbbreviation() + ")";
	}

	private void refreshLanguageList() {
		languageAdapter.clear();
		for (Language language : App.getLanguageManager().languages()) {
			languageAdapter.add(getLanguageDescription(language));
		}
	}

}
