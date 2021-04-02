package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.coinsurancecompany.COINCOMPANY001;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.coinsurancecompany.service.interfaces.ICoinsuranceCompanyService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "CoInsuranceCompanyDialogActionBean")
@ViewScoped
public class CoInsuranceCompanyDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{CoinsuranceCompanyService}")
	private ICoinsuranceCompanyService coinsuranceCompanyService;

	public void setCoinsuranceCompanyService(ICoinsuranceCompanyService coinsuranceCompanyService) {
		this.coinsuranceCompanyService = coinsuranceCompanyService;
	}

	private List<COINCOMPANY001> coinsuranceCompanyList;

	@PostConstruct
	public void init() {
		coinsuranceCompanyList = coinsuranceCompanyService.findAllCOINCOMPANY();
	}

	public void selectCoinsuranceCompany(COINCOMPANY001 coinsuranceCompany001) {
		CoinsuranceCompany coinsuranceCompany = coinsuranceCompanyService.findById(coinsuranceCompany001.getId());
		PrimeFaces.current().dialog().closeDynamic(coinsuranceCompany);
	}

	public List<COINCOMPANY001> getCoinsuranceCompanyList() {
		return coinsuranceCompanyList;
	}

}
