package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bankBranch.BBRANCH001;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "BankBranchDialogActionBean")
@ViewScoped
public class BankBranchDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BankBranchService}")
	private IBankBranchService bankBranchService;

	public void setBankBranchService(IBankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	private List<BBRANCH001> bankBranchList;

	@PostConstruct
	public void init() {
		bankBranchList = bankBranchService.findAll();
	}

	public List<BBRANCH001> getBankBranchList() {
		return bankBranchList;
	}

	public void selectBankBranch(BBRANCH001 bankBranch001) {
		BankBranch bankBranch = bankBranchService.findBankBranchById(bankBranch001.getId());
		PrimeFaces.current().dialog().closeDynamic(bankBranch);
	}
}
