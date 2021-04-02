package org.ace.insurance.web.manage.medical.claim.inform;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.claim.service.interfaces.IMedicalClaimProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.document.medical.MedicalUnderwritingDocFactory;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedicalClaimInformActionBean")
public class MedicalClaimInformActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{MedicalClaimProposalService}")
	private IMedicalClaimProposalService medicalClaimProposalService;

	public void setMedicalClaimProposalService(IMedicalClaimProposalService medicalClaimProposalService) {
		this.medicalClaimProposalService = medicalClaimProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private boolean printFlag;
	private String remark;
	private User responsiblePerson;
	private User user;
	private MedicalClaimProposalDTO medicalClaimProposalDTO;
	private ClaimAcceptedInfo claimAcceptedInfo;

	@PostConstruct
	public void init() {
		initializeInjection();
		claimAcceptedInfo = new ClaimAcceptedInfo();
		claimAcceptedInfo.setReferenceNo(medicalClaimProposalDTO.getId());
		claimAcceptedInfo.setClaimAmount(Utils.getTotalClaimAmount(medicalClaimProposalDTO));
		claimAcceptedInfo.setInformDate(new Date());
		claimAcceptedInfo.setReferenceType(ReferenceType.MEDICAL_CLAIM);
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalClaimProposal");
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalClaimProposalDTO = (medicalClaimProposalDTO == null) ? (MedicalClaimProposalDTO) getParam("medicalClaimProposal") : medicalClaimProposalDTO;
	}

	public String informApproveMedicalClaim() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalClaimProposalDTO.getId(), remark, WorkflowTask.CLAIM_CONFIRMATION, WorkflowReferenceType.MEDICAL_CLAIM, user,
					responsiblePerson);
			medicalClaimProposalService.informMedicalClaimProposal(workFlowDTO, claimAcceptedInfo);
			printFlag = true;
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.MEDICAL_ClAIM_INFORM_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalClaimProposalDTO.getClaimRequestId());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	// for acceptance letter
	private final String acceptanceReportName = "MedicalClaimLetter";
	private final String acceptancePdfDirPath = "/pdf-report/" + acceptanceReportName + "/" + System.currentTimeMillis() + "/";
	private final String acceptanceDirPath = getSystemPath() + acceptancePdfDirPath;
	private final String acceptanceDirFileName = acceptanceReportName + ".pdf";

	public String getStream() {
		return acceptancePdfDirPath + acceptanceDirFileName;
	}

	public void generateReport() {
		MedicalUnderwritingDocFactory.generateMedicalClaimLetter(medicalClaimProposalDTO, claimAcceptedInfo, acceptanceDirPath, acceptanceDirFileName);
	}

	public MedicalClaimProposalDTO getMedicalClaimProposalDTO() {
		return medicalClaimProposalDTO;
	}

	public boolean isPrintFlag() {
		return printFlag;
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

	public List<WorkFlowHistory> getWorkflowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(medicalClaimProposalDTO.getId());
	}

	public ClaimAcceptedInfo getClaimAcceptedInfo() {
		return claimAcceptedInfo;
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_INFORM, WorkFlowType.MEDICAL_INSURANCE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(acceptanceDirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
