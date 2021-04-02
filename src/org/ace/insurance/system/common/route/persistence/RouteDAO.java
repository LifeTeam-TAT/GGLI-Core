/***************************************************************************************
 * @author <<Chan Mrate Ko Ko>>
 * @Date 2015-08-05
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.route.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.route.Route;
import org.ace.insurance.system.common.route.Route001;
import org.ace.insurance.system.common.route.persistence.interfaces.IRouteDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("RouteDAO")
public class RouteDAO extends BasicDAO implements IRouteDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Route insert(Route route) throws DAOException {
		try {
			em.persist(route);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Route(id = " + route.getId() + ")", pe);
		}
		return route;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Route update(Route route) throws DAOException {
		try {
			route = em.merge(route);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Route(id = " + route.getId() + ")", pe);
		}
		return route;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Route route) throws DAOException {
		try {
			route = em.merge(route);
			em.remove(route);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Route(id = " + route.getId() + ")", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Route> findAll() {
		List<Route> result = null;
		try {
			Query q = em.createNamedQuery("Route.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Route.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Route findById(String id) throws DAOException {
		Route result = null;
		try {
			result = em.find(Route.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Route(ID = " + id + ")", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Route> findByCriteria(String criteria) throws DAOException {
		List<Route> result = null;
		try {
			Query q = em.createQuery("Select r from Route r where r.name Like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Route.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Route001> findAllRoute() {
		List<Route001> result = null;
		try {
			Query q = em.createQuery("SELECT NEW" + Route001.class.getName() + " (r.id, r.name, r.description ) FROM Route r ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Route.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Route001> findByCriteria001(String criteria) throws DAOException {
		List<Route001> result = null;
		try {
			Query q = em.createQuery("Select New" + Route001.class.getName() + " (r.id,r.name,r.description) FROM Route r where r.name Like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Route.", pe);
		}
		return result;
	}
}
