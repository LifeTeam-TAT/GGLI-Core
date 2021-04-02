package org.ace.insurance.life.snakebite.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.snakebite.SnakeBiteBeneficiary;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDTO;
import org.ace.insurance.life.snakebite.SnakeBitePolicyKeyFactorValue;
import org.ace.insurance.life.snakebite.SnakeBitePolicyNoCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicySearch;
import org.ace.insurance.life.snakebite.persistence.interfaces.ISnakeBitePolicyDAO;
import org.ace.insurance.life.snakebite.service.interfaces.ISnakeBitePolicyService;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "SnakeBitePolicyService")
public class SnakeBitePolicyService extends BaseService implements ISnakeBitePolicyService {

	@Resource(name = "SnakeBitePolicyDAO")
	private ISnakeBitePolicyDAO snakeBitePolicyDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "OrganizationDAO")
	private IOrganizationDAO organizationDAO;

	@Resource(name = "UserProcessService")
	private IUserProcessService userProcessService;

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicy addNewSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy) {
		try {
			String policyPrefix = customIDGenerator.getPrefix(SnakeBitePolicy.class, true);
			snakeBitePolicy.setPrefix(policyPrefix);
			String benePrefix = customIDGenerator.getPrefix(SnakeBiteBeneficiary.class, false);
			setIdPrefixForSnakeBitePolicyBeneficiary(benePrefix, snakeBitePolicy.getSnakeBiteBeneficiaryList());
			String keyFactorPrefix = customIDGenerator.getPrefix(SnakeBitePolicyKeyFactorValue.class, false);
			setIdPrefixForSnakeBitePolicyKeyfactor(keyFactorPrefix, snakeBitePolicy.getSnakeBitePolicyKeyFactorValueList());
			calculatePremium(snakeBitePolicy);
			snakeBitePolicy = snakeBitePolicyDAO.insert(snakeBitePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a User", e);
		}
		return snakeBitePolicy;
	}

	private void setIdPrefixForSnakeBitePolicyBeneficiary(String prefix, List<SnakeBiteBeneficiary> beneficiaryList) {
		if (beneficiaryList != null) {
			for (SnakeBiteBeneficiary snake : beneficiaryList) {
				snake.setPrefix(prefix);
			}
		}
	}

	private void setIdPrefixForSnakeBitePolicyKeyfactor(String prefix, List<SnakeBitePolicyKeyFactorValue> keyFactorList) {
		if (keyFactorList != null) {
			for (SnakeBitePolicyKeyFactorValue keyFactor : keyFactorList) {
				keyFactor.setPrefix(prefix);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicy findSnakeBitePolicyByPolicyNo(String snakeBitePolicyNo) {
		SnakeBitePolicy result = null;
		try {
			result = snakeBitePolicyDAO.findSnakeBitePolicyByPolicyNo(snakeBitePolicyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Snake Bite Policy (PolicyNo : " + snakeBitePolicyNo + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicy updateSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy) {
		try {
			snakeBitePolicy = snakeBitePolicyDAO.update(snakeBitePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update user", e);
		}
		return snakeBitePolicy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy) {
		try {
			snakeBitePolicyDAO.delete(snakeBitePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a SnakeBitePolicy", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicy findSnakeBitePolicyById(String id) {
		SnakeBitePolicy result = null;
		try {
			result = snakeBitePolicyDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a SnakeBitePolicy (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findSnakeBitePolicy(List<String> proposalIdList) {
		List<SnakeBitePolicy> result = null;
		try {
			result = snakeBitePolicyDAO.findByIdList(proposalIdList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SnakeBitePolicy)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findSnakeBitePolicyByReceiptNo(String receiptNo) {
		List<SnakeBitePolicy> result = null;
		try {
			result = snakeBitePolicyDAO.findSnakeBitePolicyByReceiptNo(receiptNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SnakeBitePolicy)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findAllSnakeBitePolicy() {
		List<SnakeBitePolicy> result = null;
		try {
			result = snakeBitePolicyDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SnakeBitePolicy)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicyDTO findSnakeBitePolicyForPaymentDashboard(String referenceNo) {
		SnakeBitePolicyDTO result = null;
		try {
			result = snakeBitePolicyDAO.findSnakeBitePolicyForPaymentDashboard(referenceNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SnakeBitePolicy)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void calculatePremium(SnakeBitePolicy snakeBitePolicy) {
		if (snakeBitePolicy.getProduct().getAutoCalculate()) {
			Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
			for (SnakeBitePolicyKeyFactorValue vhKf : snakeBitePolicy.getSnakeBitePolicyKeyFactorValueList()) {
				/* Reset Sum Insured */
				KeyFactor keyfactor = vhKf.getKeyFactor();
				if (KeyFactorChecker.isSumInsured(keyfactor)) {
					double sumInsured = Double.parseDouble(vhKf.getValue());
					snakeBitePolicy.setSumInsured(sumInsured);
				}
				keyfatorValueMap.put(vhKf.getKeyFactor(), vhKf.getValue());
			}
			Double premium = productService.findProductPremiumRate(keyfatorValueMap, snakeBitePolicy.getProduct(), null);
			System.out.println("premium ***" + premium);
			snakeBitePolicy.setPremium(premium);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findSnakeBitePolicyByCriteria(SnakeBitePolicyCriteria snakeBitePolicyCriteria) {
		List<SnakeBitePolicy> result = null;
		try {
			result = snakeBitePolicyDAO.findSnakeBitePolicyByCriteria(snakeBitePolicyCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SnakeBitePolicy)", e);
		}
		return result;
	}

	// @Transactional(propagation = Propagation.REQUIRED)
	// public List<SnakeBitePolicy> paymentPolicy(List<SnakeBitePolicy>
	// snakeBitePolicyList, WorkFlowDTO workFlowDTO) {
	// List<SnakeBitePolicy> result = null;
	//
	// try {
	// logger.debug("paymentPolicy() method has been started.");
	// workFlowDTOService.updateWorkFlow(workFlowDTO);
	// if(snakeBitePolicyList != null){
	// List<AgentCommission> agentCommissionList=new
	// ArrayList<AgentCommission>();
	// double
	// agentCommissionPercent=snakeBitePolicyList.get(0).getProduct().getFirstCommission();
	// for(SnakeBitePolicy snakeBitePolicy:snakeBitePolicyList){
	// double commission = (snakeBitePolicy.getPremium() / 100) *
	// agentCommissionPercent;
	// agentCommissionList.add(new
	// AgentCommission(snakeBitePolicy.getSnakeBitePolicyNo(),
	// PolicyReferenceType.SNAKE_BITE_POLICY, snakeBitePolicy.getAgent(),
	// commission, new Date()));
	//
	// }
	// }
	// logger.debug("paymentPolicy() method has been successfully finisehd.");
	// } catch (DAOException e) {
	// logger.error("paymentPolicy() method has been failed.");
	// throw new SystemException(e.getErrorCode(), "Faield paymentPolicy)", e);
	// }
	// return result;
	// }

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentSnakeBitePolicy(List<SnakeBitePolicy> snakeBitePolicyList, Payment payment) {
		try {
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			workFlowDTOService.deleteWorkFlowByRefNo(payment.getReceiptNo());
			double totalCommission = 0.0;
			String customerId = null;
			User user = userProcessService.getLoginUser();
			List<AgentCommission> agentCommissionList = new ArrayList<AgentCommission>();
			if (snakeBitePolicyList.size() > 0) {
				double agentCommissionPercent = snakeBitePolicyList.get(0).getProduct().getFirstCommission();
				for (SnakeBitePolicy policy : snakeBitePolicyList) {
					policy.setComplete(true);
					snakeBitePolicyDAO.update(policy);
					/* update ActivePolicy Count in CustomerTable */
					int activePolicyCount = policy.getCustomer().getActivePolicy() + 1;
					customerDAO.updateActivePolicy(activePolicyCount, policy.getCustomer().getId());
					if (policy.getAgent() != null) {
						totalCommission += (policy.getPremium() / 100) * agentCommissionPercent;
					}
				}

				if (snakeBitePolicyList.get(0).getAgent() != null) {
					agentCommissionList
							.add(new AgentCommission(payment.getReceiptNo(), PolicyReferenceType.SNAKE_BITE_POLICY, snakeBitePolicyList.get(0).getAgent(), totalCommission));
				}

			}
			if (snakeBitePolicyList.get(0).getAgent() != null) {
				customerId = snakeBitePolicyList.get(0).getAgent().getId();
			} else if (snakeBitePolicyList.get(0).getSaleMan() != null) {
				customerId = snakeBitePolicyList.get(0).getSaleMan().getId();
			} else if (snakeBitePolicyList.get(0).getReferral() != null) {
				customerId = snakeBitePolicyList.get(0).getReferral().getId();
			}
			paymentService.activateSnakeBitePayment(payment, customerId, user.getBranch(), agentCommissionList, currencyCode, snakeBitePolicyList.get(0).getSalePoint());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a SnakeBite for ReceiptNo : " + payment.getReceiptNo(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmSnakeBitePolicy(List<SnakeBitePolicy> snakeBitePolicyList, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO) {
		List<Payment> paymentList = new ArrayList<Payment>();
		double totalPremium = 0.0;
		double agentCommissionAmount = 0.0;
		try {
			double commission = snakeBitePolicyList.get(0).getProduct().getFirstCommission();
			for (SnakeBitePolicy snakeBitePolicy : snakeBitePolicyList) {
				if (snakeBitePolicy.getAgent() != null) {
					agentCommissionAmount = snakeBitePolicy.getPremium() * commission / 100;
					totalPremium += snakeBitePolicy.getPremium() - agentCommissionAmount;
				} else {
					totalPremium += snakeBitePolicy.getPremium();
				}
			}
			Payment payment = new Payment();
			payment.setBank(paymentDTO.getBank());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.SNAKE_BITE_POLICY);
			payment.setConfirmDate(new Date());
			payment.setBasicPremium(totalPremium);
			payment.setReferenceNo(paymentDTO.getReferenceNo());
			paymentList.add(payment);
			paymentList = paymentService.prePayment(paymentList);
			workFlowDTO.setReferenceNo(paymentList.get(0).getReceiptNo());
			workFlowDTOService.addNewWorkFlow(workFlowDTO);
			for (SnakeBitePolicy snakeBitePolicy : snakeBitePolicyList) {
				snakeBitePolicy.setReferenceNo(paymentList.get(0).getReceiptNo());
				snakeBitePolicyDAO.update(snakeBitePolicy);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield confirmSnakeBitePolicy)", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> findBookNo() {
		List<String> result = null;
		try {
			result = snakeBitePolicyDAO.findBookNo();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find bookNo)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicySearch> findSnakeBitePolicyNoByCriteria(SnakeBitePolicyNoCriteria criteria) {
		List<SnakeBitePolicySearch> ret = new ArrayList<SnakeBitePolicySearch>();
		try {
			ret = snakeBitePolicyDAO.findSnakeBitePolicyNoByCriteria(criteria);
		} catch (DAOException e) {
			throw new org.ace.java.component.SystemException(e.getErrorCode(), "Faield to retrieve Snake Bite Policy No. by Criteria", e);
		}
		return ret;
	}
}