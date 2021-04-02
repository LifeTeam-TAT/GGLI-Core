package org.ace.insurance.travel.expressTravel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
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

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.interfaces.IInsuredItem;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.TRAVELPROPOSAL)
@TableGenerator(name = "TRAVELPROPOSAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "TRAVELPROPOSAL_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "TravelProposal.findAll", query = "SELECT t FROM TravelProposal t "),
		@NamedQuery(name = "TravelProposal.findByDate", query = "SELECT t FROM TravelProposal t WHERE t.submittedDate BETWEEN :startDate AND :endDate") })
@Access(value = AccessType.FIELD)
public class TravelProposal implements IPolicy, Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;

	@Transient
	private String prefix;

	private String proposalNo;

	@Temporal(TemporalType.DATE)
	private Date submittedDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "travelProposal", orphanRemoval = true)
	private List<TravelExpress> expressList;

	@Temporal(TemporalType.DATE)
	private Date fromDate;

	@Temporal(TemporalType.DATE)
	private Date toDate;

	@OneToOne
	@JoinColumn(name = "SALEPOINTID")
	private SalePoint salePoint;

	@Version
	private int version;

	public TravelProposal() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TRAVELPROPOSAL_GEN")
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

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<TravelExpress> getExpressList() {
		if (expressList == null) {
			expressList = new ArrayList<TravelExpress>();
		}
		return expressList;
	}

	public void setExpressList(List<TravelExpress> expressList) {
		this.expressList = expressList;
	}

	public void addExpress(TravelExpress express) {
		express.setTravelProposal(this);
		getExpressList().add(express);
	}

	public int getTotalUnit() {
		int totalUnit = 0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalUnit += travelExpress.getNoOfUnit();
		}
		return totalUnit;
	}

	public double getTotalNetPremium() {
		double totalPremium = 0.0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalPremium += travelExpress.getNetPremium();
		}
		return totalPremium;
	}

	public double getTotalCommission() {
		double totalCommission = 0.0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalCommission += travelExpress.getCommission();
		}
		return totalCommission;
	}

	public int getTotalPassenger() {
		int totalPassenger = 0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalPassenger += travelExpress.getNoOfPassenger();
		}
		return totalPassenger;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
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
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((expressList == null) ? 0 : expressList.hashCode());
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((saleChannelType == null) ? 0 : saleChannelType.hashCode());
		result = prime * result + ((salePoint == null) ? 0 : salePoint.hashCode());
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
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
		TravelProposal other = (TravelProposal) obj;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (expressList == null) {
			if (other.expressList != null)
				return false;
		} else if (!expressList.equals(other.expressList))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (paymentType == null) {
			if (other.paymentType != null)
				return false;
		} else if (!paymentType.equals(other.paymentType))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (saleChannelType != other.saleChannelType)
			return false;
		if (salePoint == null) {
			if (other.salePoint != null)
				return false;
		} else if (!salePoint.equals(other.salePoint))
			return false;
		if (submittedDate == null) {
			if (other.submittedDate != null)
				return false;
		} else if (!submittedDate.equals(other.submittedDate))
			return false;
		if (toDate == null) {
			if (other.toDate != null)
				return false;
		} else if (!toDate.equals(other.toDate))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public String getPolicyNo() {
		// TODO Auto-generated method stub
		return proposalNo;
	}

	@Override
	public double getStandardExcess() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalDiscountAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPremium() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAddOnPremium() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalPremium() {
		// TODO Auto-generated method stub
		return getTotalNetPremium();
	}

	@Override
	public double getTotalTermPremium() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalSumInsured() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCoinsuranceApplied() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getCommenmanceDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent getAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getAgentCommission() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRenewalAgentCommission() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SaleMan getSaleMan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Organization getOrganization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Customer getCustomer() {
		// TODO Auto-ge
		return null;
	}

	@Override
	public String getCustomerName() {
		String customerName = "";
		for (TravelExpress ex : expressList) {
			if (!customerName.isEmpty()) {
				customerName = ",";
			}
			customerName += ex.getExpress().getName();
		}
		return customerName;

	}

	@Override
	public String getCustomerAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductGroup getProductGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getApprovedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IInsuredItem> getInsuredItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InsuranceType getInsuranceType() {
		// TODO Auto-generated method stub
		return null;
	}

}
