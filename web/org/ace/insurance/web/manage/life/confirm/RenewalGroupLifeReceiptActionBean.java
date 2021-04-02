package org.ace.insurance.web.manage.life.confirm;

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

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
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
@ManagedBean(name = "RenewalGroupLifeReceiptActionBean")
public class RenewalGroupLifeReceiptActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RenewalGroupLifeProposalService}")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	public void setRenewalGroupLifeProposalService(IRenewalGroupLifeProposalService renewalGroupLifeProposalService) {
		this.renewalGroupLifeProposalService = renewalGroupLifeProposalService;
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

	private User user;
	private LifeProposal lifeProposal;
	private WorkFlowDTO workFlowDTO;

	private PaymentDTO payment;
	private List<Payment> paymentList;
	private String presentDate;
	private Bank bank;
	private PaymentChannel channelValue;
	private boolean isCheque;
	private boolean actualPayment;
	private boolean isTransfer;
	private boolean isBank;
	private boolean isAccountBank;
	private boolean isSundry;
	private BankCharges bankCharges;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
		removeParam("workFlowDTO");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		payment = new PaymentDTO();
		AcceptedInfo acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposal.getId(), ReferenceType.LIFE_PROPOSAL);
		if (acceptedInfo != null) {
			payment.setDiscountPercent(acceptedInfo.getDiscountPercent());
			payment.setServicesCharges(acceptedInfo.getServicesCharges());
			payment.setStampFees(acceptedInfo.getStampFees());
		}
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		presentDate = format.format(new Date());
	}

	public void addNewLifeProposalReceiptInfo() {
		try {
			if (!validPayment()) {
				return;
			}
			payment.setPaymentChannel(channelValue);
			if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				payment.setBankWallet(bankCharges);
			}
			paymentList = renewalGroupLifeProposalService.confirmLifeProposal(lifeProposal, workFlowDTO, payment, user.getBranch(), null);
			payment = new PaymentDTO(paymentList);
			actualPayment = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, lifeProposal.getProposalNo());
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
		if (isCheque) {
			if (payment.getChequeNo() == null || payment.getChequeNo().isEmpty()) {
				addErrorMessage(formID + ":chequeNo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (payment.getBank() == null) {
				addErrorMessage(formID + ":bankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (payment.getAccountBank() == null) {
				addErrorMessage(formID + ":accountBankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}
		if (channelValue.equals(PaymentChannel.TRANSFER)) {
			if (payment.getPoNo() == null || payment.getPoNo().isEmpty()) {
				addErrorMessage(formID + ":poNo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (payment.getBank() == null) {
				addErrorMessage(formID + ":bankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (payment.getAccountBank() == null) {
				addErrorMessage(formID + ":accountBankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}
		if (channelValue.equals(PaymentChannel.SUNDRY)) {
			if (payment.getAccountBank() == null) {
				addErrorMessage(formID + ":accountBankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}

		return valid;
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		if (channelValue.equals(PaymentChannel.CASHED)) {
			payment.setAccountBank(null);
			payment.setBank(null);
			payment.setChequeNo(null);
			payment.setPoNo(null);
		} else if (channelValue.equals(PaymentChannel.TRANSFER)) {
			payment.setChequeNo(null);
			payment.setAccountBank(null);
		} else if (channelValue.equals(PaymentChannel.CHEQUE)) {
			payment.setPoNo(null);
		} else if (channelValue.equals(PaymentChannel.SUNDRY)) {
			payment.setChequeNo(null);
			payment.setPoNo(null);
		}
	}

	public PaymentChannel getChannelValue() {
		return channelValue;
	}

	public void setChannelValue(PaymentChannel channelValue) {
		if (channelValue.equals(PaymentChannel.CHEQUE)) {
			isAccountBank = true;
			isCheque = true;
			isBank = true;
			isTransfer = false;
			isSundry = false;
		} else if (channelValue.equals(PaymentChannel.TRANSFER)) {
			isAccountBank = true;
			isCheque = false;
			isBank = true;
			isTransfer = true;
			isSundry = false;
		} else if (channelValue.equals(PaymentChannel.SUNDRY)) {
			isSundry = true;
			isAccountBank = true;
			isCheque = false;
			isBank = false;
			isTransfer = false;
		} else {
			isAccountBank = false;
			isCheque = false;
			isBank = false;
			isTransfer = false;
			isSundry = false;
		}
		this.channelValue = channelValue;
	}

	public PaymentChannel[] getChannelValues() {
		return PaymentChannel.values();
	}

	public boolean isAtualPayment() {
		return actualPayment;
	}

	public String getPresentDate() {
		return presentDate;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
	}

	public boolean getIsCheque() {
		return isCheque;
	}

	public void setIsCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public String getPublicLifeId() {
		return ProductIDConfig.getPublicLifeId();
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
	}

	public String getEndorsementNetPremium() {
		Double temp = lifeProposal.getEndorsementNetPremium();
		String netPremium = temp.toString();
		if (temp < 0) {
			netPremium = "( " + netPremium.substring(1) + " )";
		}
		return netPremium;
	}

	public double getEndorseDisAmount() {
		if (lifeProposal.getEndorsementNetPremium() * payment.getDiscountPercent() == -0.00) {
			return 0.00;
		}
		return lifeProposal.getEndorsementNetPremium() * payment.getDiscountPercent() / 100;
	}

	public String getEndorsePremiumWithDis() {
		Double temp = lifeProposal.getEndorsementNetPremium() - getEndorseDisAmount();
		String disPremium = temp.toString();
		if (temp < 0) {
			disPremium = "( " + disPremium.substring(1) + " )";
		}
		return disPremium;
	}

	public String getTotalEndorsePremium() {
		Double temp = lifeProposal.getEndorsementNetPremium() - getEndorseDisAmount();
		temp = temp + payment.getServicesCharges() + payment.getStampFees();
		String totalPremium = temp.toString();
		if (temp < 0) {
			totalPremium = "( " + totalPremium.substring(1) + " )";
		}
		return totalPremium;
	}

	public Boolean showCustomerReceipt() {
		Double temp = lifeProposal.getEndorsementNetPremium() - getEndorseDisAmount();
		temp = temp + payment.getServicesCharges() + payment.getStampFees();
		if (lifeProposal.getLifePolicy() != null && temp <= 0) {
			return false;
		}
		return true;
	}

	public boolean isLicenseNoNull() {
		if (lifeProposal.getAgent() != null) {
			return true;
		} else
			return false;
	}

	private final String reportName = "LifeCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName = "";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		String customerName = lifeProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "GroupLife_" + customerName + "_Receipt" + ".pdf";
		DocumentBuilder.generateLifeCashReceipt(lifeProposal, payment, dirPath, fileName);
	}

	public boolean getIsTransfer() {
		return isTransfer;
	}

	public void setIsTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	public boolean getIsBank() {
		return isBank;
	}

	public void setIsBank(boolean isBank) {
		this.isBank = isBank;
	}

	public boolean getIsAccountBank() {
		return isAccountBank;
	}

	public void setIsAccountBank(boolean isAccountBank) {
		this.isAccountBank = isAccountBank;
	}

	public void setCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public void returnAccountBank(SelectEvent event) {
		Bank accountBank = (Bank) event.getObject();
		payment.setAccountBank(accountBank);
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		payment.setBank(bank);
	}

	public BankCharges getBankCharges() {
		return bankCharges;
	}

	public void setBankCharges(BankCharges bankCharges) {
		this.bankCharges = bankCharges;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean getIsSundry() {
		return isSundry;
	}

	public void setSundry(boolean isSundry) {
		this.isSundry = isSundry;
	}

	public void returnBankWallet(SelectEvent event) {
		bankCharges = (BankCharges) event.getObject();

	}

	public double getCharges() {
		if (null != bankCharges) {
			if (bankCharges.getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
				return bankCharges.getCharges() + bankCharges.getAdditionalCharges();
			} else {
				return (lifeProposal.getTotalBasicTermPremium() * bankCharges.getCharges() / 100) + bankCharges.getAdditionalCharges();
			}
		}
		return 0.0;

	}

	public void removeBankWallet() {
		bankCharges.setName(null);
		bankCharges.setCharges(0.00);
	}

}
