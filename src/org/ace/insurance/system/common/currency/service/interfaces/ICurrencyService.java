/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.currency.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.Currency001;
import org.ace.java.component.SystemException;

public interface ICurrencyService {
	public void addNewCurrency(Currency currency);

	public void updateCurrency(Currency currency);

	public void deleteCurrency(Currency currency);

	public Currency findCurrencyById(String id);

	public Currency findCurrencyByCurrencyCode(String currencyCode);

	public List<Currency> findAllCurrency();

	public List<Currency001> findAllCurrency001();

	public Currency findHomeCurrency() throws SystemException;

	public Currency findCurrencyByCSCCurCode(String cscCurCode) throws SystemException;
}
