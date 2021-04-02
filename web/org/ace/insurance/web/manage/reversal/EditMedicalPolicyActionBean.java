package org.ace.insurance.web.manage.reversal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.PassportType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.Unit;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.medical.proposal.MedicalPersonHistoryRecord;
import org.ace.insurance.medical.proposal.PersonProductHistory;
import org.ace.insurance.medical.proposal.frontService.proposalService.interfaces.IAddNewMedicalProposalFrontService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.coinsurancecompany.service.interfaces.ICoinsuranceCompanyService;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.service.interfaces.IPaymentTypeService;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.religion.service.interfaces.IReligionService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.ErrorMessage;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.common.ValidationResult;
import org.ace.insurance.web.common.Validator;
import org.ace.insurance.web.common.WebUtils;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonBeneficiaryDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonDTO;
import org.ace.insurance.web.manage.medical.claim.factory.MedicalPolicyFactory;
import org.ace.insurance.web.manage.medical.proposal.CustomerDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProGuardianDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuAddOnDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuDTO;
import org.ace.insurance.web.manage.medical.proposal.PolicyGuardianDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.CustomerFactory;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

/***************************************************************************************
 * @author KMH
 * @Date 2014-08-14
 * @Version 1.0
 * @Purpose This class serves as the Presentation Layer to manipulate the
 *          <code>MedicalProposal</code> underWriting process.
 * 
 ***************************************************************************************/
@ViewScoped
@ManagedBean(name = "EditMedicalPolicyActionBean")
public class EditMedicalPolicyActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PolicyBeneficiariesValidator}")
	private Validator<MedicalPolicyInsuredPersonBeneficiaryDTO> beneficiariesValidator;

	public void setBeneficiariesValidator(Validator<MedicalPolicyInsuredPersonBeneficiaryDTO> beneficiariesValidator) {
		this.beneficiariesValidator = beneficiariesValidator;
	}

	@ManagedProperty(value = "#{ProposalInsuredPersonValidator}")
	private Validator<MedicalPolicyInsuredPersonDTO> insuredpersonValidator;

	public void setInsuredpersonValidator(Validator<MedicalPolicyInsuredPersonDTO> insuredpersonValidator) {
		this.insuredpersonValidator = insuredpersonValidator;
	}

	@ManagedProperty(value = "#{CustomerValidator}")
	private Validator<CustomerDTO> customerValidator;

	public void setCustomerValidator(Validator<CustomerDTO> customerValidator) {
		this.customerValidator = customerValidator;
	}

	@ManagedProperty(value = "#{GuardianPersonValidator}")
	private Validator<PolicyGuardianDTO> guardianValidator;

	public void setGuardianValidator(Validator<PolicyGuardianDTO> guardianValidator) {
		this.guardianValidator = guardianValidator;
	}

	@ManagedProperty(value = "#{ReligionService}")
	protected IReligionService religionService;

	public void setReligionService(IReligionService religionService) {
		this.religionService = religionService;
	}

	@ManagedProperty(value = "#{BankBranchService}")
	protected IBankBranchService bankBranchService;

	public void setBankBranchService(IBankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{AddNewMedicalProposalFrontService}")
	private IAddNewMedicalProposalFrontService addNewMedicalProposalFrontService;

	public void setAddNewMedicalProposalFrontService(IAddNewMedicalProposalFrontService addNewMedicalProposalFrontService) {
		this.addNewMedicalProposalFrontService = addNewMedicalProposalFrontService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{PaymentTypeService}")
	private IPaymentTypeService paymentTypeService;

	public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
		this.paymentTypeService = paymentTypeService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{CoinsuranceCompanyService}")
	private ICoinsuranceCompanyService coinsuranceCompanyService;

	public void setCoinsuranceCompanyService(ICoinsuranceCompanyService coinsuranceCompanyService) {
		this.coinsuranceCompanyService = coinsuranceCompanyService;
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

	@ManagedProperty(value = "#{SalePointService}")
	private ISalePointService salePointService;

	public void setSalePointService(ISalePointService salePointService) {
		this.salePointService = salePointService;
	}

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaMethodService;

	public void setBancaMethodService(IBancaMethodService bancaMethodService) {
		this.bancaMethodService = bancaMethodService;
	}

	private int periodYear;

	private String remark;
	private String userType;
	private String insuredPersonFullName;
	private String guardianPersonFullName;
	private static final String formID = "medicalProposalEntryForm";

	private boolean createNewAddOn;
	private boolean createEdit;
	private boolean createEditBeneficiariesInfo;
	private boolean createNewBeneficiariesInfo;
	private boolean createNewIsuredPersonInfo;
	private boolean createNewGuradianPersonInfo;
	private boolean existingCustomer;
	private boolean haveTownship;
	private boolean isEditProposal;
	private boolean surveyAgain;
	private boolean isOldPolicy;
	private boolean isEnquiryEdit;
	private boolean isEditHistoryRecord;

	private Unit selectedUnit;
	private User user;
	private Product product;
	private PaymentType paymentType;
	private User responsiblePerson;
	private MedicalPolicyDTO medicalPolicyDTO;
	private MedicalPolicyInsuredPersonDTO insuredPersonDTO;
	private MedicalPolicyInsuredPersonBeneficiaryDTO beneficiariesInfoDTO;
	private MedicalPersonHistoryRecord historyRecord;
	private PolicyGuardianDTO medPolicyGuardianDTO;
	private BancaassurancePolicy bancaassurancePolicy;
	private BancaassuranceProposal bancaassuranceProposal;
	private BancaMethod bancaMethod;
	private boolean isChannel;
	private List<BancaMethod> bancaMethodList;
	private List<Product> productList;
	private List<Product> selectedProduct;
	private List<Country> countryList;
	private List<Religion> religionList;
	private List<Industry> industryList;
	private List<BankBranch> bankBranchList;
	private List<RelationShip> relationshipList;
	private List<CoinsuranceCompany> companyList;
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();

	private Map<String, MedicalPolicyInsuredPersonBeneficiaryDTO> beneficiariesInfoDTOMap;
	private Map<String, MedicalPolicyInsuredPersonDTO> insuredPersonDTOMap;
	private Map<String, PolicyGuardianDTO> guardianPersonDTOMap;
	private Map<String, MedicalPersonHistoryRecord> historyRecordMap;
	private Map<Integer, Unit> unitsMap;
	private HealthType healthType;

	private List<MedicalPolicyInsuredPersonAddOn> insuredPersonAddOnList;

	private boolean microHealthRender;
	private boolean criticalIllnessRender;
	private boolean guardianInfoDisable;
	private boolean sameCustomer;
	private boolean individualProduct;

	@PostConstruct
	public void init() {
		insuredPersonDTOMap = new HashMap<String, MedicalPolicyInsuredPersonDTO>();
		guardianPersonDTOMap = new HashMap<String, PolicyGuardianDTO>();
		beneficiariesInfoDTOMap = new HashMap<String, MedicalPolicyInsuredPersonBeneficiaryDTO>();
		insuredPersonDTO = new MedicalPolicyInsuredPersonDTO();
		historyRecord = new MedicalPersonHistoryRecord();
		isEditHistoryRecord = false;
		guardianInfoDisable = true;
		historyRecordMap = new HashMap<String, MedicalPersonHistoryRecord>();
		healthType = (HealthType) getParam("HEALTHTYPE");

		initializeInjection();
		loadData();
		createNewMedicalInsuredPerson();
		createNewBeneficiariesInfo();
		createNewGuradianPersonInfo();

	}

	public void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		// medicalPolicy = (MedicalPolicy) getParam("medicalPolicy");
		bancaassurancePolicy = (BancaassurancePolicy) getParam("bancaassurancePolicy");
		bancaMethodList = bancaMethodService.findAllBanca();
		isChannel = false;
		if (isExistParam("medicalPolicy")) {
			MedicalPolicy medicalPolicy = (MedicalPolicy) getParam("medicalPolicy");
			isOldPolicy = true;

			if (medicalPolicy.getCustomer() != null) {
				individualProduct = true;
			} else {
				individualProduct = false;
			}
			medicalPolicyDTO = MedicalPolicyFactory.createMedicalPolicyDTO(medicalPolicy);

			for (MedicalPolicyInsuredPersonDTO insuredPersonDTO : medicalPolicyDTO.getPolicyInsuredPersonDtoList()) {
				if (insuredPersonDTO.getOrganization() != null) {
					if (insuredPersonDTO.getCustomer().getId().equals(medicalPolicy.getCustomer().getId())) {
						insuredPersonDTO.setSameInsuredPerson(true);
					}
				}
				insuredPersonDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
			}
			if (insuredPersonDTO.getOrganization() != null) {
				loadTownshipCodeByStateCode(medicalPolicyDTO.getCustomer().getStateCode());
			}
			loadUserType();
		}

	}

	private void loadUserType() {
		if (medicalPolicyDTO.getAgent() != null) {
			userType = UserType.AGENT.toString();
		} else if (medicalPolicyDTO.getSaleMan() != null) {
			userType = UserType.SALEMAN.toString();
		} else {
			userType = UserType.REFERRAL.toString();
		}
	}

	public void loadData() {
		productList = productService.findProductByInsuranceType(InsuranceType.MEDICAL);
		switch (healthType) {
			case CRITICALILLNESS:
				if (CustomerType.CORPORATECUSTOMER.equals(medicalPolicyDTO.getCustomerType())) {
					product = productService.findProductById(ProductIDConfig.getGroupCriticalIllness_Id());
					medicalPolicyDTO.setCustomerType(CustomerType.CORPORATECUSTOMER);
				} else if (CustomerType.INDIVIDUALCUSTOMER.equals(medicalPolicyDTO.getCustomerType())) {
					product = productService.findProductById(ProductIDConfig.getIndividualCriticalIllness_Id());
					medicalPolicyDTO.setCustomerType(CustomerType.INDIVIDUALCUSTOMER);
				}
				criticalIllnessRender = true;
				microHealthRender = false;
				break;
			case HEALTH:
				if (CustomerType.CORPORATECUSTOMER.equals(medicalPolicyDTO.getCustomerType())) {
					product = productService.findProductById(ProductIDConfig.getGroupHealthInsuranceId());
					medicalPolicyDTO.setCustomerType(CustomerType.CORPORATECUSTOMER);
				} else {
					product = productService.findProductById(ProductIDConfig.getIndividualHealthInsuranceId());
					medicalPolicyDTO.setCustomerType(CustomerType.INDIVIDUALCUSTOMER);
				}
				criticalIllnessRender = false;
				microHealthRender = false;
				break;
			case MICROHEALTH:
				product = productService.findProductById(ProductIDConfig.getMicroHealthInsurance());
				criticalIllnessRender = false;
				microHealthRender = true;
				break;
			case MEDICAL_INSURANCE:
				product = productService.findProductById(ProductIDConfig.getMedicalProductId());
				criticalIllnessRender = false;
				microHealthRender = false;
			default:
				break;

		}

		relationshipList = relationShipService.findAllRelationShip();
		religionList = religionService.findAllReligion();
		bankBranchList = bankBranchService.findAllBankBranch();
		stateCodeList = stateCodeService.findAllStateCode();
		companyList = coinsuranceCompanyService.findAll();
	}

	public void changeStateCode(AjaxBehaviorEvent e) {
		StateCode stateCode = (StateCode) ((UIOutput) e.getSource()).getValue();
		loadTownshipCodeByStateCode(stateCode);
		haveTownship = true;
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalPolicy");
		removeParam("bancaassurancePolicy");
		removeParam("HEALTHTYPE");
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if (medicalPolicyDTO.getCustomer() != null) {
			loadTownshipCodeByStateCode(medicalPolicyDTO.getCustomer().getStateCode());
		}

		return valid ? event.getNewStep() : event.getOldStep();
	}

	public List<Product> getSelectedProduct() {
		return selectedProduct;
	}

	public SaleChannelType[] getSaleChannelType() {
		return SaleChannelType.values();
	}

	public void setSelectedProduct(List<Product> selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public MedicalPersonHistoryRecord getHistoryRecord() {
		return historyRecord;
	}

	public void setHistoryRecord(MedicalPersonHistoryRecord historyRecord) {
		this.historyRecord = historyRecord;
	}

	public List<CoinsuranceCompany> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<CoinsuranceCompany> companyList) {
		this.companyList = companyList;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public void setStateCodeList(List<StateCode> stateCodeList) {
		this.stateCodeList = stateCodeList;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public void setTownshipCodeList(List<TownshipCode> townshipCodeList) {
		this.townshipCodeList = townshipCodeList;
	}

	public boolean isHaveTownship() {
		return haveTownship;
	}

	public void setHaveTownship(boolean haveTownship) {
		this.haveTownship = haveTownship;
	}

	public boolean isSelfRelation() {
		if (insuredPersonDTO.getRelationship() != null && "SELF".equalsIgnoreCase(insuredPersonDTO.getRelationship().getName()))
			return true;
		else
			return false;
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public IdConditionType[] getIdConditionType() {
		return IdConditionType.values();
	}

	public MaritalStatus[] getMaritalStatus() {
		return MaritalStatus.values();
	}

	public PassportType[] getPassportTypes() {
		return PassportType.values();
	}

	public MedicalPolicyInsuredPersonBeneficiaryDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public void setBeneficiariesInfoDTO(MedicalPolicyInsuredPersonBeneficiaryDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	public void setCreateNewBeneficiariesInfo(boolean createNewBeneficiariesInfo) {
		this.createNewBeneficiariesInfo = createNewBeneficiariesInfo;
	}

	public boolean isCreateNewIsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	public void setCreateNewIsuredPersonInfo(boolean createNewIsuredPersonInfo) {
		this.createNewIsuredPersonInfo = createNewIsuredPersonInfo;
	}

	public boolean isCreateNewGuradianPersonInfo() {
		return createNewGuradianPersonInfo;
	}

	public void setCreateNewGuradianPersonInfo(boolean createNewGuradianPersonInfo) {
		this.createNewGuradianPersonInfo = createNewGuradianPersonInfo;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isCreateNewAddOn() {
		return createNewAddOn;
	}

	public void setCreateNewAddOn(boolean createNewAddOn) {
		this.createNewAddOn = createNewAddOn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	private RelationShip getSelfRelationship() {
		RelationShip result = null;
		for (RelationShip rs : relationshipList) {
			if ("SELF".equalsIgnoreCase(rs.getName().trim())) {
				result = rs;
			}
		}
		return result;
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

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	private boolean isNrcCustomer = true;
	private boolean isStillApplyCustomer = true;

	public void changeIdType(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		checkIdType(idType, "C");
	}

	public boolean isNrcCustomer() {
		return isNrcCustomer;
	}

	public void setNrcCustomer(boolean isNrcCustomer) {
		this.isNrcCustomer = isNrcCustomer;
	}

	public boolean isStillApplyCustomer() {
		return isStillApplyCustomer;
	}

	public void setStillApplyCustomer(boolean isStillApplyCustomer) {
		this.isStillApplyCustomer = isStillApplyCustomer;
	}

	/********************************* customer *********************/
	private boolean isNrcInsuredPerson = true;
	private boolean isStillApplyInsuredPerson = true;

	public void changeIdTypeInsuredPerson(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		checkIdType(idType, "I");
	}

	public void changeIdTypeGuardianPerson(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		checkIdType(idType, "G");
	}

	public boolean isNrcInsuredPerson() {
		return isNrcInsuredPerson;
	}

	public void setNrcInsuredPerson(boolean isNrcInsuredPerson) {
		this.isNrcInsuredPerson = isNrcInsuredPerson;
	}

	public boolean isStillApplyInsuredPerson() {
		return isStillApplyInsuredPerson;
	}

	public void setStillApplyInsuredPerson(boolean isStillApplyInsuredPerson) {
		this.isStillApplyInsuredPerson = isStillApplyInsuredPerson;
	}

	/**************** insured person ********************/
	private boolean isNrcBene = true;
	private boolean isNrcGuar = true;
	private boolean isStillApplyBene = true;
	private boolean isStillApplyGuar = true;

	public void checkBeneficiariesIDNo(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		checkIdType(idType, "B");
	}

	public void checkIdType(IdType idType, String customerType) {
		switch (idType) {
			case NRCNO:
				if ("C".equals(customerType)) {
					isNrcCustomer = true;
					isStillApplyCustomer = true;
				} else if ("I".equals(customerType)) {
					isNrcInsuredPerson = true;
					isStillApplyInsuredPerson = true;
				} else if ("B".equals(customerType)) {
					isNrcBene = true;
					isStillApplyBene = true;
				} else if ("G".equals(customerType)) {
					isNrcGuar = true;
					isStillApplyGuar = true;
				}
				break;
			case STILL_APPLYING:
				if ("C".equals(customerType)) {
					haveTownship = false;
					isNrcCustomer = false;
					isStillApplyCustomer = false;
					medicalPolicyDTO.getCustomer().setFullIdNo(null);
					medicalPolicyDTO.getCustomer().setStateCode(null);
					medicalPolicyDTO.getCustomer().setTownshipCode(null);
					medicalPolicyDTO.getCustomer().setIdConditionType(null);
					medicalPolicyDTO.getCustomer().setIdNo("");
				} else if ("I".equals(customerType)) {
					haveTownship = false;
					isNrcInsuredPerson = false;
					isStillApplyInsuredPerson = false;
					insuredPersonDTO.getCustomer().setFullIdNo(null);
					insuredPersonDTO.getCustomer().setStateCode(null);
					insuredPersonDTO.getCustomer().setTownshipCode(null);
					insuredPersonDTO.getCustomer().setIdConditionType(null);
					insuredPersonDTO.getCustomer().setIdNo("");
				} else if ("B".equals(customerType)) {
					haveTownship = false;
					isNrcBene = false;
					isStillApplyBene = false;
					beneficiariesInfoDTO.setFullIdNo(null);
					beneficiariesInfoDTO.setStateCode(null);
					beneficiariesInfoDTO.setTownshipCode(null);
					beneficiariesInfoDTO.setIdConditionType(null);
					beneficiariesInfoDTO.setIdNo("");
				} else if ("G".equals(customerType)) {
					haveTownship = false;
					isNrcGuar = false;
					isStillApplyGuar = false;
					medPolicyGuardianDTO.getCustomerDTO().setFullIdNo(null);
					medPolicyGuardianDTO.getCustomerDTO().setStateCode(null);
					medPolicyGuardianDTO.getCustomerDTO().setTownshipCode(null);
					medPolicyGuardianDTO.getCustomerDTO().setIdConditionType(null);
					medPolicyGuardianDTO.getCustomerDTO().setIdNo("");
				}
				break;
			default:
				haveTownship = false;
				if ("C".equals(customerType)) {
					isNrcCustomer = false;
					isStillApplyCustomer = true;
				} else if ("I".equals(customerType)) {
					isNrcInsuredPerson = false;
					isStillApplyInsuredPerson = true;
				} else if ("B".equals(customerType)) {
					isNrcBene = false;
					isStillApplyBene = true;
				} else if ("G".equals(customerType)) {
					isNrcGuar = false;
					isStillApplyGuar = true;
				}
				break;
		}
	}

	public boolean isNrcBene() {
		return isNrcBene;
	}

	public void setNrcBene(boolean isNrcBene) {
		this.isNrcBene = isNrcBene;
	}

	public boolean isStillApplyBene() {
		return isStillApplyBene;
	}

	public void setStillApplyBene(boolean isStillApplyBene) {
		this.isStillApplyBene = isStillApplyBene;
	}

	public boolean isNrcGuar() {
		return isNrcGuar;
	}

	public void setNrcGuar(boolean isNrcGuar) {
		this.isNrcGuar = isNrcGuar;
	}

	public boolean isStillApplyGuar() {
		return isStillApplyGuar;
	}

	public void setStillApplyGuar(boolean isStillApplyGuar) {
		this.isStillApplyGuar = isStillApplyGuar;
	}

	public String updateMedicalPolicy() {
		String result = null;
		try {

			medicalPolicyDTO.setPolicyInsuredPersonDtoList(getMedicalInsuredPersonDTOList());
			MedicalPolicy medicalPolicy = MedicalPolicyFactory.createMedicalPolicy(medicalPolicyDTO);
			medicalPolicyService.overwriteMedicalPolicy(medicalPolicy);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.EDIT_POLICY_PROCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	/******************** Start Customer Entry **************************/

	public boolean isExistingCustomer() {
		return existingCustomer;
	}

	public void resetCustomer() {
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setGender(Gender.MALE);
		customerDTO.setIdType(IdType.NRCNO);
		customerDTO.setMaritalStatus(MaritalStatus.SINGLE);
		customerDTO.setpType(PassportType.WORKING);
		medicalPolicyDTO.setCustomerDTO(customerDTO);
		existingCustomer = false;
	}

	public void resetInsuredPerson() {
		guardianInfoDisable = true;
		String tempId = insuredPersonDTO.getTempId();
		insuredPersonDTO = new MedicalPolicyInsuredPersonDTO();
		insuredPersonDTO.setRelationship(null);
		insuredPersonFullName = "";
		if (createEdit) {
			insuredPersonDTO.setTempId(tempId);
		}
	}

	public void resetGuradianPerson() {
		String tempId = medPolicyGuardianDTO.getTempId();
		medPolicyGuardianDTO = new PolicyGuardianDTO();
		medPolicyGuardianDTO.setRelationship(null);
		guardianPersonFullName = "";
		if (createEdit) {
			medPolicyGuardianDTO.setTempId(tempId);
		}

	}

	public void changeChannel(AjaxBehaviorEvent event) {
		if (isChannel) {
			isChannel = true;
		} else {
			isChannel = false;
			bancaassurancePolicy = new BancaassurancePolicy();
		}

	}

//	public void selectBancaBrm() {
//		selectBancaBRM(bancaassurancePolicy.getBancaBranch().getId());
//	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		bancaassurancePolicy.setChannel(channel);
	}

	public void returnBancaLIC(SelectEvent event) {
		BancaLIC bancaLIC = (BancaLIC) event.getObject();
		bancaassurancePolicy.setBancaLIC(bancaLIC);
	}

	public void returnBancaBrm(SelectEvent event) {
		BancaBRM bancaBrm = (BancaBRM) event.getObject();
		bancaassurancePolicy.setBancaBRM(bancaBrm);
	}

	public void returnBancaRefferal(SelectEvent event) {
		BancaRefferal bancaRefferal = (BancaRefferal) event.getObject();
		bancaassurancePolicy.setBancaRefferal(bancaRefferal);
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		bancaassurancePolicy.setBancaBranch(bancaBranch);
	}

	public BancaassurancePolicy getBancaassurancePolicy() {
		return bancaassurancePolicy;
	}

	public void setBancaassurancePolicy(BancaassurancePolicy bancaassurancePolicy) {
		this.bancaassurancePolicy = bancaassurancePolicy;
	}

	public BancaMethod getBancaMethod() {
		return bancaMethod;
	}

	public void setBancaMethod(BancaMethod bancaMethod) {
		this.bancaMethod = bancaMethod;
	}

	public List<BancaMethod> getBancaMethodList() {
		return bancaMethodList;
	}

	public void setBancaMethodList(List<BancaMethod> bancaMethodList) {
		this.bancaMethodList = bancaMethodList;
	}

	public boolean isChannel() {
		return isChannel;
	}

	public void setChannel(boolean isChannel) {
		this.isChannel = isChannel;
	}

	/******************** Start InsuredPerson Entry **************************/
	private void changeCusToInsuredPerson() {
		CustomerDTO customer = medicalPolicyDTO.getCustomerDTO();
		insuredPersonDTO.setRelationship(getSelfRelationship());
		insuredPersonDTO.setSameInsuredPerson(true);
		// insuredPersonDTO.setCustomer(new Customer());
		if (customer.getId() != null) {
			insuredPersonFullName = customer.getFullName();
		}
		loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
		checkIdType(insuredPersonDTO.getCustomer().getIdType(), "I");
	}

	public void handleInsuredPersonRelationship() {
		if (insuredPersonDTO.getRelationship() != null) {
			boolean isContainSelfRS = false;
			if (getSelfRelationship().equals(insuredPersonDTO.getRelationship())) {
				if (insuredPersonDTOMap.size() != 0) {
					for (MedicalPolicyInsuredPersonDTO dto : getMedicalInsuredPersonDTOList()) {
						if (getSelfRelationship().equals(dto.getRelationship())) {
							isContainSelfRS = true;
						}
					}
					if (isContainSelfRS) {
						insuredPersonDTO.setRelationship(null);
						addErrorMessage(formID + ":beneficiaryRelationShip", MessageId.ALREADY_ADD_SELF_RELATION);
					} else {
						changeCusToInsuredPerson();
					}
				} else {
					changeCusToInsuredPerson();
				}
				PrimeFaces.current().ajax().update(formID + ":insuredPersonInfoWizardPanel");
			} else {
				insuredPersonDTO.setSameInsuredPerson(false);
			}
		}
	}

	public void handleGuardianPersonRelationship() {
		if (medPolicyGuardianDTO.getRelationship() != null) {
			boolean isContainSelfRS = false;
			if (getSelfRelationship().equals(medPolicyGuardianDTO.getRelationship())) {
				// THROW ERROR , GUARDIAN CAN"T BE SELF
			}
		}
	}

	public void changeSelectedCusToInsuredPerson(CustomerDTO customer) {
		insuredPersonDTO.setCustomerDTO(customer);
		// insuredPersonDTO.getCustomer().setExistsEntity(true);
		insuredPersonDTO.setRelationship(null);
		insuredPersonDTO.setSameInsuredPerson(false);
		if (customer.getId() != null) {
			insuredPersonFullName = customer.getFullName();
		}
		loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
		checkIdType(insuredPersonDTO.getCustomer().getIdType(), "I");
	}

	public void changeSelectedCusToGuardianPerson(CustomerDTO customer) {
		medPolicyGuardianDTO.setCustomerDTO(customer);
		medPolicyGuardianDTO.getCustomerDTO().setExistsEntity(true);
		checkIdType(medPolicyGuardianDTO.getCustomerDTO().getIdType(), "G");
	}

	/********************
	 * Start InsuredPerson Beneficiaries Entry
	 **************************/

	public void prepareAddNewBeneficiariesInfo() {
		createNewBeneficiariesInfo();
		isNrcBene = true;
		isStillApplyBene = true;
		createEditBeneficiariesInfo = false;
		saveBene = false;
	}

	public void prepareAddNewGuradianInfo() {
		createNewGuradianPersonInfo();
		isNrcBene = true;
		isStillApplyBene = true;
		// createEdit = false;
	}

	private void createNewBeneficiariesInfo() {
		createNewBeneficiariesInfo = true;
		beneficiariesInfoDTO = new MedicalPolicyInsuredPersonBeneficiaryDTO();
		beneficiariesInfoDTO.setPercentage(100.0f);
	}

	private void createNewGuradianPersonInfo() {
		medPolicyGuardianDTO = new PolicyGuardianDTO();
		guardianPersonFullName = "";
	}

	public boolean isSaveBene() {
		return saveBene;
	}

	private boolean saveBene;
	private boolean saveGuar;

	public void saveBeneficiariesInfo() {
		ValidationResult result = validBeneficiary(beneficiariesInfoDTO);
		if (result.isVerified()) {
			beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
			createNewBeneficiariesInfo();
			hideBeneficiaryDialog();
			saveBene = true;
		} else {
			for (ErrorMessage message : result.getErrorMeesages()) {
				addErrorMessage(message);
			}
		}
	}

	public void saveGuardianInfo() {
		ValidationResult result = guardianValidator.validate(medPolicyGuardianDTO);
		if (result.isVerified()) {
			guardianPersonDTOMap.put(medPolicyGuardianDTO.getTempId(), medPolicyGuardianDTO);
			insuredPersonDTO.setGuardian(medPolicyGuardianDTO);
			saveGuar = true;
			createNewGuradianPersonInfo();
			hideGuradianDialog();
		} else {
			for (ErrorMessage message : result.getErrorMeesages()) {
				addErrorMessage(message);
			}
		}
	}

	public void hideBeneficiaryDialog() {
		loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
		PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
	}

	public void hideGuradianDialog() {
		PrimeFaces.current().executeScript("PF('guradianInfoEntryDialog').hide()");
	}

	public List<MedicalPolicyInsuredPersonBeneficiaryDTO> getProposalInsuredPersonBeneficiariesList() {
		return new ArrayList<MedicalPolicyInsuredPersonBeneficiaryDTO>(sortByValue(beneficiariesInfoDTOMap).values());
	}

	public List<PolicyGuardianDTO> getGuradianPersonList() {
		if (insuredPersonDTO.getGuardian() != null) {
			return Arrays.asList(insuredPersonDTO.getGuardian());
		} else {
			return null;
		}
	}

	public Map<String, MedicalPolicyInsuredPersonBeneficiaryDTO> sortByValue(Map<String, MedicalPolicyInsuredPersonBeneficiaryDTO> map) {
		List<Map.Entry<String, MedicalPolicyInsuredPersonBeneficiaryDTO>> list = new LinkedList<Map.Entry<String, MedicalPolicyInsuredPersonBeneficiaryDTO>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, MedicalPolicyInsuredPersonBeneficiaryDTO>>() {
			public int compare(Map.Entry<String, MedicalPolicyInsuredPersonBeneficiaryDTO> o1, Map.Entry<String, MedicalPolicyInsuredPersonBeneficiaryDTO> o2) {
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

		Map<String, MedicalPolicyInsuredPersonBeneficiaryDTO> result = new LinkedHashMap<String, MedicalPolicyInsuredPersonBeneficiaryDTO>();
		for (Map.Entry<String, MedicalPolicyInsuredPersonBeneficiaryDTO> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public Map<String, MedProGuardianDTO> sortByGuardianValue(Map<String, MedProGuardianDTO> map) {
		List<Map.Entry<String, MedProGuardianDTO>> list = new LinkedList<Map.Entry<String, MedProGuardianDTO>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, MedProGuardianDTO>>() {
			public int compare(Map.Entry<String, MedProGuardianDTO> o1, Map.Entry<String, MedProGuardianDTO> o2) {
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

		Map<String, MedProGuardianDTO> result = new LinkedHashMap<String, MedProGuardianDTO>();
		for (Map.Entry<String, MedProGuardianDTO> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public void prepareEditBeneficiariesInfo(MedicalPolicyInsuredPersonBeneficiaryDTO beneficiariesInfo) {
		this.beneficiariesInfoDTO = beneficiariesInfo;
		this.createNewBeneficiariesInfo = false;
		saveBene = true;
		createEditBeneficiariesInfo = true;
		loadTownshipCodeByStateCode(beneficiariesInfo.getStateCode());
		checkIdType(beneficiariesInfoDTO.getIdType(), "B");
	}

	public void prepareEditGuradianInfo(PolicyGuardianDTO guradianInfo) {
		this.medPolicyGuardianDTO = guradianInfo;
		this.createNewGuradianPersonInfo = false;
		saveGuar = false;
		loadTownshipCodeByStateCode(guradianInfo.getCustomerDTO().getStateCode());
		checkIdType(medPolicyGuardianDTO.getCustomerDTO().getIdType(), "G");
	}

	public void removeBeneficiariesInfo(MedicalPolicyInsuredPersonBeneficiaryDTO beneficiariesInfo) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfo.getTempId());
		createNewBeneficiariesInfo();
	}

	public void removeGuradianInfo(MedProGuardianDTO guradianInfo) {
		guardianPersonDTOMap.remove(guradianInfo.getTempId());
		saveGuar = false;
		insuredPersonDTO.setGuardian(null);
		createNewGuradianPersonInfo();
	}

	private ValidationResult validBeneficiary(MedicalPolicyInsuredPersonBeneficiaryDTO beneficiariesInfo) {
		ValidationResult result = beneficiariesValidator.validate(beneficiariesInfo);
		if (beneficiariesInfoDTOMap.size() > 0) {
			int percentage = 0;
			for (MedicalPolicyInsuredPersonBeneficiaryDTO beneficiary : beneficiariesInfoDTOMap.values()) {
				percentage += beneficiary.getPercentage();
			}
			if (!beneficiariesInfoDTOMap.containsKey(beneficiariesInfo.getTempId())) {
				percentage += beneficiariesInfo.getPercentage();
			}
			if (percentage > 100) {
				result.addErrorMessage("beneficiaryInfoEntryForm" + ":percentage", MessageId.OVER_BENEFICIARY_PERCENTAGE);
			}

		}
		return result;
	}

	/************************* for p:ajax event *****************************/

//	public void changeSaleEvent(AjaxBehaviorEvent event) {
//		if (userType.equals(UserType.AGENT.toString())) {
//			medicalPolicyDTO.setSaleMan(null);
//			medicalPolicyDTO.setReferral(null);
//		} else if (userType.equals(UserType.SALEMAN.toString())) {
//			medicalPolicyDTO.setAgent(null);
//			medicalPolicyDTO.setReferral(null);
//		} else if (userType.equals(UserType.REFERRAL.toString())) {
//			medicalPolicyDTO.setSaleMan(null);
//			medicalPolicyDTO.setAgent(null);
//		}
//	}
	
	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (medicalPolicyDTO.getSaleChannelType().equals(SaleChannelType.AGENT)) {
			medicalPolicyDTO.setSaleMan(null);
			medicalPolicyDTO.setReferral(null);
		} else if (medicalPolicyDTO.getSaleChannelType().equals(SaleChannelType.SALEMAN)) {
			medicalPolicyDTO.setAgent(null);
			medicalPolicyDTO.setReferral(null);
		} else if (medicalPolicyDTO.getSaleChannelType().equals(SaleChannelType.REFERRAL)) {
			medicalPolicyDTO.setSaleMan(null);
			medicalPolicyDTO.setAgent(null);
		} else if (medicalPolicyDTO.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
			medicalPolicyDTO.setSaleMan(null);
			medicalPolicyDTO.setAgent(null);
			medicalPolicyDTO.setReferral(null);
			bancaassuranceProposal = new BancaassuranceProposal();
		}
	}

	/**********************
	 * for pop up data to show in their respective fields
	 **********************/

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		medicalPolicyDTO.setCustomerDTO((CustomerFactory.getCustomerDTO(customer)));
		medicalPolicyDTO.getCustomerDTO().setExistsEntity(true);
		loadTownshipCodeByStateCode(medicalPolicyDTO.getCustomer().getStateCode());
		checkIdType(medicalPolicyDTO.getCustomer().getIdType(), "C");
		checkInsuredPerson();
		medicalPolicyDTO.getCustomerDTO().setHealthType(healthType);
	}

	public void checkInsuredPerson() {
		for (MedicalPolicyInsuredPersonDTO insuDTO : getMedicalInsuredPersonDTOList()) {
			if (medicalPolicyDTO.getCustomer().getId().equals(insuDTO.getCustomer().getId())) {
				insuDTO.setRelationship(getSelfRelationship());

			} else if (getSelfRelationship().equals(insuDTO.getRelationship())) {
				insuDTO.setRelationship(null);
			}
		}
		createNewMedicalInsuredPerson();
		createEdit = false;
	}

	public void returnInsuredPersonCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		boolean isAlreadyHave = false;
		for (MedicalPolicyInsuredPersonDTO insuDTO : getMedicalInsuredPersonDTOList()) {
			if (customer.getId().equals(insuDTO.getCustomer().getId())) {
				addErrorMessage(formID + ":customerRegInsu", MessageId.ALREADY_ADD_INSUREDPERSON);
				isAlreadyHave = true;
			}
		}
		if (!isAlreadyHave) {
			if (medicalPolicyDTO.getCustomer() != null) {
				if (customer.getId().equals(medicalPolicyDTO.getCustomer().getId())) {
					changeCusToInsuredPerson();
				} else {
					changeSelectedCusToInsuredPerson(CustomerFactory.getCustomerDTO(customer));
					loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
					insuredPersonFullName = insuredPersonDTO.getCustomer().getFullName();
				}
			} else {
				changeSelectedCusToInsuredPerson(CustomerFactory.getCustomerDTO(customer));
				loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
				insuredPersonFullName = insuredPersonDTO.getCustomer().getFullName();
			}
		}

		if (medicalPolicyDTO.getCustomer() != null) {
			if (insuredPersonDTO.getCustomer() != null && insuredPersonDTO.getCustomer().getId() != null
					&& insuredPersonDTO.getCustomer().getId().equals(medicalPolicyDTO.getCustomer().getId())) {
				insuredPersonDTO.setSameInsuredPerson(true);
			}

		}
		handleInsuredPersonAge();

	}

	public void returnGuradianPersonCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		boolean isAlreadyHave = false;
		for (PolicyGuardianDTO garDTO : getMedicalGuardianPersonDTOList()) {
			if (customer.getId().equals(garDTO.getCustomerDTO().getId())) {
				// addErrorMessage(formID + ":customerRegInsu",
				// MessageId.ALREADY_ADD_INSUREDPERSON);
				medPolicyGuardianDTO.setCustomerDTO(garDTO.getCustomerDTO());
				isAlreadyHave = true;
			}
		}
		if (!isAlreadyHave) {
			changeSelectedCusToGuardianPerson(CustomerFactory.getCustomerDTO(customer));
		}

		if (medicalPolicyDTO.getCustomer() != null && medicalPolicyDTO.getCustomer().getId() != null
				&& medicalPolicyDTO.getCustomer().getId().equals(medPolicyGuardianDTO.getCustomerDTO().getId())) {
			medPolicyGuardianDTO.setSameCustomer(true);
		}

		loadTownshipCodeByStateCode(medPolicyGuardianDTO.getCustomerDTO().getStateCode());
		guardianPersonFullName = medPolicyGuardianDTO.getCustomerDTO().getFullName();
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		medicalPolicyDTO.setPaymentType(paymentType);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		medicalPolicyDTO.setSaleMan(saleMan);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		medicalPolicyDTO.setAgent(agent);
	}

	public void returnReferral(SelectEvent event) {
		CustomerDTO referral = CustomerFactory.getCustomerDTO((Customer) event.getObject());
		medicalPolicyDTO.setReferralDTO(referral);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		medicalPolicyDTO.setBranch(branch);
		if (!existingCustomer) {
			medicalPolicyDTO.getCustomer().setBranch(branch);
		}
	}

	public void returnInsuredPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		insuredPersonDTO.getCustomer().getResidentAddress().setTownship(townShip);
	}

	public void returnGuradianPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		medPolicyGuardianDTO.getCustomerDTO().getResidentAddress().setTownship(townShip);
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonDTO.getCustomer().setOccupation(occupation);
	}

	public void returnOccupationForGuardian(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		medPolicyGuardianDTO.getCustomerDTO().setOccupation(occupation);
	}

	public void returnOccupationForCustomer(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		medicalPolicyDTO.getCustomer().setOccupation(occupation);
	}

	public void returnNationalityForCustomer(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		medicalPolicyDTO.getCustomer().setCountry(nationality);
	}

	public void returnNationalityForInsuredPerson(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		insuredPersonDTO.getCustomer().setCountry(nationality);

	}

	public void returnNationalityForGuardianPerson(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		medPolicyGuardianDTO.getCustomerDTO().setCountry(nationality);
	}

	public void returnEmployeeOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		medicalPolicyDTO.getCustomer().setOccupation(occupation);
	}

	public void returnQualification(SelectEvent event) {
		Qualification qualification = (Qualification) event.getObject();
		medicalPolicyDTO.getCustomer().setQualification(qualification);
	}

	public void returnResidentTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalPolicyDTO.getCustomer().getResidentAddress().setTownship(township);

	}

	public void returnPermanentTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalPolicyDTO.getCustomer().getPermanentAddress().setTownship(township);
	}

	public void returnOfficeTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalPolicyDTO.getCustomer().getOfficeAddress().setTownship(township);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnCustomerType(SelectEvent event) {
		CustomerType customerType = (CustomerType) event.getObject();
		medicalPolicyDTO.setCustomerType(customerType);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		medicalPolicyDTO.setOrganization(organization);
	}

	public MaritalStatus[] getMaritalStatusList() {
		return MaritalStatus.values();
	}

	public void selectUser() {
		if (isEditProposal) {
			if (surveyAgain) {
				selectUser(WorkflowTask.SURVEY, WorkFlowType.MEDICAL_INSURANCE);
			} else {
				selectUser(WorkflowTask.APPROVAL, WorkFlowType.MEDICAL_INSURANCE);
			}
		} else {
			selectUser(WorkflowTask.SURVEY, WorkFlowType.MEDICAL_INSURANCE);
		}
	}

	public void changeCustomerType(AjaxBehaviorEvent event) {
		if (medicalPolicyDTO.getCustomerType().equals(CustomerType.INDIVIDUALCUSTOMER)) {
			medicalPolicyDTO.setOrganization(null);
		} else if (medicalPolicyDTO.getCustomerType().equals(CustomerType.CORPORATECUSTOMER)) {
			medicalPolicyDTO.setCustomer(null);
		}
		medicalPolicyDTO.setCorporate(medicalPolicyDTO.getCustomerType().equals(CustomerType.CORPORATECUSTOMER));
		// medicalProposalDTO.setPaymentType(paymentType);
	}

	public void prepareHistoryRecord(MedicalPolicyInsuredPersonDTO insuredPersonDTO) {
		this.insuredPersonDTO = insuredPersonDTO;
		createEdit = false;
		historyRecordMap = new HashMap<String, MedicalPersonHistoryRecord>();
		// if (insuredPersonDTO.getHistoryRecordList() != null) {
		// for (MedicalPersonHistoryRecord record :
		// insuredPersonDTO.getHistoryRecordList()) {
		// historyRecordMap.put(record.getTempId(), record);
		// }
		// }
	}

	public List<MedicalPolicyInsuredPersonDTO> getMedicalInsuredPersonDTOList() {
		return new ArrayList<MedicalPolicyInsuredPersonDTO>(insuredPersonDTOMap.values());
	}

	public List<PolicyGuardianDTO> getMedicalGuardianPersonDTOList() {
		return new ArrayList<PolicyGuardianDTO>(guardianPersonDTOMap.values());
	}

	public CustomerType[] getCustomerTypes() {
		return CustomerType.values();
	}

	public List<Country> getCountryList() {
		return countryList;
	}

	public List<Religion> getReligionList() {
		return religionList;
	}

	public List<Industry> getIndustryList() {
		return industryList;
	}

	public List<BankBranch> getBankBranchList() {
		return bankBranchList;
	}

	public Map<Integer, Unit> getUnitsMap() {
		return unitsMap;
	}

	public void setUnitsMap(Map<Integer, Unit> unitsMap) {
		this.unitsMap = unitsMap;
	}

	public Unit getSelectedUnit() {
		return selectedUnit;
	}

	public void setSelectedUnit(Unit selectedUnit) {
		this.selectedUnit = selectedUnit;
	}

	public boolean isCreateEdit() {
		return createEdit;
	}

	public void setCreateEdit(boolean createEdit) {
		this.createEdit = createEdit;
	}

	public int getPeriodYear() {
		return periodYear;
	}

	public void setPeriodYear(int periodYear) {
		this.periodYear = periodYear;
	}

	private void setKeyFactorValue(PaymentType paymentType, int age, Gender gender) {
		for (MedicalPolicyInsuredPersonKeyFactorValue vehKF : insuredPersonDTO.getPolicyInsuredPersonkeyFactorValueList()) {
			KeyFactor kf = vehKF.getKeyFactor();
			if (KeyFactorChecker.isGender(kf)) {
				vehKF.setValue(gender + "");
			} else if (KeyFactorChecker.isMedicalAge(kf)) {
				vehKF.setValue(age + "");
			} else {
				vehKF.setValue(paymentType.getId() + "");
			}
		}
	}

	private void createNewMedicalInsuredPerson() {
		createNewIsuredPersonInfo = true;
		guardianInfoDisable = true;
		insuredPersonDTO = new MedicalPolicyInsuredPersonDTO();
		isNrcInsuredPerson = true;
		isStillApplyInsuredPerson = true;
		createEdit = false;
		insuredPersonFullName = "";
		createNewBeneficiariesInfoDTOMap();
	}

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, MedicalPolicyInsuredPersonBeneficiaryDTO>();
	}

	public void saveMedicalInsuredPerson() {
		// insuredPersonDTO.getCustomerDTO().setHealthType(healthType);
		insuredPersonDTO.setProduct(product);
		insuredPersonDTO.setPeriodYear(1);
		insuredPersonDTO.setMedicalPolicyDTO(medicalPolicyDTO);
		insuredPersonDTO.setPolicyInsuredPersonBeneficiariesDtoList((getProposalInsuredPersonBeneficiariesList()));

		if (checkBenePercentage()) {
			insuredPersonDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
			createNewMedicalInsuredPerson();
		}

	}

	private boolean checkBenePercentage() {
		if (beneficiariesInfoDTOMap != null && beneficiariesInfoDTOMap.size() != 0) {
			float totalPercent = 0.0f;
			for (MedicalPolicyInsuredPersonBeneficiaryDTO bfDTO : beneficiariesInfoDTOMap.values()) {
				totalPercent = totalPercent + bfDTO.getPercentage();
			}
			if (totalPercent > 100) {
				return false;
			} else if (totalPercent < 100) {
				return false;
			} else {
				return true;
			}
		} else {
			addErrorMessage("medicalProposalEntryForm:beneficiariesInfoTablePanel", MessageId.REQUIRED_BENEFICIARY_PERSON);
		}
		return false;
	}

	public void selectPaymentType() {
		selectPaymentType(product, medicalPolicyDTO.getCustomerType());
	}

	public void selectSalePoint() {
		PrimeFaces.current().dialog().openDynamic("salePointDialog", WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		medicalPolicyDTO.setSalePoint(salePoint);
	}

	public void prepareEditInsuredPersonInfo(MedicalPolicyInsuredPersonDTO insuredPersonInfo) {
		this.insuredPersonDTO = new MedicalPolicyInsuredPersonDTO(insuredPersonInfo);
		insuredPersonFullName = insuredPersonDTO.getCustomer().getId() != null ? insuredPersonDTO.getCustomer().getFullName() : "";
		this.product = insuredPersonDTO.getProduct();
		if (insuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (MedicalPolicyInsuredPersonBeneficiaryDTO bdto : insuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList()) {
				beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
			}
		}
		if (insuredPersonDTO.getGuardian() != null) {
			PolicyGuardianDTO gdto = insuredPersonDTO.getGuardian();
			guardianPersonDTOMap.put(gdto.getTempId(), gdto);
			guardianInfoDisable = false;
		}
		this.createNewIsuredPersonInfo = false;
		createEdit = true;
		loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
		checkIdType(insuredPersonDTO.getCustomer().getIdType(), "I");
	}

	private void loadTownshipCodeByStateCode(StateCode stateCode) {
		if (stateCode != null)
			townshipCodeList = townshipCodeService.findByStateCode(stateCode);
	}

	public void removeInsuredPersonInfo(MedProInsuDTO insuredPersonInfo) {
		insuredPersonDTOMap.remove(insuredPersonInfo.getTempId());
		createNewGuradianPersonInfo();
		createNewMedicalInsuredPerson();
	}

	public void saveHistoryRecord() {
		List<PersonProductHistory> productHistoryList = new ArrayList<>();
		for (Product product : selectedProduct) {
			PersonProductHistory productHisotry = new PersonProductHistory();
			productHisotry.setProduct(product);
			historyRecord.addPersonProductHistory(productHisotry);
			productHistoryList.add(productHisotry);
		}
		historyRecordMap.put(historyRecord.getTempId(), historyRecord);
		selectedProduct.clear();
		newHistoryRecord();
	}

	public void newHistoryRecord() {
		historyRecord = new MedicalPersonHistoryRecord();
	}

	public void addHistoryRecord() {
		if (getHistoryRecordList() == null || getHistoryRecordList().isEmpty()) {
			addErrorMessage("addOnConfigForm1:hitoryRecordTable", MessageId.REQUIRE_HISTORY_RECORD);
		} else {
			// insuredPersonDTO.setHistoryRecordList(getHistoryRecordList());
			insuredPersonDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
			createNewMedicalInsuredPerson();
			PrimeFaces.current().executeScript("PF('addOnConfigDialog1').hide();");
		}
	}

	public void handleHistoryRecordDialog() {
		isEditHistoryRecord = false;
		createNewMedicalInsuredPerson();
		PrimeFaces.current().executeScript("PF('addOnConfigDialog1').hide();");
	}

	public void updateHistoryRecord() {
		List<PersonProductHistory> productHistoryList = new ArrayList<>();
		for (Product product : selectedProduct) {
			PersonProductHistory productHisotry = new PersonProductHistory();
			productHisotry.setProduct(product);
			productHistoryList.add(productHisotry);
		}
		historyRecord.setProductList(productHistoryList);
		historyRecordMap.put(historyRecord.getTempId(), historyRecord);
		selectedProduct.clear();
		newHistoryRecord();
	}

	public void prepareEditHistoryRecord(MedicalPersonHistoryRecord historyRecord) {
		isEditHistoryRecord = true;
		this.historyRecord = historyRecord;
		for (PersonProductHistory productHistory : historyRecord.getProductList()) {
			selectedProduct.add(productHistory.getProduct());
		}
	}

	public void removeHistoryRecord(MedicalPersonHistoryRecord historyRecord) {
		historyRecordMap.remove(historyRecord.getTempId());
		newHistoryRecord();
	}

	public void handleInsuredPersonAge() {
		guardianInfoDisable = true;
		if (insuredPersonDTO != null && insuredPersonDTO.getCustomer() != null && insuredPersonDTO.getCustomer().getDateOfBirth() != null) {
			if (Utils.getAgeForNextYear(insuredPersonDTO.getCustomer().getDateOfBirth()) < 18) {
				guardianInfoDisable = false;
				if (insuredPersonDTO.getGuardian() != null && insuredPersonDTO.getGuardian().getCustomerDTO() != null) {
					saveGuar = true;
				} else {
					saveGuar = false;
				}
			} else {
				insuredPersonDTO.setGuardian(null);
			}
		}
	}

	public void handleSameCustomer() {
		if (medPolicyGuardianDTO.isSameCustomer()) {
			medPolicyGuardianDTO.setCustomerDTO(medicalPolicyDTO.getCustomerDTO());
			medPolicyGuardianDTO.setSameCustomer(true);
			guardianPersonFullName = medicalPolicyDTO.getCustomer().getFullName();
		}
	}

	public void handleAddOnDialogClose() {
		createNewMedicalInsuredPerson();
		PrimeFaces.current().executeScript("PF('addOnConfigDialog').hide();");
	}

	public void handleCheckBox(MedProInsuAddOnDTO addOnDTO) {
		if (addOnDTO.isSelected()) {
			addOnDTO.setSelected(true);
		} else {
			addOnDTO.setSelected(false);
			addOnDTO.setUnit(0);
		}

	}

	public boolean isPassportUser() {
		if (medicalPolicyDTO.getCustomer() != null) {
			if (medicalPolicyDTO.getCustomer().getIdType().equals(IdType.PASSPORTNO)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEditProposal() {
		return isEditProposal;
	}

	public boolean isSurveyAgain() {
		return surveyAgain;
	}

	public void setSurveyAgain(boolean surveyAgain) {
		this.surveyAgain = surveyAgain;
	}

	public boolean handleUpdateRelationShip() {
		return false;
	}

	public String getInsuredPersonFullName() {
		return insuredPersonFullName;
	}

	public boolean isOldPolicy() {
		return isOldPolicy;
	}

	public boolean isEnquiryEdit() {
		return isEnquiryEdit;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public boolean isEditHistoryRecord() {
		return isEditHistoryRecord;
	}

	public ArrayList<MedicalPersonHistoryRecord> getHistoryRecordList() {
		return new ArrayList<MedicalPersonHistoryRecord>(historyRecordMap.values());
	}

	public String getPageHeader() {
		return (isEditProposal ? "Confirm Edit" : isEnquiryEdit ? "Enquiry Edit" : isOldPolicy ? "Edit" : "Add New") + " " + healthType.getLabel() + " "
				+ (isOldPolicy ? "Policy" : "Proposal");
	}

	public boolean isMicroHealthRender() {
		return microHealthRender;
	}

	public String getGuardianPersonFullName() {
		return guardianPersonFullName;
	}

	public boolean getGuardianInfoDisable() {
		return guardianInfoDisable;
	}

	public boolean isSameCustomer() {
		return sameCustomer;
	}

	public void setSameCustomer(boolean sameCustomer) {
		this.sameCustomer = sameCustomer;
	}

	public boolean isSaveGuar() {
		return saveGuar;
	}

	public boolean isDisableInsureInfo() {
		if (HealthType.MICROHEALTH.equals(healthType)) {
			return false;
		} else {
			return individualProduct && getMedicalInsuredPersonDTOList().size() > 0 && createNewIsuredPersonInfo;
		}
	}

	public boolean isCreateEditBeneficiariesInfo() {
		return createEditBeneficiariesInfo;
	}

	public boolean isIndividualProduct() {
		return individualProduct;
	}

	public void setIndividualProduct(boolean individualProduct) {
		this.individualProduct = individualProduct;
	}

	public boolean isCriticalIllnessRender() {
		return criticalIllnessRender;
	}

	public MedicalPolicyDTO getMedicalPolicyDTO() {
		return medicalPolicyDTO;
	}

	public MedicalPolicyInsuredPersonDTO getInsuredPersonDTO() {
		return insuredPersonDTO;
	}

	public PolicyGuardianDTO getMedPolicyGuardianDTO() {
		return medPolicyGuardianDTO;
	}

	public Map<String, MedicalPolicyInsuredPersonBeneficiaryDTO> getBeneficiariesInfoDTOMap() {
		return beneficiariesInfoDTOMap;
	}

	public Map<String, MedicalPolicyInsuredPersonDTO> getInsuredPersonDTOMap() {
		return insuredPersonDTOMap;
	}

	public Map<String, PolicyGuardianDTO> getGuardianPersonDTOMap() {
		return guardianPersonDTOMap;
	}

	public List<MedicalPolicyInsuredPersonAddOn> getInsuredPersonAddOnList() {
		return insuredPersonAddOnList;
	}

}
