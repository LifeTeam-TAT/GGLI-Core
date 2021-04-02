package org.ace.insurance.web.manage.travel;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.PaymentOrderCashReceiptDTO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewTravelProposalReceiptActionBean")
public class AddNewTravelProposalReceiptActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	@ManagedProperty(value = "#{AcceptedInfoService}")
	private IAcceptedInfoService acceptedInfoService;

	public void setAcceptedInfoService(IAcceptedInfoService acceptedInfoService) {
		this.acceptedInfoService = acceptedInfoService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private TravelProposal travelProposal;
	private WorkFlowDTO workFlowDTO;
	private User user;
	private PaymentDTO payment;
	private String presentDate;
	private Bank bank;
	private boolean actualPayment;
	private boolean showAdministrationFee;
	private boolean isAccountBank;
	private boolean isBank;
	private BankCharges bankCharges;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (TravelProposal) getParam("travelProposal");
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
	}

	@PreDestroy
	public void destroy() {
		removeParam("travelProposal");
		removeParam("workFlowDTO");
	}

	@PostConstruct
	public void init() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		presentDate = format.format(new Date());
		initializeInjection();
		payment = new PaymentDTO();

	}

	public String addNewTravelProposalReceiptInfo() {
		String result = null;
		List<Payment> paymentList;
		if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
			payment.setBankWallet(bankCharges);
		}
		try {
			if (!isValidPayment()) {
				return null;
			}
			Payment p = paymentService.findPaymentByReferenceNo(travelProposal.getId());

			if (p != null) {
				paymentList = travelProposalService.enquiryEditconfirmTravelProposal(travelProposal, workFlowDTO, payment, user.getBranch(), p);
			} else {
				paymentList = travelProposalService.confirmTravelProposal(travelProposal, workFlowDTO, payment, user.getBranch());
			}

			payment = new PaymentDTO(paymentList);
			actualPayment = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, travelProposal.getProposalNo());

		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private boolean isValidPayment() {
		boolean valid = true;
		String formID = "proposalReceiptEntryForm";
		if (payment.getServicesCharges() < 0) {
			addErrorMessage(formID + ":additionalCharges", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		payment.setAccountBank(null);
		payment.setBank(null);
		payment.setChequeNo(null);
		payment.setPoNo(null);
	}

	private final String reportName = "TravelProposalCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		PaymentOrderCashReceiptDTO orderDto = null;
		String customerName = travelProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "Travel_" + customerName + "_Receipt" + ".pdf";
		if (payment.getPaymentChannel() != null && payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
			orderDto = paymentService.getPaymentOrderCashReceipt(travelProposal, InsuranceType.TRAVEL_INSURANCE, user.getBranch(), payment);
		}

		DocumentBuilder.generateTravelCashReceipt(travelProposal, payment, dirPath, fileName, orderDto);
	}

	public boolean isAtualPayment() {
		return actualPayment;
	}

	public boolean getIsAccountBank() {
		return isAccountBank;
	}

	public void setAccountBank(boolean isAccountBank) {
		this.isAccountBank = isAccountBank;
	}

	public boolean getIsBank() {
		return isBank;
	}

	public void setBank(boolean isBank) {
		this.isBank = isBank;
	}

	public String getPresentDate() {
		return presentDate;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public BankCharges getBankCharges() {
		return bankCharges;
	}

	public void setBankCharges(BankCharges bankCharges) {
		this.bankCharges = bankCharges;
	}

	public boolean isShowAdministrationFee() {
		return showAdministrationFee;
	}

	public void returnAccountBank(SelectEvent event) {
		Bank accountBank = (Bank) event.getObject();
		payment.setAccountBank(accountBank);
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		payment.setBank(bank);
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void returnBankWallet(SelectEvent event) {
		bankCharges = (BankCharges) event.getObject();
	}

	public void removeBankWallet() {
		// bankCharges = new BankCharges();
		bankCharges.setName(null);
		bankCharges.setCharges(0.00);
		bankCharges.setBank(null);
		// bankCharges = new BankCharges();

	}

	public double getCharges() {
		if (null != bankCharges) {
			if (bankCharges.getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
				return bankCharges.getCharges() + bankCharges.getAdditionalCharges();
			} else {
				return (travelProposal.getTotalNetPremium() * bankCharges.getCharges() / 100) + bankCharges.getAdditionalCharges();
			}
		}
		return 0.0;

	}
}
