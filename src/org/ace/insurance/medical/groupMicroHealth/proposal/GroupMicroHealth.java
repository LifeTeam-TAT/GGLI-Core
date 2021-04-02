package org.ace.insurance.medical.groupMicroHealth.proposal;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.medical.groupMicroHealth.policy.GroupMicroHealthDTO;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.GROUPMICROHEALTH)
@TableGenerator(name = "GROUPMICROHEALTH_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "GROUPMICROHEALTH_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "GroupMicroHealth.findAll", query = "SELECT m FROM GroupMicroHealth m ") })
@EntityListeners(IDInterceptor.class)
public class GroupMicroHealth implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GROUPMICROHEALTH_GEN")
	private String id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	private int numberOfInsuredPerson;
	private int numberOfUnit;
	private double totalPremium;
	private String proposalNo;
	private SaleChannelType saleChannelType;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SALEMANID", referencedColumnName = "ID")
	private SaleMan saleMan;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFERRALID", referencedColumnName = "ID")
	private Customer referral;

	@OneToOne
	@JoinColumn(name = "SALEPOINTID")
	private SalePoint salePoint;

	private boolean paymentComplete;

	private boolean processComplete;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public GroupMicroHealth() {

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

	public GroupMicroHealth(GroupMicroHealthDTO dto) {
		this.id = dto.getId();
		this.proposalNo = dto.getProposalNo();
		this.startDate = dto.getStartDate();
		this.numberOfInsuredPerson = dto.getNumberOfInsuredPerson();
		this.numberOfUnit = dto.getNumberOfUnit();
		this.totalPremium = dto.getTotalPremium();
		this.agent = dto.getAgent();
		this.referral = dto.getReferral();
		this.saleMan = dto.getSaleMan();
		this.salePoint = dto.getSalePoint();
		this.paymentComplete = dto.isPaymentComplete();
		this.processComplete = dto.isPaymentComplete();
		this.version = dto.getVersion();
		this.branch = dto.getBranch();

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getNumberOfInsuredPerson() {
		return numberOfInsuredPerson;
	}

	public void setNumberOfInsuredPerson(int numberOfInsuredPerson) {
		this.numberOfInsuredPerson = numberOfInsuredPerson;
	}

	public int getNumberOfUnit() {
		return numberOfUnit;
	}

	public void setNumberOfUnit(int numberOfUnit) {
		this.numberOfUnit = numberOfUnit;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(double totalPremium) {
		this.totalPremium = totalPremium;
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

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public boolean isProcessComplete() {
		return processComplete;
	}

	public void setProcessComplete(boolean processComplete) {
		this.processComplete = processComplete;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + numberOfInsuredPerson;
		result = prime * result + numberOfUnit;
		result = prime * result + (paymentComplete ? 1231 : 1237);
		result = prime * result + (processComplete ? 1231 : 1237);
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((referral == null) ? 0 : referral.hashCode());
		result = prime * result + ((saleChannelType == null) ? 0 : saleChannelType.hashCode());
		result = prime * result + ((saleMan == null) ? 0 : saleMan.hashCode());
		result = prime * result + ((salePoint == null) ? 0 : salePoint.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		long temp;
		temp = Double.doubleToLongBits(totalPremium);
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
		GroupMicroHealth other = (GroupMicroHealth) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (numberOfInsuredPerson != other.numberOfInsuredPerson)
			return false;
		if (numberOfUnit != other.numberOfUnit)
			return false;
		if (paymentComplete != other.paymentComplete)
			return false;
		if (processComplete != other.processComplete)
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (referral == null) {
			if (other.referral != null)
				return false;
		} else if (!referral.equals(other.referral))
			return false;
		if (saleChannelType != other.saleChannelType)
			return false;
		if (saleMan == null) {
			if (other.saleMan != null)
				return false;
		} else if (!saleMan.equals(other.saleMan))
			return false;
		if (salePoint == null) {
			if (other.salePoint != null)
				return false;
		} else if (!salePoint.equals(other.salePoint))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (Double.doubleToLongBits(totalPremium) != Double.doubleToLongBits(other.totalPremium))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
