/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.branch.persistence;

import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.branch.BRA001;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.persistence.interfaces.IBranchDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BranchDAO")
public class BranchDAO extends BasicDAO implements IBranchDAO {

	@Resource(name = "CSC_BRANCH_CONFIG")
	private Properties properties;

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Branch branch) throws DAOException {
		try {
			em.persist(branch);
			insertProcessLog(TableName.BRANCH, branch.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Branch", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Branch branch) throws DAOException {
		try {
			em.merge(branch);
			updateProcessLog(TableName.BRANCH, branch.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Branch", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Branch branch) throws DAOException {
		try {
			branch = em.merge(branch);
			em.remove(branch);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Branch", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Branch findById(String id) throws DAOException {
		Branch result = null;
		try {
			Query q = em.createNamedQuery("Branch.findById");
			q.setParameter("id", id);
			result = (Branch) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Branch(Branch = " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Branch findByCode(String code) throws DAOException {
		Branch result = null;
		try {
			Query q = em.createNamedQuery("Branch.findByCode");
			q.setParameter("branchCode", code);
			result = (Branch) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Branch", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Branch findByCSCBranchCode(String cscBranchCode) throws DAOException {
		Branch result = null;
		try {
			String branchCode = properties.getProperty(cscBranchCode);
			result = findByCode(branchCode);
		} catch (PersistenceException pe) {
			throw translate("Failed to find Branch", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRA001> findAll_BRA001() throws DAOException {
		List<BRA001> result = null;
		try {
			TypedQuery<BRA001> query = em.createQuery("Select NEW " + BRA001.class.getName()
					+ "(b.id, b.name, b.branchCode, b.description,b.township.name,b.Address,b.entity) from Branch b ORDER BY b.branchCode ASC", BRA001.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Branch", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRA001> findAll_BRA001ByStatus() throws DAOException {
		List<BRA001> result = null;
		try {
			TypedQuery<BRA001> query = em.createQuery(
					"Select NEW " + BRA001.class.getName()
							+ "(b.id, b.name, b.branchCode, b.description,b.township.name,b.Address,b.entity) from Branch b WHERE b.status= '1' ORDER BY b.branchCode ASC",
					BRA001.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Branch", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Branch> findAllBranch() throws DAOException {
		List<Branch> result = null;
		try {
			TypedQuery<Branch> query = em.createNamedQuery("Branch.findAll", Branch.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Branch", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExitBranch(Branch branch) throws DAOException {
		boolean exist = false;
		String branchName = branch.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.name) > 0) THEN TRUE ELSE FALSE END FROM Branch b ");
			buffer.append(" WHERE LOWER(b.branchCode) = :code ");
			buffer.append(branch.getId() != null ? " AND b.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (branch.getId() != null)
				query.setParameter("id", branch.getId());
			query.setParameter("code", branch.getBranchCode());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.id) > 0) THEN TRUE ELSE FALSE END FROM Branch b");
				buffer.append(" WHERE LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
				buffer.append(" AND LOWER(b.Address)= :address ");
				buffer.append("	AND b.township.id = :townshipId");
				buffer.append(branch.getId() != null ? " AND b.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (branch.getId() != null)
					query.setParameter("id", branch.getId());
				query.setParameter("name", branchName.toLowerCase());
				query.setParameter("townshipId", branch.getTownship().getId());
				query.setParameter("address", branch.getAddress().toLowerCase());
				exist = (Boolean) query.getSingleResult();
			}
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExistBranchCode(String branchCode) throws DAOException {
		Branch branch = findByCode(branchCode);
		if (branch != null) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRA001> findBranchByEntityIdAndBranchId(String entityId, String branchId) throws DAOException {
		List<BRA001> result = null;
		try {
			TypedQuery<BRA001> query = em
					.createQuery("Select NEW " + BRA001.class.getName() + "(b.id, b.name, b.branchCode, b.description,b.township.name,b.Address,b.entity) from Branch b "
							+ " where b.entity.id=:entity and b.id= :branchId and b.status= '1' " + " ORDER BY b.branchCode ASC", BRA001.class);
			query.setParameter("entity", entityId);
			query.setParameter("branchId", branchId);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Branch By entity ", pe);
		}

		return result;
	}

}
