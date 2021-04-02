package org.ace.insurance.web.manage.travel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.deno.service.interfaces.IDenoService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalInfo;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewPersonTravelPaymentActionBean")
public class AddNewPersonTravelPaymentActionBean extends BaseBean {

	@ManagedProperty(value = "#{DenoService}")
	private IDenoService denoService;

	public void setDenoService(IDenoService denoService) {
		this.denoService = denoService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{BankService}")
	private IBankService bankService;

	public void setBankService(IBankService bankService) {
		this.bankService = bankService;
	}

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	private User user;
	private User responsiblePerson;
	private PersonTravelProposal travelProposal;
	private PersonTravelPolicy personTravelPolicy;
	private PaymentDTO paymentDTO;
	private boolean cashPayment = true;
	private String remark;
	private String personTravelProposalId;
	private List<Payment> paymentList;
	private List<PersonTravelPolicy> personTravelPolicyList;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		personTravelProposalId = (String) getParam("personTravelProposalId");
	}

	@PreDestroy
	public void destroy() {
		removeParam("personTravelProposalId");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		personTravelPolicyList = new ArrayList<PersonTravelPolicy>();
		if (personTravelProposalId != null) {
			personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(personTravelProposalId);
			paymentList = paymentService.findByPolicy(personTravelPolicy.getId());
			travelProposal = personTravelProposalService.findPersonTravelProposalById(personTravelProposalId);
			personTravelPolicyList.add(personTravelPolicy);
			PersonTravelProposalInfo proposalInfo = travelProposal.getPersonTravelInfo();
		}
		paymentDTO = new PaymentDTO(paymentList);
	}

	public boolean isCashPayment() {
		return cashPayment;
	}

	public PersonTravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(PersonTravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String paymentTravelProposal() {
		String result = null;
		try {
			String formID = "lifePaymentForm";
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
				return null;
			}
			WorkflowTask workflowTask = WorkflowTask.ISSUING;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), remark, workflowTask, WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL, user, responsiblePerson);
			personTravelProposalService.paymentPersonTravelProposal(travelProposal, workFlowDTO, paymentList, user.getBranch(), RequestStatus.FINISHED.name());

			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	//////////////////////

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public void setPayment(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

	public void selectUser() {
		selectUser(WorkflowTask.ISSUING, WorkFlowType.PERSON_TRAVEL);
	}

	public EnumSet<PaymentChannel> getPaymentChannels() {
		return EnumSet.of(PaymentChannel.CHEQUE, PaymentChannel.CASHED);
	}

	public void changePaymentChannel(AjaxBehaviorEvent e) {
		PaymentChannel paymentChannel = (PaymentChannel) ((SelectOneMenu) e.getSource()).getValue();
		if (paymentChannel.equals(PaymentChannel.CHEQUE)) {
			cashPayment = false;
		} else {
			cashPayment = true;
		}
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getPersonTravelProposalId() {
		return personTravelProposalId;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public List<PersonTravelPolicy> getPersonTravelPolicyList() {
		return personTravelPolicyList;
	}
}
