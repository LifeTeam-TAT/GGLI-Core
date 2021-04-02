package org.ace.insurance.web.manage.enquires.life;

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

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.life.proposal.InsuredPersonAddon;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.CloseEvent;

@ViewScoped
@ManagedBean(name = "LProposalInformEnquiryActionBean")
public class LProposalInformEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

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

	@ManagedProperty(value = "#{AcceptedInfoService}")
	private IAcceptedInfoService acceptedInfoService;

	public void setAcceptedInfoService(IAcceptedInfoService acceptedInfoService) {
		this.acceptedInfoService = acceptedInfoService;
	}

	private LifeProposal lifeProposal;
	private boolean approvedProposal = true;
	private boolean disablePrintBtn = true;
	private AcceptedInfo acceptedInfo;
	private Map<String, AddOn> addOnMap;

	public Date getCurrentDate() {
		return new Date();
	}

	@PostConstruct
	public void init() {
		lifeProposal = (LifeProposal) getParam("lifeProposal");
		addOnMap = new HashMap<String, AddOn>();
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			for (InsuredPersonAddon insuredPersonAddon : pv.getInsuredPersonAddOnList()) {
				AddOn addOn = insuredPersonAddon.getAddOn();
				addOnMap.put(addOn.getId(), addOn);
			}
		}
		String productId = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getId();
		if (lifeProposal.getProposalInsuredPersonList().size() >= 1 && productId.equals(getGroupLifeId())) {
			int approvedCount = 0;
			for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
				if (pv.isApproved()) {
					approvedCount++;
				}
			}
			if (approvedCount < 5) {
				approvedProposal = false;
			}
		} else {
			approvedProposal = lifeProposal.getProposalInsuredPersonList().get(0).isApproved();
		}

		if (approvedProposal) {
			acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposal.getId(), ReferenceType.LIFE_PROPOSAL);
		} else {
			acceptedInfo = new AcceptedInfo();
			acceptedInfo.setInformDate(new Date());
		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
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
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
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

	public String getPublicLifeId() {
		return ProductIDConfig.getPublicLifeId();
	}

	public String getGroupLifeId() {
		return ProductIDConfig.getGroupLifeId();
	}

	public boolean checkPublicLife() {
		Boolean isPublicLife = true;
		String productId = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getId();
		if (productId.equals(getPublicLifeId())) {
			isPublicLife = true;
		} else {
			isPublicLife = false;
		}
		return isPublicLife;
	}

	private final String reportName = "LifeAcceptedLetterEnquiry";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		if (approvedProposal) {
			DocumentBuilder.generateLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
		} else {
			DocumentBuilder.generateLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
		}
	}

	public void handleCloseLifeAcceptedLetter(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
