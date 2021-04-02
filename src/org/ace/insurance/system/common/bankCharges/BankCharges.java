package org.ace.insurance.system.common.bankCharges;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.java.component.FormatID;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.BANKCHARGES)
@TableGenerator(name = "BANKCHARGES_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "BANKCHARGES_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
@Access(value = AccessType.FIELD)
public class BankCharges implements Serializable {

	private static final long serialVersionUID = 1L;
	@Transient
	private String id;
	@Transient
	private String prefix;
	private String name;

	@Enumerated(EnumType.STRING)
	private TypesOfCharges typesOfCharges;

	private double charges;
	private double additionalCharges;

	@Temporal(TemporalType.DATE)
	private Date startdate;

	@Temporal(TemporalType.DATE)
	private Date enddate;
	private boolean status;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BANKID", referencedColumnName = "ID")
	private Bank bank;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	private int version;

	public BankCharges() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "BANKCHARGES_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCharges() {
		return charges;
	}

	public void setCharges(double charges) {
		this.charges = charges;
	}

	public TypesOfCharges getTypesOfCharges() {
		return typesOfCharges;
	}

	public void setTypesOfCharges(TypesOfCharges typesOfCharges) {
		this.typesOfCharges = typesOfCharges;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public double getAdditionalCharges() {
		return additionalCharges;
	}

	public void setAdditionalCharges(double additionalCharges) {
		this.additionalCharges = additionalCharges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(additionalCharges);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((bank == null) ? 0 : bank.hashCode());
		temp = Double.doubleToLongBits(charges);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((enddate == null) ? 0 : enddate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((startdate == null) ? 0 : startdate.hashCode());
		result = prime * result + (status ? 1231 : 1237);
		result = prime * result + ((typesOfCharges == null) ? 0 : typesOfCharges.hashCode());
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
		BankCharges other = (BankCharges) obj;
		if (Double.doubleToLongBits(additionalCharges) != Double.doubleToLongBits(other.additionalCharges))
			return false;
		if (bank == null) {
			if (other.bank != null)
				return false;
		} else if (!bank.equals(other.bank))
			return false;
		if (Double.doubleToLongBits(charges) != Double.doubleToLongBits(other.charges))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (enddate == null) {
			if (other.enddate != null)
				return false;
		} else if (!enddate.equals(other.enddate))
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
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (startdate == null) {
			if (other.startdate != null)
				return false;
		} else if (!startdate.equals(other.startdate))
			return false;
		if (status != other.status)
			return false;
		if (typesOfCharges != other.typesOfCharges)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
