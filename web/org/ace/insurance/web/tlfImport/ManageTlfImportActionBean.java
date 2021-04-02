package org.ace.insurance.web.tlfImport;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.accounting.service.interfaces.IInterfaceFileService;
import org.ace.insurance.common.Utils;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "ManageTlfImportActionBean")
@ViewScoped
public class ManageTlfImportActionBean extends BaseBean {

	@ManagedProperty(value = "#{InterfaceFileService}")
	public IInterfaceFileService interfaceFileService;

	public void setInterfaceFileService(IInterfaceFileService interfaceFileService) {
		this.interfaceFileService = interfaceFileService;
	}

	@ManagedProperty(value = "#{CurrencyService}")
	public ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	@ManagedProperty(value = "#{BranchService}")
	public IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private UploadedFile uploadedFile;

	private String uploadedFileName;

	private Date actionDate;

	private boolean fileUploaded;

	private boolean imported;

	private boolean errorExcelGenerated;

	private final String formID = "TlfImportForm";

	private List<String> convertableFiles;

	private String convertConfirmMessage;

	@PostConstruct
	public void init() {
		actionDate = Utils.resetStartDate(new Date());
		convertableFiles = interfaceFileService.findConvertableFileCount();
		pageReset();
	}

	// public String loginCheck() {
	// if (getParam(Constants.LOGIN_USER) == null) {
	// return "login";
	// }
	// return null;
	// }

	private void pageReset() {
		imported = false;
		errorExcelGenerated = false;
		fileUploaded = false;
		uploadedFileName = null;
		uploadedFile = null;
	}

	public void handleFileUpload(FileUploadEvent event) {
		imported = false;
		fileUploaded = true;
		uploadedFile = event.getFile();
		uploadedFileName = uploadedFile.getFileName();
		addInfoMessage(null, MessageId.IS_UPLOADED, event.getFile().getFileName());
	}

	public void removeUploadedFile() {
		fileUploaded = false;
		uploadedFile = null;
		uploadedFileName = null;
	}

	public void importFile() {
		if (uploadedFile == null) {
			addErrorMessage(formID + ":fileUpload", UIInput.REQUIRED_MESSAGE_ID);
			return;
		} else {
			insertDataFromExcel();
		}
	}

	private void insertDataFromExcel() {
		List<String> dataStringList = new ArrayList<String>();
		try {
			// this is the count of data rows in excel file

			InputStream inputStream = uploadedFile.getInputstream();
			Workbook wb = WorkbookFactory.create(inputStream);

			Sheet sheet = wb.getSheetAt(0);

			boolean readAgain = true;
			int rowIndex = 4;

			// TODO There is no way to check duplicate currently
			// UniqueKey uniqueKey = getUniqueKey(sheet);;
			// long duplicateCount =
			// interfaceFileService.findCountByCriteria(uniqueKey);
			// if (duplicateCount > 0) {
			// // this loop is to count data rows
			// while (readAgain) {
			// Row row = sheet.getRow(rowIndex);
			// if (row == null || row.getCell(0) == null) {
			// readAgain = false;
			// break;
			// } else {
			// rowIndex++;
			// }
			// }
			// if (rowIndex - 1 == duplicateCount) {
			// addWranningMessage("This file is already imported. The number of
			// files matches.");
			// } else {
			// addErrorMessage("This file is already imported. But the number of
			// files doesnt match. Contact Admin.");
			// }
			// } else {

			// TODO There is no way to check duplicate currently
			long duplicateCount = interfaceFileService.findCountByDate(actionDate, false);
			int rows = 0;
			if (duplicateCount > 0) {
				// this loop is to count data rows
				while (readAgain) {
					Row row = sheet.getRow(rowIndex);
					if (row == null || row.getCell(0) == null) {
						readAgain = false;
						break;
					} else {
						if (row.getCell(0).getCellType() != 0) {
							// System.out.println(row.getCell(0).getCellType());
							// rowIndex++;
							// rows++;
							readAgain = false;
							break;
						} else {
							rowIndex++;
							rows++;
						}
					}
				}
				if (rows == duplicateCount) {
					addWranningMessage("There is a file already imported with the date (" + Utils.getDateFormatString(actionDate)
							+ "). The number of rows in this file and imported file matches.");
					return;
				} else {
					addErrorMessage("There is a file already imported with the date (" + Utils.getDateFormatString(actionDate) + "). But the number of files doesnt match.");
					return;
				}
			} else {
				while (readAgain) {
					Row row = sheet.getRow(rowIndex);
					if (row == null || row.getCell(0) == null) {
						readAgain = false;
						break;
					} else {
						if (row.getCell(0).getCellType() != 0) {
							readAgain = false;
							break;
						}
						String dataString = "";
						for (int index = 1; index <= 46; index++) {
							if (index > 1) {
								dataString += " , ";
							}

							// no idea what's causing the cell to be null
							if (row.getCell(index) == null) {
								dataString += "NULL";
							} else {
								switch (row.getCell(index).getCellType()) {
									/*
									 * 0 = numeric 1 = string 2 = formula 3 =
									 * blank 4 = boolean 5 = ERROR Ref:
									 * https://poi.apache.org/apidocs/org/apache
									 * /poi /ss/usermodel/Cell.html
									 */
									// 0 is number
									case 0:
										if (String.valueOf(row.getCell(index).getNumericCellValue()).trim().isEmpty()) {
											dataString += "NULL";
										} else {
											if (HSSFDateUtil.isCellDateFormatted(row.getCell(index))) {
												dataString += "'" + Utils.getDatabaseDateString(row.getCell(index).getDateCellValue()) + "'";

												// 41 is ACCNUM col and 33 is
												// sacstype, which have both
												// number and string cell , and
												// must
												// be
												// loaded as string, soo....
											} else if (index == 42 || index == 34) {
												dataString += String.valueOf((int) row.getCell(index).getNumericCellValue());
											} else {
												dataString += new BigDecimal(row.getCell(index).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP).toPlainString();
											}

										}
										break;
									// 1 is string data
									case 1:
										if (row.getCell(index).getStringCellValue() == null || row.getCell(index).getStringCellValue().trim().isEmpty()) {
											dataString += "NULL";
										} else {
											dataString += "'" + row.getCell(index).getStringCellValue().trim() + "'";
										}
										break;
									// 3 is blank
									case 3:
										dataString += "NULL";
										break;
									default:
										break;
								}
							}
						}
						dataStringList.add(dataString);
					}
					rowIndex++;
					rows++;
				}
			}
			int importedCount = interfaceFileService.importInterfaceFiles(dataStringList, rows, actionDate, uploadedFileName);
			convertableFiles = interfaceFileService.findConvertableFileCount();
			String fileName = Utils.getFlatDateString(new Date()) + " " + uploadedFile.getFileName();
			String filePath = "/upload/CSC_INTF/" + fileName;
			File file = new File(getUploadPath() + filePath);
			file.setWritable(false);
			createFile(file, uploadedFile.getContents());
			imported = true;
			addInfoMessage(importedCount + " rows were imported.");
		} catch (SystemException e) {
			handelSysException(e);
			return;
		} catch (Exception e) {
			addErrorMessage(e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public void openConfirmDialog() {
		long fileCount = interfaceFileService.findCountByDate(actionDate, true);
		if (fileCount == 0) {
			addErrorMessage("There is no imported file for " + Utils.getDateFormatString(actionDate) + ".");
			return;
		} else {
			convertConfirmMessage = "Are you sure to convert " + fileCount + " files for " + Utils.getDateFormatString(actionDate) + " ? This may take " + getDuration(fileCount)
					+ ".";
			PrimeFaces.current().executeScript("PF('PF('confirmDialog')').show()");
		}
	}

	private String getDuration(long fileCount) {
		// TODO delete later
		String result = "";
		// let's assume it take 2 secs to convert a file

		long secs = fileCount * 2;
		// if less than one hour , more than one minute
		if (secs / 3600 == 0 && secs / 60 > 0) {
			result += "up to ";
			result += ((secs / 60) + 1) + (secs / 60 > 1 ? " minutes" : " minute");
			return result;
			// if less than one minute
		} else if (secs / 3600 == 0 && secs / 60 == 0) {
			result = "less than a minute";
			return result;
		}

		// if more than one hour
		if (secs / 3600 > 0) {
			result += "up to ";
			result += secs / 3600 + (secs / 3600 > 1 ? " hours" : " hour");
			secs = secs % 3600;
		}
		// left over minutes over hour
		if (secs / 60 > 0) {
			if (!result.isEmpty()) {
				result += " and ";
			}
			result += ((secs / 60) + 1) + (secs / 60 > 1 ? " minutes" : " minute");
		}
		return result;
	}

	public void convertFile() {
		try {
			long fileCount = interfaceFileService.findCountByDate(actionDate, true);
			long tlfCount = interfaceFileService.convertFiles(actionDate);
			convertableFiles = interfaceFileService.findConvertableFileCount();
			addInfoMessage(tlfCount + " TLFs converted from " + fileCount + " interface files.");
			// addInfoMessage(" TLFs converted from " + fileCount + " interface
			// files.");
		} catch (SystemException e) {
			handelSysException(e);
		} catch (Exception e) {
			e.printStackTrace();
			addErrorMessage(e.getMessage());
		}
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getUploadedFileName() {
		return uploadedFileName;
	}

	public boolean isFileUploaded() {
		return fileUploaded;
	}

	public boolean isErrorExcelGenerated() {
		return errorExcelGenerated;
	}

	public boolean isImported() {
		return imported;
	}

	public List<String> getConvertableFiles() {
		return convertableFiles;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Date getMaxDate() {
		return new Date();
	}

	public Date getMinDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		return calendar.getTime();
	}

	public String getConvertConfirmMessage() {
		return convertConfirmMessage;
	}
}
