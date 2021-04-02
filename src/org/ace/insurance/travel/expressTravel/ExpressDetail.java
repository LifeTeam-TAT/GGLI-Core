package org.ace.insurance.travel.expressTravel;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.EXPRESSDETAIL)
@TableGenerator(name = "EXPRESSDETAIL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "EXPRESSDETAIL_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "ExpressDetail.findExpressDetailByTravelReportCriteria", query = "SELECT d FROM ExpressDetail d INNER JOIN Tour t ON d.tour.id = t.id"
		+ " INNER JOIN TravelExpress e ON t.travelExpress.id = e.id INNER JOIN TravelProposal p ON e.travelProposal.id = p.id "
		+ "WHERE p.submittedDate = :submittedDate AND p.branch = :branch") })
@Access(value = AccessType.FIELD)
public class ExpressDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;
	private String prefix;

	private String registrationNo;
	private int noOfPassenger;
	private int noOfUnit;
	private double premium;
	private double commission;
	private double netPremium;
	@Temporal(TemporalType.DATE)
	private Date date;
	private String remark;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOURID", referencedColumnName = "ID")
	private Tour tour;

	@Version
	private int version;

	@Transient
	private String tempId;

	public ExpressDetail() {
		tempId = System.nanoTime() + "";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "EXPRESSDETAIL_GEN")
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

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public double getNetPremium() {
		return netPremium;
	}

	public void setNetPremium(double netPremium) {
		this.netPremium = netPremium;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public Tour getTour() {
		return tour;
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}

	public int getNoOfUnit() {
		return noOfUnit;
	}

	public void setNoOfUnit(int noOfUnit) {
		this.noOfUnit = noOfUnit;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getExpressName() {
		String expressName = getTour().getTravelExpress().getExpress().getName();
		return expressName;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(commission);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		temp = Double.doubleToLongBits(netPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + noOfPassenger;
		result = prime * result + noOfUnit;
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((registrationNo == null) ? 0 : registrationNo.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
		result = prime * result + ((tour == null) ? 0 : tour.hashCode());
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
		ExpressDetail other = (ExpressDetail) obj;
		if (Double.doubleToLongBits(commission) != Double.doubleToLongBits(other.commission))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
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
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (registrationNo == null) {
			if (other.registrationNo != null)
				return false;
		} else if (!registrationNo.equals(other.registrationNo))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
			return false;
		if (tour == null) {
			if (other.tour != null)
				return false;
		} else if (!tour.equals(other.tour))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
