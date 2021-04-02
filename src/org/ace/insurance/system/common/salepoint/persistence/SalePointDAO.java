package org.ace.insurance.system.common.salepoint.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.persistence.interfaces.ISalePointDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("SalePointDAO")
public class SalePointDAO extends BasicDAO implements ISalePointDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(SalePoint salePoint) throws DAOException {
		try {
			em.persist(salePoint);
			insertProcessLog(TableName.SALEPOINT, salePoint.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert SalePoint", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SalePoint salePoint) throws DAOException {
		try {
			em.merge(salePoint);
			updateProcessLog(TableName.SALEPOINT, salePoint.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SalePoint", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(SalePoint salePoint) throws DAOException {
		try {
			salePoint = em.merge(salePoint);
			em.remove(salePoint);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SalePoint", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public SalePoint findById(String id) throws DAOException {
		SalePoint result = null;
		try {
			result = em.find(SalePoint.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find SalePoint", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public SalePoint findByCode(String code) throws DAOException {
		SalePoint result = null;
		try {
			Query q = em.createNamedQuery("SalePoint.findByCode");
			q.setParameter("branchCode", code);
			result = (SalePoint) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find SalePoint", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SalePoint> findAllSalePointByBrach(String branchId) throws DAOException {
		List<SalePoint> result = null;
		try {
			Query q = em.createQuery("SELECT  s  FROM  SalePoint s WHERE s.branch.id = :branchId AND s.status = '1' order by s.name asc");
			q.setParameter("branchId", branchId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SalePoint", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SalePoint> findAllByStatus() throws DAOException {
		List<SalePoint> result = null;
		try {
			Query q = em.createQuery("SELECT s FROM  SalePoint s WHERE s.status = '1' order by s.name asc");
			result = q.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SalePoint", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SalePoint> findAll() throws DAOException {
		List<SalePoint> result = null;
		try {
			Query q = em.createNamedQuery("SalePoint.findAll");
			result = q.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SalePoint", pe);
		}
		return result;
	}

	@Override
	public boolean isAlreadyExist(SalePoint salePoint) throws DAOException {
		boolean exist = false;
		String salePointName = salePoint.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.salePointCode) > 0) THEN TRUE ELSE FALSE END FROM SalePoint c ");
			buffer.append(" WHERE c.salePointCode = :code ");
			buffer.append(salePoint.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (salePoint.getId() != null)
				query.setParameter("id", salePoint.getId());
			query.setParameter("code", salePoint.getSalePointCode());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.id) > 0) THEN TRUE ELSE FALSE END FROM SalePoint c");
				buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
				buffer.append(" AND LOWER(c.Address)= :address ");
				buffer.append("	AND c.township.id = :townshipId");
				buffer.append(salePoint.getId() != null ? " AND c.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (salePoint.getId() != null)
					query.setParameter("id", salePoint.getId());
				query.setParameter("name", salePointName.toLowerCase());
				query.setParameter("townshipId", salePoint.getTownship().getId());
				query.setParameter("address", salePoint.getAddress().toLowerCase());
				exist = (Boolean) query.getSingleResult();
			}
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

}