package org.ace.insurance.web.manage.life.billcollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.common.GenericDataModel;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.payment.AgentPaymentCashReceiptDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentDelegateService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;

@ViewScoped
@ManagedBean(name = "RegenerateBillCollectionActionBean")
public class RegenerateBillCollectionActionBean<T extends IDataModel> extends BaseBean {

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

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	private GenericDataModel<Payment> paymentDataModel;
	private T[] selectedPaymentList;
	List<Payment> paymentList;
	private User user;
	private PolicyCriteria policyCriteria;

	private ReferenceType paymentReferenceType;
	private String policyNo;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		policyCriteria = new PolicyCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		policyCriteria.setFromDate(cal.getTime());
		Date endDate = new Date();
		policyCriteria.setToDate(endDate);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void search() {
		try {
			paymentList = paymentService.findBCPaymentByPolicyNo(policyNo, paymentReferenceType);

			paymentDataModel = new GenericDataModel(paymentList);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	private final String reportName = "LifeBillCollection";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	@SuppressWarnings("unchecked")
	public void generateCashReceipt() {
		List<Payment> selectedList = (List<Payment>) Arrays.asList(selectedPaymentList);
		if (selectedList != null && !selectedList.isEmpty()) {
			AgentPaymentCashReceiptDTO agentPaymentDto = null;
			boolean hasAgent;
			List<BillCollectionCashReceiptDTO> dtoList = new ArrayList<BillCollectionCashReceiptDTO>();
			for (Payment payment : selectedList) {

				BillCollectionCashReceiptDTO dto = new BillCollectionCashReceiptDTO();
				if (PolicyReferenceType.MEDICAL_BILL_COLLECTION.equals(payment.getReferenceType())
						|| PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION.equals(payment.getReferenceType())
						|| PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION.equals(payment.getReferenceType())) {
					MedicalPolicy policy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					dto.setMedicalPolicy(policy);
					dto.setReferenceType(ReferenceType.MEDICAL_BILL_COLLECTION);
					hasAgent = policy.getAgent() != null ? true : false;
					if (hasAgent) {
						agentPaymentDto = paymentService.getAgentPaymentCashReceipt(policy, InsuranceType.MEDICAL, user.getBranch(), payment.getDiscountPercent());
					}
				} else {
					LifePolicy policy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					dto.setLifePolicy(policy);
					dto.setReferenceType(ReferenceType.LIFE_BILL_COLLECTION);
					hasAgent = policy.getAgent() != null ? true : false;
					if (hasAgent) {
						agentPaymentDto = paymentService.getAgentPaymentCashReceipt(policy, InsuranceType.LIFE, user.getBranch(), payment.getDiscountPercent());
					}
				}

				dto.setAgentComission(agentPaymentDto);
				dto.setPayment(payment);

				dtoList.add(dto);
			}

			DocumentBuilder.generateLifePaymentBillCashReceipt(dtoList, dirPath, fileName, true);
			PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
		} else {
			addErrorMessage("billCollectionForm:receiptTable", UIInput.REQUIRED_MESSAGE_ID);
		}
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public T[] getSelectedPaymentList() {
		return selectedPaymentList;
	}

	public void setSelectedPaymentList(T[] selectedPaymentList) {
		this.selectedPaymentList = selectedPaymentList;
	}

	public GenericDataModel<Payment> getPaymentDataModel() {
		return paymentDataModel;
	}

	public void setPaymentDataModel(GenericDataModel<Payment> paymentDataModel) {
		this.paymentDataModel = paymentDataModel;
	}

	public PolicyCriteria getPolicyCriteria() {
		return policyCriteria;
	}

	public void setPolicyCriteria(PolicyCriteria policyCriteria) {
		this.policyCriteria = policyCriteria;
	}

	public ReferenceType[] getReferenceTypeSelectedItemList() {
		return new ReferenceType[] { ReferenceType.LIFE_BILL_COLLECTION, ReferenceType.HEALTH_POLICY_BILL_COLLECTION, ReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION,
				ReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION, ReferenceType.STUDENT_LIFE_BILL_COLLECTION };
	}

	public ReferenceType getPaymentReferenceType() {
		return paymentReferenceType;
	}

	public void setPaymentReferenceType(ReferenceType paymentReferenceType) {
		this.paymentReferenceType = paymentReferenceType;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

}
