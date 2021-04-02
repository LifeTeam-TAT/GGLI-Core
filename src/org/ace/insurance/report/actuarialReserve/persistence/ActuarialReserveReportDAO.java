package org.ace.insurance.report.actuarialReserve.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Gender;
import org.ace.insurance.report.actuarialReserve.ActuarialReserveReport;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("ActuarialReserveReportDAO")
public class ActuarialReserveReportDAO extends BasicDAO implements IActuarialReserveReportDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ActuarialReserveReport> findActuarialReserveReport(List<String> productIdList, int startYear, int endYear) throws DAOException {

		List<ActuarialReserveReport> resultList = new ArrayList<>();
		List<Object[]> objectList = new ArrayList<>();

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT DISTINCT t2.policyNo,t2.ACTIVEDPOLICYSTARTDATE,t1.AGE,t1.GENDER,t1.PERIODOFMONTH/12,");
			buffer.append(" CASE WHEN (T3.MONTH = 12) THEN 1");
			buffer.append(" WHEN (T3.MONTH = 6) THEN 2");
			buffer.append(" WHEN (T3.MONTH = 3) THEN 4");
			buffer.append(" WHEN (T3.MONTH = 1) THEN 12");
			buffer.append(" ELSE 0 END,T2.ACTIVEDPOLICYENDDATE,T1.BASICTERMPREMIUM,T1.sumInsured");
			buffer.append(" FROM LIFEPOLICY T2");
			buffer.append(" LEFT OUTER JOIN LIFEPOLICY_INSUREDPERSON_LINK T1 ON (T1.LIFEPOLICYID = T2.ID) ");
			buffer.append(" LEFT OUTER JOIN PRODUCT t0 ON (t0.ID = t1.PRODUCTID)");
			buffer.append(" LEFT OUTER JOIN PAYMENTTYPE T3 ON (T3.ID = T2.PAYMENTTYPEID)");
			buffer.append(" WHERE (DATEPART(YEAR, T2.ACTIVEDPOLICYSTARTDATE) >=" + startYear + "");
			buffer.append(" AND DATEPART(YEAR, T2.ACTIVEDPOLICYSTARTDATE) <=" + endYear + ")");
			buffer.append(" AND T0.ID in ('ISPRD0030001000000002131032013','ISPRD003001000009575103082018')");

			Query query = em.createNativeQuery(buffer.toString());
			objectList = query.getResultList();
			for (Object[] object : objectList) {
				resultList.add(new ActuarialReserveReport((String) object[0], new Date(((java.sql.Timestamp) object[1]).getTime()), (int) object[2],
						Gender.valueOf(String.valueOf(object[3])), (int) object[4], (int) object[5], (Date) object[6], (BigDecimal) object[7], (BigDecimal) object[8]));
			}

			return resultList;

		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Fail to find Actuarial Reserve Report", pe);
		}

	}

}
