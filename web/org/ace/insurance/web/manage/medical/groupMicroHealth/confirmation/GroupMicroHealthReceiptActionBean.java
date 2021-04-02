package org.ace.insurance.web.manage.medical.groupMicroHealth.confirmation;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.payment.Payment;
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
@ManagedBean(name = "GroupMicroHealthReceiptActionBean")
public class GroupMicroHealthReceiptActionBean extends BaseBean {
	
	@ManagedProperty(value = "#{GroupMicroHealthService}")
	private IGroupMicroHealthService groupMicroHealthService;
	


	public void setGroupMicroHealthService(IGroupMicroHealthService groupMicroHealthService) {
		this.groupMicroHealthService = groupMicroHealthService;
	}

	private PaymentDTO payment;
	private GroupMicroHealth groupMicroHealth;
	private User user;
	private WorkFlowDTO workFlowDTO;
	
	private final String reportName = "MedicalCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	private boolean actualPayment;

	@PostConstruct
	public void init() {
		initializeInjection();
		payment = new PaymentDTO();
	}
	
	@PreDestroy
	public void destroy() {
		removeParam("groupMicroHealth");
		removeParam("workFlowDTO");
	}

	public void initializeInjection() {
		user = (user == null)? (User) getParam(Constants.LOGIN_USER): user;
		groupMicroHealth = (groupMicroHealth == null) ? (GroupMicroHealth) getParam("groupMicroHealth"):groupMicroHealth;
		workFlowDTO = (workFlowDTO == null)?(WorkFlowDTO) getParam("workFlowDTO"):workFlowDTO;
	}
	
	public String addNewGroupMicroHealthReceiptInfo() {
		String result = null;
		try {
			payment.setReferenceType(PolicyReferenceType.GROUP_MICRO_HEALTH);
			List<Payment> paymentList = null; 
			paymentList = groupMicroHealthService.confirmGroupMicroHealth(groupMicroHealth, workFlowDTO, payment,user.getBranch());
			payment = new PaymentDTO(paymentList);
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, groupMicroHealth.getId());
			actualPayment = true;
//			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		
		return result;
	}
	
	public String getReportStream() {
		return pdfDirPath + fileName;
	}
	public void generateReport() {
		MedicalUnderwritingDocFactory.generateGroupMicroHealthCashReceipt(groupMicroHealth, payment, dirPath, fileName);

	}
	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		payment.setBank(bank);
	}
	public void changePaymentChannel(AjaxBehaviorEvent event) {
		payment.setAccountBank(null);
		payment.setBank(null);
		payment.setChequeNo(null);
		payment.setPoNo(null);
	}

	public void returnAccountBank(SelectEvent event) {
		Bank accountBank = (Bank) event.getObject();
		payment.setAccountBank(accountBank);
	}

	

	public PaymentDTO getPayment() {
		return payment;
	}

	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public WorkFlowDTO getWorkFlowDTO() {
		return workFlowDTO;
	}

	public void setWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		this.workFlowDTO = workFlowDTO;
	}

	public GroupMicroHealth getGroupMicroHealth() {
		return groupMicroHealth;
	}

	public void setGroupMicroHealth(GroupMicroHealth groupMicroHealth) {
		this.groupMicroHealth = groupMicroHealth;
	}

	public boolean isActualPayment() {
		return actualPayment;
	}

	public void setActualPayment(boolean actualPayment) {
		this.actualPayment = actualPayment;
	}
	

}
