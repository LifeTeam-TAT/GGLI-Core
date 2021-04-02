package org.ace.insurance.report.coinsurance.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.report.JRGenerateUtility;
import org.ace.insurance.report.coinsurance.persistence.interfaces.ICustomerInformationReportDAO;
import org.ace.insurance.report.coinsurance.service.interfaces.ICustomerInformationReportService;
import org.ace.insurance.report.customer.CustomerInformationCriteria;
import org.ace.insurance.report.customer.CustomerInformationReport;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service(value = "CustomerInformationReportService")
public class CustomerInformationReportService implements ICustomerInformationReportService {

	@Resource(name = "CustomerInformationReportDAO")
	private ICustomerInformationReportDAO customerDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CustomerInformationReport> findCustomerInformation(CustomerInformationCriteria criteria) {
		List<CustomerInformationReport> result = null;
		try {
			result = customerDAO.findAllCustomer(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Customer Information Report by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<IPolicy> findAllActivePoliciesByCustomerId(String Id) {
		List<IPolicy> result = null;
		try {
			result = customerDAO.findAllPolicies(Id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Customer Information Report by criteria.", e);
		}
		return result;
	}

	public void generateReport(List<CustomerInformationReport> reportList, String filePath, String fileName) {
		final String templatePath = "/report-template/customer/";
		final String templateName = "customerInformationReport.jrxml";
		String templateFullPath = templatePath + templateName;
		String outputFilePdf = filePath + fileName;

		// Create the DataSource instance with the given list which
		// in turn to be filled up in the report
		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(reportList);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("TableDataSource", ds);

		new JRGenerateUtility().generateReport(templateFullPath, outputFilePdf, paramMap);
	}

	public void generateReportIndividual(CustomerInformationReport customer, String filePath, String fileName) {
		final String templatePath = "/report-template/customer/";
		final String templateName = "customerIndividualInformationReport.jrxml";
		String templateFullPath = templatePath + templateName;
		String outputFilePdf = filePath + fileName;

		// Create the DataSource instance with the given list which
		// in turn to be filled up in the report
		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(customerDAO.findFamilyInfoByCustomerId(customer.getCustomerId()));

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("TableDataSource", ds);
		paramMap.put("name", customer.getCustomerName());
		paramMap.put("nrc", customer.getNrc());
		paramMap.put("dob", customer.getDob());
		paramMap.put("age", customer.getAge());
		paramMap.put("qualification", customer.getQualificaiton());
		paramMap.put("address", customer.getAddress());
		paramMap.put("mobile", customer.getMobile());
		paramMap.put("telephone", customer.getPhoneNo());
		paramMap.put("email", customer.getEmail());
		paramMap.put("fatherName", customer.getFatherName());

		new JRGenerateUtility().generateReport(templateFullPath, outputFilePdf, paramMap);
	}

}
