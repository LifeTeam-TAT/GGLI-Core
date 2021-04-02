/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.bankBranch.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.bankBranch.BBRANCH001;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.persistence.interfaces.IBankBranchDAO;
import org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "BankBranchService")
public class BankBranchService extends BaseService implements IBankBranchService {

	@Resource(name = "BankBranchDAO")
	private IBankBranchDAO bankBranchDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewBankBranch(BankBranch bankBranch) {
		try {
			bankBranch.setPrefix(getPrefix(BankBranch.class));
			bankBranchDAO.insert(bankBranch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new BankBranch", e);
		}
	}

	public void updateBankBranch(BankBranch bankBranch) {
		try {
			bankBranchDAO.update(bankBranch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a BankBranch", e);
		}
	}

	public void deleteBankBranch(BankBranch bankBranch) {
		try {
			bankBranchDAO.delete(bankBranch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a BankBranch", e);
		}
	}

	public List<BankBranch> findAllBankBranch() {
		List<BankBranch> result = null;
		try {
			result = bankBranchDAO.findAllBankBranch();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of BankBranch)", e);
		}
		return result;
	}

	public BankBranch findBankBranchById(String id) {
		BankBranch result = null;
		try {
			result = bankBranchDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a BankBranch (ID : " + id + ")", e);
		}
		return result;
	}

	
	public List<BBRANCH001> findAll() {
		List<BBRANCH001> result = null;
		try {
			result = bankBranchDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of BankBranch)", e);
		}
		return result;
	}

	

	
}