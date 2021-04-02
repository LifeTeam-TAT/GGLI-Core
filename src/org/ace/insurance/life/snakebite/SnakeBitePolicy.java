package org.ace.insurance.life.snakebite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.SNAKEBITEPOLICY)
@TableGenerator(name = "SNAKEBITEPOLICY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "SNAKEBITEPOLICY_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "SnakeBitePolicy.findAll", query = "SELECT m FROM SnakeBitePolicy m "),
		@NamedQuery(name = "SnakeBitePolicy.findBySnakeBitePolicyNo", query = "SELECT s FROM SnakeBitePolicy s WHERE s.snakeBitePolicyNo = :snakeBitePolicyNo"),
		@NamedQuery(name = "SnakeBitePolicy.updateCompleteStatus", query = "UPDATE SnakeBitePolicy m SET m.complete = :complete WHERE m.id = :id") })
@Access(value = AccessType.FIELD)
public class SnakeBitePolicy implements IDataModel, Serializable, ISorter {
	@Transient
	private String id;
	@Transient
	private String prefix;
	private boolean complete;
	private double premium;
	private double sumInsured;
	@Column(name = "PERIODOFYEAR")
	private int periodOfYear;

	@Temporal(TemporalType.TIMESTAMP)
	private Date policyEndDate;

	private String bookNo;

	@Temporal(TemporalType.TIMESTAMP)
	private Date policyStartDate;

	private String snakeBitePolicyNo;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALEMANID", referencedColumnName = "ID")
	private SaleMan saleMan;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
	private Customer customer;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFERRALID", referencedColumnName = "ID")
	private Customer referral;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "snakeBitePolicy", orphanRemoval = true)
	private List<SnakeBiteBeneficiary> snakeBiteBeneficiaryList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "snakeBitePolicy", orphanRemoval = true)
	private List<SnakeBitePolicyKeyFactorValue> snakeBitePolicyKeyFactorValueList;

	@OneToOne
	@JoinColumn(name = "SALEPOINTID")
	private SalePoint salePoint;

	@Version
	private int version;

	private String referenceNo;

	public SnakeBitePolicy() {

	}

	public SnakeBitePolicy(Date policyStartDate, String snakeBitePolicyNo, SaleMan saleMan, boolean complete, Agent agent, Branch branch, Customer customer, double premium,
			double sumInsured, Date policyEndDate, String bookNo, PaymentType paymentType, Customer referral, Product product, int periodOfYear) {
		super();
		this.policyStartDate = policyStartDate;
		this.snakeBitePolicyNo = snakeBitePolicyNo;
		this.saleMan = saleMan;
		this.complete = complete;
		this.agent = agent;
		this.branch = branch;
		this.customer = customer;
		this.premium = premium;
		this.sumInsured = sumInsured;
		this.policyEndDate = policyEndDate;
		this.bookNo = bookNo;
		this.paymentType = paymentType;
		this.referral = referral;
		this.product = product;
		this.periodOfYear = periodOfYear;
	}

	public SnakeBitePolicy(SnakeBitePolicy policy) {
		super();
		this.policyStartDate = new Date();
		this.snakeBitePolicyNo = policy.getSnakeBitePolicyNo();
		this.saleMan = policy.getSaleMan();
		this.complete = false;
		this.agent = policy.getAgent();
		this.branch = policy.getBranch();
		this.customer = policy.getCustomer();
		this.premium = 0.0;
		this.sumInsured = policy.getSumInsured();
		this.policyEndDate = null;
		this.bookNo = policy.getBookNo();
		this.paymentType = policy.getPaymentType();
		this.referral = policy.getReferral();
		this.product = policy.getProduct();
		this.periodOfYear = policy.getPeriodOfYear();
		this.snakeBiteBeneficiaryList = new ArrayList<SnakeBiteBeneficiary>();
		for (SnakeBiteBeneficiary snakeBiteBeneficiary : policy.getSnakeBiteBeneficiaryList()) {
			snakeBiteBeneficiaryList.add(snakeBiteBeneficiary);
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "SNAKEBITEPOLICY_GEN")
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

	public Date getPolicyStartDate() {
		return policyStartDate;
	}

	public void setPolicyStartDate(Date policyStartDate) {
		this.policyStartDate = policyStartDate;
	}

	public String getSnakeBitePolicyNo() {
		return snakeBitePolicyNo;
	}

	public void setSnakeBitePolicyNo(String snakeBitePolicyNo) {
		this.snakeBitePolicyNo = snakeBitePolicyNo;
	}

	public boolean getComplete() {
		return this.complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
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

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public Date getPolicyEndDate() {
		return policyEndDate;
	}

	public void setPolicyEndDate(Date policyEndDate) {
		this.policyEndDate = policyEndDate;
	}

	public String getBookNo() {
		return bookNo;
	}

	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<SnakeBiteBeneficiary> getSnakeBiteBeneficiaryList() {
		if (snakeBiteBeneficiaryList == null) {
			snakeBiteBeneficiaryList = new ArrayList<SnakeBiteBeneficiary>();
		}
		return snakeBiteBeneficiaryList;
	}

	public void setSnakeBiteBeneficiaryList(List<SnakeBiteBeneficiary> snakeBiteBeneficiaryList) {
		this.snakeBiteBeneficiaryList = snakeBiteBeneficiaryList;
	}

	public void addSnakeBiteBeneficiary(SnakeBiteBeneficiary snakeBiteBeneficiary) {
		if (snakeBiteBeneficiaryList == null) {
			snakeBiteBeneficiaryList = new ArrayList<SnakeBiteBeneficiary>();
		}
		snakeBiteBeneficiary.setSnakeBitePolicy(this);
		snakeBiteBeneficiaryList.add(snakeBiteBeneficiary);
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

	public String getCustomerName() {
		if (customer != null) {
			return customer.getFullName();
		}
		return null;
	}

	public List<SnakeBitePolicyKeyFactorValue> getSnakeBitePolicyKeyFactorValueList() {
		if (snakeBitePolicyKeyFactorValueList == null) {
			snakeBitePolicyKeyFactorValueList = new ArrayList<SnakeBitePolicyKeyFactorValue>();
		}
		return snakeBitePolicyKeyFactorValueList;
	}

	public void setSnakeBitePolicyKeyFactorValueList(List<SnakeBitePolicyKeyFactorValue> snakeBitePolicyKeyFactorValueList) {
		this.snakeBitePolicyKeyFactorValueList = snakeBitePolicyKeyFactorValueList;
	}

	public void addSnakeBitePolicyKeyFactorValueList(SnakeBitePolicyKeyFactorValue snakeBitePolicyKeyFactorValue) {
		if (snakeBitePolicyKeyFactorValueList == null) {
			snakeBitePolicyKeyFactorValueList = new ArrayList<SnakeBitePolicyKeyFactorValue>();
		}
		snakeBitePolicyKeyFactorValue.setSnakeBitePolicy(this);
		snakeBitePolicyKeyFactorValueList.add(snakeBitePolicyKeyFactorValue);
	}

	public int getPeriodOfYear() {
		return periodOfYear;
	}

	public void setPeriodOfYear(int periodOfYear) {
		this.periodOfYear = periodOfYear;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public double getPremiumOfExcludeAgentCommission() {
		double agentCommission = 0.0;
		if (agent != null) {
			agentCommission = Utils.getPercentOf(product.getFirstCommission(), premium);
		}
		return premium - agentCommission;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bookNo == null) ? 0 : bookNo.hashCode());
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + periodOfYear;
		result = prime * result + ((policyEndDate == null) ? 0 : policyEndDate.hashCode());
		result = prime * result + ((policyStartDate == null) ? 0 : policyStartDate.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		long temp;
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((snakeBitePolicyNo == null) ? 0 : snakeBitePolicyNo.hashCode());
		temp = Double.doubleToLongBits(sumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		SnakeBitePolicy other = (SnakeBitePolicy) obj;
		if (bookNo == null) {
			if (other.bookNo != null)
				return false;
		} else if (!bookNo.equals(other.bookNo))
			return false;
		if (complete != other.complete)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (periodOfYear != other.periodOfYear)
			return false;
		if (policyEndDate == null) {
			if (other.policyEndDate != null)
				return false;
		} else if (!policyEndDate.equals(other.policyEndDate))
			return false;
		if (policyStartDate == null) {
			if (other.policyStartDate != null)
				return false;
		} else if (!policyStartDate.equals(other.policyStartDate))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (referenceNo == null) {
			if (other.referenceNo != null)
				return false;
		} else if (!referenceNo.equals(other.referenceNo))
			return false;
		if (snakeBitePolicyNo == null) {
			if (other.snakeBitePolicyNo != null)
				return false;
		} else if (!snakeBitePolicyNo.equals(other.snakeBitePolicyNo))
			return false;
		if (Double.doubleToLongBits(sumInsured) != Double.doubleToLongBits(other.sumInsured))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public String getRegistrationNo() {
		return snakeBitePolicyNo;
	}

}
