package org.ace.insurance.life.claim;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.claim.disabilityPart.ProductDisabilityRate;
import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.paymenttype.PaymentType;

@Entity
@Table(name = TableName.DISABILITYLIFECLAIM)
@DiscriminatorValue(value = LifeClaimType.DISIBILITY_CLAIM)
public class DisabilityLifeClaim extends LifePolicyClaim implements Serializable {
	private static final long serialVersionUID = 1L;

	private int waitingPeriod;
	private int paymentterm;
	private int paidterm;

	@Enumerated(value = EnumType.STRING)
	private PaymentStatus paymentStatus;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@Temporal(TemporalType.TIMESTAMP)
	private Date waitingEndDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date waitingStartDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTDISABILITYRATEID", referencedColumnName = "ID")
	private ProductDisabilityRate productDisabilityRate;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	private String disabilityPart;

	private double percentage;

	private double termDisabilityAmount;

	private double disabilityAmount;

	public DisabilityLifeClaim() {
		super();
	}

	public int getWaitingPeriod() {
		return waitingPeriod;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public int getPaymentterm() {
		return paymentterm;
	}

	public void setPaymentterm(int paymentterm) {
		this.paymentterm = paymentterm;
	}

	public int getPaidterm() {
		return paidterm;
	}

	public void setPaidterm(int paidterm) {
		this.paidterm = paidterm;
	}

	public Date getWaitingEndDate() {
		return waitingEndDate;
	}

	public Date getWaitingStartDate() {
		return waitingStartDate;
	}

	public ProductDisabilityRate getProductDisabilityRate() {
		return productDisabilityRate;
	}

	public String getDisabilityPart() {
		return disabilityPart;
	}

	public double getPercentage() {
		return percentage;
	}

	public double getDisabilityAmount() {
		return disabilityAmount;
	}

	public void setWaitingPeriod(int waitingPeriod) {
		this.waitingPeriod = waitingPeriod;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public void setWaitingEndDate(Date waitingEndDate) {
		this.waitingEndDate = waitingEndDate;
	}

	public void setWaitingStartDate(Date waitingStartDate) {
		this.waitingStartDate = waitingStartDate;
	}

	public void setProductDisabilityRate(ProductDisabilityRate productDisabilityRate) {
		this.productDisabilityRate = productDisabilityRate;
	}

	public void setDisabilityPart(String disabilityPart) {
		this.disabilityPart = disabilityPart;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public void setDisabilityAmount(double disabilityAmount) {
		this.disabilityAmount = disabilityAmount;
	}

	public double getTermDisabilityAmount() {
		return termDisabilityAmount;
	}

	public void setTermDisabilityAmount(double termDisabilityAmount) {
		this.termDisabilityAmount = termDisabilityAmount;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		long temp;
		temp = Double.doubleToLongBits(disabilityAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((disabilityPart == null) ? 0 : disabilityPart.hashCode());
		result = prime * result + paidterm;
		result = prime * result + ((paymentStatus == null) ? 0 : paymentStatus.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + paymentterm;
		temp = Double.doubleToLongBits(percentage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((productDisabilityRate == null) ? 0 : productDisabilityRate.hashCode());
		temp = Double.doubleToLongBits(termDisabilityAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((waitingEndDate == null) ? 0 : waitingEndDate.hashCode());
		result = prime * result + waitingPeriod;
		result = prime * result + ((waitingStartDate == null) ? 0 : waitingStartDate.hashCode());
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
		DisabilityLifeClaim other = (DisabilityLifeClaim) obj;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (Double.doubleToLongBits(disabilityAmount) != Double.doubleToLongBits(other.disabilityAmount))
			return false;
		if (disabilityPart == null) {
			if (other.disabilityPart != null)
				return false;
		} else if (!disabilityPart.equals(other.disabilityPart))
			return false;
		if (paidterm != other.paidterm)
			return false;
		if (paymentStatus != other.paymentStatus)
			return false;
		if (paymentType == null) {
			if (other.paymentType != null)
				return false;
		} else if (!paymentType.equals(other.paymentType))
			return false;
		if (paymentterm != other.paymentterm)
			return false;
		if (Double.doubleToLongBits(percentage) != Double.doubleToLongBits(other.percentage))
			return false;
		if (productDisabilityRate == null) {
			if (other.productDisabilityRate != null)
				return false;
		} else if (!productDisabilityRate.equals(other.productDisabilityRate))
			return false;
		if (Double.doubleToLongBits(termDisabilityAmount) != Double.doubleToLongBits(other.termDisabilityAmount))
			return false;
		if (waitingEndDate == null) {
			if (other.waitingEndDate != null)
				return false;
		} else if (!waitingEndDate.equals(other.waitingEndDate))
			return false;
		if (waitingPeriod != other.waitingPeriod)
			return false;
		if (waitingStartDate == null) {
			if (other.waitingStartDate != null)
				return false;
		} else if (!waitingStartDate.equals(other.waitingStartDate))
			return false;
		return true;
	}

}
