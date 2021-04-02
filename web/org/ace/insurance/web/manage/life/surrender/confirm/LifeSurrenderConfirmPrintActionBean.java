package org.ace.insurance.web.manage.life.surrender.confirm;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.claimaccept.service.interfaces.IClaimAcceptedInfoService;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.service.interfaces.ILifeSurrenderProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifeSurrenderConfirmPrintActionBean")
public class LifeSurrenderConfirmPrintActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Bank bank;
	private PaymentDTO payment;
	private boolean actualPayment;
	private LifeSurrenderProposal surrenderProposal;
	private List<Payment> paymentList;
	private User user;
	private WorkFlowDTO workFlowDTO;
	private boolean isEndowmentSurrender;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{LifeSurrenderProposalService}")
	private ILifeSurrenderProposalService surrenderProposalService;

	public void setSurrenderProposalService(ILifeSurrenderProposalService surrenderProposalService) {
		this.surrenderProposalService = surrenderProposalService;
	}

	@ManagedProperty(value = "#{ClaimAcceptedInfoService}")
	private IClaimAcceptedInfoService claimAcceptedInfoService;

	public void setClaimAcceptedInfoService(IClaimAcceptedInfoService claimAcceptedInfoService) {
		this.claimAcceptedInfoService = claimAcceptedInfoService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		isEndowmentSurrender = surrenderProposal.getClaimProduct().getId().equals(KeyFactorChecker.getLifeSurrenderProductId());
		ReferenceType referenceType = isEndowmentSurrender ? ReferenceType.LIFESURRENDER_PROPOSAL : ReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL;
		ClaimAcceptedInfo claInfo = claimAcceptedInfoService.findClaimAcceptedInfoByReferenceNo(surrenderProposal.getId(), referenceType);
		payment = new PaymentDTO();
		payment.setDiscountPercent(0.0);
		payment.setServicesCharges(claInfo.getServicesCharges());
		payment.setStampFees(0.0);
	}

	@PreDestroy
	public void destroy() {
		removeParam("surrenderProposal");
		removeParam("workFlowDTO");
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		surrenderProposal = (LifeSurrenderProposal) getParam("surrenderProposal");
		workFlowDTO = (WorkFlowDTO) getParam("workFlowDTO");
	}

	public void addSurrenderReceiptInfo() {
		try {
			paymentList = surrenderProposalService.confirmLifeSurrenderProposal(surrenderProposal, workFlowDTO, payment, user.getBranch());
			payment = new PaymentDTO(paymentList);
			actualPayment = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, surrenderProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public PaymentChannel[] getChannelValues() {
		return new PaymentChannel[] { PaymentChannel.CASHED, PaymentChannel.CHEQUE };
	}

	public void returnAccountBank(SelectEvent event) {
		Bank accountBank = (Bank) event.getObject();
		payment.setAccountBank(accountBank);
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

	private final String reportName = "lifeSurrenderClaimCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		DocumentBuilder.generateLifeSurrenderCashReceipt(surrenderProposal, payment, dirPath, fileName);
	}

	public Bank getBank() {
		return bank;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public LifeSurrenderProposal getSurrenderProposal() {
		return surrenderProposal;
	}

	public boolean isActualPayment() {
		return actualPayment;
	}

	public boolean isEndowmentSurrender() {
		return isEndowmentSurrender;
	}

}
