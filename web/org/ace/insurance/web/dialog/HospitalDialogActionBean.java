package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.system.common.hospital.Hospital001;
import org.ace.insurance.system.common.hospital.service.interfaces.IHospitalService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "HospitalDialogActionBean")
@ViewScoped
public class HospitalDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{HospitalService}")
	private IHospitalService hospitalService;

	public void setHospitalService(IHospitalService hospitalService) {
		this.hospitalService = hospitalService;
	}

	private List<Hospital001> hospitalList;
	private String criteria;

	@PostConstruct
	public void init() {
		hospitalList = hospitalService.findAllHospital001();
	}

	public List<Hospital001> getHospitalList() {
		return hospitalList;
	}

	public void selectHospital(Hospital001 hospital001) {
		Hospital hospital = hospitalService.findHospitalById(hospital001.getId());
		PrimeFaces.current().dialog().closeDynamic(hospital);
	}

	public void search() {
		hospitalList = hospitalService.findByCriteria001(criteria);
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public void setHospitalList(List<Hospital001> hospitalList) {
		this.hospitalList = hospitalList;
	}

}
