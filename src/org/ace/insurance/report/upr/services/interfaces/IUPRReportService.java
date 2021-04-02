package org.ace.insurance.report.upr.services.interfaces;

import java.util.List;

import org.ace.insurance.report.upr.UPRReport;
import org.ace.java.component.SystemException;

public interface IUPRReportService {

	List<UPRReport> findUPRReport(int startYear, int endYear, boolean isFinancialYear) throws SystemException;

}