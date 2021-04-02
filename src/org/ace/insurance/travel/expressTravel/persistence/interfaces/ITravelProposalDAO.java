package org.ace.insurance.travel.expressTravel.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.travel.TravelDailyIncomeReport;
import org.ace.insurance.report.travel.TravelMonthlyIncomeReport;
import org.ace.insurance.travel.expressTravel.ExpressDetail;
import org.ace.insurance.travel.expressTravel.Tour;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface ITravelProposalDAO {
	public void insert(TravelProposal travelProposal) throws DAOException;

	public void update(TravelProposal travelProposal) throws DAOException;

	public void delete(TravelProposal travelProposal) throws DAOException;

	public TravelProposal findById(String id) throws DAOException;

	public List<TravelProposal> findAll() throws DAOException;

	public List<TravelProposal> findTravelProposalByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException;

	public List<TravelExpress> findExpressList() throws DAOException;

	public void insertExpressDetail(ExpressDetail detail);

	public void updateExpress(TravelExpress express) throws DAOException;

	public void insertTour(Tour tour) throws DAOException;

	public void updateTour(Tour tour) throws DAOException;

	public List<Tour> findTourList() throws DAOException;

	public List<TravelDailyIncomeReport> findExpressDetailByTravelReportCritera(TravelReportCriteria criteria);

	public List<TravelMonthlyIncomeReport> findTravelMonthlyIncome(TravelReportCriteria criteria);

	public List<TravelProposal> findTravelProposalByPOByReceiptNo(String receiptNo, String bpmsReceiptNo) throws DAOException;

}
