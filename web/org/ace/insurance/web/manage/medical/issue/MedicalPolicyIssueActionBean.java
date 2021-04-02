package org.ace.insurance.web.manage.medical.issue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.ExportExcel;
import org.ace.insurance.web.common.document.medical.MedicalUnderwritingDocFactory;
import org.ace.insurance.web.export.PolicyIssueExportExcel;
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
@ManagedBean(name = "MedicalPolicyIssueActionBean")
public class MedicalPolicyIssueActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

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
	private List<MedicalPolicy> medicalPolicyList;
	private MedicalProposal medicalProposal;
	private MedProDTO medProDTO;
	private MedicalPolicy medicalPolicy;
	private boolean nilExcess;
	private boolean showPreview;
	private PaymentDTO paymentDTO;
	private HealthType healthType;
	private Language language;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalProposal = (medicalProposal == null) ? (MedicalProposal) getParam("medicalProposal") : medicalProposal;
		healthType = (HealthType) getParam("WORKFLOWHEALTHTYPE");
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		medicalPolicyList = new ArrayList<MedicalPolicy>();
		medProDTO = MedicalProposalFactory.getMedProDTO(medicalProposal);
		medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposal.getId());
		medicalPolicyList.add(medicalPolicy);
		medicalProposal = medicalProposalService.findMedicalProposalById(medicalProposal.getId());
		language = Language.MYANMAR;

	}

	public PaymentDTO getPaymentDTO() {
		return paymentDTO;
	}

	public void setPaymentDTO(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

	public MedProDTO getMedProDTO() {
		return medProDTO;
	}

	public void setMedProDTO(MedProDTO medProDTO) {
		this.medProDTO = medProDTO;
	}

	public void preparePolicyIssue(MedicalPolicy medicalPolicy) {
		this.medicalPolicy = medicalPolicy;
	}

	public void changeNillExcessValue(MedicalPolicy medicalPolicy) {
		medicalPolicy.setNilExcess(nilExcess);
	}

	public void issuePolicy() {
		try {
			medicalProposalService.issueMedicalProposal(medicalPolicy);
			this.showPreview = true;
			addInfoMessage(null, MessageId.ISSUING_PROCESS_SUCCESS_PARAM, medicalProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void increasePrintCount() {
		// medicalPolicyService.increaseMedicalPolicyPrintCount(medicalPolicy.getId());
	}

	public void exportPolicyIssueExcel() {
		List<Payment> paymentList = paymentService.findByPolicy(medicalPolicy.getId());
		paymentDTO = new PaymentDTO(paymentList);
		ExternalContext ec = getFacesContext().getExternalContext();
		ec.responseReset();
		ec.setResponseContentType("application/vnd.ms-excel");
		String reportNamePolicyIssue = "MedicalPolicyIssue";
		String fileName = medicalPolicy.getPolicyNo() + "(" + reportNamePolicyIssue + ")" + ".xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try {
			OutputStream op = ec.getResponseOutputStream();
			ExportExcel policyIssueExportExcel = new PolicyIssueExportExcel(medicalPolicy, paymentDTO);
			policyIssueExportExcel.generate(op);
			getFacesContext().responseComplete();

		} catch (IOException e) {
			throw new SystemException(null, "Failed to export " + medicalPolicy.getPolicyNo() + " MedicalPolicyIssue.xlsx", e);
		}
	}

	public List<MedicalPolicy> getMedicalPolicyList() {
		return medicalPolicyList;
	}

	public void setMedicalPolicyList(List<MedicalPolicy> medicalPolicyList) {
		this.medicalPolicyList = medicalPolicyList;
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

	public MedicalPolicy getMedicalPolicy() {
		return medicalPolicy;
	}

	public void setMedicalPolicy(MedicalPolicy medicalPolicy) {
		this.medicalPolicy = medicalPolicy;
	}

	public boolean getNilExcess() {
		return nilExcess;
	}

	public void setNilExcess(boolean nilExcess) {
		this.nilExcess = nilExcess;
	}

	public boolean getShowPreview() {
		return showPreview;
	}

	private final String reportName = "MedicalPolicyIssue";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName;

	private MedicalPolicy printedPolicy;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport(MedicalPolicy medicalPolicy) {
		changeFileName(medicalPolicy);
		fileName = fileName + "_Issue" + ".pdf";
		Payment payment = paymentService.findPaymentByReferenceNo(medicalPolicy.getId());
		MedicalUnderwritingDocFactory.generateMedicalPolicyIssue(medicalPolicy, payment, dirPath, fileName, healthType, language);
	}

	public void returnLanguage(SelectEvent event) {
		this.language = (Language) event.getObject();
		generateReport(printedPolicy);
		PrimeFaces.current().executeScript("PF('issuePolicyPDFDialog').show()");
	}

	private void changeFileName(MedicalPolicy medicalPolicy) {
		String customerName = medicalProposal.getCustomerName();
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

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
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

	public void setPrintedPolicy(MedicalPolicy printedPolicy) {
		this.printedPolicy = printedPolicy;
	}

}
