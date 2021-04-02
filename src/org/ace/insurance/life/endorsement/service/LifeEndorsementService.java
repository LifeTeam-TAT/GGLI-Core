package org.ace.insurance.life.endorsement.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.AddOnType;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.endorsement.BeneficiaryEndorseStatus;
import org.ace.insurance.life.endorsement.InsuredPersonEndorseStatus;
import org.ace.insurance.life.endorsement.LifeBeneficiary;
import org.ace.insurance.life.endorsement.LifeEndorseBeneficiary;
import org.ace.insurance.life.endorsement.LifeEndorseChange;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.LifeEndorseInsuredPerson;
import org.ace.insurance.life.endorsement.LifeEndorseItem;
import org.ace.insurance.life.endorsement.persistence.interfaces.ILifeEndorsementDAO;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyLog.LifePolicyEndorseLog;
import org.ace.insurance.life.policyLog.LifePolicyIdLog;
import org.ace.insurance.life.policyLog.LifePolicyTimeLineLog;
import org.ace.insurance.life.policyLog.service.interfaces.ILifePolicyTimeLineLogService;
import org.ace.insurance.life.proposal.InsuredPersonAddon;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.persistence.interfaces.ILifeProposalDAO;
import org.ace.insurance.life.proposalhistory.service.interfaces.ILifeProposalHistoryService;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.exception.CustomIDGeneratorException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "LifeEndorsementService")
public class LifeEndorsementService extends BaseService implements ILifeEndorsementService {
	@Resource(name = "LifeEndorsementDAO")
	private ILifeEndorsementDAO lifeEndorseDAO;

	@Resource(name = "LifePolicySummaryService")
	private ILifePolicySummaryService lifePolicySummaryService;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "LifeProposalDAO")
	private ILifeProposalDAO lifeProposalDAO;

	@Resource(name = "LifePolicyService")
	private ILifePolicyService lifePolicyService;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "LifePolicyTimeLineLogService")
	private ILifePolicyTimeLineLogService lifePolicyTimeLineLogService;

	@Resource(name = "AcceptedInfoService")
	private IAcceptedInfoService acceptedInfoService;

	@Resource(name = "LifeProposalHistoryService")
	private ILifeProposalHistoryService lifeProposalHistoryService;

	boolean isPaidPremium = false;

	private void setIDPrefixForInsert(LifeEndorseInfo lifeEndorseInfo) {
		String endorseInfoPrefix = customIDGenerator.getPrefix(LifeEndorseInfo.class, false);
		String endorseInsuredPersonPrefix = customIDGenerator.getPrefix(LifeEndorseInsuredPerson.class, false);
		String endorseChangePrefix = customIDGenerator.getPrefix(LifeEndorseChange.class, false);
		String endorseBeneficiaryPrefix = customIDGenerator.getPrefix(LifeEndorseBeneficiary.class, false);

		lifeEndorseInfo.setPrefix(endorseInfoPrefix);
		for (LifeEndorseInsuredPerson lifeEndorseInsuredPerson : lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList()) {
			lifeEndorseInsuredPerson.setPrefix(endorseInsuredPersonPrefix);
			for (LifeEndorseChange change : lifeEndorseInsuredPerson.getLifeEndorseChangeList()) {
				change.setPrefix(endorseChangePrefix);
				for (LifeEndorseBeneficiary lifeEndorseBeneficiary : change.getLifeEndorseBeneficiaryInfoList()) {
					lifeEndorseBeneficiary.setPrefix(endorseBeneficiaryPrefix);
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo addNewLifeEndorseInfo(LifeProposal lifeProposal) {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {

			if (lifeProposal.getLifePolicy().getPolicyInsuredPersonList().size() == 1) {
				lifeEndorseInfo = checkChanges(lifeProposal);
				calculateEndorsementPremium(lifeEndorseInfo);
			} else {
				lifeEndorseInfo = checkChangesForGroup(lifeProposal);
				calculateEndorseForGroup(lifeEndorseInfo);
			}
			setIDPrefixForInsert(lifeEndorseInfo);
			lifeEndorseDAO.insert(lifeEndorseInfo);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeEndorseInfo", e);
		}
		return lifeEndorseInfo;
	}

	// for public life
	public LifeEndorseInfo checkChanges(LifeProposal newData) {

		List<LifeEndorseChange> lifeEndorseChangeList = new ArrayList<LifeEndorseChange>();
		LifeEndorseInsuredPerson lifeEndorseInsuredPerson = null;
		List<LifeBeneficiary> beneficiaryList = null;
		List<LifeBeneficiary> newBenificiariesList = new ArrayList<LifeBeneficiary>();
		List<LifeBeneficiary> oldBeneficiariesList = new ArrayList<LifeBeneficiary>();

		ProposalInsuredPerson newPerson = newData.getProposalInsuredPersonList().get(0);
		PolicyInsuredPerson oldPerson = newData.getLifePolicy().getPolicyInsuredPersonList().get(0);
		LifePolicy oldData = newData.getLifePolicy();
		Date startDate = oldData.getCommenmanceDate();
		Date endorseDate = newData.getSubmittedDate();
		int passedMonths = Utils.monthsBetween(startDate, endorseDate, false, true);

		LifeEndorseInfo lifeEndorseInfo = new LifeEndorseInfo();
		lifeEndorseInfo.setLifePolicyNo(oldData.getPolicyNo());
		lifeEndorseInfo.setSourcePolicyReferenceNo(oldData.getId());
		lifeEndorseInfo.setCommenmanceDate(oldData.getCommenmanceDate());
		lifeEndorseInfo.setActivePolicyEndDate(oldData.getActivedPolicyEndDate());
		lifeEndorseInfo.setEndorsementDate(newData.getSubmittedDate());
		lifeEndorseInfo.setProductId(oldData.getPolicyInsuredPersonList().get(0).getProduct().getId());
		lifeEndorseInfo.setPassedMonth(passedMonths);
		lifeEndorseInfo.setMonthsOfPaymentType(oldData.getPaymentType().getMonth());

		for (ProposalInsuredPerson proposalInsuredPerson : newData.getProposalInsuredPersonList()) {
			for (PolicyInsuredPerson policyInsuredPerson : oldData.getPolicyInsuredPersonList()) {
				if (proposalInsuredPerson.getInsPersonCodeNo().equals(policyInsuredPerson.getInsPersonCodeNo())) {
					lifeEndorseInsuredPerson = new LifeEndorseInsuredPerson();
					lifeEndorseInsuredPerson.setInsuredPersonCodeNo(proposalInsuredPerson.getInsPersonCodeNo());
					lifeEndorseInsuredPerson.setInsuredPersonEndorseStatus(InsuredPersonEndorseStatus.EDIT);
					lifeEndorseInsuredPerson.setPeriodOfMonths(proposalInsuredPerson.getPeriodMonth());

					for (InsuredPersonBeneficiaries newBeneficiaries : proposalInsuredPerson.getInsuredPersonBeneficiariesList()) {
						newBenificiariesList.add(new LifeBeneficiary(newBeneficiaries));
					}

					for (PolicyInsuredPersonBeneficiaries oldBeneficiaries : policyInsuredPerson.getPolicyInsuredPersonBeneficiariesList()) {
						oldBeneficiariesList.add(new LifeBeneficiary(oldBeneficiaries));
					}

					// InsuredPerson Change
					if (Utils.isNotNull(newData.getCustomer()) && Utils.isNotNull(oldData.getCustomer())) {
						if (!newData.getCustomer().getId().equals(oldData.getCustomer().getId())) {
							LifeEndorseChange endorseChange = new LifeEndorseChange(LifeEndorseItem.INSUREDPERSON_CHANGE, oldData.getCustomerId(), newData.getCustomerId());
							lifeEndorseChangeList.add(endorseChange);
						}
					}

					// SI Increase
					if (Utils.isNotNull(newData.getProposalInsuredPersonList()) && Utils.isNotNull(oldData.getPolicyInsuredPersonList())) {
						if (newPerson.getProposedSumInsured() > oldPerson.getSumInsured()) {
							LifeEndorseChange endorseChange = new LifeEndorseChange(LifeEndorseItem.INCREASE_SI, String.valueOf(oldPerson.getSumInsured()),
									String.valueOf(newPerson.getProposedSumInsured()), oldPerson.getPremium(), newPerson.getProposedPremium());
							lifeEndorseChangeList.add(endorseChange);
						}
					}

					// SI Decrease
					if (Utils.isNotNull(newData.getProposalInsuredPersonList()) && Utils.isNotNull(oldData.getPolicyInsuredPersonList())) {
						if (newPerson.getProposedSumInsured() < oldPerson.getSumInsured()) {
							isPaidPremium = newPerson.getIsPaidPremiumForPaidup();
							LifeEndorseChange endorseChange = new LifeEndorseChange(LifeEndorseItem.DECREASE_SI, String.valueOf(oldPerson.getSumInsured()),
									String.valueOf(newPerson.getProposedSumInsured()), oldPerson.getPremium(), newPerson.getProposedPremium());
							lifeEndorseChangeList.add(endorseChange);
						}
					}

					// Term Increase
					if (Utils.isNotNull(newData.getProposalInsuredPersonList()) && Utils.isNotNull(oldData.getPolicyInsuredPersonList())) {
						if (newPerson.getPeriodYears() > oldPerson.getPeriodYears()) {
							LifeEndorseChange endorseChange = new LifeEndorseChange(LifeEndorseItem.INCREASE_TERM, String.valueOf(oldPerson.getPeriodYears()),
									String.valueOf(newPerson.getPeriodYears()), oldPerson.getPremium(), newPerson.getProposedPremium());
							lifeEndorseChangeList.add(endorseChange);
						}
					}

					// Term Decrease
					if (Utils.isNotNull(newData.getProposalInsuredPersonList()) && Utils.isNotNull(oldData.getPolicyInsuredPersonList())) {
						if (newPerson.getPeriodYears() < oldPerson.getPeriodYears()) {
							LifeEndorseChange endorseChange = new LifeEndorseChange(LifeEndorseItem.DECREASE_TERM, String.valueOf(oldPerson.getPeriodYears()),
									String.valueOf(newPerson.getPeriodYears()), oldPerson.getPremium(), newPerson.getProposedPremium());
							lifeEndorseChangeList.add(endorseChange);
						}
					}

					// Terminate Customer
					if (EndorsementStatus.TERMINATE_BY_CUSTOMER.equals(newPerson.getEndorsementStatus())) {
						LifeEndorseChange endorseChange = new LifeEndorseChange(LifeEndorseItem.TERMINATE_CUSTOMER, oldData.getSumInsured(), 0);
						lifeEndorseChangeList.add(endorseChange);
					}

					// Beneficiary Change
					// Add New Beneficiary
					beneficiaryList = EndorsementUtil.getNewBeneficiaryItemList(oldBeneficiariesList, newBenificiariesList);
					if (!beneficiaryList.isEmpty()) {
						LifeEndorseChange lifeEndorseChange = new LifeEndorseChange(LifeEndorseItem.BENEFICIARY_CHANGE);
						for (LifeBeneficiary lifeBeneficiary : beneficiaryList) {
							lifeEndorseChange.addLifeEndorseBeneficiary(new LifeEndorseBeneficiary(lifeBeneficiary, BeneficiaryEndorseStatus.NEW));
						}
						lifeEndorseChangeList.add(lifeEndorseChange);
					}
					beneficiaryList = Collections.EMPTY_LIST;

					// Delete Beneficiary
					beneficiaryList = EndorsementUtil.getRemoveBeneficiaryItemList(oldBeneficiariesList, newBenificiariesList);
					if (!beneficiaryList.isEmpty()) {
						LifeEndorseChange lifeEndorseChange = new LifeEndorseChange(LifeEndorseItem.BENEFICIARY_CHANGE);
						for (LifeBeneficiary lifeBeneficiary : beneficiaryList) {
							lifeEndorseChange.addLifeEndorseBeneficiary(new LifeEndorseBeneficiary(lifeBeneficiary, BeneficiaryEndorseStatus.DELETE));
						}
						lifeEndorseChangeList.add(lifeEndorseChange);
					}

					// Replace Beneficiary
					beneficiaryList = EndorsementUtil.getReplaceBeneficiaryItemList(oldBeneficiariesList, newBenificiariesList);
					if (!beneficiaryList.isEmpty()) {
						LifeEndorseChange lifeEndorseChange = new LifeEndorseChange(LifeEndorseItem.BENEFICIARY_CHANGE);
						for (LifeBeneficiary lifeBeneficiary : beneficiaryList) {
							lifeEndorseChange.addLifeEndorseBeneficiary(new LifeEndorseBeneficiary(lifeBeneficiary, BeneficiaryEndorseStatus.REPLACE));
						}
						lifeEndorseChangeList.add(lifeEndorseChange);
					}

					if (lifeEndorseChangeList.isEmpty()) {
						lifeEndorseInsuredPerson.setLifeEndorseChangeList(Collections.EMPTY_LIST);
					} else {
						for (LifeEndorseChange change : lifeEndorseChangeList) {
							lifeEndorseInsuredPerson.addLifeEndorseChange(change);
						}
					}
					lifeEndorseInfo.addLifeEndorseInsuredPerson(lifeEndorseInsuredPerson);
				}
			}
		}
		return lifeEndorseInfo;
	}

	public double calculateInterest(double oneYearPremium, double oneMonthPremium, int passedMonth, int passedYear) {
		double interest = 0.0;
		double totalInterest = 0.0;
		// Calculate Interest for Year
		for (int i = 0; i < passedYear; i++) {
			interest = ((oneYearPremium + interest) * 6.25) / 100;
			totalInterest = totalInterest + interest;
		}
		// Calculate Interest for month
		if (passedMonth > 0) {
			// old code >> interest = ((interest + (oneMonthPremium *
			// passedMonth)) * 6.25) / 100;
			interest = ((interest + (oneMonthPremium * passedMonth)) * 6.25) / 100;
			interest = interest * passedMonth / 12;
			totalInterest = totalInterest + interest;
		}
		return totalInterest;
	}

	// For Public Life (SI Increase, SI Decrease, Term Increase, Term Decrease,
	// Terminate Customer)
	private void calculateEndorsementPremium(LifeEndorseInfo lifeEndorseInfo) {
		LifeEndorseChange lifeEndorseChange = null;
		LifePolicySummary lifePolicySummary = null;
		double endorsePremium = 0.0;
		double oldAmount = 0;
		double newAmount = 0;
		double differenceSI = 0;
		double oldPremium = 0.0;
		double newPremium = 0.0;
		double differencePremium = 0.0;
		double additionalPre = 0.0;
		int oldPeriodOfYear = 0;
		double totalInterest = 0.0;
		int covertime = 0;
		double refundAmount = 0.0;
		double paidUpValue = 0.0;
		int passedMonths = 0;
		int passedYears = 0;
		int month = 0;
		int paymentType;

		lifePolicySummary = lifePolicySummaryService.findLifePolicyByPolicyNo(lifeEndorseInfo.getSourcePolicyReferenceNo());
		if (lifePolicySummary == null) {
			lifePolicySummary = new LifePolicySummary();
			lifePolicySummary.setPolicyNo(lifeEndorseInfo.getSourcePolicyReferenceNo());
		} else {
			lifePolicySummary.setCoverTime(0);
			refundAmount = lifePolicySummary.getRefund();
			paidUpValue = lifePolicySummary.getPaidUpValue();
		}

		for (LifeEndorseInsuredPerson lifeEndorseInsuredPerson : lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList()) {
			oldPeriodOfYear = lifeEndorseInsuredPerson.getPeriodOfMonths() / 12;
			passedMonths = lifeEndorseInfo.getPassedMonth();
			passedYears = passedMonths / 12;

			// SI Increase
			if (EndorsementUtil.isLifeSIIncreOnly(lifeEndorseInsuredPerson.getLifeEndorseItemList())) {
				lifeEndorseChange = lifeEndorseInsuredPerson.getLifeEndorseChangeByItem(LifeEndorseItem.INCREASE_SI);

				oldPremium = Utils.divide(lifeEndorseChange.getOldPremium(), 12);
				newPremium = Utils.divide(lifeEndorseChange.getNewPremium(), 12);
				differencePremium = Utils.getTwoDecimalPoint(newPremium - oldPremium);
				additionalPre = Utils.getTwoDecimalPoint(differencePremium * passedMonths);

				if (newPremium > oldPremium) {
					month = passedMonths % 12;
					double oneYearPremium = differencePremium * 12;
					// Interest Calculation Method
					totalInterest = calculateInterest(oneYearPremium, differencePremium, month, passedYears);
				}

				lifePolicySummary.setInterest(totalInterest);
				lifeEndorseChange.setEndorsePremium(additionalPre);
				lifeEndorseChange.setInterest(totalInterest);
			}

			// SI Decrease
			if (EndorsementUtil.isLifeSIDecreOnly(lifeEndorseInsuredPerson.getLifeEndorseItemList())) {
				lifeEndorseChange = lifeEndorseInsuredPerson.getLifeEndorseChangeByItem(LifeEndorseItem.DECREASE_SI);
				oldPremium = Utils.divide(lifeEndorseChange.getOldPremium(), 12);
				oldAmount = Double.parseDouble(lifeEndorseChange.getOldValue());
				newAmount = Double.parseDouble(lifeEndorseChange.getNewValue());
				differenceSI = Utils.getTwoDecimalPoint(newAmount - oldAmount);

				if (isPaidPremium && passedMonths % 12 > 5) {
					int restMonth = 12 - (passedMonths % 12);
					passedYears = passedYears + 1;
					if ((oldPeriodOfYear <= 12 && passedYears >= 2) || oldPeriodOfYear > 12 && passedYears >= 3) {
						paidUpValue = paidUpValue + Utils.divide(Utils.getTwoDecimalPoint(passedYears * differenceSI), oldPeriodOfYear);
						endorsePremium = Utils.getTwoDecimalPoint(oldPremium * restMonth);
						lifePolicySummary.setPaidUpValue(paidUpValue);
						lifeEndorseChange.setEndorsePremium(endorsePremium);
						lifeEndorseChange.setPaidUpValue(paidUpValue);
					}
				} else {
					if ((oldPeriodOfYear <= 12 && passedYears >= 2) || oldPeriodOfYear > 12 && passedYears >= 3) {
						paidUpValue = paidUpValue + Utils.divide(Utils.getTwoDecimalPoint(passedYears * differenceSI), oldPeriodOfYear);
						paidUpValue = Utils.nagateIfNagative(paidUpValue);
						lifePolicySummary.setPaidUpValue(paidUpValue);
						lifeEndorseChange.setPaidUpValue(paidUpValue);
					}
				}
			}

			// Term Increase
			if (EndorsementUtil.isLifeTermIncreOnly(lifeEndorseInsuredPerson.getLifeEndorseItemList())) {
				lifeEndorseChange = lifeEndorseInsuredPerson.getLifeEndorseChangeByItem(LifeEndorseItem.INCREASE_TERM);
				oldPremium = Utils.divide(lifeEndorseChange.getOldPremium(), 12);
				newPremium = Utils.divide(lifeEndorseChange.getNewPremium(), 12);
				differencePremium = newPremium - oldPremium;
				additionalPre = Utils.getTwoDecimalPoint(differencePremium * passedMonths);
				paymentType = lifeEndorseInfo.getMonthsOfPaymentType();
				additionalPre = additionalPre + refundAmount;
				covertime = (int) Utils.divide(additionalPre, (newPremium * paymentType));
				covertime = (int) Utils.nagateIfNagative(covertime);
				refundAmount = (additionalPre % (newPremium * paymentType));
				refundAmount = Utils.nagateIfNagative(refundAmount);

				// lifeEndorseChange.setEndorsePremium(additionalPre); // old
				// code
				lifeEndorseChange.setCoverTime(covertime);
				lifeEndorseChange.setRefundAmount(refundAmount);
				lifePolicySummary.setCoverTime(covertime);
				lifePolicySummary.setRefund(refundAmount);
			}

			// Term Decreased
			if (EndorsementUtil.isLifeTermDecreOnly(lifeEndorseInsuredPerson.getLifeEndorseItemList())) {
				lifeEndorseChange = lifeEndorseInsuredPerson.getLifeEndorseChangeByItem(LifeEndorseItem.DECREASE_TERM);

				oldPremium = Utils.divide(lifeEndorseChange.getOldPremium(), 12);
				newPremium = Utils.divide(lifeEndorseChange.getNewPremium(), 12);
				differencePremium = newPremium - oldPremium;
				additionalPre = Utils.getTwoDecimalPoint(differencePremium * passedMonths);

				if (newPremium > oldPremium) {
					month = passedMonths % 12;
					double oneYearPremium = Utils.getTwoDecimalPoint(differencePremium * 12);
					// Interest Calculation Method
					totalInterest = calculateInterest(oneYearPremium, differencePremium, month, passedYears);
				}
				lifePolicySummary.setInterest(totalInterest);
				lifeEndorseChange.setEndorsePremium(additionalPre);
				lifeEndorseChange.setInterest(totalInterest);
			}

			// Terminate By Customer
			if (lifeEndorseChange != null) {
				if (lifeEndorseChange.getLifeEndorseItem().equals(LifeEndorseItem.TERMINATE_CUSTOMER)) {
					oldAmount = Double.parseDouble(lifeEndorseChange.getOldValue());
					oldPremium = Utils.divide(lifeEndorseChange.getOldPremium(), 12);

					if (passedMonths % 12 > 5) {
						int restMonth = 12 - (passedMonths % 12);
						passedYears = passedYears + 1;
						if ((oldPeriodOfYear <= 12 && passedYears >= 2) || oldPeriodOfYear > 12 && passedYears >= 3) {
							paidUpValue = paidUpValue + Utils.divide(Utils.getTwoDecimalPoint(passedYears * oldAmount), oldPeriodOfYear);
							paidUpValue = Utils.nagateIfNagative(paidUpValue);
							endorsePremium = Utils.getTwoDecimalPoint(oldPremium * restMonth);
							endorsePremium = Utils.nagate(endorsePremium);

							lifePolicySummary.setPaidUpValue(paidUpValue);
							lifeEndorseChange.setPaidUpValue(paidUpValue);
							lifeEndorseChange.setEndorsePremium(endorsePremium);
						}
					} else {
						if ((oldPeriodOfYear <= 12 && passedYears >= 2) || oldPeriodOfYear > 12 && passedYears >= 3) {
							paidUpValue = paidUpValue + Utils.divide(Utils.getTwoDecimalPoint(passedYears * oldAmount), oldPeriodOfYear);
							paidUpValue = Utils.nagateIfNagative(paidUpValue);

							lifeEndorseChange.setPaidUpValue(paidUpValue);
							lifePolicySummary.setPaidUpValue(paidUpValue);
						}
					}
				}
			}
		}
		lifePolicySummaryService.updateLifePolicySummary(lifePolicySummary);
	}

	// For Group Life
	private void calculateEndorseForGroup(LifeEndorseInfo lifeEndorseInfo) {
		int passedMonths = lifeEndorseInfo.getPassedMonth();
		double oldPremium = 0.0;
		double newPremium = 0.0;
		double differencePremium = 0.0;
		double additionalPre = 0.0;

		LifeEndorseChange lifeEndorseChange = null;
		for (LifeEndorseInsuredPerson lifeEndorseInsuredPerson : lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList()) {
			lifeEndorseChange = new LifeEndorseChange();
			if ((lifeEndorseInsuredPerson.getInsuredPersonEndorseStatus().equals(InsuredPersonEndorseStatus.REPLACE))
					|| lifeEndorseInsuredPerson.getInsuredPersonEndorseStatus().equals(InsuredPersonEndorseStatus.EDIT)) {
				if (EndorsementUtil.isLifeSIIncreOnly(lifeEndorseInsuredPerson.getLifeEndorseItemList())) {

					lifeEndorseChange = lifeEndorseInsuredPerson.getLifeEndorseChangeByItem(LifeEndorseItem.INCREASE_SI);
					passedMonths = lifeEndorseInfo.getPassedMonth();
					Utils.divide(lifeEndorseChange.getOldPremium(), 12);
					oldPremium = Utils.divide(lifeEndorseChange.getOldPremium(), 12);
					newPremium = Utils.divide(lifeEndorseChange.getNewPremium(), 12);
					differencePremium = newPremium - oldPremium;
					additionalPre = Utils.getTwoDecimalPoint(differencePremium * passedMonths);
					if (newPremium > oldPremium) {
						lifeEndorseChange.setEndorsePremium(additionalPre);
					}
				}
			} else if (lifeEndorseInsuredPerson.getInsuredPersonEndorseStatus().equals(InsuredPersonEndorseStatus.NEW)) {
				lifeEndorseChange = lifeEndorseInsuredPerson.getLifeEndorseChangeList().get(0);
				newPremium = lifeEndorseChange.getNewPremium();
				lifeEndorseChange.setEndorsePremium(newPremium);
			}
		}
	}

	// For Group Checkers
	public LifeEndorseInfo checkChangesForGroup(LifeProposal lifeProposal) {
		List<LifeEndorseChange> lifeEndorseChangeList = new ArrayList<LifeEndorseChange>();
		List<LifeEndorseInsuredPerson> lifeEndorseInsuredPersonList = new ArrayList<LifeEndorseInsuredPerson>();
		LifeEndorseInsuredPerson lifeEndorseInsuredPerson = null;
		List<LifeBeneficiary> beneficiaryList = null;

		List<LifeBeneficiary> newBenificiariesList = new ArrayList<LifeBeneficiary>();
		List<LifeBeneficiary> oldBeneficiariesList = new ArrayList<LifeBeneficiary>();

		LifeEndorseInfo lifeEndorseInfo = null;
		List<ProposalInsuredPerson> newProposalInsuredPersonList = null;

		LifePolicy lp = lifeProposal.getLifePolicy();
		Date startDate = lp.getCommenmanceDate();
		Date endDate = lp.getActivedPolicyEndDate();
		int passedMonths = Utils.monthsBetween(startDate, endDate, false, true);
		lifeEndorseInfo = new LifeEndorseInfo();
		lifeEndorseInfo.setLifePolicyNo(lp.getPolicyNo());
		lifeEndorseInfo.setSourcePolicyReferenceNo(lp.getId());
		lifeEndorseInfo.setCommenmanceDate(lp.getCommenmanceDate());
		lifeEndorseInfo.setActivePolicyEndDate(lp.getActivedPolicyEndDate());
		lifeEndorseInfo.setEndorsementDate(lifeProposal.getSubmittedDate());
		lifeEndorseInfo.setProductId(lp.getPolicyInsuredPersonList().get(0).getProduct().getId());
		lifeEndorseInfo.setPassedMonth(passedMonths);
		lifeEndorseInfo.setMonthsOfPaymentType(lp.getPaymentType().getMonth());

		// For Organization Change
		if (Utils.isNotNull(lifeProposal.getOrganization()) && Utils.isNotNull(lp.getOrganization())) {
			if (!lifeProposal.getOrganization().getId().equals(lp.getOrganization().getId())) {
				LifeEndorseChange endorseChange = new LifeEndorseChange(LifeEndorseItem.ORGANIZATION_CHANGE, lp.getOrganization().getId(), lifeProposal.getOrganization().getId());
				lifeEndorseChangeList.add(endorseChange);
			}
		}

		// For New InsuredPerson
		newProposalInsuredPersonList = EndorsementUtil.getNewProposalInsuredPersonItemList(lifeProposal.getLifePolicy().getPolicyInsuredPersonList(),
				lifeProposal.getProposalInsuredPersonList());
		if (newProposalInsuredPersonList != null) {
			for (ProposalInsuredPerson proposalInsuredPerson : newProposalInsuredPersonList) {
				lifeEndorseInsuredPerson = new LifeEndorseInsuredPerson();
				lifeEndorseInsuredPerson.setInsuredPersonCodeNo(proposalInsuredPerson.getInsPersonCodeNo());
				lifeEndorseInsuredPerson.setInsuredPersonEndorseStatus(InsuredPersonEndorseStatus.NEW);
				lifeEndorseInsuredPerson.setPeriodOfMonths(proposalInsuredPerson.getPeriodMonth());
				lifeEndorseInsuredPersonList.add(lifeEndorseInsuredPerson);
				LifeEndorseChange endorseChange = new LifeEndorseChange(LifeEndorseItem.INSUREDPERSON_CHANGE, String.valueOf(0),
						String.valueOf(proposalInsuredPerson.getProposedSumInsured()), 0, proposalInsuredPerson.getProposedPremium());
				lifeEndorseChangeList.add(endorseChange);

				for (InsuredPersonBeneficiaries newBeneficiaries : proposalInsuredPerson.getInsuredPersonBeneficiariesList()) {
					newBenificiariesList.add(new LifeBeneficiary(newBeneficiaries));
				}

				if (!newBenificiariesList.isEmpty()) {
					LifeEndorseChange lifeEndorseChange = new LifeEndorseChange(LifeEndorseItem.BENEFICIARY_CHANGE);
					for (LifeBeneficiary lifeBeneficiary : newBenificiariesList) {
						lifeEndorseChange.addLifeEndorseBeneficiary(new LifeEndorseBeneficiary(lifeBeneficiary.getBeneficiaryNo(), lifeBeneficiary.getPercentage(),
								BeneficiaryEndorseStatus.NEW, lifeBeneficiary.getInsuredPersonCodeNo(), lifeBeneficiary.getInitialId(), lifeBeneficiary.getIdNo(),
								lifeBeneficiary.getAge(), lifeBeneficiary.getName(), lifeBeneficiary.getIdType(), lifeBeneficiary.getResidentAddress(),
								lifeBeneficiary.getRelationship(), lifeBeneficiary.getGender()));

					}
					lifeEndorseChangeList.add(lifeEndorseChange);
				}

				for (LifeEndorseChange lifeEndorseChange : lifeEndorseChangeList) {
					lifeEndorseInsuredPerson.addLifeEndorseChange(lifeEndorseChange);
				}
				lifeEndorseInfo.addLifeEndorseInsuredPerson(lifeEndorseInsuredPerson);
				newBenificiariesList.clear();
				lifeEndorseChangeList.clear();
			}
		}

		// For Replace InsuredPerson
		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			for (PolicyInsuredPerson policyInsuredPerson : lifeProposal.getLifePolicy().getPolicyInsuredPersonList()) {
				if (proposalInsuredPerson.getInsPersonCodeNo().equals(policyInsuredPerson.getInsPersonCodeNo())) {
					if (EndorsementUtil.isInsuredPersonChanges(proposalInsuredPerson, policyInsuredPerson)) {
						lifeEndorseInsuredPerson = new LifeEndorseInsuredPerson();
						lifeEndorseInsuredPerson.setInsuredPersonCodeNo(proposalInsuredPerson.getInsPersonCodeNo());
						lifeEndorseInsuredPerson.setInsuredPersonEndorseStatus(InsuredPersonEndorseStatus.REPLACE);
						lifeEndorseInsuredPerson.setPeriodOfMonths(proposalInsuredPerson.getPeriodMonth());

						lifeEndorseInsuredPersonList.add(lifeEndorseInsuredPerson);
						for (InsuredPersonBeneficiaries newBeneficiaries : proposalInsuredPerson.getInsuredPersonBeneficiariesList()) {
							newBenificiariesList.add(new LifeBeneficiary(newBeneficiaries));
						}

						for (PolicyInsuredPersonBeneficiaries oldBeneficiaries : policyInsuredPerson.getPolicyInsuredPersonBeneficiariesList()) {
							oldBeneficiariesList.add(new LifeBeneficiary(oldBeneficiaries));
						}

						// SI Increase
						if (Utils.isNotNull(lifeProposal.getProposalInsuredPersonList()) && Utils.isNotNull(lp.getPolicyInsuredPersonList())) {
							if (proposalInsuredPerson.getProposedSumInsured() > policyInsuredPerson.getSumInsured()) {
								LifeEndorseChange change = new LifeEndorseChange(LifeEndorseItem.INCREASE_SI, String.valueOf(policyInsuredPerson.getSumInsured()),
										String.valueOf(proposalInsuredPerson.getProposedSumInsured()), policyInsuredPerson.getPremium(),
										proposalInsuredPerson.getProposedPremium());
								lifeEndorseChangeList.add(change);
							}
						}

						// Beneficiary Change
						// Add New Beneficiary
						beneficiaryList = EndorsementUtil.getNewBeneficiaryItemList(oldBeneficiariesList, newBenificiariesList);
						if (!beneficiaryList.isEmpty()) {
							LifeEndorseChange lifeEndorseChange = new LifeEndorseChange(LifeEndorseItem.BENEFICIARY_CHANGE);
							for (LifeBeneficiary lifeBeneficiary : beneficiaryList) {
								lifeEndorseChange.addLifeEndorseBeneficiary(new LifeEndorseBeneficiary(lifeBeneficiary, BeneficiaryEndorseStatus.NEW));

							}
							lifeEndorseChangeList.add(lifeEndorseChange);
						}

						// Delete Beneficiary
						beneficiaryList = EndorsementUtil.getRemoveBeneficiaryItemList(oldBeneficiariesList, newBenificiariesList);
						if (!beneficiaryList.isEmpty()) {
							LifeEndorseChange lifeEndorseChange = new LifeEndorseChange(LifeEndorseItem.BENEFICIARY_CHANGE);
							for (LifeBeneficiary lifeBeneficiary : beneficiaryList) {
								lifeEndorseChange.addLifeEndorseBeneficiary(new LifeEndorseBeneficiary(lifeBeneficiary, BeneficiaryEndorseStatus.DELETE));
							}
							lifeEndorseChangeList.add(lifeEndorseChange);
						}

						// Replace Beneficiary
						beneficiaryList = EndorsementUtil.getReplaceBeneficiaryItemList(oldBeneficiariesList, newBenificiariesList);
						if (!beneficiaryList.isEmpty()) {
							LifeEndorseChange lifeEndorseChange = new LifeEndorseChange(LifeEndorseItem.BENEFICIARY_CHANGE);
							for (LifeBeneficiary lifeBeneficiary : beneficiaryList) {
								lifeEndorseChange.addLifeEndorseBeneficiary(new LifeEndorseBeneficiary(lifeBeneficiary, BeneficiaryEndorseStatus.REPLACE));
							}
							lifeEndorseChangeList.add(lifeEndorseChange);
						}

						if (lifeEndorseChangeList.isEmpty()) {
							lifeEndorseInsuredPerson.setLifeEndorseChangeList(Collections.EMPTY_LIST);
						} else {
							for (LifeEndorseChange change : lifeEndorseChangeList) {
								lifeEndorseInsuredPerson.addLifeEndorseChange(change);
							}
						}
						lifeEndorseInfo.addLifeEndorseInsuredPerson(lifeEndorseInsuredPerson);
						beneficiaryList.clear();
						oldBeneficiariesList.clear();
						newBenificiariesList.clear();
						lifeEndorseChangeList.clear();
					}
				}
			}
		}
		return lifeEndorseInfo;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo updateLifeEndorseInfo(LifeProposal lifeProposal) {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {
			LifeEndorseInfo oldLifeEndorseInfo = lifeEndorseDAO.findBySourcePolicyReferenceNo(lifeProposal.getLifePolicy().getId());
			if (lifeProposal.getLifePolicy().getPolicyInsuredPersonList().size() == 1) {
				lifeEndorseInfo = checkChanges(lifeProposal);
				calculateEndorsementPremium(lifeEndorseInfo);
			} else {
				lifeEndorseInfo = checkChangesForGroup(lifeProposal);
				calculateEndorseForGroup(lifeEndorseInfo);
			}

			setIDPrefixForInsert(lifeEndorseInfo);
			lifeEndorseDAO.insert(lifeEndorseInfo);
			lifeEndorseDAO.deleteLifeEndorseInfo(oldLifeEndorseInfo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update LifeEndorseInfo", e);
		}
		return lifeEndorseInfo;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo findLastLifeEndorseInfoByProposalNo(String policyNo) {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {
			lifeEndorseInfo = lifeEndorseDAO.findLastLifeEndorseInfoByProposalNo(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find last LifeEndorseInfo", e);
		}
		return lifeEndorseInfo;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo findBySourcePolicyReferenceNo(String policyId) {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {
			lifeEndorseInfo = lifeEndorseDAO.findBySourcePolicyReferenceNo(policyId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifeEndorseInfo By SourcePolicyReferenceNo", e);
		}
		return lifeEndorseInfo;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo findByEndorsePolicyReferenceNo(String policyId) {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {
			lifeEndorseInfo = lifeEndorseDAO.findByEndorsePolicyReferenceNo(policyId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifeEndorseInfo By EndorsePolicyReferenceNo", e);
		}
		return lifeEndorseInfo;
	}

	public void calculatePremium(LifeProposal lifeProposal) {
		int paymentType = lifeProposal.getPaymentType().getMonth();
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			if (pv.getProduct().getAutoCalculate()) {
				Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
				for (InsuredPersonKeyFactorValue vhKf : pv.getKeyFactorValueList()) {
					/* Reset Sum Insured */
					KeyFactor keyfactor = vhKf.getKeyFactor();
					if (KeyFactorChecker.isSumInsured(keyfactor)) {
						double sumInsured = Double.parseDouble(vhKf.getValue());
						pv.setProposedSumInsured(sumInsured);
					}
					if (KeyFactorChecker.isAge(keyfactor)) {
						int age = Integer.parseInt(vhKf.getValue());
						age = Utils.getAgeForNextYear(pv.getDateOfBirth());
						vhKf.setValue(age + "");
						pv.setAge(age);
					}
					keyfatorValueMap.put(vhKf.getKeyFactor(), vhKf.getValue());
				}
				Double premium = productService.findProductPremiumRate(keyfatorValueMap, pv.getProduct(), pv.getSumInsuredType());
				double termPremium = 0.0;
				if (premium != null && premium > 0) {
					if (paymentType > 0) {
						int paymentTerm = pv.getPeriodMonth() / paymentType;
						termPremium = (paymentType * premium) / 12;
						pv.setPaymentTerm(paymentTerm);
						pv.setBasicTermPremium(termPremium);
					} else {
						// *** Calculation for Lump Sum ***
						pv.setPaymentTerm(0);
						termPremium = (pv.getPeriodMonth() * premium) / 12;
						pv.setBasicTermPremium(termPremium);
					}
					pv.setProposedPremium(premium);
				} else {
					throw new SystemException(ErrorCode.NO_PREMIUM_RATE, keyfatorValueMap, "There is no premiumn.");
				}
			}
			List<InsuredPersonAddon> insuredPersonAddOnList = pv.getInsuredPersonAddOnList();
			if (insuredPersonAddOnList != null && !insuredPersonAddOnList.isEmpty()) {
				for (InsuredPersonAddon insuredPersonAddOn : insuredPersonAddOnList) {
					double addOnPremium = 0.0;
					AddOn addOn = insuredPersonAddOn.getAddOn();
					AddOnType type = addOn.getAddOnType();
					if (type.equals(AddOnType.FIXED)) {
						addOnPremium = addOn.getValue();
					} else if (type.equals(AddOnType.BASED_ON_PREMIUN)) {
						addOnPremium = (pv.getProposedPremium() / 100) * addOn.getValue();
					} else if (type.equals(AddOnType.BASED_ON_SUMINSU)) {
						addOnPremium = (pv.getProposedSumInsured() / 100) * addOn.getValue();
					} else if (type.equals(AddOnType.BASED_ON_ADDON_SUMINSU)) {
						addOnPremium = (insuredPersonAddOn.getProposedSumInsured() / 100) * addOn.getValue();
					} else if (type.equals(AddOnType.BASED_ON_BASE_PREMIUM)) {
						Map<KeyFactor, String> thirdPartyKeyfatorValueMap = new HashMap<KeyFactor, String>();
						for (InsuredPersonKeyFactorValue vhKf : pv.getKeyFactorValueList()) {
							KeyFactor keyfactor = vhKf.getKeyFactor();
							thirdPartyKeyfatorValueMap.put(vhKf.getKeyFactor(), vhKf.getValue());
						}
						Double premium = productService.findThirdPartyPremiumRate(thirdPartyKeyfatorValueMap, pv.getProduct());
						if (premium != null && premium != 0) {
							addOnPremium = (premium / 100) * addOn.getValue();
						} else {
							throw new SystemException(ErrorCode.NO_PREMIUM_RATE, thirdPartyKeyfatorValueMap, "There is no premiumn.");
						}
					}
					insuredPersonAddOn.setProposedPremium(addOnPremium);
				}
			}
			double addOnPremium = lifeProposal.getAddOnPremium();
			double termPremium = 0.0;
			if (paymentType > 0) {
				termPremium = (paymentType * addOnPremium) / 12;
				pv.setAddOnTermPremium(termPremium);
			} else {
				// *** Calculation for Lump Sum AddOn Premium***
				termPremium = (pv.getPeriodMonth() * addOnPremium) / 12;
				pv.setAddOnTermPremium(termPremium);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal addNewShortEndowLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, String name) {
		try {
			calculatePremium(lifeProposal);
			String productCode = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getProductGroup().getProposalNoPrefix();
			String proposalNo = null;
			String insPersonCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();

			lifeProposal.setProposalType(ProposalType.ENDORSEMENT);
			if (ProductIDConfig.isShortEndowmentLife(product)) {
				proposalNo = customIDGenerator.getNextId(SystemConstants.SHORT_TERM_ENDOWMENT_ENDORSE_PROPOSAL_NO, productCode, true);
			}

			lifePolicyService.updateEndorsementStatus(true, lifeProposal.getLifePolicy());
			// Custom ID GEN For Proposal Insured Person and Beneficiary
			for (ProposalInsuredPerson person : lifeProposal.getProposalInsuredPersonList()) {
				if (person.getInsPersonCodeNo() == null) {
					insPersonCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_CODENO, null, true);
					person.setInsPersonCodeNo(insPersonCodeNo);
				}
				for (InsuredPersonBeneficiaries beneficiary : person.getInsuredPersonBeneficiariesList()) {
					if (beneficiary.getBeneficiaryNo() == null) {
						beneficiaryNo = customIDGenerator.getNextId(SystemConstants.LIFE_BENEFICIARY_NO, null, true);
						beneficiary.setBeneficiaryNo(beneficiaryNo);
					}
				}
			}
			lifeProposal.setProposalNo(proposalNo);
			setIDPrefixForInsert(lifeProposal);

			LifeProposal mp = lifeEndorseDAO.insert(lifeProposal);
			workFlowDTO.setReferenceNo(mp.getId());
			workFlowDTOService.addNewWorkFlow(workFlowDTO);
			lifeProposalDAO.updateStatus(name, lifeProposal.getPortalId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeProposal", e);
		} catch (CustomIDGeneratorException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeProposal", e);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal addNewLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, String status) {
		try {
			LifeEndorseInfo info = null;
			calculatePremium(lifeProposal);
			String productCode = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getProductGroup().getProposalNoPrefix();
			String proposalNo = null;
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();

			lifeProposal.setProposalType(ProposalType.ENDORSEMENT);
			if (ProductIDConfig.isPublicLife(product)) {
				proposalNo = customIDGenerator.getNextId(SystemConstants.ENDOWMENT_LIFE_ENDORSE_PROPOSAL_NO, productCode, true);
			} else if (ProductIDConfig.isGroupLife(product)) {
				proposalNo = customIDGenerator.getNextId(SystemConstants.GROUP_LIFE_ENDORSE_PROPOSAL_NO, productCode, true);
				// } else if (isSportMan(product)) {
				// proposalNo =
				// customIDGenerator.getNextId(SystemConstants.SPORTSMAN_LIFE_PROPOSAL_NO,
				// productCode, true);
			} else if (ProductIDConfig.isShortEndowmentLife(product)) {
				proposalNo = customIDGenerator.getNextId(SystemConstants.SHORT_TERM_ENDOWMENT_ENDORSE_PROPOSAL_NO, productCode, true);
			}

			lifePolicyService.updateEndorsementStatus(true, lifeProposal.getLifePolicy());
			// Custom ID GEN For Proposal Insured Person and Beneficiary
			for (ProposalInsuredPerson person : lifeProposal.getProposalInsuredPersonList()) {
				if (person.getInsPersonCodeNo() == null) {
					insPersonCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_CODENO, null, false);
					person.setInsPersonCodeNo(insPersonCodeNo);
				}
				if (ProductIDConfig.isGroupLife(product)) {
					inPersonGroupCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_GROUP_CODENO, null, false);
					person.setInPersonGroupCodeNo(inPersonGroupCodeNo);
				}
				for (InsuredPersonBeneficiaries beneficiary : person.getInsuredPersonBeneficiariesList()) {
					if (beneficiary.getBeneficiaryNo() == null) {
						beneficiaryNo = customIDGenerator.getNextId(SystemConstants.LIFE_BENEFICIARY_NO, null, false);
						beneficiary.setBeneficiaryNo(beneficiaryNo);
					}
				}
			}
			lifeProposal.setProposalNo(proposalNo);
			setIDPrefixForInsert(lifeProposal);

			if (lifeProposal.getLifePolicy() != null) {
				info = addNewLifeEndorseInfo(lifeProposal);
				setEndorsementPremium(info, lifeProposal);
			}

			LifeProposal mp = lifeEndorseDAO.insert(lifeProposal);
			workFlowDTO.setReferenceNo(mp.getId());
			workFlowDTO.setReferenceType(WorkflowReferenceType.LIFE_PROPOSAL);
			workFlowDTOService.addNewWorkFlow(workFlowDTO);
			lifeProposalDAO.updateStatus(status, lifeProposal.getPortalId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeProposal", e);
		} catch (CustomIDGeneratorException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeProposal", e);
		}
		return lifeProposal;
	}

	private void setIDPrefixForInsert(LifeProposal lifeProposal) {
		String mProposalPrefix = customIDGenerator.getPrefix(LifeProposal.class, true);
		String insuredPersonPrefix = customIDGenerator.getPrefix(ProposalInsuredPerson.class, false);
		String insuredPersonBeneficiaryPrefix = customIDGenerator.getPrefix(InsuredPersonBeneficiaries.class, false);
		String isPesAddOnPrefix = customIDGenerator.getPrefix(InsuredPersonKeyFactorValue.class, false);
		String insuredPersonAddOnPrefix = customIDGenerator.getPrefix(InsuredPersonAddon.class, false);
		String insuredPersonAttachPrefix = customIDGenerator.getPrefix(InsuredPersonAttachment.class, false);
		String proposalAttachPrefix = customIDGenerator.getPrefix(LifeProposalAttachment.class, false);

		lifeProposal.setPrefix(mProposalPrefix);
		for (LifeProposalAttachment attach : lifeProposal.getAttachmentList()) {
			attach.setPrefix(proposalAttachPrefix);
		}
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			pv.setPrefix(insuredPersonPrefix);
			for (InsuredPersonKeyFactorValue vhKf : pv.getKeyFactorValueList()) {
				vhKf.setPrefix(isPesAddOnPrefix);
			}
			for (InsuredPersonAttachment vehAttach : pv.getAttachmentList()) {
				vehAttach.setPrefix(insuredPersonAttachPrefix);
			}
			List<InsuredPersonAddon> insuredPersonAddOnList = pv.getInsuredPersonAddOnList();
			if (insuredPersonAddOnList != null && !insuredPersonAddOnList.isEmpty()) {
				for (InsuredPersonAddon insuredPersonAddOn : insuredPersonAddOnList) {
					insuredPersonAddOn.setPrefix(insuredPersonAddOnPrefix);
				}
			}
			List<InsuredPersonBeneficiaries> insuredPersonBeneficiaryList = pv.getInsuredPersonBeneficiariesList();
			if (insuredPersonBeneficiaryList != null && !insuredPersonBeneficiaryList.isEmpty()) {
				for (InsuredPersonBeneficiaries insuredpersonbeneficiary : insuredPersonBeneficiaryList) {
					insuredpersonbeneficiary.setPrefix(insuredPersonBeneficiaryPrefix);
				}
			}
		}
	}

	private void setEndorsementPremium(LifeEndorseInfo lifeEndorseInfo, LifeProposal lifeProposal) {
		initializeEndorsementPremium(lifeProposal);
		for (LifeEndorseInsuredPerson endorsePerson : lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList()) {
			for (ProposalInsuredPerson proposalPerson : lifeProposal.getProposalInsuredPersonList()) {
				if (endorsePerson.getInsuredPersonCodeNo().equals(proposalPerson.getInsPersonCodeNo()) && proposalPerson.getEndorsementStatus() != null) {
					proposalPerson.setEndorsementNetBasicPremium(endorsePerson.getEndorsePremium() + endorsePerson.getEndorseInterest());
					proposalPerson.setInterest(endorsePerson.getEndorseInterest());
				}
			}
		}
	}

	private void initializeEndorsementPremium(LifeProposal lifeProposal) {
		for (ProposalInsuredPerson proposalPerson : lifeProposal.getProposalInsuredPersonList()) {
			proposalPerson.setEndorsementNetBasicPremium(0);
			proposalPerson.setInterest(0);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void approveLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			lifeEndorseDAO.updateInsuredPersonApprovalInfo(lifeProposal.getProposalInsuredPersonList());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to approve a LifeProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reject a LifeProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status) {

		List<Payment> paymentList = new ArrayList<Payment>();
		boolean isEndorse = lifeProposal.getProposalType() == ProposalType.ENDORSEMENT;
		boolean isRenewal = lifeProposal.getProposalType() == ProposalType.RENEWAL;
		try {

			if (null != lifeProposal.getId()) {
				List<LifePolicy> policyList = lifePolicyService.findPolicyByProposalId(lifeProposal.getId());
				if (null != policyList && policyList.size() > 0) {
					throw new SystemException(ErrorCode.PROPOSAL_ALREADY_CONFIRMED, " Proposal is already confirmed.");
				}
			}

			workFlowDTOService.updateWorkFlow(workFlowDTO);
			// reset insured person start/end date
			if (!isEndorse) {
				for (ProposalInsuredPerson proposalInsPerson : lifeProposal.getProposalInsuredPersonList()) {
					if (isRenewal) {
						if (proposalInsPerson.getEndDate().compareTo(new Date()) > 0) {
							proposalInsPerson.setStartDate(proposalInsPerson.getEndDate());
						} else {
							proposalInsPerson.setStartDate(new Date());
						}
						Calendar cal = Calendar.getInstance();
						cal.setTime(proposalInsPerson.getStartDate());
						cal.add(Calendar.MONTH, proposalInsPerson.getPeriodMonth());
						proposalInsPerson.setEndDate(cal.getTime());
					} else {
						proposalInsPerson.setStartDate(new Date());
						Calendar cal = Calendar.getInstance();
						cal.setTime(proposalInsPerson.getStartDate());
						cal.add(Calendar.MONTH, proposalInsPerson.getPeriodMonth());
						proposalInsPerson.setEndDate(cal.getTime());
					}
				}
				if (recalculatePremium(CONFIRMATION)) {
					calculatePremium(lifeProposal);
				}
			}

			// create LifePolicy
			LifePolicy lifePolicy = new LifePolicy(lifeProposal);

			// Set Id and Version for endorsement process
			lifePolicy.setId(lifeProposal.getLifePolicy().getId());
			lifePolicy.setVersion(lifeProposal.getLifePolicy().getVersion());
			lifePolicy.setPolicyNo(lifeProposal.getLifePolicy().getPolicyNo());
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			String endorsementNo = null;
			if (ProductIDConfig.isPublicLife(product)) {
				endorsementNo = customIDGenerator.getNextId(SystemConstants.ENDOWMENT_LIFE_ENDORSE_POLICY_NO, null, true);
			} else if (ProductIDConfig.isGroupLife(product)) {
				endorsementNo = customIDGenerator.getNextId(SystemConstants.GROUP_LIFE_ENDORSE_POLICY_NO, null, true);
			} else if (ProductIDConfig.isShortEndowmentLife(product)) {
				endorsementNo = customIDGenerator.getNextId(SystemConstants.SHORT_TERM_ENDOWMENT_ENDORSE_POLICY_NO, null, true);
			}
			lifePolicy.setEndorsementNo(endorsementNo);
			lifePolicy.setEndorsementApplied(lifeProposal.getLifePolicy().isEndorsementApplied());
			lifePolicy.setCoinsuranceApplied(lifeProposal.getLifePolicy().isCoinsuranceApplied());
			lifePolicy.setCommenmanceDate(lifeProposal.getLifePolicy().getCommenmanceDate());
			lifePolicy.setActivedPolicyStartDate(lifeProposal.getLifePolicy().getActivedPolicyStartDate());
			lifePolicy.setActivedPolicyEndDate(lifeProposal.getLifePolicy().getActivedPolicyEndDate());
			lifePolicy.setLastPaymentTerm(lifeProposal.getLifePolicy().getLastPaymentTerm());
			for (PolicyInsuredPerson newPolicyPerson : lifePolicy.getPolicyInsuredPersonList()) {
				for (PolicyInsuredPerson oldPolicyPerson : lifeProposal.getLifePolicy().getPolicyInsuredPersonList()) {
					if (oldPolicyPerson.getInsPersonCodeNo().equals(newPolicyPerson.getInsPersonCodeNo())) {
						newPolicyPerson.setId(oldPolicyPerson.getId());
						newPolicyPerson.setVersion(oldPolicyPerson.getVersion());
					}
				}

			}

			// recalculate payment term and term premium
			for (PolicyInsuredPerson person : lifePolicy.getPolicyInsuredPersonList()) {
				calculateTermPremium(person, lifePolicy.getPaymentType().getMonth());
			}

			// create Payment
			Payment payment = new Payment();
			payment.setPaymentType(lifeProposal.getPaymentType());
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.LIFE_POLICY);
			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());
			payment.setDiscountPercent(paymentDTO.getDiscountPercent());
			payment.setReceivedDeno(paymentDTO.getReceivedDeno());
			payment.setRefundDeno(paymentDTO.getRefundDeno());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			if (isEndorse) {
				payment.setBasicPremium(lifePolicy.getEndorsementBasicPremium());
				payment.setAddOnPremium(lifePolicy.getEndorsementAddOnPremium());
			} else {
				payment.setBasicPremium(lifePolicy.getTotalBasicTermPremium());
				payment.setAddOnPremium(lifePolicy.getTotalAddOnTermPremium());
			}

			// calculate payment term and term premium for discount
			if (paymentDTO.getDiscountPercent() > 0.0) {
				for (PolicyInsuredPerson person : lifePolicy.getPolicyInsuredPersonList()) {
					calculateDiscount(person, paymentDTO.getDiscountPercent(), isEndorse);
				}
			}

			if (EndorsementUtil.isTerminatePolicy(lifeProposal)) {
				lifePolicyService.terminateLifePolicy(lifePolicy);
			} else {
				lifePolicyService.updateLifePolicy(lifePolicy);
			}
			String sourcePolicyId = lifeProposal.getLifePolicy().getId();
			// update lifeendorseinfo for changed policy id
			lifeEndorseDAO.updateEndorsePolicyReferenceNo(sourcePolicyId, lifePolicy.getId());
			// reset new motorpolicy id for endorsement case
			lifeProposal.setLifePolicy(lifePolicy);
			// For Public Life
			if (lifeProposal.getProposalInsuredPersonList().size() == 1) {
				ProposalInsuredPerson newPv = lifeProposal.getProposalInsuredPersonList().get(0);
				LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getId());
				if (summary != null) {
					if (summary.getCoverTime() > 0) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(lifeProposal.getLifePolicy().getActivedPolicyEndDate());
						for (int i = 0; i < summary.getCoverTime(); i++) {
							cal.add(Calendar.MONTH, lifeProposal.getPaymentType().getMonth());
							cal.setTime(cal.getTime());
						}
						lifePolicy.setActivedPolicyEndDate(cal.getTime());
					}
				}
			}

			payment.setReferenceNo(lifePolicy.getId());
			paymentList.add(payment);
			lifeEndorseDAO.update(lifeProposal);
			paymentList = paymentService.prePayment(paymentList);
			// prepayment TLF and change status of TLF payment.
			prePaymentLifeProposal(lifeProposal, paymentList, branch, status);

			if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				prePaymentLifeProposalTransfer(lifeProposal, paymentList, branch);
			}

			/* Entry to LifePolicyEndorseLog (Endorsement) */
			Date endorsementDate = lifeProposal.getSubmittedDate();
			Date endorsementConfirmDate = lifePolicy.getCommenmanceDate();
			LifeEndorseInfo endorseInfo = findByEndorsePolicyReferenceNo(lifePolicy.getId());
			LifePolicyTimeLineLog timeLineLog = lifePolicyTimeLineLogService.findLifePolicyTimeLineLogByPolicyNo(lifePolicy.getPolicyNo());
			LifePolicyEndorseLog endorseLog = new LifePolicyEndorseLog(endorsementDate, endorsementConfirmDate, endorseInfo, timeLineLog);
			lifePolicyTimeLineLogService.addLifePolicyEndorseLog(endorseLog);

			LifePolicyIdLog idLog = new LifePolicyIdLog(sourcePolicyId, lifePolicy.getId(), lifePolicy.getLifeProposal().getId(), ProposalType.ENDORSEMENT, timeLineLog);
			lifePolicyTimeLineLogService.addLifePolicyIdLog(idLog);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm a LifeProposal", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmShortTermEndowLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, Branch branch) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			if (recalculatePremium(CONFIRMATION)) {
				calculatePremium(lifeProposal);
			}
			// create LifePolicy
			LifePolicy lifePolicy = new LifePolicy(lifeProposal);

			// Set Id and Version for endorsement process
			lifePolicy.setId(lifeProposal.getLifePolicy().getId());
			lifePolicy.setVersion(lifeProposal.getLifePolicy().getVersion());
			lifePolicy.setPolicyNo(lifeProposal.getLifePolicy().getPolicyNo());
			lifePolicy.setEndorsementApplied(lifeProposal.getLifePolicy().isEndorsementApplied());
			lifePolicy.setCoinsuranceApplied(lifeProposal.getLifePolicy().isCoinsuranceApplied());
			lifePolicy.setCommenmanceDate(lifeProposal.getLifePolicy().getCommenmanceDate());
			lifePolicy.setActivedPolicyStartDate(lifeProposal.getLifePolicy().getActivedPolicyStartDate());
			lifePolicy.setActivedPolicyEndDate(lifeProposal.getLifePolicy().getActivedPolicyEndDate());
			lifePolicy.setLastPaymentTerm(lifeProposal.getLifePolicy().getLastPaymentTerm());
			/*
			 * if (lifeProposal.getPaymentType().getMonth() > 0) { int
			 * paidMonths =
			 * DateUtils.monthsBetween(lifePolicy.getActivedPolicyStartDate(),
			 * lifePolicy.getActivedPolicyEndDate(), false, false);
			 * lifePolicy.setLastPaymentTerm(paidMonths /
			 * lifeProposal.getPaymentType().getMonth()); }
			 */
			for (PolicyInsuredPerson newPolicyPerson : lifePolicy.getPolicyInsuredPersonList()) {
				for (PolicyInsuredPerson oldPolicyPerson : lifeProposal.getLifePolicy().getPolicyInsuredPersonList()) {
					if (oldPolicyPerson.getInsPersonCodeNo().equals(newPolicyPerson.getInsPersonCodeNo())) {
						newPolicyPerson.setId(oldPolicyPerson.getId());
						newPolicyPerson.setVersion(oldPolicyPerson.getVersion());
					}
				}
			}
			if (EndorsementUtil.isTerminatePolicy(lifeProposal)) {
				lifePolicyService.terminateLifePolicy(lifePolicy);
			} else {
				lifePolicyService.updateLifePolicy(lifePolicy);
			}
			lifeProposal.setLifePolicy(lifePolicy);
			lifeEndorseDAO.update(lifeProposal);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm a LifeProposal", e);
		}

		return null;
	}

	private void calculateTermPremium(PolicyInsuredPerson policyInsuredPerson, int paymentTypeMonth) {
		double basicPremium = policyInsuredPerson.getPremium();
		double addOnPremium = policyInsuredPerson.getAddOnPremium();
		if (paymentTypeMonth > 0) {
			int paymentTerm = policyInsuredPerson.getPeriodMonth() / paymentTypeMonth;
			policyInsuredPerson.setPaymentTerm(paymentTerm);
			/* Basic Term Premium */
			double basicTermPremium = (paymentTypeMonth * basicPremium) / 12;
			policyInsuredPerson.setBasicTermPremium(basicTermPremium);
			/* AddOn Term Premium */
			double addOnTermPremium = (paymentTypeMonth * addOnPremium) / 12;
			policyInsuredPerson.setAddOnTermPremium(addOnTermPremium);
		} else {
			policyInsuredPerson.setPaymentTerm(1);
			/* Basic Term Premium For Lump Sum */
			double basicTermPremium = (policyInsuredPerson.getPeriodMonth() * basicPremium) / 12;
			policyInsuredPerson.setBasicTermPremium(basicTermPremium);
			/* AddOn Term Premium For Lump Sum */
			double addOnTermPremium = (policyInsuredPerson.getPeriodMonth() * addOnPremium) / 12;
			policyInsuredPerson.setAddOnTermPremium(addOnTermPremium);
		}
	}

	private void calculateDiscount(PolicyInsuredPerson policyInsuredPerson, double discountPercent, boolean isEndorse) {
		/* Reset Discount EndorsementNetBasicPremium */
		double endorsementBasicPremium = policyInsuredPerson.getEndorsementNetBasicPremium();
		double discountEndorsementBasicPremium = endorsementBasicPremium - Utils.getPercentOf(discountPercent, endorsementBasicPremium);
		policyInsuredPerson.setEndorsementNetBasicPremium(discountEndorsementBasicPremium);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentLifeProposal(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch, String status) {
		try {
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			List<LifePolicy> policyList = new ArrayList<LifePolicy>();
			policyList = lifePolicyService.activateLifePolicy(lifeProposal.getId());
			List<AgentCommission> agentCommissionList = null;
			if (lifeProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				for (LifePolicy lifePolicy : policyList) {
					double totalCommission = 0.0;
					for (PolicyInsuredPerson pip : lifePolicy.getInsuredPersonInfo()) {
						double commissionPercent = pip.getProduct().getFirstCommission();
						if (commissionPercent > 0) {
							double totalPremium = pip.getBasicTermPremium() + pip.getAddOnTermPremium();
							double commission = (totalPremium / 100) * commissionPercent;
							totalCommission = totalCommission + commission;
						}
					}
					agentCommissionList.add(new AgentCommission(lifePolicy.getId(), PolicyReferenceType.LIFE_POLICY, lifePolicy.getAgent(), totalCommission, new Date()));
				}
			}

			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (LifePolicy lifePolicy : policyList) {
				String accountCode = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getProductGroup().getAccountCode();
				for (Payment payment : paymentList) {
					if (lifePolicy.getId().equals(payment.getReferenceNo())) {
						payment.setFromTerm(1);
						payment.setToTerm(1);
						accountPaymentList.add(new AccountPayment(accountCode, payment));
						break;
					}
				}
			}

			String currencyId = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getCurrency().getId();
			paymentService.preActivateEndorsementPayment(accountPaymentList, lifeProposal.getCustomerId(), branch, currencyId, lifeProposal.getSalePoint());
			lifeEndorseDAO.updateStatus(status, lifeProposal.getPortalId());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentLifeProposalTransfer(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (Payment payment : paymentList) {
				accountPaymentList.add(new AccountPayment(payment.getAccountBank().getAcode(), payment));
			}
			String currencyId = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getCurrency().getId();
			paymentService.activatePaymentTransfer(accountPaymentList, lifeProposal.getCustomerId(), branch, false, currencyId, lifeProposal.getSalePoint());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String status) {
		try {
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			boolean isShortTermLife = KeyFactorChecker.isShortTermEndowment(lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getId());
			List<LifePolicy> policyList = lifePolicyService.activateLifePolicy(lifeProposal.getId());
			if (!isShortTermLife) {
				List<AgentCommission> agentCommissionList = null;
				/* get agent commission of each policy */
				// temporarily block for agent commission insertion process
				/*
				 * if (lifeProposal.getAgent() != null) { agentCommissionList =
				 * new ArrayList<AgentCommission>(); for (LifePolicy lifePolicy
				 * : policyList) { double firstAgentCommission =
				 * lifePolicy.getAgentCommission(); agentCommissionList.add(new
				 * AgentCommission(lifePolicy.getId(),
				 * PolicyReferenceType.LIFE_POLICY, lifePolicy.getAgent(),
				 * firstAgentCommission, new Date())); } }
				 */

				/* add agent commission, activate TLF and Payment flag */
				// TODO FIXME PSH add salepoint
				paymentService.activatePaymentAndTLF(paymentList, agentCommissionList, branch, currencyCode, null);
				/* workflow */
				lifeEndorseDAO.updateStatus(RequestStatus.FINISHED.name(), lifeProposal.getId());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void issueLifeProposal(LifeProposal lifeProposal) {
		try {
			workFlowDTOService.deleteWorkFlowByRefNo(lifeProposal.getId());
			lifeEndorseDAO.updateCompleteStatus(true, lifeProposal.getId());
			if (lifeProposal.getLifePolicy() != null) {
				lifePolicyService.updateEndorsementStatus(false, lifeProposal.getLifePolicy());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to issue a LifeProposal.", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewSurvey(LifeSurvey lifeSurvey, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			String prefix = customIDGenerator.getPrefix(LifeSurvey.class, false);
			lifeSurvey.setPrefix(prefix);
			for (LifeProposalAttachment att : lifeSurvey.getLifeProposal().getAttachmentList()) {
				String attPrefix = customIDGenerator.getPrefix(LifeProposalAttachment.class, false);
				att.setPrefix(attPrefix);
			}
			for (ProposalInsuredPerson per : lifeSurvey.getLifeProposal().getProposalInsuredPersonList()) {
				for (InsuredPersonAttachment perAtt : per.getAttachmentList()) {
					String perAttPrefix = customIDGenerator.getPrefix(InsuredPersonAttachment.class, false);
					perAtt.setPrefix(perAttPrefix);
				}
			}
			lifeEndorseDAO.insertSurvey(lifeSurvey);

			lifeEndorseDAO.update(lifeSurvey.getLifeProposal());
			lifeEndorseDAO.addAttachment(lifeSurvey.getLifeProposal());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Survey", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal updateLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO) {
		try {
			lifeProposalHistoryService.addNewLifeProposalHistory(lifeProposal);
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			calculatePremium(lifeProposal);
			// Custom ID GEN For Proposal Insured Person and Beneficiary
			for (ProposalInsuredPerson person : lifeProposal.getProposalInsuredPersonList()) {
				if (person.getInsPersonCodeNo() == null) {
					insPersonCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_CODENO, null, false);
					person.setInsPersonCodeNo(insPersonCodeNo);
				}

				if (ProductIDConfig.isGroupLife(product)) {
					if (person.getInPersonGroupCodeNo() == null) {
						inPersonGroupCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_GROUP_CODENO, null, false);
						person.setInPersonGroupCodeNo(inPersonGroupCodeNo);
					}
				}
				for (InsuredPersonBeneficiaries beneficiary : person.getInsuredPersonBeneficiariesList()) {
					if (beneficiary.getBeneficiaryNo() == null) {
						beneficiaryNo = customIDGenerator.getNextId(SystemConstants.LIFE_BENEFICIARY_NO, null, false);
						beneficiary.setBeneficiaryNo(beneficiaryNo);
					}
				}
			}
			setIDPrefixForUpdate(lifeProposal);
			if (lifeProposal.getLifePolicy() != null) {
				org.ace.insurance.life.endorsement.LifeEndorseInfo info = null;
				info = updateLifeEndorseInfo(lifeProposal);
				;
				setEndorsementPremium(info, lifeProposal);
			}

			// Underwriting
			lifeEndorseDAO.update(lifeProposal);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
		return lifeProposal;
	}

	private void setIDPrefixForUpdate(LifeProposal lifeProposal) {
		String mProposalPrefix = customIDGenerator.getPrefix(LifeProposal.class, true);
		String insuredPersonPrefix = customIDGenerator.getPrefix(ProposalInsuredPerson.class, false);
		String insuredPersonBeneficiaryPrefix = customIDGenerator.getPrefix(InsuredPersonBeneficiaries.class, false);
		String isPesAddOnPrefix = customIDGenerator.getPrefix(InsuredPersonKeyFactorValue.class, false);
		String insuredPersonAddOnPrefix = customIDGenerator.getPrefix(InsuredPersonAddon.class, false);

		lifeProposal.setPrefix(mProposalPrefix);
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			pv.setPrefix(insuredPersonPrefix);
			for (InsuredPersonKeyFactorValue vhKf : pv.getKeyFactorValueList()) {
				vhKf.setPrefix(isPesAddOnPrefix);
			}
			List<InsuredPersonAddon> insuredPersonAddOnList = pv.getInsuredPersonAddOnList();
			if (insuredPersonAddOnList != null && !insuredPersonAddOnList.isEmpty()) {
				for (InsuredPersonAddon insuredPersonAddOn : insuredPersonAddOnList) {
					insuredPersonAddOn.setPrefix(insuredPersonAddOnPrefix);
				}
			}
			List<InsuredPersonBeneficiaries> insuredPersonBeneficiaryList = pv.getInsuredPersonBeneficiariesList();
			if (insuredPersonBeneficiaryList != null && !insuredPersonBeneficiaryList.isEmpty()) {
				for (InsuredPersonBeneficiaries insuredpersonbeneficiary : insuredPersonBeneficiaryList) {
					insuredpersonbeneficiary.setPrefix(insuredPersonBeneficiaryPrefix);
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal overWriteLifeProposal(LifeProposal lifeProposal) {
		try {
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			lifeProposalHistoryService.addNewLifeProposalHistory(lifeProposalDAO.findById(lifeProposal.getId()));
			calculatePremium(lifeProposal);
			// Custom ID GEN For Proposal Insured Person and Beneficiary
			for (ProposalInsuredPerson person : lifeProposal.getProposalInsuredPersonList()) {
				if (person.getInsPersonCodeNo() == null) {
					insPersonCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_CODENO, null, false);
					person.setInsPersonCodeNo(insPersonCodeNo);
				}

				if (ProductIDConfig.isGroupLife(product)) {
					if (person.getInPersonGroupCodeNo() == null) {
						inPersonGroupCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_GROUP_CODENO, null, false);
						person.setInPersonGroupCodeNo(inPersonGroupCodeNo);
					}
				}
				for (InsuredPersonBeneficiaries beneficiary : person.getInsuredPersonBeneficiariesList()) {
					if (beneficiary.getBeneficiaryNo() == null) {
						beneficiaryNo = customIDGenerator.getNextId(SystemConstants.LIFE_BENEFICIARY_NO, null, false);
						beneficiary.setBeneficiaryNo(beneficiaryNo);
					}
				}
			}
			setIDPrefixForUpdate(lifeProposal);
			updateLifeEndorseInfo(lifeProposal);
			lifeProposalDAO.update(lifeProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
		return lifeProposal;
	}

}
