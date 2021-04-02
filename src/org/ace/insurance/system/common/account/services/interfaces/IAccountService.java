package org.ace.insurance.system.common.account.services.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bank.Coa;

public interface IAccountService {

	List<Coa> findAllCoa();

}