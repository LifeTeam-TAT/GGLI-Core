package org.ace.insurance.mobile.transaction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ace.insurance.common.TableName;

@Entity
@Table(name = TableName.TWOC2P_RESPONSE)
public class PaymentResponse {

	@Id
	@GeneratedValue
	String id;
	String uniqueTransactionCode;
	String dateTime;
	String paidAgent;
	String approvalCode;
	String backendInvoice;
	String amt;
	String aci;
	String bankName;
	String tranRef;
	String paymentScheme;
	String processBy;
	String failReason;
	String pan;
	String ippInterestRate;
	String ippInterestType;
	String ippPeriod;
	String version;
	String hashValue;
	String paymentChannel;
	String timeStamp;
	String issuerCountry;
	String merchantID;
	String refNumber;
	String userDefined1;
	String userDefined2;
	String userDefined3;
	String userDefined4;
	String userDefined5;
	String paidChannel;
	String respCode;
	String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getPaidAgent() {
		return paidAgent;
	}

	public void setPaidAgent(String paidAgent) {
		this.paidAgent = paidAgent;
	}

	public String getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	public String getBackendInvoice() {
		return backendInvoice;
	}

	public void setBackendInvoice(String backendInvoice) {
		this.backendInvoice = backendInvoice;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getAci() {
		return aci;
	}

	public void setAci(String aci) {
		this.aci = aci;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getTranRef() {
		return tranRef;
	}

	public void setTranRef(String tranRef) {
		this.tranRef = tranRef;
	}

	public String getPaymentScheme() {
		return paymentScheme;
	}

	public void setPaymentScheme(String paymentScheme) {
		this.paymentScheme = paymentScheme;
	}

	public String getProcessBy() {
		return processBy;
	}

	public void setProcessBy(String processBy) {
		this.processBy = processBy;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getIppInterestRate() {
		return ippInterestRate;
	}

	public void setIppInterestRate(String ippInterestRate) {
		this.ippInterestRate = ippInterestRate;
	}

	public String getIppInterestType() {
		return ippInterestType;
	}

	public void setIppInterestType(String ippInterestType) {
		this.ippInterestType = ippInterestType;
	}

	public String getIppPeriod() {
		return ippPeriod;
	}

	public void setIppPeriod(String ippPeriod) {
		this.ippPeriod = ippPeriod;
	}

	public String getUniqueTransactionCode() {
		return uniqueTransactionCode;
	}

	public void setUniqueTransactionCode(String uniqueTransactionCode) {
		this.uniqueTransactionCode = uniqueTransactionCode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHashValue() {
		return hashValue;
	}

	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}

	public String getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(String paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getIssuerCountry() {
		return issuerCountry;
	}

	public void setIssuerCountry(String issuerCountry) {
		this.issuerCountry = issuerCountry;
	}

	public String getMerchantID() {
		return merchantID;
	}

	public void setMerchantID(String merchantID) {
		this.merchantID = merchantID;
	}

	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	public String getUserDefined1() {
		return userDefined1;
	}

	public void setUserDefined1(String userDefined1) {
		this.userDefined1 = userDefined1;
	}

	public String getUserDefined2() {
		return userDefined2;
	}

	public void setUserDefined2(String userDefined2) {
		this.userDefined2 = userDefined2;
	}

	public String getUserDefined3() {
		return userDefined3;
	}

	public void setUserDefined3(String userDefined3) {
		this.userDefined3 = userDefined3;
	}

	public String getUserDefined4() {
		return userDefined4;
	}

	public void setUserDefined4(String userDefined4) {
		this.userDefined4 = userDefined4;
	}

	public String getUserDefined5() {
		return userDefined5;
	}

	public void setUserDefined5(String userDefined5) {
		this.userDefined5 = userDefined5;
	}

	public String getPaidChannel() {
		return paidChannel;
	}

	public void setPaidChannel(String paidChannel) {
		this.paidChannel = paidChannel;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aci == null) ? 0 : aci.hashCode());
		result = prime * result + ((amt == null) ? 0 : amt.hashCode());
		result = prime * result + ((approvalCode == null) ? 0 : approvalCode.hashCode());
		result = prime * result + ((backendInvoice == null) ? 0 : backendInvoice.hashCode());
		result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((failReason == null) ? 0 : failReason.hashCode());
		result = prime * result + ((hashValue == null) ? 0 : hashValue.hashCode());
		result = prime * result + ((ippInterestRate == null) ? 0 : ippInterestRate.hashCode());
		result = prime * result + ((ippInterestType == null) ? 0 : ippInterestType.hashCode());
		result = prime * result + ((ippPeriod == null) ? 0 : ippPeriod.hashCode());
		result = prime * result + ((issuerCountry == null) ? 0 : issuerCountry.hashCode());
		result = prime * result + ((merchantID == null) ? 0 : merchantID.hashCode());
		result = prime * result + ((paidAgent == null) ? 0 : paidAgent.hashCode());
		result = prime * result + ((paidChannel == null) ? 0 : paidChannel.hashCode());
		result = prime * result + ((pan == null) ? 0 : pan.hashCode());
		result = prime * result + ((paymentChannel == null) ? 0 : paymentChannel.hashCode());
		result = prime * result + ((paymentScheme == null) ? 0 : paymentScheme.hashCode());
		result = prime * result + ((processBy == null) ? 0 : processBy.hashCode());
		result = prime * result + ((refNumber == null) ? 0 : refNumber.hashCode());
		result = prime * result + ((respCode == null) ? 0 : respCode.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
		result = prime * result + ((tranRef == null) ? 0 : tranRef.hashCode());
		result = prime * result + ((uniqueTransactionCode == null) ? 0 : uniqueTransactionCode.hashCode());
		result = prime * result + ((userDefined1 == null) ? 0 : userDefined1.hashCode());
		result = prime * result + ((userDefined2 == null) ? 0 : userDefined2.hashCode());
		result = prime * result + ((userDefined3 == null) ? 0 : userDefined3.hashCode());
		result = prime * result + ((userDefined4 == null) ? 0 : userDefined4.hashCode());
		result = prime * result + ((userDefined5 == null) ? 0 : userDefined5.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		PaymentResponse other = (PaymentResponse) obj;
		if (aci == null) {
			if (other.aci != null)
				return false;
		} else if (!aci.equals(other.aci))
			return false;
		if (amt == null) {
			if (other.amt != null)
				return false;
		} else if (!amt.equals(other.amt))
			return false;
		if (approvalCode == null) {
			if (other.approvalCode != null)
				return false;
		} else if (!approvalCode.equals(other.approvalCode))
			return false;
		if (backendInvoice == null) {
			if (other.backendInvoice != null)
				return false;
		} else if (!backendInvoice.equals(other.backendInvoice))
			return false;
		if (bankName == null) {
			if (other.bankName != null)
				return false;
		} else if (!bankName.equals(other.bankName))
			return false;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (failReason == null) {
			if (other.failReason != null)
				return false;
		} else if (!failReason.equals(other.failReason))
			return false;
		if (hashValue == null) {
			if (other.hashValue != null)
				return false;
		} else if (!hashValue.equals(other.hashValue))
			return false;
		if (ippInterestRate == null) {
			if (other.ippInterestRate != null)
				return false;
		} else if (!ippInterestRate.equals(other.ippInterestRate))
			return false;
		if (ippInterestType == null) {
			if (other.ippInterestType != null)
				return false;
		} else if (!ippInterestType.equals(other.ippInterestType))
			return false;
		if (ippPeriod == null) {
			if (other.ippPeriod != null)
				return false;
		} else if (!ippPeriod.equals(other.ippPeriod))
			return false;
		if (issuerCountry == null) {
			if (other.issuerCountry != null)
				return false;
		} else if (!issuerCountry.equals(other.issuerCountry))
			return false;
		if (merchantID == null) {
			if (other.merchantID != null)
				return false;
		} else if (!merchantID.equals(other.merchantID))
			return false;
		if (paidAgent == null) {
			if (other.paidAgent != null)
				return false;
		} else if (!paidAgent.equals(other.paidAgent))
			return false;
		if (paidChannel == null) {
			if (other.paidChannel != null)
				return false;
		} else if (!paidChannel.equals(other.paidChannel))
			return false;
		if (pan == null) {
			if (other.pan != null)
				return false;
		} else if (!pan.equals(other.pan))
			return false;
		if (paymentChannel == null) {
			if (other.paymentChannel != null)
				return false;
		} else if (!paymentChannel.equals(other.paymentChannel))
			return false;
		if (paymentScheme == null) {
			if (other.paymentScheme != null)
				return false;
		} else if (!paymentScheme.equals(other.paymentScheme))
			return false;
		if (processBy == null) {
			if (other.processBy != null)
				return false;
		} else if (!processBy.equals(other.processBy))
			return false;
		if (refNumber == null) {
			if (other.refNumber != null)
				return false;
		} else if (!refNumber.equals(other.refNumber))
			return false;
		if (respCode == null) {
			if (other.respCode != null)
				return false;
		} else if (!respCode.equals(other.respCode))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (timeStamp == null) {
			if (other.timeStamp != null)
				return false;
		} else if (!timeStamp.equals(other.timeStamp))
			return false;
		if (tranRef == null) {
			if (other.tranRef != null)
				return false;
		} else if (!tranRef.equals(other.tranRef))
			return false;
		if (uniqueTransactionCode == null) {
			if (other.uniqueTransactionCode != null)
				return false;
		} else if (!uniqueTransactionCode.equals(other.uniqueTransactionCode))
			return false;
		if (userDefined1 == null) {
			if (other.userDefined1 != null)
				return false;
		} else if (!userDefined1.equals(other.userDefined1))
			return false;
		if (userDefined2 == null) {
			if (other.userDefined2 != null)
				return false;
		} else if (!userDefined2.equals(other.userDefined2))
			return false;
		if (userDefined3 == null) {
			if (other.userDefined3 != null)
				return false;
		} else if (!userDefined3.equals(other.userDefined3))
			return false;
		if (userDefined4 == null) {
			if (other.userDefined4 != null)
				return false;
		} else if (!userDefined4.equals(other.userDefined4))
			return false;
		if (userDefined5 == null) {
			if (other.userDefined5 != null)
				return false;
		} else if (!userDefined5.equals(other.userDefined5))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
