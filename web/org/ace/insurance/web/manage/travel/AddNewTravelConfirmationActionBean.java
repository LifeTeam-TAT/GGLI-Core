package org.ace.insurance.web.manage.travel;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewTravelConfirmationActionBean")
public class AddNewTravelConfirmationActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private User user;
	private TravelProposal travelProposal;
	private String remark;
	private User responsiblePerson;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (TravelProposal) getParam("travelProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
	}

	public double getTotalPremium() {
		double totalPremium = 0.0;
		for (TravelExpress express : travelProposal.getExpressList()) {
			totalPremium += express.getNetPremium();
		}
		return totalPremium;
	}

	public String confirmTravelProposal() {
		if (responsiblePerson == null) {
			addErrorMessage("travelProposalConfirmForm:responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			return null;
		}
		WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), remark, WorkflowTask.PAYMENT, WorkflowReferenceType.TRAVEL_PROPOSAL, user, responsiblePerson);
		outjectTravelProposal(travelProposal);
		outjectWorkFlowDTO(workFlowDTO);
		return "confirmTravelProposalPrint";
	}

	private void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("travelProposal", travelProposal);
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		travelProposal.setBranch(branch);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		travelProposal.setPaymentType(paymentType);
	}

	public void selectUser() {
		selectUser(WorkflowTask.PAYMENT, WorkFlowType.TRAVEL);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String editTravelProposal() {
		BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByTravelproposalId(travelProposal.getId());
		putParam("bancaassuranceProposal", bancaassuranceProposal);
		outjectTravelProposal(travelProposal);
		return "editTravelProposal";
	}

	public String denyTravelProposal() {
		String result = null;
		try {
			if (responsiblePerson == null) {
				responsiblePerson = user;
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), remark, WorkflowTask.PROPOSAL_REJECT, WorkflowReferenceType.TRAVEL_PROPOSAL, user, responsiblePerson);
			travelProposalService.rejectTravelProposal(travelProposal, workFlowDTO);
			outjectTravelProposal(travelProposal);
			outjectWorkFlowDTO(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}
}
