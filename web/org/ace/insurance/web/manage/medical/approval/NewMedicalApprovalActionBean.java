package org.ace.insurance.web.manage.medical.approval;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
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

/***************************************************************************************
 * @author NNH
 * @Date 2014-08-14
 * @Version 1.0
 * @Purpose This class serves as the Presentation Layer to manipulate the
 *          <code>MedicalProposal</code> approve process.
 * 
 ***************************************************************************************/
@ViewScoped
@ManagedBean(name = "NewMedicalApprovalActionBean")
public class NewMedicalApprovalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

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

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private User user;
	private MedProDTO medProDTO;
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
	private HealthType healthType;
	private WorkflowReferenceType referenceType;
	private boolean basicPlusDisable;

	private BancaassuranceProposal bancaproposal;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalProposalObj = (MedicalProposal) ((medicalProposalObj == null) ? getParam("medicalProposal") : medicalProposalObj);
		bancaproposal = bancaassuranceProposalService.findBancaassuranceProposalByMedicalproposalId(medicalProposalObj.getId());
		medProDTO = MedicalProposalFactory.getMedProDTO(medicalProposalObj);
		workflowHistoryList = workFlowService.findWorkFlowHistoryByRefNo(medProDTO.getId());
		healthType = (HealthType) getParam("WORKFLOWHEALTHTYPE");
		referenceType = null;
		switch (healthType) {
			case HEALTH:
				referenceType = WorkflowReferenceType.HEALTH_PROPOSAL;
				break;
			case CRITICALILLNESS:
				referenceType = WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL;
				break;
			case MICROHEALTH:
				referenceType = WorkflowReferenceType.MICRO_HEALTH_PROPOSAL;
				break;
			case MEDICAL_INSURANCE:
				basicPlusDisable = true;
				break;
			default:
				break;
		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalProposal");
		removeParam("HEALTHTYPE");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		double authourity = user.getAuthority();
		if (authourity >= medProDTO.getTotalPremium()) {
			isApproved = true;
		}
		isAllApproved = true;
		for (MedicalProposalInsuredPerson insuPerson : medicalProposalObj.getMedicalProposalInsuredPersonList()) {
			if (!insuPerson.isApproved()) {
				isAllApproved = false;
			}
		}
	}

	public void detailDialog() {
		if (surveyQuestinList == null || surveyQuestinList.isEmpty()) {
			surveyQuestinList = loadSurveyQuestionAnswer(medProDTO.getId());
		}
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
		return medProDTO;
	}

	public void setMedicalProposal(MedProDTO medicalProposal) {
		this.medProDTO = medicalProposal;
	}

	public String addNewMedicalApproval() {
		String result = null;
		WorkFlowDTO workFlowDTO;

		try {
			workFlowDTO = new WorkFlowDTO(medicalProposalObj.getId(), remark, WorkflowTask.INFORM, referenceType, user, responsiblePerson);
			approvedMedicalProposalFrontService.approveMedicalProposal(medicalProposalObj, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.APPROVAL_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medProDTO.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String redriectMedicalApproval() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(medProDTO.getId(), remark, WorkflowTask.APPROVAL, referenceType, user, responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.REDIRECT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medProDTO.getProposalNo());
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
		selectUser(WorkflowTask.INFORM, WorkFlowType.MEDICAL_INSURANCE);
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

	public boolean isBasicPlusDisable() {
		return basicPlusDisable;
	}

	public BancaassuranceProposal getBancaproposal() {
		return bancaproposal;
	}

}
