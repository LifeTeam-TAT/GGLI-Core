package org.ace.insurance.report.upr.persistence;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.report.upr.UPRReport;
import org.ace.insurance.report.upr.persistence.interfaces.IUPRReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("UPRReportDAO")
public class UPRReportDAO extends BasicDAO implements IUPRReportDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<UPRReport> findUPRReport(int startYear, int endYear, boolean isFinancialYear) throws DAOException, ParseException {
		List<UPRReport> resultList = new ArrayList<>();
		List<Object[]> objectList = new ArrayList<>();
		List<String> lifeList = Arrays.asList("LIFEPOLICY", "LIFEPOLICY_INSUREDPERSON_LINK", "LIFEPOLICYID");
		List<String> medicalList = Arrays.asList("MEDICALPOLICY", "MEDICALPOLICY_INSUREDPERSON_LINK", "MEDICALPOLICYID");
		List<String> personTravelList = Arrays.asList("PERSONTRAVEL_POLICY", "PERSONTRAVEL_POLICY_INFO", "PERSONTRAVELPOLICYINFOID");
		List<String> travelList = Arrays.asList("TRAVELPROPOSAL", "TRAVEL_EXPRESS", "TRAVELPROPOSALID");
		List<List<String>> tableNameList = new ArrayList<>();
		tableNameList.add(lifeList);
		tableNameList.add(medicalList);
		tableNameList.add(personTravelList);
		tableNameList.add(travelList);

		try {
			StringBuffer buffer = new StringBuffer();
			for (List<String> stringList : tableNameList) {
				if (buffer.length() > 0) {
					buffer.append(" UNION ");
				}
				if (stringList.get(0).equals("PERSONTRAVEL_POLICY")) {
					buffer.append("SELECT DISTINCT t0.NAME, t2.POLICYNO,t1.DEPARTUREDATE, t1.ARRIVALDATE, (t1.BASICTERMPREMIUM), t3.NAME, t1.ARRIVALDATE");
					buffer.append(" FROM " + stringList.get(0) + " t2 ");
					buffer.append(" LEFT OUTER JOIN " + stringList.get(1) + " t1 ON (t2." + stringList.get(2) + " = t1.ID) ");
					buffer.append(" LEFT OUTER JOIN PRODUCT t0 ON (t0.ID = t2.PRODUCTID)");
					buffer.append(" LEFT OUTER JOIN PAYMENTTYPE t3 ON (t3.ID = t2.PAYMENTTYPEID)");
					buffer.append(" WHERE t0.NAME IS NOT NULL");
					buffer.append(" AND t2.POLICYNO IS NOT NULL");
					if (isFinancialYear) {
						Date financialStartDate = null;
						Date financialEndDate = null;
						String startDate = "01/04/" + startYear;
						financialStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);

						String endDate = "31/03/" + endYear;
						financialEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);

						buffer.append(" AND (CONVERT (VARCHAR(10),t1.DEPARTUREDATE,120) >='" + DateUtils.formattedSqlDate(financialStartDate) + "'");
						buffer.append(" AND CONVERT (VARCHAR(10),t1.ARRIVALDATE,120) <='" + DateUtils.formattedSqlDate(financialEndDate) + "')");

					} else {
						buffer.append(" AND (DATEPART(YEAR, t1.DEPARTUREDATE) >=" + startYear);
						buffer.append(" AND DATEPART(YEAR, t1.ARRIVALDATE) <=" + endYear + ")");
					}
				} else if (stringList.get(0).equals("TRAVELPROPOSAL")) {
					buffer.append("SELECT DISTINCT t0.NAME, t2.PROPOSALNO,t2.FROMDATE, t2.TODATE, t1.NETPREMIUM, t3.NAME, t2.TODATE");
					buffer.append(" FROM " + stringList.get(0) + " t2 ");
					buffer.append(" LEFT OUTER JOIN " + stringList.get(1) + " t1 ON (t1." + stringList.get(2) + " = t2.ID) ");
					buffer.append(" LEFT OUTER JOIN PRODUCT t0 ON (t0.ID = t2.PRODUCTID)");
					buffer.append(" LEFT OUTER JOIN PAYMENTTYPE t3 ON (t3.ID = t2.PAYMENTTYPEID)");
					buffer.append(" WHERE t0.NAME IS NOT NULL");
					buffer.append(" AND t2.PROPOSALNO IS NOT NULL");
					if (isFinancialYear) {
						Date financialStartDate = null;
						Date financialEndDate = null;
						String startDate = "01/04/" + startYear;
						financialStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);

						String endDate = "31/03/" + endYear;
						financialEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);

						buffer.append(" AND (CONVERT (VARCHAR(10),t2.FROMDATE,120) >='" + DateUtils.formattedSqlDate(financialStartDate) + "'");
						buffer.append(" AND CONVERT (VARCHAR(10),t2.TODATE,120) <='" + DateUtils.formattedSqlDate(financialEndDate) + "')");

					} else {
						buffer.append(" AND (DATEPART(YEAR, t2.FROMDATE) >=" + startYear);
						buffer.append(" AND DATEPART(YEAR, t2.TODATE) <=" + endYear + ")");
					}
				} else {
					buffer.append(
							"SELECT DISTINCT t0.NAME, t2.POLICYNO, t2.ACTIVEDPOLICYSTARTDATE, t1.ENDDATE, (t1.BASICTERMPREMIUM + ISNULL(t1.ADDONTERMPREMIUM,0.0)), t3.NAME, t2.ACTIVEDPOLICYENDDATE");
					buffer.append(" FROM " + stringList.get(0) + " t2 ");
					buffer.append(" LEFT OUTER JOIN " + stringList.get(1) + " t1 ON (t1." + stringList.get(2) + " = t2.ID) ");
					buffer.append(" LEFT OUTER JOIN PRODUCT t0 ON (t0.ID = t1.PRODUCTID)");
					buffer.append(" LEFT OUTER JOIN PAYMENTTYPE t3 ON (t3.ID = t2.PAYMENTTYPEID)");
					buffer.append(" WHERE t0.NAME IS NOT NULL");
					buffer.append(" AND t2.POLICYNO IS NOT NULL");

					if (isFinancialYear) {
						Date financialStartDate = null;
						Date financialEndDate = null;
						String startDate = "01/04/" + startYear;
						financialStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);

						String endDate = "31/03/" + endYear;
						financialEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);

						buffer.append(" AND (CONVERT (VARCHAR(10),t2.ACTIVEDPOLICYSTARTDATE,120) >='" + DateUtils.formattedSqlDate(financialStartDate) + "'");
						buffer.append(" AND CONVERT (VARCHAR(10),t2.ACTIVEDPOLICYSTARTDATE,120) <='" + DateUtils.formattedSqlDate(financialEndDate) + "')");

					} else {
						buffer.append(" AND (DATEPART(YEAR, t2.ACTIVEDPOLICYSTARTDATE) >=" + startYear);
						buffer.append(" AND DATEPART(YEAR, t2.ACTIVEDPOLICYSTARTDATE) <=" + endYear + ")");
					}
				}
			}
			Query query = em.createNativeQuery(buffer.toString());

			objectList = query.getResultList();
			for (Object[] object : objectList) {
				resultList.add(new UPRReport(String.valueOf(object[0]), String.valueOf(object[1]), (Date) object[2], (Date) object[3], ((BigDecimal) object[4]).doubleValue(),
						String.valueOf(object[5]), (Date) object[6]));
			}
			return resultList;
		} catch (

		NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Fail to find UPR Report", pe);
		}

	}

}
