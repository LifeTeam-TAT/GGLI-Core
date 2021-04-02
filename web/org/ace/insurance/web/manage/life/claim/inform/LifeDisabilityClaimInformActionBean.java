package org.ace.insurance.web.manage.life.claim.inform;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.model.SelectItem;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RespPersonCriteria;
import org.ace.insurance.common.RespPersonCriteriaItems;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.claim.LifeClaim;
import org.ace.insurance.life.claim.LifeDisabilityClaim;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimService;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author T&D Infomation System Ltd
 * @since 1.0.0
 * @date 2013/07/16
 */
@ViewScoped
@ManagedBean(name = "LifeDisabilityClaimInformActionBean")
public class LifeDisabilityClaimInformActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean approveFlag;
	private boolean printFlag;
	private String remark;
	private String claimRequestId;
	private String approvalStatus;
	private User responsiblePerson;
	private LifeDisabilityClaim disabilityClaim;
	private List<WorkFlowHistory> workflowList;
	private List<User> userList;
	private ClaimAcceptedInfo claimAcceptedInfo;
	private String selectedRespPersonCriteria;
	private List<SelectItem> respPersonCriteriaItemList;
	private RespPersonCriteria respPersonCriteria;
	private List<User> respPersonList;
	private User user;
	private WorkFlowDTO workFlowDTO;
	private LifeClaim claim;
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

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifeClaimService}")
	private ILifeClaimService claimService;

	public void setClaimService(ILifeClaimService claimService) {
		this.claimService = claimService;
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		claim = (claim == null) ? (LifeClaim) getParam("lifeDisabilityClaim") : claim;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		// user
		respPersonCriteria = new RespPersonCriteria();
		respPersonCriteriaItemList = new ArrayList<SelectItem>();
		for (RespPersonCriteriaItems criteriaItem : RespPersonCriteriaItems.values()) {
			respPersonCriteriaItemList.add(new SelectItem(criteriaItem.getLabel(), criteriaItem.getLabel()));
		}
		printFlag = false;
		approveFlag = false;
		disabilityClaim = (LifeDisabilityClaim) claim;
		claimAcceptedInfo = new ClaimAcceptedInfo();
		claimAcceptedInfo.setReferenceNo(claim.getId());
		claimAcceptedInfo.setReferenceType(ReferenceType.LIFE_DIS_CLAIM);
		claimAcceptedInfo.setClaimAmount(disabilityClaim.getClaimInsuredPerson().getClaimAmount());
		if (disabilityClaim.getClaimInsuredPerson().isApproved()) {
			setApprovalStatus("Approved");
			approveFlag = true;
		} else {
			setApprovalStatus("Rejected");
			approveFlag = false;
		}
	}

	/********************************************
	 * Action Controller
	 ********************************************/
	// Detail PopUp Click Event
	public void loadWorkflow() {
		workflowList = workFlowService.findWorkFlowHistoryByRefNo(disabilityClaim.getClaimRequestId());
	}

	// Responsible User PopUp Click Event
	public void loadUser() {
		userList = userService.findUserByPermission(WorkflowTask.CLAIM_CONFIRMATION, WorkFlowType.LIFE);
	}

	// Submit Button Click Event
	public void informApprovedLifeDisabilityClaim() {
		try {
			String formID = "lifeDisabilityClaimCustomerInformForm";
			if (claimAcceptedInfo.getServicesCharges() < 0) {
				addErrorMessage(formID + ":additionalCharges", UIInput.REQUIRED_MESSAGE_ID);
				return;
			}
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			} else {
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(claimRequestId, remark, WorkflowTask.CLAIM_CONFIRMATION, WorkflowReferenceType.LIFE_DIS_CLAIM, user, responsiblePerson);
				claimService.informLifeClaim(disabilityClaim, workFlowDTO, claimAcceptedInfo);
				/*
				 * ExternalContext extContext =
				 * getFacesContext().getExternalContext();
				 * extContext.getSessionMap().put(Constants.MESSAGE_ID,
				 * MessageId.INFORM_PROCESS_SUCCESS);
				 */
				addInfoMessage(null, MessageId.INFORM_PROCESS_SUCCESS_PARAM, disabilityClaim.getClaimRequestId());
				printFlag = true;

			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_CONFIRMATION, WorkFlowType.LIFE);
	}

	// Print Preview Click Event
	/*
	 * public void afterPrint() { printFlag = false; }
	 */
	/******************************************
	 * End Action Controller
	 ******************************************/

	/********************************************
	 * Getter/Setter
	 *************************************************/
	public List<WorkFlowHistory> getWorkFlowList() {
		return workflowList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public boolean isPrintFlag() {
		return printFlag;
	}

	public LifeDisabilityClaim getDisabilityClaim() {
		return disabilityClaim;
	}

	public void setDisabilityClaim(LifeDisabilityClaim disabilityClaim) {
		this.disabilityClaim = disabilityClaim;
	}

	public String getClaimRequestId() {
		return claimRequestId;
	}

	public void setClaimRequestId(String claimRequestId) {
		this.claimRequestId = claimRequestId;
	}

	public void setPrintFlag(boolean printFlag) {
		this.printFlag = printFlag;
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

	public LifeClaim getClaim() {
		return claim;
	}

	public void setLifeClaim(LifeClaim claim) {
		this.claim = claim;
	}

	public boolean isApproveFlag() {
		return approveFlag;
	}

	public void setApproveFlag(boolean approveFlag) {
		this.approveFlag = approveFlag;
	}

	public WorkFlowDTO getWorkFlowDTO() {
		return workFlowDTO;
	}

	public void setWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		this.workFlowDTO = workFlowDTO;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public ClaimAcceptedInfo getClaimAcceptedInfo() {
		return claimAcceptedInfo;
	}

	public void setClaimAcceptedInfo(ClaimAcceptedInfo claimAcceptedInfo) {
		this.claimAcceptedInfo = claimAcceptedInfo;
	}

	/******************************************
	 * End Getter/Setter
	 ***********************************************/
	// user
	public void searchRespPerson() {
		respPersonCriteria.setRespPersonCriteria(null);
		for (RespPersonCriteriaItems criteriaItem : RespPersonCriteriaItems.values()) {
			if (criteriaItem.toString().equals(selectedRespPersonCriteria)) {
				respPersonCriteria.setRespPersonCriteria(criteriaItem);
			}
		}
		if (inputCheck(respPersonCriteria)) {
			respPersonList = userService.findRespPersonByPermission(WorkflowTask.CLAIM_CONFIRMATION, WorkFlowType.LIFE, respPersonCriteria);
		}
	}

	private boolean inputCheck(RespPersonCriteria respPersonCriteria) {
		boolean result = true;
		if (respPersonCriteria.getRespPersonCriteria() == null) {
			addErrorMessage("selectUserForm:respPersonCriteria", MessageId.REQUIRED_ADDON);
			result = false;
		}
		return result;
	}

	public void resetRespPersonCriteria() {
		setSelectedRespPersonCriteria(null);
		respPersonCriteria.setCriteriaValue(null);
		if (respPersonList != null) {
			respPersonList = userService.findRespPersonByPermission(WorkflowTask.CLAIM_CONFIRMATION, WorkFlowType.LIFE);
		}
	}

	public List<User> getRespPersonList() {
		return respPersonList;
	}

	public void loadRespPerson() {
		if (respPersonList == null) {
			respPersonList = userService.findRespPersonByPermission(WorkflowTask.CLAIM_CONFIRMATION, WorkFlowType.LIFE);
		}
	}

	public void loadRespApproverList() {
		if (respPersonList == null) {
			respPersonList = userService.findRespPersonByPermission(WorkflowTask.CLAIM_CONFIRMATION, WorkFlowType.LIFE);
		}
	}

	public String getSelectedRespPersonCriteria() {
		return selectedRespPersonCriteria;
	}

	public void setSelectedRespPersonCriteria(String selectedRespPersonCriteria) {
		this.selectedRespPersonCriteria = selectedRespPersonCriteria;
	}

	public List<SelectItem> getRespPersonCriteriaItemList() {
		return respPersonCriteriaItemList;
	}

	public void setRespPersonCriteriaItemList(List<SelectItem> respPersonCriteriaItemList) {
		this.respPersonCriteriaItemList = respPersonCriteriaItemList;
	}

	public RespPersonCriteria getRespPersonCriteria() {
		return respPersonCriteria;
	}

	public void setRespPersonCriteria(RespPersonCriteria respPersonCriteria) {
		this.respPersonCriteria = respPersonCriteria;
	}

	// prepare disability claim

	// for reject letter
	private final String reportName = "lifeDisabilityClaimInformRejectLetter";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	// for acceptance letter
	private final String acceptanceReportName = "lifeDisabilityClaimInformAcceptanceLetter";
	private final String acceptancePdfDirPath = "/pdf-report/" + acceptanceReportName + "/" + System.currentTimeMillis() + "/";
	private final String acceptanceDirPath = getSystemPath() + acceptancePdfDirPath;
	private final String acceptanceDirFileName = acceptanceReportName + ".pdf";

	public String getStream() {
		if (approveFlag) {
			return acceptancePdfDirPath + acceptanceDirFileName;
		} else {
			return pdfDirPath + fileName;
		}
	}

	public void generateReport() {
		if (approveFlag) {
			DocumentBuilder.generateLifeDisabilityClaimAcceptanceLetter(disabilityClaim, claimAcceptedInfo, acceptanceDirPath, acceptanceDirFileName);
		} else {
			DocumentBuilder.generateLifeDisabilityClaimRejectLetter(disabilityClaim, claimAcceptedInfo, dirPath, fileName);
		}
	}

	public void returnResponsibleUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleClose(CloseEvent event) {
		try {
			if (approveFlag) {
				org.ace.insurance.web.util.FileHandler.forceDelete(acceptanceDirPath);
			} else {
				org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
