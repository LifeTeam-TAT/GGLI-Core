package org.ace.insurance.web.manage.medical.proposal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedProperty;

import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.eips.service.interfaces.IEipsService;
import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.percentage.Percentage;
import org.ace.insurance.system.common.percentage.service.interfaces.IPercentageService;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.service.interfaces.IRelationShipTypeService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
import org.ace.insurance.web.common.CommonDTO;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyDTO;

public class MedProDTO extends CommonDTO {
	private String id;
	private boolean existsEntity;
	private String proposalNo;
	private ProposalType proposalType;
	private Date submittedDate;
	private Branch branch;
	private CustomerDTO customer;
	private CustomerDTO referral;
	private PaymentType paymentType;
	private Agent agent;
	private SaleMan saleMan;
	private List<MedProAttDTO> attachmentList;
	private List<MedProInsuDTO> medProInsuDTOList;
	private int version;
	private MedicalPolicyDTO medicalPolicyDTO;
	private CustomerType customerType;
	private boolean isCorporate;
	private SaleChannelType saleChannelType;
	private OrganizationDTO organization;
	private SalePoint salePoint;
	private String groupMicroHealthID;

	private BancaassuranceProposal bancaassurance;

	private List<GGIOrganization> ggiOrganizationList;

	private List<Staff> staffList;

	private List<RelationShipType> relationShipTypeList;

	// private Boolean selectItem;

	private GGIOrganization ggiOrganization;

	private Staff staff;

	private RelationShipType relationShipType;

	private Percentage percentage;

	private Eips eips;

	private boolean isStaffPlan;

	private Channel channel;

	@ManagedProperty(value = "#{GGIOrganizationService}")
	private IGGIOrganizationService ggiOrganizationService;

	@ManagedProperty(value = "#{StaffService}")
	private IStaffService staffService;

	@ManagedProperty(value = "#{RelationShipTypeService}")
	private IRelationShipTypeService relationShipTypeService;

	@ManagedProperty(value = "#{PercentageService}")
	private IPercentageService percentageService;

	@ManagedProperty(value = "#{EipsService}")
	private IEipsService eipsService;

	public MedProDTO() {
	}

	public String getCustomerName() {
		if (customer != null) {
			return customer.getFullName();
		}
		if (organization != null) {
			return organization.getName();
		}
		return null;
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

	public MedProDTO(String id, boolean existsEntity, boolean complete, String proposalNo, ProposalType proposalType, Date submittedDate, Branch branch, CustomerDTO customer,
			CustomerDTO referral, OrganizationDTO organization, PaymentType paymentType, Agent agent, SaleMan saleMan, List<MedProAttDTO> attachmentList,
			List<MedProInsuDTO> medProInsuDTOList, int version, int maxUnit, CustomerType customerType, SalePoint salePoint, SaleChannelType saleChannelType) {
		this.id = id;
		this.existsEntity = existsEntity;
		this.proposalNo = proposalNo;
		this.proposalType = proposalType;
		this.submittedDate = submittedDate;
		this.branch = branch;
		this.customer = customer;
		this.referral = referral;
		this.paymentType = paymentType;
		this.agent = agent;
		this.saleMan = saleMan;
		this.attachmentList = attachmentList;
		this.medProInsuDTOList = medProInsuDTOList;
		this.version = version;
		this.customerType = customerType;
		this.organization = organization;
		this.salePoint = salePoint;
		this.saleChannelType = saleChannelType;
	}

	public MedProDTO(List<MedProInsuDTO> medProInsuDTO, CustomerDTO customer) {
		this.medProInsuDTOList = medProInsuDTO;
		this.customer = customer;
	}

	public List<MedProInsuDTO> getMedProInsuDTOList() {
		if (medProInsuDTOList == null) {
			medProInsuDTOList = new ArrayList<MedProInsuDTO>();
		}
		return medProInsuDTOList;
	}

	public MedicalPolicyDTO getMedicalPolicyDTO() {
		return medicalPolicyDTO;
	}

	public void setMedicalPolicyDTO(MedicalPolicyDTO medicalPolicyDTO) {
		this.medicalPolicyDTO = medicalPolicyDTO;
	}

	public void setMedProInsuDTOList(List<MedProInsuDTO> medProInsuDTOList) {
		this.medProInsuDTOList = medProInsuDTOList;
	}

	public boolean isExistsEntity() {
		return existsEntity;
	}

	public void setExistsEntity(boolean existsEntity) {
		this.existsEntity = existsEntity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public ProposalType getProposalType() {
		return proposalType;
	}

	public void setProposalType(ProposalType proposalType) {
		this.proposalType = proposalType;
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

	public CustomerDTO getCustomer() {
		return customer;
	}

	public CustomerDTO getReferral() {
		return referral;
	}

	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}

	public void setReferral(CustomerDTO referral) {
		this.referral = referral;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	public boolean isCorporate() {
		return isCorporate;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	public void setCorporate(boolean isCorporate) {
		this.isCorporate = isCorporate;
	}

	public List<MedProAttDTO> getAttachmentList() {
		if (attachmentList == null) {
			attachmentList = new ArrayList<MedProAttDTO>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<MedProAttDTO> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public void addAttachment(MedProAttDTO attachment) {
		if (!getAttachmentList().contains(attachment)) {
			getAttachmentList().add(attachment);
		}
	}

	public void addInsuredPerson(MedProInsuDTO dto) {
		dto.setMedProDTO(this);
		if (!getMedProInsuDTOList().contains(dto)) {
			getMedProInsuDTOList().add(dto);
		}
	}

	public double getTotalPremium() {
		return getTotalInsuProposedPremium() + getBasicPlusPremium() + getTotalInsuAddOnPremium();
	}

	public double getTermPremium() {
		double termPremium = 0.0;
		for (MedProInsuDTO person : medProInsuDTOList) {
			termPremium += person.getBasicTermPremium() + person.getAddOnTermPremium() + person.getBasicPlusTermPremium();
		}
		return termPremium;
	}

	public double getAddOnTermPremium() {
		double termPremium = 0.0;
		for (MedProInsuDTO person : medProInsuDTOList) {
			termPremium += person.getAddOnTermPremium();
		}
		return termPremium;
	}

	public double getBasicTermPremium() {
		double basicTermPremium = 0.0;
		for (MedProInsuDTO person : medProInsuDTOList) {
			basicTermPremium += person.getBasicTermPremium();
		}
		return basicTermPremium;
	}

	public double getTotalInsuAddOnPremium() {
		double premium = 0.0;
		for (MedProInsuDTO insuDTO : medProInsuDTOList) {
			premium += insuDTO.getAddOnPremium();
		}
		return premium;
	}

	public double getTotalInsuProposedPremium() {
		double premium = 0.0;
		for (MedProInsuDTO insuDTO : medProInsuDTOList) {
			premium += insuDTO.getProposedPremium();
		}
		return premium;
	}

	public double getBasicPlusPremium() {
		double premium = 0.0;
		for (MedProInsuDTO insuDTO : medProInsuDTOList) {
			premium += insuDTO.getBasicPlusPremium();
		}
		return premium;
	}

	public double getTotalUnit() {
		return getTotalInsuProposedUnit() + getTotalInsuAddOnUnit();
	}

	public double getTotalInsuAddOnUnit() {
		double premium = 0.0;
		for (MedProInsuDTO insuDTO : medProInsuDTOList) {
			premium += insuDTO.getTotalAddOnUnit();
		}
		return premium;
	}

	public double getTotalInsuProposedUnit() {
		double unit = 0;
		for (MedProInsuDTO insuDTO : medProInsuDTOList) {
			unit += insuDTO.getUnit();
		}
		return unit;
	}

	public double getTotalInsuBasicPlusUnit() {
		double premium = 0.0;
		for (MedProInsuDTO insuDTO : medProInsuDTOList) {
			premium += insuDTO.getBasicPlusUnit();
		}
		return premium;
	}

	public double getNcbPremium() {
		double premium = 0.0;
		for (MedProInsuDTO insuDTO : medProInsuDTOList) {
			premium += insuDTO.getTotalNcbPremium();
		}
		return premium;
	}

	public double getTotalPremiumwithDiscount() {
		return getTotalPremium() - getNcbPremium();
	}

	public String getAttachmentRootPath() {
		return id;
	}

	public void addInsurancePerson(MedProInsuDTO insurancePerson) {
		if (!getMedProInsuDTOList().contains(insurancePerson)) {
			getMedProInsuDTOList().add(insurancePerson);
		}
	}

	public OrganizationDTO getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationDTO organization) {
		this.organization = organization;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public String getGroupMicroHealthID() {
		return groupMicroHealthID;
	}

	public void setGroupMicroHealthID(String groupMicroHealthID) {
		this.groupMicroHealthID = groupMicroHealthID;
	}

	public List<GGIOrganization> getGgiOrganizationList() {
		return ggiOrganizationList;
	}

	public void setGgiOrganizationList(List<GGIOrganization> ggiOrganizationList) {
		this.ggiOrganizationList = ggiOrganizationList;
	}

	public List<Staff> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<Staff> staffList) {
		this.staffList = staffList;
	}

	public List<RelationShipType> getRelationShipTypeList() {
		return relationShipTypeList;
	}

	public void setRelationShipTypeList(List<RelationShipType> relationShipTypeList) {
		this.relationShipTypeList = relationShipTypeList;
	}

	/*
	 * public Boolean getSelectItem() { return selectItem; }
	 * 
	 * public void setSelectItem(Boolean selectItem) { this.selectItem =
	 * selectItem; }
	 */
	public GGIOrganization getGgiOrganization() {
		return ggiOrganization;
	}

	public void setGgiOrganization(GGIOrganization ggiOrganization) {
		this.ggiOrganization = ggiOrganization;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public RelationShipType getRelationShipType() {
		return relationShipType;
	}

	public void setRelationShipType(RelationShipType relationShipType) {
		this.relationShipType = relationShipType;
	}

	public Percentage getPercentage() {
		return percentage;
	}

	public void setPercentage(Percentage percentage) {
		this.percentage = percentage;
	}

	public Eips getEips() {
		return eips;
	}

	public void setEips(Eips eips) {
		this.eips = eips;
	}

	public IGGIOrganizationService getGgiOrganizationService() {
		return ggiOrganizationService;
	}

	public void setGgiOrganizationService(IGGIOrganizationService ggiOrganizationService) {
		this.ggiOrganizationService = ggiOrganizationService;
	}

	public IStaffService getStaffService() {
		return staffService;
	}

	public void setStaffService(IStaffService staffService) {
		this.staffService = staffService;
	}

	public IRelationShipTypeService getRelationShipTypeService() {
		return relationShipTypeService;
	}

	public void setRelationShipTypeService(IRelationShipTypeService relationShipTypeService) {
		this.relationShipTypeService = relationShipTypeService;
	}

	public IPercentageService getPercentageService() {
		return percentageService;
	}

	public void setPercentageService(IPercentageService percentageService) {
		this.percentageService = percentageService;
	}

	public IEipsService getEipsService() {
		return eipsService;
	}

	public void setEipsService(IEipsService eipsService) {
		this.eipsService = eipsService;
	}

	public boolean isStaffPlan() {
		return isStaffPlan;
	}

	public void setStaffPlan(boolean isStaffPlan) {
		this.isStaffPlan = isStaffPlan;
	}

	public BancaassuranceProposal getBancaassurance() {
		return bancaassurance;
	}

	public void setBancaassurance(BancaassuranceProposal bancaassurance) {
		this.bancaassurance = bancaassurance;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((attachmentList == null) ? 0 : attachmentList.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((customerType == null) ? 0 : customerType.hashCode());
		result = prime * result + (existsEntity ? 1231 : 1237);
		result = prime * result + ((groupMicroHealthID == null) ? 0 : groupMicroHealthID.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isCorporate ? 1231 : 1237);
		result = prime * result + ((medProInsuDTOList == null) ? 0 : medProInsuDTOList.hashCode());
		result = prime * result + ((medicalPolicyDTO == null) ? 0 : medicalPolicyDTO.hashCode());
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((proposalType == null) ? 0 : proposalType.hashCode());
		result = prime * result + ((referral == null) ? 0 : referral.hashCode());
		result = prime * result + ((saleChannelType == null) ? 0 : saleChannelType.hashCode());
		result = prime * result + ((saleMan == null) ? 0 : saleMan.hashCode());
		result = prime * result + ((salePoint == null) ? 0 : salePoint.hashCode());
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MedProDTO other = (MedProDTO) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		if (attachmentList == null) {
			if (other.attachmentList != null)
				return false;
		} else if (!attachmentList.equals(other.attachmentList))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (customerType != other.customerType)
			return false;
		if (existsEntity != other.existsEntity)
			return false;
		if (groupMicroHealthID == null) {
			if (other.groupMicroHealthID != null)
				return false;
		} else if (!groupMicroHealthID.equals(other.groupMicroHealthID))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isCorporate != other.isCorporate)
			return false;
		if (medProInsuDTOList == null) {
			if (other.medProInsuDTOList != null)
				return false;
		} else if (!medProInsuDTOList.equals(other.medProInsuDTOList))
			return false;
		if (medicalPolicyDTO == null) {
			if (other.medicalPolicyDTO != null)
				return false;
		} else if (!medicalPolicyDTO.equals(other.medicalPolicyDTO))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (paymentType == null) {
			if (other.paymentType != null)
				return false;
		} else if (!paymentType.equals(other.paymentType))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (proposalType != other.proposalType)
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
		if (submittedDate == null) {
			if (other.submittedDate != null)
				return false;
		} else if (!submittedDate.equals(other.submittedDate))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
