package org.ace.insurance.system.common.account.services;

import java.util.List;

import org.ace.insurance.system.common.account.persistence.interfaces.IAccountDAO;
import org.ace.insurance.system.common.account.services.interfaces.IAccountService;
import org.ace.insurance.system.common.bank.Coa;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "AccountService")
public class AccountService extends BaseService implements IAccountService {

	@Autowired
	private IAccountDAO accountDAO;

	@Override
	public List<Coa> findAllCoa() {
		try {
			return accountDAO.findAllCoa();
		}
		catch (DAOException de) {
			throw new SystemException(de.getErrorCode(), "fail to find all Coa");
		}
	}

}
