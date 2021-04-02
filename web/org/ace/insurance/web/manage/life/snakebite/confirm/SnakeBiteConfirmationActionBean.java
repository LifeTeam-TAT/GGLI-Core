package org.ace.insurance.web.manage.life.snakebite.confirm;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDataModel;
import org.ace.insurance.life.snakebite.service.interfaces.ISnakeBitePolicyService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.UserType;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "SnakeBiteConfirmationActionBean")
public class SnakeBiteConfirmationActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SnakeBitePolicyService}")
	private ISnakeBitePolicyService snakeBitePolicyService;

	public void setSnakeBitePolicyService(ISnakeBitePolicyService snakeBitePolicyService) {
		this.snakeBitePolicyService = snakeBitePolicyService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private User user;

	private User responsiblePerson;
	private String userType;
	private SnakeBitePolicy snakeBitePolicy;
	private List<SnakeBitePolicy> snakeBitePolicyList;
	private SnakeBitePolicyCriteria snakeBitePolicyCriteria;
	private Agent agent;
	private SaleMan saleMan;
	private Customer referral;
	private List<String> bookNoList;
	private String bookNo;
	private PaymentDTO paymentDTO;
	private Payment payment;
	private boolean disablePrintBtn = true;
	List<SnakeBitePolicy> snakeBitePolicySelectList = new ArrayList<SnakeBitePolicy>();

	private final String reportName = "SnakeBiteConfirmationCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentDTO = new PaymentDTO();
		payment = new Payment();
		snakeBitePolicy = new SnakeBitePolicy();
	}

	public String reset1() {
		return "SnakeBiteConfirmation";
	}

	public boolean isDisablePrintBtn() {
		return disablePrintBtn;
	}

	public Agent getAgent() {
		return agent;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public Customer getReferral() {
		return referral;
	}

	public void selectUser() {
		selectUser(WorkflowTask.PAYMENT, WorkFlowType.SNAKE_BITE_POLICY);
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void filter() {
		String agentId = null;
		String saleManId = null;
		String referralId = null;
		snakeBitePolicyCriteria = new SnakeBitePolicyCriteria();
		snakeBitePolicyCriteria.setAgent(null);
		snakeBitePolicyCriteria.setSaleMan(null);
		snakeBitePolicyCriteria.setRefferal(null);

		if (userType.equals(UserType.AGENT.toString())) {
			agentId = (agent == null ? null : agent.getId());
			snakeBitePolicyCriteria.setAgent(agentId);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			saleManId = (saleMan == null ? null : saleMan.getId());
			snakeBitePolicyCriteria.setSaleMan(saleManId);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			referralId = (referral == null ? null : referral.getId());
			snakeBitePolicyCriteria.setRefferal(referralId);
		}
		snakeBitePolicyCriteria.setBookNo(bookNo);
		snakeBitePolicyList = snakeBitePolicyService.findSnakeBitePolicyByCriteria(snakeBitePolicyCriteria);
		snakeBitePolicyDataModel = new SnakeBitePolicyDataModel(snakeBitePolicyList);

	}

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		payment.setAccountBank(null);
		payment.setBank(null);
		payment.setChequeNo(null);
		payment.setPoNo(null);
	}

	public void confirmSnakeBitePolicy() {
		try {
			if (!validPayment()) {
				return;
			}
			for (SnakeBitePolicy snakeBite : snakeBitePolicys) {
				snakeBitePolicySelectList.add(snakeBite);
			}
			// if (isCheque) {
			// paymentDTO.setPaymentChannel(PaymentChannel.CHEQUE);
			//
			// } else {
			// paymentDTO.setPaymentChannel(PaymentChannel.CASHED);
			// }
			paymentDTO.setReferenceNo(snakeBitePolicySelectList.get(0).getBookNo());
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(null, null, WorkflowTask.PAYMENT, WorkflowReferenceType.SNAKEBITEPOLICY, user, responsiblePerson);
			snakeBitePolicyService.confirmSnakeBitePolicy(snakeBitePolicySelectList, workFlowDTO, paymentDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.CONFIRMATION_PROCESS_SUCCESS);
			disablePrintBtn = false;
		} catch (SystemException ex) {
			handelSysException(ex);
		}

	}

	// for mutil check
	private SnakeBitePolicyDataModel snakeBitePolicyDataModel;
	private SnakeBitePolicy[] snakeBitePolicys;

	public SnakeBitePolicyDataModel getSnakeBitePolicyDataModel() {
		return snakeBitePolicyDataModel;
	}

	public void setSnakeBitePolicyDataModel(SnakeBitePolicyDataModel snakeBitePolicyDataModel) {
		this.snakeBitePolicyDataModel = snakeBitePolicyDataModel;
	}

	public void setSelectedSnakeBitePolicy(SnakeBitePolicy[] snakeBitePolicys) {
		this.snakeBitePolicys = snakeBitePolicys;
	}

	public SnakeBitePolicy[] getSelectedSnakeBitePolicy() {
		return null;
	}

	public SnakeBitePolicy getSnakeBitePolicy() {
		return snakeBitePolicy;
	}

	public void setSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy) {
		this.snakeBitePolicy = snakeBitePolicy;
	}

	public List<String> getBookNoList() {
		return bookNoList;
	}

	public void setBookNoList(List<String> bookNoList) {
		this.bookNoList = bookNoList;
	}

	public String getBookNo() {
		return bookNo;
	}

	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}

	public List<SnakeBitePolicy> getSnakeBitePolicyList() {
		return snakeBitePolicyList;
	}

	public void setSnakeBitePolicyList(List<SnakeBitePolicy> snakeBitePolicyList) {
		this.snakeBitePolicyList = snakeBitePolicyList;
	}

	public List<SnakeBitePolicy> getSnakeBitePolicySelectList() {
		return snakeBitePolicySelectList;
	}

	public void setSnakeBitePolicySelectList(List<SnakeBitePolicy> snakeBitePolicySelectList) {
		this.snakeBitePolicySelectList = snakeBitePolicySelectList;
	}

	public SnakeBitePolicyCriteria getSnakeBitePolicyCriteria() {
		return snakeBitePolicyCriteria;
	}

	public void setSnakeBitePolicyCriteria(SnakeBitePolicyCriteria snakeBitePolicyCriteria) {
		this.snakeBitePolicyCriteria = snakeBitePolicyCriteria;
	}

	public PaymentDTO getPaymentDTO() {
		return paymentDTO;
	}

	public void setPaymentDTO(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

	public String getUserType() {
		if (userType == null) {
			userType = UserType.AGENT.toString();
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			snakeBitePolicy.setSaleMan(null);
			snakeBitePolicy.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			snakeBitePolicy.setAgent(null);
			snakeBitePolicy.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			snakeBitePolicy.setSaleMan(null);
			snakeBitePolicy.setAgent(null);
		}
	}

	public boolean validPayment() {
		boolean valid = true;
		String formID = "sankeBiteConfirmationForm";
		if (responsiblePerson == null) {
			addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		return valid;
	}

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		payment = paymentService.findPaymentByReceiptNo(snakeBitePolicySelectList.get(0).getReferenceNo()).get(0);
		DocumentBuilder.generateSnakeBiteCashReceipt(snakeBitePolicySelectList, payment, dirPath, fileName);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		this.agent = agent;
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		this.saleMan = saleMan;
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		this.referral = referral;
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		paymentDTO.setBank(bank);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}
	
	public void returnAccountBank(SelectEvent event) {
		Bank accountBank = (Bank) event.getObject();
		paymentDTO.setAccountBank(accountBank);
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
