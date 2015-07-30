package com.brass.votosurveytaker;

import org.votomobile.proxy.taker.Question.ResponseType;

import android.app.Activity;
import android.content.Intent;

/**
 * 
 * helper class to help switch questions between different types
 */

public class QuestionActivitySelector {
	private static int currentQuestionIndex = -1;

	// getter function
	public static int getCurrentQuestionIndex() {
		return currentQuestionIndex;
	}

	// setter
	public static void setCurrentQuestionIndex(int currentQuestionIndex) {
		QuestionActivitySelector.currentQuestionIndex = currentQuestionIndex;
	}

	// static function to switch activities
	public static void switchToQuestionActivity(Activity currentActivity,
			ResponseType type, int surveyId, int surveyIndex) {
		switch (type) {
		case MULTIPLE_CHOICE:
			Intent intent = new Intent(currentActivity,
					QuestionMultipleActivity.class);
			intent.putExtra("surveyId", surveyId);
			intent.putExtra("surveyIndex", surveyIndex);
			currentActivity.startActivity(intent);
			break;
		case NUMERIC:
			Intent intent2 = new Intent(currentActivity,
					QuestionNumberActivity.class);
			intent2.putExtra("surveyId", surveyId);
			intent2.putExtra("surveyIndex", surveyIndex);
			currentActivity.startActivity(intent2);
			break;
		case OPEN:
			Intent intent3 = new Intent(currentActivity,
					QuestionOpenActivity.class);
			intent3.putExtra("surveyId", surveyId);
			intent3.putExtra("surveyIndex", surveyIndex);
			currentActivity.startActivity(intent3);
			break;
		case INTRODUCTION_OR_CONCLUSION:
			Intent intent4 = new Intent(currentActivity,
					SurveyDescriptionActivity.class);
			intent4.putExtra("surveyId", surveyId);
			intent4.putExtra("surveyIndex", surveyIndex);
			currentActivity.startActivity(intent4);
			break;
		default:
			assert false;
		}

		currentActivity.finish();
	}

}
