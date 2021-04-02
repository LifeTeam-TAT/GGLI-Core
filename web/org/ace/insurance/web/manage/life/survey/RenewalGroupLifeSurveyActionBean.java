package org.ace.insurance.web.manage.life.survey;

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
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "RenewalGroupLifeSurveyActionBean")
public class RenewalGroupLifeSurveyActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{RenewalGroupLifeProposalService}")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	public void setRenewalGroupLifeProposalService(IRenewalGroupLifeProposalService renewalGroupLifeProposalService) {
		this.renewalGroupLifeProposalService = renewalGroupLifeProposalService;
	}

	private User user;
	private LifeSurvey lifeSurvey;

	private Map<String, String> proposalUploadedFileMap;
	private Map<String, ProposalInsuredPerson> personMap;
	private Map<String, Map<String, String>> personAttchmentMap;
	private boolean showEntry;
	private String remark;
	private User responsiblePerson;
	private final String PROPOSAL_DIR = "/upload/life-proposal/";
	private String temporyDir;
	private String lifeProposalId;

	private ProposalInsuredPerson proposalInsuredPerson;
	private LifeEndorseInfo lifeEndorseInfo;
	private LifeProposal oldLifeProposal;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeSurvey = (lifeSurvey == null) ? (LifeSurvey) getParam("lifeSurvey") : lifeSurvey;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeSurvey");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		/* Create temp directory for upload */
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";

		LifeProposal lifeProposal = lifeSurvey.getLifeProposal();

		proposalUploadedFileMap = new HashMap<String, String>();
		personAttchmentMap = new HashMap<String, Map<String, String>>();
		personMap = new HashMap<String, ProposalInsuredPerson>();

		if (lifeProposal.getLifePolicy() != null) {
			copyAttachment(lifeProposal);
			oldLifeProposal = lifeProposal.getLifePolicy().getLifeProposal();
			// prepare
			// proposal attachment
			String srcPath = getUploadPath() + PROPOSAL_DIR + oldLifeProposal.getId();
			String destPath = getUploadPath() + temporyDir + lifeProposalId;
			try {
				FileHandler.copyDirectory(srcPath, destPath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// proposalInsuredPerson attachment
			for (ProposalInsuredPerson oldProposalInsuredPerson : oldLifeProposal.getProposalInsuredPersonList()) {
				for (ProposalInsuredPerson newProposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
					if (oldProposalInsuredPerson.getInsPersonCodeNo().equals(newProposalInsuredPerson.getInsPersonCodeNo())) {
						srcPath = getUploadPath() + temporyDir + lifeProposalId + "/" + oldProposalInsuredPerson.getId();
						destPath = getUploadPath() + temporyDir + lifeProposalId + "/" + newProposalInsuredPerson.getId();
						FileHandler.renameFile(srcPath, destPath);
					}
				}
			}
			String filePath = null;
			// rename filePath
			for (LifeProposalAttachment attach : lifeProposal.getAttachmentList()) {
				filePath = attach.getFilePath();
				filePath = filePath.replaceAll("/upload/life-proposal/", temporyDir);
				filePath = filePath.replaceAll(oldLifeProposal.getId(), lifeProposalId);
				attach.setFilePath(filePath);
			}
			for (ProposalInsuredPerson oldProposalInsuredPerson : oldLifeProposal.getProposalInsuredPersonList()) {
				for (ProposalInsuredPerson newProposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
					if (oldProposalInsuredPerson.getInsPersonCodeNo().equals(newProposalInsuredPerson.getInsPersonCodeNo())) {
						for (InsuredPersonAttachment attach : newProposalInsuredPerson.getAttachmentList()) {
							filePath = attach.getFilePath();
							filePath = filePath.replaceAll("/upload/life-proposal/", temporyDir);
							filePath = filePath.replaceAll(oldLifeProposal.getId(), lifeProposalId);
							filePath = filePath.replaceAll(oldProposalInsuredPerson.getId(), newProposalInsuredPerson.getId());
							attach.setFilePath(filePath);
						}
					}
				}
			}
			// prepare
		}

		for (ProposalInsuredPerson per : lifeSurvey.getLifeProposal().getProposalInsuredPersonList()) {
			personMap.put(per.getId(), per);
		}

		for (LifeProposalAttachment att : lifeProposal.getAttachmentList()) {
			proposalUploadedFileMap.put(att.getName(), att.getFilePath());
		}

		for (ProposalInsuredPerson pip : lifeSurvey.getLifeProposal().getProposalInsuredPersonList()) {
			personMap.put(pip.getId(), pip);
			Map<String, String> perAttMap = new HashMap<String, String>();
			for (InsuredPersonAttachment ipAtt : pip.getAttachmentList()) {
				perAttMap.put(ipAtt.getName(), ipAtt.getFilePath());
			}
			personAttchmentMap.put(pip.getId(), perAttMap);
		}
		this.lifeProposalId = lifeProposal.getId();

	}

	// prepare
	public void copyAttachment(LifeProposal lifeProposal) {
		LifeProposal oldProposal = lifeProposal.getLifePolicy().getLifeProposal();
		if (lifeProposal.getAttachmentList().size() > 0) {
			// TODO here newPath is from underwriting proposal , you have to
			// create new folder of attachemnt for renewal proposal
			String filePath = lifeProposal.getAttachmentList().get(0).getFilePath();
			String newPath = filePath.replace(filePath.substring(filePath.lastIndexOf("/")), "");
			try {
				FileHandler.copyDirectory(getUploadPath() + newPath, getUploadPath() + temporyDir + lifeProposal.getId());
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (ProposalInsuredPerson oldProposalInsuredPerson : oldProposal.getProposalInsuredPersonList()) {
				for (ProposalInsuredPerson newProposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
					if (oldProposalInsuredPerson.getInsPersonCodeNo().equals(newProposalInsuredPerson.getInsPersonCodeNo())) {
						String srcPath = getUploadPath() + temporyDir + lifeProposal.getId() + "/" + oldProposalInsuredPerson.getId();
						String destPath = getUploadPath() + temporyDir + lifeProposal.getId() + "/" + newProposalInsuredPerson.getId();
						FileHandler.renameFile(srcPath, destPath);
					}
				}
			}
			String fPath = null;
			for (LifeProposalAttachment attach : lifeProposal.getAttachmentList()) {
				fPath = attach.getFilePath();
				fPath = fPath.replaceAll("/upload/life-proposal/", temporyDir);
				fPath = fPath.replaceAll(oldProposal.getId(), lifeProposal.getId());
				attach.setFilePath(fPath);
			}
			for (ProposalInsuredPerson oldProposalInsuredPerson : oldProposal.getProposalInsuredPersonList()) {
				for (ProposalInsuredPerson newProposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
					if (oldProposalInsuredPerson.getInsPersonCodeNo().equals(newProposalInsuredPerson.getInsPersonCodeNo())) {
						for (InsuredPersonAttachment attach : newProposalInsuredPerson.getAttachmentList()) {
							fPath = attach.getFilePath();
							fPath = fPath.replaceAll("/upload/life-proposal/", temporyDir);
							fPath = fPath.replaceAll(oldProposal.getId(), lifeProposal.getId());
							fPath = fPath.replaceAll(oldProposalInsuredPerson.getId(), newProposalInsuredPerson.getId());
							attach.setFilePath(fPath);
						}
					}
				}
			}
		}
	}

	// end prepare
	public String getTemporyDir() {
		return temporyDir;
	}

	public boolean isShowEntry() {
		return showEntry;
	}

	public void setSurvey(LifeSurvey survey) {
		this.lifeSurvey = survey;
	}

	public LifeSurvey getSurvey() {
		return lifeSurvey;
	}

	public ProposalInsuredPerson getProposalInsuredPerson() {
		return proposalInsuredPerson;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setProposalInsuredPerson(ProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
	}

	public List<String> getProposalUploadedFileList() {
		return new ArrayList<String>(proposalUploadedFileMap.values());
	}

	public List<String> getPersonUploadedFileList() {
		if (proposalInsuredPerson != null) {
			Map<String, String> insuredPersonFileMap = personAttchmentMap.get(proposalInsuredPerson.getId());
			return new ArrayList<String>(insuredPersonFileMap.values());
		}
		return new ArrayList<String>();
	}

	public LifeEndorseInfo getLifeEndorseInfo() {
		return lifeEndorseInfo;
	}

	public LifeProposal getOldLifeProposal() {
		return oldLifeProposal;
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

	public void handleInsurePersonAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		Map<String, String> insuredPersonFileMap = personAttchmentMap.get(proposalInsuredPerson.getId());
		if (!insuredPersonFileMap.containsKey(fileName)) {
			String filePath = temporyDir + lifeProposalId + "/" + proposalInsuredPerson.getId() + "/" + fileName;
			insuredPersonFileMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}
	}

	// end perpare
	public void preparePersonAttachment(ProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
		showEntry = true;
		if (!personAttchmentMap.containsKey(proposalInsuredPerson.getId())) {
			personAttchmentMap.put(proposalInsuredPerson.getId(), new HashMap<String, String>());
		}
	}

	public void updateClassificationOfHealth() {
		showEntry = false;
	}

	public void preparePersonAttachmentDetails(ProposalInsuredPerson proposalPerson) {
		this.proposalInsuredPerson = proposalPerson;
		showEntry = false;
	}

	private void moveUploadedFiles() {
		try {
			FileHandler.moveFiles(getUploadPath(), temporyDir + lifeProposalId, PROPOSAL_DIR + lifeProposalId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeSurvey.getLifeProposal().getId());
	}

	private void loadAttachment() {
		if (lifeSurvey.getLifeProposal().getAttachmentList().size() > 0) {
			lifeSurvey.getLifeProposal().setAttachmentList(new ArrayList<LifeProposalAttachment>());

			for (ProposalInsuredPerson ipa : lifeSurvey.getLifeProposal().getProposalInsuredPersonList()) {
				ipa.setAttachmentList(new ArrayList<InsuredPersonAttachment>());
			}
		}
		LifeProposal lifeProposal = lifeSurvey.getLifeProposal();
		for (String fileName : proposalUploadedFileMap.keySet()) {
			String filePath = PROPOSAL_DIR + lifeProposalId + "/" + fileName;
			lifeProposal.addAttachment(new LifeProposalAttachment(fileName, filePath));
		}
		if (personAttchmentMap.keySet() != null) {
			for (String insuPersonId : personAttchmentMap.keySet()) {
				Map<String, String> personUploadedMap = personAttchmentMap.get(insuPersonId);
				if (personUploadedMap != null) {
					for (String fileName : personUploadedMap.keySet()) {
						String filePath = PROPOSAL_DIR + lifeProposalId + "/" + insuPersonId + "/" + fileName;
						personMap.get(insuPersonId).addAttachment(new InsuredPersonAttachment(fileName, filePath));
					}
				}
			}
		}
	}

	public String addNewSurvey() {
		String result = null;
		try {
			if (!validSurvey()) {
				return null;
			}
			loadAttachment();
			WorkflowTask workflowTask = null;
			workflowTask = WorkflowTask.RENEWAL_APPROVAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeSurvey.getLifeProposal().getId(), remark, workflowTask, WorkflowReferenceType.LIFE_PROPOSAL, user, responsiblePerson);
			renewalGroupLifeProposalService.addNewSurvey(lifeSurvey, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.SURVEY_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeSurvey.getLifeProposal().getProposalNo());
			result = "dashboard";
			moveUploadedFiles();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public boolean validSurvey() {
		boolean valid = true;
		String formID = "surveyEntryForm";
		if (responsiblePerson == null) {
			addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		List<ProposalInsuredPerson> personList = lifeSurvey.getLifeProposal().getProposalInsuredPersonList();
		for (ProposalInsuredPerson insuPerson : personList) {
			if (insuPerson.getClsOfHealth() == null) {
				addErrorMessage(formID + ":personTable", MessageId.NEED_CLASSFICATION_HEALTH, insuPerson.getFullName());
				valid = false;
				break;
			}
		}
		if (lifeSurvey.getDate() != null && lifeSurvey.getDate().before(lifeSurvey.getLifeProposal().getSubmittedDate())) {
			addErrorMessage(formID + ":surveyDate", MessageId.INVALID_SURVEY_DATE);
			valid = false;
		}
		if (lifeSurvey.getDate() == null) {
			addErrorMessage(formID + ":surveyDate", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;
	}

	public ClassificationOfHealth[] getClassificationHealthList() {
		return ClassificationOfHealth.values();
	}

	public boolean isEmptyAtt(ProposalInsuredPerson proposalInsuredPerson) {
		String vehId = proposalInsuredPerson.getId();
		Map<String, String> personFileMap = personAttchmentMap.get(vehId);
		if (personFileMap == null || personFileMap.isEmpty()) {
			return true;
		}
		return false;
	}

	public void removeInsuPersonUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			Map<String, String> vehFileMap = personAttchmentMap.get(proposalInsuredPerson.getId());
			vehFileMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			String path = temporyDir + lifeProposalId + "/" + proposalInsuredPerson.getId();
			if (vehFileMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + path));
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	private final String reportName = "LifeSanction";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		boolean valid = true;
		LifeProposal lifeProposal = lifeSurvey.getLifeProposal();
		String customerName = lifeProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "GroupLife_" + customerName + "_Sanction" + ".pdf";
		String formID = "surveyEntryForm";
		List<ProposalInsuredPerson> personList = lifeSurvey.getLifeProposal().getProposalInsuredPersonList();
		for (ProposalInsuredPerson insuPerson : personList) {
			if (insuPerson.getClsOfHealth() == null) {
				addErrorMessage(formID + ":personTable", MessageId.NEED_CLASSFICATION_HEALTH, insuPerson.getFullName());
				valid = false;
				break;
			}
		}
		if (valid) {
			DocumentBuilder.generateGroupLIfeSanction(lifeSurvey.getLifeProposal(), user.getName(), dirPath, fileName);
			PrimeFaces.current().executeScript("PF('issuePolicyPDFDialogSingle').show()");
		}
	}

	public void selectUser() {
		selectUser(WorkflowTask.RENEWAL_APPROVAL, WorkFlowType.LIFE);
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		lifeSurvey.setTownship(township);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
