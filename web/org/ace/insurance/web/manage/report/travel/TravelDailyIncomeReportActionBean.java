
package org.ace.insurance.web.manage.report.travel;

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
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.travel.TravelDailyIncomeReport;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.occurrence.Occurrence;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
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
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "TravelDailyIncomeReportActionBean")
public class TravelDailyIncomeReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	private TravelReportCriteria criteria;
	private List<TravelDailyIncomeReport> detailList;
	private User user;
	private boolean accessBranches;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		resetCriteria();
		detailList = travelProposalService.findExpressDetailByProposalSubmittedDate(criteria);
	}

	public void searchDetail() {
		detailList = travelProposalService.findExpressDetailByProposalSubmittedDate(criteria);
	}

	public void resetCriteria() {
		criteria = new TravelReportCriteria();
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}
		if (criteria.getFromDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			criteria.setFromDate(cal.getTime());
		}
		if (criteria.getToDate() == null) {
			Date endDate = new Date();
			criteria.setToDate(endDate);
		}

		// criteria.setBranch(user.getBranch());
		criteria.setTour(null);
		criteria.setTravelExpress(null);
		detailList = travelProposalService.findExpressDetailByProposalSubmittedDate(criteria);
	}

	// Exporting
	public void exportExcel() {
		if (null != detailList && detailList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "TravelDailyIncomeReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel(detailList);
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export Travel_ExpressDetail_Report.xlsx", e);
			}
		} else {
			addErrorMessage("dailyIncomeReportForm:detailList", MessageId.THERE_IS_NO_DATA);
		}

	}

	private class ExportExcel {
		private List<TravelDailyIncomeReport> detailList;
		private XSSFWorkbook wb;

		public ExportExcel(List<TravelDailyIncomeReport> detailList) {
			this.detailList = detailList;
			load();
		}

		private XSSFCellStyle getTitleCellStyle() {
			XSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			Font font = wb.createFont();
			font.setFontName("Myanmar3");
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			return cellStyle;
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("Travel");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				row = sheet.createRow(3);

				cell = row.createCell(1);
				cell.getCellStyle().setBorderTop(XSSFCellStyle.BORDER_THIN);
				// fromdate - todate
				StringBuffer buffer = new StringBuffer();
				// from date
				if (criteria.getFromDate() != null && criteria.getToDate() != null) {
					buffer.append(Utils.getDateFormatString(criteria.getFromDate()));
					buffer.append(" - ");
					buffer.append(Utils.getDateFormatString(criteria.getToDate()));
				}

				cell.setCellValue(buffer.toString());
				cell.setCellStyle(getTitleCellStyle());
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(3, 3, 0, 7), sheet, wb);

				int i = 4;
				int index = 0;
				int startIndex = i + 1 + 1;
				String strFormula;
				for (TravelDailyIncomeReport expressDetail : detailList) {
					i = i + 1;
					index = index + 1;
					row = sheet.createRow(i);

					// index
					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(defaultCellStyle);

					// express name
					cell = row.createCell(1);
					cell.setCellValue(expressDetail.getExpressName());
					cell.setCellStyle(textCellStyle);

					// registration no
					cell = row.createCell(2);
					cell.setCellValue(expressDetail.getRegistrationNo());
					cell.setCellStyle(textCellStyle);

					// occurrence
					cell = row.createCell(3);
					cell.setCellValue(expressDetail.getOccurrenceDesc());
					cell.setCellStyle(textCellStyle);

					// no of passenger
					cell = row.createCell(4);
					cell.setCellValue(expressDetail.getNoOfPassenger());
					cell.setCellStyle(defaultCellStyle);

					// premium
					cell = row.createCell(5);
					cell.setCellValue(expressDetail.getPremium());
					cell.setCellStyle(currencyCellStyle);

					// commission
					cell = row.createCell(6);
					cell.setCellValue(expressDetail.getCommission());
					cell.setCellStyle(currencyCellStyle);

					// net premium
					cell = row.createCell(7);
					cell.setCellValue(expressDetail.getNetPremium());
					cell.setCellStyle(currencyCellStyle);

					// branch
					cell = row.createCell(8);
					cell.setCellValue(expressDetail.getBranchName());
					cell.setCellStyle(currencyCellStyle);

					// salepoint
					cell = row.createCell(9);
					cell.setCellValue(expressDetail.getSalePointName());
					cell.setCellStyle(currencyCellStyle);

					// remark
					cell = row.createCell(10);
					cell.setCellStyle(textCellStyle);
				}
				i = i + 1;
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 3));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 3), sheet, wb);

				cell = row.createCell(5);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(F" + startIndex + ":F" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(6);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(G" + startIndex + ":G" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(7);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(H" + startIndex + ":H" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				wb.setPrintArea(0, 0, 10, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/travel/TravelDailyIncomeReport.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load TravelDailyIncomeReport.xlsx template", e);
			}
		}

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

	///
	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
	}

	public void returnOccurrence(SelectEvent event) {
		Occurrence occurrence = (Occurrence) event.getObject();
		criteria.setTour(occurrence.getDescription());
	}

	public void returnExpress(SelectEvent event) {
		Express express = (Express) event.getObject();
		criteria.setTravelExpress(express.getName());
	}

	public TravelReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TravelReportCriteria criteria) {
		this.criteria = criteria;
	}

	public List<TravelDailyIncomeReport> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<TravelDailyIncomeReport> detailList) {
		this.detailList = detailList;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void setAccessBranches(boolean accessBranches) {
		this.accessBranches = accessBranches;
	}

	public void setEntityNull() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
		criteria.setEntity(null);
	}

	public void setBranchNull() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

}
