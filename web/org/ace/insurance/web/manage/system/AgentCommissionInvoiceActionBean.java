package org.ace.insurance.web.manage.system;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.service.interfaces.IAgentSanctionService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.service.interfaces.IAgentService;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AgentCommissionInvoiceActionBean")
public class AgentCommissionInvoiceActionBean<T extends IDataModel> extends BaseBean {

	@ManagedProperty(value = "#{AgentSanctionService}")
	private IAgentSanctionService agentSanctionService;

	public void setAgentSanctionService(IAgentSanctionService agentSanctionService) {
		this.agentSanctionService = agentSanctionService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
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

	@ManagedProperty(value = "#{AgentService}")
	private IAgentService agentService;

	public void setAgentService(IAgentService agentService) {
		this.agentService = agentService;
	}

	@ManagedProperty(value = "#{CurrencyService}")
	private ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	@ManagedProperty(value = "#{GroupMicroHealthService}")
	private IGroupMicroHealthService groupMicroHealthService;

	public void setGroupMicroHealthService(IGroupMicroHealthService groupMicroHealthService) {
		this.groupMicroHealthService = groupMicroHealthService;
	}

	private AgentCommission agentCommission;
	private User user;
	private User responsiblePerson;
	private AgentCommissionDetailCriteria criteria;
	private List<AgentCommission> agentCommissionList;
	private List<AgentCommission> selectedAgentCommissionList;
	private final String reportName = "AgentInvoiceSlip";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	private boolean disablePrintBtn = true;
	private boolean currencyrender = true;
	private boolean isCheque;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
	}

	@PostConstruct
	public void init() {
		resetCriteria();
		initializeInjection();
		agentCommission = new AgentCommission();
	}

	private void resetCriteria() {
		currencyrender = false;
		criteria = new AgentCommissionDetailCriteria();
		criteria.setInsuranceType(InsuranceType.LIFE);
		agentCommission = new AgentCommission();
		responsiblePerson = null;
		isCheque = false;
		if (criteria.getStartDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			criteria.setStartDate(cal.getTime());
		}
		if (criteria.getEndDate() == null) {
			Date endDate = new Date();
			criteria.setEndDate(endDate);
		}

		agentCommissionList = new ArrayList<AgentCommission>();
		selectedAgentCommissionList = new ArrayList<AgentCommission>();
	}

	public void filter() {
		agentCommissionList = paymentService.findAgentCommissionDetail(criteria);
	}

	public void invoiceAgentCommission() {
		try {
			if (validationAgentCommission()) {
				List<WorkFlowDTO> workFlowDTOList = new ArrayList<WorkFlowDTO>();
				for (AgentCommission ac : selectedAgentCommissionList) {
					WorkFlowDTO workFlowDTO = new WorkFlowDTO(ac.getId(), null, WorkflowTask.AGENT_COMMISSION_PAYMENT,
							WorkflowReferenceType.AGENT_COMMISSION, user, responsiblePerson);
					workFlowDTOList.add(workFlowDTO);
				}
				paymentService.updateAgentCommission(selectedAgentCommissionList, agentCommission, isCheque,
						workFlowDTOList);
				removeAgentCommissionListAfterPaid();
				addInfoMessage(null, MessageId.AGENT_COMMISSION_INVOICE_SUCCESS);
				disablePrintBtn = false;
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}

	}

	public boolean validationAgentCommission() {
		boolean result = true;
		if (selectedAgentCommissionList.isEmpty()) {
			addInfoMessage(null, MessageId.ATLEAST_ONE_AGENTCOMMISSION);
			result = false;
		} else {
			if (responsiblePerson == null) {
				addErrorMessage("agentCommissionListForm:responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
				result = false;
			}

			if (agentCommission.getPaymentChannel() == null) {
				addErrorMessage("agentCommissionListForm:paymentChannel", UIInput.REQUIRED_MESSAGE_ID);
				result = false;
			}

			if (agentCommission.getPaymentChannel() == PaymentChannel.CHEQUE) {
				if (agentCommission.getBank() == null) {
					addErrorMessage("agentCommissionListForm:bankName", UIInput.REQUIRED_MESSAGE_ID);
					result = false;
				}

				if (agentCommission.getBankaccountno() == null) {
					addErrorMessage("agentCommissionListForm:bankAccountNo", UIInput.REQUIRED_MESSAGE_ID);
					result = false;
				}
			}
		}

		return result;

	}

	private void removeAgentCommissionListAfterPaid() {
		agentCommissionList.removeAll(selectedAgentCommissionList);
	}

	public void reset() {
		resetCriteria();
		agentCommissionList = new ArrayList<AgentCommission>();
	}

	public void generateReport() {
		agentService.generateAgentInvoice(selectedAgentCommissionList, false, dirPath, fileName);
	}

	public void selectUser() {
		selectUser(WorkflowTask.AGENT_COMMISSION_PAYMENT, WorkFlowType.AGENT_COMMISSION);
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public List<AgentCommission> getAgentCommissionList() {
		return agentCommissionList;
	}

	public String getPolicyNo(AgentCommission comm) {
		String policyNo = null;
		IPolicy policy = null;
		switch (comm.getReferenceType()) {

		case LIFE_POLICY:
		case PA_POLICY:
		case FARMER_POLICY:
		case SHORT_ENDOWMENT_LIFE_POLICY:
		case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
		case STUDENT_LIFE_POLICY:
		case STUDENT_LIFE_POLICY_BILL_COLLECTION:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
		case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			policy = lifePolicyService.findLifePolicyById(comm.getReferenceNo());
			policyNo = policy.getPolicyNo();
			break;
		case MEDICAL_POLICY:
		case MEDICAL_BILL_COLLECTION:
		case HEALTH_POLICY:
		case HEALTH_POLICY_BILL_COLLECTION:
		case MICRO_HEALTH_POLICY:
		case CRITICAL_ILLNESS_POLICY:
		case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			policy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
			policyNo = policy.getPolicyNo();
			break;

		case PERSON_TRAVEL_POLICY:
			policy = personTravelPolicyService.findPersonTravelPolicyById(comm.getReferenceNo());
			policyNo = policy.getPolicyNo();
			break;
		case GROUP_FARMER_PROPOSAL:
			GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(comm.getReferenceNo());
			policyNo = proposal.getProposalNo();
			break;
		case GROUP_MICRO_HEALTH:
			GroupMicroHealth groupMicroHealth = groupMicroHealthService.findById(comm.getReferenceNo());
			policyNo = groupMicroHealth.getId();
			break;
		default:
			break;
		}
		return policyNo;
	}

	public String getStream() {
		return pdfDirPath + fileName;
	}

	// Payment Channel
	public void changePaymentChannel(AjaxBehaviorEvent event) {

		if (agentCommission.getPaymentChannel() == PaymentChannel.CASHED) {
			agentCommission.setBank(null);
			agentCommission.setChequeNo(null);
			agentCommission.setBankaccountno(null);
			isCheque = false;
		} else if (agentCommission.getPaymentChannel() == PaymentChannel.CHEQUE) {
			agentCommission.setBank(null);
			agentCommission.setChequeNo(null);
			agentCommission.setBankaccountno(null);
			isCheque = true;
		} else if (agentCommission.getPaymentChannel() == PaymentChannel.TRANSFER) {
			agentCommission.setBank(null);
			agentCommission.setChequeNo(null);
			agentCommission.setBankaccountno(null);
			isCheque = true;
		}
	}

	public PaymentChannel[] getPaymentChannelSet() {
		return new PaymentChannel[] { PaymentChannel.CASHED, PaymentChannel.CHEQUE, PaymentChannel.TRANSFER };
	}

	public boolean getIsCheque() {
		return isCheque;
	}

	public void setIsCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public AgentCommission getAgentCommission() {
		return agentCommission;
	}

	public void setAgentCommission(AgentCommission agentCommission2) {
		this.agentCommission = agentCommission2;
	}

	public boolean isDisablePrintBtn() {
		return disablePrintBtn;
	}

	public InsuranceType[] getInsuranceTypes() {
		return new InsuranceType[] { InsuranceType.LIFE, InsuranceType.MEDICAL, InsuranceType.HEALTH,
				InsuranceType.MICRO_HEALTH, InsuranceType.CRITICAL_ILLNESS, InsuranceType.PERSONAL_ACCIDENT,
				InsuranceType.FARMER, InsuranceType.PERSON_TRAVEL, InsuranceType.SHORT_ENDOWMENT_LIFE,
				InsuranceType.GROUP_MICRO_HEALTH, InsuranceType.GROUP_FARMER, InsuranceType.STUDENT_LIFE,
				InsuranceType.PUBLIC_TERM_LIFE, InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE,
				InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE, InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE };
	}

	public List<AgentCommission> getSelectedAgentCommissionList() {
		return selectedAgentCommissionList;
	}

	public void setSelectedAgentCommissionList(List<AgentCommission> selectedAgentCommissionList) {
		this.selectedAgentCommissionList = selectedAgentCommissionList;
	}

	public AgentCommissionDetailCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(AgentCommissionDetailCriteria criteria) {
		this.criteria = criteria;
	}

	public List<Currency> getCurrencyList() {
		return currencyService.findAllCurrency();
	}

	public void changeCurrency(AjaxBehaviorEvent e) {
		switch (criteria.getInsuranceType()) {

		default:
			currencyrender = false;
			criteria.setCurrency(null);
		}
	}

	public boolean isCurrencyrender() {
		return currencyrender;
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		agentCommission.setBank(bank);
	}

	public void selectBranchBank() {
		if (PaymentChannel.TRANSFER.equals(agentCommission.getPaymentChannel())) {
			selectInterBranchBank();
		} else {
			selectBank();
		}
	}
}
