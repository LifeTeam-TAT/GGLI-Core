package org.ace.insurance.report.upr.persistence.interfaces;

import java.text.ParseException;
import java.util.List;

import org.ace.insurance.report.upr.UPRReport;
import org.ace.java.component.persistence.exception.DAOException;

public interface IUPRReportDAO {

	List<UPRReport> findUPRReport(int startYear, int endYear, boolean isFinancialYear) throws DAOException, ParseException;

}