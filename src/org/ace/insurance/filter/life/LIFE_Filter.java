package org.ace.insurance.filter.life;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.filter.life.interfaces.ILIFE_Filter;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LIFE_Filter")
public class LIFE_Filter extends BasicDAO implements ILIFE_Filter {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LFP001> find(LifeFilterCriteria criteria) throws DAOException {
		List<LFP001> results = new ArrayList<>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + LFP001.class.getName());
			buffer.append("(l.id, l.policyNo, c.initialId, c.name, o.name, l.activedPolicyStartDate, l.activedPolicyEndDate, l.policyStatus) ");
			buffer.append("FROM LifePolicy l lEFT OUTER JOIN l.customer c LEFT OUTER JOIN l.organization o LEFT JOIN l.policyInsuredPersonList p ");
			buffer.append("WHERE p.product.id = :productId ");
			if (criteria.getItem() != null) {
				switch (criteria.getItem()) {
					case POLICY_NO:
						buffer.append("AND l.policyNo LIKE :param ");
						break;

					case CUSTOMER_NAME:
						buffer.append(
								"AND CONCAT(TRIM(c.initialId),' ' ,TRIM(c.name.firstName),' ', TRIM(CONCAT(TRIM(c.name.middleName), ' ')), TRIM(c.name.lastName)) LIKE :param ");
						break;

					case ORGANIZATION_NAME:
						buffer.append("AND o.name like :param ");
						break;

				}
			}

			Query query = em.createQuery(buffer.toString());
			query.setParameter("productId", criteria.getProductId());
			if (criteria.getItem() != null)
				query.setParameter("param", "%" + criteria.getValue() + "%");

			query.setMaxResults(30);
			results = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy by LFP001 : ", pe);
		}

		RegNoSorter<LFP001> regNoSorter = new RegNoSorter<LFP001>(results);
		return regNoSorter.getSortedList();
	}
}
