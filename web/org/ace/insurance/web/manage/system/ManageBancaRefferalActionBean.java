package org.ace.insurance.web.manage.system;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.utils.BancaStatus;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.bancaRefferal.service.interfaces.IBancaRefferalService;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageBancaRefferalActionBean")
public class ManageBancaRefferalActionBean extends BaseBean {

	@ManagedProperty(value = "#{BancaRefferalService}")
	private IBancaRefferalService bancarefferalService;

	public void setBancarefferalService(IBancaRefferalService bancarefferalService) {
		this.bancarefferalService = bancarefferalService;
	}

	private boolean createNew;
	private BancaRefferal bancaRefferal;
	private List<BancaRefferal> bancaRefferalInfoList;
	private List<BancaRefferal> filterBancaRefferalInfoList;

	private List<StateCode> stateCodeList;

	private List<TownshipCode> townshipCodeList;

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	@PostConstruct
	public void init() {
		stateCodeList = new ArrayList<StateCode>();
		townshipCodeList = new ArrayList<TownshipCode>();
		createNewBancaRefferalInfo();
		loadBancaRefferalInfo();
		stateCodeList = stateCodeService.findAllStateCode();
	}

	public void loadBancaRefferalInfo() {
		bancaRefferalInfoList = bancarefferalService.findAllBancaRefferal();
	}

	public void createNewBancaRefferalInfo() {
		createNew = true;
		bancaRefferal = new BancaRefferal();
		// PrimeFaces.current().resetInputs(":branchEntryForm");
	}

	public void prepareUpdateBancaRefferal(BancaRefferal bancaRefferal) {
		createNew = false;
		this.bancaRefferal = bancaRefferal;
	}

	public void addNewBancaRefferal() {
		try {
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			if (bancaRefferal.getLicenseExpireDate().compareTo(today) < 0) {
				bancaRefferal.setStatus(bancaRefferal.getStatus().INACTIVE);
			}
			bancarefferalService.insert(bancaRefferal);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, bancaRefferal.getName());
			createNewBancaRefferalInfo();
			loadBancaRefferalInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaRefferalInfo();
	}

	public void updateBancaRefferal() {
		try {
			bancarefferalService.update(bancaRefferal);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, bancaRefferal.getName());
			createNewBancaRefferalInfo();
			loadBancaRefferalInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaRefferalInfo();
	}

	public void deleteBancaRefferal(BancaRefferal bancaRefferal) {
		try {
			bancarefferalService.delete(bancaRefferal);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, bancaRefferal.getName());
			createNewBancaRefferalInfo();
			loadBancaRefferalInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaRefferalInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public BancaRefferal getBancaRefferal() {
		return bancaRefferal;
	}

	public void setBancaRefferal(BancaRefferal bancaRefferal) {
		this.bancaRefferal = bancaRefferal;
	}

	public List<BancaRefferal> getBancaRefferalInfoList() {
		return bancaRefferalInfoList;
	}

	public void setBancaRefferalInfoList(List<BancaRefferal> bancaRefferalInfoList) {
		this.bancaRefferalInfoList = bancaRefferalInfoList;
	}

	public List<BancaRefferal> getFilterBancaRefferalInfoList() {
		return filterBancaRefferalInfoList;
	}

	public void setFilterBancaRefferalInfoList(List<BancaRefferal> filterBancaRefferalInfoList) {
		this.filterBancaRefferalInfoList = filterBancaRefferalInfoList;
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		bancaRefferal.setBancaBranch(bancaBranch);
	}

	public void removeBancaBranch() {
		bancaRefferal.setBancaBranch(null);
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public BancaStatus[] getBancaStatus() {
		return BancaStatus.values();
	}

	public IdType[] getIdTypeSelectItemList() {
		return IdType.values();
	}

	public void changeStaffIdType(AjaxBehaviorEvent e) {
		bancaRefferal.setFullIdNo(null);
		bancaRefferal.setIdNo(null);
		bancaRefferal.setStateCode(null);
		bancaRefferal.setTownshipCode(null);
		bancaRefferal.setIdConditionType(null);
		PrimeFaces.current().resetInputs("bancaEntryForm:bancaRefferalIdNoPanelGrid");
	}

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCode(bancaRefferal.getStateCode());
	}

	public IdConditionType[] getIdConditionTypeSelectItemList() {
		return IdConditionType.values();
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
}
