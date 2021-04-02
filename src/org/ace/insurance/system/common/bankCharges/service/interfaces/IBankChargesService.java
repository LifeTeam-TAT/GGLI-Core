package org.ace.insurance.system.common.bankCharges.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bankCharges.BankCharges;

public interface IBankChargesService {

	public void insert(BankCharges bankCharges);

	public void delete(BankCharges bankCharges);

	public void update(BankCharges bankCharges);

	public BankCharges findByBankChargesId(String bankChargesId);

	public List<BankCharges> findAllBankCharges();

	public List<BankCharges> findAllBankChargesActive(String branchId);

	public boolean checkExistingBankCharges(BankCharges bankCharges);

}
