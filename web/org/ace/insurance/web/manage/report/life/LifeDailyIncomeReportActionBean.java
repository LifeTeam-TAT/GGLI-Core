package org.ace.insurance.web.manage.report.life;

import java.io.FileNotFoundException;
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
import org.ace.insurance.common.LifeProductType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.report.life.LifeDailyIncomeReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ILifeDailyIncomeReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.insurance.web.util.FileHandler;
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
import org.primefaces.model.LazyDataModel;

@ViewScoped
@ManagedBean(name = "LifeDailyIncomeReportActionBean")
public class LifeDailyIncomeReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeDailyIncomeReportService}")
	private ILifeDailyIncomeReportService lifeDailyIncomeReportService;

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	public void setLifeDailyIncomeReportService(ILifeDailyIncomeReportService lifeDailyIncomeReportService) {
		this.lifeDailyIncomeReportService = lifeDailyIncomeReportService;
	}

	private LifeDailyIncomeReportCriteria criteria;
	private List<LifeDailyIncomeReport> lifeDailyIncomeList;
	private boolean accessBranches;
	private User user;
	private LazyDataModelUtil<LifeDailyIncomeReport> lazyModel;
	private List<String> productIdList;
	private LifeProductType selectedProductType;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}
		productIdList = new ArrayList<String>();
		productIdList.add(ProductIDConfig.getPublicLifeId());
		productIdList.add(ProductIDConfig.getGroupLifeId());
		productIdList.add(ProductIDConfig.getShortEndowLifeId());
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new LifeDailyIncomeReportCriteria();
		/*
		 * if (!accessBranches) { criteria.setBranch(user.getBranch()); }
		 */
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		lazyModel = null;

	}

	public ProposalType[] getProposalTypeSelectItemList() {
		return ProposalType.values();
	}

	public LazyDataModel<LifeDailyIncomeReport> getLazyModel() {
		return lazyModel;
	}

	public void filter() {
		boolean valid = true;
		String formID = "incomeList";
		if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
			if (criteria.getStartDate().after(criteria.getEndDate())) {
				addErrorMessage(formID + ":startDate", MessageId.STARTDATE_MUSTBE_LESSTHAN_ENDDATE);
				valid = false;
			}
		}
		if (valid) {
			lifeDailyIncomeList = lifeDailyIncomeReportService.findLifeDailyIncome(criteria, productIdList);
			lazyModel = new LazyDataModelUtil<LifeDailyIncomeReport>(lifeDailyIncomeList);
		}
	}

	public void handleProductChange() {
		productIdList.clear();
		if (LifeProductType.PUBLIC_LIFE.equals(selectedProductType)) {
			productIdList.add(ProductIDConfig.getPublicLifeId());
		} else if (LifeProductType.GROUP_LIFE.equals(selectedProductType)) {
			productIdList.add(ProductIDConfig.getGroupLifeId());
		} else if (LifeProductType.SHORT_ENDOWMENT_LIFE.equals(selectedProductType)) {
			productIdList.add(ProductIDConfig.getShortEndowLifeId());
		} else if (LifeProductType.STUDENT_LIFE.equals(selectedProductType)) {
			productIdList.add(ProductIDConfig.getStudentLifeId());
		}else {
			productIdList.add(ProductIDConfig.getGroupLifeId());
			productIdList.add(ProductIDConfig.getPublicLifeId());
			productIdList.add(ProductIDConfig.getShortEndowLifeId());
			productIdList.add(ProductIDConfig.getStudentLifeId());
		}
	}

	public LifeProductType[] getLifePorductTypes() {
		return new LifeProductType[] { LifeProductType.PUBLIC_LIFE, LifeProductType.GROUP_LIFE, LifeProductType.SHORT_ENDOWMENT_LIFE ,LifeProductType.STUDENT_LIFE};
	}

	public LifeDailyIncomeReportCriteria getCriteria() {
		return criteria;
	}

	public List<LifeDailyIncomeReport> getLifeDailyIncomeList() {
		return lifeDailyIncomeList;
	}

	public void setCriteria(LifeDailyIncomeReportCriteria criteria) {
		this.criteria = criteria;
	}

	public double totalAmount() {
		double totalAmount = 0.0;
		for (LifeDailyIncomeReport l : lifeDailyIncomeList) {
			totalAmount += l.getAmount();
		}
		return totalAmount;
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
	}

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(criteria.getEntity() == null ? "" : criteria.getEntity().getId(), user.getBranch().getId());
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

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);
	}

	public void resetSalePoint() {
		criteria.setSalePoint(null);
	}

	public void removeBranch() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public List<Branch> getBranchList() {
		return branchService.findAllBranch();

	}

	private final String reportName = "LifeDailyIncomeReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		try {
			String branch = "";
			if (criteria.getBranch() == null) {
				branch = "All";
			} else {
				branch = criteria.getBranch().getName();
			}

			FileHandler.forceMakeDirectory(dirPath);
			lifeDailyIncomeReportService.generateLifeDailyIncomeReport(lifeDailyIncomeList, dirPath, fileName, branch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportExcel() {
		if (null != lifeDailyIncomeList && lifeDailyIncomeList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "Life_Daily_Income_Report.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel();
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export Life_Daily_Income_Report.xlsx", e);
			}
		} else {
			addErrorMessage("incomeList:incomeTable", MessageId.THERE_IS_NO_DATA);
		}

	}

	private class ExportExcel {
		private XSSFWorkbook wb;

		public ExportExcel() {
			load();
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/life/Life_Daily_Income_Report.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load Life_Daily_Income_Report.xlsx tempalte", e);
			}
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("LifeDailyIncome");
				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;
				// ExcelUtils.fillCompanyLogo(wb, sheet,11);
				ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 4, 11, 13);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				if (criteria.getBranch() == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Life Daily Income Report ( All )");
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Life Daily Income Report ( " + criteria.getBranch().getName() + " )");
				}

				int i = 1;
				int index = 0;
				int startIndex;
				String strFormula;
				String GrandSIFormula = "";
				String GrandStampFeeFormula = "";
				Map<String, List<LifeDailyIncomeReport>> map = separateBranch();
				for (List<LifeDailyIncomeReport> branchList : map.values()) {
					startIndex = i + 1 + 1;
					for (LifeDailyIncomeReport report : branchList) {
						i = i + 1;
						index = index + 1;

						row = sheet.createRow(i);
						cell = row.createCell(0);
						cell.setCellValue(index);
						cell.setCellStyle(defaultCellStyle);

						cell = row.createCell(1);
						cell.setCellValue(report.getPolicyNo());
						cell.setCellStyle(textCellStyle);

						cell = row.createCell(2);
						cell.setCellValue(report.getProposalNo());
						cell.setCellStyle(textCellStyle);

						cell = row.createCell(3);
						cell.setCellValue(report.getPolicyType());
						cell.setCellStyle(textCellStyle);

						cell = row.createCell(4);
						cell.setCellValue(report.getCashReceiptNo());
						cell.setCellStyle(textCellStyle);

						cell = row.createCell(5);
						cell.setCellValue(report.getCustomerName());
						cell.setCellStyle(textCellStyle);

						cell = row.createCell(6);
						cell.setCellValue(report.getCommencementDate());
						cell.setCellStyle(dateCellStyle);

						cell = row.createCell(7);
						cell.setCellValue(report.getPaymentDate());
						cell.setCellStyle(dateCellStyle);

						cell = row.createCell(8);
						cell.setCellValue(report.getAmount());
						cell.setCellStyle(currencyCellStyle);

						cell = row.createCell(9);
						cell.setCellValue(report.getStampFee());
						cell.setCellStyle(currencyCellStyle);

						cell = row.createCell(10);
						cell.setCellValue(report.getPaymentTypeName());
						cell.setCellStyle(textCellStyle);

						cell = row.createCell(11);
						cell.setCellValue(report.getBranchName());
						cell.setCellStyle(textCellStyle);

						cell = row.createCell(12);
						cell.setCellValue(report.getSalePointName());
						cell.setCellStyle(textCellStyle);

					}
					i = i + 1;
					sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
					row = sheet.createRow(i);

					cell = row.createCell(0);
					cell.setCellValue("Sub Total");
					cell.setCellStyle(defaultCellStyle);
					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);

					cell = row.createCell(8);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(I" + startIndex + ":I" + i + ")";
					GrandSIFormula += "I" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					cell = row.createCell(9);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(J" + startIndex + ":J" + i + ")";
					GrandStampFeeFormula += "J" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					sheet.addMergedRegion(new CellRangeAddress(i, i, 9, 9));
					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 9, 10), sheet, wb);
				}
				i = i + 1;
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);

				cell = row.createCell(8);
				cell.setCellStyle(currencyCellStyle);
				GrandSIFormula = GrandSIFormula.substring(0, GrandSIFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandSIFormula);

				cell = row.createCell(9);
				cell.setCellStyle(currencyCellStyle);
				GrandStampFeeFormula = GrandStampFeeFormula.substring(0, GrandStampFeeFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandStampFeeFormula);

				sheet.addMergedRegion(new CellRangeAddress(i, i, 9, 9));
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 9, 10), sheet, wb);

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

	public LazyDataModelUtil<LifeDailyIncomeReport> getLifeDailyIncomeReportLazyModel() {
		return lazyModel;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void setAccessBranches(boolean accessBranches) {
		this.accessBranches = accessBranches;
	}

	public LifeProductType getSelectedProductType() {
		return selectedProductType;
	}

	public void setSelectedProductType(LifeProductType selectedProductType) {
		this.selectedProductType = selectedProductType;
	}

	public Map<String, List<LifeDailyIncomeReport>> separateBranch() {
		Map<String, List<LifeDailyIncomeReport>> map = new LinkedHashMap<String, List<LifeDailyIncomeReport>>();
		if (lifeDailyIncomeList != null) {
			for (LifeDailyIncomeReport report : lifeDailyIncomeList) {
				if (map.containsKey(report.getBranchName())) {
					map.get(report.getBranchName()).add(report);
				} else {
					List<LifeDailyIncomeReport> list = new ArrayList<LifeDailyIncomeReport>();
					list.add(report);
					map.put(report.getBranchName(), list);
				}
			}
		}
		return map;

	}
}
