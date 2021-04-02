package org.ace.insurance.web.manage.life.issue;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.common.ListSplitor;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.PolicyInsuredPersonHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifePolicyService;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;

@ViewScoped
@ManagedBean(name = "RenewalGroupLifeIssueActionBean")
public class RenewalGroupLifeIssueActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RenewalGroupLifePolicyService}")
	private IRenewalGroupLifePolicyService renewalGroupLifePolicyService;

	public void setRenewalGroupLifePolicyService(IRenewalGroupLifePolicyService renewalGroupLifePolicyService) {
		this.renewalGroupLifePolicyService = renewalGroupLifePolicyService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{RenewalGroupLifeProposalService}")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	public void setRenewalGroupLifeProposalService(IRenewalGroupLifeProposalService renewalGroupLifeProposalService) {
		this.renewalGroupLifeProposalService = renewalGroupLifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private Date todaydate = new Date();
	private LifePolicyHistory lifePolicyHistory;
	private List<LifePolicy> lifePolicyList;
	private Map<String, LifePolicy> lifePolicyMap;
	private LifePolicy lifePolicy;
	private PaymentDTO paymentDTO;
	private boolean nilExcess;
	private boolean showPreview;
	private boolean disableIssueBtn;
	private boolean disableSetUPBtn;
	private String AddDispline;
	private String Reasons;
	private String Changes;

	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
		lifeProposal = (LifeProposal) getParam("lifeProposal");
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		disableSetUPBtn = true;
		List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.LIFE_POLICY, true);
		paymentDTO = new PaymentDTO(paymentList);
		lifePolicyMap = new HashMap<String, LifePolicy>();
		lifePolicyList = renewalGroupLifePolicyService.findLifePolicyByProposalId(lifeProposal.getId());
		for (LifePolicy lp : lifePolicyList) {
			lifePolicyMap.put(lp.getId(), lp);
		}
		lifeProposal = renewalGroupLifeProposalService.findLifeProposalById(lifeProposal.getId());
		if (lifeProposal.getComplete()) {
			this.disableIssueBtn = true;
		}
		if (recalculatePremium(CONFIRMATION)) {
			renewalGroupLifeProposalService.calculatePremium(lifeProposal);
		}
		outjectLifeProposal(lifeProposal);
	}

	public void changeNillExcessValue(LifePolicy lifePolicy) {
		lifePolicy = lifePolicyMap.get(lifePolicy.getId());
		lifePolicy.setNilExcess(nilExcess);
	}

	public int getYear() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		return year;
	}

	public int getMonth() {
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MONTH);
		return month + 1;
	}

	public int getDay() {
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	public void issuePolicy() {
		try {
			renewalGroupLifeProposalService.issueLifeProposal(lifeProposal);
			lifeProposal = renewalGroupLifeProposalService.findLifeProposalById(lifeProposal.getId());
			this.showPreview = true;
			this.disableIssueBtn = true;
			outjectLifeProposal(lifeProposal);
			addInfoMessage(null, MessageId.ISSUING_PROCESS_SUCCESS_PARAM, lifeProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}

	}

	private final String reportName = "LifePolicyIssue";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private String fileName = "";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport(LifePolicy lifePolicy) {

		String customerName = lifeProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "GroupLife_" + customerName + "_Issue" + ".pdf";
		DocumentBuilder.generateGroupLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
	}

	public void increasePrintCount() {
		renewalGroupLifePolicyService.increaseLifePolicyPrintCount(lifePolicy.getId());
		lifePolicy = lifePolicyMap.get(lifePolicy.getId());
		lifePolicy.setPrintCount(lifePolicy.getPrintCount() + 1);
	}

	public Date getPresentDate() {
		return new Date();
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

	public boolean getShowPreview() {
		return showPreview;
	}

	public boolean getDisableIssueBtn() {
		return disableIssueBtn;
	}

	public LifePolicyHistory getLifePolicyHistory() {
		return lifePolicyHistory;
	}

	public void setLifePolicyHistory(LifePolicyHistory lifePolicyHistory) {
		this.lifePolicyHistory = lifePolicyHistory;
	}

	public Date getTodaydate() {
		return todaydate;
	}

	public void setTodaydate(Date todaydate) {
		this.todaydate = todaydate;
	}

	public String getAddDispline() {
		return AddDispline;
	}

	public void setAddDispline(String addDispline) {
		AddDispline = addDispline;
	}

	public String getReasons() {
		return Reasons;
	}

	public void setReasons(String reasons) {
		Reasons = reasons;
	}

	public String getChanges() {
		return Changes;
	}

	public void setChanges(String changes) {
		Changes = changes;
	}

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public boolean isChangeOthers() {
		if (lifeProposal.getLifePolicy() != null) {
			PolicyInsuredPersonHistory insuPersonHistory;
			if (lifeProposal.getLifePolicy().getInsuredPersonInfo().size() != lifePolicyHistory.getInsuredPersonInfo().size()) {
				return true;
			}
			if (lifeProposal.getLifePolicy().getPolicyStatus() == PolicyStatus.TERMINATE) {
				return true;
			}
			if (!lifeProposal.getLifePolicy().getPaymentType().getId().equalsIgnoreCase(lifePolicyHistory.getPaymentType().getId())) {
				return true;
			}
		}
		return false;
	}

	public boolean isLicenseNoNull() {
		if (lifeProposal.getAgent() != null) {
			return true;
		} else
			return false;
	}

	public List<List<PolicyInsuredPerson>> getPolicyInsuredList() {
		if (lifePolicy != null) {
			return ListSplitor.split(lifePolicy.getInsuredPersonInfo(), 8);
		}
		return null;
	}

	public boolean isDisableSetUPBtn() {
		return disableSetUPBtn;
	}

	public void setDisableSetUPBtn(boolean disableSetUPBtn) {
		this.disableSetUPBtn = disableSetUPBtn;
	}

	private void outjectLifeProposal(LifeProposal lifeProposal) {
		putParam("lifeProposal", lifeProposal);
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* For Template */
	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifePolicySummaryService}")
	private ILifePolicySummaryService lifePolicySummaryService;

	public void setLifePolicySummaryService(ILifePolicySummaryService lifePolicySummaryService) {
		this.lifePolicySummaryService = lifePolicySummaryService;
	}

	private List<LifePolicyHistory> lifePolicyHistoryList;

	public boolean isEndorse(LifeProposal lifeProposal) {
		if (lifeProposal == null) {
			return false;
		}
		return EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy());
	}

	public List<LifePolicyHistory> getLifePolicyHistoryList() {
		return lifePolicyHistoryList;
	}

	public LifePolicySummary getLifePolicySummary(String policyId) {
		LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(policyId);
		return summary;
	}
}
