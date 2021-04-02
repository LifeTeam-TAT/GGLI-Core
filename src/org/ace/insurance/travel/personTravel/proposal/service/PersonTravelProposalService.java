package org.ace.insurance.travel.personTravel.proposal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.persistence.interfaces.IBancaassuranceProposalDAO;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.bancassurance.proposalhistory.service.interfaces.IBancaassuranceProposalHistoryService;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.city.persistence.interfaces.ICityDAO;
import org.ace.insurance.system.common.country.persistence.interfaces.ICountryDAO;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.insurance.system.common.township.persistence.interfaces.ITownshipDAO;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalKeyfactorValue;
import org.ace.insurance.travel.personTravel.proposal.ProposalTraveller;
import org.ace.insurance.travel.personTravel.proposal.persistence.interfaces.IPersonTravelProposalDAO;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "PersonTravelProposalService")
public class PersonTravelProposalService extends BaseService implements IPersonTravelProposalService {
	@Resource(name = "PersonTravelProposalDAO")
	IPersonTravelProposalDAO personTravelProposalDAO;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "WorkFlowService")
	public IWorkFlowService workFlowService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PersonTravelPolicyService")
	private IPersonTravelPolicyService personTravelPolicyService;

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "OrganizationDAO")
	private IOrganizationDAO organizationDAO;

	@Resource(name = "TownshipDAO")
	private ITownshipDAO townshipDAO;

	@Resource(name = "CityDAO")
	private ICityDAO cityDAO;

	@Resource(name = "CountryDAO")
	private ICountryDAO countryDAO;

	@Resource(name = "BancaassuranceProposalDAO")
	private IBancaassuranceProposalDAO bancaassuranceProposalDAO;

	@Resource(name = "BancaassuranceProposalService")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	@Resource(name = "BancaassurancePolicyService")
	private IBancaassurancePolicyService bancaassuracePolicyService;

	@Resource(name = "BancaassuranceProposalHistoryService")
	private IBancaassuranceProposalHistoryService bancaassuranceProposalHistoryService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, String status, BancaassuranceProposal bancaassuranceProposal) {
		try {

			String productCode = personTravelProposal.getProduct().getProductGroup().getProposalNoPrefix();
			// TODO FIX PSH

			Product product = personTravelProposal.getProduct();

			String travelProposalGenerateItem = null;
			if (ProductIDConfig.isForeignTravelInsurance(product) || ProductIDConfig.isLocalTravelInsurance(product)) {
				travelProposalGenerateItem = SystemConstants.GENERAL_TRAVEL_PROPOSAL_NO;
			} else if (ProductIDConfig.isUnder100MileTravelInsurance(product)) {
				travelProposalGenerateItem = SystemConstants.TRAVEL_UNDER_100_MILES_PROPOSAL_NO;
			}
			String proposalNo = customIDGenerator.getNextId(travelProposalGenerateItem, productCode, true);
			personTravelProposal.setProposalNo(proposalNo);
			calculatePremium(personTravelProposal);
			personTravelProposalDAO.insert(personTravelProposal);
			if (personTravelProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				bancaassuranceProposal.setPersonTravelProposal(personTravelProposal);
				bancaassuraceProposalService.insert(bancaassuranceProposal);
			}
			workFlowDTO.setReferenceNo(personTravelProposal.getId());
			workFlowService.addNewWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new PersonTravelProposal", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePersonTravelProposal(PersonTravelProposal personTravelProposal, BancaassuranceProposal bancaassuranceProposal) {
		try {
			calculatePremium(personTravelProposal);

			// To Change Bancaassurance from Other SaleChannel
			if (personTravelProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				BancaassuranceProposal oldProposal = bancaassuranceProposal.getId() == null ? null : bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
				if (null != oldProposal) {
					bancaassuraceProposalService.update(oldProposal);
				} else {
					bancaassuranceProposal.setPersonTravelProposal(personTravelProposal);
					bancaassuraceProposalService.insert(bancaassuranceProposal);
				}
			} else {
				if (null != bancaassuranceProposal && null != bancaassuranceProposal.getId()) {
					BancaassuranceProposal oldProposal = bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
					String personTravelProposalId = bancaassuranceProposal.getPersonTravelProposal().getId();
					if (null != oldProposal) {
						bancaassuraceProposalService.CreateHistoryAndRemoveBancaassuranceProposalByPersonTravelproposalId(personTravelProposalId);

					}
				}

			}

			personTravelProposalDAO.update(personTravelProposal);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update PersonTravelProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePersonTravelProposal(PersonTravelProposal personTravelProposal) {
		try {
			personTravelProposalDAO.delete(personTravelProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete PersonTravelProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelProposal> findAllPersonTravelProposal() {
		List<PersonTravelProposal> result = null;
		try {
			result = personTravelProposalDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of PersonTravelProposal", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelProposal findPersonTravelProposalById(String id) {
		PersonTravelProposal result = null;
		try {
			result = personTravelProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelProposal by Id (id = " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void calculatePremium(PersonTravelProposal travelProposal) {
		double totalBasicPremium = 0.0;
		Double premium = 0.0;
		Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
		if (!travelProposal.getPersonTravelInfo().getTravelProposalKeyfactorValueList().isEmpty()) {
			for (PersonTravelProposalKeyfactorValue keyfactor : travelProposal.getPersonTravelInfo().getTravelProposalKeyfactorValueList()) {
				keyfatorValueMap.put(keyfactor.getKeyfactor(), keyfactor.getValue());
			}
			premium = productService.findProductPremiumRate(keyfatorValueMap, travelProposal.getProduct(), null);
			if (travelProposal.getPersonTravelInfo().getProposalTravellerList().size() > 0) {
				for (ProposalTraveller traveller : travelProposal.getPersonTravelInfo().getProposalTravellerList()) {
					totalBasicPremium = traveller.getUnit() * premium;
					traveller.setBasicTermPremium(totalBasicPremium);
					traveller.setPremium(totalBasicPremium);
				}
			}
			totalBasicPremium = travelProposal.getPersonTravelInfo().getTotalUnit() * premium;
		} else {
			premium = (double) (travelProposal.getProduct().getFixedValue());
			totalBasicPremium = travelProposal.getPersonTravelInfo().getNoOfPassenger() * premium;
			for (ProposalTraveller traveller : travelProposal.getPersonTravelInfo().getProposalTravellerList()) {

				traveller.setBasicTermPremium(premium);
				traveller.setPremium(premium);
			}
		}

		travelProposal.getPersonTravelInfo().setPremium(totalBasicPremium);
		travelProposal.getPersonTravelInfo().setPremiumRate(premium);
		travelProposal.getPersonTravelInfo().setPaymentTerm(1);
		travelProposal.getPersonTravelInfo().setBasicTermPremium(totalBasicPremium);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String status) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			PersonTravelPolicy policy = personTravelPolicyService.activatePersonTravelPolicy(personTravelProposal.getId());
			/* get Receipt No. */
			String receiptNo = null;
			if (paymentList != null && !paymentList.isEmpty()) {
				receiptNo = paymentList.get(0).getReceiptNo();
			}
			List<AgentCommission> agentCommissionList = null;
			/* get agent commission of each policy */
			if (personTravelProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				agentCommissionList.add(new AgentCommission(policy.getId(), PolicyReferenceType.PERSON_TRAVEL_POLICY, policy.getAgent(), policy.getAgentCommission(), new Date(),
						receiptNo, policy.getTotalBasicTermPremium(), policy.getProduct().getFirstCommission(), AgentCommissionEntryType.UNDERWRITING, 1.0,
						policy.getAgentCommission(), policy.getProduct().getCurrency().getCurrencyCode(), policy.getTotalBasicTermPremium()));
			}
			/* add agent commission, activate TLF and Payment flag */
			// TODO FIXME PSH add salepoint
			paymentService.activatePaymentAndTLF(paymentList, agentCommissionList, branch, currencyCode, null);

			/* update ActivePolicy Count in CustomerTable */
			if (personTravelProposal.getCustomer() != null) {
				int activePolicyCount = personTravelProposal.getCustomer().getActivePolicy();
				customerDAO.updateActivePolicy(activePolicyCount, personTravelProposal.getCustomerId());
				if (personTravelProposal.getCustomer().getActivedDate() == null) {
					customerDAO.updateActivedPolicyDate(new Date(), personTravelProposal.getCustomerId());
				}
			}
			if (personTravelProposal.getOrganization() != null) {
				int activePolicyCount = personTravelProposal.getOrganization().getActivePolicy();
				organizationDAO.updateActivePolicy(activePolicyCount, personTravelProposal.getCustomerId());
				if (personTravelProposal.getOrganization().getActivedDate() == null) {
					organizationDAO.updateActivedPolicyDate(new Date(), personTravelProposal.getCustomerId());
				}
			}

			/* workflow */
			personTravelProposalDAO.updateStatus(RequestStatus.FINISHED.name(), personTravelProposal.getId());
			//////////////////////
			// String receiptNo = null;
			// String currencyCode =
			// CurrencyUtils.getCurrencyCode(personTravelProposal.getProduct().getCurrency());
			// workFlowDTOService.updateWorkFlow(workFlowDTO);
			// if (paymentList != null && !paymentList.isEmpty()) {
			// receiptNo = paymentList.get(0).getReceiptNo();
			// }
			// double rate = paymentList.get(0).getRate();
			// List<AgentCommission> agentCommissionList = null;
			// /* get agent commission of each policy */
			// if (personTravelProposal.getAgent() != null) {
			// agentCommissionList = new ArrayList<AgentCommission>();
			// double commissionPercent =
			// personTravelProposal.getProduct().getFirstCommission();
			// agentCommissionList.add(new
			// AgentCommission(personTravelProposal.getId(),
			// PolicyReferenceType.PERSON_TRAVEL_POLICY,
			// personTravelProposal.getAgent(), 0.0, new Date(), receiptNo,
			// personTravelProposal.getPersonTravelInfo().getTotalBasicTermPremium(),
			// commissionPercent, AgentCommissionEntryType.UNDERWRITING, rate,
			// rate * 0.0, currencyCode,
			// rate *
			// personTravelProposal.getPersonTravelInfo().getTotalBasicTermPremium()));
			// }
			// paymentService.activatePaymentAndTLF(paymentList,
			// agentCommissionList, branch, currencyCode);
			// workFlowDTOService.deleteWorkFlowByRefNo(personTravelProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a PersonTravelProposal ID : " + personTravelProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO) throws SystemException {
		try {
			workFlowService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reject Person Travel Proposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PTPL001> findPersonTravelDTOByCriteria(EnquiryCriteria criteria) {
		List<PTPL001> result = null;
		try {
			result = personTravelProposalDAO.findByEnquiryCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  PersonTravelProposal by Criteria ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch)
			throws SystemException {
		List<Payment> paymentList = new ArrayList<Payment>();
		double rate = 1.0;
		try {
			// create PersonTravelPolicy
			PersonTravelPolicy personTravelPolicy = new PersonTravelPolicy(personTravelProposal);

			Payment payment = new Payment();
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.PERSON_TRAVEL_POLICY);
			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());

			if (paymentDTO.getBankWallet() != null) {
				payment.setBankWallet(paymentDTO.getBankWallet());
				if (payment.getBankWallet().getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
					payment.setBankCharges(paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(rate * (paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges()));
				} else {
					payment.setBankCharges((paymentDTO.getBankWallet().getCharges() * personTravelProposal.getPersonTravelInfo().getPremium() / 100)
							+ paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges((rate * (paymentDTO.getBankWallet().getCharges() * personTravelProposal.getPersonTravelInfo().getPremium() / 100))
							+ paymentDTO.getBankWallet().getAdditionalCharges());
				}
			}
			payment.setDiscountPercent(paymentDTO.getDiscountPercent());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setBasicPremium(personTravelProposal.getPersonTravelInfo().getPremium());
			payment.setRate(rate);
			payment.setAmount(payment.getTotalAmount() * rate);
			payment.setHomeAmount(payment.getTotalAmount() * rate);
			payment.setHomePremium(payment.getBasicPremium() * rate);
			payment.setHomeAddOnPremium(payment.getAddOnPremium() * rate);
			payment.setHomeAmount(payment.getAmount() * rate);
			BancaassuranceProposal BancaProposal = bancaassuraceProposalService.findBancaassuranceProposalByPersonTravelproposalId(personTravelProposal.getId());
			if (BancaProposal != null) {
				BancaassurancePolicy bancaassurancePolicy = new BancaassurancePolicy(BancaProposal);
				bancaassurancePolicy.setPersonTravelPolicy(personTravelPolicy);
				bancaassuracePolicyService.insert(bancaassurancePolicy);
			}

			if (personTravelProposal.isStaffPlan()) {
				personTravelPolicy.setEips(personTravelProposal.getEips());
			}
			personTravelPolicyService.addNewPersonTravelPolicy(personTravelPolicy);
			payment.setReferenceNo(personTravelPolicy.getId());
			paymentList.add(payment);
			paymentList = paymentService.prePayment(paymentList);
			workFlowService.updateWorkFlow(workFlowDTO);

			// insert TLF
			prePaymentPersonTravelProposal(personTravelProposal, workFlowDTO, paymentList, branch);
			// Cheque / Payment Order
			if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE) || paymentDTO.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
				prePaymentPersonTravelProposalTransfer(personTravelProposal, paymentList, branch);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm Person Travel Proposal)", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			workFlowService.updateWorkFlow(workFlowDTO);
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			String accountCode = personTravelProposal.getProduct().getProductGroup().getAccountCode();
			PolicyReferenceType referenceType = paymentList.get(0).getReferenceType();
			List<AgentCommission> agentCommissionList = null;
			List<StaffAgentCommission> staffagentCommissionList = null;
			String currencyID = personTravelProposal.getProduct().getCurrency().getId();
			PersonTravelPolicy personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(personTravelProposal.getId());
			for (Payment payment : paymentList) {
				if (personTravelPolicy.getId().equals(payment.getReferenceNo())) {
					accountPaymentList.add(new AccountPayment(accountCode, payment));
					break;
				}
			}

			/* get agent commission */
			if (personTravelProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				double commissionPercent = personTravelProposal.getProduct().getFirstCommission();
				double agentCommission = 0;
				if (commissionPercent > 0) {
					agentCommission = Utils.getPercentOf(commissionPercent, personTravelProposal.getPersonTravelInfo().getPremium());
				}
				agentCommissionList.add(
						new AgentCommission(personTravelPolicy.getId(), PolicyReferenceType.PERSON_TRAVEL_POLICY, personTravelProposal.getAgent(), agentCommission, new Date()));
			}

			if (personTravelProposal.isStaffPlan()) {
				String eNo = accountPaymentList.get(0).getPayment().getId();
				Payment paymentByIndex = accountPaymentList.get(0).getPayment();
				staffagentCommissionList = new ArrayList<StaffAgentCommission>();
				staffagentCommissionList.add(new StaffAgentCommission(personTravelPolicy.getId(), referenceType, personTravelPolicy.getEips().getStaff(),
						personTravelPolicy.getEips().getAmount(), new Date()));

				if (!personTravelPolicy.getBranch().getId().equals(branch.getId())) {
					paymentService.addStaffCommissionTLF(staffagentCommissionList, personTravelPolicy.getBranch(), paymentByIndex, eNo, false, currencyID,
							personTravelProposal.getSalePoint());

				} else {
					paymentService.addStaffCommissionTLF(staffagentCommissionList, branch, paymentByIndex, eNo, false, currencyID, personTravelProposal.getSalePoint());
				}
			}

			// FOR INTER BRANCH
			if (!personTravelProposal.getBranch().getId().equals(branch.getId())) {
				paymentService.preActivatePaymentForInterBranch(accountPaymentList, personTravelProposal.getCustomerId(), branch, agentCommissionList, false, currencyCode,
						personTravelProposal.getSalePoint(), personTravelProposal.getBranch());

			} else {
				paymentService.preActivatePayment(accountPaymentList, personTravelProposal.getCustomerId(), branch, agentCommissionList, false, currencyCode,
						personTravelProposal.getSalePoint());
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a MotorProposal ID : " + personTravelProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentPersonTravelProposalTransfer(PersonTravelProposal personTravelProposal, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (Payment payment : paymentList) {
				accountPaymentList.add(new AccountPayment(payment.getAccountBank().getAcode(), payment));
			}
			paymentService.activatePaymentTransfer(accountPaymentList, personTravelProposal.getCustomerId(), branch, false, currencyCode, personTravelProposal.getSalePoint());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a MotorProposal ID : " + personTravelProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void issuePersonTravelProposal(PersonTravelProposal personTravelProposal) {
		try {
			workFlowDTOService.deleteWorkFlowByRefNo(personTravelProposal.getId());
			personTravelProposalDAO.updateCompleteStatus(true, personTravelProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to issue a Person Travel Proposal.", e);
		}
	}

}