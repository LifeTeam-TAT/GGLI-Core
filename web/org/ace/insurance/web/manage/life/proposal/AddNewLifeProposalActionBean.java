package org.ace.insurance.web.manage.life.proposal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.SalutationType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.utils.SumInsuredType;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuranceHistoryRecord;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.addon.service.AddOnService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.auditLog.AuditLog;
import org.ace.insurance.system.common.auditLog.service.interfaces.IAuditLogService;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.bmiChart.service.interfaces.IBMIService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.eips.service.interfaces.IEipsService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.keyfactor.service.KeyFactorService;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.organization.service.interfaces.IOrganizationService;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.percentage.Percentage;
import org.ace.insurance.system.common.percentage.service.interfaces.IPercentageService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.service.interfaces.IRelationShipTypeService;
import org.ace.insurance.system.common.riskyOccupation.RiskyOccupation;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.system.common.typesOfSport.TypesOfSport;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.PeriodType;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.SurveyAnswerOne;
import org.ace.insurance.web.common.SurveyAnswerTwo;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.ace.java.web.upload.UploadedFileException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "AddNewLifeProposalActionBean")
public class AddNewLifeProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{GGIOrganizationService}")
	private IGGIOrganizationService ggiOrganizationService;

	@ManagedProperty(value = "#{StaffService}")
	private IStaffService staffService;

	@ManagedProperty(value = "#{RelationShipTypeService}")
	private IRelationShipTypeService relationShipTypeService;

	@ManagedProperty(value = "#{PercentageService}")
	private IPercentageService percentageService;

	@ManagedProperty(value = "#{EipsService}")
	private IEipsService eipsService;

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

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

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{OrganizationService}")
	private IOrganizationService organizationService;

	public void setOrganizationService(IOrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@ManagedProperty(value = "#{AuditLogService}")
	private IAuditLogService auditLogService;

	public void setAuditLogService(IAuditLogService auditLogService) {
		this.auditLogService = auditLogService;
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

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaMethodService;

	public void setBancaMethodService(IBancaMethodService bancaMethodService) {
		this.bancaMethodService = bancaMethodService;
	}

	@ManagedProperty(value = "#{BancaassuraceProposalService}")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	public void setBancaassuraceProposalService(IBancaassuraceProposalService bancaassuraceProposalService) {
		this.bancaassuraceProposalService = bancaassuraceProposalService;
	}

	@ManagedProperty(value = "#{BMIService}")
	private IBMIService bmiService;

	@ManagedProperty(value = "#{AddOnService}")
	private AddOnService addOnService;

	public void setBmiService(IBMIService bmiService) {
		this.bmiService = bmiService;
	}

	@ManagedProperty(value = "#{KeyFactorService}")
	private KeyFactorService keyfactorService;

	public void setKeyfactorService(KeyFactorService keyfactorService) {
		this.keyfactorService = keyfactorService;
	}

	private List<KeyFactor> simpleLifeKeyfactorList;

	private KeyFactor simpleLifeKeyfactor;

	private List<GGIOrganization> ggiOrganizationList;

	private List<Staff> staffList;

	private List<RelationShipType> relationShipTypeList;

	private Boolean selectItem;

	private GGIOrganization ggiOrganization;

	private Staff staff;

	private RelationShipType relationShipType;

	private Percentage percentage;

	private Eips eips;

	private User user;
	private LifeProposal lifeProposal;
	private LifePolicy lifepolicy;
	private List<RelationShip> relationshipList;
	private List<Product> productsList;
	private boolean createNew;
	private String remark;
	private User responsiblePerson;
	private String userType;
	private Product product;
	private Entitys entitys;
	private InsuranceHistoryRecord insuranceHistoryRecord;
	private Map<String, InsuranceHistoryRecord> insuranceHistoryRecordMap;
	private Map<String, String> periodOfInsur;
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();
	private List<TownshipCode> benTownshipCodeList = new ArrayList<TownshipCode>();

	private boolean isSportMan;
	private boolean isPersonalAccident;
	private boolean isFarmer;
	private boolean isShortTermEndowment;
	private boolean isSinglePremiumEndowmentLife;
	private boolean isSinglePremiumCreditLife;
	private boolean isShortTermSinglePremiumCreditLife;
	private boolean isSimpleLife;
	private boolean editInsuHistRec;
	private boolean isMonthBase;
	private boolean isEndorse;
	private boolean isSTELendorse;
	private boolean isValidate;
	private boolean isSIOcase = false;
	private String branchId;
	private BancaassuranceProposal bancaassuranceProposal;
	private List<BancaMethod> bancaMethodList;
	private BancaMethod bancaMethod;
	private boolean isChannel;
	private boolean isRefferal;
	// for short term single premium
	private boolean isPublicTermLife;
	private boolean isPeriodOfYear = false;
	private boolean isPeriodofWeek = false;
	private boolean isPeriodofMonth = false;
	private boolean isDisabled = false;
	private int periodOfYears;
	private double maxSIForThreeYearsPeriod;
	private int maximumTremWeek;
	private int munimumTremWeek;

	private PeriodType periodType;

	private boolean isExcelUpload = false;
	private String uploadMethod = "manual";
	String errorMessage = "";
	private UploadedFile file;
	List<InsuredPersonInfoDTO> insuredPersonDTOList;
	AuditLog auditLog;
	private Organization lifeProposalOrganization;
	private Customer proposedCustomer;

	private boolean showSimpleLifeSurvey;
	// private String excelFileId = "";

	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
		lifepolicy = (LifePolicy) getParam("lifePolicy");
		product = (Product) getParam("product");
		bancaMethod = new BancaMethod();
		bancaassuranceProposal = new BancaassuranceProposal();
		bancaMethodList = bancaMethodService.findAllBanca();
		isChannel = false;
		isPeriodofWeek = true;

	}

	@PreDestroy
	public void destroy() {
		removeParam("lifePolicy");
		removeParam("product");
	}

	@PostConstruct
	public void init() {
		initializeInjection();

		eips = new Eips();
		auditLog = new AuditLog();
		editInsuHistRec = false;
		ggiOrganizationList = ggiOrganizationService.findAllGGIOrganization();
		relationShipTypeList = relationShipTypeService.findAllRelationShipType();

		relationshipList = relationShipService.findAllRelationShip();

		if (lifepolicy == null) {
			createNewLifeProposal();
		} else {
			lifeProposal = new LifeProposal(lifepolicy);
			lifeProposal.setSubmittedDate(new Date());
			if (lifeProposal.getCustomer() == null) {
				organization = true;
			}

			product = lifepolicy.getInsuredPersonInfo().get(0).getProduct();
		}

		loadRenderValues();

		if (isSimpleLife) {
			simpleLifeKeyfactorList = keyfactorService.findKeyFactorForSimpleLife();
			simpleLifeKeyfactor = new KeyFactor();
		}

		productsList = Arrays.asList(product);
		insuranceHistoryRecord = new InsuranceHistoryRecord();
		insuranceHistoryRecordMap = new HashMap<String, InsuranceHistoryRecord>();
		insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
		/* endorsement case */
		if (lifepolicy != null) {
			entitys = lifepolicy.getBranch().getEntity();
			List<ProposalInsuredPerson> insuredPersonList = new ArrayList<ProposalInsuredPerson>();
			for (PolicyInsuredPerson policyInsuredPerson : lifepolicy.getPolicyInsuredPersonList()) {
				insuredPersonList.add(new ProposalInsuredPerson(policyInsuredPerson));
			}
			lifeProposal.getProposalInsuredPersonList().clear();
			for (ProposalInsuredPerson proposalInsuredPerson : insuredPersonList) {
				lifeProposal.addInsuredPerson(proposalInsuredPerson);
			}

			for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
				insuredPersonInfoDTO = new InsuredPersonInfoDTO(pv);
				insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			}
			isEndorse = true;
			if (isSTELendorse = KeyFactorChecker.isShortTermEndowment(product.getId()))
				isValidate = false;
		}
		// Change to user
		/*
		 * List<SalePoint> salePointList = salePointService.findAllSalePoint();
		 * if (salePointList.size() > 0) {
		 * lifeProposal.setSalePoint(salePointList.get(0)); }
		 */
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
		createNewBeneficiariesInfo();
		createInsuredPersonAddOnDTO();
		isSportMan = false;
		stateCodeList = stateCodeService.findAllStateCode();
	}

	/*************************************************
	 * Start Beneficiary Manage
	 *********************************************************/
	private boolean createNewBeneficiariesInfo;
	private BeneficiariesInfoDTO beneficiariesInfoDTO;
	private BeneficiariesInfoDTO oldBeneficiariesInfoDTO;
	private Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap;

	public IdType[] getIdTypes() {

		if (isSinglePremiumCreditLife) {
			return getIdTypeNotSTILL_APPLYING();
		}
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public SumInsuredType[] getSumInsuredType() {
		return SumInsuredType.values();
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	private void createNewBeneficiariesInfo() {
		createNewBeneficiariesInfo = true;
		beneficiariesInfoDTO = new BeneficiariesInfoDTO();
		beneficiariesInfoDTO.setIdType(IdType.NRCNO);
	}

	public BeneficiariesInfoDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public void setBeneficiariesInfoDTO(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
	}

	public void prepareAddNewBeneficiariesInfo() {
		createNewBeneficiariesInfo();
	}

	public void prepareEditBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		if (beneficiariesInfoDTO.getOrganization() == null) {
			beneficiariesInfoDTO.loadFullIdNo();
			this.beneficiariesInfoDTO = new BeneficiariesInfoDTO(beneficiariesInfoDTO);
			changeBeneStateCode();

		} else {
			this.beneOrganization = true;
			this.beneficiariesInfoDTO = new BeneficiariesInfoDTO(beneficiariesInfoDTO);
		}
		this.createNewBeneficiariesInfo = false;
	}

	public Map<String, BeneficiariesInfoDTO> sortByValue(Map<String, BeneficiariesInfoDTO> map) {
		List<Map.Entry<String, BeneficiariesInfoDTO>> list = new LinkedList<Map.Entry<String, BeneficiariesInfoDTO>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, BeneficiariesInfoDTO>>() {
			public int compare(Map.Entry<String, BeneficiariesInfoDTO> o1, Map.Entry<String, BeneficiariesInfoDTO> o2) {
				try {
					Long l1 = Long.parseLong(o1.getKey());
					Long l2 = Long.parseLong(o2.getKey());
					if (l1 > l2) {
						return 1;
					} else if (l1 < l2) {
						return -1;
					} else {
						return 0;
					}
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		});

		Map<String, BeneficiariesInfoDTO> result = new LinkedHashMap<String, BeneficiariesInfoDTO>();
		for (Map.Entry<String, BeneficiariesInfoDTO> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public void saveBeneficiariesInfo() {
		if (validateBeneficiaryInfo()) {
			if (beneficiariesInfoDTO.getOrganization() == null) {
				beneficiariesInfoDTO.setFullIdNo();
			}
			beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
			insuredPersonInfoDTO.setBeneficiariesInfoDTOList(new ArrayList<BeneficiariesInfoDTO>(sortByValue(beneficiariesInfoDTOMap).values()));
			createNewBeneficiariesInfo();
			PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
		}
	}

	public boolean validateBeneficiaryInfo() {

		boolean valid = true;
		if (!beneOrganization) {
			String formID = "beneficiaryInfoEntryForm";
			Date dob = beneficiariesInfoDTO.getDateOfBirth();
			int age = beneficiariesInfoDTO.getAge();
			if (null != dob) {
				Date currentDate = new Date();
				if (currentDate.compareTo(dob) < 0) {
					addErrorMessage(formID + ":dateOfBirth", MessageId.INVALID_DATE_OF_BIRTH);
					valid = false;
				}
			}
			if (age < 0 || age > 100) {
				addErrorMessage(formID + ":benificiaryAge", MessageId.INVALID_AGE);
				valid = false;
			}
		}
		return valid;

	}

	public void removeBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.oldBeneficiariesInfoDTO = beneficiariesInfoDTO;
		beneficiariesInfoDTOMap.remove(beneficiariesInfoDTO.getTempId());
		insuredPersonInfoDTO.getBeneficiariesInfoDTOList().remove(beneficiariesInfoDTO);
		createNewBeneficiariesInfo();
	}

	private boolean createNewAddOn;
	private InsuredPersonAddOnDTO insuredPersonAddOnDTO;
	private Map<String, InsuredPersonAddOnDTO> insuredPersonAddOnDTOMap;

	public boolean isCreateNewAddOn() {
		return createNewAddOn;
	}

	private void createInsuredPersonAddOnDTO() {
		createNewAddOn = true;
		insuredPersonAddOnDTO = new InsuredPersonAddOnDTO();
		insuredPersonAddOnDTOMap = new HashMap<String, InsuredPersonAddOnDTO>();
	}

	public InsuredPersonAddOnDTO getInsuredPersonAddOnDTO() {
		return insuredPersonAddOnDTO;
	}

	public void setInsuredPersonAddOnDTO(InsuredPersonAddOnDTO insuredPersonAddOnDTO) {
		this.insuredPersonAddOnDTO = insuredPersonAddOnDTO;
	}

	public void prepareInsuredPersonAddOnDTO() {
		createInsuredPersonAddOnDTO();
	}

	public void prepareEditInsuredPersonAddOnDTO(InsuredPersonAddOnDTO insuredPersonAddOnDTO) {
		this.insuredPersonAddOnDTO = insuredPersonAddOnDTO;
		this.createNewAddOn = false;
	}

	public void saveInsuredPersonAddOnDTO() {
		if (validInsuredPersonAddOn()) {
			insuredPersonInfoDTO.addInsuredPersonAddOn(insuredPersonAddOnDTO);
			createInsuredPersonAddOnDTO();
		}
	}

	public void removeAddOn(InsuredPersonAddOnDTO insuredPersonAddOnDTO) {
		insuredPersonInfoDTO.removeInsuredPersonAddOn(insuredPersonAddOnDTO);
		createInsuredPersonAddOnDTO();
	}

	private boolean validInsuredPersonAddOn() {
		boolean valid = true;
		if (insuredPersonAddOnDTO.getAddOn() == null) {
			addErrorMessage("addOnEntryForm" + ":addOn", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (valid) {
			PrimeFaces.current().executeScript("PF('addOnEntryDialog').hide()");
		}
		return valid;
	}

	/*************************************************
	 * End AddOn Manage
	 *********************************************************/

	/*************************************************
	 * Start InsuredPerson Manage
	 ******************************************************/
	private Map<String, InsuredPersonInfoDTO> insuredPersonInfoDTOMap;
	private InsuredPersonInfoDTO insuredPersonInfoDTO;
	private InsuredPersonInfoDTO oldInsuredPersonInfoDTO;
	private boolean createNewIsuredPersonInfo;
	private Boolean isEdit = false;
	private Boolean isReplace = false;

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	public void changeProduct(AjaxBehaviorEvent e) {
		loadRenderValues();
	}

	public void changeInsuIdType() {
		checkIdType("I");
	}

	public void changeBeneIdType() {
		checkIdType("B");
		PrimeFaces.current().resetInputs("beneficiaryInfoEntryForm:idNo");
		PrimeFaces.current().resetInputs("beneficiaryInfoEntryForm:nrcNo");
	}

	public void checkIdType(String customerType) {
		if ("I".equals(customerType)) {
			insuredPersonInfoDTO.setProvinceCode(null);
			insuredPersonInfoDTO.setTownshipCode(null);
			insuredPersonInfoDTO.setIdNo(null);
			insuredPersonInfoDTO.setFullIdNo(null);
		} else {
			beneficiariesInfoDTO.setProvinceCode(null);
			beneficiariesInfoDTO.setTownshipCode(null);
			beneficiariesInfoDTO.setIdNo(null);
			beneficiariesInfoDTO.setFullIdNo(null);
		}
	}

	private void loadRenderValues() {
		isFarmer = KeyFactorChecker.isFarmer(product);
		isSportMan = KeyFactorChecker.isSportMan(product);
		isPersonalAccident = (KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product));
		isSimpleLife = KeyFactorChecker.isSimpleLife(product);
		isShortTermEndowment = KeyFactorChecker.isShortTermEndowment(product.getId());
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		isMonthBase = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product) || KeyFactorChecker.isSinglePremiumCreditLife(product)
				|| KeyFactorChecker.isSinglePremiumEndowmentLife(product) || KeyFactorChecker.isPublicLife(product) || KeyFactorChecker.isGroupLife(product) || isShortTermEndowment
						? false
						: true;

	}

	private void createNewInsuredPersonInfo() {
		createNewIsuredPersonInfo = true;
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		insuredPersonInfoDTO.setIdType(IdType.NRCNO);
		insuredPersonInfoDTO.setStartDate(new Date());
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
		isEdit = false;
		isReplace = false;

	}

	public Boolean getIsReplace() {
		return isReplace;
	}

	public boolean getIsSportMan() {
		return isSportMan;
	}

	public boolean getIsMonthBase() {
		return isMonthBase;
	}

	public boolean getIsPersonalAccident() {
		return isPersonalAccident;
	}

	public List<Integer> getPeriodMonths() {
		return Arrays.asList(3, 6, 12);
	}

	/* Short Term Endowment Life */
	public List<Integer> getSePeriodYears() {

		return Arrays.asList(5, 7, 10);
	}

	/* Single Premium Endowment Life */
	/* Short Term Single Premium Credit Life */
	/* Single Premium Credit Life */
	public List<Integer> getSpPeriodYear() {
		int maxTerm = product.getMaxTerm();
		int minTerm = product.getMinTerm();

		List<Integer> lists = new ArrayList<Integer>();
		for (int i = minTerm; i <= maxTerm; i++) {
			lists.add(i);
		}
		return lists;
	}

	public List<Integer> getSimpleLifePeriodYear() {
		if (isPeriodOfYear) {
			return Arrays.asList(1);
		}
		if (isPeriodofMonth) {
			return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
		}
		if (isPeriodofWeek) {
			return Arrays.asList(1, 2, 3);
		}
		return Arrays.asList(1, 2, 3);
	}

	public void validateSimpleLife() {
		if (isLessThanOneMonthAndGreater1Mi() || isGraterOneMonthAndAvobe2Mi()) {
			showSimpleLifeSurvey = true;
		} else {
			showSimpleLifeSurvey = false;
		}
	}

	private boolean isLessThanOneMonthAndGreater1Mi() {
		if (isLessThanOneMonth() && insuredPersonInfoDTO.getSumInsuredInfoNum().doubleValue() > 1000000.00) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isGraterOneMonthAndAvobe2Mi() {
		if (!isLessThanOneMonth() && insuredPersonInfoDTO.getSumInsuredInfoNum().doubleValue() > 2000000.00) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isLessThanOneMonth() {
		if (isPeriodofWeek) {
			return true;
		} else if (isPeriodofMonth && insuredPersonInfoDTO.getPeriodOfYears() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public SurveyAnswerOne[] getSurveyAnswerOne() {
		return SurveyAnswerOne.values();
	}

	public SurveyAnswerTwo[] getSurveyAnswerTwo() {
		return SurveyAnswerTwo.values();
	}

	public List<Number> getSelectSI() {

		Number maxSI = product.getMaxSumInsured();
		Number minSI = product.getMinSumInsured();

		if (KeyFactorChecker.isSinglePremiumEndowmentLife(product)) {
			maxSIForThreeYearsPeriod = ((Number) 20000000).doubleValue();
		}

		List<Number> lists = new ArrayList<Number>();

		do {
			if (periodOfYears == 3 && minSI.doubleValue() > maxSIForThreeYearsPeriod) {
				break;
			}
			lists.add(minSI.doubleValue());
			minSI = minSI.doubleValue() + 1000000;
		} while (minSI.intValue() <= maxSI.doubleValue());

		return lists;
	}

	public void periodOfInsurValueChanged() {

	}

	public void periodValueChanged() {
		periodOfYears = insuredPersonInfoDTO.getPeriodOfYears();
	}

	public void setIsReplace(Boolean isReplace) {
		this.isReplace = isReplace;
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

	public void prepareAddNewInsuredPersonInfo() {
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
	}

	public void clearInsuredPersonFields() {
		// this.lifeProposal.setCustomer(null);
		prepareAddNewInsuredPersonInfo();
	}

	public void prepareEditInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		float percentage = 0;
		for (BeneficiariesInfoDTO dto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
			percentage += dto.getPercentage();
		}
		if (isValidate && percentage < 100) {
			beneficiariesInfoDTOMap.clear();
			this.insuredPersonInfoDTO = new InsuredPersonInfoDTO(oldInsuredPersonInfoDTO);
			this.oldInsuredPersonInfoDTO = new InsuredPersonInfoDTO(oldInsuredPersonInfoDTO);

		} else {
			this.insuredPersonInfoDTO = new InsuredPersonInfoDTO(insuredPersonInfoDTO);
			this.insuredPersonInfoDTO.setPeriodOfYears(insuredPersonInfoDTO.getPeriodOfYears());
			this.oldInsuredPersonInfoDTO = new InsuredPersonInfoDTO(insuredPersonInfoDTO);
			isValidate = true;
			if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList() != null) {
				createNewBeneficiariesInfoDTOMap();
				for (BeneficiariesInfoDTO bdto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
					beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
				}
			}
		}
		this.insuredPersonInfoDTO.loadFullIdNo();
		changeStateCode();
		createNewIsuredPersonInfo = false;
		PrimeFaces.current().executeScript("PF('wiz').nextNav.hide();");
		isEdit = true;
	}

	public void prepareReplaceInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
		createNewBeneficiariesInfoDTOMap();
		createNewIsuredPersonInfo = false;
		isReplace = true;
	}

	// private UploadedFile uploadedFile;
	//
	// public UploadedFile getUploadedFile() {
	// return uploadedFile;
	// }
	//
	// public void setUploadedFile(UploadedFile uploadedFile) {
	// this.uploadedFile = uploadedFile;
	// }
	//
	// public void upload(ActionEvent event) {
	// try {
	// InputStream inputStream = uploadedFile.getInputstream();
	// Workbook wb = WorkbookFactory.create(inputStream);
	// Sheet sheet = wb.getSheetAt(0);
	// boolean readAgain = true;
	// int i = 1;
	// while (readAgain) {
	// InsuredPersonInfoDTO insuredPersonInfoDTO = new InsuredPersonInfoDTO();
	// ResidentAddress ra = new ResidentAddress();
	// Name name = new Name();
	// Row row = sheet.getRow(i);
	// if (row == null) {
	// readAgain = false;
	// break;
	// }
	// String initialId = row.getCell(0).getStringCellValue();
	// if (initialId == null || initialId.isEmpty()) {
	// readAgain = false;
	// break;
	// } else {
	// insuredPersonInfoDTO.setInitialId(initialId);
	// String firstName = row.getCell(1).getStringCellValue();
	// name.setFirstName(firstName);
	// String middleName = row.getCell(2).getStringCellValue();
	// name.setMiddleName(middleName);
	// String lastName = row.getCell(3).getStringCellValue();
	// name.setLastName(lastName);
	//
	// String fatherName = row.getCell(4).getStringCellValue();
	// insuredPersonInfoDTO.setFatherName(fatherName);
	//
	// String idNo = row.getCell(5).getStringCellValue();
	// insuredPersonInfoDTO.setIdNo(idNo);
	//
	// IdType idType = IdType.valueOf(row.getCell(6).getStringCellValue());
	// insuredPersonInfoDTO.setIdType(idType);
	//
	// Date dateOfBirth = row.getCell(7).getDateCellValue();
	// insuredPersonInfoDTO.setDateOfBirth(dateOfBirth);
	//
	// double suminsured = row.getCell(8).getNumericCellValue();
	// insuredPersonInfoDTO.setSumInsuredInfo(suminsured);
	//
	// String resAddress = row.getCell(9).getStringCellValue();
	// ra.setResidentAddress(resAddress);
	// String resTownshipId = row.getCell(10).getStringCellValue();
	// Township township = townshipService.findTownshipById(resTownshipId);
	// ra.setTownship(township);
	//
	// String occupationId = row.getCell(11).getStringCellValue();
	// if (occupationId == null || occupationId.isEmpty()) {
	// Occupation occupation =
	// occupationService.findOccupationById(occupationId);
	// insuredPersonInfoDTO.setOccupation(occupation);
	// }
	//
	// Gender gender = Gender.valueOf(row.getCell(12).getStringCellValue());
	// insuredPersonInfoDTO.setGender(gender);
	//
	// String productId = row.getCell(13).getStringCellValue();
	// Product product = productService.findProductById(productId);
	// insuredPersonInfoDTO.setProduct(product);
	//
	// int periodOfYears = (int) row.getCell(14).getNumericCellValue();
	// insuredPersonInfoDTO.setPeriodOfYears(periodOfYears);
	// insuredPersonInfoDTO.setResidentAddress(ra);
	// insuredPersonInfoDTO.setName(name);
	//
	// insuredPersonInfoDTO.setStartDate(new Date());
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(insuredPersonInfoDTO.getStartDate());
	// cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
	// insuredPersonInfoDTO.setEndDate(cal.getTime());
	// for (InsuredPersonKeyFactorValue insKFV :
	// insuredPersonInfoDTO.getKeyFactorValueList()) {
	// KeyFactor kf = insKFV.getKeyFactor();
	// if (KeyFactorChecker.isSumInsured(kf)) {
	// insKFV.setValue(insuredPersonInfoDTO.getSumInsuredInfo() + "");
	// } else if (KeyFactorChecker.isTerm(kf)) {
	// insKFV.setValue(insuredPersonInfoDTO.getPeriodOfYears() + "");
	// } else if (KeyFactorChecker.isAge(kf)) {
	// insKFV.setValue(insuredPersonInfoDTO.getAgeForNextYear() + "");
	// } else if (KeyFactorChecker.isMedicalAge(kf)) {
	// insKFV.setValue(insuredPersonInfoDTO.getAgeForNextYear() + "");
	// }
	// }
	// insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(),
	// insuredPersonInfoDTO);
	// }
	// i++;
	// }
	// } catch (IOException e) {
	// FacesContext.getCurrentInstance().addMessage("messages", new
	// FacesMessage("Failed to upload the file!"));
	// } catch (InvalidFormatException e) {
	// FacesContext.getCurrentInstance().addMessage("messages", new
	// FacesMessage("Invalid data is occured in Uploaded File!"));
	// }
	// }

	public void saveInsuredPersonInfo() {
		insuredPersonInfoDTO.setProduct(product);
		Calendar cal = Calendar.getInstance();
		if (insuredPersonInfoDTO.getStartDate() == null) {
			insuredPersonInfoDTO.setStartDate(new Date());
		}
		cal.setTime(insuredPersonInfoDTO.getStartDate());

		// if (isSimpleLife) {
		// calculatePounds();
		// }

		if (validInsuredPerson(insuredPersonInfoDTO)) {
			if (isSimpleLife) {
				if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
					cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
				} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
					cal.add(Calendar.MONTH, insuredPersonInfoDTO.getPeriodOfYears());
				} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
					cal.add(Calendar.DAY_OF_MONTH, insuredPersonInfoDTO.getPeriodOfYears() * 7);
				} else {
					if (isMonthBase) {
						cal.add(Calendar.MONTH, insuredPersonInfoDTO.getPeriodOfYears());
					} else {
						cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
					}
				}
			}

			insuredPersonInfoDTO.setInsuranceHistoryRecord(getInsuranceHistoryRecordList());
			insuredPersonInfoDTO.setEndDate(cal.getTime());
			if (!isEndorse) {
				setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears(),
						insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
				// For Endorsement
			} else {
				// Set InsuredPerson Endorsement Status
				PolicyInsuredPerson policyInsuPerson = lifePolicyService.findInsuredPersonByCodeNo(insuredPersonInfoDTO.getInsPersonCodeNo());
				if (policyInsuPerson == null) {
					insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.NEW);
				} else {
					if (isEdit == true && insuredPersonInfoDTO.getEndorsementStatus() != EndorsementStatus.REPLACE) {
						insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.EDIT);
					} else if (isReplace == true) {
						insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.REPLACE);
					}
				}
				// Set Key Factor For Public Life
				if (checkPublicLife()) {
					if (insuredPersonInfoDTO.getEndorsementStatus() == EndorsementStatus.TERMINATE) {
						setKeyFactorValue(lifepolicy.getInsuredPersonInfo().get(0).getSumInsured(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()),
								lifepolicy.getInsuredPersonInfo().get(0).getPeriodYears(), insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
					} else {
						if (isIncreasedSIAmount(insuredPersonInfoDTO)) {
							setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(),
									lifepolicy.getInsuredPersonInfo().get(0).getPeriodYears(), insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
						} else {
							setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()),
									insuredPersonInfoDTO.getPeriodOfYears(), insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
						}
					}
					insuredPersonInfoDTO.setEndDate(cal.getTime());
					// Set Key Factor For Group Life
				} else {
					if (insuredPersonInfoDTO.getEndorsementStatus() == EndorsementStatus.TERMINATE) {
						setKeyFactorValue(policyInsuPerson.getSumInsured(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()), policyInsuPerson.getPeriodYears(),
								insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
					} else {
						setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears(),
								insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
					}
					insuredPersonInfoDTO.setEndDate(lifepolicy.getInsuredPersonInfo().get(0).getEndDate());
				}
			}
			insuredPersonInfoDTO.setFullIdNo();
			insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			clearInsuredPersonFields();
			PrimeFaces.current().executeScript("PF('wiz').nextNav.show();");
		}
	}

	// public void calculatePounds() {
	// insuredPersonInfoDTO.setHeight(insuredPersonInfoDTO.getFeets() * 12 +
	// insuredPersonInfoDTO.getInches());
	// if (insuredPersonInfoDTO.getHeight() < 58) {
	// insuredPersonInfoDTO.setHeight(58);
	// } else if (insuredPersonInfoDTO.getHeight() > 72) {
	// insuredPersonInfoDTO.setHeight(72);
	// }
	// if (insuredPersonInfoDTO.getAgeForNextYear() < 20) {
	// insuredPersonInfoDTO.setAge(20);
	// } else if (insuredPersonInfoDTO.getAgeForNextYear() > 45) {
	// insuredPersonInfoDTO.setAge(45);
	// } else {
	// insuredPersonInfoDTO.setAge(insuredPersonInfoDTO.getAgeForNextYear());
	// }
	// int bmiWeight =
	// bmiService.findPoundByAgeAndHeight(insuredPersonInfoDTO.getAge(),
	// insuredPersonInfoDTO.getHeight());
	// insuredPersonInfoDTO.setPounds(bmiWeight);
	// }

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
	}

	/**
	 * 
	 * This method is used to retrieve the available SI amount for an insured
	 * person.
	 * 
	 * @param insuredPersonInfoDTO
	 * @return double
	 */
	public double getAvailableSISportMan(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		double availableSI = 0.0;
		double maxSi = insuredPersonInfoDTO.getProduct().getMaxSumInsured();
		availableSI = maxSi - (getTotalSISportMan(insuredPersonInfoDTO));

		return availableSI;
	}

	/**
	 * 
	 * This method is used to retrieve the total SI amount of an insured
	 * person's active policies.
	 * 
	 * @param insuredPersonInfoDTO
	 * @return double
	 * 
	 */
	public double getTotalSISportMan(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		List<PolicyInsuredPerson> people = lifeProposalService.findPolicyInsuredPersonByParameters(insuredPersonInfoDTO.getName(), insuredPersonInfoDTO.getIdNo(),
				insuredPersonInfoDTO.getFatherName(), insuredPersonInfoDTO.getDateOfBirth());
		double sumInsured = 0.0;
		Date endDate;

		if (people != null) {
			for (PolicyInsuredPerson p : people) {
				endDate = p.getLifePolicy().getActivedPolicyEndDate();
				if (endDate.after(new Date()) && KeyFactorChecker.isSportMan(p.getProduct())) {
					sumInsured += p.getSumInsured();
				}
			}
		}

		return sumInsured;
	}

	/**
	 * 
	 * This method is used to decide whether an sport man insuredPerson's
	 * sumInsured amount is over sport man product's maximum SI or not in all of
	 * his/her active policies.
	 * 
	 * @param insuredPersonInfoDTO
	 * @return boolean true if SI is over.
	 */
	public boolean isExcessSISportMan(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		double sumInsured = getTotalSISportMan(insuredPersonInfoDTO) + insuredPersonInfoDTO.getSumInsuredInfo();
		boolean flag = false;

		if (sumInsured > insuredPersonInfoDTO.getProduct().getMaxSumInsured()) {
			flag = true;
		}

		return flag;
	}

	/**
	 * 
	 * This method is used to validate Insured Person's Information according to
	 * selected product type.
	 * 
	 * @param insuredPersonInfoDTO
	 * @return
	 */
	private boolean validInsuredPerson(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		boolean valid = true;
		String formID = "lifeProposalEntryForm";
		int maxAge = product.getMaxAge();
		int minAge = product.getMinAge();
		int personAge = insuredPersonInfoDTO.getAgeForNextYear();
		int periodofYear = isMonthBase ? insuredPersonInfoDTO.getPeriodOfYears() / 12 : insuredPersonInfoDTO.getPeriodOfYears();
		if (personAge < minAge) {
			addErrorMessage(formID + ":dateOfBirth", MessageId.MINIMUN_INSURED_PERSON_AGE, minAge);
			valid = false;
		} else if (personAge > maxAge) {
			if (isShortTermEndowment)
				addErrorMessage(formID + ":dateOfBirth", MessageId.MAXIMUM_INSURED_PERSON_AGE, maxAge - 5);
			else
				addErrorMessage(formID + ":dateOfBirth", MessageId.MAXIMUM_INSURED_PERSON_AGE, maxAge);
			valid = false;

		} else if (!isSinglePremiumCreditLife && !isShortTermSinglePremiumCreditLife && personAge + periodofYear > maxAge) {
			addErrorMessage(formID + ":periodOfInsurance", MessageId.MAXIMUM_INSURED_YEARS, maxAge - personAge);
			valid = false;
		} else if (isSinglePremiumCreditLife && personAge + periodofYear > maxAge + 3) {
			addErrorMessage(formID + ":periodOfInsurance", MessageId.MAXIMUM_INSURED_YEARS, maxAge + 3 - personAge);
			valid = false;
		} else if (isShortTermSinglePremiumCreditLife && personAge + periodofYear > maxAge + 1) {
			addErrorMessage(formID + ":periodOfInsurance", MessageId.MAXIMUM_INSURED_YEARS, maxAge + 1 - personAge);
			valid = false;
		} else if (insuredPersonInfoDTO.getPounds() > 25) {
			addErrorMessage(formID + ":weight", MessageId.BMI_MAXIMUM_POUNDS_LIMATION);
			valid = false;
		}

		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList().isEmpty()) {
			insuredPersonInfoDTOMap.remove(insuredPersonInfoDTO.getTempId());
			if (oldInsuredPersonInfoDTO != null) {
				insuredPersonInfoDTOMap.put(oldInsuredPersonInfoDTO.getTempId(), oldInsuredPersonInfoDTO);
			}
			addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.REQUIRED_BENEFICIARY_PERSON);
			valid = false;
		} else {
			float totalPercent = 0.0f;
			for (BeneficiariesInfoDTO beneficiary : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				totalPercent += beneficiary.getPercentage();
			}
			if (totalPercent > 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.OVER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
			if (totalPercent < 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.LOWER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
		}

		if (lifepolicy != null && checkPublicLife()) {
			if (getPassedMonths() > insuredPersonInfoDTO.getPeriodOfMonths()) {
				int availablePeriod = getPassedYears();
				if (getPassedMonths() % 12 != 0) {
					availablePeriod = availablePeriod + 1;
				}
				addErrorMessage(formID + ":periodOfInsurance", MessageId.MINIMUN_INSURED_PERIOD, availablePeriod);
				valid = false;
			}
		}

		if (isSinglePremiumEndowmentLife) {
			// check decimal value part
			if (insuredPersonInfoDTO.getSumInsuredInfo() % 1 != 0) {
				addErrorMessage(formID + ":sumInsuredInfo", MessageId.INVALID_SI);
				valid = false;
			}
		}

		if (isSimpleLife && showSimpleLifeSurvey) {

			/* addErrorMessage */
			if (!isBmiInRange(insuredPersonInfoDTO.getBmi())) {
				valid = false;
				addErrorMessage(formID + ":bmi", MessageId.INVALID_BMI);
			}

			if (insuredPersonInfoDTO.getSurveyquestionOne().equals(SurveyAnswerOne.YES) || insuredPersonInfoDTO.getSurveyquestionTwo().equals(SurveyAnswerTwo.YES)) {
				valid = false;
				addErrorMessage(formID + ":survery1", MessageId.INVALID_SURVEY_ANSWER);
			}

		}

		return valid;
	}

	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	public void removeInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		if (!isEmpty(insuredPersonInfoDTO.getInsPersonCodeNo())) {
			insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.TERMINATE);
		} else {
			insuredPersonInfoDTOMap.remove(insuredPersonInfoDTO.getTempId());
		}
		createNewInsuredPersonInfo();
	}

	/**
	 * 
	 * This method is used to retrieve ProposalInsuredPerson instances from DTO
	 * Map.
	 * 
	 * @return A {@link List} of {@link ProposalInsuredPerson} instances
	 */

	public List<ProposalInsuredPerson> getInsuredPersonInfoList() {
		List<ProposalInsuredPerson> result = new ArrayList<ProposalInsuredPerson>();
		if (insuredPersonInfoDTOMap.values() != null) {
			ProposalInsuredPerson proposalInsuredPerson = null;
			for (InsuredPersonInfoDTO insuredPersonInfoDTO : insuredPersonInfoDTOMap.values()) {
				ClassificationOfHealth classificationOfHealth = insuredPersonInfoDTO.getClassificationOfHealth();
				Customer customer = customerService.findCustomerByInsuredPerson(insuredPersonInfoDTO.getName(), insuredPersonInfoDTO.getIdNo(),
						insuredPersonInfoDTO.getFatherName(), insuredPersonInfoDTO.getDateOfBirth());
				if (isSimpleLife) {
					customer = customerService.findCustomerByNrc(insuredPersonInfoDTO.getFullIdNo());
				}
				insuredPersonInfoDTO.setCustomer(customer);
				insuredPersonInfoDTO.setAge(insuredPersonInfoDTO.getAgeForNextYear());
				proposalInsuredPerson = new ProposalInsuredPerson(insuredPersonInfoDTO, lifeProposal);
				proposalInsuredPerson.setInsuredPersonAddOnList(insuredPersonInfoDTO.getInsuredPersonAddOnList(proposalInsuredPerson));
				proposalInsuredPerson.setClsOfHealth(classificationOfHealth);
				proposalInsuredPerson.setTypesOfSport(insuredPersonInfoDTO.getTypesOfSport()); // Case:SportMan
				proposalInsuredPerson.setUnit(insuredPersonInfoDTO.getUnit());

				if (isSimpleLife) {
					if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
						proposalInsuredPerson.setPeriodYear(insuredPersonInfoDTO.getPeriodOfYears());
						proposalInsuredPerson.setPeriodMonth(0);
						proposalInsuredPerson.setPeriodWeek(0);
						proposalInsuredPerson.setPeriodType(insuredPersonInfoDTO.getPeriodType());
					} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
						proposalInsuredPerson.setPeriodMonth(insuredPersonInfoDTO.getPeriodOfYears());
						proposalInsuredPerson.setPeriodWeek(0);
						proposalInsuredPerson.setPeriodYear(0);
						proposalInsuredPerson.setPeriodType(insuredPersonInfoDTO.getPeriodType());
					} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
						proposalInsuredPerson.setPeriodWeek(insuredPersonInfoDTO.getPeriodOfYears());
						proposalInsuredPerson.setPeriodMonth(0);
						proposalInsuredPerson.setPeriodYear(0);
						proposalInsuredPerson.setPeriodType(insuredPersonInfoDTO.getPeriodType());
					}
				}

				// prepare
				List<InsuredPersonAttachment> insuredPersonAttachments = insuredPersonInfoDTO.getPerAttachmentList();
				if (insuredPersonAttachments != null) {
					for (InsuredPersonAttachment attachment : insuredPersonAttachments) {
						proposalInsuredPerson.addAttachment(attachment);
					}
				}

				List<InsuranceHistoryRecord> historyRecordList = insuredPersonInfoDTO.getInsuranceHistoryRecord();
				if (historyRecordList != null) {
					for (InsuranceHistoryRecord record : historyRecordList) {
						proposalInsuredPerson.addInsuranceHistoryRecord(record);
					}
				}
				proposalInsuredPerson.setInsuredPersonBeneficiariesList(insuredPersonInfoDTO.getBeneficiariesInfoList(proposalInsuredPerson));
				proposalInsuredPerson.setKeyFactorValueList(insuredPersonInfoDTO.getKeyFactorValueList(proposalInsuredPerson));
				result.add(proposalInsuredPerson);
			}
		}
		return result;
	}

	public String backLifeProposal() {
		createNewInsuredPersonInfo();
		return "lifeProposal";
	}

	/*************************************************
	 * End InsuredPerson Manage
	 ********************************************************/
	/*************************************************
	 * Proposal Manage
	 ********************************************/
	private boolean organization;

	private boolean beneOrganization = false;

	public boolean isBeneOrganization() {
		return beneOrganization;
	}

	public void setBeneOrganization(boolean beneOrganization) {
		this.beneOrganization = beneOrganization;
	}

	public boolean isOrganization() {
		return organization;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (organization) {
			lifeProposal.setCustomer(null);
			insuredPersonInfoDTO = new InsuredPersonInfoDTO();
			insuredPersonInfoDTO.setIdType(IdType.NRCNO);
			isDisabled = false;
		} else {
			lifeProposal.setOrganization(null);
		}
	}

	public void changeBeneOrgEvent(AjaxBehaviorEvent event) {
		if (beneOrganization) {
			beneficiariesInfoDTO.setCustomer(null);
			beneficiariesInfoDTO = new BeneficiariesInfoDTO();
			beneficiariesInfoDTO.setIdType(IdType.NRCNO);
		} else {
			beneficiariesInfoDTO.setOrganization(null);
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

	/*************************************************
	 * Proposal Manage
	 **********************************************/

	public void createNewLifeProposal() {
		createNew = true;
		lifeProposal = new LifeProposal();
		lifeProposal.setSubmittedDate(new Date());

		if (isSimpleLife) {
			lifeProposalOrganization = null;
			proposedCustomer = null;
		}
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
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

	public void changeUploadMethod(AjaxBehaviorEvent e) {
		if (uploadMethod.equals("excel")) {
			setExcelUpload(true);
		} else {
			setExcelUpload(false);
		}
		this.insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
	}

	public void upload() {
		if (file != null) {
			InputStream input = null;
			OutputStream output = null;

			try {
				String filename = file.getFileName();
				input = file.getInputstream();
				String currentDir = "D:\\";
				String filePath = currentDir + filename;

				output = new FileOutputStream(new File(currentDir, filename));
				IOUtils.copy(input, output);

				File excelFile = new File(filePath);
				FileInputStream fis = new FileInputStream(excelFile);

				// create an XSSF Workbook object for our XLSX Excel File
				XSSFWorkbook workbook = new XSSFWorkbook(fis);

				boolean isValid = validateExcelFile(workbook);

				if (isValid) {

					this.insuredPersonDTOList = new ArrayList<InsuredPersonInfoDTO>();
					this.insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();

					List<InsuredPersonInfoDTO> insuredPersonInfoDTOList = this.getErrorMessage().isEmpty() ? getInsuredPersonInfoDTOListFromExcel(workbook)
							: new ArrayList<InsuredPersonInfoDTO>();
					List<BeneficiariesInfoDTO> beneficiaryInfoDTOList = this.getErrorMessage().isEmpty() ? getBeneficiariesInfoDTOListFromExcel(workbook)
							: new ArrayList<BeneficiariesInfoDTO>();

					if (this.getErrorMessage().isEmpty()) {
						// linking beneficiary to insured person
						insuredPersonInfoDTOList.forEach(insuredPersonDTO -> {
							beneficiaryInfoDTOList.forEach(beneficiaryDTO -> {
								if (insuredPersonDTO.getTempId().equals(beneficiaryDTO.getTempId())) {
									insuredPersonDTO.getBeneficiariesInfoDTOList().add(beneficiaryDTO);
								}
							});
							this.insuredPersonDTOList.add(insuredPersonDTO);
							insuredPersonInfoDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
						});

						FacesMessage message = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
						FacesContext.getCurrentInstance().addMessage(null, message);
					}

				} else {
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal!", "Excel file is not valid in format.");
					FacesContext.getCurrentInstance().addMessage(null, message);
				}

				this.setErrorMessage("");
				this.file = null;
				// this.setExcelFileId("");

				workbook.close();
				input.close();
				output.close();
				fis.close();

				// delete temporarily saved excel file and clean up data
				Files.deleteIfExists(Paths.get(filePath));
				createNewInsuredPersonInfo();

			} catch (IOException e) {
				e.printStackTrace();

				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal!", "System Error " + file.getFileName() + " cannot upload.");
				FacesContext.getCurrentInstance().addMessage(null, message);
			} finally {
				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
			}
		} else {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal!", "Can't upload data. Please attach Excel file.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public boolean validateExcelFile(XSSFWorkbook workbook) {

		boolean isValidExcel = false;
		int numberOfSheet = workbook.getNumberOfSheets();

		if (numberOfSheet > 1) {
			int numOfInsuredPersonDataColumn = workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells();
			int numOfBeneficiaryDataColumn = workbook.getSheetAt(1).getRow(0).getPhysicalNumberOfCells();
			String firstSheetName = workbook.getSheetAt(0).getSheetName();
			String secondSheetName = workbook.getSheetAt(1).getSheetName();

			if (numberOfSheet == 6 && numOfInsuredPersonDataColumn == 23 && numOfBeneficiaryDataColumn == 14 && firstSheetName.equals("Insured Persons")
					&& secondSheetName.equals("Beneficiaries")) {
				isValidExcel = true;
			}
		}
		return isValidExcel;
	}

	private boolean checkValidation(Cell cell, String errType) throws UploadedFileException {

		boolean isValid = true;

//		if (cell.getColumnIndex() == 4 || cell.getColumnIndex() == 5) {
//			return isValid;
//		}

		int rowNumber = cell.getRow().getRowNum() + 1; // excel row starts from 0 in Java
		int columnIndex = cell.getColumnIndex();
		String sheetName = cell.getSheet().getSheetName();
		String cellColumnName = cell.getSheet().getRow(0).getCell(columnIndex).toString();

		String err = "";

		if (cell.getCellType() == Cell.CELL_TYPE_BLANK && errType.equals("EMPTY")) {
			err = "The value from column name '" + cellColumnName + "', row number '" + rowNumber + "' in sheet '" + sheetName + "' should not be blank.";
			isValid = false;
			setErrorMessage(err);
			throw new UploadedFileException(err);
		}

		if (errType.equals("INVALID_ID")) {
			err = "Can't fetch data for related ID for column name '" + cellColumnName + "', row number '" + rowNumber + "' in sheet '" + sheetName
					+ "'. \nUploading unsuccessful.";
			isValid = false;
			setErrorMessage(err);
			throw new UploadedFileException(err);
		}

		if (errType.equals("INCORRECT_EXCELID")) {
			err = "Excel File ID already exist. \nPlease change value in column name '" + cellColumnName + "', row number '" + rowNumber + "' in sheet '" + sheetName
					+ "'. \nUploading unsuccessful.";
			isValid = false;
			setErrorMessage(err);
			throw new UploadedFileException(err);
		}

		if (errType.equals("DIFFERENT_EXCELID")) {
			err = "Excel File ID value must be the same for all Insured Person in uploading file. \nPlease change value in column name '" + cellColumnName + "', row number '"
					+ rowNumber + "' in sheet '" + sheetName + "'. \nUploading unsuccessful.";
			isValid = false;
			setErrorMessage(err);
			throw new UploadedFileException(err);
		}

		// Removed BMI validation for excel
		// if (errType.equals("INVALID_BMI")) {
		// err = "BMI value should be between 16-30. \nPlease change provided
		// weight and hight value in row number '"
		// + rowNumber + "' in sheet '" + sheetName + "'. \nUploading
		// unsuccessful.";
		// isValid = false;
		// setErrorMessage(err);
		// throw new UploadedFileException(err);
		// }

		return isValid;
	}

	private Date getEndDate(Date startDate, PeriodType periodType) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		if (periodType == PeriodType.YEAR) {
			cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
		} else if (periodType == PeriodType.MONTH) {
			cal.add(Calendar.MONTH, insuredPersonInfoDTO.getPeriodOfYears());
		} else if (periodType == PeriodType.WEEK) {
			cal.add(Calendar.DAY_OF_MONTH, insuredPersonInfoDTO.getPeriodOfYears() * 7);
		}

		return cal.getTime();
	}

	private List<InsuredPersonInfoDTO> getInsuredPersonInfoDTOListFromExcel(XSSFWorkbook workbook) {
		// get first sheet
		XSSFSheet sheet = workbook.getSheetAt(0);
		// iterate on rows
		Iterator<Row> rowIterator = sheet.iterator();

		List<InsuredPersonInfoDTO> insuredPersonInfoDTOList = getInsuredPersonInfoDTOList();

		try {
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				// iterate on cells for the current row
				Iterator<Cell> cellIterator = row.cellIterator();

				createNewInsuredPersonInfo();
				Name name = new Name();
				ContentInfo contentInfo = new ContentInfo();
				ResidentAddress residentAddress = new ResidentAddress();
				
				// check if program is reading empty row
//				boolean isFirstCellBlank = row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK;
//				boolean isNotFirstCell = cellIterator.next().getColumnIndex() != 0;
//				if (isFirstCellBlank && isNotFirstCell) {
//					break;
//				}

				// skipping first row which is title
				if (row.getRowNum() > 1) {
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						
						if (checkValidation(cell, "EMPTY")) {
							switch (cell.getColumnIndex()) {
								case 0:
									DataFormatter formatter = new DataFormatter();
									insuredPersonInfoDTO.setTempId(formatter.formatCellValue(cell));
									break;
								case 1:
									insuredPersonInfoDTO.setSameCustomer(cell.getBooleanCellValue());
									break;
								case 2:
									insuredPersonInfoDTO.setInitialId(cell.getStringCellValue());
									break;
								case 3:
									name.setFirstName(cell.getStringCellValue());
									insuredPersonInfoDTO.setName(name);
									break;
								case 4:
									insuredPersonInfoDTO.setFatherName(cell.getStringCellValue());
									break;
								case 5:
									IdType idType = IdType.valueOf(cell.getStringCellValue());
									insuredPersonInfoDTO.setIdType(idType);
									break;
								case 6:
									if (insuredPersonInfoDTO.getIdType().equals(IdType.NRCNO)) {
										insuredPersonInfoDTO.setIdNo(cell.getStringCellValue());
										insuredPersonInfoDTO.setFullIdNo(cell.getStringCellValue());
									} else if (insuredPersonInfoDTO.getIdType().equals(IdType.STILL_APPLYING)) {
										insuredPersonInfoDTO.setIdNo(null);
									} else {
										insuredPersonInfoDTO.setIdNo(cell.getStringCellValue());
									}
									break;
								case 7:
									insuredPersonInfoDTO.setDateOfBirth(cell.getDateCellValue());
									break;
								case 8:
									insuredPersonInfoDTO.setSumInsuredInfo(cell.getNumericCellValue());
									break;
								case 9:
									residentAddress.setResidentAddress(cell.getStringCellValue());
									break;
								case 10:
									break;
								case 11:
									Township township = townshipService.findTownshipById(cell.getStringCellValue());
									if (township == null) {
										checkValidation(cell, "INVALID_ID");
									} else {
										residentAddress.setTownship(township);
										insuredPersonInfoDTO.setResidentAddress(residentAddress);
									}
									break;
								case 12:
									break;
								case 13:
									Occupation occupation = occupationService.findOccupationById(cell.getStringCellValue());
									if (occupation == null) {
										checkValidation(cell, "INVALID_ID");
									} else {
										insuredPersonInfoDTO.setOccupation(occupation);
									}
									break;
								case 14:
									if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
										double phone = cell.getNumericCellValue();
										DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
										df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
										contentInfo.setPhone(String.valueOf(df.format(phone)));
									} else {
										contentInfo.setPhone(cell.getStringCellValue());
									}
									insuredPersonInfoDTO.setContentInfo(contentInfo);
									break;
								case 15:
									Gender gender = Gender.valueOf(cell.getStringCellValue());
									insuredPersonInfoDTO.setGender(gender);
									break;
								case 16:
									PeriodType periodType = PeriodType.valueOf(cell.getStringCellValue());
									this.setPeriodType(periodType);
									this.insuredPersonInfoDTO.setPeriodType(periodType);
									break;
								case 17:
									insuredPersonInfoDTO.setPeriodOfYears((int) cell.getNumericCellValue());
									break;
								case 18:
									insuredPersonInfoDTO.setWeight((int) cell.getNumericCellValue());
									break;
								case 19:
									insuredPersonInfoDTO.setFeets((int) cell.getNumericCellValue());
									break;
								case 20:
									insuredPersonInfoDTO.setInches((int) cell.getNumericCellValue());
									break;
								case 21:
									break;
								case 22:
									KeyFactor coverOption = keyfactorService.findKeyFactorById(cell.getStringCellValue());
									if (coverOption == null) {
										checkValidation(cell, "INVALID_ID");
										break;
									} else {
										simpleLifeKeyfactor = coverOption;
										insuredPersonInfoDTO.setProduct(product);
										// calculatePounds();
										insuredPersonInfoDTO.setStartDate(new Date());
										setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(),
												insuredPersonInfoDTO.getPeriodOfYears(), insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
										insuredPersonInfoDTO.setEndDate(getEndDate(insuredPersonInfoDTO.getStartDate(), insuredPersonInfoDTO.getPeriodType()));
										// No need BMI calculation for excel
										// calculateBMI();
										// if
										// (!isBmiInRange(insuredPersonInfoDTO.getBmi()))
										// {
										// checkValidation(cell, "INVALID_BMI");
										// }
										insuredPersonInfoDTO.setHeight(insuredPersonInfoDTO.getFeets() * 12 + insuredPersonInfoDTO.getInches());
										insuredPersonInfoDTO.setInsuranceHistoryRecord(getInsuranceHistoryRecordList());
										break;
									}
							}
						}
					}
					insuredPersonInfoDTOList.add(insuredPersonInfoDTO);
				}
			}
		} catch (UploadedFileException e) {
			e.printStackTrace();

			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Upload Failed!", e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return insuredPersonInfoDTOList;
	}

	private List<BeneficiariesInfoDTO> getBeneficiariesInfoDTOListFromExcel(XSSFWorkbook workbook) {
		// get first sheet
		XSSFSheet sheet = workbook.getSheetAt(1);
		// iterate on rows
		Iterator<Row> rowIterator = sheet.iterator();

		List<BeneficiariesInfoDTO> beneficiaryInfoDTOList = new ArrayList<BeneficiariesInfoDTO>();

		try {
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				// iterate on cells for the current row
				Iterator<Cell> cellIterator = row.cellIterator();

				createNewBeneficiariesInfo();
				Name name = new Name();
				ResidentAddress residentAddress = new ResidentAddress();
				ContentInfo contentInfo = new ContentInfo();
				
				// check if program is reading empty row
//				boolean isFirstCellBlank = row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK;
//				boolean isNotFirstCell = cellIterator.next().getColumnIndex() != 0;
//				if (isFirstCellBlank && isNotFirstCell) {
//					break;
//				}
				
				if (row.getRowNum() != 0) {
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						
						if (checkValidation(cell, "EMPTY")) {
							switch (cell.getColumnIndex()) {
								case 0:
									DataFormatter formatter = new DataFormatter();
									beneficiariesInfoDTO.setTempId(formatter.formatCellValue(cell));
									break;
								case 1:
									beneficiariesInfoDTO.setInitialId(cell.getStringCellValue());
									break;
								case 2:
									name.setFirstName(cell.getStringCellValue());
									beneficiariesInfoDTO.setName(name);
									break;
								case 3:
									IdType idType = IdType.valueOf(cell.getStringCellValue());
									beneficiariesInfoDTO.setIdType(idType);
									break;
								case 4:
									if (beneficiariesInfoDTO.getIdType().equals(IdType.NRCNO)) {
										beneficiariesInfoDTO.setFullIdNo(cell.getStringCellValue());
									} else if (beneficiariesInfoDTO.getIdType().equals(IdType.STILL_APPLYING)) {
										beneficiariesInfoDTO.setIdNo(null);
									} else {
										beneficiariesInfoDTO.setIdNo(cell.getStringCellValue());
									}
									break;
								case 5:
									beneficiariesInfoDTO.setDateOfBirth(cell.getDateCellValue());
									calculateAgeForBene();
									break;
								case 6:
									beneficiariesInfoDTO.setPercentage((float) cell.getNumericCellValue());
									break;
								case 7:
									break;
								case 8:
									RelationShip relationship = relationShipService.findRelationShipById(cell.getStringCellValue());
									if (relationship == null) {
										checkValidation(cell, "INVALID_ID");
										break;
									} else {
										beneficiariesInfoDTO.setRelationship(relationship);
										break;
									}
								case 9:
									if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
										double phone = cell.getNumericCellValue();
										DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
										df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
										contentInfo.setPhone(df.format(phone));
									} else {
										contentInfo.setPhone(cell.getStringCellValue());
									}
									beneficiariesInfoDTO.setContentInfo(contentInfo);
									break;
								case 10:
									Gender gender = Gender.valueOf(cell.getStringCellValue());
									beneficiariesInfoDTO.setGender(gender);
									break;
								case 11:
									residentAddress.setResidentAddress(cell.getStringCellValue());
									break;
								case 12:
									break;
								case 13:
									Township township = townshipService.findTownshipById(cell.getStringCellValue());
									if (township == null) {
										checkValidation(cell, "INVALID_ID");
										break;
									} else {
										residentAddress.setTownship(township);
										beneficiariesInfoDTO.setResidentAddress(residentAddress);
										break;
									}
							}
						}
					}
					beneficiaryInfoDTOList.add(beneficiariesInfoDTO);
				}
			}
		} catch (UploadedFileException e) {
			e.printStackTrace();

			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Upload Failed!", e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return beneficiaryInfoDTOList;
	}

	private void setCustomerForExcelUpload(InsuredPersonInfoDTO insuredPersonDTO) {

		/*
		 * find customer by insured person nrc if exist, add customer obj to
		 * insured person if doesn't, create new customer obj and add it to
		 * insured person
		 */
		Customer insuredCustomer = customerService.findCustomerByNrc(insuredPersonDTO.getFullIdNo());

		if (insuredCustomer == null) {
			insuredCustomer = new Customer(insuredPersonDTO);
		}

		// set organization back to original
		if (lifeProposalOrganization != null) {
			lifeProposal.setOrganization(lifeProposalOrganization);
		}

		if (lifeProposal.getOrganization() == null) {

			if (proposedCustomer == null) {
				proposedCustomer = lifeProposal.getCustomer();
			}

			// if same customer
			if (insuredPersonDTO.isSameCustomer()) {
				lifeProposal.setCustomer(insuredCustomer);
				insuredPersonDTO.setCustomer(insuredCustomer);
			} else {
				lifeProposal.setCustomer(proposedCustomer);
				insuredPersonDTO.setCustomer(insuredCustomer);
			}

		} else {
			if (lifeProposalOrganization == null) {
				lifeProposalOrganization = lifeProposal.getOrganization();
			}

			if (insuredPersonDTO.isSameCustomer()) {
				lifeProposal.setCustomer(insuredCustomer);
				lifeProposal.setOrganization(null);
				insuredPersonDTO.setCustomer(insuredCustomer);
			} else {
				lifeProposal.setCustomer(null);
				lifeProposal.setOrganization(lifeProposalOrganization);
				insuredPersonDTO.setCustomer(insuredCustomer);
			}
		}
	}

	public String addNewLifeProposal() {
		String result = null;

		try {
			if (this.isExcelUpload) {

				this.insuredPersonDTOList.forEach(insuredPersonDTO -> {

					if (lifeProposal.getProposalNo() != null)
						lifeProposal.setProposalNo(null);
					if (lifeProposal.getId() != null)
						lifeProposal.setId(null);

					List<ProposalInsuredPerson> proposalInsuredPersonList = new ArrayList<ProposalInsuredPerson>();
					setCustomerForExcelUpload(insuredPersonDTO);

					ProposalInsuredPerson proposalInsuredPerson = new ProposalInsuredPerson(insuredPersonDTO, lifeProposal);

					// setting periods
					if (isSimpleLife) {
						if (proposalInsuredPerson.getPeriodType().equals(PeriodType.YEAR)) {
							proposalInsuredPerson.setPeriodYear(insuredPersonDTO.getPeriodOfYears());
							proposalInsuredPerson.setPeriodMonth(0);
							proposalInsuredPerson.setPeriodWeek(0);
							proposalInsuredPerson.setPeriodType(insuredPersonDTO.getPeriodType());
						} else if (proposalInsuredPerson.getPeriodType().equals(PeriodType.MONTH)) {
							proposalInsuredPerson.setPeriodMonth(insuredPersonDTO.getPeriodOfYears());
							proposalInsuredPerson.setPeriodYear(0);
							proposalInsuredPerson.setPeriodWeek(0);
							proposalInsuredPerson.setPeriodType(insuredPersonDTO.getPeriodType());
						} else if (proposalInsuredPerson.getPeriodType().equals(PeriodType.WEEK)) {
							proposalInsuredPerson.setPeriodWeek(insuredPersonDTO.getPeriodOfYears());
							proposalInsuredPerson.setPeriodMonth(0);
							proposalInsuredPerson.setPeriodYear(0);
							proposalInsuredPerson.setPeriodType(insuredPersonDTO.getPeriodType());
						}
					}

					proposalInsuredPersonList.add(proposalInsuredPerson);

//					lifeProposal.setStaffPlan(selectItem);
//					if (selectItem == true) {
//						saveEipsData();
//						lifeProposal.setEips(eips);
//					}

					lifeProposal.setInsuredPersonList(proposalInsuredPersonList);

					// setting audit log in life proposal
					this.auditLog.setOrganization(lifeProposalOrganization);
					lifeProposal.setAuditLog(auditLog);

					WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
							: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
									: isPublicTermLife ? WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL
											: isSimpleLife ? WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL
													: isSinglePremiumEndowmentLife ? WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
															: isSinglePremiumCreditLife ? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
																	: isShortTermSinglePremiumCreditLife ? WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
																			: isShortTermEndowment ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL
																					: WorkflowReferenceType.LIFE_PROPOSAL;
					WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_SURVEY : isSportMan ? WorkflowTask.APPROVAL : WorkflowTask.SURVEY;
					WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
					if (isEndorse) {
						lifeProposal.setProposalType(ProposalType.ENDORSEMENT);
						if (isSTELendorse) {
							lifeProposal = lifeEndorsementService.addNewShortEndowLifeProposal(lifeProposal, workFlowDTO, RequestStatus.PROPOSED.name());
						} else {
							lifeProposal = lifeEndorsementService.addNewLifeProposal(lifeProposal, workFlowDTO, RequestStatus.PROPOSED.name());
						}
					} else {
						lifeProposal.setProposalType(ProposalType.UNDERWRITING);
						lifeProposal = lifeProposalService.addNewLifeProposal(lifeProposal, workFlowDTO, RequestStatus.PROPOSED.name(), bancaassuranceProposal);
					}
//					if (selectItem == true) {
//						eips.setAmount(calculateEipsAmount());
//						eipsService.save(eips);
//					}
				});

				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.EXCEL_PROPOSAL, lifeProposal.getAuditLog().getExcelFileId());

			} else {
//				lifeProposal.setStaffPlan(selectItem);
//				if (selectItem == true) {
//					saveEipsData();
//					lifeProposal.setEips(eips);
//				}

				lifeProposal.setInsuredPersonList(getInsuredPersonInfoList());
				WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
						: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
								: isPublicTermLife ? WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL
										: isSimpleLife ? WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL
												: isSinglePremiumEndowmentLife ? WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
														: isSinglePremiumCreditLife ? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
																: isShortTermSinglePremiumCreditLife ? WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
																		: isShortTermEndowment ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL
																				: WorkflowReferenceType.LIFE_PROPOSAL;
				WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_SURVEY : isSportMan ? WorkflowTask.APPROVAL : WorkflowTask.SURVEY;
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
				if (isEndorse) {
					lifeProposal.setProposalType(ProposalType.ENDORSEMENT);
					if (isSTELendorse) {
						lifeProposal = lifeEndorsementService.addNewShortEndowLifeProposal(lifeProposal, workFlowDTO, RequestStatus.PROPOSED.name());
					} else {
						lifeProposal = lifeEndorsementService.addNewLifeProposal(lifeProposal, workFlowDTO, RequestStatus.PROPOSED.name());
					}
				} else {
					lifeProposal.setProposalType(ProposalType.UNDERWRITING);
					lifeProposal = lifeProposalService.addNewLifeProposal(lifeProposal, workFlowDTO, RequestStatus.PROPOSED.name(), bancaassuranceProposal);
				}
//				if (selectItem == true) {
//					eips.setAmount(calculateEipsAmount());
//					eipsService.save(eips);
//				}

				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			}

			createNewLifeProposal();
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("InsuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
			int personSize = getInsuredPersonInfoDTOList().size();
			if (personSize < 1) {
				addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", MessageId.REQUIRED_INSURED_PERSION);
				valid = false;
			} else {
				if (KeyFactorChecker.isGroupLife(product)) {
					if (personSize < 5) {
						addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", MessageId.REQUIRED_GROUPLIFE_INSURED_PERSON, 5);
						valid = false;
					}
				} else if (!(KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product)) && !this.isExcelUpload) {
					if (personSize > 1) {
						addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", MessageId.INVALID_INSURED_PERSON, product.getName());
						valid = false;
					}
				}
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public Map<String, BeneficiariesInfoDTO> getBeneficiariesInfoDTOMap() {
		return beneficiariesInfoDTOMap;
	}

	public String getPublicLifeId() {
		return ProductIDConfig.getPublicLifeId();
	}

	public String getGroupLifeId() {
		return ProductIDConfig.getGroupLifeId();
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	private boolean checkPublicLife() {
		Boolean isPublicLife = true;
		String productId = lifepolicy.getInsuredPersonInfo().get(0).getProduct().getId();
		if (productId.equals(getPublicLifeId())) {
			isPublicLife = true;
		} else {
			isPublicLife = false;
		}
		return isPublicLife;
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

	public Boolean isChangePeriod() {
		if (insuredPersonInfoDTO.getPeriodOfYears() != lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears()) {
			return true;
		}
		return false;
	}

	public Boolean isIncreasedSIAmount(InsuredPersonInfoDTO dto) {
		if (dto.getSumInsuredInfo() <= lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()) {
			return false;
		}
		return true;
	}

	public Boolean isReplaceColumn() {
		if (lifepolicy != null && checkPublicLife() == false && !isSTELendorse) {
			return true;
		}
		return false;
	}

	public Boolean isDecreasedSIAmount() {
		if (insuredPersonInfoDTO.getInsPersonCodeNo() != null) {
			PolicyInsuredPerson policyInsuPerson = lifePolicyService.findInsuredPersonByCodeNo(insuredPersonInfoDTO.getInsPersonCodeNo());
			if (insuredPersonInfoDTO.getSumInsuredInfo() < policyInsuPerson.getSumInsured()) {
				int passedMonths = getPassedMonths();
				int passedYear = passedMonths / 12;
				int period = insuredPersonInfoDTO.getPeriodOfYears();
				if ((period <= 12 && passedYear >= 2) || period > 12 && passedYear >= 3) {
					if (passedMonths % 12 > 5) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isBmiInRange(double bmiValue) {

		boolean result = false;
		if (bmiValue >= 16 && bmiValue <= 30) {
			result = true;
		}
		return result;
	}

	public void addNewInsuredPersonInfo() {
		if (createNewIsuredPersonInfo) {
			saveInsuredPersonInfo();
		} else {
			if (isEndorse && isEdit && isDecreasedSIAmount() && checkPublicLife()) {
				PrimeFaces.current().executeScript("PF('paidPremiumConfirmationDialog').show()");
			} else {
				saveInsuredPersonInfo();
			}
		}
	}

	public void paidPremiumForPaidUp(Boolean isPaid) {
		if (isPaid) {
			insuredPersonInfoDTO.setIsPaidPremiumForPaidup(true);
		} else {
			insuredPersonInfoDTO.setIsPaidPremiumForPaidup(false);
		}
		saveInsuredPersonInfo();
	}

	public int getPassedMonths() {
		DateTime vDate = new DateTime(lifeProposal.getLifePolicy().getActivedPolicyEndDate());
		DateTime cDate = new DateTime(lifeProposal.getLifePolicy().getCommenmanceDate());
		int paymentType = lifeProposal.getLifePolicy().getPaymentType().getMonth();
		int passedMonths = Months.monthsBetween(cDate, vDate).getMonths();
		if (paymentType > 0) {
			if (passedMonths % paymentType != 0) {
				passedMonths = passedMonths + 1;
			} // if paymentType is Lumpsum
		} else if (paymentType == 0) {
			if (passedMonths % 12 != 0) {
				passedMonths = passedMonths + 1;
			}
		}
		return passedMonths;
	}

	public int getPassedYears() {
		return getPassedMonths() / 12;
	}

	private void setKeyFactorValue(double sumInsured, int age, int period, PeriodType periodType, KeyFactor coverOption) {
		for (InsuredPersonKeyFactorValue vehKF : insuredPersonInfoDTO.getKeyFactorValueList()) {
			KeyFactor kf = vehKF.getKeyFactor();
			if (KeyFactorChecker.isSumInsured(kf)) {
				vehKF.setValue(sumInsured + "");
			} else if (KeyFactorChecker.isAge(kf)) {
				vehKF.setValue(age + "");
			} else if (KeyFactorChecker.isMedicalAge(kf)) {
				vehKF.setValue(age + "");
			} else if (KeyFactorChecker.isAgeFromTo(kf)) {
				vehKF.setValue(age + "");
			} else if (KeyFactorChecker.isTerm(kf)) {
				if (null == insuredPersonInfoDTO.getPeriodType() || insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
					vehKF.setValue(period + "");
				} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH) || insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
					vehKF.setValue(0 + "");
				} else {
					vehKF.setValue(period + "");
				}
			} else if (KeyFactorChecker.isMonth(kf)) {
				if (null == insuredPersonInfoDTO.getPeriodType() || insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
					vehKF.setValue(period + "");
				} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR) || insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
					vehKF.setValue(0 + "");
				}
			} else if (KeyFactorChecker.isWeek(kf)) {
				if (null == insuredPersonInfoDTO.getPeriodType() || insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
					vehKF.setValue(period + "");
				} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH) || insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
					vehKF.setValue(0 + "");
				}
			} else if (KeyFactorChecker.isCoverOption(kf)) {
				vehKF.setValue(coverOption.getValue());
			}
		}
	}

	public int getAgeForNextYearEndose() {
		Calendar cal_1 = Calendar.getInstance();
		int currentYear = cal_1.get(Calendar.YEAR);
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(insuredPersonInfoDTO.getDateOfBirth());
		cal_2.set(Calendar.YEAR, currentYear);
		if (lifepolicy == null) {
			cal_1.get(Calendar.YEAR);
		} else {
			cal_1.setTime(lifepolicy.getCommenmanceDate());
		}
		if (new Date().after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(insuredPersonInfoDTO.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(insuredPersonInfoDTO.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}

	public void selectUser() {
		WorkFlowType workFlowType = isPersonalAccident ? WorkFlowType.PERSONAL_ACCIDENT
				: isFarmer ? WorkFlowType.FARMER
						: isSimpleLife ? WorkFlowType.SIMPLE_LIFE
								: isShortTermEndowment ? WorkFlowType.SHORT_ENDOWMENT
										: isSinglePremiumCreditLife ? WorkFlowType.SINGLE_PREMIUM_CREDIT_LIFE
												: isSinglePremiumEndowmentLife ? WorkFlowType.SINGLE_PREMIUM_ENDOWMENT_LIFE
														: isShortTermSinglePremiumCreditLife ? WorkFlowType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE : WorkFlowType.LIFE;
		WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_SURVEY : isSportMan ? WorkflowTask.APPROVAL : WorkflowTask.SURVEY;
		selectUser(workflowTask, workFlowType);
	}

	public SalutationType[] getSalutationTypes() {
		return SalutationType.values();
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void removeEntity() {
		entitys = null;
		lifeProposal.setBranch(null);
		lifeProposal.setSalePoint(null);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(lifeProposal.getBranch() == null ? "" : lifeProposal.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void selectBancaBranch() {
		selectBancaBranchByChannel(bancaassuranceProposal.getChannel() == null ? "" : bancaassuranceProposal.getChannel().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

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

	public List<BancaMethod> getBancaMethodList() {
		return bancaMethodList;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<PaymentType> getPaymentTypes() {
		if (product == null) {
			return new ArrayList<PaymentType>();
		} else {
			return product.getPaymentTypeList();
		}
	}

	public int getMaximumTrem() {
		int result = 0;
		if (product != null) {
			result = isMonthBase ? product.getMaxTerm() : product.getMaxTerm() / 12;
		}
		return result;
	}

	public int getMaximumTremWeek() {
		return maximumTremWeek;
	}

	public int getMunimumTremWeek() {
		return munimumTremWeek;
	}

	public void changePeriodType(AjaxBehaviorEvent event) {

		if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
			this.isPeriodOfYear = true;
			this.isPeriodofMonth = false;
			this.isPeriodofWeek = false;
			insuredPersonInfoDTO.setPeriodOfYears(0);
		} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
			this.isPeriodofMonth = true;
			this.isPeriodOfYear = false;
			this.isPeriodofWeek = false;
			insuredPersonInfoDTO.setPeriodOfYears(0);

		} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
			this.isPeriodofWeek = true;
			this.isPeriodOfYear = false;
			this.isPeriodofMonth = false;
			insuredPersonInfoDTO.setPeriodOfYears(0);
		}

	}

	public void onSIOCase(AjaxBehaviorEvent event) {

		if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH) && insuredPersonInfoDTO.getPeriodOfYears() <= 1 && insuredPersonInfoDTO.getSumInsuredInfo() > 1000000.0) {
			isSIOcase = true;

		} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH) && insuredPersonInfoDTO.getPeriodOfYears() > 1
				&& insuredPersonInfoDTO.getSumInsuredInfo() > 2000000.0) {
			isSIOcase = true;
		} else {
			isSIOcase = false;
		}

	}

	public boolean isSIOcase() {
		return isSIOcase;
	}

	public void setSIOcase(boolean isSIOcase) {
		this.isSIOcase = isSIOcase;
	}

	public Map<String, String> getPeriodOfInsur() {
		return periodOfInsur;
	}

	public void setPeriodOfInsur(Map<String, String> periodOfInsur) {
		this.periodOfInsur = periodOfInsur;
	}

	public int getMunimumTrem() {
		int result = 0;
		if (product != null) {
			result = isMonthBase ? product.getMinTerm() : product.getMinTerm() / 12;
		}
		return result;
	}

	public void selectAddOn() {
		selectAddOn(product);
	}

	public void calculateAgeForBene() {
		beneficiariesInfoDTO.setAge(getAgeForBeneNextYear());
	}

	public void calculateBMI() {
		insuredPersonInfoDTO.setBmi(getBMI());
	}

	public int getAgeForBeneNextYear() {
		Calendar cal_1 = Calendar.getInstance();
		int currentYear = cal_1.get(Calendar.YEAR);
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(beneficiariesInfoDTO.getDateOfBirth());
		cal_2.set(Calendar.YEAR, currentYear);

		if (new Date().after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(beneficiariesInfoDTO.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(beneficiariesInfoDTO.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}

	public double getBMI() {

		// old way of getting bmi
		// if (insuredPersonInfoDTO.getHeight() < 58) {
		// insuredPersonInfoDTO.setHeight(58);
		// } else if (insuredPersonInfoDTO.getHeight() > 72) {
		// insuredPersonInfoDTO.setHeight(72);
		// }
		// if (insuredPersonInfoDTO.getAgeForNextYear() < 20) {
		// insuredPersonInfoDTO.setAge(20);
		// } else if (insuredPersonInfoDTO.getAgeForNextYear() > 45) {
		// insuredPersonInfoDTO.setAge(45);
		// } else {
		// insuredPersonInfoDTO.setAge(insuredPersonInfoDTO.getAgeForNextYear());
		// }

		// 1 inch = 0.08333 feet
		double heightInFeetDbl = insuredPersonInfoDTO.getFeets() + (insuredPersonInfoDTO.getInches() * 0.08333);
		// long heightInFeet = Math.round(heightInFeetDbl);

		// 1lb = 0.45kg
		double weightInKg = insuredPersonInfoDTO.getWeight() * 0.45;

		// 1ft = 0.3048 meter
		double heightInMeter = heightInFeetDbl * 0.3048;

		// long bmiWeight = Math.round(weightInKg / (heightInMeter *
		// heightInMeter));
		double bmiWeight = weightInKg / (heightInMeter * heightInMeter);

		if (insuredPersonInfoDTO.getFeets() == 0 || insuredPersonInfoDTO.getWeight() == 0 || bmiWeight == 0) {
			return 0;
		}

		DecimalFormat df = new DecimalFormat("#.##");
		String bmiWeightStr = df.format(bmiWeight);

		// storing height in inches at below formula
		insuredPersonInfoDTO.setHeight(insuredPersonInfoDTO.getFeets() * 12 + insuredPersonInfoDTO.getInches());

		return Double.parseDouble(bmiWeightStr);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		customer.setInitialId(customer.getInitialId().replaceAll(" ", ""));
		lifeProposal.setCustomer(customer);
		// if (!isSTELendorse && !isSinglePremiumCreditLife) {
		if (!isSTELendorse) {
			fillInsuredPersonInfoDTO();
		}
	}

	public void returnBeneCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();

		beneficiariesInfoDTO.setInitialId(customer.getInitialId());
		beneficiariesInfoDTO.setName(customer.getName());
		beneficiariesInfoDTO.setIdType(customer.getIdType());
		beneficiariesInfoDTO.setGender(customer.getGender());
		beneficiariesInfoDTO.setDateOfBirth(customer.getDateOfBirth());
		beneficiariesInfoDTO.setAge(customer.getAgeForNextYear());
		beneficiariesInfoDTO.setResidentAddress(customer.getResidentAddress());
		beneficiariesInfoDTO.setContentInfo(customer.getContentInfo());
		beneficiariesInfoDTO.setCustomer(customer);

		beneficiariesInfoDTO.setFullIdNo(customer.getFullIdNo());
		beneficiariesInfoDTO.loadFullIdNo();
		this.beneficiariesInfoDTO = new BeneficiariesInfoDTO(beneficiariesInfoDTO);
		changeBeneStateCode();
	}

	public void returnBeneOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();

		beneficiariesInfoDTO.setOrganization(organization);
	}

	public void returnBeneBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		beneficiariesInfoDTO.getOrganization().setBranch(branch);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		lifeProposal.setSalePoint(salePoint);
	}

	private void fillInsuredPersonInfoDTO() {
		if (isSimpleLife && !organization) {
			this.isDisabled = true;
		}
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		Customer customer = lifeProposal.getCustomer();
		insuredPersonInfoDTO.setInitialId(customer.getInitialId());
		insuredPersonInfoDTO.setName(customer.getName());
		insuredPersonInfoDTO.setFatherName(customer.getFatherName());
		insuredPersonInfoDTO.setIdType(customer.getIdType());
		insuredPersonInfoDTO.setIdNo(customer.getFullIdNo());
		insuredPersonInfoDTO.setFullIdNo(customer.getFullIdNo());
		insuredPersonInfoDTO.loadFullIdNo();
		changeStateCode();
		insuredPersonInfoDTO.setDateOfBirth(customer.getDateOfBirth());
		insuredPersonInfoDTO.setResidentAddress(customer.getResidentAddress());
		insuredPersonInfoDTO.setOccupation(customer.getOccupation());
		insuredPersonInfoDTO.setGender(customer.getGender());

	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		lifeProposal.setOrganization(organization);
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

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		branchId = branch.getId();
		lifeProposal.setBranch(branch);
		lifeProposal.setSalePoint(null);
	}

//	public void selectBancaBrm() {
//		selectBancaBRM(bancaassuranceProposal.getBancaBranch().getId());
//	}

//	public void selectBancaReferralByOTC() {
//		selectBancaReferralByOTC(bancaassuranceProposal.getBancaBranch().getId());
//	}

//	public void selectBancaReferral() {
//		selectBancaReferral(bancaassuranceProposal.getBancaBranch().getId());
//	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		bancaassuranceProposal.setChannel(channel);
		bancaassuranceProposal.setBancaBranch(null);
		bancaassuranceProposal.setBancaMethod(null);
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		bancaassuranceProposal.setBancaBranch(bancaBranch);
	}

	public void returnBancaBrm(SelectEvent event) {
		BancaBRM bancaBrm = (BancaBRM) event.getObject();
		bancaassuranceProposal.setBancaBRM(bancaBrm);
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);
	}

	public void returnBancaLIC(SelectEvent event) {
		BancaLIC bancaLIC = (BancaLIC) event.getObject();
		bancaassuranceProposal.setBancaLIC(bancaLIC);
	}

	public void returnBancaRefferal(SelectEvent event) {
		BancaRefferal bancaRefferal = (BancaRefferal) event.getObject();
		bancaassuranceProposal.setBancaRefferal(bancaRefferal);
	}

	public void returnInsuredPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		insuredPersonInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonInfoDTO.setOccupation(occupation);
	}

	public void returnRiskyOccupation(SelectEvent event) {
		RiskyOccupation riskyOccupation = (RiskyOccupation) event.getObject();
		insuredPersonInfoDTO.setRiskyOccupation(riskyOccupation);
	}

	public void returnTypesOfSport(SelectEvent event) {
		TypesOfSport typesOfSport = (TypesOfSport) event.getObject();
		insuredPersonInfoDTO.setTypesOfSport(typesOfSport);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (entitys != null && !entity.getId().equals(entitys.getId())) {
			lifeProposal.setBranch(null);
			lifeProposal.setSalePoint(null);
		}
		this.entitys = entity;
	}

	public void returnCoinsuranceCompany(SelectEvent event) {
		CoinsuranceCompany coinsuranceCompany = (CoinsuranceCompany) event.getObject();
		insuranceHistoryRecord.setCompany(coinsuranceCompany);
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

	public boolean isDisableInsureInfo() {
		return getInsuredPersonInfoDTOList().size() > 0 && createNewIsuredPersonInfo;
	}

	public boolean isEditInsuHistRec() {
		return editInsuHistRec;
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

	public void returnAddOn(SelectEvent event) {
		List<AddOn> addOnList = (List<AddOn>) event.getObject();
		if (!addOnList.isEmpty()) {
			for (AddOn addOn : addOnList) {
				insuredPersonAddOnDTO.setAddOn(addOn);
				boolean flag = true;
				addon: for (InsuredPersonAddOnDTO fa : insuredPersonAddOnDTOMap.values()) {
					if (fa.getAddOn().getId().equals(insuredPersonAddOnDTO.getAddOn().getId())) {
						flag = false;
						break addon;
					}
				}
				if (flag) {
					insuredPersonAddOnDTOMap.put(insuredPersonAddOnDTO.getTempId(), insuredPersonAddOnDTO);
				}
				createNewInsurePersonAddOnDTO();
			}
		}

		if (!insuredPersonAddOnDTOMap.isEmpty()) {
			insuredPersonInfoDTO.setInsuredPersonAddOnDTOList(new ArrayList<InsuredPersonAddOnDTO>(insuredPersonAddOnDTOMap.values()));
		}
	}

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCodeNo(insuredPersonInfoDTO.getProvinceCode());
	}

	public void changeBeneStateCode() {
		benTownshipCodeList = townshipCodeService.findByStateCodeNo(beneficiariesInfoDTO.getProvinceCode());
	}

	public void createNewInsurePersonAddOnDTO() {
		insuredPersonAddOnDTO = new InsuredPersonAddOnDTO();
	}

	public List<Product> getProductsList() {
		return productsList;
	}

	public boolean getIsShortTermEndowment() {
		return isShortTermEndowment;
	}

	public boolean getIsPublicTermLife() {
		return isPublicTermLife;
	}

	public boolean getIsSinglePremiumEndowmentLife() {
		return isSinglePremiumEndowmentLife;
	}

	public boolean getIsSinglePremiumCreditLife() {
		return isSinglePremiumCreditLife;
	}

	public boolean getIsShortTermSinglePremiumCreditLife() {
		return isShortTermSinglePremiumCreditLife;
	}

	public boolean getIsSimpleLife() {
		return isSimpleLife;
	}

	/*
	 * public boolean getIsPeriodOfInsur() {
	 * 
	 * if(insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
	 * isPeriodOfYear = true; return isPeriodOfYear; }else
	 * if(insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
	 * isPeriodofWeek = true; return isPeriodofWeek; }else{isPeriodofMonth =
	 * true; return isPeriodofMonth; }
	 * 
	 * }
	 */

	public String getPageHeader() {
		return (isEndorse ? (isSTELendorse ? "Short Term Endowment Life Endorsement" : "Life Endorsement")
				: isFarmer ? "Add New Farmer"
						: isPublicTermLife ? "Add New PublicTermLife "
								: isSinglePremiumEndowmentLife ? "Add New Single Premium Endowment"
										: isSinglePremiumCreditLife ? "Add New Single Premium Credit"
												: isShortTermSinglePremiumCreditLife ? "Add New Short Term Single Premium Credit Life"
														: isSimpleLife ? "Add New Simple Life" : isPersonalAccident ? "Add New Personal Accident" : "Add New Life")
				+ " Proposal";
	}

	public boolean isSTELendorse() {
		return isSTELendorse;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public InsuredPersonInfoDTO getOldInsuredPersonInfoDTO() {
		return oldInsuredPersonInfoDTO;
	}

	public void setOldInsuredPersonInfoDTO(InsuredPersonInfoDTO oldInsuredPersonInfoDTO) {
		this.oldInsuredPersonInfoDTO = oldInsuredPersonInfoDTO;
	}

	public BeneficiariesInfoDTO getOldBeneficiariesInfoDTO() {
		return oldBeneficiariesInfoDTO;
	}

	public void setOldBeneficiariesInfoDTO(BeneficiariesInfoDTO oldBeneficiariesInfoDTO) {
		this.oldBeneficiariesInfoDTO = oldBeneficiariesInfoDTO;
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

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public List<TownshipCode> getBenTownshipCodeList() {
		return benTownshipCodeList;
	}

	public IdConditionType[] getIdConditionType() {
		return IdConditionType.values();
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

	public boolean isPublicTermLife() {
		return isPublicTermLife;
	}

	public boolean isChannel() {
		return isChannel;
	}

	public void setChannel(boolean isChannel) {
		this.isChannel = isChannel;
	}

	public boolean isPeriodOfYear() {
		return isPeriodOfYear;
	}

	public void setPeriodOfYear(boolean isPeriodOfYear) {
		this.isPeriodOfYear = isPeriodOfYear;
	}

	public boolean isPeriodofWeek() {
		return isPeriodofWeek;
	}

	public void setPeriodofWeek(boolean isPeriodofWeek) {
		this.isPeriodofWeek = isPeriodofWeek;
	}

	public boolean isPeriodofMonth() {
		return isPeriodofMonth;
	}

	public void setPeriodofMonth(boolean isPeriodofMonth) {
		this.isPeriodofMonth = isPeriodofMonth;
	}

	public boolean isSinglePremiumEndowmentLife() {
		return isSinglePremiumEndowmentLife;
	}

	public boolean isSinglePremiumCreditLife() {
		return isSinglePremiumCreditLife;
	}

	public boolean isShortTermSinglePremiumCreditLife() {
		return isShortTermSinglePremiumCreditLife;
	}

	public InsuranceHistoryRecord getInsuranceHistoryRecord() {
		return insuranceHistoryRecord;
	}

	public void setInsuranceHistoryRecord(InsuranceHistoryRecord insuranceHistoryRecord) {
		this.insuranceHistoryRecord = insuranceHistoryRecord;
	}

	public Map<String, InsuranceHistoryRecord> getInsuranceHistoryRecordMap() {
		return insuranceHistoryRecordMap;
	}

	public void setInsuranceHistoryRecordMap(Map<String, InsuranceHistoryRecord> insuranceHistoryRecordMap) {
		this.insuranceHistoryRecordMap = insuranceHistoryRecordMap;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public BancaMethod getBancaMethod() {
		return bancaMethod;
	}

	public boolean isRefferal() {
		return isRefferal;
	}

	// Choose Staff With Organization

	public void selectStaffWithOrganization() {
		staffList = staffService.findStaffWithGGIOrganization(ggiOrganization.getId());
	}

	public void showPercentageWithRelationShip() {

		percentage = percentageService.findPercentageWithRelationShip(relationShipType.getId(), product.getId());
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

	public void setRelationShipTypeService(IRelationShipTypeService relationShipTypeService) {
		this.relationShipTypeService = relationShipTypeService;
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

	public void setRelationShipTypeList(List<RelationShipType> relationShipTypeList) {
		this.relationShipTypeList = relationShipTypeList;
	}

	public void setGgiOrganizationService(IGGIOrganizationService ggiOrganizationService) {
		this.ggiOrganizationService = ggiOrganizationService;
	}

	public void setStaffService(IStaffService staffService) {
		this.staffService = staffService;
	}

	public Percentage getPercentage() {
		return percentage;
	}

	public void setPercentage(Percentage percentage) {
		this.percentage = percentage;
	}

	public void setPercentageService(IPercentageService percentageService) {
		this.percentageService = percentageService;
	}

	public Eips getEips() {
		return eips;
	}

	public void setEips(Eips eips) {
		this.eips = eips;
	}

	public void setEipsService(IEipsService eipsService) {
		this.eipsService = eipsService;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	public void setRelationshipList(List<RelationShip> relationshipList) {
		this.relationshipList = relationshipList;
	}

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	private IdType[] getIdTypeNotSTILL_APPLYING() {
		int i = 0;
		IdType[] arList = new IdType[IdType.values().length - 1];
		for (IdType ar : IdType.values()) {
			if (ar.equals(IdType.STILL_APPLYING))
				continue;
			arList[i] = ar;
			++i;
		}
		return arList;
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
		return (((percentage.getPercent()) / 100) * lifeProposal.getProposalInsuredPersonList().get(0).getBasicTermPremium());
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public List<PeriodType> getPeriodTypes() {
		return Arrays.asList(PeriodType.YEAR, PeriodType.MONTH, PeriodType.WEEK);
	}

	public String getPeriodTypeData() {

		if (isSimpleLife) {
			if (null != insuredPersonInfoDTO.getPeriodType()) {
				return "-".concat(periodType.getLabel());
			}
		} else {
			return isMonthBase ? "-Month" : "-Year";
		}

		return null;

	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isExcelUpload() {
		return isExcelUpload;
	}

	public void setExcelUpload(boolean isExcelUpload) {
		this.isExcelUpload = isExcelUpload;
	}

	public String getUploadMethod() {
		return uploadMethod;
	}

	public void setUploadMethod(String uploadMethod) {
		this.uploadMethod = uploadMethod;
	}

	public AuditLog getAuditLog() {
		return auditLog;
	}

	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

	// public String getExcelFileId() {
	// return excelFileId;
	// }
	//
	// public void setExcelFileId(String excelFileId) {
	// this.excelFileId = excelFileId;
	// }

	public List<InsuredPersonInfoDTO> getInsuredPersonDTOList() {
		return insuredPersonDTOList;
	}

	public void setInsuredPersonDTOList(List<InsuredPersonInfoDTO> insuredPersonDTOList) {
		this.insuredPersonDTOList = insuredPersonDTOList;
	}

	public AddOnService getAddOnService() {
		return addOnService;
	}

	public void setAddOnService(AddOnService addOnService) {
		this.addOnService = addOnService;
	}

	public List<KeyFactor> getSimpleLifeKeyfactorList() {
		return simpleLifeKeyfactorList;
	}

	public void setSimpleLifeKeyfactorList(List<KeyFactor> simpleLifeKeyfactorList) {
		this.simpleLifeKeyfactorList = simpleLifeKeyfactorList;
	}

	public KeyFactor getSimpleLifeKeyfactor() {
		return simpleLifeKeyfactor;
	}

	public void setSimpleLifeKeyfactor(KeyFactor simpleLifeKeyfactor) {
		this.simpleLifeKeyfactor = simpleLifeKeyfactor;
	}

	public boolean isShowSimpleLifeSurvey() {
		return showSimpleLifeSurvey;
	}

	public Organization getLifeProposalOrganization() {
		return lifeProposalOrganization;
	}

	public void setLifeProposalOrganization(Organization lifeProposalOrganization) {
		this.lifeProposalOrganization = lifeProposalOrganization;
	}

	public Customer getProposedCustomer() {
		return proposedCustomer;
	}

	public void setProposedCustomer(Customer proposedCustomer) {
		this.proposedCustomer = proposedCustomer;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

}
