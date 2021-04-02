package org.ace.insurance.medical.numberReference;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.ace.insurance.common.TableName;

@Entity
@Table(name = TableName.NUMBER_REFERENCE)
@NamedQueries(value = { @NamedQuery(name = "NumberReference.findNewNumberByOldNumber", query = "SELECT n.newNumber from NumberReference n where n.oldNumber=:oldNumber"),
		@NamedQuery(name = "NumberReference.findNewNumberByNewNumber", query = "SELECT n.oldNumber from NumberReference n where n.newNumber=:newNumber") })
public class NumberReference implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "OLD_NUMBER")
	private String oldNumber;
	@Column(name = "NEW_NUMBER")
	private String newNumber;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOldNumber() {
		return oldNumber;
	}

	public void setOldNumber(String oldNumber) {
		this.oldNumber = oldNumber;
	}

	public String getNewNumber() {
		return newNumber;
	}

	public void setNewNumber(String newNumber) {
		this.newNumber = newNumber;
	}

}
