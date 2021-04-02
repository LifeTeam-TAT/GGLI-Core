package org.ace.insurance.report.upr.services;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.report.upr.UPRReport;
import org.ace.insurance.report.upr.persistence.interfaces.IUPRReportDAO;
import org.ace.insurance.report.upr.services.interfaces.IUPRReportService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "UPRReportService")
public class UPRReportService implements IUPRReportService {

	@Resource(name = "UPRReportDAO")
	private IUPRReportDAO reportDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<UPRReport> findUPRReport(int startYear, int endYear, boolean isFinancialYear) throws SystemException {
		try {
			return reportDAO.findUPRReport(startYear, endYear, isFinancialYear);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Fail to find UPRReport ");
		} catch (ParseException d) {
			throw new SystemException("Fail to find UPRReport ", null);
		}
	}

}
