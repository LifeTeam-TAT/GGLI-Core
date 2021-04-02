package org.ace.insurance.web.manage.medical.confirmation;

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
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.document.medical.MedicalUnderwritingDocFactory;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewMedicalProposalReceiptActionBean")
public class AddNewMedicalProposalReceiptActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

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
	private boolean showAdministrationFee;

	private HealthType healthType;

	private Date updatedSubmittedDate;
	private BankCharges bankCharges;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalProposal = (medicalProposal == null) ? (MedicalProposal) getParam("medicalProposal") : medicalProposal;
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
		healthType = (HealthType) getParam("WORKFLOWHEALTHTYPE");
		updatedSubmittedDate = (Date) getParam("updatedSubmittedDate");
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
		// bankCharges = new BankCharges();

		AcceptedInfo acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(medicalProposal.getId(), ReferenceType.MEDICAL_PROPOSAL);
		if (acceptedInfo != null) {
			payment.setDiscountPercent(acceptedInfo.getDiscountPercent());
			payment.setServicesCharges(acceptedInfo.getServicesCharges());
			payment.setStampFees(acceptedInfo.getStampFees());
			payment.setAdministrationFees(acceptedInfo.getAdministrationFees());
		}
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		presentDate = format.format(new Date());

	}

	public void addNewMedicalProposalReceiptInfo() {
		try {
			if (!validPayment()) {
				return;
			}
			switch (healthType) {
				case CRITICALILLNESS:
					payment.setReferenceType(PolicyReferenceType.CRITICAL_ILLNESS_POLICY);
					break;
				case HEALTH:
					payment.setReferenceType(PolicyReferenceType.HEALTH_POLICY);
					break;
				case MEDICAL_INSURANCE:
					payment.setReferenceType(PolicyReferenceType.MEDICAL_POLICY);
					break;
				case MICROHEALTH:
					payment.setReferenceType(PolicyReferenceType.MICRO_HEALTH_POLICY);
					break;
				default:
					break;

			}

			List<Payment> paymentList = null;

			/*
			 * if (bankCharges.getName() == null) { payment.setBankWallet(null);
			 * }
			 */
			if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				payment.setBankWallet(bankCharges);
			}
			if (medicalProposal.isSkipPaymentTLF()) {
				paymentList = medicalProposalService.confirmSkipPaymentTLFMedicalProposal(medicalProposal, workFlowDTO, payment, user.getBranch(), RequestStatus.FINISHED.name(),
						updatedSubmittedDate);
			} else {
				paymentList = medicalProposalService.confirmMedicalProposal(medicalProposal, workFlowDTO, payment, user.getBranch(), RequestStatus.FINISHED.name(), false,
						updatedSubmittedDate);
			}

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

	public boolean isAtualPayment() {
		return actualPayment;
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

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
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
	private String fileName;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		changeFileName(medicalProposal);
		fileName = fileName + "_Receipt" + ".pdf";
		MedicalUnderwritingDocFactory.generateMedicalCashReceipt(medicalProposal, payment, dirPath, fileName, healthType);

	}

	private void changeFileName(MedicalProposal medicalProposal) {
		String customerName = medicalProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		switch (healthType) {
			case CRITICALILLNESS:
				fileName = "Critical_Illness_" + customerName;
				break;
			case HEALTH:
				fileName = "Medical_" + customerName;
				break;
			case MICROHEALTH:
				fileName = "Micro_Health_" + customerName;
				break;
			case MEDICAL_INSURANCE:
			default:
				break;
		}
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
		// bankCharges = new BankCharges();
		bankCharges.setName(null);
		bankCharges.setCharges(0.00);
	}

	public double getCharges() {
		if (null != bankCharges) {
			if (bankCharges.getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
				return bankCharges.getCharges() + bankCharges.getAdditionalCharges();
			} else {
				return (medicalProposal.getTermPremium() * bankCharges.getCharges() / 100) + bankCharges.getAdditionalCharges();
			}
		}
		return 0.00;
	}

}
