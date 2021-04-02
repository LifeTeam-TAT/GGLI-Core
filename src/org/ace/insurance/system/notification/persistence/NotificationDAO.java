package org.ace.insurance.system.notification.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.notification.Notification;
import org.ace.insurance.system.notification.NotificationCriteria;
import org.ace.insurance.system.notification.persistence.interfaces.INotificationDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("NotificationDAO")
public class NotificationDAO extends BasicDAO implements INotificationDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Notification> findByCriteria(NotificationCriteria criteria) throws DAOException {
		List<Notification> result = new ArrayList<Notification>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT n from Notification n WHERE n.id IS NOT NULL");
			if (criteria.getId() != null && !criteria.getId().isEmpty())
				buffer.append(" AND n.id = :id");

			if (criteria.getTitle() != null && !criteria.getTitle().isEmpty())
				buffer.append(" AND n.title LIKE :title");

			if (criteria.getCreatedDate() != null)
				buffer.append(" AND n.recorder.createdDate >= :createdDate");

			if (!criteria.isApi())
				buffer.append(" AND n.isPush = 1");

			if (criteria.isApi() && criteria.getCreatedDate() != null && criteria.getEndDate() != null) {
				buffer.append(" AND ( n.recorder.createdDate >= :createdDate OR n.endDate >= :endDate )");
			}

			if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
				buffer.append(" AND ( n.startDate >= :startDate OR n.endDate <= :endDate )");
			} else {
				if (criteria.getStartDate() != null) {
					buffer.append(" AND n.startDate >= :startDate");
				}
				if (!criteria.isApi() && criteria.getEndDate() != null) {
					buffer.append(" AND n.endDate <= :endDate");
				}
			}

			Query query = em.createQuery(buffer.toString());

			if (criteria.getId() != null && !criteria.getId().isEmpty())
				query.setParameter("id", criteria.getId());

			if (criteria.getTitle() != null && !criteria.getTitle().isEmpty())
				query.setParameter("title", "%" + criteria.getTitle() + "%");

			if (criteria.getCreatedDate() != null)
				query.setParameter("createdDate", criteria.getCreatedDate());

			if (criteria.getStartDate() != null)
				query.setParameter("startDate", criteria.getStartDate());

			if (criteria.getEndDate() != null)
				query.setParameter("endDate", criteria.getEndDate());

			if (criteria.getMaxResult() > 0)
				query.setMaxResults(criteria.getMaxResult());

			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Notification By NotificationCriteria", pe);
		}
		return result;
	}
}
