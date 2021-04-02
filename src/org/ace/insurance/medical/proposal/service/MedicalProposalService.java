package org.ace.insurance.medical.proposal.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.persistence.interfaces.IGroupMicroHealthDAO;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.process.persistence.interfaces.IProcessDAO;
import org.ace.insurance.medical.productprocess.ProductProcessCriteriaDTO;
import org.ace.insurance.medical.productprocess.StudentAgeType;
import org.ace.insurance.medical.productprocess.persistence.interfaces.IProductProcessDAO;
import org.ace.insurance.medical.proposal.MP001;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonKeyFactorValue;
import org.ace.insurance.medical.proposal.MedicalSurvey;
import org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.medical.surveyquestion.ProductProcessQuestionLink;
import org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.persistence.interfaces.IProductDAO;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "MedicalProposalService")
public class MedicalProposalService extends BaseService implements IMedicalProposalService {

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "MedicalProposalDAO")
	private IMedicalProposalDAO medicalProposalDAO;

	@Resource(name = "ProcessDAO")
	private IProcessDAO mProcessDAO;

	@Resource(name = "ProductDAO")
	private IProductDAO productDAO;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "MedicalPolicyService")
	private IMedicalPolicyService medicalPolicyService;

	@Resource(name = "CustomerService")
	private ICustomerService customerService;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "SurveyQuestionDAO")
	private ISurveyQuestionDAO surveyQuestionDAO;

	@Resource(name = "ProductProcessDAO")
	private IProductProcessDAO productProcessDAO;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowService;

	@Resource(name = "AcceptedInfoService")
	private IAcceptedInfoService acceptedInfoService;

	@Resource(name = "GroupMicroHealthDAO")
	private IGroupMicroHealthDAO groupMicroHealthDAO;

	@Resource(name = "OrganizationDAO")
	private IOrganizationDAO organizationDAO;

	@Resource(name = "BancaassuranceProposalService")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	@Resource(name = "BancaassurancePolicyService")
	private IBancaassurancePolicyService bancaassuracePolicyService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmSkipPaymentTLFMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status,
			Date updatedSubmittedDate) {
		List<Payment> paymentList = null;
		try {
			paymentList = confirmMedicalProposal(medicalProposal, workFlowDTO, paymentDTO, branch, status, true, updatedSubmittedDate);
			paymentMedicalProposal(medicalProposal, workFlowDTO, paymentList, branch, status);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirmSkipPaymentTLF a MedicalProposal ID : " + medicalProposal.getId(), e);
		}
		return paymentList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status,
			boolean isSkipTLFPayment, Date updatedSubmittedDate) {
		List<Payment> paymentList = new ArrayList<Payment>();
		double rate = 1.0;
		try {
			if (!isSkipTLFPayment)
				workFlowService.updateWorkFlow(workFlowDTO);
			if (medicalProposal.getOldMedicalPolicy() == null) {
				for (MedicalProposalInsuredPerson insuredPerson : medicalProposal.getMedicalProposalInsuredPersonList()) {
					if (insuredPerson.isApproved()) {
						insuredPerson.setStartDate(updatedSubmittedDate);
						Calendar cal = Calendar.getInstance();
						cal.setTime(insuredPerson.getStartDate());
						cal.add(Calendar.MONTH, insuredPerson.getPeriodMonth());
						insuredPerson.setEndDate(cal.getTime());
					}
				}

			}

			MedicalPolicy medicalPolicy = new MedicalPolicy(medicalProposal);
			if (medicalProposal.isStaffPlan()) {
				medicalPolicy.setEips(medicalProposal.getEips());
			}
			if (!ProposalType.UNDERWRITING.equals(medicalProposal.getProposalType())) {
				medicalPolicy.setPolicyNo(medicalProposal.getOldMedicalPolicy().getPolicyNo());
			}
			Payment payment = new Payment();
			payment.setPaymentType(medicalProposal.getPaymentType());
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(paymentDTO.getReferenceType());
			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());
			// payment.setBankWallet(paymentDTO.getBankWallet());
			if (paymentDTO.getBankWallet() != null) {
				payment.setBankWallet(paymentDTO.getBankWallet());
				if (payment.getBankWallet().getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
					payment.setBankCharges(paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(rate * (paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges()));
				} else {
					payment.setBankCharges(
							(paymentDTO.getBankWallet().getCharges() * medicalPolicy.getTotalTermPremium() / 100) + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(
							(rate * (paymentDTO.getBankWallet().getCharges() * medicalPolicy.getTotalTermPremium() / 100)) + paymentDTO.getBankWallet().getAdditionalCharges());
				}
			}
			payment.setDiscountPercent(paymentDTO.getDiscountPercent());
			payment.setReceivedDeno(paymentDTO.getReceivedDeno());
			payment.setRefundDeno(paymentDTO.getRefundDeno());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setBasicPremium(medicalPolicy.getBasicTermPremium());
			payment.setAddOnPremium(medicalPolicy.getAddonTermPremium());
			payment.setNcbPremium(medicalPolicy.getPolicyInsuredPersonList().get(0).getTotalNcbPremium());
			payment.setRate(rate);
			payment.setAmount(payment.getTotalAmount());
			payment.setHomeAmount(rate * payment.getTotalAmount());
			payment.setHomePremium(rate * payment.getBasicPremium());
			// payment.setHomeBankCharges(rate *
			// paymentDTO.getBankWallet().getCharges());
			payment.setFromTerm(1);
			payment.setToTerm(1);
			if (paymentDTO.getDiscountPercent() > 0.0) {
				for (MedicalPolicyInsuredPerson insuredPerson : medicalPolicy.getPolicyInsuredPersonList()) {
					insuredPerson.setStartDate(updatedSubmittedDate);
					Date updatedEndDate = DateUtils.addYears(updatedSubmittedDate, 1);
					insuredPerson.setEndDate(updatedEndDate);
					calculateDiscount(insuredPerson, paymentDTO.getDiscountPercent());
				}
			}

			if (medicalProposal.getOldMedicalPolicy() != null) {
				medicalPolicy.setReferencePolicyNo(medicalProposal.getOldMedicalPolicy().getPolicyNo());
			}

			BancaassuranceProposal BancaProposal = bancaassuraceProposalService.findBancaassuranceProposalByMedicalproposalId(medicalProposal.getId());
			if (BancaProposal != null) {
				BancaassurancePolicy bancaassurancePolicy = new BancaassurancePolicy(BancaProposal);
				bancaassurancePolicy.setMedicalPolicy(medicalPolicy);
				bancaassuracePolicyService.insert(bancaassurancePolicy);
			}
			medicalPolicy.setDelFlag(false);
			medicalPolicy.setLastPaymentTerm(1);
			medicalPolicy.setActivedPolicyStartDate(updatedSubmittedDate);
			medicalPolicy.setCommenmanceDate(updatedSubmittedDate);
			Date updatedEndDate = DateUtils.addMonth(updatedSubmittedDate, medicalProposal.getPaymentType().getMonth());
			medicalPolicy.setActivedPolicyEndDate(updatedEndDate);
			medicalPolicy.setCommenmanceDate(updatedSubmittedDate);
			medicalPolicyService.addNewMedicalPolicy(medicalPolicy);
			payment.setReferenceNo(medicalPolicy.getId());
			paymentList.add(payment);
			medicalProposalDAO.update(medicalProposal);
			paymentList = paymentService.prePayment(paymentList);
			if (!isSkipTLFPayment) {
				prePaymentMedicalProposal(medicalPolicy, paymentList, branch, status);
				if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
					prePaymentMedicalProposalTransfer(medicalProposal, paymentList, branch);
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm a MedicalProposal", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void informProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, AcceptedInfo acceptedInfo, String status) {
		try {
			workFlowService.updateWorkFlow(workFlowDTO);
			if (acceptedInfo != null) {
				acceptedInfoService.addNewAcceptedInfo(acceptedInfo);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to inform  MedicalProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private void prePaymentMedicalProposal(MedicalPolicy medicalPolicy, List<Payment> paymentList, Branch branch, String status) {
		try {
			List<AgentCommission> agentCommissionList = null;
			Product product = medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct();
			List<StaffAgentCommission> staffagentCommissionList = null;
			PolicyReferenceType policyReferenceType = ProductIDConfig.getMedicalPolicyReferenceType(product);
			if (medicalPolicy.getMedicalProposal().getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				double totalCommission = 0.0;
				double commissionPercent = product.getFirstCommission();
				if (commissionPercent > 0) {
					double termPremium = medicalPolicy.getTotalTermPremium();
					totalCommission = (termPremium / 100) * commissionPercent;
				}
				agentCommissionList.add(new AgentCommission(medicalPolicy.getId(), policyReferenceType, medicalPolicy.getAgent(), totalCommission, new Date()));
			}

			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			String accountCode = product.getProductGroup().getAccountCode();
			for (Payment payment : paymentList) {
				if (medicalPolicy.getId().equals(payment.getReferenceNo())) {
					accountPaymentList.add(new AccountPayment(accountCode, payment));
					break;
				}
				if (payment.getIsReinstate()) {
					accountPaymentList.add(new AccountPayment(accountCode, payment));
					break;
				}
			}
			String customerId = "";
			if (medicalPolicy.getMedicalProposal().getCustomer() != null) {
				customerId = medicalPolicy.getMedicalProposal().getCustomer().getId();
			} else if (medicalPolicy.getMedicalProposal().getOrganization() != null) {
				customerId = medicalPolicy.getMedicalProposal().getOrganization().getId();
			}
			String currencyId = product.getCurrency().getId();
			if (medicalPolicy.getMedicalProposal().isStaffPlan()) {
				String eNo = accountPaymentList.get(0).getPayment().getId();
				Payment paymentByIndex = accountPaymentList.get(0).getPayment();
				staffagentCommissionList = new ArrayList<StaffAgentCommission>();
				staffagentCommissionList.add(
						new StaffAgentCommission(medicalPolicy.getId(), policyReferenceType, medicalPolicy.getEips().getStaff(), medicalPolicy.getEips().getAmount(), new Date()));
				if (!medicalPolicy.getBranch().getId().equals(branch.getId())) {
					paymentService.addStaffCommissionTLF(staffagentCommissionList, medicalPolicy.getBranch(), paymentByIndex, eNo, false, currencyId, medicalPolicy.getSalePoint());

				} else {
					paymentService.addStaffCommissionTLF(staffagentCommissionList, branch, paymentByIndex, eNo, false, currencyId, medicalPolicy.getSalePoint());
				}
			}

			// FOR INTER BRANCH
			if (!medicalPolicy.getBranch().getId().equals(branch.getId())) {
				paymentService.preActivatePaymentForInterBranch(accountPaymentList, customerId, branch, agentCommissionList, false, currencyId, medicalPolicy.getSalePoint(),
						medicalPolicy.getBranch());

			} else {
				// TODO FIXME PSH --check for isRenewal is true in old
				paymentService.preActivatePayment(accountPaymentList, customerId, branch, agentCommissionList, false, currencyId, medicalPolicy.getSalePoint());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a MedicalProposal ID : " + medicalPolicy.getMedicalProposal().getId(), e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void issueMedicalProposal(MedicalPolicy policy) {
		try {
			MedicalProposal proposal = policy.getMedicalProposal();
			medicalPolicyService.updateMedicalPolicy(policy);
			workFlowService.deleteWorkFlowByRefNo(proposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a MotorProposal complete status", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentMedicalProposalTransfer(MedicalProposal medicalProposal, List<Payment> paymentList, Branch branch) {
		try {
			Product product = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getProduct();
			String currencyCode = product.getCurrency().getId();
			String customerId = medicalProposal.getCustomer() == null ? medicalProposal.getOrganization().getId() : medicalProposal.getCustomer().getId();
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (Payment payment : paymentList) {
				accountPaymentList.add(new AccountPayment(payment.getAccountBank().getAcode(), payment));
			}
			paymentService.activatePaymentTransfer(accountPaymentList, customerId, branch, false, currencyCode, medicalProposal.getSalePoint());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a MotorProposal ID : " + medicalProposal.getId(), e);
		}
	}

	private void calculateDiscount(MedicalPolicyInsuredPerson policyInsuredPerson, double discountPercent) {
		double premium;
		double discountPremium;
		double totalDiscountPremium = 0.00;
		/* Reset Discount Basic Premium */
		premium = policyInsuredPerson.getPremium();
		discountPremium = Utils.getPercentOf(discountPercent, premium);
		policyInsuredPerson.setPremium(premium - discountPremium);
		totalDiscountPremium += discountPremium;

		/* Reset Discount BasicPlus Premium */
		// premium = policyInsuredPerson.getBasicPlusPremium();
		// discountPremium = Utils.getPercentOf(discountPercent, premium);
		// policyInsuredPerson.setBasicPlusPremium(premium - discountPremium);
		// totalDiscountPremium += discountPremium;

		/* Reset Discount AddOn Premium */
		for (MedicalPolicyInsuredPersonAddOn insurPersonAddOn : policyInsuredPerson.getPolicyInsuredPersonAddOnList()) {
			premium = insurPersonAddOn.getPremium();
			discountPremium = Utils.getPercentOf(discountPercent, premium);
			insurPersonAddOn.setPremium(premium - discountPremium);
			totalDiscountPremium += discountPremium;
		}
		policyInsuredPerson.setTotalDiscountPremium(totalDiscountPremium);

		/* Reset Discount Basic Term Premium */
		// double basicTermPremium = policyInsuredPerson.getPremium();
		// double discountBasicTermPremium = basicTermPremium -
		// Utils.getPercentOf(discountPercent, basicTermPremium);
		// policyInsuredPerson.setPremium(discountBasicTermPremium);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalProposal findMedicalProposalById(String id) {
		MedicalProposal result = null;
		try {
			result = medicalProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a MedicalProposal (ID : " + id + ")", e);
		}
		return result;
	}

	public void updateInsPerWithReasonAndApprove(String rejectReason, boolean approved, String id) {
		try {
			medicalProposalDAO.updateInsPerWithReasonAndApprove(rejectReason, approved, id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find updateInsPerWithReasonAndApprove (ID : " + id + ")", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalSurvey findMedicalSurveyByProposalId(String medicalProposalId) {
		MedicalSurvey result = null;
		try {
			result = medicalProposalDAO.findSurveyByProposalId(medicalProposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a MedicalProposal (ID : " + medicalProposalId + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a MedicalProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductProcessQuestionLink> findPProQueByPPId(String productId, String processId, ProductProcessCriteriaDTO productProcessCriteriaDTO) {
		List<ProductProcessQuestionLink> result = null;
		try {
			result = surveyQuestionDAO.findPProQueByPPId(productId, processId, productProcessCriteriaDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find find ProductProcessQuestionLink By ProductProcess'Id ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductProcessQuestionLink> findPProQueByPPIdByOptionTypeForSPCL(String productId, String processId, ProductProcessCriteriaDTO productProcessCriteriaDTO) {
		List<ProductProcessQuestionLink> result = null;
		try {
			result = surveyQuestionDAO.findPProQueByPPIdByOptionTypeForSPCL(productId, processId, productProcessCriteriaDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find find ProductProcessQuestionLink By ProductProcess'Id ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductProcessQuestionLink> findPProQueByPPIdByOptionTypeForSTSPCL(String productId, String processId, ProductProcessCriteriaDTO productProcessCriteriaDTO) {
		List<ProductProcessQuestionLink> result = null;
		try {
			result = surveyQuestionDAO.findPProQueByPPIdByOptionTypeForSTSPCL(productId, processId, productProcessCriteriaDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find find ProductProcessQuestionLink By ProductProcess'Id ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductProcessQuestionLink> findMedicalPProQueByPPIdMedical(String productName, String processName, StudentAgeType studentAgeType) {
		List<ProductProcessQuestionLink> result = null;
		try {
			result = surveyQuestionDAO.findMedicalPProQueByPPIdMedical(productName, processName, studentAgeType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find find ProductProcessQuestionLink By ProductProcess'Id ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentMedicalProposal(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch, String status) {
		try {
			String generatedPolicyNo = null;
			switch (workFlowDTO.getReferenceType()) {
				case CRITICAL_ILLNESS_PROPOSAL:
					generatedPolicyNo = SystemConstants.CRITICAL_ILLNESS_POLICY_NO;
					break;
				case HEALTH_PROPOSAL:
					generatedPolicyNo = SystemConstants.HEALTH_POLICY_NO;
					break;
				case MICRO_HEALTH_PROPOSAL:
					generatedPolicyNo = SystemConstants.MICRO_HEALTH_POLICY_NO;
					break;
				// case MEDICAL_PROPOSAL:
				// generatedPolicyNo = SystemConstants.MEDICAL_POLICY_NO;
				// break;
				default:
					break;

			}

			MedicalPolicy medicalPolicy = medicalPolicyService.activateMedicalPolicy(medicalProposal.getId(), generatedPolicyNo);
			List<AgentCommission> agentCommissionList = null;

			/* get receipt No */
			String receiptNo = paymentList.get(0).getReceiptNo();

			/* get agent commission of each policy */
			Payment p = paymentService.findPaymentByReferenceNo(medicalPolicy.getId());
			if (medicalProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				double firstAgentCommission = medicalPolicy.getAgentCommission();
				double commissionPercent = medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct().getFirstCommission();
				AgentCommissionEntryType type = ProposalType.UNDERWRITING.equals(medicalProposal.getProposalType()) ? AgentCommissionEntryType.UNDERWRITING
						: AgentCommissionEntryType.RENEWAL;
				agentCommissionList.add(new AgentCommission(medicalPolicy.getId(), paymentList.get(0).getReferenceType(), medicalPolicy.getAgent(), firstAgentCommission,
						new Date(), receiptNo, medicalPolicy.getTotalTermPremium(), commissionPercent, type, p.getRate(), (p.getRate() * firstAgentCommission),
						KeyFactorChecker.getKyatId(), (p.getRate() * medicalPolicy.getTotalTermPremium())));
			}

			List<StaffAgentCommission> staffagentCommissionList = null;
			/* get staff agent commission of each policy */
			if (medicalProposal.isStaffPlan()) {
				double firststaffAgentCommission = 0.0;
				staffagentCommissionList = new ArrayList<StaffAgentCommission>();
				firststaffAgentCommission = medicalPolicy.getEips().getAmount();
				staffagentCommissionList
						.add(new StaffAgentCommission(medicalPolicy.getId(), paymentList.get(0).getReferenceType(), medicalPolicy.getEips().getStaff(), firststaffAgentCommission,
								new Date(), receiptNo, medicalPolicy.getTotalTermPremium(), medicalPolicy.getEips().getPercentage(), AgentCommissionEntryType.UNDERWRITING,
								p.getRate(), (p.getRate() * firststaffAgentCommission), KeyFactorChecker.getKyatId(), (p.getRate() * medicalPolicy.getTotalTermPremium())));
			}

			/* add agent commission, activate TLF and Payment flag */
			// TODO FIXME PSH add salepoint
			paymentService.activatePaymentAndTLF(paymentList, agentCommissionList, branch, CurrencyUtils.getCurrencyCode(null), null);
			if (medicalProposal.isStaffPlan()) {
				paymentService.addStaffAgentCommission(staffagentCommissionList);
			}

			/* update ActivePolicy Count in CustomerTable */
			if (medicalProposal.getCustomer() != null) {
				int activePolicyCount = medicalProposal.getCustomer().getActivePolicy() + 1;
				customerDAO.updateActivePolicy(activePolicyCount, medicalProposal.getCustomer().getId());
				if (medicalProposal.getCustomer().getActivedDate() == null) {
					customerDAO.updateActivedPolicyDate(new Date(), medicalProposal.getCustomer().getId());
				}
			}
			if (medicalProposal.getOrganization() != null) {
				int activePolicyCount = medicalProposal.getOrganization().getActivePolicy() + 1;
				organizationDAO.updateActivePolicy(activePolicyCount, medicalProposal.getOrganization().getId());
				if (medicalProposal.getOrganization().getActivedDate() == null) {
					organizationDAO.updateActivedPolicyDate(new Date(), medicalProposal.getOrganization().getId());
				}
			}
			workFlowService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a MedicalProposal ID : " + medicalProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void calculatePremium(MedicalProposal medicalProposal) {
		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
			double basicTermPremium = 0.0;
			double oneYearPremium = 0.0;
			int paymentType = medicalProposal.getPaymentType().getMonth();
			Product product = person.getProduct();
			if (person.getProduct().getAutoCalculate()) {
				Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
				for (MedicalProposalInsuredPersonKeyFactorValue keyfactor : person.getKeyFactorValueList()) {
					keyfatorValueMap.put(keyfactor.getKeyFactor(), keyfactor.getValue());
				}
				// rate is for term , not annual
				Double premiumRate = productService.findProductPremiumRate(keyfatorValueMap, product, null);
				if (premiumRate == null || premiumRate <= 0) {
					throw new SystemException(ErrorCode.NO_PREMIUM_RATE, keyfatorValueMap, "There is no premiumn.");
				}
				if (!ProductIDConfig.isMicroHealthInsurance(product)) {
					basicTermPremium = premiumRate * person.getUnit();
				} else {
					basicTermPremium = premiumRate;
				}

				int paymentTerm = 0;
				if (paymentType > 0) {
					oneYearPremium = basicTermPremium * 12 / paymentType;
					paymentTerm = person.getPeriodMonth() / paymentType;
					person.setPaymentTerm(paymentTerm);
				} else {
					oneYearPremium = basicTermPremium;
					person.setPaymentTerm(1);
				}
				person.setBasicTermPremium(basicTermPremium);
				person.setProposedPremium(oneYearPremium);
				person.setApprovedPremium(oneYearPremium);

				double totalAddonPremium = 0.0;
				double totalAddonTermPremium = 0.0;

				List<MedicalProposalInsuredPersonAddOn> insuredPersonAddOnList = person.getInsuredPersonAddOnList();
				if (insuredPersonAddOnList != null && !insuredPersonAddOnList.isEmpty()) {
					for (MedicalProposalInsuredPersonAddOn insuredPersonAddOn : insuredPersonAddOnList) {
						AddOn addOn = insuredPersonAddOn.getAddOn();
						Product addOnProduct = getAddOnProduct(product, addOn.getId());
						// rate is for term , not annual and for 1 unit
						double addOnTermPremium = productService.findProductPremiumRate(keyfatorValueMap, addOnProduct, null);
						addOnTermPremium = insuredPersonAddOn.getUnit() * addOnTermPremium;

						double addOnPremium = 0.0;
						if (paymentType > 0) {
							addOnPremium = addOnTermPremium * 12 / paymentType;
						} else {
							addOnPremium = addOnTermPremium;
						}
						totalAddonTermPremium += addOnTermPremium;
						totalAddonPremium += addOnPremium;

						insuredPersonAddOn.setProposedPremium(addOnPremium);
						insuredPersonAddOn.setApprovedPremium(addOnPremium);
					}
					person.setAddOnTermPremium(totalAddonTermPremium);
					person.setProposedAddOnPremium(totalAddonPremium);

				}
				person.setAddOnTermPremium(totalAddonTermPremium);
				person.setProposedAddOnPremium(totalAddonPremium);
			}
		}
	}

	private Product getAddOnProduct(Product product, String addOnId) {
		Product addOnProduct = null;
		if (ProductIDConfig.isIndividualHealthInsurance(product)) {
			if (ProductIDConfig.getHealthAddOn1().equals(addOnId)) {
				addOnProduct = productService.findProductById(ProductIDConfig.getIndividualHealthAddOn1ProductID());
			} else if (ProductIDConfig.getHealthAddOn2().equals(addOnId)) {
				addOnProduct = productService.findProductById(ProductIDConfig.getIndividualHealthAddOn2ProductID());
			}
		}

		if (ProductIDConfig.isGroupHealthInsurancae(product)) {
			if (ProductIDConfig.getHealthAddOn1().equals(addOnId)) {
				addOnProduct = productService.findProductById(ProductIDConfig.getGroupHealthAddOn1ProductID());
			} else if (ProductIDConfig.getHealthAddOn2().equals(addOnId)) {
				addOnProduct = productService.findProductById(ProductIDConfig.getGroupHealthAddOn2ProductID());
			}
		}
		return addOnProduct;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MP001> findAllMedicalPolicy(EnquiryCriteria enquiryCriteria) {
		List<MP001> result = null;
		try {
			result = medicalProposalDAO.findByEnquiryCriteria(enquiryCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of MedicalPolicy)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SurveyQuestionAnswer> findSurveyQuestionAnswerByProposalId(String proposalId) {
		List<SurveyQuestionAnswer> surveyQuestionAnswerList = null;
		try {
			surveyQuestionAnswerList = medicalProposalDAO.findSurveyQuestionAnswerByProposalId(proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all MedicalSurveyQuestionAnswer)", e);
		}
		return surveyQuestionAnswerList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMedicalProposal(MedicalProposal medicalProposal) {
		try {
			medicalProposalDAO.update(medicalProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update medicalProposal)", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMedicalProposalCompleteStatus(String proposalId) {
		try {
			medicalProposalDAO.updateCompleteStatus(true, proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update medicalProposal complete status)", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePayment(MedicalPolicy medicalPolicy, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			String insPersonCodeNo = null;
			String inPersonGroupCodeNo = null;
			String beneficiaryNo = null;
			CustomerType customerType = medicalPolicy.getCustomerType();
			for (MedicalProposalInsuredPerson insuredPerson : medicalPolicy.getMedicalProposal().getMedicalProposalInsuredPersonList()) {
				if (CustomerType.INDIVIDUALCUSTOMER.equals(customerType) && insuredPerson.getInsPersonCodeNo() == null) {
					insPersonCodeNo = customIDGenerator.getNextId(SystemConstants.MEDICAL_INSUREDPERSON_CODENO, null, true);
					insuredPerson.setInsPersonCodeNo(insPersonCodeNo);
				} else if (CustomerType.CORPORATECUSTOMER.equals(customerType) && insuredPerson.getInPersonGroupCodeNo() == null) {
					inPersonGroupCodeNo = customIDGenerator.getNextId(SystemConstants.MEDICAL_INSUREDPERSON_GROUPCODENO, null, true);
					insuredPerson.setInPersonGroupCodeNo(inPersonGroupCodeNo);
				}
				for (MedicalProposalInsuredPersonBeneficiaries beneficiary : insuredPerson.getInsuredPersonBeneficiariesList()) {
					beneficiaryNo = customIDGenerator.getNextId(SystemConstants.MEDICAL_BENEFICIARY_NO, null, false);
					beneficiary.setBeneficiaryNo(beneficiaryNo);
				}
			}
			medicalProposalDAO.update(medicalPolicy.getMedicalProposal());
			medicalPolicyService.deleteMedicalPolicy(medicalPolicy);
			List<Payment> paymentList = paymentService.findByPolicy(medicalPolicy.getId());
			paymentService.deletePayments(paymentList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete payment.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalProposal> findMedicalProposalByGroupMicroHealthId(String id) throws SystemException {
		try {
			return medicalProposalDAO.findMedicalProposalByGroupMicroHealthId(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete payment.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void createGroupMicroHealtPolicy(MedicalProposal medicalProposal, WorkFlowDTO workFlowDTO) throws SystemException {
		try {
			for (MedicalProposalInsuredPerson insuredPerson : medicalProposal.getMedicalProposalInsuredPersonList()) {
				insuredPerson.setStartDate(new Date());
				Calendar cal = Calendar.getInstance();
				cal.setTime(insuredPerson.getStartDate());
				cal.add(Calendar.MONTH, insuredPerson.getPeriodMonth());
				insuredPerson.setEndDate(cal.getTime());
			}

			MedicalPolicy medicalPolicy = new MedicalPolicy(medicalProposal);
			List<MedicalPolicyInsuredPerson> personList = medicalPolicy.getPolicyInsuredPersonList();
			Product product = personList.get(0).getProduct();
			String productCode = product.getProductGroup().getPolicyNoPrefix();
			medicalPolicy.setPolicyNo(customIDGenerator.getNextId(SystemConstants.MICRO_HEALTH_POLICY_NO, productCode, true));
			for (MedicalPolicyInsuredPerson person : personList) {
				person.setStartDate(new Date());
				Calendar cal = Calendar.getInstance();
				cal.setTime(person.getStartDate());
				cal.add(Calendar.MONTH, person.getPeriodMonth());
				person.setEndDate(cal.getTime());
			}
			medicalPolicy.setLastPaymentTerm(1);
			medicalPolicy.setCommenmanceDate(new Date());
			medicalPolicy.setDelFlag(false);
			medicalPolicy.setLastPaymentTerm(1);
			medicalPolicy.setActivedPolicyStartDate(new Date());
			Date updatedEndDate = DateUtils.addMonth(new Date(), medicalProposal.getPaymentType().getMonth());
			medicalPolicy.setActivedPolicyEndDate(updatedEndDate);
			medicalPolicyService.addNewMedicalPolicy(medicalPolicy);
			medicalProposalDAO.update(medicalProposal);
			GroupMicroHealth groupMicroHealth = groupMicroHealthDAO.findById(medicalProposal.getGroupMicroHealthID());
			groupMicroHealth.setProcessComplete(true);
			groupMicroHealthDAO.update(groupMicroHealth);
			workFlowService.createWorkFlowHistory(workFlowDTO);
			// workFlowService.deleteWorkFlowByRefNo(medicalProposal.getGroupMicroHealthID());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to create medicalPolicy", e);
		}

	}

}
