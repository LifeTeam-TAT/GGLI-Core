package org.ace.insurance.life.renewal.service.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;

public interface IRenewalGroupLifePolicyService {
	public PolicyInsuredPerson findInsuredPersonByCodeNo(String codeNo);

	public void updatePaymentDate(String lifePolicyId, Date paymentDate, Date paymentValidDate);

	public List<LifePolicy> findLifePolicyByProposalId(String proposalId);

	public void increaseLifePolicyPrintCount(String lifeProposalId);

	public void addNewLifePolicy(LifePolicy lifePolicy);

	public List<LifePolicy> activateLifePolicy(String lifeProposalId);
}
