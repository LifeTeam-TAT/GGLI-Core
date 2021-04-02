package org.ace.insurance.travel.personTravel.policy.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.PolicyChannel;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.mobile.travelProposal.MTP002;
import org.ace.insurance.mobile.travelProposal.MobileTravelProposal;
import org.ace.insurance.mobile.travelProposal.persistence.interfaces.IMobileTravelProposalDAO;
import org.ace.insurance.mobile.user.MobileUser;
import org.ace.insurance.mobile.user.persistence.interfaces.IMobileUserDAO;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.travel.personTravel.PersonTravelMonthlyIncomeReport;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.persistence.interfaces.IPersonTravelPolicyDAO;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.persistence.interfaces.IPersonTravelProposalDAO;
import org.ace.insurance.web.manage.report.travel.personTravel.criteria.PersonTravelCriteria;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "PersonTravelPolicyService")
public class PersonTravelPolicyService extends BaseService implements IPersonTravelPolicyService {
	@javax.annotation.Resource(name = "IDConfigLoader")
	private IDConfigLoader idConfigLoader;

	@Resource(name = "PersonTravelPolicyDAO")
	private IPersonTravelPolicyDAO personTravelPolicyDAO;

	@Resource(name = "PersonTravelProposalDAO")
	private IPersonTravelProposalDAO personTravelProposalDAO;

	@Resource(name = "MobileUserDAOTravel")
	private IMobileUserDAO mobileUserDAO;

	@Resource(name = "MobileTravelProposalDAO")
	private IMobileTravelProposalDAO mobileTravelProposalDAO;

	@Resource(name = "BranchService")
	private IBranchService branchService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "BankService")
	private IBankService bankService;

	@Resource(name = "CustomerService")
	private ICustomerService customerService;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewPersonTravelPolicy(PersonTravelPolicy personTravelPolicy) throws SystemException {
		try {
			personTravelPolicyDAO.insert(personTravelPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new PersonTravelPolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy findPersonTravelPolicyById(String id) throws SystemException {
		PersonTravelPolicy policy = null;
		try {
			policy = personTravelPolicyDAO.findPolicyById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelPolicy by ID : " + id, e);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy findPersonTravelPolicyByProposalId(String proposalId) throws SystemException {
		PersonTravelPolicy policy = null;
		try {
			policy = personTravelPolicyDAO.findPolicyByProposalId(proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelPolicy by Proposal ID : " + proposalId, e);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy activatePersonTravelPolicy(String ptProposalId) throws SystemException {
		PersonTravelPolicy personTravelPolicy = null;
		PersonTravelProposal personTravelProposal = null;
		String policyNo;
		try {
			personTravelPolicy = personTravelPolicyDAO.findPolicyByProposalId(ptProposalId);
			personTravelProposal = personTravelProposalDAO.findById(ptProposalId);
			String productCode = personTravelProposal.getProduct().getProductGroup().getPolicyNoPrefix();

			if (personTravelPolicy != null) {
				if (personTravelPolicy.getPolicyNo() == null) {
					Product product = personTravelProposal.getProduct();
					String travelProposalNo = null;
					if (ProductIDConfig.isForeignTravelInsurance(product) || ProductIDConfig.isLocalTravelInsurance(product)) {
						travelProposalNo = SystemConstants.GENERAL_TRAVEL_POLICY_NO;
					} else if (ProductIDConfig.isUnder100MileTravelInsurance(product)) {
						travelProposalNo = SystemConstants.TRAVEL_UNDER_100_MILES_POLICY_NO;
					}
					policyNo = customIDGenerator.getNextId(travelProposalNo, productCode, true);
				} else {
					policyNo = personTravelPolicy.getPolicyNo();
				}
				personTravelPolicy.setPolicyNo(policyNo);
				personTravelPolicyDAO.update(personTravelPolicy);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a PersonTravelPolicy", e);
		}
		return personTravelPolicy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPersonTravelPolicyByReceiptNoCriteria(ReceiptNoCriteria criteria, int max) throws SystemException {
		List<Payment> result = null;
		try {
			result = personTravelPolicyDAO.findByReceiptNoCriteria(criteria, max);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a PersonTravelPolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelMonthlyIncomeReport> findPersonTravelMonthlyIncome(PersonTravelCriteria criteria) throws SystemException {
		List<PersonTravelMonthlyIncomeReport> result = null;
		try {
			result = personTravelPolicyDAO.findPersonTravelMonthlyIncome(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravel Monthly Income Report by Criteria ");
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findPolicyIdByProposalId(String proposalId) throws SystemException {
		String policyId = null;
		try {
			policyId = personTravelPolicyDAO.findPolicyIdByProposalId(proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelPolicy ID by Proposal ID : " + proposalId, e);
		}
		return policyId;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void convertDataToCore(List<MTP002> selectedMobileTravelList, Date paymentDate) {
		try {
			String policyNo;
			String accountName;
			String acode;
			List<AgentCommission> agentCommissionList = null;
			List<Payment> paymentList = new ArrayList<Payment>();

			String branchCode = idConfigLoader.getBranchCode();
			Branch branch = branchService.findByBranchCode(branchCode);
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			String bankId = ProductIDConfig.getMobileAccuredBankId();
			Bank accountBank = bankService.findBankById(bankId);

			for (MTP002 mtp002 : selectedMobileTravelList) {
				List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
				MobileTravelProposal mobileTravelProposal = mobileTravelProposalDAO.findById(mtp002.getTravelProposalId());
				Customer customer = customerService.findCustomerByRefMobileUserId(mtp002.getMobileUserId());
				/* transfer mobileUser to Customer */
				if (customer == null) {
					MobileUser mobileUser = mobileUserDAO.findById(mtp002.getMobileUserId());
					customer = new Customer();
					customer.setReferenceMobileUserId(mobileUser.getId());
					customer.getName().setFirstName(mobileUser.getFirstName());
					customer.getName().setLastName(mobileUser.getLastName());
					customer.getContentInfo().setMobile(mobileUser.getMobileNumber());
					customer.getContentInfo().setEmail(mobileUser.getEmail());
					customerService.addNewCustomer(customer);
				}
				/* transfer mbTravelProposal to personTravelPolicy */
				String productCode = null;
				if (mobileTravelProposal != null) {

					// TODO FIX PSH
					policyNo = customIDGenerator.getNextId(SystemConstants.GENERAL_TRAVEL_POLICY_NO, productCode, true);
					PersonTravelPolicy personTravelPolicy = new PersonTravelPolicy(mobileTravelProposal);
					personTravelPolicy.setPolicyNo(policyNo);
					personTravelPolicy.setCustomer(customer);
					personTravelPolicy.setPolicyChannel(PolicyChannel.MOBILE);
					Product product = productService.findProductById(mobileTravelProposal.getProductId());
					personTravelPolicy.setProduct(product);
					personTravelPolicyDAO.insert(personTravelPolicy);
					/* insert payment */
					Payment payment = new Payment();
					payment.setReferenceNo(personTravelPolicy.getId());
					payment.setReferenceType(PolicyReferenceType.PERSON_TRAVEL_POLICY);
					double premium = personTravelPolicy.getPremium();
					payment.setBasicPremium(premium);
					payment.setHomePremium(premium);
					payment.setAmount(premium);
					payment.setHomeAmount(premium);
					payment.setPaymentChannel(PaymentChannel.TRANSFER);
					payment.setComplete(true);
					payment.setPaymentDate(paymentDate);
					accountName = personTravelPolicy.getProduct().getProductGroup().getAccountCode();
					payment.setAccountBank(accountBank);
					acode = paymentService.findCheckOfAccountCode(accountName, branch, currencyCode);
					Bank bank = bankService.findByACODE(acode);
					payment.setBank(bank);
					paymentList.add(payment);
					paymentList = paymentService.prePayment(paymentList);

					// insert TLF
					for (Payment p : paymentList) {
						if (personTravelPolicy.getId().equals(p.getReferenceNo())) {
							accountPaymentList.add(new AccountPayment(accountName, p));
							break;
						}
					}
					// TODO FIXME PSH
					// paymentService.preActivatePayment(accountPaymentList,
					// personTravelPolicy.getCustomer().getId(), branch,
					// agentCommissionList, false, currencyCode);
					List<TLF> tlfList = paymentService.findTLFbyTLFNo(paymentList.get(0).getReceiptNo(), false);
					for (TLF tlf : tlfList) {
						tlf.setPaid(true);
						tlf.setSettlementDate(paymentDate);
						paymentService.updateTLF(tlf);
					}
					mobileTravelProposal.setConvert(true);
					mobileTravelProposalDAO.update(mobileTravelProposal);
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to convert from Mobile to PersonTravelPolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deletePayment(PersonTravelPolicy policy, WorkFlowDTO workFlowDTO) {
		try {

			workFlowDTOService.updateWorkFlow(workFlowDTO);
			// String insPersonCodeNo = null;
			// String inPersonGroupCodeNo = null;
			// String beneficiaryNo = null;
			// Product product = policy.getPersonTravelProposal().getProduct();
			// calculatePremium(lifeProposal);
			// Custom ID GEN For Proposal Insured Person and Beneficiary

			// personTravelProposalDAO.update(policy.getPersonTravelProposal());
			personTravelPolicyDAO.deletePersonTravelPolicy(policy);
			List<Payment> paymentList = paymentService.findByPolicy(policy.getId());
			paymentService.deletePayments(paymentList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete payment.", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelPolicy> findPersonTravelPolicyByPOByReceiptNo(String receiptNo, String bpmsReceiptNo) {
		List<PersonTravelPolicy> result = null;
		try {
			result = personTravelPolicyDAO.findPaymentOrderByReceiptNo(receiptNo, bpmsReceiptNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelPolicy payment order by Receipt No ");
		}
		return result;
	}

}
