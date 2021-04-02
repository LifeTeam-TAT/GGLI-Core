package org.ace.insurance.web.manage.life.billcollection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.GenericDataModel;
import org.ace.insurance.common.MonthNames;
import org.ace.insurance.common.NotificationCriteria;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.Utils;
import org.ace.insurance.web.common.WebUtils;
import org.ace.java.component.SystemException;
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

/**
 * Life Payment Notification ActionBean
 * 
 * @author
 * @since 1.0.0
 * @date 2013/09/19
 */
@ViewScoped
@ManagedBean(name = "LifePaymentNotificationActionBean")
public class LifePaymentNotificationActionBean<T extends IDataModel> extends BaseBean {
	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	private List<PolicyNotificationDTO> policyList;
	private NotificationCriteria criteria;
	private boolean showDayBetween;
	private boolean showMonthly;
	private PolicyNotificationDTO[] selectedNotis;
	private GenericDataModel<PolicyNotificationDTO> dataModel;

	@PostConstruct
	public void init() {
		showMonthly = true;
		showDayBetween = false;
		reset();
	}

	public void search() {
		if (ReferenceType.LIFE_POLICY.equals(criteria.getReferenceType()) || ReferenceType.SHORT_ENDOWMENT_LIFE_POLICY.equals(criteria.getReferenceType())
				|| ReferenceType.STUDENT_LIFE_POLICY.equals(criteria.getReferenceType())
				|| ReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY.equals(criteria.getReferenceType())
				|| ReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY.equals(criteria.getReferenceType())
				|| ReferenceType.SINGLE_PREMIUM_ENDOWMNET_LIFE_POLICY.equals(criteria.getReferenceType())) {
			policyList = lifePolicyService.findNotificationLifePolicy(criteria);
		} else {
			policyList = medicalPolicyService.findPolicyNotification(criteria);

		}
		dataModel = new GenericDataModel(policyList);
	}

	public void reset() {
		criteria = new NotificationCriteria();
		showDayBetween = false;
		showMonthly = true;
		criteria.setReferenceType(ReferenceType.LIFE_POLICY);
		criteria.setYear(Calendar.getInstance().get(Calendar.YEAR));
		criteria.setMonth(Calendar.getInstance().get(Calendar.MONTH));
		criteria.setReportType("Monthly");
		policyList = lifePolicyService.findNotificationLifePolicy(criteria);
		dataModel = new GenericDataModel(policyList);
	}

	public List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 100; i++) {
			years.add(year - i);
		}
		return years;
	}

	public List<String> getReportTypeList() {
		List<String> result = new ArrayList<String>();
		result.add("Days Between");
		result.add("Monthly");
		return result;
	}

	public void criteriaChangeEvent(AjaxBehaviorEvent event) {
		showDayBetween = false;
		showMonthly = false;
		if (criteria.getReportType().equalsIgnoreCase("Days Between")) {
			showDayBetween = true;
		}
		if (criteria.getReportType().equalsIgnoreCase("Monthly")) {
			showMonthly = true;
		}
	}

	public boolean isShowDayBetween() {
		return showDayBetween;
	}

	public void setShowDayBetween(boolean showDayBetween) {
		this.showDayBetween = showDayBetween;
	}

	public boolean isShowMonthly() {
		return showMonthly;
	}

	public void setShowMonthly(boolean showMonthly) {
		this.showMonthly = showMonthly;
	}

	private final String reportName = "LifePolicyNotificationLetter";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		if (selectedNotis.length == 0) {
			addErrorMessage("paymentNotificationForm:lifePolicyInfoTable", MessageId.ATLEAST_ONE_CHECK_REQUIRED);
			return;
		}
		if (selectedNotis.length > 0) {
			if (ReferenceType.SHORT_ENDOWMENT_LIFE_POLICY.equals(criteria.getReferenceType()) || ReferenceType.LIFE_POLICY.equals(criteria.getReferenceType())) {
				List<LifePolicy> policies = new ArrayList<LifePolicy>();
				for (PolicyNotificationDTO dto : selectedNotis) {
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(dto.getPolicyNo());
					policies.add(lifePolicy);
				}
				DocumentBuilder.generateLifePolicyNotification(policies, dirPath, fileName);
			} else if (ReferenceType.MEDICAL_POLICY.equals(criteria.getReferenceType()) || ReferenceType.CRITICAL_ILLNESS_POLICY.equals(criteria.getReferenceType())
					|| ReferenceType.HEALTH_POLICY.equals(criteria.getReferenceType())) {
				List<MedicalPolicy> policies = new ArrayList<MedicalPolicy>();
				for (PolicyNotificationDTO dto : selectedNotis) {
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByPolicyNo(dto.getPolicyNo());
					policies.add(medicalPolicy);
				}
				DocumentBuilder.generateMedicalPolicyNotification(policies, dirPath, fileName, criteria.getReferenceType());
			}
			PrimeFaces.current().executeScript("PF('notificationLetterDialog').show()");
		}
	}

	public PolicyNotificationDTO[] getSelectedNotis() {
		return selectedNotis;
	}

	public void setSelectedNotis(PolicyNotificationDTO[] selectedNotis) {
		this.selectedNotis = selectedNotis;
	}

	public GenericDataModel getDataModel() {
		return dataModel;
	}

	public void setwsDataModel(GenericDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public ReferenceType[] getReferenceTypeSelectedItemList() {
		return new ReferenceType[] { ReferenceType.LIFE_POLICY, ReferenceType.MEDICAL_POLICY, ReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, ReferenceType.CRITICAL_ILLNESS_POLICY,
				ReferenceType.HEALTH_POLICY };
	}

	public void selectSalePoint() {
		PrimeFaces.current().dialog().openDynamic("salePointDialog", WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
	}

	public NotificationCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(NotificationCriteria criteria) {
		this.criteria = criteria;
	}

	public void exportExcel() {
		if (policyList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "PolicyPaymentNotification.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel();
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export LifeProposalReport.xlsx", e);
			}
		} else {
			addErrorMessage("paymentNotificationForm:lifePolicyInfoTable", MessageId.THERE_IS_NO_DATA);
		}
	}

	private class ExportExcel {
		private XSSFWorkbook wb;

		public ExportExcel() {
			load();
		}

		private void load() {
			try (InputStream inp = this.getClass().getResourceAsStream("/report-template/life/lifePolicyPaymentNotification.xlsx");) {
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load lifePolicyPaymentNotification.xlsx template", e);
			}
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("LifePolicyPaymentNotification");
				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle headerCellStyle = ExcelUtils.getHeaderCellStyle(wb);

				Row row;
				Cell cell;
				row = sheet.createRow(1);
				row.setHeight((short) 600);
				sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 15));

				cell = row.createCell(0);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(1, 1, 0, 13), sheet, wb);
				String start = "";
				String end = "";
				if (showDayBetween) {
					start = (String) (criteria.getStartDate() == null ? "" : "From " + Utils.formattedDate(criteria.getStartDate()));
					end = (String) (criteria.getEndDate() == null ? "" : " To " + Utils.formattedDate(criteria.getEndDate()));
					cell.setCellValue("Policy Payment Notification Report  " + start + end);
				} else {
					for (MonthNames e : MonthNames.values()) {
						if (criteria.getMonth() == e.getValue())
							start = e.name();
					}
					cell.setCellValue("Policy Payment Notification Report (" + start + ") - " + criteria.getYear());
				}
				cell.setCellStyle(headerCellStyle);

				int i = 2;
				int count = 0;
				String strFormula = null;
				for (PolicyNotificationDTO dto : policyList) {
					count = count + 1;
					i = i + 1;

					row = sheet.createRow(i);
					cell = row.createCell(0);
					cell.setCellValue(count);
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(1);
					cell.setCellValue(dto.getPolicyNo());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(2);
					cell.setCellValue(dto.getInsuredPersonName());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(3);
					cell.setCellValue(dto.getIdNo());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(4);
					cell.setCellValue(dto.getPaymentType());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(5);
					cell.setCellValue(dto.getPaymentTerm());
					cell.setCellStyle(defaultCellStyle);

					cell = row.createCell(6);
					cell.setCellValue(dto.getTermPremium());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(7);
					cell.setCellValue(dto.getLoanInterest());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(8);
					cell.setCellValue(dto.getRenewalInterest());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(9);
					cell.setCellValue(dto.getRefund());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(10);
					cell.setCellValue(dto.getTotalAmout());
					cell.setCellStyle(currencyCellStyle);

					cell = row.createCell(11);
					cell.setCellValue(dto.getActivedPolicyStartDate());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(12);
					cell.setCellValue(dto.getActivedPolicyEndDate());
					cell.setCellStyle(dateCellStyle);

					cell = row.createCell(13);
					cell.setCellValue(dto.getSalePointName());
					cell.setCellStyle(defaultCellStyle);

				}
				i = i + 1;
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 5));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(6);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(G4:G" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(7);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(H4:H" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(8);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(I4:I" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(9);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(J4:J" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(10);
				cell.setCellStyle(currencyCellStyle);
				strFormula = "SUM(K4:K" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(strFormula);

				cell = row.createCell(11);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(12);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(13);
				cell.setCellStyle(defaultCellStyle);

				wb.setPrintArea(0, 0, 13, 0, i);
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

}
