package org.ace.insurance.web.manage.life.singlepremiumcreditlife.survey;

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

import org.ace.insurance.common.ProductProcessIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
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
import org.ace.insurance.product.Product;
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
@ManagedBean(name = "AddNewSinglePremiumCreditSurveyActionBean")
public class AddNewSinglePremiumCreditSurveyActionBean extends BaseBean implements Serializable {

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

	@ManagedProperty(value = "#{LifeEndorsementService}")
	private ILifeEndorsementService lifeEndorsementService;

	public void setLifeEndorsementService(ILifeEndorsementService lifeEndorsementService) {
		this.lifeEndorsementService = lifeEndorsementService;
	}

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private User user;
	private LifeSurvey lifeSurvey;
	private BancaassuranceProposal bancaassuranceProposal;
	private Map<String, String> proposalUploadedFileMap;
	private Map<String, ProposalInsuredPerson> personMap;
	private Map<String, Map<String, String>> personAttchmentMap;
	private boolean showEntry;
	private boolean isEndorse;
	private boolean isSinglePremiumCreditLife;
	private String remark;
	private User responsiblePerson;
	private final String PROPOSAL_DIR = "/upload/SPClife-proposal/";
	private String temporyDir;
	private String lifeProposalId;
	private ProposalInsuredPerson proposalInsuredPerson;
	private LifeEndorseInfo lifeEndorseInfo;
	private LifeProposal oldLifeProposal;
	private List<SurveyQuestionAnswerDTO> surveyQuestionAnswerDTOList;

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
		/* Create temp directory for upload */
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";

		LifeProposal lifeProposal = lifeSurvey.getLifeProposal();
		this.lifeProposalId = lifeProposal.getId();
		bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeSurvey.getLifeProposal().getId());
		Product product = lifeSurvey.getLifeProposal().getProposalInsuredPersonList().get(0).getProduct();
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isEndorse = lifeSurvey.getLifeProposal().getProposalType().equals(ProposalType.ENDORSEMENT) ? true : false;
		proposalUploadedFileMap = new HashMap<String, String>();
		personAttchmentMap = new HashMap<String, Map<String, String>>();
		personMap = new HashMap<String, ProposalInsuredPerson>();
		surveyQuestionAnswerDTOList = new ArrayList<SurveyQuestionAnswerDTO>();

		if (lifeProposal.getLifePolicy() != null) {
			copyAttachment(lifeProposal);
			oldLifeProposal = lifeProposal.getLifePolicy().getLifeProposal();
			lifeEndorseInfo = lifeEndorsementService.findLastLifeEndorseInfoByProposalNo(lifeProposal.getLifePolicy().getPolicyNo());
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
					if (newProposalInsuredPerson.getInsPersonCodeNo().equals(oldProposalInsuredPerson.getInsPersonCodeNo())) {
						srcPath = getUploadPath() + temporyDir + lifeProposalId + "/" + oldProposalInsuredPerson.getId();
						destPath = getUploadPath() + temporyDir + lifeProposalId + "/" + newProposalInsuredPerson.getId();
						FileHandler.renameFile(srcPath, destPath);
					}
				}
			}

			// rename filePath
			String filePath = null;
			for (LifeProposalAttachment attach : lifeProposal.getAttachmentList()) {
				filePath = attach.getFilePath();
				filePath = filePath.replaceAll("/upload/SPClife-proposal/", temporyDir);
				filePath = filePath.replaceAll(oldLifeProposal.getId(), lifeProposalId);
				attach.setFilePath(filePath);
			}

			for (ProposalInsuredPerson oldProposalInsuredPerson : oldLifeProposal.getProposalInsuredPersonList()) {
				for (ProposalInsuredPerson newProposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
					if (newProposalInsuredPerson.getInsPersonCodeNo().equals(oldProposalInsuredPerson.getInsPersonCodeNo())) {
						for (InsuredPersonAttachment attach : newProposalInsuredPerson.getAttachmentList()) {
							filePath = attach.getFilePath();
							filePath = filePath.replaceAll("/upload/SPClife-proposal/", temporyDir);
							filePath = filePath.replaceAll(oldLifeProposal.getId(), lifeProposalId);
							filePath = filePath.replaceAll(oldProposalInsuredPerson.getId(), newProposalInsuredPerson.getId());
							attach.setFilePath(filePath);
						}
					}
				}
			}
			// prepare
		} else if (lifeProposal.getAttachmentList().size() > 0) {
			String srcPath = getUploadPath() + PROPOSAL_DIR + lifeProposalId;
			String destPath = getUploadPath() + temporyDir + lifeProposalId;
			try {
				FileHandler.copyDirectory(srcPath, destPath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			String filePath = null;
			// rename filePath
			for (LifeProposalAttachment attach : lifeProposal.getAttachmentList()) {
				filePath = attach.getFilePath();
				filePath = filePath.replaceAll("/upload/SPClife-proposal/", temporyDir);
				attach.setFilePath(filePath);
			}
			for (ProposalInsuredPerson newProposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
				for (InsuredPersonAttachment attach : newProposalInsuredPerson.getAttachmentList()) {
					filePath = attach.getFilePath();
					filePath = filePath.replaceAll("/upload/SPClife-proposal/", temporyDir);
					attach.setFilePath(filePath);
				}
			}
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
				fPath = fPath.replaceAll("/upload/SPClife-proposal/", temporyDir);
				fPath = fPath.replaceAll(oldProposal.getId(), lifeProposal.getId());
				attach.setFilePath(fPath);
			}
			for (ProposalInsuredPerson oldProposalInsuredPerson : oldProposal.getProposalInsuredPersonList()) {
				for (ProposalInsuredPerson newProposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
					if (oldProposalInsuredPerson.getInsPersonCodeNo().equals(newProposalInsuredPerson.getInsPersonCodeNo())) {
						for (InsuredPersonAttachment attach : newProposalInsuredPerson.getAttachmentList()) {
							fPath = attach.getFilePath();
							fPath = fPath.replaceAll("/upload/SPClife-proposal/", temporyDir);
							fPath = fPath.replaceAll(oldProposal.getId(), lifeProposal.getId());
							fPath = fPath.replaceAll(oldProposalInsuredPerson.getId(), newProposalInsuredPerson.getId());
							attach.setFilePath(fPath);
						}
					}
				}
			}
		}
	}

	public void loadDefaultClassOfHealth(ClassificationOfHealth clasOfHealth) {
		for (ProposalInsuredPerson insuredPerson : lifeSurvey.getLifeProposal().getProposalInsuredPersonList()) {
			if (insuredPerson.getClsOfHealth() == null) {
				insuredPerson.setClsOfHealth(clasOfHealth);
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

	public LifeSurvey getSurvey() {
		return lifeSurvey;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
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

	// end prepare
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

	private boolean validateAttachement() {
		boolean valid = true;

		if (getPersonUploadedFileList().size() <= 0) {
			String formID = "surveyEntryForm";
			addErrorMessage(formID + ":personTable", MessageId.REQUIRED_SURVEY_ATTACH);
			valid = false;
		}
		return valid;
	}

	private boolean validateSurveyQuestionsAnswer() {

		boolean valid = true;

		if (proposalInsuredPerson.getSurveyQuestionAnswerList().size() <= 0) {
			String formID = "surveyEntryForm";
			addErrorMessage(formID + ":personTable", MessageId.ANSWER_SURVERY_QUESTION);
			valid = false;
		}
		return valid;
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
			if (validateAttachement() && validateSurveyQuestionsAnswer()) {
				loadAttachment();
				WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_APPROVAL : WorkflowTask.APPROVAL;
				WorkflowReferenceType referenceType = isSinglePremiumCreditLife ? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL : WorkflowReferenceType.LIFE_PROPOSAL;
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeSurvey.getLifeProposal().getId(), remark, workflowTask, referenceType, user, responsiblePerson);
				if (isEndorse) {
					lifeEndorsementService.addNewSurvey(lifeSurvey, workFlowDTO);
				} else {
					lifeProposalService.addNewSurvey(lifeSurvey, workFlowDTO);
				}
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
			if (vehFileMap.isEmpty() && proposalUploadedFileMap.isEmpty()) {
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

	private final String reportName = "LifeSanction";
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
		fileName = "SinglePremiumCreditLife_" + customerName + "_Sanction" + ".pdf";
		LifeProposal proposal = lifeSurvey.getLifeProposal();
		DocumentBuilder.generateSinglePremiumCreditLifeSanction(proposal, dirPath, fileName);
		PrimeFaces.current().executeScript("PF('pdfDialog').show()");

	}

	public void addSurveyQuestion() {
		proposalInsuredPerson.getSurveyQuestionAnswerList().clear();
		for (SurveyQuestionAnswerDTO questionDTO : surveyQuestionAnswerDTOList) {
			SurveyQuestionAnswer question = SurveyQuestionAnswerDTOFactroy.getSurveyQuestionAnswer(questionDTO);
			proposalInsuredPerson.addSurveyQuestionAnswer(question);
		}
		PrimeFaces.current().executeScript("PF('questionDialog').hide();");
	}

	public void selectUser() {
		WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_APPROVAL : WorkflowTask.APPROVAL;
		WorkFlowType workFlowType = isSinglePremiumCreditLife ? WorkFlowType.SINGLE_PREMIUM_CREDIT_LIFE : WorkFlowType.LIFE;
		selectUser(workflowTask, workFlowType);
	}

	/* SurveyQuestionAnswer in SinglePremiumCredit */
	public void setProposalInsuredPerson(ProposalInsuredPerson insuredPerson) {
		this.proposalInsuredPerson = insuredPerson;

		surveyQuestionAnswerDTOList.clear();

		for (ProposalInsuredPerson pip : lifeSurvey.getLifeProposal().getProposalInsuredPersonList()) {
			if (pip.getId().equals(insuredPerson.getId())) {
				if (pip.getSurveyQuestionAnswerList() != null && !pip.getSurveyQuestionAnswerList().isEmpty()) {
					for (SurveyQuestionAnswer sqanswer : pip.getSurveyQuestionAnswerList()) {
						SurveyQuestionAnswerDTO surveyAnswerDTO = SurveyQuestionAnswerDTOFactroy.getSurveyQuestionAnswerDTO(sqanswer);
						surveyAnswerDTO.setSurveyType(SurveyType.SINGLE_PERMIUM_CREDIT_LIFE_SURVEY);
						surveyQuestionAnswerDTOList.add(surveyAnswerDTO);
					}
				} else {

					ProductProcessCriteriaDTO dto = new ProductProcessCriteriaDTO();
					dto.setAge(pip.getAgeForNextYear());
					dto.setSumInsured(pip.getProposedSumInsured());
					dto.setPeriodMonth(pip.getPeriodMonth());
					List<ProductProcessQuestionLink> ppQueLinkList = medicalProposalService.findPProQueByPPIdByOptionTypeForSPCL(KeyFactorChecker.getSinglePremiumCreditLifeID(),
							ProductProcessIDConfig.getProposalProcessId(), dto);
					for (ProductProcessQuestionLink ppQuesLink : ppQueLinkList) {
						SurveyQuestionAnswer surveyAnswer = new SurveyQuestionAnswer(ppQuesLink);
						surveyAnswer.setSurveyType(SurveyType.SINGLE_PERMIUM_CREDIT_LIFE_SURVEY);
						pip.getSurveyQuestionAnswerList().add(surveyAnswer);
						SurveyQuestionAnswerDTO surveyAnswerDTO = new SurveyQuestionAnswerDTO(ppQuesLink);
						surveyQuestionAnswerDTOList.add(surveyAnswerDTO);
					}
				}
			}

		}

	}

	public ProposalInsuredPerson getProposalInsuredPerson() {
		return proposalInsuredPerson;
	}

	public List<SurveyQuestionAnswerDTO> getSurveyQuestionAnswerDTOList() {
		return surveyQuestionAnswerDTOList;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
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
		ResourceQuestionAnswerDTO resourceQuestionAnswer = resourceQuestionAnswerList.get(0);
		resourceQuestionAnswer.setName(surveyQuestionAnswer.getTureLabel());
		if (surveyQuestionAnswer.isTureLabelValue()) {
			resourceQuestionAnswer.setValue(1);
		} else {
			resourceQuestionAnswer.setValue(0);
		}
		resourceQuestionAnswer = resourceQuestionAnswerList.get(1);
		resourceQuestionAnswer.setName(surveyQuestionAnswer.getFalseLabel());
		if (surveyQuestionAnswer.isTureLabelValue()) {
			resourceQuestionAnswer.setValue(0);
		} else {
			resourceQuestionAnswer.setValue(1);
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

	public boolean getIsEndorse() {
		return isEndorse;
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

	public void returnHospital(SelectEvent event) {
		Hospital hospital = (Hospital) event.getObject();
		lifeSurvey.setHospital(hospital);
		lifeSurvey.setAddress(hospital.getAddress().getPermanentAddress());
		lifeSurvey.setTownship(hospital.getAddress().getTownship());
	}

	public void removeHospital() {
		lifeSurvey.setHospital(null);
		lifeSurvey.setAddress(null);
		lifeSurvey.setTownship(null);
	}

	public String getPageHeader() {
		return (isEndorse ? "Life Endorsement" : isSinglePremiumCreditLife ? "Single Premium Credit Life" : "Life") + " Proposal Survey";
	}
}
