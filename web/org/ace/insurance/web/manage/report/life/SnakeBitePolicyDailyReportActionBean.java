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
import org.ace.insurance.life.snakebite.view.SnakeBiteDailyIncomeReport;
import org.ace.insurance.report.life.service.SnakeBitePolicyDailyReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ISnakeBitePolicyDailyReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.SelectEvent;

/**
 * This class serves as the ActionBean to manipulate the
 * <code>SnakeBite Policy Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/Nov/29
 */
@ViewScoped
@ManagedBean(name = "SnakeBitePolicyDailyReportActionBean")
public class SnakeBitePolicyDailyReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SnakeBitePolicyDailyReportService}")
	private ISnakeBitePolicyDailyReportService snakeBitePolicyDailyReportService;

	public void setSnakeBitePolicyDailyReportService(ISnakeBitePolicyDailyReportService snakeBitePolicyDailyReportService) {
		this.snakeBitePolicyDailyReportService = snakeBitePolicyDailyReportService;
	}

	private List<SnakeBiteDailyIncomeReport> snakeBitePolicyDailyReportList;
	private List<Branch> branchList;
	private SnakeBitePolicyDailyReportCriteria criteria;
	private boolean isPublicLifeProduct = false;
	private boolean isGroupLifeProduct = false;

	private final String reportName = "SnakeBitePolicyDailyReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private final String fileName = reportName + ".pdf";
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		resetCriteria();
	}

	public SnakeBitePolicyDailyReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SnakeBitePolicyDailyReportCriteria criteria) {
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

	public List<SnakeBiteDailyIncomeReport> getSnakeBitePolicyDailyReportList() {
		if (snakeBitePolicyDailyReportList == null) {
			snakeBitePolicyDailyReportList = snakeBitePolicyDailyReportService.findSnakeBitePolicyDailyReportByCriteria(criteria);
		}
		return snakeBitePolicyDailyReportList;
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
		snakeBitePolicyDailyReportList = snakeBitePolicyDailyReportService.findSnakeBitePolicyDailyReportByCriteria(criteria);
	}

	public void resetCriteria() {
		criteria = new SnakeBitePolicyDailyReportCriteria();
		if (criteria.getStartDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			criteria.setStartDate(cal.getTime());
		}
		if (criteria.getEndDate() == null) {
			criteria.setEndDate(new Date());
		}
		snakeBitePolicyDailyReportList = snakeBitePolicyDailyReportService.findSnakeBitePolicyDailyReportByCriteria(criteria);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
	}

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(criteria.getEntity() == null ? "" : criteria.getEntity().getId(), user.getBranch().getId());
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

	public void removeBranch() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);

	}

	public void clearBranch() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			branchList = branchService.findAllBranch();
			snakeBitePolicyDailyReportService.generateSnakeBitePolicyDailyReport(snakeBitePolicyDailyReportList, criteria, branchList, dirPath, fileName);
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
