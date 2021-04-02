package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bank.Coa;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "CoaDialogActionBean")
@ViewScoped
public class CoaDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{BankService}")
	private IBankService bankService;

	public void setBankService(IBankService bankService) {
		this.bankService = bankService;
	}

	private List<Coa> coaList;

	@PostConstruct
	public void init() {
		coaList = bankService.findAllCOAByAType();
	}

	public void selectCoa(Coa coa) {
		PrimeFaces.current().dialog().closeDynamic(coa);
	}

	public List<Coa> getCoaList() {
		return coaList;
	}
}
