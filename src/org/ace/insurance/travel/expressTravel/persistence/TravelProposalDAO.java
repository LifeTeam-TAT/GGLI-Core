package org.ace.insurance.travel.expressTravel.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.travel.TravelDailyIncomeReport;
import org.ace.insurance.report.travel.TravelMonthlyIncomeReport;
import org.ace.insurance.travel.expressTravel.ExpressDetail;
import org.ace.insurance.travel.expressTravel.Tour;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.persistence.interfaces.ITravelProposalDAO;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("TravelProposalDAO")
public class TravelProposalDAO extends BasicDAO implements ITravelProposalDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(TravelProposal travelProposal) throws DAOException {
		try {
			em.persist(travelProposal);
			insertProcessLog(TableName.TRAVELPROPOSAL, travelProposal.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Township", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(TravelProposal travelProposal) throws DAOException {
		try {
			em.merge(travelProposal);
			updateProcessLog(TableName.TRAVELPROPOSAL, travelProposal.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Township", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(TravelProposal travelProposal) throws DAOException {
		try {
			travelProposal = em.merge(travelProposal);
			em.remove(travelProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Township", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public TravelProposal findById(String id) throws DAOException {
		TravelProposal result = null;
		try {
			result = em.find(TravelProposal.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find TravelProposal", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelProposal> findAll() throws DAOException {
		List<TravelProposal> resultList = null;
		try {
			Query q = em.createNamedQuery("TravelProposal.findAll");
			resultList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All TravelProposal", pe);
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelProposal> findTravelProposalByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException {
		List<TravelProposal> resultList = null;
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT t FROM TravelProposal t WHERE t.proposalNo is not null ");
			if (criteria.getStartDate() != null) {
				queryString.append(" AND t.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND t.submittedDate <= :endDate");
			}
			if (!criteria.getNumber().isEmpty()) {
				queryString.append(" AND t.proposalNo= :proposalNo");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND t.branch.id = :branchId");
			}
			queryString.append(" ORDER BY t.id ASC ");
			/* Executed query */
			Query query = em.createQuery(queryString.toString());
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (!criteria.getNumber().isEmpty()) {
				query.setParameter("proposalNo", criteria.getNumber());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}

			resultList = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All TravelProposal", pe);
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelExpress> findExpressList() throws DAOException {
		List<TravelExpress> resultList = null;
		try {
			Query q = em.createNamedQuery("TravelExpress.findAll");
			resultList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All Express", pe);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertExpressDetail(ExpressDetail expressDetail) throws DAOException {
		try {
			em.persist(expressDetail);
			insertProcessLog(TableName.EXPRESSDETAIL, expressDetail.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert ExpressDetail", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateExpressDetail(ExpressDetail detail) throws DAOException {
		try {
			em.merge(detail);
			updateProcessLog(TableName.EXPRESSDETAIL, detail.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update ExpressDetail", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateExpress(TravelExpress express) throws DAOException {
		try {
			em.merge(express);
			updateProcessLog(TableName.TRAVEL_EXPRESS, express.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert ExpressDetail", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertTour(Tour tour) throws DAOException {
		try {
			em.persist(tour);
			insertProcessLog(TableName.TOUR, tour.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Tour", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateTour(Tour tour) throws DAOException {
		try {
			em.merge(tour);
			updateProcessLog(TableName.TOUR, tour.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Tour", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Tour> findTourList() throws DAOException {
		List<Tour> resultList = null;
		try {
			Query q = em.createNamedQuery("Tour.findAll");
			resultList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All Tour", pe);
		}
		return resultList;
	}

	// @Transactional(propagation = Propagation.REQUIRED)
	// public List<ExpressDetail>
	// findExpressDetailByTravelReportCritera(TravelReportCriteria criteria) {
	// List<ExpressDetail> resultList = null;
	// try {
	// Query q =
	// em.createNamedQuery("ExpressDetail.findExpressDetailByTravelReportCriteria");
	// q.setParameter("submittedDate", criteria.getSubmittedDate());
	// q.setParameter("branch", criteria.getBranch());
	// resultList = q.getResultList();
	// em.flush();
	// } catch (PersistenceException pe) {
	// throw translate("Failed to find ExpressDetail by TravelReportCriteria.",
	// pe);
	// }
	// return resultList;
	// }

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelDailyIncomeReport> findExpressDetailByTravelReportCritera(TravelReportCriteria criteria) {
		List<TravelDailyIncomeReport> resultList = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT d FROM TravelDailyIncomeReport d WHERE d.registrationNo IS NOT NULL ");
			if (criteria.getFromDate() != null) {
				query.append("AND d.fromDate >= :startDate ");
			}
			if (criteria.getToDate() != null) {
				query.append("AND d.toDate <= :endDate ");
			}
			if (!(criteria.getRegistration() == null || criteria.getRegistration().equals(""))) {
				query.append(" AND d.registrationNo = :registrationNo");
			}
			if (!(criteria.getTour() == null || criteria.getTour().equals(""))) {
				query.append(" AND d.occurrenceDesc= :description");
			}
			if (!(criteria.getTravelExpress() == null || criteria.getTravelExpress().equals(""))) {
				query.append(" AND d.expressName = :expressName");
			}
			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				query.append(" AND d.entityId = :entityId");
			} else if (criteria.getBranch() != null) {
				query.append(" AND d.branchId = :branchId");
			}
			if (criteria.getSalePoint() != null) {
				query.append(" AND d.salePointId = :salePointId");
			}

			Query q = em.createQuery(query.toString());

			if (criteria.getFromDate() != null) {
				q.setParameter("startDate", criteria.getFromDate());
			}
			if (criteria.getToDate() != null) {
				q.setParameter("endDate", criteria.getToDate());
			}
			if (!(criteria.getRegistration() == null || criteria.getRegistration().equals(""))) {
				q.setParameter("registrationNo", criteria.getRegistration());
			}
			if (!(criteria.getTour() == null || criteria.getTour().equals(""))) {
				q.setParameter("description", criteria.getTour());
			}
			if (!(criteria.getTravelExpress() == null || criteria.getTravelExpress().equals(""))) {
				q.setParameter("expressName", criteria.getTravelExpress());
			}
			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				q.setParameter("entityId", criteria.getEntity().getId());
			} else if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				q.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			resultList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find ExpressDetail by TravelReportCriteria.", pe);
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelMonthlyIncomeReport> findTravelMonthlyIncome(TravelReportCriteria criteria) {
		List<TravelMonthlyIncomeReport> resultList = new ArrayList<TravelMonthlyIncomeReport>();
		try {

			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT t FROM TravelMonthlyIncomeReport t ");
			buffer.append(" WHERE t.paymentDate>= :startDate ");
			buffer.append(" AND t.paymentDate<= :endDate ");
			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				buffer.append(" AND t.entityId = :entityId");
			} else if (criteria.getBranch() != null) {
				buffer.append(" AND t.branchId = :branchId");
			}
			if (criteria.getSalePoint() != null) {
				buffer.append(" AND t.salePointId = :salePointId");
			}
			buffer.append(" ORDER BY t.submittedDate");

			Query query = em.createQuery(buffer.toString());
			query.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
			query.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));
			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				query.setParameter("entityId", criteria.getEntity().getId());
			} else if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				query.setParameter("salePointId", criteria.getSalePoint().getId());
			}

			resultList = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find find TravelDTO001 by TravelReportCriteria.", pe);
		}
		return resultList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelProposal> findTravelProposalByPOByReceiptNo(String receiptNo, String bpmsReceiptNo) throws DAOException {
		List<TravelProposal> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT g FROM TravelProposal g, Payment p, TLF t WHERE g.id = p.referenceNo ");
			query.append("AND g.id = t.referenceNo AND p.isPO = TRUE AND t.clearing = TRUE AND t.paid = FALSE ");
			if (!StringUtils.isBlank(receiptNo)) {
				query.append("AND t.tlfNo = :receiptNo ");
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				query.append("AND t.bpmsReceiptNo = :bpmsReceiptNo ");
			}
			Query q = em.createQuery(query.toString());
			if (!StringUtils.isBlank(receiptNo)) {
				q.setParameter("receiptNo", receiptNo);
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				q.setParameter("bpmsReceiptNo", bpmsReceiptNo);
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravelPolicy PaymentOrder", pe);
		}
		return result;
	}
}
