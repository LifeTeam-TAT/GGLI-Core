package org.ace.insurance.web.manage.medical.groupMicroHealth.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import javax.faces.component.UIInput;
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
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.Unit;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.proposal.MedicalPersonHistoryRecord;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonKeyFactorValue;
import org.ace.insurance.medical.proposal.PersonProductHistory;
import org.ace.insurance.medical.proposal.frontService.proposalService.interfaces.IAddNewMedicalProposalFrontService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
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
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.common.ValidationResult;
import org.ace.insurance.web.common.Validator;
import org.ace.insurance.web.common.WebUtils;
import org.ace.insurance.web.manage.medical.proposal.CustomerDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProGuardianDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuAddOnDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuBeneDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuDTO;
import org.ace.insurance.web.manage.medical.proposal.OrganizationDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.CustomerFactory;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedProposalEntryActionBean")
public class MedProposalEntryActionBean extends BaseBean {

	@ManagedProperty(value = "#{ProposalBeneficiariesValidator}")
	private Validator<MedProInsuBeneDTO> beneficiariesValidator;

	public void setBeneficiariesValidator(Validator<MedProInsuBeneDTO> beneficiariesValidator) {
		this.beneficiariesValidator = beneficiariesValidator;
	}

	@ManagedProperty(value = "#{ProposalInsuredPersonValidator}")
	private Validator<MedProInsuDTO> insuredpersonValidator;

	public void setInsuredpersonValidator(Validator<MedProInsuDTO> insuredpersonValidator) {
		this.insuredpersonValidator = insuredpersonValidator;
	}

	@ManagedProperty(value = "#{CustomerValidator}")
	private Validator<CustomerDTO> customerValidator;

	public void setCustomerValidator(Validator<CustomerDTO> customerValidator) {
		this.customerValidator = customerValidator;
	}

	@ManagedProperty(value = "#{GuardianPersonValidator}")
	private Validator<MedProGuardianDTO> guardianValidator;

	public void setGuardianValidator(Validator<MedProGuardianDTO> guardianValidator) {
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

	@ManagedProperty(value = "#{BancaassuraceProposalService}")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	public void setBancaassuraceProposalService(IBancaassuraceProposalService bancaassuraceProposalService) {
		this.bancaassuraceProposalService = bancaassuraceProposalService;
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
	private boolean isChannel;

	private Unit selectedUnit;
	private User user;
	private Product product;
	private PaymentType paymentType;
	private User responsiblePerson;
	private MedProDTO medicalProposalDTO;
	private MedProInsuDTO insuredPersonDTO;
	private MedProInsuBeneDTO beneficiariesInfoDTO;
	private MedicalPersonHistoryRecord historyRecord;
	private MedProGuardianDTO medProGuardianDTO;
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

	private Map<String, MedProInsuBeneDTO> beneficiariesInfoDTOMap;
	private Map<String, MedProInsuDTO> insuredPersonDTOMap;
	private Map<String, MedProGuardianDTO> guardianPersonDTOMap;
	private Map<String, MedicalPersonHistoryRecord> historyRecordMap;
	private Map<Integer, Unit> unitsMap;
	private HealthType healthType;

	private List<MedProInsuAddOnDTO> insuredPersonAddOnList;

	private boolean microHealthRender;
	private boolean criticalIllnessRender;
	private boolean guardianInfoDisable;
	private boolean sameCustomer;
	private boolean individualProduct;
	private MedicalProposal medicalProposal;
	private BancaassuranceProposal bancaassuranceProposal;

	private GroupMicroHealthDTO groupMicroHealthDTO;

	@PostConstruct
	public void init() {
		groupMicroHealthDTO = (GroupMicroHealthDTO) getParam("groupMicroHealthDTO");
		medicalProposal = (MedicalProposal) getParam("medicalProposal");
		paymentType = paymentTypeService.findPaymentTypeById("ISSYS0090001000000000429032013");

		individualProduct = true;
		insuredPersonDTOMap = new HashMap<String, MedProInsuDTO>();
		guardianPersonDTOMap = new HashMap<String, MedProGuardianDTO>();
		beneficiariesInfoDTOMap = new HashMap<String, MedProInsuBeneDTO>();
		insuredPersonDTO = new MedProInsuDTO();
		historyRecord = new MedicalPersonHistoryRecord();
		isEditHistoryRecord = false;
		guardianInfoDisable = true;
		historyRecordMap = new HashMap<String, MedicalPersonHistoryRecord>();
		healthType = (HealthType) getParam("HEALTHTYPE");

		initializeInjection();
		loadUserType();
		loadData();
		createNewMedicalInsuredPerson();
		createNewBeneficiariesInfo();
		createNewGuradianPersonInfo();
		if (isEditProposal || isEnquiryEdit) {
			List<MedProInsuDTO> insuredPersonDTOList = medicalProposalDTO.getMedProInsuDTOList();
			for (MedProInsuDTO person : insuredPersonDTOList) {
				insuredPersonDTOMap.put(person.getTempId(), person);
				insuredPersonDTO.setPeriodYear(person.getPeriodYear());
				selectedUnit = new Unit();
				selectedUnit.setValue(person.getUnit());
			}

			checkIdType(medicalProposalDTO.getCustomer().getIdType(), "C");
		}

		loadData();
		if (0 != product.getMaxUnit()) {
			unitsMap = Utils.getUnits(product.getMaxUnit(), true);
		}
		medicalProposalDTO.getCustomer().setHealthType(healthType);

	}

	public void initializeInjection() {
		MedicalProposal medicalProposal = null;
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalProposal = (MedicalProposal) getParam("medicalProposal");
		groupMicroHealthDTO = (GroupMicroHealthDTO) getParam("groupMicroHealthDTO");
		// clone medical policy
		if (isExistParam("medicalPolicy")) {
			MedicalPolicy medicalPolicy = (MedicalPolicy) getParam("medicalPolicy");
			isOldPolicy = true;
			medicalProposal = new MedicalProposal(medicalPolicy);
			medicalProposalDTO = MedicalProposalFactory.getMedProDTO(medicalProposal);
			medicalProposalDTO.setSubmittedDate(new Date());
			for (MedProInsuDTO insuredPersonDTO : medicalProposalDTO.getMedProInsuDTOList()) {
				// INFO basicUnit is 0 in clone policy
				insuredPersonDTO.setUnit(0);
				insuredPersonDTO.setProposedPremium(0);
				if (insuredPersonDTO.getCustomer().getId().equals(medicalPolicy.getCustomer().getId())) {
					insuredPersonDTO.setSameInsuredPerson(true);
				}
				insuredPersonDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
			}
			loadTownshipCodeByStateCode(medicalProposalDTO.getCustomer().getStateCode());
			loadUserType();
		} else if (isExistParam("editMedicalProposal")) {
			isEditProposal = true;
			medicalProposal = (MedicalProposal) getParam("editMedicalProposal");
		} else if (isExistParam("enquiryEditMedicalProposal")) {
			isEnquiryEdit = true;
			medicalProposal = (MedicalProposal) getParam("enquiryEditMedicalProposal");
		} else {
			createNewmedicalProposalDTO();
			periodYear = 1;
			paymentType = paymentTypeService.findPaymentTypeById("ISSYS0090001000000000429032013");
			medicalProposalDTO.setPaymentType(paymentType);
			medicalProposalDTO.setBranch(user.getBranch());
		}
		if (isEditProposal || isEnquiryEdit) {
			isOldPolicy = medicalProposal.getOldMedicalPolicy() != null;
			medicalProposalDTO = MedicalProposalFactory.getMedProDTO(medicalProposal);
			loadTownshipCodeByStateCode(medicalProposalDTO.getCustomer().getStateCode());
			loadUserType();
			for (MedProInsuDTO person : medicalProposalDTO.getMedProInsuDTOList()) {
				insuredPersonDTOMap.put(person.getTempId(), person);
				insuredPersonDTO.setPeriodYear(person.getPeriodYear());
				selectedUnit = new Unit();
				selectedUnit.setValue(person.getUnit());
			}
			checkIdType(medicalProposalDTO.getCustomer().getIdType(), "C");

			if (medicalProposalDTO.getMedProInsuDTOList() != null && medicalProposalDTO.getMedProInsuDTOList().size() > 1) {
				individualProduct = false;
			}
		}
		medicalProposalDTO.setSalePoint(groupMicroHealthDTO.getSalePoint());

	}

	private void loadUserType() {
		if (groupMicroHealthDTO.getAgent() != null) {
			medicalProposalDTO.setAgent(groupMicroHealthDTO.getAgent());
			userType = UserType.AGENT.toString();
		} else if (groupMicroHealthDTO.getSaleMan() != null) {
			medicalProposalDTO.setSaleMan(groupMicroHealthDTO.getSaleMan());
			userType = UserType.SALEMAN.toString();
		} else {
			userType = UserType.REFERRAL.toString();
		}
	}

	public void loadData() {
		productList = productService.findProductByInsuranceType(InsuranceType.MEDICAL);
		switch (healthType) {
			case CRITICALILLNESS:
				if (!individualProduct) {
					product = productService.findProductById(ProductIDConfig.getGroupCriticalIllness_Id());
				} else if (CustomerType.INDIVIDUALCUSTOMER.equals(medicalProposalDTO.getCustomerType())) {
					product = productService.findProductById(ProductIDConfig.getIndividualCriticalIllness_Id());
				}
				criticalIllnessRender = true;
				microHealthRender = false;
				break;
			case HEALTH:
				if (!individualProduct) {
					product = productService.findProductById(ProductIDConfig.getGroupHealthInsuranceId());
				} else {
					product = productService.findProductById(ProductIDConfig.getIndividualHealthInsuranceId());
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
		removeParam("medicalProposalDTO");
		removeParam("medicalProposal");
		removeParam("medicalPolicy");
		removeParam("editMedicalProposal");
		removeParam("enquiryEditMedicalProposal");
		removeParam("HEALTHTYPE");
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("customerTab".equals(event.getOldStep()) && "InsuredPersonInfo".equals(event.getNewStep())) {
			if (medicalProposalDTO.getCustomer() != null) {
				ValidationResult result = customerValidator.validate(medicalProposalDTO.getCustomer());
				if (!result.isVerified()) {
					for (ErrorMessage message : result.getErrorMeesages()) {
						addErrorMessage(message);
						valid = false;
					}
				} else {
					loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
				}
			}
		}
		if ("InsuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
			if (HealthType.MICROHEALTH.equals(healthType)) {
				System.out.println(insuredPersonDTOMap.size());
				if (insuredPersonDTOMap.size() < 1) {
					addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.REQUIRE_INSUREDPERSON);
					valid = false;
				}
			} else if (insuredPersonDTOMap.size() < 1) {
				/*
				 * if (individualProduct) { addErrorMessage(formID +
				 * ":insuredPersonInfoTable", MessageId.REQUIRE_INSUREDPERSON);
				 * } else { addErrorMessage(formID + ":insuredPersonInfoTable",
				 * MessageId.REQUIRE_FIVE_INSUREDPERSON); }
				 */

				addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.REQUIRE_INSUREDPERSON);
				valid = false;
			} else if (insuredPersonDTOMap.size() >= 1) {
				if (individualProduct) {
					if (insuredPersonDTOMap.size() > 1) {
						addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.INVALID_INDIVIDUAL_CUSTOMER);
						valid = false;
					}

				}
				/*
				 * else { addErrorMessage(formID + ":insuredPersonInfoTable",
				 * MessageId.REQUIRE_FIVE_INSUREDPERSON); valid = false; }
				 */

			}
		}
		if ("InsuredPersonInfo".equals(event.getOldStep()) && "customerTab".equals(event.getNewStep()))

		{
			if (medicalProposalDTO.getCustomer() != null) {
				loadTownshipCodeByStateCode(medicalProposalDTO.getCustomer().getStateCode());
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public List<Product> getSelectedProduct() {
		return selectedProduct;
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

	public MedProDTO getMedicalProposalDTO() {
		return medicalProposalDTO;
	}

	public void setMedicalProposalDTO(MedProDTO medicalProposalDTO) {
		this.medicalProposalDTO = medicalProposalDTO;
	}

	public MedProInsuBeneDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public void setBeneficiariesInfoDTO(MedProInsuBeneDTO beneficiariesInfoDTO) {
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

	public void setmedicalProposalDTO(MedProDTO medicalProposalDTO) {
		this.medicalProposalDTO = medicalProposalDTO;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public MedProDTO getmedicalProposalDTO() {
		return medicalProposalDTO;
	}

	public void createNewmedicalProposalDTO() {
		medicalProposalDTO = new MedProDTO();
		resetCustomer();
		insuredPersonDTO = new MedProInsuDTO();
		medicalProposalDTO.setSubmittedDate(groupMicroHealthDTO.getStartDate());
		medicalProposalDTO.setCustomerType(CustomerType.INDIVIDUALCUSTOMER);
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
					medicalProposalDTO.getCustomer().setFullIdNo(null);
					medicalProposalDTO.getCustomer().setStateCode(null);
					medicalProposalDTO.getCustomer().setTownshipCode(null);
					medicalProposalDTO.getCustomer().setIdConditionType(null);
					medicalProposalDTO.getCustomer().setIdNo("");
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
					medProGuardianDTO.getCustomer().setFullIdNo(null);
					medProGuardianDTO.getCustomer().setStateCode(null);
					medProGuardianDTO.getCustomer().setTownshipCode(null);
					medProGuardianDTO.getCustomer().setIdConditionType(null);
					medProGuardianDTO.getCustomer().setIdNo("");
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

	public String addNewMedicalProposal() {
		String result = null;

		try {
			WorkFlowDTO workFlowDTO = null;
			medicalProposalDTO.setMedProInsuDTOList(getMedicalInsuredPersonDTOList());
			MedicalProposal medicalProposal = null;
			medicalProposalDTO.setProposalType(ProposalType.UNDERWRITING);
			ExternalContext extContext = getFacesContext().getExternalContext();
			WorkflowReferenceType referenceType = WorkflowReferenceType.MICRO_HEALTH_PROPOSAL;
			if (medicalProposalDTO.getCustomer() != null) {
				medicalProposalDTO.getCustomer().setHealthType(HealthType.MICROHEALTH);
			} else if (medicalProposalDTO.getOrganization() != null) {
				medicalProposalDTO.getOrganization().setHealthType(HealthType.MICROHEALTH);
			}
			medicalProposalDTO.setGroupMicroHealthID(groupMicroHealthDTO.getId());

			if (isEditProposal) {
				addNewMedicalProposalFrontService.updateMedicalProposal(medicalProposalDTO, workFlowDTO, bancaassuranceProposal);
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.EDIT_PROPOSAL_PROCESS_SUCCESS);
			} else if (isEnquiryEdit) {
				medicalProposal = addNewMedicalProposalFrontService.updateMedicalProposal(medicalProposalDTO, null, bancaassuranceProposal);
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.EDIT_PROPOSAL_PROCESS_SUCCESS);
			} else {
				for (MedProInsuDTO insuredPerson : medicalProposalDTO.getMedProInsuDTOList()) {
					insuredPerson.setApproved(true);
					insuredPerson.setFinishedApproved(true);
				}
				medicalProposal = addNewMedicalProposalFrontService.addNewMedicalProposal(medicalProposalDTO, workFlowDTO, RequestStatus.PROPOSED.name(), bancaassuranceProposal);
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			}
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalProposalDTO.getProposalNo());
			createNewmedicalProposalDTO();
			result = "detailsGroupMicroHealth";
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
		medicalProposalDTO.setCustomer(customerDTO);
		existingCustomer = false;
	}

	public void resetInsuredPerson() {
		guardianInfoDisable = true;
		String tempId = insuredPersonDTO.getTempId();
		insuredPersonDTO = new MedProInsuDTO();
		insuredPersonDTO.setRelationship(null);
		insuredPersonFullName = "";
		if (createEdit) {
			insuredPersonDTO.setTempId(tempId);
		}
	}

	public void resetGuradianPerson() {
		String tempId = medProGuardianDTO.getTempId();
		medProGuardianDTO = new MedProGuardianDTO();
		medProGuardianDTO.setRelationship(null);
		guardianPersonFullName = "";
		if (createEdit) {
			medProGuardianDTO.setTempId(tempId);
		}

	}

	/******************** Start InsuredPerson Entry **************************/
	private void changeCusToInsuredPerson() {
		CustomerDTO customer = medicalProposalDTO.getCustomer();
		insuredPersonDTO.setRelationship(getSelfRelationship());
		insuredPersonDTO.setSameInsuredPerson(true);
		insuredPersonDTO.setCustomer(customer);
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
					for (MedProInsuDTO dto : getMedicalInsuredPersonDTOList()) {
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
		if (medProGuardianDTO.getRelationship() != null) {
			boolean isContainSelfRS = false;
			if (getSelfRelationship().equals(medProGuardianDTO.getRelationship())) {
				// THROW ERROR , GUARDIAN CAN"T BE SELF
			}
		}
	}

	public void changeSelectedCusToInsuredPerson(CustomerDTO customer) {
		insuredPersonDTO.setCustomer(customer);
		insuredPersonDTO.getCustomer().setExistsEntity(true);
		insuredPersonDTO.setRelationship(null);
		insuredPersonDTO.setSameInsuredPerson(false);
		if (customer.getId() != null) {
			insuredPersonFullName = customer.getFullName();
		}
		loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
		checkIdType(insuredPersonDTO.getCustomer().getIdType(), "I");
	}

	public void changeSelectedCusToGuardianPerson(CustomerDTO customer) {
		medProGuardianDTO.setCustomer(customer);
		medProGuardianDTO.getCustomer().setExistsEntity(true);
		checkIdType(medProGuardianDTO.getCustomer().getIdType(), "G");
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
		beneficiariesInfoDTO = new MedProInsuBeneDTO();
		beneficiariesInfoDTO.setPercentage(100.0f);
	}

	private void createNewGuradianPersonInfo() {
		medProGuardianDTO = new MedProGuardianDTO();
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
		ValidationResult result = guardianValidator.validate(medProGuardianDTO);
		if (result.isVerified()) {
			guardianPersonDTOMap.put(medProGuardianDTO.getTempId(), medProGuardianDTO);
			insuredPersonDTO.setGuardianDTO(medProGuardianDTO);
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

	public List<MedProInsuBeneDTO> getProposalInsuredPersonBeneficiariesList() {
		return new ArrayList<MedProInsuBeneDTO>(sortByValue(beneficiariesInfoDTOMap).values());
	}

	public List<MedProGuardianDTO> getGuradianPersonList() {
		if (insuredPersonDTO.getGuardianDTO() != null) {
			return Arrays.asList(insuredPersonDTO.getGuardianDTO());
		} else {
			return null;
		}
	}

	public Map<String, MedProInsuBeneDTO> sortByValue(Map<String, MedProInsuBeneDTO> map) {
		List<Map.Entry<String, MedProInsuBeneDTO>> list = new LinkedList<Map.Entry<String, MedProInsuBeneDTO>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, MedProInsuBeneDTO>>() {
			public int compare(Map.Entry<String, MedProInsuBeneDTO> o1, Map.Entry<String, MedProInsuBeneDTO> o2) {
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

		Map<String, MedProInsuBeneDTO> result = new LinkedHashMap<String, MedProInsuBeneDTO>();
		for (Map.Entry<String, MedProInsuBeneDTO> entry : list) {
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

	public void prepareEditBeneficiariesInfo(MedProInsuBeneDTO beneficiariesInfo) {
		this.beneficiariesInfoDTO = beneficiariesInfo;
		this.createNewBeneficiariesInfo = false;
		saveBene = true;
		createEditBeneficiariesInfo = true;
		loadTownshipCodeByStateCode(beneficiariesInfo.getStateCode());
		checkIdType(beneficiariesInfoDTO.getIdType(), "B");
	}

	public void prepareEditGuradianInfo(MedProGuardianDTO guradianInfo) {
		this.medProGuardianDTO = guradianInfo;
		this.createNewGuradianPersonInfo = false;
		saveGuar = false;
		loadTownshipCodeByStateCode(guradianInfo.getCustomer().getStateCode());
		checkIdType(medProGuardianDTO.getCustomer().getIdType(), "G");
	}

	public void removeBeneficiariesInfo(MedProInsuBeneDTO beneficiariesInfo) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfo.getTempId());
		createNewBeneficiariesInfo();
	}

	public void removeGuradianInfo(MedProGuardianDTO guradianInfo) {
		guardianPersonDTOMap.remove(guradianInfo.getTempId());
		saveGuar = false;
		insuredPersonDTO.setGuardianDTO(null);
		createNewGuradianPersonInfo();
	}

	private ValidationResult validBeneficiary(MedProInsuBeneDTO beneficiariesInfo) {
		ValidationResult result = beneficiariesValidator.validate(beneficiariesInfo);
		if (beneficiariesInfoDTOMap.size() > 0) {
			int percentage = 0;
			for (MedProInsuBeneDTO beneficiary : beneficiariesInfoDTOMap.values()) {
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

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			medicalProposalDTO.setSaleMan(null);
			medicalProposalDTO.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			medicalProposalDTO.setAgent(null);
			medicalProposalDTO.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			medicalProposalDTO.setSaleMan(null);
			medicalProposalDTO.setAgent(null);
		}
	}

	/**********************
	 * for pop up data to show in their respective fields
	 **********************/

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		medicalProposalDTO.setCustomer(CustomerFactory.getCustomerDTO(customer));
		medicalProposalDTO.getCustomer().setExistsEntity(true);
		loadTownshipCodeByStateCode(medicalProposalDTO.getCustomer().getStateCode());
		checkIdType(medicalProposalDTO.getCustomer().getIdType(), "C");
		checkInsuredPerson();
		medicalProposalDTO.getCustomer().setHealthType(healthType);
	}

	public void checkInsuredPerson() {
		for (MedProInsuDTO insuDTO : getMedicalInsuredPersonDTOList()) {
			if (medicalProposalDTO.getCustomer().getId().equals(insuDTO.getCustomer().getId())) {
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
		for (MedProInsuDTO insuDTO : getMedicalInsuredPersonDTOList()) {
			if (customer.getId().equals(insuDTO.getCustomer().getId())) {
				addErrorMessage(formID + ":customerRegInsu", MessageId.ALREADY_ADD_INSUREDPERSON);
				isAlreadyHave = true;
			}
		}
		if (!isAlreadyHave) {
			if (medicalProposalDTO.getCustomer() != null) {
				if (customer.getId().equals(medicalProposalDTO.getCustomer().getId())) {
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

		if (medicalProposalDTO.getCustomer() != null) {
			if (insuredPersonDTO.getCustomer() != null && insuredPersonDTO.getCustomer().getId() != null
					&& insuredPersonDTO.getCustomer().getId().equals(medicalProposalDTO.getCustomer().getId())) {
				insuredPersonDTO.setSameInsuredPerson(true);
			}

		}
		handleInsuredPersonAge();

	}

	public void returnGuradianPersonCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		boolean isAlreadyHave = false;
		for (MedProGuardianDTO garDTO : getMedicalGuardianPersonDTOList()) {
			if (customer.getId().equals(garDTO.getCustomer().getId())) {
				// addErrorMessage(formID + ":customerRegInsu",
				// MessageId.ALREADY_ADD_INSUREDPERSON);
				medProGuardianDTO.setCustomer(garDTO.getCustomer());
				isAlreadyHave = true;
			}
		}
		if (!isAlreadyHave) {
			changeSelectedCusToGuardianPerson(CustomerFactory.getCustomerDTO(customer));
		}

		if (medicalProposalDTO.getCustomer() != null && medicalProposalDTO.getCustomer().getId() != null
				&& medicalProposalDTO.getCustomer().getId().equals(medProGuardianDTO.getCustomer().getId())) {
			medProGuardianDTO.setSameCustomer(true);
		}

		loadTownshipCodeByStateCode(medProGuardianDTO.getCustomer().getStateCode());
		guardianPersonFullName = medProGuardianDTO.getCustomer().getFullName();
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		medicalProposalDTO.setPaymentType(paymentType);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		medicalProposalDTO.setSaleMan(saleMan);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		medicalProposalDTO.setAgent(agent);
	}

	public void returnReferral(SelectEvent event) {
		CustomerDTO referral = CustomerFactory.getCustomerDTO((Customer) event.getObject());
		medicalProposalDTO.setReferral(referral);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		medicalProposalDTO.setBranch(branch);
		if (!existingCustomer) {
			medicalProposalDTO.getCustomer().setBranch(branch);
		}
	}

	public void returnInsuredPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		insuredPersonDTO.getCustomer().getResidentAddress().setTownship(townShip);
	}

	public void returnGuradianPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		medProGuardianDTO.getCustomer().getResidentAddress().setTownship(townShip);
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
		medProGuardianDTO.getCustomer().setOccupation(occupation);
	}

	public void returnOccupationForCustomer(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		medicalProposalDTO.getCustomer().setOccupation(occupation);
	}

	public void returnNationalityForCustomer(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		medicalProposalDTO.getCustomer().setCountry(nationality);
	}

	public void returnNationalityForInsuredPerson(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		insuredPersonDTO.getCustomer().setCountry(nationality);

	}

	public void returnNationalityForGuardianPerson(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		medProGuardianDTO.getCustomer().setCountry(nationality);
	}

	public void returnEmployeeOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		medicalProposalDTO.getCustomer().setOccupation(occupation);
	}

	public void returnQualification(SelectEvent event) {
		Qualification qualification = (Qualification) event.getObject();
		medicalProposalDTO.getCustomer().setQualification(qualification);
	}

	public void returnResidentTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalProposalDTO.getCustomer().getResidentAddress().setTownship(township);

	}

	public void returnPermanentTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalProposalDTO.getCustomer().getPermanentAddress().setTownship(township);
	}

	public void returnOfficeTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalProposalDTO.getCustomer().getOfficeAddress().setTownship(township);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnCustomerType(SelectEvent event) {
		CustomerType customerType = (CustomerType) event.getObject();
		medicalProposalDTO.setCustomerType(customerType);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		medicalProposalDTO.setOrganization(new OrganizationDTO(organization));
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
		if (medicalProposalDTO.getCustomerType().equals(CustomerType.INDIVIDUALCUSTOMER)) {
			medicalProposalDTO.setOrganization(null);
		} else if (medicalProposalDTO.getCustomerType().equals(CustomerType.CORPORATECUSTOMER)) {
			medicalProposalDTO.setCustomer(null);
		}
		medicalProposalDTO.setCorporate(medicalProposalDTO.getCustomerType().equals(CustomerType.CORPORATECUSTOMER));
		// medicalProposalDTO.setPaymentType(paymentType);
	}

	public MedProInsuDTO getInsuredPersonDTO() {
		return insuredPersonDTO;
	}

	public void setInsuredPersonDTO(MedProInsuDTO insuredPersonDTO) {
		this.insuredPersonDTO = insuredPersonDTO;
	}

	public void prepareHistoryRecord(MedProInsuDTO insuredPersonDTO) {
		this.insuredPersonDTO = insuredPersonDTO;
		createEdit = false;
		historyRecordMap = new HashMap<String, MedicalPersonHistoryRecord>();
		if (insuredPersonDTO.getHistoryRecordList() != null) {
			for (MedicalPersonHistoryRecord record : insuredPersonDTO.getHistoryRecordList()) {
				historyRecordMap.put(record.getTempId(), record);
			}
		}
	}

	public List<MedProInsuDTO> getMedicalInsuredPersonDTOList() {
		return new ArrayList<MedProInsuDTO>(insuredPersonDTOMap.values());
	}

	public List<MedProGuardianDTO> getMedicalGuardianPersonDTOList() {
		return new ArrayList<MedProGuardianDTO>(guardianPersonDTOMap.values());
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
		for (MedicalProposalInsuredPersonKeyFactorValue vehKF : insuredPersonDTO.getKeyFactorValueList()) {
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

	public void prepareAddNewInsuredPersonInfo() {
		createNewMedicalInsuredPerson();
		createNewBeneficiariesInfoDTOMap();
	}

	private void createNewMedicalInsuredPerson() {
		createNewIsuredPersonInfo = true;
		guardianInfoDisable = true;
		insuredPersonDTO = new MedProInsuDTO();
		insuredPersonDTO.setMedProDTO(medicalProposalDTO);
		insuredPersonDTO.setStartDate(new Date());
		isNrcInsuredPerson = true;
		isStillApplyInsuredPerson = true;
		createEdit = false;
		insuredPersonFullName = "";
		insuredPersonAddOnList = new ArrayList<>();
		for (AddOn addOn : product.getAddOnList()) {
			insuredPersonAddOnList.add(new MedProInsuAddOnDTO(addOn));
		}
		createNewBeneficiariesInfoDTOMap();
	}

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, MedProInsuBeneDTO>();
	}

	public void saveMedicalInsuredPerson() {
		insuredPersonDTO.getCustomer().setHealthType(healthType);
		insuredPersonDTO.setProduct(product);
		insuredPersonDTO.setPeriodYear(1);
		insuredPersonDTO.setMedProDTO(medicalProposalDTO);
		insuredPersonDTO.setInsuredPersonBeneficiariesList(getProposalInsuredPersonBeneficiariesList());
		ValidationResult result = insuredpersonValidator.validate(insuredPersonDTO);
		if (result.isVerified() && checkBenePercentage() && checkInsuredPersonByCustomerType()) {
			Calendar cal = Calendar.getInstance();
			if (insuredPersonDTO.getStartDate() == null) {
				insuredPersonDTO.setStartDate(new Date());
			}
			cal.setTime(insuredPersonDTO.getStartDate());
			cal.add(Calendar.YEAR, insuredPersonDTO.getPeriodYear());
			setKeyFactorValue(medicalProposalDTO.getPaymentType(), Utils.getAgeForNextYear(insuredPersonDTO.getCustomer().getDateOfBirth()),
					insuredPersonDTO.getCustomer().getGender());
			insuredPersonDTO.setAge(Utils.getAgeForNextYear(insuredPersonDTO.getCustomer().getDateOfBirth()));
			insuredPersonDTO.setEndDate(cal.getTime());
			// if (!isOldPolicy) {
			// insuredPersonDTO.setUnit(1);
			// } else {
			// insuredPersonDTO.setUnit(0);
			// }
			insuredPersonDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
			createNewMedicalInsuredPerson();
		} else if (!result.isVerified()) {
			for (ErrorMessage message : result.getErrorMeesages()) {
				addErrorMessage(message);
			}
		}
	}

	private boolean checkInsuredPersonByCustomerType() {
		int noOfInsuredPerson = insuredPersonDTOMap.size();
		boolean valid = true;
		boolean isAlreadySelfRs = false;
		if (noOfInsuredPerson != 0) {
			for (MedProInsuDTO insuDTO : getMedicalInsuredPersonDTOList()) {
				if (getSelfRelationship().equals(insuDTO.getRelationship()))
					isAlreadySelfRs = true;
			}
			if (!HealthType.MICROHEALTH.equals(healthType)) {
				if (individualProduct && !createEdit) {
					if (noOfInsuredPerson == 1) {
						addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.INVALID_INDIVIDUAL_CUSTOMER);
						valid = false;
					}
				}
			}

			if (isAlreadySelfRs && !createEdit && getSelfRelationship().equals(insuredPersonDTO.getRelationship()) && !createEdit) {
				addErrorMessage(formID + ":beneficiaryRelationShip", MessageId.ALREADY_ADD_SELF_RELATION);
				valid = false;
			}

		}
		return valid;
	}

	private boolean checkBenePercentage() {
		if (beneficiariesInfoDTOMap != null && beneficiariesInfoDTOMap.size() != 0) {
			float totalPercent = 0.0f;
			for (MedProInsuBeneDTO bfDTO : beneficiariesInfoDTOMap.values()) {
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
		selectPaymentType(product, medicalProposalDTO.getCustomerType());
	}

	public void selectSalePoint() {
		PrimeFaces.current().dialog().openDynamic("salePointDialog", WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		medicalProposalDTO.setSalePoint(salePoint);
	}

	public void prepareEditInsuredPersonInfo(MedProInsuDTO insuredPersonInfo) {
		this.insuredPersonDTO = new MedProInsuDTO(insuredPersonInfo);
		insuredPersonFullName = insuredPersonDTO.getCustomer().getId() != null ? insuredPersonDTO.getCustomer().getFullName() : "";
		this.product = insuredPersonDTO.getProduct();
		if (insuredPersonDTO.getInsuredPersonBeneficiariesList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (MedProInsuBeneDTO bdto : insuredPersonDTO.getInsuredPersonBeneficiariesList()) {
				beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
			}
		}
		if (insuredPersonDTO.getGuardianDTO() != null) {
			MedProGuardianDTO gdto = insuredPersonDTO.getGuardianDTO();
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

	// public void applyProduct() {
	// insuredPersonDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
	// createNewMedicalInsuredPerson();
	// RequestContext context = RequestContext.getCurrentInstance();
	// context.execute("PF('addOnConfigDialog').hide();");
	// }

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
			insuredPersonDTO.setHistoryRecordList(getHistoryRecordList());
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

	public void configAddon(MedProInsuDTO insuDTO) {
		insuredPersonAddOnList = new ArrayList<>();
		if (insuDTO.getInsuredPersonAddOnList() != null && !insuDTO.getInsuredPersonAddOnList().isEmpty()) {
			insuredPersonAddOnList.addAll(insuDTO.getInsuredPersonAddOnList());

			if (insuredPersonAddOnList.size() != product.getAddOnList().size()) {
				for (AddOn addon : product.getAddOnList()) {
					if (!containAddon(addon)) {
						insuredPersonAddOnList.add(new MedProInsuAddOnDTO(addon));
					}
				}

			}
		} else {
			for (AddOn addOn : product.getAddOnList()) {
				insuredPersonAddOnList.add(new MedProInsuAddOnDTO(addOn));
			}
		}
	}

	private boolean containAddon(AddOn addon) {
		for (MedProInsuAddOnDTO dto : insuredPersonAddOnList) {
			if (dto.getAddOn() != null && addon.getId().equals(dto.getAddOn().getId())) {
				return true;
			}
		}
		return false;
	}

	public void addUnit() {
		boolean error = false;
		insuredPersonDTO.getInsuredPersonAddOnList().clear();
		for (MedProInsuAddOnDTO addon : insuredPersonAddOnList) {
			if (addon.isSelected()) {
				if (addon.getUnit() < 1) {
					error = true;
					addErrorMessage("addOnConfigForm:addOnTable", UIInput.REQUIRED_MESSAGE_ID);
				} else if (addon.getUnit() > insuredPersonDTO.getUnit()) {
					error = true;
					addErrorMessage("addOnConfigForm:addOnTable", MessageId.INVALID_ADDON_UNIT, insuredPersonDTO.getUnit());
				} else {
					insuredPersonDTO.getInsuredPersonAddOnList().add(addon);
				}
			}
		}
		if (!error) {
			PrimeFaces.current().executeScript("PF('addOnConfigDialog').hide();");
		}
	}

	public void handleInsuredPersonAge() {
		guardianInfoDisable = true;
		if (insuredPersonDTO != null && insuredPersonDTO.getCustomer() != null && insuredPersonDTO.getCustomer().getDateOfBirth() != null) {
			if (Utils.getAgeForNextYear(insuredPersonDTO.getCustomer().getDateOfBirth()) < 18) {
				guardianInfoDisable = false;
				if (insuredPersonDTO.getGuardianDTO() != null && insuredPersonDTO.getGuardianDTO().getCustomer() != null) {
					saveGuar = true;
				} else {
					saveGuar = false;
				}
			} else {
				insuredPersonDTO.setGuardianDTO(null);
			}
		}
	}

	public void handleSameCustomer() {
		if (medProGuardianDTO.isSameCustomer()) {
			medProGuardianDTO.setCustomer(medicalProposalDTO.getCustomer());
			medProGuardianDTO.setSameCustomer(true);
			guardianPersonFullName = medicalProposalDTO.getCustomer().getFullName();
		}
	}

	public void handleAddOnDialogClose() {
		createNewMedicalInsuredPerson();
		PrimeFaces.current().executeScript("PF('addOnConfigDialog').hide();");
	}

	public int getMaxUnit() {
		return productService.findProductById(KeyFactorChecker.getMEDPRO1ID()).getMaxUnit();
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
		if (medicalProposalDTO.getCustomer() != null) {
			if (medicalProposalDTO.getCustomer().getIdType().equals(IdType.PASSPORTNO)) {
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

	public MedProGuardianDTO getMedProGuardianDTO() {
		return medProGuardianDTO;
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

	public List<MedProInsuAddOnDTO> getInsuredPersonAddOnList() {
		return insuredPersonAddOnList;
	}

	public void setInsuredPersonAddOnList(List<MedProInsuAddOnDTO> insuredPersonAddOnList) {
		this.insuredPersonAddOnList = insuredPersonAddOnList;
	}

	public boolean isCriticalIllnessRender() {
		return criticalIllnessRender;
	}

	public GroupMicroHealthDTO getGroupMicroHealthDTO() {
		return groupMicroHealthDTO;
	}

	public void setGroupMicroHealthDTO(GroupMicroHealthDTO groupMicroHealthDTO) {
		this.groupMicroHealthDTO = groupMicroHealthDTO;
	}

	public boolean isChannel() {
		return isChannel;
	}

	public void setChannel(boolean isChannel) {
		this.isChannel = isChannel;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
	}

}
