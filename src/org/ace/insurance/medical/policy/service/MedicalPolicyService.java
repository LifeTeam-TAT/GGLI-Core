/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.medical.policy.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.NotificationCriteria;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyHistoryEntryType;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.medical.claim.MedicalPolicyCriteria;
import org.ace.insurance.medical.policy.MED002;
import org.ace.insurance.medical.policy.MPL001;
import org.ace.insurance.medical.policy.MPL002;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.policy.persistence.interfaces.IMedicalPolicyDAO;
import org.ace.insurance.medical.policy.policyEditHistory.service.interfaces.IMedicalPolicyHistoryService;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IAgentCommissionService;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.web.manage.enquires.PolicyEnquiryCriteria;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionDTO;
import org.ace.insurance.web.manage.life.billcollection.PolicyNotificationDTO;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "MedicalPolicyService")
public class MedicalPolicyService extends BaseService implements IMedicalPolicyService {

	@Resource(name = "MedicalPolicyDAO")
	private IMedicalPolicyDAO medicalPolicyDAO;

	@Resource(name = "MedicalProposalService")
	private IMedicalProposalService medicalProposalService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "AgentCommissionService")
	private IAgentCommissionService agentCommissionService;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "MedicalPolicyHistoryService")
	private IMedicalPolicyHistoryService medicalPolicyHistoryService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewMedicalPolicy(MedicalPolicy medicalPolicy) {
		try {
			medicalPolicyDAO.insert(medicalPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new MedicalPolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMedicalPolicy(MedicalPolicy medicalPolicy) {
		try {
			medicalPolicyDAO.update(medicalPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a MedicalPolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteMedicalPolicy(MedicalPolicy medicalPolicy) {
		try {
			medicalPolicyDAO.delete(medicalPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a MedicalPolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BillCollectionDTO> findBillCollectionByCriteria(PolicyCriteria criteria) {
		List<BillCollectionDTO> result = null;
		try {
			result = medicalPolicyDAO.findBillCollectionByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a findBillCollectionByCriteria ");
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int findMedicalPolicyInsuredPersonById(String id) {
		List<String> result = null;
		int unit = 0;
		try {
			result = medicalPolicyDAO.findMedPolicyInsuredPersonById(id);
			for (String insuUnit : result) {
				unit += Integer.parseInt(insuUnit);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a MedicalProposal (ID : " + id + ")", e);
		}
		return unit;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalPolicy> findAllMedicalPolicy() {
		List<MedicalPolicy> result = null;
		try {
			result = medicalPolicyDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of MedicalPolicy)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalPolicy findMedicalPolicyById(String id) {
		MedicalPolicy result = null;
		try {
			result = medicalPolicyDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a MedicalPolicy (ID : " + id + ")", e);
		}
		return result;
	}

	/*
	 * used in proposalPayment & cloneProposalPayment & renewalPayment &
	 * billCollectionPayment(MultiPayment)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalPolicy activateMedicalPolicy(String medicalProposalId, String generatedPolicyNo) {
		MedicalPolicy policy = null;
		try {
			policy = medicalPolicyDAO.findByProposalId(medicalProposalId);
			List<MedicalPolicyInsuredPerson> personList = policy.getPolicyInsuredPersonList();
			Product product = personList.get(0).getProduct();
			String productCode = product.getProductGroup().getPolicyNoPrefix();
			// TODO Change Policy no MICRO_HEALTH (GHM0000000001)
			policy.setPolicyNo(customIDGenerator.getNextId(generatedPolicyNo, productCode, true));
			policy.setCommenmanceDate(policy.getCommenmanceDate());
			if (policy.getMedicalProposal().getProposalType().equals(ProposalType.RENEWAL)) {
				// submittedDate is startDate in medicalRenewal
				MedicalProposalInsuredPerson proposalInsuredPerson = policy.getMedicalProposal().getMedicalProposalInsuredPersonList().get(0);
				for (MedicalPolicyInsuredPerson person : personList) {
					person.setStartDate(proposalInsuredPerson.getStartDate());
					person.setEndDate(proposalInsuredPerson.getEndDate());
				}
				policy.setActivedPolicyStartDate(proposalInsuredPerson.getStartDate());
			} else {
				for (MedicalPolicyInsuredPerson person : personList) {
					person.setStartDate(policy.getActivedPolicyStartDate());
					Calendar cal = Calendar.getInstance();
					cal.setTime(person.getStartDate());
					cal.add(Calendar.MONTH, person.getPeriodMonth());
					person.setEndDate(cal.getTime());
				}
			}
			
			if (policy.getPaymentType().getMonth() != 0) {
				Calendar lpCal = Calendar.getInstance();
				lpCal.setTime(policy.getActivedPolicyStartDate());
				lpCal.add(Calendar.MONTH, policy.getPaymentType().getMonth());
				policy.setActivedPolicyEndDate(lpCal.getTime());
			} else {
				policy.setActivedPolicyEndDate(personList.get(0).getEndDate());
			}
			policy.setLastPaymentTerm(1);
			medicalPolicyDAO.update(policy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a MedicalPolicy", e);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalPolicy findMedicalPolicyByProposalId(String proposalId) {
		MedicalPolicy result = null;
		try {
			result = medicalPolicyDAO.findByProposalId(proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Policy by ProposalId : " + proposalId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalPolicy findMedicalPolicyByPolicyNo(String policyNo) {
		MedicalPolicy result = null;
		try {
			result = medicalPolicyDAO.findMedicalPolicyByPolicyNo(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a MedicalProposal (PolicyNo : " + policyNo + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MPL002> findMedicalPolicyForClaimByCriteria(MedicalPolicyCriteria criteria) {
		List<MPL002> medicalPolicyList = null;
		try {
			medicalPolicyList = medicalPolicyDAO.findMedicalPolicyForClaimByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve Medical Policy for Claim by Criteria", e);
		}
		return medicalPolicyList;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MPL002> findMedicalPolicyForClaimByProduct(String product) {
		List<MPL002> medicalPolicyList = null;
		try {
			medicalPolicyList = medicalPolicyDAO.findMedicalPolicyForClaimByProduct(product);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve Medical Policy for Claim by Product", e);
		}
		return medicalPolicyList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MED002> findMedicalPolicyForPolicyEnquiry(PolicyEnquiryCriteria criteria) {
		List<MED002> medicalPolicyList = new ArrayList<MED002>();
		try {
			medicalPolicyList = medicalPolicyDAO.findMedicalPolicyForPolicyEnquiry(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve Medical Policy for Claim by Criteria", e);
		}
		return medicalPolicyList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMedicalPolicy(MedicalPolicy medicalPolicy, Payment payment) {
		try {
			if (PolicyReferenceType.getMedicalPolicyReference().contains(payment.getReferenceType())) {
				String productCode = medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct().getProductGroup().getPolicyNoPrefix();
				String productId = medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct().getId();
				String systemConstant = null;
				// if
				// (productId.equals(ProductIDConfig.getMedicalProductId())) {
				// systemConstant = SystemConstants.MEDICAL_POLICY_NO;
				// } else
				if (productId.equals(ProductIDConfig.getIndividualHealthInsuranceId()) || productId.equals(ProductIDConfig.getGroupHealthInsuranceId())) {
					systemConstant = SystemConstants.HEALTH_POLICY_NO;
				} else if (productId.equals(ProductIDConfig.getMicroHealthInsurance())) {
					systemConstant = SystemConstants.MICRO_HEALTH_POLICY_NO;
				} else if (productId.equals(ProductIDConfig.getIndividualCriticalIllness_Id()) || productId.equals(ProductIDConfig.getGroupCriticalIllness_Id())) {
					systemConstant = SystemConstants.CRITICAL_ILLNESS_POLICY_NO;
				}
				String policyNo = null;
				policyNo = customIDGenerator.getNextId(systemConstant, productCode, true);
				medicalPolicy.setPolicyNo(policyNo);
				/*
				 * if (medicalPolicy.getAgent() != null) { AgentCommission
				 * agentComission = //
				 * agentCommissionService.findAgentCommissionByChallanNo
				 * (payment.getChalanNo());
				 * agentCommissionService.updateAgentCommisssion(medicalPolicy,
				 * agentComission); }
				 */
			}
			String receiptNo = null;
			if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.TRANSFER_RECEIPT_NO, null, true);
			} else if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
			} else if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
			}
			payment.setReceiptNo(receiptNo);
			medicalProposalService.updateMedicalProposalCompleteStatus(medicalPolicy.getMedicalProposal().getId());
			medicalPolicyDAO.update(medicalPolicy);
			paymentDAO.update(payment);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a MedicalPolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalPolicy> findByCustomer(Customer customer) {
		List<MedicalPolicy> ret = null;
		try {
			ret = medicalPolicyDAO.findByCustomer(customer);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find medicalPolicy by customer", e);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void activateBillCollection(MedicalPolicy medicalPolicy) {
		try {
			medicalPolicyDAO.updateBillCollection(medicalPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update payment.", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MPL001> findMedicalPolicyByCriteria(MedicalPolicyCriteria criteria) {
		List<MPL001> policyList = new ArrayList<MPL001>();
		try {
			policyList = medicalPolicyDAO.findPolicyByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve Medical Policy by Criteria", e);
		}
		return policyList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PolicyNotificationDTO> findPolicyNotification(NotificationCriteria criteria) {
		List<PolicyNotificationDTO> result = new ArrayList<PolicyNotificationDTO>();
		try {
			result = medicalPolicyDAO.findPolicyNotificationByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve PolicyNotificationDTO by Criteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void overwriteMedicalPolicy(MedicalPolicy medicalPolicy) {
		try {
			MedicalPolicy oldPolicy = findMedicalPolicyById(medicalPolicy.getId());
			medicalPolicyHistoryService.addNewMedicalPolicyHistory(oldPolicy, PolicyStatus.UPDATE, PolicyHistoryEntryType.UNDERWRITING);
			medicalPolicy.setPolicyStatus(PolicyStatus.UPDATE);
			for (MedicalPolicyInsuredPerson person : medicalPolicy.getPolicyInsuredPersonList()) {
				for (MedicalPolicyInsuredPersonBeneficiaries beneficiary : person.getPolicyInsuredPersonBeneficiariesList()) {
					if (beneficiary.getBeneficiaryNo() == null) {
						beneficiary.setBeneficiaryNo(customIDGenerator.getNextId(SystemConstants.MEDICAL_BENEFICIARY_NO, null, false));
					}
				}
			}
			medicalPolicyDAO.update(medicalPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to overwrite a LifePolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalPolicy> findLifePolicyPOByReceiptNo(String receiptNo, String bpmsReceiptNo, PolicyReferenceType policyReferenceType) {
		List<MedicalPolicy> result = new ArrayList<MedicalPolicy>();
		try {
			result = medicalPolicyDAO.findPaymentOrderByReceiptNo(receiptNo, bpmsReceiptNo, policyReferenceType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find lifePolicy payment order by Receipt No ");
		}
		return result;
	}

}
