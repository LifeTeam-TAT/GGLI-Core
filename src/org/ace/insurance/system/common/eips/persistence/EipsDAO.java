package org.ace.insurance.system.common.eips.persistence;

import java.util.List;

import javax.persistence.TypedQuery;

import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.eips.persistence.interfaces.IEipsDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("EipsDAO")
public class EipsDAO extends BasicDAO implements IEipsDAO{

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(Eips eips) throws DAOException {
		if (eips.getId() != null) {
			em.merge(eips);
		} else {
			em.persist(eips);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Eips eips) throws DAOException {
		em.remove(findById(eips.getId()));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Eips findById(String id) throws DAOException {
		return em.find(Eips.class, id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Eips> findAll() throws DAOException {
		TypedQuery<Eips> q = em.createNamedQuery("Eips.findAll", Eips.class);
		em.flush();
		return q.getResultList();
	}
}
