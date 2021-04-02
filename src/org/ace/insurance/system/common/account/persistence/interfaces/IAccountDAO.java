package org.ace.insurance.system.common.account.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.account.CurrencyChartOfAccount;
import org.ace.insurance.system.common.bank.Coa;
import org.ace.java.component.persistence.exception.DAOException;

public interface IAccountDAO {

	List<Coa> findAllCoa() throws DAOException;

	void createCcoa(CurrencyChartOfAccount ccoa) throws DAOException;

	int findSetupValueByVariable(String variable) throws DAOException;

}