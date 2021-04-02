package org.ace.insurance.life.endorsement.service.interfaces;

import java.util.List;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.branch.Branch;

public interface ILifeEndorsementService {

	public LifeEndorseInfo addNewLifeEndorseInfo(LifeProposal lifeProposal);

	public LifeEndorseInfo updateLifeEndorseInfo(LifeProposal lifeProposal);

	public LifeEndorseInfo findLastLifeEndorseInfoByProposalNo(String policyNo);

	public LifeEndorseInfo findBySourcePolicyReferenceNo(String policyId);

	public LifeEndorseInfo findByEndorsePolicyReferenceNo(String policyId);

	public void calculatePremium(LifeProposal lifeProposal);

	public LifeProposal addNewLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, String status);

	public void approveLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO);

	public void rejectLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO);

	public List<Payment> confirmLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status);

	public void paymentLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, List<Payment> payment, Branch branch, String status);

	public void issueLifeProposal(LifeProposal lifeProposal);

	public void addNewSurvey(LifeSurvey lifeSurvey, WorkFlowDTO workFlowDTO);

	public LifeProposal updateLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO);

	public LifeProposal overWriteLifeProposal(LifeProposal lifeProposal);

	public LifeProposal addNewShortEndowLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, String name);

	public List<Payment> confirmShortTermEndowLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO,Branch branch);

}
