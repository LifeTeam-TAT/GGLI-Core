/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.payment.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.persistence.interfacs.ITLFDAO;
import org.ace.insurance.payment.service.interfaces.ITLFService;
import org.ace.insurance.web.manage.report.common.AccountAndLifeDeptCancelReportDTO;
import org.ace.insurance.web.manage.report.common.AccountManualReceiptDTO;
import org.ace.insurance.web.manage.report.common.DailyIncomeReportDTO;
import org.ace.insurance.web.manage.report.common.SalePointDTO;
import org.ace.insurance.web.manage.report.common.TLFCriteria;
import org.ace.insurance.web.manage.report.common.TLFDTO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "TLFService")
public class TLFService extends BaseService implements ITLFService {

	@Resource(name = "TLFDAO")
	private ITLFDAO tlfDAO;

	public void addNewTlf(TLF tlf) throws SystemException {
		try {
			tlfDAO.insert(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	public long findConvertedTlf(Date actionDate) throws SystemException {
		long result;
		try {
			result = tlfDAO.findConvertedTlf(actionDate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TLFDTO> findTLFDTOByCriteria(TLFCriteria criteria) throws SystemException {
		try {
			return tlfDAO.findTLFDTOByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all tlf by criteria", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<SalePointDTO> findSalePointDTOByCriteria(TLFCriteria criteria) throws SystemException {
		try {
			return tlfDAO.findSalePointDTOByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all tlf by criteria", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<DailyIncomeReportDTO> findDailyIncomeReportByCriteria(TLFCriteria criteria) throws SystemException {
		try {

			return tlfDAO.findDailyIncomeReportByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find daily income report by criteria", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<AccountManualReceiptDTO> findAccountManualReceiptDTOByCriteria(TLFCriteria criteria) throws SystemException {
		try {
			return tlfDAO.findAccountManualReceiptDTOByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Account Manual Receipt report by criteria", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<AccountAndLifeDeptCancelReportDTO> findAccountAndLifeDeptCancelReportDTOByCriteria(TLFCriteria criteria) throws SystemException {
		try {
			return tlfDAO.findAccountAndLifeDeptCancelReportDTOByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Account and Life Department Cancel report by criteria", e);
		}
	}
	

}