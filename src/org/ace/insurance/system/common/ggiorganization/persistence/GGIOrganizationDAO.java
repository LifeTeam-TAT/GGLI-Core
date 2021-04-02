package org.ace.insurance.system.common.ggiorganization.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.dao.interfaces.IGGIOrganizationDAO;
import org.ace.insurance.system.common.organization.ORG001;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("GGIOrganizationDAO")
public class GGIOrganizationDAO extends BasicDAO implements IGGIOrganizationDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(GGIOrganization organization) {
		try {
			if (!checkExistingorganization(organization)) {
				em.persist(organization);
			} else {
				throw new SystemException(null, organization.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert organization (organization = " + organization.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(GGIOrganization organization) {
		try {
			organization = em.merge(organization);
			em.remove(organization);
		} catch (PersistenceException e) {
			throw translate("Failed to delete organization User(organization = " + organization.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(GGIOrganization organization) {
		try {
			em.merge(organization);
		} catch (PersistenceException e) {
			throw translate("Failed to update organization User(organization = " + organization.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GGIOrganization> findAllGGIOrganization() {
		List<GGIOrganization> result = null;
		try {
			Query q = em.createQuery("select e from GGIOrganization e ");
			result = q.getResultList();
		} catch (NoResultException pe) {
			return null;

		} catch (PersistenceException e) {
			throw translate("Failed to  find allss organization ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingorganization(GGIOrganization organization) throws DAOException {
		boolean exist = false;
		String organizationName = organization.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM GGIOrganization c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(organization.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (organization.getId() != null)
				query.setParameter("id", organization.getId());
			query.setParameter("name", organizationName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<ORG001> findAll_ORG001() throws DAOException {
		List<ORG001> result = null;
		try {
			StringBuffer buffer = new StringBuffer("SELECT NEW " + ORG001.class.getName() + "(");
			buffer.append("c.id, c.name, c.OwnerName, c.regNo, c.address.permanentAddress, c.address.township.name, ");
			buffer.append("c.contentInfo.phone, c.contentInfo.mobile, c.contentInfo.email) FROM GGIOrganization c ORDER BY c.name ASC");
			TypedQuery<ORG001> query = em.createQuery(buffer.toString(), ORG001.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all ORG001.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public GGIOrganization findById(String id) throws DAOException {
		GGIOrganization result = null;
		try {
			result = em.find(GGIOrganization.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Organization", pe);
		}
		return result;
	}
}
