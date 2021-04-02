package org.ace.ws.cargo.model.produtGroup;

import java.io.Serializable;

/**
 * @author Hlaing Win Tunn
 *
 */
public class ProductGroupDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String description;
	private String policyNoPrefix;
	private String proposalNoPrefix;
	private double underSession13;
	private double underSession25;
	private double maxSumInsured;
	private String accountCode;
	private String groupType;
	private int version;

	public ProductGroupDTO() {

	}

	public ProductGroupDTO(String id, String name, String description, String policyNoPrefix, String proposalNoPrefix, double underSession13, double underSession25,
			double maxSumInsured, String accountCode, String groupType, int version) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.policyNoPrefix = policyNoPrefix;
		this.proposalNoPrefix = proposalNoPrefix;
		this.underSession13 = underSession13;
		this.underSession25 = underSession25;
		this.maxSumInsured = maxSumInsured;
		this.accountCode = accountCode;
		this.groupType = groupType;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPolicyNoPrefix() {
		return policyNoPrefix;
	}

	public void setPolicyNoPrefix(String policyNoPrefix) {
		this.policyNoPrefix = policyNoPrefix;
	}

	public String getProposalNoPrefix() {
		return proposalNoPrefix;
	}

	public void setProposalNoPrefix(String proposalNoPrefix) {
		this.proposalNoPrefix = proposalNoPrefix;
	}

	public double getUnderSession13() {
		return underSession13;
	}

	public void setUnderSession13(double underSession13) {
		this.underSession13 = underSession13;
	}

	public double getUnderSession25() {
		return underSession25;
	}

	public void setUnderSession25(double underSession25) {
		this.underSession25 = underSession25;
	}

	public double getMaxSumInsured() {
		return maxSumInsured;
	}

	public void setMaxSumInsured(double maxSumInsured) {
		this.maxSumInsured = maxSumInsured;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
