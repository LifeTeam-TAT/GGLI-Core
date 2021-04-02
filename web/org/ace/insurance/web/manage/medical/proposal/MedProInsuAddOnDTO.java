package org.ace.insurance.web.manage.medical.proposal;

import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.web.common.CommonDTO;

public class MedProInsuAddOnDTO extends CommonDTO {
	private boolean selected;
	private double proposedPremium;
	private double proposedSumInsured;
	private double approvedPremium;
	private double approvedSumInsured;
	private AddOn addOn;
	private int unit;
	private MedicalProposalInsuredPerson proposalInsuredPerson;

	public MedProInsuAddOnDTO() {
	}

	public MedProInsuAddOnDTO(AddOn addOn) {
		this.addOn = addOn;
	}

	public MedicalProposalInsuredPerson getProposalInsuredPerson() {
		return proposalInsuredPerson;
	}

	public void setProposalInsuredPerson(MedicalProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
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

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addOn == null) ? 0 : addOn.hashCode());
		long temp;
		temp = Double.doubleToLongBits(approvedPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(approvedSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((proposalInsuredPerson == null) ? 0 : proposalInsuredPerson.hashCode());
		temp = Double.doubleToLongBits(proposedPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(proposedSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + unit;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MedProInsuAddOnDTO other = (MedProInsuAddOnDTO) obj;
		if (addOn == null) {
			if (other.addOn != null)
				return false;
		} else if (!addOn.equals(other.addOn))
			return false;
		if (Double.doubleToLongBits(approvedPremium) != Double.doubleToLongBits(other.approvedPremium))
			return false;
		if (Double.doubleToLongBits(approvedSumInsured) != Double.doubleToLongBits(other.approvedSumInsured))
			return false;
		if (proposalInsuredPerson == null) {
			if (other.proposalInsuredPerson != null)
				return false;
		} else if (!proposalInsuredPerson.equals(other.proposalInsuredPerson))
			return false;
		if (Double.doubleToLongBits(proposedPremium) != Double.doubleToLongBits(other.proposedPremium))
			return false;
		if (Double.doubleToLongBits(proposedSumInsured) != Double.doubleToLongBits(other.proposedSumInsured))
			return false;
		if (unit != other.unit)
			return false;
		return true;
	}

}
