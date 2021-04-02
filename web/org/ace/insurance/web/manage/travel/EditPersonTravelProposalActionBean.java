package org.ace.insurance.web.manage.travel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.UsageOfVehicle;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.city.service.interfaces.ICityService;
import org.ace.insurance.system.common.country.service.interfaces.ICountryService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.organization.service.interfaces.IOrganizationService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalInfo;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalKeyfactorValue;
import org.ace.insurance.travel.personTravel.proposal.ProposalPersonTravelVehicle;
import org.ace.insurance.travel.personTravel.proposal.ProposalTraveller;
import org.ace.insurance.travel.personTravel.proposal.ProposalTravellerBeneficiary;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "EditPersonTravelProposalActionBean")
public class EditPersonTravelProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	IPersonTravelProposalService personTravelProposalService;

	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
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

	@ManagedProperty(value = "#{OrganizationService}")
	private IOrganizationService organizationService;

	public void setOrganizationService(IOrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@ManagedProperty(value = "#{CityService}")
	private ICityService cityService;

	public void setCityService(ICityService cityService) {
		this.cityService = cityService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{CountryService}")
	private ICountryService countryService;

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
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

	private boolean createNewInsuredPersonInfo;
	private boolean crateNewBeneficiary;
	private String userType;
	private String remark;
	private final String PROPOSAL_DIR = "/upload/personTravel-proposal/";
	private String temporyDir;
	private String travelProposalId;
	private User user;
	private User responsiblePerson;
	private PersonTravelProposal travelProposal;
	private PersonTravelProposalInfo travelInfo;
	private ProposalTraveller proposalTraveller;
	private ProposalTravellerBeneficiary beneficiary;
	private BancaassuranceProposal bancaassuranceProposal;
	private List<BancaMethod> bancaMethodList;
	private BancaMethod bancaMethod;
	private List<Product> productsList;
	private List<RelationShip> relationshipList;
	private Map<String, ProposalTraveller> proposalTravellerMap;
	private Map<String, ProposalTravellerBeneficiary> beneficiaryMap;
	private Map<String, String> proposalUploadedFileMap;
	private List<UsageOfVehicle> selectedUsages;
	private boolean organization;
	private boolean isUnder100Travel;
	private boolean isLocalTravel;
	private boolean isForeignTravel;
	private String flightNo;
	private String voyageNo;
	private String registrationNo;
	private String trainNo;
	private boolean isFlight;
	private boolean isVoyage;
	private boolean isVehicle;
	private boolean isTrain;
	private boolean isChannel;
	private int days;
	private Entitys entitys;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (travelProposal == null) ? (PersonTravelProposal) getParam("personTravelProposal") : travelProposal;
		bancaassuranceProposal = (BancaassuranceProposal) getParam("bancaassuranceProposal");
		entitys = travelProposal.getBranch().getEntity();
		bancaMethodList = bancaMethodService.findAllBanca();
		isChannel = false;
	}

	@PreDestroy
	public void destroy() {
		removeParam("personTravelProposal");
		removeParam("bancaassuranceProposal");
	}

	@PostConstruct
	public void init() {

		/* Create temp directory for upload */
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		relationshipList = relationShipService.findAllRelationShip();
		proposalUploadedFileMap = new HashMap<String, String>();
		initializeInjection();
		if (travelProposal.getCustomer() == null) {
			organization = true;
		}
		isLocalTravel = ProductIDConfig.isLocalTravelInsurance(travelProposal.getProduct());
		isUnder100Travel = ProductIDConfig.isUnder100MileTravelInsurance(travelProposal.getProduct());
		isForeignTravel = ProductIDConfig.isForeignTravelInsurance(travelProposal.getProduct());
		travelInfo = travelProposal.getPersonTravelInfo();
		proposalTravellerMap = new HashMap<String, ProposalTraveller>();
		if (!travelInfo.getProposalTravellerList().isEmpty()) {
			for (ProposalTraveller traveller : travelInfo.getProposalTravellerList()) {
				proposalTravellerMap.put(traveller.getTempId(), traveller);
			}
		}
		if (!travelInfo.getAttachmentList().isEmpty()) {
			for (Attachment attach : travelInfo.getAttachmentList()) {
				proposalUploadedFileMap.put(attach.getName(), attach.getFilePath());
			}
		}
		initializeUsageOfVehicle();
		checkUsageOfVehicle();
		createNewProposalTraveller();
		organization = travelProposal.getCustomer() == null ? true : false;
	}

	public void initializeUsageOfVehicle() {
		flightNo = "";
		voyageNo = "";
		registrationNo = "";
		trainNo = "";
		isFlight = false;
		isVoyage = false;
		isVehicle = false;
		selectedUsages = new ArrayList<>();
	}

	public void createNewProposalTraveller() {
		createNewInsuredPersonInfo = true;
		proposalTraveller = new ProposalTraveller();
		createNewBeneficiary();
		createNewBeneficiaryMap();
	}

	public void checkUsageOfVehicle() {
		selectedUsages = new ArrayList<>();
		for (ProposalPersonTravelVehicle vehicle : travelInfo.getProposalPersonTravelVehicleList()) {
			switch (vehicle.getUsageOfVehicle()) {
				case FLIGHT:
					isFlight = true;
					flightNo = vehicle.getRegistrationNo();
					break;
				case VOYAGE:
					isVoyage = true;
					voyageNo = vehicle.getRegistrationNo();
					break;
				case VEHICLE:
					isVehicle = true;
					registrationNo = vehicle.getRegistrationNo();
					break;
				case TRAIN:
					isTrain = true;
					trainNo = vehicle.getRegistrationNo();
					break;
				default:
					break;
			}
			selectedUsages.add(vehicle.getUsageOfVehicle());
		}
	}

	public void changeUsageOfVehicle(AjaxBehaviorEvent event) {
		isFlight = false;
		isVoyage = false;
		isVehicle = false;
		isTrain = false;
		for (UsageOfVehicle usage : selectedUsages) {
			switch (usage) {
				case FLIGHT:
					isFlight = true;
					break;
				case VOYAGE:
					isVoyage = true;
					break;
				case VEHICLE:
					isVehicle = true;
					break;
				case TRAIN:
					isTrain = true;
					break;
				default:
					break;
			}
		}
	}

	public void changeChannel(AjaxBehaviorEvent event) {
		if (isChannel) {
			isChannel = true;
		} else {
			isChannel = false;
			bancaassuranceProposal = new BancaassuranceProposal();
		}

	}

	public int travelDaysBaseOnMonthEndDate(Date startDate, Date endDate, boolean startDayInclude) {
		DateTime start = new DateTime(startDate);
		DateTime end = new DateTime(endDate);
		Period period = new Period(start, end);
		int month = period.getMonths();
		int week = period.getWeeks();
		int day = period.getDays();
		int totalDays = (month * 30) + (week * 7) + day;
		if (startDayInclude) {
			totalDays += 1;
		}
		return totalDays;
	}

	public void setKeyfactor(int days) {
		for (PersonTravelProposalKeyfactorValue tkf : travelInfo.getTravelProposalKeyfactorValueList()) {
			tkf.setValue(days + "");
		}

	}

	public void prepareAddNewPersonTravelProposal() {
		travelInfo.loadKeyFactor(travelProposal.getProduct());
		setKeyfactor(days);
		travelInfo.setProposalPersonTravelVehicleList(loadVehicleList());
	}

	public List<ProposalPersonTravelVehicle> loadVehicleList() {
		List<ProposalPersonTravelVehicle> vehicleList = new ArrayList<>();
		ProposalPersonTravelVehicle vehicle;
		for (UsageOfVehicle usage : selectedUsages) {
			vehicle = new ProposalPersonTravelVehicle();
			switch (usage) {
				case FLIGHT:
					vehicle.setRegistrationNo(flightNo);
					break;
				case VOYAGE:
					vehicle.setRegistrationNo(voyageNo);
					break;
				case VEHICLE:
					vehicle.setRegistrationNo(registrationNo);
					break;
				case TRAIN:
					vehicle.setRegistrationNo(trainNo);
					break;
				default:
					break;
			}
			vehicle.setUsageOfVehicle(usage);
			vehicleList.add(vehicle);
		}
		return vehicleList;
	}

	public void createNewProposalTravellerMap() {
		proposalTravellerMap = new HashMap<String, ProposalTraveller>();
	}

	public void createNewBeneficiary() {
		beneficiary = new ProposalTravellerBeneficiary();
	}

	public void createNewBeneficiaryMap() {
		beneficiaryMap = new HashMap<String, ProposalTravellerBeneficiary>();
	}

	public Map<String, ProposalTraveller> getProposalTravellerMap() {
		return proposalTravellerMap;
	}

	public Map<String, ProposalTravellerBeneficiary> getBeneficiaryMap() {
		return beneficiaryMap;
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

	public boolean isChannel() {
		return isChannel;
	}

	public void setChannel(boolean isChannel) {
		this.isChannel = isChannel;
	}

	public void saveBeneficiary() {
		beneficiaryMap.put(beneficiary.getTempId(), beneficiary);
		createNewBeneficiary();
		PrimeFaces.current().executeScript("PF('PF('beneficiariesInfoEntryDialog').hide()");
	}

	public void prepareEditBeneficiary(ProposalTravellerBeneficiary beneficiary) {
		this.beneficiary = new ProposalTravellerBeneficiary(beneficiary);

	}

	public void removeBeneficiary(String removalTempId) {
		if (beneficiary.getTempId().equals(removalTempId)) {
			createNewBeneficiary();
		}
		beneficiaryMap.remove(removalTempId);
	}

	public void saveProposalTraveller() {
		if (validInsuredPerson()) {
			proposalTraveller.setProposalTravellerBeneficiaryList(new ArrayList<ProposalTravellerBeneficiary>(beneficiaryMap.values()));
			proposalTravellerMap.put(proposalTraveller.getTempId(), proposalTraveller);
			createNewProposalTraveller();
			createNewBeneficiaryMap();
		}

	}

	public boolean validInsuredPerson() {
		boolean valid = true;
		String formID = "personTravelProposalEditForm";
		if (proposalTraveller.getName().isEmpty()) {
			addErrorMessage(formID + ":name", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		// if (proposalTraveller.getNrcNo().isEmpty()) {
		// addErrorMessage(formID + ":travellerIdNo",
		// UIInput.REQUIRED_MESSAGE_ID);
		// valid = false;
		// }
		if (travelProposal.getProduct().getAutoCalculate()) {
			if (proposalTraveller.getUnit() < 1) {
				addErrorMessage(formID + ":unit", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			// if ((proposalTraveller.getUnit() *
			// travelProposal.getProduct().getMinSumInsured() >
			// travelProposal.getProduct().getMaxSumInsured())) {
			// addErrorMessage(formID + ":unit",
			// MessageId.INVALID_TRAVELLER_UINT);
			// valid = false;
			// }
		}
		// if (proposalTraveller.getPhone().isEmpty()) {
		// addErrorMessage(formID + ":phoneNo", UIInput.REQUIRED_MESSAGE_ID);
		// valid = false;
		// }
		if (!beneficiaryMap.values().isEmpty()) {
			Double totalPercentage = 0.0;
			for (ProposalTravellerBeneficiary b : beneficiaryMap.values()) {
				totalPercentage += b.getPercentage();
			}
			if (totalPercentage > 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.OVER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
		}
		return valid;

	}

	public void prepareAddNewProposalTraveller() {
		createNewProposalTraveller();
		createNewBeneficiaryMap();
	}

	public void prepareEditProposalTraveller(ProposalTraveller proposalTraveller) {
		this.createNewInsuredPersonInfo = false;
		this.proposalTraveller = new ProposalTraveller(proposalTraveller);
		for (ProposalTravellerBeneficiary beneficiary : proposalTraveller.getProposalTravellerBeneficiaryList()) {
			beneficiaryMap.put(beneficiary.getTempId(), beneficiary);
		}
	}

	public void removeProposalTraveller(String removalTempId) {
		proposalTravellerMap.remove(removalTempId);
		createNewProposalTraveller();
	}

	public String updateProposal() {
		String result = null;
		try {
			loadAttachment();
			prepareAddNewPersonTravelProposal();
			travelInfo.setProposalTravellerList(new ArrayList<ProposalTraveller>(proposalTravellerMap.values()));
			travelProposal.setPersonTravelInfo(travelInfo);
			personTravelProposalService.updatePersonTravelProposal(travelProposal, bancaassuranceProposal);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;

	}

	public SaleChannelType[] getSaleChannelType() {
		return SaleChannelType.values();
	}

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewInsuredPersonInfo;
	}

	public boolean isCrateNewBeneficiary() {
		return crateNewBeneficiary;
	}

	public boolean isOrganization() {
		return organization;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (organization) {
			travelProposal.setCustomer(null);
			// insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		} else {
			travelProposal.setOrganization(null);
		}
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			travelProposal.setSaleMan(null);
			travelProposal.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			travelProposal.setAgent(null);
			travelProposal.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			travelProposal.setSaleMan(null);
			travelProposal.setAgent(null);
		}
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "personTravelProposalEditForm";

		if ("TravelInfo".equals(event.getOldStep()) && "insuredPersonInfo".equals(event.getNewStep())) {
			days = travelDaysBaseOnMonthEndDate(travelInfo.getDepartureDate(), travelInfo.getArrivalDate(), true);
			int maxTerm = travelProposal.getProduct().getMaxTerm();
			if ((maxTerm != 0 && days > maxTerm) || days < 1) {
				addErrorMessage(formID + ":arrivalDate", MessageId.INVALID_START_END_DATE);
				valid = false;
			}
			if (travelProposal.getProduct().getAutoCalculate() && travelInfo.getTotalUnit() < travelInfo.getNoOfPassenger()) {
				addErrorMessage(formID + ":noOfUnit", MessageId.INVALID_TOTALUNIT);
				valid = false;
			}
			if (travelProposal.getProduct().getMaxUnit() != 0) {
				if (travelInfo.getTotalUnit() > (travelInfo.getNoOfPassenger() * travelProposal.getProduct().getMaxUnit())) {
					addErrorMessage(formID + ":noOfUnit", MessageId.OVER_TOTAL_UNIT);
					valid = false;
				}
			}
		}

		if ("insuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
			int totalUnit = 0;
			if (getProposalTravellerList().size() > 0) {
				for (ProposalTraveller traveller : getProposalTravellerList()) {
					totalUnit = totalUnit + traveller.getUnit();
				}
			} else
				prepareAddNewPersonTravelProposal();
		}

		return valid ? event.getNewStep() : event.getOldStep();
	}

	public void handleProposalAttachment(FileUploadEvent event) {

		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		travelProposalId = travelInfo.getTempId();
		if (!proposalUploadedFileMap.containsKey(fileName)) {
			String filePath = temporyDir + travelProposalId + "/" + fileName;
			proposalUploadedFileMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}

	}

	public void removeProposalUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			proposalUploadedFileMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			if (proposalUploadedFileMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + temporyDir));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadAttachment() {
		for (String fileName : proposalUploadedFileMap.keySet()) {
			String filePath = PROPOSAL_DIR + travelProposalId + "/" + fileName;
			travelInfo.addAttachment(new Attachment(fileName, filePath));
		}

	}

	public List<String> getProposalUploadedFileList() {
		return new ArrayList<String>(proposalUploadedFileMap.values());
	}

	public String getUserType() {
		if (userType == null) {
			userType = UserType.AGENT.toString();
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
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

	public boolean getIsLocalTravel() {
		return isLocalTravel;
	}

	public boolean getIsForeignTravel() {
		return isForeignTravel;
	}

	public boolean getIsUnder100Travel() {
		return isUnder100Travel;
	}

	public PersonTravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(PersonTravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public PersonTravelProposalInfo getTravelInfo() {
		return travelInfo;
	}

	public void setTravelInfo(PersonTravelProposalInfo travelInfo) {
		this.travelInfo = travelInfo;
	}

	public ProposalTraveller getProposalTraveller() {
		return proposalTraveller;
	}

	public void setProposalTraveller(ProposalTraveller proposalTraveller) {
		this.proposalTraveller = proposalTraveller;
	}

	public ProposalTravellerBeneficiary getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(ProposalTravellerBeneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}

	public UsageOfVehicle[] getUsageOfVehicleList() {
		return UsageOfVehicle.values();
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		travelProposal.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		travelProposal.setOrganization(organization);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		travelProposal.setAgent(agent);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		travelProposal.setSaleMan(saleMan);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		travelProposal.setReferral(referral);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		travelProposal.setBranch(branch);
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		proposalTraveller.setTownship(township);
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township township = (Township) event.getObject();
		beneficiary.setTownship(township);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnExpress(SelectEvent event) {
		Express express = (Express) event.getObject();
		this.travelInfo.setExpress(express);
	}

//	public void selectBancaBrm() {
//		selectBancaBRM(bancaassuranceProposal.getBancaBranch().getId());
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

	public void selectUser() {

		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.PERSON_TRAVEL);
	}

	public List<Product> getProductsList() {
		return productsList;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	public List<ProposalTraveller> getProposalTravellerList() {
		return new ArrayList<ProposalTraveller>(proposalTravellerMap.values());
	}

	public List<ProposalTravellerBeneficiary> getBeneficiaryList() {
		return new ArrayList<ProposalTravellerBeneficiary>(beneficiaryMap.values());
	}

	public String getTravelProposalId() {
		return travelProposalId;
	}

	public User getUser() {
		return user;
	}

	public String getVoyageNo() {
		return voyageNo;
	}

	public String getTrainNo() {
		return trainNo;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public boolean isFlight() {
		return isFlight;
	}

	public void setFlight(boolean isFlight) {
		this.isFlight = isFlight;
	}

	public boolean isVoyage() {
		return isVoyage;
	}

	public void setVoyage(boolean isVoyage) {
		this.isVoyage = isVoyage;
	}

	public boolean isVehicle() {
		return isVehicle;
	}

	public void setVehicle(boolean isVehicle) {
		this.isVehicle = isVehicle;
	}

	public boolean isTrain() {
		return isTrain;
	}

	public void setTrain(boolean isTrain) {
		this.isTrain = isTrain;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setForeignTravel(boolean isForeignTravel) {
		this.isForeignTravel = isForeignTravel;
	}

	public List<UsageOfVehicle> getSelectedUsages() {
		return selectedUsages;
	}

	public void setSelectedUsages(List<UsageOfVehicle> selectedUsages) {
		this.selectedUsages = selectedUsages;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public void setVoyageNo(String voyageNo) {
		this.voyageNo = voyageNo;
	}

	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (entitys != null && !entity.getId().equals(entitys.getId())) {
			travelProposal.setBranch(null);
			travelProposal.setSalePoint(null);
		}
		this.entitys = entity;
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

	public void selectSalePoint() {
		selectSalePointByBranch(travelProposal.getBranch() == null ? "" : travelProposal.getBranch().getId());
	}

	public void removeChannel() {
		bancaassuranceProposal.setChannel(null);

	}

	public void removeBancaLIC() {
		bancaassuranceProposal.setBancaLIC(null);

	}

	public void removeBancaBranch() {
		bancaassuranceProposal.setBancaBranch(null);

	}

	public void removeBranch() {
		travelProposal.setBranch(null);
		travelProposal.setSalePoint(null);

	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		travelProposal.setSalePoint(salePoint);
	}

}
