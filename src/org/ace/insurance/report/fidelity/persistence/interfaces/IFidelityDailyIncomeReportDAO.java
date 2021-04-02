package org.ace.insurance.report.fidelity.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.fidelity.FidelityDailyIncomeReport;
import org.ace.insurance.report.fidelity.FidelityDailyIncomeReportCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IFidelityDailyIncomeReportDAO {
	public List<FidelityDailyIncomeReport> find(FidelityDailyIncomeReportCriteria criteria)throws DAOException;
}
