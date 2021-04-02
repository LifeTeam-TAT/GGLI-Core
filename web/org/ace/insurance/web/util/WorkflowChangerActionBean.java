package org.ace.insurance.web.util;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.GenericDataModel;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.proxy.GF001;
import org.ace.insurance.proxy.LIF001;
import org.ace.insurance.proxy.MED001;
import org.ace.insurance.proxy.TRA001;
import org.ace.insurance.proxy.WorkflowCriteria;
import org.ace.insurance.proxy.service.interfaces.IProxyService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "WorkFlowChangerActionBean")
public class WorkflowChangerActionBean<T extends IDataModel> extends BaseBean {

	@ManagedProperty(value = "#{ProxyService}")
	private IProxyService proxyService;

	public void setProxyService(IProxyService proxyService) {
		this.proxyService = proxyService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	private WorkflowReferenceType referenceName;
	private User user;
	private GenericDataModel<IDataModel> proposalDataModel;

	private T[] selectedLifeProposals;
	private User responsiblePerson;

	private boolean sumInsuredRender;

	@PostConstruct
	public void init() {
		referenceName = WorkflowReferenceType.HEALTH_PROPOSAL;
		search();
	}

	public User getUser() {
		return user;
	}

	public WorkflowReferenceType[] getReferenceType() {
		return WorkflowReferenceType.values();
	}

	public WorkflowReferenceType getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(WorkflowReferenceType referenceName) {
		this.referenceName = referenceName;
	}

	public GenericDataModel<IDataModel> getProposalDataModel() {
		return proposalDataModel;
	}

	public T[] getSelectedLifeProposals() {
		return selectedLifeProposals;
	}

	public void setSelectedLifeProposals(T[] selectedLifeProposals) {
		this.selectedLifeProposals = selectedLifeProposals;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void search() {
		proposalDataModel = new GenericDataModel<>();

		if (WorkflowReferenceType.isMedical(referenceName)) {
			List<MED001> medicalPaymentList = proxyService.find_MED001(new WorkflowCriteria(referenceName, WorkflowTask.PAYMENT, null, null));
			proposalDataModel = new GenericDataModel(medicalPaymentList);
		} else if (WorkflowReferenceType.GROUPFARMER_PROPOSAL.equals(referenceName)) {
			List<GF001> groupFarmerPaymentList = proxyService.find_GF001(new WorkflowCriteria(referenceName, WorkflowTask.PAYMENT, null, null));
			proposalDataModel = new GenericDataModel(groupFarmerPaymentList);
		} else if (WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL.equals(referenceName)) {
			List<TRA001> travelList = proxyService.find_TRA001(new WorkflowCriteria(referenceName, WorkflowTask.PAYMENT, null, null));
			proposalDataModel = new GenericDataModel(travelList);
		} else {
			List<LIF001> lifePaymentList = proxyService.find_LIF001(new WorkflowCriteria(referenceName, WorkflowTask.getPaymentBy(referenceName), null, null));
			proposalDataModel = new GenericDataModel(lifePaymentList);
		}
	}

	public String updateWorkflow() {
		String result = null;
		try {
			if (responsiblePerson != null && selectedLifeProposals.length > 0) {
				if (WorkflowReferenceType.isMedical(referenceName)) {
					for (T proposal : selectedLifeProposals) {
						WorkFlowDTO workFlowDTO = new WorkFlowDTO(proposal.getId(), "", WorkflowTask.getConfirmationBy(referenceName), referenceName, user, responsiblePerson);
						MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(proposal.getId());
						medicalProposalService.deletePayment(medicalPolicy, workFlowDTO);
					}
				} else if (WorkflowReferenceType.GROUPFARMER_PROPOSAL.equals(referenceName)) {
					for (T proposal : selectedLifeProposals) {
						WorkFlowDTO workFlowDTO = new WorkFlowDTO(proposal.getId(), "", WorkflowTask.getConfirmationBy(referenceName), referenceName, user, responsiblePerson);
						GroupFarmerProposal groupFarmerProposal = groupFarmerProposalService.findGroupFarmerById(proposal.getId());
						groupFarmerProposalService.deletePayment(groupFarmerProposal, workFlowDTO);
					}
				} else if (WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL.equals(referenceName)) {
					for (T proposal : selectedLifeProposals) {
						WorkFlowDTO workFlowDTO = new WorkFlowDTO(proposal.getId(), "", WorkflowTask.getConfirmationBy(referenceName), referenceName, user, responsiblePerson);
						PersonTravelPolicy personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(proposal.getId());
						personTravelPolicyService.deletePayment(personTravelPolicy, workFlowDTO);
					}
				} else {
					for (T proposal : selectedLifeProposals) {
						WorkFlowDTO workFlowDTO = new WorkFlowDTO(proposal.getId(), "", WorkflowTask.getConfirmationBy(referenceName), referenceName, user, responsiblePerson);
						LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(proposal.getId());
						lifeProposalService.deletePayment(lifePolicy, workFlowDTO);
					}
				}
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.WORKFLOW_CHANGER_PROCESS_SUCCESS);
				result = "dashboard";
			} else {
				addErrorMessage("proposalListForm:paymentTablePanel", MessageId.AT_LEAST_ONE_PROPOSAL_IS_REQUIRED);
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void selectUser() {
		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.LIFE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public boolean isSumInsuredRender() {
		return !(referenceName.equals(WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL) || referenceName.equals(WorkflowReferenceType.HEALTH_PROPOSAL)
				|| referenceName.equals(WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL) || referenceName.equals(WorkflowReferenceType.MICRO_HEALTH_PROPOSAL));
	}

	public void setSumInsuredRender(boolean sumInsuredRender) {
		this.sumInsuredRender = sumInsuredRender;
	}

}
