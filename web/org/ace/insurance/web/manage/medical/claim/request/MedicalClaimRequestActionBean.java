package org.ace.insurance.web.manage.medical.claim.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIData;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.CommonSettingConfig;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.claim.ClaimInitialReport;
import org.ace.insurance.medical.claim.ClaimInitialReportICD;
import org.ace.insurance.medical.claim.ClaimType;
import org.ace.insurance.medical.claim.DeathClaim;
import org.ace.insurance.medical.claim.HospitalizedClaim;
import org.ace.insurance.medical.claim.HospitalizedClaimICD10;
import org.ace.insurance.medical.claim.MedicalBeneficiaryRole;
import org.ace.insurance.medical.claim.MedicalClaimBeneficiary;
import org.ace.insurance.medical.claim.MedicalClaimProposal;
import org.ace.insurance.medical.claim.MedicationClaim;
import org.ace.insurance.medical.claim.OperationClaim;
import org.ace.insurance.medical.claim.OperationType;
import org.ace.insurance.medical.claim.frontService.request.interfaces.IMedicalClaimRequestFrontService;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonGuardian;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.system.common.icd10.ICD10;
import org.ace.insurance.system.common.icd10.service.interfaces.IICD10Service;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ErrorMessage;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.ValidationResult;
import org.ace.insurance.web.common.Validator;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

/***************************************************************************************
 * @author MPPA
 * @Date 2015-01-12
 * @Version 1.0
 * @Purpose This class serves as the Presentation Layer to manipulate the
 *          <code>MedicalClaim</code> request process.
 * 
 ***************************************************************************************/
/**
 * @author ASUS
 *
 */
@ViewScoped
@ManagedBean(name = "MedicalClaimRequestActionBean")
public class MedicalClaimRequestActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private MedicalClaimProposal medicalClaimProposal;
	private HospitalizedClaim hospitalizedClaim;
	private OperationClaim operationClaim;
	private MedicationClaim medicationClaim;
	private DeathClaim deathClaim;
	private MedicalClaimBeneficiary medicalClaimBeneficiary;
	private ClaimInitialReport medicalClaimInitialReport;
	private List<MedicalPolicyInsuredPersonBeneficiaries> policyInsuredPersonBeneficiaryList;
	private MedicalPolicyInsuredPersonBeneficiaries selectedBeneficiary;

	private ClaimType[] selectedClaimTypes;
	private List<RelationShip> relationShipList;
	private User responsiblePerson;

	private String remark;
	private boolean saleMan;
	private boolean disablelinkBtn;
	private boolean death;
	private boolean child;
	private boolean changeFlag;
	private boolean finish;
	private boolean abortion;

	private List<ICD10> hospitalizedReasonList;
	private List<ICD10> selectedHospitalizedList;

	private List<ICD10> icd10List;

	@ManagedProperty(value = "#{ICD10Service}")
	private IICD10Service iCD10Service;

	public void setiCD10Service(IICD10Service iCD10Service) {
		this.iCD10Service = iCD10Service;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	@ManagedProperty(value = "#{DeathBeneficiaryValidator}")
	private Validator<MedicalPolicyInsuredPersonBeneficiaries> deathBeneInfoValidator;

	public void setDeathBeneInfoValidator(Validator<MedicalPolicyInsuredPersonBeneficiaries> deathBeneInfoValidator) {
		this.deathBeneInfoValidator = deathBeneInfoValidator;
	}

	@ManagedProperty(value = "#{MedicalClaimRequestFrontService}")
	private IMedicalClaimRequestFrontService medicalClaimRequestFrontService;

	public void setMedicalClaimRequestFrontService(IMedicalClaimRequestFrontService medicalClaimRequestFrontService) {
		this.medicalClaimRequestFrontService = medicalClaimRequestFrontService;
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalClaimProposal");
		removeParam("medicalClaimInitialReport");
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalClaimInitialReport = (medicalClaimInitialReport == null) ? (ClaimInitialReport) getParam("medicalClaimInitialReport") : medicalClaimInitialReport;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		medicalClaimBeneficiary = new MedicalClaimBeneficiary();
		selectedBeneficiary = new MedicalPolicyInsuredPersonBeneficiaries();
		relationShipList = relationShipService.findAllRelationShip();
		rebindInitialReportData();
	}

	private void rebindInitialReportData() {
		disablelinkBtn = true;
		death = false;
		child = false;
		finish = false;
		saleMan = false;
		icd10List = iCD10Service.findAllICD10();

		medicalClaimBeneficiary = new MedicalClaimBeneficiary();
		selectedBeneficiary = new MedicalPolicyInsuredPersonBeneficiaries();
		medicalClaimProposal = new MedicalClaimProposal();
		medicalClaimProposal.setProductId(medicalClaimInitialReport.getProduct().getId());
		medicalClaimProposal.setClaimReportNo(medicalClaimInitialReport.getClaimReportNo());
		medicalClaimProposal.setPolicyNo(medicalClaimInitialReport.getPolicyNo());
		medicalClaimProposal.setPolicyInsuredPerson(medicalClaimInitialReport.getPolicyInsuredPerson());

		if (medicalClaimInitialReport.getPolicyInsuredPerson().getGuardian() != null) {
			child = true;
		}
		medicalClaimProposal.setBranch(null);
		if (medicalClaimInitialReport.getAgent() != null) {
			saleMan = true;
			medicalClaimProposal.setAgent(medicalClaimInitialReport.getAgent());
		} else if (medicalClaimInitialReport.getSaleMan() != null) {
			saleMan = false;
			medicalClaimProposal.setSaleman(medicalClaimInitialReport.getSaleMan());
		}
		medicalClaimProposal.setSubmittedDate(new Date());
		policyInsuredPersonBeneficiaryList = new ArrayList<MedicalPolicyInsuredPersonBeneficiaries>();
		for (MedicalPolicyInsuredPersonBeneficiaries mpb : medicalClaimInitialReport.getPolicyInsuredPerson().getPolicyInsuredPersonBeneficiariesList()) {
			policyInsuredPersonBeneficiaryList.add(mpb);
		}

		if (medicalClaimInitialReport.getClaimInitialReportICDList() != null) {
			if (hospitalizedReasonList == null) {
				hospitalizedReasonList = new ArrayList<ICD10>();
			}
			for (ClaimInitialReportICD temp : medicalClaimInitialReport.getClaimInitialReportICDList()) {
				hospitalizedReasonList.add(temp.getIcd10());
			}
		}

		selectedClaimTypes = null;
	}

	public String onFlowProcess(FlowEvent event) {
		String formID = "medicalClaimRequestForm";
		boolean valid = true;
		if ("claimProposal".equals(event.getOldStep()) && "generalInfo".equals(event.getNewStep())) {
			if (medicalClaimProposal.isDeath()) {
				String productId = medicalClaimProposal.getProductId();
				int unit = medicalClaimProposal.getPolicyInsuredPerson().getUnit();
				double deathAmt = 0.00;
				if (KeyFactorChecker.isGroupHealthInsurance(productId) || KeyFactorChecker.isIndividualHealthInsurance(productId)) {
					deathAmt = CommonSettingConfig.getDeathClaimAmountForBased() * unit;
				} else if (KeyFactorChecker.isGroupCriticalIllnessInsurance(productId) || KeyFactorChecker.isIndividualCritialIllnessInsurance(productId)) {
					deathAmt = CommonSettingConfig.getDeathClaimAmountForBased() * unit;
				} else {
					deathAmt = CommonSettingConfig.getMicroDeathClaimAmountForBased() * unit;
				}
				deathClaim.setDeathClaimAmount(deathAmt);
			}
		} else if ("generalInfo".equals(event.getOldStep()) && "beneficiaryClaimInfo".equals(event.getNewStep())) {
			if (medicalClaimProposal.isHospitalized()) {
				if (hospitalizedClaim.getHospitalizedStartDate().after(hospitalizedClaim.getHospitalizedEndDate())) {
					addErrorMessage(formID + ":accPanel:startDate", MessageId.HOSPITALIZED_DATE_INFO);
					valid = false;
				}
			}
			if (medicalClaimProposal.isOperation() && medicalClaimProposal.isDeath()) {
				if (operationClaim.getOperationDate().after(deathClaim.getDeathDate())) {
					addErrorMessage(formID + ":accPanel:operationDate", MessageId.OPERATION_DATE_INFO);
					valid = false;
				}

			}
			if (medicalClaimProposal.isHospitalized() && medicalClaimProposal.isDeath()) {
				if (hospitalizedClaim.getHospitalizedStartDate().after(deathClaim.getDeathDate())) {
					addErrorMessage(formID + ":accPanel:startDate", MessageId.HOSPITALIZED_STARTDATE_INFO);
					valid = false;
				}
				if (hospitalizedClaim.getHospitalizedEndDate().after(deathClaim.getDeathDate())) {
					addErrorMessage(formID + ":accPanel:endDate", MessageId.HOSPITALIZED_ENDDATE_INFO);
					valid = false;
				}
			}

			float percentage = 0.0f;

			for (MedicalPolicyInsuredPersonBeneficiaries mpb : medicalClaimInitialReport.getPolicyInsuredPerson().getPolicyInsuredPersonBeneficiariesList()) {
				if (mpb.isDeath()) {
					percentage += mpb.getPercentage();
					medicalClaimBeneficiary.setDeath(true);
					medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.SUCCESSOR);
					medicalClaimBeneficiary.setPercentage(percentage);
				}
			}
		} else if ("beneficiaryClaimInfo".equals(event.getOldStep()) && "workFlow".equals(event.getNewStep())) {
			if (policyInsuredPersonBeneficiaryList != null) {
				boolean hasBen = false;
				for (MedicalPolicyInsuredPersonBeneficiaries insBen : policyInsuredPersonBeneficiaryList) {
					if (insBen.isClaimBeneficiary()) {
						hasBen = true;
					}
				}
				if (medicalClaimInitialReport.getPolicyInsuredPerson().getGuardian() != null && !hasBen
						&& medicalClaimInitialReport.getPolicyInsuredPerson().getGuardian().isDeath()) {
					addErrorMessage(formID + ":policyInsuredPersonBeneficiariesTable", MessageId.REQUIRED_BENEFICIRY_INFO);
					valid = false;
				} else if (!hasBen && medicalClaimProposal.isDeath()) {
					addErrorMessage(formID + ":policyInsuredPersonBeneficiariesTable", MessageId.REQUIRED_BENEFICIRY_INFO);
					valid = false;
				}
			}
		}

		return valid ? event.getNewStep() : event.getOldStep();
	}

	public List<MedicalPolicyInsuredPerson> getPolicyInsuredPersonList() {
		return Arrays.asList(medicalClaimInitialReport.getPolicyInsuredPerson());
	}

	public void changeGuardian(AjaxBehaviorEvent event) {
		if (!medicalClaimInitialReport.getPolicyInsuredPerson().getGuardian().isDeath()) {
			if (medicalClaimBeneficiary != null) {
				if (medicalClaimBeneficiary.getPercentage() >= 0) {
					medicalClaimBeneficiary = new MedicalClaimBeneficiary();
					selectedBeneficiary = new MedicalPolicyInsuredPersonBeneficiaries();
					policyInsuredPersonBeneficiaryList = new ArrayList<MedicalPolicyInsuredPersonBeneficiaries>();
					for (MedicalPolicyInsuredPersonBeneficiaries mpb : medicalClaimInitialReport.getPolicyInsuredPerson().getPolicyInsuredPersonBeneficiariesList()) {
						policyInsuredPersonBeneficiaryList.add(mpb);
					}
				}
			} else {
				medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.BENEFICIARY);
			}
		}
	}

	public ArrayList<ClaimType> getClaimTypes() {
		ArrayList<ClaimType> claimTypes = new ArrayList<ClaimType>();
		if (medicalClaimProposal.getPolicyInsuredPerson().getPolicyInsuredPersonAddOnList().size() > 0) {
			for (MedicalPolicyInsuredPersonAddOn insuAddon : medicalClaimProposal.getPolicyInsuredPerson().getPolicyInsuredPersonAddOnList()) {
				if (KeyFactorChecker.isHealthAddOn1(insuAddon.getAddOn().getId())) {
					claimTypes.add(ClaimType.OPERATION_CLAIM);
				}
				if (KeyFactorChecker.isHealthAddOn2(insuAddon.getAddOn().getId())) {
					claimTypes.add(ClaimType.MEDICATION_CLAIM);
				}
			}
		}
		claimTypes.add(ClaimType.HOSPITALIZED_CLAIM);
		claimTypes.add(ClaimType.DEATH_CLAIM);
		return claimTypes;

	}

	public void changeClaimType() {
		if (selectedClaimTypes != null) {
			medicalClaimProposal.setHospitalized(false);
			medicalClaimProposal.setOperation(false);
			medicalClaimProposal.setMedication(false);
			medicalClaimProposal.setDeath(false);
			death = false;
			for (ClaimType claimtype : selectedClaimTypes) {
				if (ClaimType.HOSPITALIZED_CLAIM.equals(claimtype)) {
					hospitalizedClaim = new HospitalizedClaim();
					medicalClaimProposal.setHospitalized(true);
					hospitalizedClaim.setHospitalizedStartDate(medicalClaimInitialReport.getHospitalizedStartDate());
					hospitalizedClaim.setMedicalPlace(medicalClaimInitialReport.getMedicalPlace());
				} else if (ClaimType.OPERATION_CLAIM.equals(claimtype)) {
					operationClaim = new OperationClaim();
					medicalClaimProposal.setOperation(true);
				} else if (ClaimType.MEDICATION_CLAIM.equals(claimtype)) {
					medicalClaimProposal.setMedication(true);
					medicationClaim = new MedicationClaim();
				} else if (ClaimType.DEATH_CLAIM.equals(claimtype)) {
					if (medicalClaimInitialReport.getPolicyInsuredPerson().getGuardian() != null) {
						medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.GUARDIAN);
					} else {
						medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.BENEFICIARY);
					}
					medicalClaimProposal.setDeath(true);
					deathClaim = new DeathClaim();
					death = true;
				}
			}
		}
	}

	public void changeSelectedBeneficiaryEvent(AjaxBehaviorEvent e) {
		UIData data = (UIData) e.getComponent().findComponent("policyInsuredPersonBeneficiariesTable");
		int rowIndex = data.getRowIndex();
		MedicalPolicyInsuredPersonBeneficiaries beneficiary = policyInsuredPersonBeneficiaryList.get(rowIndex);
		if (!beneficiary.isClaimBeneficiary()) {
			if (beneficiary.isDeath()) {
				float percentage = medicalClaimBeneficiary.getPercentage();
				medicalClaimBeneficiary.setPercentage(percentage - beneficiary.getPercentage());
			}
			beneficiary.setDeath(false);
			beneficiary.setDeathDate(null);
			beneficiary.setDeathReason(null);
			beneficiary.setExit(false);
			if (medicalClaimBeneficiary.getPercentage() <= 0.0) {
				medicalClaimBeneficiary = new MedicalClaimBeneficiary();
				medicalClaimBeneficiary.setDeath(false);
			}
		}
	}

	public void resetDeathBeneficiaryEvent(AjaxBehaviorEvent e) {
		if (!selectedBeneficiary.isDeath()) {
			selectedBeneficiary.setDeath(false);
			selectedBeneficiary.setDeathDate(null);
			selectedBeneficiary.setDeathReason(null);
			selectedBeneficiary.setExit(false);
			validDeathBeneficiary = false;
		} else {
			selectedBeneficiary.setExit(true);
		}
	}

	private boolean validDeathBeneficiary;

	public boolean isValidDeathBeneficiary() {
		return validDeathBeneficiary;
	}

	public void updateDeathBeneficiary() {
		if (selectedBeneficiary.isDeath()) {
			medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.SUCCESSOR);
			ValidationResult result = deathBeneInfoValidator.validate(selectedBeneficiary);
			if (!result.isVerified()) {
				for (ErrorMessage message : result.getErrorMeesages()) {
					addErrorMessage(message);
				}
			} else {
				PrimeFaces.current().executeScript("PF('beneficiaryDeathDialog').hide()");
				resetSuccessorInfo();
			}
			validDeathBeneficiary = true;
		} else {
			PrimeFaces.current().executeScript("PF('beneficiaryDeathDialog').hide()");
			resetSuccessorInfo();
		}

	}

	public List<ICD10> getIcd10List() {
		return icd10List;
	}

	public void setIcd10List(List<ICD10> icd10List) {
		this.icd10List = icd10List;
	}

	private void resetSuccessorInfo() {
		boolean claimSuccessor = false;
		for (MedicalPolicyInsuredPersonBeneficiaries mpipb : policyInsuredPersonBeneficiaryList) {
			if (mpipb.isDeath()) {
				claimSuccessor = true;
				break;
			}
		}
		medicalClaimBeneficiary.setDeath(claimSuccessor);

		boolean transform = false;
		if (changeFlag != selectedBeneficiary.isExit()) {
			transform = true;
		}

		if (transform) {
			float percentage = medicalClaimBeneficiary.getPercentage();
			if (selectedBeneficiary.isDeath()) {
				medicalClaimBeneficiary.setPercentage(percentage + selectedBeneficiary.getPercentage());
			} else {
				medicalClaimBeneficiary.setPercentage(percentage - selectedBeneficiary.getPercentage());
			}

		} else {
			float percentage = medicalClaimBeneficiary.getPercentage();
			medicalClaimBeneficiary.setPercentage(percentage - selectedBeneficiary.getPercentage());
			if (medicalClaimBeneficiary.getPercentage() <= 0.0) {
				medicalClaimBeneficiary = new MedicalClaimBeneficiary();
			}
		}
	}

	public void prepareEditDeathBeneficiary(MedicalPolicyInsuredPersonBeneficiaries medPolInsPerBene) {
		changeFlag = medPolInsPerBene.isExit();
		this.selectedBeneficiary = medPolInsPerBene;
	}

	public void changeSaleMan(AjaxBehaviorEvent event) {
		if (!saleMan) {
			medicalClaimProposal.setAgent(null);
			medicalClaimProposal.setSaleman(null);
		} else {
			medicalClaimProposal.setSaleman(null);
			medicalClaimProposal.setAgent(null);
		}
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		medicalClaimProposal.setSaleman(saleMan);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		medicalClaimProposal.setAgent(agent);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		medicalClaimProposal.setBranch(branch);
	}

	public void returnDeathICD(SelectEvent event) {
		ICD10 icd = (ICD10) event.getObject();
		deathClaim.setDeathReason(icd);
	}

	public void returnOperationICD(SelectEvent event) {
		ICD10 icd = (ICD10) event.getObject();
		operationClaim.setOperationReason(icd);
	}

	public void returnMedicationICD(SelectEvent event) {
		ICD10 icd = (ICD10) event.getObject();
		medicationClaim.setMedicationReason(icd);
	}

	public void returnMedicalPlaceDialog(SelectEvent event) {
		Hospital medicalPlace = (Hospital) event.getObject();
		this.hospitalizedClaim.setMedicalPlace(medicalPlace);
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_SURVEY, WorkFlowType.MEDICAL_INSURANCE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public String submitClaimInfo() {
		String result = null;
		List<MedicalPolicyInsuredPersonBeneficiaries> beneficiaryList = new ArrayList<MedicalPolicyInsuredPersonBeneficiaries>();
		WorkflowTask workflowTask = WorkflowTask.CLAIM_SURVEY;
		try {
			if (medicalClaimBeneficiary.getPercentage() > 0.0) {
				medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.SUCCESSOR);
				medicalClaimBeneficiary.setDeath(true);
				medicalClaimProposal.addClaimBeneficiary(medicalClaimBeneficiary);
			} else if (medicalClaimProposal.getPolicyInsuredPerson().getGuardian() != null && !medicalClaimProposal.getPolicyInsuredPerson().getGuardian().isDeath()) {
				medicalClaimBeneficiary = new MedicalClaimBeneficiary();
				medicalClaimBeneficiary
						.setMedicalPolicyInsuredPersonGuardian(new MedicalPolicyInsuredPersonGuardian(medicalClaimInitialReport.getPolicyInsuredPerson().getGuardian()));
				medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.GUARDIAN);
				medicalClaimProposal.addClaimBeneficiary(medicalClaimBeneficiary);
			} else if (!isChild() && !isDeath()) {
				MedicalPolicyInsuredPerson medicalPolicyInsuredPerson = medicalClaimProposal.getPolicyInsuredPerson();
				medicalClaimBeneficiary = new MedicalClaimBeneficiary();
				medicalClaimBeneficiary.setMedicalPolicyInsuredPerson(medicalPolicyInsuredPerson);
				medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.INSURED_PERSON);
				medicalClaimProposal.addClaimBeneficiary(medicalClaimBeneficiary);
			}

			for (MedicalPolicyInsuredPersonBeneficiaries mpInsuPerBene : policyInsuredPersonBeneficiaryList) {
				beneficiaryList.add(mpInsuPerBene);
				if (mpInsuPerBene.isClaimBeneficiary() && !mpInsuPerBene.isDeath()) {
					medicalClaimBeneficiary = new MedicalClaimBeneficiary();
					medicalClaimBeneficiary.setBeneficiaryRole(MedicalBeneficiaryRole.BENEFICIARY);
					medicalClaimBeneficiary.setBeneficiaryNo(mpInsuPerBene.getBeneficiaryNo());
					medicalClaimBeneficiary.setMedicalPolicyInsuredPersonBeneficiaries(mpInsuPerBene);
					medicalClaimProposal.addClaimBeneficiary(medicalClaimBeneficiary);
				}
			}
			if (medicalClaimProposal.isHospitalized()) {
				for (ICD10 temp : hospitalizedReasonList) {
					HospitalizedClaimICD10 hospICD10 = new HospitalizedClaimICD10();
					hospICD10.setIcd10(temp);
					hospitalizedClaim.addHospitalizedClaimICD10(hospICD10);
				}
				medicalClaimProposal.addMedicalClaim(hospitalizedClaim);
				medicalClaimProposal.getPolicyInsuredPerson().setHosp_day_count(medicalClaimProposal.getPolicyInsuredPerson().getHosp_day_count()
						+ Utils.daysBetween(hospitalizedClaim.getHospitalizedStartDate(), hospitalizedClaim.getHospitalizedEndDate(), false, true));
			}
			if (medicalClaimProposal.isOperation()) {
				operationClaim.setOperationType(OperationType.OPERATION);
				medicalClaimProposal.addMedicalClaim(operationClaim);
			}
			if (medicalClaimProposal.isMedication()) {
				medicalClaimProposal.addMedicalClaim(medicationClaim);
			}
			if (medicalClaimProposal.isDeath()) {
				medicalClaimProposal.addMedicalClaim(deathClaim);
				medicalClaimProposal.getPolicyInsuredPerson().setDeath(true);
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO("", remark, workflowTask, WorkflowReferenceType.MEDICAL_CLAIM, user, responsiblePerson);
			medicalClaimProposal = medicalClaimRequestFrontService.addNewMedicalClaim(medicalClaimProposal, medicalClaimInitialReport, beneficiaryList, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.MEDICAL_ClAIM_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalClaimProposal.getClaimRequestId());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void removeHospitalizedList(ICD10 icd) {
		hospitalizedReasonList.remove(icd);
	}

	public void addNewHospitalizedICD10(ICD10 icd) {
		if (hospitalizedReasonList == null) {
			hospitalizedReasonList = new ArrayList<ICD10>();
		}

		for (ICD10 var : selectedHospitalizedList) {
			if (!hospitalizedReasonList.contains(var)) {
				hospitalizedReasonList.add(var);
			}
		}
	}

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	public ClaimType[] getSelectedClaimTypes() {
		return selectedClaimTypes;
	}

	public void setSelectedClaimTypes(ClaimType[] selectedClaimTypes) {
		this.selectedClaimTypes = selectedClaimTypes;
	}

	public MedicalClaimProposal getMedicalClaimProposal() {
		return medicalClaimProposal;
	}

	public void setMedicalClaimProposal(MedicalClaimProposal medicalClaimProposal) {
		this.medicalClaimProposal = medicalClaimProposal;
	}

	public HospitalizedClaim getHospitalizedClaim() {
		return hospitalizedClaim;
	}

	public void setHospitalizedClaim(HospitalizedClaim hospitalizedClaim) {
		this.hospitalizedClaim = hospitalizedClaim;
	}

	public MedicationClaim getMedicationClaim() {
		return medicationClaim;
	}

	public void setMedicationClaim(MedicationClaim medicationClaim) {
		this.medicationClaim = medicationClaim;
	}

	public DeathClaim getDeathClaim() {
		return deathClaim;
	}

	public void setDeathClaim(DeathClaim deathClaim) {
		this.deathClaim = deathClaim;
	}

	public OperationClaim getOperationClaim() {
		return operationClaim;
	}

	public void setOperationClaim(OperationClaim operationClaim) {
		this.operationClaim = operationClaim;
	}

	public MedicalClaimBeneficiary getMedicalClaimBeneficiary() {
		return medicalClaimBeneficiary;
	}

	public void setMedicalClaimBeneficiary(MedicalClaimBeneficiary medicalClaimBeneficiary) {
		this.medicalClaimBeneficiary = medicalClaimBeneficiary;
	}

	public ClaimInitialReport getMedicalClaimInitialReport() {
		return medicalClaimInitialReport;
	}

	public void setMedicalClaimInitialReport(ClaimInitialReport medicalClaimInitialReport) {
		this.medicalClaimInitialReport = medicalClaimInitialReport;
	}

	public MedicalPolicyInsuredPersonBeneficiaries getSelectedBeneficiary() {
		return selectedBeneficiary;
	}

	public void setSelectedBeneficiary(MedicalPolicyInsuredPersonBeneficiaries selectedBeneficiary) {
		this.selectedBeneficiary = selectedBeneficiary;
	}

	public List<MedicalPolicyInsuredPersonBeneficiaries> getPolicyInsuredPersonBeneficiaryList() {
		return policyInsuredPersonBeneficiaryList;
	}

	public void setSaleMan(boolean saleMan) {
		this.saleMan = saleMan;
	}

	public boolean isSaleMan() {
		return saleMan;
	}

	public void setDisablelinkBtn(boolean disablelinkBtn) {
		this.disablelinkBtn = disablelinkBtn;
	}

	public boolean isDisablelinkBtn() {
		return disablelinkBtn;
	}

	public boolean isDeath() {
		return death;
	}

	public void setDeath(boolean death) {
		this.death = death;
	}

	public boolean isAbortion() {
		return abortion;
	}

	public List<RelationShip> getRelationShipList() {
		return relationShipList;
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

	public boolean isChild() {
		return child;
	}

	public boolean isFinish() {
		return finish;
	}

	public List<ICD10> getSelectedHospitalizedList() {
		return selectedHospitalizedList;
	}

	public void setSelectedHospitalizedList(List<ICD10> selectedHospitalizedList) {
		this.selectedHospitalizedList = selectedHospitalizedList;
	}

	public List<ICD10> getHospitalizedReasonList() {
		return hospitalizedReasonList;
	}

	public void calculateMedicationFee(AjaxBehaviorEvent event) {
		// String productId = medicalClaimProposal.getProductId();
		int unit = medicalClaimProposal.getPolicyInsuredPerson().getUnit();
		// if (KeyFactorChecker.isGroupHealthInsurance(productId) ||
		// KeyFactorChecker.isIndividualHealthInsurance(productId)) {
		double medicationFee = 2500.00 * medicationClaim.getNoOfTimes() * unit;
		medicationClaim.setMedicationFee(medicationFee);
		// }
	}

	public void calculateHospitalFee(AjaxBehaviorEvent event) {
		if (hospitalizedClaim.getHospitalizedStartDate() != null && hospitalizedClaim.getHospitalizedEndDate() != null) {
			String productId = medicalClaimProposal.getProductId();
			int unit = medicalClaimProposal.getPolicyInsuredPerson().getUnit();
			int totalHospitalizedDays = Utils.daysBetween(hospitalizedClaim.getHospitalizedStartDate(), hospitalizedClaim.getHospitalizedEndDate(), false, true);
			double hospitalFee = 0.00;
			if (KeyFactorChecker.isGroupHealthInsurance(productId) || KeyFactorChecker.isIndividualHealthInsurance(productId)) {
				hospitalFee = CommonSettingConfig.getHospitalizedClaimAmountForBased() * totalHospitalizedDays * unit;
			} else if (KeyFactorChecker.isGroupCriticalIllnessInsurance(productId) || KeyFactorChecker.isIndividualCritialIllnessInsurance(productId)) {
				hospitalFee = CommonSettingConfig.getCriticalHospitalizedClaimAmountForBased() * unit;
			} else {
				hospitalFee = CommonSettingConfig.getMicroHospitalizedClaimAmountForBased() * totalHospitalizedDays * unit;
			}
			hospitalizedClaim.setHospitalizedAmount(hospitalFee);
		}
	}

	public void calculateAbortionAmt(AjaxBehaviorEvent event) {
		if (abortion) {
			operationClaim.setAbortionAmount(300000.00);
		} else {
			operationClaim.setAbortionAmount(00.00);
		}
	}

	public void setAbortion(boolean abortion) {
		this.abortion = abortion;
	}

}
