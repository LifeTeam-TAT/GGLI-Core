package org.ace.insurance.web.manage.coinsurance.confirm;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewInCoinsuranceConfirmationAtionBean")
public class AddNewInCoinsuranceConfirmationAtionBean extends BaseBean {

	@ManagedProperty(value = "#{CoinsuranceService}")
	private ICoinsuranceService coinsuranceService;

	public void setCoinsuranceService(ICoinsuranceService coinsuranceService) {
		this.coinsuranceService = coinsuranceService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private static final String FORM_ID = "inCoinsuranceConfirmaitonForm";
	private User user;
	private WorkFlowDTO workFlowDTO;
	private Coinsurance coinsurance;
	private String remark;
	private User responsiblePerson;
	private boolean isCheque;
	private boolean disableConfirm;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
		coinsurance = (coinsurance == null) ? (Coinsurance) getParam("Coinsurance") : coinsurance;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
	}

	public void confirmInCoinsurance() {
		if (!isValid()) {
			return;
		}
		try {
			if (isCheque) {
				coinsurance.setPaymentChannel(PaymentChannel.CHEQUE);
			} else {
				coinsurance.setPaymentChannel(PaymentChannel.CASHED);
			}
			workFlowDTO = new WorkFlowDTO(coinsurance.getId(), remark, WorkflowTask.COINSURANCE_PAYMENT, WorkflowReferenceType.COINSURANCE, user, responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			coinsuranceService.confirmCoinsurance(coinsurance);
			disableConfirm = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void selectRespPerson() {
		selectUser(WorkflowTask.COINSURANCE_PAYMENT, WorkFlowType.COINSURANCE);
	}

	private boolean isValid() {
		boolean valid = true;

		if (isCheque) {
			if (coinsurance.getCustomerbank() == null) {
				addErrorMessage(FORM_ID + ":bankName", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}

		if (responsiblePerson == null) {
			addErrorMessage(FORM_ID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		return valid;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		if (!isCheque) {
			coinsurance.setCustomerbank(null);
			coinsurance.setChequeNo(null);
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean getIsCheque() {
		return isCheque;
	}

	public void setIsCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public Coinsurance getCoinsurance() {
		return coinsurance;
	}

	public void setCoinsurance(Coinsurance coinsurance) {
		this.coinsurance = coinsurance;
	}

	public boolean isDisableConfirm() {
		return disableConfirm;
	}

	public void setDisableConfirm(boolean disableConfirm) {
		this.disableConfirm = disableConfirm;
	}

	private final String reportName = "coinsuranceCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		String coaCodeCredit = null;
		String accountNameCredit = null;
		String accountNameDebit = null;
		String debitLetter = null;
		if (coinsurance.getCustomerbank() != null) {
			accountNameDebit = coinsurance.getCustomerbank().getAcode();
			debitLetter = paymentService.findCOAAccountNameByCode(accountNameDebit);
		}

		DocumentBuilder.generatecoinsuranceCashReceipt(coinsurance, dirPath, fileName, accountNameCredit, accountNameDebit, debitLetter);
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		coinsurance.setCustomerbank(bank);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		responsiblePerson = user;
	}
}
