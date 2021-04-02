package org.ace.insurance.medical.proposal.frontService.paymentService.interfaces;

import java.util.List;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.branch.Branch;

/***************************************************************************************
 * @author NNH
 * @Date 2014-08-14
 * @Version 1.0
 * @Purpose This interface serves as the Service Layer to manipulate the
 *          <code>MedicalProposal</code> approved process.
 * 
 ***************************************************************************************/
public interface IPaymentMedicalProposalFrontService {
	/**
	 * @param {@link MedicalProposal}, {@link WorkFlowDTO}
	 * @throws SystemException
	 *             An exception occurs during the DB operation
	 * @return {@link MedicalProposal} instance
	 * @Purpose update InsuredPerson of MedicalProposal for approved status.<br/>
	 *          update WorkFlow of MedicalProposal for WorkFlow Process.<br/>
	 *          insert new WorkFlowHistory.<br/>
	 */
	public void paymentMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String status);

}
