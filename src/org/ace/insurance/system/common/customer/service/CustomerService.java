/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.customer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.CustomerCriteria;
import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.OfficeAddress;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.medical.proposal.CustomerInfoStatus;
import org.ace.insurance.report.customer.CustomerInformationCriteria;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.country.service.interfaces.ICountryService;
import org.ace.insurance.system.common.customer.CUST001;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.CustomerHistory;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerHistoryService;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.industry.service.interfaces.IIndustryService;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.qualification.service.interfaces.IQualificationService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.religion.service.interfaces.IReligionService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.user.User;
import org.ace.insurance.ws.customer.model.CustomerDto;
import org.ace.insurance.ws.customer.model.FamilyInfoDto;
import org.ace.insurance.ws.customer.model.OfficeAddressDto;
import org.ace.insurance.ws.customer.model.PermanentAddressDto;
import org.ace.insurance.ws.customer.model.ResidentAddressDto;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "CustomerService")
public class CustomerService extends BaseService implements ICustomerService {

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Autowired
	private ITownshipService townshipService;

	@Autowired
	private IQualificationService qualificationService;

	@Autowired
	private IBankService bankService;

	@Autowired
	private IBankBranchService bankBranchService;

	@Autowired
	private IBranchService branchService;

	@Autowired
	private IReligionService religionService;

	@Autowired
	private IRelationShipService relationShipService;

	@Autowired
	private IOccupationService occupationService;

	@Autowired
	private IIndustryService industryService;

	@Autowired
	private ICountryService countryService;

	@Autowired
	private ICustomerHistoryService customerhistoryService;

	@Transactional(propagation = Propagation.REQUIRED)
	public Customer addNewCustomer(Customer customer) {
		try {
			customer.setPrefix(getPrefix(Customer.class));
			customerDAO.insert(customer);
			customer = customerDAO.findById(customer.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Customer", e);
		}
		return customer;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewCustomer(CustomerDto customerDto, User user) throws SystemException {
		try {
			Customer customer = createCustomerEntity(customerDto);
			customer.setPrefix(getPrefix(Customer.class, user));
			customerDAO.insert(customer, user);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to create a new customer", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewCustomerByList(List<Customer> customerList) {
		try {
			for (Customer customer : customerList) {
				customer.setPrefix(getPrefix(Customer.class));
			}
			customerDAO.insert(customerList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Customer", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Customer updateCustomer(Customer customer) {
		try {
			customer = customerDAO.update(customer);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Customer", e);
		}
		return customer;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalInsuredPerson(ProposalInsuredPerson p) {
		try {
			customerDAO.updateProposalInsuredPerson(p);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Customer", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyInsuredPerson(PolicyInsuredPerson p) {
		try {
			customerDAO.updatePolicylInsuredPerson(p);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Customer", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteCustomer(Customer customer) throws SystemException {
		try {
			customerDAO.delete(customer);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a Customer", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Customer findCustomerById(String id) {
		Customer result = null;
		try {
			result = customerDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Customer (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean checkExistingCustomer(Customer customer) {

		boolean result = false;
		try {
			result = customerDAO.checkExistingCustomer(customer);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to check a Customer (ID : " + customer.getFullName() + ")", e);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProposalInsuredPerson> findProposalInsuredPersonById(String id) {
		List<ProposalInsuredPerson> resultList = null;
		try {
			resultList = customerDAO.findProposalInsuredPersonByCustomerId(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Customer (ID : " + id + ")", e);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PolicyInsuredPerson> findPolicyInsuredPersonById(String id) {
		List<PolicyInsuredPerson> resultList = null;
		try {
			resultList = customerDAO.findPolicyInsuredPersonByCustomerId(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Customer (ID : " + id + ")", e);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CUST001> findCustomerByCriteria(CustomerCriteria criteria, int max) {
		List<CUST001> result = null;
		try {
			result = customerDAO.findByCriteria(criteria, max);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Customer (criteriaValue : " + criteria.getCriteriaValue() + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Customer> findCustomerByCriteria(CustomerCriteria criteria) {
		List<Customer> result = null;
		try {
			result = customerDAO.findByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Customer (criteriaValue : " + criteria.getCriteriaValue() + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Customer> findCustomerByCustomerInformationCriteria(CustomerInformationCriteria criteria) {
		List<Customer> result = null;
		try {
			result = customerDAO.findByCustomerInformationCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Customer by CustomerInformationCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateActivePolicy(int policyCount, String customerId) {
		try {
			customerDAO.updateActivePolicy(policyCount, customerId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update activePolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Customer findCustomerByInsuredPerson(Name name, String idNo, String fatherName, Date dob) {
		Customer result = null;
		try {
			result = customerDAO.findCustomerByInsuredPerson(name, idNo, fatherName, dob);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Customer by CustomerInformationCriteria", e);
		}
		return result;
	}

	private Customer createCustomerEntity(CustomerDto customerDto) {
		Customer customer = new Customer(customerDto);

		OfficeAddressDto officeAddressDto = customerDto.getOfficeAddress();
		if (officeAddressDto != null) {
			OfficeAddress officeAddress = new OfficeAddress();
			officeAddress.setOfficeAddress(officeAddressDto.getOfficeAddress());
			String townshipId = officeAddressDto.getTownshipId();
			if (townshipId != null) {
				Township township = townshipService.findTownshipById(townshipId);
				officeAddress.setTownship(township);
			}
			customer.setOfficeAddress(officeAddress);
		}

		PermanentAddressDto permanentAddressDto = customerDto.getPermanentAddress();
		if (permanentAddressDto != null) {
			PermanentAddress permanentAddress = new PermanentAddress();
			String townshipId = permanentAddressDto.getTownshipId();
			if (townshipId != null) {
				Township township = townshipService.findTownshipById(townshipId);
				permanentAddress.setTownship(township);
			}
			customer.setPermanentAddress(permanentAddress);
		}

		ResidentAddressDto residentAddressDto = customerDto.getResidentAddress();
		if (residentAddressDto != null) {
			ResidentAddress residentAddress = new ResidentAddress();
			residentAddress.setResidentAddress(residentAddressDto.getResidentAddress());
			String townshipId = residentAddressDto.getTownshipId();
			if (townshipId != null) {
				Township township = townshipService.findTownshipById(townshipId);
				residentAddress.setTownship(township);
			}
			customer.setResidentAddress(residentAddress);
		}

		if (customerDto.getFamilyInfo() != null) {
			List<FamilyInfo> familyInfoList = new ArrayList<FamilyInfo>();

			for (FamilyInfoDto familyInfoDto : customerDto.getFamilyInfo()) {
				FamilyInfo familyInfo = new FamilyInfo(familyInfoDto);

				String relationshipId = familyInfoDto.getRelationShipId();
				if (relationshipId != null) {
					RelationShip relationShip = relationShipService.findRelationShipById(relationshipId);
					familyInfo.setRelationShip(relationShip);
				}

				String occupationId = familyInfoDto.getOccupationId();
				if (occupationId != null) {
					Occupation occupation = occupationService.findOccupationById(occupationId);
					familyInfo.setOccupation(occupation);
				}

				String industryId = familyInfoDto.getIndustryId();
				if (industryId != null) {
					Industry industry = industryService.findIndustryById(industryId);
					familyInfo.setIndustry(industry);
				}

				familyInfoList.add(familyInfo);
			}
			customer.setFamilyInfo(familyInfoList);
		}

		String branchId = customerDto.getBranchId();
		if (branchId != null) {
			Branch branch = branchService.findBranchById(branchId);
			customer.setBranch(branch);
		}

		String qualificationId = customerDto.getQualificationId();
		if (qualificationId != null) {
			Qualification qualification = qualificationService.findQualificationById(qualificationId);
			customer.setQualification(qualification);
		}

		String bankBranchId = customerDto.getBankBranchId();
		if (bankBranchId != null) {
			BankBranch bankBranch = bankBranchService.findBankBranchById(bankBranchId);

			customer.setBankBranch(bankBranch);
		}

		String religionId = customerDto.getReligionId();
		if (religionId != null) {
			Religion religion = religionService.findReligionById(religionId);
			customer.setReligion(religion);
		}

		String occupationId = customerDto.getOccupationId();
		if (occupationId != null) {
			Occupation occupation = occupationService.findOccupationById(occupationId);
			customer.setOccupation(occupation);
		}

		String industryId = customerDto.getIndustryId();
		if (industryId != null) {
			Industry industry = industryService.findIndustryById(industryId);
			customer.setIndustry(industry);
		}

		String countryId = customerDto.getCountryId();
		if (countryId != null) {
			Country country = countryService.findCountryById(countryId);
			customer.setCountry(country);
		}

		return customer;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewCustomerStatus(CustomerInfoStatus customerStatus) {
		try {
			customerDAO.insertCustomerStatus(customerStatus);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Customer", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<CustomerInfoStatus> findInfoStatusByCustomerId(String id) throws DAOException {
		List<CustomerInfoStatus> customerInfoStatusList = null;
		try {
			customerInfoStatusList = customerDAO.findInfoStatusByCustomerId(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Customer by CustomerInformationCriteria", e);
		}
		return customerInfoStatusList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Customer findCustomerByRefMobileUserId(String id) {
		Customer customer = null;
		try {
			customer = customerDAO.findByRefMobileUserId(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Customer by RefMobileUserId", e);
		}

		return customer;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void CreateHistoryAndRemoveCustomerById(String customerId) throws SystemException {
		try {
			Customer customer = findCustomerById(customerId);
			CustomerHistory customerhistory = new CustomerHistory(customer);
			customerhistoryService.addCustomerHistory(customerhistory);
			deleteCustomer(customer);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Fail to delete customer", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Customer findCustomerByNrc(String nrc) {
		
		Customer customer = null;
		try {
			customer = customerDAO.findCustomerByNrc(nrc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Customer by RefMobileUserId", e);
		}

		return customer;
	}
}