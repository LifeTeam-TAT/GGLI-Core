package org.ace.insurance.life.policy.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.coinsurance.OutCoinsuranceCriteria;
import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.NotificationCriteria;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyHistoryEntryType;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.LifeEndowmentPolicySearch;
import org.ace.insurance.life.claim.ClaimStatus;
import org.ace.insurance.life.claim.LifePolicyCriteria;
import org.ace.insurance.life.claim.LifePolicySearch;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.LifeEndorseItem;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.EndorsementLifePolicyPrint;
import org.ace.insurance.life.policy.LPC001;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.LifePolicyAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonAddon;
import org.ace.insurance.life.policy.PolicyInsuredPersonAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.PolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.life.policy.persistence.interfaces.ILifePolicyDAO;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyEditHistory.service.interfaces.ILifePolicyEditHistoryService;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LPL002;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionDTO;
import org.ace.insurance.web.manage.life.billcollection.PolicyNotificationDTO;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "LifePolicyService")
public class LifePolicyService extends BaseService implements ILifePolicyService {

	@Resource(name = "LifePolicyDAO")
	private ILifePolicyDAO lifePolicyDAO;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "LifePolicyHistoryService")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	@Resource(name = "LifePolicyEditHistoryService")
	private ILifePolicyEditHistoryService lifePolicyEditHistoryService;

	@Resource(name = "LifeProposalService")
	private ILifeProposalService lifeProposalService;

	@Resource(name = "LifePolicySummaryService")
	private ILifePolicySummaryService lifePolicySummaryService;

	@Resource(name = "LifeEndorsementService")
	private ILifeEndorsementService lifeEndorsementService;

	@Resource(name = "CoinsuranceService")
	private ICoinsuranceService coinsuranceService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

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
	public List<LifePolicy> activateLifePolicy(String lifeProposalId) {
		List<LifePolicy> policyList = null;
		try {
			policyList = lifePolicyDAO.findByProposalId(lifeProposalId);
			LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
			boolean isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
			String endorsementNo = null;
			String policyNo = null;
			for (LifePolicy lifePolicy : policyList) {
				if (lifePolicy.isEndorsementApplied()) {

					/* Set Old Policy No */
					String lifePolicyNo = lifeProposal.getLifePolicy().getPolicyNo();
					lifePolicy.setPolicyNo(lifePolicyNo);

					/* Set Endorsement No */
					String productCode = lifePolicy.getInsuredPersonInfo().get(0).getProduct().getProductGroup().getPolicyNoPrefix();
					if (ProductIDConfig.isPublicLife(product)) {
						endorsementNo = customIDGenerator.getNextId(SystemConstants.ENDOWMENT_LIFE_ENDORSE_POLICY_NO, productCode, true);
					} else if (ProductIDConfig.isGroupLife(product)) {
						endorsementNo = customIDGenerator.getNextId(SystemConstants.GROUP_LIFE_ENDORSE_POLICY_NO, productCode, true);
					}
					// TODO FIXME PSH
					// else if (isSportMan(product)) {
					// endorsementNo =
					// customIDGenerator.getNextId(SystemConstants.SPORTSMAN_LIFE_POLICY_ENDORSEMENTS_NO,
					// productCode, true);
					// }
					else if (ProductIDConfig.isShortEndowmentLife(product)) {
						endorsementNo = customIDGenerator.getNextId(SystemConstants.SHORT_TERM_ENDOWMENT_ENDORSE_POLICY_NO, productCode, true);
					}
					lifePolicy.setEndorsementNo(endorsementNo);

					/*
					 * if PublicLife Policy, reset activePolicyEndDate By
					 * CoverTime
					 */
					if (!isShortEndowLife) {
						if (lifePolicy.getInsuredPersonInfo().size() == 1) {
							Date startDate = lifePolicy.getCommenmanceDate();
							Date endDate = lifePolicy.getActivedPolicyEndDate();
							int passedMonths = Utils.monthsBetween(startDate, endDate, false, true);
							LifeEndorseInfo lifeEndorseInfo = lifeEndorsementService.findByEndorsePolicyReferenceNo(lifeProposal.getLifePolicy().getId());
							LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(lifeEndorseInfo.getSourcePolicyReferenceNo());
							Date activePolicyDate = lifeProposal.getLifePolicy().getActivedPolicyEndDate();
							if (summary != null) {
								if (summary.getCoverTime() > 0) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(activePolicyDate);
									for (int i = 0; i < summary.getCoverTime(); i++) {
										cal.add(Calendar.MONTH, lifeProposal.getLifePolicy().getPaymentType().getMonth());
										activePolicyDate = cal.getTime();
										cal.setTime(activePolicyDate);
									}
								} else if (lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList().get(0).getLifeEndorseChangeList() != null
										&& !lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList().get(0).getLifeEndorseChangeList().isEmpty()) {
									if (lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList().get(0).getLifeEndorseChangeList().get(0).getEndorsePremium() != 0.0
											&& passedMonths % 12 > 5 && lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList().get(0).getLifeEndorseChangeList().get(0)
													.getLifeEndorseItem().equals(LifeEndorseItem.DECREASE_SI)) {
										int restmonths = 12 - (passedMonths % 12);

										activePolicyDate = Utils.addMonth(activePolicyDate, restmonths);
									}

								}
							}
							lifePolicy.setActivedPolicyEndDate(activePolicyDate);
						}
					}
				} else {
					String productCode = product.getProductGroup().getPolicyNoPrefix();
					String policyIdIdGen = null;
					if (ProductIDConfig.isPublicLife(product)) {
						policyIdIdGen = SystemConstants.ENDOWMENT_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isGroupLife(product)) {
						policyIdIdGen = SystemConstants.GROUP_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isSportMan(product)) {
						policyIdIdGen = SystemConstants.SPORTSMAN_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isFarmer(product)) {
						policyIdIdGen = SystemConstants.FARMER_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isPersonalAccidentKYT(product) || ProductIDConfig.isPersonalAccidentUSD(product)) {
						policyIdIdGen = SystemConstants.PERSONAL_ACCIDENT_POLICY_NO;
					} else if (ProductIDConfig.isShortEndowmentLife(product)) {
						policyIdIdGen = SystemConstants.SHORT_TERM_ENDOWMENT_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isStudentLife(product)) {
						policyIdIdGen = SystemConstants.STUDENT_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						policyIdIdGen = SystemConstants.SNAKE_BITE_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isPublicTermLife(product)) {
						policyIdIdGen = SystemConstants.PUBLIC_TERM_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isSinglePremiumEndowmentLife(product)) {
						policyIdIdGen = SystemConstants.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isSinglePremiumCreditLife(product)) {
						policyIdIdGen = SystemConstants.SINGLE_PREMIUM_CREDIT_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isShortTermSinglePremiumCreditLife(product)) {
						policyIdIdGen = SystemConstants.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY_NO;
					} else if (ProductIDConfig.isSimpleLife(product)) {
						policyIdIdGen = SystemConstants.SIMPLE_LIFE_POLICY_NO;
					}

					policyNo = customIDGenerator.getNextId(policyIdIdGen, productCode, true);
					lifePolicy.setPolicyNo(policyNo);
					lifePolicy.setCommenmanceDate(new Date());
					if (ProductIDConfig.isSimpleLife(product)) {
						lifePolicy.setCommenmanceDate(lifeProposal.getProposalInsuredPersonList().get(0).getStartDate());
						for (PolicyInsuredPerson policyInsPerson : lifePolicy.getInsuredPersonInfo()) {
							policyInsPerson.setStartDate(lifeProposal.getProposalInsuredPersonList().get(0).getStartDate());
							Calendar cal = Calendar.getInstance();
							cal.setTime(policyInsPerson.getStartDate());
							if (policyInsPerson.getPeriodYear() > 0) {
								cal.add(Calendar.YEAR, policyInsPerson.getPeriodYear());
							} else if (policyInsPerson.getPeriodMonth() > 0) {
								cal.add(Calendar.MONTH, policyInsPerson.getPeriodMonth());
							} else {
								cal.add(Calendar.DAY_OF_MONTH, policyInsPerson.getPeriodWeek() * 7);
							}
							policyInsPerson.setEndDate(cal.getTime());
						}

					} else if (!(isShortEndowLife || isStudentLife)) {
						for (PolicyInsuredPerson policyInsPerson : lifePolicy.getInsuredPersonInfo()) {
							policyInsPerson.setStartDate(new Date());
							Calendar cal = Calendar.getInstance();
							cal.setTime(policyInsPerson.getStartDate());
							cal.add(Calendar.MONTH, policyInsPerson.getPeriodMonth());
							policyInsPerson.setEndDate(cal.getTime());
						}
					}

					lifePolicy.setActivedPolicyStartDate(
							((isShortEndowLife || ProductIDConfig.isSimpleLife(product)) || isStudentLife) ? lifePolicy.getInsuredPersonInfo().get(0).getStartDate() : new Date());
					if (lifePolicy.getPaymentType().getMonth() != 0) {
						if (isShortEndowLife && lifePolicy.getLifeProposal().isMigrate()) {
							List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, false);
							lifePolicy.setLastPaymentTerm(paymentList.size());
							LPC001 oldLifePolicyNoAndId = new LPC001();
							oldLifePolicyNoAndId.setId(lifeProposal.getLifePolicy().getId());
							oldLifePolicyNoAndId.setPolicyStatus(PolicyStatus.TERMINATE);
							updatePublicLifePolicy(oldLifePolicyNoAndId);
						} else {
							lifePolicy.setLastPaymentTerm(1);
						}
						Calendar lpCal = Calendar.getInstance();
						lpCal.setTime(lifePolicy.getActivedPolicyStartDate());
						lpCal.add(Calendar.MONTH, lifePolicy.getLastPaymentTerm() * lifeProposal.getPaymentType().getMonth());
						lifePolicy.setActivedPolicyEndDate(lpCal.getTime());
					} else {
						lifePolicy.setActivedPolicyEndDate(lifePolicy.getInsuredPersonInfo().get(0).getEndDate());
						lifePolicy.setLastPaymentTerm(1);
					}
					// calculate premium payment end date
					Calendar cal = Calendar.getInstance();
					cal.setTime(lifePolicy.getPolicyInsuredPersonList().get(0).getEndDate());
					if (isStudentLife)
						cal.add(Calendar.YEAR, -3);
					lifePolicy.setPaymentEndDate(cal.getTime());
				}
				lifePolicy.setPolicyStatus(PolicyStatus.INFORCE);
				lifePolicyDAO.update(lifePolicy);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifePolicy", e);
		}
		return policyList;
	}

	private void resetId(LifePolicy lifePolicy) {
		lifePolicy.overwriteId(null);
		lifePolicy.setVersion(0);
		for (LifePolicyAttachment attach : lifePolicy.getAttachmentList()) {
			attach.overwriteId(null);
			attach.setVersion(0);
		}
		for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
			person.overwriteId(null);
			person.setVersion(0);
			for (PolicyInsuredPersonAttachment attach : person.getAttachmentList()) {
				attach.overwriteId(null);
				attach.setVersion(0);
			}
			for (PolicyInsuredPersonAddon addOn : person.getPolicyInsuredPersonAddOnList()) {
				addOn.overwriteId(null);
				addOn.setVersion(0);
			}
			for (PolicyInsuredPersonKeyFactorValue kfv : person.getPolicyInsuredPersonkeyFactorValueList()) {
				kfv.overwriteId(null);
				kfv.setVersion(0);
			}
			for (PolicyInsuredPersonBeneficiaries beneficiaries : person.getPolicyInsuredPersonBeneficiariesList()) {
				beneficiaries.overwriteId(null);
				beneficiaries.setVersion(0);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateLifePolicy(LifePolicy lifePolicy) {
		try {
			LifePolicy oldPolicy = findLifePolicyById(lifePolicy.getId());
			lifePolicyHistoryService.addNewLifePolicy(oldPolicy, PolicyStatus.UPDATE, PolicyHistoryEntryType.ENDORSEMENT);
			lifePolicy.setPolicyStatus(PolicyStatus.UPDATE);
			resetId(lifePolicy);
			addNewLifePolicy(lifePolicy);
			lifePolicyDAO.delete(oldPolicy);
			// deleteLifePolicy(oldPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifePolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicy(LifePolicy lifePolicy) {
		try {
			lifePolicyDAO.update(lifePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifePolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void overwriteLifePolicy(LifePolicy lifePolicy) {
		try {
			LifePolicy oldPolicy = findLifePolicyById(lifePolicy.getId());
			lifePolicyEditHistoryService.addNewLifePolicy(oldPolicy, PolicyStatus.UPDATE, PolicyHistoryEntryType.UNDERWRITING);
			lifePolicy.setPolicyStatus(PolicyStatus.UPDATE);
			setPrefix(lifePolicy);
			for (PolicyInsuredPerson person : lifePolicy.getPolicyInsuredPersonList()) {
				for (PolicyInsuredPersonBeneficiaries beneficiary : person.getPolicyInsuredPersonBeneficiariesList()) {
					if (beneficiary.getBeneficiaryNo() == null) {
						beneficiary.setBeneficiaryNo(customIDGenerator.getNextId(SystemConstants.LIFE_BENEFICIARY_NO, null, false));
					}
				}
			}
			lifePolicyDAO.update(lifePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to overwrite a LifePolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void terminateLifePolicy(LifePolicy lifePolicy) {
		try {
			PolicyStatus status = null;
			int policyPeriodYear = lifePolicy.getPolicyInsuredPersonList().get(0).getPeriodMonth() / 12;
			DateTime vDate = new DateTime(lifePolicy.getActivedPolicyEndDate());
			DateTime cDate = new DateTime(lifePolicy.getCommenmanceDate());
			int passedYear = (Months.monthsBetween(cDate, vDate).getMonths() + 1) / 12;
			if (lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getId().equals(ProductIDConfig.getPublicLifeId())) {
				if (policyPeriodYear >= 5 && passedYear >= 2 || policyPeriodYear >= 12 && passedYear >= 3) {
					status = PolicyStatus.TERMINATE;
				}
			} else {
				status = PolicyStatus.DELETE;
			}
			LifePolicy oldPolicy = findLifePolicyById(lifePolicy.getId());
			lifePolicyHistoryService.addNewLifePolicy(oldPolicy, status, PolicyHistoryEntryType.ENDORSEMENT);
			lifePolicy.setPolicyStatus(status);
			lifePolicy.setDelFlag(true);
			resetId(lifePolicy);
			addNewLifePolicy(lifePolicy);
			deleteLifePolicy(oldPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to terminate a LifePolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteLifePolicy(LifePolicy lifePolicy) {
		try {
			lifePolicyDAO.delete(lifePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a LifePolicy", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifePolicy findLifePolicyById(String id) {
		LifePolicy result = null;
		try {
			result = lifePolicyDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LPC001 findLifePolicyNoAndIdById(String id) {
		LPC001 result = null;
		try {
			result = lifePolicyDAO.findLifePolicyNoAndIdById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a find Life PolicyNo And Id By Proposal Id", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifePolicy findLifePolicyByLifeProposalId(String lifeProposaId) {
		LifePolicy result = null;
		try {
			result = lifePolicyDAO.findByLifeProposalId(lifeProposaId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal (lifeProposaId : " + lifeProposaId + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findPolicyByProposalId(String lifeProposaId) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findByProposalId(lifeProposaId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifePolicy", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findLifePolicyByPolicyId(String policyId) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findByPolicyId(policyId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Policy by PolicyId : " + policyId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findLifePolicyByReceiptNo(String receiptNo) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findByReceiptNo(receiptNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of LifePolicy by ReceiptNo : " + receiptNo, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findAllLifePolicy() {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of LifePolicy)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findAllActiveLifePolicy() {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findAllActiveLifePolicy();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of ActiveLifePolicy)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findByDate(Date startDate, Date endDate) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findByDate(startDate, endDate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal (ID : " + e);
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
	public void updateLifePolicyCommenmanceDate(Date date, String lifeProposalId) {
		try {
			lifePolicyDAO.updateCommenmanceDate(date, lifeProposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update LifePolicy commenmance date. " + e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LPL002> findLifePolicyByEnquiryCriteria(EnquiryCriteria criteria) {
		List<LPL002> result = null;
		try {
			result = lifePolicyDAO.findByEnquiryCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifePolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findByCustomer(Customer customer) {
		List<LifePolicy> ret = null;
		try {
			ret = lifePolicyDAO.findByCustomer(customer);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find lifePolicy by customer", e);
		}
		return ret;
	}

	@Override
	public void updatePaymentDate(String lifePolicyId, Date paymentDate, Date paymentValidDate) {
		try {
			lifePolicyDAO.updatePaymentDate(lifePolicyId, paymentDate, paymentValidDate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Life Policy payment status", e);
		}
	}

	@Override
	public void updateEndorsementStatus(Boolean status, LifePolicy lifePolicy) {
		try {
			lifePolicy.setEndorsementApplied(status);
			lifePolicyDAO.updateEndorsementStatus(status, lifePolicy.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Life Policy endorsement status", e);
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

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuredPersonStatusByCodeNo(String codeNo, EndorsementStatus status) {
		try {
			lifePolicyDAO.updateInsuredPersonStatusByCodeNo(codeNo, status);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update InsuredPerson Status By CodeNo : " + codeNo, e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyStatusById(String id, PolicyStatus status) {
		try {
			lifePolicyDAO.updatePolicyStatusById(id, status);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Policy Status By Id : " + id, e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyAttachment(LifePolicy lifePolicy) {
		try {
			setPrefix(lifePolicy);
			lifePolicyDAO.updatePolicyAttachment(lifePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a MotorProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifePolicy findLifePolicyByPolicyNo(String policyNo) {
		LifePolicy result = null;
		try {
			result = lifePolicyDAO.findLifePolicyByPolicyNo(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal (PolicyNo : " + policyNo + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBeneficiaryClaimStatusById(String id, ClaimStatus status) {
		try {
			lifePolicyDAO.updateBeneficiaryClaimStatusById(id, status);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Claim Status By Id : " + id, e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findLifeCoPolicyByCriteria(OutCoinsuranceCriteria criteria) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findLifeCoPolicyByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Life CoPolicy By Criteria " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicySearch> findLifePolicyForClaimByCriteria(LifePolicyCriteria criteria) {
		List<LifePolicySearch> ret = new ArrayList<LifePolicySearch>();
		try {
			ret = lifePolicyDAO.findLifePolicyForClaimByCriteria(criteria);
		} catch (DAOException e) {
			throw new org.ace.java.component.SystemException(e.getErrorCode(), "Faield to retrieve Life Policy for Claim by Criteria", e);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicySearch> findActiveLifePolicy(LifePolicyCriteria criteria) {
		List<LifePolicySearch> ret = new ArrayList<LifePolicySearch>();
		try {
			ret = lifePolicyDAO.findActiveLifePolicy(criteria);
		} catch (DAOException e) {
			throw new org.ace.java.component.SystemException(e.getErrorCode(), "Faield to retrieve Life Policy for Claim by Criteria", e);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LPC001> findRenewalGroupLifePolicyByPolicyCriteria(PolicyCriteria criteria, int max) {
		List<LPC001> result = null;
		try {
			result = lifePolicyDAO.findRenewalGroupLifePolicyByPolicyCriteria(criteria, max);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifePolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BillCollectionDTO> findBillCollectionByCriteria(PolicyCriteria criteria) {
		List<BillCollectionDTO> result = null;
		try {
			result = lifePolicyDAO.findBillCollectionByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifePolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BillCollectionDTO> findLifePaidUpProposalByCriteria(PolicyCriteria criteria) {
		List<BillCollectionDTO> result = null;
		try {
			result = lifePolicyDAO.findPaidUpPolicyByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifePolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findLifePolicyByPageCriteria(PolicyCriteria criteria) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findByPageCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifePolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void activateBillCollection(LifePolicy lifePolicy) {
		try {
			lifePolicyDAO.updateBillCollection(lifePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update payment.", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PolicyNotificationDTO> findNotificationLifePolicy(NotificationCriteria criteria) {
		List<PolicyNotificationDTO> result = null;
		try {
			result = lifePolicyDAO.findNotificationLifePolicy(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find " + e);
		}
		return result;
	}

	private void setPrefix(EndorsementLifePolicyPrint policyPrint) {
		String policyPrintPrefix = customIDGenerator.getPrefix(EndorsementLifePolicyPrint.class, true);
		policyPrint.setPrefix(policyPrintPrefix);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<EndorsementLifePolicyPrint> findEndorsementPolicyPrintByLifePolicyNo(String lifePolicyNo) {
		List<EndorsementLifePolicyPrint> result = null;
		try {
			result = lifePolicyDAO.findEndorsementPolicyPrintByLifePolicyNo(lifePolicyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield find EndorsementPolicyPrint By FirePolicyNo", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewEndorsementLifePolicyPrint(EndorsementLifePolicyPrint endorsementPolicyPrint) {
		try {
			setPrefix(endorsementPolicyPrint);
			lifePolicyDAO.insert(endorsementPolicyPrint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new EndorsementLifePolicyPrint", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateEndorsementLifePolicyPrint(EndorsementLifePolicyPrint endorsementPolicyPrint) {
		try {
			lifePolicyDAO.update(endorsementPolicyPrint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new EndorsementLifePolicyPrint", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public EndorsementLifePolicyPrint findEndorsementPolicyPrintById(String id) {
		EndorsementLifePolicyPrint result = null;
		try {
			result = lifePolicyDAO.findEndorsementLifePolicyPrintById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new EndorsementLifePolicyPrint", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findLifePolicyByReceiptNoCriteria(ReceiptNoCriteria criteria, int max) {
		List<Payment> result = null;
		try {
			result = lifePolicyDAO.findByReceiptNoCriteria(criteria, max);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifePolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LPC001> findByPolicyCriteria(PolicyCriteria criteria, int max) {
		List<LPC001> result = null;
		try {
			result = lifePolicyDAO.findEndorsementByCriteria(criteria, max);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifePolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeEndowmentPolicySearch> findPublicLifePolicyBycriteria(LifePolicyCriteria criteria, boolean isMigrate) {
		List<LifeEndowmentPolicySearch> result = new ArrayList<>();
		double totalPaidPremiumOfHis = 0;
		try {
			for (LifeEndowmentPolicySearch endowPolicy : lifePolicyDAO.findPublicLifePolicyByCriteria(criteria, isMigrate)) {
				totalPaidPremiumOfHis = lifePolicyDAO.findTotalPaidPremiumFromLifePolicyHistoryByPolicyNo(endowPolicy.getPolicyNo());
				if (totalPaidPremiumOfHis > 0) {
					endowPolicy.setPaidPremium(endowPolicy.getPaidPremium() + totalPaidPremiumOfHis);
				}
				result.add(endowPolicy);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to Find PublicLife Policy by Criteria" + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePublicLifePolicy(LPC001 oldLifePolicy) {
		LPC001 result = null;
		try {
			lifePolicyDAO.updatePublicLifePolicy(oldLifePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update PublicLife Policy" + e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double findPublicLifeTotalPaidPremium(String lifePolicyId) {
		double result = 0.0;
		try {
			result = lifePolicyDAO.findPublicLifeTotalPaidPremium(lifePolicyId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update PublicLife Policy" + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findLifePolicyIdByLifeProposalId(String id) {
		String result = null;
		try {
			result = lifePolicyDAO.findLifePolicyIdByLifeProposalId(id);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifePolicyId By LifeProposalId" + e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public LifePolicy findByLifeProposalId(String lifeProposalId) {
		LifePolicy result = null;
		try {
			result = lifePolicyDAO.findByLifeProposalId(lifeProposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifePolicyId By LifeProposalId" + e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findAllLifePolicyByGroupFarmerProposalID(String groupFarmerId) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findAllLifePolicyByGroupFarmerProposalID(groupFarmerId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifePolicyId By LifeProposalId" + e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicySearch> findLifePolicyForClaimByProduct(String productId) {
		List<LifePolicySearch> result = null;
		try {
			result = lifePolicyDAO.findLifePolicyForClaimByProduct(productId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifePolicy By productId" + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<LifePolicy> findLifePolicyPOByReceiptNo(String receiptNo, String bpmsReceiptNo, PolicyReferenceType policyReferenceType) {
		List<LifePolicy> result = null;
		try {
			result = lifePolicyDAO.findPaymentOrderByReceiptNo(receiptNo, bpmsReceiptNo, policyReferenceType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find lifePolicy payment order");
		}
		return result;
	}

}