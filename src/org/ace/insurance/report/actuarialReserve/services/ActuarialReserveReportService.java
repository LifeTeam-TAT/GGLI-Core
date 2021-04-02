package org.ace.insurance.report.actuarialReserve.services;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.report.actuarialReserve.ActuarialReserveReport;
import org.ace.insurance.report.actuarialReserve.persistence.IActuarialReserveReportDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "ActuarialReserveReportService")
public class ActuarialReserveReportService implements IActuarialReserveReportService {
	@Resource(name = "ActuarialReserveReportDAO")
	private IActuarialReserveReportDAO reportDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ActuarialReserveReport> findActurarialReserveReport(List<String> productIdList, int startYear, int endYear) throws SystemException {
		try {
			return reportDAO.findActuarialReserveReport(productIdList, startYear, endYear);
		} catch (DAOException de) {
			throw new SystemException(de.getErrorCode(), "Fail to find Actuarial Reserve Report ");
		}
	}

}
