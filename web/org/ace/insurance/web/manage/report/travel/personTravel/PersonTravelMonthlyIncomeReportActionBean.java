package org.ace.insurance.web.manage.report.travel.personTravel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.travel.personTravel.PersonTravelMonthlyIncomeReport;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.MyanmarReportTitlePicker;
import org.ace.insurance.web.manage.report.travel.personTravel.criteria.PersonTravelCriteria;
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
@ManagedBean(name = "PersonTravelMonthlyIncomeReportActionBean")
public class PersonTravelMonthlyIncomeReportActionBean extends BaseBean {

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private PersonTravelCriteria criteria;
	private List<PersonTravelMonthlyIncomeReport> reportList;
	private User user;
	private boolean isAccessBranches;
	private boolean isUnder100Travel;
	private String productId;
	private List<Product> productList;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		productList = productService.findProductByInsuranceType(InsuranceType.PERSON_TRAVEL);
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new PersonTravelCriteria();
		DateTime dateTime = new DateTime();
		criteria.setMonth(dateTime.getMonthOfYear() - 1);
		criteria.setYear(dateTime.getYear());
		criteria.setProductId(productList.get(0).getId());
		if (user.isAccessAllBranch()) {
			isAccessBranches = true;
		} else {
			criteria.setBranchId(user.getBranch().getId());
			criteria.setBranchName(user.getBranch().getName());
		}
		search();
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

	public void search() {
		productId = criteria.getProductId();
		isUnder100Travel = KeyFactorChecker.isUnder100MilesTravel(productId);
		reportList = personTravelPolicyService.findPersonTravelMonthlyIncome(criteria);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgentId(agent.getId());
		criteria.setAgentName(agent.getFullName());
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);

	}

	public void clearBranch() {
		// criteria.setBranchId(null);
		// criteria.setBranchName(null);
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void clearAgent() {
		criteria.setAgentId(null);
		criteria.setAgentName(null);
	}

	// Exporting
	public void exportExcel() {
		if (null != reportList && reportList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			boolean isUnder100Travel = KeyFactorChecker.isUnder100MilesTravel(productId);
			String fileName = isUnder100Travel ? "Under100TravelMonthlyIncomeReport.xlsx" : "GenerallTravelMonthlyIncomeReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try (OutputStream op = ec.getResponseOutputStream();) {
				ExportExcel exportExcel = new ExportExcel(reportList, isUnder100Travel);
				if (isUnder100Travel) {
					exportExcel.generateUnder100Travel(op);
				} else {
					exportExcel.generateGeneralTravel(op);
				}
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export Person Travel Monthly Income Report", e);
			}
		} else {
			addErrorMessage("montlyIncomeReportForm:travelList", MessageId.THERE_IS_NO_DATA);
		}
	}

	public class ExportExcel {
		private List<PersonTravelMonthlyIncomeReport> reportList;
		private boolean isUnder100Travel;
		private XSSFWorkbook wb;

		public ExportExcel(List<PersonTravelMonthlyIncomeReport> reportList, boolean isUnder100Travel) {
			this.reportList = reportList;
			this.isUnder100Travel = isUnder100Travel;
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

		public void generateUnder100Travel(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("Under100Travel");
				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				row = sheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue(criteria.getYear() + MyanmarReportTitlePicker.getYearMyanmarText() + Utils.getMonthString(criteria.getMonth())
						+ MyanmarReportTitlePicker.getForMonthMyanmarText() + MyanmarReportTitlePicker.getTravelIncomeMyanmarText());
				cell.setCellStyle(getTitleCellStyle());

				int i = 4;
				int index = 0;
				String strFormula;
				for (PersonTravelMonthlyIncomeReport travel : reportList) {
					index = index + 1;
					row = sheet.createRow(i);

					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(1);
					cell.setCellValue(travel.getPaymentDate());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(2);
					cell.setCellValue(travel.getExpressName());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(3);
					cell.setCellValue(travel.getAgentName());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(4);
					cell.setCellValue(travel.getTravelPath());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(5);
					cell.setCellValue(travel.getVehicleNo());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(6);
					cell.setCellValue(travel.getTravelDays());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(7);
					cell.setCellValue(travel.getTotalUnit());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(8);
					cell.setCellValue(travel.getSumInsured());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(9);
					cell.setCellValue(travel.getPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(10);
					cell.setCellValue(travel.getCommission());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(11);
					cell.setCellValue(travel.getNetPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(12);
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(13);
					cell.setCellValue(travel.getBranchName());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(14);
					cell.setCellValue(travel.getSalePointName());
					cell.setCellStyle(defaultCellStyle);

					i = i + 1;
				}
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));

				row = sheet.createRow(i);
				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);

				cell = row.createCell(7);
				cell.setCellStyle(defaultCellStyle);
				strFormula = "SUM(H5:H" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(8);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(I5:I" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(9);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(J5:J" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(10);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(K5:K" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(11);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(L5:L" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(12);
				cell.setCellStyle(defaultCellStyle);

				wb.setPrintArea(0, 0, 14, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (op != null) {
					try {
						op.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void generateGeneralTravel(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("GeneralTravel");
				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
				Row row;
				Cell cell;

				String reportTitle = null;
				if (KeyFactorChecker.isLocalTravel(productId)) {
					reportTitle = MyanmarReportTitlePicker.getLocalTravelMyanmarText();
				} else if (KeyFactorChecker.isOverseaTravel(productId)) {
					reportTitle = MyanmarReportTitlePicker.getOverseaTravelMyanmarText();
				}
				row = sheet.createRow(1);
				cell = row.createCell(0);
				cell.setCellValue(reportTitle);
				cell.setCellStyle(getTitleCellStyle());

				row = sheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue(criteria.getYear() + MyanmarReportTitlePicker.getYearMyanmarText() + Utils.getMonthString(criteria.getMonth())
						+ MyanmarReportTitlePicker.getForMonthMyanmarText() + MyanmarReportTitlePicker.getTravelIncomeMyanmarText());
				cell.setCellStyle(getTitleCellStyle());

				int i = 4;
				int index = 0;
				String strFormula;
				for (PersonTravelMonthlyIncomeReport travel : reportList) {
					index = index + 1;
					row = sheet.createRow(i);

					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(1);
					cell.setCellValue(travel.getPaymentDate());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(2);
					cell.setCellValue(travel.getProposalNo());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(3);
					cell.setCellValue(travel.getPolicyNo());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(4);
					cell.setCellValue(travel.getAgentName());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(5);
					cell.setCellValue(travel.getInsurers());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(6);
					cell.setCellValue(travel.getTravelPath());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(7);
					cell.setCellValue(travel.getVehicleNo());
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(8);
					cell.setCellValue(travel.getTravelDays());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(9);
					cell.setCellValue(travel.getTotalUnit());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(10);
					cell.setCellValue(travel.getSumInsured());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(11);
					cell.setCellValue(travel.getPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(12);
					cell.setCellValue(travel.getCommission());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(13);
					cell.setCellValue(travel.getNetPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(14);
					cell.setCellStyle(textCellStyle);

					cell = row.createCell(15);
					cell.setCellValue(travel.getBranchName());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(16);
					cell.setCellValue(travel.getSalePointName());
					cell.setCellStyle(defaultCellStyle);

					i = i + 1;
				}
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 8));
				row = sheet.createRow(i);
				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(9);
				cell.setCellStyle(defaultCellStyle);
				strFormula = "SUM(J5:J" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(10);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(K5:K" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(11);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(L5:L" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(12);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(M5:M" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(13);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(N5:N" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(14);
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(15);
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(16);
				cell.setCellStyle(textCellStyle);

				wb.setPrintArea(0, "$A$1:$Q$" + (i + 1));
				wb.write(op);
				op.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (op != null) {
					try {
						op.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void load() {
			String path = isUnder100Travel ? "/report-template/travel/personTravel/Under100TravelMonthlyIncomeReport.xlsx"
					: "/report-template/travel/personTravel/GenerallTravelMonthlyIncomeReport.xlsx";
			try (InputStream inp = this.getClass().getResourceAsStream(path);) {
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load TravelMonthlyIncomeReport.xlsx template", e);
			}
		}
	}

	public PersonTravelCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(PersonTravelCriteria criteria) {
		this.criteria = criteria;
	}

	public List<PersonTravelMonthlyIncomeReport> getReportList() {
		return reportList;
	}

	public boolean isAccessBranches() {
		return isAccessBranches;
	}

	public boolean isUnder100Travel() {
		return isUnder100Travel;
	}

	public List<Product> getProductList() {
		return productList;
	}

}
