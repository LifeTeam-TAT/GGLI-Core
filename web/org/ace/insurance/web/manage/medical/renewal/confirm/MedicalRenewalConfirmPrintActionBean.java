package org.ace.insurance.web.manage.medical.renewal.confirm;

import java.io.IOException;
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
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.document.medical.MedicalUnderwritingDocFactory;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedicalRenewalConfirmPrintActionBean")
public class MedicalRenewalConfirmPrintActionBean extends BaseBean {

	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
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

	private MedicalProposal medicalProposal;
	private WorkFlowDTO workFlowDTO;
	private User user;
	private PaymentDTO payment;
	private String presentDate;

	private boolean actualPayment;
	private Bank bank;
	private PaymentChannel channelValue;
	private boolean showAdministrationFee;
	private boolean isCheque;
	private boolean isTransfer;
	private boolean isAccountBank;
	private boolean isBank;
	private HealthType healthType;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalProposal = (medicalProposal == null) ? (MedicalProposal) getParam("medicalProposal") : medicalProposal;
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
		healthType = (HealthType) getParam("WORKFLOWHEALTHTYPE");
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalproposal");
		removeParam("workFlowDTO");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		payment = new PaymentDTO();
		AcceptedInfo acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(medicalProposal.getId(), ReferenceType.MEDICAL_RENEWAL_PROPOSAL);
		if (acceptedInfo != null) {
			payment.setDiscountPercent(acceptedInfo.getDiscountPercent());
			payment.setServicesCharges(acceptedInfo.getServicesCharges());
			payment.setStampFees(acceptedInfo.getStampFees());
			payment.setAdministrationFees(acceptedInfo.getAdministrationFees());
			payment.setNcbPremium(acceptedInfo.getNcbPremium());
		}
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		presentDate = format.format(new Date());

	}

	public void addNewMedicalProposalReceiptInfo() {
		try {
			if (!validPayment()) {
				return;
			}
			payment.setPaymentChannel(channelValue);
			List<Payment> paymentList = null;
			if (medicalProposal.isSkipPaymentTLF()) {
				// TODO FIXME PSH FOR RENEWAL CASE USING Updated Date
				// paymentList =
				// medicalProposalService.confirmSkipPaymentTLFMedicalProposal(medicalProposal,
				// workFlowDTO, payment, user.getBranch(),
				// RequestStatus.FINISHED.name());
			} else
				// paymentList =
				// medicalProposalService.confirmMedicalProposal(medicalProposal,
				// workFlowDTO, payment, user.getBranch(),
				// RequestStatus.FINISHED.name(), false);

				payment = new PaymentDTO(paymentList);
			actualPayment = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, medicalProposal.getProposalNo());
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
		if (channelValue.equals(PaymentChannel.CHEQUE)) {
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
		}
	}

	public PaymentChannel[] getChannelValues() {
		return PaymentChannel.values();
	}

	public boolean isAtualPayment() {
		return actualPayment;
	}

	public boolean getIsTransfer() {
		return isTransfer;
	}

	public void setIsTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	public void setCheque(boolean isCheque) {
		this.isCheque = isCheque;
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

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
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

	public PaymentChannel getChannelValue() {
		return channelValue;
	}

	public void setChannelValue(PaymentChannel channelValue) {
		if (channelValue.equals(PaymentChannel.CHEQUE)) {
			isAccountBank = true;
			isCheque = true;
			isBank = true;
			isTransfer = false;
		} else if (channelValue.equals(PaymentChannel.TRANSFER)) {
			isAccountBank = true;
			isCheque = false;
			isBank = true;
			isTransfer = true;
		} else {
			isAccountBank = false;
			isCheque = false;
			isBank = false;
			isTransfer = false;
		}
		this.channelValue = channelValue;
	}

	public boolean isEndorsed() {
		return ProposalType.ENDORSEMENT == medicalProposal.getProposalType();
	}

	public boolean isShowAdministrationFee() {
		return showAdministrationFee;
	}

	public boolean isLicenseNoNull() {
		if (medicalProposal.getAgent() != null) {
			return true;
		} else
			return false;
	}

	private final String reportName = "MedicalCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		MedicalUnderwritingDocFactory.generateMedicalCashReceipt(medicalProposal, payment, dirPath, fileName, healthType);

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

}
