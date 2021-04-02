package org.ace.insurance.report.fidelity.service;

/**
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/5
 */
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.report.fidelity.FidelityMonthlyReport;
import org.ace.insurance.report.fidelity.FidelityMonthlyReportCriteria;
import org.ace.insurance.report.fidelity.persistence.interfaces.IFidelityMonthlyReportDAO;
import org.ace.insurance.report.fidelity.service.interfaces.IFidelityMonthlyReportService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "FidelityMonthlyReportService")
public class FidelityMonthlyReportService implements IFidelityMonthlyReportService {

	@Resource(name = "FidelityMonthlyReportDAO")
	private IFidelityMonthlyReportDAO fidelityMonthlyReportDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FidelityMonthlyReport> findFidelityMonthlyReport(FidelityMonthlyReportCriteria fidelityMonthlyCriteria) {
		List<FidelityMonthlyReport> resultList = null;
		try {
			resultList = fidelityMonthlyReportDAO.find(fidelityMonthlyCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find FidelityMonthlyReport by criteria.", e);
		}
		return resultList;

	}

}
