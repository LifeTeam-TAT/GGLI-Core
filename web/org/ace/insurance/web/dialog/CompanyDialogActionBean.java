package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.company.Company;
import org.ace.insurance.system.common.company.Company001;
import org.ace.insurance.system.common.company.service.interfaces.ICompanyService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "CompanyDialogActionBean")
@ViewScoped
public class CompanyDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CompanyService}")
	private ICompanyService companyService;

	public void setCompanyService(ICompanyService companyService) {
		this.companyService = companyService;
	}

	private List<Company001> companyList;

	@PostConstruct
	public void init() {
		companyList = companyService.findAll();
	}

	public List<Company001> getCompanyList() {
		return companyList;
	}

	public void selectCompany(Company001 company001) {
		Company company = companyService.findCompanyById(company001.getId());
		PrimeFaces.current().dialog().closeDynamic(company);
	}

}
