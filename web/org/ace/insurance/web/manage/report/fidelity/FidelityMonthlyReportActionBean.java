package org.ace.insurance.web.manage.report.fidelity;

/**
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/5
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.MonthNames;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.fidelity.FidelityMonthlyReport;
import org.ace.insurance.report.fidelity.FidelityMonthlyReportCriteria;
import org.ace.insurance.report.fidelity.service.interfaces.IFidelityMonthlyReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "FidelityMonthlyReportActionBean")
public class FidelityMonthlyReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{FidelityMonthlyReportService}")
	private IFidelityMonthlyReportService fidelityMonthlyReportService;
	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setFidelityMonthlyReportService(IFidelityMonthlyReportService fidelityMonthlyReportService) {
		this.fidelityMonthlyReportService = fidelityMonthlyReportService;
	}

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private FidelityMonthlyReportCriteria criteria;
	private FidelityMonthlyReport report;
	private Branch branch;
	private List<FidelityMonthlyReport> fidelityMonthlyReportList;
	private User user;
	private boolean accessBranchs;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		if (user.isAccessAllBranch()) {
			accessBranchs = true;
		}
		DateTime dateTime = new DateTime();
		criteria = new FidelityMonthlyReportCriteria();
		criteria.setBranch(user.getBranch());
		criteria.setMonth(new Date().getMonth());
		criteria.setYear(dateTime.getYear());
		fidelityMonthlyReportList = new ArrayList<FidelityMonthlyReport>();
	}

	public boolean isAccessBranchs() {
		return accessBranchs;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public FidelityMonthlyReport getReport() {
		return report;
	}

	public void setReport(FidelityMonthlyReport report) {
		this.report = report;
	}

	public List<FidelityMonthlyReport> getFidelityMonthlyReportList() {
		return fidelityMonthlyReportList;
	}

	public void setFidelityMonthlyReportList(List<FidelityMonthlyReport> fidelityMonthlyReportList) {
		this.fidelityMonthlyReportList = fidelityMonthlyReportList;
	}

	public FidelityMonthlyReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FidelityMonthlyReportCriteria criteria) {
		this.criteria = criteria;
	}

	public EnumSet<MonthNames> getMonthSet() {

		return EnumSet.allOf(MonthNames.class);
	}

	public List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		int endYear = Calendar.getInstance().get(Calendar.YEAR);
		for (int startYear = 1999; startYear <= endYear; startYear++) {
			years.add(startYear);
		}
		Collections.reverse(years);
		return years;
	}

	public void filter() {
		fidelityMonthlyReportList = fidelityMonthlyReportService.findFidelityMonthlyReport(criteria);
	}

	public void resetCriteria() {
		init();
	}

	public void exportExcel() {
		ExternalContext ec = getFacesContext().getExternalContext();
		ec.responseReset();
		ec.setResponseContentType("application/vnd.ms-excel");
		String fileName = "Fidelity_Monthly_Report.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try {
			OutputStream op = ec.getResponseOutputStream();
			ExportExcel exportExcel = new ExportExcel(criteria.getYear(), Utils.getMonthString(criteria.getMonth()));
			exportExcel.generate(op);
			getFacesContext().responseComplete();
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export Fidelity_Monthly_Report.xlsx", e);
		}
	}

	private class ExportExcel {
		private int year;
		private String month;
		private XSSFWorkbook wb;
		private final String CURRENCY_FORMAT = "#,###.00";

		public ExportExcel(int year, String month) {
			this.year = year;
			this.month = month;
			load();

		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/fidelity/Fidelity_Monthly_Report.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load Fidelity_Monthly_Report.xlsx tempalte", e);
			}
		}

		public void generate(OutputStream op) {
			try {
				XSSFDataFormat cellFormat = wb.createDataFormat();
				Sheet sheet = wb.getSheet("fidelity");

				Row titleRow = sheet.getRow(2);
				Cell titleCell = titleRow.getCell(0);
				titleCell.setCellValue(year + " \u1001\u102F\u1014\u103E\u1005\u103A " + month
						+ " \u101C \u1021\u1010\u103D\u1000\u103A \u1004\u103D\u1031\u101E\u102C\u1038\u101C\u102F\u1036\u1001\u103C\u102F\u1036\u1019\u103E\u102F \u1021\u102C\u1019\u1001\u1036\u101C\u1001\u103B\u102F\u1015\u103A \u1005\u102C\u101B\u1004\u103A\u1038");
				int i = 4;
				int index = 0;

				Row row = null;
				Cell noCell = null;
				Cell cusNameCell = null;
				Cell addressCell = null;
				Cell siCell = null;
				Cell rate = null;
				Cell premiumCell = null;
				Cell receiptCell = null;
				Cell remarkCell = null;

				XSSFCellStyle textStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle currencyStyle = ExcelUtils.getCurrencyCellStyle(wb);

				for (FidelityMonthlyReport mmr : fidelityMonthlyReportList) {
					i = i + 1;
					index = index + 1;
					row = sheet.createRow(i);

					noCell = row.createCell(0);
					noCell.setCellValue(index);
					noCell.setCellStyle(textStyle);
					cusNameCell = row.createCell(1);
					cusNameCell.setCellValue(mmr.getCustomerId() == null ? mmr.getOrganizationName() : mmr.getCustomerName());
					cusNameCell.setCellStyle(textStyle);

					addressCell = row.createCell(2);
					addressCell.setCellValue(mmr.getCustomerId() == null ? mmr.getOrganizationAddress() : mmr.getCustomerAddress());
					addressCell.setCellStyle(textStyle);

					siCell = row.createCell(3);
					siCell.setCellValue(mmr.getSuminsured());
					siCell.setCellStyle(currencyStyle);

					rate = row.createCell(4);
					rate.setCellValue(mmr.getRate());
					rate.setCellStyle(textStyle);

					premiumCell = row.createCell(5);
					premiumCell.setCellValue(mmr.getPremium());
					premiumCell.setCellStyle(currencyStyle);

					receiptCell = row.createCell(6);
					receiptCell.setCellValue(mmr.getReceiptNo() + "\n" + Utils.getDateFormatString(mmr.getPaymentDate()));
					receiptCell.setCellStyle(textStyle);

					remarkCell = row.createCell(7);
					remarkCell.setCellValue("");
					remarkCell.setCellStyle(textStyle);

				}
				i = i + 1;
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 4));
				Row totalRow = sheet.createRow(i);
				Cell totalCell = totalRow.createCell(0);
				totalCell.setCellValue("Total Premium");
				setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 4), sheet, wb);
				totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);

				Cell totalPremiumCell = totalRow.createCell(5);
				totalPremiumCell.setCellValue(getTotalPremium());
				totalPremiumCell.setCellStyle(currencyStyle);

				wb.setPrintArea(0, "$A$1:$T$" + (i + 1));
				wb.write(op);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private double getTotalPremium() {
			double result = 0.0;
			for (FidelityMonthlyReport mmr : fidelityMonthlyReportList) {
				result = result + mmr.getPremium();
			}
			return result;
		}

		private void setRegionBorder(int borderWidth, CellRangeAddress crAddress, Sheet sheet, Workbook workBook) {
			RegionUtil.setBorderTop(borderWidth, crAddress, sheet, workBook);
			RegionUtil.setBorderBottom(borderWidth, crAddress, sheet, workBook);
			RegionUtil.setBorderRight(borderWidth, crAddress, sheet, workBook);
			RegionUtil.setBorderLeft(borderWidth, crAddress, sheet, workBook);

		}
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}
}
