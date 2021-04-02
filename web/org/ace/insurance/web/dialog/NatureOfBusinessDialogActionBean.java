package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.buildingOccupation.BuildingOccupation;
import org.ace.insurance.system.common.buildingOccupation.BuildingOccupation001;
import org.ace.insurance.system.common.buildingOccupation.service.interfaces.IBuildingOccupationService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "NatureOfBusinessDialogActionBean")
@ViewScoped
public class NatureOfBusinessDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BuildingOccupationService}")
	private IBuildingOccupationService buildingOccupationService;

	public void setBuildingOccupationService(IBuildingOccupationService buildingOccupationService) {
		this.buildingOccupationService = buildingOccupationService;
	}

	private List<BuildingOccupation001> buildingOccupationList;
	private String searchName;

	@PostConstruct
	public void init() {
		buildingOccupationList = buildingOccupationService.findAllBuildingOccupation001();
	}

	public void search() {
		buildingOccupationList = buildingOccupationService.findByCriteria001(searchName);
	}

	public void selectBuildingOccupation(BuildingOccupation001 busiOcc001) {
		BuildingOccupation busiOcc = buildingOccupationService.findBuildingOccupationById(busiOcc001.getId());
		PrimeFaces.current().dialog().closeDynamic(busiOcc);
	}

	public List<BuildingOccupation001> getBusiOccList() {
		return buildingOccupationList;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public List<BuildingOccupation001> getBuildingOccupationList() {
		return buildingOccupationList;
	}

	public void setBuildingOccupationList(List<BuildingOccupation001> buildingOccupationList) {
		this.buildingOccupationList = buildingOccupationList;
	}
}
