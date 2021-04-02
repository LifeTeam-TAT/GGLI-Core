package org.ace.insurance.web;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.claim.LifeClaim;
import org.ace.insurance.life.claim.LifeClaimBeneficiary;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeClaimSurvey;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimService;
import org.ace.insurance.life.claim.service.interfaces.ILifeDisabilityClaimService;
import org.ace.insurance.life.paidUp.LifePaidUpProposal;
import org.ace.insurance.life.paidUp.service.interfaces.ILifePaidUpProposalService;
import org.ace.insurance.life.premium.LifePolicyBilling;
import org.ace.insurance.life.premium.service.interfaces.ILifePolicyBillingService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDTO;
import org.ace.insurance.life.snakebite.service.interfaces.ISnakeBitePolicyService;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.service.interfaces.ILifeSurrenderProposalService;
import org.ace.insurance.medical.claim.MedicalClaim;
import org.ace.insurance.medical.claim.MedicalClaimProposal;
import org.ace.insurance.medical.claim.MedicalClaimRole;
import org.ace.insurance.medical.claim.OperationClaim;
import org.ace.insurance.medical.claim.OperationType;
import org.ace.insurance.medical.claim.service.interfaces.IMedicalClaimProposalService;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalSurvey;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.proxy.CIN001;
import org.ace.insurance.proxy.CIN002;
import org.ace.insurance.proxy.GF001;
import org.ace.insurance.proxy.LCLD001;
import org.ace.insurance.proxy.LCP001;
import org.ace.insurance.proxy.LIF001;
import org.ace.insurance.proxy.LPP001;
import org.ace.insurance.proxy.LSP001;
import org.ace.insurance.proxy.MCL001;
import org.ace.insurance.proxy.MED001;
import org.ace.insurance.proxy.MEDCLM002;
import org.ace.insurance.proxy.MEDICAL002;
import org.ace.insurance.proxy.TRA001;
import org.ace.insurance.proxy.WF001;
import org.ace.insurance.proxy.WorkflowCriteria;
import org.ace.insurance.proxy.service.interfaces.IProxyService;
import org.ace.insurance.system.configuration.SettingKeys;
import org.ace.insurance.system.configuration.interfaces.ISettingVariableService;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.insurance.web.manage.agent.payment.StaffCommissionDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.web.manage.medical.claim.factory.MedicalClaimProposalFactory;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.insurance.web.manage.medical.survey.MedicalSurveyDTO;
import org.ace.insurance.web.manage.medical.survey.factory.MedicalSurveyDTOFactory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

/**
 * @author PPA
 *
 */
@ViewScoped
@ManagedBean(name = "DashBoard")
public class DashBoard extends BaseBean {
	private static final String MESSAGE_PARAM_SUFFIX = "_PARAM";
	private static final String MESSAGE_EXCEL_PARAM_SUFFIX = "_EXL_PARAM";
	private static final String MESSAGE_REQUEST_PARAM_SUFFIX = "_REQUEST_PARAM";

	@ManagedProperty(value = "#{ProxyService}")
	private IProxyService proxyService;

	public void setProxyService(IProxyService proxyService) {
		this.proxyService = proxyService;
	}

	@ManagedProperty(value = "#{UserProcessService}")
	private IUserProcessService userProcessService;

	public void setUserProcessService(IUserProcessService userProcessService) {
		this.userProcessService = userProcessService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{SnakeBitePolicyService}")
	private ISnakeBitePolicyService snakeBitePolicyService;

	public void setSnakeBitePolicyService(ISnakeBitePolicyService snakeBitePolicyService) {
		this.snakeBitePolicyService = snakeBitePolicyService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{LifeClaimService}")
	private ILifeClaimService claimService;

	public void setClaimService(ILifeClaimService claimService) {
		this.claimService = claimService;
	}

	@ManagedProperty(value = "#{LifeDisabilityClaimService}")
	private ILifeDisabilityClaimService disabilityClaimService;

	public void setDisabilityClaimService(ILifeDisabilityClaimService disabilityClaimService) {
		this.disabilityClaimService = disabilityClaimService;
	}

	@ManagedProperty(value = "#{LifePolicyBillingService}")
	private ILifePolicyBillingService lifePolicyBillingService;

	public void setLifePolicyBillingService(ILifePolicyBillingService lifePolicyBillingService) {
		this.lifePolicyBillingService = lifePolicyBillingService;
	}

	@ManagedProperty(value = "#{CoinsuranceService}")
	private ICoinsuranceService coinsuranceService;

	public void setCoinsuranceService(ICoinsuranceService coinsuranceService) {
		this.coinsuranceService = coinsuranceService;
	}

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	@ManagedProperty(value = "#{MedicalClaimProposalService}")
	private IMedicalClaimProposalService medicalClaimProposalService;

	public void setMedicalClaimProposalService(IMedicalClaimProposalService medicalClaimProposalService) {
		this.medicalClaimProposalService = medicalClaimProposalService;
	}

	@ManagedProperty(value = "#{LifeSurrenderProposalService}")
	private ILifeSurrenderProposalService surrenderProposalService;

	public void setSurrenderProposalService(ILifeSurrenderProposalService surrenderProposalService) {
		this.surrenderProposalService = surrenderProposalService;
	}

	@ManagedProperty(value = "#{LifePaidUpProposalService}")
	private ILifePaidUpProposalService lifePaidUpProposalService;

	public void setLifePaidUpProposalService(ILifePaidUpProposalService lifePaidUpProposalService) {
		this.lifePaidUpProposalService = lifePaidUpProposalService;
	}

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	/**
	 * @param personTravelProposalService
	 *            the personTravelProposalService to set
	 */
	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	@ManagedProperty(value = "#{SettingVariableService}")
	private ISettingVariableService settingvariableService;

	public void setSettingvariableService(ISettingVariableService settingvariableService) {
		this.settingvariableService = settingvariableService;
	}

	@ManagedProperty(value = "#{GroupMicroHealthService}")
	private IGroupMicroHealthService groupMicroHealthService;

	public void setGroupMicroHealthService(IGroupMicroHealthService groupMicroHealthService) {
		this.groupMicroHealthService = groupMicroHealthService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
	}

	private User user;

	private MedicalClaimProposal claimProposal;
	private MedicalClaimProposalDTO claimProposalDTO;
	private LifeClaim lifeDisabilityClaim;
	private Coinsurance coinsurance;
	private LifeClaim lifeClaim;
	private LifeClaimBeneficiary lifeClaimBeneficiary;
	private LifeProposal lifeProposal;
	private LifeSurvey lifeSurvey;
	private GroupFarmerProposal groupFarmerProposal;
	private TravelProposal travelProposal;
	private LifeSurrenderProposal surrenderProposal;
	private LifePaidUpProposal paidUpProposal;

	private List<LifePolicyBilling> lifePolicyBillingList;
	private List<WF001> userWorkFlows;

	/****************************************
	 * DashBoardCount
	 ********************************/
	/**** MedicalPrposal UnderWriting ***/
	private long medicalSurveyCount;
	private long medicalApprovalCount;
	private long medicalInformCount;
	private long medicalPaymentCount;
	private long medicalConfirmationCount;
	private long medicalIssueCount;

	/**** MedicalPrposal Renewal ***/
	private long medicalRenewalSurveyCount;
	private long medicalRenewalApprovalCount;
	private long medicalRenewalInformCount;
	private long medicalRenewalPaymentCount;
	private long medicalRenewalConfirmationCount;
	private long medicalRenewalIssueCount;

	/********************************
	 * for Hospitalized Claim
	 *******************************************/
	private long medicalClaimSurveyCount;
	private long medicalClaimApproveCount;
	private long medicalClaimInformCount;
	private long medicalClaimPaymentCount;
	private long medicalClaimConfirmCount;

	private long medicalProposalSurveyCount;
	private long medicalProposalApproveCount;

	/**** MotorProposal UnderWriting ***/
	/**** FireProposal UnderWriting ***/
	/**** LifeProposal UnderWriting ***/
	private long lifeSurveyCount;
	private long lifeApprovalCount;
	private long lifeConfirmationCount;
	private long lifeInformCount;
	private long lifePaymentCount;
	private long lifeIssueCount;
	/**** FidelityProposal UnderWriting ***/
	/**** CashInSafeProposal UnderWriting ***/
	/**** CashInTransitProposal UnderWriting ***/
	/**** CashInTransit Claim ****/
	/**** SnakeBitePolicy UnderWriting ***/
	private long snakeBitePaymentCount;
	/**** MotorProposal Endorsement ***/
	/**** Terminate Motor Proposal ***/
	/**** FireProposal Endorsement ***/
	/**** LifeProposal Endorsement ***/
	private long lifeEndorseSurveyCount;
	private long lifeEndorseApprovalCount;
	private long lifeEndorseConfirmationCount;
	private long lifeEndorsePaymentCount;
	private long lifeEndorseIssueCount;
	/**** GroupLifeProposal Endorsement ***/
	private long lifeRenewalSurveyCount;
	private long lifeRenewalApprovalCount;
	private long lifeRenewalInformCount;
	private long lifeRenewalConfirmationCount;
	private long lifeRenewalPaymentCount;
	private long lifeRenewalIssueCount;
	/**** CargoProposal Endorsement ***/

	/**** ShortTerm Endowment Endorsement ***/
	private long shortEndowmentEndorsementSurveyCount;
	private long shortEndowmentEndorsementApprovalCount;
	private long shortEndowmentEndorsementConfirmationCount;
	private long shortEndowmentEndorsementInformCount;
	private long shortEndowmentEndorsementPaymentCount;
	private long shortEndowmentEndorsementIssueCount;

	/**** MotorProposal Renewal ***/
	/**** FireProposal Renewal ***/
	/*** Damaged Vehicle(Motor Claim) ***/
	/*** WindScreen (Motor Claim) ***/
	/*** Towing (Motor Claim) ***/
	/*** Person Casualty (Motor Claim) ***/
	private long personCasualtySurveyCount;
	private long personCasualtyApprovalCount;
	private long personCasualtyConfirmationCount;
	private long personCasualtyInformCount;
	private long personCasualtyPaymentCount;
	/*** ThirdPartyProperty (Motor Claim) ***/
	private long thirdPartySurveyCount;
	private long thirdPartyApprovalCount;
	private long thirdPartyConfirmationCount;
	private long thirdPartyInformCount;
	private long thirdPartyPaymentCount;
	/*** Medical Expenses (Motor Claim) ***/
	private long medicalExpenseSurveyCount;
	private long medicalExpenseApprovalCount;
	private long medicalExpenseConfirmationCount;
	private long medicalExpenseInformCount;
	private long medicalExpensePaymentCount;
	/*** Cargo Claim **/
	/*** Fire Claim **/
	/*** Life Claim **/
	private long lifeClaimRequestCount;
	private long lifeClaimApprovalCount;
	private long lifeClaimConfirmationCount;
	private long lifeClaimInformCount;
	private long lifeClaimPaymentCount;
	private long lifeClaimSurveyCount;

	/*** Life Disability Claim **/
	private long lifeDisabilityClaimRequestCount;
	private long lifeDisabilityClaimApprovalCount;
	private long lifeDisabilityClaimConfirmationCount;
	private long lifeDisabilityClaimInformCount;
	private long lifeDisabilityClaimPaymentCount;
	/*** Co_insurance **/
	private long coinsuranceFromConfirmationCount;
	private long coinsuranceFromPaymentCount;
	private long coinsuranceToPaymentCount;

	/*** Agent Commission **/
	private long agentCommissionPaymentCount;
	private long staffCommissionPaymentCount;

	/*** Travel Insurance ***/
	private long travelProposalConfirmationCount;
	private long travelProposalPaymentCount;

	/***** CargoPrposal UnderWriting ***/

	/***** CashInSafe Claim ***/

	/***** Life Surrender Proposal *********/
	private long lifeSurrenderApprovalCount;
	private long lifeSurrenderConfirmationCount;
	private long lifeSurrenderInformCount;
	private long lifeSurrenderPaymentCount;
	private long lifeSurrenderIssueCount;

	/***** Life Surrender Proposal *********/
	private long shortTermLifeSurrenderApprovalCount;
	private long shortTermLifeSurrenderConfirmationCount;
	private long shortTermLifeSurrenderInformCount;
	private long shortTermLifeSurrenderPaymentCount;
	private long shortTermLifeSurrenderIssueCount;

	/***** Life PaidUp Proposal *********/
	private long lifePaidUpApprovalCount;
	private long lifePaidUpConfirmationCount;
	private long lifePaidUpInformCount;

	/***** ShortTerm Life PaidUp Proposal ******/
	private long shortTermLifePaidUpApprovalCount;
	private long shortTermLifePaidUpConfirmationCount;
	private long shortTermLifePaidUpInformCount;

	/***** Personal Accident Proposal *********/
	private long paSurveyCount;
	private long paApprovalCount;
	private long paInformCount;
	private long paConfirmationCount;
	private long paPaymentCount;
	private long paIssueCount;

	/***** Personal Travel Proposal *********/
	private long personTravelConfirmationCount;
	private long personTravelPaymentCount;
	private long personTravelIssueCount;

	/**** Farmer UnderWriting ***/
	private long farmerSurveyCount;
	private long farmerApprovalCount;
	private long farmerConfirmationCount;
	private long farmerInformCount;
	private long farmerPaymentCount;
	private long farmerIssueCount;

	/**** ShortTerm Endowment UnderWriting ***/
	private long shortEndowmentSurveyCount;
	private long shortEndowmentApprovalCount;
	private long shortEndowmentConfirmationCount;
	private long shortEndowmentInformCount;
	private long shortEndowmentPaymentCount;
	private long shortEndowmentIssueCount;

	/**** Student Life UnderWriting ***/
	private long studentLifeSurveyCount;
	private long studentLifeApprovalCount;
	private long studentLifeConfirmationCount;
	private long studentLifeInformCount;
	private long studentLifePaymentCount;
	private long studentLifeIssueCount;

	/******************************************************************
	 * List***********************************
	 **************/
	private List<MEDCLM002> medicalClaimApproveList;
	private List<MEDCLM002> medicalClaimInformList;
	private List<MEDCLM002> medicalClaimPaymentList;
	private List<MEDCLM002> medicalClaimSurveyList;
	private List<MEDCLM002> medicalClaimConfirmList;

	private List<MED001> medicalIssueList;
	private List<MED001> medicalSurveyList;
	private List<MED001> medicalApprovalList;
	private List<MED001> medicalPaymentList;
	private List<MED001> medicalConfirmationList;
	private List<MED001> medicalInformList;

	private List<MED001> medicalRenewalSurveyList;
	private List<MED001> medicalRenewalInformList;
	private List<MED001> medicalRenewalApprovalList;
	private List<MED001> medicalRenewalConfirmationList;
	private List<MED001> medicalRenewalPaymentList;
	private List<MED001> medicalRenewalIssueList;

	/**** MotorProposal UnderWriting ***/
	/**** FireProposal UnderWriting ***/
	/**** LifeProposal UnderWriting ***/
	private List<LIF001> lifeSurveyList;
	private List<LIF001> lifeApprovalList;
	private List<LIF001> lifeConfirmationList;
	private List<LIF001> lifeInformList;
	private List<LIF001> lifePaymentList;
	private List<LIF001> lifeIssueList;
	/**** FidelityProposal UnderWriting ***/
	/**** CashInSafeProposal UnderWriting ***/
	/**** CashInTransitProposal UnderWriting ***/
	/**** SnakeBitePolicy UnderWriting ***/
	private List<SnakeBitePolicyDTO> snakeBitePaymentList;
	/**** CargoProposal Endorsement ***/
	/**** MotorProposal Endorsement ***/
	/**** MotorProposal Endorsement ***/
	private List<LIF001> shortTermEndowmentEndorseSurveyList;
	private List<LIF001> shortTermEndowmentEndorseApprovalList;
	private List<LIF001> shortTermEndowmentEndorseInformList;
	private List<LIF001> shortTermEndowmentEndorseConfirmationList;
	private List<LIF001> shortTermEndowmentEndorsePaymentList;
	private List<LIF001> shortTermEndowmentEndorseIssueList;
	/**** Terminate Motor Proposal ***/
	/**** FireProposal Endorsement ***/
	/**** LifeProposal Endorsement ***/
	private List<LIF001> lifeEndorseSurveyList;
	private List<LIF001> lifeEndorseApprovalList;
	private List<LIF001> lifeEndorseConfirmationList;
	private List<LIF001> lifeEndorsePaymentList;
	private List<LIF001> lifeEndorseIssueList;

	/**** GroupLifeProposal Renewal ***/
	private List<LIF001> lifeRenewalSurveyList;
	private List<LIF001> lifeRenewalApprovalList;
	private List<LIF001> lifeRenewalInformList;
	private List<LIF001> lifeRenewalConfirmationList;
	private List<LIF001> lifeRenewalPaymentList;
	private List<LIF001> lifeRenewalIssueList;
	/**** MotorProposal Renewal ***/
	/**** FireProposal Renewal ***/
	/**** Damaged Vehicle (Motor Claim) ***/
	/**** WindScreen (Motor Claim) ***/
	/**** Towing ( Motor Claim) ***/
	/*** ThirdPartyProperty (Motor Claim) ***/
	private List<MCL001> thirdPartySurveyList;
	private List<MCL001> thirdPartyApprovalList;
	private List<MCL001> thirdPartyConfirmationList;
	private List<MCL001> thirdPartyInformList;
	private List<MCL001> thirdPartyPaymentList;
	/*** PresonCasualty (Motor Claim) ***/
	private List<MCL001> personCasualtySurveyList;
	private List<MCL001> personCasualtyApprovalList;
	private List<MCL001> personCasualtyConfirmationList;
	private List<MCL001> personCasualtyInformList;
	private List<MCL001> personCasualtyPaymentList;
	/*** MedicalExpense (Motor Claim) ***/
	private List<MCL001> medicalExpenseSurveyList;
	private List<MCL001> medicalExpenseApprovalList;
	private List<MCL001> medicalExpenseConfirmationList;
	private List<MCL001> medicalExpenseInformList;
	private List<MCL001> medicalExpensePaymentList;
	/*** Cargo Claim ***/
	/*** Fire Claim ***/
	/*** Life Claim ***/
	private List<LCLD001> lifeClaimRequestList;
	// private List<LCLD001> lifeClaimApprovalList;
	// private List<LCLD001> lifeClaimConfirmationList;
	// private List<LCLD001> lifeClaimInformList;
	// private List<LCB001> lifeClaimPaymentList;

	private List<LCP001> lifeClaimSurveyList;
	private List<LCP001> lifeClaimApprovalList;
	private List<LCP001> lifeClaimInformList;
	private List<LCP001> lifeClaimConfirmationList;
	private List<LCP001> lifeClaimPaymentList;

	/*** Life Claim ***/
	private List<LCLD001> lifeDisabilityClaimRequestList;
	private List<LCLD001> lifeDisabilityClaimApprovalList;
	private List<LCLD001> lifeDisabilityClaimConfirmationList;
	private List<LCLD001> lifeDisabilityClaimInformList;
	private List<LCLD001> lifeDisabilityClaimPaymentList;
	/*** CashInTransit Claim ***/
	/*** CashInSafe Claim ***/
	/*** Co_insurance ***/
	private List<CIN001> coinsuranceFromConfirmationList;
	private List<CIN001> coinsuranceFromPaymentList;
	private List<CIN002> coinsuranceToPaymentList;
	/*** AgentCommission ***/
	private List<AgentCommissionDTO> agentCommissionPaymentList;
	private List<StaffCommissionDTO> staffCommissionPaymentList;
	/** Travel Insurance ***/
	private List<TRA001> travelProposalConfirmationList;
	private List<TRA001> travelProposalPaymentList;

	private MedicalProposal medicalProposal;
	private MedicalSurveyDTO medicalSurveyDTO;

	/* Life Surrender */
	private List<LSP001> lifeSurrenderApprovalList;
	private List<LSP001> lifeSurrenderConfirmationList;
	private List<LSP001> lifeSurrenderInformList;
	private List<LSP001> lifeSurrenderPaymentList;
	private List<LSP001> lifeSurrenderIssueList;

	/* Short Term Life Surrender */
	private List<LSP001> shortTermLifeSurrenderApprovalList;
	private List<LSP001> shortTermLifeSurrenderConfirmationList;
	private List<LSP001> shortTermLifeSurrenderInformList;
	private List<LSP001> shortTermLifeSurrenderPaymentList;
	private List<LSP001> shortTermLifeSurrenderIssueList;

	/* Life PaidUp */
	private List<LPP001> lifePaidUpApprovalList;
	private List<LPP001> lifePaidUpConfirmationList;
	private List<LPP001> lifePaidUpInformList;

	/* ShortTerm Life PaidUp */
	private List<LPP001> ShortTermlifePaidUpApprovalList;
	private List<LPP001> ShortTermlifePaidUpConfirmationList;
	private List<LPP001> ShortTermlifePaidUpInformList;

	/* Personal Accident */
	private List<LIF001> paSurveyList;
	private List<LIF001> paApprovalList;
	private List<LIF001> paConfirmationList;
	private List<LIF001> paInformList;
	private List<LIF001> paPaymentList;
	private List<LIF001> paIssueList;

	/* Person Travel */
	private List<TRA001> personTravelConfirmationList;
	private List<TRA001> personTravelPaymentList;
	private List<TRA001> personTravelIssueList;

	/**** Farmer UnderWriting ***/
	private List<LIF001> farmerSurveyList;
	private List<LIF001> farmerApprovalList;
	private List<LIF001> farmerConfirmationList;
	private List<LIF001> farmerInformList;
	private List<LIF001> farmerPaymentList;
	private List<LIF001> farmerIssueList;

	/**** ShortTerm Endowment UnderWriting ***/
	private List<LIF001> shortEndowmentSurveyList;
	private List<LIF001> shortEndowmentApprovalList;
	private List<LIF001> shortEndowmentInformList;
	private List<LIF001> shortEndowmentConfirmationList;
	private List<LIF001> shortEndowmentPaymentList;
	private List<LIF001> shortEndowmentIssueList;

	/*** CRITICAL_ILLNESS_PROPOSAL ***/
	private long criticalIllnessSurveyCount;
	private long criticalIllnessApprovalCount;
	private long criticalIllnessInformCount;
	private long criticalIllnessConfirmationCount;
	private long criticalIllnessPaymentCount;
	private long criticalIllnessIssueCount;

	/*** HEALTH_PROPOSAL ***/
	private long healthSurveyCount;
	private long healthApprovalCount;
	private long healthInformCount;
	private long healthConfirmationCount;
	private long healthPaymentCount;
	private long healthIssueCount;

	/*** MICRO_HEALTH_PROPOSAL ***/
	private long microHealthSurveyCount;
	private long microHealthApprovalCount;
	private long microHealthInformCount;
	private long microHealthConfirmationCount;
	private long microHealthPaymentCount;
	private long microHealthIssueCount;

	private long groupMicroHealthApprovalCount;
	private long groupMicroHealthConfirmationCount;
	private long groupMicroHealthPaymentCount;

	/**** Health UnderWriting ***/
	private List<MEDICAL002> healthSurveyList;
	private List<MEDICAL002> healthApprovalList;
	private List<MEDICAL002> healthInformList;
	private List<MEDICAL002> healthConfirmationList;
	private List<MEDICAL002> healthPaymentList;
	private List<MEDICAL002> healthIssueList;

	/**** Micro Health UnderWriting ***/
	private List<MEDICAL002> microHealthSurveyList;
	private List<MEDICAL002> microHealthApprovalList;
	private List<MEDICAL002> microHealthInformList;
	private List<MEDICAL002> microHealthConfirmationList;
	private List<MEDICAL002> microHealthPaymentList;
	private List<MEDICAL002> microHealthIssueList;
	private List<GroupMicroHealth> groupMicroHealthApprovalList;
	private List<GroupMicroHealth> groupMicroHealthConfirmationList;
	private List<GroupMicroHealth> groupMicroHealthPaymentList;

	/**** CriticalIllness UnderWriting ***/
	private List<MEDICAL002> criticalIllnessSurveyList;
	private List<MEDICAL002> criticalIllnessApprovalList;
	private List<MEDICAL002> criticalIllnessInformList;
	private List<MEDICAL002> criticalIllnessConfirmationList;
	private List<MEDICAL002> criticalIllnessPaymentList;
	private List<MEDICAL002> criticalIllnessIssueList;

	/*** Group Farmer ***/
	private long groupFarmerConfirmCount;
	private long groupFarmerPaymentCount;

	private List<GF001> groupFarmerPaymentList;
	private List<GF001> groupFarmerConfirmationList;

	private MedicalProposal healthMedicalProposal;
	private MedicalSurvey medicalSurvey;

	/** Student Life ***/
	private List<LIF001> studentLifeSurveyList;
	private List<LIF001> studentLifeApprovalList;
	private List<LIF001> studentLifeConfirmationList;
	private List<LIF001> studentLifeInformList;
	private List<LIF001> studentLifePaymentList;
	private List<LIF001> studentLifeIssueList;

	/* Public Term life */
	private List<LIF001> publicTermlifeSurveyList;
	private List<LIF001> publicTermlifeApprovalList;
	private List<LIF001> publicTermlifeConfirmationList;
	private List<LIF001> publicTermlifeInformList;
	private List<LIF001> publicTermlifePaymentList;
	private List<LIF001> publicTermlifeIssueList;

	/* Single Premium Endowment life */
	private List<LIF001> singlePremiumEndowmentlifeSurveyList;
	private List<LIF001> singlePremiumEndowmentlifeApprovalList;
	private List<LIF001> singlePremiumEndowmentlifeConfirmationList;
	private List<LIF001> singlePremiumEndowmentlifeInformList;
	private List<LIF001> singlePremiumEndowmentlifePaymentList;
	private List<LIF001> singlePremiumEndowmentlifeIssueList;

	/* For publicTerm LIfe Count */
	private long publicTermlifeSurveyCount;
	private long publicTermlifeApprovalCount;
	private long publicTermlifeConfirmationCount;
	private long publicTermlifeInformCount;
	private long publicTermlifePaymentCount;
	private long publicTermlifeIssueCount;

	/* For Single Premium Endowment Count */
	private long singlePremiumEndowmentlifeSurveyCount;
	private long singlePremiumEndowmentlifeApprovalCount;
	private long singlePremiumEndowmentlifeConfirmationCount;
	private long singlePremiumEndowmentlifeInformCount;
	private long singlePremiumEndowmentlifePaymentCount;
	private long singlePremiumEndowmentlifeIssueCount;

	/* Single Premium Credit life */
	private List<LIF001> singlePremiumCreditlifeSurveyList;
	private List<LIF001> singlePremiumCreditlifeApprovalList;
	private List<LIF001> singlePremiumCreditlifeConfirmationList;
	private List<LIF001> singlePremiumCreditlifeInformList;
	private List<LIF001> singlePremiumCreditlifePaymentList;
	private List<LIF001> singlePremiumCreditlifeIssueList;

	/* For Single Premium Credit Count */
	private long singlePremiumCreditlifeSurveyCount;
	private long singlePremiumCreditlifeApprovalCount;
	private long singlePremiumCreditlifeConfirmationCount;
	private long singlePremiumCreditlifeInformCount;
	private long singlePremiumCreditlifePaymentCount;
	private long singlePremiumCreditlifeIssueCount;

	/* Short Term Single Premium Credit life */
	private List<LIF001> shortTermSinglePremiumCreditlifeSurveyList;
	private List<LIF001> shortTermSinglePremiumCreditlifeApprovalList;
	private List<LIF001> shortTermSinglePremiumCreditlifeConfirmationList;
	private List<LIF001> shortTermSinglePremiumCreditlifeInformList;
	private List<LIF001> shortTermSinglePremiumCreditlifePaymentList;
	private List<LIF001> shortTermSinglePremiumCreditlifeIssueList;

	/* For Single Premium Credit Count */
	private long shortTermSinglePremiumCreditlifeSurveyCount;
	private long shortTermSinglePremiumCreditlifeApprovalCount;
	private long shortTermSinglePremiumCreditlifeConfirmationCount;
	private long shortTermSinglePremiumCreditlifeInformCount;
	private long shortTermSinglePremiumCreditlifePaymentCount;
	private long shortTermSinglePremiumCreditlifeIssueCount;

	/* Single life */
	private List<LIF001> simplelifeSurveyList;
	private List<LIF001> simplelifeApprovalList;
	private List<LIF001> simplelifeConfirmationList;
	private List<LIF001> simplelifeInformList;
	private List<LIF001> simplelifePaymentList;
	private List<LIF001> simplelifeIssueList;
	
	
	/* For Single life Count */
	private long simplelifeSurveyCount;
	private long simplelifeApprovalCount;
	private long simplelifeConfirmationCount;
	private long simplelifeInformCount;
	private long simplelifePaymentCount;
	private long simplelifeIssueCount;
	
	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		clearCount();
		loadWorkflowCountByUser();
		refreshList();

	}

	private boolean loadOldHealthRedirect() {
		boolean result = false;
		try {
			result = settingvariableService.getSettingVariable(SettingKeys.Medical.IS_OLD_HEALTH_DISABLE, false);
		} catch (SystemException e) {
			handelSysException(e);
		}
		return result;
	}

	public String prepareMedicalRenewalSurvey(String id) {
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		outjectMedicalProposal(changeProDataToProDTO(medicalProposal));
		MedicalSurvey medicalSurvey = new MedicalSurvey();
		medicalSurvey.setMedicalProposal(medicalProposal);
		medicalSurveyDTO = MedicalSurveyDTOFactory.getMedicalSurveyDTO(medicalSurvey);
		outjectMedicalSurvey(medicalSurveyDTO);
		return "medicalRenewalSurvey";
	}

	private MedicalClaimProposalDTO changeMedicalClaimProposalDataToClaimDTO(MedicalClaimProposal medicalClaimProposal) {
		MedicalClaimProposalDTO medicalClaimProposalDTO = MedicalClaimProposalFactory.createMedicalClaimProposalDTO(medicalClaimProposal);
		return medicalClaimProposalDTO;
	}

	/********************************************************
	 * Outjection Method
	 ****************************************************/

	private void outjectMedicalClaimProposal(MedicalClaimProposalDTO medicalClaimProposal) {
		putParam("medicalClaimProposal", medicalClaimProposal);
	}

	private void outjectHealthProposal(MedicalProposal medicalProposal) {
		putParam("medicalProposal", medicalProposal);
	}

	private void outjectHealthSurvey(MedicalSurvey medicalSurvey) {
		putParam("medicalSurvey", medicalSurvey);
	}

	/*********************************
	 * prepare method for Medical claim
	 **************************************/
	public String prepareMedicalClaimSurvey(String id) {
		claimProposal = medicalClaimProposalService.findMedicalClaimProposalById(id);
		claimProposalDTO = changeMedicalClaimProposalDataToClaimDTO(claimProposal);
		for (MedicalClaim mc : claimProposal.getMedicalClaimList()) {
			if (mc.getClaimRole().equals(MedicalClaimRole.OPERATION_CLAIM)) {
				if (((OperationClaim) mc).getOperationType().equals(OperationType.OPERATION)) {
					claimProposalDTO.setOperationClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				} else {
					claimProposalDTO.setAbortionClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				}
			} else if (mc.getClaimRole().equals(MedicalClaimRole.DEATH_CLAIM)) {
				claimProposalDTO.setDeathClaimDTO(medicalClaimProposalService.findDeathClaimById(mc.getId()));
			} else if (mc.getClaimRole().equals(MedicalClaimRole.HOSPITALIZED_CLAIM)) {
				claimProposalDTO.setHospitalizedClaimDTO(medicalClaimProposalService.findHospitalizedClaimById(mc.getId()));
			} else if (mc.getClaimRole().equals(MedicalClaimRole.MEDICATION_CLAIM)) {
				claimProposalDTO.setMedicationClaimDTO(medicalClaimProposalService.findMedicationClaimById(mc.getId()));
			}
		}
		outjectMedicalClaimProposal(claimProposalDTO);
		return "medicalClaimSurvey";
	}

	/*********************************
	 * prepare method for medical claim
	 **************************************/
	public String prepareMedicalClaimApprove(String id) {
		claimProposal = medicalClaimProposalService.findMedicalClaimProposalById(id);
		claimProposalDTO = changeMedicalClaimProposalDataToClaimDTO(claimProposal);
		for (MedicalClaim mc : claimProposal.getMedicalClaimList()) {
			if (mc.getClaimRole().equals(MedicalClaimRole.DEATH_CLAIM)) {
				claimProposalDTO.setDeathClaimDTO(medicalClaimProposalService.findDeathClaimById(mc.getId()));
			} else if (mc.getClaimRole().equals(MedicalClaimRole.HOSPITALIZED_CLAIM)) {
				claimProposalDTO.setHospitalizedClaimDTO(medicalClaimProposalService.findHospitalizedClaimById(mc.getId()));
			} else if (mc.getClaimRole().equals(MedicalClaimRole.MEDICATION_CLAIM)) {
				claimProposalDTO.setMedicationClaimDTO(medicalClaimProposalService.findMedicationClaimById(mc.getId()));
			} else {
				if (OperationType.OPERATION.equals(((OperationClaim) mc).getOperationType())) {
					claimProposalDTO.setOperationClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				} else {
					claimProposalDTO.setAbortionClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				}
			}
		}

		outjectMedicalClaimProposal(claimProposalDTO);
		return "medicalClaimApprove";
	}

	public String prepareMedicalClaimInform(String id) {
		claimProposal = medicalClaimProposalService.findMedicalClaimProposalById(id);
		claimProposalDTO = changeMedicalClaimProposalDataToClaimDTO(claimProposal);
		for (MedicalClaim mc : claimProposal.getMedicalClaimList()) {
			if (mc.getClaimRole().equals(MedicalClaimRole.OPERATION_CLAIM)) {
				if (((OperationClaim) mc).getOperationType().equals(OperationType.OPERATION)) {
					claimProposalDTO.setOperationClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				} else {
					claimProposalDTO.setAbortionClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				}
			} else if (mc.getClaimRole().equals(MedicalClaimRole.DEATH_CLAIM)) {
				claimProposalDTO.setDeathClaimDTO(medicalClaimProposalService.findDeathClaimById(mc.getId()));
			} else if (mc.getClaimRole().equals(MedicalClaimRole.HOSPITALIZED_CLAIM)) {
				claimProposalDTO.setHospitalizedClaimDTO(medicalClaimProposalService.findHospitalizedClaimById(mc.getId()));
			} else {
				claimProposalDTO.setMedicationClaimDTO(medicalClaimProposalService.findMedicationClaimById(mc.getId()));
			}
		}

		outjectMedicalClaimProposal(claimProposalDTO);
		return "medicalClaimInform";
	}

	public String prepareMedicalClaimConfirm(String id) {
		claimProposal = medicalClaimProposalService.findMedicalClaimProposalById(id);
		claimProposalDTO = changeMedicalClaimProposalDataToClaimDTO(claimProposal);
		for (MedicalClaim mc : claimProposal.getMedicalClaimList()) {
			if (mc.getClaimRole().equals(MedicalClaimRole.DEATH_CLAIM)) {
				claimProposalDTO.setDeathClaimDTO(medicalClaimProposalService.findDeathClaimById(mc.getId()));
			} else if (mc.getClaimRole().equals(MedicalClaimRole.OPERATION_CLAIM)) {
				if (((OperationClaim) mc).getOperationType().equals(OperationType.OPERATION)) {
					claimProposalDTO.setOperationClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				} else {
					claimProposalDTO.setAbortionClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				}
			} else if (mc.getClaimRole().equals(MedicalClaimRole.HOSPITALIZED_CLAIM)) {
				claimProposalDTO.setHospitalizedClaimDTO(medicalClaimProposalService.findHospitalizedClaimById(mc.getId()));
			} else {
				claimProposalDTO.setMedicationClaimDTO(medicalClaimProposalService.findMedicationClaimById(mc.getId()));
			}
		}
		outjectMedicalClaimProposal(claimProposalDTO);
		return "medicalClaimConfirm";
	}

	public String prepareMedicalClaimPayment(String id) {
		claimProposal = medicalClaimProposalService.findMedicalClaimProposalById(id);
		claimProposalDTO = changeMedicalClaimProposalDataToClaimDTO(claimProposal);
		for (MedicalClaim mc : claimProposal.getMedicalClaimList()) {
			if (mc.getClaimRole().equals(MedicalClaimRole.OPERATION_CLAIM)) {
				if (((OperationClaim) mc).getOperationType().equals(OperationType.OPERATION)) {
					claimProposalDTO.setOperationClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				} else {
					claimProposalDTO.setAbortionClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
				}
			} else if (mc.getClaimRole().equals(MedicalClaimRole.DEATH_CLAIM)) {
				claimProposalDTO.setDeathClaimDTO(medicalClaimProposalService.findDeathClaimById(mc.getId()));
			} else if (mc.getClaimRole().equals(MedicalClaimRole.HOSPITALIZED_CLAIM)) {
				claimProposalDTO.setHospitalizedClaimDTO(medicalClaimProposalService.findHospitalizedClaimById(mc.getId()));
			} else if (mc.getClaimRole().equals(MedicalClaimRole.MEDICATION_CLAIM)) {
				claimProposalDTO.setMedicationClaimDTO(medicalClaimProposalService.findMedicationClaimById(mc.getId()));
			}
		}

		outjectMedicalClaimProposal(claimProposalDTO);
		return "medicalClaimPayment";
	}

	public MedicalClaimProposal getClaimProposal() {
		return claimProposal;
	}

	public void setClaimProposal(MedicalClaimProposal claimProposal) {
		this.claimProposal = claimProposal;
	}

	public MedProDTO changeProDataToProDTO(MedicalProposal medicalProposal) {
		MedProDTO proposalDTO = MedicalProposalFactory.getMedProDTO(medicalProposal);
		return proposalDTO;

	}

	@SuppressWarnings("unchecked")
	public String prepareMedicalProposalApprove(String id) {

		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		HealthType healthType = null;
		String productId = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getProduct().getId();
		if (productId.equals(ProductIDConfig.getMicroHealthInsurance())) {
			healthType = HealthType.MICROHEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualHealthInsuranceId()) || productId.equals(ProductIDConfig.getGroupHealthInsuranceId())) {
			healthType = HealthType.HEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualCriticalIllness_Id()) || productId.equals(ProductIDConfig.getGroupCriticalIllness_Id())) {
			healthType = HealthType.CRITICALILLNESS;
		} else if (productId.equals(ProductIDConfig.getMedicalProductId())) {
			healthType = HealthType.MEDICAL_INSURANCE;
		}
		if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
			if (loadOldHealthRedirect()) {
				getSessionMap().put(Constants.MESSAGE_ID, MessageId.NOT_ALLOW_FOR_OLD_HEALTH);
				return null;
			}
		}
		clearSessionMap();
		outjectHealthType(healthType);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalApproval";
	}

	@SuppressWarnings("unchecked")
	public String prepareMedicalSurvey(String medicalProposalId) {

		medicalProposal = medicalProposalService.findMedicalProposalById(medicalProposalId);
		HealthType healthType = null;
		String productId = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getProduct().getId();
		if (productId.equals(ProductIDConfig.getMicroHealthInsurance())) {
			healthType = HealthType.MICROHEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualHealthInsuranceId()) || productId.equals(ProductIDConfig.getGroupHealthInsuranceId())) {
			healthType = HealthType.HEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualCriticalIllness_Id()) || productId.equals(ProductIDConfig.getGroupCriticalIllness_Id())) {
			healthType = HealthType.CRITICALILLNESS;
		} else if (productId.equals(ProductIDConfig.getMedicalProductId())) {
			healthType = HealthType.MEDICAL_INSURANCE;
		}
		if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
			if (loadOldHealthRedirect()) {
				getSessionMap().put(Constants.MESSAGE_ID, MessageId.NOT_ALLOW_FOR_OLD_HEALTH);
				return null;
			}
		}
		clearSessionMap();
		outjectHealthType(healthType);
		outjectMedicalProposal(changeProDataToProDTO(medicalProposal));
		MedicalSurvey medicalSurvey = new MedicalSurvey();
		medicalSurvey.setMedicalProposal(medicalProposal);
		medicalSurveyDTO = MedicalSurveyDTOFactory.getMedicalSurveyDTO(medicalSurvey);
		outjectMedicalSurvey(medicalSurveyDTO);
		return "medicalSurvey";
	}

	public String prepareMedicalProposalInform(String id) {

		HealthType healthType = null;
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		String productId = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getProduct().getId();
		if (productId.equals(ProductIDConfig.getMicroHealthInsurance())) {
			healthType = HealthType.MICROHEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualHealthInsuranceId()) || productId.equals(ProductIDConfig.getGroupHealthInsuranceId())) {
			healthType = HealthType.HEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualCriticalIllness_Id()) || productId.equals(ProductIDConfig.getGroupCriticalIllness_Id())) {
			healthType = HealthType.CRITICALILLNESS;
		} else if (productId.equals(ProductIDConfig.getMedicalProductId())) {
			healthType = HealthType.MEDICAL_INSURANCE;
		}
		if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
			if (loadOldHealthRedirect()) {
				getSessionMap().put(Constants.MESSAGE_ID, MessageId.NOT_ALLOW_FOR_OLD_HEALTH);
				return null;
			}
		}
		clearSessionMap();
		outjectHealthType(healthType);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalInform";

	}

	public String prepareMedicalProposalConfirmation(String id) {
		HealthType healthType = null;
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		String productId = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getProduct().getId();
		if (productId.equals(ProductIDConfig.getMicroHealthInsurance())) {
			healthType = HealthType.MICROHEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualHealthInsuranceId()) || productId.equals(ProductIDConfig.getGroupHealthInsuranceId())) {
			healthType = HealthType.HEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualCriticalIllness_Id()) || productId.equals(ProductIDConfig.getGroupCriticalIllness_Id())) {
			healthType = HealthType.CRITICALILLNESS;
		} else if (productId.equals(ProductIDConfig.getMedicalProductId())) {
			healthType = HealthType.MEDICAL_INSURANCE;
		}
		if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
			if (loadOldHealthRedirect()) {
				getSessionMap().put(Constants.MESSAGE_ID, MessageId.NOT_ALLOW_FOR_OLD_HEALTH);
				return null;
			}
		}
		clearSessionMap();
		outjectHealthType(healthType);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalConfirmation";
	}

	private void clearSessionMap() {
		if (getSessionMap().containsKey("medicalProposal")) {
			getSessionMap().remove("medicalProposal");
		}

	}

	public String prepareMedicalProposalPayment(String id) {

		HealthType healthType = null;
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		String productId = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getProduct().getId();
		if (productId.equals(ProductIDConfig.getMicroHealthInsurance())) {
			healthType = HealthType.MICROHEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualHealthInsuranceId()) || productId.equals(ProductIDConfig.getGroupHealthInsuranceId())) {
			healthType = HealthType.HEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualCriticalIllness_Id()) || productId.equals(ProductIDConfig.getGroupCriticalIllness_Id())) {
			healthType = HealthType.CRITICALILLNESS;
		} else if (productId.equals(ProductIDConfig.getMedicalProductId())) {
			healthType = HealthType.MEDICAL_INSURANCE;
			loadOldHealthRedirect();
		}
		if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
			if (loadOldHealthRedirect()) {
				getSessionMap().put(Constants.MESSAGE_ID, MessageId.NOT_ALLOW_FOR_OLD_HEALTH);
				return null;
			}
		}
		clearSessionMap();
		outjectHealthType(healthType);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalPayment";
	}

	public String prepareMedicalIssue(String id) {
		HealthType healthType = null;
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		String productId = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getProduct().getId();
		if (productId.equals(ProductIDConfig.getMicroHealthInsurance())) {
			healthType = HealthType.MICROHEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualHealthInsuranceId()) || productId.equals(ProductIDConfig.getGroupHealthInsuranceId())) {
			healthType = HealthType.HEALTH;
		} else if (productId.equals(ProductIDConfig.getIndividualCriticalIllness_Id()) || productId.equals(ProductIDConfig.getGroupCriticalIllness_Id())) {
			healthType = HealthType.CRITICALILLNESS;
		} else if (productId.equals(ProductIDConfig.getMedicalProductId())) {
			healthType = HealthType.MEDICAL_INSURANCE;
			loadOldHealthRedirect();
		}
		if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
			if (loadOldHealthRedirect()) {
				getSessionMap().put(Constants.MESSAGE_ID, MessageId.NOT_ALLOW_FOR_OLD_HEALTH);
				return null;
			}
		}
		clearSessionMap();
		outjectHealthType(healthType);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalIssue";

	}

	public String prepareGroupMicroHealthApprove(String id) {
		GroupMicroHealth groupMicroHealth = groupMicroHealthService.findById(id);
		clearSessionMap();
		outjectGroupMicroHealthObj(groupMicroHealth);
		return "groupMicroHealthApproval";
	}

	public String prepareGroupMicroHealthConfirm(String id) {
		GroupMicroHealth groupMicroHealth = groupMicroHealthService.findById(id);
		clearSessionMap();
		outjectGroupMicroHealthObj(groupMicroHealth);
		return "groupMicroHealthConfirm";
	}

	public String prepareGroupMicroHealthPayment(String id) {
		GroupMicroHealth groupMicroHealth = groupMicroHealthService.findById(id);
		clearSessionMap();
		outjectGroupMicroHealthObj(groupMicroHealth);
		return "groupMicroHealthPayment";
	}

	public String prepareMedicalRenewalProposalApprove(String id) {
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalRenewalApproval";
	}

	public String prepareMedicalRenewalProposalInform(String id) {
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalRenewalInform";
	}

	public String prepareMedicalRenewalProposalConfirmation(String id) {
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalRenewalConfirm";
	}

	public String prepareMedicalRenewalProposalPayment(String id) {
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalRenewalPayment";
	}

	public String prepareMedicalRenewalIssue(String id) {
		medicalProposal = medicalProposalService.findMedicalProposalById(id);
		outjectMedicalProposalObj(medicalProposal);
		return "medicalRenewalIssue";
	}

	private void outjectMedicalProposalObj(MedicalProposal medicalProposal) {
		putParam("medicalProposal", medicalProposal);
	}

	private void outjectGroupMicroHealthObj(GroupMicroHealth groupMicroHealth) {
		putParam("groupMicroHealth", groupMicroHealth);
	}

	/**************************
	 * for Hospitalized Claim
	 *******************************/

	public String getMedicalClaimSurvey() {
		return DashboardCategory.MED_CLAIM_SURVEY;
	}

	public String getMedicalClaimApprove() {
		return DashboardCategory.MED_CLAIM_APPROVE;
	}

	public String getMedicalClaimInfrom() {
		return DashboardCategory.MED_CLAIM_INFORM;
	}

	public String getMedicalClaimPayment() {
		return DashboardCategory.MED_CLAIM_PAYMENT;
	}

	public String getParamMenu() {
		return "medicalClaimRequest";

	}

	public String getMedicalClaimInitialReportMenu() {
		return "medicalClaimInitialReport";
	}

	public String prepareLifeRenewalSurvey(String lifeproposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeproposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "renewalGroupLifeSurvey";
	}

	public String prepareLifeRenewalInform(String lifeproposalId) {
		outjectLifeProposal(lifeproposalId);
		return "renewalGroupLifeInform";
	}

	public String prepareLifeRenewalConfirmation(String lifeproposalId) {
		outjectLifeProposal(lifeproposalId);
		return "renewalGroupLifeConfirm";
	}

	public String prepareLifeRenewalIssuing(String lifeproposalId) {
		outjectLifeProposal(lifeproposalId);
		return "renewalGroupLifeIssue";
	}

	public String prepareLifeRenewalPayment(String lifeproposalId) {
		outjectLifeProposal(lifeproposalId);
		return "renewalGroupLifePayment";
	}

	public String prepareLifeRenewalApproval(String lifeproposalId) {
		outjectLifeProposal(lifeproposalId);
		return "renewalGroupLifeApproval";
	}

	public String prepareLifeSurvey(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "lifeSurvey";
	}

	public String prepareLifeApproval(String lifeProposalId) {
		// outjectLifeProposal(lifeProposalId);
		putParam("lifeProposalId", lifeProposalId);
		return "lifeApproval";
	}

	public String prepareLifeInform(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeInform";
	}

	public String prepareLifeConfirmation(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeConfirmation";
	}

	public String prepareLifePayment(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifePayment";
	}

	public String prepareLifeIssuing(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeIssuing";
	}

	public String prepareLifeApprovalPrint(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeApprovalPrint";
	}

	public String prepareLifeRejectPrint(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeRejectPrint";
	}

	public String prepareLifeSurveyEndorsement(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "lifeSurveyEndorsement";
	}

	public String prepareLifeApprovalEndorsement(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeApprovalEndorsement";
	}

	public String prepareLifeInformEndorsement(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeInformEndorsement";
	}

	public String prepareLifeConfirmationEndorsement(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeConfirmationEndorsement";
	}

	public String prepareLifePaymentEndorsement(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifePaymentEndorsement";
	}

	public String prepareLifeIssuingEndorsement(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeIssuingEndorsement";
	}

	private void outjectLifeProposal(String lifeProposalId) {
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		outjectLifeProposal(lifeProposal);
	}

	public String prepareAgentCommissionPayment(AgentCommissionDTO agentCommissionDTO) {
		outjectAgentCommissionDTO(agentCommissionDTO);
		return "agentCommissionPayment";
	}

	public String prepareStaffCommissionPayment(StaffCommissionDTO staffCommissionDTO) {
		outjectStaffCommissionDTO(staffCommissionDTO);
		return "staffCommissionPayment";
	}

	public String prepareOutCoinsurancePayment(String coinsuranceId) {
		outjectCoinsurance(coinsuranceId);
		return "outCoinsurancePayment";
	}

	public String prepareInCoinsuranceConfirmation(String coinsuranceId) {
		outjectCoinsurance(coinsuranceId);
		return "inCoinsuranceConfirmation";
	}

	public String prepareInCoinsurancePayment(String coinsuranceId) {
		outjectCoinsurance(coinsuranceId);
		return "inCoinsurancePayment";
	}

	public String prepareFarmerSurvey(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "lifeSurvey";
	}

	public String prepareFarmerApproval(String lifeProposalId) {
		outjectLifeProposalId(lifeProposalId);
		return "lifeApproval";
	}

	public String prepareFarmerInform(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeInform";
	}

	public String prepareFarmerConfirmation(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeConfirmation";
	}

	public String prepareFarmerPayment(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifePayment";
	}

	public String prepareFarmerIssuing(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeIssuing";
	}

	/* Student Life */
	public String prepareStudentLifeSurvey(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "studentLifeSurvey";
	}

	public String prepareStudentLifeApproval(String lifeProposalId) {
		// outjectLifeProposal(lifeProposalId);
		putParam("lifeProposalId", lifeProposalId);
		return "studentLifeApproval";
	}

	public String prepareStudentLifeInform(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "studentLifeInform";
	}

	public String prepareStudentLifeConfirmation(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "studentLifeConfirm";
	}

	public String prepareStudentLifePayment(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "studentLifePayment";
	}

	public String prepareStudentLifeIssuing(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "studentLifeIssue";
	}

	/* ForPublicLife */
	/* End Student Life */

	public String preparePublicTermLifeSurvey(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "publictermlifesurvey";
	}

	public String preparePublicTermLifeApproval(String lifeProposalId) {
		// outjectLifeProposal(lifeProposalId);
		putParam("lifeProposalId", lifeProposalId);
		return "publictermlifeapprove";
	}

	public String preparePublicTermLifeInform(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeInform";
	}

	public String preparePublicTermLifeConfirmation(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeConfirmation";

	}

	public String prepareSPublicTermLifePayment(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifePayment";
	}

	public String preparePublicTermLifeIssuing(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeIssuing";
	}

	/* For Single Premium Endowment Life */
	public String prepareSinglePremiumEndowmentLifeSurvey(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "singlepremiumendowmentlifesurvey";
	}

	public String prepareSinglePremiumEndowmentLifeApproval(String lifeProposalId) {
		// outjectLifeProposal(lifeProposalId);
		putParam("lifeProposalId", lifeProposalId);
		return "singlepremiumendowmentlifeapproval";
	}

	public String prepareSinglePremiumEndowmentLifeInform(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeInform";
	}

	public String prepareSinglePremiumEndowmentLifeConfirmation(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeConfirmation";

	}

	public String prepareSinglePremiumEndowmentLifePayment(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifePayment";
	}

	public String prepareSinglePremiumEndowmentLifeIssuing(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeIssuing";
	}

	/* For Single Premium Credit Life */
	public String prepareSinglePremiumCreditLifeSurvey(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "singlepremiumcreditlifesurvey";
	}

	public String prepareSinglePremiumCreditLifeApproval(String lifeProposalId) {
		// outjectLifeProposal(lifeProposalId);
		putParam("lifeProposalId", lifeProposalId);
		return "singlepremiumcreditlifeapproval";
	}

	public String prepareSinglePremiumCreditLifeInform(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeInform";
	}

	public String prepareSinglePremiumCreditLifeConfirmation(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeConfirmation";

	}

	public String prepareSinglePremiumCreditLifePayment(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifePayment";
	}

	public String prepareSinglePremiumCreditLifeIssuing(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeIssuing";
	}

	/* For Short Term Single Premium Credit Life */
	public String prepareShortTermSinglePremiumCreditLifeSurvey(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "shorttermsinglepremiumcreditlifesurvey";
	}

	public String prepareShortTermSinglePremiumCreditLifeApproval(String lifeProposalId) {
		// outjectLifeProposal(lifeProposalId);
		putParam("lifeProposalId", lifeProposalId);
		return "shorttermsinglepremiumcreditlifeapproval";
	}

	public String prepareShortTermSinglePremiumCreditLifeInform(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeInform";
	}

	public String prepareShortTermSinglePremiumCreditLifeConfirmation(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeConfirmation";

	}

	public String prepareShortTermSinglePremiumCreditLifePayment(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifePayment";
	}

	public String prepareShortTermSinglePremiumCreditLifeIssuing(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeIssuing";
	}

	
	/* For Simple Life */
	public String prepareSimpleLifeSurvey(String lifeProposalId) {
		this.lifeSurvey = new LifeSurvey();
		lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		lifeSurvey.setLifeProposal(lifeProposal);
		outjectLifeSurvey(lifeSurvey);
		return "lifeSurvey";
	}

	public String prepareSimpleLifeApproval(String lifeProposalId) {
		// outjectLifeProposal(lifeProposalId);
		putParam("lifeProposalId", lifeProposalId);
		return "simplelifeapproval";
	}

	public String prepareSimpleLifeInform(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeInform";
	}

	public String prepareSimpleLifeConfirmation(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeConfirmation";

	}

	public String prepareSimpleLifePayment(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifePayment";
	}

	public String prepareSimpleLifeIssuing(String lifeProposalId) {
		outjectLifeProposal(lifeProposalId);
		return "lifeIssuing";
	}

	/* prepare short Term Endowment Life */
	// same life workFlow

	public void checkMessage(ComponentSystemEvent event) {
		ExternalContext extContext = getFacesContext().getExternalContext();
		String messageId = (String) extContext.getSessionMap().get(Constants.MESSAGE_ID);
		String proposalNo = (String) extContext.getSessionMap().get(Constants.PROPOSAL_NO);
		String requestNo = (String) extContext.getSessionMap().get(Constants.REQUEST_NO);
		String excelProposal = (String) extContext.getSessionMap().get(Constants.EXCEL_PROPOSAL);
		
		if (messageId != null) {
			if (proposalNo != null) {
				addInfoMessage(null, messageId + MESSAGE_PARAM_SUFFIX, proposalNo);
				extContext.getSessionMap().remove(Constants.MESSAGE_ID);
				extContext.getSessionMap().remove(Constants.PROPOSAL_NO);
			} else {
				if (requestNo != null) {
					addInfoMessage(null, messageId + MESSAGE_REQUEST_PARAM_SUFFIX, requestNo);
					extContext.getSessionMap().remove(Constants.MESSAGE_ID);
					extContext.getSessionMap().remove(Constants.REQUEST_NO);
				} else {
					if (excelProposal != null) {
						addInfoMessage(null, messageId + MESSAGE_EXCEL_PARAM_SUFFIX, excelProposal);
						extContext.getSessionMap().remove(Constants.MESSAGE_ID);
						extContext.getSessionMap().remove(Constants.EXCEL_PROPOSAL);
					} else {
						addInfoMessage(null, messageId);
						extContext.getSessionMap().remove(Constants.MESSAGE_ID);
					}
				}
			}
		}
	}

	/*************************
	 * Prepare Survey Method
	 ****************************************/

	/*************************
	 * Prepare Approval Method
	 ****************************************/

	/*************************
	 * Prepare Inform Method
	 ****************************************/

	/*************************
	 * Prepare Confirm Method
	 ****************************************/

	/*****************************
	 * Prepare Payment Method
	 ***********************************/

	/*************************
	 * Prepare Confirm Method
	 ****************************************/

	public String prepareLifeClaimRequest(String claimRequestId) {
		lifeClaim = claimService.findLifeClaimByRequestId(claimRequestId);
		outjectLifeClaim(lifeClaim, false);
		return "lifeDeathClaimRequest";
	}

	public String prepareLifeClaimSurvey(String lifeClaimProposalId) {
		LifeClaimProposal lifeClaimProposal = lifeClaimProposalService.findLifeClaimProposalById(lifeClaimProposalId);
		LifeClaimSurvey lifeClaimSurvey = new LifeClaimSurvey();
		lifeClaimSurvey.setLifeClaimProposal(lifeClaimProposal);
		// outjectLifeClaimProposal(lifeClaimProposal);
		putParam("lifeClaimSurvey", lifeClaimSurvey);
		return "lifeClaimSurvey";
	}

	public String prepareLifeClaimApproval(String lifeClaimProposalId) {
		LifeClaimProposal lifeClaimProposal = lifeClaimProposalService.findLifeClaimProposalById(lifeClaimProposalId);
		outjectLifeClaimProposal(lifeClaimProposal);
		return "lifeClaimApproval";
	}

	public String prepareLifeClaimInform(String lifeClaimProposalId) {
		LifeClaimProposal lifeClaimProposal = lifeClaimProposalService.findLifeClaimProposalById(lifeClaimProposalId);
		outjectLifeClaimProposal(lifeClaimProposal);
		return "lifeClaimInform";
	}

	public String prepareLifeClaimConfirmation(String lifeClaimProposalId) {
		LifeClaimProposal lifeClaimProposal = lifeClaimProposalService.findLifeClaimProposalById(lifeClaimProposalId);
		outjectLifeClaimProposal(lifeClaimProposal);
		return "lifeClaimConfirmation";
	}

	public String prepareLifeClaimPayment(String lifeClaimProposalId) {
		LifeClaimProposal lifeClaimProposal = lifeClaimProposalService.findLifeClaimProposalById(lifeClaimProposalId);
		outjectLifeClaimProposal(lifeClaimProposal);
		return "lifeClaimPayment";
	}

	public String prepareLifeDeathClaimPayment(String refundNo) {
		lifeClaimBeneficiary = claimService.findLifeClaimBeneficaryByRefundNo(refundNo);
		outjectLifeClaimBeneficiary(lifeClaimBeneficiary);
		return "lifeClaimPayment";
	}

	public String prepareLifeDisabilityClaimRequest(String claimRequestId) {
		lifeDisabilityClaim = disabilityClaimService.findDisabilityClaimByRequestId(claimRequestId);
		outjectLifeClaim(lifeDisabilityClaim, true);
		return "lifeDisabilityClaimRequest";
	}

	public String prepareLifeDisabilityClaimApproval(String claimRequestId) {
		lifeDisabilityClaim = disabilityClaimService.findDisabilityClaimByRequestId(claimRequestId);
		outjectLifeClaim(lifeDisabilityClaim, true);
		return "lifeDisabilityClaimApproval";
	}

	public String prepareLifeDisabilityClaimInform(String claimRequestId) {
		lifeDisabilityClaim = disabilityClaimService.findDisabilityClaimByRequestId(claimRequestId);
		outjectLifeClaim(lifeDisabilityClaim, true);
		return "lifeDisabilityClaimInform";
	}

	public String prepareLifeDisabilityClaimConfirmation(String claimRequestId) {
		lifeDisabilityClaim = disabilityClaimService.findDisabilityClaimByRequestId(claimRequestId);
		outjectLifeClaim(lifeDisabilityClaim, true);
		return "lifeDisabilityClaimConfirm";
	}

	public String prepareLifeDisabilityClaimPayment(String claimRequestId) {
		lifeDisabilityClaim = disabilityClaimService.findDisabilityClaimByRequestId(claimRequestId);
		outjectLifeClaim(lifeDisabilityClaim, true);
		return "lifeDisabilityClaimPayment";
	}

	public String prepareLifePolicyPremium(LifePolicyBilling lifePolicyBilling) {
		outjectLifePolicyBilling(lifePolicyBilling);
		return "lifeBillCollectionPaymentForm";
	}

	public String prepareSnakeBitePolicyPayment(SnakeBitePolicyDTO snakeBitePolicyDTO) {
		outjectSnakeBitePolicyDTO(snakeBitePolicyDTO);
		return "SnakeBitePayment";
	}

	/*** prepare methods for Life Surrender ***/
	public String prepareLifeSurrenderApproval(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "approveLifeSurrenderProposal";

	}

	public String prepareLifeSurrenderInform(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "informLifeSurrender";

	}

	public String prepareLifeSurrenderConfirmation(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "confirmLifeSurrender";

	}

	public String prepareLifeSurrenderPayment(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "lifeSurrenderPayment";

	}

	public String prepareLifeSurrenderIssue(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "lifeSurrenderIssue";

	}

	/*** prepare methods for Short Term Life Surrender ***/
	public String prepareShortTermLifeSurrenderApproval(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "approveLifeSurrenderProposal";

	}

	public String prepareShortTermLifeSurrenderInform(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "informLifeSurrender";

	}

	public String prepareShortTermLifeSurrenderConfirmation(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "confirmLifeSurrender";

	}

	public String prepareShortTermLifeSurrenderPayment(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "lifeSurrenderPayment";

	}

	public String prepareShortTermLifeSurrenderIssue(String proposalNo) {
		surrenderProposal = surrenderProposalService.findByProposalNo(proposalNo);
		outjectSurrenderProposal(surrenderProposal);
		return "lifeSurrenderIssue";

	}

	/*** prepare methods for Life PaidUp ***/
	public String prepareLifePaidUpApproval(String proposalNo) {
		paidUpProposal = lifePaidUpProposalService.findByProposalNo(proposalNo);
		outjectPaidUpProposal(paidUpProposal);
		return "approveLifePaidUpProposal";

	}

	public String prepareLifePaidUpInform(String proposalNo) {
		paidUpProposal = lifePaidUpProposalService.findByProposalNo(proposalNo);
		outjectPaidUpProposal(paidUpProposal);
		return "informLifePaidUpProposal";

	}

	public String prepareLifePaidUpConfirmation(String proposalNo) {
		paidUpProposal = lifePaidUpProposalService.findByProposalNo(proposalNo);
		outjectPaidUpProposal(paidUpProposal);
		return "confirmLifePaidUpProposal";

	}

	public String prepareTravelProposal(String id) {
		travelProposal = travelProposalService.findTravelProposalById(id);
		outjectTravelProposal(travelProposal);
		return "travelConfirm";
	}

	public String prepareTravelProposalPayment(String id) {
		travelProposal = travelProposalService.findTravelProposalById(id);
		outjectTravelProposal(travelProposal);
		return "travelPayment";
	}

	public String prepareGroupFarmerProposalConfirm(String id) {
		groupFarmerProposal = groupFarmerProposalService.findGroupFarmerById(id);
		outjectGroupFarmerProposal(groupFarmerProposal);
		return "confirmGroupFarmer";
	}

	public String prepareGroupFarmerProposalPayment(String id) {
		groupFarmerProposal = groupFarmerProposalService.findGroupFarmerById(id);
		outjectGroupFarmerProposal(groupFarmerProposal);
		return "groupFarmerPayment";
	}

	public String prepareGroupFarmerProposalIssue(String id) {
		groupFarmerProposal = groupFarmerProposalService.findGroupFarmerById(id);
		outjectGroupFarmerProposal(groupFarmerProposal);
		return "groupFarmerPolicyListForm";
	}

	/*** prepare methods for Reinstate ***/

	/*** prepare methods for Person Travel ***/
	public String preparePersonTravelConfirmation(String id) {
		putParam("personTravelProposalId", id);
		return "confirmPersonTravel";
	}

	public String preparePersonTravelPayment(String id) {
		putParam("personTravelProposalId", id);
		return "paymentPersonTravel";
	}

	public String preparePersonTravelIssuing(String id) {
		putParam("personTravelProposalId", id);
		return "issuePersonTravel";
	}

	/********************************************************
	 * load count
	 ****************************************************/
	private void loadWorkflowCountByUser() {
		userWorkFlows = workFlowService.find_WF001ByUser(user.getId());
		for (WF001 workFlow : userWorkFlows) {
			WorkflowReferenceType referenceType = workFlow.getReferenceType();
			WorkflowTask workflowTask = workFlow.getWorkflowTask();
			switch (referenceType) {
				case GROUP_MICROHEALTH: {
					switch (workflowTask) {
						case APPROVAL: {
							groupMicroHealthApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}

						case CONFIRMATION: {
							groupMicroHealthConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							groupMicroHealthPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}

						default:
							break;
					}
					break;
				}

				case MEDICAL_PROPOSAL: {
					switch (workflowTask) {
						case SURVEY: {
							medicalSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							medicalApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							medicalInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							medicalConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							medicalPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							medicalIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;

					}
					break;
				}

				case MEDICAL_RENEWAL_PROPOSAL: {
					switch (workflowTask) {

						/*** count for Renewal Medical Proposal ***/
						case RENEWAL_SURVEY: {
							medicalRenewalSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_APPROVAL: {
							medicalRenewalApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_INFORM: {
							medicalRenewalInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_CONFIRMATION: {
							medicalRenewalConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_PAYMENT: {
							medicalRenewalPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_ISSUING: {
							medicalRenewalIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;

					}
					break;
				}

				case MEDICAL_CLAIM: {
					switch (workflowTask) {
						case CLAIM_SURVEY: {
							medicalClaimSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_APPROVAL: {
							medicalClaimApproveCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_INFORM: {
							medicalClaimInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_CONFIRMATION: {
							medicalClaimConfirmCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_PAYMENT: {
							medicalClaimPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				/*** Life Proposal ***/
				case LIFE_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							lifeSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							lifeApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							lifeInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							lifeConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							lifePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							lifeIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}

						/*** count for Endorsement life Proposal ***/
						case ENDORSEMENT_SURVEY: {
							lifeEndorseSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ENDORSEMENT_APPROVAL: {
							lifeEndorseApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ENDORSEMENT_CONFIRMATION: {
							lifeEndorseConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ENDORSEMENT_PAYMENT: {
							lifeEndorsePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ENDORSEMENT_ISSUING: {
							lifeEndorseIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}

						/*** count for Renewal Group life Proposal ***/
						case RENEWAL_SURVEY: {
							lifeRenewalSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_APPROVAL: {
							lifeRenewalApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_INFORM: {
							lifeRenewalInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_CONFIRMATION: {
							lifeRenewalConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_PAYMENT: {
							lifeRenewalPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case RENEWAL_ISSUING: {
							lifeRenewalIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;

					}
					break;
				}

				/*** count for Damaged Vehicle (Motor Claim ) ***/

				/*** count for Life Claim ***/
				case LIFE_CLAIM: {
					switch (workflowTask) {
						case CLAIM_REQUEST: {
							lifeClaimRequestCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}

						case CLAIM_SURVEY: {
							lifeClaimSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}

						case CLAIM_APPROVAL: {
							lifeClaimApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_INFORM: {
							lifeClaimInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_CONFIRMATION: {
							lifeClaimConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_PAYMENT: {
							lifeClaimPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				/*** count for Life Disability Claim ***/
				case LIFE_DIS_CLAIM: {
					switch (workflowTask) {
						case CLAIM_REQUEST: {
							lifeDisabilityClaimRequestCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_APPROVAL: {
							lifeDisabilityClaimApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_INFORM: {
							lifeDisabilityClaimInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_CONFIRMATION: {
							lifeDisabilityClaimConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CLAIM_PAYMENT: {
							lifeDisabilityClaimPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				/** Count for Life Surrender Proposal ***/
				case LIFESURRENDER_PROPOSAL: {
					switch (workflowTask) {
						case APPROVAL: {
							lifeSurrenderApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							lifeSurrenderInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							lifeSurrenderConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							lifeSurrenderPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							lifeSurrenderIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				/** Count for ShortTermLife Surrender Proposal ***/
				case SHORTENDOWMENTLIFESURRENDER_PROPOSAL: {
					switch (workflowTask) {
						case APPROVAL: {
							shortTermLifeSurrenderApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							shortTermLifeSurrenderInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							shortTermLifeSurrenderConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							shortTermLifeSurrenderPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							shortTermLifeSurrenderIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				/** Count for Life paidup Proposal ***/
				case LIFE_PAIDUP_PROPOSAL: {
					switch (workflowTask) {
						case APPROVAL: {
							lifePaidUpApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							lifePaidUpInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							lifePaidUpConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				/** Count for ShortTerm Life PaidUp Proposal ***/

				case SHORTTERM_LIFE_PAIDUP_PROPOSAL: {
					switch (workflowTask) {
						case APPROVAL: {
							shortTermLifePaidUpApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							shortTermLifePaidUpInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							shortTermLifePaidUpConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT:
						default:
							break;
					}
					break;
				}

				/*** count for Cargo Claim ***/

				/*** count for CashInSafe Claim ***/

				/*** count for Fidelity Proposal ***/

				/*** count for Agent Commission Payment ***/
				case AGENT_COMMISSION: {
					switch (workflowTask) {
						case AGENT_COMMISSION_PAYMENT: {
							agentCommissionPaymentCount = proxyService
									.findAgentCommissionCount(new WorkflowCriteria(WorkflowReferenceType.AGENT_COMMISSION, WorkflowTask.AGENT_COMMISSION_PAYMENT, user));
							break;
						}
						default:
							break;
					}
					break;
				}

				case STAFF_COMMISSION: {
					switch (workflowTask) {
						case STAFF_COMMISSION_PAYMENT: {
							staffCommissionPaymentCount = proxyService
									.findStaffCommissionCount(new WorkflowCriteria(WorkflowReferenceType.STAFF_COMMISSION, WorkflowTask.STAFF_COMMISSION_PAYMENT, user));
							break;
						}
						default:
							break;
					}
					break;
				}

				/*** count for Co_insurance ***/
				case COINSURANCE: {
					switch (workflowTask) {
						case COINSURANCE_CONFIRMATION: {
							coinsuranceFromConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case COINSURANCE_PAYMENT: {
							/*** count for From Other Company ***/
							coinsuranceFromPaymentCount = workFlowService.findCoinsuranceCountForDashBoard(workflowTask, referenceType, user.getId(), CoinsuranceType.IN);
							/*** count for To Other Company ***/
							coinsuranceToPaymentCount = workFlowService.findCoinsuranceCountForDashBoard(workflowTask, referenceType, user.getId(), CoinsuranceType.OUT);
							break;
						}
						default:
							break;
					}
					break;
				}

				/*** count for Snakebite Policy ***/
				case SNAKEBITEPOLICY: {
					switch (workflowTask) {
						case PAYMENT: {
							snakeBitePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				case TRAVEL_PROPOSAL: {
					switch (workflowTask) {
						case CONFIRMATION: {
							travelProposalConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							travelProposalPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				/*** Personal Accident ***/
				case PA_PROPOSAL: {
					/***
					 * count for UnderWriting Personal Accident Proposal
					 ***/
					switch (workflowTask) {
						case SURVEY: {
							paSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							paApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							paInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							paConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							paPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							paIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				/*** Person Travel ***/
				case PERSON_TRAVEL_PROPOSAL: {
					/***
					 * count for UnderWriting Personal Travel Proposal
					 ***/
					switch (workflowTask) {
						case CONFIRMATION: {
							personTravelConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							personTravelPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							personTravelIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}
				/*** Farmer Underwriting ***/
				case FARMER_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							farmerSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							farmerApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							farmerInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							farmerConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							farmerPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							farmerIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}
				/*** ShortTerm Endowment Underwriting ***/
				case SHORT_ENDOWMENT_LIFE_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							shortEndowmentSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							shortEndowmentApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							shortEndowmentInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							shortEndowmentConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							shortEndowmentPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							shortEndowmentIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}

						case ENDORSEMENT_SURVEY: {
							shortEndowmentEndorsementSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}

						case ENDORSEMENT_APPROVAL: {
							shortEndowmentEndorsementApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ENDORSEMENT_INFORM: {
							shortEndowmentEndorsementInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ENDORSEMENT_CONFIRMATION: {
							shortEndowmentEndorsementConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ENDORSEMENT_PAYMENT: {
							shortEndowmentEndorsementPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ENDORSEMENT_ISSUING: {
							shortEndowmentEndorsementIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}
				case CRITICAL_ILLNESS_POLICY:
					break;
				case CRITICAL_ILLNESS_PROPOSAL: {
					switch (workflowTask) {
						case SURVEY: {
							criticalIllnessSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							criticalIllnessApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							criticalIllnessInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							criticalIllnessConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							criticalIllnessPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							criticalIllnessIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}
				case HEALTH_PROPOSAL: {
					switch (workflowTask) {
						case SURVEY: {
							healthSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							healthApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							healthInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							healthConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							healthPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							healthIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}
				case HEALTH_POLICY:
					break;
				case LIFE_COLLECTION:
					break;
				case MEDICAL_POLICY:
					break;
				case MICRO_HEALTH_POLICY:
					break;
				case MICRO_HEALTH_PROPOSAL: {
					switch (workflowTask) {
						case SURVEY: {
							microHealthSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							microHealthApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							microHealthInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							microHealthConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							microHealthPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							microHealthIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}
				case GROUPFARMER_PROPOSAL: {
					/***
					 * count for UnderWriting Personal Travel Proposal
					 ***/
					switch (workflowTask) {
						case CONFIRMATION: {
							groupFarmerConfirmCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							groupFarmerPaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				case PUBLIC_TERM_LIFE_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							publicTermlifeSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							publicTermlifeApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							publicTermlifeInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							publicTermlifeConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							publicTermlifePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							publicTermlifeIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				case SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							singlePremiumEndowmentlifeSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							singlePremiumEndowmentlifeApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							singlePremiumEndowmentlifeInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							singlePremiumEndowmentlifeConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							singlePremiumEndowmentlifePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							singlePremiumEndowmentlifeIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				case SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							singlePremiumCreditlifeSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							singlePremiumCreditlifeApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							singlePremiumCreditlifeInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							singlePremiumCreditlifeConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							singlePremiumCreditlifePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							singlePremiumCreditlifeIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							shortTermSinglePremiumCreditlifeSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							shortTermSinglePremiumCreditlifeApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							shortTermSinglePremiumCreditlifeInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							shortTermSinglePremiumCreditlifeConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							shortTermSinglePremiumCreditlifePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							shortTermSinglePremiumCreditlifeIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}

				case SIMPLE_LIFE_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							simplelifeSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							simplelifeApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							simplelifeInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							simplelifeConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							simplelifePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							simplelifeIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}
				case STUDENT_LIFE_PROPOSAL: {
					switch (workflowTask) {
						/*** count for UnderWriting Life Proposal ***/
						case SURVEY: {
							studentLifeSurveyCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case APPROVAL: {
							studentLifeApprovalCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case INFORM: {
							studentLifeInformCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case CONFIRMATION: {
							studentLifeConfirmationCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case PAYMENT: {
							studentLifePaymentCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						case ISSUING: {
							studentLifeIssueCount = workFlowService.findCountForDashBoard(workflowTask, referenceType, user.getId());
							break;
						}
						default:
							break;
					}
					break;
				}
				default:
					break;

			}
		}
	}

	/* FOR CRITICAL_ILLNESS_INSURANCE */
	public String getCriticalIllnessSurvey() {
		return DashboardCategory.CRITICAL_ILLNESS_SURVEY;
	}

	public String getCriticalIllnessApproval() {
		return DashboardCategory.CRITICAL_ILLNESS_APPROVAL;
	}

	public String getCriticalIllnessInform() {
		return DashboardCategory.CRITICAL_ILLNESS_INFORM;
	}

	public String getCriticalIllnessConfirmation() {
		return DashboardCategory.CRITICAL_ILLNESS_CONFIRMATION;
	}

	public String getCriticalIllnessPayment() {
		return DashboardCategory.CRITICAL_ILLNESS_PAYMENT;
	}

	public String getCriticalIllnessIssuing() {
		return DashboardCategory.CRITICAL_ILLNESS_ISSUE;
	}

	/* FOR HEALTH INSURANCE */

	public String getHealthSurvey() {
		return DashboardCategory.HEALTH_SURVEY;
	}

	public String getHealthApproval() {
		return DashboardCategory.HEALTH_APPROVAL;
	}

	public String getHealthInform() {
		return DashboardCategory.HEALTH_INFORM;
	}

	public String getHealthConfirmation() {
		return DashboardCategory.HEALTH_CONFIRMATION;
	}

	public String getHealthPayment() {
		return DashboardCategory.HEALTH_PAYMENT;
	}

	public String getHealthIssuing() {
		return DashboardCategory.HEALTH_ISSUE;
	}

	/* FOR MICRO HEALTH INSURANCE */
	public String getMicroHealthSurvey() {
		return DashboardCategory.MICRO_HEALTH_SURVEY;
	}

	public String getMicroHealthApproval() {
		return DashboardCategory.MICRO_HEALTH_APPROVAL;
	}

	public String getMicroHealthInform() {
		return DashboardCategory.MICRO_HEALTH_INFORM;
	}

	public String getMicroHealthConfirmation() {
		return DashboardCategory.MICRO_HEALTH_CONFIRMATION;
	}

	public String getMicroHealthPayment() {
		return DashboardCategory.MICRO_HEALTH_PAYMENT;
	}

	public String getMicroHealthIssuing() {
		return DashboardCategory.MICRO_HEALTH_ISSUE;
	}

	/* FOR Group Micro Health */
	public String getGroupMicroHealthApproval() {
		return DashboardCategory.GROUP_MICRO_HEALTH_APPROVAL;
	}

	public String getGroupMicroHealthConfirmation() {
		return DashboardCategory.GROUP_MICRO_HEALTH_CONFIRMATION;
	}

	public String getGroupMicroHealthPayment() {
		return DashboardCategory.GROUP_MICRO_HEALTH_PAYMENT;
	}

	/***************************
	 * for cargo insurance
	 *******************************/

	public String getCargoSurvey() {
		return DashboardCategory.CARGO_SURVEY;
	}

	public String getCargoApproval() {
		return DashboardCategory.CARGO_APPROVAL;
	}

	public String getCargoInform() {
		return DashboardCategory.CARGO_INFORM;
	}

	public String getCargoConfirmation() {
		return DashboardCategory.CARGO_COMFIRMATION;
	}

	public String getCargoPayment() {
		return DashboardCategory.CARGO_PAYMENT;
	}

	public String getCargoIssuing() {
		return DashboardCategory.CARGO_ISSUE;
	}

	/**********************************************
	 * getting category name
	 ****************************************/
	public String getMedicalClaimConfirm() {
		return DashboardCategory.MED_CLAIM_CONFIRM;
	}

	public String getMedicalSurvey() {
		return DashboardCategory.MEDICAL_SURVEY;
	}

	public String getMedicalApproval() {
		return DashboardCategory.MEDICAL_APPROVAL;
	}

	public String getMedicalInform() {
		return DashboardCategory.MEDICAL_INFORM;
	}

	public String getMedicalConfirmation() {
		return DashboardCategory.MEDICAL_CONFIRMATION;
	}

	public String getMedicalPayment() {
		return DashboardCategory.MEDICAL_PAYMENT;
	}

	public String getMedicalIssue() {
		return DashboardCategory.MEDICAL_ISSUE;
	}

	/* Medical Renewal */
	public String getMedicalRenewalSurvey() {
		return DashboardCategory.MEDICAL_RENEWAL_SURVEY;
	}

	public String getMedicalRenewalInform() {
		return DashboardCategory.MEDICAL_RENEWAL_INFORM;
	}

	public String getMedicalRenewalApproval() {
		return DashboardCategory.MEDICAL_RENEWAL_APPROVAL;
	}

	public String getMedicalRenewalConfirm() {
		return DashboardCategory.MEDICAL_RENEWAL_CONFIRMATION;
	}

	public String getMedicalRenewalPayment() {
		return DashboardCategory.MEDICAL_RENEWAL_PAYMENT;
	}

	public String getMedicalRenewalIssue() {
		return DashboardCategory.MEDICAL_RENEWAL_ISSUE;
	}

	/** MotorProposal UnderWriting **/

	/** FireProposal UnderWriting **/

	public String getLifeSurvey() {
		return DashboardCategory.LIFE_SURVEY;
	}

	public String getLifeApproval() {
		return DashboardCategory.LIFE_APPROVAL;
	}

	public String getLifeInform() {
		return DashboardCategory.LIFE_INFORM;
	}

	public String getLifeConfirmation() {
		return DashboardCategory.LIFE_COMFIRMATION;
	}

	public String getLifePayment() {
		return DashboardCategory.LIFE_PAYMENT;
	}

	public String getLifeIssue() {
		return DashboardCategory.LIFE_ISSUE;
	}

	public String getFarmerSurvey() {
		return DashboardCategory.FARMER_SURVEY;
	}

	public String getFarmerApproval() {
		return DashboardCategory.FARMER_APPROVAL;
	}

	public String getFarmerInform() {
		return DashboardCategory.FARMER_INFORM;
	}

	public String getFarmerConfirmation() {
		return DashboardCategory.FARMER_COMFIRMATION;
	}

	public String getFarmerPayment() {
		return DashboardCategory.FARMER_PAYMENT;
	}

	public String getFarmerIssue() {
		return DashboardCategory.FARMER_ISSUE;
	}

	/***** Cash In Transit Claim ******/

	public String getSnakeBitePayment() {
		return DashboardCategory.SNAKE_BITE_PAYMENT;
	}

	/***** Endorsement ******/

	public String getLifeEndorsementSurvey() {
		return DashboardCategory.LIFE_ENDORSEMENT_SURVEY;
	}

	public String getLifeEndorsementApproval() {
		return DashboardCategory.LIFE_ENDORSEMENT_APPROVAL;
	}

	public String getLifeEndorsementConfirmation() {
		return DashboardCategory.LIFE_ENDORSEMENT_COMFIRMATION;
	}

	public String getLifeEndorsementPayment() {
		return DashboardCategory.LIFE_ENDORSEMENT_PAYMENT;
	}

	public String getLifeEndorsementIssue() {
		return DashboardCategory.LIFE_ENDORSEMENT_ISSUE;
	}

	public String getShortTermEndowmentEndorsementSurvey() {
		return DashboardCategory.SHORT_TERM_ENDOWMENT_ENDORSEMENT_SURVEY;
	}

	public String getShortTermEndowmentEndorsementApproval() {
		return DashboardCategory.SHORT_TERM_ENDOWMENT_ENDORSEMENT_APPROVAL;
	}

	public String getShortTermEndowmentEndorsementInform() {
		return DashboardCategory.SHORT_TERM_ENDOWMENT_ENDORSEMENT_Inform;
	}

	public String getShortTermEndowmentEndorsementConfirmation() {
		return DashboardCategory.SHORT_TERM_ENDOWMENT_ENDORSEMENT_COMFIRMATION;
	}

	public String getShortTermEndowmentEndorsementPayment() {
		return DashboardCategory.SHORT_TERM_ENDOWMENT_ENDORSEMENT_PAYMENT;
	}

	public String getShortTermEndowmentEndorsementIssue() {
		return DashboardCategory.SHORT_TERM_ENDOWMENT_ENDORSEMENT_ISSUE;
	}

	public String getLifeRenewalSurvey() {
		return DashboardCategory.GROUPLIFE_RENEWAL_SURVEY;
	}

	public String getLifeRenewalApproval() {
		return DashboardCategory.GROUPLIFE_RENEWAL_APPROVAL;
	}

	public String getLifeRenewalInform() {
		return DashboardCategory.GROUPLIFE_RENEWAL_INFORM;
	}

	public String getLifeRenewalConfirmation() {
		return DashboardCategory.GROUPLIFE_RENEWAL_COMFIRMATION;
	}

	public String getLifeRenewalPayment() {
		return DashboardCategory.GROUPLIFE_RENEWAL_PAYMENT;
	}

	public String getLifeRenewalIssue() {
		return DashboardCategory.GROUPLIFE_RENEWAL_ISSUE;
	}

	/*** Claim *****/

	public String getPersonCasualtySurvey() {
		return DashboardCategory.PERSONCASUALTY_SURVEY;
	}

	public String getPersonCasualtyApproval() {
		return DashboardCategory.PERSONCASUALTY_APPROVAL;
	}

	public String getPersonCasualtyInform() {
		return DashboardCategory.PERSONCASUALTY_INFORM;
	}

	public String getPersonCasualtyConfirmation() {
		return DashboardCategory.PERSONCASUALTY_COMFIRMATION;
	}

	public String getPersonCasualtyPayment() {
		return DashboardCategory.PERSONCASUALTY_PAYMENT;
	}

	public String getMedicalExpenseSurvey() {
		return DashboardCategory.MEDICALEXPENSE_SURVEY;
	}

	public String getMedicalExpenseApproval() {
		return DashboardCategory.MEDICALEXPENSE_APPROVAL;
	}

	public String getMedicalExpenseInform() {
		return DashboardCategory.MEDICALEXPENSE_INFORM;
	}

	public String getMedicalExpenseConfirmation() {
		return DashboardCategory.MEDICALEXPENSE_COMFIRMATION;
	}

	public String getMedicalExpensePayment() {
		return DashboardCategory.MEDICALEXPENSE_PAYMENT;
	}

	public String getLifeClaimRequest() {
		return DashboardCategory.LIFE_CLAIM_REQUEST;
	}

	public String getLifeClaimApproval() {
		return DashboardCategory.LIFE_CLAIM_APPROVAL;
	}

	public String getLifeClaimSurvey() {
		return DashboardCategory.LIFE_CLAIM_SURVEY;
	}

	public String getLifeClaimInform() {
		return DashboardCategory.LIFE_CLAIM_INFORM;
	}

	public String getLifeClaimConfirmation() {
		return DashboardCategory.LIFE_CLAIM_COMFIRMATION;
	}

	public String getLifeClaimPayment() {
		return DashboardCategory.LIFE_CLAIM_PAYMENT;
	}

	public String getLifeDisabilityClaimRequest() {
		return DashboardCategory.LIFE_DISABILITY_CLAIM_REQUEST;
	}

	public String getLifeDisabilityClaimApproval() {
		return DashboardCategory.LIFE_DISABILITY_CLAIM_APPROVAL;
	}

	public String getLifeDisabilityClaimInform() {
		return DashboardCategory.LIFE_DISABILITY_CLAIM_INFORM;
	}

	public String getLifeDisabilityClaimConfirmation() {
		return DashboardCategory.LIFE_DISABILITY_CLAIM_COMFIRMATION;
	}

	public String getLifeDisabilityClaimPayment() {
		return DashboardCategory.LIFE_DISABILITY_CLAIM_PAYMENT;
	}

	public String getCoinsuranceFromConfirmation() {
		return DashboardCategory.COINSURANCE_FROM_COMFIRMATION;
	}

	public String getCoinsuranceFromPayment() {
		return DashboardCategory.COINSURANCE_FROM_PAYMENT;
	}

	public String getCoinsuranceToPayment() {
		return DashboardCategory.COINSURANCE_TO_PAYMENT;
	}

	public String getAgentCommissionPayment() {
		return DashboardCategory.AGENT_COMMISSION_PAYMENT;
	}

	public String getStaffCommissionPayment() {
		return DashboardCategory.STAFF_COMMISSION_PAYMENT;
	}

	public String getTravelProposalConfirmation() {
		return DashboardCategory.TRAVEL_PROPOSAL_CONFIRMATION;
	}

	public String getTravelProposalPayment() {
		return DashboardCategory.TRAVEL_PROPOSAL_PAYMENT;
	}

	/******** Life Surrender *******/
	public String getLifeSurrenderApproval() {
		return DashboardCategory.LIFE_SURRENDER_APPROVAL;

	}

	public String getLifeSurrenderInform() {
		return DashboardCategory.LIFE_SURRENDER_INFORM;

	}

	public String getLifeSurrenderConfirmation() {
		return DashboardCategory.LIFE_SURRENDER_CONFIRMATION;

	}

	public String getLifeSurrenderPayment() {
		return DashboardCategory.LIFE_SURRENDER_PAYMENT;

	}

	public String getLifeSurrenderIssue() {
		return DashboardCategory.LIFE_SURRENDER_ISSUE;

	}

	/******** Short Term Life Surrender *******/
	public String getShortTermLifeSurrenderApproval() {
		return DashboardCategory.SHORTTERMLIFE_SURRENDER_APPROVAL;

	}

	public String getShortTermLifeSurrenderInform() {
		return DashboardCategory.SHORTTERMLIFE_SURRENDER_INFORM;

	}

	public String getShortTermLifeSurrenderConfirmation() {
		return DashboardCategory.SHORTTERMLIFE_SURRENDER_CONFIRMATION;

	}

	public String getShortTermLifeSurrenderPayment() {
		return DashboardCategory.SHORTTERMLIFE_SURRENDER_PAYMENT;

	}

	public String getShortTermLifeSurrenderIssue() {
		return DashboardCategory.SHORTTERMLIFE_SURRENDER_ISSUE;

	}

	/******** Life PaidUp *******/
	public String getLifePaidUpApproval() {
		return DashboardCategory.LIFE_PAIDUP_APPROVAL;

	}

	public String getLifePaidUpInform() {
		return DashboardCategory.LIFE_PAIDUP_INFORM;

	}

	public String getLifePaidUpConfirmation() {
		return DashboardCategory.LIFE_PAIDUP_CONFIRMATION;

	}

	/********* ShortTerm Endowment PaidUp ******/

	public String getShortTermLifePaidUpApproval() {
		return DashboardCategory.SHORTTERM_LIFE_PAIDUP_APPROVAL;

	}

	public String getShortTermLifePaidUpInform() {
		return DashboardCategory.SHORTTERM_LIFE_PAIDUP_INFORM;

	}

	public String getShortTermLifePaidUpConfirmation() {
		return DashboardCategory.SHORTTERM_LIFE_PAIDUP_CONFIRMATION;

	}

	/*********************** for Medical proposal *****************/

	public String getMedicalProposalApprove() {
		return DashboardCategory.MEDICAL_APPROVAL;
	}

	/***********************
	 * Dashboard Category For Personal Accident Underwriting
	 *****************/
	public String getPASurvey() {
		return DashboardCategory.PA_SURVEY;
	}

	public String getPAApproval() {
		return DashboardCategory.PA_APPROVAL;
	}

	public String getPAInform() {
		return DashboardCategory.PA_INFORM;
	}

	public String getPAConfirmation() {
		return DashboardCategory.PA_COMFIRMATION;
	}

	public String getPAPayment() {
		return DashboardCategory.PA_PAYMENT;
	}

	public String getPAIssue() {
		return DashboardCategory.PA_ISSUE;
	}

	/***********************
	 * Dashboard Category For Person Travel Underwriting
	 ***********************/
	public String getPersonTravelConfirmation() {
		return DashboardCategory.PERSON_TRAVEL_COMFIRMATION;
	}

	public String getPersonTravelPayment() {
		return DashboardCategory.PERSON_TRAVEL_PAYMENT;
	}

	public String getPersonTravelIssue() {
		return DashboardCategory.PERSON_TRAVEL_ISSUE;
	}

	/* DashBoard Category for Short Term Endowment Life */
	public String getShortEndowmentSurvey() {
		return DashboardCategory.SHORT_ENDOWMENT_SURVEY;
	}

	public String getShortEndowmentApproval() {
		return DashboardCategory.SHORT_ENDOWMENT_APPROVAL;
	}

	public String getShortEndowmentInform() {
		return DashboardCategory.SHORT_ENDOWMENT_INFORM;
	}

	public String getShortEndowmentConfirmation() {
		return DashboardCategory.SHORT_ENDOWMENT_COMFIRMATION;
	}

	public String getShortEndowmentPayment() {
		return DashboardCategory.SHORT_ENDOWMENT_PAYMENT;
	}

	public String getShortEndowmentIssue() {
		return DashboardCategory.SHORT_ENDOWMENT_ISSUE;
	}

	/* DashBoard Category for Group Farmer */
	public String getGroupFarmerConfirmation() {
		return DashboardCategory.GROUPFARMER_CONFIRMATION;
	}

	public String getGroupFarmerPayment() {
		return DashboardCategory.GROUPFARMER_PAYMENT;
	}

	public String getGroupFarmerIssue() {
		return DashboardCategory.GROUPFARMER_ISSUE;
	}

	/* DashBoard Category for Student Life */
	public String getStudentLifeSurvey() {
		return DashboardCategory.STUDENT_LIFE_SURVEY;
	}

	public String getStudentLifeApproval() {
		return DashboardCategory.STUDENT_LIFE_APPROVAL;
	}

	public String getStudentLifeInform() {
		return DashboardCategory.STUDENT_LIFE_INFORM;
	}

	public String getStudentLifeConfirmation() {
		return DashboardCategory.STUDENT_LIFE_COMFIRMATION;
	}

	public String getStudentLifePayment() {
		return DashboardCategory.STUDENT_LIFE_PAYMENT;
	}

	public String getStudentLifeIssue() {
		return DashboardCategory.STUDENT_LIFE_ISSUE;
	}

	/* Single Premium Endowment Life */
	/* DashBoard Category for PSingle Premium Endowment Life */
	public String getSinglePremiumEndowmentLifeSurvey() {
		return DashboardCategory.SINGLE_PREMIUM_ENDOWMENT_LIFE_SURVEY;
	}

	public String getSinglePremiumEndowmentLifeApproval() {
		return DashboardCategory.SINGLE_PREMIUM_ENDOWMENT_LIFE_APPROVAL;
	}

	public String getSinglePremiumEndowmentLifeInform() {
		return DashboardCategory.SINGLE_PREMIUM_ENDOWMENT_LIFE_INFORM;
	}

	public String getSinglePremiumEndowmentLifeConfirmation() {
		return DashboardCategory.SINGLE_PREMIUM_ENDOWMENT_LIFE_COMFIRMATION;
	}

	public String getSinglePremiumEndowmentLifePayment() {
		return DashboardCategory.SINGLE_PREMIUM_ENDOWMENT_LIFE_PAYMENT;
	}

	public String getSinglePremiumEndowmentLifeIssue() {
		return DashboardCategory.SINGLE_PREMIUM_ENDOWMENT_LIFE_ISSUE;
	}

	/* Single Premium Credit Life */
	/* DashBoard Category for Single Premium Credit Life */
	public String getSinglePremiumCreditLifeSurvey() {
		return DashboardCategory.SINGLE_PREMIUM_Credit_LIFE_SURVEY;
	}

	public String getSinglePremiumCreditLifeApproval() {
		return DashboardCategory.SINGLE_PREMIUM_Credit_LIFE_APPROVAL;
	}

	public String getSinglePremiumCreditLifeInform() {
		return DashboardCategory.SINGLE_PREMIUM_Credit_LIFE_INFORM;
	}

	public String getSinglePremiumCreditLifeConfirmation() {
		return DashboardCategory.SINGLE_PREMIUM_Credit_LIFE_COMFIRMATION;
	}

	public String getSinglePremiumCreditLifePayment() {
		return DashboardCategory.SINGLE_PREMIUM_Credit_LIFE_PAYMENT;
	}

	public String getSinglePremiumCreditLifeIssue() {
		return DashboardCategory.SINGLE_PREMIUM_Credit_LIFE_ISSUE;
	}

	/* Short Term Single Premium Credit Life */
	/* DashBoard Category for Short Term Single Premium Credit Life */
	public String getShortTermSinglePremiumCreditLifeSurvey() {
		return DashboardCategory.SHORT_TERM_SINGLE_PREMIUM_Credit_LIFE_SURVEY;
	}

	public String getShortTermSinglePremiumCreditLifeApproval() {
		return DashboardCategory.SHORT_TERM_SINGLE_PREMIUM_Credit_LIFE_APPROVAL;
	}

	public String getShortTermSinglePremiumCreditLifeInform() {
		return DashboardCategory.SHORT_TERM_SINGLE_PREMIUM_Credit_LIFE_INFORM;
	}

	public String getShortTermSinglePremiumCreditLifeConfirmation() {
		return DashboardCategory.SHORT_TERM_SINGLE_PREMIUM_Credit_LIFE_COMFIRMATION;
	}

	public String getShortTermSinglePremiumCreditLifePayment() {
		return DashboardCategory.SHORT_TERM_SINGLE_PREMIUM_Credit_LIFE_PAYMENT;
	}

	public String getShortTermSinglePremiumCreditLifeIssue() {
		return DashboardCategory.SHORT_TERM_SINGLE_PREMIUM_Credit_LIFE_ISSUE;
	}

	/* Simple Life */
	/* DashBoard Category for Simple Life*/
	public String getSimpleLifeSurvey() {
		return DashboardCategory.SIMPLE_LIFE_SURVEY;
	}

	public String getSimpleLifeApproval() {
		return DashboardCategory.SIMPLE_LIFE_APPROVAL;
	}

	public String getSimpleLifeInform() {
		return DashboardCategory.SIMPLE_LIFE_INFORM;
	}

	public String getSimpleLifeConfirmation() {
		return DashboardCategory.SIMPLE_LIFE_COMFIRMATION;
	}

	public String getSimpleLifePayment() {
		return DashboardCategory.SIMPLE_LIFE_PAYMENT;
	}

	public String getSimpleLifeIssue() {
		return DashboardCategory.SIMPLE_LIFE_ISSUE;
	}
	
	/* Public Term Life */
	/* DashBoard Category for Public Term Life */
	public String getPublicTermLifeSurvey() {
		return DashboardCategory.PUBLIC_TERM_LIFE_SURVEY;
	}

	public String getPublicTermLifeApproval() {
		return DashboardCategory.PUBLIC_TERM_LIFE_APPROVAL;
	}

	public String getPublicTermLifeInform() {
		return DashboardCategory.PUBLIC_TERM_LIFE_INFORM;
	}

	public String getPublicTermLifeConfirmation() {
		return DashboardCategory.PUBLIC_TERM_LIFE_COMFIRMATION;
	}

	public String getPublicTermLifePayment() {
		return DashboardCategory.PUBLIC_TERM_LIFE_PAYMENT;
	}

	public String getPublicTermLifeIssue() {
		return DashboardCategory.PUBLIC_TERM_LIFE_ISSUE;
	}

	/*********************************************
	 * count getter
	 *****************************************************************************/

	public long getMedicalSurveyCount() {
		return medicalSurveyCount;
	}

	public long getMedicalApprovalCount() {
		return medicalApprovalCount;
	}

	public long getMedicalInformCount() {
		return medicalInformCount;
	}

	public long getMedicalConfirmationCount() {
		return medicalConfirmationCount;
	}

	public long getMedicalPaymentCount() {
		return medicalPaymentCount;
	}

	public long getMedicalIssueCount() {
		return medicalIssueCount;
	}

	public long getMedicalRenewalSurveyCount() {
		return medicalRenewalSurveyCount;
	}

	public long getMedicalRenewalApprovalCount() {
		return medicalRenewalApprovalCount;
	}

	public long getMedicalRenewalInformCount() {
		return medicalRenewalInformCount;
	}

	public long getMedicalRenewalPaymentCount() {
		return medicalRenewalPaymentCount;
	}

	public long getMedicalRenewalConfirmationCount() {
		return medicalRenewalConfirmationCount;
	}

	public long getMedicalRenewalIssueCount() {
		return medicalRenewalIssueCount;
	}

	/** FireProposal UnderWriting **/

	public long getLifeSurveyCount() {
		return lifeSurveyCount;
	}

	public long getLifeApprovalCount() {
		return lifeApprovalCount;
	}

	public long getLifeConfirmationCount() {
		return lifeConfirmationCount;
	}

	public long getLifeInformCount() {
		return lifeInformCount;
	}

	public long getLifePaymentCount() {
		return lifePaymentCount;
	}

	public long getLifeIssueCount() {
		return lifeIssueCount;
	}

	public long getFarmerSurveyCount() {
		return farmerSurveyCount;
	}

	public long getFarmerApprovalCount() {
		return farmerApprovalCount;
	}

	public long getFarmerConfirmationCount() {
		return farmerConfirmationCount;
	}

	public long getFarmerInformCount() {
		return farmerInformCount;
	}

	public long getFarmerPaymentCount() {
		return farmerPaymentCount;
	}

	public long getFarmerIssueCount() {
		return farmerIssueCount;
	}

	public long getSnakeBitePaymentCount() {
		return snakeBitePaymentCount;
	}

	public long getLifeEndorseSurveyCount() {
		return lifeEndorseSurveyCount;
	}

	public long getLifeEndorseApprovalCount() {
		return lifeEndorseApprovalCount;
	}

	public long getLifeEndorseConfirmationCount() {
		return lifeEndorseConfirmationCount;
	}

	public long getLifeEndorsePaymentCount() {
		return lifeEndorsePaymentCount;
	}

	public long getLifeEndorseIssueCount() {
		return lifeEndorseIssueCount;
	}

	public long getLifeRenewalSurveyCount() {
		return lifeRenewalSurveyCount;
	}

	public long getLifeRenewalApprovalCount() {
		return lifeRenewalApprovalCount;
	}

	public long getLifeRenewalConfirmationCount() {
		return lifeRenewalConfirmationCount;
	}

	public long getLifeRenewalInformCount() {
		return lifeRenewalInformCount;
	}

	public long getLifeRenewalPaymentCount() {
		return lifeRenewalPaymentCount;
	}

	public long getLifeRenewalIssueCount() {
		return lifeRenewalIssueCount;
	}

	public long getPersonCasualtySurveyCount() {
		return personCasualtySurveyCount;
	}

	public long getPersonCasualtyApprovalCount() {
		return personCasualtyApprovalCount;
	}

	public long getPersonCasualtyConfirmationCount() {
		return personCasualtyConfirmationCount;
	}

	public long getPersonCasualtyInformCount() {
		return personCasualtyInformCount;
	}

	public long getPersonCasualtyPaymentCount() {
		return personCasualtyPaymentCount;
	}

	public long getMedicalExpenseSurveyCount() {
		return medicalExpenseSurveyCount;
	}

	public long getMedicalExpenseApprovalCount() {
		return medicalExpenseApprovalCount;
	}

	public long getMedicalExpenseConfirmationCount() {
		return medicalExpenseConfirmationCount;
	}

	public long getMedicalExpenseInformCount() {
		return medicalExpenseInformCount;
	}

	public long getMedicalExpensePaymentCount() {
		return medicalExpensePaymentCount;
	}

	public long getThirdPartySurveyCount() {
		return thirdPartySurveyCount;
	}

	public long getThirdPartyApprovalCount() {
		return thirdPartyApprovalCount;
	}

	public long getThirdPartyConfirmationCount() {
		return thirdPartyConfirmationCount;
	}

	public long getThirdPartyInformCount() {
		return thirdPartyInformCount;
	}

	public long getThirdPartyPaymentCount() {
		return thirdPartyPaymentCount;
	}

	public long getLifeClaimRequestCount() {
		return lifeClaimRequestCount;
	}

	public long getLifeClaimSurveyCount() {
		return lifeClaimSurveyCount;
	}

	public long getLifeClaimApprovalCount() {
		return lifeClaimApprovalCount;
	}

	public long getLifeClaimConfirmationCount() {
		return lifeClaimConfirmationCount;
	}

	public long getLifeClaimInformCount() {
		return lifeClaimInformCount;
	}

	public long getLifeClaimPaymentCount() {
		return lifeClaimPaymentCount;
	}

	public long getLifeDisabilityClaimRequestCount() {
		return lifeDisabilityClaimRequestCount;
	}

	public long getLifeDisabilityClaimApprovalCount() {
		return lifeDisabilityClaimApprovalCount;
	}

	public long getLifeDisabilityClaimConfirmationCount() {
		return lifeDisabilityClaimConfirmationCount;
	}

	public long getLifeDisabilityClaimInformCount() {
		return lifeDisabilityClaimInformCount;
	}

	public long getLifeDisabilityClaimPaymentCount() {
		return lifeDisabilityClaimPaymentCount;
	}

	public long getCoinsuranceFromConfirmationCount() {
		return coinsuranceFromConfirmationCount;
	}

	public long getCoinsuranceFromPaymentCount() {
		return coinsuranceFromPaymentCount;
	}

	public long getCoinsuranceToPaymentCount() {
		return coinsuranceToPaymentCount;
	}

	public long getAgentCommissionPaymentCount() {
		return agentCommissionPaymentCount;
	}

	public long getStaffCommissionPaymentCount() {
		return staffCommissionPaymentCount;
	}

	/* For Life Surrender */
	public long getLifeSurrenderApprovalCount() {
		return lifeSurrenderApprovalCount;
	}

	public long getLifeSurrenderInformCount() {
		return lifeSurrenderInformCount;
	}

	public long getLifeSurrenderConfirmationCount() {
		return lifeSurrenderConfirmationCount;
	}

	public long getLifeSurrenderPaymentCount() {
		return lifeSurrenderPaymentCount;
	}

	public long getLifeSurrenderIssueCount() {
		return lifeSurrenderIssueCount;
	}

	/* For Short Term Life Surrender */
	public long getShortTermLifeSurrenderApprovalCount() {
		return shortTermLifeSurrenderApprovalCount;
	}

	public long getShortTermLifeSurrenderConfirmationCount() {
		return shortTermLifeSurrenderConfirmationCount;
	}

	public long getShortTermLifeSurrenderInformCount() {
		return shortTermLifeSurrenderInformCount;
	}

	public long getShortTermLifeSurrenderPaymentCount() {
		return shortTermLifeSurrenderPaymentCount;
	}

	public long getShortTermLifeSurrenderIssueCount() {
		return shortTermLifeSurrenderIssueCount;
	}

	/* For Life PaidUp */

	public long getLifePaidUpApprovalCount() {
		return lifePaidUpApprovalCount;
	}

	public long getLifePaidUpInformCount() {
		return lifePaidUpInformCount;
	}

	public long getLifePaidUpConfirmationCount() {
		return lifePaidUpConfirmationCount;
	}

	/* For ShortTerm Life PaidUp */

	public long getShortTermLifePaidUpApprovalCount() {
		return shortTermLifePaidUpApprovalCount;
	}

	public long getShortTermLifePaidUpInformCount() {
		return shortTermLifePaidUpInformCount;
	}

	public long getShortTermLifePaidUpConfirmationCount() {
		return shortTermLifePaidUpConfirmationCount;
	}

	/* Short Term Endowment Life */
	public long getShortEndowmentSurveyCount() {
		return shortEndowmentSurveyCount;
	}

	public long getShortEndowmentApprovalCount() {
		return shortEndowmentApprovalCount;
	}

	public ISnakeBitePolicyService getSnakeBitePolicyService() {
		return snakeBitePolicyService;
	}

	public LifeSurrenderProposal getSurrenderProposal() {
		return surrenderProposal;
	}

	public List<LPP001> getShortTermlifePaidUpConfirmationList() {
		return ShortTermlifePaidUpConfirmationList;
	}

	public List<LPP001> getShortTermlifePaidUpInformList() {
		return ShortTermlifePaidUpInformList;
	}

	public long getShortEndowmentInformCount() {
		return shortEndowmentInformCount;
	}

	public long getShortEndowmentConfirmationCount() {
		return shortEndowmentConfirmationCount;
	}

	public long getShortEndowmentPaymentCount() {
		return shortEndowmentPaymentCount;
	}

	public long getShortEndowmentIssueCount() {
		return shortEndowmentIssueCount;
	}

	/*******************************
	 * Load Method for MotorProposal UnderWriting
	 ************************************************/

	public void loadMedicalApproval() {
		clearList();
		medicalApprovalList = proxyService.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalInform() {
		clearList();
		medicalInformList = proxyService.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalConfirmation() {
		clearList();
		medicalConfirmationList = proxyService
				.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalPayment() {
		clearList();
		medicalPaymentList = proxyService.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalIssue() {
		clearList();
		medicalIssueList = proxyService.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/* Load Method for microhealth , criticalillness and health */

	/* Group Micro Health */

	public void loadGroupMicroHealthApproval() {
		clearList();
		groupMicroHealthApprovalList = proxyService.findGroupMicroHealth(new WorkflowCriteria(WorkflowReferenceType.GROUP_MICROHEALTH, WorkflowTask.APPROVAL, user));
	}

	public void loadGroupMicroHealthConfirm() {
		clearList();
		groupMicroHealthConfirmationList = proxyService.findGroupMicroHealth(new WorkflowCriteria(WorkflowReferenceType.GROUP_MICROHEALTH, WorkflowTask.CONFIRMATION, user));

	}

	public void loadGroupMicroHealthPayment() {
		clearList();
		groupMicroHealthPaymentList = proxyService.findGroupMicroHealth(new WorkflowCriteria(WorkflowReferenceType.GROUP_MICROHEALTH, WorkflowTask.PAYMENT, user));
	}

	/* Health */

	public void loadHealthSurvey() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualHealthInsuranceId(), ProductIDConfig.getGroupHealthInsuranceId());
		healthSurveyList = proxyService.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.HEALTH_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user),
				productIdList);
	}

	public void loadHealthApprove() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualHealthInsuranceId(), ProductIDConfig.getGroupHealthInsuranceId());
		healthApprovalList = proxyService
				.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.HEALTH_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadHealthInform() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualHealthInsuranceId(), ProductIDConfig.getGroupHealthInsuranceId());
		healthInformList = proxyService.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.HEALTH_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user),
				productIdList);
	}

	public void loadHealthConfirm() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualHealthInsuranceId(), ProductIDConfig.getGroupHealthInsuranceId());
		healthConfirmationList = proxyService
				.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.HEALTH_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadHealthIssue() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualHealthInsuranceId(), ProductIDConfig.getGroupHealthInsuranceId());
		healthIssueList = proxyService.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.HEALTH_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user),
				productIdList);
	}

	public void loadHealthPayment() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualHealthInsuranceId(), ProductIDConfig.getGroupHealthInsuranceId());
		healthPaymentList = proxyService.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.HEALTH_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user),
				productIdList);
	}

	/* Micro Health */

	public void loadMicroHealthSurvey() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getMicroHealthInsurance());
		microHealthSurveyList = proxyService
				.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.MICRO_HEALTH_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadMicroHealthApprove() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getMicroHealthInsurance());
		microHealthApprovalList = proxyService
				.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.MICRO_HEALTH_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadMicroHealthInform() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getMicroHealthInsurance());
		microHealthInformList = proxyService
				.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.MICRO_HEALTH_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadMicroHealthConfirm() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getMicroHealthInsurance());
		microHealthConfirmationList = proxyService.findMEDICAL002ByProduct(
				new WorkflowCriteria(WorkflowReferenceType.MICRO_HEALTH_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadMicroHealthIssue() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getMicroHealthInsurance());
		microHealthIssueList = proxyService
				.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.MICRO_HEALTH_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadMicroHealthPayment() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getMicroHealthInsurance());
		microHealthPaymentList = proxyService
				.findMEDICAL002ByProduct(new WorkflowCriteria(WorkflowReferenceType.MICRO_HEALTH_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user), productIdList);
	}

	/* Critical Illness */

	public void loadCriticalIllnessSurvey() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualCriticalIllness_Id(), ProductIDConfig.getGroupCriticalIllness_Id());
		criticalIllnessSurveyList = proxyService.findMEDICAL002ByProduct(
				new WorkflowCriteria(WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadCriticalIllnessApprove() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualCriticalIllness_Id(), ProductIDConfig.getGroupCriticalIllness_Id());
		criticalIllnessApprovalList = proxyService.findMEDICAL002ByProduct(
				new WorkflowCriteria(WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadCriticalIllnessInform() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualCriticalIllness_Id(), ProductIDConfig.getGroupCriticalIllness_Id());
		criticalIllnessInformList = proxyService.findMEDICAL002ByProduct(
				new WorkflowCriteria(WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadCriticalIllnessConfirm() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualCriticalIllness_Id(), ProductIDConfig.getGroupCriticalIllness_Id());
		criticalIllnessConfirmationList = proxyService.findMEDICAL002ByProduct(
				new WorkflowCriteria(WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadCriticalIllnessIssue() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualCriticalIllness_Id(), ProductIDConfig.getGroupCriticalIllness_Id());
		criticalIllnessIssueList = proxyService.findMEDICAL002ByProduct(
				new WorkflowCriteria(WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadCriticalIllnessPayment() {
		clearList();
		List<String> productIdList = Arrays.asList(ProductIDConfig.getIndividualCriticalIllness_Id(), ProductIDConfig.getGroupCriticalIllness_Id());
		criticalIllnessPaymentList = proxyService.findMEDICAL002ByProduct(
				new WorkflowCriteria(WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user), productIdList);
	}

	public void loadGroupFarmerConfirmation() {
		clearList();
		groupFarmerConfirmationList = proxyService.find_GF001(new WorkflowCriteria(WorkflowReferenceType.GROUPFARMER_PROPOSAL, WorkflowTask.CONFIRMATION, null, user));
	}

	public void loadGroupFarmerPayment() {
		clearList();
		groupFarmerPaymentList = proxyService.find_GF001(new WorkflowCriteria(WorkflowReferenceType.GROUPFARMER_PROPOSAL, WorkflowTask.PAYMENT, null, user));
	}

	/*******************************
	 * Load Method for FireProposal UnderWriting
	 ************************************************/

	/*******************************
	 * Load Method for LifeProposal UnderWriting
	 ************************************************/

	public void loadLifeSurvey() {
		clearList();
		lifeSurveyList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void loadLifeApproval() {
		clearList();
		lifeApprovalList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void loadLifeInform() {
		clearList();
		lifeInformList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void loadLifeConfirmation() {
		clearList();
		lifeConfirmationList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void loadLifePayment() {
		clearList();
		lifePaymentList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void loadLifeIssue() {
		clearList();
		lifeIssueList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/*******************************
	 * Load Method for FidelityProposal UnderWriting
	 ************************************************/

	/**********************************
	 * for hospitalized Claim
	 ****************************************************/

	public void loadMedicalClaimSurvey() {
		clearList();
		medicalClaimSurveyList = proxyService.find_MEDCLM002(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_CLAIM, WorkflowTask.CLAIM_SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalApprove() {
		clearList();
		medicalApprovalList = proxyService.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	/*******************************
	 * Load Method for MedicalProposal UnderWriting
	 ************************************************/

	public void loadMedicalClaimConfirm() {
		clearList();
		medicalClaimConfirmList = proxyService
				.find_MEDCLM002(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_CLAIM, WorkflowTask.CLAIM_CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalSurvey() {
		clearList();
		medicalSurveyList = proxyService.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalClaimApprove() {
		clearList();
		medicalClaimApproveList = proxyService
				.find_MEDCLM002(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_CLAIM, WorkflowTask.CLAIM_APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalClaimInform() {
		clearList();
		medicalClaimInformList = proxyService.find_MEDCLM002(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_CLAIM, WorkflowTask.CLAIM_INFORM, ProposalType.UNDERWRITING, user));
	}

	public void loadMedicalClaimPayment() {
		clearList();
		medicalClaimPaymentList = proxyService
				.find_MEDCLM002(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_CLAIM, WorkflowTask.CLAIM_PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void loadShortTermEndowmentEndorsementSurvey() {
		clearList();
		shortTermEndowmentEndorseSurveyList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_SURVEY, ProposalType.ENDORSEMENT, user));
	}

	public void loadShortTermEndowmentEndorsementApproval() {
		clearList();
		shortTermEndowmentEndorseApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_APPROVAL, ProposalType.ENDORSEMENT, user));
	}

	public void loadShortTermEndowmentEndorsementInform() {
		clearList();
		shortTermEndowmentEndorseInformList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_INFORM, ProposalType.ENDORSEMENT, user));
	}

	public void loadShortTermEndowmentEndorsementConfirmation() {
		clearList();
		shortTermEndowmentEndorseConfirmationList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_CONFIRMATION, ProposalType.ENDORSEMENT, user));
	}

	public void loadShortTermEndowmentEndorsementPayment() {
		clearList();
		shortTermEndowmentEndorsePaymentList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_PAYMENT, ProposalType.ENDORSEMENT, user));
	}

	public void loadShortTermEndowmentEndorsementIssue() {
		clearList();
		shortTermEndowmentEndorseIssueList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_ISSUING, ProposalType.ENDORSEMENT, user));
	}

	/*******************************
	 * Medical Renewal
	 ************************************************/

	public void loadMedicalRenewalSurvey() {
		clearList();
		medicalRenewalSurveyList = proxyService
				.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, WorkflowTask.RENEWAL_SURVEY, ProposalType.RENEWAL, user));
	}

	public void loadMedicalRenewalInform() {
		clearList();
		medicalRenewalInformList = proxyService
				.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, WorkflowTask.RENEWAL_INFORM, ProposalType.RENEWAL, user));
	}

	public void loadMedicalRenewalApproval() {
		clearList();
		medicalRenewalApprovalList = proxyService
				.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, WorkflowTask.RENEWAL_APPROVAL, ProposalType.RENEWAL, user));
	}

	public void loadMedicalRenewalConfirm() {
		clearList();
		medicalRenewalConfirmationList = proxyService
				.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, WorkflowTask.RENEWAL_CONFIRMATION, ProposalType.RENEWAL, user));
	}

	public void loadMedicalRenewalPayment() {
		clearList();
		medicalRenewalPaymentList = proxyService
				.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, WorkflowTask.RENEWAL_PAYMENT, ProposalType.RENEWAL, user));
	}

	public void loadMedicalRenewalIssue() {
		clearList();
		medicalRenewalIssueList = proxyService
				.find_MED001(new WorkflowCriteria(WorkflowReferenceType.MEDICAL_RENEWAL_PROPOSAL, WorkflowTask.RENEWAL_ISSUING, ProposalType.RENEWAL, user));
	}

	/*******************************
	 * Load Method for CargoProposal Endorsement
	 ************************************************/

	/*******************************
	 * Load Method for FireProposal Endorsement
	 ************************************************/

	/*******************************
	 * Load Method for LifeProposal Endorsement
	 ************************************************/

	public void loadLifeEndorsementSurvey() {
		clearList();
		lifeEndorseSurveyList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_SURVEY, ProposalType.ENDORSEMENT, user));
	}

	public void loadLifeEndorsementApproval() {
		clearList();
		lifeEndorseApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_APPROVAL, ProposalType.ENDORSEMENT, user));
	}

	public void loadLifeEndorsementConfirmation() {
		clearList();
		lifeEndorseConfirmationList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_CONFIRMATION, ProposalType.ENDORSEMENT, user));
	}

	public void loadLifeEndorsementPayment() {
		clearList();
		lifeEndorsePaymentList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_PAYMENT, ProposalType.ENDORSEMENT, user));
	}

	public void loadLifeEndorsementIssue() {
		clearList();
		lifeEndorseIssueList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.ENDORSEMENT_ISSUING, ProposalType.ENDORSEMENT, user));
	}

	/*******************************
	 * Load Method for LifeProposal Renewal
	 ************************************************/

	public void loadLifeRenewalSurvey() {
		clearList();
		lifeRenewalSurveyList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.RENEWAL_SURVEY, ProposalType.RENEWAL, user));
	}

	public void loadLifeRenewalApproval() {
		clearList();
		lifeRenewalApprovalList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.RENEWAL_APPROVAL, ProposalType.RENEWAL, user));
	}

	public void loadLifeRenewalInform() {
		clearList();
		lifeRenewalInformList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.RENEWAL_INFORM, ProposalType.RENEWAL, user));
	}

	public void loadLifeRenewalConfirmation() {
		clearList();
		lifeRenewalConfirmationList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.RENEWAL_CONFIRMATION, ProposalType.RENEWAL, user));
	}

	public void loadLifeRenewalPayment() {
		clearList();
		lifeRenewalPaymentList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.RENEWAL_PAYMENT, ProposalType.RENEWAL, user));
	}

	public void loadLifeRenewalIssue() {
		clearList();
		lifeRenewalIssueList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PROPOSAL, WorkflowTask.RENEWAL_ISSUING, ProposalType.RENEWAL, user));
	}

	/*******************************
	 * Load Method for LifeProposal Renewal
	 ************************************************/

	/*******************************
	 * Load Method for FireProposal Renewal
	 ************************************************/

	/*******************************
	 * Load Method for Damaged Vehicle (Motor Claim)
	 ************************************************/

	/*******************************
	 * Load Method for WindScreen(Motor Claim)
	 ************************************************/

	/*******************************
	 * Load Method for Towing(Motor Claim)
	 ************************************************/

	/*******************************
	 * Load Method for ThirdPartyProperty(Motor Claim)
	 ************************************************/

	/*******************************
	 * Load Method for PersomCasualty (Motor Claim)
	 ************************************************/

	/*******************************
	 * Load Method for MedicalExpense (Motor Claim)
	 ************************************************/

	/*******************************
	 * Load Method for Life Claim
	 ************************************************/

	public void loadLifeClaimRequest() {
		clearList();
		lifeClaimRequestList = proxyService.find_LCLD001(new WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM, WorkflowTask.CLAIM_REQUEST, null, user));
	}

	public void loadLifeClaimSurvey() {
		clearList();
		lifeClaimSurveyList = proxyService.find_LCP001(new WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM, WorkflowTask.CLAIM_SURVEY, null, user));
	}

	public void loadLifeClaimApproval() {
		clearList();
		// lifeClaimApprovalList = proxyService.find_LCLD001(new
		// WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM,
		// WorkflowTask.CLAIM_APPROVAL, null, user));
		lifeClaimApprovalList = proxyService.find_LCP001(new WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM, WorkflowTask.CLAIM_APPROVAL, null, user));
	}

	public void loadLifeClaimInform() {
		clearList();
		// lifeClaimInformList = proxyService.find_LCLD001(new
		// WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM,
		// WorkflowTask.CLAIM_INFORM, null, user));
		lifeClaimInformList = proxyService.find_LCP001(new WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM, WorkflowTask.CLAIM_INFORM, null, user));

	}

	public void loadLifeClaimConfirmation() {
		clearList();
		// lifeClaimConfirmationList = proxyService.find_LCLD001(new
		// WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM,
		// WorkflowTask.CLAIM_CONFIRMATION, null, user));
		lifeClaimConfirmationList = proxyService.find_LCP001(new WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM, WorkflowTask.CLAIM_CONFIRMATION, null, user));

	}

	public void loadLifeClaimPayment() {
		clearList();
		// lifeClaimPaymentList = proxyService.find_LCB001(new
		// WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM,
		// WorkflowTask.CLAIM_PAYMENT, null, user));
		lifeClaimPaymentList = proxyService.find_LCP001(new WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM, WorkflowTask.CLAIM_PAYMENT, null, user));

	}

	/*******************************
	 * Load Method for Life Disability Claim
	 ************************************************/

	public void loadLifeDisabilityClaimRequest() {
		clearList();
		lifeDisabilityClaimRequestList = proxyService.find_LCLD001(new WorkflowCriteria(WorkflowReferenceType.LIFE_DIS_CLAIM, WorkflowTask.CLAIM_REQUEST, null, user));
	}

	public void loadLifeDisabilityClaimApproval() {
		clearList();
		lifeDisabilityClaimApprovalList = proxyService.find_LCLD001(new WorkflowCriteria(WorkflowReferenceType.LIFE_DIS_CLAIM, WorkflowTask.CLAIM_APPROVAL, null, user));
	}

	public void loadLifeDisabilityClaimInform() {
		clearList();
		lifeDisabilityClaimInformList = proxyService.find_LCLD001(new WorkflowCriteria(WorkflowReferenceType.LIFE_DIS_CLAIM, WorkflowTask.CLAIM_INFORM, null, user));
	}

	public void loadLifeDisabilityClaimConfirmation() {
		clearList();
		lifeDisabilityClaimConfirmationList = proxyService.find_LCLD001(new WorkflowCriteria(WorkflowReferenceType.LIFE_DIS_CLAIM, WorkflowTask.CLAIM_CONFIRMATION, null, user));
	}

	public void loadLifeDisabilityClaimPayment() {
		clearList();
		lifeDisabilityClaimPaymentList = proxyService.find_LCLD001(new WorkflowCriteria(WorkflowReferenceType.LIFE_DIS_CLAIM, WorkflowTask.CLAIM_PAYMENT, null, user));
	}

	/******************************
	 * Load Method for Life Surrender Proposal
	 */
	public void loadLifeSurrenderApproval() {
		clearList();
		lifeSurrenderApprovalList = proxyService.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.LIFESURRENDER_PROPOSAL, WorkflowTask.APPROVAL, null, user));
	}

	public void loadLifeSurrenderInform() {
		clearList();
		lifeSurrenderInformList = proxyService.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.LIFESURRENDER_PROPOSAL, WorkflowTask.INFORM, null, user));
	}

	public void loadLifeSurrenderConfirmation() {
		clearList();
		lifeSurrenderConfirmationList = proxyService.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.LIFESURRENDER_PROPOSAL, WorkflowTask.CONFIRMATION, null, user));
	}

	public void loadLifeSurrenderPayment() {
		clearList();
		lifeSurrenderPaymentList = proxyService.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.LIFESURRENDER_PROPOSAL, WorkflowTask.PAYMENT, null, user));
	}

	public void loadLifeSurrenderIssue() {
		clearList();
		lifeSurrenderIssueList = proxyService.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.LIFESURRENDER_PROPOSAL, WorkflowTask.ISSUING, null, user));
	}

	/******************************
	 * Load Method for Short Term Life Surrender Proposal
	 */
	public void loadShortTermLifeSurrenderApproval() {
		clearList();
		shortTermLifeSurrenderApprovalList = proxyService
				.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL, WorkflowTask.APPROVAL, null, user));
	}

	public void loadShortTermLifeSurrenderInform() {
		clearList();
		shortTermLifeSurrenderInformList = proxyService
				.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL, WorkflowTask.INFORM, null, user));
	}

	public void loadShortTermLifeSurrenderConfirmation() {
		clearList();
		shortTermLifeSurrenderConfirmationList = proxyService
				.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL, WorkflowTask.CONFIRMATION, null, user));
	}

	public void loadShortTermLifeSurrenderPayment() {
		clearList();
		shortTermLifeSurrenderPaymentList = proxyService
				.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL, WorkflowTask.PAYMENT, null, user));
	}

	public void loadShortTermLifeSurrenderIssue() {
		clearList();
		shortTermLifeSurrenderIssueList = proxyService
				.find_LSP001(new WorkflowCriteria(WorkflowReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL, WorkflowTask.ISSUING, null, user));
	}

	/******************************
	 * Load Method for Life PaidUp Proposal
	 */

	/******************************
	 * Load Method for Life PaidUp Proposal
	 */

	public void loadLifePaidUpApproval() {
		clearList();
		lifePaidUpApprovalList = proxyService.find_LPP001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PAIDUP_PROPOSAL, WorkflowTask.APPROVAL, null, user));
	}

	public void loadLifePaidUpInform() {
		clearList();
		lifePaidUpInformList = proxyService.find_LPP001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PAIDUP_PROPOSAL, WorkflowTask.INFORM, null, user));
	}

	public void loadLifePaidUpConfirmation() {
		clearList();
		lifePaidUpConfirmationList = proxyService.find_LPP001(new WorkflowCriteria(WorkflowReferenceType.LIFE_PAIDUP_PROPOSAL, WorkflowTask.CONFIRMATION, null, user));
	}

	/******************************
	 * Load Method for ShortTerm Life PaidUp Proposal
	 */
	public void loadShortTermLifePaidUpApproval() {
		clearList();
		ShortTermlifePaidUpApprovalList = proxyService.find_LPP001(new WorkflowCriteria(WorkflowReferenceType.SHORTTERM_LIFE_PAIDUP_PROPOSAL, WorkflowTask.APPROVAL, null, user));
	}

	public void loadShortTermLifePaidUpInform() {
		clearList();
		ShortTermlifePaidUpInformList = proxyService.find_LPP001(new WorkflowCriteria(WorkflowReferenceType.SHORTTERM_LIFE_PAIDUP_PROPOSAL, WorkflowTask.INFORM, null, user));
	}

	public void loadShortTermLifePaidUpConfirmation() {
		clearList();
		ShortTermlifePaidUpConfirmationList = proxyService
				.find_LPP001(new WorkflowCriteria(WorkflowReferenceType.SHORTTERM_LIFE_PAIDUP_PROPOSAL, WorkflowTask.CONFIRMATION, null, user));
	}

	/*******************************
	 * Load Method for Coinsurance Claim
	 ************************************************/

	public void loadCoinsuranceFromOtherCompanyConfirmation() {
		clearList();
		coinsuranceFromConfirmationList = proxyService.find_CIN001(WorkflowReferenceType.COINSURANCE, CoinsuranceType.IN, WorkflowTask.COINSURANCE_CONFIRMATION, user.getId());
	}

	public void loadCoinsuranceFromOtherCompanyPayment() {
		clearList();
		coinsuranceFromPaymentList = proxyService.find_CIN001(WorkflowReferenceType.COINSURANCE, CoinsuranceType.IN, WorkflowTask.COINSURANCE_PAYMENT, user.getId());
	}

	public void loadCoinsuranceToOtherCompanyPayment() {
		clearList();
		coinsuranceToPaymentList = proxyService.find_CIN002(WorkflowReferenceType.COINSURANCE, CoinsuranceType.OUT, WorkflowTask.COINSURANCE_PAYMENT, user.getId());
	}

	/***************************************
	 * Load Method for Agent Commission Payment
	 ************************************************/

	public void loadAgentCommissionPayment() {
		clearList();
		agentCommissionPaymentList = proxyService
				.findAgentCommissionForDashboard(new WorkflowCriteria(WorkflowReferenceType.AGENT_COMMISSION, WorkflowTask.AGENT_COMMISSION_PAYMENT, null, user));
	}

	public void loadStaffCommissionPayment() {
		clearList();
		staffCommissionPaymentList = proxyService
				.findStaffCommissionForDashboard(new WorkflowCriteria(WorkflowReferenceType.STAFF_COMMISSION, WorkflowTask.STAFF_COMMISSION_PAYMENT, null, user));
	}

	/*****************************************
	 * Load Method for SnakeBitePolicy UnderWriting
	 ****************************************/
	public void loadSnakeBitePayment() {
		clearList();
		snakeBitePaymentList = proxyService.findSnakeBitePolicyForDashboard(new WorkflowCriteria(WorkflowReferenceType.SNAKEBITEPOLICY, WorkflowTask.PAYMENT, null, user));
	}

	/*****************************************
	 * Load Method for Travel Proposal
	 ****************************************/
	public void loadTravelProposalConfrimation() {
		clearList();
		travelProposalConfirmationList = proxyService
				.findTravelProposalForDashboard(new WorkflowCriteria(WorkflowReferenceType.TRAVEL_PROPOSAL, WorkflowTask.CONFIRMATION, null, user));
	}

	public void loadTravelProposalPayment() {
		clearList();
		travelProposalPaymentList = proxyService.findTravelProposalForDashboard(new WorkflowCriteria(WorkflowReferenceType.TRAVEL_PROPOSAL, WorkflowTask.PAYMENT, null, user));
	}

	/*******************************
	 * Load Method for Farmer Underwriting
	 ************************************************/

	public void loadFarmerSurvey() {
		clearList();
		farmerSurveyList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.FARMER_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void loadFarmerApproval() {
		clearList();
		farmerApprovalList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.FARMER_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void loadFarmerInform() {
		clearList();
		farmerInformList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.FARMER_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void loadFarmerConfirmation() {
		clearList();
		farmerConfirmationList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.FARMER_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void loadFarmerPayment() {
		clearList();
		farmerPaymentList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.FARMER_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void loadFarmerIssue() {
		clearList();
		farmerIssueList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.FARMER_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/*******************************
	 * Load Method for LifeProposal UnderWriting
	 ************************************************/

	public void loadShortEndowmentSurvey() {
		clearList();
		shortEndowmentSurveyList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void loadShortEndowmentApproval() {
		clearList();
		shortEndowmentApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void loadShortEndowmentInform() {
		clearList();
		shortEndowmentInformList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void loadShortEndowmentConfirmation() {
		clearList();
		shortEndowmentConfirmationList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void loadShortEndowmentPayment() {
		clearList();
		shortEndowmentPaymentList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void loadShortEndowmentIssue() {
		clearList();
		shortEndowmentIssueList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/****************************************************
	 * List getter Method
	 ****************************************************/
	public List<MED001> getMedicalSurveyList() {
		return medicalSurveyList;
	}

	public List<MED001> getMedicalIssueList() {
		return medicalIssueList;
	}

	public List<MED001> getMedicalApprovalList() {
		return medicalApprovalList;
	}

	public List<MED001> getMedicalInformList() {
		return medicalInformList;
	}

	public List<MED001> getMedicalConfirmationList() {
		return medicalConfirmationList;
	}

	public List<MED001> getMedicalPaymentList() {
		return medicalPaymentList;
	}

	public List<MED001> getMedicalRenewalSurveyList() {
		return medicalRenewalSurveyList;
	}

	public List<MED001> getMedicalRenewalInformList() {
		return medicalRenewalInformList;
	}

	public List<MED001> getMedicalRenewalApprovalList() {
		return medicalRenewalApprovalList;
	}

	public List<MED001> getMedicalRenewalConfirmationList() {
		return medicalRenewalConfirmationList;
	}

	public List<MED001> getMedicalRenewalPaymentList() {
		return medicalRenewalPaymentList;
	}

	public List<MED001> getMedicalRenewalIssueList() {
		return medicalRenewalIssueList;
	}

	public List<LIF001> getStudentLifeSurveyList() {
		return studentLifeSurveyList;
	}

	public List<LIF001> getStudentLifeApprovalList() {
		return studentLifeApprovalList;
	}

	public List<LIF001> getStudentLifeConfirmationList() {
		return studentLifeConfirmationList;
	}

	public List<LIF001> getStudentLifeInformList() {
		return studentLifeInformList;
	}

	public List<LIF001> getStudentLifePaymentList() {
		return studentLifePaymentList;
	}

	public List<LIF001> getStudentLifeIssueList() {
		return studentLifeIssueList;
	}

	public long getStudentLifeSurveyCount() {
		return studentLifeSurveyCount;
	}

	public long getStudentLifeApprovalCount() {
		return studentLifeApprovalCount;
	}

	public long getStudentLifeInformCount() {
		return studentLifeInformCount;
	}

	public long getStudentLifeConfirmationCount() {
		return studentLifeConfirmationCount;
	}

	public long getStudentLifePaymentCount() {
		return studentLifePaymentCount;
	}

	public long getStudentLifeIssueCount() {
		return studentLifeIssueCount;
	}

	/***************** Medical Hospitalization Claim Proposal **************/
	public long getMedicalClaimConfirmCount() {
		return medicalClaimConfirmCount;
	}

	public long getMedicalClaimSurveyCount() {
		return medicalClaimSurveyCount;
	}

	public long getMedicalClaimApproveCount() {
		return medicalClaimApproveCount;
	}

	public long getMedicalClaimInformCount() {
		return medicalClaimInformCount;
	}

	public long getMedicalClaimPaymentCount() {
		return medicalClaimPaymentCount;
	}

	public List<LifePolicyBilling> getLifePolicyBillingList() {
		return lifePolicyBillingList;
	}

	public List<SnakeBitePolicyDTO> getSnakeBitePaymentList() {
		return snakeBitePaymentList;
	}

	public List<LIF001> getLifeSurveyList() {
		return lifeSurveyList;
	}

	public List<LIF001> getLifeApprovalList() {
		return lifeApprovalList;
	}

	public List<LIF001> getLifeConfirmationList() {
		return lifeConfirmationList;
	}

	public List<LIF001> getLifeInformList() {
		return lifeInformList;
	}

	public List<LIF001> getLifePaymentList() {
		return lifePaymentList;
	}

	public List<LIF001> getLifeIssueList() {
		return lifeIssueList;
	}

	public List<LIF001> getLifeEndorseSurveyList() {
		return lifeEndorseSurveyList;
	}

	public List<LIF001> getLifeEndorseApprovalList() {
		return lifeEndorseApprovalList;
	}

	public List<LIF001> getLifeEndorseConfirmationList() {
		return lifeEndorseConfirmationList;
	}

	public List<LIF001> getLifeEndorsePaymentList() {
		return lifeEndorsePaymentList;
	}

	public List<LIF001> getLifeEndorseIssueList() {
		return lifeEndorseIssueList;
	}

	public List<LIF001> getLifeRenewalSurveyList() {
		return lifeRenewalSurveyList;
	}

	public List<LIF001> getLifeRenewalApprovalList() {
		return lifeRenewalApprovalList;
	}

	public List<LIF001> getLifeRenewalConfirmationList() {
		return lifeRenewalConfirmationList;
	}

	public List<LIF001> getLifeRenewalInformList() {
		return lifeRenewalInformList;
	}

	public List<LIF001> getLifeRenewalPaymentList() {
		return lifeRenewalPaymentList;
	}

	public List<LIF001> getLifeRenewalIssueList() {
		return lifeRenewalIssueList;
	}

	public List<MCL001> getThirdPartySurveyList() {
		return thirdPartySurveyList;
	}

	public List<MCL001> getThirdPartyApprovalList() {
		return thirdPartyApprovalList;
	}

	public List<MCL001> getThirdPartyConfirmationList() {
		return thirdPartyConfirmationList;
	}

	public List<MCL001> getThirdPartyInformList() {
		return thirdPartyInformList;
	}

	public List<MCL001> getThirdPartyPaymentList() {
		return thirdPartyPaymentList;
	}

	public List<MCL001> getPersonCasualtySurveyList() {
		return personCasualtySurveyList;
	}

	public List<MCL001> getPersonCasualtyApprovalList() {
		return personCasualtyApprovalList;
	}

	public List<MCL001> getPersonCasualtyConfirmationList() {
		return personCasualtyConfirmationList;
	}

	public List<MCL001> getPersonCasualtyInformList() {
		return personCasualtyInformList;
	}

	public List<MCL001> getPersonCasualtyPaymentList() {
		return personCasualtyPaymentList;
	}

	public List<MCL001> getMedicalExpenseSurveyList() {
		return medicalExpenseSurveyList;
	}

	public List<MCL001> getMedicalExpenseApprovalList() {
		return medicalExpenseApprovalList;
	}

	public List<MCL001> getMedicalExpenseConfirmationList() {
		return medicalExpenseConfirmationList;
	}

	public List<MCL001> getMedicalExpenseInformList() {
		return medicalExpenseInformList;
	}

	public List<MCL001> getMedicalExpensePaymentList() {
		return medicalExpensePaymentList;
	}

	public List<LCLD001> getLifeClaimRequestList() {
		return lifeClaimRequestList;
	}

	public List<LCP001> getLifeClaimSurveyList() {
		return lifeClaimSurveyList;
	}

	public List<LCP001> getLifeClaimApprovalList() {
		return lifeClaimApprovalList;
	}

	public List<LCP001> getLifeClaimConfirmationList() {
		return lifeClaimConfirmationList;
	}

	public List<LCP001> getLifeClaimInformList() {
		return lifeClaimInformList;
	}

	public List<LCP001> getLifeClaimPaymentList() {
		return lifeClaimPaymentList;
	}

	public List<LCLD001> getLifeDisabilityClaimRequestList() {
		return lifeDisabilityClaimRequestList;
	}

	public List<LCLD001> getLifeDisabilityClaimApprovalList() {
		return lifeDisabilityClaimApprovalList;
	}

	public List<LCLD001> getLifeDisabilityClaimConfirmationList() {
		return lifeDisabilityClaimConfirmationList;
	}

	public List<LCLD001> getLifeDisabilityClaimInformList() {
		return lifeDisabilityClaimInformList;
	}

	public List<LCLD001> getLifeDisabilityClaimPaymentList() {
		return lifeDisabilityClaimPaymentList;
	}

	public List<CIN001> getCoinsuranceFromConfirmationList() {
		return coinsuranceFromConfirmationList;
	}

	public List<CIN001> getCoinsuranceFromPaymentList() {
		return coinsuranceFromPaymentList;
	}

	public List<CIN002> getCoinsuranceToPaymentList() {
		return coinsuranceToPaymentList;
	}

	public List<LifePolicyBilling> getLifePolicyPremiumList() {
		return lifePolicyBillingList;
	}

	public List<AgentCommissionDTO> getAgentCommissionPaymentList() {
		return agentCommissionPaymentList;
	}

	public List<StaffCommissionDTO> getStaffCommissionPaymentList() {
		return staffCommissionPaymentList;
	}

	/* For Life Surrender */
	public List<LSP001> getLifeSurrenderApprovalList() {
		return lifeSurrenderApprovalList;
	}

	public List<LSP001> getLifeSurrenderInformList() {
		return lifeSurrenderInformList;
	}

	public List<LSP001> getLifeSurrenderConfirmationList() {
		return lifeSurrenderConfirmationList;
	}

	public List<LSP001> getLifeSurrenderPaymentList() {
		return lifeSurrenderPaymentList;
	}

	public List<LSP001> getLifeSurrenderIssueList() {
		return lifeSurrenderIssueList;
	}

	/* Short Term Life Surrender */
	public List<LSP001> getShortTermLifeSurrenderApprovalList() {
		return shortTermLifeSurrenderApprovalList;
	}

	public List<LSP001> getShortTermLifeSurrenderConfirmationList() {
		return shortTermLifeSurrenderConfirmationList;
	}

	public List<LSP001> getShortTermLifeSurrenderInformList() {
		return shortTermLifeSurrenderInformList;
	}

	public List<LSP001> getShortTermLifeSurrenderPaymentList() {
		return shortTermLifeSurrenderPaymentList;
	}

	public List<LSP001> getShortTermLifeSurrenderIssueList() {
		return shortTermLifeSurrenderIssueList;
	}

	/* For Life PaidUp */
	public List<LPP001> getLifePaidUpApprovalList() {
		return lifePaidUpApprovalList;
	}

	public List<LPP001> getLifePaidUpInformList() {
		return lifePaidUpInformList;
	}

	public List<LPP001> getLifePaidUpConfirmationList() {
		return lifePaidUpConfirmationList;
	}

	/* for shortTerm PaidUp */
	public List<LPP001> getShortTermLifePaidUpApprovalList() {
		return ShortTermlifePaidUpApprovalList;
	}

	public List<LPP001> getShortTermLifePaidUpInformList() {
		return ShortTermlifePaidUpInformList;
	}

	public List<LPP001> getShortTermLifePaidUpConfirmationList() {
		return ShortTermlifePaidUpConfirmationList;
	}

	/********************************************************
	 * Outjection Method
	 ****************************************************/
	private void outjectHealthType(HealthType healthType) {
		putParam("WORKFLOWHEALTHTYPE", healthType);
	}

	private void outjectMedicalProposal(MedProDTO medProDTO) {
		putParam("medicalProposal", medProDTO);
	}

	private void outjectMedicalSurvey(MedicalSurveyDTO medicalSurveyDTO) {
		putParam("medicalSurveyDTO", medicalSurveyDTO);
	}

	private void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("travelProposal", travelProposal);
	}

	private void outjectSnakeBitePolicyDTO(SnakeBitePolicyDTO snakeBitePolicyDTO) {
		putParam("snakeBitePolicyDTO", snakeBitePolicyDTO);
	}

	private void outjectLifePolicyBilling(LifePolicyBilling lifePolicyBilling) {
		putParam("lifePolicyBilling", lifePolicyBilling);
	}

	private void outjectLifeClaim(LifeClaim lifeClaim, boolean isDisability) {
		if (isDisability) {
			putParam("lifeDisabilityClaim", lifeClaim);
		} else {
			putParam("lifeClaim", lifeClaim);
			putParam("fromDashboard", true);
		}
	}

	private void outjectLifeClaimProposal(LifeClaimProposal claimProposal) {
		putParam("lifeClaimProposal", claimProposal);
	}

	private void outjectCoinsurance(String coinsuranceId) {
		coinsurance = coinsuranceService.findById(coinsuranceId);
		putParam("Coinsurance", coinsurance);
	}

	private void outjectLifeClaimBeneficiary(LifeClaimBeneficiary lifeClaimBeneficiary) {
		putParam("lifeClaimBeneficiary", lifeClaimBeneficiary);
	}

	private void outjectLifeProposal(LifeProposal lifeProposal) {
		putParam("lifeProposal", lifeProposal);
	}

	private void outjectLifeProposalId(String lifeProposalId) {
		putParam("lifeProposalId", lifeProposalId);
	}

	private void outjectLifeSurvey(LifeSurvey lifeSurvey) {
		putParam("lifeSurvey", lifeSurvey);
	}

	private void outjectAgentCommissionDTO(AgentCommissionDTO agentCommissionDTO) {
		putParam("AgentCommissionDTO", agentCommissionDTO);
	}

	private void outjectStaffCommissionDTO(StaffCommissionDTO staffCommissionDTO) {
		putParam("StaffCommissionDTO", staffCommissionDTO);
	}

	private void outjectSurrenderProposal(LifeSurrenderProposal surrenderProposal) {
		putParam("surrenderProposal", surrenderProposal);
	}

	private void outjectPaidUpProposal(LifePaidUpProposal paidUpProposal) {
		putParam("paidUpProposal", paidUpProposal);
	}

	private void outjectGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		putParam("groupFarmerProposal", groupFarmerProposal);
	}

	/*******************************
	 * Load Method for Personal Accident UnderWriting
	 ************************************************/

	public void loadPASurvey() {
		clearList();
		paSurveyList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PA_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void loadPAApproval() {
		clearList();
		paApprovalList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PA_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void loadPAInform() {
		clearList();
		paInformList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PA_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void loadPAConfirmation() {
		clearList();
		paConfirmationList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PA_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void loadPAPayment() {
		clearList();
		paPaymentList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PA_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void loadPAIssue() {
		clearList();
		paIssueList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PA_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/*******************************
	 * Load Method for Person Travel UnderWriting
	 ************************************************/

	public void loadPersonTravelConfirmation() {
		clearList();
		personTravelConfirmationList = proxyService
				.find_TRA001(new WorkflowCriteria(WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void loadPersonTravelPayment() {
		clearList();
		personTravelPaymentList = proxyService
				.find_TRA001(new WorkflowCriteria(WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void loadPersonTravelIssue() {
		clearList();
		personTravelIssueList = proxyService.find_TRA001(new WorkflowCriteria(WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	public void loadStudentLifeSurvey() {
		clearList();
		studentLifeSurveyList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.STUDENT_LIFE_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void loadStudentLifeApproval() {
		clearList();
		studentLifeApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.STUDENT_LIFE_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void loadStudentLifeInform() {
		clearList();
		studentLifeInformList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.STUDENT_LIFE_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void loadStudentLifeConfirmation() {
		clearList();
		studentLifeConfirmationList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.STUDENT_LIFE_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void loadStudentLifePayment() {
		clearList();
		studentLifePaymentList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.STUDENT_LIFE_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void loadStudentLifeIssue() {
		clearList();
		studentLifeIssueList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.STUDENT_LIFE_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/*************************************
	 * clear list
	 *******************************************/

	private void clearList() {

		medicalClaimConfirmList = null;
		medicalIssueList = null;
		medicalSurveyList = null;
		medicalApprovalList = null;
		medicalInformList = null;
		medicalConfirmationList = null;
		medicalPaymentList = null;

		medicalRenewalSurveyList = null;
		medicalRenewalInformList = null;
		medicalRenewalApprovalList = null;
		medicalRenewalConfirmationList = null;
		medicalRenewalPaymentList = null;
		medicalRenewalIssueList = null;

		/*** Medical Hospitalization Claim ***/
		medicalClaimSurveyList = null;
		medicalClaimApproveList = null;
		medicalClaimInformList = null;
		medicalClaimPaymentList = null;

		lifeSurveyList = null;
		lifeApprovalList = null;
		lifeConfirmationList = null;
		lifeInformList = null;
		lifePaymentList = null;
		lifeIssueList = null;

		snakeBitePaymentList = null;

		lifeEndorseSurveyList = null;
		lifeEndorseApprovalList = null;
		lifeEndorseConfirmationList = null;
		lifeEndorsePaymentList = null;
		lifeEndorseIssueList = null;

		lifeRenewalSurveyList = null;
		lifeRenewalApprovalList = null;
		lifeRenewalConfirmationList = null;
		lifeRenewalInformList = null;
		lifeRenewalPaymentList = null;
		lifeRenewalIssueList = null;

		thirdPartySurveyList = null;
		thirdPartyApprovalList = null;
		thirdPartyConfirmationList = null;
		thirdPartyInformList = null;
		thirdPartyPaymentList = null;

		personCasualtySurveyList = null;
		personCasualtyApprovalList = null;
		personCasualtyConfirmationList = null;
		personCasualtyInformList = null;
		personCasualtyPaymentList = null;

		medicalExpenseSurveyList = null;
		medicalExpenseApprovalList = null;
		medicalExpenseConfirmationList = null;
		medicalExpenseInformList = null;
		medicalExpensePaymentList = null;

		lifeClaimRequestList = null;
		lifeClaimApprovalList = null;
		lifeClaimConfirmationList = null;
		lifeClaimInformList = null;
		lifeClaimPaymentList = null;

		lifeDisabilityClaimRequestList = null;
		lifeDisabilityClaimApprovalList = null;
		lifeDisabilityClaimConfirmationList = null;
		lifeDisabilityClaimInformList = null;
		lifeDisabilityClaimPaymentList = null;

		coinsuranceFromConfirmationList = null;
		coinsuranceFromPaymentList = null;
		coinsuranceToPaymentList = null;

		agentCommissionPaymentList = null;

		travelProposalConfirmationList = null;
		travelProposalPaymentList = null;

		/* For Life Surrender */
		lifeSurrenderApprovalList = null;
		lifeSurrenderInformList = null;
		lifeSurrenderConfirmationList = null;
		lifeSurrenderPaymentList = null;
		lifeSurrenderIssueList = null;

		/* For Short Term Life Surrender */
		shortTermLifeSurrenderApprovalList = null;
		shortTermLifeSurrenderConfirmationList = null;
		shortTermLifeSurrenderInformList = null;
		shortTermLifeSurrenderPaymentList = null;
		shortTermLifeSurrenderIssueList = null;

		/* For Life PaidUp */
		lifePaidUpApprovalList = null;
		lifePaidUpInformList = null;
		lifePaidUpConfirmationList = null;

		/* For ShortTerm Life PaidUp */
		ShortTermlifePaidUpApprovalList = null;
		ShortTermlifePaidUpInformList = null;
		ShortTermlifePaidUpConfirmationList = null;

		/* For Personal Accident */
		paSurveyList = null;
		paApprovalList = null;
		paConfirmationList = null;
		paInformList = null;
		paPaymentList = null;
		paIssueList = null;

		/* Personal Travel */
		personTravelConfirmationList = null;
		personTravelPaymentList = null;
		personTravelIssueList = null;

		/* Farmer Underwriting */
		farmerSurveyList = null;
		farmerApprovalList = null;
		farmerConfirmationList = null;
		farmerInformList = null;
		farmerPaymentList = null;
		farmerIssueList = null;

		/* Short Term Endowment Life */
		shortEndowmentSurveyList = null;
		shortEndowmentApprovalList = null;
		shortEndowmentInformList = null;
		shortEndowmentConfirmationList = null;
		shortEndowmentPaymentList = null;
		shortEndowmentIssueList = null;

		/* Short Term Endowment endorsement Life */
		shortTermEndowmentEndorseSurveyList = null;
		shortTermEndowmentEndorseApprovalList = null;
		shortTermEndowmentEndorseInformList = null;
		shortTermEndowmentEndorseConfirmationList = null;
		shortTermEndowmentEndorsePaymentList = null;
		shortTermEndowmentEndorseIssueList = null;

		/**** Health UnderWriting ***/
		healthSurveyList = null;
		healthApprovalList = null;
		healthInformList = null;
		healthConfirmationList = null;
		healthPaymentList = null;
		healthIssueList = null;

		/**** Micro Health UnderWriting ***/
		microHealthSurveyList = null;
		microHealthApprovalList = null;
		microHealthInformList = null;
		microHealthConfirmationList = null;
		microHealthPaymentList = null;
		microHealthIssueList = null;
		groupMicroHealthApprovalList = null;
		groupMicroHealthConfirmationList = null;
		groupMicroHealthPaymentList = null;

		/**** CriticalIllness UnderWriting ***/
		criticalIllnessSurveyList = null;
		criticalIllnessApprovalList = null;
		criticalIllnessInformList = null;
		criticalIllnessConfirmationList = null;
		criticalIllnessPaymentList = null;
		criticalIllnessIssueList = null;

		/*** Group Farmer ***/
		groupFarmerConfirmationList = null;
		groupFarmerPaymentList = null;

		/*** Student Life ***/
		studentLifeSurveyList = null;
		studentLifeApprovalList = null;
		studentLifeConfirmationList = null;
		studentLifeInformList = null;
		studentLifePaymentList = null;
		studentLifeIssueList = null;

		singlePremiumEndowmentlifeSurveyList = null;
		singlePremiumEndowmentlifeApprovalList = null;
		singlePremiumEndowmentlifeConfirmationList = null;
		singlePremiumEndowmentlifeInformList = null;
		singlePremiumEndowmentlifePaymentList = null;
		singlePremiumEndowmentlifeIssueList = null;

		singlePremiumCreditlifeSurveyList = null;
		singlePremiumCreditlifeApprovalList = null;
		singlePremiumCreditlifeConfirmationList = null;
		singlePremiumCreditlifeInformList = null;
		singlePremiumCreditlifePaymentList = null;
		singlePremiumCreditlifeIssueList = null;

		shortTermSinglePremiumCreditlifeSurveyList = null;
		shortTermSinglePremiumCreditlifeApprovalList = null;
		shortTermSinglePremiumCreditlifeConfirmationList = null;
		shortTermSinglePremiumCreditlifeInformList = null;
		shortTermSinglePremiumCreditlifePaymentList = null;
		shortTermSinglePremiumCreditlifeIssueList = null;
		
		
		simplelifeSurveyList = null;
		simplelifeApprovalList = null;
		simplelifeConfirmationList = null;
		simplelifeInformList = null;
		simplelifePaymentList = null;
		simplelifeIssueList = null;
		

	}

	/*************************************
	 * clear count
	 *******************************************/

	private void clearCount() {
		/*** CRITICAL_ILLNESS_PROPOSAL ***/
		criticalIllnessSurveyCount = 0;
		criticalIllnessApprovalCount = 0;
		criticalIllnessInformCount = 0;
		criticalIllnessConfirmationCount = 0;
		criticalIllnessPaymentCount = 0;
		criticalIllnessIssueCount = 0;

		/*** HEALTH_PROPOSAL ***/
		healthSurveyCount = 0;
		healthApprovalCount = 0;
		healthInformCount = 0;
		healthConfirmationCount = 0;
		healthPaymentCount = 0;
		healthIssueCount = 0;

		/*** MICRO_HEALTH_PROPOSAL ***/
		microHealthSurveyCount = 0;
		microHealthApprovalCount = 0;
		microHealthInformCount = 0;
		microHealthConfirmationCount = 0;
		microHealthPaymentCount = 0;
		microHealthIssueCount = 0;

		medicalClaimConfirmCount = 0;
		medicalIssueCount = 0;
		medicalSurveyCount = 0;
		medicalApprovalCount = 0;

		/*** Medical Insurance underwriting ***/
		medicalClaimSurveyCount = 0;
		medicalClaimApproveCount = 0;
		medicalClaimInformCount = 0;
		medicalClaimPaymentCount = 0;
		medicalProposalSurveyCount = 0;
		medicalProposalApproveCount = 0;

		medicalInformCount = 0;
		medicalConfirmationCount = 0;
		medicalPaymentCount = 0;

		/*** Medical Renewal ***/
		medicalRenewalSurveyCount = 0;
		medicalRenewalApprovalCount = 0;
		medicalRenewalInformCount = 0;
		medicalRenewalConfirmationCount = 0;
		medicalRenewalPaymentCount = 0;
		medicalRenewalIssueCount = 0;

		/**** MotorProposal UnderWriting ***/
		/**** FireProposal UnderWriting ***/
		/**** LifeProposal UnderWriting ***/
		lifeSurveyCount = 0;
		lifeApprovalCount = 0;
		lifeConfirmationCount = 0;
		lifeInformCount = 0;
		lifePaymentCount = 0;
		lifeIssueCount = 0;
		/**** FidelityProposal UnderWriting ***/
		/**** CashInSafeProposal UnderWriting ***/
		/**** CashInTransitProposal UnderWriting ***/
		/**** CashInTransitProposal Claim ***/
		/**** SnakeBitePolicy UnderWriting ***/

		/*
		 * Forpublic Term life
		 */
		publicTermlifeSurveyCount = 0;
		publicTermlifeApprovalCount = 0;
		publicTermlifeConfirmationCount = 0;
		publicTermlifeInformCount = 0;
		publicTermlifePaymentCount = 0;
		publicTermlifeIssueCount = 0;

		/* For Single Premium Endowment Life */
		singlePremiumEndowmentlifeSurveyCount = 0;
		singlePremiumEndowmentlifeApprovalCount = 0;
		singlePremiumEndowmentlifeConfirmationCount = 0;
		singlePremiumEndowmentlifeInformCount = 0;
		singlePremiumEndowmentlifePaymentCount = 0;
		singlePremiumEndowmentlifeIssueCount = 0;

		/* For Single Premium Credit Life */
		singlePremiumCreditlifeSurveyCount = 0;
		singlePremiumCreditlifeApprovalCount = 0;
		singlePremiumCreditlifeConfirmationCount = 0;
		singlePremiumCreditlifeInformCount = 0;
		singlePremiumCreditlifePaymentCount = 0;
		singlePremiumCreditlifeIssueCount = 0;

		/* For Short Term Single Premium Credit Life */
		shortTermSinglePremiumCreditlifeSurveyCount = 0;
		shortTermSinglePremiumCreditlifeApprovalCount = 0;
		shortTermSinglePremiumCreditlifeConfirmationCount = 0;
		shortTermSinglePremiumCreditlifeInformCount = 0;
		shortTermSinglePremiumCreditlifePaymentCount = 0;
		shortTermSinglePremiumCreditlifeIssueCount = 0;

		snakeBitePaymentCount = 0;
		/**** MotorProposal Endorsement ***/
		/**** Terminate Motor Proposal ***/
		/**** FireProposal Endorsement ***/
		/**** LifeProposal Endorsement ***/
		lifeEndorseSurveyCount = 0;
		lifeEndorseApprovalCount = 0;
		lifeEndorseConfirmationCount = 0;
		lifeEndorsePaymentCount = 0;
		lifeEndorseIssueCount = 0;
		/**** CargoProposal Endorsement ***/
		/**** MotorProposal Renewal ***/
		/**** FireProposal Renewal ***/
		/**** GroupLifeProposal Renewal ***/
		lifeRenewalSurveyCount = 0;
		lifeRenewalApprovalCount = 0;
		lifeRenewalConfirmationCount = 0;
		lifeRenewalInformCount = 0;
		lifeRenewalPaymentCount = 0;
		lifeRenewalIssueCount = 0;

		/*** Damaged Vehicle (Motor Claim) ***/
		/*** WindScreen(Motor Claim) ***/
		/*** Towing(Motor Claim) ***/
		/*** ThirdPartyProperty(Motor Claim) ***/
		thirdPartySurveyCount = 0;
		thirdPartyApprovalCount = 0;
		thirdPartyConfirmationCount = 0;
		thirdPartyInformCount = 0;
		thirdPartyPaymentCount = 0;
		/*** PersonCasulaty ***/
		personCasualtySurveyCount = 0;
		personCasualtyApprovalCount = 0;
		personCasualtyConfirmationCount = 0;
		personCasualtyInformCount = 0;
		personCasualtyPaymentCount = 0;
		/*** Medical Expense ***/
		medicalExpenseSurveyCount = 0;
		medicalExpenseApprovalCount = 0;
		medicalExpenseConfirmationCount = 0;
		medicalExpenseInformCount = 0;
		medicalExpensePaymentCount = 0;
		/*** Fire Claim **/
		/*** Fire Claim **/
		/*** Life Claim **/
		lifeClaimRequestCount = 0;
		lifeClaimApprovalCount = 0;
		lifeClaimConfirmationCount = 0;
		lifeClaimInformCount = 0;
		lifeClaimPaymentCount = 0;
		/*** Life Disability Claim **/
		lifeDisabilityClaimRequestCount = 0;
		lifeDisabilityClaimApprovalCount = 0;
		lifeDisabilityClaimConfirmationCount = 0;
		lifeDisabilityClaimInformCount = 0;
		lifeDisabilityClaimPaymentCount = 0;
		/*** Co_insurance **/
		coinsuranceFromConfirmationCount = 0;
		coinsuranceFromPaymentCount = 0;
		coinsuranceToPaymentCount = 0;
		agentCommissionPaymentCount = 0;
		/*** Travel Insurance ***/
		travelProposalConfirmationCount = 0;
		travelProposalPaymentCount = 0;
		/*** CashInSafe Claim ***/

		/**** Life Surrender Proposal **/
		lifeSurrenderApprovalCount = 0;
		lifeSurrenderConfirmationCount = 0;
		lifeSurrenderInformCount = 0;
		lifeSurrenderPaymentCount = 0;
		lifeSurrenderIssueCount = 0;

		/**** ShortTermLife Surrender Proposal **/
		shortTermLifeSurrenderApprovalCount = 0;
		shortTermLifeSurrenderConfirmationCount = 0;
		shortTermLifeSurrenderInformCount = 0;
		shortTermLifeSurrenderPaymentCount = 0;
		shortTermLifeSurrenderIssueCount = 0;

		/**** Life Surrender PaidUp **/
		lifePaidUpApprovalCount = 0;
		lifePaidUpConfirmationCount = 0;
		lifePaidUpInformCount = 0;

		/**** ShortTerm Paidup *****/
		shortTermLifePaidUpApprovalCount = 0;
		shortTermLifePaidUpConfirmationCount = 0;
		shortTermLifePaidUpInformCount = 0;

		/**** Personal Accident **/
		paSurveyCount = 0;
		paApprovalCount = 0;
		paConfirmationCount = 0;
		paInformCount = 0;
		paPaymentCount = 0;
		paIssueCount = 0;

		/**** Personal Travel **/
		personTravelConfirmationCount = 0;
		personTravelPaymentCount = 0;
		personTravelIssueCount = 0;

		/**** Farmer UnderWriting ***/
		farmerSurveyCount = 0;
		farmerApprovalCount = 0;
		farmerConfirmationCount = 0;
		farmerInformCount = 0;
		farmerPaymentCount = 0;
		farmerIssueCount = 0;

		/*** Gruoup Farmer ***/
		groupFarmerConfirmCount = 0;
		groupFarmerPaymentCount = 0;

		/*** Student Life ***/
		studentLifeSurveyCount = 0;
		studentLifeApprovalCount = 0;
		studentLifeConfirmationCount = 0;
		studentLifeInformCount = 0;
		studentLifePaymentCount = 0;
		studentLifeIssueCount = 0;

	}

	/*************************************
	 * refresh list
	 *******************************************/

	private void refreshList() {
		/*** Cargo Insurance underwriting ***/

		/*** Medical Insurance underwriting ***/
		if (medicalClaimConfirmList != null) {
			loadMedicalClaimConfirm();
		}
		if (medicalClaimSurveyList != null) {
			loadMedicalClaimSurvey();
		}
		if (medicalClaimApproveList != null) {
			loadMedicalClaimSurvey();
		}

		if (medicalClaimInformList != null) {
			loadMedicalClaimApprove();
		}

		if (medicalClaimPaymentList != null) {
			// loadMedicalClaimComfirm();
		}

		if (medicalSurveyList != null) {
			loadMedicalSurvey();
		}
		if (medicalApprovalList != null) {
			loadMedicalApproval();
		}
		if (medicalInformList != null) {
			loadMedicalInform();
		}
		if (medicalConfirmationList != null) {
			loadMedicalConfirmation();
		}
		if (medicalPaymentList != null) {
			loadMedicalPayment();
		}

		if (lifeSurveyList != null) {
			loadLifeSurvey();
		}
		if (lifeApprovalList != null) {
			loadLifeApproval();
		}
		if (lifeConfirmationList != null) {
			loadLifeConfirmation();
		}
		if (lifeInformList != null) {
			loadLifeInform();
		}
		if (lifePaymentList != null) {
			loadLifePayment();
		}
		if (lifeIssueList != null) {
			loadLifeIssue();
		}

		if (snakeBitePaymentList != null) {
			loadSnakeBitePayment();
		}

		if (lifeEndorseSurveyList != null) {
			loadLifeEndorsementSurvey();
		}
		if (lifeEndorseApprovalList != null) {
			loadLifeEndorsementApproval();
		}
		if (lifeEndorseConfirmationList != null) {
			loadLifeEndorsementConfirmation();
		}
		if (lifeEndorsePaymentList != null) {
			loadLifeEndorsementPayment();
		}
		if (lifeEndorseIssueList != null) {
			loadLifeEndorsementIssue();
		}
		if (lifeRenewalSurveyList != null) {
			loadLifeRenewalSurvey();
		}
		if (lifeRenewalApprovalList != null) {
			loadLifeRenewalApproval();
		}
		if (lifeRenewalConfirmationList != null) {
			loadLifeRenewalConfirmation();
		}
		if (lifeRenewalInformList != null) {
			loadLifeRenewalInform();
		}
		if (lifeRenewalPaymentList != null) {
			loadLifeRenewalPayment();
		}
		if (lifeRenewalIssueList != null) {
			loadLifeRenewalIssue();
		}

		if (lifeClaimRequestList != null) {
			loadLifeClaimRequest();
		}
		if (lifeClaimApprovalList != null) {
			loadLifeClaimApproval();
		}
		if (lifeClaimConfirmationList != null) {
			loadLifeClaimConfirmation();
		}
		if (lifeClaimInformList != null) {
			loadLifeClaimInform();
		}
		if (lifeClaimPaymentList != null) {
			loadLifeClaimPayment();
		}

		if (lifeDisabilityClaimRequestList != null) {
			loadLifeDisabilityClaimRequest();
		}
		if (lifeDisabilityClaimApprovalList != null) {
			loadLifeDisabilityClaimApproval();
		}
		if (lifeDisabilityClaimConfirmationList != null) {
			loadLifeDisabilityClaimConfirmation();
		}
		if (lifeDisabilityClaimInformList != null) {
			loadLifeDisabilityClaimInform();
		}
		if (lifeDisabilityClaimPaymentList != null) {
			loadLifeDisabilityClaimPayment();
		}

		if (coinsuranceFromConfirmationList != null) {
			loadCoinsuranceFromOtherCompanyConfirmation();
		}
		if (coinsuranceFromPaymentList != null) {
			loadCoinsuranceFromOtherCompanyPayment();
		}
		if (coinsuranceToPaymentList != null) {
			loadCoinsuranceToOtherCompanyPayment();
		}

		if (agentCommissionPaymentList != null) {
			loadAgentCommissionPayment();
		}
		if (travelProposalConfirmationList != null) {
			loadTravelProposalConfrimation();
		}
		if (travelProposalPaymentList != null) {

		}
		if (lifeSurrenderApprovalList != null) {
			loadLifeSurrenderApproval();
		}
		if (lifeSurrenderInformList != null) {
			loadLifeSurrenderInform();
		}
		if (lifeSurrenderConfirmationList != null) {
			loadLifeSurrenderConfirmation();
		}
		if (lifeSurrenderPaymentList != null) {
			loadLifeSurrenderPayment();
		}
		if (lifeSurrenderIssueList != null) {
			loadLifeSurrenderIssue();
		}

		if (shortTermLifeSurrenderApprovalList != null) {
			loadShortTermLifeSurrenderApproval();
		}
		if (shortTermLifeSurrenderConfirmationList != null) {
			loadShortTermLifeSurrenderInform();
		}
		if (shortTermLifeSurrenderInformList != null) {
			loadShortTermLifeSurrenderConfirmation();
		}
		if (shortTermLifeSurrenderPaymentList != null) {
			loadShortTermLifeSurrenderPayment();
		}
		if (shortTermLifeSurrenderIssueList != null) {
			loadShortTermLifeSurrenderIssue();
		}

		if (lifePaidUpApprovalList != null) {
			loadLifePaidUpApproval();
		}
		if (lifePaidUpInformList != null) {
			loadLifePaidUpInform();
		}
		if (lifePaidUpConfirmationList != null) {
			loadLifePaidUpConfirmation();
		}

		if (ShortTermlifePaidUpApprovalList != null) {
			loadShortTermLifePaidUpApproval();
		}
		if (ShortTermlifePaidUpInformList != null) {
			loadShortTermLifePaidUpInform();
		}
		if (ShortTermlifePaidUpConfirmationList != null) {
			loadShortTermLifePaidUpConfirmation();
		}

		/**** Personal Accident ****/
		if (paSurveyList != null) {
			loadPASurvey();
		}
		if (paApprovalList != null) {
			loadPAApproval();
		}
		if (paConfirmationList != null) {
			loadPAConfirmation();
		}
		if (paInformList != null) {
			loadPAInform();
		}
		if (paPaymentList != null) {
			loadPAPayment();
		}
		if (paIssueList != null) {
			loadPAIssue();
		}

		/**** Person Travel ****/
		if (personTravelConfirmationList != null) {
			loadPersonTravelConfirmation();
		}
		if (personTravelPaymentList != null) {
			loadPersonTravelPayment();
		}
		if (personTravelIssueList != null) {
			loadPersonTravelIssue();
		}

		/* Health,MicroHealth,CriticalIllness */
		if (healthSurveyList != null) {
			loadHealthSurvey();
		}

		if (healthApprovalList != null) {
			loadHealthApprove();
		}

		if (healthInformList != null) {
			loadHealthInform();
		}

		if (healthConfirmationList != null) {
			loadHealthConfirm();
		}

		if (healthIssueList != null) {
			loadHealthIssue();
		}
		if (healthPaymentList != null) {
			loadHealthPayment();
		}

		/* Group Micro Health */
		if (groupMicroHealthApprovalList != null) {
			loadGroupMicroHealthApproval();
		}

		if (groupMicroHealthConfirmationList != null) {
			loadGroupMicroHealthConfirm();
		}

		if (groupMicroHealthPaymentList != null) {
			loadGroupMicroHealthPayment();
		}

		/* Micro Health */

		if (microHealthSurveyList != null) {
			loadMicroHealthSurvey();
		}

		if (microHealthApprovalList != null) {
			loadMicroHealthApprove();
		}

		if (microHealthInformList != null) {
			loadMicroHealthInform();
		}

		if (microHealthConfirmationList != null) {
			loadMicroHealthConfirm();
		}

		if (microHealthIssueList != null) {
			loadMicroHealthIssue();
		}

		if (microHealthPaymentList != null) {
			loadMicroHealthPayment();
		}

		/* Critical Illness */

		if (criticalIllnessSurveyList != null) {
			loadCriticalIllnessSurvey();
		}

		if (criticalIllnessApprovalList != null) {
			loadCriticalIllnessApprove();
		}

		if (criticalIllnessInformList != null) {
			loadCriticalIllnessInform();
		}

		if (criticalIllnessConfirmationList != null) {
			loadCriticalIllnessConfirm();
		}

		if (criticalIllnessIssueList != null) {
			loadCriticalIllnessIssue();
		}

		if (criticalIllnessPaymentList != null) {
			loadCriticalIllnessPayment();
		}

		// Group Farmer
		if (groupFarmerConfirmationList != null) {
			loadGroupFarmerConfirmation();

		}
		if (groupFarmerPaymentList != null) {
			loadGroupFarmerPayment();
		}

		// Student Life
		if (studentLifeSurveyList != null) {
			loadStudentLifeSurvey();
		}
		if (studentLifeApprovalList != null) {
			loadStudentLifeApproval();
		}
		if (studentLifeConfirmationList != null) {
			loadStudentLifeConfirmation();
		}
		if (studentLifeInformList != null) {
			loadLifeInform();
		}
		if (lifePaymentList != null) {
			loadStudentLifePayment();
		}
		if (studentLifeIssueList != null) {
			loadStudentLifeIssue();
		}

	}

	public void clearSession() {
		User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.LOGIN_USER);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(Constants.LOGIN_USER, user);
		userProcessService.registerUser(user);
	}

	public long getMedicalProposalSurveyCount() {
		return medicalProposalSurveyCount;
	}

	public long getmedicalProposalApproveCount() {
		return medicalProposalApproveCount;
	}

	public List<MEDCLM002> getMedicalClaimSurveyList() {
		return medicalClaimSurveyList;
	}

	public List<MEDCLM002> getMedicalClaimApproveList() {
		return medicalClaimApproveList;
	}

	public List<MEDCLM002> getMedicalClaimInformList() {
		return medicalClaimInformList;
	}

	public List<MEDCLM002> getMedicalClaimConfirmList() {
		return medicalClaimConfirmList;
	}

	public List<MEDCLM002> getMedicalClaimPaymentList() {
		return medicalClaimPaymentList;
	}

	public List<TRA001> getTravelProposalConfirmationList() {
		return travelProposalConfirmationList;
	}

	public long getTravelProposalConfirmationCount() {
		return travelProposalConfirmationCount;
	}

	public long getTravelProposalPaymentCount() {
		return travelProposalPaymentCount;
	}

	public List<TRA001> getTravelProposalPaymentList() {
		return travelProposalPaymentList;
	}

	public MedicalClaimProposalDTO getClaimProposalDTO() {
		return claimProposalDTO;
	}

	public void setClaimProposalDTO(MedicalClaimProposalDTO claimProposalDTO) {
		this.claimProposalDTO = claimProposalDTO;
	}

	/**
	 * @return the paSurveyList
	 */
	public List<LIF001> getPaSurveyList() {
		return paSurveyList;
	}

	/**
	 * @return the paApprovalList
	 */
	public List<LIF001> getPaApprovalList() {
		return paApprovalList;
	}

	/**
	 * @return the paConfirmationList
	 */
	public List<LIF001> getPaConfirmationList() {
		return paConfirmationList;
	}

	/**
	 * @return the paInformList
	 */
	public List<LIF001> getPaInformList() {
		return paInformList;
	}

	/**
	 * @return the paPaymentList
	 */
	public List<LIF001> getPaPaymentList() {
		return paPaymentList;
	}

	/**
	 * @return the paIssueList
	 */
	public List<LIF001> getPaIssueList() {
		return paIssueList;
	}

	/**
	 * @return the paSurveyCount
	 */
	public long getPaSurveyCount() {
		return paSurveyCount;
	}

	/**
	 * @return the paApprovalCount
	 */
	public long getPaApprovalCount() {
		return paApprovalCount;
	}

	/**
	 * @return the paConfirmationCount
	 */
	public long getPaConfirmationCount() {
		return paConfirmationCount;
	}

	/**
	 * @return the paInformCount
	 */
	public long getPaInformCount() {
		return paInformCount;
	}

	/**
	 * @return the paPaymentCount
	 */
	public long getPaPaymentCount() {
		return paPaymentCount;
	}

	/**
	 * @return the paIssueCount
	 */
	public long getPaIssueCount() {
		return paIssueCount;
	}

	/**
	 * @return the personTravelConfirmationCount
	 */
	public long getPersonTravelConfirmationCount() {
		return personTravelConfirmationCount;
	}

	/**
	 * @return the personTravelPaymentCount
	 */
	public long getPersonTravelPaymentCount() {
		return personTravelPaymentCount;
	}

	/**
	 * @return the personTravelIssueCount
	 */
	public long getPersonTravelIssueCount() {
		return personTravelIssueCount;
	}

	public long getGroupFarmerConfirmCount() {
		return groupFarmerConfirmCount;
	}

	public long getGroupFarmerPaymentCount() {
		return groupFarmerPaymentCount;
	}

	/**
	 * @return the personTravelConfirmationList
	 */
	public List<TRA001> getPersonTravelConfirmationList() {
		return personTravelConfirmationList;
	}

	/**
	 * @return the personTravelPaymentList
	 */
	public List<TRA001> getPersonTravelPaymentList() {
		return personTravelPaymentList;
	}

	/**
	 * @return the personTravelIssueList
	 */
	public List<TRA001> getPersonTravelIssueList() {
		return personTravelIssueList;
	}

	public List<LIF001> getFarmerSurveyList() {
		return farmerSurveyList;
	}

	public List<LIF001> getFarmerApprovalList() {
		return farmerApprovalList;
	}

	public List<LIF001> getFarmerConfirmationList() {
		return farmerConfirmationList;
	}

	public List<LIF001> getFarmerInformList() {
		return farmerInformList;
	}

	public List<LIF001> getFarmerPaymentList() {
		return farmerPaymentList;
	}

	public List<LIF001> getFarmerIssueList() {
		return farmerIssueList;
	}

	public List<LIF001> getShortEndowmentSurveyList() {
		return shortEndowmentSurveyList;
	}

	public List<LIF001> getShortEndowmentApprovalList() {
		return shortEndowmentApprovalList;
	}

	public List<LIF001> getShortEndowmentInformList() {
		return shortEndowmentInformList;
	}

	public List<LIF001> getShortEndowmentConfirmationList() {
		return shortEndowmentConfirmationList;
	}

	public List<LIF001> getShortEndowmentPaymentList() {
		return shortEndowmentPaymentList;
	}

	public List<LIF001> getShortEndowmentIssueList() {
		return shortEndowmentIssueList;
	}

	public long getShortEndowmentEndorsementSurveyCount() {
		return shortEndowmentEndorsementSurveyCount;
	}

	public long getShortEndowmentEndorsementApprovalCount() {
		return shortEndowmentEndorsementApprovalCount;
	}

	public long getShortEndowmentEndorsementConfirmationCount() {
		return shortEndowmentEndorsementConfirmationCount;
	}

	public long getShortEndowmentEndorsementInformCount() {
		return shortEndowmentEndorsementInformCount;
	}

	public long getShortEndowmentEndorsementPaymentCount() {
		return shortEndowmentEndorsementPaymentCount;
	}

	public long getShortEndowmentEndorsementIssueCount() {
		return shortEndowmentEndorsementIssueCount;
	}

	public List<LIF001> getShortTermEndowmentEndorseSurveyList() {
		return shortTermEndowmentEndorseSurveyList;
	}

	public List<LIF001> getShortTermEndowmentEndorseApprovalList() {
		return shortTermEndowmentEndorseApprovalList;
	}

	public List<LIF001> getShortTermEndowmentEndorseInformList() {
		return shortTermEndowmentEndorseInformList;
	}

	public List<LIF001> getShortTermEndowmentEndorseConfirmationList() {
		return shortTermEndowmentEndorseConfirmationList;
	}

	public List<LIF001> getShortTermEndowmentEndorsePaymentList() {
		return shortTermEndowmentEndorsePaymentList;
	}

	public List<LIF001> getShortTermEndowmentEndorseIssueList() {
		return shortTermEndowmentEndorseIssueList;
	}

	public long getCriticalIllnessSurveyCount() {
		return criticalIllnessSurveyCount;
	}

	public long getCriticalIllnessApprovalCount() {
		return criticalIllnessApprovalCount;
	}

	public long getCriticalIllnessInformCount() {
		return criticalIllnessInformCount;
	}

	public long getCriticalIllnessConfirmationCount() {
		return criticalIllnessConfirmationCount;
	}

	public long getCriticalIllnessPaymentCount() {
		return criticalIllnessPaymentCount;
	}

	public long getCriticalIllnessIssueCount() {
		return criticalIllnessIssueCount;
	}

	public long getHealthSurveyCount() {
		return healthSurveyCount;
	}

	public long getHealthApprovalCount() {
		return healthApprovalCount;
	}

	public long getHealthInformCount() {
		return healthInformCount;
	}

	public long getHealthConfirmationCount() {
		return healthConfirmationCount;
	}

	public long getHealthPaymentCount() {
		return healthPaymentCount;
	}

	public long getHealthIssueCount() {
		return healthIssueCount;
	}

	public long getMicroHealthSurveyCount() {
		return microHealthSurveyCount;
	}

	public long getMicroHealthApprovalCount() {
		return microHealthApprovalCount;
	}

	public long getMicroHealthInformCount() {
		return microHealthInformCount;
	}

	public long getMicroHealthConfirmationCount() {
		return microHealthConfirmationCount;
	}

	public long getMicroHealthPaymentCount() {
		return microHealthPaymentCount;
	}

	public long getMicroHealthIssueCount() {
		return microHealthIssueCount;
	}

	public List<MEDICAL002> getHealthSurveyList() {
		return healthSurveyList;
	}

	public List<MEDICAL002> getHealthApprovalList() {
		return healthApprovalList;
	}

	public List<MEDICAL002> getHealthInformList() {
		return healthInformList;
	}

	public List<MEDICAL002> getHealthConfirmationList() {
		return healthConfirmationList;
	}

	public List<MEDICAL002> getHealthPaymentList() {
		return healthPaymentList;
	}

	public List<MEDICAL002> getHealthIssueList() {
		return healthIssueList;
	}

	public List<MEDICAL002> getMicroHealthSurveyList() {
		return microHealthSurveyList;
	}

	public List<MEDICAL002> getMicroHealthApprovalList() {
		return microHealthApprovalList;
	}

	public List<MEDICAL002> getMicroHealthInformList() {
		return microHealthInformList;
	}

	public List<MEDICAL002> getMicroHealthConfirmationList() {
		return microHealthConfirmationList;
	}

	public List<MEDICAL002> getMicroHealthPaymentList() {
		return microHealthPaymentList;
	}

	public List<MEDICAL002> getMicroHealthIssueList() {
		return microHealthIssueList;
	}

	public List<MEDICAL002> getCriticalIllnessSurveyList() {
		return criticalIllnessSurveyList;
	}

	public List<MEDICAL002> getCriticalIllnessApprovalList() {
		return criticalIllnessApprovalList;
	}

	public List<MEDICAL002> getCriticalIllnessInformList() {
		return criticalIllnessInformList;
	}

	public List<MEDICAL002> getCriticalIllnessConfirmationList() {
		return criticalIllnessConfirmationList;
	}

	public List<MEDICAL002> getCriticalIllnessPaymentList() {
		return criticalIllnessPaymentList;
	}

	public List<MEDICAL002> getCriticalIllnessIssueList() {
		return criticalIllnessIssueList;
	}

	public List<GroupMicroHealth> getGroupMicroHealthApprovalList() {
		return groupMicroHealthApprovalList;
	}

	public List<GroupMicroHealth> getGroupMicroHealthConfirmationList() {
		return groupMicroHealthConfirmationList;
	}

	public List<GroupMicroHealth> getGroupMicroHealthPaymentList() {
		return groupMicroHealthPaymentList;
	}

	public long getGroupMicroHealthApprovalCount() {
		return groupMicroHealthApprovalCount;
	}

	public long getGroupMicroHealthConfirmationCount() {
		return groupMicroHealthConfirmationCount;
	}

	public long getGroupMicroHealthPaymentCount() {
		return groupMicroHealthPaymentCount;
	}

	public List<GF001> getGroupFarmerPaymentList() {
		return groupFarmerPaymentList;
	}

	public void setGroupFarmerPaymentList(List<GF001> groupFarmerPaymentList) {
		this.groupFarmerPaymentList = groupFarmerPaymentList;
	}

	public List<GF001> getGroupFarmerConfirmationList() {
		return groupFarmerConfirmationList;
	}

	public void setGroupFarmerConfirmationList(List<GF001> groupFarmerConfirmationList) {
		this.groupFarmerConfirmationList = groupFarmerConfirmationList;
	}

	public void setGroupFarmerConfirmCount(long groupFarmerConfirmCount) {
		this.groupFarmerConfirmCount = groupFarmerConfirmCount;
	}

	public void setGroupFarmerPaymentCount(long groupFarmerPaymentCount) {
		this.groupFarmerPaymentCount = groupFarmerPaymentCount;
	}

	/* For Public Term LIfe */

	public List<LIF001> getPublicTermlifeSurveyList() {
		return publicTermlifeSurveyList;
	}

	public void setPublicTermlifeSurveyList(List<LIF001> publicTermlifeSurveyList) {
		this.publicTermlifeSurveyList = publicTermlifeSurveyList;
	}

	public List<LIF001> getPublicTermlifeApprovalList() {
		return publicTermlifeApprovalList;
	}

	public void setPublicTermlifeApprovalList(List<LIF001> publicTermlifeApprovalList) {
		this.publicTermlifeApprovalList = publicTermlifeApprovalList;
	}

	public List<LIF001> getPublicTermlifeConfirmationList() {
		return publicTermlifeConfirmationList;
	}

	public void setPublicTermlifeConfirmationList(List<LIF001> publicTermlifeConfirmationList) {
		this.publicTermlifeConfirmationList = publicTermlifeConfirmationList;
	}

	public List<LIF001> getPublicTermlifeInformList() {
		return publicTermlifeInformList;
	}

	public void setPublicTermlifeInformList(List<LIF001> publicTermlifeInformList) {
		this.publicTermlifeInformList = publicTermlifeInformList;
	}

	public List<LIF001> getPublicTermlifePaymentList() {
		return publicTermlifePaymentList;
	}

	public void setPublicTermlifePaymentList(List<LIF001> publicTermlifePaymentList) {
		this.publicTermlifePaymentList = publicTermlifePaymentList;
	}

	public List<LIF001> getPublicTermlifeIssueList() {
		return publicTermlifeIssueList;
	}

	public void setPublicTermlifeIssueList(List<LIF001> publicTermlifeIssueList) {
		this.publicTermlifeIssueList = publicTermlifeIssueList;
	}

	public long getPublicTermlifeSurveyCount() {
		return publicTermlifeSurveyCount;
	}

	public void setPublicTermlifeSurveyCount(long publicTermlifeSurveyCount) {
		this.publicTermlifeSurveyCount = publicTermlifeSurveyCount;
	}

	public long getPublicTermlifeApprovalCount() {
		return publicTermlifeApprovalCount;
	}

	public void setPublicTermlifeApprovalCount(long publicTermlifeApprovalCount) {
		this.publicTermlifeApprovalCount = publicTermlifeApprovalCount;
	}

	public long getPublicTermlifeConfirmationCount() {
		return publicTermlifeConfirmationCount;
	}

	public void setPublicTermlifeConfirmationCount(long publicTermlifeConfirmationCount) {
		this.publicTermlifeConfirmationCount = publicTermlifeConfirmationCount;
	}

	public long getPublicTermlifeInformCount() {
		return publicTermlifeInformCount;
	}

	public void setPublicTermlifeInformCount(long publicTermlifeInformCount) {
		this.publicTermlifeInformCount = publicTermlifeInformCount;
	}

	public long getPublicTermlifePaymentCount() {
		return publicTermlifePaymentCount;
	}

	public void setPublicTermlifePaymentCount(long publicTermlifePaymentCount) {
		this.publicTermlifePaymentCount = publicTermlifePaymentCount;
	}

	public long getPublicTermlifeIssueCount() {
		return publicTermlifeIssueCount;
	}

	public void setPublicTermlifeIssueCount(long publicTermlifeIssueCount) {
		this.publicTermlifeIssueCount = publicTermlifeIssueCount;
	}

	public void publicTermloadLifeSurvey() {
		clearList();
		publicTermlifeSurveyList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void publicTermloadLifeApproval() {
		clearList();
		publicTermlifeApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void publicTermloadLifeInform() {
		clearList();
		publicTermlifeInformList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void publicTermloadLifeConfirmation() {
		clearList();
		publicTermlifeConfirmationList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void publicTermloadLifePayment() {
		clearList();
		publicTermlifePaymentList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void publicTermloadLifeIssue() {
		clearList();
		publicTermlifeIssueList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/* For Single Premium Endowment Life */
	public List<LIF001> getSinglePremiumEndowmentlifeSurveyList() {
		return singlePremiumEndowmentlifeSurveyList;
	}

	public void setSinglePremiumEndowmentlifeSurveyList(List<LIF001> singlePremiumEndowmentlifeSurveyList) {
		this.singlePremiumEndowmentlifeSurveyList = singlePremiumEndowmentlifeSurveyList;
	}

	public List<LIF001> getSinglePremiumEndowmentlifeApprovalList() {
		return singlePremiumEndowmentlifeApprovalList;
	}

	public void setSinglePremiumEndowmentlifeApprovalList(List<LIF001> singlePremiumEndowmentlifeApprovalList) {
		this.singlePremiumEndowmentlifeApprovalList = singlePremiumEndowmentlifeApprovalList;
	}

	public List<LIF001> getSinglePremiumEndowmentlifeConfirmationList() {
		return singlePremiumEndowmentlifeConfirmationList;
	}

	public void setSinglePremiumEndowmentlifeConfirmationList(List<LIF001> singlePremiumEndowmentlifeConfirmationList) {
		this.singlePremiumEndowmentlifeConfirmationList = singlePremiumEndowmentlifeConfirmationList;
	}

	public List<LIF001> getSinglePremiumEndowmentlifeInformList() {
		return singlePremiumEndowmentlifeInformList;
	}

	public void setSinglePremiumEndowmentlifeInformList(List<LIF001> singlePremiumEndowmentlifeInformList) {
		this.singlePremiumEndowmentlifeInformList = singlePremiumEndowmentlifeInformList;
	}

	public List<LIF001> getSinglePremiumEndowmentlifePaymentList() {
		return singlePremiumEndowmentlifePaymentList;
	}

	public void setSinglePremiumEndowmentlifePaymentList(List<LIF001> singlePremiumEndowmentlifePaymentList) {
		this.singlePremiumEndowmentlifePaymentList = singlePremiumEndowmentlifePaymentList;
	}

	public List<LIF001> getSinglePremiumEndowmentlifeIssueList() {
		return singlePremiumEndowmentlifeIssueList;
	}

	public void setSinglePremiumEndowmentlifeIssueList(List<LIF001> singlePremiumEndowmentlifeIssueList) {
		this.singlePremiumEndowmentlifeIssueList = singlePremiumEndowmentlifeIssueList;
	}

	public long getSinglePremiumEndowmentlifeSurveyCount() {
		return singlePremiumEndowmentlifeSurveyCount;
	}

	public void setSinglePremiumEndowmentlifeSurveyCount(long singlePremiumEndowmentlifeSurveyCount) {
		this.singlePremiumEndowmentlifeSurveyCount = singlePremiumEndowmentlifeSurveyCount;
	}

	public long getSinglePremiumEndowmentlifeApprovalCount() {
		return singlePremiumEndowmentlifeApprovalCount;
	}

	public void setSinglePremiumEndowmentlifeApprovalCount(long singlePremiumEndowmentlifeApprovalCount) {
		this.singlePremiumEndowmentlifeApprovalCount = singlePremiumEndowmentlifeApprovalCount;
	}

	public long getSinglePremiumEndowmentlifeConfirmationCount() {
		return singlePremiumEndowmentlifeConfirmationCount;
	}

	public void setSinglePremiumEndowmentlifeConfirmationCount(long singlePremiumEndowmentlifeConfirmationCount) {
		this.singlePremiumEndowmentlifeConfirmationCount = singlePremiumEndowmentlifeConfirmationCount;
	}

	public long getSinglePremiumEndowmentlifeInformCount() {
		return singlePremiumEndowmentlifeInformCount;
	}

	public void setSinglePremiumEndowmentlifeInformCount(long singlePremiumEndowmentlifeInformCount) {
		this.singlePremiumEndowmentlifeInformCount = singlePremiumEndowmentlifeInformCount;
	}

	public long getSinglePremiumEndowmentlifePaymentCount() {
		return singlePremiumEndowmentlifePaymentCount;
	}

	public void setSinglePremiumEndowmentlifePaymentCount(long singlePremiumEndowmentlifePaymentCount) {
		this.singlePremiumEndowmentlifePaymentCount = singlePremiumEndowmentlifePaymentCount;
	}

	public long getSinglePremiumEndowmentlifeIssueCount() {
		return singlePremiumEndowmentlifeIssueCount;
	}

	public void setSinglePremiumEndowmentlifeIssueCount(long singlePremiumEndowmentlifeIssueCount) {
		this.singlePremiumEndowmentlifeIssueCount = singlePremiumEndowmentlifeIssueCount;
	}

	public void singlePremiumEndowmentloadLifeSurvey() {
		clearList();
		singlePremiumEndowmentlifeSurveyList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumEndowmentloadLifeApproval() {
		clearList();
		singlePremiumEndowmentlifeApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumEndowmentloadLifeInform() {
		clearList();
		singlePremiumEndowmentlifeInformList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumEndowmentloadLifeConfirmation() {
		clearList();
		singlePremiumEndowmentlifeConfirmationList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumEndowmentloadLifePayment() {
		clearList();
		singlePremiumEndowmentlifePaymentList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumEndowmentloadLifeIssue() {
		clearList();
		singlePremiumEndowmentlifeIssueList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/* For Single Premium Credit Life */
	public List<LIF001> getSinglePremiumCreditlifeSurveyList() {
		return singlePremiumCreditlifeSurveyList;
	}

	public void setSinglePremiumCreditlifeSurveyList(List<LIF001> singlePremiumCreditlifeSurveyList) {
		this.singlePremiumCreditlifeSurveyList = singlePremiumCreditlifeSurveyList;
	}

	public List<LIF001> getSinglePremiumCreditlifeApprovalList() {
		return singlePremiumCreditlifeApprovalList;
	}

	public void setSinglePremiumCreditlifeApprovalList(List<LIF001> singlePremiumCreditlifeApprovalList) {
		this.singlePremiumCreditlifeApprovalList = singlePremiumCreditlifeApprovalList;
	}

	public List<LIF001> getSinglePremiumCreditlifeConfirmationList() {
		return singlePremiumCreditlifeConfirmationList;
	}

	public void setSinglePremiumCreditlifeConfirmationList(List<LIF001> singlePremiumCreditlifeConfirmationList) {
		this.singlePremiumCreditlifeConfirmationList = singlePremiumCreditlifeConfirmationList;
	}

	public List<LIF001> getSinglePremiumCreditlifeInformList() {
		return singlePremiumCreditlifeInformList;
	}

	public void setSinglePremiumCreditlifeInformList(List<LIF001> singlePremiumCreditlifeInformList) {
		this.singlePremiumCreditlifeInformList = singlePremiumCreditlifeInformList;
	}

	public List<LIF001> getSinglePremiumCreditlifePaymentList() {
		return singlePremiumCreditlifePaymentList;
	}

	public void setSinglePremiumCreditlifePaymentList(List<LIF001> singlePremiumCreditlifePaymentList) {
		this.singlePremiumCreditlifePaymentList = singlePremiumCreditlifePaymentList;
	}

	public List<LIF001> getSinglePremiumCreditlifeIssueList() {
		return singlePremiumCreditlifeIssueList;
	}

	public void setSinglePremiumCreditlifeIssueList(List<LIF001> singlePremiumCreditlifeIssueList) {
		this.singlePremiumCreditlifeIssueList = singlePremiumCreditlifeIssueList;
	}

	public long getSinglePremiumCreditlifeSurveyCount() {
		return singlePremiumCreditlifeSurveyCount;
	}

	public void setSinglePremiumCreditlifeSurveyCount(long singlePremiumCreditlifeSurveyCount) {
		this.singlePremiumCreditlifeSurveyCount = singlePremiumCreditlifeSurveyCount;
	}

	public long getSinglePremiumCreditlifeApprovalCount() {
		return singlePremiumCreditlifeApprovalCount;
	}

	public void setSinglePremiumCreditlifeApprovalCount(long singlePremiumCreditlifeApprovalCount) {
		this.singlePremiumCreditlifeApprovalCount = singlePremiumCreditlifeApprovalCount;
	}

	public long getSinglePremiumCreditlifeConfirmationCount() {
		return singlePremiumCreditlifeConfirmationCount;
	}

	public void setSinglePremiumCreditlifeConfirmationCount(long singlePremiumCreditlifeConfirmationCount) {
		this.singlePremiumCreditlifeConfirmationCount = singlePremiumCreditlifeConfirmationCount;
	}

	public long getSinglePremiumCreditlifeInformCount() {
		return singlePremiumCreditlifeInformCount;
	}

	public void setSinglePremiumCreditlifeInformCount(long singlePremiumCreditlifeInformCount) {
		this.singlePremiumCreditlifeInformCount = singlePremiumCreditlifeInformCount;
	}

	public long getSinglePremiumCreditlifePaymentCount() {
		return singlePremiumCreditlifePaymentCount;
	}

	public void setSinglePremiumCreditlifePaymentCount(long singlePremiumCreditlifePaymentCount) {
		this.singlePremiumCreditlifePaymentCount = singlePremiumCreditlifePaymentCount;
	}

	public long getSinglePremiumCreditlifeIssueCount() {
		return singlePremiumCreditlifeIssueCount;
	}

	public void setSinglePremiumCreditlifeIssueCount(long singlePremiumCreditlifeIssueCount) {
		this.singlePremiumCreditlifeIssueCount = singlePremiumCreditlifeIssueCount;
	}

	public void singlePremiumCreditloadLifeSurvey() {
		clearList();
		singlePremiumCreditlifeSurveyList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumCreditloadLifeApproval() {
		clearList();
		singlePremiumCreditlifeApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumCreditloadLifeInform() {
		clearList();
		singlePremiumCreditlifeInformList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumCreditloadLifeConfirmation() {
		clearList();
		singlePremiumCreditlifeConfirmationList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumCreditloadLifePayment() {
		clearList();
		singlePremiumCreditlifePaymentList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void singlePremiumCreditloadLifeIssue() {
		clearList();
		singlePremiumCreditlifeIssueList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	/* For Short Term Single Premium Credit Life */
	public List<LIF001> getShortTermSinglePremiumCreditlifeSurveyList() {
		return shortTermSinglePremiumCreditlifeSurveyList;
	}

	public void setShortTermSinglePremiumCreditlifeSurveyList(List<LIF001> shortTermSinglePremiumCreditlifeSurveyList) {
		this.shortTermSinglePremiumCreditlifeSurveyList = shortTermSinglePremiumCreditlifeSurveyList;
	}

	public List<LIF001> getShortTermSinglePremiumCreditlifeApprovalList() {
		return shortTermSinglePremiumCreditlifeApprovalList;
	}

	public void setShortTermSinglePremiumCreditlifeApprovalList(List<LIF001> shortTermSinglePremiumCreditlifeApprovalList) {
		this.shortTermSinglePremiumCreditlifeApprovalList = shortTermSinglePremiumCreditlifeApprovalList;
	}

	public List<LIF001> getShortTermSinglePremiumCreditlifeConfirmationList() {
		return shortTermSinglePremiumCreditlifeConfirmationList;
	}

	public void setShortTermSinglePremiumCreditlifeConfirmationList(List<LIF001> shortTermSinglePremiumCreditlifeConfirmationList) {
		this.shortTermSinglePremiumCreditlifeConfirmationList = shortTermSinglePremiumCreditlifeConfirmationList;
	}

	public List<LIF001> getShortTermSinglePremiumCreditlifeInformList() {
		return shortTermSinglePremiumCreditlifeInformList;
	}

	public void setShortTermSinglePremiumCreditlifeInformList(List<LIF001> shortTermSinglePremiumCreditlifeInformList) {
		this.shortTermSinglePremiumCreditlifeInformList = shortTermSinglePremiumCreditlifeInformList;
	}

	public List<LIF001> getShortTermSinglePremiumCreditlifePaymentList() {
		return shortTermSinglePremiumCreditlifePaymentList;
	}

	public void setShortTermSinglePremiumCreditlifePaymentList(List<LIF001> shortTermSinglePremiumCreditlifePaymentList) {
		this.shortTermSinglePremiumCreditlifePaymentList = shortTermSinglePremiumCreditlifePaymentList;
	}

	public List<LIF001> getShortTermSinglePremiumCreditlifeIssueList() {
		return shortTermSinglePremiumCreditlifeIssueList;
	}

	public void setShortTermSinglePremiumCreditlifeIssueList(List<LIF001> shortTermSinglePremiumCreditlifeIssueList) {
		this.shortTermSinglePremiumCreditlifeIssueList = shortTermSinglePremiumCreditlifeIssueList;
	}

	public long getShortTermSinglePremiumCreditlifeSurveyCount() {
		return shortTermSinglePremiumCreditlifeSurveyCount;
	}

	public void setShortTermSinglePremiumCreditlifeSurveyCount(long shortTermSinglePremiumCreditlifeSurveyCount) {
		this.shortTermSinglePremiumCreditlifeSurveyCount = shortTermSinglePremiumCreditlifeSurveyCount;
	}

	public long getShortTermSinglePremiumCreditlifeApprovalCount() {
		return shortTermSinglePremiumCreditlifeApprovalCount;
	}

	public void setShortTermSinglePremiumCreditlifeApprovalCount(long shortTermSinglePremiumCreditlifeApprovalCount) {
		this.shortTermSinglePremiumCreditlifeApprovalCount = shortTermSinglePremiumCreditlifeApprovalCount;
	}

	public long getShortTermSinglePremiumCreditlifeConfirmationCount() {
		return shortTermSinglePremiumCreditlifeConfirmationCount;
	}

	public void setShortTermSinglePremiumCreditlifeConfirmationCount(long shortTermSinglePremiumCreditlifeConfirmationCount) {
		this.shortTermSinglePremiumCreditlifeConfirmationCount = shortTermSinglePremiumCreditlifeConfirmationCount;
	}

	public long getShortTermSinglePremiumCreditlifeInformCount() {
		return shortTermSinglePremiumCreditlifeInformCount;
	}

	public void setShortTermSinglePremiumCreditlifeInformCount(long shortTermSinglePremiumCreditlifeInformCount) {
		this.shortTermSinglePremiumCreditlifeInformCount = shortTermSinglePremiumCreditlifeInformCount;
	}

	public long getShortTermSinglePremiumCreditlifePaymentCount() {
		return shortTermSinglePremiumCreditlifePaymentCount;
	}

	public void setShortTermSinglePremiumCreditlifePaymentCount(long shortTermSinglePremiumCreditlifePaymentCount) {
		this.shortTermSinglePremiumCreditlifePaymentCount = shortTermSinglePremiumCreditlifePaymentCount;
	}

	public long getShortTermSinglePremiumCreditlifeIssueCount() {
		return shortTermSinglePremiumCreditlifeIssueCount;
	}

	public void setShortTermSinglePremiumCreditlifeIssueCount(long shortTermSinglePremiumCreditlifeIssueCount) {
		this.shortTermSinglePremiumCreditlifeIssueCount = shortTermSinglePremiumCreditlifeIssueCount;
	}

	public void shortTermSinglePremiumCreditloadLifeSurvey() {
		clearList();
		shortTermSinglePremiumCreditlifeSurveyList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void shortTermSinglePremiumCreditloadLifeApproval() {
		clearList();
		shortTermSinglePremiumCreditlifeApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void shortTermSinglePremiumCreditloadLifeInform() {
		clearList();
		shortTermSinglePremiumCreditlifeInformList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void shortTermSinglePremiumCreditloadLifeConfirmation() {
		clearList();
		shortTermSinglePremiumCreditlifeConfirmationList = proxyService.find_LIF001(
				new WorkflowCriteria(WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void shortTermSinglePremiumCreditloadLifePayment() {
		clearList();
		shortTermSinglePremiumCreditlifePaymentList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}

	public void shortTermSinglePremiumCreditloadLifeIssue() {
		clearList();
		shortTermSinglePremiumCreditlifeIssueList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}

	
	/* Simple Life */
	public List<LIF001> getSimplelifeSurveyList() {
		return simplelifeSurveyList;
	}

	public void setSimplelifeSurveyList(List<LIF001> simplelifeSurveyList) {
		this.simplelifeSurveyList = simplelifeSurveyList;
	}

	public List<LIF001> getSimplelifeApprovalList() {
		return simplelifeApprovalList;
	}

	public void setSimplelifeApprovalList(List<LIF001> simplelifeApprovalList) {
		this.simplelifeApprovalList = simplelifeApprovalList;
	}

	public List<LIF001> getSimplelifeConfirmationList() {
		return simplelifeConfirmationList;
	}

	public void setSimplelifeConfirmationList(List<LIF001> simplelifeConfirmationList) {
		this.simplelifeConfirmationList = simplelifeConfirmationList;
	}

	public List<LIF001> getSimplelifeInformList() {
		return simplelifeInformList;
	}

	public void setSimplelifeInformList(List<LIF001> simplelifeInformList) {
		this.simplelifeInformList = simplelifeInformList;
	}

	public List<LIF001> getSimplelifePaymentList() {
		return simplelifePaymentList;
	}

	public void setSimplelifePaymentList(List<LIF001> simplelifePaymentList) {
		this.simplelifePaymentList = simplelifePaymentList;
	}

	public List<LIF001> getSimplelifeIssueList() {
		return simplelifeIssueList;
	}

	public void setSimplelifeIssueList(List<LIF001> simplelifeIssueList) {
		this.simplelifeIssueList = simplelifeIssueList;
	}

	public long getSimplelifeSurveyCount() {
		return simplelifeSurveyCount;
	}

	public void setSimplelifeSurveyCount(long simplelifeSurveyCount) {
		this.simplelifeSurveyCount = simplelifeSurveyCount;
	}

	public long getSimplelifeApprovalCount() {
		return simplelifeApprovalCount;
	}

	public void setSimplelifeApprovalCount(long simplelifeApprovalCount) {
		this.simplelifeApprovalCount = simplelifeApprovalCount;
	}

	public long getSimplelifeConfirmationCount() {
		return simplelifeConfirmationCount;
	}

	public void setSimplelifeConfirmationCount(long simplelifeConfirmationCount) {
		this.simplelifeConfirmationCount = simplelifeConfirmationCount;
	}

	public long getSimplelifeInformCount() {
		return simplelifeInformCount;
	}

	public void setSimplelifeInformCount(long simplelifeInformCount) {
		this.simplelifeInformCount = simplelifeInformCount;
	}

	public long getSimplelifePaymentCount() {
		return simplelifePaymentCount;
	}

	public void setSimplelifePaymentCount(long simplelifePaymentCount) {
		this.simplelifePaymentCount = simplelifePaymentCount;
	}

	public long getSimplelifeIssueCount() {
		return simplelifeIssueCount;
	}

	public void setSimplelifeIssueCount(long simplelifeIssueCount) {
		this.simplelifeIssueCount = simplelifeIssueCount;
	}
	
	public void simpleloadLifeSurvey() {
		clearList();
		simplelifeSurveyList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL, WorkflowTask.SURVEY, ProposalType.UNDERWRITING, user));
	}

	public void simpleloadLifeApproval() {
		clearList();
		simplelifeApprovalList = proxyService
				.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL, WorkflowTask.APPROVAL, ProposalType.UNDERWRITING, user));
	}

	public void simpleloadLifeInform() {
		clearList();
		simplelifeInformList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL, WorkflowTask.INFORM, ProposalType.UNDERWRITING, user));
	}

	public void simpleloadLifeConfirmation() {
		clearList();
		simplelifeConfirmationList = proxyService.find_LIF001(
				new WorkflowCriteria(WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL, WorkflowTask.CONFIRMATION, ProposalType.UNDERWRITING, user));
	}

	public void simpleloadLifePayment() {
		clearList();
		simplelifePaymentList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL, WorkflowTask.PAYMENT, ProposalType.UNDERWRITING, user));
	}
	public void simpleloadLifeIssue() {
		clearList();
		simplelifeIssueList = proxyService.find_LIF001(new WorkflowCriteria(WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL, WorkflowTask.ISSUING, ProposalType.UNDERWRITING, user));
	}
	
	
}
