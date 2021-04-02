package org.ace.insurance.medical.proposal.frontService.approvedService;

import javax.annotation.Resource;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.frontService.approvedService.interfaces.IApprovedMedicalProposalFrontService;
import org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.service.interfaces.IAgentCommissionService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/***************************************************************************************
 * @author NNH
 * @Date 2014-08-14
 * @Version 1.0
 * @Purpose This class serves as the Service Layer to manipulate the
 *          <code>MedicalProposal</code> approved process.
 * 
 ***************************************************************************************/
@Service(value = "ApprovedMedicalProposalFrontService")
public class ApprovedMedicalProposalFrontService extends BaseService implements IApprovedMedicalProposalFrontService {
	@Resource(name = "MedicalProposalDAO")
	private IMedicalProposalDAO medicalProposalDAO;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "MedicalPolicyService")
	public IMedicalPolicyService medicalPolicyService;

	@Resource(name = "MedicalProposalService")
	public IMedicalProposalService medicalProposalService;

	@Resource(name = "AgentCommissionService")
	public IAgentCommissionService agentCommissionService;

	/**
	 * @see org.ace.insurance.medical.proposal.frontService.approvedService.interfaces.IApprovedMedicalProposalFrontService
	 *      #approveMedicalProposal(org.ace.insurance.medical.proposal.MedicalProposal,org.ace.insurance.common.WorkFlowDTO)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void approveMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO) {
		try {
			medicalProposalDAO.update(medicalProposal);
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
	}

	public void calculateTermPremium(MedicalPolicyInsuredPerson policyInsuredPerson, int paymentTypeMonth) {
		double basicPremium = policyInsuredPerson.getPremium();
		double addOnPremium = policyInsuredPerson.getAddOnPremium();
		if (paymentTypeMonth > 0) {
			int paymentTerm = policyInsuredPerson.getPeriodMonth() / paymentTypeMonth;
			policyInsuredPerson.setPaymentTerm(paymentTerm);
			/* Basic Term Premium */
			double basicTermPremium = (basicPremium * paymentTypeMonth) / 12;
			// policyInsuredPerson.setBasicTermPremium(basicTermPremium);
			/* AddOn Term Premium */
			double addOnTermPremium = (addOnPremium * paymentTypeMonth) / 12;
			// policyInsuredPerson.setAddOnTermPremium(addOnTermPremium);
		} else {
			policyInsuredPerson.setPaymentTerm(1);
			/* Basic Term Premium For Lump Sum */
			double basicTermPremium = (basicPremium * policyInsuredPerson.getPeriodMonth());
			// policyInsuredPerson.setBasicTermPremium(basicTermPremium);
			/* AddOn Term Premium For Lump Sum */
			double addOnTermPremium = (addOnPremium * policyInsuredPerson.getPeriodMonth());
			// policyInsuredPerson.setAddOnTermPremium(addOnTermPremium);
		}
	}
}
