package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.Currency001;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "CurrencyDialogActionBean")
@ViewScoped
public class CurrencyDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{CurrencyService}")
	private ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	private List<Currency001> currencyList;

	@PostConstruct
	public void init() {
		currencyList = currencyService.findAllCurrency001();
	}

	public List<Currency001> getCurrencyList() {
		return currencyList;
	}

	public void selectCurrency(Currency001 currency001) {
		Currency currency = currencyService.findCurrencyById(currency001.getId());
		PrimeFaces.current().dialog().closeDynamic(currency);
	}
}
