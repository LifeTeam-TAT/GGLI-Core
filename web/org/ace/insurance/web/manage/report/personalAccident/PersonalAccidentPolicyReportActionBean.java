package org.ace.insurance.web.manage.report.personalAccident;

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
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.life.LifePolicyReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ILifePolicyReportService;
import org.ace.insurance.report.personalAccident.PersonalAccidentPolicyReport;
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
@ManagedBean(name = "PersonalAccidentPolicyReportActionBean")
public class PersonalAccidentPolicyReportActionBean extends BaseBean implements Serializable {

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

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private LifePolicyReportCriteria criteria;
	private List<PersonalAccidentPolicyReport> lifePolicyList;
	private LazyDataModelUtil<PersonalAccidentPolicyReport> lazyModel;
	private boolean accessBranches;
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		}
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
		filter();
	}

	public LazyDataModel<PersonalAccidentPolicyReport> getLazyModel() {
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
			lifePolicyList = lifePolicyReportService.findPersonalAccidentPolicyReport(criteria);
			lazyModel = new LazyDataModelUtil<PersonalAccidentPolicyReport>(lifePolicyList);
		}
	}

	public ILifePolicyReportService getLifePolicyReportService() {
		return lifePolicyReportService;
	}

	public LifePolicyReportCriteria getCriteria() {
		return criteria;
	}

	private final String reportName = "PAPolicyReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			// if (criteria.getBranch() == null) {
			// branch = "All";
			// } else {
			// branch = criteria.getBranch().getName();
			// }

			lifePolicyReportService.generatePersonalAccidentPolicyReport(lifePolicyList, dirPath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Exporting
	public void exportExcel() {
		if (null != lifePolicyList && lifePolicyList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "PersonalAccidentPolicyReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				ExportExcel exportExcel = new ExportExcel();
				exportExcel.generate(op);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export PersonalAccidentPolicyReport.xlsx", e);
			}
		} else {
			addErrorMessage("policyList:lifePolicyTable", MessageId.THERE_IS_NO_DATA);
		}
	}

	private class ExportExcel {
		private XSSFWorkbook wb;

		public ExportExcel() {
			load();
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/personalAccident/PersonalAccidentPolicyReport.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load PersonalAccidentPolicyReport.xlsx template", e);
			}
		}

		public Map<String, List<PersonalAccidentPolicyReport>> separateBranch() {
			Map<String, List<PersonalAccidentPolicyReport>> map = new LinkedHashMap<String, List<PersonalAccidentPolicyReport>>();
			for (PersonalAccidentPolicyReport report : lifePolicyList) {
				if (map.containsKey(report.getBranchName())) {
					map.get(report.getBranchName()).add(report);
				} else {
					List<PersonalAccidentPolicyReport> list = new ArrayList<PersonalAccidentPolicyReport>();
					list.add(report);
					map.put(report.getBranchName(), list);
				}
			}
			return map;
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("PersonalAccidentPolicyReport");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				// ExcelUtils.fillCompanyLogo(wb, sheet, 8);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				if (criteria.getBranch() == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Personal Accident Policy Report ( All )");
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Personal Accident Policy Report ( " + criteria.getBranch().getName() + " )");
				}

				int i = 3;
				int index = 0;
				int startIndex;
				String strFormula;
				String GrandSIFormula = "";
				String GrandPremiumFormula = "";
				Map<String, List<PersonalAccidentPolicyReport>> map = separateBranch();
				if (map != null && !map.isEmpty()) {
					for (List<PersonalAccidentPolicyReport> branchList : map.values()) {
						startIndex = i + 1;
						for (PersonalAccidentPolicyReport lifePolicyReport : branchList) {
							index = index + 1;
							row = sheet.createRow(i);

							// index
							cell = row.createCell(0);
							cell.setCellValue(index);
							cell.setCellStyle(defaultCellStyle);

							// policyNo
							cell = row.createCell(1);
							cell.setCellValue(lifePolicyReport.getPolicyNo());
							cell.setCellStyle(textCellStyle);

							// insuredPersonName
							cell = row.createCell(2);
							cell.setCellValue(lifePolicyReport.getInsuredPersonName());
							cell.setCellStyle(textCellStyle);

							// addressAndPhone
							cell = row.createCell(3);
							cell.setCellValue(lifePolicyReport.getAddress());
							cell.setCellStyle(textCellStyle);

							// ageandDateOfBirth
							cell = row.createCell(4);
							if (lifePolicyReport.getDateOfBirth() != null) {
								cell.setCellValue(lifePolicyReport.getDateOfBirth());
								cell.setCellStyle(textCellStyle);
							}

							// agentNameandagentCode
							cell = row.createCell(5);
							cell.setCellValue(lifePolicyReport.getAgentName());
							cell.setCellStyle(textCellStyle);

							// policyStartDate
							cell = row.createCell(6);
							if (lifePolicyReport.getPolicyStartDate() != null) {
								cell.setCellValue(lifePolicyReport.getPolicyStartDate());
								cell.setCellStyle(dateCellStyle);
							}

							// policyEndDate
							cell = row.createCell(7);
							if (lifePolicyReport.getPolicyEndDate() != null) {
								cell.setCellValue(lifePolicyReport.getPolicyEndDate());
								cell.setCellStyle(dateCellStyle);
							}

							// sumInsured
							cell = row.createCell(8);
							cell.setCellValue(lifePolicyReport.getSumInsured());
							cell.setCellStyle(currencyCellStyle);

							// premium
							cell = row.createCell(9);
							cell.setCellValue(lifePolicyReport.getPremium());
							cell.setCellStyle(currencyCellStyle);

							// branch
							cell = row.createCell(10);
							cell.setCellValue(lifePolicyReport.getReceiptNo());
							cell.setCellStyle(textCellStyle);

							// remark
							cell = row.createCell(11);
							if (lifePolicyReport.getRemark() != null) {
								cell.setCellValue(lifePolicyReport.getRemark());
								cell.setCellStyle(textCellStyle);
							}
							i = i + 1;
						}
						sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
						row = sheet.createRow(i);

						cell = row.createCell(0);
						cell.setCellValue("Sub Total");
						cell.setCellStyle(defaultCellStyle);
						ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 7), sheet, wb);

						cell = row.createCell(8);
						cell.setCellStyle(currencyCellStyle);
						strFormula = "SUM(I" + startIndex + ":I" + i + ")";
						GrandSIFormula += "I" + (i + 1) + "+";
						cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
						cell.setCellFormula(strFormula);

						cell = row.createCell(9);
						cell.setCellStyle(currencyCellStyle);
						strFormula = "SUM(J" + startIndex + ":J" + i + ")";
						GrandPremiumFormula += "J" + (i + 1) + "+";
						cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
						cell.setCellFormula(strFormula);
						ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 10, 11), sheet, wb);
						i = i + 1;
					}
					sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
					row = sheet.createRow(i);

					cell = row.createCell(0);
					cell.setCellValue("Grand Total");
					cell.setCellStyle(defaultCellStyle);
					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 7), sheet, wb);

					cell = row.createCell(8);
					cell.setCellStyle(currencyCellStyle);
					GrandSIFormula = GrandSIFormula.substring(0, GrandSIFormula.length() - 1);
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(GrandSIFormula);

					cell = row.createCell(9);
					cell.setCellStyle(currencyCellStyle);
					GrandPremiumFormula = GrandPremiumFormula.substring(0, GrandPremiumFormula.length() - 1);
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(GrandPremiumFormula);

					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 10, 11), sheet, wb);

					wb.setPrintArea(0, "$A$1:$L$" + (i + 1));
					wb.write(op);
					op.flush();
				}
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
	}

	// Entity

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

	///
	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
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

	public List<Product> getProductList() {
		return productService.findProductByInsuranceType(InsuranceType.PERSONAL_ACCIDENT);
	}

	public List<Branch> getBranchList() {
		return branchService.findAllBranch();
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void setEntityNull() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
		criteria.setEntity(null);
	}

	public void setBranchNull() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

}
