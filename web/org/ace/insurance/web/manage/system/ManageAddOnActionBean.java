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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.AddOnType;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.addon.service.interfaces.IAddOnService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageAddOnActionBean")
public class ManageAddOnActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean createNew;
	@ManagedProperty(value = "#{AddOnService}")
	private IAddOnService addOnService;

	public void setAddOnService(IAddOnService addOnService) {
		this.addOnService = addOnService;
	}

	private AddOn addOn;
	private List<AddOn> addOnList;

	@PostConstruct
	public void init() {
		createNew = true;
		addOn = new AddOn();
		loadAddOn();
	}

	private void loadAddOn() {
		addOnList = addOnService.findAllAddOn();
	}

	public AddOnType[] getAddOnTypeSelectItemList() {
		return AddOnType.values();
	}

	public void createNewAddOn() {
		createNew = true;
		addOn = new AddOn();

	}

	public void prepareUpdateAddOn(AddOn addOn) {
		createNew = false;
		this.addOn = addOn;
	}

	public void addNewAddOn() {
		try {
			if (addOn.getAddOnType() == null) {
				addInfoMessage(null, MessageId.REQUIRED_ADDONTYPE);
				return;
			}
			addOnService.addNewAddOn(addOn);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, addOn.getName());
			createNewAddOn();
			loadAddOn();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateAddOn() {
		try {
			addOnService.updateAddOn(addOn);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, addOn.getName());
			createNewAddOn();
			loadAddOn();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deleteAddOn(AddOn addOn) {
		try {
			addOnService.deleteAddOn(addOn);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, addOn.getName());
			createNewAddOn();
			loadAddOn();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public AddOn getAddOn() {
		return addOn;
	}

	public List<AddOn> getAddOnList() {
		return addOnList;
	}

}
