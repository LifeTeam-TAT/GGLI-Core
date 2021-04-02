package org.ace.insurance.web.manage.life.studentLife.inform;

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

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "StudentLifeProposalInformActionBean")
public class StudentLifeProposalInformActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private boolean approvedProposal;
	private boolean isAllowedPrint;
	private String remark;
	private User responsiblePerson;
	private AcceptedInfo acceptedInfo;
	// for reject letter
	private final String reportName = "StudentLifeInformLetter";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private String fileName = null;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		if (recalculatePremium(CONFIRMATION)) {
			lifeProposalService.calculatePremium(lifeProposal);
		}
		acceptedInfo = new AcceptedInfo();
		acceptedInfo.setReferenceNo(lifeProposal.getId());
		acceptedInfo.setReferenceType(ReferenceType.STUDENT_LIFE_PROPOSAL);
		acceptedInfo.setBasicPremium(lifeProposal.getApprovedPremium());
		acceptedInfo.setAddOnPremium(lifeProposal.getApprovedAddOnPremium());
		acceptedInfo.setPaymentType(lifeProposal.getPaymentType());
		approvedProposal = true;
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			if (!pv.isApproved()) {
				approvedProposal = false;
				break;
			}
		}
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void selectUser() {
		WorkflowTask workflowTask = WorkflowTask.CONFIRMATION;
		WorkFlowType workFlowType = WorkFlowType.STUDENT_LIFE;
		selectUser(workflowTask, workFlowType);
	}

	public AcceptedInfo getAcceptedInfo() {
		return acceptedInfo;
	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public boolean getIsAllowedPrint() {
		return isAllowedPrint;
	}

	public void informApprovedLifeProposal() {
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.CONFIRMATION, WorkflowReferenceType.STUDENT_LIFE_PROPOSAL, user,
					responsiblePerson);
			lifeProposalService.informProposal(lifeProposal, workFlowDTO, acceptedInfo, RequestStatus.APPROVED.name());
			addInfoMessage(null, MessageId.INFORM_PROCESS_SUCCESS_PARAM, lifeProposal.getProposalNo());
			isAllowedPrint = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void informRejectLifeProposal() {
		try {
			WorkflowTask workflowTask = WorkflowTask.CONFIRMATION;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, WorkflowReferenceType.STUDENT_LIFE_PROPOSAL, user, responsiblePerson);
			lifeProposalService.informProposal(lifeProposal, workFlowDTO, null, RequestStatus.REJECTED.name());
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.INFORM_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());

			isAllowedPrint = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public Date getCurrentDate() {
		return new Date();
	}

	public boolean isVisibleEndorseNetPremium() {
		if (lifeProposal.getLifePolicy() != null && lifeProposal.getEndorsementNetPremium() > 0) {
			return true;
		}
		return false;
	}

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		String customerName = lifeProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "StudentLife_" + customerName + "_Inform" + ".pdf";
		if (approvedProposal) {
			DocumentBuilder.generateStudentLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
		} else {
			DocumentBuilder.generateStudentLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
		}
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double netAmount() {
		double discountPercent = acceptedInfo.getDiscountPercent();
		double totalPremium = acceptedInfo.getTotalPremium();
		double discountAmount = (totalPremium / 100) * discountPercent;
		double serviceCharges = acceptedInfo.getServicesCharges();
		double stampfee = acceptedInfo.getStampFees();
		double netAmount = (totalPremium - discountAmount) + serviceCharges + stampfee;
		return netAmount;
	}

	
	public void openTemplateDialog() {
		putParam("lifeProposal", lifeProposal);
		putParam("workFlowList", getWorkFlowList());
		openStudentLifeInfoTemplate();
	}
	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

}
