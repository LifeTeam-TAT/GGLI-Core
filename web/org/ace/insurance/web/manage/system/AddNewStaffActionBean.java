package org.ace.insurance.web.manage.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.province.service.interfaces.IProvinceService;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
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
@ManagedBean(name = "AddNewStaffActionBean")
public class AddNewStaffActionBean extends BaseBean {

	private static final long serialVersionUID = 1L;

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

	private Staff staff;

	private List<Staff> list;

	private List<GGIOrganization> ggiOrganizationList;

	private List<StateCode> stateCodeList;

	private List<TownshipCode> townshipCodeList;

	private List<Staff> filterStaffList;

	// private FamilyInfoDTO familyInfoDTO;
	private Entitys entitys;
	private User user;
	private List<RelationShip> relationShipList;
	private boolean createNewFamilyInfo;
	// private Map<String, FamilyInfoDTO> familyInfoDTOMap;
	private boolean createNew;

	@ManagedProperty(value = "#{StaffService}")
	private IStaffService staffService;

	@ManagedProperty(value = "#{GGIOrganizationService}")
	private IGGIOrganizationService organizationService;

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	@PostConstruct
	public void init() {
		initialization();
		user = (User) getParam("LoginUser");
		// staff = new Staff();
		list = new ArrayList<Staff>();
		stateCodeList = new ArrayList<StateCode>();
		townshipCodeList = new ArrayList<TownshipCode>();

		ggiOrganizationList = organizationService.findAllGGIOrganization();
		list = staffService.findAll();
		stateCodeList = stateCodeService.findAllStateCode();

		relationShipList = relationShipService.findAllRelationShip();
		// familyInfoDTOMap = new HashMap<String, FamilyInfoDTO>();
		loadProvinceNo();
		loadStaff();
	}

	private void initialization() {
		staff = (Staff) getParam("staff");
	}

	private void loadStaff() {
		if (staff != null) {
			createNew = false;
			entitys = staff.getBranch().getEntity();
			if (staff.getStateCode() != null)
				changeStateCode();
			// prepareAddNewFamilyInfo();
			// loadFamilyInfoDTO();

		} else {
			createNewStaff();
		}
	}

	public void createNewStaff() {
		createNew = true;
		staff = new Staff();
		staff.setIdType(IdType.NRCNO);
		// familyInfoDTOMap = new HashMap<String, FamilyInfoDTO>();
		// prepareAddNewFamilyInfo();
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
		for (FamilyInfo familyInfo : staff.getFamilyInfo()) {
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

			// familyInfoDTOMap.put(familyInfoDTO.getTempId(), familyInfoDTO);
		}
	}

	public void loadProvinceNo() {
		stateCodeList = stateCodeService.findAllStateCode();
	}

	public String addNewStaff() {
		try {

			staffService.save(staff);
			if (staff.getId() == null) {
				addInfoMessage(null, MessageId.INSERT_SUCCESS, staff.getName().getFullName());
			} else {
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, this.staff.getName().getFullName());
			}

			staff = new Staff();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		list = staffService.findAll();

		return "manageStaff";
	}

	public String updateStaff() {
		String result = null;
		try {
			// staff.setFamilyInfo(getFamilyInfoList());
			staffService.update(staff);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UPDATE_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, staff.getFullName());
			result = "manageStaff";
		} catch (SystemException ex) {
			handelSysException(ex);
		}

		return result;
	}

	public String newStaff() {
		staff = new Staff();

		return "manageStaff";
	}

	public void prepareUpdateStaff(Staff staff) {
		this.staff = staff;
		loadStaff();
	}

	public void deleteStaff(Staff staff) {
		try {
			staffService.delete(staff);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, staff.getName().getFullName());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		list = staffService.findAll();
	}

	public IdType[] getIdTypeSelectItemList() {
		return IdType.values();
	}

	public IdConditionType[] getIdConditionTypeSelectItemList() {
		return IdConditionType.values();
	}

	public void changeStaffIdType(AjaxBehaviorEvent e) {
		staff.setFullIdNo(null);
		staff.setIdNo(null);
		staff.setStateCode(null);
		staff.setTownshipCode(null);
		staff.setIdConditionType(null);
		PrimeFaces.current().resetInputs("staffForm:staffIdNoPanelGrid");
	}

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCode(staff.getStateCode());
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("personalInfo".equals(event.getOldStep()) && "generalInfo".equals(event.getNewStep())) {
			loadStaffFullIdNo();
			// FIXME existing staff in service
			// boolean isExistingStaff = staffService.checkExistingStaff(staff);
			if (staff.getDateOfBirth().after(new Date())) {
				addErrorMessage("staffEntryForm:dob", MessageId.INVALID_CUSTOMER_DOB);
				valid = false;
			}
			// if (isExistingStaff) {
			// valid = false;
			// addInfoMessage(null, MessageId.EXISTING_STAFF,
			// staff.getFullName());
			// PrimeFaces.current().ajax().update("staffEntryForm:growl");
			// }

		} else if ("generalInfo".equals(event.getOldStep()) && "personalInfo".equals(event.getNewStep())) {
			if (staff.getStateCode() != null)
				changeStateCode();
		}

		return valid ? event.getNewStep() : event.getOldStep();
	}

	private void loadStaffFullIdNo() {
		String fullIdNo = null;
		IdType idType = staff.getIdType();
		if (idType.equals(IdType.NRCNO)) {
			fullIdNo = staff.getStateCode().getCodeNo() + "/" + staff.getTownshipCode().getTownshipcodeno() + "(" + staff.getIdConditionType() + ")" + staff.getIdNo();
		} else if (idType.equals(IdType.FRCNO) || idType.equals(IdType.PASSPORTNO)) {
			fullIdNo = staff.getIdNo();
		}
		staff.setFullIdNo(fullIdNo);
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public List<GGIOrganization> getGgiOrganizationList() {
		return ggiOrganizationList;
	}

	public void setGgiOrganizationList(List<GGIOrganization> ggiOrganizationList) {
		this.ggiOrganizationList = ggiOrganizationList;
	}

	public IStaffService getStaffService() {
		return staffService;
	}

	public void setStaffService(IStaffService staffService) {
		this.staffService = staffService;
	}

	public IGGIOrganizationService getOrganizationService() {
		return organizationService;
	}

	public void setOrganizationService(IGGIOrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	public List<Staff> getList() {
		return list;
	}

	public void setList(List<Staff> list) {
		this.list = list;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public void setStateCodeList(List<StateCode> stateCodeList) {
		this.stateCodeList = stateCodeList;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public void setTownshipCodeList(List<TownshipCode> townshipCodeList) {
		this.townshipCodeList = townshipCodeList;
	}

	public ITownshipCodeService getTownshipCodeService() {
		return townshipCodeService;
	}

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
	}

	public IStateCodeService getStateCodeService() {
		return stateCodeService;
	}

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
	}

	public List<Staff> getFilterStaffList() {
		return filterStaffList;
	}

	public void setFilterStaffList(List<Staff> filterStaffList) {
		this.filterStaffList = filterStaffList;
	}

	public MaritalStatus[] getMaritalStatusSelectItemList() {
		return MaritalStatus.values();
	}

	public Gender[] getGenderSelectItemList() {
		return Gender.values();
	}

	// public FamilyInfoDTO getFamilyInfoDTO() {
	// return familyInfoDTO;
	// }
	//
	// public void setFamilyInfoDTO(FamilyInfoDTO familyInfoDTO) {
	// this.familyInfoDTO = familyInfoDTO;
	// }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<RelationShip> getRelationShipList() {
		return relationShipList;
	}

	public void setRelationShipList(List<RelationShip> relationShipList) {
		this.relationShipList = relationShipList;
	}

	public void returnNationality(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		staff.setCountry(nationality);
	}

	public void returnReligion(SelectEvent event) {
		Religion religion = (Religion) event.getObject();
		staff.setReligion(religion);
	}

	public void returnQualification(SelectEvent event) {
		Qualification qualification = (Qualification) event.getObject();
		staff.setQualification(qualification);
	}

	public void returnBankBranch(SelectEvent event) {
		BankBranch bankBranch = (BankBranch) event.getObject();
		staff.setBankBranch(bankBranch);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		staff.setBranch(branch);
		staff.setSalePoint(null);
	}

	public void returnIndustry(SelectEvent event) {
		Industry industry = (Industry) event.getObject();
		staff.setIndustry(industry);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		staff.setOccupation(occupation);
	}

	public void returnResidentTownship(SelectEvent event) {
		Township residentTownship = (Township) event.getObject();
		staff.getResidentAddress().setTownship(residentTownship);
	}

	public void returnPermanentTownship(SelectEvent event) {
		Township permanentTownship = (Township) event.getObject();
		staff.getPermanentAddress().setTownship(permanentTownship);
	}

	public void returnOfficeTownship(SelectEvent event) {
		Township officeTownship = (Township) event.getObject();
		staff.getOfficeAddress().setTownship(officeTownship);
	}

	// public void returnFamilyOccupation(SelectEvent event) {
	// Occupation familyOccupation = (Occupation) event.getObject();
	// familyInfoDTO.setOccupation(familyOccupation);
	// }
	//
	// public void returnFamilyIndustry(SelectEvent event) {
	// Industry familyIndustry = (Industry) event.getObject();
	// familyInfoDTO.setIndustry(familyIndustry);
	// }

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (entitys != null && !entity.getId().equals(entitys.getId())) {
			staff.setBranch(null);
			staff.setSalePoint(null);
		}
		this.entitys = entity;

	}

	public void removeEntity() {
		this.entitys = null;
		staff.setBranch(null);
		staff.setSalePoint(null);
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void removeBranch() {
		staff.setBranch(null);
		staff.setSalePoint(null);

	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		staff.setSalePoint(salePoint);
	}

	public void selectSalePoint() {

		selectSalePointByBranch(staff.getBranch() == null ? "" : staff.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	// public void changeFamilyIdType(AjaxBehaviorEvent e) {
	// if (IdType.NRCNO.equals(familyInfoDTO.getIdType())) {
	// familyInfoDTO.setIdNo(null);
	// familyInfoDTO.setFullIdNo(null);
	// } else {
	// familyInfoDTO.setIdNo(null);
	// familyInfoDTO.setStateCode(null);
	// familyInfoDTO.setTownshipCode(null);
	// familyInfoDTO.setIdConditionType(null);
	// }
	// }

	// public void changeStateCodeForFamilyInfo() {
	// townshipCodeList =
	// townshipCodeService.findByStateCode(familyInfoDTO.getStateCode());
	// }

	public boolean isCreateNewFamilyInfo() {
		return createNewFamilyInfo;
	}

	// private void createNewFamilyInfo() {
	// createNewFamilyInfo = true;
	// familyInfoDTO = new FamilyInfoDTO();
	// familyInfoDTO.setIdType(IdType.NRCNO);
	// }

	// public void prepareAddNewFamilyInfo() {
	// createNewFamilyInfo();
	// }
	//
	// public void prepareEditFamilyInfo(FamilyInfoDTO familyInfoDTO) {
	// this.familyInfoDTO = familyInfoDTO;
	// if (familyInfoDTO.getStateCode() != null)
	// changeStateCodeForFamilyInfo();
	// this.createNewFamilyInfo = false;
	// }
	//
	// public void saveFamilyInfo() {
	//
	// familyInfoDTO.setFullIdNo();
	// familyInfoDTOMap.put(familyInfoDTO.getTempId(), familyInfoDTO);
	// createNewFamilyInfo();
	//
	// }

	// public void removeFamilyInfo(FamilyInfoDTO familyInfoDTO) {
	// familyInfoDTOMap.remove(familyInfoDTO.getTempId());
	// }
	//
	// public List<FamilyInfo> getFamilyInfoList() {
	// List<FamilyInfo> result = new ArrayList<FamilyInfo>();
	// if (familyInfoDTOMap.values() != null) {
	// for (FamilyInfoDTO familyInfoDTO : familyInfoDTOMap.values()) {
	// result.add(new FamilyInfo(familyInfoDTO.getInitialId(),
	// familyInfoDTO.getFullIdNo(), familyInfoDTO.getIdType(),
	// familyInfoDTO.getDateOfBirth(),
	// familyInfoDTO.getName(), familyInfoDTO.getRelationShip(),
	// familyInfoDTO.getIndustry(), familyInfoDTO.getOccupation()));
	// }
	// }
	// return result;
	// }
	//
	// public List<FamilyInfoDTO> getFamilyInfoDTOList() {
	// if (familyInfoDTOMap.values() != null) {
	// return new ArrayList<FamilyInfoDTO>(familyInfoDTOMap.values());
	// }
	// return new ArrayList<FamilyInfoDTO>();
	// }

	public boolean isCreateNew() {
		return createNew;
	}

}
