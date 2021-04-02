package org.ace.insurance.web.manage.report.farmer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.life.farmer.service.interfaces.IFarmerReportService;
import org.ace.insurance.report.farmer.FarmerSummaryReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
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
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "FarmerSummaryReportActionBean")
public class FarmerSummaryReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{FarmerReportService}")
	private IFarmerReportService farmerReportService;

	public void setFarmerReportService(IFarmerReportService farmerReportService) {
		this.farmerReportService = farmerReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private LifeDailyIncomeReportCriteria criteria;
	private User user;
	private boolean accessBranches;
	private List<FarmerSummaryReport> farmerSummaryReportList;
	private List<Branch> branchList;

	@PostConstruct
	private void init() {
		user = (User) getParam("LoginUser");
		branchList = branchService.findAllBranch();
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}

		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new LifeDailyIncomeReportCriteria();
		if (!accessBranches) {
			criteria.setBranch(user.getBranch());
			criteria.setEntity(user.getBranch().getEntity());
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);

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

	public void filter() {
		boolean valid = true;
		String formID = "farmerSummaryListForm";
		if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
			if (criteria.getStartDate().after(criteria.getEndDate())) {
				addErrorMessage(formID + ":startDate", MessageId.STARTDATE_MUSTBE_LESSTHAN_ENDDATE);
				valid = false;
			}
		}

		if (valid) {
			try {
				farmerSummaryReportList = farmerReportService.findFarmerSummaryReport(criteria);
			} catch (SystemException ex) {
				handelSysException(ex);
			}
		}
	}

	public void exportExcel() {
		if (null != farmerSummaryReportList && farmerSummaryReportList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "FarmerSummaryReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel(criteria.getBranch(), farmerSummaryReportList);
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export FarmerSummaryReport.xlsx", e);
			}
		} else {
			addErrorMessage("farmerSummaryListForm:farmerSummaryListTable", MessageId.THERE_IS_NO_DATA);
		}
	}

	private class ExportExcel {
		private List<FarmerSummaryReport> farmerSummaryReportList;
		private Branch branch;
		private XSSFWorkbook wb;

		public ExportExcel(Branch branch, List<FarmerSummaryReport> farmerSummaryReportList) {
			this.branch = branch;
			this.farmerSummaryReportList = farmerSummaryReportList;
			load();
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/Farmer/Farmer_Summary_Report.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load Farmer_Summary_Report.xlsx template", e);
			}
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("Farmer Summary");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);

				Row titleRow = sheet.getRow(2);
				Cell titleCell = titleRow.getCell(0);
				titleCell.setCellValue(getMessage("FARMER_SUMMARY_REPORT_TITLE", branch == null ? "All" : criteria.getBranch().getName()));

				Row row = null;
				Cell cell;

				int i = 3;
				int index = 0;
				for (FarmerSummaryReport report : farmerSummaryReportList) {
					i = i + 1;
					index = index + 1;
					row = sheet.createRow(i);
					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(1);
					cell.setCellValue(report.getDate());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(2);
					cell.setCellValue(report.getNumberOfPolicy());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(3);
					cell.setCellValue(report.getSumInsured());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(4);
					cell.setCellValue(report.getPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(5);
					cell.setCellValue(report.getCommission());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(6);
					cell.setCellValue(report.getRemark());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(7);
					cell.setCellValue(report.getBranch());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(8);
					cell.setCellValue(report.getSalepoint());
					cell.setCellStyle(textCellStyle);

				}

				String strFormula;
				Font font = wb.createFont();
				font.setFontName("Myanmar3");
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);

				i = i + 1;
				row = sheet.createRow(i);

				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));

				cell = row.createCell(0);
				cell.setCellValue("Sub Total");
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 1), sheet, wb);
				cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
				cell.getCellStyle().setFont(font);

				cell = row.createCell(2);
				cell.setCellStyle(defaultCellStyle);
				strFormula = "SUM(C5:C" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(3);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(D5:D" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(4);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(E5:E" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(5);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(F5:F" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				wb.setPrintArea(0, 0, 8, 0, i);
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
	public boolean isAccessBranches() {
		return accessBranches;
	}

	public LifeDailyIncomeReportCriteria getCriteria() {
		return criteria;
	}

	public List<FarmerSummaryReport> getFarmerSummaryReportList() {
		return farmerSummaryReportList;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

}
