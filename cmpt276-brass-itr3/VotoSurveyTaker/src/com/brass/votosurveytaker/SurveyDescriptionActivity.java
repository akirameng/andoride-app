package com.brass.votosurveytaker;

import java.util.ArrayList;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.Question;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * class dealing with introduction and conclusion display android activity class
 */
public class SurveyDescriptionActivity extends AbstractActivity implements
		DataChangeListener {

	private int surveyId;
	private int surveyIndex;
	private int currentQuestionIndex = -1;
	private ArrayList<String> questionArray = new ArrayList<String>();
	private ArrayList<Question> questions = new ArrayList<Question>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_description);

		getInvitationInfoFromIntent();
		setupTitleDisplay();
		populateListOfQuestions();
		setupButtons();
	}

	private void getInvitationInfoFromIntent() {
		surveyId = getIntent().getIntExtra("surveyId", -1);
		surveyIndex = getIntent().getIntExtra("surveyIndex", -1);
	}

	private void setupTitleDisplay() {
		TextView title = (TextView) findViewById(R.id.txtSurveyIdAndTitleDescription);
		if (surveyIndex != -1) {
			title.setText(getString(R.string.survey_title_)
					+ App.getInvitationManager().getInvitationAt(surveyIndex)
							.getSurveyTitle());
		}
	}

	private void populateListOfQuestions() {
		if (surveyId != -1) {
			App.getQuestionManager().setCurrentSurveyId(surveyId);
			App.getQuestionManager().fetchQuestions();
		}
	}

	private void setupButtons() {
		setupPreviousButton();
		setupNextButton();
		setupAudioButtons();
	}

	private void setupPreviousButton() {
		Button btn = (Button) findViewById(R.id.btnPreviousQDescription);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentQuestionIndex == 0) {
					Toast.makeText(SurveyDescriptionActivity.this,
							getString(R.string.already_on_intro),
							Toast.LENGTH_SHORT).show();
				} else {
					currentQuestionIndex--;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							SurveyDescriptionActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				}
				AudioHandler
						.stopMusic((TextView) findViewById(R.id.txtAudioStatusDescription));
			}
		});
	}

	private void setupNextButton() {
		Button btn = (Button) findViewById(R.id.btnNextQDescription);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentQuestionIndex >= questionArray.size() - 1) {
					Toast.makeText(SurveyDescriptionActivity.this,
							getString(R.string.reached_end_of_survey_),
							Toast.LENGTH_SHORT).show();
					QuestionActivitySelector.setCurrentQuestionIndex(-1);
					finish();
				} else {
					currentQuestionIndex++;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							SurveyDescriptionActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				}
				AudioHandler
						.stopMusic((TextView) findViewById(R.id.txtAudioStatusDescription));
			}
		});
	}

	private void setupAudioButtons() {
		Button btnPlay = (Button) findViewById(R.id.btnPlayAudioDescription);
		Button btnPause = (Button) findViewById(R.id.btnPauseAudioDescription);
		Button btnStop = (Button) findViewById(R.id.btnStopAudioDescription);
		final TextView text = (TextView) findViewById(R.id.txtAudioStatusDescription);
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AudioHandler
						.startMusic(text, questions.get(currentQuestionIndex)
								.getAudioPromptUrl());
			}
		});
		btnPause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AudioHandler.pauseMusic(text);
			}
		});
		btnStop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AudioHandler.stopMusic(text);
			}
		});
	}

	private void updateDescriptionDisplay(int index) {
		// update the title (Introduction or Conclusion)
		QuestionActivitySelector.setCurrentQuestionIndex(index);
		TextView titleTextView = (TextView) findViewById(R.id.txtSurveyDescriptionTitle);
		if (currentQuestionIndex == 0) {
			titleTextView.setText(getString(R.string.survey_description_intro));
		} else {
			titleTextView
					.setText(getString(R.string.survey_description_conclusion));
		}

		// update the SMS content
		TextView questionTextView = (TextView) findViewById(R.id.txtSMSPromptDescription);
		questionTextView.setMovementMethod(new ScrollingMovementMethod());
		questionTextView.setText("" + questionArray.get(index));
		setPrevButtonVisibility(index);
	}

	private void setPrevButtonVisibility(int index) {
		final Button btn = (Button) findViewById(R.id.btnPreviousQDescription);
		if (index == 0) {
			btn.setVisibility(View.INVISIBLE);
			btn.setClickable(false);
		} else {
			btn.setVisibility(View.VISIBLE);
			btn.setClickable(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.question_multiple, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		AudioHandler
				.stopMusic((TextView) findViewById(R.id.txtAudioStatusDescription));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AudioHandler
				.stopMusic((TextView) findViewById(R.id.txtAudioStatusDescription));
	}

	@Override
	public void dataChanged() {
		questionArray.clear();
		questions.clear();
		for (Question question : App.getQuestionManager().questions()) {
			questionArray.add(question.getSMSPrompt());
			questions.add(question);
		}
		if (QuestionActivitySelector.getCurrentQuestionIndex() == -1)
			QuestionActivitySelector.setCurrentQuestionIndex(0);

		currentQuestionIndex = QuestionActivitySelector
				.getCurrentQuestionIndex();
		updateDescriptionDisplay(currentQuestionIndex);
	}

	@Override
	protected AbstractManager getManager() {
		return App.getQuestionManager();
	}

}
