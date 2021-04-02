package org.ace.insurance.web.manage.report.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.payment.service.interfaces.ITLFService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
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

@ManagedBean(name = "NewSalePointReportActionBean")
@ViewScoped
public class NewSalePointReportActionBean extends BaseBean {

	@ManagedProperty(value = "#{TLFService}")
	private ITLFService tlfService;

	public void setTlfService(ITLFService tlfService) {
		this.tlfService = tlfService;
	}

	private List<SalePointDTO> salePointDTOList;
	private TLFCriteria tlfCriteria;
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
		salePointDTOList = new ArrayList<>();
	}

	public void resetCriteriaDate() {
		tlfCriteria.setEndDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		tlfCriteria.setStartDate(cal.getTime());
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
			salePointDTOList = tlfService.findSalePointDTOByCriteria(tlfCriteria);
			Collections.sort(salePointDTOList);
		}
	}

	public void resetCriteria() {
		tlfCriteria = new TLFCriteria();
		salePointDTOList = new ArrayList<>();
		resetCriteriaDate();
	}

	public void selectSalePoint() {
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

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		tlfCriteria.setBranch(branch);
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

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportExcel() {
		if (salePointDTOList.size() > 0) {

			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "SalePointReport.xlsx";
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
			try (InputStream inp = this.getClass().getResourceAsStream("/report-template/salePoint/SalePointReport.xlsx");) {
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
				ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 3, 11, 13);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				if (tlfCriteria.getSalePoint() == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Sale Point Report ( All ) \t" + date1);
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Sale Point Report ( " + tlfCriteria.getSalePoint().getName() + " ) \t" + date1);
				}

				int i = 1;
				int index = 0;
				int startIndex;
				String cashDebitFormula;
				String cashCreditFormula;
				String transferDebitFormula;
				String transferCreditFormula;
				String GrandSIFormula = "";
				String GrandPremiumFormula = "";
				Map<String, List<SalePointDTO>> map = separateSalePoint();
				for (List<SalePointDTO> salePointList : map.values()) {
					startIndex = i + 1 + 1;
					for (SalePointDTO salePointDTO : salePointList) {
						i = i + 1;
						index = index + 1;
						row = sheet.createRow(i);
						// index
						cell = row.createCell(0);
						cell.setCellValue(index);
						cell.setCellStyle(defaultCellStyle);
						// policy no
						cell = row.createCell(1);
						cell.setCellValue(salePointDTO.getPolicyNo());
						cell.setCellStyle(textCellStyle);

						// TLF No
						cell = row.createCell(2);
						cell.setCellValue(salePointDTO.getTlfNo());
						cell.setCellStyle(textCellStyle);

						// coaid
						cell = row.createCell(3);
						cell.setCellValue(salePointDTO.getCoaId());
						cell.setCellStyle(textCellStyle);

						// narration
						cell = row.createCell(4);
						cell.setCellValue(salePointDTO.getNarration());
						cell.setCellStyle(textCellStyle);

						// sale point name
						cell = row.createCell(5);
						cell.setCellValue(salePointDTO.getSalePointName());
						cell.setCellStyle(textCellStyle);

						// Payment Channel
						cell = row.createCell(6);
						cell.setCellValue(salePointDTO.getPaymentChannel());
						cell.setCellStyle(textCellStyle);

						// Start Date
						cell = row.createCell(7);
						cell.setCellValue(salePointDTO.getCreatedDate());
						cell.setCellStyle(dateCellStyle);

						// CASHDEBIT
						cell = row.createCell(8);
						cell.setCellValue(salePointDTO.getCashDebit());
						cell.setCellStyle(currencyCellStyle);

						// CASHCREDIT
						cell = row.createCell(9);
						cell.setCellValue(salePointDTO.getCashCredit());
						cell.setCellStyle(currencyCellStyle);

						// CASHCREDIT
						cell = row.createCell(10);
						cell.setCellValue(salePointDTO.getTransferDebit());
						cell.setCellStyle(currencyCellStyle);

						// CASHCREDIT
						cell = row.createCell(11);
						cell.setCellValue(salePointDTO.getTransferCredit());
						cell.setCellStyle(currencyCellStyle);

						// Agent Transaction
						cell = row.createCell(12);
						cell.setCellValue(salePointDTO.isAgentTransaction());
						cell.setCellStyle(textCellStyle);

						/*
						 * cell = row.createCell(13);
						 * cell.setCellValue(salePointDTO.getBranchName());
						 * cell.setCellStyle(textCellStyle);
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
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 7), sheet, wb);
				cell.setCellValue("Total");
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(8);
				cell.setCellStyle(currencyCellStyle);
				cashDebitFormula = "SUMIF(M3:M" + i + "," + "\"FALSE\"" + "," + "I3:I" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(cashDebitFormula);

				cell = row.createCell(9);
				cell.setCellStyle(currencyCellStyle);
				cashCreditFormula = "SUMIF(M3:M" + i + "," + "\"FALSE\"" + "," + "J3:J" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(cashCreditFormula);

				cell = row.createCell(10);
				cell.setCellStyle(currencyCellStyle);
				transferDebitFormula = "SUMIF(M3:M" + i + "," + "\"FALSE\"" + "," + "K3:K" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(transferDebitFormula);

				cell = row.createCell(11);
				cell.setCellStyle(currencyCellStyle);
				transferCreditFormula = "SUMIF(M3:M" + i + "," + "\"FALSE\"" + "," + "L3:L" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(transferCreditFormula);
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

				wb.setPrintArea(0, 0, 12, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, List<SalePointDTO>> separateSalePoint() {
		Map<String, List<SalePointDTO>> map = new LinkedHashMap<>();
		for (SalePointDTO dto : salePointDTOList) {
			if (map.containsKey(dto.getSalePointName())) {
				map.get(dto.getSalePointName()).add(dto);
			} else {
				List<SalePointDTO> list = new ArrayList<>();
				list.add(dto);
				map.put(dto.getSalePointName(), list);
			}
		}
		return map;
	}

	public List<SalePointDTO> getSalePointDTOList() {
		return salePointDTOList;
	}

	public TLFCriteria getTlfCriteria() {
		return tlfCriteria;
	}

	public void setTlfCriteria(TLFCriteria tlfCriteria) {
		this.tlfCriteria = tlfCriteria;
	}

	public EnumSet<PaymentChannel> getPaymentChannels() {
		return EnumSet.of(PaymentChannel.CASHED, PaymentChannel.CHEQUE, PaymentChannel.TRANSFER, PaymentChannel.SUNDRY);
	}

}
