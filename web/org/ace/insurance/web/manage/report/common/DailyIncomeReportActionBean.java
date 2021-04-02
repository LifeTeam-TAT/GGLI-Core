package org.ace.insurance.web.manage.report.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProductType;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.payment.service.interfaces.ITLFService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
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

@ManagedBean(name = "DailyIncomeReportActionBean")
@ViewScoped
public class DailyIncomeReportActionBean extends BaseBean {

	@ManagedProperty(value = "#{TLFService}")
	private ITLFService tlfService;

	public void setTlfService(ITLFService tlfService) {
		this.tlfService = tlfService;
	}

	private List<DailyIncomeReportDTO> dailyIncomeReportDTOList;
	private TLFCriteria tlfCriteria;
	private ProductType productType;
	private List<String> productIdList;
	// TODO FIXME PSH create salepoint report file
	private final String reportName = "LifePolicyReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		initialization();
	}

	public void initialization() {
		tlfCriteria = new TLFCriteria();
		resetCriteriaDate();
		dailyIncomeReportDTOList = new ArrayList<>();
	}

	public void resetCriteriaDate() {
		tlfCriteria.setEndDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		tlfCriteria.setStartDate(cal.getTime());
	}

	public void resetCriteria() {
		tlfCriteria = new TLFCriteria();
		dailyIncomeReportDTOList = new ArrayList<>();
		resetCriteriaDate();
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		tlfCriteria.setBranch(branch);
		tlfCriteria.setSalePoint(null);
	}

	public void selectSalePoint() {
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
		selectSalePointByBranch(tlfCriteria.getBranch() == null ? "" : tlfCriteria.getBranch().getId());
	}

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(tlfCriteria.getEntity() == null ? "" : tlfCriteria.getEntity().getId(), user.getBranch().getId());
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		tlfCriteria.setSalePoint(salePoint);
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		tlfCriteria.setEntity(entity);
		tlfCriteria.setBranch(null);
		tlfCriteria.setSalePoint(null);
	}

	public void removeEntity() {
		tlfCriteria.setEntity(null);
		tlfCriteria.setBranch(null);
		tlfCriteria.setSalePoint(null);
	}

	public void removeBranch() {
		tlfCriteria.setSalePoint(null);
		tlfCriteria.setBranch(null);
	}

	public void resetSalePoint() {
		tlfCriteria.setSalePoint(null);
	}

	public void SearchDTOList() {
		boolean valid = true;
		String formID = "accountList";
		if (tlfCriteria.getStartDate() != null && tlfCriteria.getEndDate() != null) {
			if (tlfCriteria.getStartDate().after(tlfCriteria.getEndDate())) {
				addErrorMessage(formID + ":startDate", MessageId.STARTDATE_MUSTBE_LESSTHAN_ENDDATE);
				valid = false;
			}
		}
		if (valid) {
			getProductIdFromProdctType();
			dailyIncomeReportDTOList = tlfService.findDailyIncomeReportByCriteria(tlfCriteria);
		}
	}

	public void getProductIdFromProdctType() {
		this.tlfCriteria.getProductIdList().clear();
		List<String> idList = new ArrayList<>();
		if (null != this.productType) {
			switch (productType) {
				case CRITICALILLNESS:
					idList.add(ProductIDConfig.getGroupCriticalIllness_Id());
					idList.add(ProductIDConfig.getIndividualCriticalIllness_Id());
					break;
				case FARMER:
					idList.add(ProductIDConfig.getFarmerId());
					break;
				case GROUP_LIFE:
					idList.add(ProductIDConfig.getGroupLifeId());
					break;
				case HEALTH_INSURANCE:
					idList.add(ProductIDConfig.getGroupHealthInsuranceId());
					idList.add(ProductIDConfig.getIndividualHealthInsuranceId());
					break;
				case MICRO_HEALTH_INSURANCE:
					idList.add(ProductIDConfig.getMicroHealthInsurance());
					break;
				case PUBLIC_LIFE:
					idList.add(ProductIDConfig.getPublicLifeId());
					break;
				case SHORT_TERM_ENDOWMNENT:
					idList.add(ProductIDConfig.getShortEndowLifeId());
					break;
				case SNAKE_BITE:
					idList.add(ProductIDConfig.getSnakeBikeId());
					break;
				case SPORT_MAN:
					idList.add(ProductIDConfig.getSportManId());
					break;
				case TRAVEL:
					idList.add(ProductIDConfig.getTravelInsuranceId());
					break;
				case PERSONTRAVEL:
					idList.add(ProductIDConfig.getPersonTravelId());
					idList.add(ProductIDConfig.getPersonTravelFoeign());
					idList.add(ProductIDConfig.getPersonTravelUnder100Id());
					break;
				default:
					break;

			}
		} else {
			idList.add(ProductIDConfig.getGroupCriticalIllness_Id());
			idList.add(ProductIDConfig.getIndividualCriticalIllness_Id());
			idList.add(ProductIDConfig.getFarmerId());
			idList.add(ProductIDConfig.getGroupLifeId());
			idList.add(ProductIDConfig.getGroupHealthInsuranceId());
			idList.add(ProductIDConfig.getIndividualHealthInsuranceId());
			idList.add(ProductIDConfig.getMicroHealthInsurance());
			idList.add(ProductIDConfig.getPublicLifeId());
			idList.add(ProductIDConfig.getShortEndowLifeId());
			idList.add(ProductIDConfig.getSnakeBikeId());
			idList.add(ProductIDConfig.getSportManId());
			idList.add(ProductIDConfig.getTravelInsuranceId());
			idList.add(ProductIDConfig.getPersonTravelId());
			idList.add(ProductIDConfig.getPersonTravelFoeign());
			idList.add(ProductIDConfig.getPersonTravelUnder100Id());
		}

		this.tlfCriteria.setProductIdList(idList);
	}

	public void exportExcel() {
		ExternalContext ec = getFacesContext().getExternalContext();
		ec.responseReset();
		ec.setResponseContentType("application/vnd.ms-excel");
		String fileName = "dailyIncomeReport.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try (OutputStream op = ec.getResponseOutputStream();) {
			ExportExcel exportExcel = new ExportExcel();
			exportExcel.generate(op);
			getFacesContext().responseComplete();
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export LifeProposalReport.xlsx", e);
		}
	}

	private class ExportExcel {
		private XSSFWorkbook wb;

		public ExportExcel() {
			load();
		}

		private void load() {
			try (InputStream inp = this.getClass().getResourceAsStream("/report-template/accountReport/dailyIncomeReport.xlsx");) {
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load LifeProposalReport.xlsx template", e);
			}
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("SalePointReport");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;
				Date date = new Date();
				String date1 = DateUtils.getDateFormatString(date);
				// ExcelUtils.fillCompanyLogo(wb, sheet, 7);
				ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 3, 7, 9);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				if (productType == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Daily Income Report ( All ) \t " + date1);
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Daily Income Report ( " + productType.getLabel() + " ) \t " + date1);
				}

				int i = 1;
				int index = 0;
				int startIndex = 0;
				String strFormula;
				String GrandSIFormula = "";
				String GrandPremiumFormula = "";
				Map<String, List<DailyIncomeReportDTO>> map = separateSalePoint();
				for (List<DailyIncomeReportDTO> dailyIncomeDTOList : map.values()) {
					startIndex = i + 1 + 1;
					for (DailyIncomeReportDTO dailyIncomeDTO : dailyIncomeDTOList) {
						i = i + 1;
						index = index + 1;
						row = sheet.createRow(i);
						// index
						cell = row.createCell(0);
						cell.setCellValue(index);
						cell.setCellStyle(defaultCellStyle);
						// product name
						cell = row.createCell(1);
						cell.setCellValue(dailyIncomeDTO.getProductName());
						cell.setCellStyle(textCellStyle);
						// pocliy no
						cell = row.createCell(2);
						cell.setCellValue(dailyIncomeDTO.getPolicyNo());
						cell.setCellStyle(textCellStyle);

						// Receipt No
						cell = row.createCell(3);
						cell.setCellValue(dailyIncomeDTO.getReceiptNo());
						cell.setCellStyle(textCellStyle);

						// received Date
						cell = row.createCell(4);
						cell.setCellValue(dailyIncomeDTO.getReceiveDate());
						cell.setCellStyle(dateCellStyle);

						// Amont
						cell = row.createCell(5);
						cell.setCellValue(dailyIncomeDTO.getAmount());
						cell.setCellStyle(currencyCellStyle);

						// payment channel
						cell = row.createCell(6);
						cell.setCellValue(dailyIncomeDTO.getPaymentChannel().getLabel());
						cell.setCellStyle(textCellStyle);

						cell = row.createCell(8);
						cell.setCellValue(dailyIncomeDTO.getBranchName());
						cell.setCellStyle(textCellStyle);

						// salepoint name
						cell = row.createCell(7);
						cell.setCellValue(dailyIncomeDTO.getSalePointName());
						cell.setCellStyle(textCellStyle);

						/*
						 * // workFlowTask cell = row.createCell(11);
						 * cell.setCellValue(lifeProposalReport.getWorkflow());
						 * cell.setCellStyle(textCellStyle);
						 * 
						 * // responsiblePerson cell = row.createCell(12);
						 * cell.setCellValue(lifeProposalReport.
						 * getResponsiblePerson());
						 * cell.setCellStyle(textCellStyle);
						 * 
						 * // remarks cell = row.createCell(13);
						 * cell.setCellValue(lifeProposalReport.getRemark());
						 * cell.setCellStyle(textCellStyle); //
						 */
					}
					// i = i + 1;
					// sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
					// row = sheet.createRow(i);
					//
					// cell = row.createCell(0);
					// ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new
					// CellRangeAddress(i, i, 0, 6), sheet, wb);
					// cell.setCellValue("Sub Total");
					// cell.setCellStyle(defaultCellStyle);
					//
					// cell = row.createCell(8);
					// cell.setCellStyle(currencyCellStyle);
					// strFormula = "SUM(H" + startIndex + ":H" + i + ")";
					// GrandSIFormula += "H" + (i + 1) + "+";
					// cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					// cell.setCellFormula(strFormula);

					// cell = row.createCell(7);
					// cell.setCellStyle(defaultCellStyle);
					//
					// cell = row.createCell(9);
					// cell.setCellStyle(currencyCellStyle);
					// strFormula = "SUM(J" + startIndex + ":J" + i + ")";
					// GrandPremiumFormula += "J" + (i + 1) + "+";
					// cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					// cell.setCellFormula(strFormula);
					//
					// sheet.addMergedRegion(new CellRangeAddress(i, i, 10,
					// 13));
					// ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new
					// CellRangeAddress(i, i, 10, 13), sheet, wb);
				}

				i = i + 1;
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 4));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 4), sheet, wb);
				cell.setCellValue("Total");
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(5);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(F3:F" + i + ")";
				GrandSIFormula += "H" + (i + 1) + "+";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);
				// i = i + 1;
				// sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
				// row = sheet.createRow(i);
				//
				// cell = row.createCell(0);
				// cell.setCellValue("Grand Total");
				// ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new
				// CellRangeAddress(i, i, 0, 6), sheet, wb);
				// cell.setCellStyle(defaultCellStyle);
				//
				// cell = row.createCell(8);
				// cell.setCellStyle(currencyCellStyle);
				// GrandSIFormula = GrandSIFormula.substring(0,
				// GrandSIFormula.length() - 1);
				// cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				// cell.setCellFormula(GrandSIFormula);
				//
				// cell = row.createCell(7);
				// cell.setCellStyle(defaultCellStyle);
				//
				// cell = row.createCell(9);
				// cell.setCellStyle(currencyCellStyle);
				// GrandPremiumFormula = GrandPremiumFormula.substring(0,
				// GrandPremiumFormula.length() - 1);
				// cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				// cell.setCellFormula(GrandPremiumFormula);
				//
				// sheet.addMergedRegion(new CellRangeAddress(i, i, 10, 13));
				// ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new
				// CellRangeAddress(i, i, 10, 13), sheet, wb);

				wb.setPrintArea(0, 0, 8, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (

			Exception e) {
				e.printStackTrace();
			}
		}

	}

	public Map<String, List<DailyIncomeReportDTO>> separateSalePoint() {
		Map<String, List<DailyIncomeReportDTO>> map = new LinkedHashMap<>();
		for (DailyIncomeReportDTO dto : dailyIncomeReportDTOList) {
			if (map.containsKey(dto.getPolicyNo())) {
				map.get(dto.getPolicyNo()).add(dto);
			} else {
				List<DailyIncomeReportDTO> list = new ArrayList<>();
				list.add(dto);
				map.put(dto.getPolicyNo(), list);
			}
		}
		return map;
	}

	public TLFCriteria getTlfCriteria() {
		return tlfCriteria;
	}

	public void setTlfCriteria(TLFCriteria tlfCriteria) {
		this.tlfCriteria = tlfCriteria;
	}

	public List<DailyIncomeReportDTO> getDailyIncomeReportDTOList() {
		return dailyIncomeReportDTOList;
	}

	public ProductType[] getProductTypes() {
		return ProductType.values();
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
}
