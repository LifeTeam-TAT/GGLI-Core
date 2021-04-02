package org.ace.insurance.web.manage.enquires;

import java.util.Date;
import java.util.List;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class EnquiryCriteria {
	private Date startDate;
	private Date endDate;
	private Agent agent;
	private Customer customer;
	private Product product;
	private Organization organization;
	private Bank bankCustomer;
	private SaleMan saleMan;
	private Branch branch;
	private String number;
	private boolean isEndorsement;
	private ProposalType proposalType;
	private boolean isClosed;
	private String registrationNo;
	private String claimReferenceNo;
	private Customer referral;
	private InsuranceType insuranceType;
	private List<String> productIdList;
	private SalePoint salePoint;
	private Entitys entity;

	public EnquiryCriteria() {
	}

	public EnquiryCriteria(Date startDate, Date endDate, Agent agent, Customer customer, Product product, Organization organization, Branch branch, SaleMan saleMan, String number,
			String registrationNo, Bank bankCustomer, String claimReferencNo, InsuranceType insuranceType,Entitys entity) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.agent = agent;
		this.customer = customer;
		this.product = product;
		this.organization = organization;
		this.saleMan = saleMan;
		this.branch = branch;
		this.number = number;
		this.registrationNo = registrationNo;
		this.bankCustomer = bankCustomer;
		this.claimReferenceNo = claimReferencNo;
		this.insuranceType = insuranceType;
		this.entity = entity;
	}

	public EnquiryCriteria(Date startDate, Date endDate, Agent agent, Customer customer, Product product, Organization organization, Branch branch, SaleMan saleMan, String number,
			String registrationNo, String claimReferencNo, InsuranceType insuranceType) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.agent = agent;
		this.customer = customer;
		this.product = product;
		this.organization = organization;
		this.saleMan = saleMan;
		this.branch = branch;
		this.number = number;
		this.registrationNo = registrationNo;
		this.claimReferenceNo = claimReferencNo;
		this.insuranceType = insuranceType;
	}

	public String getSalePersonName() {
		if (agent != null) {
			return agent.getFullName();
		} else if (saleMan != null) {
			return saleMan.getFullName();
		} else if (referral != null) {
			return referral.getFullName();
		}
		return null;
	}

	public String getClaimReferenceNo() {
		return claimReferenceNo;
	}

	public void setClaimReferenceNo(String claimReferenceNo) {
		this.claimReferenceNo = claimReferenceNo;
	}

	public Bank getBankCustomer() {
		return bankCustomer;
	}

	public void setBankCustomer(Bank bankCustomer) {
		this.bankCustomer = bankCustomer;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public ProposalType getProposalType() {
		return proposalType;
	}

	public void setProposalType(ProposalType proposalType) {
		this.proposalType = proposalType;
	}

	public boolean isEndorsement() {
		return isEndorsement;
	}

	public void setEndorsement(boolean isEndorsement) {
		this.isEndorsement = isEndorsement;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public List<String> getProductIdList() {
		return productIdList;
	}

	public void setProductIdList(List<String> productIdList) {
		this.productIdList = productIdList;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

}
