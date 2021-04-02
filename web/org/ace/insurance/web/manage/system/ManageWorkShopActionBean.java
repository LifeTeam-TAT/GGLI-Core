package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.workshop.WorkShop;
import org.ace.insurance.system.common.workshop.service.interfaces.IWorkShopService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageWorkShopActionBean")
public class ManageWorkShopActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{WorkShopService}")
	private IWorkShopService workshopService;

	public void setWorkshopService(IWorkShopService workshopService) {
		this.workshopService = workshopService;
	}

	private boolean createNew;
	private WorkShop workshop;
	private List<WorkShop> workshopList;
	private String criteria;

	@PostConstruct
	public void init() {
		prepareCreateNewWorkShop();
		loadWorkShop();
	}

	public void search() {
		workshopList = workshopService.findByCriteria(criteria);
	}

	public void loadWorkShop() {
		workshopList = workshopService.findAll();
	}

	public void prepareCreateNewWorkShop() {
		createNew = true;
		workshop = new WorkShop();
	}

	public void prepareUpdateWorkShop(WorkShop workShop) {
		createNew = false;
		this.workshop = workShop;
	}

	public void createNewWorkShop() {
		try {
			Township township = workshop.getAddress().getTownship();
			if (township == null) {
				addInfoMessage(null, MessageId.REQUIRED_TOWNSHIP);
			} else {
				workshopService.insert(workshop);
				loadWorkShop();
				addInfoMessage(null, MessageId.INSERT_SUCCESS, workshop.getName());
				prepareCreateNewWorkShop();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateWorkShop() {
		try {
			workshopService.update(workshop);
			loadWorkShop();
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, workshop.getName());
			prepareCreateNewWorkShop();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void deleteWorkShop() {
		try {
			workshopService.delete(workshop);
			loadWorkShop();
			addInfoMessage(null, MessageId.DELETE_SUCCESS, workshop.getName());
			prepareCreateNewWorkShop();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public WorkShop getWorkshop() {
		return workshop;
	}

	public void setWorkshop(WorkShop workshop) {
		this.workshop = workshop;
	}

	public List<WorkShop> getWorkshopList() {
		return workshopList;
	}

	public void setWorkshopList(List<WorkShop> workshopList) {
		this.workshopList = workshopList;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		workshop.getAddress().setTownship(township);
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
}
