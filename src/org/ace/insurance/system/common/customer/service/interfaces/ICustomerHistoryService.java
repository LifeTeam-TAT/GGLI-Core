package org.ace.insurance.system.common.customer.service.interfaces;

import org.ace.insurance.system.common.customer.CustomerHistory;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICustomerHistoryService {

	public CustomerHistory addCustomerHistory(CustomerHistory customer) throws DAOException;

}
