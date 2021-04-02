package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.LifeDailyIncomeReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface ILifeDailyIncomeReportDAO {
	public List<LifeDailyIncomeReport> find(LifeDailyIncomeReportCriteria lifeDailyCriteria, List<String> productIdList) throws DAOException;
}
