package org.ace.insurance.web.manage.life.publictermlife.survey;

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
import javax.faces.component.UIData;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.ProductProcessIDConfig;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.medical.productprocess.ProductProcessCriteriaDTO;
import org.ace.insurance.medical.proposal.SurveyType;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.medical.surveyquestion.ProductProcessQuestionLink;
import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.medical.survey.ResourceQuestionAnswerDTO;
import org.ace.insurance.web.manage.medical.survey.SurveyQuestionAnswerDTO;
import org.ace.insurance.web.manage.medical.survey.factory.SurveyQuestionAnswerDTOFactroy;
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
@ManagedBean(name = "AddNewPublictermlifeSurveyActionBean")
public class AddNewPublictermlifeSurveyActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	private User user;
	private LifeSurvey lifeSurvey;
	private Map<String, String> proposalUploadedFileMap;
	private Map<String, String> customerMedChkUpAttMap;
	private Map<String, String> personAttchmentMap;
	private String remark;
	private User responsiblePerson;
	private final String PROPOSAL_DIR = "/upload/life-proposal/";
	private String temporyDir;
	private String lifeProposalId;
	private ProposalInsuredPerson proposalInsuredPerson;
	private List<SurveyQuestionAnswerDTO> surveyQuestionAnswerDTOList;
	private boolean isMedicalCheckUpAtt;
	private boolean isInsuAtt;
	private boolean isInsuPerson;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeSurvey = (lifeSurvey == null) ? (LifeSurvey) getParam("lifeSurvey") : lifeSurvey;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeSurvey");
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		LifeProposal lifeProposal = lifeSurvey.getLifeProposal();
		this.lifeProposalId = lifeProposal.getId();
		this.proposalInsuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		createNewAttachMap();
		changedTempFile(lifeProposal);
		saveIntoTempFolder(lifeProposal);
	}

	private void createNewAttachMap() {
		proposalUploadedFileMap = new HashMap<String, String>();
		personAttchmentMap = new HashMap<String, String>();
		customerMedChkUpAttMap = new HashMap<String, String>();
		surveyQuestionAnswerDTOList = new ArrayList<SurveyQuestionAnswerDTO>();
	}

	// when file remove and refresh page
	private void changedTempFile(LifeProposal lifeProposal) {
		List<LifeProposalAttachment> listProposalAttList = new ArrayList<LifeProposalAttachment>();
		List<Attachment> attList = new ArrayList<Attachment>();
		List<InsuredPersonAttachment> insuAttList = new ArrayList<InsuredPersonAttachment>();
		for (LifeProposalAttachment attach : lifeProposal.getAttachmentList()) {
			if (attach.getFilePath().indexOf("temp") != -1) {
				String fileName = attach.getName();
				String filePath = PROPOSAL_DIR + lifeProposalId + "/" + fileName;
				listProposalAttList.add(new LifeProposalAttachment(fileName, filePath));
			}
		}
		if (listProposalAttList.size() > 0) {
			lifeProposal.setAttachmentList(new ArrayList<LifeProposalAttachment>());
			lifeProposal.getAttachmentList().addAll(listProposalAttList);
		}
		for (Attachment attach : lifeProposal.getCustomerMedicalCheckUpAttachmentList()) {
			if (attach.getFilePath().indexOf("temp") != -1) {
				String fileName = attach.getName();
				String filePath = PROPOSAL_DIR + lifeProposalId + "/" + fileName;
				attList.add(new Attachment(fileName, filePath));
			}
		}
		if (attList.size() > 0) {
			lifeProposal.setCustomerMedicalCheckUpAttachmentList(new ArrayList<Attachment>());
			lifeProposal.getCustomerMedicalCheckUpAttachmentList().addAll(attList);
		}
		for (InsuredPersonAttachment attach : lifeProposal.getProposalInsuredPersonList().get(0).getAttachmentList()) {
			if (attach.getFilePath().indexOf("temp") != -1) {
				String fileName = attach.getName();
				String filePath = PROPOSAL_DIR + lifeProposalId + "/" + proposalInsuredPerson.getId() + "/" + fileName;
				insuAttList.add(new InsuredPersonAttachment(fileName, filePath));
			}
		}
		if (insuAttList.size() > 0) {
			lifeProposal.getProposalInsuredPersonList().get(0).setAttachmentList(new ArrayList<InsuredPersonAttachment>());
			lifeProposal.getProposalInsuredPersonList().get(0).getAttachmentList().addAll(insuAttList);
		}
	}

	private void saveIntoTempFolder(LifeProposal lifeProposal) {
		if (lifeProposal.getAttachmentList().size() > 0 || lifeProposal.getCustomerMedicalCheckUpAttachmentList().size() > 0
				|| lifeProposal.getProposalInsuredPersonList().get(0).getAttachmentList().size() > 0
				|| lifeProposal.getProposalInsuredPersonList().get(0).getBirthCertificateAttachment().size() > 0) {
			String srcPath = getUploadPath() + PROPOSAL_DIR + lifeProposalId;
			String destPath = getUploadPath() + temporyDir + lifeProposalId;
			try {
				FileHandler.copyDirectory(srcPath, destPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String filePath = null;
			for (LifeProposalAttachment attach : lifeProposal.getAttachmentList()) {
				filePath = attach.getFilePath();
				filePath = filePath.replaceAll("/upload/life-proposal/", temporyDir);
				attach.setFilePath(filePath);
				proposalUploadedFileMap.put(attach.getName(), attach.getFilePath());
			}
			for (InsuredPersonAttachment attach : proposalInsuredPerson.getAttachmentList()) {
				filePath = attach.getFilePath();
				filePath = filePath.replaceAll("/upload/life-proposal/", temporyDir);
				attach.setFilePath(filePath);
				personAttchmentMap.put(attach.getName(), attach.getFilePath());
			}
			for (Attachment att : lifeProposal.getCustomerMedicalCheckUpAttachmentList()) {
				filePath = att.getFilePath();
				filePath = filePath.replaceAll("/upload/life-proposal/", temporyDir);
				att.setFilePath(filePath);
				customerMedChkUpAttMap.put(att.getName(), att.getFilePath());
			}
		}
	}

	public boolean isMedicalCheckUpAtt() {
		return isMedicalCheckUpAtt;
	}

	public boolean isInsuAtt() {
		return isInsuAtt;
	}

	public LifeSurvey getSurvey() {
		return lifeSurvey;
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

	public List<String> getProposalUploadedFileList() {
		return new ArrayList<String>(proposalUploadedFileMap.values());
	}

	public List<String> getCustomerMedicalCheckUpUploadedFileList() {
		return new ArrayList<String>(customerMedChkUpAttMap.values());
	}

	public List<String> getPersonUploadedFileList() {
		return new ArrayList<String>(personAttchmentMap.values());
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
		if (!personAttchmentMap.containsKey(fileName)) {
			String filePath = temporyDir + lifeProposalId + "/" + proposalInsuredPerson.getId() + "/" + fileName;
			personAttchmentMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}
	}

	public void handleCustomerAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		if (!customerMedChkUpAttMap.containsKey(fileName)) {
			String filePath = temporyDir + lifeProposalId + "/" + fileName;
			customerMedChkUpAttMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}
	}

	public void returnHospital(SelectEvent event) {
		Hospital hospital = (Hospital) event.getObject();
		lifeSurvey.setHospital(hospital);
		lifeSurvey.setAddress(hospital.getAddress().getPermanentAddress());
		lifeSurvey.setTownship(hospital.getAddress().getTownship());
	}

	public void preparePersonAttachment() {
		isInsuAtt = true;
	}

	public void prepareMedChkUpAttachment() {
		isMedicalCheckUpAtt = true;
	}

	public void preparePersonAttachmentDetails(ProposalInsuredPerson proposalPerson) {
		this.proposalInsuredPerson = proposalPerson;
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
		}
		if (lifeSurvey.getLifeProposal().getCustomerMedicalCheckUpAttachmentList().size() > 0) {
			lifeSurvey.getLifeProposal().setCustomerMedicalCheckUpAttachmentList(new ArrayList<Attachment>());
		}
		if (lifeSurvey.getLifeProposal().getProposalInsuredPersonList().get(0).getAttachmentList().size() > 0) {
			lifeSurvey.getLifeProposal().getProposalInsuredPersonList().get(0).setAttachmentList(new ArrayList<InsuredPersonAttachment>());
		}
		LifeProposal lifeProposal = lifeSurvey.getLifeProposal();
		for (String fileName : proposalUploadedFileMap.keySet()) {
			String filePath = PROPOSAL_DIR + lifeProposalId + "/" + fileName;
			lifeProposal.addAttachment(new LifeProposalAttachment(fileName, filePath));
		}
		for (String fileName : customerMedChkUpAttMap.keySet()) {
			String filePath = PROPOSAL_DIR + lifeProposalId + "/" + fileName;
			lifeProposal.addCustomerMedicalChkUpAttachment(new Attachment(fileName, filePath));
		}
		for (String fileName : personAttchmentMap.keySet()) {
			String filePath = PROPOSAL_DIR + lifeProposalId + "/" + proposalInsuredPerson.getId() + "/" + fileName;
			lifeProposal.getProposalInsuredPersonList().get(0).addAttachment(new InsuredPersonAttachment(fileName, filePath));
		}
	}

	public String addNewSurvey() {
		String result = null;
		loadAttachment();
		try {
			if (validateMedicalCheckUpAttach()) {
				WorkflowTask workflowTask = WorkflowTask.APPROVAL;
				WorkflowReferenceType referenceType = WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL;
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeSurvey.getLifeProposal().getId(), remark, workflowTask, referenceType, user, responsiblePerson);
				lifeProposalService.addNewSurvey(lifeSurvey, workFlowDTO);
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.SURVEY_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeSurvey.getLifeProposal().getProposalNo());
				result = "dashboard";
				moveUploadedFiles();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public boolean validateMedicalCheckUpAttach() {
		boolean valid = true;
		if (validateClsOfHealth()) {
			valid = false;
			addErrorMessage("surveyEntryForm:personTable", MessageId.CLS_OF_HEALTH_REQUIRED);
		} else if (surveyQuestionAnswerDTOList.isEmpty()) {
			valid = true;
			addErrorMessage("surveyEntryForm:personTable", "Survey Questions is required!");
		}
		return valid;
	}

	private boolean validateClsOfHealth() {
		boolean result = lifeSurvey.getLifeProposal().getProposalInsuredPersonList().stream().anyMatch(person -> {

			if (person.getClsOfHealth() == null) {
				return true;
			}
			return false;
		});

		return result;
	}

	public ClassificationOfHealth[] getClassificationHealthList() {
		return ClassificationOfHealth.values();
	}

	public boolean isEmptyInsuPerAtt() {
		if (personAttchmentMap == null || personAttchmentMap.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean isEmptyAtt() {
		if (customerMedChkUpAttMap == null || customerMedChkUpAttMap.isEmpty()) {
			return true;
		}
		return false;
	}

	public void removeInsuPersonUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			personAttchmentMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			String path = temporyDir + lifeProposalId + "/" + proposalInsuredPerson.getId();
			if (personAttchmentMap.isEmpty() && proposalUploadedFileMap.isEmpty()) {
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
			if (proposalUploadedFileMap.isEmpty() && personAttchmentMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + temporyDir));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeCustomerMedChkUpUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			customerMedChkUpAttMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			if (customerMedChkUpAttMap.isEmpty() && personAttchmentMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + temporyDir));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final String reportName = "PublicTermLifeSanction";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private String fileName = null;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		String customerName = lifeSurvey.getLifeProposal().getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "PublicTermLife_" + customerName + "_Sanction" + ".pdf";
		LifeProposal proposal = lifeSurvey.getLifeProposal();
		DocumentBuilder.generatePublicTermLifeSanction(proposal, dirPath, fileName);
		PrimeFaces.current().executeScript("PF('pdfDialog').show()");

	}

	public void selectUser() {
		WorkflowTask workflowTask = WorkflowTask.APPROVAL;
		WorkFlowType workFlowType = WorkFlowType.PUBLIC_TERM_LIFE;
		selectUser(workflowTask, workFlowType);
	}

	public void addSurveyQuestion() {
		proposalInsuredPerson.getSurveyQuestionAnswerList().clear();
		for (SurveyQuestionAnswerDTO questionDTO : surveyQuestionAnswerDTOList) {
			SurveyQuestionAnswer question = SurveyQuestionAnswerDTOFactroy.getSurveyQuestionAnswer(questionDTO);
			proposalInsuredPerson.addSurveyQuestionAnswer(question);
		}
		PrimeFaces.current().executeScript("PF('questionDialog').hide();");
	}

	/* SurveyQuestionAnswer in ShortTermEndowLife */
	public void setProposalInsuredPerson(ProposalInsuredPerson insuredPerson) {
		List<ProductProcessQuestionLink> ppQueLinkList = null;
		SurveyType type = null;
		for (ProposalInsuredPerson pip : lifeSurvey.getLifeProposal().getProposalInsuredPersonList()) {
			if (pip.getSurveyQuestionAnswerList().isEmpty()) {
				ProductProcessCriteriaDTO dto = new ProductProcessCriteriaDTO();
				dto.setAge(pip.getAgeForNextYear());
				dto.setSumInsured(pip.getProposedSumInsured());
				ppQueLinkList = medicalProposalService.findPProQueByPPId(KeyFactorChecker.getPublicTermLifeId(), ProductProcessIDConfig.getProposalProcessId(), dto);
				if (pip.getProposedSumInsured() >= 5000000 && pip.getAgeForNextYear() >= 18 && pip.getAgeForNextYear() <= 60) {
					type = SurveyType.PUBLIC_TERM_LIFE_OVER_5_MILLION;
				} else if (pip.getProposedSumInsured() >= 3000000 && pip.getAgeForNextYear() >= 61 && pip.getAgeForNextYear() <= 75) {
					type = SurveyType.PUBLIC_TERM_LIFE_OVER_3_MILLION;
				}
				pip.getSurveyQuestionAnswerList().clear();

				for (ProductProcessQuestionLink ppQuesLink : ppQueLinkList) {
					SurveyQuestionAnswer surveyAnswer = new SurveyQuestionAnswer(ppQuesLink);
					surveyAnswer.setSurveyType(type);
					pip.getSurveyQuestionAnswerList().add(surveyAnswer);
					SurveyQuestionAnswerDTO surveyAnswerDTO = new SurveyQuestionAnswerDTO(ppQuesLink);
					surveyQuestionAnswerDTOList.add(surveyAnswerDTO);
				}
			}
		}
		this.proposalInsuredPerson = insuredPerson;
	}

	public ProposalInsuredPerson getProposalInsuredPerson() {
		return proposalInsuredPerson;
	}

	public List<SurveyQuestionAnswerDTO> getSurveyQuestionAnswerDTOList() {
		return surveyQuestionAnswerDTOList;
	}

	public void changeResourceQuestion(AjaxBehaviorEvent e) {
		UIData data = (UIData) e.getComponent().findComponent("questionTable");
		int rowIndex = data.getRowIndex();
		SurveyQuestionAnswerDTO surveyQuestionAnswer = getSurveyQuestionAnswerDTOList().get(rowIndex);
		List<ResourceQuestionAnswerDTO> resourceQuestionAnswerList = surveyQuestionAnswer.getResourceQuestionList();
		for (ResourceQuestionAnswerDTO resourceQuestionAnswer : resourceQuestionAnswerList) {
			resourceQuestionAnswer.setValue(0);
		}
		int index = resourceQuestionAnswerList.indexOf(surveyQuestionAnswer.getSelectedResourceQAnsDTO());
		ResourceQuestionAnswerDTO selectedResourceQAnsDTO = resourceQuestionAnswerList.get(index);
		selectedResourceQAnsDTO.setValue(1);
		surveyQuestionAnswer.setSelectedResourceQAnsDTO(selectedResourceQAnsDTO);
	}

	public void changeResourceQuestionList(AjaxBehaviorEvent e) {
		UIData data = (UIData) e.getComponent().findComponent("questionTable");
		int rowIndex = data.getRowIndex();
		SurveyQuestionAnswerDTO surveyQuestionAnswer = getSurveyQuestionAnswerDTOList().get(rowIndex);
		List<ResourceQuestionAnswerDTO> resourceQuestionAnswerList = surveyQuestionAnswer.getResourceQuestionList();
		for (ResourceQuestionAnswerDTO resourceQuestionAnswer : resourceQuestionAnswerList) {
			resourceQuestionAnswer.setValue(0);
		}

		for (ResourceQuestionAnswerDTO resourceQuestionAnswer : surveyQuestionAnswer.getSelectedResourceQAnsDTOList()) {
			int index = resourceQuestionAnswerList.indexOf(resourceQuestionAnswer);
			ResourceQuestionAnswerDTO selectedResourceQAnsDTO = resourceQuestionAnswerList.get(index);
			selectedResourceQAnsDTO.setValue(1);
			surveyQuestionAnswer.setSelectedResourceQAnsDTO(selectedResourceQAnsDTO);
		}
	}

	public void changeBooleanValue(AjaxBehaviorEvent e) {
		UIData data = (UIData) e.getComponent().findComponent("questionTable");
		int rowIndex = data.getRowIndex();
		SurveyQuestionAnswerDTO surveyQuestionAnswer = getSurveyQuestionAnswerDTOList().get(rowIndex);
		List<ResourceQuestionAnswerDTO> resourceQuestionAnswerList = surveyQuestionAnswer.getResourceQuestionList();
		resourceQuestionAnswerList.get(0).setName(surveyQuestionAnswer.getTureLabel());
		if (surveyQuestionAnswer.isTureLabelValue()) {
			resourceQuestionAnswerList.get(0).setValue(1);
		} else {
			resourceQuestionAnswerList.get(0).setValue(0);
		}
		resourceQuestionAnswerList.get(1).setName(surveyQuestionAnswer.getFalseLabel());
		if (surveyQuestionAnswer.isTureLabelValue()) {
			resourceQuestionAnswerList.get(1).setValue(0);
		} else {
			resourceQuestionAnswerList.get(1).setValue(1);
		}
	}

	public void changeDate(SelectEvent e) {
		UIData data = (UIData) e.getComponent().findComponent("questionTable");
		int rowIndex = data.getRowIndex();
		SurveyQuestionAnswerDTO surveyQuestionAnswer = getSurveyQuestionAnswerDTOList().get(rowIndex);
		List<ResourceQuestionAnswerDTO> resourceQuestionAnswerList = surveyQuestionAnswer.getResourceQuestionList();
		resourceQuestionAnswerList.get(0).setResult(Utils.getDateFormatString(surveyQuestionAnswer.getAnswerDate()));
	}

	public void resetDate(SurveyQuestionAnswerDTO surveyQuestionAnswer) {
		surveyQuestionAnswer.setAnswerDate(null);
		surveyQuestionAnswer.getResourceQuestionList().get(0).setResult(null);
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

	public void removeHospital() {
		lifeSurvey.setHospital(null);
		lifeSurvey.setAddress(null);
		lifeSurvey.setTownship(null);
	}

	public void openTemplateDialog() {
		putParam("lifeProposal", lifeSurvey.getLifeProposal());
		putParam("workFlowList", getWorkFlowList());
		openNewLifeProposalInfoTemplate();
	}
}
