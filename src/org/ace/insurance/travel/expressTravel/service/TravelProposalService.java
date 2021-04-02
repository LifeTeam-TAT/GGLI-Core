
package org.ace.insurance.travel.expressTravel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.persistence.interfaces.IBancaassuranceProposalDAO;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.bancassurance.proposalhistory.service.interfaces.IBancaassuranceProposalHistoryService;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.report.travel.TravelDailyIncomeReport;
import org.ace.insurance.report.travel.TravelMonthlyIncomeReport;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.travel.expressTravel.ExpressDetail;
import org.ace.insurance.travel.expressTravel.Tour;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.persistence.interfaces.ITravelProposalDAO;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "TravelProposalService")
public class TravelProposalService extends BaseService implements ITravelProposalService {

	@Resource(name = "TravelProposalDAO")
	private ITravelProposalDAO travelProposalDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "BancaassuranceProposalDAO")
	private IBancaassuranceProposalDAO bancaassuranceProposalDAO;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "BancaassuranceProposalService")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	@Resource(name = "BancaassurancePolicyService")
	private IBancaassurancePolicyService bancaassuracePolicyService;

	@Resource(name = "BancaassuranceProposalHistoryService")
	private IBancaassuranceProposalHistoryService bancaassuranceProposalHistoryService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO, String status) {
		try {

			setProposalNo(travelProposal);
			setIDPrefixForInsert(travelProposal);

			travelProposal.setPrefix(getPrefix(TravelProposal.class));
			travelProposalDAO.insert(travelProposal);
			/*
			 * if (travelProposal.getSaleChannelType().equals(SaleChannelType.
			 * BANCASSURANCE)) {
			 * bancaassuranceProposal.setTravelProposal(travelProposal);
			 * bancaassuraceProposalService.insert(bancaassuranceProposal); }
			 */
			workFlowDTO.setReferenceNo(travelProposal.getId());
			workFlowDTO.setReferenceType(WorkflowReferenceType.TRAVEL_PROPOSAL);
			workFlowDTOService.addNewWorkFlow(workFlowDTO);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TravelProposal", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO) {
		try {
			// bancaassuranceProposalHistoryService.addNewBancaassuranceProposalHistory(bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId()));
			setIDPrefixForUpdate(travelProposal);
			travelProposalDAO.update(travelProposal);
			// bancaassuranceProposalDAO.update(bancaassuranceProposal);
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a TravelProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteTravelProposal(TravelProposal travelProposal) {
		try {
			travelProposalDAO.delete(travelProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a TravelProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public TravelProposal findTravelProposalById(String id) {
		TravelProposal result = null;
		try {
			result = travelProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a TravelProposal (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelProposal> findAllTravelProposal() {
		List<TravelProposal> result = null;
		try {
			result = travelProposalDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of TravelProposal)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelProposal> findTravelProposalByEnquiryCriteria(EnquiryCriteria criteria) {
		List<TravelProposal> result = null;
		try {
			result = travelProposalDAO.findTravelProposalByEnquiryCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of TravelProposal)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch) {
		List<Payment> paymentList = new ArrayList<Payment>();

		try {
			double rate = 1.0;

			Payment payment = new Payment();
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.TRAVEL_PROPOSAL);

			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());

			if (paymentDTO.getBankWallet() != null) {
				payment.setBankWallet(paymentDTO.getBankWallet());
				if (payment.getBankWallet().getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
					payment.setBankCharges(paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(rate * (paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges()));
				} else {
					payment.setBankCharges(
							(paymentDTO.getBankWallet().getCharges() * travelProposal.getTotalNetPremium() / 100) + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(
							(rate * (paymentDTO.getBankWallet().getCharges() * travelProposal.getTotalNetPremium() / 100)) + paymentDTO.getBankWallet().getAdditionalCharges());
				}
			}
			payment.setDiscountPercent(paymentDTO.getDiscountPercent());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setReferenceNo(travelProposal.getId());
			payment.setBasicPremium(travelProposal.getTotalNetPremium());

			payment.setAmount(payment.getTotalAmount());
			payment.setHomeAmount(payment.getTotalAmount());
			payment.setHomePremium(payment.getBasicPremium());

			BancaassuranceProposal BancaProposal = bancaassuraceProposalService.findBancaassuranceProposalByTravelproposalId(travelProposal.getId());
			if (BancaProposal != null) {
				BancaassurancePolicy bancaassurancePolicy = new BancaassurancePolicy(BancaProposal);
				bancaassurancePolicy.setTravelProposl(travelProposal);
				bancaassuracePolicyService.insert(bancaassurancePolicy);
			}
			paymentList.add(payment);
			paymentList = paymentService.prePayment(paymentList);
			workFlowDTOService.updateWorkFlow(workFlowDTO);

			// insert TLF
			prePaymentTravelProposal(travelProposal, workFlowDTO, paymentList, branch);
			// Cheque / Payment Order
			if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE) || paymentDTO.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
				prePaymentTravelProposalTransfer(travelProposal, paymentList, branch);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to co confirm TravelProposal)", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reject a TravelProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> enquiryEditconfirmTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, Payment payment) {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.TRAVEL_PROPOSAL);
			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());
			payment.setDiscountPercent(paymentDTO.getDiscountPercent());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setReferenceNo(travelProposal.getId());
			payment.setBasicPremium(travelProposal.getTotalNetPremium());
			paymentList.add(payment);
			for (Payment p : paymentList) {
				p = paymentDAO.update(p);
			}

			workFlowDTOService.updateWorkFlow(workFlowDTO);
			// delete TLF
			List<TLF> tlfList = paymentService.findTLFbyTLFNo(payment.getReceiptNo(), null);
			paymentDAO.deleteTLFs(tlfList);

			// insert TLF
			prePaymentTravelProposal(travelProposal, workFlowDTO, paymentList, branch);
			// Cheque / Payment Order
			if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				prePaymentTravelProposalTransfer(travelProposal, paymentList, branch);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to co confirm TravelProposal)", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			String accountCode = travelProposal.getProduct().getProductGroup().getAccountCode();
			for (Payment payment : paymentList) {
				if (travelProposal.getId().equals(payment.getReferenceNo())) {
					accountPaymentList.add(new AccountPayment(accountCode, payment));
					break;
				}
			}

			if (!travelProposal.getBranch().getId().equals(branch.getId())) {
				paymentService.preActivatePaymentForInterBranch(accountPaymentList, null, branch, null, false, currencyCode, travelProposal.getSalePoint(),
						travelProposal.getBranch());
			} else {
				paymentService.preActivatePayment(accountPaymentList, null, branch, null, false, currencyCode, travelProposal.getSalePoint());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a MotorProposal ID : " + travelProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentTravelProposalTransfer(TravelProposal travelProposal, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = KeyFactorChecker.getKyatId();
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (Payment payment : paymentList) {
				accountPaymentList.add(new AccountPayment(payment.getAccountBank().getAcode(), payment));
			}

			paymentService.activatePaymentTransfer(accountPaymentList, null, branch, false, currencyCode, travelProposal.getSalePoint());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a MotorProposal ID : " + travelProposal.getId(), e);
		}
	}

	private void setIDPrefixForInsert(TravelProposal travelProposal) {
		String tProposalPrefix = customIDGenerator.getPrefix(TravelProposal.class, true);
		String ePrefix = customIDGenerator.getPrefix(TravelExpress.class, true);
		travelProposal.setPrefix(tProposalPrefix);
		for (TravelExpress e : travelProposal.getExpressList()) {
			e.setPrefix(ePrefix);
		}
	}

	private void setIDPrefixForUpdate(TravelProposal travelProposal) {
		String tProposalPrefix = customIDGenerator.getPrefix(TravelProposal.class, true);
		String ePrefix = customIDGenerator.getPrefix(TravelExpress.class, true);
		travelProposal.setPrefix(tProposalPrefix);
		for (TravelExpress e : travelProposal.getExpressList()) {
			e.setPrefix(ePrefix);
		}
	}

	private void setIDPrefixForUpdate(Tour tour) {
		String tourPrefix = customIDGenerator.getPrefix(Tour.class, false);
		String expressDetailPrefix = customIDGenerator.getPrefix(ExpressDetail.class, false);
		tour.setPrefix(tourPrefix);
		for (ExpressDetail expressDetail : tour.getExpressDetailList()) {
			expressDetail.setPrefix(expressDetailPrefix);
		}
	}

	private void setProposalNo(TravelProposal travelProposal) {
		String proposalNo = null;
		String travelProposalNo = SystemConstants.SPECIAL_TRAVELLING_PROPOSAL_NO;
		proposalNo = customIDGenerator.getNextId(travelProposalNo, null, true);
		travelProposal.setProposalNo(proposalNo);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelExpress> findExpressList() {
		List<TravelExpress> resultList = null;
		try {
			resultList = travelProposalDAO.findExpressList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Express)", e);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateExpress(TravelExpress travelExpress) {
		try {
			for (Tour tour : travelExpress.getTourList()) {
				setIDPrefixForUpdate(tour);
			}
			travelProposalDAO.updateExpress(travelExpress);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Travel Express", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentTravelProposal(TravelProposal travelProposal, List<Payment> paymentList, Branch branch) {
		try {
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			// TODO FIXME PSH add salepoint
			paymentService.activatePaymentAndTLF(paymentList, null, branch, currencyCode, null);
			workFlowDTOService.deleteWorkFlowByRefNo(travelProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a TravelProposal ID : " + travelProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateTour(Tour tour) {
		try {
			travelProposalDAO.updateTour(tour);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Tour", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Tour> findTourList() {
		List<Tour> resultList = null;
		try {
			resultList = travelProposalDAO.findTourList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Tour)", e);
		}
		return resultList;
	}

	// Report
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelDailyIncomeReport> findExpressDetailByProposalSubmittedDate(TravelReportCriteria criteria) {
		List<TravelDailyIncomeReport> resultList = null;
		try {
			resultList = travelProposalDAO.findExpressDetailByTravelReportCritera(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find ExpressDetail by TravelProposalCriteria.)", e);
		}
		return resultList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelMonthlyIncomeReport> findTravelMonthlyIncome(TravelReportCriteria criteria) {
		List<TravelMonthlyIncomeReport> resultList = null;
		try {
			resultList = travelProposalDAO.findTravelMonthlyIncome(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find TravelMonthlyIncomeReport by TravelProposalCriteria.)", e);
		}
		return resultList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelProposal> findTravelProposalByPOByReceiptNo(String receiptNo, String bpmsReceiptNo) {
		List<TravelProposal> result = null;
		try {
			result = travelProposalDAO.findTravelProposalByPOByReceiptNo(receiptNo, bpmsReceiptNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Travel Proposal by receiptNo)", e);
		}
		return result;
	}
}
