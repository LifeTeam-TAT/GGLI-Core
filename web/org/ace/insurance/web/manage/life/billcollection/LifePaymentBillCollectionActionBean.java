package org.ace.insurance.web.manage.life.billcollection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.migrate.service.interfaces.IShortEndowmentExtraValueService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.payment.AgentPaymentCashReceiptDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 * 
 * Life Payment Bill Collection ActionBean
 * 
 * @author Tun Lin Aung
 * @since 1.0.0
 * @date 2013/09/19
 */
@ViewScoped
@ManagedBean(name = "LifePaymentBillCollectionActionBean")
public class LifePaymentBillCollectionActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{LifePolicySummaryService}")
	private ILifePolicySummaryService lifePolicySummaryService;

	public void setLifePolicySummaryService(ILifePolicySummaryService lifePolicySummaryService) {
		this.lifePolicySummaryService = lifePolicySummaryService;
	}

	@ManagedProperty(value = "#{ShortEndowmentExtraValueService}")
	private IShortEndowmentExtraValueService shortEndowmentExtraValueService;

	public void setShortEndowmentExtraValueService(IShortEndowmentExtraValueService shortEndowmentExtraValueService) {
		this.shortEndowmentExtraValueService = shortEndowmentExtraValueService;
	}

	private BillCollectionDTO billCollection;
	private List<BillCollectionDTO> billCollectionList;
	private BillCollectionDTO[] selectedDTOList;
	private List<BillCollectionCashReceiptDTO> cashReceiptDTOList;
	private boolean availablePrint;
	private PolicyCriteria policyCriteria;
	private boolean isCheque;
	private boolean renderButton;
	private List<Payment> payments;
	private User user;
	private boolean isTransfer;
	private boolean isBank;
	private boolean isAccBank;
	private PaymentChannel channelValue;
	private String poNo;
	private Bank bank;
	private Bank accountBank;
	private String chequeNo;
	private boolean validPaidUpPolicNo;
	private boolean validSurrenderPolicy;
	private boolean isSundry;
	private boolean skipTLF;
	private SalePoint salePoint;
	private BankCharges bankCharges;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		reset();

	}

	public void reset() {
		salePoint = new SalePoint();
		policyCriteria = new PolicyCriteria();
		policyCriteria.setCriteriaValue(null);
		policyCriteria.setPolicyCriteria(null);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		policyCriteria.setFromDate(cal.getTime());
		Date endDate = new Date();
		policyCriteria.setToDate(endDate);
		billCollectionList = new ArrayList<BillCollectionDTO>();
		billCollection = new BillCollectionDTO();
		policyCriteria.setReferenceType(ReferenceType.LIFE_POLICY);
		channelValue = null;
		isAccBank = false;
		isCheque = false;
		isBank = false;
		isTransfer = false;
		bank = null;
		accountBank = null;
		chequeNo = null;
		poNo = null;
		// bankCharges.setCharges(0.00);
		renderButton = true;
	}

	public boolean validation() {
		boolean result = true;
		if ((policyCriteria.getPolicyCriteria() != null)) {
			if (policyCriteria.getCriteriaValue() == null || policyCriteria.getCriteriaValue().isEmpty()) {
				addErrorMessage("paymentBillCollectionForm:policyCriteria", MessageId.ATLEAST_ONE_REQUIRED);
				result = false;
			}
		}

		if (policyCriteria.getCriteriaValue() != null && !policyCriteria.getCriteriaValue().isEmpty()) {
			if (policyCriteria.getPolicyCriteria() == null) {
				addErrorMessage("paymentBillCollectionForm:selectPolicyCriteria", MessageId.ATLEAST_ONE_REQUIRED);
				result = false;
			}
		}
		return result;

	}

	private boolean isMedicalReferenceType() {
		return ReferenceType.MEDICAL_POLICY.equals(policyCriteria.getReferenceType()) || ReferenceType.HEALTH_POLICY.equals(policyCriteria.getReferenceType())
				|| ReferenceType.MICRO_HEALTH_POLICY.equals(policyCriteria.getReferenceType()) || ReferenceType.CRITICAL_ILLNESS_POLICY.equals(policyCriteria.getReferenceType());
	}

	public void search() {
		if (validation()) {
			if (isMedicalReferenceType()) {
				billCollectionList = medicalPolicyService.findBillCollectionByCriteria(policyCriteria);
			} else {
				billCollectionList = lifePolicyService.findBillCollectionByCriteria(policyCriteria);
			}

			for (BillCollectionDTO billCollection : billCollectionList) {
				LifePolicySummary lifePolicySummary = lifePolicySummaryService.findLifePolicyByPolicyNo(billCollection.getPolicyNo());
				if (lifePolicySummary != null) {
					billCollection.setRefund(lifePolicySummary.getRefund());
				}
			}
			if (billCollectionList.size() > 0) {
				renderButton = false;
			}
			availablePrint = false;
		}
		// policyCriteria = new PolicyCriteria();
	}

	public void confirm() {
		if (!validCorrelationCheck()) {
			return;
		}
		try {
			payments = new ArrayList<Payment>();
			cashReceiptDTOList = new ArrayList<BillCollectionCashReceiptDTO>();
			double rate = 1.0;
			for (BillCollectionDTO billCollection : selectedDTOList) {
				Payment payment = new Payment();
				payment.setBank(bank);
				payment.setAccountBank(accountBank);
				payment.setChequeNo(chequeNo);
				payment.setPoNo(poNo);
				payment.setPaymentChannel(channelValue);

				if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
					payment.setBankWallet(bankCharges);
				}
				payment.setReferenceNo(billCollection.getPolicyId());
				if (policyCriteria.getReferenceType().equals(ReferenceType.LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.LIFE_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.MEDICAL_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.MEDICAL_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.CRITICAL_ILLNESS_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.HEALTH_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.SHORT_ENDOWMENT_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.STUDENT_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.SINGLE_PREMIUM_ENDOWMNET_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
				}

				// TODO FIXME multiply by payment times
				payment.setBasicPremium((billCollection.getBasicTermPremium() * billCollection.getPaymentTimes()) - billCollection.getExtraAmount());
				payment.setAddOnPremium(billCollection.getAddOnPremium() * billCollection.getPaymentTimes());
				payment.setDiscountPercent(billCollection.getDiscountPercent());
				payment.setNcbPremium(billCollection.getNcbPremium());
				payment.setServicesCharges(billCollection.getServiceCharges());
				if (payment.getBankWallet() != null) {
					payment.setBankWallet(payment.getBankWallet());
					if (payment.getBankWallet().getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
						payment.setBankCharges(payment.getBankWallet().getCharges() + payment.getBankWallet().getAdditionalCharges());
						payment.setHomeBankCharges(rate * (payment.getBankWallet().getCharges() + payment.getBankWallet().getAdditionalCharges()));
					} else {
						payment.setBankCharges(payment.getBankWallet().getCharges() * billCollection.getTermPremium() / 100);
						payment.setHomeBankCharges(rate * (payment.getBankWallet().getCharges() * billCollection.getTermPremium() / 100));
					}
				}
				payment.setRenewalInterest(billCollection.getRenewalInterest());
				payment.setLoanInterest(billCollection.getLoanInterest());
				payment.setRefund(billCollection.getRefund());
				payment.setAmount(payment.getTotalAmount());
				payment.setHomeAmount(payment.getTotalAmount());
				payment.setHomePremium(payment.getBasicPremium());

				// payment.setHomeBankCharges(rate * payment.getBankCharges());
				payment.setComplete(false);
				payment.setFromTerm(billCollection.getLastPaymentTerm() + 1);
				payment.setToTerm(billCollection.getLastPaymentTerm() + billCollection.getPaymentTimes());
				payment.setPaymentType(billCollection.getPaymentType());
				payment.setConfirmDate(new Date());

				// multiply basic premium if payment times is more than 1
				// if (billCollection.getPaymentTimes() > 1) {
				// double basicPremium = payment.getBasicPremium() *
				// billCollection.getPaymentTimes();
				// payment.setBasicPremium(basicPremium);
				// }

				payments.add(payment);
				cashReceiptDTOList.add(new BillCollectionCashReceiptDTO(payment, billCollection, policyCriteria.getReferenceType()));

			}
			Payment payment = payments.get(0);
			String currencyId = payment.getCur() == null ? KeyFactorChecker.getKyatId()
					: payment.getCur().equalsIgnoreCase("KYT") ? KeyFactorChecker.getKyatId() : KeyFactorChecker.getUsdId();
			paymentService.extendPaymentTimes(payments, user.getBranch(), currencyId);
			renderButton = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS);
			availablePrint = true;

		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void editBill(ActionEvent event) {
		int paymentTimes = billCollection.getPaymentTimes();
		if (paymentTimes < 1) {
			billCollection.setPaymentTimes(1);
			addErrorMessage("lifePaymentDialogForm:paymentTimes", UIInput.REQUIRED_MESSAGE_ID);
			PrimeFaces.current().executeScript("PF('lifePaymentDialog').show()");
			// TODO FIXME lastPaymentTerm + paymentTime must be < total payment
			// term
		} else {
			int totalPaymentTimes = 0;
			if (ReferenceType.MEDICAL_POLICY.equals(policyCriteria.getReferenceType()) || ReferenceType.HEALTH_POLICY.equals(policyCriteria.getReferenceType())
					|| ReferenceType.CRITICAL_ILLNESS_POLICY.equals(policyCriteria.getReferenceType())) {
				MedicalPolicy policy = medicalPolicyService.findMedicalPolicyById(billCollection.getPolicyId());
				totalPaymentTimes = policy.getTotalPaymentTimes();
			} else {
				LifePolicy policy = lifePolicyService.findLifePolicyById(billCollection.getPolicyId());
				totalPaymentTimes = policy.getTotalPaymentTimes();
			}

			if (totalPaymentTimes > 0 && billCollection.getLastPaymentTerm() + paymentTimes > totalPaymentTimes) {
				billCollection.setPaymentTimes(totalPaymentTimes - billCollection.getLastPaymentTerm());
				addErrorMessage("lifePaymentDialogForm:paymentTimes", MessageId.CANT_BE_GREATER_THAN_REMAINING_PAYMENT_TIMES);
				PrimeFaces.current().executeScript("PF('lifePaymentDialog').show()");
			} else {
				Date date = billCollection.getStartDate();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.MONTH, billCollection.getPaymentType().getMonth() * paymentTimes);
				billCollection.setEndDate(calendar.getTime());
				PrimeFaces.current().executeScript("PF('lifePaymentDialog').hide()");
			}
		}
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		bank = null;
		accountBank = null;
		chequeNo = null;
		poNo = null;
		// bankCharges = 0.00;
	}

	private boolean validCorrelationCheck() {
		boolean valid = true;
		String formID = "paymentBillCollectionForm";
		if (isCheque) {
			if (chequeNo == null || chequeNo.isEmpty()) {
				addErrorMessage(formID + ":chequeNo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (bank == null) {
				addErrorMessage(formID + ":bankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (accountBank == null) {
				addErrorMessage(formID + ":accountBankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}

		}
		if (isTransfer) {
			if (poNo == null || poNo.isEmpty()) {
				addErrorMessage(formID + ":poNo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (bank == null) {
				addErrorMessage(formID + ":bankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (accountBank == null) {
				addErrorMessage(formID + ":accountBankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}

		if (selectedDTOList.length == 0) {
			addErrorMessage("paymentBillCollectionForm:lifePolicyInfoTable", MessageId.ATLEAST_ONE_CHECK_REQUIRED);
			valid = false;
		}

		for (BillCollectionDTO bcDTO : selectedDTOList) {
			if (bcDTO.getPaymentTimes() < 0) {
				addErrorMessage("paymentBillCollectionForm:lifePolicyInfoTable", MessageId.PAYMENTTIMES_GRT_ONE);
				valid = false;
			}
		}
		return valid;
	}

	private final String reportName = "LifeBillCollectionCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}

	public void generateReport() {
		if (selectedDTOList.length == 0) {
			addErrorMessage("paymentBillCollectionForm:lifePolicyInfoTable", MessageId.ATLEAST_ONE_CHECK_REQUIRED);
			return;
		} else {
			for (BillCollectionCashReceiptDTO dto : cashReceiptDTOList) {
				if (ReferenceType.MEDICAL_POLICY.equals(dto.getReferenceType()) || ReferenceType.HEALTH_POLICY.equals(dto.getReferenceType())
						|| ReferenceType.CRITICAL_ILLNESS_POLICY.equals(dto.getReferenceType()) || ReferenceType.MICRO_HEALTH_POLICY.equals(dto.getReferenceType())) {
					MedicalPolicy policy = medicalPolicyService.findMedicalPolicyById(dto.getBillCollection().getPolicyId());
					dto.setMedicalPolicy(policy);
					if (policy.getAgent() != null) {
						AgentPaymentCashReceiptDTO agentDto = paymentService.getAgentPaymentCashReceipt(policy, InsuranceType.MEDICAL, user.getBranch(), 0);
						dto.setAgentComission(agentDto);
					}
				} else {
					LifePolicy policy = lifePolicyService.findLifePolicyById(dto.getBillCollection().getPolicyId());
					dto.setLifePolicy(policy);
					if (policy.getAgent() != null) {
						AgentPaymentCashReceiptDTO agentDto = paymentService.getAgentPaymentCashReceipt(policy, InsuranceType.LIFE, user.getBranch(), 0);
						dto.setAgentComission(agentDto);
					}
				}

			}
			DocumentBuilder.generateLifePaymentBillCashReceipt(cashReceiptDTOList, dirPath, fileName, false);
		}
	}

	public boolean isAvailablePrint() {
		return availablePrint;
	}

	public BillCollectionDTO getBillCollection() {
		return billCollection;
	}

	public void setBillCollection(BillCollectionDTO lineBean) {
		this.billCollection = lineBean;
	}

	public BillCollectionModel getBillCollectionModel() {
		return new BillCollectionModel(billCollectionList);
	}

	public BillCollectionDTO[] getSelectedDTOList() {
		return selectedDTOList;
	}

	public void setSelectedDTOList(BillCollectionDTO[] selectedLineBeans) {
		this.selectedDTOList = selectedLineBeans;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public PolicyCriteria getPolicyCriteria() {
		return policyCriteria;
	}

	public void setPolicyCriteria(PolicyCriteria policyCriteria) {
		this.policyCriteria = policyCriteria;
	}

	public String getSumTotalAmounts() {
		double sumTotalAmounts = 0.0;
		if (billCollectionList != null) {
			for (BillCollectionDTO line : billCollectionList) {
				sumTotalAmounts += line.getTotalAmount();
			}
		}
		return Utils.getCurrencyFormatString(sumTotalAmounts);
	}

	public boolean getIsCheque() {
		return isCheque;
	}

	public void setCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public boolean isRenderButton() {
		return renderButton;
	}

	public void setRenderButton(boolean renderButton) {
		this.renderButton = renderButton;
	}

	public Bank getBank() {
		return bank;
	}

	public void returnBank(SelectEvent event) {
		bank = (Bank) event.getObject();
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public PaymentChannel getChannelValue() {
		return channelValue;
	}

	public void setChannelValue(PaymentChannel channelValue) {
		if (channelValue != null) {
			if (channelValue.equals(PaymentChannel.CHEQUE)) {
				isAccBank = true;
				isCheque = true;
				isBank = true;
				isTransfer = false;
				isSundry = false;
			} else if (channelValue.equals(PaymentChannel.SUNDRY)) {
				isAccBank = true;
				isCheque = false;
				isBank = false;
				isTransfer = false;
				isSundry = true;
			} else if (channelValue.equals(PaymentChannel.TRANSFER)) {
				isAccBank = true;
				isCheque = false;
				isBank = true;
				isTransfer = true;
				isSundry = false;
			} else {
				isAccBank = false;
				isCheque = false;
				isBank = false;
				isTransfer = false;
				isSundry = false;
			}
			this.channelValue = channelValue;
		}
	}

	public void confirmSkipTLF() {
		if (!validCorrelationCheck()) {
			return;
		}
		try {
			payments = new ArrayList<Payment>();
			cashReceiptDTOList = new ArrayList<BillCollectionCashReceiptDTO>();
			for (BillCollectionDTO billCollection : selectedDTOList) {
				Payment payment = new Payment();
				payment.setBank(bank);
				payment.setAccountBank(accountBank);
				payment.setChequeNo(chequeNo);
				payment.setPoNo(poNo);
				if (payment.getBankWallet() != null) {
					payment.setBankCharges(payment.getBankWallet().getCharges());
				}
				payment.setPaymentChannel(channelValue);
				payment.setReferenceNo(billCollection.getPolicyId());
				if (policyCriteria.getReferenceType().equals(ReferenceType.LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.LIFE_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.MEDICAL_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.MEDICAL_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.CRITICAL_ILLNESS_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.HEALTH_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.SHORT_ENDOWMENT_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.STUDENT_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.SINGLE_PREMIUM_ENDOWMNET_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION);
				}
				if (policyCriteria.getReferenceType().equals(ReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY)) {
					payment.setReferenceType(PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
				}

				// TODO FIXME multiply by payment times
				payment.setBasicPremium((billCollection.getBasicTermPremium() * billCollection.getPaymentTimes()) - billCollection.getExtraAmount());
				payment.setAddOnPremium(billCollection.getAddOnPremium() * billCollection.getPaymentTimes());
				payment.setDiscountPercent(billCollection.getDiscountPercent());
				payment.setNcbPremium(billCollection.getNcbPremium());
				payment.setServicesCharges(billCollection.getServiceCharges());
				payment.setRenewalInterest(billCollection.getRenewalInterest());
				payment.setLoanInterest(billCollection.getLoanInterest());
				payment.setRefund(billCollection.getRefund());
				payment.setAmount(payment.getNetPremium());
				payment.setComplete(false);
				payment.setFromTerm(billCollection.getLastPaymentTerm() + 1);
				payment.setToTerm(billCollection.getLastPaymentTerm() + billCollection.getPaymentTimes());
				payment.setPaymentType(billCollection.getPaymentType());
				payment.setConfirmDate(new Date());

				// multiply basic premium if payment times is more than 1
				// if (billCollection.getPaymentTimes() > 1) {
				// double basicPremium = payment.getBasicPremium() *
				// billCollection.getPaymentTimes();
				// payment.setBasicPremium(basicPremium);
				// }

				payments.add(payment);
				cashReceiptDTOList.add(new BillCollectionCashReceiptDTO(payment, billCollection, policyCriteria.getReferenceType()));

			}
			if (skipTLF) {
				paymentService.extendPaymentTimesAccountSkip(payments, user.getBranch(), CurrencyUtils.getCurrencyCode(null));
			} else {
				paymentService.extendPaymentTimes(payments, user.getBranch(), CurrencyUtils.getCurrencyCode(null));
			}
			renderButton = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS);
			availablePrint = true;

		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public ReferenceType[] getReferenceTypeSelectedItemList() {
		return new ReferenceType[] { ReferenceType.LIFE_POLICY, ReferenceType.MEDICAL_POLICY, ReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, ReferenceType.HEALTH_POLICY,
				ReferenceType.MICRO_HEALTH_POLICY, ReferenceType.CRITICAL_ILLNESS_POLICY, ReferenceType.STUDENT_LIFE_POLICY, ReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY,
				ReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY, ReferenceType.SINGLE_PREMIUM_ENDOWMNET_LIFE_POLICY };
	}

	public void returnAccountBank(SelectEvent event) {
		accountBank = (Bank) event.getObject();
	}

	public boolean getIsTransfer() {
		return isTransfer;
	}

	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	public boolean getIsBank() {
		return isBank;
	}

	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}

	public BankCharges getBankCharges() {
		return bankCharges;
	}

	public void setBankCharges(BankCharges bankCharges) {
		this.bankCharges = bankCharges;
	}

	public Bank getAccountBank() {
		return accountBank;
	}

	public boolean isAccBank() {
		return isAccBank;
	}

	public void setAccBank(boolean isAccBank) {
		this.isAccBank = isAccBank;
	}

	public void setAccountBank(Bank accountBank) {
		this.accountBank = accountBank;
	}

	public boolean getIsSundry() {
		return isSundry;
	}

	public void setSundry(boolean isSundry) {
		this.isSundry = isSundry;
	}

	public boolean isSkipTLF() {
		return skipTLF;
	}

	public void setSkipTLF(boolean skipTLF) {
		this.skipTLF = skipTLF;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public void selectSalePointByBranch() {
		selectSalePointByBranch(user.getBranch().getId());
	}

	public void returnSalePoint(SelectEvent event) {
		salePoint = (SalePoint) event.getObject();
		policyCriteria.setSalePointId(salePoint.getId());
	}

	public void returnBankWallet(SelectEvent event) {
		bankCharges = (BankCharges) event.getObject();
	}

	public void removeBankWallet() {
		bankCharges = new BankCharges();
		/*
		 * bankCharges.setName(null); bankCharges.setCharges(0.00);
		 */
	}

	public double getCharges() {
		if (null != bankCharges) {
			if (bankCharges.getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
				return bankCharges.getCharges() + bankCharges.getAdditionalCharges();
			} else {
				return (billCollectionList.get(0).getTermPremium() * bankCharges.getCharges() / 100) + bankCharges.getAdditionalCharges();
			}
		}
		return 0.00;
	}

}
