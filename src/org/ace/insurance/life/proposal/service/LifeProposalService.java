package org.ace.insurance.life.proposal.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.AddOnType;
import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.CustomerStatus;
import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.LifePolicyCriteriaItems;
import org.ace.insurance.common.Name;
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
import org.ace.insurance.life.LifeEndowmentPolicySearch;
import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.persistence.interfaces.IBancaassuranceProposalDAO;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.bancassurance.proposalhistory.service.interfaces.IBancaassuranceProposalHistoryService;
import org.ace.insurance.life.claim.LifePolicyCriteria;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.LifeEndorseInsuredPerson;
import org.ace.insurance.life.endorsement.persistence.interfaces.ILifeEndorsementDAO;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.migrate.ShortEndowmentExtraValue;
import org.ace.insurance.life.migrate.persistence.interfaces.IShortEndowmentExtraValueDAO;
import org.ace.insurance.life.policy.LPC001;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.persistence.interfaces.ILifePolicyDAO;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyLog.LifePolicyIdLog;
import org.ace.insurance.life.policyLog.LifePolicyTimeLineLog;
import org.ace.insurance.life.policyLog.service.interfaces.ILifePolicyTimeLineLogService;
import org.ace.insurance.life.proposal.InsuredPersonAddon;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LPL001;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.persistence.interfaces.ILifeProposalDAO;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.life.proposalhistory.service.interfaces.ILifeProposalHistoryService;
import org.ace.insurance.medical.proposal.CustomerInfoStatus;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.auditLog.AuditLog;
import org.ace.insurance.system.common.auditLog.service.interfaces.IAuditLogService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.IDGen;
import org.ace.java.component.idgen.exception.CustomIDGeneratorException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "LifeProposalService")
public class LifeProposalService extends BaseService implements ILifeProposalService {

	@Resource(name = "LifeProposalDAO")
	private ILifeProposalDAO lifeProposalDAO;

	@Resource(name = "LifePolicyDAO")
	private ILifePolicyDAO lifePolicyDAO;

	@Resource(name = "LifeEndorsementDAO")
	private ILifeEndorsementDAO lifeEndorsementDAO;

	@Resource(name = "BancaassuranceProposalDAO")
	private IBancaassuranceProposalDAO bancaassuranceProposalDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "LifePolicyService")
	private ILifePolicyService lifePolicyService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "AcceptedInfoService")
	private IAcceptedInfoService acceptedInfoService;

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "OrganizationDAO")
	private IOrganizationDAO organizationDAO;

	@Resource(name = "LifePolicySummaryService")
	private ILifePolicySummaryService lifePolicySummaryService;

	@Resource(name = "LifeProposalHistoryService")
	private ILifeProposalHistoryService lifeProposalHistoryService;

	@Resource(name = "BancaassuranceProposalHistoryService")
	private IBancaassuranceProposalHistoryService bancaassuranceProposalHistoryService;

	@Resource(name = "LifeEndorsementService")
	private ILifeEndorsementService lifeEndorsementService;

	@Resource(name = "LifePolicyTimeLineLogService")
	private ILifePolicyTimeLineLogService lifePolicyTimeLineLogService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "CustomerService")
	public ICustomerService customerService;

	@Resource(name = "AuditLogService")
	public IAuditLogService auditLogService;

	@Resource(name = "ShortEndowmentExtraValueDAO")
	private IShortEndowmentExtraValueDAO shortEndowmentExtraValueDAO;

	@Resource(name = "BancaassuranceProposalService")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	@Resource(name = "BancaassurancePolicyService")
	private IBancaassurancePolicyService bancaassuracePolicyService;

	public void calculatePremium(LifeProposal lifeProposal) {
		int paymentType = lifeProposal.getPaymentType().getMonth();
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		boolean isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
		boolean isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		boolean isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		boolean isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		boolean isSimpleLife = KeyFactorChecker.isSimpleLife(product);
		boolean isPaymentTypeKeyFactor = isStudentLife;
		for (ProposalInsuredPerson person : lifeProposal.getProposalInsuredPersonList()) {
			Double premium = 0.0;
			double termPremium = 0.0;
			int paymentTerm = 0;
			if (product.getAutoCalculate()) {
				Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
				for (InsuredPersonKeyFactorValue vhKf : person.getKeyFactorValueList()) {
					keyfatorValueMap.put(vhKf.getKeyFactor(), vhKf.getValue());
				}

				// if (isSimpleLife) {
				// for (InsuredPersonAddon personAddon :
				// person.getInsuredPersonAddOnList()) {
				// for (InsuredPersonKeyFactorValue keyfactorValue :
				// person.getKeyFactorValueList()) {
				// if
				// (KeyFactorChecker.isSimpleLifeAddon(keyfactorValue.getKeyFactor()))
				// {
				// keyfactorValue.setValue(personAddon.getAddOn().getId() + "");
				// keyfatorValueMap.put(keyfactorValue.getKeyFactor(),
				// keyfactorValue.getValue());
				// premium +=
				// productService.findProductPremiumRate(keyfatorValueMap,
				// product, person.getSumInsuredType());
				// }
				// }
				// }
				// } else {
				// premium =
				// productService.findProductPremiumRate(keyfatorValueMap,
				// product, person.getSumInsuredType());
				// }
				premium = productService.findProductPremiumRate(keyfatorValueMap, product, person.getSumInsuredType());

				if ((premium == null || premium <= 0)) {
					throw new SystemException(ErrorCode.NO_PREMIUM_RATE, keyfatorValueMap, "There is no premiumn.");
				}

				if (paymentType > 0) {
					if (isPaymentTypeKeyFactor) {
						termPremium = premium;
						// one year premium
						premium = premium * (12 / paymentType);
					} else
						termPremium = (paymentType * premium) / 12;
					if (isStudentLife) {
						paymentTerm = (person.getPeriodYears() - 3) * 12 / paymentType;
					} else
						paymentTerm = person.getPeriodMonth() / paymentType;
					person.setPaymentTerm(paymentTerm);
					person.setBasicTermPremium(termPremium);
				} else {
					// *** Calculation for Lump Sum ***
					person.setPaymentTerm(0);
					if (isSinglePremiumEndowmentLife || isSinglePremiumCreditLife || isShortTermSinglePremiumCreditLife || isSimpleLife) {
						// premium rate from db is already calculated based on
						// term for basicTerm
						person.setBasicTermPremium(premium);
					} else {
						termPremium = (person.getPeriodMonth() * premium) / 12;
						person.setBasicTermPremium(termPremium);
					}
				}

				person.setProposedPremium(premium);
				calculateAddOnPremium(person);
				double addOnPremium = lifeProposal.getAddOnPremium();
				if (paymentType > 0) {
					if (isPaymentTypeKeyFactor) {
						termPremium = addOnPremium;
					} else
						termPremium = (paymentType * addOnPremium) / 12;
					person.setAddOnTermPremium(termPremium);
				} else {
					// *** Calculation for Lump Sum AddOn Premium***
					termPremium = (person.getPeriodMonth() * addOnPremium) / 12;
					person.setAddOnTermPremium(termPremium);
				}

			} else {
				// one year premium
				premium = Utils.getPercentOf(person.getProduct().getFixedValue(), person.getProposedSumInsured());
				if (person.getPeriodMonth() < 12) {
					premium = premium * person.getPeriodMonth() / 12;
				}
				person.setProposedPremium(premium);

				// *** Calculation for Lump Sum ***
				person.setPaymentTerm(0);
				person.setBasicTermPremium(premium);
				calculateAddOnPremium(person);
				person.setAddOnTermPremium(person.getAddOnPremium());
			}
		}

		if (!ProposalType.RENEWAL.equals(lifeProposal.getProposalType())) {
			resetDate(lifeProposal);
		}
	}

	public void calculateAddOnPremium(ProposalInsuredPerson pv) {
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

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal addNewMigrateLifeProposal(LifeProposal lifeProposal, LifeEndowmentPolicySearch searchPolicy, WorkFlowDTO workFlowDTO, String status) {
		try {
			calculatePremium(lifeProposal);

			if (lifeProposal.getProposalInsuredPersonList().get(0).getBasicTermPremium() <= searchPolicy.getPaidPremium()) {
				String productCode = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getProductGroup().getProposalNoPrefix();
				String proposalNo = null;
				String insPersonCodeNo = null;
				String beneficiaryNo = null;
				Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();

				if (lifeProposal.getProposalNo() == null) {
					String proposalIdGen = null;
					if (ProductIDConfig.isShortEndowmentLife(product)) {
						proposalIdGen = SystemConstants.SHORT_TERM_ENDOWMENT_LIFE_PROPOSAL_NO;
					}
					proposalNo = customIDGenerator.getNextId(proposalIdGen, productCode, true);
				}

				// Custom ID GEN For Proposal Insured Person and Beneficiary
				for (ProposalInsuredPerson person : lifeProposal.getProposalInsuredPersonList()) {
					if (person.getInsPersonCodeNo() == null) {
						insPersonCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_CODENO, null, false);
						person.setInsPersonCodeNo(insPersonCodeNo);
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
				lifeProposal = lifeProposalDAO.insert(lifeProposal);
				// EndowmentLifePolicy update
				LifePolicy endowLifePolicy = lifeProposal.getLifePolicy();
				endowLifePolicy.setMigrated(true);
				lifePolicyDAO.update(endowLifePolicy);
				workFlowDTO.setReferenceNo(lifeProposal.getId());
				workFlowDTOService.addNewWorkFlow(workFlowDTO);
				lifeProposalDAO.updateStatus(status, lifeProposal.getPortalId());
			} else {
				return null;
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeProposal", e);
		} catch (CustomIDGeneratorException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeProposal", e);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal addNewLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, String status, BancaassuranceProposal bancaassuranceProposal) {
		try {
			calculatePremium(lifeProposal);
			String productCode = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getProductGroup().getProposalNoPrefix();
			String proposalNo = null;
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			if (lifeProposal.getProposalNo() == null) {
				String proposalIdGen = null;
				if (ProductIDConfig.isPublicLife(product)) {
					proposalIdGen = SystemConstants.ENDOWMENT_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isGroupLife(product)) {
					proposalIdGen = SystemConstants.GROUP_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isShortEndowmentLife(product)) {
					proposalIdGen = SystemConstants.SHORT_TERM_ENDOWMENT_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isSportMan(product)) {
					proposalIdGen = SystemConstants.SPORTSMAN_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isFarmer(product)) {
					proposalIdGen = SystemConstants.FARMER_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isPersonalAccidentKYT(product) || ProductIDConfig.isPersonalAccidentUSD(product)) {
					proposalIdGen = SystemConstants.PERSONAL_ACCIDENT_PROPOSAL_NO;
				} else if (ProductIDConfig.isSnakeBite(product)) {
					proposalIdGen = SystemConstants.SNAKE_BITE_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isStudentLife(product)) {
					proposalIdGen = SystemConstants.STUDENT_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isPublicTermLife(product)) {
					proposalIdGen = SystemConstants.PUBLIC_TERM_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isSinglePremiumEndowmentLife(product)) {
					proposalIdGen = SystemConstants.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isSinglePremiumCreditLife(product)) {
					proposalIdGen = SystemConstants.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isShortTermSinglePremiumCreditLife(product)) {
					proposalIdGen = SystemConstants.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL_NO;
				} else if (ProductIDConfig.isSimpleLife(product)) {
					proposalIdGen = SystemConstants.SIMPLE_LIFE_PROPOSAL_NO;
				}

				proposalNo = customIDGenerator.getNextId(proposalIdGen, productCode, true);
			}
			if (proposalNo == null) {
				throw new SystemException(ErrorCode.FAIL_TOLOAD_PROPOSALNO, "Failed to add a new LifeProposal");
			}
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
			if (ProductIDConfig.isStudentLife(lifeProposal.getProposalInsuredPersonList().get(0).getProduct())) {
				Customer customer = lifeProposal.getCustomer();
				CustomerInfoStatus s = new CustomerInfoStatus();
				if (customer != null) {
					if (customer.getId() == null) {
						s.setStatusName(CustomerStatus.CONTRACTOR);
						customer.addCustomerInfoStatus(s);
						customer = customerService.addNewCustomer(customer);
					} else {
						/** Exiting Customer **/
						s.setStatusName(CustomerStatus.CONTRACTOR);
						customer.addCustomerInfoStatus(s);
						customer = customerService.updateCustomer(customer);
					}
					lifeProposal.setCustomer(customer);
				}
			}
			if (ProductIDConfig.isSimpleLife(product)) {
				Customer proposalCustomer = lifeProposal.getCustomer();
				if (proposalCustomer != null) {
					if (proposalCustomer.getId() == null) {
						proposalCustomer = customerService.addNewCustomer(proposalCustomer);
					}
					lifeProposal.setCustomer(proposalCustomer);
				}
				Customer insuredCustomer = lifeProposal.getProposalInsuredPersonList().get(0).getCustomer();
				if (insuredCustomer != null) {
					if (insuredCustomer.getId() == null) {
						insuredCustomer = customerService.addNewCustomer(insuredCustomer);
					}
					lifeProposal.getProposalInsuredPersonList().get(0).setCustomer(insuredCustomer);
				}

				AuditLog auditLog = lifeProposal.getAuditLog();
				if (auditLog != null) {
					if (auditLog.getId() == null) {
						String excelFileId = customIDGenerator.getNextId(SystemConstants.SIMPLE_LIFE_EXCEL_PROPOSAL_NO, null, true);
						if (excelFileId == null) {
							throw new SystemException("Failed to add a new LifeProposal");
						} else {
							auditLog.setExcelFileId(excelFileId);
							auditLog = auditLogService.addNewAuditLog(auditLog);
						}
					}
					lifeProposal.setAuditLog(auditLog);
				}
			}

			lifeProposal = lifeProposalDAO.insert(lifeProposal);

			if (lifeProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				bancaassuranceProposal.setLifeProposal(lifeProposal);
				bancaassuraceProposalService.insert(bancaassuranceProposal);
			}

			if (lifeProposal.getId() != null && lifeProposal.getProposalInsuredPersonList().get(0).getBirthCertificateAttachment().size() > 0) {
				String filePath = null;
				for (Attachment a : lifeProposal.getProposalInsuredPersonList().get(0).getBirthCertificateAttachment()) {
					filePath = "/upload/life-proposal/" + lifeProposal.getId() + "/" + lifeProposal.getProposalInsuredPersonList().get(0).getId() + "/Birth_Certificate" + "/"
							+ a.getName();
					a.setFilePath(filePath);
				}
				updateLifeProposal(lifeProposal);
			}
			workFlowDTO.setReferenceNo(lifeProposal.getId());
			workFlowDTOService.addNewWorkFlow(workFlowDTO);
			lifeProposalDAO.updateStatus(status, lifeProposal.getPortalId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeProposal", e);
		} catch (CustomIDGeneratorException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeProposal", e);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal renewalGroupLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, String status) {
		try {
			org.ace.insurance.life.endorsement.LifeEndorseInfo info = null;
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
			throw new SystemException(e.getErrorCode(), "Faield to renewal GroupLifeProposal.", e);
		} catch (CustomIDGeneratorException e) {
			throw new SystemException(e.getErrorCode(), "Faield to renewal GroupLifeProposal.", e);
		}
		return lifeProposal;
	}

	private void setIDPrefixForInsert(LifeProposal lifeProposal) {
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
	public void approveLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			lifeProposalDAO.updateInsuredPersonApprovalInfo(lifeProposal.getProposalInsuredPersonList());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to approve a LifeProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			if (ProductIDConfig.isStudentLife(lifeProposal.getProposalInsuredPersonList().get(0).getProduct())) {
				lifeProposalDAO.updateProposalStatus(lifeProposal.getProposalStatus(), lifeProposal.getId());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reject a LifeProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void informProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, AcceptedInfo acceptedInfo, String status) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			if (!EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy())) {
				if (recalculatePremium(INFORM)) {
					calculatePremium(lifeProposal);
					lifeProposalDAO.update(lifeProposal);
				}
			}
			if (acceptedInfo != null) {
				acceptedInfoService.addNewAcceptedInfo(acceptedInfo);
				lifeProposalDAO.updateStatus(status, lifeProposal.getPortalId());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to inform a LifeProposal", e);
		}
	}

	private void resetDate(LifeProposal lifeProposal) {
		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (proposalInsuredPerson.isApproved()) {
				/* Recalculate Start Date and End Date */
				proposalInsuredPerson.setStartDate(new Date());
				Calendar cal = Calendar.getInstance();
				cal.setTime(proposalInsuredPerson.getStartDate());
				if (KeyFactorChecker.isSimpleLife(proposalInsuredPerson.getProduct())) {
					if (proposalInsuredPerson.getPeriodYear() > 0) {
						cal.add(Calendar.YEAR, proposalInsuredPerson.getPeriodYear());
					} else if (proposalInsuredPerson.getPeriodMonth() > 0) {
						cal.add(Calendar.MONTH, proposalInsuredPerson.getPeriodMonth());
					} else {
						cal.add(Calendar.DAY_OF_MONTH, proposalInsuredPerson.getPeriodWeek() * 7);
					}
					proposalInsuredPerson.setEndDate(cal.getTime());
				} else {
					cal.add(Calendar.MONTH, proposalInsuredPerson.getPeriodMonth());
					proposalInsuredPerson.setEndDate(cal.getTime());
				}
			}
		}
	}

	private void calculateDiscount(PolicyInsuredPerson policyInsuredPerson, double discountPercent) {
		/* Reset Discount Basic Premium */
		double basicPremium = policyInsuredPerson.getPremium();
		double discountBasicPremium = basicPremium - Utils.getPercentOf(discountPercent, basicPremium);
		policyInsuredPerson.setPremium(discountBasicPremium);

		/* Reset Discount Basic Term Premium */
		double basicTermPremium = policyInsuredPerson.getBasicTermPremium();
		double discountBasicTermPremium = basicTermPremium - Utils.getPercentOf(discountPercent, basicTermPremium);
		policyInsuredPerson.setBasicTermPremium(discountBasicTermPremium);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status, boolean isSkipTLFPayment) {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {

			if (null != lifeProposal.getId()) {
				List<LifePolicy> policyList = lifePolicyService.findPolicyByProposalId(lifeProposal.getId());
				if (null != policyList && policyList.size() > 0) {
					throw new SystemException(ErrorCode.PROPOSAL_ALREADY_CONFIRMED, " Proposal is already confirmed.");
				} else {

					if (paymentDTO.getConfirmDate() == null) {
						paymentDTO.setConfirmDate(new Date());
					}
					boolean isRenewal = lifeProposal.getProposalType() == ProposalType.RENEWAL;
					Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
					boolean isPersonalAccident = KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product);
					boolean isFarmer = KeyFactorChecker.isFarmer(product);
					boolean isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
					boolean isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
					boolean isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
					boolean isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
					boolean isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
					boolean isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
					boolean isSimpleLife = KeyFactorChecker.isSimpleLife(product);
					PolicyReferenceType referenceType = isFarmer ? PolicyReferenceType.FARMER_POLICY
							: isPublicTermLife ? PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY
									: isSinglePremiumEndowmentLife ? PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY
											: isSinglePremiumCreditLife ? PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY
													: isShortTermSinglePremiumCreditLife ? PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY
															: isPersonalAccident ? PolicyReferenceType.PA_POLICY
																	: isShortEndowLife ? PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY
																			: isStudentLife ? PolicyReferenceType.STUDENT_LIFE_POLICY
																					: isSimpleLife ? PolicyReferenceType.SIMPLE_LIFE_POLICY : PolicyReferenceType.LIFE_POLICY;
					if (!isSkipTLFPayment) {
						workFlowDTOService.updateWorkFlow(workFlowDTO);
					}
					if (recalculatePremium(CONFIRMATION)) {
						calculatePremium(lifeProposal);
					}
					// reset insured person start/end date
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
							proposalInsPerson.setStartDate(paymentDTO.getConfirmDate());
							Calendar cal = Calendar.getInstance();
							cal.setTime(proposalInsPerson.getStartDate());
							if (isSimpleLife) {
								if (proposalInsPerson.getPeriodYear() > 0) {
									cal.add(Calendar.YEAR, proposalInsPerson.getPeriodYear());
								} else if (proposalInsPerson.getPeriodMonth() > 0) {
									cal.add(Calendar.MONTH, proposalInsPerson.getPeriodMonth());
								} else {
									cal.add(Calendar.DAY_OF_MONTH, proposalInsPerson.getPeriodWeek() * 7);
								}
								proposalInsPerson.setEndDate(cal.getTime());

							} else {
								cal.add(Calendar.MONTH, proposalInsPerson.getPeriodMonth());
								proposalInsPerson.setEndDate(cal.getTime());
							}
						}
					}
					// create LifePolicy
					LifePolicy lifePolicy = new LifePolicy(lifeProposal);

					// calculate premium payment end date
					Calendar cal = Calendar.getInstance();
					cal.setTime(lifePolicy.getPolicyInsuredPersonList().get(0).getEndDate());
					if (isStudentLife) {
						cal.add(Calendar.YEAR, -3);
					}
					lifePolicy.setPaymentEndDate(cal.getTime());

					// current rate for HomeAmount
					double rate = 1.0;
					if (CurrencyUtils.isUSD(product.getCurrency())) {
						rate = paymentDAO.findActiveRate();
					}

					// create Payment
					Payment payment = new Payment();
					payment.setPaymentType(lifePolicy.getPaymentType());
					payment.setBank(paymentDTO.getBank());
					payment.setBankAccountNo(paymentDTO.getBankAccountNo());
					payment.setChequeNo(paymentDTO.getChequeNo());
					payment.setPaymentChannel(paymentDTO.getPaymentChannel());
					payment.setReferenceType(referenceType);
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
							payment.setHomeBankCharges((rate * (paymentDTO.getBankWallet().getCharges() * lifePolicy.getTotalBasicTermPremium() / 100))
									+ paymentDTO.getBankWallet().getAdditionalCharges());
						}
					}
					payment.setDiscountPercent(paymentDTO.getDiscountPercent());
					payment.setReceivedDeno(paymentDTO.getReceivedDeno());
					payment.setRefundDeno(paymentDTO.getRefundDeno());
					payment.setConfirmDate(paymentDTO.getConfirmDate());
					payment.setPoNo(paymentDTO.getPoNo());
					payment.setAccountBank(paymentDTO.getAccountBank());
					payment.setBasicPremium(lifePolicy.getTotalBasicTermPremium());
					payment.setAddOnPremium(lifePolicy.getTotalAddOnTermPremium());
					payment.setCur(CurrencyUtils.getCurrencyCode(product.getCurrency()));
					payment.setRate(rate);
					payment.setAmount(payment.getTotalAmount());
					payment.setHomeAmount(rate * payment.getTotalAmount());
					payment.setHomePremium(rate * payment.getBasicPremium());
					payment.setHomeAddOnPremium(rate * payment.getAddOnPremium());
					payment.setHomeStampFees(rate * paymentDTO.getStampFees());
					payment.setHomeServicesCharges(rate * paymentDTO.getServicesCharges());
					payment.setHomePenaltyPremium(rate * paymentDTO.getPenaltyPremium());

					// calculate payment term and term premium for discount
					if (paymentDTO.getDiscountPercent() > 0.0) {
						for (PolicyInsuredPerson person : lifePolicy.getPolicyInsuredPersonList()) {
							calculateDiscount(person, paymentDTO.getDiscountPercent());
						}
					}

					BancaassuranceProposal BancaProposal = bancaassuraceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposal.getId());
					if (BancaProposal != null) {
						BancaassurancePolicy bancaassurancePolicy = new BancaassurancePolicy(BancaProposal);
						bancaassurancePolicy.setLifePolicy(lifePolicy);
						bancaassuracePolicyService.insert(bancaassurancePolicy);
					}

					if (lifeProposal.isStaffPlan()) {
						lifePolicy.setEips(lifeProposal.getEips());
					}
					lifePolicyService.addNewLifePolicy(lifePolicy);
					payment.setReferenceNo(lifePolicy.getId());
					paymentList.add(payment);
					lifeProposalDAO.update(lifeProposal);
					paymentList = paymentService.prePayment(paymentList);
					if (!isSkipTLFPayment) {
						// prepayment TLF and change status of TLF payment.
						prePaymentLifeProposal(lifeProposal, paymentList, branch, status);

						if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE) || paymentDTO.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
							prePaymentLifeProposalTransfer(lifeProposal, paymentList, branch);
						}
					}
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm a LifeProposal", e);
		}
		return paymentList;
	}

	/*
	 * @Purpose SkipPaymentTLF in confirm stage for farmer (non-Javadoc)
	 * 
	 * @see
	 * org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService#
	 * confirmSkipPaymentTLFLifeProposal(org.ace.insurance.life.proposal.
	 * LifeProposal, org.ace.insurance.common.WorkFlowDTO,
	 * org.ace.insurance.common.PaymentDTO,
	 * org.ace.insurance.system.common.branch.Branch, java.lang.String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmSkipPaymentTLFLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status) {
		List<Payment> paymentList = null;
		try {
			paymentList = confirmLifeProposal(lifeProposal, workFlowDTO, paymentDTO, branch, status, true);
			paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, branch, status);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirmSkipPaymentTLF a LifeProposal ID : " + lifeProposal.getId(), e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentLifeProposal(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch, String status) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			String accountCode = null;
			// for simple life
			if (KeyFactorChecker.isSimpleLife(product)) {
				for (InsuredPersonKeyFactorValue vehKF : lifeProposal.getProposalInsuredPersonList().get(0).getKeyFactorValueList()) {
					if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {

						switch (vehKF.getValue()) {
							case "DEATH COVER":
								accountCode = "Death_Cover_Premium";
								break;
							case "DEATH AND TOTAL PERMANENT DISABLE (TPD)":
								accountCode = "Death_Total_Permanent _Disable_Premium";
								break;
							case "DEATH, ADDITIONAL COVER FOR ACCIDENTAL DEATH AND ACCIDENTAL TPD":
								accountCode = "Death_AdditionalCover_AccidentalDeath_TPD_Premium";
								break;
							case "DEATH AND TPD, ADDITIONAL COVER FOR ACCIDENTAL DEATH AND ACCIDENTAL TPD":
								accountCode = "Death_TPD_AdditionalCover_AccidentalDeath_TPD_Premium";
								break;
							case "ACCIDENTAL DEATH AND ACCIDENTAL TPD ONLY":
								accountCode = "AccidentalDeath_AccidentalTPDOnly_Premium";
								break;

							default:
								break;
						}
					}
				}
			} else {
				accountCode = product.getProductGroup().getAccountCode();
			}

			String currencyCode = CurrencyUtils.getCurrencyCode(product.getCurrency());
			String currencyID = product.getCurrency().getId();
			PolicyReferenceType referenceType = paymentList.get(0).getReferenceType();
			List<LifePolicy> policyList = lifePolicyDAO.findByProposalId(lifeProposal.getId());
			List<AgentCommission> agentCommissionList = null;
			List<StaffAgentCommission> staffagentCommissionList = null;
			/* get agent commission of each policy */
			if (lifeProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				for (LifePolicy lifePolicy : policyList) {
					double firstAgentCommission = lifePolicy.getAgentCommission();
					agentCommissionList.add(new AgentCommission(lifePolicy.getId(), referenceType, lifePolicy.getAgent(), firstAgentCommission, new Date()));
				}
			}

			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			/* get AccountPayment of each policy */
			for (Payment payment : paymentList) {
				payment.setFromTerm(1);
				payment.setToTerm(1);
				accountPaymentList.add(new AccountPayment(accountCode, payment));
			}

			if (lifeProposal.isStaffPlan()) {
				String eNo = accountPaymentList.get(0).getPayment().getId();
				Payment paymentByIndex = accountPaymentList.get(0).getPayment();
				staffagentCommissionList = new ArrayList<StaffAgentCommission>();
				for (LifePolicy lifePolicy : policyList) {
					// double firststaffagentCommissionList =
					// lifePolicy.getAgentCommission();
					staffagentCommissionList
							.add(new StaffAgentCommission(lifePolicy.getId(), referenceType, lifePolicy.getEips().getStaff(), lifePolicy.getEips().getAmount(), new Date()));
				}

				if (!lifeProposal.getBranch().getId().equals(branch.getId())) {
					paymentService.addStaffCommissionTLF(staffagentCommissionList, lifeProposal.getBranch(), paymentByIndex, eNo, false, currencyID, lifeProposal.getSalePoint());

				} else {
					paymentService.addStaffCommissionTLF(staffagentCommissionList, branch, paymentByIndex, eNo, false, currencyID, lifeProposal.getSalePoint());
				}
			}

			// for interbranch
			if (!lifeProposal.getBranch().getId().equals(branch.getId())) {
				paymentService.preActivatePaymentForInterBranch(accountPaymentList, lifeProposal.getCustomerId(), branch, agentCommissionList, false, currencyID,
						lifeProposal.getSalePoint(), lifeProposal.getBranch());

			} else {
				paymentService.preActivatePayment(accountPaymentList, lifeProposal.getCustomerId(), branch, agentCommissionList, false, currencyID, lifeProposal.getSalePoint());
			}

			/* workflow */
			lifeProposalDAO.updateStatus(status, lifeProposal.getPortalId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentGroupLifeProposal(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch, String status) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			if (!EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy())) {
				if (recalculatePremium(PAYMENT)) {
					calculatePremium(lifeProposal);
					lifeProposalDAO.update(lifeProposal);
				}
			}

			List<LifePolicy> policyList = lifePolicyService.activateLifePolicy(lifeProposal.getId());
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

			paymentService.preActivatePayment(accountPaymentList, lifeProposal.getCustomerId(), branch, agentCommissionList, false, currencyCode, lifeProposal.getSalePoint());
			lifeProposalDAO.updateStatus(status, lifeProposal.getPortalId());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String status) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			String currencyCode = product.getCurrency().getId();
			boolean isPersonalAccident = KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product);
			boolean isFarmer = KeyFactorChecker.isFarmer(product);
			boolean isShortTermSinglePremium = KeyFactorChecker.isPublicTermLife(product);
			boolean isSingelPremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
			boolean isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
			boolean isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
			boolean isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
			boolean isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
			boolean isSimpleLife = KeyFactorChecker.isSimpleLife(product);
			PolicyReferenceType referenceType = isFarmer ? PolicyReferenceType.FARMER_POLICY
					: isShortTermSinglePremium ? PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY
							: isSingelPremiumEndowmentLife ? PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY
									: isSinglePremiumCreditLife ? PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY
											: isShortTermSinglePremiumCreditLife ? PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY
													: isPersonalAccident ? PolicyReferenceType.PA_POLICY
															: isSimpleLife ? PolicyReferenceType.SIMPLE_LIFE_POLICY
																	: isShortEndowLife ? PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY
																			: isStudentLife ? PolicyReferenceType.STUDENT_LIFE_POLICY : PolicyReferenceType.LIFE_POLICY;
			double commissionPercent = product.getFirstCommission();
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			List<LifePolicy> policyList = lifePolicyService.activateLifePolicy(lifeProposal.getId());
			/* get Receipt No. */
			String receiptNo = paymentList.get(0).getReceiptNo();
			double rate = paymentList.get(0).getRate();

			List<AgentCommission> agentCommissionList = null;
			/* get agent commission of each policy */
			if (lifeProposal.getAgent() != null) {
				double firstAgentCommission = 0.0;
				agentCommissionList = new ArrayList<AgentCommission>();
				for (LifePolicy lifePolicy : policyList) {
					firstAgentCommission = lifePolicy.getAgentCommission();
					agentCommissionList.add(new AgentCommission(lifePolicy.getId(), referenceType, lifePolicy.getAgent(), firstAgentCommission, new Date(), receiptNo,
							lifePolicy.getTotalTermPremium(), commissionPercent, AgentCommissionEntryType.UNDERWRITING, rate, (rate * firstAgentCommission), currencyCode,
							(rate * lifePolicy.getTotalTermPremium())));
				}
			}

			List<StaffAgentCommission> staffagentCommissionList = null;
			/* get staff agent commission of each policy */
			if (lifeProposal.isStaffPlan()) {
				double firststaffAgentCommission = 0.0;
				staffagentCommissionList = new ArrayList<StaffAgentCommission>();
				for (LifePolicy lifePolicy : policyList) {
					firststaffAgentCommission = lifePolicy.getEips().getAmount();
					staffagentCommissionList.add(new StaffAgentCommission(lifePolicy.getId(), referenceType, lifePolicy.getEips().getStaff(), firststaffAgentCommission, new Date(),
							receiptNo, lifePolicy.getTotalTermPremium(), lifePolicy.getEips().getPercentage(), AgentCommissionEntryType.UNDERWRITING, rate,
							(rate * firststaffAgentCommission), currencyCode, (rate * lifePolicy.getTotalTermPremium())));
				}
			}

			/* add agent commission, activate TLF and Payment flag */
			// TODO FIX ME PSH add salepoint
			paymentService.activatePaymentAndTLF(paymentList, agentCommissionList, branch, currencyCode, null);
			/* add staff agent commission */
			if (null != staffagentCommissionList) {
				paymentService.addStaffAgentCommission(staffagentCommissionList);
			}

			/* update ActivePolicy Count in CustomerTable */
			if (lifeProposal.getCustomer() != null) {
				int activePolicyCount = lifeProposal.getCustomer().getActivePolicy() + policyList.size();
				customerDAO.updateActivePolicy(activePolicyCount, lifeProposal.getCustomerId());
				if (lifeProposal.getCustomer().getActivedDate() == null) {
					customerDAO.updateActivedPolicyDate(new Date(), lifeProposal.getCustomerId());
				}
			}
			if (lifeProposal.getOrganization() != null) {
				int activePolicyCount = lifeProposal.getOrganization().getActivePolicy() + policyList.size();
				organizationDAO.updateActivePolicy(activePolicyCount, lifeProposal.getCustomerId());
				if (lifeProposal.getOrganization().getActivedDate() == null) {
					organizationDAO.updateActivedPolicyDate(new Date(), lifeProposal.getCustomerId());
				}
			}

			/* Entry to LifePolicyTimeLineLog (Underwriting) */
			if (lifeProposal.getProposalType().equals(ProposalType.UNDERWRITING)) {
				for (LifePolicy lifePolicy : policyList) {
					LifePolicyTimeLineLog timeLineLog = new LifePolicyTimeLineLog(lifePolicy.getPolicyNo(), lifePolicy.getActivedPolicyStartDate(),
							lifePolicy.getActivedPolicyEndDate(), lifePolicy.getCommenmanceDate());
					lifePolicyTimeLineLogService.addLifePolicyTimeLineLog(timeLineLog);

					LifePolicyIdLog idLog = new LifePolicyIdLog(lifePolicy.getId(), null, lifePolicy.getLifeProposal().getId(), ProposalType.UNDERWRITING, timeLineLog);
					lifePolicyTimeLineLogService.addLifePolicyIdLog(idLog);
				}
			}

			/* workflow */
			lifeProposalDAO.updateStatus(RequestStatus.FINISHED.name(), lifeProposal.getId());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal updateMigrateLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO) {
		try {
			LifeProposal lProposal = lifeProposal;
			calculatePremium(lifeProposal);
			LifePolicyCriteria lifePolicyCriteria = new LifePolicyCriteria();
			lifePolicyCriteria.setCriteriaValue(lifeProposal.getLifePolicy().getPolicyNo());
			lifePolicyCriteria.setLifePolicyCriteriaItems(LifePolicyCriteriaItems.POLICY_NO);
			List<LifeEndowmentPolicySearch> lifeEndowmentPolicySearch = lifePolicyService.findPublicLifePolicyBycriteria(lifePolicyCriteria, false);
			if (lifeProposal.getProposalInsuredPersonList().get(0).getBasicTermPremium() <= lifeEndowmentPolicySearch.get(0).getPaidPremium()) {
				lifeProposalHistoryService.addNewLifeProposalHistory(lProposal);
				workFlowDTOService.updateWorkFlow(workFlowDTO);
				String insPersonCodeNo = null;
				String inPersonGroupCodeNo = null;
				String beneficiaryNo = null;
				Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
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
				if (lifeProposal.getLifePolicy() != null && lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT)) {
					org.ace.insurance.life.endorsement.LifeEndorseInfo info = null;
					info = lifeEndorsementService.updateLifeEndorseInfo(lifeProposal);
					setEndorsementPremium(info, lifeProposal);
				}

				// Underwriting
				lifeProposalDAO.update(lifeProposal);
				return lifeProposal;
			} else {
				return null;
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal updateLifeProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, BancaassuranceProposal bancaassuranceProposal) {
		try {
			Customer customer = lifeProposal.getCustomer();
			if (customer != null) {
				if (customer.getId() == null) {
					CustomerInfoStatus status = new CustomerInfoStatus();
					status.setStatusName(CustomerStatus.CONTRACTOR);
					customer.addCustomerInfoStatus(status);
					customer.setFullIdNo();
					customerService.addNewCustomer(customer);
					lifeProposal.setCustomer(customer);
				} else {
					/** Exiting Customer **/
					CustomerInfoStatus status = new CustomerInfoStatus();
					status.setStatusName(CustomerStatus.CONTRACTOR);
					customer.addCustomerInfoStatus(status);
					customer = customerService.updateCustomer(customer);
				}
			}

			// Other SaleChannel to Bancaassurance
			if (lifeProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				BancaassuranceProposal oldProposal = bancaassuranceProposal.getId() == null ? null : bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
				if (null != oldProposal) {
					bancaassuraceProposalService.update(oldProposal);
					bancaassuranceProposalHistoryService.addNewBancaassuranceProposalHistory(oldProposal);

				} else {
					bancaassuranceProposal.setLifeProposal(lifeProposal);
					bancaassuraceProposalService.insert(bancaassuranceProposal);
				}
			} else {
				if (null != bancaassuranceProposal && null != bancaassuranceProposal.getId()) {
					BancaassuranceProposal oldProposal = bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
					String lifeProposalId = bancaassuranceProposal.getLifeProposal().getId();
					if (null != oldProposal) {
						bancaassuraceProposalService.CreateHistoryAndRemoveBancaassuranceProposalByLifeproposalId(lifeProposalId);

					}
				}

			}

			// if(null != bancaassuranceProposal) {
			// BancaassuranceProposal oldBancaassuranceProposal =
			// bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
			// if(null != oldBancaassuranceProposal) {
			// bancaassuranceProposalHistoryService.addNewBancaassuranceProposalHistory(oldBancaassuranceProposal);
			// }
			// }

			calculatePremium(lifeProposal);
			if (null != workFlowDTO) {
				workFlowDTOService.updateWorkFlow(workFlowDTO);
			}
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
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
			if (lifeProposal.getLifePolicy() != null && lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT)) {
				org.ace.insurance.life.endorsement.LifeEndorseInfo info = null;
				info = lifeEndorsementService.updateLifeEndorseInfo(lifeProposal);
				setEndorsementPremium(info, lifeProposal);
			}

			// Underwriting
			lifeProposalDAO.update(lifeProposal);
			if (null != bancaassuranceProposal && null != lifeProposal.getSaleChannelType() && lifeProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				bancaassuranceProposalDAO.update(bancaassuranceProposal);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void issueLifeProposal(LifeProposal lifeProposal) {
		try {
			workFlowDTOService.deleteWorkFlowByRefNo(lifeProposal.getId());
			lifeProposalDAO.updateCompleteStatus(true, lifeProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to issue a LifeProposal.", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal overWriteLifeProposal(LifeProposal lifeProposal, BancaassuranceProposal bancaassuranceProposal) {
		try {
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			lifeProposalHistoryService.addNewLifeProposalHistory(lifeProposalDAO.findById(lifeProposal.getId()));

			if (lifeProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				BancaassuranceProposal oldProposal = bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
				if (null != oldProposal) {
					bancaassuraceProposalService.update(oldProposal);
				} else {
					bancaassuranceProposal.setLifeProposal(lifeProposal);
					bancaassuraceProposalService.insert(bancaassuranceProposal);
				}
			} else {
				// CreateHistory And Remove BancaassuranceProposal
				if (null != bancaassuranceProposal && null != bancaassuranceProposal.getId()) {
					BancaassuranceProposal oldProposal = bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
					String lifeProposalId = bancaassuranceProposal.getLifeProposal().getId();
					if (null != oldProposal) {
						bancaassuraceProposalService.CreateHistoryAndRemoveBancaassuranceProposalByLifeproposalId(lifeProposalId);

					}
				}

			}

			// if(null != bancaassuranceProposal) {
			// BancaassuranceProposal oldProposal=
			// bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
			// if(null != oldProposal) {
			// bancaassuranceProposalHistoryService.addNewBancaassuranceProposalHistory(oldProposal);
			// }
			//
			// }

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
			lifeProposalDAO.update(lifeProposal);
			if (null != bancaassuranceProposal) {
				bancaassuranceProposalDAO.update(bancaassuranceProposal);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteLifeProposal(LifeProposal lifeProposal) {
		try {
			lifeProposalDAO.delete(lifeProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a LifeProposal", e);
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
	public LifeProposal findLifePortalById(String id) {
		LifeProposal result = null;
		try {
			result = lifeProposalDAO.findLifePortalById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal Portal (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findLifeProposal(List<String> proposalIdList) {
		List<LifeProposal> result = null;
		try {
			result = lifeProposalDAO.findByIdList(proposalIdList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifeProposal.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findByDate(Date startDate, Date endDate) {
		List<LifeProposal> result = null;
		try {
			result = lifeProposalDAO.findByDate(startDate, endDate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal (ID : " + e);
		}
		return result;
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
			lifeProposalDAO.update(lifeSurvey.getLifeProposal());
			lifeProposalDAO.addAttachment(lifeSurvey.getLifeProposal());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Survey", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findAllLifeProposal() {
		List<LifeProposal> result = null;
		try {
			result = lifeProposalDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of LifeProposal)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal updateLifeProposal(LifeProposal lifeProposal) {
		try {
			// lifeProposalHistoryService.addNewLifeProposalHistory(lifeProposal);
			calculatePremium(lifeProposal);
			setIDPrefixForUpdate(lifeProposal);
			Customer customer = lifeProposal.getCustomer();
			if (customer != null) {
				if (customer.getId() == null) {
					CustomerInfoStatus status = new CustomerInfoStatus();
					status.setStatusName(CustomerStatus.CONTRACTOR);
					customer.addCustomerInfoStatus(status);
					customerService.addNewCustomer(customer);
					lifeProposal.setCustomer(customer);
				} else {
					/** Exiting Customer **/
					CustomerInfoStatus status = new CustomerInfoStatus();
					status.setStatusName(CustomerStatus.CONTRACTOR);
					customer.addCustomerInfoStatus(status);
					customer = customerService.updateCustomer(customer);
				}
			}
			lifeProposalDAO.update(lifeProposal);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeProposal", e);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LPL001> findLifeProposalByEnquiryCriteria(EnquiryCriteria criteria, List<Product> productList) {
		List<LPL001> result = null;
		try {
			result = lifeProposalDAO.findByEnquiryCriteria(criteria, productList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifeProposal by Enquiry Criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LPL001> findLifeProposalPortalByEnquiryCriteria(EnquiryCriteria criteria) {
		List<LPL001> result = null;
		try {
			result = lifeProposalDAO.findByPortalEnquiryCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of LifeProposal)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ProposalInsuredPerson findProposalInsuredPersonById(String id) {
		ProposalInsuredPerson result = null;
		try {
			result = lifeProposalDAO.findProposalInsuredPersonById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a ProposalInsuredPerson (ID : " + id + ")", e);
		}
		return result;
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
			// interest = ((oneYearPremium + interest) * 6.25) / 100;
			// interest = interest * passedMonth / 12;
			interest = ((interest + (oneMonthPremium * passedMonth)) * 6.25) / 100;
			// for year
			interest = interest * passedMonth / 12;
			totalInterest = totalInterest + interest;
		}
		return totalInterest;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeSurvey findSurveyByProposalId(String proposalId) {
		LifeSurvey result = null;
		try {
			result = lifeProposalDAO.findSurveyByProposalId(proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Proposal Survey Date)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePayment(LifePolicy lifePolicy, WorkFlowDTO workFlowDTO) {
		try {

			workFlowDTOService.updateWorkFlow(workFlowDTO);
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			Product product = lifePolicy.getLifeProposal().getProposalInsuredPersonList().get(0).getProduct();
			// calculatePremium(lifeProposal);
			// Custom ID GEN For Proposal Insured Person and Beneficiary
			for (ProposalInsuredPerson person : lifePolicy.getLifeProposal().getProposalInsuredPersonList()) {
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
			setIDPrefixForUpdate(lifePolicy.getLifeProposal());
			lifePolicy.getLifeProposal().setSkipPaymentTLF(false);
			lifeProposalDAO.update(lifePolicy.getLifeProposal());
			lifePolicyService.deleteLifePolicy(lifePolicy);
			List<Payment> paymentList = paymentService.findByPolicy(lifePolicy.getId());
			paymentService.deletePayments(paymentList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete payment.", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PolicyInsuredPerson> findPolicyInsuredPersonByParameters(Name name, String idNo, String fatherName, Date dob) {
		List<PolicyInsuredPerson> results = null;
		try {
			results = lifeProposalDAO.findPolicyInsuredPersonByParameters(name, idNo, fatherName, dob);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PolicyInsuredPerson.", e);
		}

		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findStatusById(String id) {
		String result = null;
		try {
			result = lifeProposalDAO.findStatusById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePortalStatus(String status, String id) {
		try {
			lifeProposalDAO.updateStatus(status, id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Status (Status : " + status + ")", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentLifeProposalTransfer(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch) {
		try {
			// String currencyCode = CurrencyUtils.getCurrencyCode(null);
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
	public LifeProposal findLifeProposalByProposalNo(String proposalNo) {
		LifeProposal result = null;
		try {
			result = lifeProposalDAO.findByProposalNo(proposalNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeProposal (No : " + proposalNo + ")", e);
		}
		return result;
	}

	// insuredPersonCodeNo migratiion
	// delete after migration
	public void updateCodeNo(List<ProposalInsuredPerson> proposalPersonList, List<PolicyInsuredPerson> policyPersonList, List<InsuredPersonBeneficiaries> proposalBeneList,
			List<PolicyInsuredPersonBeneficiaries> policyBeneList, List<IDGen> idGenList) {

		// update proposal insured person
		lifeProposalDAO.updateProposalPersonCode(proposalPersonList);

		// update policy insured person
		lifePolicyDAO.updatePolicyPersonCode(policyPersonList);

		// update proposal beneficiary person
		lifeProposalDAO.updateProposalBeneficiaryCode(proposalBeneList);

		// update policy beneficiary person
		lifePolicyDAO.updatePolicyBeneficiaryCode(policyBeneList);

		// update id gen
		for (IDGen idGen : idGenList) {
			customIDGenerator.updateIDGen(idGen);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentPAProposalTransfer(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (Payment payment : paymentList) {
				accountPaymentList.add(new AccountPayment(payment.getAccountBank().getAcode(), payment));
			}
			paymentService.activatePaymentTransfer(accountPaymentList, lifeProposal.getCustomerId(), branch, false, currencyCode, lifeProposal.getSalePoint());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a PA Proposal ID : " + lifeProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmMigrateShortTermProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, Object object,
			boolean isSkipTLFPayment) {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			if (paymentDTO.getConfirmDate() == null) {
				paymentDTO.setConfirmDate(new Date());
			}
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			PolicyReferenceType referenceType = PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY;
			if (!isSkipTLFPayment)
				workFlowDTOService.updateWorkFlow(workFlowDTO);
			if (recalculatePremium(CONFIRMATION))
				calculatePremium(lifeProposal);
			// reset insured person start/end date
			for (ProposalInsuredPerson proposalInsPerson : lifeProposal.getProposalInsuredPersonList()) {
				proposalInsPerson.setStartDate(paymentDTO.getConfirmDate());
				Calendar cal = Calendar.getInstance();
				cal.setTime(proposalInsPerson.getStartDate());
				cal.add(Calendar.MONTH, proposalInsPerson.getPeriodMonth());
				proposalInsPerson.setEndDate(cal.getTime());
			}

			LPC001 oldLifePolicyNoAndId = lifePolicyService.findLifePolicyNoAndIdById(lifeProposal.getLifePolicy().getId());
			// create LifePolicy
			LifePolicy lifePolicy = new LifePolicy(lifeProposal);
			// recalculate payment term and term premium
			// calculate ExtraAmount
			double totalPaidPremium = 0.0;
			int totalPaidDay = 0;
			double extraAmount = 0.0;
			boolean isMigrate = false;
			for (PolicyInsuredPerson person : lifePolicy.getPolicyInsuredPersonList()) {
				LifePolicyCriteria criteria = new LifePolicyCriteria();
				criteria.setCriteriaValue(oldLifePolicyNoAndId.getPolicyNo());
				criteria.setLifePolicyCriteriaItems(LifePolicyCriteriaItems.POLICY_NO);
				List<LifeEndowmentPolicySearch> policyCriteriaList = lifePolicyService.findPublicLifePolicyBycriteria(criteria, isMigrate);
				for (LifeEndowmentPolicySearch policySearch : policyCriteriaList) {
					totalPaidPremium = policySearch.getPaidPremium();
				}
				totalPaidDay = (int) Utils.divideNoDecimal(totalPaidPremium, person.getBasicTermPremium());
				totalPaidDay = (totalPaidDay > person.getPeriodMonth()) ? person.getPeriodMonth() : totalPaidDay;
				extraAmount = totalPaidPremium - (person.getBasicTermPremium() * totalPaidDay);
			}
			// current rate for HomeAmount
			double rate = 1.0;
			if (CurrencyUtils.isUSD(product.getCurrency())) {
				rate = paymentDAO.findActiveRate();
			}
			if (extraAmount > 0) {
				lifePolicy.setHasExtraValue(true);
			}
			lifePolicyService.addNewLifePolicy(lifePolicy);
			Payment payment;
			for (int i = 0; i < totalPaidDay; i++) {
				payment = new Payment();
				payment.setPaymentType(lifePolicy.getPaymentType());
				payment.setBank(paymentDTO.getBank());
				payment.setBankAccountNo(paymentDTO.getBankAccountNo());
				payment.setChequeNo(paymentDTO.getChequeNo());
				payment.setPaymentChannel(paymentDTO.getPaymentChannel());
				payment.setReferenceType(referenceType);
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
						payment.setHomeBankCharges((rate * (paymentDTO.getBankWallet().getCharges() * lifePolicy.getTotalBasicTermPremium() / 100))
								+ paymentDTO.getBankWallet().getAdditionalCharges());
					}
				}

				payment.setDiscountPercent(paymentDTO.getDiscountPercent());
				payment.setReceivedDeno(paymentDTO.getReceivedDeno());
				payment.setRefundDeno(paymentDTO.getRefundDeno());
				payment.setConfirmDate(paymentDTO.getConfirmDate());
				payment.setPoNo(paymentDTO.getPoNo());
				payment.setAccountBank(paymentDTO.getAccountBank());
				payment.setBasicPremium(lifePolicy.getTotalBasicTermPremium());
				payment.setAddOnPremium(lifePolicy.getTotalAddOnTermPremium());
				payment.setCur(CurrencyUtils.getCurrencyCode(product.getCurrency()));
				payment.setRate(rate);
				payment.setAmount(payment.getTotalAmount());
				payment.setHomeAmount(rate * payment.getTotalAmount());
				payment.setHomePremium(rate * payment.getBasicPremium());
				payment.setHomeAddOnPremium(rate * payment.getAddOnPremium());
				payment.setHomeStampFees(rate * paymentDTO.getStampFees());
				payment.setHomeServicesCharges(rate * paymentDTO.getServicesCharges());
				payment.setHomePenaltyPremium(rate * paymentDTO.getPenaltyPremium());
				payment.setPaymentDate(new Date());
				// calculate payment term and term premium for discount
				if (paymentDTO.getDiscountPercent() > 0.0) {
					for (PolicyInsuredPerson person : lifePolicy.getPolicyInsuredPersonList()) {
						calculateDiscount(person, paymentDTO.getDiscountPercent());
					}
				}
				payment.setReferenceNo(lifePolicy.getId());
				paymentList.add(payment);
			}

			// insert New ShortEndowmentExtraValue
			String lifepolicyID = lifePolicyService.findLifePolicyIdByLifeProposalId(lifeProposal.getId());
			ShortEndowmentExtraValue shortEndowmentExtraValue = new ShortEndowmentExtraValue();
			shortEndowmentExtraValue.setEndowmentPolicyNo(oldLifePolicyNoAndId.getPolicyNo());
			shortEndowmentExtraValue.setReferenceNo(lifepolicyID);
			shortEndowmentExtraValue.setExtraAmount(extraAmount);
			shortEndowmentExtraValue.setPaid(false);
			shortEndowmentExtraValue.setPrefix(getPrefix(ShortEndowmentExtraValue.class));
			shortEndowmentExtraValueDAO.insert(shortEndowmentExtraValue);

			lifeProposalDAO.update(lifeProposal);
			paymentList = paymentService.prePayment(paymentList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm a LifeProposal", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentMigrateShortTermProposal(LifeProposal lifeProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String name) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			List<LifePolicy> policyList = lifePolicyService.activateLifePolicy(lifeProposal.getId());
			int term = 1;
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			for (Payment payment : paymentList) {
				payment.setFromTerm(term);
				payment.setToTerm(term);
				payment.setComplete(true);
				payment.setPaymentDate(cal.getTime());
				paymentDAO.update(payment);
				cal.add(Calendar.MONTH, lifeProposal.getPaymentType().getMonth());
				term++;
			}

			shortEndowmentExtraValueDAO.updateShortTermPolicyNo(policyList.get(0).getId(), policyList.get(0).getPolicyNo());
			/* update ActivePolicy Count in CustomerTable */
			if (lifeProposal.getCustomer() != null) {
				int activePolicyCount = lifeProposal.getCustomer().getActivePolicy() + policyList.size();
				customerDAO.updateActivePolicy(activePolicyCount, lifeProposal.getCustomerId());
				if (lifeProposal.getCustomer().getActivedDate() == null) {
					customerDAO.updateActivedPolicyDate(new Date(), lifeProposal.getCustomerId());
				}
			}
			if (lifeProposal.getOrganization() != null) {
				int activePolicyCount = lifeProposal.getOrganization().getActivePolicy() + policyList.size();
				organizationDAO.updateActivePolicy(activePolicyCount, lifeProposal.getCustomerId());
				if (lifeProposal.getOrganization().getActivedDate() == null) {
					organizationDAO.updateActivedPolicyDate(new Date(), lifeProposal.getCustomerId());
				}
			}

			/* Entry to LifePolicyTimeLineLog (Underwriting) */
			if (lifeProposal.getProposalType().equals(ProposalType.UNDERWRITING)) {
				for (LifePolicy lifePolicy : policyList) {
					LifePolicyTimeLineLog timeLineLog = new LifePolicyTimeLineLog(lifePolicy.getPolicyNo(), lifePolicy.getActivedPolicyStartDate(),
							lifePolicy.getActivedPolicyEndDate(), lifePolicy.getCommenmanceDate());
					lifePolicyTimeLineLogService.addLifePolicyTimeLineLog(timeLineLog);

					LifePolicyIdLog idLog = new LifePolicyIdLog(lifePolicy.getId(), null, lifePolicy.getLifeProposal().getId(), ProposalType.UNDERWRITING, timeLineLog);
					lifePolicyTimeLineLogService.addLifePolicyIdLog(idLog);
				}
			}

			/* workflow */
			lifeProposalDAO.updateStatus(RequestStatus.FINISHED.name(), lifeProposal.getId());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findAllLifeProposalByGroupFarmerId(String groupFarmerId) {
		List<LifeProposal> result = null;
		try {
			result = lifeProposalDAO.findAllLifeProposalByFroupFarmerId(groupFarmerId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a LifeProposal ID : " + groupFarmerId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean findOverMaxSIByMotherNameAndNRCAndInsuNameAndNRC(LifeProposal lifeProposal, InsuredPersonInfoDTO insuredPersonInfoDTO) {
		boolean result = false;
		try {
			result = lifeProposalDAO.findOverMaxSIByMotherNameAndNRCAndInsuNameAndNRC(lifeProposal, insuredPersonInfoDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find OverMaxSI By MotherName And NRC And InsuName And NRC", e);
		}
		return result;
	}

}
