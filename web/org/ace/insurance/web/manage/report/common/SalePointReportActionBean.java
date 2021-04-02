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
import org.ace.insurance.payment.service.interfaces.ITLFService;
import org.ace.insurance.report.life.LifeProposalReport;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.WebUtils;
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
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name="SalePointReportActionBean")
public class SalePointReportActionBean extends BaseBean {
	
	@ManagedProperty(value="#{TLFService}")
	private ITLFService tlfService;
	
	
	public void setTlfService(ITLFService tlfService) {
		this.tlfService = tlfService;
	}
	
	
	private List<TLFDTO> tlfDTOList;
	private TLFCriteria tlfCriteria;
	//TODO FIXME PSH create salepoint report file
	private final String reportName = "LifePolicyReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";



	@PostConstruct
	public void init(){
		initialization();
	}
	
	public void initialization(){
		tlfCriteria = new TLFCriteria();
		resetCriteriaDate();
		tlfDTOList = new ArrayList<>();
	}
	
	public void resetCriteriaDate(){
		tlfCriteria.setEndDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-7);
		tlfCriteria.setStartDate(cal.getTime());
	}
	
	public void SearchDTOList(){
		boolean valid = true;
		String formID = "accountList";
		if (tlfCriteria.getStartDate() != null && tlfCriteria.getEndDate() != null) {
			if (tlfCriteria.getStartDate().after(tlfCriteria.getEndDate())) {
				addErrorMessage(formID + ":startDate", MessageId.STARTDATE_MUSTBE_LESSTHAN_ENDDATE);
				valid = false;
			}
		}
		if (valid) {
			tlfDTOList = tlfService.findTLFDTOByCriteria(tlfCriteria);
		}
	}
	
	public void resetCriteria(){
		tlfCriteria = new TLFCriteria();
		tlfDTOList = new ArrayList<>();
		resetCriteriaDate();
	}

	public void selectSalePoint() {
		PrimeFaces.current().dialog().openDynamic("salePointDialog", WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		tlfCriteria.setSalePoint(salePoint);
	}

	public String getStream() {
		return pdfDirPath + fileName;
	}
	
	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			
			//TODO FIXME PSH Create salePoint report file
			//lifePolicyReportService.generateLifePolicyReport(tlfCriteria, dirPath, fileName, branch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportExcel() {
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

				ExcelUtils.fillCompanyLogo(wb, sheet, 10);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				if (tlfCriteria.getSalePoint() == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Life Proposal Report ( All )");
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Life Proposal Report ( " + tlfCriteria.getSalePoint().getName() + " )");
				}

				int i = 1;
				int index = 0;
				int startIndex;
				String strFormula;
				String GrandSIFormula = "";
				String GrandPremiumFormula = "";
				Map<String, List<TLFDTO>> map = separateSalePoint();
				for (List<TLFDTO> salePointList : map.values()) {
					startIndex = i + 1 + 1;
					for (TLFDTO tlfDTO : salePointList) {
						i = i + 1;
						index = index + 1;
						row = sheet.createRow(i);
						// index
						cell = row.createCell(0);
						cell.setCellValue(index);
						cell.setCellStyle(defaultCellStyle);

						// TLF No
						cell = row.createCell(1);
						cell.setCellValue(tlfDTO.getTlfNo());
						cell.setCellStyle(textCellStyle);

						// TLF Acode
						cell = row.createCell(2);
						cell.setCellValue(tlfDTO.getAcode());
						cell.setCellStyle(textCellStyle);

						// Transaction Type
						cell = row.createCell(3);
						cell.setCellValue(tlfDTO.getTransaction());
						cell.setCellStyle(textCellStyle);

						// Narration
						cell = row.createCell(4);
						cell.setCellValue(tlfDTO.getNarration());
						cell.setCellStyle(textCellStyle);

						// Sale Point Name
						cell = row.createCell(5);
						cell.setCellValue(tlfDTO.getSalePoint().getName());
						cell.setCellStyle(textCellStyle);

						// Sale Point Code
						cell = row.createCell(6);
						cell.setCellValue(tlfDTO.getSalePoint().getSalePointCode());
						cell.setCellStyle(textCellStyle);

						// Payment Channel
						cell = row.createCell(7);
						cell.setCellValue(tlfDTO.getPaymentChannel().getLabel());
						cell.setCellStyle(textCellStyle);

						// Home Amount
						cell = row.createCell(8);
						cell.setCellValue(tlfDTO.getHomeAmount());
						cell.setCellStyle(currencyCellStyle);

						// Local Amount
						cell = row.createCell(9);
						cell.setCellValue(tlfDTO.getLocalAmount());
						cell.setCellStyle(currencyCellStyle);

						// Start Date
						cell = row.createCell(10);
						cell.setCellValue(tlfDTO.getStartDate());
						cell.setCellStyle(dateCellStyle);

						/*// workFlowTask
						cell = row.createCell(11);
						cell.setCellValue(lifeProposalReport.getWorkflow());
						cell.setCellStyle(textCellStyle);

						// responsiblePerson
						cell = row.createCell(12);
						cell.setCellValue(lifeProposalReport.getResponsiblePerson());
						cell.setCellStyle(textCellStyle);

						// remarks
						cell = row.createCell(13);
						cell.setCellValue(lifeProposalReport.getRemark());
						cell.setCellStyle(textCellStyle);
*/					}
					i = i + 1;
					sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
					row = sheet.createRow(i);

					cell = row.createCell(0);
					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);
					cell.setCellValue("Sub Total");
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(8);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(H" + startIndex + ":H" + i + ")";
					GrandSIFormula += "H" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					cell = row.createCell(7);
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(9);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(J" + startIndex + ":J" + i + ")";
					GrandPremiumFormula += "J" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					sheet.addMergedRegion(new CellRangeAddress(i, i, 10, 13));
					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 10, 13), sheet, wb);
				}
				i = i + 1;
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(8);
				cell.setCellStyle(currencyCellStyle);
				GrandSIFormula = GrandSIFormula.substring(0, GrandSIFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandSIFormula);

				cell = row.createCell(7);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(9);
				cell.setCellStyle(currencyCellStyle);
				GrandPremiumFormula = GrandPremiumFormula.substring(0, GrandPremiumFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandPremiumFormula);

				sheet.addMergedRegion(new CellRangeAddress(i, i, 10, 13));
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 10, 13), sheet, wb);

				wb.setPrintArea(0, 0, 13, 0, i);
				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Map<String, List<TLFDTO>> separateSalePoint() {
		Map<String, List<TLFDTO>> map = new LinkedHashMap<>();
		for (TLFDTO dto : tlfDTOList) {
			if (map.containsKey(dto.getSalePoint().getId())) {
				map.get(dto.getSalePoint().getId()).add(dto);
			} else {
				List<TLFDTO> list = new ArrayList<TLFDTO>();
				list.add(dto);
				map.put(dto.getSalePoint().getId(), list);
			}
		}
		return map;
	}

	public List<TLFDTO> getTlfDTOList() {
		return tlfDTOList;
	}

	public TLFCriteria getTlfCriteria() {
		return tlfCriteria;
	}

	public void setTlfCriteria(TLFCriteria tlfCriteria) {
		this.tlfCriteria = tlfCriteria;
	}

}
