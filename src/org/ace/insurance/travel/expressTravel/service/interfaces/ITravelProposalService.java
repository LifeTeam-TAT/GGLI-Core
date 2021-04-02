package org.ace.insurance.travel.expressTravel.service.interfaces;

import java.util.List;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.travel.TravelDailyIncomeReport;
import org.ace.insurance.report.travel.TravelMonthlyIncomeReport;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.travel.expressTravel.Tour;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;

public interface ITravelProposalService {
	public void addNewTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO, String status);

	public void updateTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO);

	public void deleteTravelProposal(TravelProposal travelProposal);

	public TravelProposal findTravelProposalById(String id);

	public List<TravelProposal> findTravelProposalByEnquiryCriteria(EnquiryCriteria criteria);

	public List<TravelProposal> findAllTravelProposal();

	public List<TravelExpress> findExpressList();

	public void updateExpress(TravelExpress express);

	public void updateTour(Tour tour);

	public List<Tour> findTourList();

	public List<Payment> confirmTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO, PaymentDTO payment, Branch branch);

	public void rejectTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO);

	public List<Payment> enquiryEditconfirmTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, Payment payment);

	public void paymentTravelProposal(TravelProposal travelProposal, List<Payment> paymentList, Branch branch);

	public List<TravelDailyIncomeReport> findExpressDetailByProposalSubmittedDate(TravelReportCriteria criteria);

	public List<TravelMonthlyIncomeReport> findTravelMonthlyIncome(TravelReportCriteria criteria);

	public List<TravelProposal> findTravelProposalByPOByReceiptNo(String receiptNo, String bpmsReceiptNo);

}
