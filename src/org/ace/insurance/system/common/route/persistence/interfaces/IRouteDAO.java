/***************************************************************************************
 * @author <<Chan Mrate Ko Ko>>
 * @Date 2015-08-05
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.route.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.route.Route;
import org.ace.insurance.system.common.route.Route001;
import org.ace.java.component.persistence.exception.DAOException;

public interface IRouteDAO {

	public Route insert(Route route) throws DAOException;

	public Route update(Route route) throws DAOException;

	public void delete(Route route) throws DAOException;

	public List<Route> findAll() throws DAOException;

	public List<Route001> findAllRoute() throws DAOException;

	public Route findById(String id) throws DAOException;

	public List<Route> findByCriteria(String criteria) throws DAOException;

	public List<Route001> findByCriteria001(String criteria) throws DAOException;

}
