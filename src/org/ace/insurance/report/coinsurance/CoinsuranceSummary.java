package org.ace.insurance.report.coinsurance;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.COINSURANCESUMMARY)
@ReadOnly
public class CoinsuranceSummary implements ISorter {

	private static final long serialVersionUID = 1L;

	@Id
	public String id;
	public String name;
	public String coinsuType;
	public double receivedSumInsured;
	public double sumInsured;

	@Override
	public String getRegistrationNo() {
		return null;
	}

}
