package com.brass.votosurveytaker;

import java.util.Comparator;

import org.votomobile.proxy.taker.Invitation;

public class InvitationSorter implements Comparator<Invitation> {
	// 1 for true, 0 for false
	private byte isInv1Complete;
	private byte isInv2Complete;

	@Override
	public int compare(Invitation inv1, Invitation inv2) {
		boolean isInv1CompletedSurvey = inv1.getDeliveryStatus() == Invitation.DeliveryStatus.COMPLETED;
		boolean isInv2CompletedSurvey = inv2.getDeliveryStatus() == Invitation.DeliveryStatus.COMPLETED;

		// determine inv1 and inv2 delivery status (completion)
		if (isInv1CompletedSurvey) {
			isInv1Complete = 1;
		} else {
			isInv1Complete = 0;
		}

		if (isInv2CompletedSurvey) {
			isInv2Complete = 1;
		} else {
			isInv2Complete = 0;
		}

		return isInv1Complete - isInv2Complete;
	}
}
