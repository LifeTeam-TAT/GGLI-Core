package org.ace.insurance.travel.personTravel.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PERSONTRAVEL_PROPOSALINFO)
@TableGenerator(name = "PERSONTRAVEL_PROPOSALINFO_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PERSONTRAVEL_PROPOSALINFO_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PersonTravelProposalInfo.findAll", query = "SELECT p FROM PersonTravelProposalInfo p") })
@EntityListeners(IDInterceptor.class)
public class PersonTravelProposalInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PERSONTRAVEL_PROPOSALINFO_GEN")
	private String id;
	private int noOfPassenger;
	private int totalUnit;
	private int paymentTerm;
	private double premiumRate;
	private double premium;
	private double basicTermPremium;

	@Temporal(TemporalType.DATE)
	private Date departureDate;

	@Temporal(TemporalType.DATE)
	private Date arrivalDate;

	@Column(name = "TRAVELPATH")
	private String travelPath;

	private boolean isRoundTrip;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXPRESSID", referencedColumnName = "ID")
	private Express express;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PERSONTRAVELINFOID", referencedColumnName = "ID")
	private List<ProposalPersonTravelVehicle> proposalPersonTravelVehicleList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PERSONTRAVELINFOID", referencedColumnName = "ID")
	private List<PersonTravelProposalKeyfactorValue> travelProposalKeyfactorValueList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PERSONTRAVELINFOID", referencedColumnName = "ID")
	private List<ProposalTraveller> proposalTravellerList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "HOLDERID", referencedColumnName = "ID")
	private List<Attachment> attachmentList;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Transient
	private String tempId;

	@Version
	private int version;

	public PersonTravelProposalInfo() {

		tempId = System.nanoTime() + "";
	}

	public String getTempId() {
		return tempId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}

	public int getTotalUnit() {
		return totalUnit;
	}

	public void setTotalUnit(int totalUnit) {
		this.totalUnit = totalUnit;
	}

	public int getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public double getPremiumRate() {
		return premiumRate;
	}

	public void setPremiumRate(double premiumRate) {
		this.premiumRate = premiumRate;
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

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getTravelPath() {
		return travelPath;
	}

	public void setTravelPath(String travelPath) {
		this.travelPath = travelPath;
	}

	public boolean isRoundTrip() {
		return isRoundTrip;
	}

	public void setRoundTrip(boolean isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}

	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public void loadKeyFactor(Product product) {
		this.travelProposalKeyfactorValueList = new ArrayList<PersonTravelProposalKeyfactorValue>();

		for (KeyFactor kf : product.getKeyFactorList()) {
			PersonTravelProposalKeyfactorValue insKf = new PersonTravelProposalKeyfactorValue(kf);
			insKf.setKeyfactor(kf);
			this.travelProposalKeyfactorValueList.add(insKf);
		}

	}

	public List<PersonTravelProposalKeyfactorValue> getTravelProposalKeyfactorValueList() {
		if (travelProposalKeyfactorValueList == null) {
			travelProposalKeyfactorValueList = new ArrayList<PersonTravelProposalKeyfactorValue>();
		}
		return travelProposalKeyfactorValueList;
	}

	public void setTravelProposalKeyfactorValueList(List<PersonTravelProposalKeyfactorValue> travelProposalKeyfactorValueList) {
		this.travelProposalKeyfactorValueList = travelProposalKeyfactorValueList;
	}

	public List<ProposalTraveller> getProposalTravellerList() {
		return proposalTravellerList;
	}

	public void setProposalTravellerList(List<ProposalTraveller> proposalTravellerList) {
		this.proposalTravellerList = proposalTravellerList;
	}

	public List<Attachment> getAttachmentList() {
		if (attachmentList == null) {
			attachmentList = new ArrayList<Attachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<Attachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public void addAttachment(Attachment attachment) {
		if (attachmentList == null) {
			attachmentList = new ArrayList<Attachment>();
		}
		attachmentList.add(attachment);
	}

	/**
	 * @return the proposalPersonTravelVehicleList
	 */
	public List<ProposalPersonTravelVehicle> getProposalPersonTravelVehicleList() {
		return proposalPersonTravelVehicleList;
	}

	/**
	 * @param proposalPersonTravelVehicleList
	 *            the proposalPersonTravelVehicleList to set
	 */
	public void setProposalPersonTravelVehicleList(List<ProposalPersonTravelVehicle> proposalPersonTravelVehicleList) {
		this.proposalPersonTravelVehicleList = proposalPersonTravelVehicleList;
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

	public double getTotalBasicTermPremium() {
		double termPermium = 0.0;
		termPermium = Utils.getTwoDecimalPoint(termPermium + getBasicTermPremium());
		return termPermium;
	}

	public int getPeriodOfInsurance() {
		return Utils.daysBetween(departureDate, arrivalDate, false, true);
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
		result = prime * result + ((arrivalDate == null) ? 0 : arrivalDate.hashCode());
		long temp;
		temp = Double.doubleToLongBits(basicTermPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((departureDate == null) ? 0 : departureDate.hashCode());
		result = prime * result + ((express == null) ? 0 : express.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isRoundTrip ? 1231 : 1237);
		result = prime * result + noOfPassenger;
		result = prime * result + paymentTerm;
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(premiumRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
		result = prime * result + totalUnit;
		result = prime * result + ((travelPath == null) ? 0 : travelPath.hashCode());
		result = prime * result + version;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonTravelProposalInfo other = (PersonTravelProposalInfo) obj;
		if (arrivalDate == null) {
			if (other.arrivalDate != null)
				return false;
		} else if (!arrivalDate.equals(other.arrivalDate))
			return false;
		if (Double.doubleToLongBits(basicTermPremium) != Double.doubleToLongBits(other.basicTermPremium))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (departureDate == null) {
			if (other.departureDate != null)
				return false;
		} else if (!departureDate.equals(other.departureDate))
			return false;
		if (express == null) {
			if (other.express != null)
				return false;
		} else if (!express.equals(other.express))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isRoundTrip != other.isRoundTrip)
			return false;
		if (noOfPassenger != other.noOfPassenger)
			return false;
		if (paymentTerm != other.paymentTerm)
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (Double.doubleToLongBits(premiumRate) != Double.doubleToLongBits(other.premiumRate))
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
			return false;
		if (totalUnit != other.totalUnit)
			return false;
		if (travelPath == null) {
			if (other.travelPath != null)
				return false;
		} else if (!travelPath.equals(other.travelPath))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
