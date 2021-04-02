package org.ace.insurance.web.manage.report.fidelity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.report.fidelity.FidelityDailyIncomeReport;
import org.ace.insurance.report.fidelity.FidelityDailyIncomeReportCriteria;
import org.ace.insurance.report.fidelity.service.interfaces.IFidelityDailyIncomeReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.user.User;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "FidelityDailyIncomeReportActionBean")
public class FidelityDailyIncomeReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{FidelityDailyIncomeReportService}")
	private IFidelityDailyIncomeReportService fdiReportService;

	public void setFdiReportService(IFidelityDailyIncomeReportService fdiReportService) {
		this.fdiReportService = fdiReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private FidelityDailyIncomeReport fdiReport;
	private List<FidelityDailyIncomeReport> fdiReportList;
	private FidelityDailyIncomeReportCriteria criteria;
	private List<Branch> branchList;
	private boolean accessBranchs;
	private User user;
	private String reportName = "fidelityDailyIncomeReport";
	private String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private String dirPath = getSystemPath() + pdfDirPath;
	private String fileName = reportName + ".pdf";

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new FidelityDailyIncomeReportCriteria();
		if (user.isAccessAllBranch()) {
			accessBranchs = true;
		}
		criteria.setBranch(user.getBranch());
		if (criteria.getStartDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			criteria.setStartDate(cal.getTime());
		}
		if (criteria.getEndDate() == null) {
			Date endDate = new Date();
			criteria.setEndDate(endDate);
		}
	}

	public void filter() {
		fdiReportList = fdiReportService.findFidelityDailyIncomeReport(criteria);
	}

	public void generateReport() {
		try {
			String fullTemplateFilePath = "report-template/fire/fidelityDailyIncomeReport.jrxml";
			branchList = getBranchList();
			fdiReportService.generateFidelityDailyIncomeReport(fullTemplateFilePath, criteria, branchList, dirPath, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public FidelityDailyIncomeReport getFdiReport() {
		return fdiReport;
	}

	public void setFdiReport(FidelityDailyIncomeReport fdiReport) {
		this.fdiReport = fdiReport;
	}

	public List<FidelityDailyIncomeReport> getFdiReportList() {
		return fdiReportList;
	}

	public void setFdiReportList(List<FidelityDailyIncomeReport> fdiReportList) {
		this.fdiReportList = fdiReportList;
	}

	public FidelityDailyIncomeReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FidelityDailyIncomeReportCriteria criteria) {
		this.criteria = criteria;
	}

	public List<Branch> getBranchList() {
		if (branchList == null) {
			branchList = branchService.findAllBranch();
		}
		return branchList;
	}

	public void setBranchList(List<Branch> branchList) {
		this.branchList = branchList;
	}

	public boolean isAccessBranchs() {
		return accessBranchs;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}
}
