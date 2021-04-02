/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.bank.service.interfaces;

import java.util.List;

import org.ace.insurance.filter.bankCustomer.BANKCUSTOMER001;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.Coa;
import org.ace.java.component.SystemException;

public interface IBankService {
	public void addNewBank(Bank Bank);

	public void updateBank(Bank Bank);

	public void deleteBank(Bank Bank);

	public Bank findBankById(String id);

	public List<BANKCUSTOMER001> findAllBank();

	public List<Coa> findAllCOAByAType();

	public List<Bank> findACodeNotNull();

	public Bank findByCSCBankCode(String cscBankCode) throws SystemException;

	public Bank findByACODE(String acode) throws SystemException;

	public List<BANKCUSTOMER001> findAllInterBranchBank() throws SystemException;

	List<Bank> findAll() throws SystemException;

	public Bank findBybranchId(String id) throws SystemException;
}
