package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.ace.insurance.system.common.city.CITY001;
import org.ace.insurance.system.common.city.City;
import org.ace.insurance.system.common.city.service.interfaces.ICityService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "CityDialogActionBean")
@ViewScoped
public class CityDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CityService}")
	private ICityService cityService;

	public void setCityService(ICityService cityService) {
		this.cityService = cityService;
	}

	private String criteria;
	private List<CITY001> cityList;

	@PostConstruct
	public void init() {
		cityList = cityService.findCity();
	}

	public void search() {
		cityList = cityService.findByCityCriteria(criteria);
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public List<CITY001> getCityList() {
		return cityList;
	}

	public void selectCity(CITY001 city001) {
		City city = cityService.findCityById(city001.getId());
		PrimeFaces.current().dialog().closeDynamic(city);
	}
}
