package org.ace.insurance.web.manage.claim.disabilityPart;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.claim.disabilityPart.service.interfaces.IDisabilityPartService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageDisabilityPartActionBean")
public class ManageDisabilityPartActionBean extends BaseBean {

	@ManagedProperty(value = "#{DisabilityPartService}")
	private IDisabilityPartService disabilityPartService;

	public void setDisabilityPartService(IDisabilityPartService disabilityPartService) {
		this.disabilityPartService = disabilityPartService;
	}

	private boolean createNew;
	private DisabilityPart disabilityPart;
	private List<DisabilityPart> disabilityPartList;

	@PostConstruct
	public void init() {
		createNewDisabilityPart();
		loadDisabilityPart();
	}

	public void createNewDisabilityPart() {
		createNew = true;
		disabilityPart = new DisabilityPart();
	}

	public void loadDisabilityPart() {
		disabilityPartList = disabilityPartService.findAllDisabilityPart();
	}

	public void prepareUpdateDisabilityPart(DisabilityPart disabilityPart) {
		this.disabilityPart = disabilityPart;
		createNew = false;
	}

	public void addNewDisabilityPart() {
		try {
			disabilityPartService.addNewDisabilityPart(disabilityPart);
			disabilityPartList.add(disabilityPart);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, disabilityPart.getName());
			createNewDisabilityPart();
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void updateDisabilityPart() {
		try {
			disabilityPartService.updateDisabilityPart(disabilityPart);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, disabilityPart.getName());
			createNewDisabilityPart();
			loadDisabilityPart();
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public String deleteDisabilityPart(DisabilityPart disabilityPart) {
		try {
			disabilityPartService.deleteDisabilityPart(disabilityPart);
			disabilityPartList.remove(disabilityPart);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, disabilityPart.getName());
			createNewDisabilityPart();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public DisabilityPart getDisabilityPart() {
		return disabilityPart;
	}

	public void setDisabilityPart(DisabilityPart disabilityPart) {
		this.disabilityPart = disabilityPart;
	}

	public List<DisabilityPart> getDisabilityPartList() {
		return disabilityPartList;
	}

}
