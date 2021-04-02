package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.buildingOccupation.BuildingOccupation;
import org.ace.insurance.system.common.buildingOccupation.service.interfaces.IBuildingOccupationService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageBuildingOccupationActionBean")
public class ManageBuildingOccupationActionBean extends BaseBean implements Serializable {                                              private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BuildingOccupationService}")
	private IBuildingOccupationService buildingOccupationService;

	public void setBuildingOccupationService(IBuildingOccupationService buildingOccupationService) {
		this.buildingOccupationService = buildingOccupationService;
	}

	private boolean createNew;
	private BuildingOccupation buildingOccupation;
	private String criteria;
	private List<BuildingOccupation> buildingOccupationList;

	@PostConstruct
	public void init() {
		createNewBuildingOccupation();
		loadBuildingOccupation();
	}

	public void search() {
		buildingOccupationList = buildingOccupationService.findByCriteria(criteria);
	}

	public void loadBuildingOccupation() {
		buildingOccupationList = buildingOccupationService.findAllBuildingOccupation();
	}

	public void createNewBuildingOccupation() {
		createNew = true;
		buildingOccupation = new BuildingOccupation();
	}

	public void prepareUpdateBuildingOccupation(BuildingOccupation buildingOccupation) {
		createNew = false;
		this.buildingOccupation = buildingOccupation;
	}

	public void addNewBuildingOccupation() {
		try {
			buildingOccupationService.addNewBuildingOccupation(buildingOccupation);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, buildingOccupation.getName());
			createNewBuildingOccupation();
			loadBuildingOccupation();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateBuildingOccupation() {
		try {
			buildingOccupationService.updateBuildingOccupation(buildingOccupation);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, buildingOccupation.getName());
			createNewBuildingOccupation();
			loadBuildingOccupation();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deleteBuildingOccupation() {
		try {
			buildingOccupationService.deleteBuildingOccupation(buildingOccupation);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, buildingOccupation.getName());
			createNewBuildingOccupation();
			loadBuildingOccupation();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<BuildingOccupation> getBuildingOccupationList() {
		return buildingOccupationList;
	}

	public void setBuildingOccupationList(List<BuildingOccupation> buildingOccupationList) {
		this.buildingOccupationList = buildingOccupationList;
	}

	public BuildingOccupation getBuildingOccupation() {
		return buildingOccupation;
	}

	public void setBuildingOccupation(BuildingOccupation buildingOccupation) {
		this.buildingOccupation = buildingOccupation;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
}
