package org.ace.insurance.web.manage.reversal;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.SalutationType;
import org.ace.insurance.common.utils.SumInsuredType;
import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.service.interfaces.IAgentService;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.service.interfaces.IPaymentTypeService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.PeriodType;
import org.ace.insurance.web.common.SaleChannelType;
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

import com.csvreader.CsvReader;

@ViewScoped
@ManagedBean(name = "EditLifePolicyReversalActionBean")
public class EditLifePolicyReversalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{PaymentTypeService}")
	private IPaymentTypeService paymentTypeService;

	public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
		this.paymentTypeService = paymentTypeService;
	}

	@ManagedProperty(value = "#{AgentService}")
	private IAgentService agentService;

	public void setAgentService(IAgentService agentService) {
		this.agentService = agentService;
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

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townShipService;

	public void setTownShipService(ITownshipService townShipService) {
		this.townShipService = townShipService;
	}

	@ManagedProperty(value = "#{OccupationService}")
	private IOccupationService occupationService;

	public void setOccupationService(IOccupationService occupationService) {
		this.occupationService = occupationService;
	}

	@ManagedProperty(value = "#{CustomerService}")
	private ICustomerService customerService;

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaMethodService;

	public void setBancaMethodService(IBancaMethodService bancaMethodService) {
		this.bancaMethodService = bancaMethodService;
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

	private LifePolicy lifePolicy;
	private BancaassurancePolicy bancaassurancePolicy;
	private BancaMethod bancaMethod;
	private Product product;
	private boolean createNew;
	private boolean isChannel;
	private boolean isPublicTermLife;
	private boolean isSinglePremiumEndowmentLife;
	private boolean isShortTermSinglePremiumCreditLife;
	private boolean isSinglePremiumCreditLife;
	private List<BancaMethod> bancaMethodList;
	private List<Product> productList;
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();
	private List<TownshipCode> benTownshipCodeList = new ArrayList<TownshipCode>();
	private String userType;
	private boolean isAgent = true;

	@PostConstruct
	public void init() {
		lifePolicy = (LifePolicy) getParam("lifePolicy");
		bancaassurancePolicy = (BancaassurancePolicy) getParam("bancaassurancePolicy");
		InsuranceType insuranceType = (InsuranceType) getParam("PA_PRODUCT");
		productList = productService.findProductByInsuranceType(insuranceType);
		this.product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
		bancaMethodList = bancaMethodService.findAllBanca();
		stateCodeList = stateCodeService.findAllStateCode();
		isChannel = false;
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isPublicTermLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		if (lifePolicy.getCustomer() == null) {
			organization = true;
		}
		relationShipMap = new HashMap<String, RelationShip>();
		insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
		productMap = new HashMap<String, Product>();
		for (PolicyInsuredPerson pv : lifePolicy.getPolicyInsuredPersonList()) {
			InsuredPersonInfoDTO vhDTO = new InsuredPersonInfoDTO(pv);
			insuredPersonInfoDTOMap.put(vhDTO.getTempId(), vhDTO);
		}
		List<RelationShip> relationShipList = relationShipService.findAllRelationShip();
		relationShipItemList = new ArrayList<SelectItem>();
		for (RelationShip relationShip : relationShipList) {
			relationShipItemList.add(new SelectItem(relationShip.getName(), relationShip.getName()));
			relationShipMap.put(relationShip.getName(), relationShip);
		}
		productSelectItems = new ArrayList<SelectItem>();
		if (productSelectItems == null || productSelectItems.isEmpty()) {
			if (InsuranceType.PERSONAL_ACCIDENT.equals(insuranceType)) {
				productList = productService.findProductByInsuranceType(InsuranceType.PERSONAL_ACCIDENT);
			} else {
				productList = productService.findProductByInsuranceType(InsuranceType.LIFE);
			}
			for (Product product : productList) {
				productSelectItems.add(new SelectItem(product.getId(), product.getName()));
				productMap.put(product.getId(), product);
			}
			selectedProductId = productList.get(0).getId();
		}
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
		organization = lifePolicy.getCustomer() == null ? true : false;
		isAgent = lifePolicy.getAgent() != null ? true : false;
		loadUserType();
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifePolicy");
		removeParam("bancaassurancePolicy");
		removeParam("PA_PRODUCT");
	}

	public void loadUserType() {
		if (lifePolicy.getAgent() != null) {
			userType = UserType.AGENT.toString();
		}
		if (lifePolicy.getSaleMan() != null) {
			userType = UserType.SALEMAN.toString();
		}
		if (lifePolicy.getReferral() != null) {
			userType = UserType.REFERRAL.toString();
		}
	}

	public PeriodType[] getPeriodType() {
		return new PeriodType[] { PeriodType.MONTH, PeriodType.YEAR };
	}

	/*************************************************
	 * Start Beneficiary Manage
	 *********************************************************/
	private boolean createNewBeneficiariesInfo;
	private Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap;
	private BeneficiariesInfoDTO beneficiariesInfoDTO;
	private String beneficiaryRelationShipId;

	public String getBeneficiaryRelationShipId() {
		return beneficiaryRelationShipId;
	}

	public void setBeneficiaryRelationShipId(String beneficiaryRelationShipId) {
		this.beneficiaryRelationShipId = beneficiaryRelationShipId;
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public SumInsuredType[] getSumInsuredType() {
		return SumInsuredType.values();
	}

	public IdConditionType[] getIdConditionType() {
		return IdConditionType.values();
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	private void createNewBeneficiariesInfo() {
		createNewBeneficiariesInfo = true;
		beneficiariesInfoDTO = new BeneficiariesInfoDTO();
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

	public void calculateAgeForBene() {
		beneficiariesInfoDTO.setAge(getAgeForBeneNextYear());
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

	public void prepareEditBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
		this.createNewBeneficiariesInfo = false;
		if (beneficiariesInfoDTO.getRelationship() != null) {
			this.beneficiaryRelationShipId = beneficiariesInfoDTO.getRelationship().getName();
		} else {
			this.beneOrganization = true;
			this.beneficiariesInfoDTO = new BeneficiariesInfoDTO(beneficiariesInfoDTO);
			this.beneficiaryRelationShipId = null;
		}
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

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCodeNo(insuredPersonInfoDTO.getProvinceCode());
	}

	public void changeBeneStateCode() {
		benTownshipCodeList = townshipCodeService.findByStateCodeNo(beneficiariesInfoDTO.getProvinceCode());
	}

	public void changeBeneIdType() {
		checkIdType("B");
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

	public void saveBeneficiariesInfo() {
		if (validBeneficiary(beneficiariesInfoDTO)) {
			RelationShip selectedRelationShip = relationShipMap.get(beneficiaryRelationShipId);
			beneficiariesInfoDTO.setRelationship(selectedRelationShip);
			beneficiaryRelationShipId = null;
			beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
			insuredPersonInfoDTO.setBeneficiariesInfoDTOList(new ArrayList<BeneficiariesInfoDTO>(sortByValue(beneficiariesInfoDTOMap).values()));
			createNewBeneficiariesInfo();
			PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
		}
	}

	public Map<String, BeneficiariesInfoDTO> sortByValue(Map<String, BeneficiariesInfoDTO> map) {
		List<Map.Entry<String, BeneficiariesInfoDTO>> list = new LinkedList<Map.Entry<String, BeneficiariesInfoDTO>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, BeneficiariesInfoDTO>>() {
			public int compare(Map.Entry<String, BeneficiariesInfoDTO> o1, Map.Entry<String, BeneficiariesInfoDTO> o2) {
				long l1 = 0l;
				long l2 = 0l;
				try {
					l1 = Long.parseLong(o1.getKey());
					l2 = Long.parseLong(o2.getKey());
				} catch (NumberFormatException e) {
					try {
						l1 = Long.parseLong(o1.getKey().substring(11, 20));
						l2 = Long.parseLong(o2.getKey().substring(11, 20));
					} catch (StringIndexOutOfBoundsException exp) {
						l1 = Long.parseLong(o1.getKey().substring(11));
						l2 = Long.parseLong(o2.getKey().substring(11));
					}
				}
				if (l1 > l2) {
					return 1;
				} else if (l1 < l2) {
					return -1;
				} else {
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
		insuredPersonInfoDTO.addInsuredPersonAddOn(insuredPersonAddOnDTO);
		createInsuredPersonAddOnDTO();
	}

	public void removeAddOn(InsuredPersonAddOnDTO insuredPersonAddOnDTO) {
		insuredPersonInfoDTO.removeInsuredPersonAddOn(insuredPersonAddOnDTO);
		createInsuredPersonAddOnDTO();
	}

	/*************************************************
	 * End AddOn Manage
	 *********************************************************/

	/*************************************************
	 * Start InsuredPerson Manage
	 ******************************************************/
	private Map<String, InsuredPersonInfoDTO> insuredPersonInfoDTOMap;
	private InsuredPersonInfoDTO insuredPersonInfoDTO;
	private Map<String, Product> productMap;
	private List<SelectItem> productSelectItems;
	private String selectedProductId;
	private boolean createNewIsuredPersonInfo;
	private Boolean isEdit = false;
	private Boolean isReplace = false;

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	public void changeProduct(AjaxBehaviorEvent e) {
		Product product = productMap.get(selectedProductId);
		insuredPersonInfoDTO.setProduct(product);
	}

	private void createNewInsuredPersonInfo() {
		createNewIsuredPersonInfo = true;
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		beneficiariesInfoDTO = new BeneficiariesInfoDTO();
		Product product = productList.get(0);
		insuredPersonInfoDTO.setProduct(product);
		insuredPersonInfoDTO.setStartDate(new Date());
		selectedProductId = product.getId();
		isEdit = false;
		isReplace = false;
	}

	public String getSelectedProductId() {
		return selectedProductId;
	}

	public void setSelectedProductId(String selectedProductId) {
		this.selectedProductId = selectedProductId;
	}

	public List<SelectItem> getProductSelectItems() {
		return productSelectItems;
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

	public void prepareEditInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
		this.selectedProductId = insuredPersonInfoDTO.getProduct().getId();
		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (BeneficiariesInfoDTO bdto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
			}
		}
		insuredPersonInfoDTO.setInsuredPersonAddOnDTOList(new ArrayList<>(insuredPersonInfoDTO.getInsuredPersonAddOnDTOMap().values()));
		createNewIsuredPersonInfo = false;
		isEdit = true;
	}

	public void prepareReplaceInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
		this.selectedProductId = insuredPersonInfoDTO.getProduct().getId();
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

	public void upload(ActionEvent event) {
		try {
			List<InsuredPersonInfoDTO> insuredPersonInfoDTOList = new ArrayList<InsuredPersonInfoDTO>();
			InputStream inputStream = uploadedFile.getInputstream();
			CsvReader reader = new CsvReader(inputStream, Charset.forName("UTF-8"));
			reader.readHeaders();
			while (reader.readRecord()) {
				InsuredPersonInfoDTO insuredPersonInfoDTO = new InsuredPersonInfoDTO();
				insuredPersonInfoDTO.setStartDate(new Date());
				Name name = new Name();
				ResidentAddress ra = new ResidentAddress();
				insuredPersonInfoDTOList.add(insuredPersonInfoDTO);
				for (int i = 0; i < reader.getHeaders().length; i++) {
					String headerName[] = reader.getHeaders();
					String data = reader.get(reader.getHeader(i));
					if (headerName[i].trim().equalsIgnoreCase("INITIALID")) {
						insuredPersonInfoDTO.setInitialId(data);
					} else if (headerName[i].trim().equalsIgnoreCase("FIRSTNAME")) {
						name.setFirstName(data);
					} else if (headerName[i].trim().equalsIgnoreCase("MIDDLENAME")) {
						name.setMiddleName(data);
					} else if (headerName[i].trim().equalsIgnoreCase("LASTNAME")) {
						name.setLastName(data);
					} else if (headerName[i].trim().equalsIgnoreCase("FATHERNAME")) {
						insuredPersonInfoDTO.setFatherName(data);
					} else if (headerName[i].trim().equalsIgnoreCase("IDNO")) {
						insuredPersonInfoDTO.setIdNo(data);
					} else if (headerName[i].trim().equalsIgnoreCase("IDTYPE")) {
						insuredPersonInfoDTO.setIdType(IdType.valueOf(data));
					} else if (headerName[i].trim().equalsIgnoreCase("DATEOFBIRTH")) {
						DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
						try {
							Date date = df.parse(data);
							insuredPersonInfoDTO.setDateOfBirth(date);
						} catch (Exception e) {
							FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Invalid date format is occured in Uploaded File!"));
						}
					} else if (headerName[i].trim().equalsIgnoreCase("SUMINSURED")) {
						insuredPersonInfoDTO.setSumInsuredInfo(Double.parseDouble(data));
					} else if (headerName[i].trim().equalsIgnoreCase("RESIDENTADDRESS")) {
						ra.setResidentAddress(data);
					} else if (headerName[i].trim().equalsIgnoreCase("RESIDENTTOWNSHIPID")) {
						Township t = townShipService.findTownshipById(data);
						ra.setTownship(t);
					} else if (headerName[i].trim().equalsIgnoreCase("OCCUPATIONID")) {
						Occupation oc = occupationService.findOccupationById(data);
						insuredPersonInfoDTO.setOccupation(oc);
					} else if (headerName[i].trim().equalsIgnoreCase("GENDER")) {
						insuredPersonInfoDTO.setGender(Gender.valueOf(data));
					} else if (headerName[i].trim().equalsIgnoreCase("PRODUCTID")) {
						Product t = productService.findProductById(data);
						insuredPersonInfoDTO.setProduct(t);
					} else if (headerName[i].trim().equalsIgnoreCase("PERIOD_OF_INSURANCE_YEAR")) {
						insuredPersonInfoDTO.setPeriodOfYears(Integer.parseInt(data));
					}
				}
				insuredPersonInfoDTO.setResidentAddress(ra);
				insuredPersonInfoDTO.setName(name);

				insuredPersonInfoDTO.setStartDate(new Date());
				Calendar cal = Calendar.getInstance();
				cal.setTime(insuredPersonInfoDTO.getStartDate());
				cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
				insuredPersonInfoDTO.setEndDate(cal.getTime());

				for (InsuredPersonKeyFactorValue insKFV : insuredPersonInfoDTO.getKeyFactorValueList()) {
					KeyFactor kf = insKFV.getKeyFactor();
					if (KeyFactorChecker.isSumInsured(kf)) {
						insKFV.setValue(insuredPersonInfoDTO.getSumInsuredInfo() + "");
					} else if (KeyFactorChecker.isTerm(kf)) {
						insKFV.setValue(insuredPersonInfoDTO.getPeriodOfYears() + "");
					} else if (KeyFactorChecker.isAge(kf)) {
						insKFV.setValue(insuredPersonInfoDTO.getAgeForNextYear() + "");
					}
				}
				insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			}
		} catch (IOException e1) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Invalid data is occured in Uploaded File!"));
		}
		FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Successful Customer Information Uploaded!"));
	}

	public void saveInsuredPersonInfo() {
		Product product = productMap.get(selectedProductId);
		insuredPersonInfoDTO.setProduct(product);
		if (lifePolicy != null) {
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
		cal.setTime(insuredPersonInfoDTO.getStartDate());
		cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
		if (validInsuredPerson(insuredPersonInfoDTO)) {
			if (lifePolicy == null) {
				setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears());
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
						setKeyFactorValue(lifePolicy.getInsuredPersonInfo().get(0).getSumInsured(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()),
								lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears());
					} else {
						if (isIncreasedSIAmount(insuredPersonInfoDTO)) {
							setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(),
									lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears());
						} else {
							setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()),
									insuredPersonInfoDTO.getPeriodOfYears());
						}
					}
					insuredPersonInfoDTO.setEndDate(cal.getTime());
				} else {
					if (insuredPersonInfoDTO.getEndorsementStatus() == EndorsementStatus.TERMINATE) {
						setKeyFactorValue(policyInsuPerson.getSumInsured(), getAgeForOldYear(insuredPersonInfoDTO.getDateOfBirth()), policyInsuPerson.getPeriodYears());
					} else {
						setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears());
					}
					insuredPersonInfoDTO.setEndDate(lifePolicy.getInsuredPersonInfo().get(0).getEndDate());
				}
			}
			insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			createNewInsuredPersonInfo();
			createNewBeneficiariesInfoDTOMap();
		}
	}

	private boolean validInsuredPerson(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		boolean valid = true;
		String formID = "lifePolicyEntryForm";
		if (isEmpty(insuredPersonInfoDTO.getInitialId())) {
			addErrorMessage(formID + ":initialId", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(insuredPersonInfoDTO.getName().getFirstName())) {
			addErrorMessage(formID + ":firstName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(insuredPersonInfoDTO.getFatherName())) {
			addErrorMessage(formID + ":fatherName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!insuredPersonInfoDTO.getIdType().equals(IdType.STILL_APPLYING) && isEmpty(insuredPersonInfoDTO.getIdNo())) {
			addErrorMessage(formID + ":idNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(insuredPersonInfoDTO.getResidentAddress().getResidentAddress())) {
			addErrorMessage(formID + ":resident", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (insuredPersonInfoDTO.getResidentAddress().getTownship() == null) {
			addErrorMessage(formID + ":townShip", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (insuredPersonInfoDTO.getGender() == null) {
			addErrorMessage(formID + ":gender", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (insuredPersonInfoDTO.getProduct() == null) {
			addErrorMessage(formID + ":product", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (insuredPersonInfoDTO.getDateOfBirth() == null) {
			addErrorMessage(formID + ":dateOfBirth", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (insuredPersonInfoDTO.getSumInsuredInfo() <= 0) {
			addErrorMessage(formID + ":sumInsuredInfo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (KeyFactorChecker.isPublicLife(insuredPersonInfoDTO.getProduct()) && insuredPersonInfoDTO.getDateOfBirth() != null) {
			if (insuredPersonInfoDTO.getAgeForNextYear() < 10) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MINIMUN_INSURED_PERSON_AGE, 10);
				valid = false;
			} else if (insuredPersonInfoDTO.getPeriodOfYears() < 5) {
				addErrorMessage(formID + ":periodOfInsurance", MessageId.MINIMUN_INSURED_PERIOD, 5);
				valid = false;
			} else if (insuredPersonInfoDTO.getAgeForNextYear() + insuredPersonInfoDTO.getPeriodOfYears() > 65) {
				int availablePeriod = 65 - insuredPersonInfoDTO.getAgeForNextYear();
				addErrorMessage(formID + ":periodOfInsurance", MessageId.MAXIMUM_INSURED_YEARS, availablePeriod < 0 ? 0 : availablePeriod);
				valid = false;
			}
		}
		if (KeyFactorChecker.isGroupLife(insuredPersonInfoDTO.getProduct()) && insuredPersonInfoDTO.getDateOfBirth() != null) {
			if (insuredPersonInfoDTO.getAgeForNextYear() < 10) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MINIMUN_INSURED_PERSON_AGE, 10);
				valid = false;
			} else if (insuredPersonInfoDTO.getAgeForNextYear() > 60) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MAXIMUM_INSURED_PERSON_AGE, 60);
				valid = false;
			} else if (insuredPersonInfoDTO.getPeriodOfYears() != 1) {
				addErrorMessage(formID + ":periodOfInsurance", MessageId.AVAILABLE_INSURED_PERIOD, 1);
				valid = false;
			} else if (insuredPersonInfoDTO.getAgeForNextYear() + insuredPersonInfoDTO.getPeriodOfYears() > 65) {
				int availablePeriod = 60 - insuredPersonInfoDTO.getAgeForNextYear();
				addErrorMessage(formID + ":periodOfInsurance", MessageId.MAXIMUM_INSURED_YEARS, availablePeriod < 0 ? 0 : availablePeriod);
				valid = false;
			}
		}

		// new Product PA
		if (KeyFactorChecker.isPersonalAccident(insuredPersonInfoDTO.getProduct())) {
			if (insuredPersonInfoDTO.getAgeForNextYear() < 16) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MINIMUN_INSURED_PERSON_AGE, 16);
				valid = false;
			}
			if (insuredPersonInfoDTO.getAgeForNextYear() > 65) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MAXIMUM_INSURED_PERSON_AGE, 65);
				valid = false;
			}
			if (insuredPersonInfoDTO.getPeriodOfYears() != 3 && insuredPersonInfoDTO.getPeriodOfYears() != 6 && insuredPersonInfoDTO.getPeriodOfYears() != 12) {
				addErrorMessage(formID + ":periodOfInsurance", MessageId.AVAILABLE_INSURED_PERIOD, "3 or 6 or 12 Months");
				valid = false;
			}

			if (getInsuredPersonInfoDTOList().size() >= 1 && !isEdit) {
				addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", MessageId.PA_MAX_INSURED_PERSON, 1);
				valid = false;
			}

		}

		if (KeyFactorChecker.isPersonalAccidentUSD(insuredPersonInfoDTO.getProduct())) {
			if (insuredPersonInfoDTO.getAgeForNextYear() < 16) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MINIMUN_INSURED_PERSON_AGE, 16);
				valid = false;
			}
			if (insuredPersonInfoDTO.getAgeForNextYear() > 65) {
				addErrorMessage(formID + ":dateOfBirth", MessageId.MAXIMUM_INSURED_PERSON_AGE, 65);
				valid = false;
			}

			if (insuredPersonInfoDTO.getPeriodOfYears() != 3 && insuredPersonInfoDTO.getPeriodOfYears() != 6 && insuredPersonInfoDTO.getPeriodOfYears() != 12) {
				addErrorMessage(formID + ":periodOfInsurance", MessageId.AVAILABLE_INSURED_PERIOD, "3 or 6 or 12 Months");
				valid = false;
			}

			if (getInsuredPersonInfoDTOList().size() >= 1 && !isEdit) {
				addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", MessageId.PA_MAX_INSURED_PERSON, 1);
				valid = false;
			}
		}

		if (insuredPersonInfoDTO.getStartDate() == null) {
			addErrorMessage(formID + ":startDate", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList() != null && insuredPersonInfoDTO.getBeneficiariesInfoDTOList().size() != 0) {
			float totalPercent = 0.0f;
			for (BeneficiariesInfoDTO bfDTO : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				totalPercent = totalPercent + bfDTO.getPercentage();
			}
			if (totalPercent > 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.OVER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
			if (totalPercent < 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.LOWER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
			for (BeneficiariesInfoDTO benfInfoDTO : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				if (!benfInfoDTO.isValidBeneficiaries()) {
					addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.INVALID_BENEFICIARY_PERSON);
					valid = false;
					break;
				}
			}
		} else {
			addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.REQUIRED_BENEFICIARY_PERSON);
			valid = false;

		}
		if (lifePolicy != null && checkPublicLife()) {
			if (getPassedMonths() > insuredPersonInfoDTO.getPeriodOfMonths()) {
				int availablePeriod = getPassedYears();
				if (getPassedMonths() % 12 != 0) {
					availablePeriod = availablePeriod + 1;
				}
				addErrorMessage(formID + ":periodOfInsurance", MessageId.MINIMUN_INSURED_PERIOD, availablePeriod);
				valid = false;
			}
		}
		return valid;
	}

	public void removeInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		if (lifePolicy != null && lifePolicyService.findInsuredPersonByCodeNo(insuredPersonInfoDTO.getInsPersonCodeNo()) != null) {
			insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.TERMINATE);
		} else {
			insuredPersonInfoDTOMap.remove(insuredPersonInfoDTO.getTempId());
		}
		createNewInsuredPersonInfo();
	}

	public String backLifePolicy() {
		createNewInsuredPersonInfo();
		return "lifePolicy";
	}

	/*************************************************
	 * End InsuredPerson Manage
	 ********************************************************/
	/*************************************************
	 * Proposal Manage
	 ********************************************/
	private Map<String, RelationShip> relationShipMap;
	private List<SelectItem> relationShipItemList;
	private boolean organization;
	private boolean beneOrganization = false;

	public boolean isBeneOrganization() {
		return beneOrganization;
	}

	public void setBeneOrganization(boolean beneOrganization) {
		this.beneOrganization = beneOrganization;
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

	public boolean isOrganization() {
		return organization;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (organization) {
			lifePolicy.setCustomer(null);
		} else {
			lifePolicy.setOrganization(null);
		}
	}

	public List<SelectItem> getRelationShipItemList() {
		return relationShipItemList;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public LifePolicy getLifePolicy() {
		if (lifePolicy == null) {
			if (lifePolicy.getCustomer() != null) {
				Customer customer = lifePolicy.getCustomer();
				insuredPersonInfoDTO.setInitialId(customer.getInitialId());
				insuredPersonInfoDTO.setName(customer.getName());
				insuredPersonInfoDTO.setIdNo(customer.getIdNo());
				insuredPersonInfoDTO.setIdType(customer.getIdType());
				insuredPersonInfoDTO.setGender(customer.getGender());
				insuredPersonInfoDTO.setDateOfBirth(customer.getDateOfBirth());
				insuredPersonInfoDTO.setResidentAddress(customer.getResidentAddress());
				insuredPersonInfoDTO.setFatherName(customer.getFatherName());
				insuredPersonInfoDTO.setOccupation(customer.getOccupation());
			}
		}
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public String updateLifePolicy() {
		String result = null;
		try {
			lifePolicy.setPolicyInsuredPersonList(getInsuredPersonInfoList());
			lifePolicyService.overwriteLifePolicy(lifePolicy);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.EDIT_POLICY_PROCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public List<PolicyInsuredPerson> getInsuredPersonInfoList() {
		List<PolicyInsuredPerson> result = new ArrayList<PolicyInsuredPerson>();
		if (insuredPersonInfoDTOMap.values() != null) {
			for (InsuredPersonInfoDTO insuredPersonInfoDTO : insuredPersonInfoDTOMap.values()) {
				Customer customer = customerService.findCustomerByInsuredPerson(insuredPersonInfoDTO.getName(), insuredPersonInfoDTO.getIdNo(),
						insuredPersonInfoDTO.getFatherName(), insuredPersonInfoDTO.getDateOfBirth());
				PolicyInsuredPerson policyInsuredPerson = new PolicyInsuredPerson(insuredPersonInfoDTO);
				policyInsuredPerson.setCustomer(customer);
				policyInsuredPerson.setLifePolicy(lifePolicy);

				result.add(policyInsuredPerson);
			}
		}
		return result;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "lifePolicyEntryForm";
		if ("policyInfo".equals(event.getOldStep())) {
			if (lifePolicy.getCustomer() == null && lifePolicy.getOrganization() == null) {
				addErrorMessage(formID + ":customer", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (lifePolicy.getPaymentType() == null) {
				addErrorMessage(formID + ":paymentType", MessageId.REQUIRED_PAYMENTTYPE);
				valid = false;
			}
			if (lifePolicy.getSaleMan() == null && lifePolicy.getAgent() == null && lifePolicy.getReferral() == null) {
				addErrorMessage(formID + ":userType", MessageId.REQUIRED_SALEMAN_OR_AGENT);
				valid = false;
			}
			if (lifePolicy.getBranch() == null) {
				addErrorMessage(formID + ":branch", MessageId.REQUIRED_BRANCH);
				valid = false;
			}
		}

		if ("InsuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
			if (getInsuredPersonInfoDTOList().isEmpty()) {
				addErrorMessage("lifePolicyEntryForm:insuredPersonInfoDTOTable", MessageId.REQUIRED_DRIVER);
				valid = false;
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public String getPublicLifeId() {
		return ProductIDConfig.getPublicLifeId();
	}

	public Boolean isChangePeriod() {
		if (insuredPersonInfoDTO.getPeriodOfYears() != lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears()) {
			return true;
		}
		return false;
	}

	public Boolean isIncreasedSIAmount(InsuredPersonInfoDTO dto) {
		if (dto.getSumInsuredInfo() <= lifePolicy.getPolicyInsuredPersonList().get(0).getSumInsured()) {
			return false;
		}
		return true;
	}

	public boolean checkPublicLife() {
		Boolean isPublicLife = true;
		String productId = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getId();
		if (productId.equals(getPublicLifeId())) {
			isPublicLife = true;
		} else {
			isPublicLife = false;
		}
		return isPublicLife;
	}

	public int getAgeForOldYear(Date dob) {
		Calendar cal_1 = Calendar.getInstance();
		cal_1.setTime(lifePolicy.getCommenmanceDate());
		int currentYear = cal_1.get(Calendar.YEAR);

		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(dob);
		cal_2.set(Calendar.YEAR, currentYear);
		if (lifePolicy.getCommenmanceDate().after(cal_2.getTime())) {
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

	private void setKeyFactorValue(double sumInsured, int age, int period) {
		for (InsuredPersonKeyFactorValue vehKF : insuredPersonInfoDTO.getKeyFactorValueList()) {
			KeyFactor kf = vehKF.getKeyFactor();
			if (KeyFactorChecker.isSumInsured(kf)) {
				vehKF.setValue(sumInsured + "");
			} else if (KeyFactorChecker.isAge(kf)) {
				vehKF.setValue(age + "");
			} else if (KeyFactorChecker.isTerm(kf)) {
				vehKF.setValue(period + "");
			}
		}
	}

	public boolean getIsEndorse() {
		return (EndorsementUtil.isEndorsementProposal(lifePolicy));
	}

	private boolean validBeneficiary(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		boolean valid = true;
		String formID = "beneficiaryInfoEntryForm";
		if (isEmpty(beneficiariesInfoDTO.getName().getFirstName())) {
			addErrorMessage(formID + ":firstName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!beneficiariesInfoDTO.getIdType().equals(IdType.STILL_APPLYING) && isEmpty(beneficiariesInfoDTO.getIdNo())) {
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

	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
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

	public int getPassedMonths() {
		DateTime vDate = new DateTime(lifePolicy.getActivedPolicyEndDate());
		DateTime cDate = new DateTime(lifePolicy.getCommenmanceDate());
		int paymentType = lifePolicy.getPaymentType().getMonth();
		int passedMonths = Months.monthsBetween(cDate, vDate).getMonths();
		if (paymentType != 0) {
			if (passedMonths % paymentType != 0) {
				passedMonths = passedMonths + 1;
			}
		}
		return passedMonths;
	}

	public int getPassedYears() {
		return getPassedMonths() / 12;
	}

	public void showPaidUpDialog() {
		if (lifePolicy != null && isEdit == true && isDecreasedSIAmount() && checkPublicLife()) {
			PrimeFaces.current().executeScript("paidPremiumConfirmationDialog').show()");
		} else {
			saveInsuredPersonInfo();
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

	public String getUserType() {
		if (lifePolicy.getAgent() != null) {
			userType = UserType.AGENT.toString();
		} else if (lifePolicy.getSaleMan() != null) {
			userType = UserType.SALEMAN.toString();
		} else if (lifePolicy.getReferral() != null) {
			userType = UserType.REFERRAL.toString();
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isAgent() {
		return isAgent;
	}

	public void setAgent(boolean isAgent) {
		this.isAgent = isAgent;
	}

	/*
	 * public void changeSaleEvent(AjaxBehaviorEvent event) { if
	 * (userType.equals(UserType.AGENT.toString())) {
	 * lifePolicy.setSaleMan(null); lifePolicy.setReferral(null); } else if
	 * (userType.equals(UserType.SALEMAN.toString())) {
	 * lifePolicy.setAgent(null); lifePolicy.setReferral(null); } else if
	 * (userType.equals(UserType.REFERRAL.toString())) {
	 * lifePolicy.setSaleMan(null); lifePolicy.setAgent(null); } }
	 */

	public SaleChannelType[] getSaleChannelType() {
		return SaleChannelType.values();
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (lifePolicy.getSaleChannelType().equals(SaleChannelType.AGENT)) {
			lifePolicy.setSaleMan(null);
			lifePolicy.setReferral(null);
		} else if (lifePolicy.getSaleChannelType().equals(SaleChannelType.SALEMAN)) {
			lifePolicy.setAgent(null);
			lifePolicy.setReferral(null);
		} else if (lifePolicy.getSaleChannelType().equals(SaleChannelType.REFERRAL)) {
			lifePolicy.setSaleMan(null);
			lifePolicy.setAgent(null);
		} else if (lifePolicy.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
			lifePolicy.setSaleMan(null);
			lifePolicy.setAgent(null);
			lifePolicy.setReferral(null);
			bancaassurancePolicy = new BancaassurancePolicy();
		}
	}

	public void changeBancaEvent(AjaxBehaviorEvent event) {

		bancaassurancePolicy.setBancaBRM(null);
		bancaassurancePolicy.setBancaLIC(null);
		bancaassurancePolicy.setBancaRefferal(null);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		lifePolicy.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		lifePolicy.setOrganization(organization);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		lifePolicy.setPaymentType(paymentType);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		lifePolicy.setBranch(branch);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		lifePolicy.setAgent(agent);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		lifePolicy.setSaleMan(saleMan);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		lifePolicy.setReferral(referral);
	}

	public void returnTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonInfoDTO.setOccupation(occupation);
	}

	public void selectAddOn() {
		selectAddOn(insuredPersonInfoDTO.getProduct());
	}

	public void selectTownShip() {
		selectTownship();
	}

	public void returnInsuPersonTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		insuredPersonInfoDTO.getResidentAddress().setTownship(township);
	}

	public Boolean getIsEdit() {
		return isEdit;
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

	public SalutationType[] getSalutationTypes() {
		return SalutationType.values();
	}

	public void createNewInsurePersonAddOnDTO() {
		insuredPersonAddOnDTO = new InsuredPersonAddOnDTO();
	}

	public boolean getIsPublicTermLife() {
		return isPublicTermLife;
	}

	public boolean getIsShortTermSinglePremiumCreditLife() {
		return isShortTermSinglePremiumCreditLife;
	}

	public boolean getIsSinglePremiumEndowmentLife() {
		return isSinglePremiumEndowmentLife;
	}

	public boolean getIsSinglePremiumCreditLife() {
		return isSinglePremiumCreditLife;
	}

	public boolean isPublicTermLife() {
		return isPublicTermLife;
	}

	public void setPublicTermLife(boolean isPublicTermLife) {
		this.isPublicTermLife = isPublicTermLife;
	}

	public boolean isShortTermSinglePremiumCreditLife() {
		return isShortTermSinglePremiumCreditLife;
	}

	public boolean isSinglePremiumCreditLife() {
		return isSinglePremiumCreditLife;
	}

	public boolean isSinglePremiumEndowmentLife() {
		return isSinglePremiumEndowmentLife;
	}

}
