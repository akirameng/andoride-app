package com.brass.votosurveytaker;

import java.util.ArrayList;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.Question.ResponseType;
import org.votomobile.proxy.taker.QuestionResponse;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * class dealing with open text questions android activity class
 */
public class QuestionOpenActivity extends AbstractActivity implements
		DataChangeListener {
	private static final int DEFAULT_NUMERIC = 42;
	private int surveyId;
	private int surveyIndex;
	private int currentQuestionIndex = -1;
	private int numOfQuestions = 0;
	private ArrayList<String> questionArray = new ArrayList<String>();
	private ArrayList<Question> questions = new ArrayList<Question>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_open);

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
		setupSubmitButton();
		setupAudioButtons();
	}

	private void setupPreviousButton() {
		Button btn = (Button) findViewById(R.id.btnPreviousQOpen);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentQuestionIndex == 0) {
					Toast.makeText(QuestionOpenActivity.this,
							getString(R.string.already_on_the_first_question_),
							Toast.LENGTH_SHORT).show();
				} else if (questions.get(currentQuestionIndex - 1)
						.getResponseType() != ResponseType.OPEN) {
					currentQuestionIndex--;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							QuestionOpenActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				}

				else {
					currentQuestionIndex--;
					updateQuestionDisplay(currentQuestionIndex);
				}
				AudioHandler
						.stopMusic((TextView) findViewById(R.id.txtAudioStatusOpen));
			}
		});
	}

	private void setupNextButton() {
		Button btn = (Button) findViewById(R.id.btnNextQOpen);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentQuestionIndex >= questionArray.size() - 1) {
					Toast.makeText(QuestionOpenActivity.this,
							getString(R.string.reached_end_of_survey_),
							Toast.LENGTH_SHORT).show();
					QuestionActivitySelector.setCurrentQuestionIndex(-1);
					finish();
				} else if (questions.get(currentQuestionIndex + 1)
						.getResponseType() != ResponseType.OPEN) {
					currentQuestionIndex++;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							QuestionOpenActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				} else {
					currentQuestionIndex++;
					updateQuestionDisplay(currentQuestionIndex);
				}
				AudioHandler
						.stopMusic((TextView) findViewById(R.id.txtAudioStatusOpen));
			}
		});
	}

	private void setupSubmitButton() {
		Button btn = (Button) findViewById(R.id.btnSubmitOpen);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = ((EditText) findViewById(R.id.fieldTextEntry))
						.getText().toString();

				if (questions.get(currentQuestionIndex) == null) {
					Toast.makeText(QuestionOpenActivity.this,
							getString(R.string.select_a_question_first_),
							Toast.LENGTH_SHORT).show();
					return;
				}

				// empty box
				if (result.trim().equals("")) {
					Toast.makeText(
							QuestionOpenActivity.this,
							getString(R.string.answer_can_not_be_empty_or_only_spaces_please_try_again_),
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (questions.get(currentQuestionIndex).getResponse() == null) {
					QuestionResponse response = new QuestionResponse();
					switch (questions.get(currentQuestionIndex)
							.getResponseType()) {
					case NUMERIC:
						response.setNumericResponse(DEFAULT_NUMERIC);
						break;
					case MULTIPLE_CHOICE:
						response.setOptionChosen(0);
						break;
					case OPEN:
						response.setOpenText(result);
					default:
					}
					App.getQuestionManager().commitAnswer(
							questions.get(currentQuestionIndex).getId(),
							response);
				} else {
					QuestionResponse response = questions.get(
							currentQuestionIndex).getResponse();
					switch (questions.get(currentQuestionIndex)
							.getResponseType()) {
					case NUMERIC:
						response.setNumericResponse(DEFAULT_NUMERIC);
						break;
					case MULTIPLE_CHOICE:
						response.setOptionChosen(0);
						break;
					case OPEN:
						response.setOpenText(result);
					default:
					}
					App.getQuestionManager().commitAnswerChange(
							questions.get(currentQuestionIndex).getId());
				}

				if (currentQuestionIndex >= questionArray.size() - 1) {
					Toast.makeText(QuestionOpenActivity.this,
							getString(R.string.finished_the_last_question_),
							Toast.LENGTH_SHORT).show();
					QuestionActivitySelector.setCurrentQuestionIndex(-1);
					finish();
				} else if (questions.get(currentQuestionIndex + 1)
						.getResponseType() != ResponseType.OPEN) {
					currentQuestionIndex++;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							QuestionOpenActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				} else {
					Toast.makeText(QuestionOpenActivity.this,
							getString(R.string.answer_submitted_),
							Toast.LENGTH_SHORT).show();
					currentQuestionIndex++;
					updateQuestionDisplay(currentQuestionIndex);
					Log.i("Demo", "Index: " + currentQuestionIndex);
				}
			}
		});
	}

	private void setupAudioButtons() {
		Button btnPlay = (Button) findViewById(R.id.btnPlayAudioOpen);
		Button btnPause = (Button) findViewById(R.id.btnPauseAudioOpen);
		Button btnStop = (Button) findViewById(R.id.btnStopAudioOpen);
		final TextView text = (TextView) findViewById(R.id.txtAudioStatusOpen);
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

	// updates the text field showing a previous response if available
	private void updateTextEdit() {
		EditText edit = (EditText) findViewById(R.id.fieldTextEntry);
		if (questions.get(currentQuestionIndex).getResponse() != null) {
			edit.setText(questions.get(currentQuestionIndex).getResponse()
					.getOpenText());
		} else {
			edit.setText("");
		}
	}

	private void updateQuestionDisplay(int index) {
		QuestionActivitySelector.setCurrentQuestionIndex(index);

		// update the SMS content
		TextView questionTextView = (TextView) findViewById(R.id.txtSMSPromptOpen);
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
		TextView progressTextView = (TextView) findViewById(R.id.txtProgressOpen);
		progressTextView.setText(getString(R.string.question_)
				+ currentQuestionText + getString(R.string._of_)
				+ totalQuestionsText);

		// update the text field (if there is an existing response)
		updateTextEdit();
		
		setPrevButtonVisibility(currentQuestionIndex);
	}
	
	private void setPrevButtonVisibility(int index) {
		final Button btn = (Button) findViewById(R.id.btnPreviousQOpen);
		if (index == 0) {
			btn.setVisibility(View.INVISIBLE);
			btn.setClickable(false);
		} else {
			btn.setVisibility(View.VISIBLE);
			btn.setClickable(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		AudioHandler
				.stopMusic((TextView) findViewById(R.id.txtAudioStatusOpen));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AudioHandler
				.stopMusic((TextView) findViewById(R.id.txtAudioStatusOpen));
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
	protected AbstractManager getManager() {
		return App.getQuestionManager();
	}

}
