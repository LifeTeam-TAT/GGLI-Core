package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.occupation.OCCUPATION001;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "OccupationDialogActionBean")
@ViewScoped
public class OccupationDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{OccupationService}")
	private IOccupationService occupationService;

	public void setOccupationService(IOccupationService occupationService) {
		this.occupationService = occupationService;
	}

	private List<OCCUPATION001> occupationList;

	@PostConstruct
	public void init() {
		occupationList = occupationService.findOccupation();
	}

	public List<OCCUPATION001> getOccupationList() {
		return occupationList;
	}

	public void selectOccupation(OCCUPATION001 occupation001) {
		Occupation occupation = occupationService.findOccupationById(occupation001.getId());
		PrimeFaces.current().dialog().closeDynamic(occupation);
	}

}
