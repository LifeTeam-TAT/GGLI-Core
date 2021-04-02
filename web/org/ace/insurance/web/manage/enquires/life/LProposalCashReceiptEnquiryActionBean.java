package org.ace.insurance.web.manage.enquires.life;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "LProposalCashReceiptEnquiryActionBean")
public class LProposalCashReceiptEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
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
	private PaymentDTO payment;
	private String presentDate;

	@PostConstruct
	public void init() {
		List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.LIFE_POLICY, null);
		payment = new PaymentDTO(paymentList);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		presentDate = format.format(new Date());
	}

	public String getPresentDate() {
		return presentDate;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public String getPublicLifeId() {
		return ProductIDConfig.getPublicLifeId();
	}

	private final String reportName = "LifeCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		DocumentBuilder.generateLifeCashReceipt(lifeProposal, payment, dirPath, fileName);
	}
}
