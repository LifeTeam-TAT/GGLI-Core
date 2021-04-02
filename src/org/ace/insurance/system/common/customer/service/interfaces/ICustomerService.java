/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.customer.service.interfaces;

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
import org.ace.insurance.ws.customer.model.CustomerDto;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICustomerService {
	public Customer addNewCustomer(Customer Customer);

	public void addNewCustomer(CustomerDto customerDto, User user) throws SystemException;

	public void addNewCustomerByList(List<Customer> customerList);

	public Customer updateCustomer(Customer Customer);

	public void deleteCustomer(Customer Customer);

	public Customer findCustomerById(String id);

	public List<CUST001> findCustomerByCriteria(CustomerCriteria criteria, int max);

	public List<Customer> findCustomerByCriteria(CustomerCriteria criteria);

	public List<Customer> findCustomerByCustomerInformationCriteria(CustomerInformationCriteria criteria);

	public void updateActivePolicy(int policyCount, String customerId);

	public List<ProposalInsuredPerson> findProposalInsuredPersonById(String id);

	public void updateProposalInsuredPerson(ProposalInsuredPerson p);

	public void updatePolicyInsuredPerson(PolicyInsuredPerson p);

	public List<PolicyInsuredPerson> findPolicyInsuredPersonById(String id);

	public Customer findCustomerByInsuredPerson(Name name, String idNo, String fatherName, Date dob);

	public void addNewCustomerStatus(CustomerInfoStatus customerStatus);

	public List<CustomerInfoStatus> findInfoStatusByCustomerId(String id) throws DAOException;

	public boolean checkExistingCustomer(Customer customer);

	public Customer findCustomerByRefMobileUserId(String id);

	void CreateHistoryAndRemoveCustomerById(String customerId) throws SystemException;
	
	public Customer findCustomerByNrc(String nrc);
}
