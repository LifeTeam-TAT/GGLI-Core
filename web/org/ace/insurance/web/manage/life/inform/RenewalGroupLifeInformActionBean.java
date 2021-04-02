package org.ace.insurance.web.manage.life.inform;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.InsuredPersonAddon;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "RenewalGroupLifeInformActionBean")
public class RenewalGroupLifeInformActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

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

	private User user;
	private LifeProposal lifeProposal;
	private WorkFlowDTO workFlowDTO;

	private LifePolicySummary policySummary;
	private boolean approvedProposal = true;
	private boolean disablePrintBtn = true;
	private String remark;
	private User responsiblePerson;
	private AcceptedInfo acceptedInfo;
	private Map<String, AddOn> addOnMap;
	// for reject letter
	private final String reportName = "lifeInformRejectLetter";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private String fileName;
	// for acceptance letter
	private final String acceptanceReportName = "lifeInformAcceptanceLetter";
	private final String acceptancePdfDirPath = "/pdf-report/" + acceptanceReportName + "/" + System.currentTimeMillis() + "/";
	private final String acceptanceDirPath = getSystemPath() + acceptancePdfDirPath;
	private String acceptanceDirFileName;
	private List<WorkFlowHistory> workflowHistoryList;

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void selectUser() {
		selectUser(WorkflowTask.RENEWAL_CONFIRMATION, WorkFlowType.LIFE);
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
		workflowHistoryList = workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
		removeParam("workFlowDTO");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		addOnMap = new HashMap<String, AddOn>();
		if (!EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy())) {
			if (recalculatePremium(CONFIRMATION)) {
				renewalGroupLifeProposalService.calculatePremium(lifeProposal);
			}
		}
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			for (InsuredPersonAddon insuredPersonAddon : pv.getInsuredPersonAddOnList()) {
				AddOn addOn = insuredPersonAddon.getAddOn();
				addOnMap.put(addOn.getId(), addOn);
			}
		}
		acceptedInfo = new AcceptedInfo();
		acceptedInfo.setReferenceNo(lifeProposal.getId());
		acceptedInfo.setReferenceType(ReferenceType.LIFE_PROPOSAL);
		acceptedInfo.setBasicPremium(lifeProposal.getApprovedPremium());
		acceptedInfo.setAddOnPremium(lifeProposal.getApprovedAddOnPremium());
		acceptedInfo.setEndorsementNetPremium(lifeProposal.getEndorsementNetPremium());
		acceptedInfo.setPaymentType(lifeProposal.getPaymentType());

		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			if (pv.isApproved()) {
				approvedProposal = true;
			} else {
				approvedProposal = false;
				break;
			}
		}
	}

	public List<AddOn> getAddOnList() {
		return new ArrayList<AddOn>(addOnMap.values());
	}

	public AcceptedInfo getAcceptedInfo() {
		return acceptedInfo;
	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public boolean isApproveProposal() {
		return approvedProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workflowHistoryList;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public boolean isDisablePrintBtn() {
		return disablePrintBtn;
	}

	public void informApprovedLifeProposal() {
		try {
			String formID = "lifeProposalInformForm";
			if (acceptedInfo.getServicesCharges() < 0) {
				addErrorMessage(formID + ":additionalCharges", UIInput.REQUIRED_MESSAGE_ID);
				return;
			}
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
				return;
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.RENEWAL_CONFIRMATION, WorkflowReferenceType.LIFE_PROPOSAL, user,
					responsiblePerson);
			renewalGroupLifeProposalService.informProposal(lifeProposal, workFlowDTO, acceptedInfo, RequestStatus.APPROVED.name());
			outjectLifeProposal(lifeProposal);
			outjectWorkFlowDTO(workFlowDTO);
			addInfoMessage(null, MessageId.INFORM_PROCESS_SUCCESS_PARAM, lifeProposal.getProposalNo());

			disablePrintBtn = false;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void informRejectLifeProposal() {
		try {
			String formID = "lifeProposalInformForm";
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.RENEWAL_CONFIRMATION, WorkflowReferenceType.LIFE_PROPOSAL, user,
					responsiblePerson);
			renewalGroupLifeProposalService.informProposal(lifeProposal, workFlowDTO, null, RequestStatus.REJECTED.name());
			outjectLifeProposal(lifeProposal);
			outjectWorkFlowDTO(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.INFORM_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			disablePrintBtn = false;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public double calculateTotalAmount() {
		double totalAmount = 0.0;
		int coverTime = 0;
		if (lifeProposal.getLifePolicy() != null) {
			if (policySummary != null) {
				coverTime = policySummary.getCoverTime();
			}
			if (coverTime == 0) {
				if (new Date().before(lifeProposal.getLifePolicy().getActivedPolicyEndDate())) {
					for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList())
						return totalAmount += proposalInsuredPerson.getEndorsementNetBasicPremium();
				} else {
					totalAmount = acceptedInfo.getNetTermAmount();
					for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
						totalAmount += proposalInsuredPerson.getEndorsementNetBasicPremium();
					}
				}
			} else {
				totalAmount = 0.00;
			}
		}
		return totalAmount;
	}

	public LifePolicySummary getPolicySummary() {
		return policySummary;
	}

	public void setPolicySummary(LifePolicySummary policySummary) {
		this.policySummary = policySummary;
	}

	public Date getCurrentDate() {
		return new Date();
	}

	public String getStream() {
		if (approvedProposal) {
			return acceptancePdfDirPath + acceptanceDirFileName;
		} else {
			return pdfDirPath + fileName;
		}
	}

	public void generateReport() {
		String customerName = lifeProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "GroupLife_" + customerName + "_Inform" + ".pdf";
		acceptanceDirFileName = "GroupLife_" + customerName + "_Inform" + ".pdf";
		if (approvedProposal) {
			DocumentBuilder.generateLifeAcceptanceLetter(lifeProposal, acceptedInfo, acceptanceDirPath, acceptanceDirFileName);
		} else {
			DocumentBuilder.generateLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
		}
	}

	private void outjectLifeProposal(LifeProposal lifeProposal) {
		putParam("lifeProposal", lifeProposal);
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleClose(CloseEvent event) {
		try {
			if (approvedProposal) {
				FileHandler.forceDelete(acceptanceDirPath);
			} else {
				FileHandler.forceDelete(dirPath);
			}
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

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	@ManagedProperty(value = "#{LifePolicySummaryService}")
	private ILifePolicySummaryService lifePolicySummaryService;

	public void setLifePolicySummaryService(ILifePolicySummaryService lifePolicySummaryService) {
		this.lifePolicySummaryService = lifePolicySummaryService;
	}

	private List<LifePolicy> lifePolicyList;
	private List<LifePolicyHistory> lifePolicyHistoryList;

	public List<LifePolicy> getLifePolicyList() {
		return lifePolicyList;
	}

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
