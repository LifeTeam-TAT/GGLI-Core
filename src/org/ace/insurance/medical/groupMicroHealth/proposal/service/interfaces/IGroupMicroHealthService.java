package org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces;

import java.util.List;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.enquires.medical.groupMicroHealth.GroupMicroHealthCriteria;
import org.ace.insurance.web.manage.medical.groupMicroHealth.policy.GroupMicroHealthDTO;
import org.ace.java.component.SystemException;

public interface IGroupMicroHealthService {

	void createGroupMicroHealth(GroupMicroHealth groupMicroHealth, WorkFlowDTO workFlowDTO, BancaassuranceProposal bancaassuranceProposal) throws SystemException;

	void updateGroupMicroHealth(GroupMicroHealth groupMicroHealth) throws SystemException;

	void deleteGroupMicroHealth(GroupMicroHealth groupMicroHealth, BancaassuranceProposal bancaassuranceProposal) throws SystemException;

	List<GroupMicroHealth> findAllGroupMicroHealth() throws SystemException;

	GroupMicroHealth findById(String id) throws SystemException;

	void updateGroupMicroHealth(GroupMicroHealth groupMicroHealth, WorkFlowDTO workFlowDTO, BancaassuranceProposal bancaassuranceProposal) throws SystemException;

	List<Payment> confirmGroupMicroHealth(GroupMicroHealth groupMicroHealth, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch) throws SystemException;

	void paymentGroupMicroHealth(GroupMicroHealth groupMicroHealth, List<Payment> paymentList, Branch branch);

	List<GroupMicroHealthDTO> findAllPaymentCompleteDTO(GroupMicroHealthCriteria criteria) throws SystemException;

	List<GroupMicroHealthDTO> findAllProcessCompleteDTO(EnquiryCriteria criteria) throws SystemException;

}