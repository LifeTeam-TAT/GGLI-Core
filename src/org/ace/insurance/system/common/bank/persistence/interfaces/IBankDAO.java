/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.bank.persistence.interfaces;

import java.util.List;

import org.ace.insurance.filter.bankCustomer.BANKCUSTOMER001;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.Coa;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBankDAO {
	public void insert(Bank Bank) throws DAOException;

	public void update(Bank Bank) throws DAOException;

	public void delete(Bank Bank) throws DAOException;

	public Bank findById(String id) throws DAOException;

	public List<BANKCUSTOMER001> findAll() throws DAOException;

	public List<Coa> findCAOByAType() throws DAOException;

	public List<Bank> findACodeNotNull() throws DAOException;

	public Bank findByCSCBankCode(String cscBankCode) throws DAOException;

	public Bank findByACODE(String acode) throws DAOException;

	public List<BANKCUSTOMER001> findAllInterBranchBank() throws DAOException;

	List<Bank> findAllBank() throws DAOException;

	public Bank findBybranchId(String id) throws DAOException;
}
