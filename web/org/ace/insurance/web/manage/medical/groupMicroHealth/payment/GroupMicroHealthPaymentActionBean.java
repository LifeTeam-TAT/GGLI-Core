package org.ace.insurance.web.manage.medical.groupMicroHealth.payment;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "GroupMicroHealthPaymentActionBean")
public class GroupMicroHealthPaymentActionBean extends BaseBean {
	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{GroupMicroHealthService}")
	private IGroupMicroHealthService groupMicroHealthService;

	public void setGroupMicroHealthService(IGroupMicroHealthService groupMicroHealthService) {
		this.groupMicroHealthService = groupMicroHealthService;
	}

	private User user;
	private GroupMicroHealth groupMicroHealth;
	private PaymentDTO paymentDTO;
	private List<Payment> paymentList;

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentList = paymentService.findByProposal(groupMicroHealth.getId(), PolicyReferenceType.GROUP_MICRO_HEALTH, false);
		paymentDTO = new PaymentDTO(paymentList);
	}

	@PreDestroy
	public void destroy() {
		removeParam("groupMicroHealth");
	}

	public void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		groupMicroHealth = (groupMicroHealth == null) ? (GroupMicroHealth) getParam("groupMicroHealth") : groupMicroHealth;

	}

	public String paymentMedicalProposal() {

		String result = null;
		try {
			groupMicroHealthService.paymentGroupMicroHealth(groupMicroHealth, paymentList, user.getBranch());
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, groupMicroHealth.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
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

	public GroupMicroHealth getGroupMicroHealth() {
		return groupMicroHealth;
	}

	public void setGroupMicroHealth(GroupMicroHealth groupMicroHealth) {
		this.groupMicroHealth = groupMicroHealth;
	}

}
