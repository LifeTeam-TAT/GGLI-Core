package org.ace.insurance.coinsurance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.java.component.FormatID;

/**
 * Entity implementation class for Entity: Coinsurance
 * 
 */
@Entity
@Table(name = TableName.COINSURANCE)
@TableGenerator(name = "COINSURANCE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "COINSURANCE_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "Coinsurance.findAll", query = "SELECT c FROM Coinsurance c"),
		@NamedQuery(name = "Coinsurance.findById", query = "SELECT c FROM Coinsurance c WHERE c.id = :id"),
		@NamedQuery(name = "Coinsurance.findByPolicyNo", query = "SELECT c FROM Coinsurance c WHERE c.policyNo = :policyNo AND c.invoiceNo IS NULL ORDER BY c.coInsuPercentage DESC"),
		@NamedQuery(name = "Coinsurance.findByCargoPolicyNo", query = "SELECT c FROM Coinsurance c WHERE c.policyNo = :policyNo AND c.invoiceNo IS NOT NULL ORDER BY c.coInsuPercentage DESC"),
		@NamedQuery(name = "Coinsurance.findByPolicyByOutPolicyNo", query = "SELECT c FROM Coinsurance c WHERE c.policyNo = :policyNo "),
		@NamedQuery(name = "Coinsurance.findINPolicyNo", query = "SELECT c FROM Coinsurance c WHERE c.invoiceDate = (SELECT MAX(cc.invoiceDate) FROM Coinsurance cc WHERE cc.policyNo = :policyNo ) ") })
@Access(value = AccessType.FIELD)
public class Coinsurance implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;
	@Transient
	private String prefix;
	private String policyNo;
	private String customerName;
	private String agentName;
	private String customerAddress;
	private double netPremium;
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	@Column(name = "SUMINSURED")
	private double sumInsuranced;
	private double receivedSumInsured;
	private double premium;
	@Enumerated(value = EnumType.STRING)
	private InsuranceType insuranceType;
	@Enumerated(value = EnumType.STRING)
	@Column(name = "COINSUTYPE")
	private CoinsuranceType coinsuranceType;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COINSURANCECOMPANYID", referencedColumnName = "ID")
	private CoinsuranceCompany coinsuranceCompany;
	private double coInsuPercentage;
	private double commissionPercent;
	private String invoiceNo;
	private String accountNo;
	private String coinsuranceNo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date invoiceDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	@Column(name = "CHEQUENO")
	private String chequeNo;
	@Enumerated(value = EnumType.STRING)
	private PaymentChannel paymentChannel;
	private String vehicleCodeNo;

	private double rate;
	private double homeNetPremium;
	private double homeSumInsured;
	private double homeReceivedSumInsured;
	private double homePremium;

	@Enumerated(EnumType.STRING)
	private ProposalType proposalType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERBANKID", referencedColumnName = "ID")
	private Bank customerbank;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANYBANKID", referencedColumnName = "ID")
	private Bank companybank;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "coinsurance", orphanRemoval = true)
	private List<CoinsuranceAttachment> coinsuranceAttachmentList;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CURRENCYID", referencedColumnName = "ID")
	private Currency currency;

	@Version
	private int version;

	public Coinsurance() {
	}

	public Coinsurance(String policyNo, int policyTerm, Date startDate, double sumInsuranced, double premium, InsuranceType insuranceType, CoinsuranceType coinsuranceType,
			CoinsuranceCompany coinsuranceCompany, double coInsuPercentage, double commissionPercent, Branch branch) {
		this.policyNo = policyNo;
		this.startDate = startDate;
		this.sumInsuranced = sumInsuranced;
		this.premium = premium;
		this.insuranceType = insuranceType;
		this.coinsuranceType = coinsuranceType;
		this.coinsuranceCompany = coinsuranceCompany;
		this.coInsuPercentage = coInsuPercentage;
		this.commissionPercent = commissionPercent;
		this.branch = branch;
	}

	public Coinsurance(String policyNo, String vehicleCodeNo, int policyTerm, Date startDate, double sumInsuranced, double premium, InsuranceType insuranceType,
			CoinsuranceType coinsuranceType, CoinsuranceCompany coinsuranceCompany, double coInsuPercentage, double commissionPercent, Branch branch, ProposalType proposalType,
			String customerAddress, String customerName, String agentName) {
		this.policyNo = policyNo;
		this.vehicleCodeNo = vehicleCodeNo;
		this.startDate = startDate;
		this.sumInsuranced = sumInsuranced;
		this.premium = premium;
		this.insuranceType = insuranceType;
		this.coinsuranceType = coinsuranceType;
		this.coinsuranceCompany = coinsuranceCompany;
		this.coInsuPercentage = coInsuPercentage;
		this.commissionPercent = commissionPercent;
		this.branch = branch;
		this.proposalType = proposalType;
		this.customerAddress = customerAddress;
		this.customerName = customerName;
		this.agentName = agentName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "COINSURANCE_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getHomeNetPremium() {
		return homeNetPremium;
	}

	public void setHomeNetPremium(double homeNetPremium) {
		this.homeNetPremium = homeNetPremium;
	}

	public double getHomeSumInsured() {
		return homeSumInsured;
	}

	public void setHomeSumInsured(double homeSumInsured) {
		this.homeSumInsured = homeSumInsured;
	}

	public double getHomeReceivedSumInsured() {
		return homeReceivedSumInsured;
	}

	public void setHomeReceivedSumInsured(double homeReceivedSumInsured) {
		this.homeReceivedSumInsured = homeReceivedSumInsured;
	}

	public double getHomePremium() {
		return homePremium;
	}

	public void setHomePremium(double homePremium) {
		this.homePremium = homePremium;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getVehicleCodeNo() {
		return vehicleCodeNo;
	}

	public void setVehicleCodeNo(String vehicleCodeNo) {
		this.vehicleCodeNo = vehicleCodeNo;
	}

	public ProposalType getProposalType() {
		return proposalType;
	}

	public void setProposalType(ProposalType proposalType) {
		this.proposalType = proposalType;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
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

	public double getSumInsuranced() {
		return sumInsuranced;
	}

	public void setSumInsuranced(double sumInsuranced) {
		this.sumInsuranced = sumInsuranced;
	}

	public double getReceivedSumInsured() {
		return receivedSumInsured;
	}

	public void setReceivedSumInsured(double receivedSumInsured) {
		this.receivedSumInsured = receivedSumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public CoinsuranceType getCoinsuranceType() {
		return coinsuranceType;
	}

	public void setCoinsuranceType(CoinsuranceType coinsuranceType) {
		this.coinsuranceType = coinsuranceType;
	}

	public CoinsuranceCompany getCoinsuranceCompany() {
		return coinsuranceCompany;
	}

	public void setCoinsuranceCompany(CoinsuranceCompany coinsuranceCompany) {
		this.coinsuranceCompany = coinsuranceCompany;
	}

	public double getCoInsuPercentage() {
		return coInsuPercentage;
	}

	public void setCoInsuPercentage(double coInsuPercentage) {
		this.coInsuPercentage = coInsuPercentage;
	}

	public double getCommissionPercent() {
		return commissionPercent;
	}

	public void setCommissionPercent(double commissionPercent) {
		this.commissionPercent = commissionPercent;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public PaymentChannel getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(PaymentChannel paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Bank getCustomerbank() {
		return customerbank;
	}

	public void setCustomerbank(Bank customerbank) {
		this.customerbank = customerbank;
	}

	public Bank getCompanybank() {
		return companybank;
	}

	public void setCompanybank(Bank companybank) {
		this.companybank = companybank;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public List<CoinsuranceAttachment> getCoinsuranceAttachmentList() {
		if (coinsuranceAttachmentList == null) {
			coinsuranceAttachmentList = new ArrayList<CoinsuranceAttachment>();
		}
		return coinsuranceAttachmentList;
	}

	public void setCoinsuranceAttachmentList(List<CoinsuranceAttachment> coinsuranceAttachmentList) {
		this.coinsuranceAttachmentList = coinsuranceAttachmentList;
	}

	public void addCoinsuranceAttachment(CoinsuranceAttachment attachment) {
		if (coinsuranceAttachmentList == null) {
			coinsuranceAttachmentList = new ArrayList<CoinsuranceAttachment>();
		}
		attachment.setCoinsurance(this);
		coinsuranceAttachmentList.add(attachment);
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getCoinsuranceNo() {
		return coinsuranceNo;
	}

	public void setCoinsuranceNo(String coinsuranceNo) {
		this.coinsuranceNo = coinsuranceNo;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double getNetPremium() {
		return netPremium;
	}

	public void setNetPremium(double netPremium) {
		this.netPremium = netPremium;
	}

	public double getCommissionAmount() {
		return Utils.getPercentOf(commissionPercent, premium);
	}

	public String getCommissionPercentString() {
		return "(-" + commissionPercent + "%)";
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountNo == null) ? 0 : accountNo.hashCode());
		result = prime * result + ((agentName == null) ? 0 : agentName.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((chequeNo == null) ? 0 : chequeNo.hashCode());
		long temp;
		temp = Double.doubleToLongBits(coInsuPercentage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((coinsuranceAttachmentList == null) ? 0 : coinsuranceAttachmentList.hashCode());
		result = prime * result + ((coinsuranceCompany == null) ? 0 : coinsuranceCompany.hashCode());
		result = prime * result + ((coinsuranceNo == null) ? 0 : coinsuranceNo.hashCode());
		result = prime * result + ((coinsuranceType == null) ? 0 : coinsuranceType.hashCode());
		temp = Double.doubleToLongBits(commissionPercent);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((companybank == null) ? 0 : companybank.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((customerAddress == null) ? 0 : customerAddress.hashCode());
		result = prime * result + ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result + ((customerbank == null) ? 0 : customerbank.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		temp = Double.doubleToLongBits(homeNetPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(homePremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(homeReceivedSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(homeSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((insuranceType == null) ? 0 : insuranceType.hashCode());
		result = prime * result + ((invoiceDate == null) ? 0 : invoiceDate.hashCode());
		result = prime * result + ((invoiceNo == null) ? 0 : invoiceNo.hashCode());
		temp = Double.doubleToLongBits(netPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((paymentChannel == null) ? 0 : paymentChannel.hashCode());
		result = prime * result + ((paymentDate == null) ? 0 : paymentDate.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((proposalType == null) ? 0 : proposalType.hashCode());
		temp = Double.doubleToLongBits(rate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(receivedSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		temp = Double.doubleToLongBits(sumInsuranced);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((vehicleCodeNo == null) ? 0 : vehicleCodeNo.hashCode());
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
		Coinsurance other = (Coinsurance) obj;
		if (accountNo == null) {
			if (other.accountNo != null)
				return false;
		} else if (!accountNo.equals(other.accountNo))
			return false;
		if (agentName == null) {
			if (other.agentName != null)
				return false;
		} else if (!agentName.equals(other.agentName))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (chequeNo == null) {
			if (other.chequeNo != null)
				return false;
		} else if (!chequeNo.equals(other.chequeNo))
			return false;
		if (Double.doubleToLongBits(coInsuPercentage) != Double.doubleToLongBits(other.coInsuPercentage))
			return false;
		if (coinsuranceAttachmentList == null) {
			if (other.coinsuranceAttachmentList != null)
				return false;
		} else if (!coinsuranceAttachmentList.equals(other.coinsuranceAttachmentList))
			return false;
		if (coinsuranceCompany == null) {
			if (other.coinsuranceCompany != null)
				return false;
		} else if (!coinsuranceCompany.equals(other.coinsuranceCompany))
			return false;
		if (coinsuranceNo == null) {
			if (other.coinsuranceNo != null)
				return false;
		} else if (!coinsuranceNo.equals(other.coinsuranceNo))
			return false;
		if (coinsuranceType != other.coinsuranceType)
			return false;
		if (Double.doubleToLongBits(commissionPercent) != Double.doubleToLongBits(other.commissionPercent))
			return false;
		if (companybank == null) {
			if (other.companybank != null)
				return false;
		} else if (!companybank.equals(other.companybank))
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (customerAddress == null) {
			if (other.customerAddress != null)
				return false;
		} else if (!customerAddress.equals(other.customerAddress))
			return false;
		if (customerName == null) {
			if (other.customerName != null)
				return false;
		} else if (!customerName.equals(other.customerName))
			return false;
		if (customerbank == null) {
			if (other.customerbank != null)
				return false;
		} else if (!customerbank.equals(other.customerbank))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (Double.doubleToLongBits(homeNetPremium) != Double.doubleToLongBits(other.homeNetPremium))
			return false;
		if (Double.doubleToLongBits(homePremium) != Double.doubleToLongBits(other.homePremium))
			return false;
		if (Double.doubleToLongBits(homeReceivedSumInsured) != Double.doubleToLongBits(other.homeReceivedSumInsured))
			return false;
		if (Double.doubleToLongBits(homeSumInsured) != Double.doubleToLongBits(other.homeSumInsured))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (insuranceType != other.insuranceType)
			return false;
		if (invoiceDate == null) {
			if (other.invoiceDate != null)
				return false;
		} else if (!invoiceDate.equals(other.invoiceDate))
			return false;
		if (invoiceNo == null) {
			if (other.invoiceNo != null)
				return false;
		} else if (!invoiceNo.equals(other.invoiceNo))
			return false;
		if (Double.doubleToLongBits(netPremium) != Double.doubleToLongBits(other.netPremium))
			return false;
		if (paymentChannel != other.paymentChannel)
			return false;
		if (paymentDate == null) {
			if (other.paymentDate != null)
				return false;
		} else if (!paymentDate.equals(other.paymentDate))
			return false;
		if (paymentType == null) {
			if (other.paymentType != null)
				return false;
		} else if (!paymentType.equals(other.paymentType))
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (proposalType != other.proposalType)
			return false;
		if (Double.doubleToLongBits(rate) != Double.doubleToLongBits(other.rate))
			return false;
		if (Double.doubleToLongBits(receivedSumInsured) != Double.doubleToLongBits(other.receivedSumInsured))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (Double.doubleToLongBits(sumInsuranced) != Double.doubleToLongBits(other.sumInsuranced))
			return false;
		if (vehicleCodeNo == null) {
			if (other.vehicleCodeNo != null)
				return false;
		} else if (!vehicleCodeNo.equals(other.vehicleCodeNo))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
