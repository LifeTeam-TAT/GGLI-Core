package org.ace.insurance.payment.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.persistence.interfacs.ITLFDAO;
import org.ace.insurance.web.manage.report.common.AccountAndLifeDeptCancelReportDTO;
import org.ace.insurance.web.manage.report.common.AccountManualReceiptDTO;
import org.ace.insurance.web.manage.report.common.DailyIncomeReportDTO;
import org.ace.insurance.web.manage.report.common.SalePointDTO;
import org.ace.insurance.web.manage.report.common.TLFCriteria;
import org.ace.insurance.web.manage.report.common.TLFDTO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("TLFDAO")
public class TLFDAO extends BasicDAO implements ITLFDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public TLF insert(TLF tlf) throws DAOException {
		try {
			em.persist(tlf);
			insertProcessLog(TableName.TLF, tlf.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert TLF", pe);
		}
		return tlf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public long findConvertedTlf(Date actionDate) throws DAOException {
		long result = 0;
		try {
			Query q = em.createQuery("Select count(t) from TLF t where t.referenceType = org.ace.insurance.common.PolicyReferenceType.CSC_IMPORT and "
					+ " t.commonCreateAndUpateMarks.createdDate >= :actionDate1 AND t.commonCreateAndUpateMarks.createdDate <= :actionDate2 ");
			q.setParameter("actionDate1", DateUtils.resetStartDate(actionDate));
			q.setParameter("actionDate2", DateUtils.resetEndDate(actionDate));
			result = (long) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert TLF", pe);
		}
		return result;
	}

	// TODO FIXME Remove later
	/*
	 * public static void main(String args[]){ ApplicationContext context =new
	 * ClassPathXmlApplicationContext("spring-bean.xml"); TLFDAO tlfDAO =
	 * context.getBean("TLFDAO",TLFDAO.class); TLFCriteria criteria = new
	 * TLFCriteria(); }
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TLFDTO> findTLFDTOByCriteria(TLFCriteria criteria) throws DAOException {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT NEW " + TLFDTO.class.getName());
			buffer.append(" (t.tlfNo,coa.acode,t.homeAmount,t.localAmount,tr.tranCode,t.narration,t.salePoint,t.paymentChannel,t.commonCreateAndUpateMarks.createdDate)");
			buffer.append(" FROM TLF t INNER JOIN CurrencyChartOfAccount ccoa INNER JOIN Coa coa INNER JOIN TranType tr WHERE 1=1 AND t.coaId= ccoa.id AND ccoa.coaid=coa.id AND tr.id= tlf.trantypeId AND t.reverse ='false' AND t.paid ='true'");
			if (null != criteria.getStartDate()) {
				buffer.append(" AND t.commonCreateAndUpateMarks.createdDate >=:startDate ");
			}
			if (null != criteria.getEndDate()) {
				buffer.append(" AND t.commonCreateAndUpateMarks.createdDate <=:endDate");
			}
			if (null != criteria.getSalePoint()) {
				buffer.append(" AND t.salePoint.id =:salePointId");
			}
			TypedQuery<TLFDTO> query = em.createQuery(buffer.toString(), TLFDTO.class);
			if (null != criteria.getStartDate()) {
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (null != criteria.getEndDate()) {
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (null != criteria.getSalePoint()) {
				query.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			return query.getResultList();
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException e) {
			throw translate("Fail to findall TLFDTO by criteria", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<SalePointDTO> findSalePointDTOByCriteria(TLFCriteria criteria) throws DAOException {
		List<SalePointDTO> salePointDTOList = new ArrayList<>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT TLFNO, COAID,  NARRATION,NAME,PAYMENTCHANNEL,CREATEDDATE,");
			buffer.append(" ISNULL (CONVERT(DECIMAL(18,4),[CSDEBIT]),0.00) AS CASHDEBIT, ISNULL (CONVERT(DECIMAL(18,4), [CSCREDIT]),0.00) AS CASHCREDIT,");
			buffer.append(
					" ISNULL (CONVERT(DECIMAL(18,4),[TRDEBIT]),0.00) AS TRANSFERDEBIT, ISNULL (CONVERT(DECIMAL(18,4),[TRCREDIT]),0.00) AS TRANSFERCREDIT,POLICYNO,AGENTTRANSACTION,BRANCHNAME");
			buffer.append(" FROM(");
			buffer.append(" SELECT T0.TLFNO AS TLFNO, c.accode AS COAID, T0.HOMEAMOUNT AS HOMEAMOUNT, tr.TRANCODE AS TRANCODE , T0.NARRATION AS NARRATION,");
			buffer.append(
					" T1.NAME AS NAME,T0.PAYMENTCHANNEL AS PAYMENTCHANNEL, T0.CREATEDDATE AS CREATEDDATE, T0.POLICYNO AS POLICYNO,T0.AGENTTRANSACTION AS AGENTTRANSACTION,B1.NAME AS BRANCHNAME FROM TLF T0 INNER JOIN CCOA cc ON T0.CCOAID = cc.id INNER JOIN COA c ON c.id = cc.coaid INNER JOIN Trantype tr ON tr.id = T0.trantypeId , SALEPOINT T1,BRANCH B1,ENTITY E");
			buffer.append(" WHERE 1=1 AND T0.REVERSE = 0 AND T0.PAID = 1");

			if (null != criteria.getSalePoint()) {
				buffer.append(" AND T0.SALEPOINTID = '" + criteria.getSalePoint().getId() + "'");
			}
			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				buffer.append(" AND E.ID = '" + criteria.getEntity().getId() + "'");

			} else if (null != criteria.getBranch()) {
				buffer.append(" AND T0.BRANCHID = '" + criteria.getBranch().getId() + "'");
			}
			if (null != criteria.getPaymentChannel()) {
				buffer.append(" AND T0.PAYMENTCHANNEL = '" + criteria.getPaymentChannel() + "'");
			}
			Date startDate = DateUtils.resetStartDate(criteria.getStartDate());
			String formatedStartDate = DateUtils.formattedSqlDate(startDate);
			Date endDate = DateUtils.resetEndDate(criteria.getEndDate());
			String foramtedEndDate = DateUtils.formattedSqlDate(endDate);

			buffer.append(" AND T1.ID = T0.SALEPOINTID  AND   T0.BRANCHID = B1.ID AND B1.ENTITYSID=E.ID");
			buffer.append(" AND CONVERT (VARCHAR(10), T0.CREATEDDATE, 120) >= '" + formatedStartDate + "' AND ");
			buffer.append(" CONVERT (VARCHAR(10), T0.CREATEDDATE, 120) <= '" + foramtedEndDate + "'");
			buffer.append(" ) RAW");
			buffer.append(" PIVOT (");
			buffer.append(" SUM(HOMEAMOUNT)");
			buffer.append(" FOR TRANCODE IN ([CSDEBIT], [CSCREDIT], [TRDEBIT], [TRCREDIT])");
			buffer.append(" ) AS PIVOTTABLE ORDER BY TLFNO,");
			buffer.append(" CASE WHEN COAID LIKE 'A%' THEN 1");
			buffer.append(" WHEN COAID LIKE 'I%'  THEN 2");
			buffer.append(" WHEN COAID LIKE 'L%'  THEN 3");
			buffer.append(" WHEN COAID LIKE 'E%'  THEN 4 END");

			Query query = em.createNativeQuery(buffer.toString());
			objectList = query.getResultList();
			for (Object[] object : objectList) {
				salePointDTOList
						.add(new SalePointDTO(String.valueOf(object[0]), String.valueOf(object[1]), String.valueOf(object[2]), String.valueOf(object[3]), String.valueOf(object[4]),
								(Date) object[5], ((BigDecimal) object[6]).doubleValue(), ((BigDecimal) object[7]).doubleValue(), ((BigDecimal) object[8]).doubleValue(),
								((BigDecimal) object[9]).doubleValue(), String.valueOf(object[10]), (boolean) object[11], String.valueOf(object[12])));
			}

		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException e) {
			throw translate("Fail to find all SalePointDTO by criteria", e);
		}
		return salePointDTOList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<DailyIncomeReportDTO> findDailyIncomeReportByCriteria(TLFCriteria criteria) throws DAOException {
		List<DailyIncomeReportDTO> groupFarmerList = new ArrayList<>();
		List<DailyIncomeReportDTO> resultList = new ArrayList<>();
		try {
			for (String productId : criteria.getProductIdList()) {
				String selectQuery = null;
				String fromQuery = null;
				String andQuery = null;

				if (ProductIDConfig.isLifeProduct(productId)) {
					fromQuery = " LifePolicy l LEFT JOIN l.policyInsuredPersonList pl";
					selectQuery = " (pl.product.name,l.policyNo";
					andQuery = " AND pl.product.id =:productId";
				} else if (ProductIDConfig.isMedicalProduct(productId)) {
					fromQuery = " MedicalPolicy l LEFT JOIN l.policyInsuredPersonList pl";
					selectQuery = " (pl.product.name,l.policyNo";
					andQuery = " AND pl.product.id =:productId";
				} else if (ProductIDConfig.isTravelProduct(productId)) {
					fromQuery = " TravelProposal l";
					selectQuery = " (l.product.name,l.proposalNo";
					andQuery = " AND l.product.id =:productId";
				} else if (ProductIDConfig.isParsonalTravelProduct(productId) || ProductIDConfig.isForeignTravel(productId) || ProductIDConfig.isUnder100Travel(productId)) {
					fromQuery = " PersonTravelPolicy l";
					selectQuery = " (l.product.name,l.policyNo";
					andQuery = " AND l.product.id =:productId";
				}

				if (ProductIDConfig.isGroupFarmer(productId)) {
					groupFarmerList = findGroupFarmerDailyIncomeReport(criteria);
				}

				StringBuffer buffer = new StringBuffer();
				buffer.append(" SELECT DISTINCT NEW " + DailyIncomeReportDTO.class.getName());
				buffer.append(selectQuery);
				buffer.append(" ,p.receiptNo, p.paymentDate, t.homeAmount,p.paymentChannel,s.name,b.name)");
				buffer.append(" from Payment p,TLF t,SalePoint s,Branch b,Entitys e,CurrencyChartOfAccount ccoa,Coa c, ");
				buffer.append(fromQuery);
				buffer.append(" WHERE s.id=t.salePoint.id and s.branch.id = b.id and b.entity.id=e.id and p.reverse = false");
				buffer.append(" AND p.receiptNo = t.tlfNo");
				buffer.append(" AND l.id = p.referenceNo");
				buffer.append(" AND t.coaId = ccoa.id");
				buffer.append(" AND ccoa.coaid = c.id");
				buffer.append(" AND c.acode like 'I%'");
				buffer.append(andQuery);

				if (null != criteria.getStartDate()) {
					Date startDate = DateUtils.resetStartDate(criteria.getStartDate());
					String formatedStartDate = DateUtils.formattedSqlDate(startDate);
					buffer.append(" AND p.paymentDate >='" + formatedStartDate + "'");
				}
				if (null != criteria.getEndDate()) {
					Date endDate = DateUtils.resetEndDate(criteria.getEndDate());
					String foramtedEndDate = DateUtils.formattedSqlDate(endDate);
					buffer.append(" AND p.paymentDate <='" + foramtedEndDate + "'");
				}

				if (null != criteria.getSalePoint()) {
					buffer.append(" AND t.salePoint.id = '" + criteria.getSalePoint().getId() + "'");

				}
				if (null != criteria.getEntity() && null == criteria.getBranch()) {
					buffer.append(" AND e.id = '" + criteria.getEntity().getId() + "'");
				} else if (null != criteria.getBranch()) {
					buffer.append(" AND b.id = '" + criteria.getBranch().getId() + "'");
				}

				/*
				 * if (null != criteria.getBranch()) {
				 * buffer.append(" AND t.branchId = '" +
				 * criteria.getBranch().getBranchCode() + "'");
				 * 
				 * }
				 */
				if (null != criteria.getPaymentChannel()) {
					buffer.append(" AND p.paymentChannel =:paymentChannel");
				}
				buffer.append(" ORDER BY p.paymentDate ");

				Query query = em.createQuery(buffer.toString());
				query.setParameter("productId", productId);
				if (null != criteria.getPaymentChannel()) {
					query.setParameter("paymentChannel", criteria.getPaymentChannel());
				}
				resultList.addAll(query.getResultList());
			}
			resultList.addAll(groupFarmerList);
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException e) {
			throw translate("Fail to find all daily income report by criteria", e);
		}
		return resultList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<AccountManualReceiptDTO> findAccountManualReceiptDTOByCriteria(TLFCriteria criteria) throws DAOException {
		List<AccountManualReceiptDTO> resultList = new ArrayList<>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT C.ACNAME, SUM(T.HOMEAMOUNT), CONVERT (VARCHAR(10),T.CREATEDDATE,120) AS CREATEDDATE ,S.NAME AS SALEPOINTNAME,B.NAME AS BRANCHNAME");
			buffer.append(" FROM TLF T,CCOA cc, COA C,BRANCH B,SALEPOINT S,ENTITY E");
			buffer.append("	WHERE ENO LIKE 'VOC%' AND T.CCOAID=cc.id AND cc.coaid = c.id and c.accode LIKE 'I%' ");

			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				buffer.append(" AND E.ID = '" + criteria.getEntity().getId() + "'");
			} else if (null != criteria.getBranch()) {
				buffer.append(" AND T.BRANCHID = '" + criteria.getBranch().getId() + "'");
			}
			if (null != criteria.getSalePoint()) {
				buffer.append(" AND T.SALEPOINTID = '" + criteria.getSalePoint().getId() + "'");
			}
			if (null != criteria.getStartDate()) {
				Date startDate = DateUtils.resetStartDate(criteria.getStartDate());
				String formatedStartDate = DateUtils.formattedSqlDate(startDate);
				buffer.append(" AND CONVERT (VARCHAR(10), T.CREATEDDATE, 120) >= '" + formatedStartDate + "' AND ");
			}
			if (null != criteria.getEndDate()) {
				Date endDate = DateUtils.resetEndDate(criteria.getEndDate());
				String foramtedEndDate = DateUtils.formattedSqlDate(endDate);
				buffer.append(" CONVERT (VARCHAR(10), T.CREATEDDATE, 120) <= '" + foramtedEndDate + "'");
			}

			buffer.append(" AND S.ID = T.SALEPOINTID  AND  T.BRANCHID = B.ID AND B.ENTITYSID=E.ID");
			buffer.append(" GROUP BY CCOAID,COAID , C.ACNAME, CONVERT (VARCHAR(10) ,T.CREATEDDATE, 120),B.NAME,S.NAME");
			buffer.append(" ORDER BY CONVERT (VARCHAR(10),T.CREATEDDATE,120)");
			Query query = em.createNativeQuery(buffer.toString());
			objectList = query.getResultList();
			for (Object[] object : objectList) {
				resultList.add(new AccountManualReceiptDTO(String.valueOf(object[0]), ((BigDecimal) object[1]).doubleValue(), String.valueOf(object[2]), String.valueOf(object[3]),
						String.valueOf(object[4])));
			}
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException e) {
			throw translate("Fail to find all Account Manual Receipt by criteria", e);
		}
		return resultList;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<AccountAndLifeDeptCancelReportDTO> findAccountAndLifeDeptCancelReportDTOByCriteria(TLFCriteria criteria) throws DAOException {
		List<AccountAndLifeDeptCancelReportDTO> resultList = new ArrayList<>();

		try {
			for (String productId : criteria.getProductIdList()) {
				String selectQuery = null;
				String fromQuery = null;
				String andQuery = null;
				if (ProductIDConfig.isLifeProduct(productId)) {
					fromQuery = " LifePolicy l LEFT JOIN l.policyInsuredPersonList pl";
					selectQuery = " pl.product.name";
					andQuery = " AND pl.product.id =:productId";
				} else if (ProductIDConfig.isMedicalProduct(productId)) {
					fromQuery = " MedicalPolicy l LEFT JOIN l.policyInsuredPersonList pl";
					selectQuery = " pl.product.name";
					andQuery = " AND pl.product.id =:productId";
				} else if (ProductIDConfig.isTravelProduct(productId)) {
					fromQuery = " TravelProposal l";
					selectQuery = " l.product.name";
					andQuery = " AND l.product.id =:productId";
				} else if (ProductIDConfig.isParsonalTravelProduct(productId)) {
					fromQuery = " PersonTravelPolicy l";
					selectQuery = " l.product.name";
					andQuery = " AND l.product.id =:productId";
				}

				StringBuffer buffer = new StringBuffer();
				buffer.append(" SELECT NEW " + AccountAndLifeDeptCancelReportDTO.class.getName());
				buffer.append(" ( t.tlfNo,");
				buffer.append(selectQuery);
				buffer.append(" ,t.salePoint.name, t.paymentChannel,t.commonCreateAndUpateMarks.createdDate,t.reverse,t.homeAmount,");
				buffer.append("  pa.paymentDate, pa.reverse, pa.basicPremium, pa.complete,l.branch.name,'') ");
				buffer.append(" FROM Payment pa, TLF t ,Branch b, CurrencyChartOfAccount ccoa, Coa c, ");
				buffer.append(fromQuery);
				buffer.append(" WHERE b.id=l.branch.id and pa.receiptNo = t.tlfNo");
				buffer.append(" AND t.coaId = ccoa.id");
				buffer.append(" AND ccoa.coaid = c.id");
				buffer.append(" AND c.acode like 'I%'");
				buffer.append(" AND pa.referenceNo = l.id");
				buffer.append(andQuery);

				if (null != criteria.getStartDate()) {
					Date startDate = DateUtils.resetStartDate(criteria.getStartDate());
					String formatedStartDate = DateUtils.formattedSqlDate(startDate);
					buffer.append(" AND ((pa.confirmDate >='" + formatedStartDate + "' OR t.commonCreateAndUpateMarks.createdDate >='" + formatedStartDate + "'))");
				}
				if (null != criteria.getEndDate()) {
					Date endDate = DateUtils.resetEndDate(criteria.getEndDate());
					String foramtedEndDate = DateUtils.formattedSqlDate(endDate);
					buffer.append(" AND ((pa.confirmDate <='" + foramtedEndDate + "' OR t.commonCreateAndUpateMarks.createdDate <='" + foramtedEndDate + "'))");

				}
				if (null != criteria.getSalePoint()) {
					buffer.append(" AND t.salePoint.id = '" + criteria.getSalePoint().getId() + "'");

				}
				if (null != criteria.getEntity() && null == criteria.getBranch()) {
					buffer.append(" AND b.entity.id = :entityId");
				} else if (criteria.getBranch() != null) {
					buffer.append(" AND b.id = :branchId");
				}
				if (null != criteria.getPaymentChannel()) {
					buffer.append(" AND t.paymentChannel =:paymentChannel");
				}
				TypedQuery<AccountAndLifeDeptCancelReportDTO> query = em.createQuery(buffer.toString(), AccountAndLifeDeptCancelReportDTO.class);
				if (null != criteria.getPaymentChannel()) {
					query.setParameter("paymentChannel", criteria.getPaymentChannel());
				}
				if (null != criteria.getEntity() && null == criteria.getBranch()) {
					query.setParameter("entityId", criteria.getEntity().getId());
				} else if (criteria.getBranch() != null) {
					query.setParameter("branchId", criteria.getBranch().getId());
				}
				query.setParameter("productId", productId);
				resultList.addAll(query.getResultList());

			}
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException e) {
			throw translate("Fail to find all Account Manual Receipt by criteria", e);
		}
		return resultList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<DailyIncomeReportDTO> findGroupFarmerDailyIncomeReport(TLFCriteria criteria) throws DAOException {
		List<DailyIncomeReportDTO> result = new ArrayList<DailyIncomeReportDTO>();
		StringBuffer buffer = new StringBuffer();
		buffer.append(" SELECT DISTINCT NEW " + DailyIncomeReportDTO.class.getName());
		buffer.append(" ('FARMER', g.proposalNo, py.receiptNo, py.paymentDate, tf.homeAmount,py.paymentChannel,tf.salePoint.name,g.branch.name)");
		buffer.append(" from Payment py,TLF tf, CurrencyChartOfAccount ccoa, Coa c,");
		buffer.append(" GroupFarmerProposal g");
		buffer.append(" WHERE py.reverse = false");
		buffer.append(" AND py.receiptNo = tf.tlfNo");
		buffer.append(" AND g.id = py.referenceNo");
		buffer.append(" AND tf.coaId = ccoa.id ");
		buffer.append(" AND ccoa.coaid = c.id");
		buffer.append(" AND c.acode like 'I%'");

		if (null != criteria.getStartDate()) {
			Date startDate = DateUtils.resetStartDate(criteria.getStartDate());
			String formatedStartDate = DateUtils.formattedSqlDate(startDate);
			buffer.append(" AND py.paymentDate >='" + formatedStartDate + "'");
		}
		if (null != criteria.getEndDate()) {
			Date endDate = DateUtils.resetEndDate(criteria.getEndDate());
			String foramtedEndDate = DateUtils.formattedSqlDate(endDate);
			buffer.append(" AND py.paymentDate <='" + foramtedEndDate + "'");
		}

		if (null != criteria.getSalePoint()) {
			buffer.append(" AND tf.salePoint.id = '" + criteria.getSalePoint().getId() + "'");

		}
		if (null != criteria.getPaymentChannel()) {
			buffer.append(" AND py.paymentChannel =:paymentChannel");
		}
		buffer.append(" ORDER BY py.paymentDate ");
		Query q = em.createQuery(buffer.toString());
		if (null != criteria.getPaymentChannel()) {
			q.setParameter("paymentChannel", criteria.getPaymentChannel());
		}
		result = q.getResultList();
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void reverseBytlfNo(String tlfNo) throws DAOException {
		try {
			TypedQuery<TLF> query = em.createNamedQuery("TLF.reverseByTLFNo", TLF.class);
			query.setParameter("tlfNo", tlfNo);
			query.executeUpdate();
		} catch (PersistenceException pe) {
			throw translate("Fail to reverse TLF by eno", pe);
		}
	}

}
