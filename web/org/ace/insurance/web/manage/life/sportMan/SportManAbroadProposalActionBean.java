/**
 * This class serves as the backing bean for submitting the sport man proposal (abroad).
 * 
 * @author YKKH
 * @version 1.0.0
 * @Date 2013/12/07
 */
package org.ace.insurance.web.manage.life.sportMan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.life.claim.LifePolicySearch;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.web.manage.life.proposal.BeneficiariesInfoDTO;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "SportManAbroadProposalActionBean")
public class SportManAbroadProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townShipService;

	public void setTownShipService(ITownshipService townShipService) {
		this.townShipService = townShipService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	private String policyNo;
	private PolicyCriteria policyCriteria;
	private BeneficiariesInfoDTO beneficiariesInfoDTO;
	private Map<String, BeneficiariesInfoDTO> beneficiariesInfoDTOMap;
	private String beneficiaryRelationShipId;
	private InsuredPersonInfoDTO insuredPersonInfoDTO;
	private List<RelationShip> relationshipList;
	private LifePolicy lifePolicy;

	@PostConstruct
	public void init() {
		policyCriteria = new PolicyCriteria();
		createNewInsuredPersonInfo();
		createNewBeneficiariesInfoDTOMap();
		createNewBeneficiariesInfo();
		relationshipList = relationShipService.findAllRelationShip();
	}
	
	    @PreDestroy
	    public void destroy() {
	        removeParam("lifePolicy");
	        removeParam("InsuranceType");
	    }

	/** Insured Person methods start */

	private void createNewInsuredPersonInfo() {
		insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		insuredPersonInfoDTO.setStartDate(new Date());
	}

	public InsuredPersonInfoDTO getInsuredPersonInfoDTO() {
		return insuredPersonInfoDTO;
	}

	/** Insured Person methods end */

	/** Beneficiaries methods start */

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	/**
	 * This method used to save the BeneficiariesInfo into the memory.
	 * 
	 */
	public void saveBeneficiariesInfo() {
		if (validBeneficiary(beneficiariesInfoDTO)) {
			beneficiaryRelationShipId = null;
			beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
			insuredPersonInfoDTO.setBeneficiariesInfoDTOList(new ArrayList<BeneficiariesInfoDTO>(beneficiariesInfoDTOMap.values()));
			createNewBeneficiariesInfo();
			PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
		}
	}

	/**
	 * This method is used to validate the information of Beneficiaries people.
	 * 
	 * @param beneficiariesInfoDTO
	 * @return validated boolean value
	 */
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

			return valid; // to exist the method without checking total
							// percentage below
		}

		// to validate total percentage
		double total = 0.0;
		for (BeneficiariesInfoDTO bene : beneficiariesInfoDTOMap.values()) {
			if (!(bene.getTempId().equals(beneficiariesInfoDTO.getTempId()))) {
				total = total + bene.getPercentage();
			}
		}

		total = total + beneficiariesInfoDTO.getPercentage();

		if (total > 100) {
			addErrorMessage(formID + ":percentage", MessageId.OVER_BENEFICIARY_PERCENTAGE);
			valid = false;
		}

		return valid;
	}

	/**
	 * This method prepares to edit Beneficiaries Information.
	 * 
	 * @param beneficiariesInfoDTO
	 */
	public void prepareEditBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;

		if (beneficiariesInfoDTO.getRelationship() != null) {
			this.beneficiaryRelationShipId = beneficiariesInfoDTO.getRelationship().getName();
		} else {
			this.beneficiaryRelationShipId = null;
		}
	}

	/**
	 * This method removes Beneficiary.
	 * 
	 * @param beneficiariesInfoDTO
	 */
	public void removeBeneficiariesInfo(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfoDTO.getTempId());
		insuredPersonInfoDTO.getBeneficiariesInfoDTOList().remove(beneficiariesInfoDTO);
		createNewBeneficiariesInfo();
	}

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, BeneficiariesInfoDTO>();
	}

	public BeneficiariesInfoDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public void setBeneficiariesInfoDTO(BeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public String getBeneficiaryRelationShipId() {
		return beneficiaryRelationShipId;
	}

	public void setBeneficiaryRelationShipId(String beneficiaryRelationShipId) {
		this.beneficiaryRelationShipId = beneficiaryRelationShipId;
	}

	public void prepareAddNewBeneficiariesInfo() {
		createNewBeneficiariesInfo();
	}

	private void createNewBeneficiariesInfo() {
		beneficiariesInfoDTO = new BeneficiariesInfoDTO();
	}

	/** Beneficiaries methods end */

	/** Common methods start */
	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * This method find LifePolicy object and populate data into InsuredPerson
	 * DTO.
	 */
	public void search() {
		String formId = "LifePolicyForm";
		LifePolicy lp = lifePolicyService.findLifePolicyByPolicyNo(policyNo);
		insuredPersonInfoDTO = new InsuredPersonInfoDTO(lp.getPolicyInsuredPersonList().get(0));

		for (PolicyInsuredPersonBeneficiaries p : lp.getPolicyInsuredPersonList().get(0).getPolicyInsuredPersonBeneficiariesList()) {
			BeneficiariesInfoDTO beneDTO = new BeneficiariesInfoDTO(p);
			beneficiariesInfoDTOMap.put(beneDTO.getTempId(), beneDTO);
		}

		if (lp.equals(null)) {
			addErrorMessage(formId + ":lifePolicy", MessageId.POLICY_NUMBER_NOT_EXIST);
			return;
		}

		if (StringUtils.isBlank(policyNo)) {
			addErrorMessage(formId + ":lifePolicy", UIInput.REQUIRED_MESSAGE_ID);
			return;
		}
	}

	/**
	 * This method is used to reset the whole page including search results.
	 */
	public void reset() {
		init();
		policyNo = "";
	}

	public void searchPolicyCriteria() {
		if (inputCheck(policyCriteria)) {
			policyCriteria.setProduct("SPORTMAN");
		}
	}

	public void resetPolicyCriteria() {
		policyCriteria.setCriteriaValue("");
	}

	private boolean inputCheck(PolicyCriteria policyCriteria) {
		boolean result = true;
		if ((policyCriteria.getPolicyCriteria() == null) || (policyCriteria.getPolicyCriteria().toString().equals("Select"))) {
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select policy criteria.", "Please select policy criteria"));
			result = false;
		} else if (StringUtils.isBlank(policyCriteria.getCriteriaValue())) {
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Criteria value is required.", "Criteria value is required."));
			result = false;
		}
		return result;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public PolicyCriteria getPolicyCriteria() {
		return policyCriteria;
	}

	public void setPolicyCriteria(PolicyCriteria policyCriteria) {
		this.policyCriteria = policyCriteria;
	}

	/** Common methods end */

	public void returnLifePolicy(SelectEvent event) {
		LifePolicySearch lifePolicySearch = (LifePolicySearch) event.getObject();
		lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(lifePolicySearch.getPolicyNo());
		this.policyNo = lifePolicy.getPolicyNo();
	}

	public void returnTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}
}
