package org.ace.insurance.web.manage.agent.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.COACode;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.agent.PolicyDTO;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "AddNewStaffCommissionPaymentActionBean")
public class AddNewStaffCommissionPaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lPolicyService) {
		this.lifePolicyService = lPolicyService;
	}

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{CurrencyService}")
	private ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupfarmerProposalService;

	public void setGroupfarmerProposalService(IGroupfarmerProposalService groupfarmerProposalService) {
		this.groupfarmerProposalService = groupfarmerProposalService;
	}

	private User user;
	private StaffCommissionDTO staffCommissionDTO;
	private List<StaffAgentCommission> commissionList;
	private boolean isCheque;
	private final String reportName = "agentCommissionPayment";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	String currencyCode;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		staffCommissionDTO = (staffCommissionDTO == null) ? (StaffCommissionDTO) getParam("StaffCommissionDTO") : staffCommissionDTO;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		commissionList = paymentService.findStaffCommissionPaymentByAgent(staffCommissionDTO.getInvoiceNo(), staffCommissionDTO.getStaff().getId());
		if (commissionList != null) {
			StaffAgentCommission staffCommission = commissionList.get(0);
			staffCommissionDTO.setBank(staffCommission.getBank());
			staffCommissionDTO.setReferenceType(staffCommission.getReferenceType());
			staffCommissionDTO.setChequeNo(staffCommission.getBankaccountno());
			currencyCode = KeyFactorChecker.getKyatId();
		}
	}

	public List<PolicyDTO> getPolicyListForCommission() {
		List<PolicyDTO> policyList = new ArrayList<PolicyDTO>();
		PolicyDTO policyDTO = null;
		for (StaffAgentCommission comm : commissionList) {
			PolicyReferenceType rType = comm.getReferenceType();
			switch (rType) {
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					LifePolicy lPolicy = lifePolicyService.findLifePolicyById(comm.getReferenceNo());
					policyDTO = new PolicyDTO(lPolicy, comm.getCommissionStartDate(), comm.getCommission(), rType);
					break;
				case PA_POLICY:
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(comm.getReferenceNo());
					policyDTO = new PolicyDTO(paPolicy, comm.getCommissionStartDate(), comm.getCommission(), PolicyReferenceType.PA_POLICY);
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					LifePolicy stePolicy = lifePolicyService.findLifePolicyById(comm.getReferenceNo());
					policyDTO = new PolicyDTO(stePolicy, comm.getCommissionStartDate(), comm.getCommission(), rType);
					break;
				case FARMER_POLICY:
					LifePolicy farmerPolicy = lifePolicyService.findLifePolicyById(comm.getReferenceNo());
					policyDTO = new PolicyDTO(farmerPolicy, comm.getCommissionStartDate(), comm.getCommission(), PolicyReferenceType.FARMER_POLICY);
					break;
				case MEDICAL_POLICY:
				case MEDICAL_BILL_COLLECTION:
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
					policyDTO = new PolicyDTO(medicalPolicy, comm.getCommissionStartDate(), comm.getCommission(), rType);
					break;
				case PERSON_TRAVEL_POLICY:
					PersonTravelPolicy personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyById(comm.getReferenceNo());
					policyDTO = new PolicyDTO(personTravelPolicy, comm.getCommissionStartDate(), comm.getCommission());
					break;
				case GROUP_FARMER_PROPOSAL:
					GroupFarmerProposal groupFarmerProposal = groupfarmerProposalService.findGroupFarmerById(comm.getReferenceNo());
					policyDTO = new PolicyDTO(groupFarmerProposal, comm.getCommissionStartDate(), comm.getCommission());
					break;
				case STUDENT_LIFE_POLICY:
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					LifePolicy studentPolicy = lifePolicyService.findLifePolicyById(comm.getReferenceNo());
					policyDTO = new PolicyDTO(studentPolicy, comm.getCommissionStartDate(), comm.getCommission(), rType);
					break;
				default:
					break;
			}
			policyList.add(policyDTO);
		}
		return policyList;
	}

	public StaffCommissionDTO getStaffCommissionDTO() {
		return staffCommissionDTO;
	}

	public void setStaffCommissionDTO(StaffCommissionDTO staffCommissionDTO) {
		this.staffCommissionDTO = staffCommissionDTO;
	}

	public String payAgentCommission() {
		String result = null;
		try {
			if (!validPayment()) {
				return result;
			}
			paymentService.paymentStaffCommission(commissionList, staffCommissionDTO, user.getBranch(), currencyCode);

			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put("Agent Id :", staffCommissionDTO.getStaff().getId());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public List<StaffAgentCommission> getCommissionList() {
		return commissionList;
	}

	public void setCommissionList(List<StaffAgentCommission> commissionList) {
		this.commissionList = commissionList;
	}

	private boolean validPayment() {
		boolean valid = true;
		String formID = "agentCommissionPaymentForm";
		if (isCheque) {
			if (staffCommissionDTO.getChequeNo() == null || staffCommissionDTO.getChequeNo().isEmpty()) {
				addErrorMessage(formID + ":chequeNo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (staffCommissionDTO.getBank() == null) {
				addErrorMessage(formID + ":bankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}
		return valid;
	}

	public boolean getIsCheque() {
		return isCheque;
	}

	public void setIsCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		String coaNameCr;
		String coaNameDr;
		String paymentCcoaCode1 = null;
		String paymentCcoaCode2 = null;
		TLF tlfForCoInsuPayCr;
		TLF tlfForCoInsuPayDr;
		Branch branch = user.getBranch();
		// Insurance Type
		if (staffCommissionDTO.getReferenceType() != null && staffCommissionDTO.getReferenceType().equals(PolicyReferenceType.LIFE_POLICY)) {
			paymentCcoaCode1 = paymentService.findCheckOfAccountCode(COACode.LIFE_AGENT_COMMISSION, branch, currencyCode);
		}
		tlfForCoInsuPayDr = new TLF();
		tlfForCoInsuPayDr.setCoaId(paymentCcoaCode1);

		if (staffCommissionDTO.getPaymentChannel() != null) {
			if (staffCommissionDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paymentCcoaCode2 = paymentService.findCCOAByCode(staffCommissionDTO.getBank().getAcode(), branch.getId(), currencyCode);
			} else if (staffCommissionDTO.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				paymentCcoaCode2 = paymentService.findCheckOfAccountCode(COACode.CASH, branch, currencyCode);
			} else if (staffCommissionDTO.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				paymentCcoaCode2 = paymentService.findCheckOfAccountCode(COACode.YANGON_INTERBRANCH, branch, currencyCode);
			}
		}
		tlfForCoInsuPayCr = new TLF();
		tlfForCoInsuPayCr.setCoaId(paymentCcoaCode2);

		coaNameCr = paymentService.findCOAAccountNameByCode(tlfForCoInsuPayCr.getCoaId());
		coaNameDr = paymentService.findCOAAccountNameByCode(tlfForCoInsuPayDr.getCoaId());
		DocumentBuilder.generateStaffCommissionPayment(staffCommissionDTO, tlfForCoInsuPayCr, tlfForCoInsuPayDr, coaNameCr, coaNameDr, dirPath, fileName);
	}

}
