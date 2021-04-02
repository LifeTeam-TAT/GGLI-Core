package org.ace.insurance.web.manage.life.claim.inform;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimService;
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
@ManagedBean(name = "LifeDeathClaimInformActionBean")
public class LifeDeathClaimInformActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean printFlag;
	private boolean rejectFlag;
	private String approvalStatus;
	private String remark;
	private User responsiblePerson;
	private List<User> userList;
	private List<WorkFlowHistory> workflowList;
	private ClaimAcceptedInfo claimAcceptedInfo;
	private String selectedRespPersonCriteria;
	private List<SelectItem> respPersonCriteriaItemList;
	private RespPersonCriteria respPersonCriteria;
	private List<User> respPersonList;
	private LifeClaim lifeClaim;
	private User user;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{LifeClaimService}")
	private ILifeClaimService lifeClaimService;

	public void setLifeClaimService(ILifeClaimService lifeClaimService) {
		this.lifeClaimService = lifeClaimService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeClaim = (lifeClaim == null) ? (LifeClaim) getParam("lifeClaim") : lifeClaim;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeClaim");
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
		claimAcceptedInfo = new ClaimAcceptedInfo();
		claimAcceptedInfo.setReferenceNo(lifeClaim.getId());
		claimAcceptedInfo.setReferenceType(ReferenceType.LIFE_CLAIM);
		claimAcceptedInfo.setClaimAmount(lifeClaim.getTotalClaimAmount());
		if (lifeClaim != null) {
			if (lifeClaim.getClaimInsuredPerson().isApproved()) {
				rejectFlag = false;
			} else {
				rejectFlag = true;
			}
		}
	}

	/********************************************
	 * Action Controller
	 ********************************************/
	// Responsible User PopUp Click Event
	public void loadUser() {
		userList = userService.findUserByPermission(WorkflowTask.CLAIM_CONFIRMATION, WorkFlowType.LIFE);
	}

	// Detail PopUp Click Event
	public void loadWorkflow() {
		workflowList = workFlowService.findWorkFlowHistoryByRefNo(lifeClaim.getClaimRequestId());
	}

	// Submit Button Click Event
	public void informApproveLifeDeathClaim() {
		try {
			String formID = "lifeDeathClaimCustomerInformForm";
			if (claimAcceptedInfo.getServicesCharges() < 0) {
				addErrorMessage(formID + ":additionalCharges", UIInput.REQUIRED_MESSAGE_ID);
				return;
			}
			if (this.getResponsiblePerson() == null || isEmpty(this.getResponsiblePerson().getName())) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaim.getClaimRequestId(), remark, WorkflowTask.CLAIM_CONFIRMATION, WorkflowReferenceType.LIFE_CLAIM, user,
					responsiblePerson);
			lifeClaimService.informLifeClaim(lifeClaim, workFlowDTO, claimAcceptedInfo);
			/*
			 * ExternalContext extContext =
			 * getFacesContext().getExternalContext();
			 * extContext.getSessionMap().put(Constants.MESSAGE_ID,
			 * MessageId.INFORM_PROCESS_SUCCESS);
			 * extContext.getSessionMap().put(Constants.MESSAGE_ID,
			 * MessageId.INFORM_PROCESS_SUCCESS);
			 */
			addInfoMessage(null, MessageId.INFORM_PROCESS_SUCCESS_PARAM, lifeClaim.getClaimRequestId());
			printFlag = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_CONFIRMATION, WorkFlowType.LIFE);
	}

	/******************************************
	 * End Action Controller
	 ******************************************/
	/**********************************************
	 * Helper
	 *****************************************************/
	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	/********************************************
	 * End Helper
	 ***************************************************/
	/********************************************
	 * Getter/Setter
	 *************************************************/
	public List<User> getUserList() {
		return userList;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workflowList;
	}

	public boolean isPrintFlag() {
		return printFlag;
	}

	public void setPrintFlag(boolean printFlag) {
		this.printFlag = printFlag;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public LifeClaim getLifeClaim() {
		return lifeClaim;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public boolean isRejectFlag() {
		return rejectFlag;
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

	// prepare
	// for reject letter
	private final String reportName = "lifeDeathClaimInformRejectLetter";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	// for acceptance letter
	private final String acceptanceReportName = "lifeDeathClaimInformAcceptanceLetter";
	private final String acceptancePdfDirPath = "/pdf-report/" + acceptanceReportName + "/" + System.currentTimeMillis() + "/";
	private final String acceptanceDirPath = getSystemPath() + acceptancePdfDirPath;
	private final String acceptanceDirFileName = acceptanceReportName + ".pdf";

	public String getStream() {
		if (!rejectFlag) {
			return acceptancePdfDirPath + acceptanceDirFileName;
		} else {
			return pdfDirPath + fileName;
		}
	}

	public void generateReport() {
		if (!rejectFlag) {
			DocumentBuilder.generateLifeDeathClaimAcceptanceLetter(lifeClaim, claimAcceptedInfo, acceptanceDirPath, acceptanceDirFileName);
		} else {
			DocumentBuilder.generateLifeDeathClaimRejectLetter(lifeClaim, claimAcceptedInfo, dirPath, fileName);
		}
	}

	public void returnResponsibleUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleClose(CloseEvent event) {
		try {
			if (!rejectFlag) {
				org.ace.insurance.web.util.FileHandler.forceDelete(acceptanceDirPath);
			} else {
				org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
