package org.ace.insurance.system.common.customer.persistence.interfaces;

import org.ace.insurance.system.common.customer.CustomerHistory;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICustomerHistoryDAO {

	public void insert(CustomerHistory customer) throws DAOException;

}
