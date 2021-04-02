/***************************************************************************************
 * @author HS
 * @Date 2019-01-22
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.country.COUNTRY001;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.country.service.interfaces.ICountryService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;

@ViewScoped
@ManagedBean(name = "ManageCountryActionBean")
public class ManageCountryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CountryService}")
	private ICountryService countryService;

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
	}

	private Country country;
	private boolean createNew;
	private List<COUNTRY001> countryList;

	@PostConstruct
	public void init() {
		createNew = true;
		country = new Country();
		loadCountry();
	}

	public void loadCountry() {
		countryList = countryService.findCountry();
	}

	public void createNewCountry() {
		createNew = true;
		country = new Country();
		PrimeFaces.current().resetInputs(":countryEntryForm");
	}

	public void prepareUpdateCountry(COUNTRY001 country001) {
		createNew = false;
		this.country = countryService.findCountryById(country001.getId());
	}

	public void addNewCountry() {
		try {
			countryService.addNewCountry(country);
			countryList.add(new COUNTRY001(country));
			addInfoMessage(null, MessageId.INSERT_SUCCESS, country.getName());
			createNewCountry();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateCountry() {
		try {
			countryService.updateCountry(country);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, country.getName());
			createNewCountry();
			loadCountry();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void deleteCountry(COUNTRY001 country001) {
		try {
			country = countryService.findCountryById(country001.getId());
			countryService.deleteCountry(country);
			countryList.remove(country001);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, country.getName());
			createNewCountry();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<COUNTRY001> getCountryList() {
		return countryList;
	}

	public Country getCountry() {
		return country;
	}

}
