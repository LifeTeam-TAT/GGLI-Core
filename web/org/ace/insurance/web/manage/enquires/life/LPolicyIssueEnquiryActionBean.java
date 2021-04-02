package org.ace.insurance.web.manage.enquires.life;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.ListSplitor;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.life.policy.EndorsementLifePolicyPrint;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.CloseEvent;

@ViewScoped
@ManagedBean(name = "LPolicyIssueEnquiryActionBean")
public class LPolicyIssueEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private LifeProposal lifeProposal;
	private List<LifePolicy> lifePolicyList;
	private LifePolicy lifePolicy;
	private PaymentDTO paymentDTO;
	private boolean nilExcess;
	private boolean isEndorsement;

	@PostConstruct
	public void init() {
		lifeProposal = (LifeProposal) getParam("lifeProposal");
		lifePolicyList = new ArrayList<LifePolicy>();
		List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.LIFE_POLICY, true);
		paymentDTO = new PaymentDTO(paymentList);
		lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposal.getId());
		lifePolicyList.add(lifePolicy);
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposal.getId());
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	public void preparePolicyIssue(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public void changeNillExcessValue(LifePolicy lifePolicy) {
		lifePolicy.setNilExcess(nilExcess);
	}

	public void increasePrintCount() {
		lifePolicyService.increaseLifePolicyPrintCount(lifePolicy.getId());
		lifePolicy.setPrintCount(lifePolicy.getPrintCount() + 1);
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public List<LifePolicy> getLifePolicyList() {
		return lifePolicyList;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public boolean getNilExcess() {
		return nilExcess;
	}

	public void setNilExcess(boolean nilExcess) {
		this.nilExcess = nilExcess;
	}

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public List<List<PolicyInsuredPerson>> getPolicyInsuredList() {
		if (lifePolicy != null) {
			return ListSplitor.split(lifePolicy.getInsuredPersonInfo(), 8);
		}
		return null;
	}

	private final String reportName = "LifePolicyIssue";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void handleClosePolicyIssue(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateReport() {
		if (!ProposalType.ENDORSEMENT.equals(lifePolicy.getLifeProposal().getProposalType())) {
			if (lifePolicy != null && isSinglePrint()) {
				DocumentBuilder.generatePublicLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
			} else if (lifePolicy != null && isGroupPrint()) {
				DocumentBuilder.generateGroupLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
			}
		} else {
			List<EndorsementLifePolicyPrint> endorsementPolicyPrintList = lifePolicyService.findEndorsementPolicyPrintByLifePolicyNo(lifePolicy.getPolicyNo());
			System.out.println("================================"+endorsementPolicyPrintList.size());
			
			List<LifePolicyHistory> lifePolicyHistorylist = lifePolicyHistoryService.findLifePolicyHistoryByPolicyNo(lifePolicy.getPolicyNo());
			DocumentBuilder.generatePrintSetUpLifePolicy(endorsementPolicyPrintList.get(0), lifeProposal, lifePolicy, lifePolicyHistorylist.get(0), dirPath, fileName);
		}
	}

	public String outjectLifeProposal() {
		if (ProposalType.ENDORSEMENT.equals(lifePolicy.getLifeProposal().getProposalType()))
			return "lifeProposalEndorsementEnquiry";
		else if (ProposalType.RENEWAL.equals(lifePolicy.getLifeProposal().getProposalType()))
			return "renewalLifeProposalEnquiry";
		else
			return "lifeProposalEnquiry";

	}

	public boolean isSinglePrint() {
		if (lifeProposal.getProposalInsuredPersonList().size() == 1 && isEndorsement == false) {
			return true;
		}
		return false;
	}

	public boolean isGroupPrint() {
		if (lifeProposal.getProposalInsuredPersonList().size() > 1 && isEndorsement == false) {
			return true;
		}
		return false;
	}

	public boolean isEndorsementSingle() {
		if (isEndorsement == true && lifeProposal.getLifePolicy().getInsuredPersonInfo().size() == 1) {
			return true;
		}
		return false;
	}

	public boolean isEndorsementMultiple() {
		if (isEndorsement == true && lifeProposal.getLifePolicy().getInsuredPersonInfo().size() > 1) {
			return true;
		}
		return false;
	}
}
