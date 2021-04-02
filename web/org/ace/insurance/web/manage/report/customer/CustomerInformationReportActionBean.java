package org.ace.insurance.web.manage.report.customer;

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

import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.report.coinsurance.service.interfaces.ICustomerInformationReportService;
import org.ace.insurance.report.customer.CustomerInformationCriteria;
import org.ace.insurance.report.customer.CustomerInformationReport;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "CustomerInformationReportActionBean")
public class CustomerInformationReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(this.getClass());

	@ManagedProperty(value = "#{CustomerInformationReportService}")
	private ICustomerInformationReportService customerInformationService;

	public void setCustomerInformationService(ICustomerInformationReportService customerInformationService) {
		this.customerInformationService = customerInformationService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private boolean attachFlag;
	private CustomerInformationCriteria criteria;
	private CustomerInformationReport customerIndi;
	private Customer customer;
	private List<CustomerInformationReport> customerInformationList;
	private final String reportName = "CustomerInformationReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	private final String reportNameIndividual = "CustomerIndividualInformationReport";
	private final String pdfDirPathIndividual = "/pdf-report/" + reportNameIndividual + "/" + System.currentTimeMillis() + "/";
	private final String dirPathIndividual = getSystemPath() + pdfDirPathIndividual;
	private final String fileNameIndividual = reportNameIndividual + ".pdf";
	private List<IPolicy> policyList;
	private IPolicy policy;
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		customer = new Customer();
		criteria = new CustomerInformationCriteria();
		policyList = new ArrayList<IPolicy>();

		if (criteria.getStartDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -30);
			criteria.setStartDate(cal.getTime());
		}
		if (criteria.getEndDate() == null) {
			Date endDate = new Date();
			criteria.setEndDate(endDate);
		}
	}

	public boolean isAttachFlag() {
		return attachFlag;
	}

	public void setAttach(boolean attachFlag) {
		this.attachFlag = attachFlag;
	}

	public CustomerInformationReport getCustomerIndi() {
		return customerIndi;
	}

	public void setCustomerIndi(CustomerInformationReport customer) {
		this.customerIndi = customer;
	}

	public void prepareIndividual(CustomerInformationReport customer) {
		this.customerIndi = customer;
	}

	public List<CustomerInformationReport> getCustomerInformationList() {
		return customerInformationList;
	}

	public void setCustomerInformationList(List<CustomerInformationReport> customerInformationList) {
		this.customerInformationList = customerInformationList;
	}

	public CustomerInformationCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CustomerInformationCriteria criteria) {
		this.criteria = criteria;
	}

	public void filter() {
		if (customer != null) {
			criteria.setCustomer(customer.getId());
		}
		customerInformationList = customerInformationService.findCustomerInformation(criteria);
	}

	public void findAllActivePolicies(CustomerInformationReport customer) {
		policyList = customerInformationService.findAllActivePoliciesByCustomerId(customer.getCustomerId());
	}

	public List<Integer> getActivePolicyList() {
		List<Integer> ap = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			ap.add(i);
		}
		return ap;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<IPolicy> getPolicyList() {
		return policyList;
	}

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}

	public String getStreamIndividual() {
		String fileFullPath = pdfDirPathIndividual + fileNameIndividual;
		return fileFullPath;
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			List<CustomerInformationReport> reports = getCustomerInformationList();
			customerInformationService.generateReport(reports, dirPath, fileName);
		} catch (IOException e) {
			logger.error("Fail to create the directory(" + dirPath + ").", e);
			e.printStackTrace();
		}
	}

	public void generateReportIndividual(CustomerInformationReport customer) {
		try {
			FileHandler.forceMakeDirectory(dirPathIndividual);
			customerInformationService.generateReportIndividual(customer, dirPathIndividual, fileNameIndividual);
		} catch (IOException e) {
			logger.error("Fail to create the directory(" + dirPathIndividual + ").", e);
			e.printStackTrace();
		}
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		List<WorkFlowHistory> workFlowHistories = new ArrayList<WorkFlowHistory>();
		if (policy != null) {

			if (policy instanceof LifePolicy) {
				LifePolicy lifePolicy = (LifePolicy) policy;
				workFlowHistories = workFlowService.findWorkFlowHistoryByRefNo(lifePolicy.getLifeProposal().getId());
			}

		}
		return workFlowHistories;
	}

	public boolean isLifePolicy() {
		if (policy instanceof LifePolicy) {
			return true;
		}
		return false;
	}

	public LifePolicy getActivedLifePolicy() {
		if (policy instanceof LifePolicy) {
			return (LifePolicy) policy;
		}
		return null;
	}

	public IPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(IPolicy policy) {
		this.policy = policy;
	}

	public void selectedPolicy(IPolicy policy) {
		this.policy = policy;
	}

	public void returnCustomer(SelectEvent event) {
		customer = (Customer) event.getObject();
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		criteria.setEntity(entity);
		criteria.setCustomerBrach(null);
		criteria.setCustomersalepoint(null);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setCustomerBrach(branch);
		criteria.setCustomersalepoint(null);
	}

	public void removeBranch() {
		criteria.setCustomerBrach(null);
		criteria.setCustomersalepoint(null);
	}

	public void removeEntity() {
		criteria.setEntity(null);
		criteria.setCustomerBrach(null);
		criteria.setCustomersalepoint(null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setCustomersalepoint(salePoint);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getCustomerBrach() == null ? "" : criteria.getCustomerBrach().getId());
	}

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(criteria.getEntity() == null ? "" : criteria.getEntity().getId(), user.getBranch().getId());
	}

}
