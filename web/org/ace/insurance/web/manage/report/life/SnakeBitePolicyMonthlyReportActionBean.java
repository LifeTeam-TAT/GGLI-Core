package org.ace.insurance.web.manage.report.life;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.LifeProductType;
import org.ace.insurance.common.MonthType;
import org.ace.insurance.report.life.SnakeBitePolicyMonthlyReport;
import org.ace.insurance.report.life.service.SnakeBitePolicyMonthlyReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ISnakeBitePolicyMonthlyReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.web.common.BaseBean;

/**
 * This class serves as the ActionBean to manipulate the
 * <code>SnakeBite Policy Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/Nov/29
 */
@ViewScoped
@ManagedBean(name = "SnakeBitePolicyMonthlyReportActionBean")
public class SnakeBitePolicyMonthlyReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SnakeBitePolicyMonthlyReportService}")
	private ISnakeBitePolicyMonthlyReportService snakeBitePolicyMonthlyReportService;
	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	public void setSnakeBitePolicyMonthlyReportService(ISnakeBitePolicyMonthlyReportService snakeBitePolicyMonthlyReportService) {
		this.snakeBitePolicyMonthlyReportService = snakeBitePolicyMonthlyReportService;
	}

	private List<SnakeBitePolicyMonthlyReport> snakeBitePolicyMonthlyReportList;
	private List<Branch> branchList;
	private SnakeBitePolicyMonthlyReportCriteria criteria;
	private boolean isPublicLifeProduct = false;
	private boolean isGroupLifeProduct = false;
	private final String reportName = "SnakeBitePolicyMonthlyReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	@PostConstruct
	public void init() {
		resetCriteria();
	}

	public SnakeBitePolicyMonthlyReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SnakeBitePolicyMonthlyReportCriteria criteria) {
		this.criteria = criteria;
	}

	public boolean isPublicLifeProduct() {
		return isPublicLifeProduct;
	}

	public void setPublicLifeProduct(boolean isPublicLifeProduct) {
		this.isPublicLifeProduct = isPublicLifeProduct;
	}

	public boolean isGroupLifeProduct() {
		return isGroupLifeProduct;
	}

	public void setGroupLifeProduct(boolean isGroupLifeProduct) {
		this.isGroupLifeProduct = isGroupLifeProduct;
	}

	public List<SnakeBitePolicyMonthlyReport> getSnakeBitePolicyMonthlyReportList() {
		if (snakeBitePolicyMonthlyReportList == null) {
			snakeBitePolicyMonthlyReportList = snakeBitePolicyMonthlyReportService.findSnakeBitePolicyMonthlyReportByCriteria(criteria);
		}
		return snakeBitePolicyMonthlyReportList;
	}

	public LifeProductType[] getLifeProductTypes() {
		return LifeProductType.values();
	}

	public MonthType[] getMonthTypeList() {
		return MonthType.values();
	}

	public Map<Integer, Integer> getYears() {
		SortedMap<Integer, Integer> years = new TreeMap<Integer, Integer>(Collections.reverseOrder());
		int endYear = Calendar.getInstance().get(Calendar.YEAR);
		for (int startYear = 1900; startYear <= endYear; startYear++) {
			years.put(startYear, startYear);
		}
		return years;
	}

	public void search() {
		snakeBitePolicyMonthlyReportList = snakeBitePolicyMonthlyReportService.findSnakeBitePolicyMonthlyReportByCriteria(criteria);
	}

	public void resetCriteria() {
		criteria = new SnakeBitePolicyMonthlyReportCriteria();
		if (criteria.getStartDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			criteria.setStartDate(cal.getTime());
		}
		if (criteria.getEndDate() == null) {
			criteria.setEndDate(new Date());
		}
		snakeBitePolicyMonthlyReportList = snakeBitePolicyMonthlyReportService.findSnakeBitePolicyMonthlyReportByCriteria(criteria);
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			branchList = branchService.findAllBranch();
			snakeBitePolicyMonthlyReportService.generateSnakeBitePolicyMonthlyReport(snakeBitePolicyMonthlyReportList, criteria, branchList, dirPath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}
}
