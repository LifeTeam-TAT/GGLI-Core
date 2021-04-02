package org.ace.insurance.web.manage.coinsurance.payment;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "AddNewInCoinsurancePaymentActionBean")
public class AddNewInCoinsurancePaymentActionBean extends BaseBean implements Serializable {
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

	private Coinsurance coinsurance;
	private User user;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		coinsurance = (coinsurance == null) ? (Coinsurance) getParam("Coinsurance") : coinsurance;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
	}

	@PreDestroy
	public void destroy() {
		removeParam("Coinsurance");
	}

	public String confirm() {
		String result = null;
		try {
			coinsurance.setPaymentDate(new Date());
			paymentService.paymentInCoinsurance(coinsurance, user.getBranch(), CurrencyUtils.getCurrencyCode(null));
			addInfoMessage(null, MessageId.PAYMENT_PROCESS_SUCCESS, coinsurance.getCoinsuranceCompany().getId());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public Coinsurance getCoinsurance() {
		return coinsurance;
	}

	public void setCoinsurance(Coinsurance coinsurance) {
		this.coinsurance = coinsurance;
	}
}
