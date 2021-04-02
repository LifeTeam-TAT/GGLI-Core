package org.ace.insurance.medical.claim.frontService.approved.interfaces;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.claim.MedicalClaimProposal;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;

/***************************************************************************************
 * @author NNH
 * @Date 2014-08-15
 * @Version 1.0
 * @Purpose This interface serves as the Service Layer to manipulate the
 *          <code>MedicalClaim</code> approved process.
 * 
 ***************************************************************************************/
public interface IApprovedMedicalClaimFrontService {

	/**
	 * 
	 * @param {@link MedicalClaimProposal}, {@link WorkFlowDTO}
	 * @throws SystemException
	 *             An exception occurs during the DB operation
	 * @return {@link MedicalClaimProposal} instance
	 * @Purpose update InsuredPerson of MedicalClaim for approved status.<br/>
	 *          update WorkFlow of MedicalClaim for WorkFlow Process.<br/>
	 *          insert new WorkFlowHistory.<br/>
	 */

	public void approveMedicalClaim(MedicalClaimProposalDTO claimProposalDTO, WorkFlowDTO  workflowDTO);
}
