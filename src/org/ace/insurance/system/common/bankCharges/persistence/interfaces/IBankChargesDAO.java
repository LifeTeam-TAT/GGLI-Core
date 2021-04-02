package org.ace.insurance.system.common.bankCharges.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBankChargesDAO {

	public void insert(BankCharges bankCharges) throws DAOException;

	public void delete(BankCharges bankCharges) throws DAOException;

	public void update(BankCharges bankCharges) throws DAOException;

	public BankCharges findById(String bankChargesId) throws DAOException;

	public List<BankCharges> findAllBankCharges() throws DAOException;

	public List<BankCharges> findAllbankChargesActive(String branchId) throws DAOException;

	public boolean checkExistingBankCharges(BankCharges bankCharges) throws DAOException;
}
