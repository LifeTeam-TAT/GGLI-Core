package org.ace.insurance.system.common.bankCharges.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.system.common.bankCharges.persistence.interfaces.IBankChargesDAO;
import org.ace.insurance.system.common.bankCharges.service.interfaces.IBankChargesService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("BankChargesService")
public class BankChargesService implements IBankChargesService {

	@Resource(name = "BankChargesDAO")
	private IBankChargesDAO bankChargesDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BankCharges bankCharges) {
		try {
			bankChargesDAO.insert(bankCharges);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a bankCharges", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BankCharges bankCharges) {
		try {
			bankChargesDAO.delete(bankCharges);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a bancarefferal", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BankCharges bankCharges) {
		try {
			bankChargesDAO.update(bankCharges);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a bankCharges", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BankCharges findByBankChargesId(String bankChargesId) {
		BankCharges result = null;
		try {
			result = bankChargesDAO.findById(bankChargesId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find bankChargesId", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BankCharges> findAllBankCharges() {
		List<BankCharges> result = null;
		try {
			result = bankChargesDAO.findAllBankCharges();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a BankCharges", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BankCharges> findAllBankChargesActive(String branchId) {
		List<BankCharges> result = null;
		try {
			result = bankChargesDAO.findAllbankChargesActive(branchId);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a BankCharges", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBankCharges(BankCharges bankCharges) {
		return bankChargesDAO.checkExistingBankCharges(bankCharges);
	}

}
