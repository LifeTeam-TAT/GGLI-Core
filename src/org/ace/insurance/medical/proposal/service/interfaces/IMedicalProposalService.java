package org.ace.insurance.medical.proposal.service.interfaces;

import java.util.Date;
/***************************************************************************************
 * @author Kyaw Myat Htut
 * @Date 2014-6-31
 * @Version 1.0
 * @Purpose 
 * 
 *    
 ***************************************************************************************/
import java.util.List;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.productprocess.ProductProcessCriteriaDTO;
import org.ace.insurance.medical.productprocess.StudentAgeType;
import org.ace.insurance.medical.proposal.MP001;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalSurvey;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.medical.surveyquestion.ProductProcessQuestionLink;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.SystemException;

public interface IMedicalProposalService {
	public void issueMedicalProposal(MedicalPolicy medicalPolicy);

	/*
	 * public List<Payment> confirmMedicalProposal(MedicalProposal
	 * medicalProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch
	 * branch, String status, boolean isSkipTLFPayment);
	 */

	/*
	 * public List<Payment> confirmSkipPaymentTLFMedicalProposal(MedicalProposal
	 * medicalProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch
	 * branch, String status);
	 */

	public void informProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, AcceptedInfo acceptedInfo, String status);

	public void rejectMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO);

	public MedicalProposal findMedicalProposalById(String id);

	/**
	 * 
	 * @param {@link
	 *            MedicalProposal} instance
	 * @Purpose calculate all of the total premium( proposal premium and addOn
	 *          premium ) of MedicalProposal (based on key factor)
	 */
	public void calculatePremium(MedicalProposal medicalProposal);

	public List<ProductProcessQuestionLink> findPProQueByPPId(String productId, String processId, ProductProcessCriteriaDTO productProcessCriteriaDTO);

	public List<ProductProcessQuestionLink> findPProQueByPPIdByOptionTypeForSPCL(String productId, String processId, ProductProcessCriteriaDTO productProcessCriteriaDTO);

	public List<ProductProcessQuestionLink> findPProQueByPPIdByOptionTypeForSTSPCL(String productId, String processId, ProductProcessCriteriaDTO productProcessCriteriaDTO);

	public List<MP001> findAllMedicalPolicy(EnquiryCriteria enquiryCriteria);

	public MedicalSurvey findMedicalSurveyByProposalId(String id);

	public List<SurveyQuestionAnswer> findSurveyQuestionAnswerByProposalId(String proposalId);

	public void updateMedicalProposal(MedicalProposal medicalProposal);

	public void updateInsPerWithReasonAndApprove(String rejectReason, boolean approved, String id);

	public void updateMedicalProposalCompleteStatus(String proposalId);

	public void paymentMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String status);

	public void prePaymentMedicalProposalTransfer(MedicalProposal medicalProposal, List<Payment> paymentList, Branch branch);

	public void deletePayment(MedicalPolicy medicalPolicy, WorkFlowDTO workFlowDTO);

	List<Payment> confirmMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status, boolean isSkipTLFPayment,
			Date updatedSubmittedDate);

	List<Payment> confirmSkipPaymentTLFMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status,
			Date updatedSubmittedDate);

	List<MedicalProposal> findMedicalProposalByGroupMicroHealthId(String id) throws SystemException;

	void createGroupMicroHealtPolicy(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO) throws SystemException;

	public List<ProductProcessQuestionLink> findMedicalPProQueByPPIdMedical(String productName, String processName, StudentAgeType studentAgeType);

}
