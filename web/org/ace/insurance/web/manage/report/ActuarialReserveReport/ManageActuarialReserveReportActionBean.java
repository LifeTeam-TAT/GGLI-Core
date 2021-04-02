package org.ace.insurance.web.manage.report.ActuarialReserveReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.report.actuarialReserve.ActuarialReserveReport;
import org.ace.insurance.report.actuarialReserve.services.IActuarialReserveReportService;
import org.ace.java.web.common.BaseBean;

@ManagedBean(name = "ManageActuarialReserveReportActionBean")
@ViewScoped
public class ManageActuarialReserveReportActionBean extends BaseBean {
	@ManagedProperty(value = "#{ActuarialReserveReportService}")
	private IActuarialReserveReportService reportService;

	public void setReportService(IActuarialReserveReportService reportService) {
		this.reportService = reportService;
	}

	private int startYear;
	private int endYear;
	private List<ActuarialReserveReport> reportList;
	private List<String> productIdList;
	private List<Integer> yearList;

	@PostConstruct
	public void init() {
		reportList = new ArrayList<>();
		productIdList = new ArrayList<>();
		initializeProductId();
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

	private void initializeProductId() {
		productIdList.add(ProductIDConfig.getShortEndowLifeId());
		productIdList.add(ProductIDConfig.getGroupLifeId());
	}

	public void search() {
		reportList = reportService.findActurarialReserveReport(productIdList, startYear, endYear);
		Collections.sort(reportList);
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

	public List<ActuarialReserveReport> getReportList() {
		return reportList;
	}

	public List<Integer> getYearList() {
		return yearList;
	}

}
