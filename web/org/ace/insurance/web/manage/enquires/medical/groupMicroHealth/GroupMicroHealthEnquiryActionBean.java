package org.ace.insurance.web.manage.enquires.medical.groupMicroHealth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.document.medical.MedicalUnderwritingDocFactory;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.medical.groupMicroHealth.policy.GroupMicroHealthDTO;
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
@ManagedBean(name = "GroupMicroHealthEnquiryActionBean")
public class GroupMicroHealthEnquiryActionBean extends BaseBean {
	@ManagedProperty(value = "#{GroupMicroHealthService}")
	private IGroupMicroHealthService groupMicroHealthService;

	public void setGroupMicroHealthService(IGroupMicroHealthService groupMicroHealthService) {
		this.groupMicroHealthService = groupMicroHealthService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private User user;
	private EnquiryCriteria criteria;
	private List<GroupMicroHealthDTO> groupMicroHealthDTOList;
	private String message;

	@PostConstruct
	public void init() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		resetCriteria();
		loadGroupMicroHealthDTO();
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		loadGroupMicroHealthDTO();
	}

	public void loadGroupMicroHealthDTO() {
		List<GroupMicroHealthDTO> groupMicroHealthDTOList = new ArrayList<>();
		groupMicroHealthDTOList = groupMicroHealthService.findAllProcessCompleteDTO(criteria);
		for (GroupMicroHealthDTO groupMicroHealthDTO : groupMicroHealthDTOList) {
			List<MedicalProposal> medicalProposalList = new ArrayList<>();
			medicalProposalList = medicalProposalService.findMedicalProposalByGroupMicroHealthId(groupMicroHealthDTO.getId());
			groupMicroHealthDTO.setMedicalProposalList(medicalProposalList);
		}
		this.groupMicroHealthDTOList = groupMicroHealthDTOList;
	}

	public String editProposal(GroupMicroHealthDTO dto) {
		if (allowToEdit(dto.getId())) {
			outjectGroupMicroHealthProposal(new GroupMicroHealth(dto));
			return "editGroupMicroHealth";
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
			if (wf.getWorkflowTask().equals(WorkflowTask.UNDERWRITING) || wf.getWorkflowTask().equals(WorkflowTask.APPROVAL) || wf.getWorkflowTask().equals(WorkflowTask.INFORM)
					|| wf.getWorkflowTask().equals(WorkflowTask.CONFIRMATION)) {
				flag = true;
			} else {
				flag = false;
				this.message = "This proposal is not in the editable workflow phase. It's currently at " + wf.getWorkflowTask().getLabel() + " phase";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");

			}
		}
		return flag;
	}

	public void generateLifePolicyLedger(MedicalProposal medicalProposal) {
		String customerName = medicalProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		if (allowToPrintForIssue(medicalProposalService.findMedicalProposalById(medicalProposal.getId()), WorkflowTask.ISSUING)) {
			MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposal.getId());
			reportName = "MedicalPolicyIssue";
			fileName = "Micro_Health_" + customerName + "_Issue" + ".pdf";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			MedicalUnderwritingDocFactory.generateMedicalPolicyIssue(medicalPolicy, null, dirPath, fileName, HealthType.MICROHEALTH, Language.MYANMAR);
			PrimeFaces.current().executeScript("PF('issuePolicyPDFDialogSingle').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.ISSUING.getLabel().toLowerCase());
		}
	}

	private boolean allowToPrintForIssue(MedicalProposal medicalProposal, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(medicalProposal.getId(), workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
			this.message = "Does not allow to print, proposal does not finished  process yet";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
			return false;
		} else {
			return true;
		}
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void returnProduct(SelectEvent event) {
		Product product = (Product) event.getObject();
		criteria.setProduct(product);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		criteria.setSaleMan(saleMan);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		criteria.setReferral(referral);
	}

	String reportName = "";
	String pdfDirPath = "";
	String dirPath = "";
	String fileName = "";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateCashReceipt(GroupMicroHealthDTO dto) {
		if (allowToPrint(dto, WorkflowTask.CONFIRMATION, WorkflowTask.PAYMENT)) {
			List<Payment> paymentList = paymentService.findByProposal(dto.getId(), PolicyReferenceType.GROUP_MICRO_HEALTH, null);
			PaymentDTO payment = new PaymentDTO(paymentList);
			reportName = "MedicalCashReceipt";
			fileName = reportName + ".pdf";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			MedicalUnderwritingDocFactory.generateGroupMicroHealthCashReceipt(new GroupMicroHealth(dto), payment, dirPath, fileName);
			PrimeFaces.current().executeScript("PF('documentCashReceiptDailog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}

	}

	private boolean allowToPrint(GroupMicroHealthDTO proposal, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(proposal.getId(), workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
			this.message = "This proposal can't not print. It's currently at ";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
			return false;
		} else {
			return true;
		}
	}

	public void outjectGroupMicroHealthProposal(GroupMicroHealth groupMicroHealth) {
		putParam("groupMicroHealth", groupMicroHealth);
	}

	public List<GroupMicroHealthDTO> getGroupMicroHealthDTOList() {
		return groupMicroHealthDTOList;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public void setGroupMicroHealthDTOList(List<GroupMicroHealthDTO> groupMicroHealthDTOList) {
		this.groupMicroHealthDTOList = groupMicroHealthDTOList;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
