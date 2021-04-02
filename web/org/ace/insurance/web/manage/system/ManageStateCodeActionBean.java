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
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;

@ViewScoped
@ManagedBean(name = "ManageStateCodeActionBean")
public class ManageStateCodeActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
	}

	private StateCode stateCode;
	private boolean createNew;
	private List<StateCode> stateCodeList;

	@PostConstruct
	public void init() {
		createNew = true;
		stateCode = new StateCode();
		loadStateCode();
	}

	public void loadStateCode() {
		stateCodeList = stateCodeService.findAllStateCode();
	}

	public void createNewStateCode() {
		createNew = true;
		stateCode = new StateCode();
		PrimeFaces.current().resetInputs(":stateCodeEntryForm");
	}

	public void prepareUpdateStateCode(StateCode stateCode) {
		createNew = false;
		this.stateCode = stateCode;
	}

	public void addNewStateCode() {
		try {
			stateCodeService.addNewStateCode(stateCode);
			stateCodeList.add(stateCode);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, stateCode.getName());
			createNewStateCode();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateStateCode() {
		try {
			stateCodeService.updateStateCode(stateCode);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, stateCode.getName());
			createNewStateCode();
			loadStateCode();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void deleteStateCode(StateCode stateCode) {
		try {
			stateCode = stateCodeService.findStateCodeById(stateCode.getId());
			stateCodeService.deleteStateCode(stateCode);
			stateCodeList.remove(stateCode);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, stateCode.getName());
			createNewStateCode();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public StateCode getStateCode() {
		return stateCode;
	}

}
