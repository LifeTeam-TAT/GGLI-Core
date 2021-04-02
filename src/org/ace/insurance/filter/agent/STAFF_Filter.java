package org.ace.insurance.filter.agent;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.ace.insurance.filter.agent.interfaces.ISTAFF_Filter;
import org.ace.insurance.filter.cirteria.CRISTAFF001;
import org.ace.java.component.persistence.BasicDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component(value = "STAFF_Filter")
public class STAFF_Filter extends BasicDAO implements ISTAFF_Filter {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<STAFF001> find(CRISTAFF001 item, String value) {
		List<STAFF001> result = new ArrayList<STAFF001>();
		StringBuffer bufferer = new StringBuffer("SELECT c.id,");
		bufferer.append("CONCAT(TRIM(c.name.firstName), ' ', TRIM(c.name.middleName), ' ', TRIM(c.name.lastName)), ");
		bufferer.append("c.idNo,");
		bufferer.append("CONCAT(TRIM(c.residentAddress.residentAddress), ', ', TRIM(t.name), ', ', TRIM(t.province.name), ', ', TRIM(t.province.country.name)), ");
		bufferer.append("c.branch.name ");
		bufferer.append("FROM Staff c LEFT OUTER JOIN c.residentAddress.township t ");
		switch (item) {
			case FIRSTNAME: {
				bufferer.append("WHERE c.status = '1' AND c.name.firstName LIKE '%" + value + "%'");
			}
				break;
			case MIDDLENAME: {
				bufferer.append("WHERE c.status = '1' AND c.name.middleName LIKE '%" + value + "%'");
			}
				break;
			case LASTNAME: {
				bufferer.append("WHERE c.status = '1' AND c.name.lastName LIKE '%" + value + "%'");
			}
				break;
			case NRCNO: {
				bufferer.append("WHERE c.status = '1' AND c.idNo = '" + value + "' AND c.idType = org.ace.insurance.common.IdType.NRCNO");
			}
				break;
			case FRCNO: {
				bufferer.append("WHERE c.status = '1' AND c.idNo = '" + value + "' AND c.idType = org.ace.insurance.common.IdType.FRCNO");
			}
				break;
			case PASSPORTNO: {
				bufferer.append("WHERE c.status = '1' AND c.idNo = '" + value + "' AND c.idType = org.ace.insurance.common.IdType.PASSPORTNO");
			}
				break;
			default: {
				bufferer.append("WHERE c.status = '1' AND CONCAT(c.name.firstName, ' ', c.name.middleName, ' ', c.name.lastName) like '%" + value + "%' "
						+ "OR CONCAT(c.name.firstName, ' ', c.name.middleName, c.name.lastName) like '%" + value + "%'");
			}
		}

		Query query = em.createQuery(bufferer.toString());
		List<Object[]> objectList = query.getResultList();
		String id = null;
		//String agentCode = null;
		//String licenseNo = null;
		String name = null;
		String idNo = null;
		String address = null;
		String branch = null;
		for (Object[] obj : objectList) {
			id = (String) obj[0];
			//agentCode = (String) obj[1];
			//licenseNo = (String) obj[2];
			name = (String) obj[1];
			idNo = (String) obj[2];
			address = (String) obj[3];
			branch = (String) obj[4];
			result.add(new STAFF001(id, name, idNo, address, branch));
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<STAFF001> find(int maxResult) {
		List<STAFF001> result = new ArrayList<STAFF001>();
		StringBuffer bufferer = new StringBuffer("SELECT c.id,");
		bufferer.append("CONCAT(TRIM(c.name.firstName), ' ', TRIM(c.name.middleName), ' ', TRIM(c.name.lastName)), ");
		bufferer.append("c.idNo,");
		bufferer.append("CONCAT(TRIM(c.residentAddress.residentAddress), ', ', TRIM(t.name), ', ', TRIM(t.province.name), ', ', TRIM(t.province.country.name)), ");
		bufferer.append("c.branch.name ");
		bufferer.append("FROM Staff c LEFT OUTER JOIN c.residentAddress.township t  ORDER BY c.id DESC");
		Query query = em.createQuery(bufferer.toString());
		query.setMaxResults(maxResult);
		List<Object[]> objectList = query.getResultList();
		String id = null;
		String name = null;
		String idNo = null;
		String address = null;
		String branch = null;
		for (Object[] obj : objectList) {
			id = (String) obj[0];
			name = (String) obj[1];
			idNo = (String) obj[2];
			address = (String) obj[3];
			branch = (String) obj[4];
			result.add(new STAFF001(id, name, idNo, address, branch));
		}
		return result;
	}
}