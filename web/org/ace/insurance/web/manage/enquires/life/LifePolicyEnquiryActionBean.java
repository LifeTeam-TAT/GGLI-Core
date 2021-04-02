package org.ace.insurance.web.manage.enquires.life;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.LPL002;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.life.LifePremiumLedgerReport;
import org.ace.insurance.report.life.service.interfaces.ILifePremiumLedgerService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifePolicyEnquiryActionBean")
public class LifePolicyEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{LifePremiumLedgerService}")
	private ILifePremiumLedgerService lifePremiumLedgerService;

	public void setLifePremiumLedgerService(ILifePremiumLedgerService lifePremiumLedgerService) {
		this.lifePremiumLedgerService = lifePremiumLedgerService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

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

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{BancaassurancePolicyService}")
	private IBancaassurancePolicyService bancaassurancePolicyService;

	public void setBancaassurancePolicyService(IBancaassurancePolicyService bancaassurancePolicyService) {
		this.bancaassurancePolicyService = bancaassurancePolicyService;
	}

	private List<LPL002> lifePolicyList;
	private LifePolicy lifePolicy;
	private EnquiryCriteria criteria;
	private List<Product> productList;
	private User user;
	private boolean isAccessBranches;

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

	public List<WorkFlowHistory> getWorkFlowList(String proposalId) {
		return workFlowService.findWorkFlowHistoryByRefNo(lifePolicy.getLifeProposal().getId());
	}

	public List<LPL002> loadLifePolicyList() {
		return lifePolicyList;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
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

	public void findMotorProposalListByEnquiryCriteria() {
		criteria.setInsuranceType(InsuranceType.LIFE);
		lifePolicyList = lifePolicyService.findLifePolicyByEnquiryCriteria(criteria);
		sortLists(lifePolicyList);
	}

	public void sortLists(List<LPL002> lifePolicyList) {
		Collections.sort(lifePolicyList, new Comparator<LPL002>() {
			@Override
			public int compare(LPL002 obj1, LPL002 obj2) {
				if (obj1.getPolicyNo().equals(obj2.getPolicyNo()))
					return -1;
				else
					return obj1.getPolicyNo().compareTo(obj2.getPolicyNo());
			}
		});
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
		lifePolicyList = new ArrayList<LPL002>();
	}

	public String getPagePolicy(String lifePolicyId) {
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(lifePolicyId);
		outjectLifePolicy(lifePolicy);
		return "lifePolicyAttachment";
	}

	public String editLifePolicy(String lifePolicyId) {
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(lifePolicyId);
		BancaassurancePolicy bancaassurancePolicy = bancaassurancePolicyService.findBancaassurancePolicyByLifepolicyId(lifePolicyId);
		putParam("bancaassurancePolicy", bancaassurancePolicy);
		outjectLifePolicy(lifePolicy);
		return "editLifePolicy";
	}

	private final String reportName = "LifePremiumLedgerReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void handleClosePremiumLedger(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateReport(String lifePolicyId) {
		try {
			LifePremiumLedgerReport ledgerReport;
			FileHandler.forceMakeDirectory(dirPath);
			ledgerReport = lifePremiumLedgerService.findLifePremiumLedgerReport(lifePolicyId);
			lifePremiumLedgerService.generateLifePremiumLedgerReportt(ledgerReport, dirPath + fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void outjectLifePolicy(LifePolicy lifePolicy) {
		putParam("lifePolicy", lifePolicy);
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

	public boolean getIsStudentLife() {
		if (criteria.getProduct() != null) {
			return KeyFactorChecker.isStudentLife(criteria.getProduct().getId());
		} else {
			return false;
		}

	}

	public void openTemplateDialog(LPL002 lifePolicyDTO) {
		this.lifePolicy = lifePolicyService.findLifePolicyById(lifePolicyDTO.getId());
		PolicyInsuredPerson insuredPerson = lifePolicy.getPolicyInsuredPersonList().get(0);
		putParam("lifePolicy", lifePolicy);
		putParam("workFlowList", getWorkFlowList(lifePolicy.getLifeProposal().getId()));
		if (ProductIDConfig.isStudentLife(insuredPerson.getProduct())) {
			openNewLifePolicyInfoTemplate();
		} else {
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('lifePolicyDetailsDialog').show();");
		}
	}

}