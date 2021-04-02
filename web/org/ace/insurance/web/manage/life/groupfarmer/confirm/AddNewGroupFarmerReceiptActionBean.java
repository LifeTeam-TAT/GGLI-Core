package org.ace.insurance.web.manage.life.groupfarmer.confirm;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewGroupFarmerReceiptActionBean")
public class AddNewGroupFarmerReceiptActionBean extends BaseBean {

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	private GroupFarmerProposal groupFarmerProposal;
	private WorkFlowDTO workFlowDTO;
	private User user;
	private PaymentDTO payment;
	private String presentDate;

	private boolean actualPayment;
	private Bank bank;
	private boolean showAdministrationFee;
	private BankCharges bankCharges;

	@PostConstruct
	public void init() {
		payment = new PaymentDTO();
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		groupFarmerProposal = (groupFarmerProposal == null) ? (GroupFarmerProposal) getParam("groupFarmerProposal") : groupFarmerProposal;
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
	}

	@PreDestroy
	public void destroy() {
		removeParam("groupFarmerProposal");
		removeParam("workFlowDTO");
	}

	public void addNewGroupFarmerProposalReceiptInfo() {

		try {
			if (!validPayment()) {
				return;
			}
			List<Payment> paymentList = null;
			if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				payment.setBankWallet(bankCharges);
			}

			paymentList = groupFarmerProposalService.confirmGroupFarmerProposal(groupFarmerProposal, workFlowDTO, payment, user.getBranch(), RequestStatus.FINISHED.name());
			payment = new PaymentDTO(paymentList);
			actualPayment = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, groupFarmerProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	private boolean validPayment() {
		boolean valid = true;
		String formID = "proposalReceiiptEntryForm";
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

	public PaymentChannel[] getChannelValues() {
		return PaymentChannel.values();
	}

	private final String reportName = "GroupFarmerCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		String customerName = groupFarmerProposal.getOrganization().getName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "GroupFarmer_" + customerName + "_Receipt" + ".pdf";
		DocumentBuilder.generateGroupFarmerCashReceipt(groupFarmerProposal, payment, dirPath, fileName);
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

	public boolean isAtualPayment() {
		return actualPayment;
	}

	public WorkFlowDTO getWorkFlowDTO() {
		return workFlowDTO;
	}

	public void setWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		this.workFlowDTO = workFlowDTO;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
	}

	public String getPresentDate() {
		return presentDate;
	}

	public void setPresentDate(String presentDate) {
		this.presentDate = presentDate;
	}

	public boolean isActualPayment() {
		return actualPayment;
	}

	public void setActualPayment(boolean actualPayment) {
		this.actualPayment = actualPayment;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public boolean isShowAdministrationFee() {
		return showAdministrationFee;
	}

	public void setShowAdministrationFee(boolean showAdministrationFee) {
		this.showAdministrationFee = showAdministrationFee;
	}

	public IPaymentService getPaymentService() {
		return paymentService;
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
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
		bankCharges.setName(null);
		bankCharges.setCharges(0.00);
	}

	public double getCharges() {
		if (null != bankCharges) {
			if (bankCharges.getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
				return bankCharges.getCharges() + bankCharges.getAdditionalCharges();
			} else {
				return (groupFarmerProposal.getPremium() * bankCharges.getCharges() / 100) + bankCharges.getAdditionalCharges();
			}
		}
		return 0.0;

	}

}
