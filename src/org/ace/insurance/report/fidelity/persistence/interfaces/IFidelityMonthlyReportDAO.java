package org.ace.insurance.report.fidelity.persistence.interfaces;

/**
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/5
 */
import java.util.List;

import org.ace.insurance.report.fidelity.FidelityMonthlyReport;
import org.ace.insurance.report.fidelity.FidelityMonthlyReportCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IFidelityMonthlyReportDAO {

	public List<FidelityMonthlyReport> find(FidelityMonthlyReportCriteria criteria) throws DAOException;
}
