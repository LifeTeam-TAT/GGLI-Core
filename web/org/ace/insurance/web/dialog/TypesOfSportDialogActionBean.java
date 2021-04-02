package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.typesOfSport.TSP001;
import org.ace.insurance.system.common.typesOfSport.TypesOfSport;
import org.ace.insurance.system.common.typesOfSport.service.interfaces.ITypesOfSportService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "TypesOfSportDialogActionBean")
@ViewScoped
public class TypesOfSportDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TypesOfSportService}")
	private ITypesOfSportService typesOfSportService;

	public void setTypesOfSportService(ITypesOfSportService typesOfSportService) {
		this.typesOfSportService = typesOfSportService;
	}

	private List<TSP001> typesOfSportList;

	@PostConstruct
	public void init() {
		typesOfSportList = typesOfSportService.findTypesOfSport();
	}

	public void selectTypesOfSport(TSP001 typesOfSport001) {
		TypesOfSport typesOfSport = typesOfSportService.findTypesOfSportById(typesOfSport001.getId());
		PrimeFaces.current().dialog().closeDynamic(typesOfSport);
	}

	public List<TSP001> getTypesOfSportList() {
		return typesOfSportList;
	}

}
