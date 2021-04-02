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

import org.ace.insurance.system.common.province.Province;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.Township001;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageTownshipActionBean")
public class ManageTownshipActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	private Township township;
	private boolean createNew;
	private List<Township001> townshipList;

	@PostConstruct
	public void init() {
		createNew = true;
		township = new Township();
		loadTownship();
	}

	public void loadTownship() {
		townshipList = townshipService.findAll();
	}

	public void createNewTownship() {
		createNew = true;
		township = new Township();
		PrimeFaces.current().resetInputs(":townshipEntryForm");
	}

	public void prepareUpdateTownship(Township001 tsp002) {
		createNew = false;
		this.township = townshipService.findTownshipById(tsp002.getId());
	}

	public void addNewTownship() {
		try {
			townshipService.addNewTownship(township);
			townshipList.add(new Township001(township));
			addInfoMessage(null, MessageId.INSERT_SUCCESS, township.getName());
			createNewTownship();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateTownship() {
		try {
			townshipService.updateTownship(township);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, township.getName());
			createNewTownship();
			loadTownship();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void deleteTownship(Township001 tsp002) {
		try {
			township = townshipService.findTownshipById(tsp002.getId());
			townshipService.deleteTownship(township);
			townshipList.remove(tsp002);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, township.getName());
			createNewTownship();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<Township001> getTownshipList() {
		return townshipList;
	}

	public Township getTownship() {
		return township;
	}

	public void returnProvince(SelectEvent event) {
		Province province = (Province) event.getObject();
		township.setProvince(province);
	}

}
