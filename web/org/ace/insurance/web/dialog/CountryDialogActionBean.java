package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.country.COUNTRY001;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.country.service.interfaces.ICountryService;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "CountryDialogActionBean")
@ViewScoped
public class CountryDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CountryService}")
	private ICountryService countryService;

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
	}

	private List<COUNTRY001> countryList;

	@PostConstruct
	public void init() {
		countryList = countryService.findCountry();
	}

	public List<COUNTRY001> getCountryList() {
		return countryList;
	}

	public void selectCountry(COUNTRY001 country001) {
		Country country = countryService.findCountryById(country001.getId());
		PrimeFaces.current().dialog().closeDynamic(country);
	}
}
