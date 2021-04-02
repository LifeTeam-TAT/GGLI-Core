package org.ace.insurance.medical.proposal.frontService.proposalService.interfaces;

/***************************************************************************************
 * @author Kyaw Myat Htut
 * @Date 2014-6-31
 * @Version 1.0
 * @Purpose 
 * 
 *    
 ***************************************************************************************/
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;

public interface IAddNewMedicalProposalFrontService {
	/**
	 * 
	 * @param {@link
	 *            MedicalProposal}, {@link WorkFlowDTO}
	 * @throws SystemException
	 *             An exception occurs during the DB operation
	 * @return {@link MedicalProposal} instance
	 * @Purpose calculate premium of MedicalProposal and insert.
	 */
	public MedicalProposal addNewMedicalProposal(MedProDTO medicalProposalDTO, WorkFlowDTO workFlowDTO, String status, BancaassuranceProposal bancaassuranceProposal);

	/**
	 * 
	 * @param {@link
	 *            MedicalProposal}, {@link WorkFlowDTO}
	 * @throws SystemException
	 *             An exception occurs during the DB operation
	 * @return {@link MedicalProposal} instance
	 * @Purpose calculate premium of MedicalProposal and update.
	 */
	public MedicalProposal updateMedicalProposal(MedProDTO medicalProposalDTO, WorkFlowDTO workFlowDTO, BancaassuranceProposal bancaassuranceProposal);
}
