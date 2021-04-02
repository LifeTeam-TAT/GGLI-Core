package org.ace.insurance.life.renewal.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.LifePolicyAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonAddon;
import org.ace.insurance.life.policy.PolicyInsuredPersonAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.PolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.life.policy.persistence.interfaces.ILifePolicyDAO;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifePolicyService;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.product.Product;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "RenewalGroupLifePolicyService")
public class RenewalGroupLifePolicyService extends BaseService implements IRenewalGroupLifePolicyService {

	@Resource(name = "LifePolicyDAO")
	private ILifePolicyDAO lifePolicyDAO;

	@Resource(name = "RenewalGroupLifeProposalService")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewLifePolicy(LifePolicy lifePolicy) {
		try {
			setPrefix(lifePolicy);

			lifePolicyDAO.insert(lifePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifePolicy", e);
		}
	}

	private void setPrefix(LifePolicy lifePolicy) {
		String lPolicyPrefix = customIDGenerator.getPrefix(LifePolicy.class, true);
		lifePolicy.setPrefix(lPolicyPrefix);
		List<LifePolicyAttachment> lpAttachmentList = lifePolicy.getAttachmentList();
		if (lpAttachmentList != null && !lpAttachmentList.isEmpty()) {
			for (LifePolicyAttachment lpAtt : lpAttachmentList) {
				String attPrefix = customIDGenerator.getPrefix(LifePolicyAttachment.class, false);
				lpAtt.setPrefix(attPrefix);
			}
		}
		for (PolicyInsuredPerson pi : lifePolicy.getPolicyInsuredPersonList()) {
			String insPrefix = customIDGenerator.getPrefix(PolicyInsuredPerson.class, false);
			pi.setPrefix(insPrefix);
			List<PolicyInsuredPersonAttachment> piAttachmentList = pi.getAttachmentList();
			if (piAttachmentList != null && !piAttachmentList.isEmpty()) {
				for (PolicyInsuredPersonAttachment att : piAttachmentList) {
					String attPrefix = customIDGenerator.getPrefix(PolicyInsuredPersonAttachment.class, false);
					att.setPrefix(attPrefix);
				}
			}
			List<PolicyInsuredPersonAddon> insAddOnList = pi.getPolicyInsuredPersonAddOnList();
			if (insAddOnList != null && !insAddOnList.isEmpty()) {
				for (PolicyInsuredPersonAddon vehAddOn : insAddOnList) {
					String insAddOnPrefix = customIDGenerator.getPrefix(PolicyInsuredPersonAddon.class, false);
					vehAddOn.setPrefix(insAddOnPrefix);
				}
			}
			List<PolicyInsuredPersonBeneficiaries> beneficiaryList = pi.getPolicyInsuredPersonBeneficiariesList();
			if (beneficiaryList != null && !beneficiaryList.isEmpty()) {
				for (PolicyInsuredPersonBeneficiaries beneficiary : beneficiaryList) {
					String benePrefix = customIDGenerator.getPrefix(PolicyInsuredPersonBeneficiaries.class, false);
					beneficiary.setPrefix(benePrefix);
				}
			}
			List<PolicyInsuredPersonKeyFactorValue> policyKeyFactorValueList = pi.getPolicyInsuredPersonkeyFactorValueList();
			if (policyKeyFactorValueList != null && !policyKeyFactorValueList.isEmpty()) {
				for (PolicyInsuredPersonKeyFactorValue kf : policyKeyFactorValueList) {
					String kfPrefix = customIDGenerator.getPrefix(PolicyInsuredPersonKeyFactorValue.class, false);
					kf.setPrefix(kfPrefix);
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PolicyInsuredPerson findInsuredPersonByCodeNo(String codeNo) {
		PolicyInsuredPerson result = null;
		try {
			result = lifePolicyDAO.findInsuredPersonByCodeNo(codeNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find InsuredPerson By CodeNo : " + codeNo, e);
		}
		return result;
	}

	@Override
	public void updatePaymentDate(String lifePolicyId, Date paymentDate, Date paymentValidDate) {
		try {
			lifePolicyDAO.updatePaymentDate(lifePolicyId, paymentDate, paymentValidDate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Life Policy payment status", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findLifePolicyByProposalId(String proposalId) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findByProposalId(proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Policy by ProposalId : " + proposalId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseLifePolicyPrintCount(String lifeProposalId) {
		try {
			lifePolicyDAO.increasePrintCount(lifeProposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to increase LifePolicy print count. " + e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> activateLifePolicy(String lifeProposalId) {
		List<LifePolicy> policyList = null;
		try {
			policyList = lifePolicyDAO.findByProposalId(lifeProposalId);
			LifeProposal lifeProposal = renewalGroupLifeProposalService.findLifeProposalById(lifeProposalId);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			String endorsementNo = null;
			String policyNo = null;
			for (LifePolicy lifePolicy : policyList) {
				String productCode = lifePolicy.getInsuredPersonInfo().get(0).getProduct().getProductGroup().getPolicyNoPrefix();
				if (ProductIDConfig.isGroupLife(product)) {
					if (lifePolicy.getPolicyNo() == null) {
						policyNo = customIDGenerator.getNextId(SystemConstants.GROUP_LIFE_POLICY_NO, productCode, true);
					} else {
						policyNo = lifePolicy.getPolicyNo();
					}
				}
				lifePolicy.setPolicyNo(policyNo);
				lifePolicy.setCommenmanceDate(lifePolicy.getInsuredPersonInfo().get(0).getStartDate());

				lifePolicy.setActivedPolicyStartDate(lifePolicy.getInsuredPersonInfo().get(0).getStartDate());
				lifePolicy.setActivedPolicyEndDate(lifePolicy.getInsuredPersonInfo().get(0).getEndDate());
				lifePolicy.setLastPaymentTerm(1);
				lifePolicyDAO.update(lifePolicy);
				/*
				 * Don't remove this process : Zaw Than Oo
				 * if(isCoInsurance(lifePolicy)) {
				 * coinsuranceService.addNewCoinsurances(lifePolicy); }
				 */
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifePolicy", e);
		}
		return policyList;
	}
}
