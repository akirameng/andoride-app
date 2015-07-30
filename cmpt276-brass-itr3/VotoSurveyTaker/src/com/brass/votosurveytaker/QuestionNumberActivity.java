package com.brass.votosurveytaker;

import java.util.ArrayList;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.QuestionResponse;
import org.votomobile.proxy.taker.Question.ResponseType;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * class dealing with numeric questions display. android activity class
 */
public class QuestionNumberActivity extends AbstractActivity implements
		DataChangeListener {
	private static final long MIN_NUM = -2000000000L;
	private static final long MAX_NUM = 2000000000L;
	private int surveyId;
	private int surveyIndex;
	private int currentQuestionIndex = 0;
	private int numOfQuestions = 0;
	private ArrayList<String> questionArray = new ArrayList<String>();
	private ArrayList<Question> questions = new ArrayList<Question>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_number);

		getInvitationInfoFromIntent();
		setupEditText();
		populateListOfQuestions();
		setupButtons();
	}

	private void getInvitationInfoFromIntent() {
		surveyId = getIntent().getIntExtra("surveyId", -1);
		surveyIndex = getIntent().getIntExtra("surveyIndex", -1);
	}

	// additional setup for EditText (press enter to submit)
	private void setupEditText() {
		final EditText answer = (EditText) findViewById(R.id.editTextNumber);
		answer.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;

				if (actionId == EditorInfo.IME_ACTION_SEND) {
					if (answer.getText().toString().trim().equals("")) {
						Toast.makeText(
								QuestionNumberActivity.this,
								getString(R.string.invalid_number_please_try_again_),
								Toast.LENGTH_SHORT).show();
						return true;
					}
					if (Long.parseLong(answer.getText().toString()) > MAX_NUM) {
						Toast.makeText(
								QuestionNumberActivity.this,
								getString(R.string.invalid_number_please_try_again_),
								Toast.LENGTH_SHORT).show();
						answer.setText("");

						return true;
					}
					if (Long.parseLong(answer.getText().toString()) < MIN_NUM) {
						Toast.makeText(
								QuestionNumberActivity.this,
								getString(R.string.invalid_number_please_try_again_),
								Toast.LENGTH_SHORT).show();
						answer.setText("");
						return true;
					}

					int result = (int) Long.parseLong(answer.getText()
							.toString());
					if (questions.get(currentQuestionIndex).getResponse() == null) {
						QuestionResponse response = new QuestionResponse();
						response.setNumericResponse(result);
						App.getQuestionManager().commitAnswer(
								questions.get(currentQuestionIndex).getId(),
								response);
					} else {
						QuestionResponse response = questions.get(
								currentQuestionIndex).getResponse();
						response.setNumericResponse(result);
						App.getQuestionManager().commitAnswerChange(
								questions.get(currentQuestionIndex).getId());
					}

					if (currentQuestionIndex >= questionArray.size() - 1) {
						Toast.makeText(
								QuestionNumberActivity.this,
								getString(R.string.finished_the_last_question_),
								Toast.LENGTH_SHORT).show();
						QuestionActivitySelector.setCurrentQuestionIndex(-1);
						finish();
					} else if (questions.get(currentQuestionIndex + 1)
							.getResponseType() != ResponseType.NUMERIC) {
						currentQuestionIndex++;
						QuestionActivitySelector
								.setCurrentQuestionIndex(currentQuestionIndex);
						QuestionActivitySelector.switchToQuestionActivity(
								QuestionNumberActivity.this,
								questions.get(currentQuestionIndex)
										.getResponseType(), surveyId,
								surveyIndex);
					} else {
						Toast.makeText(QuestionNumberActivity.this,
								getString(R.string.answer_submitted_),
								Toast.LENGTH_SHORT).show();
						currentQuestionIndex++;
						updateQuestionDisplay(currentQuestionIndex);
						Log.i("Demo", "Index: " + currentQuestionIndex);
					}
					handled = true;
				}
				return handled;
			}
		});
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
		Button btn = (Button) findViewById(R.id.btnPreviousQNumber);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (currentQuestionIndex == 0) {
					Toast.makeText(QuestionNumberActivity.this,
							getString(R.string.already_on_the_first_question_),
							Toast.LENGTH_SHORT).show();
				} else if (questions.get(currentQuestionIndex - 1)
						.getResponseType() != ResponseType.NUMERIC) {
					currentQuestionIndex--;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							QuestionNumberActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				} else {
					currentQuestionIndex--;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					updateQuestionDisplay(currentQuestionIndex);
				}
				AudioHandler
						.stopMusic((TextView) findViewById(R.id.txtAudioStatusNumber));
			}
		});
	}

	private void setupNextButton() {
		Button btn = (Button) findViewById(R.id.btnNextQNumber);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentQuestionIndex >= questionArray.size() - 1) {
					Toast.makeText(QuestionNumberActivity.this,
							getString(R.string.reached_end_of_survey_),
							Toast.LENGTH_SHORT).show();
					QuestionActivitySelector.setCurrentQuestionIndex(-1);
					finish();
				} else if (questions.get(currentQuestionIndex + 1)
						.getResponseType() != ResponseType.NUMERIC) {
					currentQuestionIndex++;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							QuestionNumberActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				} else {
					currentQuestionIndex++;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					updateQuestionDisplay(currentQuestionIndex);
				}
				AudioHandler
						.stopMusic((TextView) findViewById(R.id.txtAudioStatusNumber));
			}
		});
	}

	private void setupSubmitButton() {
		Button SubmitBtn = (Button) findViewById(R.id.btnSubmitNumber);
		SubmitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final EditText answer = (EditText) findViewById(R.id.editTextNumber);
				if (answer.getText().toString().trim().equals("")) {
					Toast.makeText(
							QuestionNumberActivity.this,
							getString(R.string.invalid_number_please_try_again_),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (answer.getText().toString().trim().length() >= 11) {
					Toast.makeText(
							QuestionNumberActivity.this,
							getString(R.string.invalid_number_please_try_again_),
							Toast.LENGTH_SHORT).show();
					answer.setText("");
					return;
				}
				if (Long.parseLong(answer.getText().toString()) > MAX_NUM) {
					Toast.makeText(
							QuestionNumberActivity.this,
							getString(R.string.invalid_number_please_try_again_),
							Toast.LENGTH_SHORT).show();
					answer.setText("");
					return;
				}
				if (Long.parseLong(answer.getText().toString()) < MIN_NUM) {
					Toast.makeText(
							QuestionNumberActivity.this,
							getString(R.string.invalid_number_please_try_again_),
							Toast.LENGTH_SHORT).show();
					answer.setText("");
					return;
				}

				int result = (int) Long.parseLong(answer.getText().toString());
				if (questions.get(currentQuestionIndex).getResponse() == null) {
					QuestionResponse response = new QuestionResponse();
					response.setNumericResponse(result);
					App.getQuestionManager().commitAnswer(
							questions.get(currentQuestionIndex).getId(),
							response);
				} else {
					QuestionResponse response = questions.get(
							currentQuestionIndex).getResponse();
					response.setNumericResponse(result);
					App.getQuestionManager().commitAnswerChange(
							questions.get(currentQuestionIndex).getId());
				}

				if (currentQuestionIndex >= questionArray.size() - 1) {
					Toast.makeText(QuestionNumberActivity.this,
							getString(R.string.finished_the_last_question_),
							Toast.LENGTH_SHORT).show();
					QuestionActivitySelector.setCurrentQuestionIndex(-1);
					finish();
				} else if (questions.get(currentQuestionIndex + 1)
						.getResponseType() != ResponseType.NUMERIC) {
					currentQuestionIndex++;
					QuestionActivitySelector
							.setCurrentQuestionIndex(currentQuestionIndex);
					QuestionActivitySelector.switchToQuestionActivity(
							QuestionNumberActivity.this,
							questions.get(currentQuestionIndex)
									.getResponseType(), surveyId, surveyIndex);
				} else {
					Toast.makeText(QuestionNumberActivity.this,
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
		Button btnPlay = (Button) findViewById(R.id.btnPlayAudioNumber);
		Button btnPause = (Button) findViewById(R.id.btnPauseAudioNumber);
		Button btnStop = (Button) findViewById(R.id.btnStopAudioNumber);
		final TextView text = (TextView) findViewById(R.id.txtAudioStatusNumber);
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
		EditText edit = (EditText) findViewById(R.id.editTextNumber);
		if (questions.get(currentQuestionIndex).getResponse() != null) {
			edit.setText(""
					+ questions.get(currentQuestionIndex).getResponse()
							.getNumericResponse());
		} else {
			edit.setText("");
		}
	}

	private void updateQuestionDisplay(int index) {
		QuestionActivitySelector.setCurrentQuestionIndex(index);

		// update the SMS content
		TextView questionTextView = (TextView) findViewById(R.id.txtSMSPromptNumber);
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
		TextView progressTextView = (TextView) findViewById(R.id.txtProgressNumber);
		progressTextView.setText(getString(R.string.question_)
				+ currentQuestionText + " of " + totalQuestionsText);

		// update the text field (if there is an existing response)
		updateTextEdit();
		setPrevButtonVisibility(index);
	}
	
	private void setPrevButtonVisibility(int index) {
		final Button btn = (Button) findViewById(R.id.btnPreviousQNumber);
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
		getMenuInflater().inflate(R.menu.question_number, menu);
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
				.stopMusic((TextView) findViewById(R.id.txtAudioStatusNumber));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AudioHandler
				.stopMusic((TextView) findViewById(R.id.txtAudioStatusNumber));
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
