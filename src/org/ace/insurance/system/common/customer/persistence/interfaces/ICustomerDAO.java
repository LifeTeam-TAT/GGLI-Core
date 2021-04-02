/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.customer.persistence.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.common.CustomerCriteria;
import org.ace.insurance.common.Name;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.medical.proposal.CustomerInfoStatus;
import org.ace.insurance.report.customer.CustomerInformationCriteria;
import org.ace.insurance.system.common.customer.CUST001;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.user.User;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICustomerDAO {
	public void insert(Customer customer) throws DAOException;

	public void insert(Customer customer, User user) throws DAOException;

	public void insert(List<Customer> customerList) throws DAOException;

	public Customer update(Customer customer) throws DAOException;

	public void delete(Customer customer) throws DAOException;

	public Customer findById(String id) throws DAOException;

	public List<CUST001> findByCriteria(CustomerCriteria criteria, int max) throws DAOException;

	public List<Customer> findByCriteria(CustomerCriteria criteria) throws DAOException;

	public List<Customer> findByCustomerInformationCriteria(CustomerInformationCriteria customerInformationCriteria) throws DAOException;

	public void updateActivePolicy(int policyCount, String customerId) throws DAOException;

	public void updateActivedPolicyDate(Date activedDate, String customerId) throws DAOException;

	public List<ProposalInsuredPerson> findProposalInsuredPersonByCustomerId(String id) throws DAOException;

	public void updateProposalInsuredPerson(ProposalInsuredPerson p) throws DAOException;

	public List<PolicyInsuredPerson> findPolicyInsuredPersonByCustomerId(String id) throws DAOException;

	public void updatePolicylInsuredPerson(PolicyInsuredPerson p) throws DAOException;

	public Customer findCustomerByInsuredPerson(Name name, String idNo, String fatherName, Date dob) throws DAOException;

	public void insertCustomerStatus(CustomerInfoStatus status);

	public List<CustomerInfoStatus> findInfoStatusByCustomerId(String id) throws DAOException;

	public boolean checkExistingCustomer(Customer customer) throws DAOException;

	public Customer findByRefMobileUserId(String id) throws DAOException;
	
	public Customer findCustomerByNrc(String nrc);
}
