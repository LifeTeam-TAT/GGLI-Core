package org.ace.insurance.medical.groupMicroHealth.proposal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.persistence.interfaces.IGroupMicroHealthDAO;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.enquires.medical.groupMicroHealth.GroupMicroHealthCriteria;
import org.ace.insurance.web.manage.medical.groupMicroHealth.policy.GroupMicroHealthDTO;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "GroupMicroHealthService")
public class GroupMicroHealthService extends BaseService implements IGroupMicroHealthService {
	@Resource(name = "GroupMicroHealthDAO")
	private IGroupMicroHealthDAO groupMicroHealthDAO;

	@Resource(name = "WorkFlowService")
	public IWorkFlowService workFlowService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void createGroupMicroHealth(GroupMicroHealth groupMicroHealth, WorkFlowDTO workFlowDTO, BancaassuranceProposal bancaassuranceProposal) throws SystemException {
		try {
			setProposalNo(groupMicroHealth);
			groupMicroHealth = groupMicroHealthDAO.create(groupMicroHealth);
			workFlowDTO.setReferenceNo(groupMicroHealth.getId());
			workFlowService.addNewWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to create Group Micro Health", e);
		}
	}

	private void setProposalNo(GroupMicroHealth groupMicroHealth) {
		String proposalNo = null;
		String groupMicroHealthProposalNo = SystemConstants.GROUP_MICRO_HEALTH_PROPOSAL_NO;
		proposalNo = customIDGenerator.getNextId(groupMicroHealthProposalNo, null, true);
		groupMicroHealth.setProposalNo(proposalNo);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateGroupMicroHealth(GroupMicroHealth groupMicroHealth) throws SystemException {
		try {
			groupMicroHealthDAO.update(groupMicroHealth);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Group Micro Health", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteGroupMicroHealth(GroupMicroHealth groupMicroHealth, BancaassuranceProposal bancaassuranceProposal) throws SystemException {
		try {
			groupMicroHealthDAO.delete(groupMicroHealth);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to Delete Group Micro Health", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupMicroHealth> findAllGroupMicroHealth() throws SystemException {
		try {
			return groupMicroHealthDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all Group Micro Healt ", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public GroupMicroHealth findById(String id) throws SystemException {
		try {
			return groupMicroHealthDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all Group Micro Healt ", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmGroupMicroHealth(GroupMicroHealth groupMicroHealth, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch) throws SystemException {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			workFlowService.updateWorkFlow(workFlowDTO);
			Payment payment = new Payment();
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(paymentDTO.getReferenceType());
			payment.setReceivedDeno(paymentDTO.getReceivedDeno());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setBasicPremium(groupMicroHealth.getTotalPremium());
			payment.setFromTerm(1);
			payment.setToTerm(1);
			payment.setReferenceNo(groupMicroHealth.getId());
			paymentList.add(payment);
			paymentList = paymentService.prePayment(paymentList);
			prePaymentGroupMicroHealth(groupMicroHealth, paymentList, branch);
			if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				prePaymentGroupMicroHealthTransfer(groupMicroHealth, paymentList, branch);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm a MedicalProposal", e);
		}
		return paymentList;
	}

	private void prePaymentGroupMicroHealth(GroupMicroHealth groupMicroHealth, List<Payment> paymentList, Branch branch) throws SystemException {
		try {
			List<AgentCommission> agentCommissionList = null;
			if (groupMicroHealth.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				double totalCommission = 0.0;
				double commissionPercent = 10;
				if (commissionPercent > 0) {
					double termPremium = groupMicroHealth.getTotalPremium();
					totalCommission = (termPremium / 100) * commissionPercent;
				}

				agentCommissionList
						.add(new AgentCommission(groupMicroHealth.getId(), PolicyReferenceType.GROUP_MICRO_HEALTH, groupMicroHealth.getAgent(), totalCommission, new Date()));
			}
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			// TODO FIXME PSH
			String accountCode = "HEALTH_PREMIUM_INCOME";
			for (Payment payment : paymentList) {
				if (groupMicroHealth.getId().equals(payment.getReferenceNo())) {
					accountPaymentList.add(new AccountPayment(accountCode, payment));
					break;
				}

			}

			// TODO FIXME PSH GET customer Id
			String customerId = null;

			paymentService.preActivatePayment(accountPaymentList, customerId, branch, agentCommissionList, false, CurrencyUtils.getCurrencyCode(null),
					groupMicroHealth.getSalePoint());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a MedicalProposal ID : " + groupMicroHealth.getId(), e);
		}

	}

	public void prePaymentGroupMicroHealthTransfer(GroupMicroHealth groupMicroHealth, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = "KYT";
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (Payment payment : paymentList) {
				accountPaymentList.add(new AccountPayment(payment.getAccountBank().getAcode(), payment));
			}
			paymentService.activatePaymentTransfer(accountPaymentList, null, branch, false, currencyCode, groupMicroHealth.getSalePoint());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a MotorProposal ID : " + groupMicroHealth.getId(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentGroupMicroHealth(GroupMicroHealth groupMicroHealth, List<Payment> paymentList, Branch branch) {
		try {

			List<AgentCommission> agentCommissionList = null;

			/* get receipt No */
			String receiptNo = paymentList.get(0).getReceiptNo();

			/* get agent commission of each policy */

			if (groupMicroHealth.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				double firstAgentCommission = (groupMicroHealth.getTotalPremium() / 100) * 10;
				double commissionPercent = 10;
				Payment p = paymentService.findPaymentByReferenceNo(groupMicroHealth.getId());
				AgentCommissionEntryType type = AgentCommissionEntryType.UNDERWRITING;
				agentCommissionList.add(new AgentCommission(groupMicroHealth.getId(), paymentList.get(0).getReferenceType(), groupMicroHealth.getAgent(), firstAgentCommission,
						new Date(), receiptNo, groupMicroHealth.getTotalPremium(), commissionPercent, type, p.getRate(), (p.getRate() * firstAgentCommission), p.getCur(),
						(p.getRate() * groupMicroHealth.getTotalPremium())));
			}

			/* add agent commission, activate TLF and Payment flag */
			// TODO FIXME PSH add salepoint
			paymentService.activatePaymentAndTLF(paymentList, agentCommissionList, branch, CurrencyUtils.getCurrencyCode(null), null);

			/* update ActivePolicy Count in CustomerTable */
			/*
			 * if (medicalProposal.getCustomer() != null) { int
			 * activePolicyCount =
			 * medicalProposal.getCustomer().getActivePolicy() + 1;
			 * customerDAO.updateActivePolicy(activePolicyCount,
			 * medicalProposal.getCustomer().getId()); if
			 * (medicalProposal.getCustomer().getActivedDate() == null) {
			 * customerDAO.updateActivedPolicyDate(new Date(),
			 * medicalProposal.getCustomer().getId()); } }
			 */
			workFlowService.deleteWorkFlowByRefNo(groupMicroHealth.getId());
			groupMicroHealth.setPaymentComplete(true);
			groupMicroHealthDAO.update(groupMicroHealth);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a MedicalProposal ID : " + groupMicroHealth.getId(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupMicroHealthDTO> findAllPaymentCompleteDTO(GroupMicroHealthCriteria criteria) throws SystemException {
		try {
			return groupMicroHealthDAO.findAllPaymentCompleteDTO(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Fail to find all ", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupMicroHealthDTO> findAllProcessCompleteDTO(EnquiryCriteria criteria) throws SystemException {
		try {
			return groupMicroHealthDAO.findALLProcessCompleteDTO(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Fail to find all ", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateGroupMicroHealth(GroupMicroHealth groupMicroHealth, WorkFlowDTO workFlowDTO, BancaassuranceProposal bancaassuranceProposal) throws SystemException {
		try {
			groupMicroHealthDAO.update(groupMicroHealth);
			workFlowDTO.setReferenceNo(groupMicroHealth.getId());
			workFlowService.updateWorkFlow((workFlowDTO));
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Group Micro Health", e);
		}

	}

}
