package org.ace.insurance.web.manage.report.studentLife;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.MonthNames;
import org.ace.insurance.report.common.StudentLifeCriteria;
import org.ace.insurance.report.studentLife.BCStudentLifeView;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.Utils;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BcStudentLifeMonthlyReportExcel {
	private XSSFWorkbook wb;
	StudentLifeCriteria studentLifeCriteria;
	// private FormulaEvaluator evaluator ;

	public BcStudentLifeMonthlyReportExcel() {
	}

	private void load() {
		String excelTitle = studentLifeCriteria.isDaily() ? "StudentLife_Renewal_Daily_Report" : "StudentLife_Renewal_Monthly_Report";
		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/studentLife/" + excelTitle + ".xlsx");
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load " + excelTitle + ".xlsx template", e);
		}
	}

	public void generate(OutputStream op, List<BCStudentLifeView> newStudentLifeViewList) {
		try {
			Sheet sheet = wb.getSheet("StudentLife(BC)Report");
			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
			XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
			XSSFCellStyle headerCellStyle = ExcelUtils.getHeaderCellStyle(wb);

			Row row;
			Cell cell;
			StringBuilder builder = new StringBuilder();
			ExcelUtils.fillCompanyLogoAndAddress(wb, sheet, 3, 15, 18);
			row = sheet.getRow(0);
			cell = row.getCell(0);
			builder.append(ApplicationSetting.getCompanyLabel());

			String start = "";
			String end = "";
			if (studentLifeCriteria.isDaily()) {
				start = (String) (studentLifeCriteria.getStartDate() == null ? "" : "From " + Utils.formattedDate(studentLifeCriteria.getStartDate()));
				end = (String) (studentLifeCriteria.getEndDate() == null ? "" : " To " + Utils.formattedDate(studentLifeCriteria.getEndDate()));
				builder.append(" \n \n Renewal Daily Report (Student Life) " + start + end);
			} else {
				for (MonthNames e : MonthNames.values()) {
					if (studentLifeCriteria.getStartMonthType() == e.getValue())
						start = e.name();
				}
				for (MonthNames e : MonthNames.values()) {
					if (studentLifeCriteria.getEndMonthType() == e.getValue())
						end = " - " + e.name();
				}
				builder.append(" \n \n Renewal Monthly Report (Student Life) (" + start + end + ") - " + studentLifeCriteria.getYear());
			}
			cell.setCellValue(builder.toString());
			cell.setCellStyle(headerCellStyle);
			int i = 1;
			int count = 0;
			String strFormula = null;
			for (BCStudentLifeView report : newStudentLifeViewList) {
				count = count + 1;
				i = i + 1;
				row = sheet.createRow(i);
				cell = row.createCell(0);
				cell.setCellValue(count);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(report.getPolicyHolderName());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(report.getInsuredPersonName());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(report.getPolicyHolderDOB());
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(report.getInsuredPersonDOB());
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(report.getPolicyNo());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(report.getAddress());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(report.getSuminsured());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(report.getPolicyTerm());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(report.getPremiumTerm());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(10);
				cell.setCellValue(report.getPaymentTypeName());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(11);
				cell.setCellValue(report.getPremium());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(12);
				cell.setCellValue(report.getAgentCommission());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(13);
				cell.setCellValue(report.getReceive_Date());
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(14);
				cell.setCellValue(report.getAgentName());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(15);
				cell.setCellValue(report.getRegrestionNo());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(16);
				cell.setCellValue(report.getBranchName());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(17);
				cell.setCellValue(report.getSalePointName());
				cell.setCellStyle(defaultCellStyle);

			}
			i = i + 1;
			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
			row = sheet.createRow(i);

			cell = row.createCell(0);
			ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);
			cell.setCellValue("Grand Total");
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(7);
			cell.setCellStyle(currencyCellStyle);
			strFormula = "SUM(H4:H" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(strFormula);

			cell = row.createCell(8);
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(9);
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(10);
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(11);
			cell.setCellStyle(currencyCellStyle);
			strFormula = "SUM(L4:L" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(strFormula);

			cell = row.createCell(12);
			cell.setCellStyle(currencyCellStyle);
			strFormula = "SUM(M4:M" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(strFormula);

			cell = row.createCell(13);
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(14);
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(15);
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(16);
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(17);
			cell.setCellStyle(defaultCellStyle);

			wb.setPrintArea(0, 0, 17, 0, i);
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

	public void setStudentLifeCriteria(StudentLifeCriteria studentLifeCriteria) {
		this.studentLifeCriteria = studentLifeCriteria;
		load();
	}

}
