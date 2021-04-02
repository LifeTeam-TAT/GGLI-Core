package org.ace.insurance.web.manage.life.surrender.inform;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.ServletContext;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.life.surrender.service.interfaces.ILifeSurrenderProposalService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
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
@ManagedBean(name = "LifeSurrenderInformActionBean")
public class LifeSurrenderInformActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean approvedProposal;
	private boolean printFlag;
	private boolean isEndowmentSurrender;
	private LifeSurrenderProposal surrenderProposal;
	private String remark;
	private User responsiblePerson;
	private User user;
	private ClaimAcceptedInfo claimAcceptedInfo;
	private List<WorkFlowHistory> workFlowList;
	private List<PaymentTrackDTO> paymentList;
	private final String reportName = "lifeSurrenderInform";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	@ManagedProperty(value = "#{LifeSurrenderProposalService}")
	private ILifeSurrenderProposalService surrderProposalService;

	public void setSurrderProposalService(ILifeSurrenderProposalService surrderProposalService) {
		this.surrderProposalService = surrderProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
		surrenderProposal = (LifeSurrenderProposal) getParam("surrenderProposal");
		workFlowList = workFlowService.findWorkFlowHistoryByRefNo(surrenderProposal.getId());
		paymentList = paymentService.findPaymentTrack(surrenderProposal.getPolicyNo());
		String productId = surrenderProposal.getClaimProduct().getId();
		isEndowmentSurrender = (KeyFactorChecker.getLifeSurrenderProductId().equals(productId)) ? true : false;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		claimAcceptedInfo = new ClaimAcceptedInfo();
		claimAcceptedInfo.setReferenceNo(surrenderProposal.getId());
		ReferenceType referenceType = isEndowmentSurrender ? ReferenceType.LIFESURRENDER_PROPOSAL : ReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL;
		claimAcceptedInfo.setReferenceType(referenceType);
		claimAcceptedInfo.setClaimAmount(surrenderProposal.getSurrenderAmount());
		claimAcceptedInfo.setDuePremium(surrenderProposal.getLifePremium());
		approvedProposal = surrenderProposal.isApproved();

	}

	@PreDestroy
	public void destory() {
		removeParam("surrenderProposal");
	}

	public void informApproveLifeSurrender() {
		try {
			WorkflowReferenceType referenceType = isEndowmentSurrender ? WorkflowReferenceType.LIFESURRENDER_PROPOSAL : WorkflowReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(surrenderProposal.getId(), remark, WorkflowTask.CONFIRMATION, referenceType, user, responsiblePerson);
			surrderProposalService.informLifeSurrender(surrenderProposal, workFlowDTO, claimAcceptedInfo);
			addInfoMessage(null, MessageId.INFORM_PROCESS_SUCCESS_PARAM, surrenderProposal.getProposalNo());
			printFlag = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}

	}

	public void selectUser() {
		selectUser(WorkflowTask.INFORM, WorkFlowType.LIFESURRENDER);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public String getStream() {
		return pdfDirPath + fileName;

	}

	public void generateReport() {
		if (approvedProposal) {
			DocumentBuilder.generateLifeSurrenderInformForm(surrenderProposal, claimAcceptedInfo, dirPath, fileName);
		} else {
			DocumentBuilder.generateLifeSurrenderRejectLetter(surrenderProposal, claimAcceptedInfo, dirPath, fileName);
		}

	}

	public void handleClose(CloseEvent event) {
		try {
			FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getSystemPath() {
		Object context = getFacesContext().getExternalContext().getContext();
		String systemPath = ((ServletContext) context).getRealPath("/");
		return systemPath;
	}

	public LifeSurrenderProposal getSurrenderProposal() {
		return surrenderProposal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public boolean isPrintFlag() {
		return printFlag;
	}

	public boolean isEndowmentSurrender() {
		return isEndowmentSurrender;
	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public ClaimAcceptedInfo getClaimAcceptedInfo() {
		return claimAcceptedInfo;
	}

	public List<PaymentTrackDTO> getPaymentList() {
		return paymentList;
	}
}
