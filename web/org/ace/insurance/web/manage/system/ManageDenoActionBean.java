/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
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
import javax.faces.component.UIInput;

import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.system.common.deno.Deno;
import org.ace.insurance.system.common.deno.service.interfaces.IDenoService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageDenoActionBean")
public class ManageDenoActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{DenoService}")
	private IDenoService denoService;

	public void setDenoService(IDenoService denoService) {
		this.denoService = denoService;
	}

	@ManagedProperty(value = "#{CurrencyService}")
	private ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	private boolean createNew;
	private Deno deno;
	private List<Currency> currencyList;
	private List<Deno> denoList;

	@PostConstruct
	public void init() {
		createNewDeno();
		loadDeno();
	}

	private void loadDeno() {
		denoList = denoService.findAllDeno();
	}

	public void createNewDeno() {
		createNew = true;
		deno = new Deno();
	}

	public List<Currency> getCurrencyList() {
		if (currencyList == null) {
			currencyList = currencyService.findAllCurrency();
		}
		return currencyList;
	}

	public void prepareUpdateDeno(Deno deno) {
		createNew = false;
		this.deno = deno;
	}

	public void addNewDeno() {
		try {
			Currency currency = deno.getCurrency();
			if (currency == null) {
				addInfoMessage(null, MessageId.REQUIRED_CURRENCY);
			}
			String formID = "denoEntryForm";
			if (deno.getD1() <= 0) {
				addErrorMessage(formID + ":d1", UIInput.REQUIRED_MESSAGE_ID);
				return;
			} else {
				denoService.addNewDeno(deno);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, deno.getName());
				createNewDeno();
				loadDeno();
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateDeno() {
		try {
			denoService.updateDeno(deno);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, deno.getName());
			createNewDeno();
			loadDeno();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deleteDeno(Deno deno) {
		try {
			denoService.deleteDeno(deno);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, deno.getName());
			createNewDeno();
			loadDeno();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<Deno> getDenoList() {
		return denoList;
	}

	public Deno getDeno() {
		return deno;
	}

	public void setDeno(Deno deno) {
		this.deno = deno;
	}

	public void returnCurrency(SelectEvent event) {
		Currency currency = (Currency) event.getObject();
		deno.setCurrency(currency);
	}
}
