package org.ace.insurance.filter.customer;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.IdType;
import org.ace.insurance.filter.cirteria.CRIA001;
import org.ace.insurance.filter.customer.interfaces.ICUST_Filter;
import org.ace.java.component.persistence.BasicDAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component(value = "CUST_Filter")
public class CUST_Filter extends BasicDAO implements ICUST_Filter {

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CUST001> find(CRIA001 item, String value) {
		List<CUST001> results = null;
		try {
			StringBuffer buffer = new StringBuffer("SELECT NEW " + CUST001.class.getName());
			buffer.append("(c.id , c.initialId, c.name, c.fullIdNo, c.residentAddress.residentAddress, t.name, p.name, country.name) ");
			buffer.append("FROM Customer c LEFT JOIN c.residentAddress.township t LEFT JOIN t.province p LEFT JOIN p.country country");
			if (item != null) {
				switch (item) {
					case FULLNAME: {
						buffer.append(
								" WHERE FUNCTION( 'REPLACE', CONCAT(CONCAT(c.initialId, ''), CONCAT(c.name.firstName,''), CONCAT(c.name.middleName, ''), CONCAT(c.name.lastName , '') ), ' ', '')");
						buffer.append(" LIKE :value");
						break;
					}
					case FIRSTNAME: {
						buffer.append(" WHERE c.name.firstName LIKE :value");
						break;
					}
					case MIDDLENAME: {
						buffer.append(" WHERE c.name.middleName LIKE :value");
						break;
					}
					case LASTNAME: {
						buffer.append(" WHERE c.name.lastName LIKE :value");
						break;
					}
					case NRCNO:
					case FRCNO:
					case PASSPORTNO: {
						buffer.append(" WHERE c.fullIdNo = :value AND c.idType = :type");
						break;
					}
				}
			}

			buffer.append(" ORDER BY c.commonCreateAndUpateMarks.createdDate, c.commonCreateAndUpateMarks.updatedDate desc ");
			Query query = em.createQuery(buffer.toString());
			query.setMaxResults(30);

			if (item != null) {
				switch (item) {
					case PASSPORTNO:
						query.setParameter("type", IdType.PASSPORTNO);
						query.setParameter("value", value);
						break;
					case NRCNO:
						query.setParameter("type", IdType.NRCNO);
						query.setParameter("value", value);
						break;
					case FRCNO:
						query.setParameter("type", IdType.FRCNO);
						query.setParameter("value", value);
						break;
					default:
						query.setParameter("value", "%" + StringUtils.replace(value, " ", "") + "%");
						break;
				}
			}

			results = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Customer", pe);
		}
		return results;
	}

}