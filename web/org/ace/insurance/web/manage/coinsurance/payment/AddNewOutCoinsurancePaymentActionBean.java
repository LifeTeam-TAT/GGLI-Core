package org.ace.insurance.web.manage.coinsurance.payment;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "AddNewOutCoinsurancePaymentActionBean")
public class AddNewOutCoinsurancePaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	private User user;
	private Coinsurance coinsurance;
	private boolean isCheque;
	private LifePolicy lPolicy;
	private boolean disablePrintBtn;
	private final String reportName = "outCoinsuranceCommissionPayment";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	private String premiumtype;
	private String insurerName;
	private List<WorkFlowHistory> workFlowList;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		coinsurance = (coinsurance == null) ? (Coinsurance) getParam("Coinsurance") : coinsurance;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		if (coinsurance.getInsuranceType().equals(InsuranceType.LIFE)) {
			lPolicy = lifePolicyService.findLifePolicyByPolicyNo(coinsurance.getPolicyNo());
			insurerName = lPolicy.getCustomerName();
			premiumtype = "Life Premium";
		}
		if (coinsurance.getInsuranceType().equals(InsuranceType.LIFE)) {
			workFlowList = workFlowService.findWorkFlowHistoryByRefNo(lPolicy.getLifeProposal().getId());
		}
	}

	private class COACode {
		public static final String MOTOR_SUNDRY = "MOTOR_SUNDRY";
		public static final String MOTOR_COINSURANCE_COMMISSION = "MOTOR_COINSURANCE_COMMISSION";
		public static final String FIRE_SUNDRY = "FIRE_SUNDRY";
		public static final String FIRE_COINSURANCE_COMMISSION = "FIRE_COINSURANCE_COMMISSION";
		public static final String LIFE_SUNDRY = "LIFE_SUNDRY";
		public static final String LIFE_COINSURANCE_COMMISSION = "LIFE_COINSURANCE_COMMISSION";

	}

	public LifePolicy getlPolicy() {
		return lPolicy;
	}

	public void setlPolicy(LifePolicy lPolicy) {
		this.lPolicy = lPolicy;
	}

	public Coinsurance getCoinsurance() {
		return coinsurance;
	}

	public void setCoinsurance(Coinsurance coinsurance) {
		this.coinsurance = coinsurance;
	}

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		String coaName = null;
		String coaName2 = null;
		String coaName3 = null;
		String coaName4 = null;
		TLF tlfForCoInsuCommDebit;
		TLF tlfForCoInsuCommCredit;
		TLF tlfForCoInsuPayCr;
		TLF tlfForCoInsuPayDr;
		double commission = 0.0;
		double premium = 0.0;
		String paymentCcoaCode1 = null;
		String paymentCcoaCode2 = null;
		String paymentCcoaCode3 = null;
		String paymentCcoaCode4 = null;
		String currencyCode = CurrencyUtils.getCurrencyCode(null);
		Branch branch = coinsurance.getBranch();
		commission = Utils.getPercentOf(coinsurance.getCommissionPercent(), coinsurance.getPremium());
		premium = coinsurance.getPremium() - commission;
		if (coinsurance.getInsuranceType() != null && coinsurance.getInsuranceType().equals(InsuranceType.LIFE)) {
			paymentCcoaCode1 = paymentService.findCheckOfAccountCode(COACode.LIFE_SUNDRY, branch, currencyCode);

		}

		// type 1
		tlfForCoInsuPayCr = new TLF();
		tlfForCoInsuPayCr.setCoaId(paymentCcoaCode1);// coa code 1
		tlfForCoInsuPayCr.setHomeAmount(premium);

		if (coinsurance.getPaymentChannel() != null) {
			if (coinsurance.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paymentCcoaCode2 = coinsurance.getCompanybank().getAcode();
			} else {
				paymentCcoaCode2 = paymentService.findCheckOfAccountCode("CASH", branch, currencyCode);
			}
		}
		// type 2
		tlfForCoInsuPayDr = new TLF();
		tlfForCoInsuPayDr.setCoaId(paymentCcoaCode2);// coa code 2
		tlfForCoInsuPayDr.setHomeAmount(premium);

		// type 3 CoInsuCommReceived_Debit
		if (coinsurance.getInsuranceType() != null && coinsurance.getInsuranceType().equals(InsuranceType.LIFE)) {
			paymentCcoaCode3 = paymentService.findCheckOfAccountCode(COACode.LIFE_SUNDRY, branch, currencyCode);

		}

		// type 3
		tlfForCoInsuCommDebit = new TLF();
		tlfForCoInsuCommDebit.setCoaId(paymentCcoaCode3);// coa code 3
		tlfForCoInsuCommDebit.setHomeAmount(commission);

		// type 4 CoInsuCommReceived_Credit
		if (coinsurance.getInsuranceType() != null && coinsurance.getInsuranceType().equals(InsuranceType.LIFE)) {
			paymentCcoaCode4 = paymentService.findCheckOfAccountCode(COACode.LIFE_COINSURANCE_COMMISSION, branch, currencyCode);

		}

		tlfForCoInsuCommCredit = new TLF();
		tlfForCoInsuCommCredit.setCoaId(paymentCcoaCode4);
		tlfForCoInsuCommCredit.setHomeAmount(commission);

		if (tlfForCoInsuPayCr.getCoaId() != null) {
			coaName = paymentService.findCOAAccountNameByCode(tlfForCoInsuPayCr.getCoaId());
		}
		if (tlfForCoInsuPayDr.getCoaId() != null) {
			coaName2 = paymentService.findCOAAccountNameByCode(tlfForCoInsuPayDr.getCoaId());
		}
		if (tlfForCoInsuCommDebit.getCoaId() != null) {
			coaName3 = paymentService.findCOAAccountNameByCode(tlfForCoInsuCommDebit.getCoaId());
		}
		if (tlfForCoInsuCommCredit.getCoaId() != null) {
			coaName4 = paymentService.findCOAAccountNameByCode(tlfForCoInsuCommCredit.getCoaId());
		}

		DocumentBuilder.generateOutCoinsuranceCommissionPayment(coinsurance, tlfForCoInsuPayCr, tlfForCoInsuPayDr, tlfForCoInsuCommDebit, tlfForCoInsuCommCredit, premiumtype,
				insurerName, coaName, coaName2, coaName3, coaName4, dirPath, fileName);
	}

	private boolean validPayment() {
		boolean valid = true;
		String formID = "coinsurancePaymentForm";
		if (isCheque) {
			if (coinsurance.getChequeNo() == null || coinsurance.getChequeNo().isEmpty()) {
				addErrorMessage(formID + ":chequeNo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (coinsurance.getCompanybank() == null) {
				addErrorMessage(formID + ":bankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}
		return valid;
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		if (!isCheque) {
			coinsurance.setCompanybank(null);
			coinsurance.setChequeNo(null);
		}
	}

	public boolean getIsCheque() {
		return isCheque;
	}

	public void setIsCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public boolean isLifePolicy() {
		if (coinsurance.getInsuranceType() != null && coinsurance.getInsuranceType().equals(InsuranceType.LIFE)) {
			return true;
		}
		return false;
	}

	public boolean isDisablePrintBtn() {
		return disablePrintBtn;
	}

	public void setDisablePrintBtn(boolean disablePrintBtn) {
		this.disablePrintBtn = disablePrintBtn;
	}

	public String payCoinsurance() {
		String result = null;
		try {
			if (!validPayment()) {
				// return result;
			}
			// if(isCheque) {
			// coinsurance.setPaymentChannel(PaymentChannel.CHEQUE);
			// } else {
			// coinsurance.setPaymentChannel(PaymentChannel.CASHED);
			// }
			coinsurance.setPaymentDate(new Date());
			// TODO FIX PSH
			// paymentService.paymentOutCoinsurance(coinsurance,
			// coinsurance.getBranch(), CurrencyUtils.getCurrencyCode(null));

			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}
}
