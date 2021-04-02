package org.ace.insurance.web.manage.life.studentLife.proposal;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.SalutationType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuranceHistoryRecord;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.eips.service.interfaces.IEipsService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.gradeinfo.GradeInfo;
import org.ace.insurance.system.common.gradeinfo.service.interfaces.IGradeInfoService;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.percentage.Percentage;
import org.ace.insurance.system.common.percentage.service.interfaces.IPercentageService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.service.interfaces.IRelationShipTypeService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.insurance.system.common.school.School;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "AddNewStudentLifeProposalActionBean")
public class AddNewStudentLifeProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{CustomerService}")
	private ICustomerService customerService;

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@ManagedProperty(value = "#{OccupationService}")
	private IOccupationService occupationService;

	public void setOccupationService(IOccupationService occupationService) {
		this.occupationService = occupationService;
	}

	@ManagedProperty(value = "#{SalePointService}")
	private ISalePointService salePointService;

	public void setSalePointService(ISalePointService salePointService) {
		this.salePointService = salePointService;
	}

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
	}

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
	}

	@ManagedProperty(value = "#{GradeInfoService}")
	private IGradeInfoService gradeInfoService;

	public void setGradeInfoService(IGradeInfoService gradeInfoService) {
		this.gradeInfoService = gradeInfoService;
	}

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaMethodService;

	public void setBancaMethodService(IBancaMethodService bancaMethodService) {
		this.bancaMethodService = bancaMethodService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private LifePolicy lifepolicy;
	private List<RelationShip> relationshipList;
	private List<GradeInfo> gradeInfoList;
	private boolean createNew;
	private String remark;
	private User responsiblePerson;
	private String userType;
	private Product product;
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();
	private List<TownshipCode> fatherTownshipCodeList = new ArrayList<TownshipCode>();
	private List<TownshipCode> motherTownshipCodeList = new ArrayList<TownshipCode>();
	private List<TownshipCode> customerTownshipCodeList = new ArrayList<TownshipCode>();
	private boolean createNewIsuredPersonInfo;
	private InsuranceHistoryRecord insuranceHistoryRecord;
	private Map<String, InsuranceHistoryRecord> insuranceHistoryRecordMap;

	private boolean isEnquiryEdit;
	private boolean isEditProposal;
	private boolean surveyAgain;

	private final String BIRTH_CERTIFICATE_DIR = "/upload/life-proposal/";
	private String temporyDir;
	private Map<String, String> birthCertificateUploadedFileMap;
	private String tempId;
	private boolean isAgeAbove7;
	private boolean editInsuHistRec;
	private int maxTerm;
	private boolean sameCustomerAdderess;
	private BancaassuranceProposal bancaassuranceProposal;
	private List<BancaMethod> bancaMethodList;
	private BancaMethod bancaMethod;

	private String branchId;

	private Entitys entitys;
	
	private List<GGIOrganization> ggiOrganizationList;

	private List<Staff> staffList;

	private List<RelationShipType> relationShipTypeList;

	private Boolean selectItem;

	private GGIOrganization ggiOrganization;

	private Staff staff;

	private RelationShipType relationShipType;

	private Percentage percentage;

	private Eips eips;

	@ManagedProperty(value = "#{GGIOrganizationService}")
	private IGGIOrganizationService ggiOrganizationService;

	@ManagedProperty(value = "#{StaffService}")
	private IStaffService staffService;

	public IStaffService getStaffService() {
		return staffService;
	}

	public void setStaffService(IStaffService staffService) {
		this.staffService = staffService;
	}

	@ManagedProperty(value = "#{RelationShipTypeService}")
	private IRelationShipTypeService relationShipTypeService;

	@ManagedProperty(value = "#{PercentageService}")
	private IPercentageService percentageService;

	@ManagedProperty(value = "#{EipsService}")
	private IEipsService eipsService;


	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
		lifepolicy = (LifePolicy) getParam("lifePolicy");
		product = (Product) getParam("PRODUCT");
		bancaMethod = new BancaMethod();
		bancaassuranceProposal = new BancaassuranceProposal();
		bancaMethodList = bancaMethodService.findAllBanca();
		if (isExistParam("editLifeProposal")) {
			isEditProposal = true;
			lifeProposal = (LifeProposal) getParam("lifeProposal");
			bancaassuranceProposal = (BancaassuranceProposal) getParam("bancaassuranceProposal");
			userType = lifeProposal.getUserType();
		} else if (isExistParam("enquiryEditLifeProposal")) {
			isEnquiryEdit = true;
			lifeProposal = (LifeProposal) getParam("lifeProposal");
			bancaassuranceProposal = (BancaassuranceProposal) getParam("bancaassuranceProposal");
			userType = lifeProposal.getUserType();
		}
		if (product == null) {
			product = lifeProposal == null ? null : lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			putParam("PRODUCT", product);
		}
		maxTerm = product.getMaxTerm();
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifePolicy");
		removeParam("lifeProposal");
		removeParam("editLifeProposal");
		removeParam("enquiryEditLifeProposal");
		removeParam("isNew");
	}

	@PostConstruct
	public void init() {
		
		eips = new Eips();
		editInsuHistRec = false;
		ggiOrganizationList = ggiOrganizationService.findAllGGIOrganization();
		relationShipTypeList = relationShipTypeService.findAllRelationShipType();

		//relationshipList = relationShipService.findAllRelationShip();
		editInsuHistRec = false;
		insuranceHistoryRecord = new InsuranceHistoryRecord();
		// entitys = new Entitys();
		insuranceHistoryRecordMap = new HashMap<String, InsuranceHistoryRecord>();
		surveyAgain = true;
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		initializeInjection();
		insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
		createNewInsuredPersonInfo();
		tempId = insuredPersonInfoDTO.getTempId();
		if (lifeProposal == null) {
			createNewLifeProposal();
		}
		if (isEditProposal || isEnquiryEdit) {
			List<InsuredPersonInfoDTO> insuredPersonDTOList = new ArrayList<InsuredPersonInfoDTO>();
			insuredPersonDTOList.add(new InsuredPersonInfoDTO(lifeProposal.getProposalInsuredPersonList().get(0)));

			if (lifeProposal.getProposalInsuredPersonList().get(0).getBirthCertificateAttachment().size() > 0) {
				String srcPath = getUploadPath() + BIRTH_CERTIFICATE_DIR + lifeProposal.getId();
				String destPath = getUploadPath() + temporyDir + lifeProposal.getId() + "/";
				try {
					FileHandler.copyDirectory(srcPath, destPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			lifeProposal.getCustomer().setIdType(lifeProposal.getCustomer().getIdType());
			for (InsuredPersonInfoDTO person : insuredPersonDTOList) {
				insuredPersonInfoDTOMap.put(person.getTempId(), person);
			}
		}
		relationshipList = relationShipService.findAllRelationShip();
		gradeInfoList = gradeInfoService.findAllGradeInfo();
		stateCodeList = stateCodeService.findAllStateCode();
	}

	public void changeGender() {
		RelationShip relationShip = new RelationShip();
		if (insuredPersonInfoDTO.getGender().equals(Gender.MALE)) {
			relationShip = relationShipService.findRelationShipById(KeyFactorChecker.getSonfromRelationShipTable());
			insuredPersonInfoDTO.setRelationShip(relationShip);
		} else {
			relationShip = relationShipService.findRelationShipById(KeyFactorChecker.getDaughterfromRelationShipTable());
			insuredPersonInfoDTO.setRelationShip(relationShip);
		}
	}

	public void changeFatherStateCode() {
		String stateCode;
		if (lifeProposal.getCustomer().getGender().equals(Gender.MALE) && lifeProposal.getCustomer().getStateCode() != null) {
			stateCode = lifeProposal.getCustomer().getStateCode().getCodeNo();
		} else {
			stateCode = insuredPersonInfoDTO.getParentProvinceCode();
		}
		fatherTownshipCodeList = townshipCodeService.findByStateCodeNo(stateCode);
	}

	public void changeMotherStateCode() {
		String stateCode;
		if (lifeProposal.getCustomer().getGender().equals(Gender.FEMALE) && lifeProposal.getCustomer().getStateCode() != null) {
			stateCode = lifeProposal.getCustomer().getStateCode().getCodeNo();
		} else {
			stateCode = insuredPersonInfoDTO.getParentProvinceCode();
		}
		motherTownshipCodeList = townshipCodeService.findByStateCodeNo(stateCode);
	}

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCodeNo(insuredPersonInfoDTO.getProvinceCode());
	}

	public void changeCustomerStateCodeList() {
		if (lifeProposal.getCustomer().getStateCode() != null) {
			customerTownshipCodeList = townshipCodeService.findByStateCodeNo(lifeProposal.getCustomer().getStateCode().getCodeNo());
		}
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public List<IdType> getParentIdTypes() {
		return Arrays.asList(IdType.NRCNO, IdType.FRCNO, IdType.PASSPORTNO);
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public IdConditionType[] getIdConditionType() {
		return IdConditionType.values();
	}

	private Map<String, InsuredPersonInfoDTO> insuredPersonInfoDTOMap;
	private InsuredPersonInfoDTO insuredPersonInfoDTO;
	private Boolean isEdit = false;

	private void createNewInsuredPersonInfo() {
		birthCertificateUploadedFileMap = new HashMap<String, String>();
		createNewIsuredPersonInfo = true;
		insuranceHistoryRecord = new InsuranceHistoryRecord();
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		insuredPersonInfoDTO.setStartDate(new Date());
		insuredPersonInfoDTO.setProduct(product);
		isEdit = false;
		insuranceHistoryRecordMap = new HashMap<String, InsuranceHistoryRecord>();
	}

	public InsuredPersonInfoDTO getInsuredPersonInfoDTO() {
		return insuredPersonInfoDTO;
	}

	public List<InsuredPersonInfoDTO> getInsuredPersonInfoDTOList() {
		List<InsuredPersonInfoDTO> insuDTOList = new ArrayList<InsuredPersonInfoDTO>();
		if (insuredPersonInfoDTOMap == null || insuredPersonInfoDTOMap.values() == null) {
			return new ArrayList<InsuredPersonInfoDTO>();
		} else {
			for (InsuredPersonInfoDTO dto : insuredPersonInfoDTOMap.values()) {
				if (dto.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
					insuDTOList.add(dto);
				}
			}
		}
		return insuDTOList;
	}

	public void saveInsuredPersonInfo() {
		insuredPersonInfoDTO.setFullIdNo();
		if (insuredPersonInfoDTO.getParentIdNo() != null && !insuredPersonInfoDTO.getParentIdNo().isEmpty()) {
			insuredPersonInfoDTO.setParentFullIdNo();
		}
		if (validateInsuredPersonInfo()) {
			Calendar cal = Calendar.getInstance();
			if (insuredPersonInfoDTO.getStartDate() == null) {
				insuredPersonInfoDTO.setStartDate(new Date());
			}
			insuredPersonInfoDTO.setInsuranceHistoryRecord(getInsuranceHistoryRecordList());
			cal.setTime(insuredPersonInfoDTO.getStartDate());
			cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
			insuredPersonInfoDTO.setEndDate(cal.getTime());
			insuredPersonInfoDTO.setBirthCertificateAttachments(new ArrayList<Attachment>());
			if (lifeProposal.getId() != null) {
				for (String fileName : birthCertificateUploadedFileMap.keySet()) {
					String filePath = temporyDir + lifeProposal.getId() + "/" + lifeProposal.getProposalInsuredPersonList().get(0).getId() + "/Birth_Certificate/" + fileName;
					insuredPersonInfoDTO.addBirthCertificateAttachment(new Attachment(fileName, filePath));
				}
			} else {
				for (String fileName : birthCertificateUploadedFileMap.keySet()) {
					String filePath = temporyDir + tempId + "/" + fileName;
					insuredPersonInfoDTO.addBirthCertificateAttachment(new Attachment(fileName, filePath));
				}
			}
			insuredPersonInfoDTO.setAge(insuredPersonInfoDTO.getAgeForNextYear());
			setKeyFactorValue();
			insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			birthCertificateUploadedFileMap = new HashMap<String, String>();
			createNewInsuredPersonInfo();
			sameCustomerAdderess = false;
			if (createNewIsuredPersonInfo) {
				PrimeFaces.current().executeScript("PF('wiz').nextNav.show(); PF('wiz').cfg.showNavBar = true;");
			}
		}
	}

	public void prepareEditInsuredPersonInfo(InsuredPersonInfoDTO insuDTO) {
		String filePath = null;

		this.insuredPersonInfoDTO = new InsuredPersonInfoDTO(insuDTO);
		if (insuredPersonInfoDTO.getResidentAddress().getTownship().getId().equals(lifeProposal.getCustomer().getResidentAddress().getTownship().getId())
				&& insuredPersonInfoDTO.getResidentAddress().getResidentAddress().equalsIgnoreCase(lifeProposal.getCustomer().getResidentAddress().getResidentAddress())) {
			sameCustomerAdderess = true;
		}
		if (insuredPersonInfoDTO.getBirthCertificateAttachments().size() > 0) {
			for (Attachment att : insuredPersonInfoDTO.getBirthCertificateAttachments()) {
				filePath = att.getFilePath();
				filePath = filePath.replaceAll("/upload/life-proposal/", temporyDir);
				att.setFilePath(filePath);
				birthCertificateUploadedFileMap.put(att.getName(), att.getFilePath());
			}
		}
		for (InsuranceHistoryRecord i : insuredPersonInfoDTO.getInsuranceHistoryRecord()) {
			insuranceHistoryRecordMap.put(i.getTempId(), i);
		}
		birthCertificateUploadedFileMap.clear();
		this.insuredPersonInfoDTO.loadFullIdNo();
		if (insuredPersonInfoDTO.getParentFullIdNo() != null) {
			this.insuredPersonInfoDTO.loadParentFullIdNo();
		}
		changeStateCode();
		changeFatherStateCode();
		changeMotherStateCode();
		changeChildDateOfBirth();
		for (Attachment a : this.insuredPersonInfoDTO.getBirthCertificateAttachments()) {
			birthCertificateUploadedFileMap.put(a.getName(), a.getFilePath());
		}
		createNewIsuredPersonInfo = false;
		isEdit = true;
		if (!createNewIsuredPersonInfo) {
			PrimeFaces.current().executeScript("PF('wiz').nextNav.hide();");
		}
	}

	public void saveInsuranceHistoryRecord() {
		insuranceHistoryRecord.setProduct(product);
		insuranceHistoryRecordMap.put(insuranceHistoryRecord.getTempId(), insuranceHistoryRecord);
		resetInsuranceHistoryRecord();
	}

	public void removeInsuranceHistoryRecord(InsuranceHistoryRecord insuranceHistoryRecord) {
		insuranceHistoryRecordMap.remove(insuranceHistoryRecord.getTempId());
	}

	public void editInsuranceHistoryRecord(InsuranceHistoryRecord record) {
		this.insuranceHistoryRecord = record;
		editInsuHistRec = true;
	}

	public void resetInsuranceHistoryRecord() {
		insuranceHistoryRecord = new InsuranceHistoryRecord();
		editInsuHistRec = false;
	}

	public List<InsuranceHistoryRecord> getInsuranceHistoryRecordList() {
		List<InsuranceHistoryRecord> insuDTOList = new ArrayList<InsuranceHistoryRecord>();
		if (insuranceHistoryRecordMap == null || insuranceHistoryRecordMap.values() == null) {
			return new ArrayList<InsuranceHistoryRecord>();
		} else {
			for (InsuranceHistoryRecord dto : insuranceHistoryRecordMap.values()) {
				insuDTOList.add(dto);
			}
		}
		return insuDTOList;
	}

	public boolean validateInsuredPersonInfo() {
		boolean valid = true;
		if (birthCertificateUploadedFileMap.isEmpty() && insuredPersonInfoDTO.getIdType().equals(IdType.STILL_APPLYING)) {
			addErrorMessage("studentLifeProposalEntryForm:birthCertificateImagePanel", MessageId.BIRTH_CERTIFICATION_ATTACH_IS_REQUIRED);
			valid = false;
		}
		boolean overMaxSI = lifeProposalService.findOverMaxSIByMotherNameAndNRCAndInsuNameAndNRC(lifeProposal, insuredPersonInfoDTO);
		if (overMaxSI) {
			addErrorMessage("studentLifeProposalEntryForm:sI", MessageId.TOTAL_SUMINSURED_OF_CHILD_MUST_BE_BETWEEN_10LAKH_1000LAKH);
			if (valid) {
				valid = false;
			}
		}
		if (insuredPersonInfoDTO.getDateOfBirth().after(new Date())) {
			addErrorMessage("studentLifeProposalEntryForm:dateOfBirth", MessageId.INVALID_INSURED_DOB);
			valid = false;
		} else {
			long childAgeByDay = (new Date().getTime() - insuredPersonInfoDTO.getDateOfBirth().getTime()) / (1000 * 60 * 60 * 24);
			if (insuredPersonInfoDTO.getAgeForNextYear() > 12 || childAgeByDay < 30) {
				addErrorMessage("studentLifeProposalEntryForm:dateOfBirth", MessageId.CHILD_AGE_MUST_BE_BETWEEN_30DAYS_AND_12YEARS);
				if (valid) {
					valid = false;
				}
			}
		}
		if (insuredPersonInfoDTO.getParentDOB() != null && insuredPersonInfoDTO.getParentDOB().after(insuredPersonInfoDTO.getDateOfBirth())) {
			if (lifeProposal.getCustomer().getGender().equals(Gender.MALE)) {
				addErrorMessage("studentLifeProposalEntryForm:parentDOB", MessageId.INVALID_MOTHER_DOB);
			} else {
				addErrorMessage("studentLifeProposalEntryForm:cusDOB", MessageId.INVALID_FATHER_DOB);
			}
			valid = false;
		}
		if (lifeProposal.getCustomer().getFullIdNo() != null && insuredPersonInfoDTO.getFullIdNo() != null
				&& lifeProposal.getCustomer().getFullIdNo().equals(insuredPersonInfoDTO.getFullIdNo())) {
			addErrorMessage("studentLifeProposalEntryForm:nrcNo", MessageId.IDNO_MUST_NOT_BE_DUPLICATE);
			if (lifeProposal.getCustomer().getGender().equals(Gender.MALE)) {
				addErrorMessage("studentLifeProposalEntryForm:customerNrcNo", MessageId.IDNO_MUST_NOT_BE_DUPLICATE);
			} else {
				addErrorMessage("studentLifeProposalEntryForm:motherNrcNo", MessageId.IDNO_MUST_NOT_BE_DUPLICATE);
			}
			if (valid) {
				valid = false;
			}
		}
		if (lifeProposal.getCustomer().getFullIdNo() != null && insuredPersonInfoDTO.getParentFullIdNo() != null
				&& lifeProposal.getCustomer().getFullIdNo().equals(insuredPersonInfoDTO.getParentFullIdNo())) {
			addErrorMessage("studentLifeProposalEntryForm:customerNrcNo", MessageId.IDNO_MUST_NOT_BE_DUPLICATE);
			addErrorMessage("studentLifeProposalEntryForm:motherNrcNo", MessageId.IDNO_MUST_NOT_BE_DUPLICATE);
			if (valid) {
				valid = false;
			}
		}
		if (insuredPersonInfoDTO.getParentFullIdNo() != null && insuredPersonInfoDTO.getFullIdNo() != null
				&& insuredPersonInfoDTO.getFullIdNo().equals(insuredPersonInfoDTO.getParentFullIdNo())) {
			addErrorMessage("studentLifeProposalEntryForm:nrcNo", MessageId.IDNO_MUST_NOT_BE_DUPLICATE);
			if (lifeProposal.getCustomer().getGender().equals(Gender.MALE)) {
				addErrorMessage("studentLifeProposalEntryForm:motherNrcNo", MessageId.IDNO_MUST_NOT_BE_DUPLICATE);
			} else {
				addErrorMessage("studentLifeProposalEntryForm:customerNrcNo", MessageId.IDNO_MUST_NOT_BE_DUPLICATE);
			}
			if (valid) {
				valid = false;
			}
		}

		if (insuredPersonInfoDTO.getResidentAddress().getTownship() == null) {
			valid = false;
			addErrorMessage("studentLifeProposalEntryForm:townShip", MessageId.REQUIRED_VALUES);

		}

		return valid;
	}

	public void removeInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		insuredPersonInfoDTOMap.remove(insuredPersonInfoDTO.getTempId());
		createNewInsuredPersonInfo();
	}

	public List<ProposalInsuredPerson> getInsuredPersonInfoList() {
		List<ProposalInsuredPerson> result = new ArrayList<ProposalInsuredPerson>();
		if (insuredPersonInfoDTOMap.values() != null) {
			for (InsuredPersonInfoDTO insuredPersonInfoDTO : insuredPersonInfoDTOMap.values()) {
				ClassificationOfHealth classificationOfHealth = insuredPersonInfoDTO.getClassificationOfHealth();
				ProposalInsuredPerson proposalInsuredPerson = new ProposalInsuredPerson(insuredPersonInfoDTO, lifeProposal);
				proposalInsuredPerson.setInsuredPersonAddOnList(insuredPersonInfoDTO.getInsuredPersonAddOnList(proposalInsuredPerson));
				proposalInsuredPerson.setClsOfHealth(classificationOfHealth);
				proposalInsuredPerson.setTypesOfSport(insuredPersonInfoDTO.getTypesOfSport());
				proposalInsuredPerson.setUnit(insuredPersonInfoDTO.getUnit());

				List<InsuranceHistoryRecord> historyRecordList = insuredPersonInfoDTO.getInsuranceHistoryRecord();
				if (historyRecordList != null) {
					for (InsuranceHistoryRecord record : historyRecordList) {
						proposalInsuredPerson.addInsuranceHistoryRecord(record);
					}
				}
				List<InsuredPersonAttachment> insuredPersonAttachments = insuredPersonInfoDTO.getPerAttachmentList();
				if (insuredPersonAttachments != null) {
					for (InsuredPersonAttachment attachment : insuredPersonAttachments) {
						proposalInsuredPerson.addAttachment(attachment);
					}
				}
				List<Attachment> birthCertificateAttachments = insuredPersonInfoDTO.getBirthCertificateAttachments();
				if (birthCertificateAttachments != null) {
					for (Attachment attachment : birthCertificateAttachments) {
						proposalInsuredPerson.addBirthCertificateAttachment(attachment);
					}
				}
				for (InsuredPersonKeyFactorValue kfv : insuredPersonInfoDTO.getKeyFactorValueList(proposalInsuredPerson)) {
					if (KeyFactorChecker.isPaymentType(kfv.getKeyFactor())) {
						kfv.setValue(lifeProposal.getPaymentType().getId() + "");
					}
				}
				List<SurveyQuestionAnswer> answerList = insuredPersonInfoDTO.getSurveyQuestionAnswerList();
				if (historyRecordList != null) {
					for (SurveyQuestionAnswer answer : answerList) {
						proposalInsuredPerson.addSurveyQuestionAnswer(answer);
					}
				}
				proposalInsuredPerson.setKeyFactorValueList(insuredPersonInfoDTO.getKeyFactorValueList(proposalInsuredPerson));
				result.add(proposalInsuredPerson);
			}
		}
		return result;
	}

	public void changeChildDateOfBirth() {
		if (insuredPersonInfoDTO.getDateOfBirth() != null) {
			if (insuredPersonInfoDTO.getAgeForNextYear() > 7)
				isAgeAbove7 = true;
			else
				isAgeAbove7 = false;
			insuredPersonInfoDTO.setPeriodOfYears(maxTerm - insuredPersonInfoDTO.getAgeForNextYear() + 1);
		}
	}

	public SaleChannelType[] getSaleChannelType() {
		return SaleChannelType.values();
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (lifeProposal.getSaleChannelType().equals(SaleChannelType.AGENT)) {
			lifeProposal.setSaleMan(null);
			lifeProposal.setReferral(null);
		} else if (lifeProposal.getSaleChannelType().equals(SaleChannelType.SALEMAN)) {
			lifeProposal.setAgent(null);
			lifeProposal.setReferral(null);
		} else if (lifeProposal.getSaleChannelType().equals(SaleChannelType.REFERRAL)) {
			lifeProposal.setSaleMan(null);
			lifeProposal.setAgent(null);
		} else if (lifeProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
			lifeProposal.setSaleMan(null);
			lifeProposal.setAgent(null);
			lifeProposal.setReferral(null);
			bancaassuranceProposal = new BancaassuranceProposal();
		}
	}

	public void changeBancaEvent(AjaxBehaviorEvent event) {

		bancaassuranceProposal.setBancaBRM(null);
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);
	}

	public void createNewLifeProposal() {
		createNew = true;
		lifeProposal = new LifeProposal();
		resetCustomer();
		lifeProposal.setSubmittedDate(new Date());
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public boolean isDisableInsureInfo() {
		return getInsuredPersonInfoDTOList().size() > 0 && createNewIsuredPersonInfo;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("proposalTab".equals(event.getOldStep()) && "customerTab".equals(event.getNewStep())) {
			if (lifeProposal != null && lifeProposal.getCustomer().getStateCode() != null) {
				changeCustomerStateCodeList();
			}
		} else if ("customerTab".equals(event.getOldStep()) && "InsuredPersonInfo".equals(event.getNewStep())) {
			// if (lifeProposal.getCustomer().getResidentAddress().getTownship()
			// == null) {
			// lifeProposal.getCustomer().getResidentAddress()
			// .setTownship(lifeProposal.getCustomer().getPermanentAddress().getTownship());
			// }
			// if
			// (lifeProposal.getCustomer().getResidentAddress().getResidentAddress()
			// == null
			// ||
			// lifeProposal.getCustomer().getResidentAddress().getResidentAddress().isEmpty())
			// {
			// lifeProposal.getCustomer().getResidentAddress()
			// .setResidentAddress(lifeProposal.getCustomer().getPermanentAddress().getPermanentAddress());
			// }

			Customer customer = lifeProposal.getCustomer();

			int customerAge = customer.getAgeForNextYear();
			if (customerAge <= 18 || customerAge >= 55) {
				addErrorMessage("studentLifeProposalEntryForm:customerRegdob", MessageId.CUSTOMER_AGE_MUST_BE_BETWEEN, 18, 55);
				valid = false;
			}
			if (customer.getId() == null) {
				boolean isExit = true;
				isExit = customerService.checkExistingCustomer(customer);
				if (isExit) {
					String param = customer.getFullName();
					if (customer.getIdType() != IdType.STILL_APPLYING) {
						param += " (or) " + customer.getFullIdNo();
					}
					addErrorMessage("studentLifeProposalEntryForm:customerRegcustomer", MessageId.EXISTING_CUSTOMER_PLZ_SELECT, param);
					valid = false;
				}
			}
			if (valid) {
				if (!customer.getIdType().equals(IdType.STILL_APPLYING)) {
					changeFatherStateCode();
					changeMotherStateCode();
					lifeProposal.getCustomer().setFullIdNo(lifeProposal.getCustomer().getFullIdNo());
				}
				changeStateCode();
			}
			PrimeFaces.current().resetInputs(":studentLifeProposalEntryForm:InsuredPersonInfo");
		} else if ("InsuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
			if (getInsuredPersonInfoDTOList().size() < 1) {
				addErrorMessage("studentLifeProposalEntryForm:insuredPersonInfoTable", MessageId.REQUIRED_INSURED_PERSION);
				valid = false;
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public String addNewStudentLifeProposal() {
		String result = null;
//		lifeProposal.setStaffPlan(selectItem);
//		if (selectItem == true) {
//			saveEipsData();
//			lifeProposal.setEips(eips);
//		}
		try {
			lifeProposal.setInsuredPersonList(getInsuredPersonInfoList());
			WorkflowReferenceType referenceType = WorkflowReferenceType.STUDENT_LIFE_PROPOSAL;
			WorkflowTask workflowTask = null;
			WorkFlowDTO workFlowDTO = null;
			ExternalContext extContext = getFacesContext().getExternalContext();
			if (isEditProposal) {
				String filePath = null;
				for (Attachment a : lifeProposal.getProposalInsuredPersonList().get(0).getBirthCertificateAttachment()) {
					filePath = BIRTH_CERTIFICATE_DIR + lifeProposal.getId() + "/" + lifeProposal.getProposalInsuredPersonList().get(0).getId() + "/Birth_Certificate" + "/"
							+ a.getName();
					a.setFilePath(filePath);
				}
				workflowTask = surveyAgain ? WorkflowTask.SURVEY : WorkflowTask.APPROVAL;
				workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
				lifeProposal = lifeProposalService.updateLifeProposal(lifeProposal, workFlowDTO, bancaassuranceProposal);
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.EDIT_PROPOSAL_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			} else if (isEnquiryEdit) {
				String filePath = null;
				for (Attachment a : lifeProposal.getProposalInsuredPersonList().get(0).getBirthCertificateAttachment()) {
					filePath = BIRTH_CERTIFICATE_DIR + lifeProposal.getId() + "/" + lifeProposal.getProposalInsuredPersonList().get(0).getId() + "/Birth_Certificate" + "/"
							+ a.getName();
					a.setFilePath(filePath);
				}
				workflowTask = surveyAgain ? WorkflowTask.SURVEY : WorkflowTask.APPROVAL;
				workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
				lifeProposal = lifeProposalService.updateLifeProposal(lifeProposal, workFlowDTO, bancaassuranceProposal);
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.EDIT_PROPOSAL_PROCESS_SUCCESS);
			} else {
				workflowTask = WorkflowTask.SURVEY;
				workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
				lifeProposal.setProposalType(ProposalType.UNDERWRITING);
				lifeProposal = lifeProposalService.addNewLifeProposal(lifeProposal, workFlowDTO, RequestStatus.PROPOSED.name(), bancaassuranceProposal);
				
//				if (selectItem == true) {
//					eips.setAmount(calculateEipsAmount());
//					eipsService.save(eips);
//					}
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			}
			if (lifeProposal.getProposalInsuredPersonList().get(0).getBirthCertificateAttachment().size() > 0) {
				moveUploadedFiles();
			}
			createNewLifeProposal();
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private void moveUploadedFiles() {
		try {
			if (isEnquiryEdit || isEditProposal) {
				FileHandler.moveFiles(getUploadPath(), temporyDir + lifeProposal.getId() + "/" + lifeProposal.getProposalInsuredPersonList().get(0).getId() + "/Birth_Certificate",
						BIRTH_CERTIFICATE_DIR + lifeProposal.getId() + "/" + lifeProposal.getProposalInsuredPersonList().get(0).getId() + "/Birth_Certificate");
			} else {
				FileHandler.moveFiles(getUploadPath(), temporyDir + tempId,
						BIRTH_CERTIFICATE_DIR + lifeProposal.getId() + "/" + lifeProposal.getProposalInsuredPersonList().get(0).getId() + "/Birth_Certificate");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleProposalAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");

		if (!birthCertificateUploadedFileMap.containsKey(fileName)) {
			String filePath = null;
			if (lifeProposal.getId() != null) {
				filePath = temporyDir + lifeProposal.getId() + "/" + lifeProposal.getProposalInsuredPersonList().get(0).getId() + "/Birth_Certificate/" + fileName;
			} else {
				filePath = temporyDir + tempId + "/" + fileName;
			}

			birthCertificateUploadedFileMap.put(fileName, filePath);
			try {
				String physicalPath = getUploadPath() + filePath;

				FileHandler.createFile(new File(physicalPath), uploadedFile.getContents());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getBirthCertificateUploadedFileList() {
		return new ArrayList<String>(birthCertificateUploadedFileMap.values());
	}

	public void removeBirthCertificateUploadedFile(String filePath) {
		try {
			String fileName = FileHandler.getFileName(filePath);
			birthCertificateUploadedFileMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			if (birthCertificateUploadedFileMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + temporyDir));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public void changeParentIdType() {
		checkIdType("P");
		PrimeFaces.current().resetInputs("studentLifeProposalEntryForm:motherIdNo");
	}

	public void changeInsuIdType() {
		checkIdType("I");
	}

	public void changeCusIdType() {
		checkIdType("C");
	}

	public void checkIdType(String customerType) {
		if ("P".equals(customerType)) {
			insuredPersonInfoDTO.setParentProvinceCode(null);
			insuredPersonInfoDTO.setParentTownshipCode(null);
			insuredPersonInfoDTO.setParentIdNo(null);
			insuredPersonInfoDTO.setParentIdConditionType(null);
			insuredPersonInfoDTO.setParentFullIdNo(null);
		} else if ("C".equals(customerType)) {
			lifeProposal.getCustomer().setStateCode(null);
			lifeProposal.getCustomer().setTownshipCode(null);
			lifeProposal.getCustomer().setIdNo(null);
			lifeProposal.getCustomer().setIdConditionType(null);
			lifeProposal.getCustomer().setFullIdNo(null);
		} else {
			insuredPersonInfoDTO.setProvinceCode(null);
			insuredPersonInfoDTO.setTownshipCode(null);
			insuredPersonInfoDTO.setIdNo(null);
			insuredPersonInfoDTO.setIdConditionType(null);
			insuredPersonInfoDTO.setFullIdNo(null);
		}
	}

	public int getAgeForOldYear(Date dob) {
		Calendar cal_1 = Calendar.getInstance();
		cal_1.setTime(lifepolicy.getCommenmanceDate());
		int currentYear = cal_1.get(Calendar.YEAR);

		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(dob);
		cal_2.set(Calendar.YEAR, currentYear);
		if (lifepolicy.getCommenmanceDate().after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(dob);
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(dob);
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}

	private void setKeyFactorValue() {
		for (InsuredPersonKeyFactorValue vehKF : insuredPersonInfoDTO.getKeyFactorValueList()) {
			KeyFactor kf = vehKF.getKeyFactor();
			if (KeyFactorChecker.isSumInsured(kf)) {
				vehKF.setValue(insuredPersonInfoDTO.getSumInsuredInfo() + "");
			} else if (KeyFactorChecker.isPaymentType(kf)) {
				vehKF.setValue(lifeProposal.getPaymentType().getId() + "");
			} else if (KeyFactorChecker.isAge(kf)) {
				vehKF.setValue(insuredPersonInfoDTO.getAge() + "");
			} else if (KeyFactorChecker.isTerm(kf)) {
				vehKF.setValue(insuredPersonInfoDTO.getPeriodOfYears() + "");
			}
		}

	}

	public MaritalStatus[] getMaritalStatus() {
		return MaritalStatus.values();
	}

	public void selectUser() {
		WorkFlowType workFlowType = WorkFlowType.STUDENT_LIFE;
		WorkflowTask workflowTask = WorkflowTask.SURVEY;
		selectUser(workflowTask, workFlowType);
	}

	public void selectSalePoint() {

		selectSalePointByBranch(lifeProposal.getBranch() == null ? "" : lifeProposal.getBranch().getId());
	}

//	public void selectBancaBrm() {
//		selectBancaBRM(bancaassuranceProposal.getBancaBranch().getId());
//	}

	public void selectBancaBranch() {
		selectBancaBranchByChannel(bancaassuranceProposal.getChannel() == null ? "" : bancaassuranceProposal.getChannel().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

//	public void selectBancaReferralByOTC() {
//		selectBancaReferralByOTC(bancaassuranceProposal.getBancaBranch().getId());
//	}
//
//	public void selectBancaReferral() {
//		selectBancaReferral(bancaassuranceProposal.getBancaBranch().getId());
//	}

	public void removeBranch() {
		lifeProposal.setBranch(null);
		lifeProposal.setSalePoint(null);
	}

	public void removeChannel() {
		bancaassuranceProposal.setChannel(null);
		bancaassuranceProposal.setBancaBranch(null);
		bancaassuranceProposal.setBancaMethod(null);

	}

	public void removeBancaBranch() {
		bancaassuranceProposal.setBancaBranch(null);
		bancaassuranceProposal.setBancaMethod(null);

	}

	public void removeBancaBRM() {
		bancaassuranceProposal.setBancaBRM(null);
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);

	}

	public void removeBancaLIC() {
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);

	}

	public void removeBancaReferral() {
		bancaassuranceProposal.setBancaRefferal(null);

	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		bancaassuranceProposal.setChannel(channel);
		bancaassuranceProposal.setBancaBranch(null);
		bancaassuranceProposal.setBancaMethod(null);
	}

	public void returnBancaRefferal(SelectEvent event) {
		BancaRefferal bancaRefferal = (BancaRefferal) event.getObject();
		bancaassuranceProposal.setBancaRefferal(bancaRefferal);
	}

	public void returnBancaBrm(SelectEvent event) {
		BancaBRM bancaBrm = (BancaBRM) event.getObject();
		bancaassuranceProposal.setBancaBRM(bancaBrm);
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		bancaassuranceProposal.setBancaBranch(bancaBranch);
	}

	public void returnBancaLIC(SelectEvent event) {
		BancaLIC bancaLIC = (BancaLIC) event.getObject();
		bancaassuranceProposal.setBancaLIC(bancaLIC);
	}

	public void returnBancaBRM(SelectEvent event) {
		BancaBRM bancaBRM = (BancaBRM) event.getObject();
		bancaassuranceProposal.setBancaBRM(bancaBRM);
	}

	public List<PaymentType> getPaymentTypes() {
		if (product == null) {
			return new ArrayList<PaymentType>();
		} else {
			return product.getPaymentTypeList();
		}
	}

	public int getMaximumTerm() {
		int result = 0;
		if (product != null) {
			result = product.getMaxTerm();
		}
		return result;
	}

	public int getMinimumTerm() {
		int result = 0;
		if (product != null) {
			result = product.getMinTerm();
		}
		return result;
	}

	public double getMaximumSI() {
		double result = 0;
		if (product != null) {
			result = product.getMaxSumInsured();
		}
		return result;
	}

	public void resetCustomer() {
		Customer customer = new Customer();
		lifeProposal.setCustomer(customer);
	}

	public double getMinimumSI() {
		double result = 0;
		if (product != null) {
			result = product.getMinSumInsured();
		}
		return result;
	}

	public void checkCustomerSameAddress() {
		if (sameCustomerAdderess) {
			insuredPersonInfoDTO.getResidentAddress().setTownship(lifeProposal.getCustomer().getResidentAddress().getTownship());
			insuredPersonInfoDTO.getResidentAddress().setResidentAddress(lifeProposal.getCustomer().getResidentAddress().getResidentAddress());
		} else {
			insuredPersonInfoDTO.getResidentAddress().setTownship(null);
			insuredPersonInfoDTO.getResidentAddress().setResidentAddress(null);
		}
	}

	public SalutationType[] getSalutationTypes() {
		return SalutationType.values();
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		customer.setInitialId(customer.getInitialId().replaceAll(" ", ""));
		lifeProposal.setCustomer(customer);
		changeCustomerStateCodeList();
	}

	public void returnNationality(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		lifeProposal.getCustomer().setCountry(nationality);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		lifeProposal.setPaymentType(paymentType);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		lifeProposal.setSalePoint(salePoint);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		lifeProposal.setAgent(agent);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		lifeProposal.setSaleMan(saleMan);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		lifeProposal.setReferral(referral);
	}

	/*
	 * public void returnChannel(SelectEvent event) { Customer referral =
	 * (Customer) event.getObject(); lifeProposal.setReferral(referral); }
	 */

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		lifeProposal.setBranch(branch);
		lifeProposal.setSalePoint(null);
	}

	public void returnInsuredPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		insuredPersonInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnSchool(SelectEvent event) {
		School school = (School) event.getObject();
		insuredPersonInfoDTO.setSchool(school);
	}

	public void returnCoinsuranceCompany(SelectEvent event) {
		CoinsuranceCompany coinsuranceCompany = (CoinsuranceCompany) event.getObject();
		insuranceHistoryRecord.setCompany(coinsuranceCompany);
	}

	public void returnResidentTownship(SelectEvent event) {
		Township residentTownship = (Township) event.getObject();
		lifeProposal.getCustomer().getResidentAddress().setTownship(residentTownship);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonInfoDTO.setOccupation(occupation);
	}

	public void returnOccupationForCustomer(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		lifeProposal.getCustomer().setOccupation(occupation);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnNationalityForInsuredPerson(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		lifeProposal.getCustomer().setCountry(nationality);
	}

	public void returnOfficeTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		lifeProposal.getCustomer().getOfficeAddress().setTownship(township);
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	public List<GradeInfo> getGradeInfoList() {
		return gradeInfoList;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public List<TownshipCode> getCustomerTownshipCodeList() {
		return customerTownshipCodeList;
	}

	public List<TownshipCode> getFatherTownshipCodeList() {
		return fatherTownshipCodeList;
	}

	public List<TownshipCode> getMotherTownshipCodeList() {
		return motherTownshipCodeList;
	}

	public boolean isCreateNewIsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	public boolean isEnquiryEdit() {
		return isEnquiryEdit;
	}

	public boolean isEditProposal() {
		return isEditProposal;
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

	public boolean isSurveyAgain() {
		return surveyAgain;
	}

	public void setSurveyAgain(boolean surveyAgain) {
		this.surveyAgain = surveyAgain;
	}

	public boolean isAgeAbove7() {
		return isAgeAbove7;
	}

	public InsuranceHistoryRecord getInsuranceHistoryRecord() {
		return insuranceHistoryRecord;
	}

	public void setInsuranceHistoryRecord(InsuranceHistoryRecord insuranceHistoryRecord) {
		this.insuranceHistoryRecord = insuranceHistoryRecord;
	}

	public Product getProduct() {
		return product;
	}

	public boolean isEditInsuHistRec() {
		return editInsuHistRec;
	}

	public boolean isSameCustomerAdderess() {
		return sameCustomerAdderess;
	}

	public void setSameCustomerAdderess(boolean sameCustomerAdderess) {
		this.sameCustomerAdderess = sameCustomerAdderess;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (entitys != null && !entity.getId().equals(entitys.getId())) {
			lifeProposal.setBranch(null);
			lifeProposal.setSalePoint(null);
		}
		this.entitys = entity;

	}

	public void removeEntity() {
		entitys = null;
		lifeProposal.setBranch(null);
		lifeProposal.setSalePoint(null);
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

	public List<BancaMethod> getBancaMethodList() {
		return bancaMethodList;
	}

	public BancaMethod getBancaMethod() {
		return bancaMethod;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public List<GGIOrganization> getGgiOrganizationList() {
		return ggiOrganizationList;
	}

	public void setGgiOrganizationList(List<GGIOrganization> ggiOrganizationList) {
		this.ggiOrganizationList = ggiOrganizationList;
	}

	public List<Staff> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<Staff> staffList) {
		this.staffList = staffList;
	}

	public List<RelationShipType> getRelationShipTypeList() {
		return relationShipTypeList;
	}

	public void setRelationShipTypeList(List<RelationShipType> relationShipTypeList) {
		this.relationShipTypeList = relationShipTypeList;
	}

	public Boolean getSelectItem() {
		return selectItem;
	}

	public void setSelectItem(Boolean selectItem) {
		if (selectItem == false) {
			clearData();
		}
		this.selectItem = selectItem;
	}

	public GGIOrganization getGgiOrganization() {
		return ggiOrganization;
	}

	public void setGgiOrganization(GGIOrganization ggiOrganization) {
		this.ggiOrganization = ggiOrganization;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public RelationShipType getRelationShipType() {
		return relationShipType;
	}

	public void setRelationShipType(RelationShipType relationShipType) {
		this.relationShipType = relationShipType;
	}

	public Percentage getPercentage() {
		return percentage;
	}

	public void setPercentage(Percentage percentage) {
		this.percentage = percentage;
	}

	public Eips getEips() {
		return eips;
	}

	public void setEips(Eips eips) {
		this.eips = eips;
	}

	public IGGIOrganizationService getGgiOrganizationService() {
		return ggiOrganizationService;
	}

	public void setGgiOrganizationService(IGGIOrganizationService ggiOrganizationService) {
		this.ggiOrganizationService = ggiOrganizationService;
	}

	public IRelationShipTypeService getRelationShipTypeService() {
		return relationShipTypeService;
	}

	public void setRelationShipTypeService(IRelationShipTypeService relationShipTypeService) {
		this.relationShipTypeService = relationShipTypeService;
	}

	public IPercentageService getPercentageService() {
		return percentageService;
	}

	public void setPercentageService(IPercentageService percentageService) {
		this.percentageService = percentageService;
	}

	public IEipsService getEipsService() {
		return eipsService;
	}

	public void setEipsService(IEipsService eipsService) {
		this.eipsService = eipsService;
	}

	public void setRelationshipList(List<RelationShip> relationshipList) {
		this.relationshipList = relationshipList;
	}
	private void clearData() {
		ggiOrganization = new GGIOrganization();
		staff = new Staff();
		relationShipType = new RelationShipType();
		percentage = new Percentage();
	}

	private void saveEipsData() {
		eips.setRelationShipType(relationShipType);
		eips.setStaff(staff);
		eips.setPercentage(percentage.getPercent());
		eipsService.save(eips);
	}

	private double calculateEipsAmount() {
		return (((percentage.getPercent())/100)*lifeProposal.getProposalInsuredPersonList().get(0).getBasicTermPremium());
	}
	
	// Choose Staff With Organization

		public void selectStaffWithOrganization() {
			staffList = staffService.findStaffWithGGIOrganization(ggiOrganization.getId());
		}

		public void showPercentageWithRelationShip() {
			percentage = percentageService.findPercentageWithRelationShip(relationShipType.getId(), product.getId());
			
		}
}
