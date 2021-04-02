package org.ace.insurance.payment.service.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.payment.TLF;
import org.ace.insurance.web.manage.report.common.AccountAndLifeDeptCancelReportDTO;
import org.ace.insurance.web.manage.report.common.AccountManualReceiptDTO;
import org.ace.insurance.web.manage.report.common.DailyIncomeReportDTO;
import org.ace.insurance.web.manage.report.common.SalePointDTO;
import org.ace.insurance.web.manage.report.common.TLFCriteria;
import org.ace.insurance.web.manage.report.common.TLFDTO;
import org.ace.java.component.SystemException;

public interface ITLFService {
	public void addNewTlf(TLF tlf) throws SystemException;

	public long findConvertedTlf(Date actionDate) throws SystemException;

	List<TLFDTO> findTLFDTOByCriteria(TLFCriteria criteria) throws SystemException;

	List<SalePointDTO> findSalePointDTOByCriteria(TLFCriteria criteria) throws SystemException;

	List<DailyIncomeReportDTO> findDailyIncomeReportByCriteria(TLFCriteria criteria) throws SystemException;

	List<AccountManualReceiptDTO> findAccountManualReceiptDTOByCriteria(TLFCriteria criteria) throws SystemException;

	List<AccountAndLifeDeptCancelReportDTO> findAccountAndLifeDeptCancelReportDTOByCriteria(TLFCriteria criteria)
			throws SystemException;
}
