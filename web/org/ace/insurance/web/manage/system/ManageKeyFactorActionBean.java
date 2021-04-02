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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.KeyFactorType;
import org.ace.insurance.product.ReferenceItem;
import org.ace.insurance.product.service.interfaces.IKeyFactorConfigLoader;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.keyfactor.service.interfaces.IKeyFactorService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageKeyFactorActionBean")
public class ManageKeyFactorActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{KeyFactorService}")
	private IKeyFactorService keyFactorService;

	public void setKeyFactorService(IKeyFactorService keyFactorService) {
		this.keyFactorService = keyFactorService;
	}

	@ManagedProperty(value = "#{KeyFactorConfigLoader}")
	private IKeyFactorConfigLoader keyFactorConfigLoader;

	public void setKeyFactorConfigLoader(IKeyFactorConfigLoader keyFactorConfigLoader) {
		this.keyFactorConfigLoader = keyFactorConfigLoader;
	}

	private boolean createNew;
	private KeyFactor keyFactor;
	private boolean showReference;

	@PostConstruct
	public void init() {
		createNewKeyFactor();
	}

	public List<SelectItem> getReferenceItemList() {
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		List<ReferenceItem> refItemList = keyFactorConfigLoader.getReferenceItemList();
		for (ReferenceItem ri : refItemList) {
			itemList.add(new SelectItem(ri.getId(), ri.getName()));
		}
		return itemList;
	}

	public KeyFactorType[] getKeyFactorValueSelectItemList() {
		return KeyFactorType.values();
	}

	public void createNewKeyFactor() {
		createNew = true;
		showReference = true;
		keyFactor = new KeyFactor();
	}

	public void prepareUpdateKeyFactor(KeyFactor keyFactor) {
		createNew = false;
		this.keyFactor = keyFactor;
	}

	public void addNewKeyFactor() {
		try {
			keyFactorService.addNewKeyFactor(keyFactor);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, keyFactor.getValue());
			createNewKeyFactor();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateKeyFactor() {
		try {
			keyFactorService.updateKeyFactor(keyFactor);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, keyFactor.getValue());
			createNewKeyFactor();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deleteKeyFactor(KeyFactor keyFactor) {
		try {
			keyFactorService.deleteKeyFactor(keyFactor);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, keyFactor.getValue());
			createNewKeyFactor();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public boolean isShowReference() {
		return showReference;
	}

	public void changeKeyValue(AjaxBehaviorEvent e) {
		KeyFactorType type = keyFactor.getKeyFactorType();

	}

	public List<KeyFactor> getKeyFactorList() {
		return keyFactorService.findAllKeyFactor();
	}

	public KeyFactor getKeyFactor() {
		return keyFactor;
	}

}
