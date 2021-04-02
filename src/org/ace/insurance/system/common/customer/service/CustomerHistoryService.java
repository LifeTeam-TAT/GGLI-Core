package org.ace.insurance.system.common.customer.service;

import javax.annotation.Resource;

import org.ace.insurance.system.common.customer.CustomerHistory;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerHistoryDAO;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerHistoryService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "CustomerHistoryService")
public class CustomerHistoryService extends BaseService implements ICustomerHistoryService {

	@Resource(name = "CustomerHistoryDAO")
	private ICustomerHistoryDAO customerHistoryDAO;

	// @Resource(name = "CustomIDGenerator")
	// private ICustomIDGenerator customIDGenerator;

	@Transactional(propagation = Propagation.REQUIRED)
	public CustomerHistory addCustomerHistory(CustomerHistory customer) throws DAOException {
		try {
			// customer.setPrefix(getPrefix(CustomerHistory.class));
			customerHistoryDAO.insert(customer);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new CustomerHistory", e);
		}
		return customer;
	}

}
