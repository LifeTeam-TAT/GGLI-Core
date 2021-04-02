package org.ace.insurance.travel.personTravel.proposal;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.township.Township;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PROPOSALTRAVELLER)
@TableGenerator(name = "PROPOSALTRAVELLER_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PROPOSALTRAVELLER_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "ProposalTraveller.findAll", query = "SELECT p FROM ProposalTraveller p") })
@EntityListeners(IDInterceptor.class)
public class ProposalTraveller implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PROPOSALTRAVELLER_GEN")
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
	@JoinColumn(name = "PROPOSALTRAVELLERID", referencedColumnName = "ID")
	private List<ProposalTravellerBeneficiary> proposalTravellerBeneficiaryList;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Transient
	private String tempId;

	@Version
	private int version;

	/***** Default Constructor *****/
	public ProposalTraveller() {
		tempId = System.nanoTime() + "";
	}

	public String getTempId() {
		return tempId;
	}

	public ProposalTraveller(ProposalTraveller proposalTraveller) {
		this.tempId = proposalTraveller.getTempId();
		this.id = proposalTraveller.getId();
		this.unit = proposalTraveller.getUnit();
		this.premium = proposalTraveller.getPremium();
		this.name = proposalTraveller.getName();
		this.fatherName = proposalTraveller.getFatherName();
		this.nrcNo = proposalTraveller.getNrcNo();
		this.address = proposalTraveller.getAddress();
		this.basicTermPremium = proposalTraveller.getBasicTermPremium();
		this.email = proposalTraveller.getEmail();
		this.phone = proposalTraveller.getPhone();
		this.township = proposalTraveller.getTownship();
		this.version = proposalTraveller.getVersion();
		for (ProposalTravellerBeneficiary beneficiary : proposalTraveller.getProposalTravellerBeneficiaryList()) {
			addProposalTravellerBeneficiary(new ProposalTravellerBeneficiary(beneficiary));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getNrcNo() {
		return nrcNo;
	}

	public void setNrcNo(String nrcNo) {
		this.nrcNo = nrcNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Township getTownship() {
		return township;
	}

	public void setTownship(Township township) {
		this.township = township;
	}

	public List<ProposalTravellerBeneficiary> getProposalTravellerBeneficiaryList() {
		if (this.proposalTravellerBeneficiaryList == null) {
			this.proposalTravellerBeneficiaryList = new ArrayList<ProposalTravellerBeneficiary>();
		}
		return proposalTravellerBeneficiaryList;
	}

	public void setProposalTravellerBeneficiaryList(List<ProposalTravellerBeneficiary> proposalTravellerBeneficiary) {
		this.proposalTravellerBeneficiaryList = proposalTravellerBeneficiary;
	}

	public void addProposalTravellerBeneficiary(ProposalTravellerBeneficiary beneficiary) {
		getProposalTravellerBeneficiaryList().add(beneficiary);
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

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
		result = prime * result + ((nrcNo == null) ? 0 : nrcNo.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
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
		ProposalTraveller other = (ProposalTraveller) obj;
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
		if (nrcNo == null) {
			if (other.nrcNo != null)
				return false;
		} else if (!nrcNo.equals(other.nrcNo))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
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
