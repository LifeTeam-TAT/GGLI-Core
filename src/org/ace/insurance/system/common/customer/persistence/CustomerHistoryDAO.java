package org.ace.insurance.system.common.customer.persistence;

import javax.persistence.PersistenceException;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.customer.CustomerHistory;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerHistoryDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("CustomerHistoryDAO")
public class CustomerHistoryDAO extends BasicDAO implements ICustomerHistoryDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(CustomerHistory customer) throws DAOException {
		try {

			em.persist(customer);
			insertProcessLog(TableName.CUSTOMERHISTORY, customer.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert CustomerHistory", pe);
		}
	}

}
