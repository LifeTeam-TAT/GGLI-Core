package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.SaleManCriteria;
import org.ace.insurance.common.SaleManCriteriaItems;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.saleman.history.SaleManHistory;
import org.ace.insurance.system.common.saleman.service.interfaces.ISaleManService;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageSaleManActionBean")
public class ManageSaleManActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SaleManService}")
	private ISaleManService saleManService;

	public void setSaleManService(ISaleManService saleManService) {
		this.saleManService = saleManService;

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

	private SaleMan saleMan;
	private boolean createNew;
	private List<SaleMan> salemanList;
	private SaleManCriteria saleManCriteria;
	private List<SelectItem> saleManCriteriaItemList;
	private String selectedCriteria;
	private String branchId;
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		saleManCriteria = new SaleManCriteria();
		initializeSaleManCriteria();
		createNew = true;
		saleMan = new SaleMan();
		saleMan.setAddress(new PermanentAddress());
		saleMan.setContentInfo(new ContentInfo());
		saleMan.setIdType(IdType.NRCNO);
		saleMan.setName(new Name());
		loadSaleMan();
		stateCodeList = stateCodeService.findAllStateCode();
	}

	private void initializeSaleManCriteria() {
		saleManCriteriaItemList = new ArrayList<SelectItem>();
		saleManCriteriaItemList.add(new SelectItem(SaleManCriteriaItems.FIRSTNAME, SaleManCriteriaItems.FIRSTNAME.getLabel()));
		saleManCriteriaItemList.add(new SelectItem(SaleManCriteriaItems.LASTNAME, SaleManCriteriaItems.LASTNAME.getLabel()));
		saleManCriteriaItemList.add(new SelectItem(SaleManCriteriaItems.MIDDLENAME, SaleManCriteriaItems.MIDDLENAME.getLabel()));
		saleManCriteriaItemList.add(new SelectItem(SaleManCriteriaItems.FULLNAME, SaleManCriteriaItems.FULLNAME.getLabel()));
		saleManCriteriaItemList.add(new SelectItem(SaleManCriteriaItems.CODENO, SaleManCriteriaItems.CODENO.getLabel()));
	}

	private void loadSaleMan() {
		salemanList = saleManService.findAllSaleMan();
	}

	public void search() {
		saleManCriteria.setSaleManCriteriaItems(null);
		for (SaleManCriteriaItems criteriaItem : SaleManCriteriaItems.values()) {
			if (criteriaItem.toString().equals(selectedCriteria)) {
				saleManCriteria.setSaleManCriteriaItems(criteriaItem);
			}
		}
		salemanList = saleManService.findSaleManByCriteria(saleManCriteria);
	}

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCodeNo(saleMan.getProvinceCode());
	}

	public void createNewSaleMan() {
		createNew = true;
		saleMan = new SaleMan();
		saleMan.setAddress(new PermanentAddress());
		saleMan.setContentInfo(new ContentInfo());
		saleMan.setIdType(IdType.NRCNO);
		saleMan.setName(new Name());
		PrimeFaces.current().resetInputs(":saleManEntryForm");

	}

	public void prepareUpdateSaleMan(SaleMan saleMan) {
		saleMan.loadFullIdNo();
		createNew = false;
		this.saleMan = new SaleMan(saleMan);
		changeStateCode();
	}

	public void addNewSaleMan() {
		try {

			if (saleMan.getDateOfBirth() != null && saleMan.getDateOfBirth().after(new Date())) {
				addErrorMessage("customerEntryForm:dob", MessageId.INVALID_SALEMAN_DOB);
				return;
			}
			if (!isAlreadyExist()) {
				if (validSaleManInfo()) {
					saleMan.setFullIdNO(saleMan.getIdNo());
					saleManService.addNewSaleMan(saleMan);
					addInfoMessage(null, MessageId.INSERT_SUCCESS, saleMan.getName().getFullName());
					createNewSaleMan();
					loadSaleMan();
				}
			} else {
				addInfoMessage(null, MessageId.ALREADY_ADD_SALEMAN, saleMan.getName().getFullName());
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean validSaleManInfo() {
		boolean valid = true;
		if (saleMan.getInitialId().startsWith(" ") || saleMan.getInitialId().endsWith(" ")) {
			addErrorMessage("saleManEntryForm:intialId", MessageId.INVALID_CUSTOMER_NAME);
			valid = false;
		}
		if (saleMan.getName().getFirstName().startsWith(" ") || saleMan.getName().getFirstName().endsWith(" ")) {
			addErrorMessage("saleManEntryForm:firstName", MessageId.INVALID_CUSTOMER_NAME);
			valid = false;
		}
		if (saleMan.getName().getMiddleName().startsWith(" ") || saleMan.getName().getMiddleName().endsWith(" ")) {
			addErrorMessage("saleManEntryForm:middleName", MessageId.INVALID_CUSTOMER_NAME);
			valid = false;
		}
		if (saleMan.getName().getLastName().startsWith(" ") || saleMan.getName().getLastName().endsWith(" ")) {
			addErrorMessage("saleManEntryForm:lastName", MessageId.INVALID_CUSTOMER_NAME);
			valid = false;
		}

		return valid;

	}

	public boolean isAlreadyExist() {
		return saleManService.checkExistingSaleMan(saleMan);
	}

	public void updateSaleMan() {
		try {
			if (saleMan.getDateOfBirth() != null) {
				if (saleMan.getDateOfBirth().after(new Date())) {
					addErrorMessage("customerEntryForm:dob", MessageId.INVALID_SALEMAN_DOB);
					return;
				}
			}
			if (!isAlreadyExist()) {
				if (validSaleManInfo()) {
					saleMan.setFullIdNO(saleMan.getIdNo());
					saleManService.updateSaleMan(saleMan);
					addInfoMessage(null, MessageId.UPDATE_SUCCESS, saleMan.getName().getFullName());
					createNewSaleMan();
					loadSaleMan();
				}
			} else {
				addInfoMessage(null, MessageId.ALREADY_ADD_SALEMAN, saleMan.getName().getFullName());
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void deleteSaleMan(SaleMan saleMan) {
		try {
			saleManService.deleteSaleMan(saleMan);
			SaleManHistory salemanhistory = new SaleManHistory(saleMan);

			saleManService.addNewSaleManHistory(salemanhistory);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, saleMan.getName().getFullName());
			createNewSaleMan();
			loadSaleMan();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void idTypeChange() {
		if (saleMan.getIdType().equals(IdType.NRCNO)) {
			saleMan.setIdType(IdType.NRCNO);
		} else if (saleMan.getIdType().equals(IdType.STILL_APPLYING)) {
			saleMan.setIdNo(null);
			saleMan.setIdType(IdType.STILL_APPLYING);
		} else if (saleMan.getIdType().equals(IdType.PASSPORTNO)) {
			saleMan.setIdType(IdType.PASSPORTNO);
		} else if (saleMan.getIdType().equals(IdType.FRCNO)) {
			saleMan.setIdType(IdType.FRCNO);
		}
	}

	public List<SaleMan> getSaleManList() {
		return salemanList;
	}

	public IdType[] IdTypeSelectItemList() {
		return IdType.values();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		saleMan.getAddress().setTownship(township);
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		saleMan.setEntity(entity);
		saleMan.setBranch(null);
		saleMan.setSalePoint(null);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		branchId = branch.getId();
		saleMan.setBranch(branch);
		saleMan.setSalePoint(null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		saleMan.setSalePoint(salePoint);
	}

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(saleMan.getEntity() == null ? "" : saleMan.getEntity().getId(), user.getBranch().getId());
	}

	public void selectSalePoint() {

		selectSalePointByBranch(saleMan.getBranch() == null ? "" : saleMan.getBranch().getId());
	}

	public void removeEntity() {
		saleMan.setEntity(null);
		saleMan.setBranch(null);
		saleMan.setSalePoint(null);
	}

	public void removeBranch() {
		saleMan.setBranch(null);
		saleMan.setSalePoint(null);

	}

	public void removeTownship() {
		saleMan.setAddress(null);
	}

	public List<SaleMan> getSalemanList() {
		return salemanList;
	}

	public void setSalemanList(List<SaleMan> salemanList) {
		this.salemanList = salemanList;
	}

	public SaleManCriteria getSaleManCriteria() {
		return saleManCriteria;
	}

	public void setSaleManCriteria(SaleManCriteria criteria) {
		this.saleManCriteria = criteria;
	}

	public List<SelectItem> getSaleManCriteriaItemList() {
		return saleManCriteriaItemList;
	}

	public void setSaleManCriteriaItemList(List<SelectItem> saleManCriteriaItemList) {
		this.saleManCriteriaItemList = saleManCriteriaItemList;
	}

	public String getSelectedCriteria() {
		return selectedCriteria;
	}

	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public void setTownshipCodeList(List<TownshipCode> townshipCodeList) {
		this.townshipCodeList = townshipCodeList;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public void setStateCodeList(List<StateCode> stateCodeList) {
		this.stateCodeList = stateCodeList;
	}

	public IdConditionType[] getIdConditionType() {
		return IdConditionType.values();
	}

	public void changeInsuredPersonIdType() {
		saleMan.setProvinceCode("");
		saleMan.setTownshipCode("");
		saleMan.setIdConditionType("");
		saleMan.setIdNo("");
	}

}
