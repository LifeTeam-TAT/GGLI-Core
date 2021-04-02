package org.ace.insurance.web.manage.report.farmer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.farmer.service.interfaces.IFarmerReportService;
import org.ace.insurance.report.common.MonthlyReportCriteria;
import org.ace.insurance.report.farmer.FarmerMonthlyReport;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "FarmerMonthlyReportActionBean")
public class FarmerMonthlyReportActionBean extends BaseBean {

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@ManagedProperty(value = "#{FarmerReportService}")
	private IFarmerReportService farmerReportService;

	public void setFarmerReportService(IFarmerReportService farmerReportService) {
		this.farmerReportService = farmerReportService;
	}

	private MonthlyReportCriteria criteria;
	private boolean accessBranches;
	private List<FarmerMonthlyReport> farmerMonthlyReportList;
	private List<Branch> branchList;
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		branchList = branchService.findAllBranch();
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new MonthlyReportCriteria();
		criteria.setEntity(null);
		if (!accessBranches) {
			criteria.setBranch(user.getBranch());
			criteria.setEntity(user.getBranch().getEntity());
		}
		DateTime dateTime = new DateTime();
		criteria.setMonth(dateTime.getMonthOfYear() - 1);
		criteria.setYear(dateTime.getYear());
		filter();
	}

	public void filter() {
		try {
			farmerMonthlyReportList = farmerReportService.findFarmerMonthlyReport(criteria);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void exportExcel() {
		if (null != farmerMonthlyReportList && farmerMonthlyReportList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "FarmerMonthlyReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel(criteria.getYear(), Utils.getMonthString(criteria.getMonth()), criteria.getBranch(), farmerMonthlyReportList);
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export Health_Monthly_Report.xlsx", e);
			}
		} else {
			addErrorMessage("monthlyForm:farmerPolicyTable", MessageId.THERE_IS_NO_DATA);
		}
	}

	private class ExportExcel {
		private int year;
		private String month;
		private Branch branch;
		private List<FarmerMonthlyReport> farmerMonthlyReportList;
		private XSSFWorkbook wb;

		public ExportExcel(int year, String month, Branch branch, List<FarmerMonthlyReport> farmerMonthlyReportList) {
			this.year = year;
			this.month = month;
			this.branch = branch;
			this.farmerMonthlyReportList = farmerMonthlyReportList;
			load();
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/Farmer/Farmer_Monthly_Report.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load Farmer_Monthly_Report.xlsx tempalte", e);
			}
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("Farmer Monthly");

				Row companyNameRow = sheet.getRow(1);
				Cell companyNameCell = companyNameRow.getCell(0);

				if (branch == null) {
					companyNameCell.setCellValue(ApplicationSetting.getCompanyLabel() + "( All )");
				} else {
					companyNameCell.setCellValue(ApplicationSetting.getCompanyLabel() + "( " + criteria.getBranch().getName() + " )");
				}

				Row titleRow = sheet.getRow(2);
				Cell titleCell = titleRow.getCell(0);
				titleCell.setCellValue(getMessage("FARMER_MONTHLY_REPORT_TITLE", String.valueOf(year), month));

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row = null;
				Cell cell = null;

				ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 3, 10, 12);

				int i = 3;
				int index = 0;
				for (FarmerMonthlyReport report : farmerMonthlyReportList) {
					i = i + 1;
					index = index + 1;
					row = sheet.createRow(i);

					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(1);
					cell.setCellValue(report.getInsuredPersonName());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(2);
					cell.setCellValue(report.getPolicyNo());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(3);
					cell.setCellValue(report.getProposalNo());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(4);
					cell.setCellValue(report.getAddress());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(5);
					cell.setCellValue(report.getSumInsured());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(6);
					cell.setCellValue(report.getPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(7);
					cell.setCellValue(report.getCommission());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(8);
					cell.setCellValue(report.getCashReceiptNoAndPaymentDate());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(9);
					cell.setCellValue(report.getAgentNameAndCode());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(10);
					cell.setCellValue(report.getBranch());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(11);
					cell.setCellValue(report.getSalepoint());
					cell.setCellStyle(textCellStyle);

				}

				String strFormula;
				Font font = wb.createFont();
				font.setFontName("Myanmar3");
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);

				i = i + 1;
				row = sheet.createRow(i);

				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 3));

				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 3), sheet, wb);
				cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
				cell.getCellStyle().setFont(font);

				cell = row.createCell(5);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(F5:F" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(6);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(G5:G" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(7);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(H5:H" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				wb.setPrintArea(0, 0, 11, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Getters & Setters
	public List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		int endYear = Calendar.getInstance().get(Calendar.YEAR);
		for (int startYear = 1999; startYear <= endYear; startYear++) {
			years.add(startYear);
		}
		Collections.reverse(years);
		return years;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
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

	public MonthlyReportCriteria getCriteria() {
		return criteria;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public List<FarmerMonthlyReport> getFarmerMonthlyReportList() {
		return farmerMonthlyReportList;
	}

}
