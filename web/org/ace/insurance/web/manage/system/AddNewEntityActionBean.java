package org.ace.insurance.web.manage.system;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.entitys.service.interfaces.IEntityService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "AddNewEntityActionBean")
public class AddNewEntityActionBean extends BaseBean {

	@ManagedProperty(value = "#{EnityService}")
	private IEntityService entityService;

	public void setEntityService(IEntityService entityService) {
		this.entityService = entityService;
	}

	private Entitys entitys;
	private boolean createNew;
	private List<Entitys> entityInfoList;
	private List<Entitys> filterEntityInfoList;

	@PostConstruct
	public void init() {
		createNewEntityInfo();
		loadEntityInfo();
	}

	public void loadEntityInfo() {
		entityInfoList = entityService.findAllEntitys();
	}

	public void createNewEntityInfo() {
		createNew = true;
		entitys = new Entitys();
	}

	public void prepareUpdateEntity(Entitys entitys) {
		createNew = false;
		this.entitys = entitys;
	}

	public void addNewEntity() {
		try {
			entityService.insert(entitys);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, entitys.getName());
			createNewEntityInfo();
			loadEntityInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadEntityInfo();
	}

	public void updateEntity() {
		try {
			entityService.update(entitys);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, entitys.getName());
			createNewEntityInfo();
			loadEntityInfo();

		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadEntityInfo();
	}

	public void deleteEntity(Entitys entitys) {
		try {
			entityService.delete(entitys);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, entitys.getName());
			createNewEntityInfo();
			loadEntityInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadEntityInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

	public List<Entitys> getEntityInfoList() {
		return entityInfoList;
	}

	public void setEntityInfoList(List<Entitys> entityInfoList) {
		this.entityInfoList = entityInfoList;
	}

	public List<Entitys> getFilterEntityInfoList() {
		return filterEntityInfoList;
	}

	public void setFilterEntityInfoList(List<Entitys> filterEntityInfoList) {
		this.filterEntityInfoList = filterEntityInfoList;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

}
