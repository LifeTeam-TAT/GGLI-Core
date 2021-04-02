package org.ace.insurance.web.manage.report.shortEndowLife;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.report.life.ShortTermEndowmentLifeReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ILifePolicyReportService;
import org.ace.insurance.report.shortEndowLife.NewShortTermEndowmentLifePolicyReport;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "NewShortTermEndowmentLifeReportActionBean")
public class NewShortTermEndowmentLifeReportActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyReportService}")
	private ILifePolicyReportService lifePolicyReportService;

	public void setLifePolicyReportService(ILifePolicyReportService lifePolicyReportService) {
		this.lifePolicyReportService = lifePolicyReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private ShortTermEndowmentLifeReportCriteria criteria;
	private List<NewShortTermEndowmentLifePolicyReport> reportList;
	private LazyDataModelUtil<NewShortTermEndowmentLifePolicyReport> lazyModel;
	private User user;
	private boolean accessBranches;
	private Agent agent;

	@PostConstruct
	public void init() {
		criteria = new ShortTermEndowmentLifeReportCriteria();
		accessBranches = true;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		System.out.println(cal.getTime().toString());
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		reportList = new ArrayList<>();

	}

	public void filter() {
		boolean valid = true;
		String formID = "policyList";
		if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
			if (criteria.getStartDate().after(criteria.getEndDate())) {
				addErrorMessage(formID + ":startDate", MessageId.STARTDATE_MUSTBE_LESSTHAN_ENDDATE);
				valid = false;
			}
		}
		if (valid) {
			reportList = lifePolicyReportService.findNewShortTermEndowLifePolicyReport(criteria);
			lazyModel = new LazyDataModelUtil<NewShortTermEndowmentLifePolicyReport>(reportList);
		}
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

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(criteria.getEntity() == null ? "" : criteria.getEntity().getId(), user.getBranch().getId());
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		criteria.setInsurePersonName(customer.getFullName());
	}

	public void search() {
		this.reportList = lifePolicyReportService.findNewShortTermEndowLifePolicyReport(criteria);
	}

	public void reset() {
		this.criteria = new ShortTermEndowmentLifeReportCriteria();
		this.reportList.clear();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);

	}

	// Exporting
	public void exportExcel() {
		if (null != reportList && reportList.size() > 0) {

			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "ShortEndowLifePolicyReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel();
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export ShortEndowLifePolicyReport.xlsx", e);
			}
		} else {
			addErrorMessage("policyList:lifePolicyTable", MessageId.THERE_IS_NO_DATA);
		}

	}

	private class ExportExcel {
		private XSSFWorkbook wb;

		public ExportExcel() {
			load();
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/shortTermEndowLife/NewShortEndowLifePolicyReport.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load ShortEndowLifePolicyReport.xlsx template", e);
			}
		}

		public Map<String, List<NewShortTermEndowmentLifePolicyReport>> separateBranch() {
			Map<String, List<NewShortTermEndowmentLifePolicyReport>> map = new LinkedHashMap<String, List<NewShortTermEndowmentLifePolicyReport>>();
			for (NewShortTermEndowmentLifePolicyReport report : reportList) {
				if (map.containsKey(report.getBranchName())) {
					map.get(report.getBranchName()).add(report);
				} else {
					List<NewShortTermEndowmentLifePolicyReport> list = new ArrayList<NewShortTermEndowmentLifePolicyReport>();
					list.add(report);
					map.put(report.getBranchName(), list);
				}
			}
			return map;
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("ST Endowment");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 4, 12, 14);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				if (criteria.getBranchId() == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Short Term Endowment Life Policy Report ( All )");
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Short Term Endowment Life Policy Report ( " + criteria.getBranchName() + " )");
				}

				int i = 3;
				int index = 0;
				int startIndex;
				String strFormula;
				String GrandSIFormula = "";
				String GrandPremiumFormula = "";
				String GrandTermPremiumFormula = "";
				String GrandInstallmentPremiumFormula = "";
				Map<String, List<NewShortTermEndowmentLifePolicyReport>> map = separateBranch();
				for (List<NewShortTermEndowmentLifePolicyReport> branchList : map.values()) {
					startIndex = i + 1;
					for (NewShortTermEndowmentLifePolicyReport lifePolicyReport : branchList) {
						index = index + 1;
						row = sheet.createRow(i);

						// index
						cell = row.createCell(0);
						cell.setCellValue(index);
						cell.setCellStyle(defaultCellStyle);

						// insuredName
						cell = row.createCell(1);
						cell.setCellValue(lifePolicyReport.getInsuredPersonName());
						cell.setCellStyle(textCellStyle);

						// InuredPerson Age
						cell = row.createCell(2);
						cell.setCellValue(lifePolicyReport.getAge());
						cell.setCellStyle(textCellStyle);

						// policyNo
						cell = row.createCell(3);
						cell.setCellValue(lifePolicyReport.getPolicyNo());
						cell.setCellStyle(textCellStyle);

						// address
						cell = row.createCell(4);
						cell.setCellValue(lifePolicyReport.getInsuredPersonName());
						cell.setCellStyle(textCellStyle);

						// sumInsured
						cell = row.createCell(5);
						cell.setCellValue(lifePolicyReport.getSumInsured());
						cell.setCellStyle(currencyCellStyle);

						// modePremium
						cell = row.createCell(6);
						cell.setCellValue(lifePolicyReport.getPaymentMode());
						cell.setCellStyle(currencyCellStyle);

						// policy Term
						cell = row.createCell(7);
						cell.setCellValue(lifePolicyReport.getPolicyTerm());
						cell.setCellStyle(currencyCellStyle);

						// 1 Time Premium
						cell = row.createCell(8);
						cell.setCellValue(lifePolicyReport.getBasicTermPremium());
						cell.setCellStyle(currencyCellStyle);

						// policyStartDate
						cell = row.createCell(9);
						cell.setCellValue(lifePolicyReport.getPolicyStartDate());
						cell.setCellStyle(dateCellStyle);

						// policy Last Payment Date
						cell = row.createCell(10);
						cell.setCellValue(lifePolicyReport.getLastPaymentDate());
						cell.setCellStyle(dateCellStyle);

						// agent
						cell = row.createCell(11);
						cell.setCellValue(lifePolicyReport.getAgentName());
						cell.setCellStyle(dateCellStyle);

						// Branch
						cell = row.createCell(12);
						cell.setCellValue(lifePolicyReport.getBranchName());
						cell.setCellStyle(dateCellStyle);

						// SalePoint
						cell = row.createCell(13);
						cell.setCellValue(lifePolicyReport.getSalePointName());
						cell.setCellStyle(dateCellStyle);

						i = i + 1;
					}
					sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 4));
					row = sheet.createRow(i);

					cell = row.createCell(0);
					cell.setCellValue("Sub Total");
					cell.setCellStyle(defaultCellStyle);
					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 3), sheet, wb);

					cell = row.createCell(5);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(F" + startIndex + ":F" + i + ")";
					GrandSIFormula += "F" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 5, 7), sheet, wb);

					cell = row.createCell(8);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(I" + startIndex + ":I" + i + ")";
					GrandPremiumFormula += "I" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 9, 12), sheet, wb);
					i = i + 1;
				}
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 4));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 4), sheet, wb);

				cell = row.createCell(5);
				cell.setCellStyle(currencyCellStyle);
				GrandSIFormula = GrandSIFormula.substring(0, GrandSIFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandSIFormula);

				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 5, 7), sheet, wb);

				cell = row.createCell(8);
				cell.setCellStyle(currencyCellStyle);
				GrandPremiumFormula = GrandPremiumFormula.substring(0, GrandPremiumFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandPremiumFormula);

				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 9, 12), sheet, wb);

				wb.setPrintArea(0, 0, 13, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ShortTermEndowmentLifeReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ShortTermEndowmentLifeReportCriteria criteria) {
		this.criteria = criteria;
	}

	public List<NewShortTermEndowmentLifePolicyReport> getReportList() {
		return reportList;
	}

	public LazyDataModelUtil<NewShortTermEndowmentLifePolicyReport> getLazyModel() {
		return lazyModel;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

}
