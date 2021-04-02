package org.ace.insurance.web.manage.report.medical;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.report.medical.HealthDailyReportDTO;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HealthDailyReportExcel {
	private XSSFWorkbook wb;

	public HealthDailyReportExcel() {
		load();
	}

	private void load() {
		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/medical/HealthDailyReport.xlsx");
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load HealthDailyReportExcel.xlsx tempalte", e);
		}
	}

	public void generate(OutputStream op, List<HealthDailyReportDTO> healthDailyReportList) {
		try {
			Sheet sheet = wb.getSheet("HealthDailyReport");

			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
			XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
			XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

			Row row = null;
			Cell cell;
			ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 3, 14, 17);
			row = sheet.getRow(0);
			cell = row.getCell(0);

			for (HealthDailyReportDTO report : healthDailyReportList) {
				if (report.getBranch() == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Health Insurance ( All )");
					break;
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n \n Health Insurance ( " + report.getBranch() + " )");
					break;
				}
			}

			int i = 2;
			int index = 0;
			for (HealthDailyReportDTO report : healthDailyReportList) {
				i = i + 1;
				index = index + 1;
				row = sheet.createRow(i);
				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				if (null != report.getPaymentDate()) {
					cell.setCellValue(report.getPaymentDate());
				}
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(report.getProposalNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(report.getPolicyNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(report.getInsuredName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(report.getBeneficiaryName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(report.getBasicUnit());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(report.getAdditionalUnit());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(report.getOption1Unit());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(report.getOption2Unit());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(10);
				cell.setCellValue(report.getPremium());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(11);
				cell.setCellValue(report.getPremiumMode());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(12);
				cell.setCellValue(report.getStartDate());
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(13);
				cell.setCellValue(report.getEndDate());
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(14);
				cell.setCellValue(report.getAgentName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(15);
				cell.setCellValue(report.getSalePointName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(16);
				cell.setCellValue(report.getBranchName());
				cell.setCellStyle(textCellStyle);
			}

			String strFormula;
			Font font = wb.createFont();
			font.setFontName("Myanmar3");

			i = i + 1;
			row = sheet.createRow(i);

			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 5));

			cell = row.createCell(0);
			cell.setCellValue("Total");
			ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 5), sheet, wb);
			cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
			cell.getCellStyle().setFont(font);

			cell = row.createCell(6);
			cell.setCellStyle(defaultCellStyle);
			strFormula = "SUM(G4:G" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(strFormula);

			cell = row.createCell(7);
			cell.setCellStyle(defaultCellStyle);
			strFormula = "SUM(H4:H" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(strFormula);

			cell = row.createCell(8);
			cell.setCellStyle(defaultCellStyle);
			strFormula = "SUM(I4:I" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(strFormula);

			cell = row.createCell(9);
			cell.setCellStyle(defaultCellStyle);
			strFormula = "SUM(J4:J" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(strFormula);

			cell = row.createCell(10);
			cell.setCellStyle(currencyCellStyle);
			strFormula = "SUM(K4:K" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(strFormula);

			wb.setPrintArea(0, 0, 16, 0, i);
			wb.write(op);
			op.flush();
			op.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
