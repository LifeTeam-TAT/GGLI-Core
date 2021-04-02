package org.ace.insurance.web.manage.medical.renewal.approval;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.frontService.approvedService.interfaces.IApprovedMedicalProposalFrontService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.ValidationUtil;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.insurance.web.manage.medical.survey.SurveyQuestionAnswerDTO;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedicalRenewalApprovalActionBean")
public class MedicalRenewalApprovalActionBean extends BaseBean {

	@ManagedProperty(value = "#{ApprovedMedicalProposalFrontService}")
	private IApprovedMedicalProposalFrontService approvedMedicalProposalFrontService;

	public void setApprovedMedicalProposalFrontService(IApprovedMedicalProposalFrontService approvedMedicalProposalFrontService) {
		this.approvedMedicalProposalFrontService = approvedMedicalProposalFrontService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private User user;
	private MedProDTO medicalProposal;
	private MedicalProposal medicalProposalObj;
	private MedicalProposalInsuredPerson proposalInsuredPerson;
	private List<MedicalProposalInsuredPerson> selectedproposalInsuredPersoList;
	private String remark;
	private boolean isApproved;
	private boolean needMedicalCheckUp;
	private String rejectReason;
	private List<WorkFlowHistory> workflowHistoryList;
	private List<SurveyQuestionAnswerDTO> surveyQuestinList;
	private User responsiblePerson;
	private boolean isAllApproved;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalProposalObj = (MedicalProposal) ((medicalProposalObj == null) ? getParam("medicalProposal") : medicalProposalObj);
		medicalProposal = MedicalProposalFactory.getMedProDTO(medicalProposalObj);
		workflowHistoryList = workFlowService.findWorkFlowHistoryByRefNo(medicalProposal.getId());
		handleSingleBooleanCheckBox();
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		double authourity = user.getAuthority();
		if (authourity >= medicalProposal.getTotalPremium()) {
			isApproved = true;
		}
		isAllApproved = true;
		for (MedicalProposalInsuredPerson insuPerson : medicalProposalObj.getMedicalProposalInsuredPersonList()) {
			if (!insuPerson.isApproved()) {
				isAllApproved = false;
			}
		}
		surveyQuestinList = loadSurveyQuestionAnswer(medicalProposal.getId());
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

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public MedProDTO getMedicalProposal() {
		return medicalProposal;
	}

	public void setMedicalProposal(MedProDTO medicalProposal) {
		this.medicalProposal = medicalProposal;
	}

	public String addNewMedicalApproval() {
		String result = null;
		WorkFlowDTO workFlowDTO;
		try {
			workFlowDTO = new WorkFlowDTO(medicalProposalObj.getId(), remark, WorkflowTask.RENEWAL_INFORM, WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, user, responsiblePerson);
			approvedMedicalProposalFrontService.approveMedicalProposal(medicalProposalObj, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.APPROVAL_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String redriectMedicalApproval() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), remark, WorkflowTask.RENEWAL_APPROVAL, WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, user,
					responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.REJECT_PROPOSAL_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public boolean isNeedMedicalCheckUp() {
		return needMedicalCheckUp;
	}

	public void setNeedMedicalCheckUp(boolean needMedicalCheckUp) {
		this.needMedicalCheckUp = needMedicalCheckUp;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workflowHistoryList;
	}

	public List<SurveyQuestionAnswerDTO> getSurveyQuestinList() {
		return surveyQuestinList;
	}

	public void saveApproveInfo() {
		PrimeFaces.current().executeScript("PF('medicalApprovalDialog').hide()");
	}

	private boolean validRejectReason() {
		boolean valid = true;
		if (ValidationUtil.isStringEmpty(rejectReason)) {
			valid = false;
		}
		return valid;
	}

	public void selectUser() {
		selectUser(WorkflowTask.PAYMENT, WorkFlowType.MEDICAL_INSURANCE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleSingleBooleanCheckBox() {
		isAllApproved = true;
		for (MedicalProposalInsuredPerson insuPerson : medicalProposalObj.getMedicalProposalInsuredPersonList()) {
			if (!insuPerson.isApproved()) {
				isAllApproved = false;
			}
		}
	}

	public void handleManyBooleanCheckBox() {
		for (MedicalProposalInsuredPerson insuPerson : medicalProposalObj.getMedicalProposalInsuredPersonList()) {
			insuPerson.setApproved(isAllApproved);
		}
	}

	public List<MedicalProposalInsuredPerson> getSelectedproposalInsuredPersoList() {
		return selectedproposalInsuredPersoList;
	}

	public void setSelectedproposalInsuredPersoList(List<MedicalProposalInsuredPerson> selectedproposalInsuredPersoList) {
		for (MedicalProposalInsuredPerson person : selectedproposalInsuredPersoList) {
			person.setApproved(true);
		}
		this.selectedproposalInsuredPersoList = selectedproposalInsuredPersoList;
	}

	public MedicalProposal getMedicalProposalObj() {
		return medicalProposalObj;
	}

	public void setMedicalProposalObj(MedicalProposal medicalProposalObj) {
		this.medicalProposalObj = medicalProposalObj;
	}

	public MedicalProposalInsuredPerson getProposalInsuredPerson() {
		return proposalInsuredPerson;
	}

	public void setProposalInsuredPerson(MedicalProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
	}

	public boolean isAllApproved() {
		return isAllApproved;
	}

	public void setAllApproved(boolean isAllApproved) {
		this.isAllApproved = isAllApproved;
	}

}
