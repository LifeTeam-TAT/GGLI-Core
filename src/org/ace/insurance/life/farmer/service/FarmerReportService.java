package org.ace.insurance.life.farmer.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.life.farmer.persistence.interfaces.IFarmerReportDAO;
import org.ace.insurance.life.farmer.service.interfaces.IFarmerReportService;
import org.ace.insurance.report.common.MonthlyReportCriteria;
import org.ace.insurance.report.farmer.FarmerDailyReport;
import org.ace.insurance.report.farmer.FarmerMonthlyReport;
import org.ace.insurance.report.farmer.FarmerSummaryReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "FarmerReportService")
public class FarmerReportService implements IFarmerReportService {
	
	@Resource(name = "FarmerReportDAO")
	private IFarmerReportDAO farmerReportDAO;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FarmerDailyReport> findFarmerDailyReport(LifeDailyIncomeReportCriteria criteria) {
		List<FarmerDailyReport> result = null;
		try {
			result = farmerReportDAO.findDailyReport(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find farmer daily report.", e);
		}
		return result;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FarmerMonthlyReport> findFarmerMonthlyReport(MonthlyReportCriteria criteria) {
		List<FarmerMonthlyReport> resultList = null;
		try {
			resultList = farmerReportDAO.findMonthlyReport(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find farmer monthly report.", e);
		}
		return resultList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FarmerSummaryReport> findFarmerSummaryReport(LifeDailyIncomeReportCriteria criteria) {
		List<FarmerSummaryReport> result = null;
		try {
			result = farmerReportDAO.findSummaryReport(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find farmer summary report.", e);
		}
		return result;
	}
}
