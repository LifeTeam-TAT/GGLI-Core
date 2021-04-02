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
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.branch.Branch;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "AccountAndLifeDeptCancelReportActionBean")
@ViewScoped
public class AccountAndLifeDeptCancelReportActionBean extends BaseBean {
	@ManagedProperty(value = "#{TLFService}")
	private ITLFService tlfService;

	public void setTlfService(ITLFService tlfService) {
		this.tlfService = tlfService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private List<AccountAndLifeDeptCancelReportDTO> accountAndLifeDeptCancelReportDTOList;
	private TLFCriteria tlfCriteria;
	// private List<ProductGroup> productGroupList;
	private ProductType productType;
	private List<String> productIdList;
	// private ProductGroup productGroup;
	// TODO FIXME PSH create salepoint report file
	private final String reportName = "LifePolicyReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		initialization();
	}

	public void initialization() {
		tlfCriteria = new TLFCriteria();
		resetCriteriaDate();
		accountAndLifeDeptCancelReportDTOList = new ArrayList<>();
		// productGroupList = productService.findAllProductGroup();
	}

	public void resetCriteriaDate() {
		tlfCriteria.setEndDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		tlfCriteria.setStartDate(cal.getTime());
	}

	public void resetCriteria() {
		tlfCriteria = new TLFCriteria();
		accountAndLifeDeptCancelReportDTOList = new ArrayList<>();
		resetCriteriaDate();
		productType = null;
	}

	public void selectSalePoint() {
		selectSalePointByBranch(tlfCriteria.getBranch() == null ? "" : tlfCriteria.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
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
		tlfCriteria.setBranch(null);
		tlfCriteria.setSalePoint(null);
	}

	public void resetSalePoint() {
		tlfCriteria.setSalePoint(null);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		tlfCriteria.setBranch(branch);
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
			accountAndLifeDeptCancelReportDTOList = tlfService.findAccountAndLifeDeptCancelReportDTOByCriteria(tlfCriteria);
		}
	}

	/*
	 * public void getProductFromProductGroup() {
	 * this.tlfCriteria.getProductGroupIdList().clear(); if (null !=
	 * this.productGroup) {
	 * this.tlfCriteria.getProductGroupIdList().add(productGroup.getId()); }
	 * else { List<String> groupIdList = new ArrayList<>(); for (ProductGroup
	 * productGroup : productGroupList) { groupIdList.add(productGroup.getId());
	 * } tlfCriteria.setProductGroupIdList(groupIdList); // TODO FIXME FIND Null
	 * error
	 * 
	 * for (ProductGroup productGroup : productGroupList) {
	 * this.tlfCriteria.getProductGroupIdList().add(productGroup.getId() ); }
	 * 
	 * } }
	 */

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
					idList.add(ProductIDConfig.getPersonTravelId());
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
		}

		this.tlfCriteria.setProductIdList(idList);
	}

	public void exportExcel() {
		if (null != accountAndLifeDeptCancelReportDTOList && accountAndLifeDeptCancelReportDTOList.size() > 0) {

			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "AccountAndLifeDepartmentCancelReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try (OutputStream op = ec.getResponseOutputStream();) {
				ExportExcel exportExcel = new ExportExcel();
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export LifeProposalReport.xlsx", e);
			}
		} else {
			addErrorMessage("accountList:salePointTable", MessageId.THERE_IS_NO_DATA);
		}

	}

	private class ExportExcel {
		private XSSFWorkbook wb;

		public ExportExcel() {
			load();
		}

		private void load() {
			try (InputStream inp = this.getClass().getResourceAsStream("/report-template/accountReport/accountAndLifedeptReport.xlsx");) {
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load accountAndLifedeptReport.xlsx template", e);
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

				// ExcelUtils.fillCompanyLogo(wb, sheet, 11);
				ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 3, 10, 12);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				Date date = new Date();
				String date1 = DateUtils.getDateFormatString(date);
				if (productType == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Account And Life Department Cancel Report ( All )\t " + date1);
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Account And Life Department Cancel Report ( " + productType.getLabel() + " ) \t " + date1);
				}

				int i = 1;
				int index = 0;
				int startIndex;
				String accountPremiumFormula;
				String lifePremiumFormula;
				String GrandSIFormula = "";
				String GrandPremiumFormula = "";
				Map<String, List<AccountAndLifeDeptCancelReportDTO>> map = separateSalePoint();
				for (List<AccountAndLifeDeptCancelReportDTO> salePointList : map.values()) {
					startIndex = i + 1 + 1;
					for (AccountAndLifeDeptCancelReportDTO salePointDTO : salePointList) {
						i = i + 1;
						index = index + 1;
						row = sheet.createRow(i);
						// index
						cell = row.createCell(0);
						cell.setCellValue(index);
						cell.setCellStyle(defaultCellStyle);
						// TLF no
						cell = row.createCell(1);
						cell.setCellValue(salePointDTO.getTlfNo());
						cell.setCellStyle(defaultCellStyle);

						// PRODUCT GROUP
						cell = row.createCell(2);
						cell.setCellValue(salePointDTO.getProductGroupName());
						cell.setCellStyle(defaultCellStyle);

						// PAYMENT CHANNEL
						cell = row.createCell(3);
						if (salePointDTO.getPaymentChannel() != null) {
							salePointDTO.getPaymentChannel().getLabel();
						} else {
							cell.setCellValue("-");
						}
						cell.setCellStyle(defaultCellStyle);

						// ACCOUNT RECEIPT DATE
						cell = row.createCell(4);
						if (salePointDTO.getAccountReceiptDate() != null) {
							cell.setCellValue(salePointDTO.getAccountReceiptDate());
						} else {
							cell.setCellValue("-");
						}
						cell.setCellStyle(dateCellStyle);

						// sale point name
						cell = row.createCell(5);
						cell.setCellValue(salePointDTO.isAccountStatus() ? "CANCEL" : "COMPLETE");
						cell.setCellStyle(defaultCellStyle);

						cell = row.createCell(6);
						cell.setCellValue(salePointDTO.getAmount());
						cell.setCellStyle(currencyCellStyle);
						//
						cell = row.createCell(7);
						if (salePointDTO.getLifeDeptPaymentDate() != null) {
							cell.setCellValue(salePointDTO.getLifeDeptPaymentDate());
						} else {
							cell.setCellValue("-");
						}
						cell.setCellStyle(dateCellStyle);

						cell = row.createCell(8);
						cell.setCellValue(salePointDTO.isLifeDeptStatus() ? "CANCEL" : "COMPLETE");
						cell.setCellStyle(defaultCellStyle);

						cell = row.createCell(9);
						cell.setCellValue(salePointDTO.getAccountPremium());
						cell.setCellStyle(currencyCellStyle);

						cell = row.createCell(10);
						cell.setCellValue(salePointDTO.getBranchName());
						cell.setCellStyle(defaultCellStyle);

						// SALEPOINT
						cell = row.createCell(11);
						cell.setCellValue(salePointDTO.getSalePointName());
						cell.setCellStyle(defaultCellStyle);

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
						 * cell.setCellStyle(textCellStyle);
						 */ }
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
					//
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
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);
				cell.setCellValue("Total");
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(7);
				cell.setCellStyle(currencyCellStyle);
				accountPremiumFormula = "SUM(H3:H" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(accountPremiumFormula);

				cell = row.createCell(10);
				cell.setCellStyle(currencyCellStyle);
				accountPremiumFormula = "SUM(H3:H" + i + ")";
				lifePremiumFormula = "SUM(K3:K" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(lifePremiumFormula);
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

				wb.setPrintArea(0, 0, 11, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, List<AccountAndLifeDeptCancelReportDTO>> separateSalePoint() {
		Map<String, List<AccountAndLifeDeptCancelReportDTO>> map = new LinkedHashMap<>();
		for (AccountAndLifeDeptCancelReportDTO dto : accountAndLifeDeptCancelReportDTOList) {
			if (map.containsKey(dto.getSalePointName())) {
				map.get(dto.getSalePointName()).add(dto);
			} else {
				List<AccountAndLifeDeptCancelReportDTO> list = new ArrayList<>();
				list.add(dto);
				map.put(dto.getSalePointName(), list);
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

	public List<AccountAndLifeDeptCancelReportDTO> getAccountAndLifeDeptCancelReportDTOList() {
		return accountAndLifeDeptCancelReportDTOList;
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
