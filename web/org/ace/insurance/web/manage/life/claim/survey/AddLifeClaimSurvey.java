package org.ace.insurance.web.manage.life.claim.survey;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeClaimProposalAttachment;
import org.ace.insurance.life.claim.LifeClaimSurvey;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifeHospitalizedClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.user.User;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "AddLifeClaimSurvey")
public class AddLifeClaimSurvey extends BaseBean implements Serializable {

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private static final long serialVersionUID = 1L;
	private User user;
	private LifeClaimSurvey lifeClaimSurvey;
	private LifeClaimProposal lifeClaimProposal;
	private boolean death;
	private boolean disbility;
	private boolean hospital;
	private List<LifeDeathClaim> lifeDeathClaims;
	private List<LifeHospitalizedClaim> lifeHospitalizedClaims;
	private List<DisabilityLifeClaim> disabilityLifeClaims;
	private User responsiblePerson;

	private Map<String, String> proposalUploadedFileMap;
	private String temporyDir;
	private String lifeProposalId;
	private final String PROPOSAL_DIR = "/upload/life-claim-proposal/";

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeClaimSurvey = (lifeClaimSurvey == null) ? (LifeClaimSurvey) getParam("lifeClaimSurvey") : lifeClaimSurvey;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		lifeClaimProposal = lifeClaimSurvey.getLifeClaimProposal();
		proposalUploadedFileMap = new HashMap<String, String>();
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		this.lifeProposalId = lifeClaimProposal.getId();
		for (LifeClaimProposalAttachment att : lifeClaimProposal.getAttachmentList()) {
			proposalUploadedFileMap.put(att.getName(), att.getFilePath());
		}
		loadClaimList();
	}

	private void loadClaimList() {
		lifeDeathClaims = new ArrayList<LifeDeathClaim>();
		lifeHospitalizedClaims = new ArrayList<LifeHospitalizedClaim>();
		disabilityLifeClaims = new ArrayList<DisabilityLifeClaim>();
		for (LifePolicyClaim claim : lifeClaimProposal.getClaimList()) {
			if (claim instanceof LifeDeathClaim) {
				LifeDeathClaim deathClaim = (LifeDeathClaim) claim;
				death = true;
				lifeDeathClaims.add(deathClaim);
			} else if (claim instanceof LifeHospitalizedClaim) {
				hospital = true;
				LifeHospitalizedClaim hospitalClaim = (LifeHospitalizedClaim) claim;
				lifeHospitalizedClaims.add(hospitalClaim);
			} else {
				disbility = true;
				DisabilityLifeClaim disabilityClaim = (DisabilityLifeClaim) claim;
				disabilityLifeClaims.add(disabilityClaim);
			}
		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeClaimSurvey");
	}

	public String addLifeClaimSurvey() {
		String result = null;
		try {
			loadAttachment();
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaimSurvey.getLifeClaimProposal().getId(), null, WorkflowTask.CLAIM_APPROVAL, WorkflowReferenceType.LIFE_CLAIM, user,
					responsiblePerson);
			List<LifeClaimProposalAttachment> aa = lifeClaimSurvey.getLifeClaimProposal().getAttachmentList();
			lifeClaimProposalService.addNewLifeClaimSurvey(lifeClaimSurvey, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_SURVEY_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeClaimSurvey.getLifeClaimProposal().getClaimProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void handleProposalAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		if (!proposalUploadedFileMap.containsKey(fileName)) {
			String filePath = temporyDir + lifeProposalId + "/" + fileName;
			proposalUploadedFileMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}
	}

	public void removeProposalUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			proposalUploadedFileMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			if (proposalUploadedFileMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + temporyDir));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadAttachment() {
		List<LifeClaimProposalAttachment> bb = new ArrayList<LifeClaimProposalAttachment>();
		for (String fileName : proposalUploadedFileMap.keySet()) {
			String filePath = PROPOSAL_DIR + lifeProposalId + "/" + fileName;
			bb.add((new LifeClaimProposalAttachment(fileName, filePath)));
		}
		lifeClaimSurvey.getLifeClaimProposal().setAttachmentList(bb);
		List<LifeClaimProposalAttachment> aa = lifeClaimSurvey.getLifeClaimProposal().getAttachmentList();
	}

	public void openTemplateDialog() {
		putParam("lifeClaimProposal", lifeClaimProposal);
		putParam("workFlowList", getWorkflowList());
		openLifeClaimInfoTemplate();
	}

	public List<String> getProposalUploadedFileList() {
		return new ArrayList<String>(proposalUploadedFileMap.values());
	}

	public LifeClaimSurvey getLifeClaimSurvey() {
		return lifeClaimSurvey;
	}

	public LifeClaimProposal getLifeClaimProposal() {
		return lifeClaimProposal;
	}

	public void setLifeClaimSurvey(LifeClaimSurvey lifeClaimSurvey) {
		this.lifeClaimSurvey = lifeClaimSurvey;
	}

	public boolean isDeath() {
		return death;
	}

	public boolean isDisbility() {
		return disbility;
	}

	public boolean isHospital() {
		return hospital;
	}

	public List<LifeDeathClaim> getLifeDeathClaims() {
		return lifeDeathClaims;
	}

	public void setLifeClaimProposal(LifeClaimProposal lifeClaimProposal) {
		this.lifeClaimProposal = lifeClaimProposal;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_SURVEY, WorkFlowType.LIFE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnHospital(SelectEvent event) {
		Hospital hospital = (Hospital) event.getObject();
		lifeClaimSurvey.setHospitalPlace(hospital);
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		lifeClaimSurvey.setTownship(township);
	}

	public List<DisabilityLifeClaim> getDisabilityLifeClaims() {
		return disabilityLifeClaims;
	}

	public List<LifeHospitalizedClaim> getLifeHospitalizedClaims() {
		return lifeHospitalizedClaims;
	}

	public List<WorkFlowHistory> getWorkflowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeClaimProposal.getId());
	}

}
