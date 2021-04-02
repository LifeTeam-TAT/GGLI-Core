package org.ace.insurance.web.manage.system;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.system.common.bankCharges.service.interfaces.IBankChargesService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageBankChargesActionBean")
public class ManageBankChargesActionBean extends BaseBean {

	@ManagedProperty(value = "#{BankChargesService}")
	private IBankChargesService bankChargesService;

	public void setBankChargesService(IBankChargesService bankChargesService) {
		this.bankChargesService = bankChargesService;
	}

	private boolean createNew;
	private BankCharges bankCharges;
	private List<BankCharges> bankChargesInfoList;
	private List<BankCharges> filterBankChargesInfoList;

	@PostConstruct
	public void init() {
		createNewBankChargesInfo();
		loadBankChargesInfo();
	}

	public void loadBankChargesInfo() {
		bankChargesInfoList = bankChargesService.findAllBankCharges();
	}

	public void createNewBankChargesInfo() {
		createNew = true;
		bankCharges = new BankCharges();
		// PrimeFaces.current().resetInputs(":branchEntryForm");
	}

	public void prepareUpdateBankCharges(BankCharges bankCharges) {
		createNew = false;
		this.bankCharges = bankCharges;
	}

	public void addNewBankCharges() {
		try {
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			bankCharges.setStatus(true);
			/*
			 * if (bankCharges.getLicenseExpireDate().compareTo(today) < 0) {
			 * bankCharges.setStatus(bancaRefferal.getStatus().INACTIVE); }
			 */
			bankChargesService.insert(bankCharges);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, bankCharges.getName());
			createNewBankChargesInfo();
			loadBankChargesInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBankChargesInfo();
	}

	public void updateBankCharges() {
		try {
			bankChargesService.update(bankCharges);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, bankCharges.getName());
			createNewBankChargesInfo();
			loadBankChargesInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBankChargesInfo();
	}

	public void deActivateBankCharges(BankCharges bankCharges) {
		try {
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			bankCharges.setEnddate(today);
			bankCharges.setStatus(false);
			bankChargesService.update(bankCharges);
			addInfoMessage(null, MessageId.DEACTIVATE_SUCCESS, bankCharges.getName());
			createNewBankChargesInfo();
			loadBankChargesInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBankChargesInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public BankCharges getBankCharges() {
		return bankCharges;
	}

	public void setBankCharges(BankCharges bankCharges) {
		this.bankCharges = bankCharges;
	}

	public List<BankCharges> getBankChargesInfoList() {
		return bankChargesInfoList;
	}

	public void setBankChargesInfoList(List<BankCharges> bankChargesInfoList) {
		this.bankChargesInfoList = bankChargesInfoList;
	}

	public List<BankCharges> getFilterBankChargesInfoList() {
		return filterBankChargesInfoList;
	}

	public void setFilterBankChargesInfoList(List<BankCharges> filterBankChargesInfoList) {
		this.filterBankChargesInfoList = filterBankChargesInfoList;
	}

	public TypesOfCharges[] getTypesOfCharges() {
		return TypesOfCharges.values();
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		bankCharges.setBank(bank);
	}

	/*
	 * public BancaStatus[] getBancaStatus() { return BancaStatus.values(); }
	 */

}
