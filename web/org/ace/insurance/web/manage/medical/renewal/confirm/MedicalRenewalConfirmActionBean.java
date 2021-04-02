package org.ace.insurance.web.manage.medical.renewal.confirm;

import java.util.List;

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
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedicalRenewalConfirmActionBean")
public class MedicalRenewalConfirmActionBean extends BaseBean {
	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private User user;
	private MedProDTO medProDTO;
	private MedicalProposal medicalProposal;
	private WorkFlowDTO workFlowDTO;
	private boolean approvedProposal = true;
	private String remark;
	private User responsiblePerson;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalProposal = (medicalProposal == null) ? (MedicalProposal) getParam("medicalProposal") : medicalProposal;
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		medProDTO = MedicalProposalFactory.getMedProDTO(medicalProposal);
		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
			if (!person.isApproved()) {
				approvedProposal = false;
				break;
			}
		}

	}

	public MedProDTO getMedProDTO() {
		return medProDTO;
	}

	public void setMedProDTO(MedProDTO medProDTO) {
		this.medProDTO = medProDTO;
	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(medicalProposal.getId());
	}

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
	}

	public void setMedicalProposal(MedicalProposal medicalProposal) {
		this.medicalProposal = medicalProposal;
	}

	public String confirmMedicalProposal() {
		if (responsiblePerson == null) {
			addErrorMessage("medicalConfirmaitonForm:responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			return null;
		}
		WorkflowTask workflowTask = medicalProposal.isSkipPaymentTLF() ? WorkflowTask.RENEWAL_ISSUING : WorkflowTask.RENEWAL_PAYMENT;
		workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), remark, workflowTask, WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, user, responsiblePerson);
		outjectMedicalProposal(medicalProposal);
		outjectWorkFlowDTO(workFlowDTO);
		return "medicalRenewalConfirmPrint";
	}

	public String editMedicalProposal() {
		outjectMedicalProposal(medicalProposal);
		return "confirmEditMedicalRenewalProposal";
	}

	public String denyMedicalProposal() {
		String result = null;
		try {
			if (responsiblePerson == null) {
				responsiblePerson = user;
			}
			if (responsiblePerson == null) {
				responsiblePerson = user;
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), remark, WorkflowTask.PROPOSAL_REJECT, WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, user,
					responsiblePerson);
			medicalProposalService.rejectMedicalProposal(medicalProposal, workFlowDTO);
			outjectMedicalProposal(medicalProposal);
			outjectWorkFlowDTO(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	/*************************************************
	 * Responsible Person Criteria
	 **********************************************/

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	private void outjectMedicalProposal(MedicalProposal medicalProposal) {
		putParam("editMedicalRenewalProposal", medicalProposal);
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

	public void selectUser() {
		WorkflowTask workflowTask = medicalProposal.isSkipPaymentTLF() ? WorkflowTask.ISSUING : WorkflowTask.PAYMENT;
		selectUser(workflowTask, WorkFlowType.MEDICAL_INSURANCE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

}
