/***************************************************************************************
] * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.organization.persistence;

import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.organization.ORG001;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("OrganizationDAO")
public class OrganizationDAO extends BasicDAO implements IOrganizationDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Organization organization) throws DAOException {
		try {
			em.persist(organization);
			insertProcessLog(TableName.ORGANIZATION, organization.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Organization", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Organization organization) throws DAOException {
		try {
			em.merge(organization);
			updateProcessLog(TableName.ORGANIZATION, organization.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Organization", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Organization organization) throws DAOException {
		try {
			organization = em.merge(organization);
			em.remove(organization);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Organization", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Organization findById(String id) throws DAOException {
		Organization result = null;
		try {
			result = em.find(Organization.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Organization", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<ORG001> findAll_ORG001() throws DAOException {
		List<ORG001> result = null;
		try {
			StringBuffer buffer = new StringBuffer("SELECT NEW " + ORG001.class.getName() + "(");
			buffer.append("c.id, c.name, c.OwnerName, c.regNo, c.address.permanentAddress, c.address.township.name, ");
			buffer.append("c.contentInfo.phone, c.contentInfo.mobile, c.contentInfo.email) FROM Organization c ORDER BY c.name ASC");
			TypedQuery<ORG001> query = em.createQuery(buffer.toString(), ORG001.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all ORG001.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateActivePolicy(int policyCount, String organizationId) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE Organization o SET o.activePolicy = :activePolicy WHERE o.id = :id");
			q.setParameter("activePolicy", policyCount);
			q.setParameter("id", organizationId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update active policy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateActivedPolicyDate(Date activedDate, String organizationId) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE Organization o SET o.activedDate = :activedDate WHERE o.id = :id");
			q.setParameter("activedDate", activedDate);
			q.setParameter("id", organizationId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update active policy date", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExitOrganization(Organization organization) throws DAOException {
		boolean exist = false;
		String organizationName = organization.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(o.name) > 0) THEN TRUE ELSE FALSE END FROM Organization o ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',o.name,' ','')) = :name ");
			buffer.append(organization.getId() != null ? " AND o.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (organization.getId() != null)
				query.setParameter("id", organization.getId());
			query.setParameter("name", organizationName.toLowerCase());
			exist = (Boolean) query.getSingleResult();

			if (exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(o.id) > 0) THEN TRUE ELSE FALSE END FROM Organization o");
				buffer.append(" WHERE LOWER(FUNCTION('REPLACE',o.name,' ','')) = :name ");
				buffer.append(" AND LOWER(o.address.permanentAddress)= :address ");
				buffer.append("	AND o.address.township.id = :townshipId");
				buffer.append(organization.getId() != null ? " AND o.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (organization.getId() != null)
					query.setParameter("id", organization.getId());
				query.setParameter("name", organizationName.toLowerCase());
				query.setParameter("townshipId", organization.getAddress().getTownship().getId());
				query.setParameter("address", organization.getAddress().getPermanentAddress().toLowerCase());
				exist = (Boolean) query.getSingleResult();
			}
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

}
