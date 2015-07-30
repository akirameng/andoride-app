package com.brass.votosurveytaker;

import java.util.ArrayList;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.Question.ResponseType;
import org.votomobile.proxy.taker.QuestionResponse;

import com.android.volley.VolleyError;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * class dealing with multiple choice questions. android activity class
 */
public class QuestionMultipleActivity extends AbstractActivity implements
		DataChangeListener {

	private int surveyId;
	private int surveyIndex;
	private int currentQuestionIndex = 0;
	private int numOfQuestions = 0;
	private ArrayList<String> questionArray = new ArrayList<String>();
	private ArrayList<Question> questions = new ArrayList<Question>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_multiple);

		getInvitationInfoFromIntent();
		populateListOfQuestions();
		setupButtons();
	}

	private void getInvitationInfoFromIntent() {
		surveyId = getIntent().getIntExtra("surveyId", -1);
		surveyIndex = getIntent().getIntExtra("surveyIndex", -1);
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
		Button btn = (Button) findViewById(R.id.btnPreviousQMC);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (currentQuestionIndex == 0) {
					Toast.makeText(QuestionMultipleActivity.this,
							getString(R.string.already_on_the_first_question_),
							Toast.LENGTH_SHORT).show();
				} else if (questions.get(currentQuestionIndex - 1)
						.getResponseType() != ResponseType.MULTIPLE_CHOICE) {
					currentQuestionIndex--;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							QuestionMultipleActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				}

				else {
					currentQuestionIndex--;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					updateQuestionDisplay(currentQuestionIndex);
				}
				AudioHandler
						.stopMusic((TextView) findViewById(R.id.txtAudioStatusMC));
			}
		});
	}

	private void setupNextButton() {
		Button btn = (Button) findViewById(R.id.btnNextQMC);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentQuestionIndex >= questionArray.size() - 1) {
					Toast.makeText(QuestionMultipleActivity.this,
							getString(R.string.reached_end_of_survey_),
							Toast.LENGTH_SHORT).show();
					QuestionActivitySelector.setCurrentQuestionIndex(-1);
					finish();
				} else if (questions.get(currentQuestionIndex + 1)
						.getResponseType() != ResponseType.MULTIPLE_CHOICE) {
					currentQuestionIndex++;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							QuestionMultipleActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				}

				else {
					currentQuestionIndex++;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					updateQuestionDisplay(currentQuestionIndex);
				}
				AudioHandler
						.stopMusic((TextView) findViewById(R.id.txtAudioStatusMC));
			}
		});
	}

	private void setupAudioButtons() {
		Button btnPlay = (Button) findViewById(R.id.btnPlayAudioMC);
		Button btnPause = (Button) findViewById(R.id.btnPauseAudioMC);
		Button btnStop = (Button) findViewById(R.id.btnStopAudioMC);
		final TextView text = (TextView) findViewById(R.id.txtAudioStatusMC);
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

	// setup dropdown menu
	private void setupSubmitSpinner() {
		final Spinner spin = (Spinner) findViewById(R.id.spinnerAnswer);
		final String[] option = questions.get(
				QuestionActivitySelector.getCurrentQuestionIndex())
				.getOptions();
		String[] options = new String[option.length + 1];
		for (int i = 0; i < option.length; i++) {
			options[i] = option[i];
		}
		options[option.length] = getString(R.string.no_selected);
		ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, options);
		spin.setAdapter(spinAdapter);

		if (questions.get(QuestionActivitySelector.getCurrentQuestionIndex())
				.getResponse() != null) {
			int position = questions
					.get(QuestionActivitySelector.getCurrentQuestionIndex())
					.getResponse().getOptionChosenIndex();

			spin.setSelection(position);
		} else {
			spin.setSelection(option.length);
		}

		spin.setOnItemSelectedListener(new OnItemSelectedListener() {
			boolean check = false;

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (check) {
					int result = spin.getSelectedItemPosition();
					if (result == option.length) {
						Toast.makeText(
								QuestionMultipleActivity.this,
								getString(R.string.please_select_a_valid_answer),
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (questions.get(currentQuestionIndex).getResponse() == null) {
						QuestionResponse response = new QuestionResponse();
						response.setOptionChosen(result);
						App.getQuestionManager().commitAnswer(
								questions.get(currentQuestionIndex).getId(),
								response);
					} else {
						QuestionResponse response = questions.get(
								currentQuestionIndex).getResponse();
						response.setOptionChosen(result);
						App.getQuestionManager().commitAnswerChange(
								questions.get(currentQuestionIndex).getId());
					}

					if (currentQuestionIndex >= questionArray.size() - 1) {
						Toast.makeText(
								QuestionMultipleActivity.this,
								getString(R.string.finished_the_last_question_),
								Toast.LENGTH_SHORT).show();
						QuestionActivitySelector.setCurrentQuestionIndex(-1);
						finish();
					} else if (questions.get(currentQuestionIndex + 1)
							.getResponseType() != ResponseType.MULTIPLE_CHOICE) {
						currentQuestionIndex++;
						QuestionActivitySelector
								.setCurrentQuestionIndex(currentQuestionIndex);
						QuestionActivitySelector.switchToQuestionActivity(
								QuestionMultipleActivity.this,
								questions.get(currentQuestionIndex)
										.getResponseType(), surveyId,
								surveyIndex);
					} else {
						Toast.makeText(QuestionMultipleActivity.this,
								getString(R.string.answer_submitted_),
								Toast.LENGTH_SHORT).show();
						currentQuestionIndex++;
						updateQuestionDisplay(currentQuestionIndex);
						Log.i("Demo", "Index: " + currentQuestionIndex);
					}
				}
				check = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void updateQuestionDisplay(int index) {
		QuestionActivitySelector.setCurrentQuestionIndex(index);
		// update the SMS content
		TextView questionTextView = (TextView) findViewById(R.id.txtSMSPromptMC);
		questionTextView.setMovementMethod(new ScrollingMovementMethod());
		questionTextView.setText("" + questionArray.get(index));

		// update the progress
		int currentQuestionText = currentQuestionIndex + 1;
		int totalQuestionsText = numOfQuestions;
		if (questions.get(0).getResponseType() == ResponseType.INTRODUCTION_OR_CONCLUSION) {
			currentQuestionText--;
			totalQuestionsText--;
		}
		if (questions.get(numOfQuestions - 1).getResponseType() == ResponseType.INTRODUCTION_OR_CONCLUSION) {
			totalQuestionsText--;
		}
		TextView progressTextView = (TextView) findViewById(R.id.txtProgressMC);
		progressTextView.setText(getString(R.string.question_)
				+ currentQuestionText + getString(R.string._of_)
				+ totalQuestionsText);

		// update the spinner
		setupSubmitSpinner();
		
		setPrevButtonVisibility(index);
	}
	
	private void setPrevButtonVisibility(int index) {
		final Button btn = (Button) findViewById(R.id.btnPreviousQMC);
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
		AudioHandler.stopMusic((TextView) findViewById(R.id.txtAudioStatusMC));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AudioHandler.stopMusic((TextView) findViewById(R.id.txtAudioStatusMC));
	}

	@Override
	public void dataChanged() {
		numOfQuestions = 0;
		questionArray.clear();
		questions.clear();
		for (Question question : App.getQuestionManager().questions()) {
			questionArray.add(question.getSMSPrompt());
			questions.add(question);
			numOfQuestions++;
		}
		if (QuestionActivitySelector.getCurrentQuestionIndex() == -1)
			QuestionActivitySelector.setCurrentQuestionIndex(0);

		currentQuestionIndex = QuestionActivitySelector
				.getCurrentQuestionIndex();
		updateQuestionDisplay(currentQuestionIndex);
	}

	@Override
	public void networkError(VolleyError volleyError) {
		ErrorMessenger.showErrorMessage(this, volleyError);
	}

	@Override
	protected AbstractManager getManager() {
		return App.getQuestionManager();
	}
}
