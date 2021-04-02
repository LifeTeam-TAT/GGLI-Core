package org.ace.insurance.medical.productprocess;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class ProductProcessCriteria {
	@Enumerated(value = EnumType.STRING)
	private StudentAgeType studentAgeType;

	@Enumerated(value = EnumType.STRING)
	private OptionType Optiontype;

	private int minAge;
	private int maxAge;
	private double sumInsured;

	public StudentAgeType getStudentAgeType() {
		return studentAgeType;
	}

	public void setStudentAgeType(StudentAgeType studentAgeType) {
		this.studentAgeType = studentAgeType;
	}

	public OptionType getOptiontype() {
		return Optiontype;
	}

	public void setOptiontype(OptionType optiontype) {
		Optiontype = optiontype;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}
}
