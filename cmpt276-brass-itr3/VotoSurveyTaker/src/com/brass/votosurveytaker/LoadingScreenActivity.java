package com.brass.votosurveytaker;

import java.util.ArrayList;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.Question.ResponseType;

import android.os.Bundle;
import android.view.Menu;

/**
 * class dealing with loading questions or introduction display before jump to
 * actually question. make UI more smooth transit for the first unanswered
 * question or introduction android activity class
 */
public class LoadingScreenActivity extends AbstractActivity implements
		DataChangeListener {
	private static int surveyId;
	private static int surveyIndex;
	private static int currentQuestionIndex = -1;
	private boolean hasUnansweredQuestion = false;
	private boolean hasIntro = false;
	private ArrayList<Question> questions = new ArrayList<Question>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_screen);

		getInvitationInfoFromIntent();
		populateListOfQuestions();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loading_screen, menu);
		return true;
	}

	@Override
	public void dataChanged() {
		int index = 0;
		hasUnansweredQuestion = false;
		hasIntro = false;
		questions.clear();
		for (Question question : App.getQuestionManager().questions()) {
			questions.add(question);
			if (!hasUnansweredQuestion) {
				if (question.getResponse() == null
						&& question.getResponseType() != ResponseType.INTRODUCTION_OR_CONCLUSION) {
					hasUnansweredQuestion = true;
				} else {
					index++;
				}
			}
		}

		// check if question has an intro
		if (questions.get(0).getResponseType() == ResponseType.INTRODUCTION_OR_CONCLUSION) {
			hasIntro = true;
		}

		if (!hasUnansweredQuestion) {
			index = 0;
			currentQuestionIndex = index;
			QuestionActivitySelector
					.setCurrentQuestionIndex(currentQuestionIndex);
			QuestionActivitySelector.switchToQuestionActivity(
					LoadingScreenActivity.this,
					questions.get(currentQuestionIndex).getResponseType(),
					surveyId, surveyIndex);
		} else if (hasUnansweredQuestion) {
			// start on the introduction if the first unanswered question is the
			// question proceeding an introduction
			if (index == 1 && hasIntro) {
				index = 0;
			}
			currentQuestionIndex = index;
			QuestionActivitySelector
					.setCurrentQuestionIndex(currentQuestionIndex);
			QuestionActivitySelector.switchToQuestionActivity(
					LoadingScreenActivity.this,
					questions.get(currentQuestionIndex).getResponseType(),
					surveyId, surveyIndex);
		}
	}

	@Override
	protected AbstractManager getManager() {
		return App.getQuestionManager();
	}

}
