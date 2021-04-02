/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.claimaccept;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.CLAIMACCEPTEDINFO)
@TableGenerator(name = "CLAIMACCEPTEDINFO_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "CLAIMACCEPTEDINFO_GEN", allocationSize = 10)
@NamedQueries(value = {
		@NamedQuery(name = "ClaimAcceptedInfo.findByReferenceNo", query = "SELECT a FROM ClaimAcceptedInfo a WHERE a.referenceNo = :referenceNo AND a.referenceType = :referenceType") })
@Access(value = AccessType.FIELD)
public class ClaimAcceptedInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Transient
	private String id;
	@Transient
	private String prefix;

	@Column(name = "REFERENCENO")
	private String referenceNo;

	@Column(name = "REFERENCETYPE")
	@Enumerated(value = EnumType.STRING)
	private ReferenceType referenceType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@Column(name = "SERVICECHARGES")
	private Double servicesCharges;

	private double claimAmount;

	private double duePremium;

	@Column(name = "INFORMDATE")
	@Temporal(TemporalType.DATE)
	private Date informDate;

	@Version
	private int version;

	public ClaimAcceptedInfo() {
		informDate = new Date();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "CLAIMACCEPTEDINFO_GEN")
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

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public Double getServicesCharges() {
		if (servicesCharges == null) {
			servicesCharges = new Double(0.0);
		}
		return servicesCharges;
	}

	public void setServicesCharges(Double servicesCharges) {
		this.servicesCharges = servicesCharges;
	}

	public Number getServicesChargesNum() {
		if (servicesCharges == null) {
			servicesCharges = new Double(0.0);
		}
		return servicesCharges;
	}

	public void setServicesChargesNum(Number servicesCharges) {
		if (servicesCharges != null) {
			this.servicesCharges = servicesCharges.doubleValue();
		}
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(double claimAmount) {
		this.claimAmount = claimAmount;
	}

	public double getDuePremium() {
		return duePremium;
	}

	public void setDuePremium(double duePremium) {
		this.duePremium = duePremium;
	}

	public Date getInformDate() {
		return informDate;
	}

	public void setInformDate(Date informDate) {
		this.informDate = informDate;
	}

	public double getTotalAmount() {
		return claimAmount - servicesCharges.doubleValue() - duePremium;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(claimAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(duePremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((informDate == null) ? 0 : informDate.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((referenceType == null) ? 0 : referenceType.hashCode());
		result = prime * result + ((servicesCharges == null) ? 0 : servicesCharges.hashCode());
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
		ClaimAcceptedInfo other = (ClaimAcceptedInfo) obj;
		if (Double.doubleToLongBits(claimAmount) != Double.doubleToLongBits(other.claimAmount))
			return false;
		if (Double.doubleToLongBits(duePremium) != Double.doubleToLongBits(other.duePremium))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (informDate == null) {
			if (other.informDate != null)
				return false;
		} else if (!informDate.equals(other.informDate))
			return false;
		if (paymentType == null) {
			if (other.paymentType != null)
				return false;
		} else if (!paymentType.equals(other.paymentType))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (referenceNo == null) {
			if (other.referenceNo != null)
				return false;
		} else if (!referenceNo.equals(other.referenceNo))
			return false;
		if (referenceType != other.referenceType)
			return false;
		if (servicesCharges == null) {
			if (other.servicesCharges != null)
				return false;
		} else if (!servicesCharges.equals(other.servicesCharges))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}