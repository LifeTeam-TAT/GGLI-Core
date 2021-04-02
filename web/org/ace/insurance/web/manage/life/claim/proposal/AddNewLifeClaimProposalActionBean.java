package org.ace.insurance.web.manage.life.claim.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claim.disabilityPart.ProductDisabilityRate;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LCBP001;
import org.ace.insurance.life.claim.LifeClaimBeneficiaryPerson;
import org.ace.insurance.life.claim.LifeClaimNotification;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifeHospitalizedClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.claim.MedicalBeneficiaryRole;
import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewLifeClaimProposalActionBean")
public class AddNewLifeClaimProposalActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	private User user;
	private LifeClaimNotification lifeClaimNotification;
	private LifeClaimProposal claimProposal;
	private LifePolicyClaim lifePolicyClaim;
	private LifeDeathClaim deathClaim;
	private LifeHospitalizedClaim hospitalizedClaim;
	private DisabilityLifeClaim disabilityLifeClaim;
	private List<DisabilityLifeClaim> disabilityLifeClaimList;
	private boolean isDeathClaim;
	private boolean isHospitalClaim;
	private boolean isDisibilityClaim;
	private String[] selectedClaimTypes;
	private User responsiblePerson;
	private String remark;
	private List<LCBP001> beneficiaryList;
	private LifeClaimBeneficiaryPerson claimSuccessor;
	private boolean isSuccessor;
	private LCBP001 selectedBeneficiary;
	private List<RelationShip> relationShipList;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeClaimNotification = (lifeClaimNotification == null) ? (LifeClaimNotification) getParam("lifeClaimNotification") : lifeClaimNotification;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeClaimNotification");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		createLifePolicyClaimProposal();
		bindLifeNotificationdate();
		relationShipList = relationShipService.findAllRelationShip();

	}

	private void createLifePolicyClaimProposal() {
		claimProposal = new LifeClaimProposal();
		lifePolicyClaim = new LifePolicyClaim();
		deathClaim = new LifeDeathClaim();
		hospitalizedClaim = new LifeHospitalizedClaim();
		disabilityLifeClaim = new DisabilityLifeClaim();
		disabilityLifeClaimList = new ArrayList<DisabilityLifeClaim>();
	}

	private void bindLifeNotificationdate() {
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(lifeClaimNotification.getLifePolicyNo());
		claimProposal.setNotificationNo(lifeClaimNotification.getNotificationNo());
		claimProposal.setLifePolicy(lifePolicy);
		claimProposal.setClaimPerson(lifeClaimNotification.getClaimPerson());
		claimProposal.setProduct(lifeClaimNotification.getProduct());
		claimProposal.setBranch(lifeClaimNotification.getBranch());
		claimProposal.setSubmittedDate(new Date());
		claimProposal.setOccuranceDate(lifeClaimNotification.getOccuranceDate());
		claimProposal.setSalePoint(lifeClaimNotification.getSalePoint());
	}

	private void addLifePolicyClaim() {
		if (isDeathClaim) {
			claimProposal.addClaim(deathClaim);
		}
		if (isHospitalClaim) {
			claimProposal.addClaim(hospitalizedClaim);
		}
		if (isDisibilityClaim) {
			claimProposal.addClaim(disabilityLifeClaim);
			/*
			 * for (DisabilityLifeClaim disabilityLifeClaim :
			 * disabilityLifeClaimList) {
			 * claimProposal.addClaim(disabilityLifeClaim); }
			 */
		}
	}

	private void addLifeClaimBeneficiaryPerson() {
		if (isDeathClaim) {
			for (LCBP001 lcbp001 : beneficiaryList) {
				if (!lcbp001.isDeath()) {
					LifeClaimBeneficiaryPerson claimBeneficiaryPerson = new LifeClaimBeneficiaryPerson();
					claimBeneficiaryPerson.setBeneficiaryRole(MedicalBeneficiaryRole.BENEFICIARY);
					claimBeneficiaryPerson.setBeneficiaryPerson(lcbp001.getBeneficiaryPerson());
					claimProposal.addBeneficiary(claimBeneficiaryPerson);
				}
			}
		} else if (claimSuccessor != null) {
			claimProposal.addBeneficiary(claimSuccessor);
		} else {
			LifeClaimBeneficiaryPerson claimBeneficiaryPerson = new LifeClaimBeneficiaryPerson();
			claimBeneficiaryPerson.setBeneficiaryRole(MedicalBeneficiaryRole.INSURED_PERSON);
			claimBeneficiaryPerson.setPolicyInsuredPerson(claimProposal.getClaimPerson());
			claimProposal.addBeneficiary(claimBeneficiaryPerson);
		}
	}

	public void calculateAdmissionFee(AjaxBehaviorEvent event) {
		if (hospitalizedClaim.getStartDate() != null && hospitalizedClaim.getEndDate() != null) {
			int totalHospitalizedDays = Utils.daysBetween(hospitalizedClaim.getStartDate(), hospitalizedClaim.getEndDate(), false, true);
			hospitalizedClaim.setNoOfdays(totalHospitalizedDays);
			double admissionFee = claimProposal.getClaimPerson().getSumInsured() * 0.02 * totalHospitalizedDays;
			hospitalizedClaim.setHospitalizedAmount(admissionFee);
		}
	}

	public void changeDisabilityAmount(AjaxBehaviorEvent event) {
		double disabilityAmt = claimProposal.getClaimPerson().getSumInsured() * disabilityLifeClaim.getPercentage() / 100.00;
		disabilityLifeClaim.setDisabilityAmount(disabilityAmt);
	}

	public void changeDisabilityPercentage(AjaxBehaviorEvent event) {
		double percentage = disabilityLifeClaim.getDisabilityAmount() / claimProposal.getClaimPerson().getSumInsured() * 100.00;
		disabilityLifeClaim.setPercentage(percentage);
	}

	public String addNewLifeClaimProposal() {
		String result = null;
		try {
			addLifePolicyClaim();
			addLifeClaimBeneficiaryPerson();
			WorkFlowDTO workFlowDTO = new WorkFlowDTO("", remark, WorkflowTask.CLAIM_SURVEY, WorkflowReferenceType.LIFE_CLAIM, user, responsiblePerson);
			lifeClaimProposalService.addNewLifeClaimProposal(claimProposal, lifeClaimNotification, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, claimProposal.getClaimProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "lifeClaimProposalForm";
		PolicyInsuredPerson claimPerson = claimProposal.getClaimPerson();

		if ("claimProposal".equals(event.getOldStep())) {
			if (isDeathClaim && isDisibilityClaim) {
				addErrorMessage(formID + ":selectClaimType", MessageId.DEATH_DISABILITY_CHOOSE);
				valid = false;
			}
		}
		if ("claimProposal".equals(event.getOldStep()) && "claimType".equals(event.getNewStep())) {
			if (isDeathClaim) {
				double deathClaimAmt = claimPerson.getSumInsured();
				deathClaim.setDeathClaimAmount(deathClaimAmt);
			}
		}
		if ("claimType".equals(event.getOldStep()) && "beneficiaryTab".equals(event.getNewStep())) {
			if (isDeathClaim) {
				beneficiaryList = new ArrayList<LCBP001>();
				for (PolicyInsuredPersonBeneficiaries benefi : claimPerson.getPolicyInsuredPersonBeneficiariesList()) {
					LCBP001 lcbp = new LCBP001(benefi, false);
					beneficiaryList.add(lcbp);
				}
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public void returnMedicalPlaceDialog(SelectEvent event) {
		Hospital medicalPlace = (Hospital) event.getObject();
		hospitalizedClaim.setMedicalPlace(medicalPlace);
	}

	public void returnDisabilityPartDialog(SelectEvent event) {
		ProductDisabilityRate disability = (ProductDisabilityRate) event.getObject();
		disabilityLifeClaim.setDisabilityPart(disability.getDisabilityPart().getName());
		disabilityLifeClaim.setPercentage(disability.getPercentage());
		disabilityLifeClaim.setProductDisabilityRate(disability);
		double disabilityAmt = claimProposal.getClaimPerson().getSumInsured() * disability.getPercentage() / 100.00;
		disabilityLifeClaim.setDisabilityAmount(disabilityAmt);
	}

	public void addDisabilityPart() {
		disabilityLifeClaimList.add(disabilityLifeClaim);
		disabilityLifeClaim = new DisabilityLifeClaim();
	}

	public void changeClaimType(AjaxBehaviorEvent event) {
		if (selectedClaimTypes != null) {
			isHospitalClaim = false;
			isDeathClaim = false;
			isDisibilityClaim = false;

			for (String claimtype : selectedClaimTypes) {
				if (claimtype.equalsIgnoreCase("Hospitalization")) {
					isHospitalClaim = true;
				} else if (claimtype.equalsIgnoreCase("Disability")) {
					isDisibilityClaim = true;
				} else if (claimtype.equalsIgnoreCase("Death")) {
					isDeathClaim = true;
				}
			}
		}
	}

	public void prepareEditDeathBeneficiary(LCBP001 person) {
		this.selectedBeneficiary = person;
	}

	public void updateDeathBeneficiary() {
		if (selectedBeneficiary.isDeath()) {
			isSuccessor = true;
			claimSuccessor = new LifeClaimBeneficiaryPerson();
			claimSuccessor.setPercentage(selectedBeneficiary.getBeneficiaryPerson().getPercentage());
			// claimSuccessor.setClaimAmount(claimAmount); to add Claim Amount
		} else {
			isSuccessor = false;
			claimSuccessor = null;
		}
		PrimeFaces.current().executeScript("PF('beneficiaryDeathDialog').hide()");
	}

	public LifeClaimNotification getLifeClaimNotification() {
		return lifeClaimNotification;
	}

	public LifePolicyClaim getLifePolicyClaim() {
		return lifePolicyClaim;
	}

	public LifeDeathClaim getDeathClaim() {
		return deathClaim;
	}

	public LifeHospitalizedClaim getHospitalizedClaim() {
		return hospitalizedClaim;
	}

	public void setLifePolicyClaim(LifePolicyClaim lifePolicyClaim) {
		this.lifePolicyClaim = lifePolicyClaim;
	}

	public void setDeathClaim(LifeDeathClaim deathClaim) {
		this.deathClaim = deathClaim;
	}

	public void setHospitalizedClaim(LifeHospitalizedClaim hospitalizedClaim) {
		this.hospitalizedClaim = hospitalizedClaim;
	}

	public LifeClaimProposal getClaimProposal() {
		return claimProposal;
	}

	public void setClaimProposal(LifeClaimProposal claimProposal) {
		this.claimProposal = claimProposal;
	}

	public String[] getSelectedClaimTypes() {
		return selectedClaimTypes;
	}

	public void setSelectedClaimTypes(String[] selectedClaimTypes) {
		this.selectedClaimTypes = selectedClaimTypes;
	}

	public boolean getIsDeathClaim() {
		return isDeathClaim;
	}

	public boolean getIsHospitalClaim() {
		return isHospitalClaim;
	}

	public boolean getIsDisibilityClaim() {
		return isDisibilityClaim;
	}

	public void setDeathClaim(boolean isDeathClaim) {
		this.isDeathClaim = isDeathClaim;
	}

	public void setHospitalClaim(boolean isHospitalClaim) {
		this.isHospitalClaim = isHospitalClaim;
	}

	public void setDisibilityClaim(boolean isDisibilityClaim) {
		this.isDisibilityClaim = isDisibilityClaim;
	}

	public DisabilityLifeClaim getDisabilityLifeClaim() {
		return disabilityLifeClaim;
	}

	public void setDisabilityLifeClaim(DisabilityLifeClaim disabilityLifeClaim) {
		this.disabilityLifeClaim = disabilityLifeClaim;
	}

	public List<DisabilityLifeClaim> getDisabilityLifeClaimList() {
		return disabilityLifeClaimList;
	}

	public void setDisabilityLifeClaimList(List<DisabilityLifeClaim> disabilityLifeClaimList) {
		this.disabilityLifeClaimList = disabilityLifeClaimList;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean getIsSuccessor() {
		return isSuccessor;
	}

	public void setSuccessor(boolean isSuccessor) {
		this.isSuccessor = isSuccessor;
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_SURVEY, WorkFlowType.LIFE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public LifeClaimBeneficiaryPerson getClaimSuccessor() {
		return claimSuccessor;
	}

	public List<LCBP001> getBeneficiaryList() {
		return beneficiaryList;
	}

	public void setBeneficiaryList(List<LCBP001> beneficiaryList) {
		this.beneficiaryList = beneficiaryList;
	}

	public LCBP001 getSelectedBeneficiary() {
		return selectedBeneficiary;
	}

	public void setSelectedBeneficiary(LCBP001 selectedBeneficiary) {
		this.selectedBeneficiary = selectedBeneficiary;
	}

	public List<RelationShip> getRelationShipList() {
		return relationShipList;
	}

}
