package org.ace.insurance.web.manage.travel.personTravel;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.PaymentOrderCashReceiptDTO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "PersonTravelReceiptActionBean")
public class PersonTravelReceiptActionBean extends BaseBean {

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	/**
	 * @param personTravelProposalService
	 *            the personTravelProposalService to set
	 */
	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	/**
	 * @param paymentService
	 *            the paymentService to set
	 */
	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private User user;
	private PersonTravelProposal personTravelProposal;

	private PaymentDTO payment;
	private WorkFlowDTO workFlowDTO;
	private boolean actualPayment;
	private BankCharges bankCharges;

	@PostConstruct
	public void init() {
		initializeInjection();
		payment = new PaymentDTO();
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		personTravelProposal = (PersonTravelProposal) getParam("personTravelProposal");
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
	}

	@PreDestroy
	public void destroy() {
		removeParam("personTravelProposal");
		removeParam("workFlowDTO");
	}

	public void receivePersonTravelProposal() {
		List<Payment> paymentList;
		if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
			payment.setBankWallet(bankCharges);
		}
		try {
			paymentList = personTravelProposalService.confirmPersonTravelProposal(personTravelProposal, workFlowDTO, payment, user.getBranch());
			payment = new PaymentDTO(paymentList);
			actualPayment = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, personTravelProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		payment.setAccountBank(null);
		payment.setBank(null);
		payment.setChequeNo(null);
		payment.setPoNo(null);
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

	private final String reportName = "PersonTravelProposalCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName = null;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		PaymentOrderCashReceiptDTO orderDto = null;
		String customerName = personTravelProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "PersonTravel_" + customerName + "_Receipt" + ".pdf";
		DocumentBuilder.generatePersonTravelCashReceipt(personTravelProposal, payment, dirPath, fileName, orderDto);
	}

	/**
	 * @return the personTravelProposal
	 */
	public PersonTravelProposal getPersonTravelProposal() {
		return personTravelProposal;
	}

	/**
	 * @param personTravelProposal
	 *            the personTravelProposal to set
	 */
	public void setPersonTravelProposal(PersonTravelProposal personTravelProposal) {
		this.personTravelProposal = personTravelProposal;
	}

	/**
	 * @return the actualPayment
	 */
	public boolean isActualPayment() {
		return actualPayment;
	}

	/**
	 * @return the payment
	 */
	public PaymentDTO getPayment() {
		return payment;
	}

	/**
	 * @param payment
	 *            the payment to set
	 */
	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
	}

	public BankCharges getBankCharges() {
		return bankCharges;
	}

	public void setBankCharges(BankCharges bankCharges) {
		this.bankCharges = bankCharges;
	}

	public void returnBankWallet(SelectEvent event) {
		bankCharges = (BankCharges) event.getObject();
	}

	public void removeBankWallet() {
		bankCharges.setId(null);
		bankCharges.setName(null);
		bankCharges.setCharges(0.00);
	}

	public double getCharges() {
		if (null != bankCharges) {
			if (bankCharges.getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
				return bankCharges.getCharges() + bankCharges.getAdditionalCharges();
			} else {
				return (personTravelProposal.getPersonTravelInfo().getPremium() * bankCharges.getCharges() / 100) + bankCharges.getAdditionalCharges();
			}
		}
		return 0.0;

	}

}
