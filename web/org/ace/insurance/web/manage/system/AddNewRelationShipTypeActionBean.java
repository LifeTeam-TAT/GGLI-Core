package org.ace.insurance.web.manage.system;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.entitys.service.interfaces.IEntityService;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.service.interfaces.IRelationShipTypeService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "AddNewRelationShipTypeActionBean")
public class AddNewRelationShipTypeActionBean extends BaseBean {

	@ManagedProperty(value = "#{RelationShipTypeService}")
	private IRelationShipTypeService relationShipTypeService;

	public void setRelationShipTypeService(IRelationShipTypeService relationShipTypeService) {
		this.relationShipTypeService = relationShipTypeService;
	}

	private RelationShipType relationshiptype;
	private boolean createNew;
	private List<RelationShipType> relationshiptypeInfoList;
	private List<RelationShipType> filterrelationshiptypeInfoList;

	@PostConstruct
	public void init() {
		createNewRelationshiptypeInfo();
		loadRelationshiptypeInfo();
	}

	public void loadRelationshiptypeInfo() {
		relationshiptypeInfoList = relationShipTypeService.findAllRelationShipType();
	}

	public void createNewRelationshiptypeInfo() {
		createNew = true;
		relationshiptype = new RelationShipType();
	}

	public void prepareUpdaterelationshiptype(RelationShipType relationshiptype) {
		createNew = false;
		this.relationshiptype = relationshiptype;
	}

	public void addNewRelationshiptype() {
		try {
			relationShipTypeService.insert(relationshiptype);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, relationshiptype.getName());
			createNewRelationshiptypeInfo();
			loadRelationshiptypeInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadRelationshiptypeInfo();
	}

	public void updateRelationshiptype() {
		try {
			relationShipTypeService.update(relationshiptype);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, relationshiptype.getName());
			createNewRelationshiptypeInfo();
			loadRelationshiptypeInfo();

		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadRelationshiptypeInfo();
	}

	public void deleteRelationshiptype(RelationShipType relationshiptype) {
		try {
			relationShipTypeService.delete(relationshiptype);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, relationshiptype.getName());
			createNewRelationshiptypeInfo();
			loadRelationshiptypeInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadRelationshiptypeInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	
	public RelationShipType getRelationshiptype() {
		return relationshiptype;
	}

	public void setRelationshiptype(RelationShipType relationshiptype) {
		this.relationshiptype = relationshiptype;
	}

	public List<RelationShipType> getRelationshiptypeInfoList() {
		return relationshiptypeInfoList;
	}

	public void setRelationshiptypeInfoList(List<RelationShipType> relationshiptypeInfoList) {
		this.relationshiptypeInfoList = relationshiptypeInfoList;
	}

	public List<RelationShipType> getFilterrelationshiptypeInfoList() {
		return filterrelationshiptypeInfoList;
	}

	public void setFilterrelationshiptypeInfoList(List<RelationShipType> filterrelationshiptypeInfoList) {
		this.filterrelationshiptypeInfoList = filterrelationshiptypeInfoList;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

}
