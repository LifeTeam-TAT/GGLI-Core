package org.ace.insurance.report.actuarialReserve.services;

import java.util.List;

import org.ace.insurance.report.actuarialReserve.ActuarialReserveReport;
import org.ace.java.component.SystemException;

public interface IActuarialReserveReportService {

	List<ActuarialReserveReport> findActurarialReserveReport(List<String> productIdList, int startYear, int endYear) throws SystemException;

}