package org.ace.insurance.report.life.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.report.JRGenerateUtility;
import org.ace.insurance.report.common.StudentLifeCriteria;
import org.ace.insurance.report.common.SummaryReportCriteria;
import org.ace.insurance.report.life.LifePolicyMonthlyReport;
import org.ace.insurance.report.life.SnakeBitePolicyMonthlyReport;
import org.ace.insurance.report.life.persistence.interfaces.ILifePolicyMonthlyReportDAO;
import org.ace.insurance.report.life.report.LifeMonthlyReport;
import org.ace.insurance.report.life.service.interfaces.ILifePolicyMonthlyReportService;
import org.ace.insurance.report.studentLife.BCStudentLifeView;
import org.ace.insurance.report.studentLife.NewStudentLifeView;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service(value = "LifePolicyMonthlyReportService")
public class LifePolicyMonthlyReportService implements ILifePolicyMonthlyReportService {

	@Resource(name = "LifePolicyMonthlyReportDAO")
	private ILifePolicyMonthlyReportDAO lifePolicyMonthlyReportDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeMonthlyReport> findLifePolicyMonthlyReportByCriteria(SummaryReportCriteria criteria) {
		List<LifeMonthlyReport> result = new ArrayList<LifeMonthlyReport>();
		try {
			result = lifePolicyMonthlyReportDAO.findLifePolicyMonthlyReport(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifePolicyMonthlyReport by criteria.", e);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeMonthlyReport> findLifePolicyRenewalMonthlyReportByCriteria(SummaryReportCriteria criteria) {
		List<LifeMonthlyReport> result = new ArrayList<LifeMonthlyReport>();
		try {
			result = lifePolicyMonthlyReportDAO.findLifePolicyRenewalMonthlyReport(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifePolicyMonthlyReport by criteria.", e);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicyMonthlyReport> findSnakeBitePolicyMonthlyReportByCriteria(SummaryReportCriteria criteria) {
		List<SnakeBitePolicyMonthlyReport> result = new ArrayList<SnakeBitePolicyMonthlyReport>();
		try {
			result = lifePolicyMonthlyReportDAO.findSnakeBitePolicyMonthlyReport(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find SnakeBitePolicyMonthlyReport by criteria.", e);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void generateLifePolicyMonthlyReport(List<LifePolicyMonthlyReport> reports, String fullReportFilePath, SummaryReportCriteria criteria) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("LifePolicyMonthlyReports", new JRBeanCollectionDataSource(reports));
		params.put("productFiltered", criteria.getLifeProductType());
		params.put("monthFiltered", criteria.getMonth());
		params.put("yearFiltered", criteria.getYear());
		String fullTemplateFilePath = "report-template/life/lifePolicyMonthlyReport.jrxml";
		new JRGenerateUtility().generateReport(fullTemplateFilePath, fullReportFilePath, params);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<NewStudentLifeView> findStudentLifePolicyMonthlyReportByCriteria(StudentLifeCriteria studentLifeCriteria) {
		List<NewStudentLifeView> result = new ArrayList<NewStudentLifeView>();
		try {
			result = lifePolicyMonthlyReportDAO.findStudentLifePolicyMonthlyReportByCriteria(studentLifeCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find StudentLife Policy Monthly Report By Criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BCStudentLifeView> findBCStudentLifePolicyMonthlyReportByCriteria(StudentLifeCriteria studentLifeCriteria) {
		List<BCStudentLifeView> result = new ArrayList<BCStudentLifeView>();
		try {
			result = lifePolicyMonthlyReportDAO.findBCStudentLifePolicyMonthlyReportByCriteria(studentLifeCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find BC Student Life Policy Monthly Report By Criteria.", e);
		}
		return result;
	}
}
