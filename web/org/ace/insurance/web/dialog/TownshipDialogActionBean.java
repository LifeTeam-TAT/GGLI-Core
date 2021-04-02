package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.Township001;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "TownshipDialogActionBean")
@ViewScoped
public class TownshipDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	private List<Township001> townshipList;

	@PostConstruct
	public void init() {
		townshipList = townshipService.findAll();
	}

	public void selectTownship(Township001 tsp001) {
		Township township = townshipService.findTownshipById(tsp001.getId());
		PrimeFaces.current().dialog().closeDynamic(township);
	}

	public List<Township001> getTownshipList() {
		return townshipList;
	}

}
