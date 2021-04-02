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
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.UsageOfVehicle;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
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
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.eips.service.interfaces.IEipsService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.organization.Organization;
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
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
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
@ManagedBean(name = "AddNewPersonTravelProposalActionBean")
public class AddNewPersonTravelProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

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

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
	}

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
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

	@ManagedProperty(value = "#{BancaassuraceProposalService}")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	public void setBancaassuraceProposalService(IBancaassuraceProposalService bancaassuraceProposalService) {
		this.bancaassuraceProposalService = bancaassuraceProposalService;
	}
	
	
	

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

	@ManagedProperty(value = "#{RelationShipTypeService}")
	private IRelationShipTypeService relationShipTypeService;

	@ManagedProperty(value = "#{PercentageService}")
	private IPercentageService percentageService;

	@ManagedProperty(value = "#{EipsService}")
	private IEipsService eipsService;
	

	private boolean createNewInsuredPersonInfo;
	private boolean crateNewBeneficiary;
	private int days;
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
	private boolean editTravelProposal;
	private boolean organization;
	private boolean isUnder100Travel;
	private boolean isNewProposalTraveller;
	private List<UsageOfVehicle> selectedUsages;
	private String flightNo;
	private String voyageNo;
	private String registrationNo;
	private String trainNo;
	private boolean isFlight;
	private boolean isVoyage;
	private boolean isVehicle;
	private boolean isTrain;
	private boolean isChannel;
	private Entitys entitys;

	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
		travelProposal = (travelProposal == null) ? (PersonTravelProposal) getParam("personTravelProposal") : travelProposal;
		bancaassuranceProposal = (BancaassuranceProposal) getParam("bancaassuranceProposal");
		bancaMethodList = bancaMethodService.findAllBanca();
		isChannel = false;

		if (travelProposal != null) {
			editTravelProposal = true;
			this.entitys = travelProposal.getBranch().getEntity();
			if (travelProposal.getCustomer() == null) {
				organization = true;

			}
			isUnder100Travel = ProductIDConfig.isUnder100MileTravelInsurance(travelProposal.getProduct());
			travelInfo = travelProposal.getPersonTravelInfo();
			createNewProposalTraveller();
			createNewProposalTravellerMap();
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
			checkUsageOfVehicle();
		} else {
			createNewPersonTravelProposal();
			createNewProposalTraveller();
			createNewProposalTravellerMap();
		}
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
		productsList = productService.findProductByInsuranceType(InsuranceType.PERSON_TRAVEL);
		relationshipList = relationShipService.findAllRelationShip();
		proposalUploadedFileMap = new HashMap<String, String>();
		// travelProposal =
		// personTravelProposalService.findPersonTravelProposalById("ISTRA005001000000018124072017");

		eips = new Eips();
		ggiOrganizationList = ggiOrganizationService.findAllGGIOrganization();
		relationShipTypeList = relationShipTypeService.findAllRelationShipType();

		//relationshipList = relationShipService.findAllRelationShip();
		initializeInjection();

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

	public void createNewPersonTravelProposal() {
		travelProposal = new PersonTravelProposal();
		travelProposal.setSubmittedDate(new Date());
		travelInfo = new PersonTravelProposalInfo();
		initializeUsageOfVehicle();
	}

	public void createNewProposalTraveller() {
		createNewInsuredPersonInfo = true;
		proposalTraveller = new ProposalTraveller();
		createNewBeneficiary();
		createNewBeneficiaryMap();
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

	public void addNewProposalTraveller() {
		isNewProposalTraveller = true;
		saveProposalTraveller();
	}

	public void updateProposalTraveller() {
		isNewProposalTraveller = false;
		saveProposalTraveller();
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

	public String addNewPersonTravelProposal() {
		String result = null;
//		travelProposal.setStaffPlan(selectItem);
//		if (selectItem == true) {
//			saveEipsData();
//			travelProposal.setEips(eips);
//		}
		try {
			loadAttachment();
			prepareAddNewPersonTravelProposal();
			travelInfo.setProposalTravellerList(new ArrayList<ProposalTraveller>(proposalTravellerMap.values()));
			travelProposal.setPersonTravelInfo(travelInfo);
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), remark, WorkflowTask.CONFIRMATION, WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL, user,
					responsiblePerson);
			if (editTravelProposal) {
				personTravelProposalService.updatePersonTravelProposal(travelProposal, bancaassuranceProposal);
			} else {
				personTravelProposalService.addNewPersonTravelProposal(travelProposal, workFlowDTO, RequestStatus.PROPOSED.name(), bancaassuranceProposal);
			}
			if (travelProposalId != null) {
				moveUploadedFiles();
			}
//			if (selectItem == true) {
//				eips.setAmount((((percentage.getPercent())/100)*travelProposal.getPersonTravelInfo().getBasicTermPremium()));
//				eipsService.save(eips);
//				}
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			createNewPersonTravelProposal();
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private void moveUploadedFiles() {
		try {
			FileHandler.moveFiles(getUploadPath(), temporyDir + travelProposalId, PROPOSAL_DIR + travelProposalId);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public boolean isChannel() {
		return isChannel;
	}

	public void setChannel(boolean isChannel) {
		this.isChannel = isChannel;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (organization) {
			travelProposal.setCustomer(null);
		} else {
			travelProposal.setOrganization(null);
		}
	}

	public SaleChannelType[] getSaleChannelType() {
		return SaleChannelType.values();
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (travelProposal.getSaleChannelType().equals(SaleChannelType.AGENT)) {
			travelProposal.setSaleMan(null);
			travelProposal.setReferral(null);
		} else if (travelProposal.getSaleChannelType().equals(SaleChannelType.SALEMAN)) {
			travelProposal.setAgent(null);
			travelProposal.setReferral(null);
		} else if (travelProposal.getSaleChannelType().equals(SaleChannelType.REFERRAL)) {
			travelProposal.setSaleMan(null);
			travelProposal.setAgent(null);
		} else if (travelProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
			travelProposal.setSaleMan(null);
			travelProposal.setAgent(null);
			travelProposal.setReferral(null);
			bancaassuranceProposal = new BancaassuranceProposal();
		}
	}

	public void changeBancaEvent(AjaxBehaviorEvent event) {

		bancaassuranceProposal.setBancaBRM(null);
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);
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

	/**
	 * Calculate number of days between start date and end date according to
	 * month end day.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param startDayInclude
	 * @return number of days between start date and end date
	 */
	// public int travelDaysBaseOnMonthEndDate(Date startDate, Date endDate,
	// boolean startDayInclude) {
	// DateTime start = new DateTime(startDate);
	// DateTime end = new DateTime(endDate);
	// Period period = new Period(start, end);
	// int month = period.getMonths();
	// int week = period.getWeeks();
	// int day = period.getDays();
	// int totalDays = (month * 30) + (week * 7) + day;
	// if (startDayInclude) {
	// totalDays += 1;
	// }
	// return totalDays;
	// }

	private int travelDaysBaseOnMonthEndDate(Date startDate, Date endDate, boolean startDayInclude) {
		DateTime start = new DateTime(startDate);
		DateTime end = new DateTime(endDate);
		Period period = new Period(start, end);
		int month = period.getMonths();
		int week = period.getWeeks();
		int day = period.getDays();
		int totalDays = (month * 30) + (week * 7) + day;
		if (month == 0) {
			totalDays += 1;
		}
		return totalDays;

	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "personTravelProposalEntryForm";
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

	public boolean getEditTravelProposal() {
		return editTravelProposal;
	}

	public UsageOfVehicle[] getUsageOfVehicleList() {
		return UsageOfVehicle.values();
	}

	public void selectSalePoint() {
		selectSalePointByBranch(travelProposal.getBranch() == null ? "" : travelProposal.getBranch().getId());
	}

	public void removeBranch() {
		travelProposal.setBranch(null);
		travelProposal.setSalePoint(null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		travelProposal.setSalePoint(salePoint);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		travelProposal.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		travelProposal.setOrganization(organization);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		travelProposal.setPaymentType(paymentType);
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
		travelProposal.setSalePoint(null);
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

	public boolean getIsUnder100Travel() {
		return isUnder100Travel;
	}

	/**
	 * @return the flightNo
	 */
	public String getFlightNo() {
		return flightNo;
	}

	/**
	 * @param flightNo
	 *            the flightNo to set
	 */
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	/**
	 * @return the voyageNo
	 */
	public String getVoyageNo() {
		return voyageNo;
	}

	/**
	 * @param voyageNo
	 *            the voyageNo to set
	 */
	public void setVoyageNo(String voyageNo) {
		this.voyageNo = voyageNo;
	}

	/**
	 * @return the registrationNo
	 */
	public String getRegistrationNo() {
		return registrationNo;
	}

	/**
	 * @param registrationNo
	 *            the registrationNo to set
	 */
	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	/**
	 * @return the trainNo
	 */
	public String getTrainNo() {
		return trainNo;
	}

	/**
	 * @param trainNo
	 *            the trainNo to set
	 */
	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	/**
	 * @param trainNo
	 *            the trainNo to set
	 */
	public void setTrain(boolean isTrain) {
		this.isTrain = isTrain;
	}

	public boolean isTrain() {
		return isTrain;
	}

	/**
	 * @return the isFlight
	 */
	public boolean isFlight() {
		return isFlight;
	}

	/**
	 * @param isFlight
	 *            the isFlight to set
	 */
	public void setFlight(boolean isFlight) {
		this.isFlight = isFlight;
	}

	/**
	 * @return the isVoyage
	 */
	public boolean isVoyage() {
		return isVoyage;
	}

	/**
	 * @param isVoyage
	 *            the isVoyage to set
	 */
	public void setVoyage(boolean isVoyage) {
		this.isVoyage = isVoyage;
	}

	/**
	 * @return the isVehicle
	 */
	public boolean isVehicle() {
		return isVehicle;
	}

	/**
	 * @param isVehicle
	 *            the isVehicle to set
	 */
	public void setVehicle(boolean isVehicle) {
		this.isVehicle = isVehicle;
	}

	/**
	 * @return the selectedUsages
	 */
	public List<UsageOfVehicle> getSelectedUsages() {
		return selectedUsages;
	}

	/**
	 * @param selectedUsages
	 *            the selectedUsages to set
	 */
	public void setSelectedUsages(List<UsageOfVehicle> selectedUsages) {
		this.selectedUsages = selectedUsages;
	}

	public void productChange(AjaxBehaviorEvent event) {
		if (travelProposal.getProduct() != null) {
			travelProposal.setCurrency(travelProposal.getProduct().getCurrency());
			isUnder100Travel = ProductIDConfig.isUnder100MileTravelInsurance(travelProposal.getProduct());
			if (isUnder100Travel) {
				travelInfo.setTotalUnit(0);
				proposalTraveller.setUnit(0);
			}
		}
	}

	public void selectPaymentType() {
		selectPaymentType(travelProposal.getProduct());
	}

	public boolean validInsuredPerson() {
		boolean valid = true;
		String formID = "personTravelProposalEntryForm";
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
		}
		// if ((proposalTraveller.getUnit() *
		// travelProposal.getProduct().getMinSumInsured() >
		// travelProposal.getProduct().getMaxSumInsured())) {
		// addErrorMessage(formID + ":unit", MessageId.INVALID_TRAVELLER_UINT);
		// valid = false;
		// }
		// }
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

		if (isNewProposalTraveller && proposalTravellerMap.size() + 1 > travelInfo.getNoOfPassenger()) {
			addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.INVALID_PASSENGER);
			valid = false;
		}
		int totalUnit = 0;
		for (ProposalTraveller traveller : proposalTravellerMap.values()) {
			if (proposalTraveller.getTempId() != traveller.getTempId()) {
				totalUnit += traveller.getUnit();
			}
		}
		totalUnit += proposalTraveller.getUnit();
		if (travelInfo.getTotalUnit() < totalUnit) {
			addErrorMessage(formID + ":unit", MessageId.INVALID_UNIT);
			valid = false;
		}
		return valid;
	}

	public boolean isNewProposalTraveller() {
		return isNewProposalTraveller;
	}

	public void removeEntity() {
		this.entitys = null;
		travelProposal.setBranch(null);
		travelProposal.setSalePoint(null);
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

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
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

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
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

	public IStaffService getStaffService() {
		return staffService;
	}

	public void setStaffService(IStaffService staffService) {
		this.staffService = staffService;
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
	// Choose Staff With Organization

		public void selectStaffWithOrganization() {
			staffList = staffService.findStaffWithGGIOrganization(ggiOrganization.getId());
		}

		public void showPercentageWithRelationShip() {
			percentage = percentageService.findPercentageWithRelationShip(relationShipType.getId(),travelProposal.getProduct().getId());
		}

	
}
