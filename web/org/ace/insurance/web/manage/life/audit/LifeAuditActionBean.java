package org.ace.insurance.web.manage.life.audit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.java.component.idgen.IDGen;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "LifeAuditActionBean")
public class LifeAuditActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	@ManagedProperty(value = "#{CustomIDGenerator}")
	private ICustomIDGenerator customIDGenerator;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	public void setCustomIDGenerator(ICustomIDGenerator customIDGenerator) {
		this.customIDGenerator = customIDGenerator;
	}
	// ********************** Global Variable **********************

	private String proposalNo;
	private LifeProposal lifeProposal;
	private LifePolicy lifePolicy;
	private List<String> personCodeList;
	private List<String> beneficiaryCodeList;
	// building
	private int maxPersonValue;
	private int calMaxPersonValue;
	private int generatePersonCount;
	private IDGen personIDGen;

	// env building
	private int maxBeneValue;
	private int calMaxBeneValue;
	private int generateBeneCount;
	private IDGen beneIDGen;

	public void intitalize() {
		personIDGen = customIDGenerator.getIDGen(SystemConstants.LIFE_INSUREDPERSON_CODENO, false);
		maxPersonValue = personIDGen.getMaxValue();
		calMaxPersonValue = maxPersonValue;
		personCodeList = new ArrayList<String>();

		beneIDGen = customIDGenerator.getIDGen(SystemConstants.LIFE_BENEFICIARY_NO, false);
		maxBeneValue = beneIDGen.getMaxValue();
		calMaxBeneValue = maxBeneValue;
		beneficiaryCodeList = new ArrayList<String>();

		generatePersonCount = 0;
		generateBeneCount = 0;

		lifeProposal = null;
		lifePolicy = null;
	}

	// ********************** Action Handler **********************

	public void searchLifeProposal() {
		intitalize();
		lifeProposal = lifeProposalService.findLifeProposalByProposalNo(proposalNo);
		if (lifeProposal != null) {
			lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposal.getId());
		}
	}

	public void generatePersonCodeNo() {
		maxPersonValue++;
		generatePersonCount++;
		String personCodeNo = formatId(String.valueOf(maxPersonValue), personIDGen, null);
		personCodeList.add(personCodeNo);
	}

	public void generateBeneficiaryCodeNo() {
		maxBeneValue++;
		generateBeneCount++;
		String beneCodeNo = formatId(String.valueOf(maxBeneValue), beneIDGen, null);
		beneficiaryCodeList.add(beneCodeNo);
	}

	public void updatePersonCode() {
		List<IDGen> idGenList = new ArrayList<IDGen>();
		personIDGen.setMaxValue(calMaxPersonValue + generatePersonCount);
		beneIDGen.setMaxValue(calMaxBeneValue + generateBeneCount);
		idGenList.add(personIDGen);
		idGenList.add(beneIDGen);
		List<InsuredPersonBeneficiaries> proposalBeneList = new ArrayList<InsuredPersonBeneficiaries>();
		List<PolicyInsuredPersonBeneficiaries> policyBeneList = new ArrayList<PolicyInsuredPersonBeneficiaries>();
		for (ProposalInsuredPerson person : lifeProposal.getProposalInsuredPersonList()) {
			proposalBeneList.addAll(person.getInsuredPersonBeneficiariesList());
		}
		for (PolicyInsuredPerson person : lifePolicy.getPolicyInsuredPersonList()) {
			policyBeneList.addAll(person.getPolicyInsuredPersonBeneficiariesList());
		}

		lifeProposalService.updateCodeNo(lifeProposal.getProposalInsuredPersonList(), lifePolicy.getPolicyInsuredPersonList(), proposalBeneList, policyBeneList, idGenList);
		addInfoMessage(null, MessageId.UPDATE_SUCCESS, proposalNo);
		clearData();
	}

	private void clearData() {
		this.lifeProposal = null;
		this.lifePolicy = null;
		this.personCodeList = new ArrayList<String>();
		this.beneficiaryCodeList = new ArrayList<String>();
		this.proposalNo = null;
	}

	public boolean isUpdate() {
		return (lifeProposal != null) && (lifePolicy != null);
	}

	// ********************** Helper **********************

	private String formatId(String id, IDGen idGen, String productCode) {
		String prefix = idGen.getPrefix();
		String suffix = idGen.getSuffix();
		int maxLength = idGen.getLength();
		String branchCode = "001";
		int idLength = id.length();
		for (; (maxLength - idLength) > 0; idLength++) {
			id = '0' + id;
		}
		if (suffix == null) {
			suffix = "";
		}
		if (productCode == null) {
			productCode = "";
		}
		id = prefix + productCode + "/" + getDateString() + "/" + id + "/" + branchCode + suffix;
		return id;
	}

	private String getDateString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
		return simpleDateFormat.format(new Date());
	}

	// ********************** Getter / Setter **********************

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public List<String> getPersonCodeList() {
		return personCodeList;
	}

	public List<String> getBeneficiaryCodeList() {
		return beneficiaryCodeList;
	}

}
