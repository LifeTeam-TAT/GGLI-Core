/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.deno;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.java.component.FormatID;

@Entity
@Table(name=TableName.DENO)
@TableGenerator(name = "DENO_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "DENO_GEN", allocationSize = 10)
@NamedQueries(value = {
		@NamedQuery(name = "Deno.findAll", query = "SELECT d FROM Deno d WHERE d.currency.currencyCode = 'KYT' ORDER BY d.d1 ASC"),
		@NamedQuery(name = "Deno.findById", query = "SELECT d FROM Deno d WHERE d.id = :id") })
@Access(value = AccessType.FIELD)
public class Deno implements Serializable {
	@Transient
	private String id;
	@Transient
	private String prefix;
	private String name;
	private String symbol;	
	private float d1;
	private float d2;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CURRENCYID", referencedColumnName = "ID")
	private Currency currency;

	@Version
	private int version;

	public Deno() {
	}	

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "DENO_GEN")
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public float getD1() {
		return d1;
	}

	public void setD1(float d1) {
		this.d1 = d1;
	}

	public float getD2() {
		return d2;
	}

	public void setD2(float d2) {
		this.d2 = d2;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
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
		result = prime * result + Float.floatToIntBits(d1);
		result = prime * result + Float.floatToIntBits(d2);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
		Deno other = (Deno) obj;
		if (Float.floatToIntBits(d1) != Float.floatToIntBits(other.d1))
			return false;
		if (Float.floatToIntBits(d2) != Float.floatToIntBits(other.d2))
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
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}