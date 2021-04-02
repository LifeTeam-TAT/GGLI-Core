package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "AccountBankDialogActionBean")
@ViewScoped
public class AccountBankDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BankService}")
	private IBankService bankService;

	public void setProductService(IBankService bankService) {
		this.bankService = bankService;
	}

	private List<Bank> bankList;

	@PostConstruct
	public void init() {
		bankList = bankService.findACodeNotNull();
	}

	public List<Bank> getBankList() {
		return bankList;
	}

	public void selectAccountBank(Bank bank) {
		PrimeFaces.current().dialog().closeDynamic(bank);
	}
}
