package org.ace.insurance.web.manage.life.proposal;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifePolicyService;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.organization.service.interfaces.IOrganizationService;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.system.common.typesOfSport.TypesOfSport;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.PeriodType;
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "RenewalGroupLifeProposalActionBean")
public class RenewalGroupLifeProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{RenewalGroupLifeProposalService}")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	public void setRenewalGroupLifeProposalService(IRenewalGroupLifeProposalService renewalGroupLifeProposalService) {
		this.renewalGroupLifeProposalService = renewalGroupLifeProposalService;
	}

	@ManagedProperty(value = "#{RenewalGroupLifePolicyService}")
	private IRenewalGroupLifePolicyService renewalGroupLifePolicyService;

	public void setRenewalGroupLifePolicyService(IRenewalGroupLifePolicyService renewalGroupLifePolicyService) {
		this.renewalGroupLifePolicyService = renewalGroupLifePolicyService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{OrganizationService}")
	private IOrganizationService organizationService;

	public void setOrganizationService(IOrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@ManagedProperty(value = "#{CustomerService}")
	private ICustomerService customerService;

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@ManagedProperty(value = "#{OccupationService}")
	private IOccupationService occupationService;

	public void setOccupationService(IOccupationService occupationService) {
		this.occupationService = occupationService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private LifePolicy lifepolicy;
	private List<Product> productsList;
	private List<RelationShip> relationshipList;
	private boolean createNew;
	private String remark;
	private User responsiblePerson;
	private EnquiryCriteria criteria;
	private String userType;
	private Date startDate;
	private Date endDate;
	private Entitys entitys;

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
		lifepolicy = (LifePolicy) getParam("lifePolicy");
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifePolicy");
	}

	@PostConstruct
	public void init() {
		productsList = productService.findProductByInsuranceType(InsuranceType.LIFE);
		relationshipList = relationShipService.findAllRelationShip();
		this.product = productsList.get(3);
		initializeInjection();
		boolean isFromPortal = (lifeProposal == null) ? false : true;

		criteria = new EnquiryCriteria();
		lifeProposal = new LifeProposal(lifepolicy);
		entitys = lifeProposal.getBranch().getEntity();
		lifeProposal.setProposalType(ProposalType.RENEWAL);
		lifeProposal.setSubmittedDate(new Date());
		if (lifeProposal.getCustomer() == null) {
			organization = true;
		}

		insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();

		if (lifepolicy != null) {
			Date oldEndDate = lifepolicy.getPolicyInsuredPersonList().get(0).getEndDate();
			int periodOfMonth = lifepolicy.getPolicyInsuredPersonList().get(0).getPeriodMonth();
			Date currentDate = new Date();
			if (oldEndDate.after(currentDate)) {
				startDate = oldEndDate;
			} else {
				startDate = currentDate;
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(Calendar.MONTH, periodOfMonth);
			endDate = cal.getTime();

			lifeProposal.getProposalInsuredPersonList().clear();
			for (PolicyInsuredPerson policyInsuredPerson : lifepolicy.getPolicyInsuredPersonList()) {
				policyInsuredPerson.setStartDate(startDate);
				policyInsuredPerson.setEndDate(endDate);
				lifeProposal.addInsuredPerson(new ProposalInsuredPerson(policyInsuredPerson));
			}

			for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
				insuredPersonInfoDTO = new InsuredPersonInfoDTO(pv);
				insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			}
		} else if (isFromPortal) {

			for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
				insuredPersonInfoDTO = new InsuredPersonInfoDTO(pv);
				insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			}
		}

		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
		createNewBeneficiariesInfo();
		outjectLifeProposal(lifeProposal);
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	/*************************************************
	 * Start Beneficiary Manage
	 *********************************************************/
	private boolean createNewBeneficiariesInfo;
	private BeneficiariesInfoDTO beneficiariesInfoDTO;
	private Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap;

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	private void createNewBeneficiariesInfo() {
		createNewBeneficiariesInfo = true;
		beneficiariesInfoDTO = new BeneficiariesInfoDTO();
	}

	public BeneficiariesInfoDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public void setBeneficiariesInfoDTO(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
	}

	public void prepareAddNewBeneficiariesInfo() {
		createNewBeneficiariesInfo();
	}

	public void prepareEditBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
		this.createNewBeneficiariesInfo = false;
	}

	public Map<String, BeneficiariesInfoDTO> sortByValue(Map<String, BeneficiariesInfoDTO> map) {
		List<Map.Entry<String, BeneficiariesInfoDTO>> list = new LinkedList<Map.Entry<String, BeneficiariesInfoDTO>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, BeneficiariesInfoDTO>>() {
			public int compare(Map.Entry<String, BeneficiariesInfoDTO> o1, Map.Entry<String, BeneficiariesInfoDTO> o2) {
				try {
					Long l1 = Long.parseLong(o1.getKey());
					Long l2 = Long.parseLong(o2.getKey());
					if (l1 > l2) {
						return 1;
					} else if (l1 < l2) {
						return -1;
					} else {
						return 0;
					}
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		});

		Map<String, BeneficiariesInfoDTO> result = new LinkedHashMap<String, BeneficiariesInfoDTO>();
		for (Map.Entry<String, BeneficiariesInfoDTO> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public void saveBeneficiariesInfo() {
		if (validBeneficiary(beneficiariesInfoDTO)) {
			beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
			insuredPersonInfoDTO.setBeneficiariesInfoDTOList(new ArrayList<BeneficiariesInfoDTO>(sortByValue(beneficiariesInfoDTOMap).values()));
			createNewBeneficiariesInfo();
			PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
		}
	}

	public void removeBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfoDTO.getTempId());
		insuredPersonInfoDTO.getBeneficiariesInfoDTOList().remove(beneficiariesInfoDTO);
		createNewBeneficiariesInfo();
	}

	/*************************************************
	 * End Beneficiary Manage
	 *********************************************************/

	/*************************************************
	 * Start AddOn Manage
	 *********************************************************/
	private boolean createNewAddOn;
	private InsuredPersonAddOnDTO insuredPersonAddOnDTO;

	public boolean isCreateNewAddOn() {
		return createNewAddOn;
	}

	private void createInsuredPersonAddOnDTO() {
		createNewAddOn = true;
		insuredPersonAddOnDTO = new InsuredPersonAddOnDTO();
	}

	public InsuredPersonAddOnDTO getInsuredPersonAddOnDTO() {
		return insuredPersonAddOnDTO;
	}

	public void setInsuredPersonAddOnDTO(InsuredPersonAddOnDTO insuredPersonAddOnDTO) {
		this.insuredPersonAddOnDTO = insuredPersonAddOnDTO;
	}

	public void prepareInsuredPersonAddOnDTO() {
		createInsuredPersonAddOnDTO();
	}

	public void prepareEditInsuredPersonAddOnDTO(InsuredPersonAddOnDTO insuredPersonAddOnDTO) {
		this.insuredPersonAddOnDTO = insuredPersonAddOnDTO;
		this.createNewAddOn = false;
	}

	public void saveInsuredPersonAddOnDTO() {
		if (validInsuredPersonAddOn()) {
			insuredPersonInfoDTO.addInsuredPersonAddOn(insuredPersonAddOnDTO);
			createInsuredPersonAddOnDTO();
		}
	}

	public void removeAddOn(InsuredPersonAddOnDTO insuredPersonAddOnDTO) {
		insuredPersonInfoDTO.removeInsuredPersonAddOn(insuredPersonAddOnDTO);
		createInsuredPersonAddOnDTO();
	}

	private boolean validInsuredPersonAddOn() {
		boolean valid = true;
		if (insuredPersonAddOnDTO.getAddOn() == null) {
			addErrorMessage("addOnEntryForm" + ":addOn", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (valid) {
			PrimeFaces.current().executeScript("PF('addOnEntryDialog').hide()");
		}
		return valid;
	}

	/*************************************************
	 * End AddOn Manage
	 *********************************************************/

	/*************************************************
	 * Start InsuredPerson Manage
	 ******************************************************/
	private Map<String, InsuredPersonInfoDTO> insuredPersonInfoDTOMap;
	private InsuredPersonInfoDTO insuredPersonInfoDTO;
	private boolean createNewIsuredPersonInfo;
	private Boolean isEdit = false;
	private Boolean isReplace = false;
	private Product product;

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	private void createNewInsuredPersonInfo() {
		createNewIsuredPersonInfo = true;
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		insuredPersonInfoDTO.setProduct(product);
		insuredPersonInfoDTO.setStartDate(new Date());
		isEdit = false;
		isReplace = false;
	}

	public PeriodType[] getPeriodType() {
		return new PeriodType[] { PeriodType.MONTH, PeriodType.YEAR };
	}

	public Boolean getIsReplace() {
		return isReplace;
	}

	public void setIsReplace(Boolean isReplace) {
		this.isReplace = isReplace;
	}

	public InsuredPersonInfoDTO getInsuredPersonInfoDTO() {
		return insuredPersonInfoDTO;
	}

	public void setInsuredPersonInfoDTO(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
	}

	public List<InsuredPersonInfoDTO> getInsuredPersonInfoDTOList() {
		List<InsuredPersonInfoDTO> insuDTOList = new ArrayList<InsuredPersonInfoDTO>();
		if (insuredPersonInfoDTOMap == null || insuredPersonInfoDTOMap.values() == null) {
			return new ArrayList<InsuredPersonInfoDTO>();
		} else {
			for (InsuredPersonInfoDTO dto : insuredPersonInfoDTOMap.values()) {
				if (dto.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
					insuDTOList.add(dto);
				}
			}
		}
		return insuDTOList;
	}

	public void prepareAddNewInsuredPersonInfo() {
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
	}

	public void prepareEditInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
		product = insuredPersonInfoDTO.getProduct();
		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (BeneficiariesInfoDTO bdto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
			}
		}
		createNewIsuredPersonInfo = false;
		isEdit = true;
	}

	public void prepareReplaceInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
		createNewBeneficiariesInfoDTOMap();
		createNewIsuredPersonInfo = false;
		isReplace = true;
	}

	private UploadedFile uploadedFile;

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public void upload(ActionEvent event) {
		try {
			InputStream inputStream = uploadedFile.getInputstream();
			Workbook wb = WorkbookFactory.create(inputStream);
			Sheet sheet = wb.getSheetAt(0);
			boolean readAgain = true;
			int i = 1;
			while (readAgain) {
				InsuredPersonInfoDTO insuredPersonInfoDTO = new InsuredPersonInfoDTO();
				ResidentAddress ra = new ResidentAddress();
				Name name = new Name();
				Row row = sheet.getRow(i);
				if (row == null) {
					readAgain = false;
					break;
				}
				String initialId = row.getCell(0).getStringCellValue();
				if (initialId == null || initialId.isEmpty()) {
					readAgain = false;
					break;
				} else {
					insuredPersonInfoDTO.setInitialId(initialId);
					String firstName = row.getCell(1).getStringCellValue();
					name.setFirstName(firstName);
					String middleName = row.getCell(2).getStringCellValue();
					name.setMiddleName(middleName);
					String lastName = row.getCell(3).getStringCellValue();
					name.setLastName(lastName);

					String fatherName = row.getCell(4).getStringCellValue();
					insuredPersonInfoDTO.setFatherName(fatherName);

					String idNo = row.getCell(5).getStringCellValue();
					insuredPersonInfoDTO.setIdNo(idNo);

					IdType idType = IdType.valueOf(row.getCell(6).getStringCellValue());
					insuredPersonInfoDTO.setIdType(idType);

					Date dateOfBirth = row.getCell(7).getDateCellValue();
					insuredPersonInfoDTO.setDateOfBirth(dateOfBirth);

					double suminsured = row.getCell(8).getNumericCellValue();
					insuredPersonInfoDTO.setSumInsuredInfo(suminsured);

					String resAddress = row.getCell(9).getStringCellValue();
					ra.setResidentAddress(resAddress);
					String resTownshipId = row.getCell(10).getStringCellValue();
					Township township = townshipService.findTownshipById(resTownshipId);
					ra.setTownship(township);

					String occupationId = row.getCell(11).getStringCellValue();
					if (occupationId == null || occupationId.isEmpty()) {
						Occupation occupation = occupationService.findOccupationById(occupationId);
						insuredPersonInfoDTO.setOccupation(occupation);
					}

					Gender gender = Gender.valueOf(row.getCell(12).getStringCellValue());
					insuredPersonInfoDTO.setGender(gender);

					String productId = row.getCell(13).getStringCellValue();
					Product product = productService.findProductById(productId);
					insuredPersonInfoDTO.setProduct(product);

					int periodOfYears = (int) row.getCell(14).getNumericCellValue();
					insuredPersonInfoDTO.setPeriodOfYears(periodOfYears);
					insuredPersonInfoDTO.setResidentAddress(ra);
					insuredPersonInfoDTO.setName(name);

					insuredPersonInfoDTO.setStartDate(new Date());
					Calendar cal = Calendar.getInstance();
					cal.setTime(insuredPersonInfoDTO.getStartDate());
					cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
					insuredPersonInfoDTO.setEndDate(cal.getTime());
					for (InsuredPersonKeyFactorValue insKFV : insuredPersonInfoDTO.getKeyFactorValueList()) {
						KeyFactor kf = insKFV.getKeyFactor();
						if (KeyFactorChecker.isSumInsured(kf)) {
							insKFV.setValue(insuredPersonInfoDTO.getSumInsuredInfo() + "");
						} else if (KeyFactorChecker.isTerm(kf)) {
							insKFV.setValue(insuredPersonInfoDTO.getPeriodOfYears() + "");
						} else if (KeyFactorChecker.isAge(kf)) {
							insKFV.setValue(insuredPersonInfoDTO.getAgeForNextYear() + "");
						}
					}
					insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
				}
				i++;
			}
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Failed to upload the file!"));
		} catch (InvalidFormatException e) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Invalid data is occured in Uploaded File!"));
		}
	}

	public void saveInsuredPersonInfo() {
		insuredPersonInfoDTO.setProduct(product);
		Calendar cal = Calendar.getInstance();
		insuredPersonInfoDTO.setStartDate(startDate);
		insuredPersonInfoDTO.setEndDate(endDate);
		cal.setTime(insuredPersonInfoDTO.getStartDate());
		cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
		if (validInsuredPerson(insuredPersonInfoDTO)) {
			setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears());
			insuredPersonInfoDTO.setEndDate(cal.getTime());
			insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			createNewInsuredPersonInfo();
			createNewBeneficiariesInfoDTOMap();
		}
	}

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
	}

	/**
	 * 
	 * This method is used to validate Insured Person's Information according to
	 * selected product type.
	 * 
	 * @param insuredPersonInfoDTO
	 * @return
	 */
	private boolean validInsuredPerson(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		boolean valid = true;
		String formID = "lifeProposalEntryForm";

		if (isEmpty(insuredPersonInfoDTO.getInitialId())) {
			addErrorMessage(formID + ":initialId", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(insuredPersonInfoDTO.getName().getFirstName())) {
			addErrorMessage(formID + ":firstName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(insuredPersonInfoDTO.getFatherName())) {
			addErrorMessage(formID + ":fatherName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!insuredPersonInfoDTO.getIdType().equals(IdType.STILL_APPLYING) && isEmpty(insuredPersonInfoDTO.getFullIdNo())) {
			addErrorMessage(formID + ":idNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (insuredPersonInfoDTO.getDateOfBirth() == null) {
			addErrorMessage(formID + ":dateOfBirth", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (insuredPersonInfoDTO.getSumInsuredInfo() <= 0) {
			addErrorMessage(formID + ":sumInsuredInfo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(insuredPersonInfoDTO.getResidentAddress().getResidentAddress())) {
			addErrorMessage(formID + ":resident", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (insuredPersonInfoDTO.getResidentAddress().getTownship() == null) {
			addErrorMessage(formID + ":townShip", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (insuredPersonInfoDTO.getGender() == null) {
			addErrorMessage(formID + ":gender", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (insuredPersonInfoDTO.getProduct() == null) {
			addErrorMessage(formID + ":product", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (KeyFactorChecker.isGroupLife(insuredPersonInfoDTO.getProduct()) && insuredPersonInfoDTO.getDateOfBirth() != null) {
			if (insuredPersonInfoDTO.getAgeForNextYear() < 20) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MINIMUN_INSURED_PERSON_AGE, 20);
				valid = false;
			}
			if (insuredPersonInfoDTO.getAgeForNextYear() > 60) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MAXIMUM_INSURED_PERSON_AGE, 60);
				valid = false;
			} else if (insuredPersonInfoDTO.getPeriodOfYears() != 1) {
				addErrorMessage(formID + ":periodOfInsurance", MessageId.AVAILABLE_INSURED_PERIOD, 1);
				valid = false;
			} else if (insuredPersonInfoDTO.getAgeForNextYear() + insuredPersonInfoDTO.getPeriodOfYears() > 65) {
				int availablePeriod = 65 - insuredPersonInfoDTO.getAgeForNextYear();
				addErrorMessage(formID + ":periodOfInsurance", MessageId.MAXIMUM_INSURED_YEARS, availablePeriod < 0 ? 0 : availablePeriod);
				valid = false;
			}
		}

		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList() != null && insuredPersonInfoDTO.getBeneficiariesInfoDTOList().size() != 0) {
			float totalPercent = 0.0f;
			for (BeneficiariesInfoDTO bfDTO : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				totalPercent = totalPercent + bfDTO.getPercentage();
			}
			if (totalPercent > 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.OVER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
			if (totalPercent < 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.LOWER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
			for (BeneficiariesInfoDTO benfInfoDTO : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				if (!benfInfoDTO.isValidBeneficiaries()) {
					addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.INVALID_BENEFICIARY_PERSON);
					valid = false;
					break;
				}
			}
		} else {
			addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.REQUIRED_BENEFICIARY_PERSON);
			valid = false;

		}
		return valid;
	}

	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	public void removeInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		insuredPersonInfoDTOMap.remove(insuredPersonInfoDTO.getTempId());
		createNewInsuredPersonInfo();
	}

	/**
	 * 
	 * This method is used to retrieve ProposalInsuredPerson instances from DTO
	 * Map.
	 * 
	 * @return A {@link List} of {@link ProposalInsuredPerson} instances
	 */
	public List<ProposalInsuredPerson> getInsuredPersonInfoList() {
		List<ProposalInsuredPerson> result = new ArrayList<ProposalInsuredPerson>();
		if (insuredPersonInfoDTOMap.values() != null) {
			for (InsuredPersonInfoDTO insuredPersonInfoDTO : insuredPersonInfoDTOMap.values()) {
				ClassificationOfHealth classificationOfHealth = insuredPersonInfoDTO.getClassificationOfHealth();
				Customer customer = customerService.findCustomerByInsuredPerson(insuredPersonInfoDTO.getName(), insuredPersonInfoDTO.getFullIdNo(),
						insuredPersonInfoDTO.getFatherName(), insuredPersonInfoDTO.getDateOfBirth());
				insuredPersonInfoDTO.setCustomer(customer);
				insuredPersonInfoDTO.setAge(insuredPersonInfoDTO.getAgeForNextYear());
				ProposalInsuredPerson proposalInsuredPerson = new ProposalInsuredPerson(insuredPersonInfoDTO, lifeProposal);
				proposalInsuredPerson.setInsuredPersonAddOnList(insuredPersonInfoDTO.getInsuredPersonAddOnList(proposalInsuredPerson));
				proposalInsuredPerson.setClsOfHealth(classificationOfHealth);

				// prepare
				List<InsuredPersonAttachment> insuredPersonAttachments = insuredPersonInfoDTO.getPerAttachmentList();
				if (insuredPersonAttachments != null) {
					for (InsuredPersonAttachment attachment : insuredPersonAttachments) {
						proposalInsuredPerson.addAttachment(attachment);
					}
				}
				proposalInsuredPerson.setInsuredPersonBeneficiariesList(insuredPersonInfoDTO.getBeneficiariesInfoList(proposalInsuredPerson));
				proposalInsuredPerson.setKeyFactorValueList(insuredPersonInfoDTO.getKeyFactorValueList(proposalInsuredPerson));
				result.add(proposalInsuredPerson);
			}
		}
		return result;
	}

	public String backLifeProposal() {
		createNewInsuredPersonInfo();
		return "lifeProposal";
	}

	/*************************************************
	 * End InsuredPerson Manage
	 ********************************************************/
	/*************************************************
	 * Proposal Manage
	 ********************************************/
	private boolean organization;

	public boolean isOrganization() {
		return organization;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (organization) {
			lifeProposal.setCustomer(null);
			insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		} else {
			lifeProposal.setOrganization(null);
		}
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			lifeProposal.setSaleMan(null);
			lifeProposal.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			lifeProposal.setAgent(null);
			lifeProposal.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			lifeProposal.setSaleMan(null);
			lifeProposal.setAgent(null);
		}
	}

	/*************************************************
	 * Proposal Manage
	 **********************************************/

	public void createNewLifeProposal() {
		createNew = true;
		lifeProposal = new LifeProposal();
		lifeProposal.setSubmittedDate(new Date());
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public LifeProposal getLifeProposal() {
		/* Populate data into InsuredPersonInfoDTO when customer is selected. */
		if (lifepolicy == null) {
			if (lifeProposal.getCustomer() != null) {
				Customer customer = lifeProposal.getCustomer();
				insuredPersonInfoDTO.setInitialId(customer.getInitialId());
				insuredPersonInfoDTO.setName(customer.getName());
				insuredPersonInfoDTO.setIdNo(customer.getIdNo());
				insuredPersonInfoDTO.setIdType(customer.getIdType());
				insuredPersonInfoDTO.setGender(customer.getGender());
				insuredPersonInfoDTO.setDateOfBirth(customer.getDateOfBirth());
				insuredPersonInfoDTO.setResidentAddress(customer.getResidentAddress());
				insuredPersonInfoDTO.setFatherName(customer.getFatherName());
				insuredPersonInfoDTO.setOccupation(customer.getOccupation());
			}

		}
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public String addNewLifeProposal() {
		String result = null;
		try {
			String formID = "lifeProposalEntryForm";
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
				return null;
			}
			WorkFlowDTO workFlowDTO;
			InsuredPersonInfoDTO tempDTO = new InsuredPersonInfoDTO();
			for (InsuredPersonInfoDTO in : insuredPersonInfoDTOMap.values()) {
				tempDTO = in;
			}

			WorkflowTask workflowTask = WorkflowTask.RENEWAL_SURVEY;
			workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, WorkflowReferenceType.LIFE_PROPOSAL, user, responsiblePerson);
			lifeProposal.setInsuredPersonList(getInsuredPersonInfoList());
			lifeProposal.setLifePolicy(null);
			lifeProposal = renewalGroupLifeProposalService.renewalGroupLifeProposal(lifeProposal, workFlowDTO, RequestStatus.PROPOSED.name());
			outjectLifeProposal(lifeProposal);

			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			createNewLifeProposal();
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "lifeProposalEntryForm";
		// customerList = customerService.findAllCustomer();
		if ("proposalInfo".equals(event.getOldStep())) {
			if (lifeProposal.getSubmittedDate() == null) {
				addErrorMessage(formID + ":submittedDate", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (lifeProposal.getCustomer() == null && lifeProposal.getOrganization() == null) {
				addErrorMessage(formID + ":customer", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (isEmpty(lifeProposal.getSalePersonName())) {
				addErrorMessage(formID + ":userType", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (lifeProposal.getPaymentType() == null) {
				addErrorMessage(formID + ":paymentType", MessageId.REQUIRED_PAYMENTTYPE);
				valid = false;
			}
			if (lifeProposal.getBranch() == null) {
				addErrorMessage(formID + ":branch", MessageId.REQUIRED_BRANCH);
				valid = false;
			}
		}

		if (lifeProposal.getLifePolicy() == null) {
			if ("InsuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
				if (getInsuredPersonInfoDTOList().isEmpty()) {
					addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", MessageId.REQUIRED_INSURED_PERSION);
					valid = false;
				}
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public Map<String, BeneficiariesInfoDTO> getBeneficiariesInfoDTOMap() {
		return beneficiariesInfoDTOMap;
	}

	public void setBeneficiariesInfoDTOMap(Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap) {
		this.beneficiariesInfoDTOMap = beneficiariesInfoDTOMap;
	}

	public String getPublicLifeId() {
		return ProductIDConfig.getPublicLifeId();
	}

	public String getGroupLifeId() {
		return ProductIDConfig.getGroupLifeId();
	}

	public String getUserType() {
		if (lifeProposal.getAgent() != null) {
			userType = UserType.AGENT.toString();
		} else if (lifeProposal.getSaleMan() != null) {
			userType = UserType.SALEMAN.toString();
		} else if (lifeProposal.getReferral() != null) {
			userType = UserType.REFERRAL.toString();
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public int getAgeForOldYear(Date dob) {
		Calendar cal_1 = Calendar.getInstance();
		cal_1.setTime(lifepolicy.getCommenmanceDate());
		int currentYear = cal_1.get(Calendar.YEAR);

		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(dob);
		cal_2.set(Calendar.YEAR, currentYear);
		if (lifepolicy.getCommenmanceDate().after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(dob);
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(dob);
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}

	public Boolean isChangePeriod() {
		if (insuredPersonInfoDTO.getPeriodOfYears() != lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears()) {
			return true;
		}
		return false;
	}

	public Boolean isIncreasedSIAmount(InsuredPersonInfoDTO dto) {
		if (dto.getSumInsuredInfo() <= lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()) {
			return false;
		}
		return true;
	}

	public Boolean isReplaceColumn() {
		if (lifepolicy != null) {
			return true;
		}
		return false;
	}

	public Boolean isDecreasedSIAmount() {
		if (insuredPersonInfoDTO.getInsPersonCodeNo() != null) {
			PolicyInsuredPerson policyInsuPerson = renewalGroupLifePolicyService.findInsuredPersonByCodeNo(insuredPersonInfoDTO.getInsPersonCodeNo());
			if (insuredPersonInfoDTO.getSumInsuredInfo() < policyInsuPerson.getSumInsured()) {
				int passedMonths = getPassedMonths();
				int passedYear = passedMonths / 12;
				int period = insuredPersonInfoDTO.getPeriodOfYears();
				if ((period <= 12 && passedYear >= 2) || period > 12 && passedYear >= 3) {
					if (passedMonths % 12 > 5) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void showPaidUpDialog() {
		saveInsuredPersonInfo();
	}

	public void paidPremiumForPaidUp(Boolean isPaid) {
		if (isPaid) {
			insuredPersonInfoDTO.setIsPaidPremiumForPaidup(true);
		} else {
			insuredPersonInfoDTO.setIsPaidPremiumForPaidup(false);
		}
		saveInsuredPersonInfo();
	}

	public int getPassedMonths() {
		DateTime vDate = new DateTime(lifeProposal.getLifePolicy().getActivedPolicyEndDate());
		DateTime cDate = new DateTime(lifeProposal.getLifePolicy().getCommenmanceDate());
		int paymentType = lifeProposal.getLifePolicy().getPaymentType().getMonth();
		int passedMonths = Months.monthsBetween(cDate, vDate).getMonths();
		if (paymentType > 0) {
			if (passedMonths % paymentType != 0) {
				passedMonths = passedMonths + 1;
			} // if paymentType is Lumpsum
		} else if (paymentType == 0) {
			if (passedMonths % 12 != 0) {
				passedMonths = passedMonths + 1;
			}
		}
		return passedMonths;
	}

	public int getPassedYears() {
		return getPassedMonths() / 12;
	}

	private void setKeyFactorValue(double sumInsured, int age, int period) {
		for (InsuredPersonKeyFactorValue vehKF : insuredPersonInfoDTO.getKeyFactorValueList()) {
			KeyFactor kf = vehKF.getKeyFactor();
			if (KeyFactorChecker.isSumInsured(kf)) {
				vehKF.setValue(sumInsured + "");
			} else if (KeyFactorChecker.isAge(kf)) {
				vehKF.setValue(age + "");
			} else if (KeyFactorChecker.isTerm(kf)) {
				vehKF.setValue(period + "");
			}
		}
	}

	private boolean validBeneficiary(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		boolean valid = true;
		String formID = "beneficiaryInfoEntryForm";
		if (isEmpty(beneficiariesInfoDTO.getName().getFirstName())) {
			addErrorMessage(formID + ":firstName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!beneficiariesInfoDTO.getIdType().equals(IdType.STILL_APPLYING) && isEmpty(beneficiariesInfoDTO.getFullIdNo())) {
			addErrorMessage(formID + ":idNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (isEmpty(beneficiariesInfoDTO.getResidentAddress().getResidentAddress())) {
			addErrorMessage(formID + ":address", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (beneficiariesInfoDTO.getResidentAddress().getTownship() == null) {
			addErrorMessage(formID + ":townShip", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (beneficiariesInfoDTO.getPercentage() < 1 || beneficiariesInfoDTO.getPercentage() > 100) {
			addErrorMessage(formID + ":percentage", MessageId.BENEFICIARY_PERCENTAGE);
			valid = false;
		}
		return valid;
	}

	public void selectUser() {
		selectUser(WorkflowTask.RENEWAL_SURVEY, WorkFlowType.LIFE);
	}

	private void outjectLifeProposal(LifeProposal lifeProposal) {
		putParam("lifeProposal", lifeProposal);
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void selectAddOn() {
		selectAddOn(product);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		lifeProposal.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		lifeProposal.setOrganization(organization);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		lifeProposal.setPaymentType(paymentType);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		lifeProposal.setAgent(agent);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		lifeProposal.setSaleMan(saleMan);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		lifeProposal.setReferral(referral);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		lifeProposal.setBranch(branch);
		lifeProposal.setSalePoint(null);
	}

	public void returnInsuredPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		insuredPersonInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonInfoDTO.setOccupation(occupation);
	}

	public void returnTypesOfSport(SelectEvent event) {
		TypesOfSport typesOfSport = (TypesOfSport) event.getObject();
		insuredPersonInfoDTO.setTypesOfSport(typesOfSport);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnAddOn(SelectEvent event) {
		AddOn addOn = (AddOn) event.getObject();
		insuredPersonAddOnDTO.setAddOn(addOn);
	}

	public List<Product> getProductsList() {
		return productsList;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	public void selectSalePoint() {

		selectSalePointByBranch(lifeProposal.getBranch() == null ? "" : lifeProposal.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		lifeProposal.setSalePoint(salePoint);
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (this.entitys != null && !entity.getId().equals(this.entitys.getId())) {
			lifeProposal.setBranch(null);
			lifeProposal.setSalePoint(null);
		}
		this.entitys = entity;
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

	public void calculateAgeForBene() {
		beneficiariesInfoDTO.setAge(getAgeForBeneNextYear());
	}

	public int getAgeForBeneNextYear() {
		Calendar cal_1 = Calendar.getInstance();
		int currentYear = cal_1.get(Calendar.YEAR);
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(beneficiariesInfoDTO.getDateOfBirth());
		cal_2.set(Calendar.YEAR, currentYear);

		if (new Date().after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(beneficiariesInfoDTO.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(beneficiariesInfoDTO.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}

}
