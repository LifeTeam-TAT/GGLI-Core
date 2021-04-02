package org.ace.insurance.web.manage.life.groupfarmer.proposal;

import java.util.ArrayList;
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

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.PassportType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.Utils;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.persistence.interfaces.ILifeProposalDAO;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.web.manage.life.proposal.BeneficiariesInfoDTO;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "EditFarmerProposalActionBean")
@ViewScoped
public class EditFarmerProposalActionBean extends BaseBean {

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
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

	@ManagedProperty(value = "#{LifeProposalDAO}")
	private ILifeProposalDAO lifeProposalDAO;

	public void setLifeProposalDAO(ILifeProposalDAO lifeProposalDAO) {
		this.lifeProposalDAO = lifeProposalDAO;
	}

	@ManagedProperty(value = "#{CustomerService}")
	private ICustomerService customerService;

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
	}

	private boolean isOrganization;
	private String insuredPersonFullName;
	private boolean createNewBeneficiariesInfo;
	private List<RelationShip> relationshipList;
	private boolean createEditBeneficiariesInfo;
	private boolean createNewInsuredPersonInfo;
	private Product product;
	private InsuredPersonInfoDTO proposalInsuredPerson;
	private Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap;
	private Map<String, InsuredPersonInfoDTO> insuredPersonDTOMap;
	private BeneficiariesInfoDTO proposalInsuredPersonBeneficiaries;
	private GroupFarmerProposal groupFarmerProposal;
	private LifeProposal lifeProposal;
	public double sumInsured;
	private boolean createEdit;
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();
	private List<TownshipCode> benTownshipCodeList = new ArrayList<TownshipCode>();
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();

	@PostConstruct
	public void init() {
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
		relationshipList = relationShipService.findAllRelationShip();
		insuredPersonDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			InsuredPersonInfoDTO vhDTO = new InsuredPersonInfoDTO(pv);
			insuredPersonDTOMap.put(vhDTO.getTempId(), vhDTO);
		}
		createNewGroupFarmerInsuredPerson();
		loadProduct();
		stateCodeList = stateCodeService.findAllStateCode();
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	public void loadProduct() {
		product = productService.findProductById(ProductIDConfig.getFarmerId());
	}

	public void changeStateCode() {
		townshipCodeList = townshipCodeService.findByStateCodeNo(proposalInsuredPerson.getProvinceCode());
	}

	public void changeBeneStateCode() {
		benTownshipCodeList = townshipCodeService.findByStateCodeNo(proposalInsuredPersonBeneficiaries.getProvinceCode());
	}

	public void saveGroupFarmerInsuredPerson() {
		double premium = 0.0;
		premium = calcuateInsPersonPremium(proposalInsuredPerson);
		proposalInsuredPerson.setFullIdNo();
		proposalInsuredPerson.setPremium(premium);
		proposalInsuredPerson.setBasicTermPremium(premium);
		proposalInsuredPerson.setProduct(product);
		proposalInsuredPerson.setPeriodOfYears(1);
		proposalInsuredPerson.setBeneficiariesInfoDTOList(getProposalInsuredPersonBeneficiariesList());
		if (valideteInsurePerson() && checkBenePercentage()) {
			Calendar cal = Calendar.getInstance();
			if (proposalInsuredPerson.getStartDate() == null) {
				proposalInsuredPerson.setStartDate(new Date());
			}
			cal.setTime(proposalInsuredPerson.getStartDate());
			cal.add(Calendar.YEAR, proposalInsuredPerson.getPeriodOfYears());
			proposalInsuredPerson.setAge(Utils.getAgeForNextYear(proposalInsuredPerson.getDateOfBirth()));
			proposalInsuredPerson.setEndDate(cal.getTime());
			insuredPersonDTOMap.put(proposalInsuredPerson.getTempId(), proposalInsuredPerson);
			createNewGroupFarmerInsuredPerson();
		}
		createEdit = false;
	}

	public void prepareEditInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfodto) {
		insuredPersonInfodto.loadFullIdNo();
		this.proposalInsuredPerson = new InsuredPersonInfoDTO(insuredPersonInfodto);
		this.product = proposalInsuredPerson.getProduct();
		if (proposalInsuredPerson.getBeneficiariesInfoDTOList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (BeneficiariesInfoDTO bene : proposalInsuredPerson.getBeneficiariesInfoDTOList()) {
				beneficiariesInfoDTOMap.put(bene.getTempId(), bene);
			}
		}
		changeStateCode();
		this.createNewInsuredPersonInfo = false;
		createEdit = true;
		PrimeFaces.current().resetInputs(":groupFarmerInsForm:insuredPersonInfoWizardPanel");
	}

	public void removeInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDto) {
		insuredPersonDTOMap.remove(insuredPersonInfoDto.getTempId());
		createNewGroupFarmerInsuredPerson();
	}

	public double calcuateInsPersonPremium(InsuredPersonInfoDTO insuredPerson) {
		double insPremium = 0.0;
		double sumInsured = 0.0;
		sumInsured = insuredPerson.getSumInsuredInfo();
		insPremium = (sumInsured / 100) * 1;
		return insPremium;

	}

	public String updateFarmerProposal() {
		String result = null;
		if (validate()) {
			lifeProposal.setInsuredPersonList(getInsuredPersonInfoList());
			lifeProposalDAO.update(lifeProposal);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UPDATE_PROCESS_SUCCESS);
			result = "groupFarmerPolicyListForm";
		}
		return result;

	}

	public void prepareEditBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfo) {
		beneficiariesInfo.loadFullIdNo();
		this.proposalInsuredPersonBeneficiaries = new BeneficiariesInfoDTO(beneficiariesInfo);
		proposalInsuredPersonBeneficiaries.setExistEntity(false);
		changeBeneStateCode();
		this.createNewBeneficiariesInfo = false;

	}

	public void removeBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfo) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfo.getTempId());
		createNewBeneficiariesInfo();
	}

	public boolean valideteInsurePerson() {
		String formID = "groupFarmerInsForm";
		boolean result = true;
		if (proposalInsuredPerson.getBeneficiariesInfoDTOList().size() < 1) {
			addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.REQUIRE_BENEFICIARY);
			result = false;
		}
		if (proposalInsuredPerson.getSumInsuredInfo() == 0.0) {
			addErrorMessage(formID + ":sumInsuredInfo", MessageId.MUST_NOT_EQUAL_ZERO);
			result = false;
		}
		if (proposalInsuredPerson.getAgeForNextYear() < 16 || proposalInsuredPerson.getAgeForNextYear() > 60) {
			addErrorMessage(formID + ":dateOfBirth", MessageId.INSURED_PERSON_AGE_MUST_BE_BETWEEN, 16, 60);
			result = false;
		}
		return result;
	}

	public boolean validate() {
		boolean result = true;
		String formID = "groupFarmerInsForm";

		if (getInsuredPersonDTOList().size() == 0 || getInsuredPersonDTOList().isEmpty()) {
			addErrorMessage(formID + ":proposalInsuredPersonTable", MessageId.INSURED_PERSON_REQUIRED, groupFarmerProposal.getNoOfInsuredPerson());
			result = false;
		}

		if (getInsuredPersonDTOList().size() > 1) {
			addErrorMessage(formID + ":proposalInsuredPersonTable", MessageId.INVALID_INSURED_PERSON, product.getName());
			result = false;
		}
		return result;

	}

	public List<InsuredPersonInfoDTO> getInsuredPersonDTOList() {
		return new ArrayList<InsuredPersonInfoDTO>(insuredPersonDTOMap.values());
	}

	public double totalInsuredPesronSI() {
		double totalSI = 0.0;
		for (ProposalInsuredPerson insPerson : getInsuredPersonInfoList()) {
			totalSI += insPerson.getProposedSumInsured();
		}
		return totalSI;
	}

	public void saveBeneficiariesInfo() {
		if (validBeneficiary(proposalInsuredPersonBeneficiaries)) {
			proposalInsuredPersonBeneficiaries.setFullIdNo();
			beneficiariesInfoDTOMap.put(proposalInsuredPersonBeneficiaries.getTempId(), proposalInsuredPersonBeneficiaries);
			createNewBeneficiariesInfo();
			hideBeneficiaryDialog();
		}

	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (isOrganization) {
			lifeProposal.setCustomer(null);
		} else {
			lifeProposal.setOrganization(null);
		}
	}

	private boolean validBeneficiary(BeneficiariesInfoDTO beneficiariesInfo) {
		String formID = "beneficiaryInfoEntryForm";
		boolean result = true;
		if (beneficiariesInfo.getPercentage() == 0.0) {
			addErrorMessage(formID + ":percentage", MessageId.BENEFICIARY_PERCENTAGE);
			result = false;
		} else if (beneficiariesInfo.getPercentage() < 1 || beneficiariesInfo.getPercentage() > 100) {
			result = false;
			addErrorMessage(formID + ":percentage", MessageId.BENEFICIARY_PERCENTAGE);
		}
		return result;
	}

	private boolean checkBenePercentage() {
		boolean result = true;
		if (getProposalInsuredPersonBeneficiariesList() != null && getProposalInsuredPersonBeneficiariesList().size() != 0) {
			float totalPercent = 0.0f;
			for (BeneficiariesInfoDTO bene : getProposalInsuredPersonBeneficiariesList()) {
				totalPercent = totalPercent + bene.getPercentage();
			}
			if (totalPercent > 100 || totalPercent < 100) {
				result = false;
				addErrorMessage("groupFarmerInsForm:beneficiariesInfoTablePanel", MessageId.BENEFICIARY_PERCENTAGE_MUST_BE_100);
			} else {
				result = true;
			}
		} else {
			result = false;
			addErrorMessage("groupFarmerInsForm:beneficiariesInfoTablePanel", MessageId.REQUIRED_BENEFICIARY_PERSON);
		}
		return result;
	}

	public void hideBeneficiaryDialog() {
		PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
	}

	private void createNewGroupFarmerInsuredPerson() {
		createNewInsuredPersonInfo = true;
		proposalInsuredPerson = new InsuredPersonInfoDTO();
		proposalInsuredPerson.setStartDate(new Date());
		proposalInsuredPerson.setIdType(IdType.NRCNO);
		insuredPersonFullName = "";
		createNewBeneficiariesInfo();
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
	}

	public void prepareAddNewInsuredPersonInfo() {
		createNewGroupFarmerInsuredPerson();
		createNewBeneficiariesInfoDTOMap();
	}

	private void fillInsuredPersonInfoDTO() {
		Customer customer = proposalInsuredPerson.getCustomer();
		proposalInsuredPerson.setInitialId(customer.getInitialId());
		proposalInsuredPerson.setName(customer.getName());
		proposalInsuredPerson.setFatherName(customer.getFatherName());
		proposalInsuredPerson.setIdType(customer.getIdType());
		proposalInsuredPerson.setIdNo(customer.getIdNo());
		proposalInsuredPerson.setFullIdNo(customer.getFullIdNo());
		proposalInsuredPerson.loadFullIdNo();
		changeStateCode();
		proposalInsuredPerson.setDateOfBirth(customer.getDateOfBirth());
		proposalInsuredPerson.setResidentAddress(customer.getResidentAddress());
		proposalInsuredPerson.setOccupation(customer.getOccupation());
		proposalInsuredPerson.setGender(customer.getGender());
	}

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
	}

	private void createNewBeneficiariesInfo() {
		createNewBeneficiariesInfo = true;
		proposalInsuredPersonBeneficiaries = new BeneficiariesInfoDTO();
		proposalInsuredPersonBeneficiaries.setIdType(IdType.NRCNO);
	}

	public void prepareAddNewBeneficiariesInfo() {
		createNewBeneficiariesInfo();
		createEditBeneficiariesInfo = false;
	}

	public void returnInsuredPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		proposalInsuredPerson.getResidentAddress().setTownship(townShip);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		proposalInsuredPerson.setOccupation(occupation);
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		proposalInsuredPersonBeneficiaries.getResidentAddress().setTownship(townShip);
	}

	public String getInsuredPersonFullName() {
		return insuredPersonFullName;
	}

	public void setInsuredPersonFullName(String insuredPersonFullName) {
		this.insuredPersonFullName = insuredPersonFullName;
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	public void setCreateNewBeneficiariesInfo(boolean createNewBeneficiariesInfo) {
		this.createNewBeneficiariesInfo = createNewBeneficiariesInfo;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	public void setRelationshipList(List<RelationShip> relationshipList) {
		this.relationshipList = relationshipList;
	}

	public boolean isCreateEditBeneficiariesInfo() {
		return createEditBeneficiariesInfo;
	}

	public void setCreateEditBeneficiariesInfo(boolean createEditBeneficiariesInfo) {
		this.createEditBeneficiariesInfo = createEditBeneficiariesInfo;
	}

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewInsuredPersonInfo;
	}

	public void setCreateNewInsuredPersonInfo(boolean createNewInsuredPersonInfo) {
		this.createNewInsuredPersonInfo = createNewInsuredPersonInfo;
	}

	public boolean isCreateEdit() {
		return createEdit;
	}

	public void setCreateEdit(boolean createEdit) {
		this.createEdit = createEdit;
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
	}

	public void setGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		this.groupFarmerProposal = groupFarmerProposal;
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

	public InsuredPersonInfoDTO getProposalInsuredPerson() {
		return proposalInsuredPerson;
	}

	public BeneficiariesInfoDTO getProposalInsuredPersonBeneficiaries() {
		return proposalInsuredPersonBeneficiaries;
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		proposalInsuredPerson.setCustomer(customer);
		fillInsuredPersonInfoDTO();
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		lifeProposal.setOrganization(organization);
	}

	public boolean isOrganization() {
		return isOrganization;
	}

	public void setOrganization(boolean isOrganization) {
		this.isOrganization = isOrganization;
	}

	public Map<String, BeneficiariesInfoDTO> getBeneficiariesInfoDTOMap() {
		return beneficiariesInfoDTOMap;
	}

	public List<BeneficiariesInfoDTO> getProposalInsuredPersonBeneficiariesList() {
		return new ArrayList<BeneficiariesInfoDTO>(beneficiariesInfoDTOMap.values());
	}

	public List<ProposalInsuredPerson> getInsuredPersonInfoList() {
		List<ProposalInsuredPerson> result = new ArrayList<ProposalInsuredPerson>();
		if (insuredPersonDTOMap.values() != null) {
			for (InsuredPersonInfoDTO insuredPersonInfoDTO : insuredPersonDTOMap.values()) {
				for (BeneficiariesInfoDTO dto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
					dto.setExistEntity(false);
				}
				ProposalInsuredPerson proposalInsuredPerson = new ProposalInsuredPerson(insuredPersonInfoDTO);
				proposalInsuredPerson.setLifeProposal(lifeProposal);
				result.add(proposalInsuredPerson);

			}

		}
		return result;
	}

	public void changeInsuredPersonIdType() {
		checkIdType("I");
	}

	public void changeBeneIdType() {
		checkIdType("B");
	}

	public void checkIdType(String customerType) {
		if ("I".equals(customerType)) {
			proposalInsuredPerson.setProvinceCode(null);
			proposalInsuredPerson.setTownshipCode(null);
			proposalInsuredPerson.setIdConditionType(null);
			proposalInsuredPerson.setIdNo(null);
			proposalInsuredPerson.setFullIdNo(null);

		} else if ("B".equals(customerType)) {
			proposalInsuredPersonBeneficiaries.setProvinceCode(null);
			proposalInsuredPersonBeneficiaries.setTownshipCode(null);
			proposalInsuredPersonBeneficiaries.setIdConditionType(null);
			proposalInsuredPersonBeneficiaries.setIdNo("");
			proposalInsuredPersonBeneficiaries.setFullIdNo(null);
		}
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public List<TownshipCode> getBenTownshipCodeList() {
		return benTownshipCodeList;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public void calculateAgeForBene() {
		proposalInsuredPersonBeneficiaries.setAge(getAgeForBeneNextYear());
	}

	public int getAgeForBeneNextYear() {
		Calendar cal_1 = Calendar.getInstance();
		int currentYear = cal_1.get(Calendar.YEAR);
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(proposalInsuredPersonBeneficiaries.getDateOfBirth());
		cal_2.set(Calendar.YEAR, currentYear);

		if (new Date().after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(proposalInsuredPersonBeneficiaries.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(proposalInsuredPersonBeneficiaries.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}

}
