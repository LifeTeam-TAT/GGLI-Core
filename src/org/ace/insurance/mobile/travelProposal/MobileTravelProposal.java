package org.ace.insurance.mobile.travelProposal;

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

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.mobile.enums.PaymentGateway;
import org.ace.insurance.mobile.enums.ProposalStatus;
import org.ace.insurance.mobile.transaction.PaymentResponse;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.MOBILE_TRAVEL_PROPOSAL)
@TableGenerator(name = "MOBILE_TRAVEL_PROPOSAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "MOBILE_TRAVEL_PROPOSAL_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "MobileTravelProposal.findAll", query = "SELECT m FROM MobileTravelProposal m ORDER BY m.proposalNo ASC"),
		@NamedQuery(name = "MobileTravelProposal.findById", query = "SELECT m FROM MobileTravelProposal m WHERE m.id = :id"),
		@NamedQuery(name = "MobileTravelProposal.findByMobileUser", query = "SELECT m FROM MobileTravelProposal m WHERE m.userId = :mobileUserId  ORDER BY m.submittedDate DESC") })
@Access(value = AccessType.FIELD)
public class MobileTravelProposal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;
	@Transient
	private String prefix;
	private String proposalNo;
	private String productId;
	private String policyNo;

	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	// MobileUserId
	private String userId;

	@Enumerated(EnumType.STRING)
	private ProposalStatus proposalStatus;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "MOBILETRAVELPROPOSALID", referencedColumnName = "ID")
	private List<MobileTravelInsuredPerson> insuredPersonList;

	private String transactionId;

	/* payment data */
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "TRANSACTIONID", referencedColumnName = "UNIQUETRANSACTIONCODE", insertable = false, updatable = false)
	private PaymentResponse twoC2PResponse;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	@Enumerated(EnumType.STRING)
	private PaymentGateway paymentGateway;
	private boolean isConvert;

	@Version
	private int version;

	public MobileTravelProposal() {

	}

	public MobileTravelProposal(MTP001 mtp001) {
		this.id = mtp001.getId();
		this.proposalNo = mtp001.getProposalNo();
		this.transactionId = mtp001.getTransactioId();
		this.productId = mtp001.getProductId();
		this.userId = mtp001.getUserId();
		this.submittedDate = mtp001.getSubmittedDate() != null ? Utils.getDate(mtp001.getSubmittedDate()) : null;
		this.proposalStatus = mtp001.getProposalStatus();
		List<MobileTravelInsuredPerson> insuredPersonList = new ArrayList<MobileTravelInsuredPerson>();
		for (MIP001 mip001 : mtp001.getInsuredPersonList()) {
			insuredPersonList.add(new MobileTravelInsuredPerson(mip001));
		}
		this.insuredPersonList = insuredPersonList;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MOBILE_TRAVEL_PROPOSAL_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the proposalNo
	 */
	public String getProposalNo() {
		return proposalNo;
	}

	/**
	 * @param proposalNo
	 *            the proposalNo to set
	 */
	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return the submittedDate
	 */
	public Date getSubmittedDate() {
		return submittedDate;
	}

	/**
	 * @param submittedDate
	 *            the submittedDate to set
	 */
	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	/**
	 * @return the paymentType
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}

	/**
	 * @param paymentType
	 *            the paymentType to set
	 */
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the proposalStatus
	 */
	public ProposalStatus getProposalStatus() {
		return proposalStatus;
	}

	/**
	 * @param proposalStatus
	 *            the proposalStatus to set
	 */
	public void setProposalStatus(ProposalStatus proposalStatus) {
		this.proposalStatus = proposalStatus;
	}

	/**
	 * @return the insuredPersonList
	 */
	public List<MobileTravelInsuredPerson> getInsuredPersonList() {
		return insuredPersonList;
	}

	/**
	 * @param insuredPersonList
	 *            the insuredPersonList to set
	 */
	public void setInsuredPersonList(List<MobileTravelInsuredPerson> insuredPersonList) {
		this.insuredPersonList = insuredPersonList;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	public PaymentResponse getTwoC2PResponse() {
		return twoC2PResponse;
	}

	public void setTwoC2PResponse(PaymentResponse twoC2PResponse) {
		this.twoC2PResponse = twoC2PResponse;
	}

	public boolean isConvert() {
		return isConvert;
	}

	public void setConvert(boolean isConvert) {
		this.isConvert = isConvert;
	}

	/*** Generated Getter ***/
	public double getTotalPremium() {
		double premium = 0.0;
		for (MobileTravelInsuredPerson insuredPerson : getInsuredPersonList()) {
			premium += insuredPerson.getPremium();
		}
		return premium;
	}

	public int getTotalUnit() {
		int totalUnit = 0;
		for (MobileTravelInsuredPerson insuredPerson : getInsuredPersonList()) {
			totalUnit += insuredPerson.getUnit();
		}
		return totalUnit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((insuredPersonList == null) ? 0 : insuredPersonList.hashCode());
		result = prime * result + (isConvert ? 1231 : 1237);
		result = prime * result + ((paymentDate == null) ? 0 : paymentDate.hashCode());
		result = prime * result + ((paymentGateway == null) ? 0 : paymentGateway.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((proposalStatus == null) ? 0 : proposalStatus.hashCode());
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		result = prime * result + ((twoC2PResponse == null) ? 0 : twoC2PResponse.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		MobileTravelProposal other = (MobileTravelProposal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (insuredPersonList == null) {
			if (other.insuredPersonList != null)
				return false;
		} else if (!insuredPersonList.equals(other.insuredPersonList))
			return false;
		if (isConvert != other.isConvert)
			return false;
		if (paymentDate == null) {
			if (other.paymentDate != null)
				return false;
		} else if (!paymentDate.equals(other.paymentDate))
			return false;
		if (paymentGateway != other.paymentGateway)
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
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (proposalStatus != other.proposalStatus)
			return false;
		if (submittedDate == null) {
			if (other.submittedDate != null)
				return false;
		} else if (!submittedDate.equals(other.submittedDate))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		if (twoC2PResponse == null) {
			if (other.twoC2PResponse != null)
				return false;
		} else if (!twoC2PResponse.equals(other.twoC2PResponse))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}