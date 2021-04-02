package org.ace.insurance.web.manage.reversal;

import java.io.Serializable;
import java.text.DecimalFormat;
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
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.SalutationType;
import org.ace.insurance.common.utils.SumInsuredType;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.keyfactor.service.KeyFactorService;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.system.common.typesOfSport.TypesOfSport;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.PeriodType;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.SurveyAnswerOne;
import org.ace.insurance.web.common.SurveyAnswerTwo;
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.manage.life.proposal.BeneficiariesInfoDTO;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonAddOnDTO;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "EditLifeProposalReversalActionBean")
public class EditLifeProposalReversalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean createNew;

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

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
	}

	private LifeProposal lifeProposal;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{CustomerService}")
	private ICustomerService customerService;

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
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
	
	@ManagedProperty(value = "#{KeyFactorService}")
	private KeyFactorService keyfactorService;

	public void setKeyfactorService(KeyFactorService keyfactorService) {
		this.keyfactorService = keyfactorService;
	}

	private List<Product> productList;
	private String userType;
	private List<RelationShip> relationshipList;
	private Product product;
	private BancaassuranceProposal bancaassuranceProposal;
	private boolean isPersonalAccident;
	private boolean isFarmer;
	private boolean isPublicTermLife;
	private boolean isShortEndowLife;
	private boolean isSinglePremiumEndowmentLife;
	private boolean isSinglePremiumCreditLife;
	private boolean isShortTermSinglePremiumCreditLife;
	private boolean isGroupLife;
	private boolean isSimpleLife;
	private boolean isMonthBase;
	private boolean isEndorse;
	private boolean isSTELendorse;
	private boolean isPeriodOfYear = false;
	private boolean isPeriodofWeek = false;
	private boolean isPeriodofMonth = false;
	private boolean isExcelUpload = false;
	private int maximumTremWeek;
	private int munimumTremWeek;
	private List<BancaMethod> bancaMethodList;
	private List<KeyFactor> simpleLifeKeyfactorList;
	private KeyFactor simpleLifeKeyfactor;
	private PeriodType periodType;
	private boolean showSimpleLifeSurvey;
	private BancaMethod bancaMethod;
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();
	private List<TownshipCode> benTownshipCodeList = new ArrayList<TownshipCode>();

	private String branchId;

	private Entitys entitys;

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	private User user;

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
		removeParam("bancaassuranceProposal");
		removeParam("insuranceType");
	}

	private void initializeInjection() {
		lifeProposal = (LifeProposal) getParam("lifeProposal");
		bancaassuranceProposal = (BancaassuranceProposal) getParam("bancaassuranceProposal");
		InsuranceType insuranceType = (InsuranceType) getParam("insuranceType");
		productList = productService.findProductByInsuranceType(insuranceType);
		this.product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		stateCodeList = stateCodeService.findAllStateCode();
		bancaMethodList = bancaMethodService.findAllBanca();

	}

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		initializeInjection();
		entitys = lifeProposal.getBranch().getEntity();
		isEndorse = lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT);
		isPersonalAccident = (KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product));
		isFarmer = KeyFactorChecker.isFarmer(product);
		isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		isMonthBase = KeyFactorChecker.isSinglePremiumEndowmentLife(product) || KeyFactorChecker.isSinglePremiumCreditLife(product)
				|| KeyFactorChecker.isShortTermSinglePremiumCreditLife(product) || KeyFactorChecker.isPublicLife(product) || KeyFactorChecker.isGroupLife(product)
				|| isShortEndowLife ? false : true;
		isGroupLife = KeyFactorChecker.isGroupLife(product);
		isSportMan = KeyFactorChecker.isSportMan(product);
		isSimpleLife = KeyFactorChecker.isSimpleLife(product);
		insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
		relationshipList = relationShipService.findAllRelationShip();
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			InsuredPersonInfoDTO vhDTO = new InsuredPersonInfoDTO(pv);
			if (!vhDTO.getInsuredPersonAddOnDTOMap().isEmpty()) {
				vhDTO.setInsuredPersonAddOnDTOList(new ArrayList<>(vhDTO.getInsuredPersonAddOnDTOMap().values()));
			}
			insuredPersonInfoDTOMap.put(vhDTO.getTempId(), vhDTO);
		}

		if (isSimpleLife) {
			simpleLifeKeyfactorList = keyfactorService.findKeyFactorForSimpleLife();
			simpleLifeKeyfactor = new KeyFactor();
		}
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
		loadUserType();
		loadRenderValues();
		organization = lifeProposal.getCustomer() == null ? true : false;
	}

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCodeNo(insuredPersonInfoDTO.getProvinceCode());
	}

	public void changeBeneStateCode() {
		benTownshipCodeList = townshipCodeService.findByStateCodeNo(beneficiariesInfoDTO.getProvinceCode());
	}

	public void loadUserType() {
		if (lifeProposal.getAgent() != null) {
			userType = UserType.AGENT.toString();
		}
		if (lifeProposal.getSaleMan() != null) {
			userType = UserType.SALEMAN.toString();
		}
		if (lifeProposal.getReferral() != null) {
			userType = UserType.REFERRAL.toString();
		}
	}

	/*************************************************
	 * Start Beneficiary Manage
	 *********************************************************/
	private boolean createNewBeneficiariesInfo;
	private Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap;
	private BeneficiariesInfoDTO beneficiariesInfoDTO;

	public IdType[] getIdTypes() {
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

	public List<PaymentType> getPaymentTypes() {
		if (product == null) {
			return new ArrayList<PaymentType>();
		} else {
			return product.getPaymentTypeList();
		}
	}

	public BeneficiariesInfoDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public Product getProduct() {
		return product;
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
			Date currentDate = new Date();
			if (currentDate.compareTo(dob) < 0) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.INVALID_DATE_OF_BIRTH);
				valid = false;
			}
			if (age < 0 || age > 100) {
				addErrorMessage(formID + ":benificiaryAge", MessageId.INVALID_AGE);
				valid = false;
			}
		}
		return valid;

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

	private boolean validBeneficiary(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		boolean valid = true;
		String formID = "beneficiaryInfoEntryForm";
		if (isEmpty(beneficiariesInfoDTO.getName().getFirstName())) {
			addErrorMessage(formID + ":firstName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!beneficiariesInfoDTO.getIdType().equals(IdType.STILL_APPLYING) && isEmpty(beneficiariesInfoDTO.getFullIdNo())) {
			addErrorMessage(formID + ":idNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (isEmpty(beneficiariesInfoDTO.getResidentAddress().getResidentAddress())) {
			addErrorMessage(formID + ":address", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (beneficiariesInfoDTO.getResidentAddress().getTownship() == null) {
			addErrorMessage(formID + ":townShip", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (beneficiariesInfoDTO.getPercentage() < 1 || beneficiariesInfoDTO.getPercentage() > 100) {
			addErrorMessage(formID + ":percentage", MessageId.BENEFICIARY_PERCENTAGE);
			valid = false;
		}
		return valid;
	}

	public void removeBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfoDTO.getTempId());
		insuredPersonInfoDTO.getBeneficiariesInfoDTOList().remove(beneficiariesInfoDTO);
		createNewBeneficiariesInfo();
	}

	/*************************************************
	 * End Beneficiary Manage
	 *********************************************************/

	/*************************************************
	 * Start AddOn Manage
	 *********************************************************/
	public void removeAddOn(InsuredPersonAddOnDTO insuredPersonAddOnDTO) {
		insuredPersonInfoDTO.removeInsuredPersonAddOn(insuredPersonAddOnDTO);
	}

	/*************************************************
	 * End AddOn Manage
	 *********************************************************/

	/*************************************************
	 * Start InsuredPerson Manage
	 ******************************************************/
	private Map<String, InsuredPersonInfoDTO> insuredPersonInfoDTOMap;
	private InsuredPersonInfoDTO insuredPersonInfoDTO;
	private boolean createNewIsuredPersonInfo;
	private Boolean isEdit = false;
	private Boolean isReplace = false;
	private boolean isSportMan;

	public boolean getIsSportMan() {
		return isSportMan;
	}

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	public void changeProduct(AjaxBehaviorEvent e) {
		loadRenderValues();
	}

	private void loadRenderValues() {
		isMonthBase = KeyFactorChecker.isPublicLife(product) || KeyFactorChecker.isGroupLife(product) || isShortEndowLife || KeyFactorChecker.isSinglePremiumCreditLife(product)
				|| KeyFactorChecker.isSinglePremiumEndowmentLife(product) || KeyFactorChecker.isShortTermSinglePremiumCreditLife(product) ? false : true;
		isFarmer = KeyFactorChecker.isFarmer(product);
		isSportMan = KeyFactorChecker.isSportMan(product);
		isPersonalAccident = (KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product));
		isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
	}

	private void createNewInsuredPersonInfo() {
		createNewIsuredPersonInfo = true;
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		beneficiariesInfoDTO = new BeneficiariesInfoDTO();
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
		insuredPersonInfoDTO.setProduct(product);
		insuredPersonInfoDTO.setStartDate(new Date());
		insuredPersonInfoDTO.setIdType(IdType.NRCNO);
		isEdit = false;
		isReplace = false;
	}

	public InsuredPersonInfoDTO getInsuredPersonInfoDTO() {
		return insuredPersonInfoDTO;
	}

	public void setInsuredPersonInfoDTO(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;

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

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
	}

	public boolean isBmiInRange(double bmiValue) {
		
		boolean result = false;
		if (bmiValue >= 16 && bmiValue <= 30) {
			result = true;
		}
		return result; 
	}
	
	public void prepareEditInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		insuredPersonInfoDTO.loadFullIdNo();
		this.insuredPersonInfoDTO = new InsuredPersonInfoDTO(insuredPersonInfoDTO);
		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (BeneficiariesInfoDTO bdto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
			}
		}
		if (KeyFactorChecker.isSportMan(insuredPersonInfoDTO.getProduct())) {
			isSportMan = true;
		}
		createNewIsuredPersonInfo = false;
		changeStateCode();
		isEdit = true;
		
		if (isSimpleLife) {

			if (lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYear() > 0) {
				this.periodType = PeriodType.YEAR;

			} else if (lifeProposal.getProposalInsuredPersonList().get(0).getPeriodMonth() > 0) {
				this.insuredPersonInfoDTO.setPeriodOfYears(lifeProposal.getProposalInsuredPersonList().get(0).getPeriodMonth());
				this.periodType = PeriodType.MONTH;
			} else {
				this.insuredPersonInfoDTO.setPeriodOfYears(lifeProposal.getProposalInsuredPersonList().get(0).getPeriodWeek());
				this.periodType = PeriodType.WEEK;
			}
			//changePeriodType(null);
			
			if (isLessThanOneMonthAndGreater1Mi() || isGraterOneMonthAndAvobe2Mi()) {
				showSimpleLifeSurvey = true;
			} else {
				showSimpleLifeSurvey = false;
			}
			
		}	
	}

	public void prepareReplaceInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
		createNewBeneficiariesInfoDTOMap();
		createNewIsuredPersonInfo = false;
		isReplace = true;
	}

	private UploadedFile uploadedFile;

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public void saveInsuredPersonInfo() {
		insuredPersonInfoDTO.setFullIdNo();
		insuredPersonInfoDTO.setProduct(product);
		if (isEndorse) {
			PolicyInsuredPerson policyInsuPerson = lifePolicyService.findInsuredPersonByCodeNo(insuredPersonInfoDTO.getInsPersonCodeNo());
			if (isEdit == true) {
				insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.EDIT);
			} else if (isReplace == true) {
				insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.REPLACE);
			} else if (policyInsuPerson == null) {
				insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.NEW);
			}
		}

		Calendar cal = Calendar.getInstance();
		if (insuredPersonInfoDTO.getStartDate() == null) {
			insuredPersonInfoDTO.setStartDate(new Date());
		}
		if (isSimpleLife) {

			if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
				cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
				insuredPersonInfoDTO.setPeriodMonth(0);
				insuredPersonInfoDTO.setPeriodOfWeek(0);

			} else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
				cal.add(Calendar.MONTH, insuredPersonInfoDTO.getPeriodMonth());
				insuredPersonInfoDTO.setPeriodMonth(insuredPersonInfoDTO.getPeriodOfYears());
				insuredPersonInfoDTO.setPeriodOfYears(0);
			} else {
				cal.add(Calendar.DAY_OF_MONTH, insuredPersonInfoDTO.getPeriodOfWeek() * 7);
				insuredPersonInfoDTO.setPeriodOfWeek(insuredPersonInfoDTO.getPeriodOfYears());
				insuredPersonInfoDTO.setPeriodOfYears(0);
				insuredPersonInfoDTO.setPeriodMonth(0);
			}

		} 
		else {
			if (isMonthBase) {
				cal.add(Calendar.MONTH, insuredPersonInfoDTO.getPeriodOfYears());
			} else {
				cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
			}
		}
		if (validInsuredPerson(insuredPersonInfoDTO)) {
			if (isMonthBase) {
				cal.add(Calendar.MONTH, insuredPersonInfoDTO.getPeriodOfYears());
			} else {
				cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
			}

			insuredPersonInfoDTO.setEndDate(cal.getTime());
			if (!isEndorse) {
				
				int period = 0;
				if (isSimpleLife) {
					if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
						period = insuredPersonInfoDTO.getPeriodOfYears();
					}else if(insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
						period = insuredPersonInfoDTO.getPeriodMonth();
					}else if(insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
						period = insuredPersonInfoDTO.getPeriodOfWeek();
					}
				 }
				 else {
					period = insuredPersonInfoDTO.getPeriodOfYears();
				 }
				setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), period, insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
				insuredPersonInfoDTO.setEndDate(cal.getTime());
			} else {
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
				if (checkPublicLife()) {
					if (insuredPersonInfoDTO.getEndorsementStatus() == EndorsementStatus.TERMINATE) {
						setKeyFactorValue(lifeProposal.getLifePolicy().getInsuredPersonInfo().get(0).getSumInsured(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()),
								lifeProposal.getLifePolicy().getInsuredPersonInfo().get(0).getPeriodYears(), insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
					} else {
						if (isIncreasedSIAmount(insuredPersonInfoDTO)) {
							setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(),
									lifeProposal.getLifePolicy().getInsuredPersonInfo().get(0).getPeriodYears(), insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
						} else {
							setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()),
									insuredPersonInfoDTO.getPeriodOfYears(), insuredPersonInfoDTO.getPeriodType(), simpleLifeKeyfactor);
						}
					}
					insuredPersonInfoDTO.setEndDate(cal.getTime());
				} else {
					if (insuredPersonInfoDTO.getEndorsementStatus() == EndorsementStatus.TERMINATE) {
						setKeyFactorValue(policyInsuPerson.getSumInsured(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()), policyInsuPerson.getPeriodYears(), insuredPersonInfoDTO.getPeriodType(),
								simpleLifeKeyfactor);
					} else {
						setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears(), insuredPersonInfoDTO.getPeriodType(),
								simpleLifeKeyfactor);
					}
					insuredPersonInfoDTO.setEndDate(lifeProposal.getLifePolicy().getInsuredPersonInfo().get(0).getEndDate());
				}
			}
			insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			createNewInsuredPersonInfo();
			createNewBeneficiariesInfoDTOMap();
		}
	}

	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	public double getAvailableSISportMan(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		double availableSI = 0.0;
		double maxSi = insuredPersonInfoDTO.getProduct().getMaxSumInsured();
		availableSI = maxSi - (getTotalSISportMan(insuredPersonInfoDTO));

		return availableSI;
	}

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

	public boolean isExcessSISportMan(InsuredPersonInfoDTO insuredPersonInfoDTO) {

		double sumInsured = getTotalSISportMan(insuredPersonInfoDTO) + insuredPersonInfoDTO.getSumInsuredInfo();

		return sumInsured > insuredPersonInfoDTO.getProduct().getMaxSumInsured() ? true : false;
	}

	private boolean validInsuredPerson(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		boolean valid = true;
		String formID = "lifeProposalEntryForm";
		int maxAge = product.getMaxAge();
		int minAge = product.getMinAge();
		int personAge = insuredPersonInfoDTO.getAgeForNextYear();
		int periodofYear = insuredPersonInfoDTO.getPeriodOfYears();
		if (personAge < minAge) {
			addErrorMessage(formID + ":dateOfBirth", MessageId.MINIMUN_INSURED_PERSON_AGE, minAge);
			valid = false;
		} else if (personAge > maxAge) {
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
		}else if (insuredPersonInfoDTO.getPounds() > 25) {
			addErrorMessage(formID + ":weight", MessageId.BMI_MAXIMUM_POUNDS_LIMATION);
			valid = false;
		}

		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList().isEmpty()) {
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

		if (lifeProposal.getLifePolicy() != null && checkPublicLife()) {
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
				addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", 
						"BMI value should be between 16-30.");
			}
			
			if (insuredPersonInfoDTO.getSurveyquestionOne().equals(SurveyAnswerOne.YES) 
					|| insuredPersonInfoDTO.getSurveyquestionTwo().equals(SurveyAnswerTwo.YES)) {
				valid = false;
				addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", 
						"Cannot add insured person for answering YES in survey questions.");
			}
			
		}

		return valid;
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

	public void removeInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		if (isEndorse && lifePolicyService.findInsuredPersonByCodeNo(insuredPersonInfoDTO.getInsPersonCodeNo()) != null) {
			insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.TERMINATE);
		} else {
			insuredPersonInfoDTOMap.remove(insuredPersonInfoDTO.getTempId());
		}
		createNewInsuredPersonInfo();
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

	public boolean isCreateNew() {
		return createNew;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public boolean getIsPersonalAccident() {
		return isPersonalAccident;
	}

	public boolean getIsFarmer() {
		return isFarmer;
	}

	public boolean getIsShortEndowLife() {
		return isShortEndowLife;
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

	public boolean getIsMonthBase() {
		return isMonthBase;
	}

	public boolean getIsEndorse() {
		return isEndorse;
	}

	public List<Integer> getPeriodMonths() {
		return Arrays.asList(3, 6, 12);
	}

	/* Short Term Endowment Life */
	public List<Integer> getSePeriodYears() {
		return Arrays.asList(5, 7, 10);
	}

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

	public SurveyAnswerOne[] getSurveyAnswerOne() {
		return SurveyAnswerOne.values();
	}
	
	public SurveyAnswerTwo[] getSurveyAnswerTwo() {
		return SurveyAnswerTwo.values();
	}
	
	public List<Number> getSelectSI() {
		Number maxSI = product.getMaxSumInsured();
		Number minSI = product.getMinSumInsured();

		List<Number> lists = new ArrayList<Number>();

		do {
			lists.add(minSI.doubleValue());
			minSI = minSI.doubleValue() + 1000000;
		} while (minSI.intValue() <= maxSI.doubleValue());

		return lists;
	}
	
	public List<Integer> getSimpleLifePeriodYear() {	
	if(isPeriodOfYear) {
		return Arrays.asList(1);
	}
	if(isPeriodofMonth) {
		return Arrays.asList(1,2,3,4,5,6,7,8,9,10,11);
	}
	if(isPeriodofWeek) {
		return Arrays.asList(1,2,3);
	}
	return Arrays.asList(1,2,3);
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

	public String updateLifeProposal() {
		String result = null;
		try {
			lifeProposal.setInsuredPersonList(getInsuredPersonInfoList());
			if (isEndorse) {
				lifeEndorsementService.overWriteLifeProposal(lifeProposal);
			} else {
				lifeProposalService.overWriteLifeProposal(lifeProposal, bancaassuranceProposal);
			}

			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}

		return result;
	}

	public List<ProposalInsuredPerson> getInsuredPersonInfoList() {
		List<ProposalInsuredPerson> result = new ArrayList<ProposalInsuredPerson>();
		if (insuredPersonInfoDTOMap.values() != null) {
			for (InsuredPersonInfoDTO insuredPersonInfoDTO : insuredPersonInfoDTOMap.values()) {
				Customer customer = customerService.findCustomerByInsuredPerson(insuredPersonInfoDTO.getName(), insuredPersonInfoDTO.getIdNo(),
						insuredPersonInfoDTO.getFatherName(), insuredPersonInfoDTO.getDateOfBirth());
				for (BeneficiariesInfoDTO dto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
					dto.setExistEntity(false);
				}
				ProposalInsuredPerson proposalInsuredPerson = new ProposalInsuredPerson(insuredPersonInfoDTO);
				proposalInsuredPerson.setLifeProposal(lifeProposal);
				proposalInsuredPerson.setCustomer(customer);

				if (isSimpleLife) {
					if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
						proposalInsuredPerson.setPeriodYear(insuredPersonInfoDTO.getPeriodOfYears());
						proposalInsuredPerson.setPeriodMonth(0);
						proposalInsuredPerson.setPeriodWeek(0);
						proposalInsuredPerson.setPeriodType(insuredPersonInfoDTO.getPeriodType());
					}else if(insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
						proposalInsuredPerson.setPeriodYear(0);
						proposalInsuredPerson.setPeriodWeek(0);
						proposalInsuredPerson.setPeriodMonth(insuredPersonInfoDTO.getPeriodMonth());
						proposalInsuredPerson.setPeriodType(insuredPersonInfoDTO.getPeriodType());
					}else if(insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
						proposalInsuredPerson.setPeriodWeek(insuredPersonInfoDTO.getPeriodOfWeek());
						proposalInsuredPerson.setPeriodMonth(0);
						proposalInsuredPerson.setPeriodYear(0);
						proposalInsuredPerson.setPeriodType(insuredPersonInfoDTO.getPeriodType());
					}
				 }
				
				result.add(proposalInsuredPerson);
			}
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
				} else if (KeyFactorChecker.isFarmer(product) || KeyFactorChecker.isPublicLife(product)) {
					if (personSize > 1) {
						addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", MessageId.INVALID_INSURED_PERSON, product.getName());
						valid = false;
					}
				}
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public String getPublicLifeId() {
		return ProductIDConfig.getPublicLifeId();
	}

	public Boolean isChangePeriod() {
		if (insuredPersonInfoDTO.getPeriodOfYears() != lifeProposal.getLifePolicy().getInsuredPersonInfo().get(0).getPeriodYears()) {
			return true;
		}
		return false;
	}

	public Boolean isIncreasedSIAmount(InsuredPersonInfoDTO dto) {
		if (dto.getSumInsuredInfo() <= lifeProposal.getLifePolicy().getPolicyInsuredPersonList().get(0).getSumInsured()) {
			return false;
		}
		return true;
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

	public int getAgeForOldYear(Date dob) {
		Calendar cal_1 = Calendar.getInstance();
		cal_1.setTime(lifeProposal.getLifePolicy().getCommenmanceDate());
		int currentYear = cal_1.get(Calendar.YEAR);
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(dob);
		cal_2.set(Calendar.YEAR, currentYear);
		if (new Date().after(cal_2.getTime())) {
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

	/*
	 * private void setKeyFactorValue(double sumInsured, int age, int period) { for
	 * (InsuredPersonKeyFactorValue vehKF :
	 * insuredPersonInfoDTO.getKeyFactorValueList()) { KeyFactor kf =
	 * vehKF.getKeyFactor(); if (KeyFactorChecker.isSumInsured(kf)) {
	 * vehKF.setValue(sumInsured + ""); } else if (KeyFactorChecker.isAge(kf)) {
	 * vehKF.setValue(age + ""); } else if (KeyFactorChecker.isMedicalAge(kf)) {
	 * vehKF.setValue(age + ""); } else if (KeyFactorChecker.isTerm(kf)) {
	 * vehKF.setValue(period + ""); } } }
	 */
	
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

	public String getUserType() {
		if (lifeProposal.getAgent() != null) {
			userType = UserType.AGENT.toString();
		} else if (lifeProposal.getSaleMan() != null) {
			userType = UserType.SALEMAN.toString();
		} else if (lifeProposal.getReferral() != null) {
			userType = UserType.REFERRAL.toString();
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
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

	public SalutationType[] getSalutationTypes() {
		return SalutationType.values();
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		lifeProposal.setCustomer(customer);
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

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		lifeProposal.setOrganization(organization);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		lifeProposal.setPaymentType(paymentType);
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

//	public void selectBancaBrm() {
//		selectBancaBRM(bancaassuranceProposal.getBancaBranch().getId());
//	}

//	public void selectBancaReferralByOTC() {
//		selectBancaReferralByOTC(bancaassuranceProposal.getBancaBranch().getId());
//	}
//
//	public void selectBancaReferral() {
//		selectBancaReferral(bancaassuranceProposal.getBancaBranch().getId());
//	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		bancaassuranceProposal.setChannel(channel);
	}

	public void returnBancaLIC(SelectEvent event) {
		BancaLIC bancaLIC = (BancaLIC) event.getObject();
		bancaassuranceProposal.setBancaLIC(bancaLIC);
	}

	public void returnBancaBrm(SelectEvent event) {
		BancaBRM bancaBrm = (BancaBRM) event.getObject();
		bancaassuranceProposal.setBancaBRM(bancaBrm);
	}

	public void returnBancaRefferal(SelectEvent event) {
		BancaRefferal bancaRefferal = (BancaRefferal) event.getObject();
		bancaassuranceProposal.setBancaRefferal(bancaRefferal);
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		bancaassuranceProposal.setBancaBranch(bancaBranch);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		lifeProposal.setBranch(branch);
		lifeProposal.setSalePoint(null);
	}

	public void returnRelationShip(SelectEvent event) {
		RelationShip relationShip = (RelationShip) event.getObject();
		beneficiariesInfoDTO.setRelationship(relationShip);
	}

	public void returnInsuredPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		insuredPersonInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonInfoDTO.setOccupation(occupation);
	}

	public void returnTypesOfSport(SelectEvent event) {
		TypesOfSport typesOfSport = (TypesOfSport) event.getObject();
		insuredPersonInfoDTO.setTypesOfSport(typesOfSport);
	}

	public void returnInsuPersonTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		insuredPersonInfoDTO.getResidentAddress().setTownship(township);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		lifeProposal.setSalePoint(salePoint);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(lifeProposal.getBranch() == null ? "" : lifeProposal.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void removeBranch() {
		lifeProposal.setBranch(null);
		lifeProposal.setSalePoint(null);

	}

	public int getMaximumTrem() {
		int result = 0;
		if (product != null) {
			result = isMonthBase ? product.getMaxTerm() : product.getMaxTerm() / 12;
		}
		return result;
	}

	public int getMunimumTrem() {
		int result = 0;
		if (product != null) {
			result = isMonthBase ? product.getMinTerm() : product.getMinTerm() / 12;
		}
		return result;
	}

	public void selectAddOn() {
		selectAddOn(insuredPersonInfoDTO.getProduct());
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	@SuppressWarnings("unchecked")
	public void returnAddOn(SelectEvent event) {
		InsuredPersonAddOnDTO insuredPersonAddOnDTO = null;
		Map<String, InsuredPersonAddOnDTO> insuredPersonAddOnDTOMap = null;
		List<AddOn> addOnList = (List<AddOn>) event.getObject();
		if (!addOnList.isEmpty()) {
			insuredPersonAddOnDTOMap = new HashMap<String, InsuredPersonAddOnDTO>();
			for (AddOn addOn : addOnList) {
				insuredPersonAddOnDTO = new InsuredPersonAddOnDTO();
				insuredPersonAddOnDTO.setAddOn(addOn);
				insuredPersonAddOnDTOMap.put(addOn.getId(), insuredPersonAddOnDTO);
			}
		}
		if (!insuredPersonAddOnDTOMap.isEmpty()) {
			insuredPersonInfoDTO.setInsuredPersonAddOnDTOList(new ArrayList<InsuredPersonAddOnDTO>(insuredPersonAddOnDTOMap.values()));
		}
	}

	public void changeInsuredPersonIdType() {
		checkIdType("I");
	}

	public void changeBeneIdType() {
		checkIdType("B");
	}

	public void checkIdType(String customerType) {
		if ("I".equals(customerType)) {
			insuredPersonInfoDTO.setProvinceCode(null);
			insuredPersonInfoDTO.setTownshipCode(null);
			insuredPersonInfoDTO.setIdConditionType(null);
			insuredPersonInfoDTO.setIdNo(null);
			insuredPersonInfoDTO.setFullIdNo(null);

		} else if ("B".equals(customerType)) {
			beneficiariesInfoDTO.setProvinceCode(null);
			beneficiariesInfoDTO.setTownshipCode(null);
			beneficiariesInfoDTO.setIdConditionType(null);
			beneficiariesInfoDTO.setIdNo("");
			beneficiariesInfoDTO.setFullIdNo(null);
		}
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public String getPageHeader() {
		return "Edit " + (isEndorse ? "Life Endorsement"
				: isFarmer ? "Farmer"
						: isPersonalAccident ? "Personal Accident"
								: (isSinglePremiumEndowmentLife || isSinglePremiumCreditLife) ? "Single Premium Endowment"
										: isShortTermSinglePremiumCreditLife ? "Short Term Single Premium Credit Life" : isShortEndowLife ? "Short Term Endowment Life" : "Life")
				+ " Proposal";
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

	public Map<String, BeneficiariesInfoDTO> getBeneficiariesInfoDTOMap() {
		return beneficiariesInfoDTOMap;
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

	public ClassificationOfHealth[] getClassificationHealthList() {
		return ClassificationOfHealth.values();
	}

	public boolean getIsGroupLife() {
		return isGroupLife;
	}

	public void calculateAgeForBene() {
		beneficiariesInfoDTO.setAge(getAgeForBeneNextYear());
	}

	public void calculateBMI() {
		insuredPersonInfoDTO.setBmi(getBMI());
	}
	
	public boolean isSTELendorse() {
		return isSTELendorse;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
	}

	public List<BancaMethod> getBancaMethodList() {
		return bancaMethodList;
	}

	public void setBancaMethodList(List<BancaMethod> bancaMethodList) {
		this.bancaMethodList = bancaMethodList;
	}

	public BancaMethod getBancaMethod() {
		return bancaMethod;
	}

	public void setBancaMethod(BancaMethod bancaMethod) {
		this.bancaMethod = bancaMethod;
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
		
		// 1 inch = 0.08333 feet
		double heightInFeetDbl = insuredPersonInfoDTO.getFeets() + (insuredPersonInfoDTO.getInches() * 0.08333);
//		long heightInFeet = Math.round(heightInFeetDbl);
		
		// 1lb = 0.45kg
		double weightInKg = insuredPersonInfoDTO.getWeight() * 0.45;
		
		// 1ft = 0.3048 meter
		double heightInMeter = heightInFeetDbl * 0.3048;
		
//		long bmiWeight = Math.round(weightInKg / (heightInMeter * heightInMeter));
		double bmiWeight = weightInKg / (heightInMeter * heightInMeter);
		
		if (insuredPersonInfoDTO.getFeets() == 0 || insuredPersonInfoDTO.getWeight() == 0
				|| bmiWeight == 0) {
			return 0;
		}
		
		DecimalFormat df = new DecimalFormat("#.##"); 
		String bmiWeightStr = df.format(bmiWeight);

		// storing height in inches at below formula
		insuredPersonInfoDTO.setHeight(insuredPersonInfoDTO.getFeets() * 12 + insuredPersonInfoDTO.getInches());
		
		return Double.parseDouble(bmiWeightStr);
	}
	
	public boolean isSimpleLife() {
		return isSimpleLife;
	}

	public void setSimpleLife(boolean isSimpleLife) {
		this.isSimpleLife = isSimpleLife;
	}

	public boolean getIsSimpleLife() {
		return isSimpleLife;
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
	
	public boolean isPeriodType() {

		if(insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {
			return isPeriodOfYear = true;
		}else if(insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
			return isPeriodofMonth = true;
		}else {
			return isPeriodofWeek = true;
		}  
	}


	public void changePeriodType(AjaxBehaviorEvent event) {
		
		if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.YEAR)) {	
			this.isPeriodOfYear = true;
			this.isPeriodofMonth = false;
			this.isPeriodofWeek = false;
			insuredPersonInfoDTO.setPeriodOfYears(0);
		}else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.MONTH)) {
			this.isPeriodofMonth = true;
			this.isPeriodOfYear = false;
			this.isPeriodofWeek = false;
			insuredPersonInfoDTO.setPeriodOfYears(0);

		}else if (insuredPersonInfoDTO.getPeriodType().equals(PeriodType.WEEK)) {
			this.isPeriodofWeek = true;
			this.isPeriodOfYear = false;
			this.isPeriodofMonth = false;
			insuredPersonInfoDTO.setPeriodOfYears(0);
		}

	}
	public int getMaximumTremWeek() {
		return maximumTremWeek;
	}

	public int getMunimumTremWeek() {
		return munimumTremWeek;
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

	public PeriodType getPeriodType() {
		return periodType;
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
	
	public boolean isShowSimpleLifeSurvey() {
		return showSimpleLifeSurvey;
	}
	
	public boolean isExcelUpload() {
		return isExcelUpload;
	}

	public void setExcelUpload(boolean isExcelUpload) {
		this.isExcelUpload = isExcelUpload;
	}
	
	public boolean getIsPublicTermLife() {
		return isPublicTermLife;
	}

	public boolean isPublicTermLife() {
		return isPublicTermLife;
	}

	public void setPublicTermLife(boolean isPublicTermLife) {
		this.isPublicTermLife = isPublicTermLife;
	}

	public boolean isSinglePremiumCreditLife() {
		return isSinglePremiumCreditLife;
	}

	public boolean isShortTermSinglePremiumCreditLife() {
		return isShortTermSinglePremiumCreditLife;
	}

	public boolean isSinglePremiumEndowmentLife() {
		return isSinglePremiumEndowmentLife;
	}

	public void setSinglePremiumCreditLife(boolean isSinglePremiumCreditLife) {
		this.isSinglePremiumCreditLife = isSinglePremiumCreditLife;
	}

	public void setShortTermSinglePremiumCreditLife(boolean isShortTermSinglePremiumCreditLife) {
		this.isShortTermSinglePremiumCreditLife = isShortTermSinglePremiumCreditLife;
	}

	public void setSinglePremiumEndowmentLife(boolean isSinglePremiumEndowmentLife) {
		this.isSinglePremiumEndowmentLife = isSinglePremiumEndowmentLife;
	}

}
