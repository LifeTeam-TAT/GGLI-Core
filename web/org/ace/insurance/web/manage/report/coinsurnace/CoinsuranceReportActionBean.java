package org.ace.insurance.web.manage.report.coinsurnace;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.report.coinsurance.CoinsuranceCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceReport;
import org.ace.insurance.report.coinsurance.service.interfaces.ICoinsuranceReportService;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.coinsurancecompany.service.interfaces.ICoinsuranceCompanyService;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "CoinsuranceReportActionBean")
public class CoinsuranceReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CoinsuranceReportService}")
	private ICoinsuranceReportService coinsuranceReportService;

	public void setCoinsuranceReportService(ICoinsuranceReportService coinsuranceReportService) {
		this.coinsuranceReportService = coinsuranceReportService;
	}

	@ManagedProperty(value = "#{CoinsuranceCompanyService}")
	private ICoinsuranceCompanyService coinsuranceCompanyService;

	public void setCoinsuranceCompanyService(ICoinsuranceCompanyService coinsuranceCompanyService) {
		this.coinsuranceCompanyService = coinsuranceCompanyService;
	}

	private CoinsuranceCriteria criteria;
	private List<CoinsuranceReport> coinsuranceReports;
	private List<String> policyNos;
	private List<SelectItem> policySelectItems;
	private List<SelectItem> companySelectItems;
	private List<CoinsuranceCompany> coinsuranceCompanies;
	private final String reportName = "CoinsuranceReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	@PostConstruct
	public void init() {
		criteria = new CoinsuranceCriteria();
		coinsuranceCompanies = coinsuranceCompanyService.findAll();
		companySelectItems = new ArrayList<SelectItem>();
		for (CoinsuranceCompany company : coinsuranceCompanies) {
			companySelectItems.add(new SelectItem(company.getId(), company.getName()));
		}
		policyNos = coinsuranceReportService.findAllPolicyNo();
		policySelectItems = new ArrayList<SelectItem>();
		for (String policyNo : policyNos) {
			policySelectItems.add(new SelectItem(policyNo));
		}
	}

	public CoinsuranceCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CoinsuranceCriteria criteria) {
		this.criteria = criteria;
	}

	public List<CoinsuranceCompany> getCoinsuranceCompanies() {
		return coinsuranceCompanies;
	}

	public void setCoinsuranceCompanies(List<CoinsuranceCompany> coinsuranceCompanies) {
		this.coinsuranceCompanies = coinsuranceCompanies;
	}

	public List<String> getPolicyNos() {
		return policyNos;
	}

	public List<SelectItem> getPolicySelectItems() {
		return policySelectItems;
	}

	public void setPolicySelectItems(List<SelectItem> policySelectItems) {
		this.policySelectItems = policySelectItems;
	}

	public List<SelectItem> getCompanySelectItems() {
		return companySelectItems;
	}

	public void setCompanySelectItems(List<SelectItem> companySelectItems) {
		this.companySelectItems = companySelectItems;
	}

	public void setPolicyNos(List<String> policyNos) {
		this.policyNos = policyNos;
	}

	public List<CoinsuranceReport> getCoinsuranceReports() {
		return coinsuranceReports;
	}

	public void filterCoinsuranceReport() {
		if (criteria.getPolicyNo().isEmpty() || criteria.getPolicyNo() == null) {
			criteria.setPolicyNo(null);
		}
		if (criteria.getCoinsuranceCompanyId().isEmpty() || criteria.getCoinsuranceCompanyId() == null) {
			criteria.setCoinsuranceCompanyId(null);
		}
		coinsuranceReports = coinsuranceReportService.findCoinsuranceReports(criteria);
		// coinsuranceReports = null;
	}

	public void resetFilter() {
		criteria = new CoinsuranceCriteria();
		coinsuranceReports = null;
	}

	public CoinsuranceType[] getCoinsuranceTypes() {
		return CoinsuranceType.values();
	}

	public InsuranceType[] getInsuranceTypes() {
		return InsuranceType.values();
	}

	public double totalReportSumInsured() {
		double total = 0.0;
		for (CoinsuranceReport coinsuranceReport : coinsuranceReports) {
			total += coinsuranceReport.getSumInsured();
		}
		return total;
	}

	public double totalReportPremium() {
		double total = 0.0;
		for (CoinsuranceReport coinsuranceReport : coinsuranceReports) {
			total += coinsuranceReport.getPremium();
		}
		return total;
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			coinsuranceReportService.generateReport(coinsuranceReports, dirPath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}
}
