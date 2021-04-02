package org.ace.insurance.medical.policy.policyEditHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.PolicyHistoryEntryType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.TableName;
import org.ace.insurance.life.policyEditHistory.LifePolicyAttachmentEditHistory;
import org.ace.insurance.life.policyEditHistory.PolicyInsuredPersonEditHistory;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyAttachment;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name =TableName.MEDPOLICY_EDITHISTORY)
@EntityListeners(IDInterceptor.class)
@TableGenerator(name = "MEDICALPOLICYEDITHISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "MEDICALPOLICYEDITHISTORY_GEN", allocationSize = 10)
public class MedicalPolicyEditHistory {
	
	
	@Transient
	private boolean nilExcess;
	private boolean delFlag;
	private int lastPaymentTerm;
	private int printCount;
	private boolean isSkipPaymentTLF;
	@Transient
	private String prefix;
	@Version
	private int version;
	private double totalDiscountAmount;
	private double standardExcess;

	private String policyNo;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MEDICALPOLICYEDITHISTORY_GEN")
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date commenmanceDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVEDPOLICYSTARTDATE")
	private Date activedPolicyStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVEDPOLICYENDDATE")
	private Date activedPolicyEndDate;

	@Enumerated(EnumType.STRING)
	private PolicyStatus policyStatus;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
	private Customer customer;

	@Enumerated(EnumType.STRING)
	private CustomerType customerType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFERRALID", referencedColumnName = "ID")
	private Customer referral;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
	private Organization organization;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALEMANID", referencedColumnName = "ID")
	private SaleMan saleMan;

	@OneToOne
	@JoinColumn(name = "PROPOSALID")
	private MedicalProposal medicalProposal;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "MEDICALPOLICYID", referencedColumnName = "ID")
	private List<MedicalPolicyInuredPersonEditHistory> policyInsuredPersonList;

	private String referencePolicyNo;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ENTRY_TYPE")
	private PolicyHistoryEntryType entryType;

	@OneToOne
	@JoinColumn(name = "SALEPOINTID")
	private SalePoint salePoint;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;
	
	public MedicalPolicyEditHistory(){
		
	}
	
	public MedicalPolicyEditHistory(MedicalPolicy medicalPolicy){
	this.customer = medicalPolicy.getCustomer();
	this.organization = medicalPolicy.getOrganization();
	this.saleMan = medicalPolicy.getSaleMan();
	this.branch = medicalPolicy.getBranch();
	this.paymentType = medicalPolicy.getPaymentType();
	this.agent = medicalPolicy.getAgent();
	this.medicalProposal = medicalPolicy.getMedicalProposal();
	this.policyNo = medicalPolicy.getPolicyNo();
	this.commenmanceDate = medicalPolicy.getCommenmanceDate();
	}
	public boolean isNilExcess() {
		return nilExcess;
	}

	public void setNilExcess(boolean nilExcess) {
		this.nilExcess = nilExcess;
	}

	public boolean isDelFlag() {
		return delFlag;
	}

	public void setDelFlag(boolean delFlag) {
		this.delFlag = delFlag;
	}

	public int getLastPaymentTerm() {
		return lastPaymentTerm;
	}

	public void setLastPaymentTerm(int lastPaymentTerm) {
		this.lastPaymentTerm = lastPaymentTerm;
	}

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public boolean isSkipPaymentTLF() {
		return isSkipPaymentTLF;
	}

	public void setSkipPaymentTLF(boolean isSkipPaymentTLF) {
		this.isSkipPaymentTLF = isSkipPaymentTLF;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double getTotalDiscountAmount() {
		return totalDiscountAmount;
	}

	public void setTotalDiscountAmount(double totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}

	public double getStandardExcess() {
		return standardExcess;
	}

	public void setStandardExcess(double standardExcess) {
		this.standardExcess = standardExcess;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCommenmanceDate() {
		return commenmanceDate;
	}

	public void setCommenmanceDate(Date commenmanceDate) {
		this.commenmanceDate = commenmanceDate;
	}

	public Date getActivedPolicyStartDate() {
		return activedPolicyStartDate;
	}

	public void setActivedPolicyStartDate(Date activedPolicyStartDate) {
		this.activedPolicyStartDate = activedPolicyStartDate;
	}

	public Date getActivedPolicyEndDate() {
		return activedPolicyEndDate;
	}

	public void setActivedPolicyEndDate(Date activedPolicyEndDate) {
		this.activedPolicyEndDate = activedPolicyEndDate;
	}

	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(PolicyStatus policyStatus) {
		this.policyStatus = policyStatus;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
	}

	public void setMedicalProposal(MedicalProposal medicalProposal) {
		this.medicalProposal = medicalProposal;
	}

	public List<MedicalPolicyInuredPersonEditHistory> getPolicyInsuredPersonList() {
		return policyInsuredPersonList;
	}

	public void setPolicyInsuredPersonList(List<MedicalPolicyInuredPersonEditHistory> policyInsuredPersonList) {
		this.policyInsuredPersonList = policyInsuredPersonList;
	}

	

	public String getReferencePolicyNo() {
		return referencePolicyNo;
	}

	public void setReferencePolicyNo(String referencePolicyNo) {
		this.referencePolicyNo = referencePolicyNo;
	}

	public PolicyHistoryEntryType getEntryType() {
		return entryType;
	}

	public void setEntryType(PolicyHistoryEntryType entryType) {
		this.entryType = entryType;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}
	
	public void addPolicyInsuredPersonInfo(MedicalPolicyInuredPersonEditHistory policyInsuredPersonInfo) {
		if (policyInsuredPersonList == null) {
			policyInsuredPersonList = new ArrayList<MedicalPolicyInuredPersonEditHistory>();
		}
		policyInsuredPersonList.add(policyInsuredPersonInfo);
	}

	

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	

}
