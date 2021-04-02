/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.CustomerStatus;
import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.SalutationType;
import org.ace.insurance.medical.proposal.CustomerInfoStatus;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.province.service.interfaces.IProvinceService;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewCustomerActionBean")
public class AddNewCustomerActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CustomerService}")
	private ICustomerService customerService;

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{ProvinceService}")
	private IProvinceService provinceService;

	public void setProvinceService(IProvinceService provinceService) {
		this.provinceService = provinceService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
	}

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
	}

	private boolean createNew;
	private boolean createNewFamilyInfo;
	private Customer customer;
	private Map<String, FamilyInfoDTO> familyInfoDTOMap;
	private FamilyInfoDTO familyInfoDTO;
	private List<RelationShip> relationShipList;
	private List<StateCode> stateCodeList;
	private List<TownshipCode> townshipCodeList;
	private Entitys entitys;
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		initialization();
		relationShipList = relationShipService.findAllRelationShip();
		townshipCodeList = new ArrayList<>();
		familyInfoDTOMap = new HashMap<String, FamilyInfoDTO>();
		loadProvinceNo();
		loadCustomer();
	}

	private void loadCustomer() {
		if (customer != null) {
			createNew = false;
			entitys = customer.getBranch().getEntity();
			if (customer.getStateCode() != null)
				changeStateCode();
			prepareAddNewFamilyInfo();
			loadFamilyInfoDTO();

		} else {
			createNewCustomer();
		}
	}

	private void loadFamilyInfoDTO() {
		FamilyInfoDTO familyInfoDTO = null;
		StringTokenizer tokenizer = null;
		StateCode stateCode = null;
		TownshipCode townshipCode = null;
		String stateCodeNo = null;
		String townshipCodeNo = null;
		IdConditionType type = null;
		String idNo = null;
		for (FamilyInfo familyInfo : customer.getFamilyInfo()) {
			familyInfoDTO = new FamilyInfoDTO(familyInfo);
			if (familyInfoDTO.getIdType().equals(IdType.NRCNO) && familyInfoDTO.getFullIdNo().contains("/")) {
				tokenizer = new StringTokenizer(familyInfoDTO.getFullIdNo(), "/()");
				stateCodeNo = tokenizer.nextToken();
				townshipCodeNo = tokenizer.nextToken();
				stateCode = stateCodeService.findStateCodeByCodeNo(stateCodeNo, townshipCodeNo);
				townshipCode = townshipCodeService.findTownshipCodeByCodeNo(townshipCodeNo, stateCodeNo);
				type = IdConditionType.valueOf(tokenizer.nextToken());
				idNo = tokenizer.nextToken();
				familyInfoDTO.setStateCode(stateCode);
				familyInfoDTO.setTownshipCode(townshipCode);
				familyInfoDTO.setIdConditionType(type);
				familyInfoDTO.setIdNo(idNo);
			} else {
				familyInfoDTO.setIdNo(familyInfoDTO.getFullIdNo());
			}

			familyInfoDTOMap.put(familyInfoDTO.getTempId(), familyInfoDTO);
		}
	}

	private void initialization() {
		customer = (Customer) getParam("customer");
	}

	@PreDestroy
	private void destroy() {
		removeParam("customer");
	}

	public void changeCustomerIdType(AjaxBehaviorEvent e) {
		customer.setFullIdNo(null);
		customer.setIdNo(null);
		customer.setStateCode(null);
		customer.setTownshipCode(null);
		customer.setIdConditionType(null);
		PrimeFaces.current().resetInputs("customerEntryForm:customerIdNoPanelGrid");
	}

	public void changeFamilyIdType(AjaxBehaviorEvent e) {
		if (IdType.NRCNO.equals(familyInfoDTO.getIdType())) {
			familyInfoDTO.setIdNo(null);
			familyInfoDTO.setFullIdNo(null);
		} else {
			familyInfoDTO.setIdNo(null);
			familyInfoDTO.setStateCode(null);
			familyInfoDTO.setTownshipCode(null);
			familyInfoDTO.setIdConditionType(null);
		}
	}

	public void loadProvinceNo() {
		stateCodeList = stateCodeService.findAllStateCode();
	}

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCode(customer.getStateCode());
	}

	public void changeStateCodeForFamilyInfo() {
		townshipCodeList = townshipCodeService.findByStateCode(familyInfoDTO.getStateCode());
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public boolean isCreateNewFamilyInfo() {
		return createNewFamilyInfo;
	}

	private void createNewFamilyInfo() {
		createNewFamilyInfo = true;
		familyInfoDTO = new FamilyInfoDTO();
		familyInfoDTO.setIdType(IdType.NRCNO);
	}

	public FamilyInfoDTO getFamilyInfoDTO() {
		return familyInfoDTO;
	}

	public void setFamilyInfoDTO(FamilyInfoDTO familyInfoDTO) {
		this.familyInfoDTO = familyInfoDTO;
	}

	public List<FamilyInfoDTO> getFamilyInfoDTOList() {
		if (familyInfoDTOMap.values() != null) {
			return new ArrayList<FamilyInfoDTO>(familyInfoDTOMap.values());
		}
		return new ArrayList<FamilyInfoDTO>();
	}

	public void prepareAddNewFamilyInfo() {
		createNewFamilyInfo();
	}

	public void prepareEditFamilyInfo(FamilyInfoDTO familyInfoDTO) {
		this.familyInfoDTO = familyInfoDTO;
		if (familyInfoDTO.getStateCode() != null)
			changeStateCodeForFamilyInfo();
		this.createNewFamilyInfo = false;
	}

	public void saveFamilyInfo() {

		familyInfoDTO.setFullIdNo();
		familyInfoDTOMap.put(familyInfoDTO.getTempId(), familyInfoDTO);
		createNewFamilyInfo();

	}

	public void removeFamilyInfo(FamilyInfoDTO familyInfoDTO) {
		familyInfoDTOMap.remove(familyInfoDTO.getTempId());
	}

	public List<FamilyInfo> getFamilyInfoList() {
		List<FamilyInfo> result = new ArrayList<FamilyInfo>();
		if (familyInfoDTOMap.values() != null) {
			for (FamilyInfoDTO familyInfoDTO : familyInfoDTOMap.values()) {
				result.add(new FamilyInfo(familyInfoDTO.getInitialId(), familyInfoDTO.getFullIdNo(), familyInfoDTO.getIdType(), familyInfoDTO.getDateOfBirth(),
						familyInfoDTO.getName(), familyInfoDTO.getRelationShip(), familyInfoDTO.getIndustry(), familyInfoDTO.getOccupation()));
			}
		}
		return result;
	}

	public Gender[] getGenderSelectItemList() {
		return Gender.values();
	}

	public IdType[] getIdTypeSelectItemList() {
		return IdType.values();
	}

	public IdConditionType[] getIdConditionTypeSelectItemList() {
		return IdConditionType.values();
	}

	public MaritalStatus[] getMaritalStatusSelectItemList() {
		return MaritalStatus.values();
	}

	public void createNewCustomer() {
		createNew = true;
		customer = new Customer();
		customer.setIdType(IdType.NRCNO);
		familyInfoDTOMap = new HashMap<String, FamilyInfoDTO>();
		prepareAddNewFamilyInfo();
	}

	public String addNewCustomer() {
		String result = null;
		try {
			customer.setFamilyInfo(getFamilyInfoList());
			CustomerInfoStatus status = new CustomerInfoStatus();
			customer.addCustomerInfoStatus(status);
			status.setStatusName(CustomerStatus.CONTRACTOR);
			customerService.addNewCustomer(customer);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.INSERT_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, customer.getFullName());
			result = "manageCustomer";

		} catch (SystemException ex) {
			handelSysException(ex);
		}

		return result;
	}

	public String updateCustomer() {
		String result = null;
		try {
			customer.setFamilyInfo(getFamilyInfoList());
			customerService.updateCustomer(customer);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UPDATE_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, customer.getFullName());
			result = "manageCustomer";
		} catch (SystemException ex) {
			handelSysException(ex);
		}

		return result;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("personalInfo".equals(event.getOldStep()) && "generalInfo".equals(event.getNewStep())) {
			loadCustomerFullIdNo();
			boolean isExistingCustomer = customerService.checkExistingCustomer(customer);
			if (customer.getDateOfBirth().after(new Date())) {
				addErrorMessage("customerEntryForm:dob", MessageId.INVALID_CUSTOMER_DOB);
				valid = false;
			}
			if (isExistingCustomer) {
				valid = false;
				addInfoMessage(null, MessageId.EXISTING_CUSTOMER, customer.getFullName());
				PrimeFaces.current().ajax().update("customerEntryForm:growl");
			}

		} else if ("generalInfo".equals(event.getOldStep()) && "personalInfo".equals(event.getNewStep())) {
			if (customer.getStateCode() != null)
				changeStateCode();
		}

		return valid ? event.getNewStep() : event.getOldStep();
	}

	private void loadCustomerFullIdNo() {
		String fullIdNo = null;
		IdType idType = customer.getIdType();
		if (idType.equals(IdType.NRCNO)) {
			fullIdNo = customer.getStateCode().getCodeNo() + "/" + customer.getTownshipCode().getTownshipcodeno() + "(" + customer.getIdConditionType() + ")" + customer.getIdNo();
		} else if (idType.equals(IdType.FRCNO) || idType.equals(IdType.PASSPORTNO)) {
			fullIdNo = customer.getIdNo();
		}
		customer.setFullIdNo(fullIdNo);
	}

	public SalutationType[] getSalutationTypes() {
		return SalutationType.values();
	}

	public List<RelationShip> getRelationShipList() {
		return relationShipList;
	}

	public void returnNationality(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		customer.setCountry(nationality);
	}

	public void returnReligion(SelectEvent event) {
		Religion religion = (Religion) event.getObject();
		customer.setReligion(religion);
	}

	public void returnQualification(SelectEvent event) {
		Qualification qualification = (Qualification) event.getObject();
		customer.setQualification(qualification);
	}

	public void returnBankBranch(SelectEvent event) {
		BankBranch bankBranch = (BankBranch) event.getObject();
		customer.setBankBranch(bankBranch);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		customer.setBranch(branch);
		customer.setSalePoint(null);
	}

	public void returnIndustry(SelectEvent event) {
		Industry industry = (Industry) event.getObject();
		customer.setIndustry(industry);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		customer.setOccupation(occupation);
	}

	public void returnResidentTownship(SelectEvent event) {
		Township residentTownship = (Township) event.getObject();
		customer.getResidentAddress().setTownship(residentTownship);
	}

	public void returnPermanentTownship(SelectEvent event) {
		Township permanentTownship = (Township) event.getObject();
		customer.getPermanentAddress().setTownship(permanentTownship);
	}

	public void returnOfficeTownship(SelectEvent event) {
		Township officeTownship = (Township) event.getObject();
		customer.getOfficeAddress().setTownship(officeTownship);
	}

	public void returnFamilyOccupation(SelectEvent event) {
		Occupation familyOccupation = (Occupation) event.getObject();
		familyInfoDTO.setOccupation(familyOccupation);
	}

	public void returnFamilyIndustry(SelectEvent event) {
		Industry familyIndustry = (Industry) event.getObject();
		familyInfoDTO.setIndustry(familyIndustry);
	}

	public void selectSalePoint() {

		selectSalePointByBranch(customer.getBranch() == null ? "" : customer.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void removeBranch() {
		customer.setBranch(null);
		customer.setSalePoint(null);

	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		customer.setSalePoint(salePoint);
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (entitys != null && !entity.getId().equals(entitys.getId())) {
			customer.setBranch(null);
			customer.setSalePoint(null);
		}
		this.entitys = entity;

	}

	public void removeEntity() {
		this.entitys = null;
		customer.setBranch(null);
		customer.setSalePoint(null);
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

}
