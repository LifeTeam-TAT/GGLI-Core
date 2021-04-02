package org.ace.insurance.medical.proposal.frontService.approvedService.interfaces;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalProposal;

/***************************************************************************************
 * @author NNH
 * @Date 2014-08-14
 * @Version 1.0
 * @Purpose This interface serves as the Service Layer to manipulate the
 *          <code>MedicalProposal</code> approved process.
 * 
 ***************************************************************************************/
public interface IApprovedMedicalProposalFrontService {
	/**
	 * @param {@link
	 * 			MedicalProposal}, {@link WorkFlowDTO}
	 * @throws SystemException
	 *             An exception occurs during the DB operation
	 * @return {@link MedicalProposal} instance
	 * @Purpose update InsuredPerson of MedicalProposal for approved
	 *          status.<br/>
	 *          update WorkFlow of MedicalProposal for WorkFlow Process.<br/>
	 *          insert new WorkFlowHistory.<br/>
	 */
	public void approveMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO);

	// public void calculateTermPremium(MedicalPolicyInsuredPerson
	// policyInsuredPerson, int paymentTypeMonth);
}
