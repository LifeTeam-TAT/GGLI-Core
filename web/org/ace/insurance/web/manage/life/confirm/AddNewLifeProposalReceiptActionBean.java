
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
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewLifeProposalReceiptActionBean")
public class AddNewLifeProposalReceiptActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{LifeEndorsementService}")
	private ILifeEndorsementService lifeEndorsementService;

	public void setLifeEndorsementService(ILifeEndorsementService lifeEndorsementService) {
		this.lifeEndorsementService = lifeEndorsementService;
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
	private String fileName;
	private boolean actualPayment;
	private boolean isEndorsement;
	private boolean isPersonalAccident;
	private boolean isFarmer;
	private boolean isPublicTermLife;
	private boolean isShortEndowLife;
	private boolean isPublicLife;
	private boolean isSnakeBite;
	private boolean isGroupLife;
	private boolean isSportMan;
	private boolean isSinglePremiumEndowmentLife;
	private boolean isSinglePremiumCreditLife;
	private boolean isShortTermSinglePremiumCreditLife;
	private boolean isSimpleLife;
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
		isEndorsement = lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT);
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		isPersonalAccident = KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product);
		isFarmer = KeyFactorChecker.isFarmer(product);
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
		isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
		isPublicLife = KeyFactorChecker.isPublicLife(product);
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		isSimpleLife = KeyFactorChecker.isSimpleLife(product);
		isSportMan = KeyFactorChecker.isSportMan(product);
		isGroupLife = KeyFactorChecker.isGroupLife(product);
		isSnakeBite = KeyFactorChecker.isSnakeBite(product);
		ReferenceType referenceType = isPersonalAccident ? ReferenceType.PA_PROPOSAL
				: isFarmer ? ReferenceType.FARMER_PROPOSAL
						: isPublicTermLife ? ReferenceType.PUBLIC_TERM_LIFE_PROPOSAL
								: isSinglePremiumEndowmentLife ? ReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
										: isSinglePremiumCreditLife ? ReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
												: isShortTermSinglePremiumCreditLife ? ReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
														// : isSimpleLife ?
														// ReferenceType.SIMPLE_LIFE_PROPOSAL//
														: isShortEndowLife ? ReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL : ReferenceType.LIFE_PROPOSAL;
		AcceptedInfo acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposal.getId(), referenceType);
		if (acceptedInfo != null) {
			payment.setDiscountPercent(acceptedInfo.getDiscountPercent());
			payment.setServicesCharges(acceptedInfo.getServicesCharges());
			payment.setStampFees(acceptedInfo.getStampFees());
		}
		if (isShortEndowLife || isSimpleLife)
			payment.setConfirmDate(new Date());
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	}

	public void addNewLifeProposalReceiptInfo() {
		try {
			List<Payment> paymentList = null;
			if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				payment.setBankWallet(bankCharges);
			}
			if (isEndorsement) {
				if (isShortEndowLife) {
					paymentList = lifeEndorsementService.confirmShortTermEndowLifeProposal(lifeProposal, workFlowDTO, user.getBranch());
				} else {
					paymentList = lifeEndorsementService.confirmLifeProposal(lifeProposal, workFlowDTO, payment, user.getBranch(), null);
				}
			} else {
				if (isFarmer && lifeProposal.isSkipPaymentTLF()) {
					paymentList = lifeProposalService.confirmSkipPaymentTLFLifeProposal(lifeProposal, workFlowDTO, payment, user.getBranch(), null);
				} else if (lifeProposal.isMigrate()) {
					paymentList = lifeProposalService.confirmMigrateShortTermProposal(lifeProposal, workFlowDTO, payment, user.getBranch(), null, false);
				} else {
					paymentList = lifeProposalService.confirmLifeProposal(lifeProposal, workFlowDTO, payment, user.getBranch(), null, false);
				}
			}
			payment = new PaymentDTO(paymentList);
			actualPayment = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, lifeProposal.getProposalNo());
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

	public PaymentChannel[] getChannelValues() {
		if (isSinglePremiumEndowmentLife) {
			PaymentChannel[] values = { PaymentChannel.TRANSFER, PaymentChannel.CHEQUE, PaymentChannel.SUNDRY };
			return values;
		}
		return PaymentChannel.values();
	}

	public boolean isAtualPayment() {
		return actualPayment;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public boolean isEndorsement() {
		return isEndorsement;
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

	private final String reportName = "LifeCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;

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
		fileName = (isPublicLife ? "PublicLife_"
				: isSinglePremiumEndowmentLife ? "SinglePremiumEndowmentLife_"
						: isSinglePremiumCreditLife ? "SinglePremiumCreditLife_"
								: isShortTermSinglePremiumCreditLife ? "ShortTermSinglePremiumCreditLife_"
										: isSnakeBite ? "SnakeBite_"
												: isGroupLife ? "GroupLife_"
														: isFarmer ? "Farmer_" : isPublicTermLife ? "PublicTermLife_" : isShortEndowLife ? " ShortEndowLife_" : "SportMan_")
				+ customerName + "_Receipt" + ".pdf";
		paymentService.updateForCashReceiptGenerated(payment);
		DocumentBuilder.generateLifeCashReceipt(lifeProposal, payment, dirPath, fileName);
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

	public boolean getIsShortEndowLife() {
		return isShortEndowLife;
	}
	
	public boolean getIsSimpleLife() {
		return isSimpleLife;
	}

	public boolean isSinglePremiumEndowmentLife() {
		return isSinglePremiumEndowmentLife;
	}

	public boolean isSinglePremiumCreditLife() {
		return isSinglePremiumCreditLife;
	}

	public boolean isShortTermSinglePremiumCreditLife() {
		return isShortTermSinglePremiumCreditLife;
	}

	public Date getToday() {
		return new Date();
	}

	public BankCharges getBankCharges() {
		return bankCharges;
	}

	public void setBankCharges(BankCharges bankCharges) {
		this.bankCharges = bankCharges;
	}

	public String getPageHeader() {
		return (isEndorsement ? "Life Endorsement"
				: isSinglePremiumEndowmentLife ? "Single Premium Endowment Life"
						: isSinglePremiumCreditLife ? "Single Premium Credit Life"
								: isShortTermSinglePremiumCreditLife ? "Short Term Single Premium Credit Life"
										: isFarmer ? "Farmer"
												: isPublicTermLife ? "PublicTermLife"
														: isSimpleLife ? "SimpleLife"
														: isShortEndowLife ? "Short Term Endowment Life" : isPersonalAccident ? "Personal Accident" : "Life")
				+ " Proposal Confirm Print";
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
