package org.ace.insurance.common;

public class Person implements ISorter {
	private String name;
	private String proposalNo;
	
	public Person(String name, String proposalNo) {
		this.name = name;
		this.proposalNo = proposalNo;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	@Override
	public String getRegistrationNo() {
		return proposalNo;
	}
}
