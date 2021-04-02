package org.ace.insurance.web.manage.life.proposal;

import java.io.Serializable;
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
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.PeriodType;
import org.ace.insurance.web.common.UserType;
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
@ManagedBean(name = "EditRenewalGroupLifeProposalEnquiryActionBean")
public class EditRenewalGroupLifeProposalEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RenewalGroupLifeProposalService}")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	public void setRenewalGroupLifeProposalService(IRenewalGroupLifeProposalService renewalGroupLifeProposalService) {
		this.renewalGroupLifeProposalService = renewalGroupLifeProposalService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{OccupationService}")
	private IOccupationService occupationService;

	public void setOccupationService(IOccupationService occupationService) {
		this.occupationService = occupationService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
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

	private User user;
	private LifeProposal lifeProposal;
	private LifePolicy lifePolicy;
	private String userType;
	private boolean createNew;
	private Product product;
	private List<Product> productList;
	private Entitys entitys;

	public PeriodType[] getPeriodType() {
		return new PeriodType[] { PeriodType.MONTH, PeriodType.YEAR };
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		entitys = lifeProposal.getBranch().getEntity();
		if (lifeProposal.getCustomer() == null) {
			organization = true;
		}
		insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
		// createNewBeneficiariesInfoDTOMap();

		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			InsuredPersonInfoDTO vhDTO = new InsuredPersonInfoDTO(pv);
			insuredPersonInfoDTOMap.put(vhDTO.getTempId(), vhDTO);
		}

		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();

		organization = lifeProposal.getCustomer() == null ? true : false;
		lifePolicy = lifeProposal.getLifePolicy();
		productList = productService.findProductByInsuranceType(InsuranceType.LIFE);
		this.product = productList.stream().filter(p -> p.getName().equalsIgnoreCase("GROUP LIFE")).findAny().get();
		System.out.println(product.getName());

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

	public List<RelationShip> getRelationShipList() {
		return relationShipService.findAllRelationShip();
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

	public void prepareEditBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
		this.createNewBeneficiariesInfo = false;
		if (beneficiariesInfoDTO.getRelationship() != null) {
			this.beneficiaryRelationShipId = beneficiariesInfoDTO.getRelationship().getName();
		} else {
			this.beneficiaryRelationShipId = null;
		}
	}

	public void saveBeneficiariesInfo() {
		if (validBeneficiary(beneficiariesInfoDTO)) {
			beneficiaryRelationShipId = null;
			beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
			insuredPersonInfoDTO.setBeneficiariesInfoDTOList(new ArrayList<BeneficiariesInfoDTO>(beneficiariesInfoDTOMap.values()));
			createNewBeneficiariesInfo();
			PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
		}
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

	public boolean isCreateNewAddOn() {
		return createNewAddOn;
	}

	private void createInsuredPersonAddOnDTO() {
		createNewAddOn = true;
		insuredPersonAddOnDTO = new InsuredPersonAddOnDTO();
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
	private boolean createNewIsuredPersonInfo;
	private Boolean isEdit = false;
	private Boolean isReplace = false;

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	public void changeProduct(AjaxBehaviorEvent e) {
		insuredPersonInfoDTO.setProduct(product);
	}

	private void createNewInsuredPersonInfo() {
		createNewIsuredPersonInfo = true;
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		beneficiariesInfoDTO = new BeneficiariesInfoDTO();
		insuredPersonInfoDTO.setStartDate(new Date());
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
		for (InsuredPersonInfoDTO dto : insuredPersonInfoDTOMap.values()) {
			insuDTOList.add(dto);
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
		this.product = insuredPersonInfoDTO.getProduct();
		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (BeneficiariesInfoDTO bdto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
			}

		}

		createNewIsuredPersonInfo = false;
		isEdit = true;
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
		insuredPersonInfoDTO.setProduct(product);
		if (lifeProposal.getLifePolicy() != null) {
			PolicyInsuredPerson policyInsuPerson = lifePolicyService.findInsuredPersonByCodeNo(insuredPersonInfoDTO.getInsPersonCodeNo());
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(insuredPersonInfoDTO.getStartDate());
		cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
		if (validInsuredPerson(insuredPersonInfoDTO)) {
			if (lifeProposal.getLifePolicy() == null) {
				setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears());
				insuredPersonInfoDTO.setEndDate(cal.getTime());
			} else {
				PolicyInsuredPerson policyInsuPerson = lifePolicyService.findInsuredPersonByCodeNo(insuredPersonInfoDTO.getInsPersonCodeNo());
				// Set Key Factor For Public Life
				setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears());
				insuredPersonInfoDTO.setEndDate(lifeProposal.getLifePolicy().getInsuredPersonInfo().get(0).getEndDate());
			}
			insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
		}

		InsuredPersonInfoDTO tempDTO = new InsuredPersonInfoDTO();
		for (InsuredPersonInfoDTO in : insuredPersonInfoDTOMap.values()) {
			tempDTO = in;
		}

		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
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
		if (!insuredPersonInfoDTO.getIdType().equals(IdType.STILL_APPLYING) && isEmpty(insuredPersonInfoDTO.getFullIdNo())) {
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

		return valid;
	}

	public void removeInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		insuredPersonInfoDTOMap.remove(insuredPersonInfoDTO.getTempId());
		createNewInsuredPersonInfo();
	}

	public String backLifeProposal() {
		createNewInsuredPersonInfo();
		return "renewalGroupLifeProposal";
	}

	/*************************************************
	 * End InsuredPerson Manage
	 ********************************************************/
	/*************************************************
	 * Proposal Manage
	 ********************************************/
	private boolean organization;

	public boolean isOrganization() {
		return organization;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (organization) {
			lifeProposal.setCustomer(null);
		} else {
			lifeProposal.setOrganization(null);
		}
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			lifeProposal.setSaleMan(null);
			lifeProposal.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			lifeProposal.setAgent(null);
			lifeProposal.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			lifeProposal.setSaleMan(null);
			lifeProposal.setAgent(null);
		}
	}

	public List<Product> getProductList() {
		return productList;
	}

	/*************************************************
	 * Proposal Manage
	 **********************************************/

	public boolean isCreateNew() {
		return createNew;
	}

	public LifeProposal getLifeProposal() {

		/* Populate data into InsuredPersonInfoDTO when customer is selected. */
		/*
		 * if (lifeProposal.getLifePolicy() == null) { if
		 * (lifeProposal.getCustomer() != null) { Customer customer =
		 * lifeProposal.getCustomer();
		 * insuredPersonInfoDTO.setInitialId(customer.getInitialId());
		 * insuredPersonInfoDTO.setName(customer.getName());
		 * insuredPersonInfoDTO.setIdNo(customer.getIdNo());
		 * insuredPersonInfoDTO.setIdType(customer.getIdType());
		 * insuredPersonInfoDTO.setGender(customer.getGender());
		 * insuredPersonInfoDTO.setDateOfBirth(customer.getDateOfBirth());
		 * insuredPersonInfoDTO
		 * .setResidentAddress(customer.getResidentAddress());
		 * insuredPersonInfoDTO.setFatherName(customer.getFatherName());
		 * insuredPersonInfoDTO.setOccupation(customer.getOccupation());
		 * insuredPersonInfoDTO
		 * .setSumInsuredInfo(lifeProposal.getProposalInsuredPersonList
		 * ().get(0).getProposedSumInsured());
		 * insuredPersonInfoDTO.setPeriodOfYears
		 * (lifeProposal.getProposalInsuredPersonList
		 * ().get(0).getPeriodYears()); } }
		 */
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public void selectSurveyUser() {
		selectUser(WorkflowTask.RENEWAL_SURVEY, WorkFlowType.LIFE);
	}

	public void selectApprovalUser() {
		selectUser(WorkflowTask.RENEWAL_APPROVAL, WorkFlowType.LIFE);
	}

	public String updateLifeProposal() {
		String result = null;
		try {
			lifeProposal.setInsuredPersonList(getInsuredPersonInfoList());

			renewalGroupLifeProposalService.updateRenewalGroupLifeProposalEnquiry(lifeProposal);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public List<ProposalInsuredPerson> getInsuredPersonInfoList() {
		String insPersonCodeNo = null;
		String inPersonGroupCodeNo = null;
		List<ProposalInsuredPerson> result = new ArrayList<ProposalInsuredPerson>();
		if (insuredPersonInfoDTOMap.values() != null) {
			for (InsuredPersonInfoDTO insuredPersonInfoDTO : insuredPersonInfoDTOMap.values()) {
				ClassificationOfHealth classificationOfHealth = insuredPersonInfoDTO.getClassificationOfHealth();
				Customer customer = customerService.findCustomerByInsuredPerson(insuredPersonInfoDTO.getName(), insuredPersonInfoDTO.getFullIdNo(),
						insuredPersonInfoDTO.getFatherName(), insuredPersonInfoDTO.getDateOfBirth());
				insuredPersonInfoDTO.setCustomer(customer);
				insuredPersonInfoDTO.setAge(insuredPersonInfoDTO.getAgeForNextYear());
				ProposalInsuredPerson proposalInsuredPerson = new ProposalInsuredPerson(insuredPersonInfoDTO, lifeProposal);
				proposalInsuredPerson.setInsuredPersonAddOnList(insuredPersonInfoDTO.getInsuredPersonAddOnList(proposalInsuredPerson));
				proposalInsuredPerson.setClsOfHealth(classificationOfHealth);
				for (InsuredPersonAttachment attach : insuredPersonInfoDTO.getPerAttachmentList()) {
					proposalInsuredPerson.addAttachment(attach);
				}
				proposalInsuredPerson.setInsuredPersonBeneficiariesList(insuredPersonInfoDTO.getBeneficiariesInfoList(proposalInsuredPerson));
				proposalInsuredPerson.setKeyFactorValueList(insuredPersonInfoDTO.getKeyFactorValueList(proposalInsuredPerson));
				proposalInsuredPerson.setTypesOfSport(insuredPersonInfoDTO.getTypesOfSport());
				if (insuredPersonInfoDTO.isExistsEntity()) {
					proposalInsuredPerson.setId(insuredPersonInfoDTO.getTempId());
					proposalInsuredPerson.setVersion(insuredPersonInfoDTO.getVersion());
				}

				result.add(proposalInsuredPerson);
			}
		}
		return result;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "lifeProposalEntryForm";
		if ("proposalInfo".equals(event.getOldStep())) {
			if (lifeProposal.getSubmittedDate() == null) {
				addErrorMessage(formID + ":submittedDate", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (lifeProposal.getCustomer() == null && lifeProposal.getOrganization() == null) {
				addErrorMessage(formID + ":customer", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (isEmpty(lifeProposal.getSalePersonName())) {
				addErrorMessage(formID + ":userType", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (lifeProposal.getPaymentType() == null) {
				addErrorMessage(formID + ":paymentType", MessageId.REQUIRED_PAYMENTTYPE);
				valid = false;
			}
			if (lifeProposal.getBranch() == null) {
				addErrorMessage(formID + ":branch", MessageId.REQUIRED_BRANCH);
				valid = false;
			}
		}

		if ("InsuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
			if (getInsuredPersonInfoDTOList().isEmpty()) {
				addErrorMessage("lifeProposalEntryForm:insuredPersonInfoDTOTable", MessageId.REQUIRED_DRIVER);
				valid = false;
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
		if (lifeProposal.getLifePolicy().getCommenmanceDate().after(cal_2.getTime())) {
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

	/*
	 * public boolean isCustomer() { if(lifeProposal.getCustomer() != null) {
	 * return true; } else { return false; } }
	 */

	// public boolean getIsEndorse() {
	// return
	// (EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy()));
	// }

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
		DateTime vDate = new DateTime(lifeProposal.getLifePolicy().getActivedPolicyEndDate());
		DateTime cDate = new DateTime(lifeProposal.getLifePolicy().getCommenmanceDate());
		int paymentType = lifeProposal.getLifePolicy().getPaymentType().getMonth();
		int passedMonths = Months.monthsBetween(cDate, vDate).getMonths();
		if (passedMonths % paymentType != 0) {
			passedMonths = passedMonths + 1;
		}
		return passedMonths;
	}

	public int getPassedYears() {
		return getPassedMonths() / 12;
	}

	public void showPaidUpDialog() {
		if (lifeProposal.getLifePolicy() != null && isEdit == true && isDecreasedSIAmount() && checkPublicLife()) {
			PrimeFaces.current().executeScript("PF('paidPremiumConfirmationDialog').show()");
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

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		lifeProposal.setCustomer(customer);
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

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		lifeProposal.setBranch(branch);
		lifeProposal.setSalePoint(null);
	}

	public void returnTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnInsuTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		insuredPersonInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonInfoDTO.setOccupation(occupation);
	}

	public void returnAddOn(SelectEvent event) {
		AddOn addOn = (AddOn) event.getObject();
		insuredPersonAddOnDTO.setAddOn(addOn);
	}

	public void selectAddOn() {
		selectAddOn(product);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(lifeProposal.getBranch() == null ? "" : lifeProposal.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		lifeProposal.setSalePoint(salePoint);
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (this.entitys != null && !entity.getId().equals(this.entitys.getId())) {
			lifeProposal.setBranch(null);
			lifeProposal.setSalePoint(null);
		}
		this.entitys = entity;
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
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

}
