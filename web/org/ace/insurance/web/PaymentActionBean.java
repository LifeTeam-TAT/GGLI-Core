package org.ace.insurance.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.payment.service.interfaces.IPaymentDelegateService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "PaymentActionBean")
public class PaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private PolicyReferenceType referenceType;
	private String remark;
	private String receiptStream;
	private boolean isRequired;
	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{PaymentDelegateService}")
	private IPaymentDelegateService paymentDelegateService;

	public void setPaymentDelegateService(IPaymentDelegateService paymentDelegateService) {
		this.paymentDelegateService = paymentDelegateService;
	}

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private List<PaymentDTO> paymentList;
	private User responsiblePerson;
	private List<User> userList;
	private User user;
	private SalePoint salePoint;
	private boolean skipTLF;
	private String branchId;

	@PostConstruct
	public void init() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		isRequired = false;
	}

	public PolicyReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(PolicyReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public String getReceiptStream() {
		return receiptStream;
	}

	public void setReceiptStream(String receiptStream) {
		this.receiptStream = receiptStream;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public List<User> getUserList() {
		return userService.findUserByPermission(WorkflowTask.ISSUING, WorkFlowType.LIFE);
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<PaymentDTO> getPaymentList() {
		RegNoSorter<PaymentDTO> sorter = new RegNoSorter<PaymentDTO>(paymentList);
		return sorter.getSortedList();
	}

	public PolicyReferenceType[] getReferenceTypes() {
		return new PolicyReferenceType[] { PolicyReferenceType.LIFE_POLICY, PolicyReferenceType.MEDICAL_POLICY, PolicyReferenceType.HEALTH_POLICY,
				PolicyReferenceType.CRITICAL_ILLNESS_POLICY, PolicyReferenceType.MICRO_HEALTH_POLICY, PolicyReferenceType.LIFE_BILL_COLLECTION,
				PolicyReferenceType.MEDICAL_BILL_COLLECTION, PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION, PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION,
				PolicyReferenceType.FARMER_POLICY, PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION,
				PolicyReferenceType.GROUP_MICRO_HEALTH, PolicyReferenceType.STUDENT_LIFE_POLICY, PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION,
				PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY, PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY, PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY};
	}

	public void search() {
		Set<String> receiptSet = new HashSet<String>();
		String[] receiptArray = receiptStream.split("\\r?\\n");
		for (String receiptNo : receiptArray) {
			Pattern pattern = Pattern.compile("(CASH|CHQ|TRF)(\\/)[0-9]{4}(\\/)[0-9]{10}", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(receiptNo);
			if (matcher.matches()) {
				receiptSet.add(receiptNo.toUpperCase());
			}
		}
		paymentList = paymentService.findPaymentByRecipNo(new ArrayList<String>(receiptSet), referenceType, false);
	}

	public void delete(PaymentDTO payment) {
		paymentList.remove(payment);
	}

	public String getGrandTotalAmount() {
		double totalAmount = 0.0;
		if (paymentList != null) {
			for (PaymentDTO payment : paymentList) {
				totalAmount = totalAmount + payment.getBillCollectionTotalAmount();
			}
		}
		return Utils.getCurrencyFormatString(totalAmount);
	}

	public void loadUserList() {
		WorkFlowType workflowType = null;
		switch (referenceType) {
			case LIFE_POLICY:
				workflowType = WorkFlowType.LIFE;
				break;
		}
		if (userList == null) {
			userList = userService.findUserByPermission(WorkflowTask.ISSUING, workflowType);
		}
	}

	public String payment() {
		String result = null;
		try {
			if (validation()) {
				paymentDelegateService.payment(paymentList, referenceType, responsiblePerson, remark, user.getBranch(), salePoint);
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
				result = "dashboard";
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String paymentSkipTLF() {
		String result = null;
		try {
			if (validation()) {
				if (skipTLF) {
					paymentDelegateService.paymentSkipTLF(paymentList, referenceType, responsiblePerson, remark, user.getBranch(), salePoint);
				} else {
					paymentDelegateService.payment(paymentList, referenceType, responsiblePerson, remark, user.getBranch(), salePoint);
				}
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
				result = "dashboard";
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void changeReferenceType() {
		isRequired = false;
		List<PolicyReferenceType> referenceList = Arrays.asList(PolicyReferenceType.LIFE_POLICY, PolicyReferenceType.MEDICAL_POLICY, PolicyReferenceType.HEALTH_POLICY,
				PolicyReferenceType.CRITICAL_ILLNESS_POLICY, PolicyReferenceType.MICRO_HEALTH_POLICY, PolicyReferenceType.FARMER_POLICY,
				PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, PolicyReferenceType.GROUP_MICRO_HEALTH, PolicyReferenceType.STUDENT_LIFE_POLICY, 
				PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY, PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY, PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY);
		if (referenceList.contains(referenceType)) {
			isRequired = true;
		}
		paymentList = new ArrayList<PaymentDTO>();
		receiptStream = "";
		PrimeFaces.current().resetInputs(":paymentForm");
	}

	private boolean validation() {
		boolean result = true;
		if (paymentList.size() < 1) {
			result = false;
			addErrorMessage("paymentForm:paymentDataTable", UIInput.REQUIRED_MESSAGE_ID);
		}

		// if (paymentMap != null && !paymentMap.isEmpty()) {
		// List<PaymentDTO> payments = new
		// ArrayList<PaymentDTO>(paymentMap.values());
		// List<PolicyReferenceType> referenceList =
		// Arrays.asList(PolicyReferenceType.LIFE_POLICY,
		// PolicyReferenceType.MEDICAL_POLICY,
		// PolicyReferenceType.HEALTH_POLICY,
		// PolicyReferenceType.CRITICAL_ILLNESS_POLICY,
		// PolicyReferenceType.MICRO_HEALTH_POLICY,
		// PolicyReferenceType.FARMER_POLICY,
		// PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY,
		// PolicyReferenceType.GROUP_MICRO_HEALTH,
		// PolicyReferenceType.STUDENT_LIFE_POLICY);
		// if (referenceList.contains(payments.get(0).getReferenceType())) {
		// result = false;
		// addErrorMessage("paymentForm:responsiblePerson",
		// UIInput.REQUIRED_MESSAGE_ID);
		// }
		// }
		return result;
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public void selectSalePoint() {
		selectSalePointByBranch(user.getBranch() == null ? "" : user.getBranch().getId());
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		this.setSalePoint(salePoint);
	}

	public boolean isRequired() {
		return isRequired;
	}

	public boolean isSkipTLF() {
		return skipTLF;
	}

	public void setSkipTLF(boolean skipTLF) {
		this.skipTLF = skipTLF;
	}

}
