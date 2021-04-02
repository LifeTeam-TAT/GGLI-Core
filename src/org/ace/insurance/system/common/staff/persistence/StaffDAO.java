package org.ace.insurance.system.common.staff.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.persistence.interfaces.IStaffDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("StaffDAO")
public class StaffDAO extends BasicDAO implements IStaffDAO{

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(Staff staff) throws DAOException {
		
		if (staff.getId() != null) {
			em.merge(staff);
		} else {
			//TODO uncomment this after fixing checkExistingStaff issue
//			if (!checkExistingStaff(staff)) {
				em.persist(staff);
//			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Staff staff) throws DAOException {
		em.remove(findById(staff.getId()));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Staff findById(String id) throws DAOException {
		TypedQuery<Staff> q = em.createNamedQuery("staff.findById", Staff.class);
		q.setParameter("id", id);
		em.flush();
		return q.getSingleResult();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Staff> findAll() throws DAOException {
		TypedQuery<Staff> q = em.createNamedQuery("staff.findAll", Staff.class);
		em.flush();
		return q.getResultList();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Staff> findStaffWithGGIOrganization(String id) throws DAOException {
		TypedQuery<Staff> q = em.createNamedQuery("staff.findStaffWithGGIOrganization", Staff.class);
		q.setParameter("ggiId", id);
		return q.getResultList();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private boolean checkExistingStaff(Staff staff) throws DAOException {
		boolean exist = false;
		String staffName = staff.getFullName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			// FIXME not working for staff showing invalid sql syntax error
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Staff c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(staff.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (staff.getId() != null)
				query.setParameter("id", staff.getId());
			query.setParameter("name", staffName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
