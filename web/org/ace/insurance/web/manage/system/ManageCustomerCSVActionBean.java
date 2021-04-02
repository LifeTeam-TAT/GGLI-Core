package org.ace.insurance.web.manage.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.OfficeAddress;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.country.service.interfaces.ICountryService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.industry.service.interfaces.IIndustryService;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.qualification.service.interfaces.IQualificationService;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.religion.service.interfaces.IReligionService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.java.web.common.BaseBean;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "ManageCustomerCSVActionBean")
public class ManageCustomerCSVActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CustomerService}")
	private ICustomerService customerService;

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@ManagedProperty(value = "#{CountryService}")
	private ICountryService countryService;

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
	}

	@ManagedProperty(value = "#{QualificationService}")
	private IQualificationService qualificationService;

	public void setQualificationService(IQualificationService qualificationService) {
		this.qualificationService = qualificationService;
	}

	@ManagedProperty(value = "#{ReligionService}")
	private IReligionService religionService;

	public void setReligionService(IReligionService religionService) {
		this.religionService = religionService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{OccupationService}")
	private IOccupationService occupationService;

	public void setOccupationService(IOccupationService occupationService) {
		this.occupationService = occupationService;
	}

	@ManagedProperty(value = "#{BankBranchService}")
	private IBankBranchService bankBranchService;

	public void setBankBranchService(IBankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@ManagedProperty(value = "#{IndustryService}")
	private IIndustryService industryService;

	public void setIndustryService(IIndustryService industryService) {
		this.industryService = industryService;
	}

	private UploadedFile uploadedFile;
	private List<Customer> customerList;

	@PostConstruct
	public void init() {
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void upload(ActionEvent event) {
		try {
			customerList = new ArrayList<Customer>();
			InputStream inputStream = uploadedFile.getInputstream();
			Workbook wb = WorkbookFactory.create(inputStream);
			Sheet sheet = wb.getSheetAt(0);
			boolean readAgain = true;
			int i = 1;
			while (readAgain) {
				Row row = sheet.getRow(i);

				String initialId = row == null ? null : row.getCell(0).getStringCellValue();
				if (initialId == null || initialId.isEmpty()) {
					readAgain = false;
					break;
				} else {
					Customer customer = new Customer();
					Name name = new Name();
					ContentInfo cci = new ContentInfo();
					ResidentAddress ra = new ResidentAddress();
					PermanentAddress pa = new PermanentAddress();
					OfficeAddress oa = new OfficeAddress();
					customerList.add(customer);

					customer.setInitialId(initialId);
					String firstName = row.getCell(1).getStringCellValue();
					name.setFirstName(firstName);
					String middleName = row.getCell(2).getStringCellValue();
					name.setMiddleName(middleName);
					String lastName = row.getCell(3).getStringCellValue();
					name.setLastName(lastName);

					String fatherName = row.getCell(4).getStringCellValue();
					customer.setFatherName(fatherName);

					String idNo = row.getCell(5).getStringCellValue();
					customer.setIdNo(idNo);

					IdType idType = IdType.valueOf(row.getCell(6).getStringCellValue());
					customer.setIdType(idType);

					String labourNo = row.getCell(7).getStringCellValue();
					customer.setLabourNo(labourNo);

					Date dateOfBirth = row.getCell(8).getDateCellValue();
					customer.setDateOfBirth(dateOfBirth);
					Gender gender = Gender.valueOf(row.getCell(9).getStringCellValue());
					customer.setGender(gender);

					String branchId = row.getCell(10).getStringCellValue();
					Branch branch = branchService.findBranchById(branchId);
					customer.setBranch(branch);

					String nationalityId = row.getCell(11).getStringCellValue();
					Country country = countryService.findCountryById(nationalityId);
					customer.setCountry(country);

					MaritalStatus maritalStatus = MaritalStatus.valueOf(row.getCell(12).getStringCellValue());
					customer.setMaritalStatus(maritalStatus);

					String birthMark = row.getCell(13).getStringCellValue();
					customer.setBirthMark(birthMark);

					String qualificationId = row.getCell(14).getStringCellValue();
					if (qualificationId == null || qualificationId.isEmpty()) {
						Qualification qualification = qualificationService.findQualificationById(qualificationId);
						customer.setQualification(qualification);
					}

					String religionId = row.getCell(15).getStringCellValue();
					if (religionId == null || religionId.isEmpty()) {
						Religion religion = religionService.findReligionById(religionId);
						customer.setReligion(religion);
					}

					String resAddress = row.getCell(16).getStringCellValue();
					ra.setResidentAddress(resAddress);

					String resTownshipId = row.getCell(17).getStringCellValue();
					Township raTownship = townshipService.findTownshipById(resTownshipId);
					ra.setTownship(raTownship);

					Cell phoneCell = row.getCell(18);
					phoneCell.setCellType(Cell.CELL_TYPE_STRING);
					String phone = phoneCell.getStringCellValue();
					cci.setPhone(phone);

					String email = row.getCell(19).getStringCellValue();
					cci.setEmail(email);

					Cell faxCell = row.getCell(20);
					faxCell.setCellType(Cell.CELL_TYPE_STRING);
					String fax = faxCell.getStringCellValue();
					cci.setFax(fax);
					Cell mobileCell = row.getCell(21);
					mobileCell.setCellType(Cell.CELL_TYPE_STRING);
					String mobile = mobileCell.getStringCellValue();
					cci.setMobile(mobile);
					String pmAddress = row.getCell(22).getStringCellValue();
					pa.setPermanentAddress(pmAddress);

					String pmTownshipId = row.getCell(23).getStringCellValue();
					if (pmTownshipId == null || pmTownshipId.isEmpty()) {
						Township pmTownship = townshipService.findTownshipById(pmTownshipId);
						pa.setTownship(pmTownship);
					}
					String occupationId = row.getCell(24).getStringCellValue();
					if (occupationId == null || occupationId.isEmpty()) {
						Occupation occupation = occupationService.findOccupationById(occupationId);
						customer.setOccupation(occupation);
					}

					String indurstryId = row.getCell(25).getStringCellValue();
					if (occupationId == null || occupationId.isEmpty()) {
						Industry industry = industryService.findIndustryById(indurstryId);
						customer.setIndustry(industry);
					}
					String oaAddress = row.getCell(26).getStringCellValue();
					oa.setOfficeAddress(oaAddress);

					String oaTownshipId = row.getCell(27).getStringCellValue();
					if (oaTownshipId == null || oaTownshipId.isEmpty()) {
						Township oaTownship = townshipService.findTownshipById(oaTownshipId);
						oa.setTownship(oaTownship);
					}

					double salary = row.getCell(28).getNumericCellValue();
					customer.setSalary(salary + "");

					String bankAccountNo = row.getCell(29).getStringCellValue();
					customer.setBankAccountNo(bankAccountNo);

					String bankBranchId = row.getCell(30).getStringCellValue();
					if (bankBranchId == null || bankBranchId.isEmpty()) {
						BankBranch bankBranch = bankBranchService.findBankBranchById(bankBranchId);
						customer.setBankBranch(bankBranch);
					}

					customer.setContentInfo(cci);
					customer.setResidentAddress(ra);
					customer.setPermanentAddress(pa);
					customer.setName(name);
					customer.setOfficeAddress(oa);
					i++;
				}
			}
			customerService.addNewCustomerByList(customerList);
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Failed to upload the file!"));
		} catch (InvalidFormatException e) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Invalid data is occured in Uploaded File!"));
		}
	}

	public List<Customer> getCustomerList() {
		return customerList;
	}
}
