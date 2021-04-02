package org.ace.insurance.travel.personTravel.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.mobile.travelProposal.MobileTravelInsuredPerson;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.travel.personTravel.proposal.ProposalTraveller;
import org.ace.insurance.travel.personTravel.proposal.ProposalTravellerBeneficiary;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.POLICYTRAVELLER)
@TableGenerator(name = "POLICYTRAVELLER_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "POLICYTRAVELLER_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PolicyTraveller.findAll", query = "SELECT p FROM PolicyTraveller p") })
@EntityListeners(IDInterceptor.class)
public class PolicyTraveller implements Serializable {

	private static final long serialVersionUID = -8750966541973697006L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "POLICYTRAVELLER_GEN")
	private String id;
	private int unit;
	private double premium;
	private double basicTermPremium;
	private String name;
	private String fatherName;
	private String nrcNo;
	private String email;
	private String phone;
	private String address;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOWNSHIPID", referencedColumnName = "ID")
	private Township township;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "POLICYTRAVELLERID", referencedColumnName = "ID")
	private List<PolicyTravellerBeneficiary> policyTravellerBeneficiaryList;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public PolicyTraveller() {
	}

	public PolicyTraveller(ProposalTraveller proposalTraveller) {
		this.unit = proposalTraveller.getUnit();
		this.premium = proposalTraveller.getPremium();
		this.basicTermPremium = proposalTraveller.getBasicTermPremium();
		this.name = proposalTraveller.getName();
		this.fatherName = proposalTraveller.getFatherName();
		this.nrcNo = proposalTraveller.getNrcNo();
		this.email = proposalTraveller.getEmail();
		this.phone = proposalTraveller.getPhone();
		this.address = proposalTraveller.getAddress();
		this.township = proposalTraveller.getTownship();
		for (ProposalTravellerBeneficiary beneficiary : proposalTraveller.getProposalTravellerBeneficiaryList()) {
			if (policyTravellerBeneficiaryList == null) {
				this.policyTravellerBeneficiaryList = new ArrayList<>();
			}
			this.policyTravellerBeneficiaryList.add(new PolicyTravellerBeneficiary(beneficiary));
		}
	}

	public PolicyTraveller(MobileTravelInsuredPerson mtip) {
		this.unit = mtip.getUnit();
		this.premium = mtip.getPremium();
		this.basicTermPremium = mtip.getPremium();
		this.nrcNo = mtip.getIdNo();
		this.name = mtip.getFullName();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the unit
	 */
	public int getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(int unit) {
		this.unit = unit;
	}

	/**
	 * @return the premium
	 */
	public double getPremium() {
		return premium;
	}

	/**
	 * @param premium
	 *            the premium to set
	 */
	public void setPremium(double premium) {
		this.premium = premium;
	}

	/**
	 * @return the basicTermPremium
	 */
	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	/**
	 * @param basicTermPremium
	 *            the basicTermPremium to set
	 */
	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the fatherName
	 */
	public String getFatherName() {
		return fatherName;
	}

	/**
	 * @param fatherName
	 *            the fatherName to set
	 */
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	/**
	 * @return the nrcNo
	 */
	public String getNrcNo() {
		return nrcNo;
	}

	/**
	 * @param nrcNo
	 *            the nrcNo to set
	 */
	public void setNrcNo(String nrcNo) {
		this.nrcNo = nrcNo;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the township
	 */
	public Township getTownship() {
		return township;
	}

	/**
	 * @param township
	 *            the township to set
	 */
	public void setTownship(Township township) {
		this.township = township;
	}

	/**
	 * @return the policyTravellerBeneficiaryList
	 */
	public List<PolicyTravellerBeneficiary> getPolicyTravellerBeneficiaryList() {
		return policyTravellerBeneficiaryList;
	}

	/**
	 * @param policyTravellerBeneficiaryList
	 *            the policyTravellerBeneficiaryList to set
	 */
	public void setPolicyTravellerBeneficiaryList(List<PolicyTravellerBeneficiary> policyTravellerBeneficiaryList) {
		this.policyTravellerBeneficiaryList = policyTravellerBeneficiaryList;
	}

	/**
	 * @return the commonCreateAndUpateMarks
	 */
	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	/**
	 * @param commonCreateAndUpateMarks
	 *            the commonCreateAndUpateMarks to set
	 */
	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public String getFullAddress() {
		String result = "";
		if (!address.isEmpty()) {
			result += address;
		}
		if (township != null) {
			if (!result.isEmpty()) {
				result += " , " + township.getName();
			}

		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		long temp;
		temp = Double.doubleToLongBits(basicTermPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fatherName == null) ? 0 : fatherName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nrcNo == null) ? 0 : nrcNo.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((policyTravellerBeneficiaryList == null) ? 0 : policyTravellerBeneficiaryList.hashCode());
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((township == null) ? 0 : township.hashCode());
		result = prime * result + unit;
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
		PolicyTraveller other = (PolicyTraveller) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (Double.doubleToLongBits(basicTermPremium) != Double.doubleToLongBits(other.basicTermPremium))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fatherName == null) {
			if (other.fatherName != null)
				return false;
		} else if (!fatherName.equals(other.fatherName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nrcNo == null) {
			if (other.nrcNo != null)
				return false;
		} else if (!nrcNo.equals(other.nrcNo))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (policyTravellerBeneficiaryList == null) {
			if (other.policyTravellerBeneficiaryList != null)
				return false;
		} else if (!policyTravellerBeneficiaryList.equals(other.policyTravellerBeneficiaryList))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (township == null) {
			if (other.township != null)
				return false;
		} else if (!township.equals(other.township))
			return false;
		if (unit != other.unit)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
