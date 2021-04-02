package org.ace.insurance.life.proposalhistory;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.life.policy.PolicyInsuredPersonAddon;
import org.ace.insurance.life.proposal.InsuredPersonAddon;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonAddOnDTO;
import org.ace.java.component.FormatID;

@Entity
@Table(name=TableName.LIFEPROPOSAL_INSURED_PERSON_ADDON_HISTORY)
@TableGenerator(name = "LIFEPROPOSAL_INSURED_PERSON_ADDON_HISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFEPROPOSAL_INSURED_PERSON_ADDON_HISTORY_GEN", allocationSize = 10)
@NamedQueries(value = {
		@NamedQuery(name = "LifeProposalInsuredPersonAddonHistory.findAll", query = "SELECT v FROM LifeProposalInsuredPersonAddonHistory v "),
		@NamedQuery(name = "LifeProposalInsuredPersonAddonHistory.findById", query = "SELECT v FROM LifeProposalInsuredPersonAddonHistory v WHERE v.id = :id") })
@Access(value = AccessType.FIELD)
public class LifeProposalInsuredPersonAddonHistory {
	
	@Transient
	private String id;
	@Transient
	private String prefix;

	private double proposedPremium;
	private double proposedSumInsured;
	private double approvedPremium;
	private double approvedSumInsured;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFEPRODUCTADDONID", referencedColumnName = "ID")
	private AddOn addOn;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFEPROPOSALINSUREDPERSONID", referencedColumnName = "ID")
	private LifeProposalInsuredPersonHistory lifeProposalInsuredPersonHistory;
	
	@Version
	private int version;
	
	public LifeProposalInsuredPersonAddonHistory(){}
	
	public LifeProposalInsuredPersonAddonHistory(AddOn addOn, double proposedSumInsured){
		this.addOn = addOn;
		this.proposedSumInsured = proposedSumInsured;
	}

	public LifeProposalInsuredPersonAddonHistory(PolicyInsuredPersonAddon policyInsPersonaddOn){
		this.addOn = policyInsPersonaddOn.getAddOn();
		this.proposedPremium = policyInsPersonaddOn.getPremium();
		this.proposedSumInsured = policyInsPersonaddOn.getSumInsured();
	}
	
	public LifeProposalInsuredPersonAddonHistory(InsuredPersonAddon insPersonaddOn){
		this.addOn = insPersonaddOn.getAddOn();
		this.proposedPremium = insPersonaddOn.getProposedPremium();
		this.proposedSumInsured = insPersonaddOn.getProposedSumInsured();
	}
	
	public LifeProposalInsuredPersonAddonHistory(InsuredPersonAddOnDTO dto) {
		this.addOn = dto.getAddOn();
		this.proposedSumInsured = dto.getAddOnSumInsured();
		this.proposedPremium = dto.getPremium();
		this.approvedPremium = dto.getApprovedPremium();
		this.approvedSumInsured = dto.getApprovedSumInsured();
		if (dto.isExistEntity()) {
			this.id = dto.getTempId();
			this.version = dto.getVersion();
		}
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFEPROPOSAL_INSURED_PERSON_ADDON_HISTORY_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public double getProposedPremium() {
		return proposedPremium;
	}

	public void setProposedPremium(double proposedPremium) {
		this.proposedPremium = proposedPremium;
	}

	public double getProposedSumInsured() {
		return proposedSumInsured;
	}

	public void setProposedSumInsured(double proposedSumInsured) {
		this.proposedSumInsured = proposedSumInsured;
	}

	public double getApprovedPremium() {
		return approvedPremium;
	}

	public void setApprovedPremium(double approvedPremium) {
		this.approvedPremium = approvedPremium;
	}

	public double getApprovedSumInsured() {
		return approvedSumInsured;
	}

	public void setApprovedSumInsured(double approvedSumInsured) {
		this.approvedSumInsured = approvedSumInsured;
	}

	
	public AddOn getAddOn() {
		return addOn;
	}

	public void setAddOn(AddOn addOn) {
		this.addOn = addOn;
	}
	

	public LifeProposalInsuredPersonHistory getLifeProposalInsuredPersonHistory() {
		return lifeProposalInsuredPersonHistory;
	}

	public void setLifeProposalInsuredPersonHistory(LifeProposalInsuredPersonHistory lifeProposalInsuredPersonHistory) {
		this.lifeProposalInsuredPersonHistory = lifeProposalInsuredPersonHistory;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(approvedPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(approvedSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		temp = Double.doubleToLongBits(proposedPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(proposedSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LifeProposalInsuredPersonAddonHistory other = (LifeProposalInsuredPersonAddonHistory) obj;
		if (Double.doubleToLongBits(approvedPremium) != Double.doubleToLongBits(other.approvedPremium))
			return false;
		if (Double.doubleToLongBits(approvedSumInsured) != Double.doubleToLongBits(other.approvedSumInsured))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (Double.doubleToLongBits(proposedPremium) != Double.doubleToLongBits(other.proposedPremium))
			return false;
		if (Double.doubleToLongBits(proposedSumInsured) != Double.doubleToLongBits(other.proposedSumInsured))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
}