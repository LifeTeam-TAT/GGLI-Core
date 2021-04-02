package org.ace.insurance.payment;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.ace.insurance.common.TableName;


@Entity
@Table(name = TableName.REVERSELOG)
@TableGenerator(name = "REVERSELOG_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "REVERSELOG_GEN", allocationSize = 10)
public class ReversePaymentTransactionLog implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "REVERSELOG_GEN")
	private String id;
	private String userId;
	private Date reverseDate;
	private String receiptNo;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getReverseDate() {
		return reverseDate;
	}
	public void setReverseDate(Date reverseDate) {
		this.reverseDate = reverseDate;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((receiptNo == null) ? 0 : receiptNo.hashCode());
		result = prime * result + ((reverseDate == null) ? 0 : reverseDate.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		ReversePaymentTransactionLog other = (ReversePaymentTransactionLog) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (receiptNo == null) {
			if (other.receiptNo != null)
				return false;
		} else if (!receiptNo.equals(other.receiptNo))
			return false;
		if (reverseDate == null) {
			if (other.reverseDate != null)
				return false;
		} else if (!reverseDate.equals(other.reverseDate))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	
	
	

}
