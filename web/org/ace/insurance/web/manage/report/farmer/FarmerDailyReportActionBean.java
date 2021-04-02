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
import org.ace.insurance.report.farmer.FarmerBeneficiaryReportDTO;
import org.ace.insurance.report.farmer.FarmerDailyReport;
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
@ManagedBean(name = "FarmerDailyReportActionBean")
public class FarmerDailyReportActionBean extends BaseBean implements Serializable {
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
	private List<FarmerDailyReport> farmerDailyReportList;
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
		filter();
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
		String formID = "farmerPolicyListForm";
		if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
			if (criteria.getStartDate().after(criteria.getEndDate())) {
				addErrorMessage(formID + ":startDate", MessageId.STARTDATE_MUSTBE_LESSTHAN_ENDDATE);
				valid = false;
			}
		}

		if (valid) {
			try {
				farmerDailyReportList = farmerReportService.findFarmerDailyReport(criteria);
			} catch (SystemException ex) {
				handelSysException(ex);
			}
		}
	}

	public void exportExcel() {
		if (null != farmerDailyReportList && farmerDailyReportList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "FarmerDailyReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel(farmerDailyReportList);
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export FarmerDailyReport.xlsx", e);
			}
		} else {
			addErrorMessage("farmerPolicyListForm:farmerPolicyListTable", MessageId.THERE_IS_NO_DATA);
		}
	}

	private class ExportExcel {
		private List<FarmerDailyReport> farmerDailyReportList;
		private XSSFWorkbook wb;

		public ExportExcel(List<FarmerDailyReport> farmerDailyReportList) {
			this.farmerDailyReportList = farmerDailyReportList;
			load();
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/Farmer/Farmer_Daily_Report.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load Farmer_Daily_Report.xlsx template", e);
			}
		}

		private void cellMergeAndBorder(Sheet sheet, int startRow, int lastRow, int startColumn, int endColumn) {
			CellRangeAddress cellRangeAddress = new CellRangeAddress(startRow, lastRow, startColumn, endColumn);
			sheet.addMergedRegion(cellRangeAddress);
			ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, cellRangeAddress, sheet, wb);
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("Farmer Daily");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);

				Row row = null;
				Row beneficiaryRow = null;
				Cell cell;

				int startRow = 0; // start row no. for one insured person
				int lastRow = 3; // end row no. for this person
				int beneficiaryStartRow = 0;
				int index = 0;
				for (FarmerDailyReport report : farmerDailyReportList) {
					startRow = lastRow + 1;
					index = index + 1;
					lastRow = startRow + (report.getBeneficiaryList().size() - 1);

					for (int i = startRow; i <= lastRow; i++) {
						row = sheet.createRow(i);
					}

					row = sheet.getRow(startRow);

					cellMergeAndBorder(sheet, startRow, lastRow, 0, 0);
					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(defaultCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 1, 1);
					cell = row.createCell(1);
					cell.setCellValue(report.getPolicyNo());
					cell.setCellStyle(textCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 2, 2);
					cell = row.createCell(2);
					cell.setCellValue(report.getProposalNo());
					cell.setCellStyle(textCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 3, 3);
					cell = row.createCell(3);
					cell.setCellValue(report.getGroupFarmerProposalNo());
					cell.setCellStyle(textCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 4, 4);
					cell = row.createCell(4);
					cell.setCellValue(report.getInsuredPersonName());
					cell.setCellStyle(textCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 5, 5);
					cell = row.createCell(5);
					cell.setCellValue(report.getFullIdNo());
					cell.setCellStyle(textCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 6, 6);
					cell = row.createCell(6);
					cell.setCellValue(report.getAddress());
					cell.setCellStyle(textCellStyle);

					beneficiaryStartRow = startRow;
					for (FarmerBeneficiaryReportDTO bene : report.getBeneficiaryList()) {
						beneficiaryRow = sheet.getRow(beneficiaryStartRow);

						cell = beneficiaryRow.createCell(7);
						cell.setCellValue(bene.getName());
						cell.setCellStyle(textCellStyle);

						cell = beneficiaryRow.createCell(8);
						cell.setCellValue(bene.getFullIdNo());
						cell.setCellStyle(textCellStyle);

						cell = beneficiaryRow.createCell(9);
						cell.setCellValue(bene.getAddress());
						cell.setCellStyle(textCellStyle);

						if (beneficiaryStartRow < lastRow) {
							beneficiaryStartRow++;
						}
					}

					cellMergeAndBorder(sheet, startRow, lastRow, 10, 10);
					cell = row.createCell(10);
					cell.setCellValue(report.getActivedPolicyStartDate());
					cell.setCellStyle(dateCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 11, 11);
					cell = row.createCell(11);
					cell.setCellValue(report.getSumInsured());
					cell.setCellStyle(currencyCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 12, 12);
					cell = row.createCell(12);
					cell.setCellValue(report.getPremium());
					cell.setCellStyle(currencyCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 13, 13);
					cell = row.createCell(13);
					cell.setCellValue(report.getAgentName());
					cell.setCellStyle(textCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 14, 14);
					cell = row.createCell(14);
					cell.setCellValue(report.getRemark());
					cell.setCellStyle(textCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 15, 15);
					cell = row.createCell(15);
					cell.setCellValue(report.getBranch());
					cell.setCellStyle(textCellStyle);

					cellMergeAndBorder(sheet, startRow, lastRow, 16, 16);
					cell = row.createCell(16);
					cell.setCellValue(report.getSalepoint());
					cell.setCellStyle(textCellStyle);
				}

				String strFormula;
				Font font = wb.createFont();
				font.setFontName("Myanmar3");
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);

				lastRow = lastRow + 1;
				row = sheet.createRow(lastRow);

				cell = row.createCell(0);
				cell.setCellValue("Total");
				cellMergeAndBorder(sheet, lastRow, lastRow, 0, 10);
				cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
				cell.getCellStyle().setFont(font);

				cell = row.createCell(11);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(L5:L" + lastRow + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(12);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(M5:M" + lastRow + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				wb.setPrintArea(0, 0, 16, 0, lastRow);
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

	public List<FarmerDailyReport> getFarmerDailyReportList() {
		return farmerDailyReportList;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

}
