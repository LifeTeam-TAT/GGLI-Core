package org.ace.insurance.web.manage.life.surrender.confirm;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.life.surrender.service.interfaces.ILifeSurrenderProposalService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifeSurrenderConfirmActionBean")
public class LifeSurrenderConfirmActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String remark;
	private User responsiblePerson;
	private LifeSurrenderProposal surrenderProposal;
	private User user;
	private List<WorkFlowHistory> workFlowList;
	private List<PaymentTrackDTO> paymentList;
	private boolean isEndowmentSurrender;

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

	@ManagedProperty(value = "#{LifeSurrenderProposalService}")
	private ILifeSurrenderProposalService surrderProposalService;

	public void setSurrderProposalService(ILifeSurrenderProposalService surrderProposalService) {
		this.surrderProposalService = surrderProposalService;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		isEndowmentSurrender = surrenderProposal.getClaimProduct().getId().equals(KeyFactorChecker.getLifeSurrenderProductId());
		workFlowList = workFlowService.findWorkFlowHistoryByRefNo(surrenderProposal.getId());
		paymentList = paymentService.findPaymentTrack(surrenderProposal.getPolicyNo());
	}

	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
		surrenderProposal = (LifeSurrenderProposal) getParam("surrenderProposal");
	}

	public String confirmSurrenderApproval() {
		WorkflowReferenceType referenceType = isEndowmentSurrender ? WorkflowReferenceType.LIFESURRENDER_PROPOSAL : WorkflowReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL;
		WorkFlowDTO workFlowDTO = new WorkFlowDTO(surrenderProposal.getId(), remark, WorkflowTask.PAYMENT, referenceType, user, responsiblePerson);
		outjectWorkFlowDTO(workFlowDTO);
		return "printLifeSurrenderReceipt";
	}

	public String editSurrenderApproval() {
		return "editLifeSurrenderProposal";
	}

	public String denySurrenderApproval() {
		String result = null;
		try {
			responsiblePerson = (responsiblePerson == null) ? user : responsiblePerson;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(surrenderProposal.getId(), remark, WorkflowTask.PROPOSAL_REJECT, WorkflowReferenceType.LIFESURRENDER_PROPOSAL, user,
					responsiblePerson);
			surrderProposalService.rejectLifeSurrenderProposal(surrenderProposal, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, surrenderProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;

	}

	public void selectUser() {
		selectUser(WorkflowTask.PAYMENT, WorkFlowType.LIFESURRENDER);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public List<PaymentTrackDTO> getPaymentList() {
		return paymentList;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public boolean isEndowmentSurrender() {
		return isEndowmentSurrender;
	}

	public LifeSurrenderProposal getSurrenderProposal() {
		return surrenderProposal;
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

}
