package org.ace.insurance.report.coinsurance.persistence.interfaces;

import java.util.List;

import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.report.customer.CustomerInformationCriteria;
import org.ace.insurance.report.customer.CustomerInformationReport;
import org.ace.insurance.report.customer.FamilyInfoDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICustomerInformationReportDAO {

	public List<CustomerInformationReport> findAllCustomer(CustomerInformationCriteria criteria) throws DAOException;

	public List<FamilyInfoDTO> findFamilyInfoByCustomerId(String id) throws DAOException;

	public List<IPolicy> findAllPolicies(String customerId) throws DAOException;
}
