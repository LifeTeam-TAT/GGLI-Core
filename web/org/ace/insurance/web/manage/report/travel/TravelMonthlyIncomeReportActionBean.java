package org.ace.insurance.web.manage.report.travel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.travel.TravelMonthlyIncomeReport;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "TravelMonthlyIncomeReportActionBean")
public class TravelMonthlyIncomeReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	private TravelReportCriteria criteria;
	private List<TravelMonthlyIncomeReport> proposalList;
	private User user;
	private boolean accessBranches;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		resetCriteria();
	}

	public void search() {
		proposalList = travelProposalService.findTravelMonthlyIncome(criteria);
	}

	public void resetCriteria() {
		criteria = new TravelReportCriteria();
		DateTime dateTime = new DateTime();
		criteria.setMonth(new Date().getMonth());
		criteria.setYear(dateTime.getYear());
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		} else {
			criteria.setBranch(user.getBranch());
			criteria.setEntity(user.getBranch().getEntity());
		}
		proposalList = new ArrayList<TravelMonthlyIncomeReport>();
	}

	// Exporting
	public void exportExcel() {
		if (null != proposalList && proposalList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "TravelMonthlyIncomeReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel(proposalList);
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export TravelMonthlyIncomeReport.xlsx", e);
			}
		} else {
			addErrorMessage("montlyIncomeReportForm:proposalList", MessageId.THERE_IS_NO_DATA);
		}
	}

	private class ExportExcel {
		private List<TravelMonthlyIncomeReport> proposalList;
		private XSSFWorkbook wb;

		public ExportExcel(List<TravelMonthlyIncomeReport> proposalList) {
			this.proposalList = proposalList;
			load();
		}

		private XSSFCellStyle getTitleCellStyle() {
			XSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			Font font = wb.createFont();
			font.setFontName("Myanmar3");
			font.setFontHeightInPoints((short) 13);
			cellStyle.setFont(font);
			return cellStyle;
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("TravelMonthlyIncomeReport");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				String branchName = criteria.getBranch() == null ? "All" : criteria.getBranch().getDescription();
				row = sheet.createRow(1);
				cell = row.createCell(4);
				cell.setCellValue("(" + branchName + ")");
				cell.setCellStyle(getTitleCellStyle());

				row = sheet.createRow(3);
				cell = row.createCell(4);
				cell.setCellValue(criteria.getYear() + " ခုနှစ် " + Utils.getMonthString(criteria.getMonth()) + " လအတွက် ခရီးသွား အာမခံဝင်ငွေစာရင်း");
				cell.setCellStyle(getTitleCellStyle());

				int i = 5;
				int index = 0;
				String strFormula;
				for (TravelMonthlyIncomeReport tDto001 : proposalList) {
					index = index + 1;
					row = sheet.createRow(i);

					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(1);
					cell.setCellValue(tDto001.getSubmittedDate());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(2);
					cell.setCellValue(tDto001.getTotalUnit());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(3);
					cell.setCellValue(tDto001.getTotalPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(4);
					cell.setCellValue(tDto001.getTotalCommission());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(5);
					cell.setCellValue(tDto001.getTotalNetPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(6);
					cell.setCellValue(tDto001.getPaymentDate());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(7);
					cell.setCellValue(tDto001.getExpressName());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(8);
					cell.setCellValue(tDto001.getBranchName());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(9);
					cell.setCellValue(tDto001.getSalePointName());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(10);
					cell.setCellStyle(textCellStyle);

					i = i + 1;
				}
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 2));
				row = sheet.createRow(i);
				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(3);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(D7:D" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(4);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(E7:E" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(5);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(F7:F" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				sheet.addMergedRegion(new CellRangeAddress(i, i, 6, 9));
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 6, 9), sheet, wb);

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
				InputStream inp = this.getClass().getResourceAsStream("/report-template/travel/TravelMonthlyIncomeReport.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load TravelMonthlyIncomeReport.xlsx template", e);
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

	public TravelReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TravelReportCriteria criteria) {
		this.criteria = criteria;
	}

	public List<TravelMonthlyIncomeReport> getProposalList() {
		return proposalList;
	}

	public void setProposalList(List<TravelMonthlyIncomeReport> proposalList) {
		this.proposalList = proposalList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
