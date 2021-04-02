package org.ace.insurance.travel.expressTravel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.express.Express;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.TRAVEL_EXPRESS)
@TableGenerator(name = "EXPRESS_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "EXPRESS_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "TravelExpress.findAll", query = "SELECT e FROM TravelExpress e") })
@Access(value = AccessType.FIELD)
public class TravelExpress implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;
	@Transient
	private String prefix;
	private int noOfPassenger;
	private int noOfUnit;
	private double netPremium;
	private double commission;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXPRESSID", referencedColumnName = "ID")
	private Express express;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "travelExpress", orphanRemoval = true)
	private List<Tour> tourList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRAVELPROPOSALID", referencedColumnName = "ID")
	private TravelProposal travelProposal;

	@Transient
	private String tempId;

	@Version
	private int version;

	public TravelExpress() {
		tempId = System.nanoTime() + "";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "EXPRESS_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void addTour(Tour tour) {
		if (tourList == null) {
			tourList = new ArrayList<Tour>();
		}
		if (!tourList.contains(tour)) {
			tour.setTravelExpress(this);
			tourList.add(tour);
		}
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

	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}

	public double getNetPremium() {
		return netPremium;
	}

	public void setNetPremium(double netPremium) {
		this.netPremium = netPremium;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public int getNoOfUnit() {
		return noOfUnit;
	}

	public void setNoOfUnit(int noOfUnit) {
		this.noOfUnit = noOfUnit;
	}

	public List<Tour> getTourList() {
		return tourList;
	}

	public void setTourList(List<Tour> tourList) {
		this.tourList = tourList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((express == null) ? 0 : express.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(netPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + noOfPassenger;
		result = prime * result + noOfUnit;
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
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
		TravelExpress other = (TravelExpress) obj;
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
		if (Double.doubleToLongBits(netPremium) != Double.doubleToLongBits(other.netPremium))
			return false;
		if (noOfPassenger != other.noOfPassenger)
			return false;
		if (noOfUnit != other.noOfUnit)
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
			return false;
		if (travelProposal == null) {
			if (other.travelProposal != null)
				return false;
		} else if (!travelProposal.equals(other.travelProposal))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

}
