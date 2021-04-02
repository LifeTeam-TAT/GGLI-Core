package org.ace.insurance.life.claim.service.interfaces;

import java.util.List;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimNotification;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeClaimSurvey;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.web.manage.life.claim.LifeDisabilityPaymentCriteria;

public interface ILifeClaimProposalService {

	public void updateLifeClaimProposal(LifeClaimProposal claimProposal);

	public void deleteLifeClaimProposal(LifeClaimProposal claimProposal);

	public LifeClaimProposal findLifeClaimProposalById(String id);

	public void addNewLifeClaimSurvey(LifeClaimSurvey lifeClaimSurvey, WorkFlowDTO workFlow);

	public void approveLifeClaim(LifeClaimProposal claimProposal, WorkFlowDTO workFlow);

	public void informLifeClaim(ClaimAcceptedInfo claimAcceptedInfo, LifeClaimProposal lifeClaimProposal, WorkFlowDTO workFlow);

	public void confirmLifeClaimPropsal(LifeClaimProposal claimProposal, PaymentDTO paymentDTO, WorkFlowDTO workFlow);

	public void paymentLifeClaimProposal(LifeClaimProposal claimProposal, List<Payment> paymentList);

	public List<DisabilityLifeClaim> findDisabilityLifeClaimByLifeClaimProposalNo(LifeDisabilityPaymentCriteria criteria);

	public void confirmLifeDisabilityClaim(DisabilityLifeClaim disabilityClaim, PaymentDTO payment, WorkFlowDTO workFlow);

	void addNewLifeClaimProposal(LifeClaimProposal claimProposal, LifeClaimNotification lifeClaimNotification, WorkFlowDTO workFlow);
	public void rejectLifeClaimPropsal(LifeClaimProposal claimProposal, WorkFlowDTO workFlow);
}
