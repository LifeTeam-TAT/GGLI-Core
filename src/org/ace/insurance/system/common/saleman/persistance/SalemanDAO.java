package org.ace.insurance.system.common.saleman.persistance;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.IdType;
import org.ace.insurance.common.SaleManCriteria;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.saleman.history.SaleManHistory;
import org.ace.insurance.system.common.saleman.persistance.interfaces.ISalemanDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("SalemanDAO")
public class SalemanDAO extends BasicDAO implements ISalemanDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(SaleMan saleMan) throws DAOException {
		try {
			if (!checkExistingSaleMan(saleMan)) {
				em.persist(saleMan);
				insertProcessLog(TableName.SALEMAN, saleMan.getId());
				em.flush();
			} else {
				throw new SystemException(null, saleMan.getName().getFullName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert SaleMan", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SaleMan saleMan) throws DAOException {
		try {
			if (!checkExistingSaleMan(saleMan)) {
				em.merge(saleMan);
				updateProcessLog(TableName.SALEMAN, saleMan.getId());
				em.flush();
			} else {
				throw new SystemException(null, saleMan.getName().getFullName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update SaleMan", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(SaleMan saleMan) throws DAOException {
		try {
			saleMan = em.merge(saleMan);
			em.remove(saleMan);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SaleMan", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SaleMan findById(String id) throws DAOException {
		SaleMan result = null;
		try {
			result = em.find(SaleMan.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find SaleMan", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SaleMan> findAll() throws DAOException {
		List<SaleMan> result = null;
		try {
			Query q = em.createNamedQuery("SaleMan.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SaleMan", pe);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SaleMan> findSaleManByCriteria(SaleManCriteria saleManCriteria) {
		List<SaleMan> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT s FROM SaleMan s ");
			if (saleManCriteria.getSaleManCriteriaItems() != null) {
				switch (saleManCriteria.getSaleManCriteriaItems()) {
					case FIRSTNAME: {
						query.append("WHERE s.name.firstName like :name");
						break;
					}
					case MIDDLENAME: {
						query.append("WHERE s.name.middleName like :name");
						break;
					}
					case LASTNAME: {
						query.append("WHERE s.name.lastName like :name");
						break;
					}
					case FULLNAME: {
						query.append("WHERE CONCAT(s.name.firstName, ' ',s.name.middleName, ' ',s.name.lastName) like :name");
						break;
					}
					case CODENO: {
						query.append("WHERE s.codeNo = :codeNo");
						break;
					}
					case BRANCH: {
						query.append("WHERE s.branch.id = :branchId");
						break;
					}
					default:
						break;
				}
			}

			Query q = em.createQuery(query.toString());
			if (saleManCriteria.getSaleManCriteriaItems() != null) {
				switch (saleManCriteria.getSaleManCriteriaItems()) {
					case FIRSTNAME:
					case MIDDLENAME:
					case LASTNAME:
					case FULLNAME: {
						q.setParameter("name", "%" + saleManCriteria.getCriteriaValue() + "%");
						break;
					}
					case CODENO: {
						q.setParameter("codeNo", saleManCriteria.getCriteriaValue());
						break;
					}
					case BRANCH: {
						q.setParameter("branchId", saleManCriteria.getCriteriaValue());
						break;
					}
				}
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all SaleMan by criteria", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean checkExistingSaleMan(SaleMan saleMan) throws DAOException {
		boolean exist = false;
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			if (!saleMan.getIdType().equals(IdType.STILL_APPLYING)) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.idNo) > 0) THEN TRUE ELSE FALSE END FROM SaleMan c");
				buffer.append(" WHERE c.idNo = :fullIdNo ");
				buffer.append(saleMan.getId() != null ? "AND c.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (saleMan.getId() != null)
					query.setParameter("id", saleMan.getId());
				query.setParameter("fullIdNo", saleMan.getIdNo());
				exist = (Boolean) query.getSingleResult();
			}

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.id) > 0) THEN TRUE ELSE FALSE END FROM SaleMan c");
				buffer.append(" WHERE c.id != :id AND c.initialId = :initialId AND c.name = :name  AND c.idNo = :idNo");
				buffer.append(" AND c.dateOfBirth = :dateOfBirth ");
				query = em.createQuery(buffer.toString());
				query.setParameter("id", saleMan.getId());
				query.setParameter("idNo", saleMan.getIdNo());
				query.setParameter("initialId", saleMan.getInitialId());
				query.setParameter("name", saleMan.getName());
				query.setParameter("dateOfBirth", saleMan.getDateOfBirth());
				exist = (Boolean) query.getSingleResult();
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing NRC No.", pe);
		}

		return exist;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertsaleManHistory(SaleManHistory saleManhistory) throws DAOException {
		try {

			em.persist(saleManhistory);
			insertProcessLog(TableName.SALEMANHISTORY, saleManhistory.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert SaleManHistory", pe);
		}
	}

}
