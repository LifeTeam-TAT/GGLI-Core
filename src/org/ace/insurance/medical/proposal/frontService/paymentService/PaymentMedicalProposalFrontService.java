package org.ace.insurance.medical.proposal.frontService.paymentService;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.frontService.paymentService.interfaces.IPaymentMedicalProposalFrontService;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.branch.Branch;
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
@Service(value = "PaymentMedicalProposalFrontService")
public class PaymentMedicalProposalFrontService extends BaseService implements IPaymentMedicalProposalFrontService {
	@Resource(name = "MedicalProposalService")
	private IMedicalProposalService medicalProposalService;

	/**
	 * @see org.ace.insurance.medical.proposal.frontService.approvedService.interfaces.IApprovedMedicalProposalFrontService
	 *      #approveMedicalProposal(org.ace.insurance.medical.proposal.MedicalProposal,org.ace.insurance.common.WorkFlowDTO)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String status) {
		try {
			medicalProposalService.paymentMedicalProposal(medicalProposal, workFlowDTO, paymentList, branch, status);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
	}

}
