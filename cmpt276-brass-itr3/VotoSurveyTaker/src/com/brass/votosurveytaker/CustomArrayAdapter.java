package com.brass.votosurveytaker;

import java.util.ArrayList;
import org.votomobile.proxy.taker.Invitation;
import org.votomobile.proxy.taker.Invitation.DeliveryStatus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * customer class for set view for listView
 * 
 * 
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private Activity current;
	private ArrayList<Invitation> invitations;

	// constructor for class
	public CustomArrayAdapter(Context context,
			ArrayList<Invitation> invitationArray, final Activity activity) {
		super(context, R.layout.item_view);
		this.context = context;
		this.invitations = invitationArray;
		current = activity;
	}

	// override getView for customer class
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Make sure we have a view to work with (may have been given null)
		View itemView = convertView;
		if (itemView == null) {
			itemView = inflater.inflate(R.layout.item_view, parent, false);
		}

		// Find the invitation to work with.
		if (position < invitations.size()) {
			Invitation inv = invitations.get(position);

			// Fill the view
			ImageView imageView = (ImageView) itemView.findViewById(R.id.imgSurveyStatus);
			if (inv.getDeliveryStatus() == DeliveryStatus.INVITED) {
				imageView.setImageResource(R.drawable.survey_not_started);
				itemView.setBackgroundResource(R.color.LightBlue);
			} else if (inv.getDeliveryStatus() == DeliveryStatus.IN_PROGRESS) {
				imageView.setImageResource(R.drawable.survey_in_progress);
				itemView.setBackgroundResource(R.color.LightGold);
			} else {
				imageView.setImageResource(R.drawable.survey_complete);
				itemView.setBackgroundResource(R.color.LightGreen);
			}

			// Title:
			TextView titleText = (TextView) itemView
					.findViewById(R.id.txtSurveyTitle);
			titleText.setText("" + inv.getSurveyTitle());

			// Date Invited:
			TextView dateText = (TextView) itemView
					.findViewById(R.id.txtInviteDate);
			dateText.setText(current.getString(R.string.date_invited_)
					+ inv.getDateInvited());

			// Questions Info:
			TextView questionsText = (TextView) itemView
					.findViewById(R.id.txtQuestionsInfo);
			questionsText.setText(current
					.getString(R.string.questions_answered_)
					+ inv.getNumberSurveyQuestionsAnswered()
					+ current.getString(R.string._of_)
					+ inv.getNumberSurveyQuestions());
		}

		return itemView;
	}
}