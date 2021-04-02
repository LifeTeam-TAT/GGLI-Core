/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.bank.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.ace.insurance.filter.bankCustomer.BANKCUSTOMER001;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.Coa;
import org.ace.insurance.system.common.bank.persistence.interfaces.IBankDAO;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "BankService")
public class BankService extends BaseService implements IBankService {

	@Resource(name = "BankDAO")
	private IBankDAO bankDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewBank(Bank bank) {
		try {
			bank.setPrefix(getPrefix(Bank.class));
			bankDAO.insert(bank);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new Bank", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateBank(Bank bank) {
		try {
			bankDAO.update(bank);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update a Bank", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteBank(Bank bank) {
		try {
			bankDAO.delete(bank);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete a Bank", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<BANKCUSTOMER001> findAllBank() {
		List<BANKCUSTOMER001> result = null;
		try {
			result = bankDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of Bank)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Bank findBankById(String id) {
		Bank result = null;
		try {
			result = bankDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a Bank (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<Coa> findAllCOAByAType() {
		List<Coa> result = null;
		try {
			result = bankDAO.findCAOByAType();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find COA By AType)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<Bank> findACodeNotNull() {
		List<Bank> result = null;
		try {
			result = bankDAO.findACodeNotNull();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find All Banks which ACode is Not Null)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Bank findByCSCBankCode(String cscBankCode) throws SystemException {
		Bank result = null;
		try {
			result = bankDAO.findByCSCBankCode(cscBankCode);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a Bank ( cscBankCode : " + cscBankCode + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Bank findByACODE(String acode) throws SystemException {
		Bank result = null;
		try {
			result = bankDAO.findByACODE(acode);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a Bank ( ACODE : " + acode + ")", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<BANKCUSTOMER001> findAllInterBranchBank() throws SystemException {
		List<BANKCUSTOMER001> result = null;
		try {
			result = bankDAO.findAllInterBranchBank();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of InterBranchBank()", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Bank> findAll() throws SystemException {
		try {
			return bankDAO.findAllBank();
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of Bank", e);
		}
	}

	@Override
	public Bank findBybranchId(String branchId) throws SystemException {
		Bank result = null;
		try {
			result = bankDAO.findBybranchId(branchId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a Bank ( id : " + branchId + ")", e);
		}
		return result;
	}
}
