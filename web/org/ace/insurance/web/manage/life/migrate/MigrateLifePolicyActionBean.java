package org.ace.insurance.web.manage.life.migrate;

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
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.LifeEndowmentPolicySearch;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.service.interfaces.IPaymentTypeService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.manage.life.proposal.BeneficiariesInfoDTO;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MigrateLifePolicyActionBean")
public class MigrateLifePolicyActionBean extends BaseBean {

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

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

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

	@ManagedProperty(value = "#{CustomerService}")
	private ICustomerService customerService;

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private LifeProposal lifeProposal;
	private LifePolicy lifePolicy;
	private Product product;
	private User responsiblePerson;
	private User user;

	private boolean createNew;
	private boolean organization;
	private boolean createNewIsuredPersonInfo;
	private boolean createNewBeneficiariesInfo;
	private boolean isEdit;

	private String userType;
	private String remark;

	private BeneficiariesInfoDTO beneficiariesInfoDTO;
	private Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap;
	private List<RelationShip> relationshipList;
	private Map<String, InsuredPersonInfoDTO> insuredPersonInfoDTOMap;
	private InsuredPersonInfoDTO insuredPersonInfoDTO;
	private LifeEndowmentPolicySearch searchPolicy;
	private Entitys entitys;

	@PreDestroy
	public void destroy() {
		removeParam("lifePolicy");
		removeParam("InsuranceType");
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		if (isExistParam("lifeProposal")) {
			lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
			relationshipList = relationShipService.findAllRelationShip();
			product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			lifePolicy = lifeProposal.getLifePolicy();
			isEdit = true;
			entitys = lifeProposal.getBranch().getEntity();
			insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
			for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
				InsuredPersonInfoDTO personDTO = new InsuredPersonInfoDTO(pv);
				insuredPersonInfoDTOMap.put(personDTO.getTempId(), personDTO);
			}
		} else {
			createNewLifeProposal();
		}
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
		createNewBeneficiariesInfo();
	}

	public void createNewLifeProposal() {
		isEdit = false;
		createNew = true;
		lifeProposal = new LifeProposal();
		lifePolicy = new LifePolicy();
		product = null;
		lifeProposal.setSubmittedDate(new Date());
	}

	public void selectActiveLifePolicyNo() {
		selectPublicLifePolicyNo("Actived");
	}

	public void returnLifePolicy(SelectEvent event) {
		createNewLifeProposal();
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
		createNewBeneficiariesInfo();
		searchPolicy = (LifeEndowmentPolicySearch) event.getObject();
		lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(searchPolicy.getPolicyNo());
		relationshipList = relationShipService.findAllRelationShip();
		product = productService.findShortTermProductById(ProductIDConfig.getShortEndowLifeId());
		insuredPersonInfoDTOMap = new HashMap<String, InsuredPersonInfoDTO>();
		if (lifePolicy != null) {
			lifeProposal = new LifeProposal(lifePolicy);
			lifeProposal.setPaymentType(paymentTypeService.findMontlyPaymentType());
			lifeProposal.setSubmittedDate(new Date());
			if (lifeProposal.getCustomer() == null) {
				organization = true;
			}
			List<ProposalInsuredPerson> insuredPersonList = new ArrayList<ProposalInsuredPerson>();
			for (PolicyInsuredPerson policyInsuredPerson : lifePolicy.getPolicyInsuredPersonList()) {
				ProposalInsuredPerson pip = new ProposalInsuredPerson(policyInsuredPerson);
				pip.setProduct(product);
				insuredPersonList.add(pip);
			}
			lifeProposal.getProposalInsuredPersonList().clear();
			for (ProposalInsuredPerson proposalInsuredPerson : insuredPersonList) {
				lifeProposal.addInsuredPerson(proposalInsuredPerson);
			}
			for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
				pv.setPeriodMonth(0);
				pv.setProposedSumInsured(0.0);
				InsuredPersonInfoDTO personDTO = new InsuredPersonInfoDTO(pv);
				insuredPersonInfoDTOMap.put(personDTO.getTempId(), personDTO);
			}
			isUnpaidLifePolicy(lifePolicy);

		}
	}

	private boolean isUnpaidLifePolicy(LifePolicy lifePolicy) {
		boolean result = true;
		boolean pending = paymentService.havePendingPayment(lifePolicy.getPolicyNo());
		if (pending) {
			addInfoMessage(null, MessageId.INVALID_LIFE_BILL_COLLECTION);
			result = false;
		}
		return result;
	}

	private void createNewInsuredPersonInfo() {
		createNewIsuredPersonInfo = true;
		insuredPersonInfoDTO.setStartDate(new Date());
	}

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
	}

	public void createNewBeneficiariesInfo() {
		createNewBeneficiariesInfo = true;
		beneficiariesInfoDTO = new BeneficiariesInfoDTO();
	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (organization) {
			lifeProposal.setCustomer(null);
			insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		} else {
			lifeProposal.setOrganization(null);
		}
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		lifeProposal.setCustomer(customer);
		fillInsuredPersonInfoDTO();
	}

	private void fillInsuredPersonInfoDTO() {
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		Customer customer = lifeProposal.getCustomer();
		insuredPersonInfoDTO.setInitialId(customer.getInitialId());
		insuredPersonInfoDTO.setName(customer.getName());
		insuredPersonInfoDTO.setFatherName(customer.getFatherName());
		insuredPersonInfoDTO.setIdType(customer.getIdType());
		insuredPersonInfoDTO.setIdNo(customer.getFullIdNo());
		insuredPersonInfoDTO.setDateOfBirth(customer.getDateOfBirth());
		insuredPersonInfoDTO.setResidentAddress(customer.getResidentAddress());
		insuredPersonInfoDTO.setOccupation(customer.getOccupation());
		insuredPersonInfoDTO.setGender(customer.getGender());
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		lifeProposal.setOrganization(organization);
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

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("InsuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
			int personSize = getInsuredPersonInfoDTOList().size();
			if (personSize < 1) {
				addErrorMessage("migrateLifepolicyEntryForm:insuredPersonInfoDTOTable", MessageId.REQUIRED_INSURED_PERSION);
				valid = false;
			} else {
				if (!(KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product))) {
					if (personSize > 1) {
						addErrorMessage("migrateLifepolicyEntryForm:insuredPersonInfoDTOTable", MessageId.INVALID_INSURED_PERSON, product.getName());
						valid = false;
					}
				}
			}
			for (InsuredPersonInfoDTO insu : insuredPersonInfoDTOMap.values()) {
				if (insu.getSumInsuredInfoNum().equals(0.0) || insu.getPeriodOfYears() == 0) {
					addErrorMessage("migrateLifepolicyEntryForm:insuredPersonInfoDTOTable", MessageId.REQUIRED, "Period of Insurance and Sum Insured");
					valid = false;
				}
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
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

	public int getMaximumTrem() {
		int result = 0;
		if (product != null) {
			result = product.getMaxTerm() / 12;
		}
		return result;
	}

	public int getMunimumTrem() {
		int result = 0;
		if (product != null) {
			result = product.getMinTerm() / 12;
		}
		return result;
	}

	public void prepareEditBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
		this.createNewBeneficiariesInfo = false;
	}

	public void removeBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfoDTO.getTempId());
		createNewBeneficiariesInfo();
	}

	public void addNewInsuredPersonInfo() {
		if (validInsuredPerson(insuredPersonInfoDTO)) {
			insuredPersonInfoDTO.setProduct(product);
			Calendar cal = Calendar.getInstance();
			insuredPersonInfoDTO.setStartDate(new Date());
			cal.setTime(insuredPersonInfoDTO.getStartDate());
			cal.add(Calendar.YEAR, insuredPersonInfoDTO.getPeriodOfYears());
			insuredPersonInfoDTO.setEndDate(cal.getTime());
			setKeyFactorValue(insuredPersonInfoDTO.getSumInsuredInfo(), insuredPersonInfoDTO.getAgeForNextYear(), insuredPersonInfoDTO.getPeriodOfYears());
			insuredPersonInfoDTO.setBeneficiariesInfoDTOList(new ArrayList<BeneficiariesInfoDTO>(beneficiariesInfoDTOMap.values()));
			insuredPersonInfoDTOMap.put(insuredPersonInfoDTO.getTempId(), insuredPersonInfoDTO);
			createNewInsuredPersonInfo();
			insuredPersonInfoDTO = new InsuredPersonInfoDTO();
			createNewBeneficiariesInfoDTOMap();
		}
	}

	private void setKeyFactorValue(double sumInsured, int age, int period) {
		for (InsuredPersonKeyFactorValue vehKF : insuredPersonInfoDTO.getKeyFactorValueList()) {
			KeyFactor kf = vehKF.getKeyFactor();
			if (KeyFactorChecker.isSumInsured(kf)) {
				vehKF.setValue(sumInsured + "");
			} else if (KeyFactorChecker.isAge(kf)) {
				vehKF.setValue(age + "");
			} else if (KeyFactorChecker.isMedicalAge(kf)) {
				vehKF.setValue(age + "");
			} else if (KeyFactorChecker.isTerm(kf)) {
				vehKF.setValue(period + "");
			}
		}
	}

	private boolean validInsuredPerson(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		boolean valid = true;
		String formID = "migrateLifepolicyEntryForm";
		int maxAge = product.getMaxAge();
		int minAge = product.getMinAge();
		int personAge = insuredPersonInfoDTO.getAgeForNextYear();
		int periodofYear = insuredPersonInfoDTO.getPeriodOfYears();
		if (personAge < minAge) {
			addErrorMessage(formID + ":dateOfBirth", MessageId.MINIMUN_INSURED_PERSON_AGE, minAge);
			valid = false;
		} else if (personAge > maxAge) {
			addErrorMessage(formID + ":dateOfBirth", MessageId.MAXIMUM_INSURED_PERSON_AGE, maxAge - 5);
			valid = false;
		} else if (personAge + periodofYear > maxAge) {
			addErrorMessage(formID + ":dateOfBirth", MessageId.MAXIMUM_INSURED_YEARS, maxAge - personAge);
			valid = false;
		}

		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList().isEmpty()) {
			addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.REQUIRED_BENEFICIARY_PERSON);
			valid = false;
		} else {
			float totalPercent = 0.0f;
			for (BeneficiariesInfoDTO beneficiary : beneficiariesInfoDTOMap.values()) {
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
		return valid;
	}

	public List<PaymentType> getPaymentTypes() {
		if (product == null) {
			return new ArrayList<PaymentType>();
		} else {
			return product.getPaymentTypeList();
		}
	}

	public Boolean isReplaceColumn() {
		if (lifePolicy != null) {
			return true;
		}
		return false;
	}

	public void prepareAddNewInsuredPersonInfo() {
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
	}

	public void prepareEditInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
		if (insuredPersonInfoDTO.getBeneficiariesInfoDTOList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (BeneficiariesInfoDTO bdto : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
				beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
			}
		}

		createNewIsuredPersonInfo = false;
	}

	public void selectUser() {
		WorkFlowType workFlowType = WorkFlowType.SHORT_ENDOWMENT;
		WorkflowTask workflowTask = WorkflowTask.SURVEY;
		selectUser(workflowTask, workFlowType);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void removeInsuredPersonInfo(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		if (!isEmpty(insuredPersonInfoDTO.getInsPersonCodeNo())) {
			insuredPersonInfoDTO.setEndorsementStatus(EndorsementStatus.TERMINATE);
		} else {
			insuredPersonInfoDTOMap.remove(insuredPersonInfoDTO.getTempId());
		}
		createNewInsuredPersonInfo();
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
		insuredPersonInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public List<Integer> getSePeriodYears() {
		return Arrays.asList(5, 7, 10);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonInfoDTO.setOccupation(occupation);
	}

	public void saveBeneficiariesInfo() {
		beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
		insuredPersonInfoDTO.setBeneficiariesInfoDTOList(new ArrayList<BeneficiariesInfoDTO>(sortByValue(beneficiariesInfoDTOMap).values()));
		createNewBeneficiariesInfo();
		PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
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

	public String addNewLifeProposal() {
		String result = null;
		LifeProposal lproposal = null;
		try {
			lifeProposal.setInsuredPersonList(getInsuredPersonInfoList());

			WorkflowTask workflowTask = WorkflowTask.CONFIRMATION;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, user, responsiblePerson);

			lifeProposal.setProposalType(ProposalType.UNDERWRITING);
			lifeProposal.setMigrate(true);
			lifeProposal.getProposalInsuredPersonList().get(0).setApproved(true);

			if (createNew) {
				lproposal = lifeProposalService.addNewMigrateLifeProposal(lifeProposal, searchPolicy, workFlowDTO, RequestStatus.PROPOSED.name());
			} else {
				lproposal = lifeProposalService.updateMigrateLifeProposal(this.lifeProposal, workFlowDTO);
			}

			if (lproposal == null) {
				addInfoMessage(null, MessageId.MIGRATE_DOES_NOT_SUCESS, "");
				addInfoMessage(null, MessageId.PREMIUM_AMOUNT_NOT_MATCH, "");
			} else {
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
				createNewLifeProposal();
				result = "dashboard";
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public List<ProposalInsuredPerson> getInsuredPersonInfoList() {
		List<ProposalInsuredPerson> result = new ArrayList<ProposalInsuredPerson>();
		if (insuredPersonInfoDTOMap.values() != null) {
			for (InsuredPersonInfoDTO insuredPersonInfoDTO : insuredPersonInfoDTOMap.values()) {
				ClassificationOfHealth classificationOfHealth = insuredPersonInfoDTO.getClassificationOfHealth();
				Customer customer = customerService.findCustomerByInsuredPerson(insuredPersonInfoDTO.getName(), insuredPersonInfoDTO.getIdNo(),
						insuredPersonInfoDTO.getFatherName(), insuredPersonInfoDTO.getDateOfBirth());
				insuredPersonInfoDTO.setCustomer(customer);
				insuredPersonInfoDTO.setAge(insuredPersonInfoDTO.getAgeForNextYear());
				ProposalInsuredPerson proposalInsuredPerson = new ProposalInsuredPerson(insuredPersonInfoDTO, lifeProposal);
				proposalInsuredPerson.setInsuredPersonAddOnList(insuredPersonInfoDTO.getInsuredPersonAddOnList(proposalInsuredPerson));
				proposalInsuredPerson.setClsOfHealth(classificationOfHealth);
				proposalInsuredPerson.setTypesOfSport(insuredPersonInfoDTO.getTypesOfSport()); // Case:SportMan
				proposalInsuredPerson.setUnit(insuredPersonInfoDTO.getUnit());

				// prepare
				List<InsuredPersonAttachment> insuredPersonAttachments = insuredPersonInfoDTO.getPerAttachmentList();
				if (insuredPersonAttachments != null) {
					for (InsuredPersonAttachment attachment : insuredPersonAttachments) {
						proposalInsuredPerson.addAttachment(attachment);
					}
				}
				proposalInsuredPerson.setInsuredPersonBeneficiariesList(insuredPersonInfoDTO.getBeneficiariesInfoList(proposalInsuredPerson));
				proposalInsuredPerson.setKeyFactorValueList(insuredPersonInfoDTO.getKeyFactorValueList(proposalInsuredPerson));
				result.add(proposalInsuredPerson);
			}
		}
		return result;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public BeneficiariesInfoDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public void setBeneficiariesInfoDTO(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
	}

	public Map<String, BeneficiariesInfoDTO> getBeneficiariesInfoDTOMap() {
		return beneficiariesInfoDTOMap;
	}

	public void setBeneficiariesInfoDTOMap(Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap) {
		this.beneficiariesInfoDTOMap = beneficiariesInfoDTOMap;
	}

	public List<BeneficiariesInfoDTO> getBeneficiariesInfoDTOList() {
		return new ArrayList<BeneficiariesInfoDTO>(beneficiariesInfoDTOMap.values());
	}

	public Map<String, InsuredPersonInfoDTO> getInsuredPersonInfoDTOMap() {
		return insuredPersonInfoDTOMap;
	}

	public void setInsuredPersonInfoDTOMap(Map<String, InsuredPersonInfoDTO> insuredPersonInfoDTOMap) {
		this.insuredPersonInfoDTOMap = insuredPersonInfoDTOMap;
	}

	public InsuredPersonInfoDTO getInsuredPersonInfoDTO() {
		return insuredPersonInfoDTO;
	}

	public void setInsuredPersonInfoDTO(InsuredPersonInfoDTO insuredPersonInfoDTO) {
		this.insuredPersonInfoDTO = insuredPersonInfoDTO;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public boolean isOrganization() {
		return organization;
	}

	public boolean isCreateNewIsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	public Product getProduct() {
		return product;
	}

	public String getUserType() {
		if (userType == null) {
			userType = UserType.AGENT.toString();
		}
		return userType;
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

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		lifeProposal.setSalePoint(salePoint);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(lifeProposal.getBranch() == null ? "" : lifeProposal.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		lifeProposal.setBranch(branch);
		lifeProposal.setSalePoint(null);
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

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isEdit() {
		return isEdit;
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

}
