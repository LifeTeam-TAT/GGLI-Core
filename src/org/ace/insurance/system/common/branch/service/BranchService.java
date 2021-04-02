/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.branch.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.system.common.account.CurrencyChartOfAccount;
import org.ace.insurance.system.common.account.persistence.interfaces.IAccountDAO;
import org.ace.insurance.system.common.bank.Coa;
import org.ace.insurance.system.common.branch.BRA001;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.persistence.interfaces.IBranchDAO;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "BranchService")
public class BranchService extends BaseService implements IBranchService {

	@Resource(name = "BranchDAO")
	private IBranchDAO branchDAO;

	@Autowired
	private IAccountDAO accountDAO;

	@Autowired
	private ICurrencyService currencyService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewBranch(Branch branch) {
		try {
			branch.setPrefix(getPrefix(Branch.class));
			branchDAO.insert(branch);
			List<Coa> coaList = accountDAO.findAllCoa();
			String budInfo = getBudgetInfo(new Date(), 2);
			List<Currency> currencyList = currencyService.findAllCurrency();
			for (Coa chartOfAccount : coaList) {
				for (Currency currency : currencyList) {
					CurrencyChartOfAccount ccoa = new CurrencyChartOfAccount();
					ccoa.setCoaid(chartOfAccount.getId());
					ccoa.setaCNAME(chartOfAccount.getAcName());
					ccoa.setcUR(currency.getId());
					ccoa.setBranchID(branch.getId());
					ccoa.setdCODE("");
					ccoa.setbUDGET(budInfo);
					accountDAO.createCcoa(ccoa);
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Branch", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBranch(Branch branch) {
		try {
			branchDAO.update(branch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Branch", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteBranch(Branch branch) {
		try {
			branchDAO.delete(branch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a Branch", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Branch findBranchById(String id) {
		Branch result = null;
		try {
			result = branchDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Branch (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Branch findByBranchCode(String branchCode) {
		Branch result = null;
		try {
			result = branchDAO.findByCode(branchCode);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Branch (branchCode : " + branchCode + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Branch findByCSCBranchCode(String cscBranchCode) throws SystemException {
		Branch result = null;
		try {
			result = branchDAO.findByCSCBranchCode(cscBranchCode);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Branch (CSC branchCode : " + cscBranchCode + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRA001> findAll_BRA001() throws SystemException {
		List<BRA001> result = null;
		try {
			result = branchDAO.findAll_BRA001();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Branch)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRA001> findAll_BRA001ByStatus() {
		List<BRA001> result = null;
		try {
			result = branchDAO.findAll_BRA001ByStatus();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Branch)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Branch> findAllBranch() throws SystemException {
		List<Branch> result = null;
		try {
			result = branchDAO.findAllBranch();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Branch)", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExitBranch(Branch branch) {
		try {
			return branchDAO.isAlreadyExitBranch(branch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Branch By Name)", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExistBranchCode(String branchName) {
		try {
			return branchDAO.isAlreadyExistBranchCode(branchName);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Branch By Name)", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRA001> findBranchByEntityIdAndBranchId(String entityId, String branchId) {
		List<BRA001> result = null;
		try {
			result = branchDAO.findBranchByEntityIdAndBranchId(entityId, branchId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Branch By entity)", e);

		}
		return result;
	}

	public String getBudgetInfo(Date pDate, int mode) {

		int budSmth = accountDAO.findSetupValueByVariable("BUDSMTH");
		String budInfo = "";
		int year = DateUtils.getYearFromDate(pDate);
		// int nextYear = year + 1;

		switch (mode) {
			case 1:// Show Budget Year Eg. 00#01

				if (budSmth == 1) {
					budInfo = String.valueOf(year).substring(2) + "#" + String.valueOf(year).substring(2);
				} else {
					if (DateUtils.getMonthFromDate(pDate) < budSmth) {
						budInfo = String.valueOf(year - 1).substring(2) + "#" + String.valueOf(year).substring(2);
					} else {
						budInfo = String.valueOf(year).substring(2) + "#" + String.valueOf(year + 1).substring(2);
					}
				}

				break;

			case 2:// Show Budget Year Eg.2000/2001

				if (budSmth == 1) {
					budInfo = year + "/" + year;
				} else {
					int pMonth = DateUtils.getMonthFromDate(pDate) + 1;
					if (budSmth > pMonth) {
						budInfo = (year - 1) + "/" + year;
					} else {
						budInfo = year + "/" + (year + 1);
					}
				}
				break;

			case 3:// Show Budget Month Eg.M1, M2
				int month = DateUtils.getMonthFromDate(pDate) + 1;
				budInfo = String.valueOf((month < budSmth ? month + 12 : month) + 1 - budSmth);
				break;
			case 4:// Show Quarter Eg. Q1,Q2,Q3,Q4

				int counter = 0;
				int mStart = budSmth;
				int mEnd = budSmth + 2;
				int period = 1;

				for (int i = budSmth; i <= budSmth + 12; i++) {
					counter = counter + 1;

					if (counter % 3 == 0) {
						mStart = (mEnd + 1) > 12 ? mEnd + 1 - 12 : mEnd + 1;
						mEnd = (mStart + 2) > 12 ? mStart + 2 - 12 : mStart + 2;
						period = period + 1;
					}

					if (mStart <= DateUtils.getMonthFromDate(pDate) && mEnd >= DateUtils.getMonthFromDate(pDate)) {
						budInfo = period + "";
						break;
					}
				}

				break;
		}

		return budInfo;
	}

}