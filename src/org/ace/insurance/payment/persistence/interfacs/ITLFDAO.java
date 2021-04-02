package org.ace.insurance.payment.persistence.interfacs;

import java.util.Date;
import java.util.List;

import org.ace.insurance.payment.TLF;
import org.ace.insurance.web.manage.report.common.AccountAndLifeDeptCancelReportDTO;
import org.ace.insurance.web.manage.report.common.AccountManualReceiptDTO;
import org.ace.insurance.web.manage.report.common.DailyIncomeReportDTO;
import org.ace.insurance.web.manage.report.common.SalePointDTO;
import org.ace.insurance.web.manage.report.common.TLFCriteria;
import org.ace.insurance.web.manage.report.common.TLFDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface ITLFDAO {
	public TLF insert(TLF tlf) throws DAOException;

	public long findConvertedTlf(Date actionDate) throws DAOException;

	List<TLFDTO> findTLFDTOByCriteria(TLFCriteria criteria) throws DAOException;

	List<SalePointDTO> findSalePointDTOByCriteria(TLFCriteria criteria) throws DAOException;

	List<DailyIncomeReportDTO> findDailyIncomeReportByCriteria(TLFCriteria criteria) throws DAOException;

	List<AccountManualReceiptDTO> findAccountManualReceiptDTOByCriteria(TLFCriteria criteria) throws DAOException;

	List<AccountAndLifeDeptCancelReportDTO> findAccountAndLifeDeptCancelReportDTOByCriteria(TLFCriteria criteria) throws DAOException;

	List<DailyIncomeReportDTO> findGroupFarmerDailyIncomeReport(TLFCriteria criteria) throws DAOException;

	void reverseBytlfNo(String tlfNo) throws DAOException;
}
