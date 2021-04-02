package org.ace.insurance.web.manage.report.coinsurnace;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.report.coinsurance.CoinsuranceCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceSummaryReport;
import org.ace.insurance.report.coinsurance.service.interfaces.ICoinsuranceReportService;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "CoinsuranceSummaryReportActionBean")
public class CoinsuranceSummaryReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CoinsuranceReportService}")
	private ICoinsuranceReportService coinsuranceReportService;

	public void setCoinsuranceReportService(ICoinsuranceReportService coinsuranceReportService) {
		this.coinsuranceReportService = coinsuranceReportService;
	}

	private CoinsuranceCriteria criteria;
	private List<CoinsuranceSummaryReport> coinsuranceSummaryReports;
	private final String reportName = "CoinsuranceSummaryReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	@PostConstruct
	public void init() {
		criteria = new CoinsuranceCriteria();
	}

	public CoinsuranceCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CoinsuranceCriteria criteria) {
		this.criteria = criteria;
	}

	public List<CoinsuranceSummaryReport> getCoinsuranceSummaryReports() {
		if (coinsuranceSummaryReports == null) {
			coinsuranceSummaryReports = coinsuranceReportService.findCoinsuranceSummaryReports(criteria);
		}
		return coinsuranceSummaryReports;
	}

	public void resetFilter() {
		criteria = new CoinsuranceCriteria();
		coinsuranceSummaryReports = null;
	}

	public void filterCoinsuranceSummaryReport() {
		coinsuranceSummaryReports = coinsuranceReportService.findCoinsuranceSummaryReports(criteria);
		// coinsuranceSummaryReports = null;
	}

	public double totalReportInAmount() {
		double total = 0.0;
		for (CoinsuranceSummaryReport coinsuranceSummaryReport : coinsuranceSummaryReports) {
			total += coinsuranceSummaryReport.getInAmount();
		}
		return total;
	}

	public double totalReportOutAmount() {
		double total = 0.0;
		for (CoinsuranceSummaryReport coinsuranceSummaryReport : coinsuranceSummaryReports) {
			total += coinsuranceSummaryReport.getOutAmount();
		}
		return total;
	}

	public double totalReportDiffAmount() {
		double total = 0.0;
		for (CoinsuranceSummaryReport coinsuranceSummaryReport : coinsuranceSummaryReports) {
			total += coinsuranceSummaryReport.getDifference();
		}
		return total;
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			// use the below line instead of the mock reports in production
			List<CoinsuranceSummaryReport> reports = getCoinsuranceSummaryReports();
			// List<CoinsuranceSummaryReport> reports = getMockReports();
			coinsuranceReportService.generateSummaryReport(reports, dirPath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}

	/*
	 * private List<CoinsuranceSummaryReport> getMockReports() {
	 * List<CoinsuranceSummaryReport> ret = new
	 * ArrayList<CoinsuranceSummaryReport>(); CoinsuranceSummaryReport report =
	 * null; for (int i = 1; i < 30; i++) { report = new
	 * CoinsuranceSummaryReport("Company " + i, i * 1000, i * 100);
	 * ret.add(report); } return ret; }
	 */

}
