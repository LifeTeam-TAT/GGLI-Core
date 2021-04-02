package org.ace.insurance.life.renewal.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.AddOnType;
import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.LifeEndorseInsuredPerson;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
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
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifePolicyService;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
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

@Service(value = "RenewalGroupLifeProposalService")
public class RenewalGroupLifeProposalService extends BaseService implements IRenewalGroupLifeProposalService {

	@Resource(name = "LifeProposalDAO")
	private ILifeProposalDAO lifeProposalDAO;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "RenewalGroupLifePolicyService")
	private IRenewalGroupLifePolicyService renewalGroupLifePolicyService;

	@Resource(name = "AcceptedInfoService")
	private IAcceptedInfoService acceptedInfoService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "CustomerService")
	private ICustomerService customerService;

	@Resource(name = "LifeProposalHistoryService")
	private ILifeProposalHistoryService lifeProposalHistoryService;

	public ILifeProposalHistoryService getLifeProposalHistoryService() {
		return lifeProposalHistoryService;
	}

	public void setLifeProposalHistoryService(ILifeProposalHistoryService lifeProposalHistoryService) {
		this.lifeProposalHistoryService = lifeProposalHistoryService;
	}

	public ILifeEndorsementService getLifeEndorsementService() {
		return lifeEndorsementService;
	}

	public void setLifeEndorsementService(ILifeEndorsementService lifeEndorsementService) {
		this.lifeEndorsementService = lifeEndorsementService;
	}

	@Resource(name = "LifeEndorsementService")
	private ILifeEndorsementService lifeEndorsementService;

	@Resource(name = "LifePolicyTimeLineLogService")
	private ILifePolicyTimeLineLogService lifePolicyTimeLineLogService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void approveLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			lifeProposalDAO.updateInsuredPersonApprovalInfo(lifeProposal.getProposalInsuredPersonList());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal renewalGroupLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, String status) {
		try {
			calculatePremium(lifeProposal);
			String productCode = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getProductGroup().getProposalNoPrefix();
			String proposalNo = null;
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			proposalNo = customIDGenerator.getNextId(SystemConstants.GROUP_LIFE_PROPOSAL_NO, productCode, true);

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

			LifeProposal mp = lifeProposalDAO.insert(lifeProposal);
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
		String attPrefix = customIDGenerator.getPrefix(InsuredPersonAttachment.class, false);

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
			List<InsuredPersonAttachment> attList = pv.getAttachmentList();
			if (attList != null && !attList.isEmpty()) {
				for (InsuredPersonAttachment att : attList) {
					att.setPrefix(attPrefix);
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
			lifeProposalDAO.insertSurvey(lifeSurvey);
			lifeProposalDAO.updateInsuPersonMedicalStatus(lifeSurvey.getLifeProposal().getProposalInsuredPersonList());
			lifeProposalDAO.addAttachment(lifeSurvey.getLifeProposal());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Survey", e);
		}
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
	public void informProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, AcceptedInfo acceptedInfo, String status) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);

			if (recalculatePremium(INFORM)) {
				calculatePremium(lifeProposal);
				lifeProposalDAO.update(lifeProposal);
			}
			if (acceptedInfo != null) {
				acceptedInfoService.addNewAcceptedInfo(acceptedInfo);
				lifeProposalDAO.updateStatus(status, lifeProposal.getPortalId());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal updateRenewalGroupLifeProposalEnquiry(LifeProposal lifeProposal) {
		try {
			lifeProposalHistoryService.addNewLifeProposalHistory(lifeProposal);
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
				info = lifeEndorsementService.updateLifeEndorseInfo(lifeProposal);
				setEndorsementPremium(info, lifeProposal);
			}

			// Underwriting
			lifeProposalDAO.update(lifeProposal);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
		return lifeProposal;
	}

	private void initializeEndorsementPremium(LifeProposal lifeProposal) {
		for (ProposalInsuredPerson proposalPerson : lifeProposal.getProposalInsuredPersonList()) {
			proposalPerson.setEndorsementNetBasicPremium(0);
			proposalPerson.setInterest(0);
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
	public List<Payment> confirmLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status) {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);

			// reset insured person start/end date
			for (ProposalInsuredPerson proposalInsPerson : lifeProposal.getProposalInsuredPersonList()) {
				if (proposalInsPerson.getStartDate().before(new Date())) {
					proposalInsPerson.setStartDate(new Date());
				}
				Calendar cal = Calendar.getInstance();
				cal.setTime(proposalInsPerson.getStartDate());
				cal.add(Calendar.MONTH, proposalInsPerson.getPeriodMonth());
				proposalInsPerson.setEndDate(cal.getTime());
			}
			if (recalculatePremium(CONFIRMATION)) {
				calculatePremium(lifeProposal);
			}

			// create LifePolicy
			LifePolicy lifePolicy = new LifePolicy(lifeProposal);
			double rate = 1.0;

			// recalculate payment term and term premium
			for (PolicyInsuredPerson person : lifePolicy.getPolicyInsuredPersonList()) {
				calculateTermPremium(person, lifePolicy.getPaymentType().getMonth());
			}

			// create Payment
			Payment payment = new Payment();
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.LIFE_POLICY);
			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());

			if (paymentDTO.getBankWallet() != null) {
				payment.setBankWallet(paymentDTO.getBankWallet());
				if (payment.getBankWallet().getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
					payment.setBankCharges(paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(rate * (paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges()));
				} else {
					payment.setBankCharges(
							(paymentDTO.getBankWallet().getCharges() * lifePolicy.getTotalBasicTermPremium() / 100) + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(
							(rate * (paymentDTO.getBankWallet().getCharges() * lifePolicy.getTotalBasicTermPremium() / 100)) + paymentDTO.getBankWallet().getAdditionalCharges());
				}
			}
			payment.setDiscountPercent(paymentDTO.getDiscountPercent());
			payment.setReceivedDeno(paymentDTO.getReceivedDeno());
			payment.setRefundDeno(paymentDTO.getRefundDeno());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setBasicPremium(lifePolicy.getTotalBasicTermPremium());
			payment.setAddOnPremium(lifePolicy.getTotalAddOnTermPremium());
			double charges = 0.0;
			if (paymentDTO.getBankWallet() != null) {
				charges = paymentDTO.getBankWallet().getCharges();
			}
			payment.setAmount(lifePolicy.getTotalBasicTermPremium() - charges);
			payment.setHomeAmount(lifePolicy.getTotalBasicTermPremium() - charges);
			payment.setHomePremium(rate * lifePolicy.getTotalBasicTermPremium());
			/*
			 * if (paymentDTO.getBankWallet() != null) {
			 * payment.setBankCharges(paymentDTO.getBankWallet().getCharges());
			 * payment.setHomeBankCharges(rate *
			 * paymentDTO.getBankWallet().getCharges()); }
			 */

			renewalGroupLifePolicyService.addNewLifePolicy(lifePolicy);

			payment.setReferenceNo(lifePolicy.getId());
			paymentList.add(payment);
			lifeProposalDAO.update(lifeProposal);
			paymentList = paymentService.prePayment(paymentList);

			prePaymentGroupLifeProposal(lifeProposal, paymentList, branch, status);

			if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE) || paymentDTO.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
				prePaymentLifeProposalTransfer(lifeProposal, paymentList, branch);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm a LifeProposal", e);
		}
		return paymentList;
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

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentLifeProposalTransfer(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (Payment payment : paymentList) {
				accountPaymentList.add(new AccountPayment(payment.getAccountBank().getAcode(), payment));
			}
			paymentService.activatePaymentTransfer(accountPaymentList, lifeProposal.getCustomerId(), branch, false, currencyCode, lifeProposal.getSalePoint());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentGroupLifeProposal(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch, String status) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			if (recalculatePremium(PAYMENT)) {
				calculatePremium(lifeProposal);
				lifeProposalDAO.update(lifeProposal);
			}
			List<LifePolicy> policyList = renewalGroupLifePolicyService.activateLifePolicy(lifeProposal.getId());
			List<AgentCommission> agentCommissionList = null;
			if (lifeProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				for (LifePolicy lifePolicy : policyList) {
					double totalCommission = 0.0;
					for (PolicyInsuredPerson pip : lifePolicy.getInsuredPersonInfo()) {
						double commissionPercent = pip.getProduct().getRenewalCommission();
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

			// for interbranch
			if (!lifeProposal.getBranch().getId().equals(branch.getId())) {
				paymentService.preActivatePaymentForInterBranch(accountPaymentList, lifeProposal.getCustomerId(), branch, agentCommissionList, true, currencyCode,
						lifeProposal.getSalePoint(), lifeProposal.getBranch());

			} else {
				paymentService.preActivatePayment(accountPaymentList, lifeProposal.getCustomerId(), branch, agentCommissionList, true, currencyCode, lifeProposal.getSalePoint());
			}
			
			lifeProposalDAO.updateStatus(status, lifeProposal.getPortalId());
			for (LifePolicy lifePolicy : policyList) {
				/* Entry to LifePolicyTimeLineLog (Renewal) */
				LifePolicyTimeLineLog timeLineLog = new LifePolicyTimeLineLog(lifePolicy.getPolicyNo(), lifePolicy.getActivedPolicyStartDate(),
						lifePolicy.getActivedPolicyEndDate(), lifePolicy.getCommenmanceDate());
				lifePolicyTimeLineLogService.addLifePolicyTimeLineLog(timeLineLog);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String status) {
		try {
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			List<LifePolicy> policyList = renewalGroupLifePolicyService.activateLifePolicy(lifeProposal.getId());
			List<AgentCommission> agentCommissionList = null;
			if (lifeProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				String receiptNo = paymentList.get(0).getReceiptNo();
				LifePolicy lifePolicy = policyList.get(0);
				double commissionPercent = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getFirstCommission();
				Payment p = paymentService.findPaymentByReferenceNo(lifePolicy.getId());
				AgentCommissionEntryType type = ProposalType.UNDERWRITING.equals(lifeProposal.getProposalType()) ? AgentCommissionEntryType.UNDERWRITING
						: AgentCommissionEntryType.RENEWAL;
				agentCommissionList.add(new AgentCommission(lifePolicy.getId(), PolicyReferenceType.LIFE_POLICY, lifePolicy.getAgent(), lifePolicy.getAgentCommission(), new Date(),
						receiptNo, lifePolicy.getTotalPremium(), commissionPercent, type, p.getRate(), (p.getRate() * lifePolicy.getAgentCommission()), p.getCur(),
						(p.getRate() * lifePolicy.getTotalPremium())));

			}

			// TODO FIXME PSH add salepoint
			paymentService.activatePaymentAndTLF(paymentList, agentCommissionList, branch, currencyCode, null);
			lifeProposalDAO.updateStatus(RequestStatus.FINISHED.name(), lifeProposal.getId());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal findLifeProposalById(String id) {
		LifeProposal result = null;
		try {
			result = lifeProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void issueLifeProposal(LifeProposal lifeProposal) {
		try {
			workFlowDTOService.deleteWorkFlowByRefNo(lifeProposal.getId());
			lifeProposalDAO.updateCompleteStatus(true, lifeProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal complete status", e);
		}
	}
}
