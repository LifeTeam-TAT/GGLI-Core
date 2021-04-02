package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.system.common.bankCharges.service.interfaces.IBankChargesService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "BankWalletDialogActionBean")
@ViewScoped
public class BankWalletDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{BankChargesService}")
	private IBankChargesService bankChargesService;

	public void setBankChargesService(IBankChargesService bankChargesService) {
		this.bankChargesService = bankChargesService;
	}

	private List<BankCharges> bankChargesList;

	@PostConstruct
	public void init() {
		bankChargesList = bankChargesService.findAllBankCharges();

	}

	public List<BankCharges> getBankChargesList() {
		return bankChargesList;
	}

	public void setBankChargesList(List<BankCharges> bankChargesList) {
		this.bankChargesList = bankChargesList;
	}

	public void selectBankCharges(BankCharges bankCharges) {
		BankCharges selectBankCharges = bankChargesService.findByBankChargesId(bankCharges.getId());
		PrimeFaces.current().dialog().closeDynamic(selectBankCharges);

	}
}
