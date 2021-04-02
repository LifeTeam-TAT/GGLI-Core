package org.ace.insurance.web.manage.enquires.life;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.LPL001;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
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
@ManagedBean(name = "RenewalGroupLifeEnquiryActionBean")
public class RenewalGroupLifeEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{AcceptedInfoService}")
	private IAcceptedInfoService acceptedInfoService;

	public void setAcceptedInfoService(IAcceptedInfoService acceptedInfoService) {
		this.acceptedInfoService = acceptedInfoService;
	}

	private LifeProposal lifeProposal;
	private User user;
	private List<LPL001> lifeProposalList;
	private List<Product> productList;
	private WorkFlow workFlow;
	private EnquiryCriteria criteria;
	private boolean isAccessBranches;
	private PaymentDTO paymentDTO;

	@PostConstruct
	public void init() {
		resetCriteria();
		user = (User) getParam(Constants.LOGIN_USER);
		loadProductList();
		if (user.isAccessAllBranch()) {
			setIsAccessBranches(true);
		} else {
			criteria.setBranch(user.getBranch());
		}
	}

	public void loadProductList() {
		productList = productService.findProductByInsuranceType(InsuranceType.LIFE);
		String farmerId = ProductIDConfig.getFarmerId();
		for (Iterator<Product> iterator = productList.iterator(); iterator.hasNext();) {
			Product product = iterator.next();
			if (product.getId().equals(farmerId)) {
				iterator.remove();
			}
		}
	}

	public List<LPL001> getLifeProposalList() {
		RegNoSorter<LPL001> regNoSorter = new RegNoSorter<LPL001>(lifeProposalList);
		List<LPL001> lifeProposalList = regNoSorter.getSortedList();
		return lifeProposalList;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public void ShowDetailLifeProposal(LPL001 lifeProposalDTO) {
		this.lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
	}

	public WorkFlow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkFlow workFlow) {
		this.workFlow = workFlow;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public List<Product> getProductList() {
		return productList;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public void findLifeProposalListByEnquiryCriteria() {
		if (validation()) {
			lifeProposalList = lifeProposalService.findLifeProposalByEnquiryCriteria(criteria, productList);
		}
	}

	public boolean validation() {
		boolean result = true;
		if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
			if (criteria.getStartDate().after(criteria.getEndDate())) {
				addErrorMessage("renewalGrouplifeEquiryForm:startDate", MessageId.INVALID_DATE);
				result = false;
			}
		}
		return result;

	}

	public boolean getIsAccessBranches() {
		return isAccessBranches;
	}

	public void setIsAccessBranches(boolean isAccessBranches) {
		this.isAccessBranches = isAccessBranches;
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		criteria.setProposalType(ProposalType.RENEWAL);
		if (validation()) {
			lifeProposalList = lifeProposalService.findLifeProposalByEnquiryCriteria(criteria, productList);
		}
	}

	public String editLifeProposal(LPL001 lifeProposalDTO) {
		LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		if (allowToEdit(lifeProposal.getId())) {
			outjectLifeProposal(lifeProposal);
			return "editLifeProposal";
		} else {
			return null;
		}

	}

	private String message;

	public String getMessage() {
		return message;
	}

	public void printIssuePolicy(LPL001 lifeProposalDTO) {
		LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		if (allowToPrint(lifeProposal, WorkflowTask.RENEWAL_ISSUING)) {
			LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposalDTO.getId());
			List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.LIFE_POLICY, true);
			paymentDTO = new PaymentDTO(paymentList);
			String reportName = "GroupLifePolicyIssue";
			pdfDirPath = "/pdf-report/" + reportName + "/" + curentTime + "/";
			fileName = reportName + ".pdf";
			dirPath = getWebRootPath() + pdfDirPath;
			DocumentBuilder.generateGroupLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
			PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.PAYMENT.getLabel().toLowerCase());
		}
	}

	public void printAcceptedLetter(LPL001 lifeProposalDTO) {
		LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		if (allowToPrint(lifeProposal, WorkflowTask.RENEWAL_CONFIRMATION, WorkflowTask.RENEWAL_PAYMENT, WorkflowTask.RENEWAL_ISSUING)) {
			AcceptedInfo acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.LIFE_PROPOSAL);
			if (checkApproved(lifeProposal)) {
				String reportName = "";
				reportName = "GroupLifeInformAccepted";
				pdfDirPath = "/pdf-report/" + reportName + "/" + curentTime + "/";
				dirPath = getWebRootPath() + pdfDirPath;
				fileName = reportName + ".pdf";
				DocumentBuilder.generateLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
			} else {
				String reportName = "";
				dirPath = getWebRootPath() + pdfDirPath;
				reportName = "GroupLifeRejected";
				pdfDirPath = "/pdf-report/" + reportName + "/" + curentTime + "/";
				dirPath = getWebRootPath() + pdfDirPath;
				fileName = reportName + ".pdf";
				DocumentBuilder.generateLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
			}
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.INFORM.getLabel().toLowerCase());
		}
	}

	public boolean checkApproved(LifeProposal lifeProposal) {
		boolean approved = true;
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			if (!pv.isApproved()) {
				approved = false;
				break;
			}
		}
		return approved;
	}

	public void outjectLifeProposal(LifeProposal lifeProposal) {
		putParam("lifeProposal", lifeProposal);
	}

	private boolean allowToPrint(LifeProposal lifeProposal, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId(), workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
			return false;
		} else {
			this.lifeProposal = lifeProposal;
			return true;
		}
	}

	/********************************************
	 * PDF Document Generation
	 ***************************************/
	String pdfDirPath = "";
	String dirPath = "";
	String fileName = "";
	long curentTime = System.currentTimeMillis();

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateLifePolicyLedger(LPL001 lifeProposalDTO) {
		if (allowToPrint(lifeProposalService.findLifeProposalById(lifeProposalDTO.getId()), WorkflowTask.RENEWAL_ISSUING, WorkflowTask.RENEWAL_PAYMENT)) {
			LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposalDTO.getId());
			if (checkGroupLife(lifePolicy)) {
				this.message = "Group Life Proposal have not Life Policy Schedule";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");
			} else {
				Date surveydate = lifeProposalService.findSurveyByProposalId(lifeProposalDTO.getId()).getDate();
				String reportName = "LifePolicyLedger";
				pdfDirPath = "/pdf-report/" + reportName + "/" + curentTime + "/";
				dirPath = getWebRootPath() + pdfDirPath;
				fileName = reportName + ".pdf";
				DocumentBuilder.generatePublicLifePolicyLedger(lifePolicy, surveydate, dirPath, fileName);
				PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
			}
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}
	}

	public void handleCloseLifePolicyLedger(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean checkGroupLife(LifePolicy lifePolicy) {
		boolean ans = false;
		for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
			if (person.getProduct().getId().equals(ProductIDConfig.getGroupLifeId())) {
				ans = true;
			}
		}
		return ans;
	}

	public void generateLifeSanction(LPL001 lifeProposalDTO) {
		LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		if (allowToPrint(lifeProposalService.findLifeProposalById(lifeProposalDTO.getId()), WorkflowTask.RENEWAL_ISSUING, WorkflowTask.RENEWAL_PAYMENT,
				WorkflowTask.RENEWAL_CONFIRMATION, WorkflowTask.RENEWAL_INFORM, WorkflowTask.RENEWAL_APPROVAL)) {
			String reportName = "GroupLifeSanction";
			pdfDirPath = "/pdf-report/" + reportName + "/" + curentTime + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			fileName = reportName + ".pdf";
			DocumentBuilder.generateGroupLIfeSanction(lifeProposal, user.getName(), dirPath, fileName);
			PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.RENEWAL_CONFIRMATION.getLabel().toLowerCase());
		}
	}

	public void handleCloseLifeSanction(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateCashReceipt(LPL001 lifeProposalDTO) {
		if (allowToPrint(lifeProposalService.findLifeProposalById(lifeProposalDTO.getId()), WorkflowTask.RENEWAL_ISSUING, WorkflowTask.RENEWAL_PAYMENT)) {
			List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.LIFE_POLICY, null);
			PaymentDTO payment = new PaymentDTO(paymentList);
			String reportName = "LifeCashReceipt";
			pdfDirPath = "/pdf-report/" + reportName + "/" + curentTime + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			fileName = reportName + ".pdf";

			DocumentBuilder.generateLifeCashReceipt(getLifePolicy(), payment, dirPath, fileName);
			PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}
	}

	public void handleCloseCashReceipt(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
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
			if (wf.getWorkflowTask().equals(WorkflowTask.RENEWAL_UNDERWRITING) || wf.getWorkflowTask().equals(WorkflowTask.RENEWAL_SURVEY)
					|| wf.getWorkflowTask().equals(WorkflowTask.RENEWAL_APPROVAL) || wf.getWorkflowTask().equals(WorkflowTask.RENEWAL_INFORM)
					|| wf.getWorkflowTask().equals(WorkflowTask.RENEWAL_CONFIRMATION)) {
				flag = true;
			} else {
				flag = false;
				this.message = "This proposal is not in the editable workflow phase. It's currently at " + wf.getWorkflowTask().getLabel() + " phase";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");

			}
		}
		return flag;
	}

	public LifePolicy getLifePolicy() {
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposal.getId());
		return lifePolicy;
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

	public void selectProduct() {
		selectProduct(InsuranceType.LIFE);
	}

}
