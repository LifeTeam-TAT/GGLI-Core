package org.ace.java.web.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.FarmerPolicyType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.MedicalProductType;
import org.ace.insurance.common.MonthNames;
import org.ace.insurance.common.MonthType;
import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.common.SchoolLevelType;
import org.ace.insurance.common.SchoolType;
import org.ace.insurance.common.UploadFileConfig;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.interfaces.IProposal;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.medical.claim.ClaimType;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.service.MedicalProposalService;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.medical.surveyAnswer.ResourceQuestionAnswer;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.medical.surveyquestion.InputType;
import org.ace.insurance.mobile.enums.ProposalStatus;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.web.common.ErrorMessage;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.Param;
import org.ace.insurance.web.common.ValidationUtil;
import org.ace.insurance.web.common.WebUtils;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.web.manage.medical.claim.ShowSurveyQuestionAnswerDTO;
import org.ace.insurance.web.manage.medical.survey.ResourceQuestionAnswerDTO;
import org.ace.insurance.web.manage.medical.survey.SurveyQuestionAnswerDTO;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.common.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.PrimeFaces;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BaseBean {
	@ManagedProperty(value = "#{MedicalProposalService}")
	public IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}

	@ManagedProperty(value = "#{MedicalPolicyService}")
	public IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	private static final String THEME = "theme";

	protected static <T> String getProposalUserType(T proposal) {
		IProposal iProposal = null;
		if (proposal instanceof LifeProposal) {
			iProposal = (LifeProposal) proposal;
		}
		return iProposal.getUserType();
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected ServletContext getServletContext() {
		return (ServletContext) getFacesContext().getExternalContext().getContext();
	}

	protected Application getApplication() {
		return getFacesContext().getApplication();
	}

	protected Object getSpringBean(String beanName) {
		return WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean(beanName);
	}

	protected Map getApplicationMap() {
		return getFacesContext().getExternalContext().getApplicationMap();
	}

	@SuppressWarnings("rawtypes")
	protected Map getSessionMap() {
		return getFacesContext().getExternalContext().getSessionMap();
	}

	@SuppressWarnings("unchecked")
	protected void putParam(String key, Object obj) {
		getSessionMap().put(key, obj);
	}

	protected Object getParam(String key) {
		return getSessionMap().get(key);
	}

	protected boolean isExistParam(String key) {
		return getSessionMap().containsKey(key);
	}

	protected void removeParam(String key) {
		if (isExistParam(key)) {
			getSessionMap().remove(key);
		}
	}

	protected void addErrorMessage(ErrorMessage errorMessage) {
		String message = getMessage(errorMessage.getErrorcode(), errorMessage.getParams());
		getFacesContext().addMessage(errorMessage.getId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	protected ResourceBundle getBundle() {
		return ResourceBundle.getBundle(getApplication().getMessageBundle(), getFacesContext().getViewRoot().getLocale());
	}

	protected String getWebRootPath() {
		Object context = getFacesContext().getExternalContext().getContext();
		String systemPath = ((ServletContext) context).getRealPath("/");
		return systemPath;
	}

	protected String getFullTempDirPath() {
		return getWebRootPath() + Constants.TEMP_DIR;
	}

	protected String getFullUploadDirPath() {
		return getWebRootPath() + Constants.UPLOAD_DIR;
	}

	protected String getFullReportDirPath() {
		return getWebRootPath() + Constants.REPORT_DIR;
	}

	protected String getMessage(String id, Object... params) {
		String text = null;
		try {
			text = getBundle().getString(id);
		} catch (MissingResourceException e) {
			text = "!! key " + id + " not found !!";
		}
		if (params != null) {
			MessageFormat mf = new MessageFormat(text);
			text = mf.format(text, params).toString();
		}
		return text;
	}

	protected void addWranningMessage(String id, String errorCode, Object... params) {
		String message = getMessage(errorCode, params);
		getFacesContext().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_WARN, message, ""));
	}

	protected void addInfoMessage(String id, String errorCode, Object... params) {
		String message = getMessage(errorCode, params);
		getFacesContext().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
	}

	protected void addErrorMessage(String id, String errorCode, Object... params) {
		String message = getMessage(errorCode, params);
		getFacesContext().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	protected void addErrorMessage(String message) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
	}

	protected void addInfoMessage(String message) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
	}

	protected void addWranningMessage(String message) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, ""));
	}

	protected void handelSysException(SystemException systemException) {
		FacesMessage facesMessage = resolveExcption(systemException);
		facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
		getFacesContext().addMessage(null, facesMessage);
	}

	public List<SurveyQuestionAnswerDTO> loadSurveyQuestionAnswer(String proposalId) {
		List<SurveyQuestionAnswerDTO> questionDtoList = null;
		medicalProposalService = BeanUtils.getBean("MedicalProposalService", MedicalProposalService.class);
		List<SurveyQuestionAnswer> questionList = medicalProposalService.findSurveyQuestionAnswerByProposalId(proposalId);
		if (questionList != null || questionList.size() > 0) {
			questionDtoList = new ArrayList<SurveyQuestionAnswerDTO>();
			for (SurveyQuestionAnswer question : questionList) {
				if (question == null) {
					break;
				}
				SurveyQuestionAnswerDTO questionDto = new SurveyQuestionAnswerDTO();
				StringBuffer sb = new StringBuffer();
				questionDto.setDescription(question.getDescription());
				questionDto.setPriority(question.getPriority());
				if (question.getInputType().equals(InputType.TEXT) || question.getInputType().equals(InputType.NUMBER) || question.getInputType().equals(InputType.DATE)) {
					if (question.getResourceQuestionList() != null && !question.getResourceQuestionList().isEmpty()) {
						questionDto.setAnswer(question.getResourceQuestionList().get(0).getResult());
					}
				} else {
					for (ResourceQuestionAnswer resQA : question.getResourceQuestionList()) {
						if (resQA.getValue() == 1) {
							if (sb.length() > 0) {
								sb.append(",");
							}
							sb.append(resQA.getName());
						}
					}

					questionDto.setAnswer(sb.toString());
				}

				if (ValidationUtil.isStringEmpty(questionDto.getAnswer())) {
					if (question.getInputType().equals(InputType.NONE)) {
						questionDto.setAnswer("");
					} else if (question.getInputType().equals(InputType.TEXT)) {
						questionDto.setAnswer("\" \"");
					} else if (question.getInputType().equals(InputType.NUMBER)) {
						questionDto.setAnswer("0");
					} else if (question.getInputType().equals(InputType.DATE)) {
						questionDto.setAnswer("No Answer");
					} else {
						questionDto.setAnswer("No Answer");
					}
				}

				questionDtoList.add(questionDto);
			}
		}

		return questionDtoList;
	}

	public void loadSurveyQuestionAnswer(MedicalClaimProposalDTO medicalClaimProposalDTO) {

		if (medicalClaimProposalDTO.getOperationClaimDTO() != null) {
			medicalClaimProposalDTO.getOperationClaimDTO().setSurveyQuestionAnswersList(null);
		}

		if (medicalClaimProposalDTO.getDeathClaimDTO() != null) {
			medicalClaimProposalDTO.getDeathClaimDTO().setSurveyQuestionAnswersList(null);
		}

		if (medicalClaimProposalDTO.getHospitalizedClaimDTO() != null) {
			medicalClaimProposalDTO.getHospitalizedClaimDTO().setSurveyQuestionAnswersList(null);
		}

		if (medicalClaimProposalDTO.getHospitalizedClaimDTO() != null) {
			medicalClaimProposalDTO.getHospitalizedClaimDTO().setSurveyQuestionAnswersList(null);
		}

		for (SurveyQuestionAnswerDTO sQueAns : medicalClaimProposalDTO.getMedicalClaimSurvey().getSurveyQuestionAnswerDTOList()) {
			if (sQueAns.getClaimType().equals(ClaimType.OPERATION_CLAIM)) {
				medicalClaimProposalDTO.getOperationClaimDTO().addSurveyQuestionList(sQueAns);
			}

			else if (sQueAns.getClaimType().equals(ClaimType.DEATH_CLAIM)) {
				medicalClaimProposalDTO.getDeathClaimDTO().addSurveyQuestionList(sQueAns);
			}

			else if (sQueAns.getClaimType().equals(ClaimType.HOSPITALIZED_CLAIM)) {
				medicalClaimProposalDTO.getHospitalizedClaimDTO().addSurveyQuestionList(sQueAns);
			}

			else if (sQueAns.getClaimType().equals(ClaimType.MEDICATION_CLAIM)) {
				medicalClaimProposalDTO.getMedicationClaimDTO().addSurveyQuestionList(sQueAns);
			}
			prepareSurveyQuestionAnswer(sQueAns);
		}
	}

	public void prepareSurveyQuestionAnswer(SurveyQuestionAnswerDTO surveyQueAnsDTO) {
		ShowSurveyQuestionAnswerDTO showSQADTO = null;
		if (surveyQueAnsDTO.getInputType().equals(InputType.SELECT_MANY_CHECKBOX) || surveyQueAnsDTO.getInputType().equals(InputType.SELECT_MANY_MENU)
				|| surveyQueAnsDTO.getInputType().equals(InputType.SELECT_ONE_MENU) || surveyQueAnsDTO.getInputType().equals(InputType.SELECT_ONE_RADIO)
				|| surveyQueAnsDTO.getInputType().equals(InputType.BOOLEAN)) {
			StringBuffer sb = new StringBuffer();
			int i = 0;
			for (ResourceQuestionAnswerDTO resourceQuestionAnswer : surveyQueAnsDTO.getResourceQuestionList()) {
				if (resourceQuestionAnswer.getValue() == 1) {
					if (i != 0) {
						sb.append(",");
					}
					sb.append(resourceQuestionAnswer.getName());
					i++;
				}
			}
			showSQADTO = new ShowSurveyQuestionAnswerDTO(surveyQueAnsDTO.getPriority(), surveyQueAnsDTO.getDescription(), sb.toString());
		} else {
			if (!surveyQueAnsDTO.getResourceQuestionList().isEmpty()) {
				showSQADTO = new ShowSurveyQuestionAnswerDTO(surveyQueAnsDTO.getPriority(), surveyQueAnsDTO.getDescription(),
						surveyQueAnsDTO.getResourceQuestionList().get(0).getResult());
			}
		}
		surveyQueAnsDTO.setShowSurveyQuestionAnswerDTO(showSQADTO);

	}

	protected FacesMessage resolveExcption(SystemException systemException) {
		String errorCode = systemException.getErrorCode();
		if (ErrorCode.NO_PREMIUM_RATE.equals(errorCode)) {
			String paramMessage = "";
			Map<KeyFactor, String> keyfatorValueMap = (Map<KeyFactor, String>) systemException.getResponse();
			int count = keyfatorValueMap.size();
			for (KeyFactor kf : keyfatorValueMap.keySet()) {
				count--;
				if (count == 0) {
					paramMessage = paramMessage + kf.getValue() + " - " + keyfatorValueMap.get(kf);
				} else {
					paramMessage = paramMessage + kf.getValue() + " - " + keyfatorValueMap.get(kf) + ", ";
				}
			}
			return new FacesMessage(FacesMessage.SEVERITY_INFO, getMessage(errorCode, paramMessage), "");
		}
		if (errorCode != null) {
			return new FacesMessage(FacesMessage.SEVERITY_INFO, getMessage(errorCode), systemException.getMessage());
		} else {
			return new FacesMessage(FacesMessage.SEVERITY_INFO, systemException.getMessage(), "");
		}
	}

	/* Premium Recalculation config */
	protected final String PROPOSAL = "PROPOSAL";
	protected final String INFORM = "INFORM";
	protected final String CONFIRMATION = "CONFIRMATION";
	protected final String PAYMENT = "PAYMENT";
	private Properties premiumConfig;
	private static Properties themeConfig;

	protected boolean recalculatePremium(String workflow) {
		if (premiumConfig == null) {
			premiumConfig = new Properties();
			try {
				premiumConfig.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("premium-config.properties"));
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load \"premium-config.properties\"", e);
			}
		}
		return Boolean.parseBoolean(premiumConfig.getProperty(workflow));
	}

	public void setPremiumConfig(Properties premiumConfig) {
		this.premiumConfig = premiumConfig;
	}

	/* Dialog Selection */
	public void selectAgent() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 500);
		dialogOptions.put("contentWidth", 900);
		PrimeFaces.current().dialog().openDynamic("agentDialog", dialogOptions, null);
	}

	public void selectStaff() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 500);
		dialogOptions.put("contentWidth", 900);
		PrimeFaces.current().dialog().openDynamic("staffDialog", dialogOptions, null);
	}
	
	
	public void selectSaleMan() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 450);
		dialogOptions.put("contentWidth", 900);
		PrimeFaces.current().dialog().openDynamic("saleManDialog", dialogOptions, null);
	}

	public void selectLanguage() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 450);
		dialogOptions.put("contentWidth", 900);
		PrimeFaces.current().dialog().openDynamic("languageDialog", dialogOptions, null);
	}

	public void selectReferral() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 600);
		dialogOptions.put("contentWidth", 900);
		PrimeFaces.current().dialog().openDynamic("customerDialog", dialogOptions, null);
	}

	public void selectChannel() {
		PrimeFaces.current().dialog().openDynamic("channelDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCargoType() {
		PrimeFaces.current().dialog().openDynamic("cargoTypeDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCargoName() {
		PrimeFaces.current().dialog().openDynamic("cargoNameDialog", WebUtils.getDialogOption(), null);
	}

	public void selectRoute() {
		PrimeFaces.current().dialog().openDynamic("routeDialog", WebUtils.getDialogOption(), null);
	}

	public void selectPort() {
		PrimeFaces.current().dialog().openDynamic("portDialog", WebUtils.getDialogOption(), null);
	}

	protected void selectUser(WorkflowTask workflowTask, WorkFlowType workFlowType) {
		putParam("WORKFLOWTASK", workflowTask);
		putParam("WORKFLOWTYPE", workFlowType);
		PrimeFaces.current().dialog().openDynamic("userDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCashInTransitPolicyNo() {
		PrimeFaces.current().dialog().openDynamic("cashInTransitPolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectOrganization() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 500);
		dialogOptions.put("contentWidth", 900);
		PrimeFaces.current().dialog().openDynamic("organizationDialog", dialogOptions, null);
	}

	public void selectCustomer() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 600);
		dialogOptions.put("contentWidth", 900);
		PrimeFaces.current().dialog().openDynamic("customerDialog", dialogOptions, null);
	}

	public void selectExpress() {
		PrimeFaces.current().dialog().openDynamic("expressDialog", WebUtils.getDialogOption(), null);
	}

	public void selectTownship() {
		PrimeFaces.current().dialog().openDynamic("townshipDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBranch() {
		PrimeFaces.current().dialog().openDynamic("branchDialog", WebUtils.getDialogOption(), null);
	}

	public void selectChannels() {
		PrimeFaces.current().dialog().openDynamic("channelDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBankWallets() {
		PrimeFaces.current().dialog().openDynamic("bankWalletDialog", WebUtils.getDialogOption(), null);
	}

//	old method for selectBancaReferral
//	public void selectBancaReferral(String bancaBranchId) {
//		putParam("BANCABRANCHID", bancaBranchId);
//		PrimeFaces.current().dialog().openDynamic("bancaRefferalDialog", WebUtils.getDialogOption(), null);
//	}
	
	//modify method for selectBancaReferral
	public void selectBancaReferral() {
	
	PrimeFaces.current().dialog().openDynamic("bancaRefferalDialog", WebUtils.getDialogOption(), null);
	}
	
//	old method for selectBancaReferralByOTC
//	public void selectBancaReferralByOTC(String bancaBranchId) {
//		putParam("BANCABRANCHID", bancaBranchId);
//		PrimeFaces.current().dialog().openDynamic("bancaRefferalByOTCDialog", WebUtils.getDialogOption(), null);
//	}
	
	//modify method for selectBancaReferralByOTC
	public void selectBancaReferralByOTC() {
		PrimeFaces.current().dialog().openDynamic("bancaRefferalByOTCDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBancaLIC() {
		PrimeFaces.current().dialog().openDynamic("bancaLICDialog", WebUtils.getDialogOption(), null);
	}

//	public void selectBancaBRM(String bancaBranchId) {
//		putParam("BANCABRANCHID", bancaBranchId);
//		PrimeFaces.current().dialog().openDynamic("bancaBRMDialog", WebUtils.getDialogOption(), null);
//	}

	public void selectBancaBrm() {
		PrimeFaces.current().dialog().openDynamic("bancaBRMDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBancaBranch() {
		PrimeFaces.current().dialog().openDynamic("bancaBranchDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBancaBranchByChannel(String channelId) {
		putParam("CHANNELID", channelId);
		PrimeFaces.current().dialog().openDynamic("bancaBranchDialog", WebUtils.getDialogOption(), null);
	}

	public void selectClassOfInsurance() {
		PrimeFaces.current().dialog().openDynamic("classOfInsuranceDialog", WebUtils.getDialogOption(), null);
	}

	public void selectICD10() {
		PrimeFaces.current().dialog().openDynamic("ICD10Dialog", WebUtils.getDialogOption(), null);
	}

	public void selectCurrency() {
		PrimeFaces.current().dialog().openDynamic("currencyDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCoa() {
		PrimeFaces.current().dialog().openDynamic("coaDialog", WebUtils.getDialogOption(), null);
	}

	public void selectQualification() {
		PrimeFaces.current().dialog().openDynamic("qualificationDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCompany() {
		PrimeFaces.current().dialog().openDynamic("companyDialog", WebUtils.getDialogOption(), null);
	}

	public void selectTypesOfSport() {
		PrimeFaces.current().dialog().openDynamic("typesOfSportDialog", WebUtils.getDialogOption(), null);
	}

	public void selectIndustry() {
		PrimeFaces.current().dialog().openDynamic("industryDialog", WebUtils.getDialogOption(), null);
	}

	public void selectProduct(InsuranceType insuranceType) {
		putParam("INSURANCETYPE", insuranceType);
		PrimeFaces.current().dialog().openDynamic("productDialog", WebUtils.getDialogOption(), null);
	}

	public void selectProcess() {
		PrimeFaces.current().dialog().openDynamic("processDialog", WebUtils.getDialogOption(), null);
	}

	public void selectProduct() {
		PrimeFaces.current().dialog().openDynamic("productDialog", WebUtils.getDialogOption(), null);
	}

	public void selectProductGroup() {
		PrimeFaces.current().dialog().openDynamic("productGroupDialog", WebUtils.getDialogOption(), null);
	}

	public void selectProductGroup(ProductGroupType productGroupType) {
		putParam("PRODUCTGROUPTYPE", productGroupType);
		PrimeFaces.current().dialog().openDynamic("productGroupDialog", WebUtils.getDialogOption(), null);
	}

	public void selectProvince() {
		PrimeFaces.current().dialog().openDynamic("provinceDialog", WebUtils.getDialogOption(), null);
	}

	public void selectRelationShip() {
		PrimeFaces.current().dialog().openDynamic("relationShipDialog", WebUtils.getDialogOption(), null);
	}

	public void selectSchool() {
		PrimeFaces.current().dialog().openDynamic("schoolDialog", WebUtils.getDialogOption(), null);
	}

	public void selectReligion() {
		PrimeFaces.current().dialog().openDynamic("selectReligion", WebUtils.getDialogOption(), null);
	}

	public void selectWorkShop() {
		PrimeFaces.current().dialog().openDynamic("workShopDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCashier() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 500);
		dialogOptions.put("contentWidth", 1000);
		PrimeFaces.current().dialog().openDynamic("cashierDialog", dialogOptions, null);
	}

	public void selectFirePolicy() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 500);
		dialogOptions.put("contentWidth", 1350);
		PrimeFaces.current().dialog().openDynamic("firePolicyDialog", dialogOptions, null);
	}

	public void selectTypeOfBody() {
		PrimeFaces.current().dialog().openDynamic("typeOfBodyDialog", WebUtils.getDialogOption(), null);
	}

	public void selectManufacture() {
		PrimeFaces.current().dialog().openDynamic("manufactureDialog", WebUtils.getDialogOption(), null);
	}

	public void selectFloor() {
		PrimeFaces.current().dialog().openDynamic("floorDialog", WebUtils.getDialogOption(), null);
	}

	public void selectWall() {
		PrimeFaces.current().dialog().openDynamic("wallDialog", WebUtils.getDialogOption(), null);
	}

	public void selectRoof() {
		PrimeFaces.current().dialog().openDynamic("roofDialog", WebUtils.getDialogOption(), null);
	}

	public void selectNatureOfBusiness() {
		PrimeFaces.current().dialog().openDynamic("natureOfBusinessDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBank() {
		PrimeFaces.current().dialog().openDynamic("bankDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBankByBranch() {
		PrimeFaces.current().dialog().openDynamic("bankByBranchDialog", WebUtils.getDialogOption(), null);
	}

	public void selectInterBranchBank() {
		PrimeFaces.current().dialog().openDynamic("interBranchBankDialog", WebUtils.getDialogOption(), null);
	}

	public void selectAccountBank() {
		PrimeFaces.current().dialog().openDynamic("bankDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBankBranch() {
		PrimeFaces.current().dialog().openDynamic("bankBranchDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCity() {
		PrimeFaces.current().dialog().openDynamic("cityDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCountry() {
		PrimeFaces.current().dialog().openDynamic("countryDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCoInsuranceCompany() {
		PrimeFaces.current().dialog().openDynamic("coInsuranceCompanyDialog", WebUtils.getDialogOption(), null);
	}

	public void selectPaymentType() {
		PrimeFaces.current().dialog().openDynamic("paymentTypeDialog", WebUtils.getDialogOption(), null);
	}

	public void selectPaymentType(CustomerType customerType) {
		putParam("CUSTOMERTYPE", customerType);
		PrimeFaces.current().dialog().openDynamic("paymentTypeDialog", WebUtils.getDialogOption(), null);
	}

	public void selectPaymentType(Product product) {
		putParam("PRODUCT", product);
		PrimeFaces.current().dialog().openDynamic("paymentTypeDialog", WebUtils.getDialogOption(), null);
	}

	public void selectPaymentType(Product product, CustomerType customerType) {
		putParam("PRODUCT", product);
		putParam("CUSTOMERTYPE", customerType);
		PrimeFaces.current().dialog().openDynamic("paymentTypeDialog", WebUtils.getDialogOption(), null);
	}

	public void selectClaimType() {
		PrimeFaces.current().dialog().openDynamic("lifeDisabilityClaimTypeDialog", WebUtils.getDialogOption(), null);
	}

	public void selectAddOn(Product product) {
		putParam("PRODUCT", product);
		PrimeFaces.current().dialog().openDynamic("addOnDialog", WebUtils.getDialogOption(), null);
	}

	public void selectAddOn() {
		PrimeFaces.current().dialog().openDynamic("addOnDialog", WebUtils.getDialogOption(), null);
	}

	public void selectOccurrence() {
		PrimeFaces.current().dialog().openDynamic("occurrenceDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBuildingOccupation() {
		PrimeFaces.current().dialog().openDynamic("natureOfBusinessDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBuildingClass() {
		PrimeFaces.current().dialog().openDynamic("buildingClassDialog", WebUtils.getDialogOption(), null);
	}

	public void selectOccupation() {
		PrimeFaces.current().dialog().openDynamic("occupationDialog", WebUtils.getDialogOption(), null);
	}
	
	public void selectRiskyOccupation() {
		PrimeFaces.current().dialog().openDynamic("riskyOccupationDialog", WebUtils.getDialogOption(), null);
	}

	public void selectHospital() {
		PrimeFaces.current().dialog().openDynamic("hospitalDialog", WebUtils.getDialogOption(), null);
	}

	public void selectVehiclePart() {
		PrimeFaces.current().dialog().openDynamic("vehiclePartDialog", WebUtils.getDialogOption(), null);
	}

	public void selectLifePolicyNoByProduct(String productId) {
		putParam("PRODUCTID", productId);
		PrimeFaces.current().dialog().openDynamic("lifePolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectLifePolicyNo() {
		PrimeFaces.current().dialog().openDynamic("lifePolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectLifePolicyNo(String status) {
		putParam("Actived", status);
		PrimeFaces.current().dialog().openDynamic("lifePolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectLifePolicy(String productId) {
		putParam("productId", productId);
		PrimeFaces.current().dialog().openDynamic("lifePolicyDialog", WebUtils.getDialogOption(), null);
	}

	public void selectLifeClaimPolicyNo(String productId) {
		putParam("productId", productId);
		PrimeFaces.current().dialog().openDynamic("lifePolicyDialog", WebUtils.getDialogOption(), null);
	}

	public void selectPublicLifePolicyNo(String status) {
		putParam("Actived", status);
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 500);
		dialogOptions.put("contentWidth", 900);
		PrimeFaces.current().dialog().openDynamic("publicLifePolicyDialog", dialogOptions, null);
	}

	public void selectFirePolicyNo() {
		PrimeFaces.current().dialog().openDynamic("firePolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectDeclarationPolicyNo() {
		PrimeFaces.current().dialog().openDynamic("declarationPolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectSnakeBitePolicyNo() {
		PrimeFaces.current().dialog().openDynamic("snakeBitePolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectCashInSafePolicyNo() {
		PrimeFaces.current().dialog().openDynamic("cashInSafePolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectMotorPolicyNo() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 500);
		dialogOptions.put("contentWidth", 1000);
		PrimeFaces.current().dialog().openDynamic("motorPolicyNoDialog", dialogOptions, null);
	}

	public void selectCargoPolicyNo() {
		Map<String, Object> dialogOptions = new HashMap<String, Object>();
		dialogOptions = new HashMap<String, Object>();
		dialogOptions.put("modal", true);
		dialogOptions.put("draggable", false);
		dialogOptions.put("resizable", false);
		dialogOptions.put("contentHeight", 500);
		dialogOptions.put("contentWidth", 1000);
		PrimeFaces.current().dialog().openDynamic("cargoPolicyNoDialog", dialogOptions, null);
	}

	protected boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		if (value.toString().isEmpty()) {
			return true;
		}
		return false;
	}

	public EnumSet<Language> getLanguages() {
		return EnumSet.allOf(Language.class);
	}

	protected static String getPrimeTheme() {
		if (themeConfig == null) {
			themeConfig = new Properties();
			try {
				themeConfig.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("theme-config.properties"));
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load \"theme-config\"", e);
			}
		}
		return themeConfig.getProperty(THEME).toString();
	}

	private static final String ALGORITHM = "Blowfish";
	private static String keyString = "secretTest";

	public static void encrypt(File inputFile, File outputFile, OutputStream op) throws Exception {
		doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile, op);
		System.out.println("File encrypted successfully!");
	}

	public static void decrypt(File inputFile, File outputFile) throws Exception {
		OutputStream op = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		};
		doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile, op);
		System.out.println("File decrypted successfully!");
	}

	private static void doCrypto(int cipherMode, File inputFile, File outputFile, OutputStream op) throws Exception {

		Key secretKey = new SecretKeySpec(keyString.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(cipherMode, secretKey);

		FileInputStream inputStream = new FileInputStream(inputFile);
		byte[] inputBytes = new byte[(int) inputFile.length()];
		inputStream.read(inputBytes);

		byte[] outputBytes = cipher.doFinal(inputBytes);

		FileOutputStream outputStream = new FileOutputStream(outputFile);
		op.write(outputBytes);
		outputStream.write(outputBytes);
		inputStream.close();
	}

	public void selectStateCode() {
		PrimeFaces.current().dialog().openDynamic("stateCodeDialog", WebUtils.getDialogOption(), null);
	}

	public void selectMedicalPolicyNo(String product) {
		// putParam("PRODUCT", product);
		PrimeFaces.current().dialog().openDynamic("medicalPolicyNoDialog", WebUtils.getDialogOption(), null);
	}

	public void selectMedicalPlace() {
		PrimeFaces.current().dialog().openDynamic("medicalPlaceDialog", WebUtils.getDialogOption(), null);
	}

	public void selectOperation() {
		PrimeFaces.current().dialog().openDynamic("operationDialog", WebUtils.getDialogOption(), null);
	}

	public void selectDisabilityPart(List<DisabilityPart> partList) {
		putParam(Param.DISABILITY_PART_LIST, partList);
		PrimeFaces.current().dialog().openDynamic("disabilityPartDialog", WebUtils.getDialogOption(), null);
	}

	public void createFile(File file, byte[] content) {
		try {
			/* At First : Create directory of target file */
			String filePath = file.getPath();
			int lastIndex = filePath.lastIndexOf("\\") + 1;
			FileUtils.forceMkdir(new File(filePath.substring(0, lastIndex)));

			/* Create target file */
			FileOutputStream outputStream = new FileOutputStream(file);
			IOUtils.write(content, outputStream);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getSystemPath() {
		Object context = getFacesContext().getExternalContext().getContext();
		String systemPath = ((ServletContext) context).getRealPath("/");
		return systemPath;
	}

	public String getUploadPath() {
		return UploadFileConfig.getUploadFilePathHome();
	}

	public String getFileName(String filePath) {
		int lastIndex = filePath.lastIndexOf("/") + 1;
		return filePath.substring(lastIndex, filePath.length());
	}

	public void openMotorProposalInfoTempleate() {
		PrimeFaces.current().dialog().openDynamic("motorProposalInfoTemplate", WebUtils.getTemplateDialogOption(), null);
	}

	public void openMotorClaimInfoTempleate() {
		PrimeFaces.current().dialog().openDynamic("motorClaimInfoTemplate", WebUtils.getTemplateDialogOption(), null);
	}

	public void openLifeClaimInfoTemplate() {
		PrimeFaces.current().dialog().openDynamic("lifeClaimInfoTemplate", WebUtils.getTemplateDialogOption(), null);
	}

	public void openNewLifeProposalInfoTemplate() {
		PrimeFaces.current().dialog().openDynamic("newLifeProposalInfoTemplate", WebUtils.getTemplateDialogOption(), null);
	}
	
	public void openStudentLifeInfoTemplate() {
		PrimeFaces.current().dialog().openDynamic("studentLifeInfoTemplate", WebUtils.getTemplateDialogOption(), null);
	}

	public void openNewLifePolicyInfoTemplate() {
		PrimeFaces.current().dialog().openDynamic("newLifePolicyInfoTemplate", WebUtils.getTemplateDialogOption(), null);
	}

	public void selectSalePointByBranch(String branchId) {
		putParam("BRANCHID", branchId);
		PrimeFaces.current().dialog().openDynamic("salePointDialog", WebUtils.getDialogOption(), null);
	}

	public void selectBranchByEntityIdAndBranchId(String entityId, String branchId) {
		putParam("BRANCHID", branchId);
		putParam("ENTITYID", entityId);
		PrimeFaces.current().dialog().openDynamic("branchDialog", WebUtils.getDialogOption(), null);

	}

	public void selectEntity() {
		PrimeFaces.current().dialog().openDynamic("entityDialog", WebUtils.getDialogOption(), null);
	}
	
	
	public EnumSet<PaymentChannel> getPaymentChannels() {
		EnumSet<PaymentChannel> set = EnumSet.allOf(PaymentChannel.class);
		// set.remove(PaymentChannel.SUNDRY);
		return set;
	}

	public EnumSet<MonthNames> getMonthSet() {
		return EnumSet.allOf(MonthNames.class);
	}

	public EnumSet<MonthType> getMonthTypes() {
		return EnumSet.allOf(MonthType.class);
	}

	public List<Integer> getYearList() {
		List<Integer> years = new ArrayList<Integer>();
		int endYear = Calendar.getInstance().get(Calendar.YEAR);
		for (int startYear = 1999; startYear <= endYear; startYear++) {
			years.add(startYear);
		}
		Collections.reverse(years);
		return years;
	}

	public void deleteTempDir() {
		try {
			FileHandler.forceDelete(getRealSystemPath(), "/temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getRealSystemPath() {
		Object context = getFacesContext().getExternalContext().getContext();
		String systemPath = ((ServletContext) context).getRealPath("/");
		return systemPath;
	}

	public EnumSet<MedicalProductType> getMedicalProductTypes() {
		EnumSet<MedicalProductType> set = EnumSet.allOf(MedicalProductType.class);
		return set;
	}

	public EnumSet<FarmerPolicyType> getFarmerPolicyType() {
		return EnumSet.allOf(FarmerPolicyType.class);
	}

	public String getProductByReferenceType(String product) {
		String result = null;
		if (product == "Individual Critical Illness") {
			result = KeyFactorChecker.getIndividualCriticalIllnessProductId();
		} else if (product == "Group Critical Illness") {
			result = KeyFactorChecker.getGroupCriticalIllnessProductId();
		} else if (product == "Individual Health") {
			result = KeyFactorChecker.getIndividualHealthProductId();
		} else if (product == "Group Health") {
			result = KeyFactorChecker.getGroupHealthProductId();
		} else if (product == "Micro Health") {
			result = KeyFactorChecker.getMicroHealthProductId();
		} else if (product == "Medical") {
			result = KeyFactorChecker.getMedicalProductId();
		}

		return result;
	}

	public void selectDisabilityPartRate(String productId) {
		putParam("produtId", productId);
		PrimeFaces.current().dialog().openDynamic("productDisabiltyPartRate", WebUtils.getDialogOption(), null);
	}

	public EnumSet<SchoolType> getSchoolTypes() {
		return EnumSet.allOf(SchoolType.class);
	}

	public EnumSet<ProposalStatus> getProposalStatus() {
		return EnumSet.allOf(ProposalStatus.class);
	}

	public EnumSet<SchoolLevelType> getSchoolLevelTypes() {
		return EnumSet.allOf(SchoolLevelType.class);
	}
}
