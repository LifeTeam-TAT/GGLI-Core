package org.ace.insurance.web.manage.life.confirm;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.eips.service.interfaces.IEipsService;
import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.percentage.Percentage;
import org.ace.insurance.system.common.percentage.service.interfaces.IPercentageService;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.service.interfaces.IRelationShipTypeService;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewLifeConfirmationActionBean")
public class AddNewLifeConfirmationActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{LifeEndorsementService}")
	private ILifeEndorsementService lifeEndorsementService;

	public void setLifeEndorsementService(ILifeEndorsementService lifeEndorsementService) {
		this.lifeEndorsementService = lifeEndorsementService;
	}

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
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

	private User user;
	private LifeProposal lifeProposal;
	private BancaassuranceProposal bancaassuranceProposal;
	private WorkFlowDTO workFlowDTO;
	private boolean approvedProposal = true;
	private boolean isEndorse;
	private boolean isPersonalAccident;
	private boolean isFarmer;
	private boolean isPublicTermLife;
	private boolean isSinglePremiumEndowmentLife;
	private boolean isSinglePremiumCreditLife;
	private boolean isShortTermSinglePremiumCreditLife;
	private boolean isSimpleLife;
	private boolean isShortEndowLife;
	private String remark;
	private String keyFactor;
	private User responsiblePerson;
	private List<PolicyInsuredPerson> sportManInsuredPeople;
	private boolean sportManSI;
	private Date minEndDate;
	private Double availableSI;

	private LifeEndorseInfo lifeEndorseInfo;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
		// bancaassuranceProposal = (BancaassuranceProposal)
		// getParam("bancaassuranceProposal");
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposal.getId());
		isEndorse = lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT);
		isPersonalAccident = (KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product));
		isFarmer = KeyFactorChecker.isFarmer(product);
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		isSimpleLife = KeyFactorChecker.isSimpleLife(product);
		isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
		approvedProposal = true;
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			if (!pv.isApproved()) {
				approvedProposal = false;
				break;
			}
		}

		if (isEndorse) {
			lifeEndorseInfo = lifeEndorsementService.findLastLifeEndorseInfoByProposalNo(lifeProposal.getLifePolicy().getPolicyNo());
		} else {
			if (recalculatePremium(CONFIRMATION)) {
				lifeProposalService.calculatePremium(lifeProposal);
			}
		}

		if(isSimpleLife) {
		for (InsuredPersonKeyFactorValue vehKF : lifeProposal.getProposalInsuredPersonList().get(0).getKeyFactorValueList()) {
			if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {
				keyFactor = vehKF.getValue();
			}
			
			}
		}
		sportManInsuredPeople = lifeProposalService.findPolicyInsuredPersonByParameters(lifeProposal.getProposalInsuredPersonList().get(0).getName(),
				lifeProposal.getProposalInsuredPersonList().get(0).getIdNo(), lifeProposal.getProposalInsuredPersonList().get(0).getFatherName(),
				lifeProposal.getProposalInsuredPersonList().get(0).getDateOfBirth());
		sportManSI = isExcessSISportMan();

	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public boolean getIsEndorse() {
		return isEndorse;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public LifeEndorseInfo getLifeEndorseInfo() {
		return lifeEndorseInfo;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public String confirmLifeProposal() {
		LifePolicy lifePolicy = lifePolicyService.findByLifeProposalId(lifeProposal.getId());
		if (lifePolicy != null) {
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.ALREADY_INSERT);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			return "";
		} else {
			WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_PAYMENT : (isFarmer && lifeProposal.isSkipPaymentTLF()) ? WorkflowTask.ISSUING : WorkflowTask.PAYMENT;
			WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
					: isSinglePremiumEndowmentLife ? WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
							: isSinglePremiumCreditLife ? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
									: isShortTermSinglePremiumCreditLife ? WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
											: isSimpleLife ? WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL
													: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
															: isPublicTermLife ? WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL
																	: isShortEndowLife ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL : WorkflowReferenceType.LIFE_PROPOSAL;
			workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
			outjectLifeProposal(lifeProposal);
			outjectWorkFlowDTO(workFlowDTO);
			return "printLifeProposal";
		}
	}

	public String editLifeProposal() {
		BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposal.getId());
		Eips eips = null;
		if (null != lifeProposal.getEips()) {
			eips = eipsService.findById(lifeProposal.getEips().getId());
		}

		if (isPersonalAccident) {
			putParam("insuranceType", InsuranceType.PERSONAL_ACCIDENT);
		} else {
			putParam("insuranceType", InsuranceType.LIFE);
		}
		putParam("bancaassuranceProposal", bancaassuranceProposal);
		putParam("eips", eips);

		putParam("lifeProposal", lifeProposal);
		if (lifeProposal.isMigrate()) {
			return "editMigrateLifePolicy";
		}
		return "editLifeProposal";
	}

	public String denyLifeProposal() {
		String result = null;
		try {
			WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_PROPOSAL_REJECT : WorkflowTask.PROPOSAL_REJECT;
			WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
					: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
							: isSinglePremiumEndowmentLife ? WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
									: isSinglePremiumCreditLife ? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
											: isShortTermSinglePremiumCreditLife ? WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
													: isSimpleLife ? WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL
															: isPublicTermLife ? WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL
																	: isShortEndowLife ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL : WorkflowReferenceType.LIFE_PROPOSAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, user);
			lifeProposalService.rejectLifeProposal(lifeProposal, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void selectUser() {
		WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_PAYMENT : (isFarmer && lifeProposal.isSkipPaymentTLF()) ? WorkflowTask.ISSUING : WorkflowTask.PAYMENT;
		WorkFlowType workFlowType = isPersonalAccident ? WorkFlowType.PERSONAL_ACCIDENT
				: isSinglePremiumEndowmentLife ? WorkFlowType.SINGLE_PREMIUM_ENDOWMENT_LIFE
						: isSinglePremiumCreditLife ? WorkFlowType.SINGLE_PREMIUM_CREDIT_LIFE
								: isShortTermSinglePremiumCreditLife ? WorkFlowType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE
										: isSimpleLife ? WorkFlowType.SIMPLE_LIFE
												: isFarmer ? WorkFlowType.FARMER
														: isPublicTermLife ? WorkFlowType.PUBLIC_TERM_LIFE : isShortEndowLife ? WorkFlowType.SHORT_ENDOWMENT : WorkFlowType.LIFE;
		selectUser(workflowTask, workFlowType);
	}

	/**
	 * 
	 * This method is used to retrieve the available SI amount for an insured
	 * person.
	 * 
	 * @param insuredPersonInfo
	 * @return double
	 */
	public double getAvailableSISportMan() {
		double availableSI = 0.0;
		double maxSi = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getMaxSumInsured();
		availableSI = maxSi - (getTotalSISportMan());

		return availableSI;
	}

	/**
	 * 
	 */

	/**
	 * 
	 * This method is used to retrieve the total SI amount of an insured
	 * person's active policies.
	 * 
	 * @param insuredPersonInfo
	 * @return double
	 * 
	 */
	public double getTotalSISportMan() {
		double sumInsured = 0.0;
		Date endDate;
		Map<Date, Double> endDateMap = new HashMap<Date, Double>();

		if (sportManInsuredPeople != null) {
			for (PolicyInsuredPerson p : sportManInsuredPeople) {
				if (p.getLifePolicy() != null) {
					endDate = p.getLifePolicy().getActivedPolicyEndDate();

					if (endDate != null && endDate.after(new Date()) && KeyFactorChecker.isSportMan(p.getProduct())) {
						sumInsured += p.getSumInsured();
						endDateMap.put(endDate, p.getSumInsured());
					}
				}

			}
		}

		int count = 0;
		for (Date i : endDateMap.keySet()) {
			if (count == 0) {
				minEndDate = i;
				availableSI = endDateMap.get(i);
			}

			if (minEndDate.after(i)) {
				minEndDate = i;
				availableSI = endDateMap.get(i);
			}
			count = 1;
		}

		return sumInsured;
	}

	/**
	 * 
	 * This method is used to decide whether an sport man insuredPerson's
	 * sumInsured amount is over sport man product's maximum SI or not in all of
	 * his/her active policies.
	 * 
	 * @param insuredPersonInfo
	 * @return boolean true if SI is over.
	 */
	public boolean isExcessSISportMan() {
		double sumInsured = getTotalSISportMan() + lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured();
		boolean flag = false;

		if (sumInsured > lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getMaxSumInsured()) {
			flag = true;
		}

		// return sumInsured > insuredPersonInfo.getProduct().getMaxSumInsured()
		// ? true: false;
		return flag;
	}

	/**
	 * 
	 * This method is used to give message to user when user's proposed sum
	 * insured is not valid in sport man.
	 * 
	 * @return String
	 */
	public String getSportManMessage() {
		Double available = availableSI + getAvailableSISportMan();
		DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		return "You will have to wait till " + df.format(minEndDate) + " to allow the sum insured amount at " + available + ".";
	}

	public boolean isSportManSI() {
		return sportManSI;
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

	private void outjectLifeProposal(LifeProposal lifeProposal) {
		putParam("lifeProposal", lifeProposal);
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	/* For Template */
	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	@ManagedProperty(value = "#{LifePolicySummaryService}")
	private ILifePolicySummaryService lifePolicySummaryService;

	public void setLifePolicySummaryService(ILifePolicySummaryService lifePolicySummaryService) {
		this.lifePolicySummaryService = lifePolicySummaryService;
	}

	private List<LifePolicy> lifePolicyList;
	private List<LifePolicyHistory> lifePolicyHistoryList;

	public List<LifePolicy> getLifePolicyList() {
		if (lifePolicyList == null) {
			lifePolicyList = lifePolicyService.findPolicyByProposalId(lifeProposal.getId());
		}
		return lifePolicyList;
	}

	public List<LifePolicyHistory> getLifePolicyHistoryList() {
		if (lifePolicyHistoryList == null) {
			if (lifeProposal.getLifePolicy() != null) {
				lifePolicyHistoryList = lifePolicyHistoryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getPolicyNo());
			}
		}
		return lifePolicyHistoryList;
	}

	public LifePolicySummary getLifePolicySummary() {
		return lifePolicySummaryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getId());
	}

	public boolean isFarmer() {
		return isFarmer;
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

	public boolean isSimpleLife() {
		return isSimpleLife;
	}

	public String getPageHeader() {
		return (isEndorse ? "Life Endorsement"
				: isFarmer ? "Farmer"
						: isSinglePremiumEndowmentLife ? "Single Premium Endownment Life"
								: isSinglePremiumCreditLife ? "Single Premium Credit Life"
										: isShortTermSinglePremiumCreditLife ? "Short Term Single Premium Credit Life"
												: isSimpleLife ? "Simple Life" : isPersonalAccident ? "Personal Accident" : isShortEndowLife ? "Short Term Endowment Life" : "Life")
				+ " Proposal Confirmation";
	}

	// Choose Staff With Organization

	public void selectStaffWithOrganization() {
		staffList = staffService.findStaffWithGGIOrganization(ggiOrganization.getId());
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

	private void clearData() {
		ggiOrganization = new GGIOrganization();
		staff = new Staff();
		relationShipType = new RelationShipType();
		percentage = new Percentage();
	}
	
	public boolean getIsSimpleLife() {
		return isSimpleLife;
	}

	public String getKeyFactor() {
		return keyFactor;
	}

}
