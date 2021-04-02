package org.ace.insurance.web.manage.enquires.medical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MP001;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalSurvey;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.common.document.medical.MedicalUnderwritingDocFactory;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.insurance.web.manage.medical.survey.SurveyQuestionAnswerDTO;
import org.ace.insurance.workflow.WorkFlow;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedicalRenewalEnquiryActionBean")
public class MedicalRenewalEnquiryActionBean extends BaseBean {
	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	@ManagedProperty(value = "#{AcceptedInfoService}")
	private IAcceptedInfoService acceptedInfoService;

	public void setAcceptedInfoService(IAcceptedInfoService acceptedInfoService) {
		this.acceptedInfoService = acceptedInfoService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}

	private String userType;
	boolean approved = false;
	private boolean accessBranches;
	private EnquiryCriteria criteria;
	private MedicalProposal medicalProposal;
	private MedProDTO medProDTO;
	private PaymentDTO paymentDTO;
	private MP001 selectedProposal;
	private AcceptedInfo acceptedInfo;
	private User user;
	private List<MP001> proposalList;
	private List<SurveyQuestionAnswerDTO> surveyQuestinList;
	private HealthType healthType;
	private Language language;

	@PostConstruct
	public void init() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}
		proposalList = new ArrayList<MP001>();
		resetCriteria();
		selectedProposal = new MP001();
		healthType = (HealthType) getParam("WORKFLOWHEALTHTYPE");
		language = Language.MYANMAR;
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setProposalType(ProposalType.RENEWAL);
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		if (!accessBranches)
			criteria.setBranch(user.getBranch());
		search();
	}

	public void search() {
		proposalList = medicalProposalService.findAllMedicalPolicy(criteria);
	}

	public MP001 getSelectedProposal() {
		return selectedProposal;
	}

	public void setSelectedProposal(MP001 selectedProposal) {
		this.selectedProposal = selectedProposal;
	}

	public AcceptedInfo getAcceptedInfo() {
		return acceptedInfo;
	}

	public void setAcceptedInfo(AcceptedInfo acceptedInfo) {
		this.acceptedInfo = acceptedInfo;
	}

	public MedProDTO getMedProDTO() {
		return medProDTO;
	}

	public void setMedProDTO(MedProDTO medProDTO) {
		this.medProDTO = medProDTO;
	}

	private void showInformationDialog(String msg) {
		this.message = msg;
		PrimeFaces.current().executeScript("PF('informationDialog').show()");
	}

	public void medicalProposalDetail(MP001 mp001) {
		medProDTO = MedicalProposalFactory.getMedProDTO(medicalProposalService.findMedicalProposalById(mp001.getId()));
		surveyQuestinList = loadSurveyQuestionAnswer(medProDTO.getId());
	}

	public String editMedicalProposal(MP001 mp001) {
		medProDTO = MedicalProposalFactory.getMedProDTO(medicalProposalService.findMedicalProposalById(mp001.getId()));
		if (allowToEdit(medProDTO.getId())) {
			outjectMedicalProposal(MedicalProposalFactory.getMedicalProposal(medProDTO));
			return "enquiryEditMedicalRenewalProposal";
		} else {
			return null;
		}
	}

	private boolean allowToEdit(String refNo) {
		boolean flag = true;
		WorkFlow wf = workFlowService.findWorkFlowByRefNo(refNo);
		if (wf == null) {
			flag = false;
			this.message = "This proposal has been legalized as policy contract.";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
		} else {
			if (WorkflowTask.RENEWAL_SURVEY.equals(wf.getWorkflowTask()) || WorkflowTask.RENEWAL_APPROVAL.equals(wf.getWorkflowTask())
					|| WorkflowTask.RENEWAL_INFORM.equals(wf.getWorkflowTask()) || WorkflowTask.RENEWAL_CONFIRMATION.equals(wf.getWorkflowTask())) {
				flag = true;
			} else {
				flag = false;
				this.message = "This proposal is not in the editable workflow phase. It's currently at " + wf.getWorkflowTask().getLabel() + " phase";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");
			}
		}
		return flag;
	}

	/* for inform letter */
	private boolean allowToPrint(String id, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(id, workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(medicalProposal.getId());
	}

	private String message;

	public String getMessage() {
		return message;
	}

	public void outjectMedicalProposal(MedicalProposal medicalProposal) {
		putParam("editEnquiryMedicalRenewalProposal", medicalProposal);
	}

	public MedicalPolicy getMedicalPolicy() {
		MedicalPolicy medicalPolicy = null;
		medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposal.getId());
		return medicalPolicy;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public List<MP001> getProposalList() {
		RegNoSorter<MP001> regNoSorter = new RegNoSorter<MP001>(proposalList);
		List<MP001> proposalList = regNoSorter.getSortedList();
		return proposalList;
	}

	public void findMedicalProposalListByEnquiryCriteria() {
		proposalList = medicalProposalService.findAllMedicalPolicy(criteria);
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			criteria.setSaleMan(null);
			criteria.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			criteria.setAgent(null);
			criteria.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			criteria.setSaleMan(null);
			criteria.setAgent(null);
		}
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public void setProposalList(List<MP001> proposalList) {
		this.proposalList = proposalList;
	}

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
	}

	public void setMedicalProposal(MedicalProposal medicalProposal) {
		this.medicalProposal = medicalProposal;
	}

	public String getUserType() {
		if (userType == null) {
			userType = UserType.AGENT.toString();
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		criteria.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		criteria.setOrganization(organization);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		criteria.setSaleMan(saleMan);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		criteria.setReferral(referral);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public List<SurveyQuestionAnswerDTO> getSurveyQuestinList() {
		return surveyQuestinList;
	}

	/*************** Accepted letter *************/
	private final String reportNameAcceptedLetter = "MedicalProposalAcceptedLetter";
	private final String pdfDirPathAcceptedLetter = "/pdf-report/" + reportNameAcceptedLetter + "/" + System.currentTimeMillis() + "/";
	private final String dirPathAcceptedLetter = getSystemPath() + pdfDirPathAcceptedLetter;
	private final String fileNameAcceptedLetter = reportNameAcceptedLetter + ".pdf";
	private final String reportNameRejectLetter = "MedicalProposalRejectLetter";
	private final String pdfDirPathRejectLetter = "/pdf-report/" + reportNameRejectLetter + "/" + System.currentTimeMillis() + "/";
	private final String dirPathRejectLetter = getSystemPath() + pdfDirPathRejectLetter;
	private final String fileNameRejectLetter = reportNameRejectLetter + ".pdf";

	public void generateAcceptedLetter(MP001 mProposal) {
		acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(mProposal.getId(), ReferenceType.MEDICAL_RENEWAL_PROPOSAL);
		this.medicalProposal = medicalProposalService.findMedicalProposalById(mProposal.getId());
		if (allowToPrint(medicalProposal.getId(), WorkflowTask.RENEWAL_CONFIRMATION, WorkflowTask.RENEWAL_PAYMENT, WorkflowTask.RENEWAL_ISSUING)) {
			if (checkApproved(medicalProposal)) {
				MedicalUnderwritingDocFactory.generateMedicalAcceptanceLetter(medicalProposal, acceptedInfo, dirPathAcceptedLetter, fileNameAcceptedLetter, healthType, language);
			} else {
				MedicalUnderwritingDocFactory.generateMedicalRejectLetter(medicalProposal, dirPathRejectLetter, fileNameRejectLetter, healthType);
			}
			PrimeFaces.current().executeScript("PF('acceptedLetterDialog').show()");

		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.RENEWAL_INFORM.getLabel().toLowerCase()));
		}
	}

	public boolean checkApproved(MedicalProposal medicalProposal) {
		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
			if (person.isApproved()) {
				approved = true;
			}
		}
		return approved;
	}

	public String getAcceptedStream() {
		if (approved) {
			return pdfDirPathAcceptedLetter + fileNameAcceptedLetter;
		} else {
			return pdfDirPathRejectLetter + fileNameRejectLetter;
		}
	}

	public void handleCloseInform(CloseEvent event) {
		try {
			if (approved) {
				org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/MedicalProposalAcceptedLetter");
			} else {
				org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/MedicalProposalRejectLetter");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*************** Sanction letter *************/

	private final String reportNameSanction = "MedicalSanction";
	private final String pdfDirPathSanction = "/pdf-report/" + reportNameSanction + "/" + System.currentTimeMillis() + "/";
	private final String dirPathSanction = getWebRootPath() + pdfDirPathSanction;
	private final String fileNameSanction = reportNameSanction + ".pdf";

	public void generateSanction(MP001 medicalProposal) {
		if (allowToPrint(medicalProposal.getId(), WorkflowTask.RENEWAL_INFORM, WorkflowTask.APPROVAL, WorkflowTask.RENEWAL_CONFIRMATION, WorkflowTask.RENEWAL_PAYMENT,
				WorkflowTask.RENEWAL_ISSUING)) {
			MedicalSurvey survey = medicalProposalService.findMedicalSurveyByProposalId(medicalProposal.getId());
			MedicalUnderwritingDocFactory.generateMedicalSanction(survey, dirPathSanction, fileNameSanction, healthType);
			PrimeFaces.current().executeScript("PF('sanctionPrintDialog').show()");
		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.RENEWAL_SURVEY.getLabel().toLowerCase()));
		}
	}

	public String getSanctionStream() {
		return pdfDirPathSanction + fileNameSanction;
	}

	public void handleCloseSanction(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/MedicalSanction");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*************** CashReceipt letter *************/
	private final String reportNameCashReceipt = "MedicalCashReceipt";
	private final String pdfDirPathCashReceipt = "/pdf-report/" + reportNameCashReceipt + "/" + System.currentTimeMillis() + "/";
	private final String dirPathCashReceipt = getWebRootPath() + pdfDirPathCashReceipt;
	private final String fileNameCashReceipt = reportNameCashReceipt + ".pdf";

	public void generateCashReceipt(MP001 mp001) {
		MedicalProposal medicalProposal = medicalProposalService.findMedicalProposalById(mp001.getId());
		if (allowToPrint(medicalProposal.getId(), WorkflowTask.RENEWAL_ISSUING, WorkflowTask.RENEWAL_PAYMENT)) {
			MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposal.getId());
			List<Payment> paymentList = paymentService.findByPolicy(medicalPolicy.getId());
			paymentDTO = new PaymentDTO(paymentList);
			MedicalUnderwritingDocFactory.generateMedicalCashReceipt(medicalProposal, paymentDTO, dirPathCashReceipt, fileNameCashReceipt, healthType);
			PrimeFaces.current().executeScript("PF('cashReceiptDialog').show()");
		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.RENEWAL_CONFIRMATION.getLabel().toLowerCase()));

		}
	}

	public String getCashReceiptStream() {
		return pdfDirPathCashReceipt + fileNameCashReceipt;
	}

	public void handleCloseCashReceipt(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/MedicalCashReceipt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*************** Policy Issue letter *************/
	private final String reportNamePolicyIssue = "MedicalPolicyIssue";
	private final String pdfDirPathPolicyIssue = "/pdf-report/" + reportNamePolicyIssue + "/" + System.currentTimeMillis() + "/";
	private final String dirPathPolicyIssue = getSystemPath() + pdfDirPathPolicyIssue;
	private final String fileNamePolicyIssue = reportNamePolicyIssue + ".pdf";

	public String getPolicyIssueStream() {
		return pdfDirPathPolicyIssue + fileNamePolicyIssue;
	}

	public void generatePolicyIssue(MP001 medicalProposal) {
		if (allowToPrint(medicalProposal.getId(), WorkflowTask.RENEWAL_ISSUING)) {
			MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposal.getId());
			Payment payment = paymentService.findPaymentByReferenceNo(medicalPolicy.getId());
			MedicalUnderwritingDocFactory.generateMedicalPolicyIssue(medicalPolicy, payment, dirPathPolicyIssue, fileNamePolicyIssue, healthType, language);
			PrimeFaces.current().executeScript("PF('issuePolicyPrintDialogSingle').show()");
		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.RENEWAL_ISSUING.getLabel().toLowerCase()));
		}
	}

	public void handleCloseIssue(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/MedicalPolicyIssue");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

}
