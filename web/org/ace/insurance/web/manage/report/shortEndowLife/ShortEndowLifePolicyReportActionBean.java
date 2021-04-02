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
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.report.life.LifePolicyReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ILifePolicyReportService;
import org.ace.insurance.report.shortEndowLife.ShortEndowLifePolicyReport;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.organization.Organization;
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
import org.primefaces.model.LazyDataModel;

@ViewScoped
@ManagedBean(name = "ShortEndowLifePolicyReportActionBean")
public class ShortEndowLifePolicyReportActionBean extends BaseBean implements Serializable {
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

	private LifePolicyReportCriteria criteria;
	private List<ShortEndowLifePolicyReport> lifePolicyList;
	private LazyDataModelUtil<ShortEndowLifePolicyReport> lazyModel;
	private boolean accessBranches;
	private User user;
	// private String branch;
	private List<String> productIdList;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}
		productIdList = new ArrayList<String>();
		productIdList.add(ProductIDConfig.getShortEndowLifeId());
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new LifePolicyReportCriteria();
		if (!accessBranches) {
			criteria.setBranch(user.getBranch());
			criteria.setEntity(user.getBranch().getEntity());
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		lifePolicyList = new ArrayList<>();
		lazyModel = null;
	}

	public ProposalType[] getProposalTypeSelectItemList() {
		return ProposalType.values();
	}

	public LazyDataModel<ShortEndowLifePolicyReport> getLazyModel() {
		return lazyModel;
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
			lifePolicyList = lifePolicyReportService.findShortEndowLifePolicyReport(criteria);
			if (lifePolicyList != null) {
				lazyModel = new LazyDataModelUtil<ShortEndowLifePolicyReport>(lifePolicyList);
			}
		}
	}

	public ILifePolicyReportService getLifePolicyReportService() {
		return lifePolicyReportService;
	}

	public LifePolicyReportCriteria getCriteria() {
		return criteria;
	}

	// private final String reportName = "ShortEndowLifePolicyReport";
	// private final String pdfDirPath = "/pdf-report/" + reportName + "/" +
	// System.currentTimeMillis() + "/";
	// private final String dirPath = getWebRootPath() + pdfDirPath;
	// private final String fileName = reportName + ".pdf";
	//
	// public String getStream() {
	// return pdfDirPath + fileName;
	// }

	// public void generateReport() {
	// try {
	// FileHandler.forceMakeDirectory(dirPath);
	// if (criteria.getBranch() == null) {
	// branch = "All";
	// } else {
	// branch = criteria.getBranch().getName();
	// }
	// ShortEndowUnderwritingDOCFactory.generateShortEndowLifePolicyReport(lifePolicyList,
	// dirPath, fileName, branch);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	public List<Branch> getBranchList() {
		return branchService.findAllBranch();
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void removeBranch() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
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

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		criteria.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		criteria.setOrganization(organization);
	}

	public void selectProduct() {
		selectProduct(InsuranceType.LIFE);
	}

	// Exporting
	public void exportExcel() {
		if (null != lifePolicyList && lifePolicyList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "ShortEndowLifePolicyReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel(lifePolicyList);
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

		public ExportExcel(List<ShortEndowLifePolicyReport> lifePolicyList) {

			load();
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/shortTermEndowLife/ShortEndowLifePolicyReport.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load ShortEndowLifePolicyReport.xlsx template", e);
			}
		}

		public Map<String, List<ShortEndowLifePolicyReport>> separateBranch() {
			Map<String, List<ShortEndowLifePolicyReport>> map = new LinkedHashMap<String, List<ShortEndowLifePolicyReport>>();
			for (ShortEndowLifePolicyReport report : lifePolicyList) {
				if (map.containsKey(report.getBranchName())) {
					map.get(report.getBranchName()).add(report);
				} else {
					List<ShortEndowLifePolicyReport> list = new ArrayList<ShortEndowLifePolicyReport>();
					list.add(report);
					map.put(report.getBranchName(), list);
				}
			}
			return map;
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("LifePolicyReport");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 4, 13, 16);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				if (criteria.getBranch() == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Short Term Endowment Life Policy Report ( All )");
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Short Term Endowment Life Policy Report ( " + criteria.getBranch().getName() + " )");
				}

				int i = 3;
				int index = 0;
				int startIndex;
				String strFormula;
				String GrandSIFormula = "";
				String GrandPremiumFormula = "";
				String GrandTermPremiumFormula = "";
				String GrandInstallmentPremiumFormula = "";
				Map<String, List<ShortEndowLifePolicyReport>> map = separateBranch();
				for (List<ShortEndowLifePolicyReport> branchList : map.values()) {
					startIndex = i + 1;
					for (ShortEndowLifePolicyReport lifePolicyReport : branchList) {
						index = index + 1;
						row = sheet.createRow(i);

						// index
						cell = row.createCell(0);
						cell.setCellValue(index);
						cell.setCellStyle(defaultCellStyle);

						// commencementDate
						cell = row.createCell(1);
						if (lifePolicyReport.getCommencementDate() != null) {
							cell.setCellValue(lifePolicyReport.getCommencementDate());
							cell.setCellStyle(dateCellStyle);
						}

						// policyNo
						cell = row.createCell(2);
						cell.setCellValue(lifePolicyReport.getPolicyNo());
						cell.setCellStyle(textCellStyle);

						// proposal No
						cell = row.createCell(3);
						cell.setCellValue(lifePolicyReport.getProposalNo());
						cell.setCellStyle(textCellStyle);

						// insuredName
						cell = row.createCell(4);
						cell.setCellValue(lifePolicyReport.getInsuredPersonName());
						cell.setCellStyle(textCellStyle);

						// sumInsured
						cell = row.createCell(5);
						cell.setCellValue(lifePolicyReport.getSumInsured());
						cell.setCellStyle(currencyCellStyle);

						// policyStartDate
						cell = row.createCell(6);
						cell.setCellValue(lifePolicyReport.getStartDate());
						cell.setCellStyle(dateCellStyle);

						// policyEndDate
						cell = row.createCell(7);
						cell.setCellValue(lifePolicyReport.getEndDate());
						cell.setCellStyle(dateCellStyle);

						// assignee / customer
						cell = row.createCell(8);
						cell.setCellValue(lifePolicyReport.getAssignee());
						cell.setCellStyle(textCellStyle);

						// yearlyPremium
						cell = row.createCell(9);
						cell.setCellValue(lifePolicyReport.getOneYearPremium());
						cell.setCellStyle(currencyCellStyle);

						// modePremium
						cell = row.createCell(10);
						cell.setCellValue(lifePolicyReport.getModeOfPremium());
						cell.setCellStyle(currencyCellStyle);

						// installmentPremium
						cell = row.createCell(11);
						cell.setCellValue(lifePolicyReport.getInstallmentPremium());
						cell.setCellStyle(currencyCellStyle);

						// agentName
						cell = row.createCell(12);
						cell.setCellValue(lifePolicyReport.getAgentName());
						cell.setCellStyle(textCellStyle);

						// branchName
						cell = row.createCell(13);
						cell.setCellValue(lifePolicyReport.getBranchName());
						cell.setCellStyle(textCellStyle);

						// salePointName
						cell = row.createCell(14);
						cell.setCellValue(lifePolicyReport.getSalePoint());
						cell.setCellStyle(textCellStyle);

						// remark
						cell = row.createCell(15);
						cell.setCellStyle(textCellStyle);
						i = i + 1;
					}
					sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 3));
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

					cell = row.createCell(9);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(J" + startIndex + ":J" + i + ")";
					GrandPremiumFormula += "J" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					cell = row.createCell(10);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(K" + startIndex + ":K" + i + ")";
					GrandTermPremiumFormula += "K" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					cell = row.createCell(11);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(L" + startIndex + ":L" + i + ")";
					GrandInstallmentPremiumFormula += "L" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 11, 13), sheet, wb);
					i = i + 1;
				}
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 3));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 3), sheet, wb);

				cell = row.createCell(5);
				cell.setCellStyle(currencyCellStyle);
				GrandSIFormula = GrandSIFormula.substring(0, GrandSIFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandSIFormula);

				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 5, 7), sheet, wb);

				cell = row.createCell(9);
				cell.setCellStyle(currencyCellStyle);
				GrandPremiumFormula = GrandPremiumFormula.substring(0, GrandPremiumFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandPremiumFormula);

				cell = row.createCell(10);
				cell.setCellStyle(currencyCellStyle);
				GrandTermPremiumFormula = GrandTermPremiumFormula.substring(0, GrandTermPremiumFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandTermPremiumFormula);

				cell = row.createCell(11);
				cell.setCellStyle(currencyCellStyle);
				GrandInstallmentPremiumFormula = GrandInstallmentPremiumFormula.substring(0, GrandInstallmentPremiumFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandInstallmentPremiumFormula);

				cell = row.createCell(13);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(14);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(15);
				cell.setCellStyle(defaultCellStyle);

				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 11, 15), sheet, wb);

				wb.setPrintArea(0, 0, 15, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
