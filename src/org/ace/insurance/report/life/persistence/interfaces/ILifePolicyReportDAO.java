package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.LifePolicyReport;
import org.ace.insurance.report.life.LifePolicyReportCriteria;
import org.ace.insurance.report.life.ShortTermEndowmentLifeReportCriteria;
import org.ace.insurance.report.personalAccident.PersonalAccidentPolicyReport;
import org.ace.insurance.report.shortEndowLife.NewShortTermEndowmentLifePolicyReport;
import org.ace.insurance.report.shortEndowLife.ShortEndowLifePolicyReport;
import org.ace.java.component.persistence.exception.DAOException;

public interface ILifePolicyReportDAO {
	public List<LifePolicyReport> findLifePolicyReport(LifePolicyReportCriteria lifePolicyReportCriteria, List<String> productIdList) throws DAOException;

	public List<PersonalAccidentPolicyReport> findPersonalAccidentPolicyReport(LifePolicyReportCriteria lifePolicyReportCriteria) throws DAOException;

	public List<ShortEndowLifePolicyReport> findShortEndowLifePolicyReport(LifePolicyReportCriteria lifePolicyReportCriteria) throws DAOException;

	List<NewShortTermEndowmentLifePolicyReport> findNewShortTermEndowmentLifePolicyReport(ShortTermEndowmentLifeReportCriteria criteria) throws DAOException;
}
