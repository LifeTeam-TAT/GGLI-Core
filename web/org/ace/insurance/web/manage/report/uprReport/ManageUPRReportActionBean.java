package org.ace.insurance.web.manage.report.uprReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.report.upr.UPRReport;
import org.ace.insurance.report.upr.services.interfaces.IUPRReportService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;

@ManagedBean(name = "ManageUPRReportActionBean")
@ViewScoped
public class ManageUPRReportActionBean extends BaseBean {

	@ManagedProperty(value = "#{UPRReportService}")
	private IUPRReportService reportService;

	public void setReportService(IUPRReportService reportService) {
		this.reportService = reportService;
	}

	private int startYear;
	private int endYear;

	private List<UPRReport> reportList;
	private List<Integer> yearList;

	private boolean financialYear;

	@PostConstruct
	public void init() {
		reportList = new ArrayList<>();
		generateYear();
	}

	private void generateYear() {
		this.yearList = new ArrayList<>();
		Integer year = Integer.valueOf(2012);
		List<Integer> yearList = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			yearList.add(year + i);
		}
		this.yearList.addAll(yearList);
	}

	public void search() {
		try {
			reportList = reportService.findUPRReport(startYear, endYear, financialYear);
			Collections.sort(reportList);
		} catch (SystemException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load UPRReport", e);
		}

	}

	public void reset() {
		reportList = new ArrayList<>();
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public List<Integer> getYearList() {
		return yearList;
	}

	public List<UPRReport> getReportList() {
		return reportList;
	}

	public boolean isFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(boolean financialYear) {
		this.financialYear = financialYear;
	}

}
