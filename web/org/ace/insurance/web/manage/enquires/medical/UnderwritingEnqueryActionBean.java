package org.ace.insurance.web.manage.enquires.medical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MP001;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalSurvey;
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
@ManagedBean(name = "UnderwritingEnqueryActionBean")
public class UnderwritingEnqueryActionBean extends BaseBean {
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

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	/*
	 * @ManagedProperty(value = "#{MedicalProposalService}") private
	 * IMedicalProposalService medicalProposalService;
	 * 
	 * public void setMedicalProposalService(IMedicalProposalService
	 * medicalProposalService) { this.medicalProposalService =
	 * medicalProposalService; }
	 */

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
	private MP001 printedProposal;

	private BancaassuranceProposal bancaproposal;

	@PostConstruct
	public void init() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}
		resetCriteria();
		selectedProposal = new MP001();
		language = Language.MYANMAR;
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		if (!accessBranches) {
			criteria.setBranch(user.getBranch());
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setProposalType(ProposalType.UNDERWRITING);
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		healthType = null;
		proposalList = new ArrayList<>();

	}

	public void search() {

		proposalList = medicalProposalService.findAllMedicalPolicy(criteria);
	}

	public void clearList() {
		proposalList = new ArrayList<>();
	}

	public void sortLists(List<MP001> proposalList) {
		Collections.sort(proposalList, new Comparator<MP001>() {
			@Override
			public int compare(MP001 obj1, MP001 obj2) {
				if (obj1.getProposalNo().equals(obj2.getProposalNo()))
					return -1;
				else
					return obj1.getProposalNo().compareTo(obj2.getProposalNo());
			}
		});
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
		bancaproposal = bancaassuranceProposalService.findBancaassuranceProposalByMedicalproposalId(medProDTO.getId());
	}

	public String editMedicalProposal(MP001 mp001) {
		String result = null;
		medProDTO = MedicalProposalFactory.getMedProDTO(medicalProposalService.findMedicalProposalById(mp001.getId()));
		BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByMedicalproposalId(mp001.getId());
		if (allowToEdit(medProDTO.getId())) {
			String productId = medProDTO.getMedProInsuDTOList().get(0).getProduct().getId();
			HealthType healthType = null;
			if (ProductIDConfig.getMedicalProductId().equals(productId)) {
				healthType = HealthType.MEDICAL_INSURANCE;
			} else if (ProductIDConfig.getIndividualCriticalIllness_Id().equals(productId) || ProductIDConfig.getGroupCriticalIllness_Id().equals(productId)) {
				healthType = HealthType.CRITICALILLNESS;
			} else if (ProductIDConfig.getIndividualHealthInsuranceId().equals(productId) || ProductIDConfig.getGroupHealthInsuranceId().equals(productId)) {
				healthType = HealthType.HEALTH;
			} else if (ProductIDConfig.getMicroHealthInsurance().equals(productId)) {
				healthType = HealthType.MICROHEALTH;
			}
			if (null != healthType) {
				putParam("bancaassuranceProposal", bancaassuranceProposal);
				outjectHealthType(healthType);
			}
			putParam("bancaassuranceProposal", bancaassuranceProposal);
			outjectMedicalProposal(MedicalProposalFactory.getMedicalProposal(medProDTO));
			result = "editMedicalProposal";
		} else {
			result = null;
		}
		return result;
	}

	private boolean allowToEdit(String refNo) {
		boolean flag = true;
		WorkFlow wf = workFlowService.findWorkFlowByRefNo(refNo);
		if (wf == null) {
			flag = false;
			this.message = "This proposal has been legalized as policy contract.";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
		} else {
			if (WorkflowTask.UNDERWRITING.equals(wf.getWorkflowTask()) || WorkflowTask.SURVEY.equals(wf.getWorkflowTask()) || WorkflowTask.INFORM.equals(wf.getWorkflowTask())
					|| WorkflowTask.CONFIRMATION.equals(wf.getWorkflowTask()) || WorkflowTask.APPROVAL.equals(wf.getWorkflowTask())) {
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
		return workFlowService.findWorkFlowHistoryByRefNo(medProDTO.getId());
	}

	private String message;

	public String getMessage() {
		return message;
	}

	public void outjectMedicalProposal(MedicalProposal medicalProposal) {
		putParam("enquiryEditMedicalProposal", medicalProposal);
	}

	private void outjectHealthType(HealthType healthType) {
		putParam("HEALTHTYPE", healthType);
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
		if (null != healthType) {
			switch (healthType) {
				case CRITICALILLNESS:
					this.criteria.setProductIdList(Arrays.asList(ProductIDConfig.getIndividualCriticalIllness_Id(), ProductIDConfig.getGroupCriticalIllness_Id()));
					break;
				case HEALTH:
					this.criteria.setProductIdList(Arrays.asList(ProductIDConfig.getIndividualHealthInsuranceId(), ProductIDConfig.getGroupHealthInsuranceId()));
					break;
				case MEDICAL_INSURANCE:
					this.criteria.setProductIdList(Arrays.asList(ProductIDConfig.getMedicalInsurance()));
					break;
				case MICROHEALTH:
					this.criteria.setProductIdList(Arrays.asList(ProductIDConfig.getMicroHealthInsurance()));
					break;
				default:
					break;

			}
		}
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

	/*************** Sanction letter *************/

	private final String reportName = "Medical";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName = reportName + ".pdf";

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/Medical");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateSanction(MP001 medicalProposal) {
		boolean allowToPrint = false;
		changeFileName(medicalProposal);
		fileName = fileName + "_Sanction" + ".pdf";
		allowToPrint = allowToPrint(medicalProposal.getId(), WorkflowTask.INFORM, WorkflowTask.APPROVAL, WorkflowTask.CONFIRMATION, WorkflowTask.PAYMENT, WorkflowTask.ISSUING);
		if (allowToPrint) {
			MedicalSurvey survey = medicalProposalService.findMedicalSurveyByProposalId(medicalProposal.getId());
			MedicalUnderwritingDocFactory.generateMedicalSanction(survey, dirPath, fileName, healthType);
			PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.SURVEY.getLabel().toLowerCase()));
		}
	}

	public void generateCashReceipt(MP001 proposal) {
		boolean allowToPrint = false;
		changeFileName(proposal);
		fileName = fileName + "_Receipt" + ".pdf";
		allowToPrint = allowToPrint(proposal.getId(), WorkflowTask.ISSUING, WorkflowTask.PAYMENT);
		if (allowToPrint) {
			MedicalProposal medicalProposal = medicalProposalService.findMedicalProposalById(proposal.getId());
			MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposal.getId());
			Payment payment = paymentService.findPaymentForCashReceipt(medicalPolicy.getLastPaymentTerm(), medicalPolicy.getId());
			paymentDTO = new PaymentDTO(payment);
			MedicalUnderwritingDocFactory.generateMedicalCashReceipt(medicalProposal, paymentDTO, dirPath, fileName, healthType);
			PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase()));

		}
	}

	public void generatePolicyIssue(MP001 medicalProposal) {
		boolean allowToPrint = false;
		changeFileName(medicalProposal);
		fileName = fileName + "_Issue" + ".pdf";
		allowToPrint = allowToPrint(medicalProposal.getId(), WorkflowTask.ISSUING);
		if (allowToPrint) {
			MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposal.getId());
			Payment payment = paymentService.findPaymentByReferenceNoAndTermOne(medicalPolicy.getId());
			MedicalUnderwritingDocFactory.generateMedicalPolicyIssue(medicalPolicy, payment, dirPath, fileName, healthType, language);
			PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.ISSUING.getLabel().toLowerCase()));
		}
	}

	public void generateAcceptedLetter(MP001 mProposal) {
		boolean allowToPrint = false;
		changeFileName(mProposal);
		fileName = fileName + "_Inform" + ".pdf";
		allowToPrint = allowToPrint(mProposal.getId(), WorkflowTask.CONFIRMATION, WorkflowTask.PAYMENT, WorkflowTask.ISSUING);
		if (allowToPrint) {
			acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(mProposal.getId(), ReferenceType.MEDICAL_PROPOSAL);
			medicalProposal = medicalProposalService.findMedicalProposalById(mProposal.getId());
			if (checkApproved()) {
				MedicalUnderwritingDocFactory.generateMedicalAcceptanceLetter(medicalProposal, acceptedInfo, dirPath, fileName, healthType, language);
			} else {
				MedicalUnderwritingDocFactory.generateMedicalRejectLetter(medicalProposal, dirPath, fileName, healthType);
			}
			PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.INFORM.getLabel().toLowerCase()));
		}
	}

	public void returnAcceptLetterLanguage(SelectEvent event) {
		this.language = (Language) event.getObject();
		generateAcceptedLetter(printedProposal);
		// PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		this.language = null;
	}

	public void returnPolicyIssueLanguage(SelectEvent event) {
		this.language = (Language) event.getObject();
		generatePolicyIssue(printedProposal);
		// PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		this.language = null;
	}

	public void generateAcceptLetter() {
		this.language = (Language) getParam("printLanguage");
		generateAcceptedLetter(printedProposal);
		// PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		removeParam("printLanguage");
	}

	private void changeFileName(MP001 medicalProposal) {
		String customerName = medicalProposal.getCustomer();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		switch (healthType) {
			case CRITICALILLNESS:
				fileName = "Critical_Illness_" + customerName;
				break;
			case HEALTH:
				fileName = "Medical_" + customerName;
				break;
			case MICROHEALTH:
				fileName = "Micro_Health_" + customerName;
				break;
			case MEDICAL_INSURANCE:
			default:
				break;
		}
	}

	public boolean checkApproved() {
		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
			if (person.isApproved()) {
				approved = true;
			}
		}
		return approved;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public HealthType getHealthType() {
		return healthType;
	}

	public void setHealthType(HealthType healthType) {
		this.healthType = healthType;
	}

	public HealthType[] getHealthTypes() {
		return HealthType.values();
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setPrintedProposal(MP001 printedProposal) {
		this.printedProposal = printedProposal;
	}

	public BancaassuranceProposal getBancaproposal() {
		return bancaproposal;
	}

	public void setBancaproposal(BancaassuranceProposal bancaproposal) {
		this.bancaproposal = bancaproposal;
	}

}
