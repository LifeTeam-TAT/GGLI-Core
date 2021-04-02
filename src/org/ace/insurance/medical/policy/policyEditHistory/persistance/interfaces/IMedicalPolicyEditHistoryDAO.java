package org.ace.insurance.medical.policy.policyEditHistory.persistance.interfaces;

import org.ace.insurance.common.PolicyHistoryEntryType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.life.policyEditHistory.LifePolicyEditHistory;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.policyEditHistory.MedicalPolicyEditHistory;
import org.ace.java.component.persistence.exception.DAOException;

public interface IMedicalPolicyEditHistoryDAO {
	
	public void insert(MedicalPolicyEditHistory  medicalPolicyHistory) throws DAOException;
	

}
