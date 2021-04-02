package org.ace.insurance.web.manage.report.medical;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.report.medical.HealthDailyIncomeReportDTO;
import org.ace.insurance.report.medical.service.interfaces.IHealthDailyIncomeReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedicalClaimPaymentReportActionBean")
public class MedicalClaimPaymentReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{HealthDailyIncomeReportService}")
	private IHealthDailyIncomeReportService healthDailyIncomeReportService;

	public void setHealthDailyIncomeReportService(IHealthDailyIncomeReportService healthDailyIncomeReportService) {
		this.healthDailyIncomeReportService = healthDailyIncomeReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private HealthProposalReportCriteria criteria;
	private LazyDataModelUtil<HealthDailyIncomeReportDTO> lazyModel;
	private List<org.ace.insurance.report.medical.HealthDailyIncomeReportDTO> hdList;
	private boolean accessBranches;
	private User user;

	private Branch branch;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}
		resetCriteria();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void resetCriteria() {
		criteria = new HealthProposalReportCriteria();
		if (!accessBranches) {
			criteria.setBranch(user.getBranch());
			criteria.setEntity(user.getBranch().getEntity());
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		branch = null;
		hdList = healthDailyIncomeReportService.findMedicalClaimPaymentReport(criteria);
		lazyModel = new LazyDataModelUtil(hdList);
	}

	public HealthProposalReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(HealthProposalReportCriteria criteria) {
		this.criteria = criteria;
	}

	public LazyDataModelUtil<HealthDailyIncomeReportDTO> getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(LazyDataModelUtil<HealthDailyIncomeReportDTO> lazyModel) {
		this.lazyModel = lazyModel;
	}

	public List<HealthDailyIncomeReportDTO> getHdList() {
		return hdList;
	}

	public void setHdList(List<HealthDailyIncomeReportDTO> hdList) {
		this.hdList = hdList;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filter() {
		hdList = healthDailyIncomeReportService.findMedicalClaimPaymentReport(criteria);
		lazyModel = new LazyDataModelUtil(hdList);
	}

	public void exportHealthDailyReport() {
		if (null != hdList && hdList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "Health_Daily_Income_Report.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				HealthClaimPaymentReportExcel accpetedLetterExportExcel = new HealthClaimPaymentReportExcel();
				accpetedLetterExportExcel.generate(op, hdList);
				getFacesContext().responseComplete();

			} catch (IOException e) {
				throw new SystemException(null, "Failed to exportMedicalClaimDailyIncomeReport.xlsx", e);
			}
		} else {
			addErrorMessage("hdList:customerTable", MessageId.THERE_IS_NO_DATA);
		}
	}

	// Entity

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(criteria.getEntity() == null ? "" : criteria.getEntity().getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		criteria.setEntity(entity);
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	///
	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);
	}

	public void removeEntity() {
		criteria.setEntity(null);
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		criteria.setCustomer(customer);
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void setAccessBranches(boolean accessBranches) {
		this.accessBranches = accessBranches;
	}

	public List<Branch> getBranchList() {
		return branchService.findAllBranch();
	}

	public void setEntityNull() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
		criteria.setEntity(null);
	}

	public void setBranchNull() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

}
