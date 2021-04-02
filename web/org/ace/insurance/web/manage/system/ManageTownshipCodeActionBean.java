/***************************************************************************************
 * @author HS
 * @Date 2019-01-22
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageTownshipCodeActionBean")
public class ManageTownshipCodeActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
	}

	private TownshipCode townshipCode;
	private boolean createNew;
	private List<TownshipCode> townshipCodeList;

	@PostConstruct
	public void init() {
		createNew = true;
		townshipCode = new TownshipCode();
		loadTownshipCode();
	}

	public void loadTownshipCode() {
		townshipCodeList = townshipCodeService.findAllTownshipCode();
	}

	public void createNewTownshipCode() {
		createNew = true;
		townshipCode = new TownshipCode();
		PrimeFaces.current().resetInputs(":townshipCodeEntryForm");
	}

	public void prepareUpdateTownshipCode(TownshipCode townshipCode) {
		createNew = false;
		this.townshipCode = townshipCode;
	}

	public void addNewTownshipCode() {
		try {
			townshipCodeService.addNewTownshipCode(townshipCode);
			townshipCodeList.add(townshipCode);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, townshipCode.getName());
			createNewTownshipCode();
			loadTownshipCode();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateTownshipCode() {
		try {
			townshipCodeService.updateTownshipCode(townshipCode);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, townshipCode.getName());
			createNewTownshipCode();
			loadTownshipCode();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void deleteTownshipCode(TownshipCode townshipCode) {
		try {
			townshipCode = townshipCodeService.findTownshipCodeById(townshipCode.getId());
			townshipCodeService.deleteTownshipCode(townshipCode);
			townshipCodeList.remove(townshipCode);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, townshipCode.getName());
			createNewTownshipCode();
			loadTownshipCode();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public TownshipCode getTownshipCode() {
		return townshipCode;
	}

	public void returnStateCode(SelectEvent event) {
		StateCode stateCode = (StateCode) event.getObject();
		townshipCode.setStateCode(stateCode);
	}

}
