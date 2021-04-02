package org.ace.insurance.web.manage.medical.groupMicroHealth.proposal;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.UserType;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "GroupMicroHealthProposalActionBean")
@ViewScoped
public class GroupMicroHealthProposalActionBean extends BaseBean {

	@ManagedProperty(value = "#{GroupMicroHealthService}")
	private IGroupMicroHealthService groupMicroHealthService;

	public void setGroupMicroHealthService(IGroupMicroHealthService groupMicroHealthService) {
		this.groupMicroHealthService = groupMicroHealthService;
	}

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaMethodService;

	public void setBancaMethodService(IBancaMethodService bancaMethodService) {
		this.bancaMethodService = bancaMethodService;
	}

	private GroupMicroHealth groupMicroHealth;

	private String userType;

	private User responsiblePerson;
	private User user;
	private boolean isEditProposal;
	private Entitys entity;
	private BancaassuranceProposal bancaassuranceProposal;
	private List<BancaMethod> bancaMethodList;
	private BancaMethod bancaMethod;
	private boolean isChannel;

	@PostConstruct
	public void init() {
		groupMicroHealth = new GroupMicroHealth();
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		bancaMethod = new BancaMethod();
		bancaassuranceProposal = (BancaassuranceProposal) getParam("bancaassuranceProposal");
		bancaMethodList = bancaMethodService.findAllBanca();
		isChannel = false;
		if (isExistParam("groupMicroHealth")) {
			groupMicroHealth = (GroupMicroHealth) getParam("groupMicroHealth");
			userType = groupMicroHealth.getAgent() != null ? UserType.AGENT.toString()
					: groupMicroHealth.getReferral() != null ? UserType.REFERRAL.toString() : UserType.SALEMAN.toString();
			isEditProposal = true;
		}
		groupMicroHealth.setStartDate(new Date());
	}

	public String createGroupMicroHealth() {
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(null, null, WorkflowTask.APPROVAL, WorkflowReferenceType.GROUP_MICROHEALTH, user, responsiblePerson);
			this.groupMicroHealth.setPaymentComplete(false);
			this.groupMicroHealth.setProcessComplete(false);
			this.groupMicroHealth.setBranch(user.getBranch());
			if (isEditProposal) {
				groupMicroHealthService.updateGroupMicroHealth(groupMicroHealth, workFlowDTO, bancaassuranceProposal);
			} else {
				groupMicroHealthService.createGroupMicroHealth(this.groupMicroHealth, workFlowDTO, bancaassuranceProposal);
			}

			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, groupMicroHealth.getProposalNo());
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			groupMicroHealth = new GroupMicroHealth();
		} catch (SystemException e) {
			handelSysException(e);
		}
		return "dashboard";
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (groupMicroHealth.getSaleChannelType().equals(SaleChannelType.AGENT)) {
			groupMicroHealth.setSaleMan(null);
			groupMicroHealth.setReferral(null);
		} else if (groupMicroHealth.getSaleChannelType().equals(SaleChannelType.SALEMAN)) {
			groupMicroHealth.setAgent(null);
			groupMicroHealth.setReferral(null);
		} else if (groupMicroHealth.getSaleChannelType().equals(SaleChannelType.REFERRAL)) {
			groupMicroHealth.setSaleMan(null);
			groupMicroHealth.setAgent(null);
		} else if (groupMicroHealth.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
			groupMicroHealth.setSaleMan(null);
			groupMicroHealth.setAgent(null);
			groupMicroHealth.setReferral(null);
			bancaassuranceProposal = new BancaassuranceProposal();
		}
	}

	public void changeBancaEvent(AjaxBehaviorEvent event) {

		bancaassuranceProposal.setBancaBRM(null);
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);
	}

	public void changeChannel(AjaxBehaviorEvent event) {
		if (isChannel) {
			isChannel = true;
		} else {
			isChannel = false;
			bancaassuranceProposal = new BancaassuranceProposal();
		}

	}

	public SaleChannelType[] getSaleChannelType() {
		return SaleChannelType.values();
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		groupMicroHealth.setSaleMan(saleMan);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		groupMicroHealth.setAgent(agent);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		groupMicroHealth.setReferral(referral);
	}

	public void returnEntity(SelectEvent event) {
		Entitys entitys = (Entitys) event.getObject();
		if (entity != null && !entitys.getId().equals(entity.getId())) {
			groupMicroHealth.setBranch(null);
			groupMicroHealth.setSalePoint(null);
		}
		this.entity = entitys;
	}

	public void removeEntity() {
		entity = null;
		groupMicroHealth.setBranch(null);
		groupMicroHealth.setSalePoint(null);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		groupMicroHealth.setBranch(branch);
		groupMicroHealth.setSalePoint(null);
	}

	public void removeBranch() {
		groupMicroHealth.setBranch(null);
		groupMicroHealth.setSalePoint(null);

	}

	public String getSalePersonName() {
		if (groupMicroHealth.getAgent() != null) {
			return groupMicroHealth.getAgent().getFullName();
		} else if (groupMicroHealth.getSaleMan() != null) {
			return groupMicroHealth.getSaleMan().getFullName();
		} else if (groupMicroHealth.getReferral() != null) {
			return groupMicroHealth.getReferral().getFullName();
		}
		return null;
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entity == null ? "" : entity.getId(), user.getBranch().getId());
	}

	public void selectSalePoint() {
		selectSalePointByBranch(groupMicroHealth.getBranch() == null ? "" : groupMicroHealth.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		groupMicroHealth.setSalePoint(salePoint);
	}

	public void selectUser() {
		selectUser(WorkflowTask.APPROVAL, WorkFlowType.MEDICAL_INSURANCE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public GroupMicroHealth getGroupMicroHealth() {
		return groupMicroHealth;
	}

	public void setGroupMicroHealth(GroupMicroHealth groupMicroHealth) {
		this.groupMicroHealth = groupMicroHealth;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public boolean isEditProposal() {
		return isEditProposal;
	}

	public void setEditProposal(boolean isEditProposal) {
		this.isEditProposal = isEditProposal;
	}

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		bancaassuranceProposal.setChannel(channel);
	}

	public void returnBancaLIC(SelectEvent event) {
		BancaLIC bancaLIC = (BancaLIC) event.getObject();
		bancaassuranceProposal.setBancaLIC(bancaLIC);
	}

	public void returnBancaRefferal(SelectEvent event) {
		BancaRefferal bancaRefferal = (BancaRefferal) event.getObject();
		bancaassuranceProposal.setBancaRefferal(bancaRefferal);
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		bancaassuranceProposal.setBancaBranch(bancaBranch);
	}

	public void returnBancaBrm(SelectEvent event) {
		BancaBRM bancaBrm = (BancaBRM) event.getObject();
		bancaassuranceProposal.setBancaBRM(bancaBrm);
	}

	public void removeChannel() {
		bancaassuranceProposal.setChannel(null);

	}

	public void removeBancaLIC() {
		bancaassuranceProposal.setBancaLIC(null);

	}

	public void removeBancaBRM() {
		bancaassuranceProposal.setBancaBRM(null);

	}

	public void removeBancaBranch() {
		bancaassuranceProposal.setBancaBranch(null);

	}

	public boolean isChannel() {
		return isChannel;
	}

	public void setChannel(boolean isChannel) {
		this.isChannel = isChannel;
	}

	public List<BancaMethod> getBancaMethodList() {
		return bancaMethodList;
	}

	public BancaMethod getBancaMethod() {
		return bancaMethod;
	}

	public void setBancaMethod(BancaMethod bancaMethod) {
		this.bancaMethod = bancaMethod;
	}

//	public void selectBancaBrm() {
//		selectBancaBRM(bancaassuranceProposal.getBancaBranch().getId());
//	}

//	public void selectBancaReferralByOTC() {
//		selectBancaReferralByOTC(bancaassuranceProposal.getBancaBranch().getId());
//	}
//
//	public void selectBancaReferral() {
//		selectBancaReferral(bancaassuranceProposal.getBancaBranch().getId());
//	}
}
