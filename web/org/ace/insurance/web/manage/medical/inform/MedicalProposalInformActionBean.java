package org.ace.insurance.web.manage.medical.inform;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.document.medical.MedicalUnderwritingDocFactory;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedicalProposalInformActionBean")
public class MedicalProposalInformActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalproposalService;

	public void setMedicalproposalService(IMedicalProposalService medicalproposalService) {
		this.medicalproposalService = medicalproposalService;
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
	private MedicalProposal medicalProposal;
	private WorkFlowDTO workFlowDTO;

	private boolean approvedProposal = true;
	private boolean disablePrintBtn = true;
	private String remark;
	private AcceptedInfo acceptedInfo;
	private MedProDTO medProDTO;

	/**
	 * for inform letter
	 */
	private final String reportNameAcceptedLetter = "MedicalProposalAcceptedLetter";
	private final String pdfDirPathAcceptedLetter = "/pdf-report/" + reportNameAcceptedLetter + "/" + System.currentTimeMillis() + "/";
	private final String dirPathAcceptedLetter = getSystemPath() + pdfDirPathAcceptedLetter;
	private String fileNameAcceptedLetter;

	private final String reportNameRejectLetter = "MedicalProposalRejectLetter";
	private final String pdfDirPathRejectLetter = "/pdf-report/" + reportNameRejectLetter + "/" + System.currentTimeMillis() + "/";
	private final String dirPathRejectLetter = getSystemPath() + pdfDirPathRejectLetter;
	private String fileNameRejectLetter;
	private User responsiblePerson;
	private HealthType healthType;
	private WorkflowReferenceType referenceType;

	private Language language;

	public Date getCurrentDate() {
		return new Date();
	}

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
		healthType = (HealthType) getParam("WORKFLOWHEALTHTYPE");
		switch (healthType) {
			case CRITICALILLNESS:
				referenceType = WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL;
				break;
			case HEALTH:
				referenceType = WorkflowReferenceType.HEALTH_PROPOSAL;
				break;
			case MICROHEALTH:
				referenceType = WorkflowReferenceType.MICRO_HEALTH_PROPOSAL;
				break;
			default:
				break;

		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalproposal");
		removeParam("WorkFlowDTO");
		removeParam("WORKFLOWHEALTHTYPE");
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

		acceptedInfo = new AcceptedInfo();
		acceptedInfo.setReferenceNo(medicalProposal.getId());
		acceptedInfo.setReferenceType(ReferenceType.MEDICAL_PROPOSAL);
		acceptedInfo.setBasicPremium(medicalProposal.getTotalApprovedPremium() + medicalProposal.getTotalBasicPlusPremium());
		acceptedInfo.setAddOnPremium(medicalProposal.getTotalAddOnPremium());
		acceptedInfo.setPaymentType(medicalProposal.getPaymentType());
		acceptedInfo.getNetPremium();
		acceptedInfo.setStampFees(0.0);
		language = Language.MYANMAR;
	}

	public void informApprovedMedicalProposal() {
		try {
			String formID = "informMedicalProposal";
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
				return;
			}
			if (acceptedInfo.getServicesCharges() < 0) {
				addErrorMessage(formID + ":additionalCharges", UIInput.REQUIRED_MESSAGE_ID);
				return;
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), remark, WorkflowTask.CONFIRMATION, referenceType, user, responsiblePerson);
			medicalproposalService.informProposal(medicalProposal, workFlowDTO, acceptedInfo, RequestStatus.APPROVED.name());
			outjectMedicalProposal(medicalProposal);
			outjectWorkFlowDTO(workFlowDTO);
			addInfoMessage(null, MessageId.INFORM_PROCESS_SUCCESS_PARAM, medicalProposal.getProposalNo());
			disablePrintBtn = false;

		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void informRejectMedicalProposal() {
		try {
			String formID = "informMedicalProposal";
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), remark, WorkflowTask.CONFIRMATION, referenceType, user, responsiblePerson);
			medicalproposalService.informProposal(medicalProposal, workFlowDTO, null, RequestStatus.REJECTED.name());
			outjectMedicalProposal(medicalProposal);
			outjectWorkFlowDTO(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.INFORM_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalProposal.getProposalNo());
			disablePrintBtn = false;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String getReportStream() {
		if (approvedProposal) {
			return pdfDirPathAcceptedLetter + fileNameAcceptedLetter;
		} else {
			return pdfDirPathRejectLetter + fileNameRejectLetter;
		}
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(medicalProposal.getId());
	}

	public void returnLanguage(SelectEvent event) {
		this.language = (Language) event.getObject();
		generateReport();
		PrimeFaces.current().executeScript("PF('acceptedLetterDialog').show()");
	}

	public void generateReport() {
		changeFileName(medicalProposal);
		fileNameAcceptedLetter = fileNameAcceptedLetter + "_Inform" + ".pdf";
		fileNameRejectLetter = fileNameRejectLetter + "_Inform" + ".pdf";
		if (checkApproved()) {
			MedicalUnderwritingDocFactory.generateMedicalAcceptanceLetter(medicalProposal, acceptedInfo, dirPathAcceptedLetter, fileNameAcceptedLetter, healthType, language);
		} else {
			MedicalUnderwritingDocFactory.generateMedicalRejectLetter(medicalProposal, dirPathRejectLetter, fileNameRejectLetter, healthType);
		}

	}

	public boolean checkApproved() {
		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
			if (person.isApproved()) {
				approvedProposal = true;
			}
		}
		return approvedProposal;
	}

	public double netAmount() {
		double netAmount = 0.0;

		double discountPercent = acceptedInfo.getDiscountPercent();

		double totalPremium = acceptedInfo.getTotalPremium();
		double discountAmount = (totalPremium / 100) * discountPercent;

		double serviceCharges = acceptedInfo.getServicesCharges();

		netAmount = (totalPremium - discountAmount) + serviceCharges;

		return netAmount;
	}

	private void changeFileName(MedicalProposal medicalProposal) {
		String customerName = medicalProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		switch (healthType) {
			case CRITICALILLNESS:
				fileNameAcceptedLetter = "Critical_Illness_" + customerName;
				fileNameRejectLetter = "Critical_Illness_" + customerName;
				break;
			case HEALTH:
				fileNameAcceptedLetter = "Medical_" + customerName;
				fileNameRejectLetter = "Medical_" + customerName;
				break;
			case MICROHEALTH:
				fileNameAcceptedLetter = "Micro_Health_" + customerName;
				fileNameRejectLetter = "Micro_Health_" + customerName;
				break;
			case MEDICAL_INSURANCE:
			default:
				break;
		}
	}

	public MedProDTO getMedProDTO() {
		return medProDTO;
	}

	public void setMedProDTO(MedProDTO medProDTO) {
		this.medProDTO = medProDTO;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
	}

	public void setMedicalProposal(MedicalProposal medicalProposal) {
		this.medicalProposal = medicalProposal;
	}

	public WorkFlowDTO getWorkFlowDTO() {
		return workFlowDTO;
	}

	public void setWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		this.workFlowDTO = workFlowDTO;
	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public void setApprovedProposal(boolean approvedProposal) {
		this.approvedProposal = approvedProposal;
	}

	public boolean isDisablePrintBtn() {
		return disablePrintBtn;
	}

	public void setDisablePrintBtn(boolean disablePrintBtn) {
		this.disablePrintBtn = disablePrintBtn;
	}

	public AcceptedInfo getAcceptedInfo() {
		return acceptedInfo;
	}

	public void setAcceptedInfo(AcceptedInfo acceptedInfo) {
		this.acceptedInfo = acceptedInfo;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	private void outjectMedicalProposal(MedicalProposal medicalProposal) {
		putParam("medicalProposal", medicalProposal);
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

	public void selectUser() {
		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.MEDICAL_INSURANCE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/MedicalProposalAcceptedLetter");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

}
