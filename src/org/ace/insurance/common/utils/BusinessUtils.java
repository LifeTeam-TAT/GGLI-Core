package org.ace.insurance.common.utils;

import java.util.Arrays;
import java.util.List;

import org.ace.insurance.common.PolicyReferenceType;

public class BusinessUtils {

	public static List<PolicyReferenceType> getLifePolicyReferenceTypeList() {
		return Arrays.asList(PolicyReferenceType.LIFE_POLICY, PolicyReferenceType.LIFE_BILL_COLLECTION,
				PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY,
				PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION, PolicyReferenceType.STUDENT_LIFE_POLICY,
				PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION,
				PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY,
				PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY,
				PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY);
	}

	public static boolean isLifePolicyReferenceType(PolicyReferenceType policyReferenceType) {
		if (getLifePolicyReferenceTypeList().contains(policyReferenceType)) {
			return true;
		}
		return false;
	}

	public static double getTermPremium(double amount, int month) {
		return month > 0 ? amount * month / 12 : amount;
	}

}
