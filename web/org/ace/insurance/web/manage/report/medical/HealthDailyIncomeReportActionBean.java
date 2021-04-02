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

import org.ace.insurance.report.medical.HealthDailyReportDTO;
import org.ace.insurance.report.medical.service.interfaces.IHealthDailyIncomeReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "HealthDailyIncomeReportActionBean")
public class HealthDailyIncomeReportActionBean extends BaseBean implements Serializable {
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

	private HealthDailyIncomeReportCriteria criteria;
	private List<HealthDailyReportDTO> healthDailyReportList;
	private List<Branch> branchList;
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		branchList = branchService.findAllBranch();
		resetCriteria();

	}

	public void resetCriteria() {
		criteria = new HealthDailyIncomeReportCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		healthDailyReportList = healthDailyIncomeReportService.findHealthDailyReportDTO(criteria);
	}

	public void filter() {
		healthDailyReportList = healthDailyIncomeReportService.findHealthDailyReportDTO(criteria);
	}

	public void exportHealthDailyReport() {
		if (null != healthDailyReportList && healthDailyReportList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "Health_Daily_Report.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				HealthDailyReportExcel accpetedLetterExportExcel = new HealthDailyReportExcel();
				accpetedLetterExportExcel.generate(op, healthDailyReportList);
				getFacesContext().responseComplete();

			} catch (IOException e) {
				throw new SystemException(null, "Failed to export HealthDailyReport.xlsx", e);
			}
		} else {
			addErrorMessage("healthDailyReportListForm:healthDailyListTable", MessageId.THERE_IS_NO_DATA);
		}
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
	}

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(criteria.getEntity() == null ? "" : criteria.getEntity().getId(), user.getBranch().getId());
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
	}

	public void removeBranch() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		criteria.setEntity(entity);
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void removeEntity() {
		criteria.setEntity(null);
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void resetSalePoint() {
		criteria.setSalePoint(null);
	}

	public HealthDailyIncomeReportCriteria getCriteria() {
		return criteria;
	}

	public List<HealthDailyReportDTO> getHealthDailyReportList() {
		return healthDailyReportList;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}
}
