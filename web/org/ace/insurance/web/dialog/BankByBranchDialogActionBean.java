package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.filter.bankCustomer.BANKCUSTOMER001;
import org.ace.insurance.filter.bankCustomer.interfaces.IBANKCUSTOMER_Filter;
import org.ace.insurance.filter.cirteria.BANKCUST001;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.user.User;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "BankByBranchDialogActionBean")
@ViewScoped
public class BankByBranchDialogActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BankService}")
	private IBankService bankService;

	public void setBankService(IBankService bankService) {
		this.bankService = bankService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@ManagedProperty(value = "#{BANKCUSTOMER_Filter}")
	protected IBANKCUSTOMER_Filter filter;
	private BANKCUST001 customerCriteria;
	private String criteriaValue;
	private List<BANKCUSTOMER001> bankList;
	private User user;

	@PostConstruct
	public void init() {
		criteriaValue = "";
		// bankList = bankService.findAllBank();
		loadBankByBranch();
	}

	private void loadBankByBranch() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		Branch branch = branchService.findBranchById(user.getBranch().getId());
		Bank bank = bankService.findBybranchId(branch.getId());
		bankList = new ArrayList<>();
		BANKCUSTOMER001 bankCustomer001 = new BANKCUSTOMER001(bank);
		this.bankList.add(bankCustomer001);

	}

	public List<BANKCUSTOMER001> getBankList() {
		return bankList;
	}

	public void selectBank(String id) {
		Bank bank = bankService.findBankById(id);
		PrimeFaces.current().dialog().closeDynamic(bank);
	}

	public BANKCUST001[] getCriteriaItems() {
		return BANKCUST001.values();
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public void setFilter(IBANKCUSTOMER_Filter filter) {
		this.filter = filter;
	}

	public BANKCUST001 getCustomerCriteria() {
		return customerCriteria;
	}

	public void setCustomerCriteria(BANKCUST001 customerCriteria) {
		this.customerCriteria = customerCriteria;
	}

	public void search() {
		bankList = filter.find(customerCriteria, criteriaValue);
	}

}
