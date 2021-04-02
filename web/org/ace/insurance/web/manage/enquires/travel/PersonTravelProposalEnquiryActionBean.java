package org.ace.insurance.web.manage.enquires.travel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.PaymentOrderCashReceiptDTO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
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
@ManagedBean(name = "PersonTravelProposalEnquiryActionBean")
public class PersonTravelProposalEnquiryActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
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

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private PersonTravelProposal travelProposal;
	private User user;
	private List<PTPL001> travelProposalList;
	private List<Product> productList;
	private WorkFlow workFlow;
	private EnquiryCriteria criteria;
	private boolean isAccessBranches;
	boolean approvedProposal = false;
	private PaymentDTO paymentDTO;
	private String message;

	@PostConstruct
	public void init() {
		resetCriteria();
		user = (User) getParam(Constants.LOGIN_USER);
		productList = productService.findProductByInsuranceType(InsuranceType.PERSON_TRAVEL);
		if (user.isAccessAllBranch()) {
			setIsAccessBranches(true);
		} else {
			criteria.setBranch(user.getBranch());
		}
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		criteria.setProposalType(ProposalType.UNDERWRITING);
		travelProposalList = new ArrayList<PTPL001>();
	}

	public void findTravelProposalListByEnquiryCriteria() {
		this.travelProposalList = personTravelProposalService.findPersonTravelDTOByCriteria(criteria);
		sortLists(travelProposalList);

	}

	public void showDetailtravelProposal(PTPL001 proposalDTO) {
		this.travelProposal = personTravelProposalService.findPersonTravelProposalById(proposalDTO.getId());

	}

	public String editPersonTravelProposal(PTPL001 proposalDTO) {
		if (allowToEdit(proposalDTO.getId())) {
			travelProposal = personTravelProposalService.findPersonTravelProposalById(proposalDTO.getId());
			BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByPersonTravelproposalId(proposalDTO.getId());
			putParam("bancaassuranceProposal", bancaassuranceProposal);
			outjectTravelProposal(travelProposal);
			return "editPersonTravelProposal";
		} else {
			return null;
		}

	}

	public void outjectTravelProposal(PersonTravelProposal personTravelProposal) {
		putParam("personTravelProposal", personTravelProposal);
	}

	/********************************************
	 * PDF Document Generation
	 ***************************************/
	String pdfDirPath = "";
	String fileName = "";
	String dirPath = "";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateCashReceipt(PTPL001 proposalDTO) {
		if (allowToPrint(proposalDTO.getId(), WorkflowTask.ISSUING, WorkflowTask.PAYMENT)) {
			travelProposal = personTravelProposalService.findPersonTravelProposalById(proposalDTO.getId());
			String policyId = personTravelPolicyService.findPolicyIdByProposalId(travelProposal.getId());
			List<Payment> paymentList = paymentService.findByPolicy(policyId);
			PaymentDTO payment = new PaymentDTO(paymentList);
			PaymentOrderCashReceiptDTO orderDto = null;
			String reportName = "PersonTravelProposalCashReceipt";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			String customerName = travelProposal.getCustomerName();
			if (customerName.contains("\\")) {
				customerName = customerName.replace("\\", "");
			}
			if (customerName.contains("/")) {
				customerName = customerName.replace("/", "");
			}
			fileName = "PersonTravel_" + customerName + "_Receipt" + ".pdf";
			DocumentBuilder.generatePersonTravelCashReceipt(travelProposal, payment, dirPath, fileName, orderDto);
			PrimeFaces.current().executeScript("PF('issuePolicyPDFDialogSingle').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
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
			if (wf.getWorkflowTask().equals(WorkflowTask.PAYMENT) || wf.getWorkflowTask().equals(WorkflowTask.ISSUING)) {
				flag = false;
				this.message = "This proposal is not in the editable workflow phase. It's currently at " + wf.getWorkflowTask().getLabel() + " phase";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");
			}
		}
		return flag;
	}

	private boolean allowToPrint(String proposalId, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(proposalId, workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
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

	public void sortLists(List<PTPL001> travelProposalList) {
		Collections.sort(travelProposalList, new Comparator<PTPL001>() {
			@Override
			public int compare(PTPL001 obj1, PTPL001 obj2) {
				if (obj1.getProposalNo().equals(obj2.getProposalNo()))
					return -1;
				else
					return obj1.getProposalNo().compareTo(obj2.getProposalNo());
			}
		});
	}

	public PersonTravelProposal getTravelProposal() {
		return travelProposal;
	}

	public boolean getIsAccessBranches() {
		return isAccessBranches;
	}

	public void setIsAccessBranches(boolean isAccessBranches) {
		this.isAccessBranches = isAccessBranches;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public List<PTPL001> getTravelProposalList() {
		return travelProposalList;
	}

	public String getMessage() {
		return message;
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
		selectProduct(InsuranceType.PERSON_TRAVEL);
	}

	String issuereportName = "PersonalTravelPolicyIssue";
	String issuePdfDirPath = "/pdf-report/" + issuereportName + "/" + System.currentTimeMillis() + "/";
	String issueDirPath = getWebRootPath() + issuePdfDirPath;
	String issueFileName;

	public void generatePersonTravelIssue(PTPL001 personTravelProposal) throws Exception {
		if (allowToPrint(personTravelProposal.getId(), WorkflowTask.ISSUING, WorkflowTask.PAYMENT)) {
			PersonTravelPolicy personTravlePolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(personTravelProposal.getId());
			if (personTravlePolicy.getPersonTravelPolicyInfo().getPolicyTravellerList() != null) {
				String customerName = personTravlePolicy.getCustomerName();
				if (customerName.contains("\\")) {
					customerName = customerName.replace("\\", "");
				}
				if (customerName.contains("/")) {
					customerName = customerName.replace("/", "");
				}
				issueFileName = "PersonTravel_" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generatePersonTravelPolicyIssue(personTravlePolicy, issueDirPath, issueFileName);
				PrimeFaces.current().executeScript("PF('issuePersonTravelPolicyPDFDialogSingle').show()");
			}

		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}
	}

	public void handleCloseIssuePolicy(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(issueDirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getIssueReportStream() {
		return issuePdfDirPath + issueFileName;
	}

}
