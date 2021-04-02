package org.ace.insurance.life.premium;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.interfaces.IEntity;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.java.component.FormatID;

/**
 * @author T&D Infomation System Ltd
 * @since 1.0.0
 * @date 2013/07/11
 */

@Entity
@Table(name = TableName.LIFEPOLICYPREMIUM)
@TableGenerator(name = "LIFEPOLICYPREMIUM_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFEPOLICYPREMIUM_GEN", allocationSize = 10)
@NamedQueries(value = {
		@NamedQuery(name = "LifePolicyPremium.findAll", query = "SELECT m FROM LifePolicyPremium m "),
		@NamedQuery(name = "LifePolicyPremium.findById", query = "SELECT m FROM LifePolicyPremium m WHERE m.id = :id") })
@Access(value = AccessType.FIELD)
public class LifePolicyPremium implements Serializable, IEntity {

	@Transient
	private String id;
	@Transient
	private String prefix;

	private int totalBillingAmount;
	private int totalPaymentTimes;
	private int totalCollectionAmount;
	private boolean isPaymentFinished;

	@Temporal(TemporalType.TIMESTAMP)
	private Date nextCreateBillingDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFEPOLICYID", referencedColumnName = "ID")
	private LifePolicy lifePolicy;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "LifePolicyPremium", orphanRemoval = true)
	private List<LifePolicyBilling> lifePolicyBillingList;

	@Version
	private int version;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFEPOLICYPREMIUM_GEN")
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

	public int getTotalBillingAmount() {
		return totalBillingAmount;
	}

	public void setTotalBillingAmount(int totalBillingAmount) {
		this.totalBillingAmount = totalBillingAmount;
	}

	public int getTotalPaymentTimes() {
		return totalPaymentTimes;
	}

	public void setTotalPaymentTimes(int totalPaymentTimes) {
		this.totalPaymentTimes = totalPaymentTimes;
	}

	public int getTotalCollectionAmount() {
		return totalCollectionAmount;
	}

	public void setTotalCollectionAmount(int totalCollectionAmount) {
		this.totalCollectionAmount = totalCollectionAmount;
	}

	public boolean isPaymentFinished() {
		return isPaymentFinished;
	}

	public void setPaymentFinished(boolean isPaymentFinished) {
		this.isPaymentFinished = isPaymentFinished;
	}

	public List<LifePolicyBilling> getLifePolicyBillingList() {
		return lifePolicyBillingList;
	}

	public void setLifePolicyBillingList(List<LifePolicyBilling> lifePolicyBillingList) {
		this.lifePolicyBillingList = lifePolicyBillingList;
	}

	public void addLifePolicyBilling(LifePolicyBilling lifePolicyBilling) {
		if (lifePolicyBillingList == null) {
			lifePolicyBillingList = new ArrayList<LifePolicyBilling>();
		}
		lifePolicyBilling.setLifePolicyPremium(this);
		this.lifePolicyBillingList.add(lifePolicyBilling);
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public Date getNextCreateBillingDate() {
		return nextCreateBillingDate;
	}

	public void setNextCreateBillingDate(Date nextCreateBillingDate) {
		this.nextCreateBillingDate = nextCreateBillingDate;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPaymentFinished ? 1231 : 1237);
		result = prime * result + ((nextCreateBillingDate == null) ? 0 : nextCreateBillingDate.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + totalBillingAmount;
		result = prime * result + totalCollectionAmount;
		result = prime * result + totalPaymentTimes;
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
		LifePolicyPremium other = (LifePolicyPremium) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isPaymentFinished != other.isPaymentFinished)
			return false;
		if (nextCreateBillingDate == null) {
			if (other.nextCreateBillingDate != null)
				return false;
		} else if (!nextCreateBillingDate.equals(other.nextCreateBillingDate))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (totalBillingAmount != other.totalBillingAmount)
			return false;
		if (totalCollectionAmount != other.totalCollectionAmount)
			return false;
		if (totalPaymentTimes != other.totalPaymentTimes)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
