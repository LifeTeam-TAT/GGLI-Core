package org.ace.insurance.life.claim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.LIFECLAIMPROPOSAL)
@TableGenerator(name = "LIFECLAIMPROPOSAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFECLAIMPROPOSAL_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class LifeClaimProposal implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFECLAIMPROPOSAL_GEN")
	private String id;

	private String notificationNo;
	private String claimProposalNo;
	private double totalClaimAmont;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFEPOLICYID", referencedColumnName = "ID")
	private LifePolicy lifePolicy;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLAIMPERSONID", referencedColumnName = "ID")
	private PolicyInsuredPerson claimPerson;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALEPOINTID", referencedColumnName = "ID")
	private SalePoint salePoint;

	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date occuranceDate;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "LIFECLAIMPROPOSALID", referencedColumnName = "ID")
	private List<LifePolicyClaim> claimList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "LIFECLAIMPROPOSALID", referencedColumnName = "ID")
	private List<LifeClaimBeneficiaryPerson> beneficiaryList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "LIFECLAIMPROPOSALID", referencedColumnName = "ID")
	private List<LifeClaimProposalAttachment> attachmentList;

	@Version
	private int version;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	public LifeClaimProposal() {

	}

	public String getId() {
		return id;
	}

	public String getNotificationNo() {
		return notificationNo;
	}

	public void setNotificationNo(String notificationNo) {
		this.notificationNo = notificationNo;
	}

	public String getClaimProposalNo() {
		return claimProposalNo;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public PolicyInsuredPerson getClaimPerson() {
		return claimPerson;
	}

	public Branch getBranch() {
		return branch;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public Date getOccuranceDate() {
		return occuranceDate;
	}

	public List<LifePolicyClaim> getClaimList() {
		return claimList;
	}

	public List<LifeClaimBeneficiaryPerson> getBeneficiaryList() {
		return beneficiaryList;
	}

	public List<LifeClaimProposalAttachment> getAttachmentList() {
		if (this.attachmentList == null) {
			this.attachmentList = new ArrayList<LifeClaimProposalAttachment>();
		}
		return attachmentList;
	}

	public int getVersion() {
		return version;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setClaimProposalNo(String claimProposalNo) {
		this.claimProposalNo = claimProposalNo;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public void setClaimPerson(PolicyInsuredPerson claimPerson) {
		this.claimPerson = claimPerson;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public void setOccuranceDate(Date occuranceDate) {
		this.occuranceDate = occuranceDate;
	}

	public void setClaimList(List<LifePolicyClaim> claimList) {
		this.claimList = claimList;
	}

	public void addClaim(LifePolicyClaim claim) {
		if (claimList == null) {
			claimList = new ArrayList<LifePolicyClaim>();
		}
		claimList.add(claim);
	}

	public void setBeneficiaryList(List<LifeClaimBeneficiaryPerson> beneficiaryList) {
		this.beneficiaryList = beneficiaryList;
	}

	public void addBeneficiary(LifeClaimBeneficiaryPerson beneficiary) {
		if (beneficiaryList == null) {
			beneficiaryList = new ArrayList<LifeClaimBeneficiaryPerson>();
		}
		beneficiaryList.add(beneficiary);
	}

	public void setAttachmentList(List<LifeClaimProposalAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public void addAttachment(LifeClaimProposalAttachment attachment) {
		if (attachmentList == null) {
			attachmentList = new ArrayList<LifeClaimProposalAttachment>();
		}
		attachmentList.add(attachment);
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public double getTotalClaimAmont() {
		return totalClaimAmont;
	}

	public void setTotalClaimAmont(double totalClaimAmont) {
		this.totalClaimAmont = totalClaimAmont;
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
		result = prime * result + ((claimPerson == null) ? 0 : claimPerson.hashCode());
		result = prime * result + ((claimProposalNo == null) ? 0 : claimProposalNo.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lifePolicy == null) ? 0 : lifePolicy.hashCode());
		result = prime * result + ((notificationNo == null) ? 0 : notificationNo.hashCode());
		result = prime * result + ((occuranceDate == null) ? 0 : occuranceDate.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((salePoint == null) ? 0 : salePoint.hashCode());
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
		long temp;
		temp = Double.doubleToLongBits(totalClaimAmont);
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
		LifeClaimProposal other = (LifeClaimProposal) obj;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (claimPerson == null) {
			if (other.claimPerson != null)
				return false;
		} else if (!claimPerson.equals(other.claimPerson))
			return false;
		if (claimProposalNo == null) {
			if (other.claimProposalNo != null)
				return false;
		} else if (!claimProposalNo.equals(other.claimProposalNo))
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
		if (lifePolicy == null) {
			if (other.lifePolicy != null)
				return false;
		} else if (!lifePolicy.equals(other.lifePolicy))
			return false;
		if (notificationNo == null) {
			if (other.notificationNo != null)
				return false;
		} else if (!notificationNo.equals(other.notificationNo))
			return false;
		if (occuranceDate == null) {
			if (other.occuranceDate != null)
				return false;
		} else if (!occuranceDate.equals(other.occuranceDate))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
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
		if (Double.doubleToLongBits(totalClaimAmont) != Double.doubleToLongBits(other.totalClaimAmont))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	public double getHospitalClaimAmount() {
		LifeHospitalizedClaim h = null;
		for (LifePolicyClaim claim : claimList) {
			if (claim instanceof LifeHospitalizedClaim) {
				h = (LifeHospitalizedClaim) claim;
				break;
			}
		}
		return h != null ? h.getHospitalizedAmount() : 0.00;
	}

	public double getDeathClaimAmount() {
		LifeDeathClaim h = null;
		for (LifePolicyClaim claim : claimList) {
			if (claim instanceof LifeDeathClaim) {
				h = (LifeDeathClaim) claim;
				break;
			}
		}
		return h != null ? h.getDeathClaimAmount() : 0.00;
	}

	public double getDisabilityClaimAmount() {
		DisabilityLifeClaim h = null;
		for (LifePolicyClaim claim : claimList) {
			if (claim instanceof DisabilityLifeClaim) {
				h = (DisabilityLifeClaim) claim;
				break;
			}
		}
		return h != null ? h.getDisabilityAmount() : 0.00;
	}
}
