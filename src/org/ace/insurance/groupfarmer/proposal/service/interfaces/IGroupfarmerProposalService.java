package org.ace.insurance.groupfarmer.proposal.service.interfaces;

import java.util.List;
import java.util.Map;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposalDTO;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.user.User;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.SystemException;

public interface IGroupfarmerProposalService {

	public void addNewGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO, Map<String, String> proposalUploadedFileMap,
			BancaassuranceProposal bancaassuranceProposal, String PROPOSAL_DIR, String tempDir, String uploadPath);

	public void updateGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, BancaassuranceProposal bancaassuranceProposal);

	public void editGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO, Map<String, String> uploadedFileMap, String proposalDir,
			String temporyDir, String uploadPath);

	public void deleteGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal);

	public GroupFarmerProposal findGroupFarmerById(String id);

	List<Payment> confirmGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status);

	public void paymentGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, List<Payment> paymentList, Branch branch, String status);

	public void addNewFarmerProposal(List<LifeProposal> proposalList, GroupFarmerProposal groupFarmerProposal);

	public List<GroupFarmerProposalDTO> findAllGroupFarmerProposalByisPaymentComplete(EnquiryCriteria criteria);

	public void addNewFarmerPolicy(List<LifeProposal> lifeProposal, User user) throws SystemException;

	public void rejectGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO);

	List<GroupFarmerProposalDTO> findGroupFarmerProposalByEnquiryCriteria(EnquiryCriteria criteria);

	public void deletePayment(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO);

	public List<GroupFarmerProposal> finGroupFarmerProposalByPOByReceiptNo(String receiptNo, String bpmsReceiptNo);

}
