package org.ace.insurance.web.manage.enquires.shortEndowLife;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.migrate.service.interfaces.IShortEndowmentExtraValueService;
import org.ace.insurance.life.policy.LifePolicy;
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
import org.ace.insurance.web.common.document.life.ShortEndowUnderwritingDOCFactory;
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
@ManagedBean(name = "ShortEndowLifeProposalEnquiryActionBean")
public class ShortEndowLifeProposalEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{ShortEndowmentExtraValueService}")
	private IShortEndowmentExtraValueService shortEndowmentExtraValueService;

	public void setShortEndowmentExtraValueService(IShortEndowmentExtraValueService shortEndowmentExtraValueService) {
		this.shortEndowmentExtraValueService = shortEndowmentExtraValueService;
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

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private LifeProposal lifeProposal;
	private User user;
	private List<LPL001> shortEndowLifeProposalList;
	private EnquiryCriteria criteria;
	private boolean isAccessBranches;
	private Product product;

	@PostConstruct
	public void init() {
		resetCriteria();
		user = (User) getParam(Constants.LOGIN_USER);
		if (user.isAccessAllBranch()) {
			isAccessBranches = true;
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
		shortEndowLifeProposalList = new ArrayList<LPL001>();
		product = productService.findProductById(ProductIDConfig.getShortEndowLifeId());
		criteria.setProduct(product);
	}

	public void findShortEndowLifeProposalList() {
		criteria.setInsuranceType(InsuranceType.LIFE);
		criteria.setProduct(product);
		shortEndowLifeProposalList = lifeProposalService.findLifeProposalByEnquiryCriteria(criteria, new ArrayList<Product>());
	}

	public void showDetailLifeProposal(LPL001 proposal) {
		this.lifeProposal = lifeProposalService.findLifeProposalById(proposal.getId());
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public User getUser() {
		return user;
	}

	public List<LPL001> getShortEndowLifeProposalList() {
		return shortEndowLifeProposalList;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public boolean isAccessBranches() {
		return isAccessBranches;
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

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	private String reportName = "ShortTermEndowmentLifeLetter";
	private String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName;

	private String message;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateSanction(LPL001 proposal) {
		boolean isPrint = allowToPrint(proposal.getId(), WorkflowTask.ISSUING, WorkflowTask.PAYMENT, WorkflowTask.CONFIRMATION, WorkflowTask.INFORM, WorkflowTask.APPROVAL);
		String customerName = proposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		if (isPrint) {
			fileName = "ShortEndowmentLife_" + customerName + "_Sanction" + ".pdf";
			LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(proposal.getId());
			ShortEndowUnderwritingDOCFactory.generateShortEndowLifeSanction(lifeProposal, dirPath, fileName);
			PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}
	}

	public void printAcceptedLetter(LPL001 proposal) {
		boolean isPrint = allowToPrint(proposal.getId(), WorkflowTask.CONFIRMATION, WorkflowTask.PAYMENT, WorkflowTask.ISSUING);
		String customerName = proposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "ShortEndowmentLife_" + customerName + "_Inform" + ".pdf";
		if (isPrint) {
			LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(proposal.getId());
			AcceptedInfo acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposal.getId(), ReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL);
			ProposalInsuredPerson person = lifeProposal.getProposalInsuredPersonList().get(0);
			if (person.isApproved()) {
				ShortEndowUnderwritingDOCFactory.generateShortEndowLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else {
				ShortEndowUnderwritingDOCFactory.generateShortEndowLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			}
			PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.INFORM.getLabel().toLowerCase());
		}
	}

	public void generateCashReceipt(LPL001 proposal) {
		boolean isPrint = allowToPrint(proposal.getId(), WorkflowTask.ISSUING, WorkflowTask.PAYMENT);
		String customerName = proposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "ShortEndowmentLife_" + customerName + "_Receipt" + ".pdf";
		if (isPrint) {
			LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(proposal.getId());
			List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, null);
			PaymentDTO payment = new PaymentDTO(paymentList);
			DocumentBuilder.generateLifeCashReceipt(lifeProposal, payment, dirPath, fileName);
			PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}
	}

	public void printIssuePolicy(LPL001 proposal) {
		LifePolicy lifePolicy = null;
		List<Payment> paymentList = null;
		lifeProposal = lifeProposalService.findLifeProposalById(proposal.getId());
		String customerName = proposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "ShortEndowmentLife_" + customerName + "_Issue" + ".pdf";
		if (allowToPrint(proposal.getId(), WorkflowTask.ISSUING)) {
			lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(proposal.getId());
			if (lifeProposal.isMigrate()) {
				paymentList = paymentService.findByProposal(proposal.getId(), PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, true);
				double extraPremium = shortEndowmentExtraValueService.findExtraAmount(lifePolicy.getId());
				ShortEndowUnderwritingDOCFactory.generateMigrateShortEndowLifePolicy(lifePolicy, dirPath, fileName, paymentList, extraPremium);
			} else
				ShortEndowUnderwritingDOCFactory.generateShortEndowLifePolicy(lifePolicy, dirPath, fileName);
			PrimeFaces.current().executeScript("PF('pdfDialog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.PAYMENT.getLabel().toLowerCase());
		}
	}

	private boolean allowToEdit(String refNo) {
		boolean flag = true;
		WorkFlow wf = workFlowService.findWorkFlowByRefNo(refNo);
		WorkflowTask task = null;
		if (wf == null) {
			flag = false;
			this.message = "This proposal has been legalized as policy contract.";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
		} else {
			task = wf.getWorkflowTask();
			if (task.equals(WorkflowTask.UNDERWRITING) || task.equals(WorkflowTask.SURVEY) || task.equals(WorkflowTask.APPROVAL) || task.equals(WorkflowTask.INFORM)
					|| task.equals(WorkflowTask.CONFIRMATION)) {
				flag = true;
			} else {
				flag = false;
				this.message = "This proposal is not in the editable workflow phase. It's currently at " + task.getLabel() + " phase";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");

			}
		}
		return flag;
	}

	public String editLifeProposal(LPL001 proposal) {
		String result = null;
		if (allowToEdit(proposal.getId())) {
			LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(proposal.getId());
			BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(proposal.getId());
			putParam("insuranceType", InsuranceType.LIFE);
			putParam("bancaassuranceProposal", bancaassuranceProposal);
			putParam("lifeProposal", lifeProposal);
			putParam("proposalType", ProposalType.UNDERWRITING);
			result = "editLifeProposal";
		}
		return result;
	}

	public void handleCloseEvent(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getMessage() {
		return message;
	}
}
