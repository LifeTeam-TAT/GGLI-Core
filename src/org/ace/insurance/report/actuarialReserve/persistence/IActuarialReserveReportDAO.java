package org.ace.insurance.report.actuarialReserve.persistence;

import java.util.List;

import org.ace.insurance.report.actuarialReserve.ActuarialReserveReport;
import org.ace.java.component.persistence.exception.DAOException;

public interface IActuarialReserveReportDAO {

	List<ActuarialReserveReport> findActuarialReserveReport(List<String> productIdList, int startYear, int endYear) throws DAOException;

}