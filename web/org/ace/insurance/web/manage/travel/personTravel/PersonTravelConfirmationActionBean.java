package org.ace.insurance.web.manage.travel.personTravel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalInfo;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "PersonTravelConfirmationActionBean")
public class PersonTravelConfirmationActionBean extends BaseBean {

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	/**
	 * @param personTravelProposalService
	 *            the personTravelProposalService to set
	 */
	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private User user;
	private String proposalId;
	private PersonTravelProposal personTravelProposal;
	private String remark;
	private User responsiblePerson;
	private BancaassuranceProposal bancaassuranceProposal;

	@PostConstruct
	public void init() {
		initializeInjection();
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		proposalId = (String) getParam("personTravelProposalId");
		personTravelProposal = personTravelProposalService.findPersonTravelProposalById(proposalId);
		PersonTravelProposalInfo proposalInfo = personTravelProposal.getPersonTravelInfo();
		bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByPersonTravelproposalId(personTravelProposal.getId());
	}

	@PreDestroy
	public void destroy() {
		removeParam("proposalId");
	}

	public String confirmPersonTravel() {
		WorkFlowDTO workFlowDTO = new WorkFlowDTO(personTravelProposal.getId(), remark, WorkflowTask.PAYMENT, WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL, user,
				responsiblePerson);
		outjectPersonTravelProposal(personTravelProposal);
		outjectWorkFlowDTO(workFlowDTO);
		return "receivePersonTravel";
	}

	private void outjectPersonTravelProposal(PersonTravelProposal personTravelProposal) {
		putParam("personTravelProposal", personTravelProposal);
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void selectUser() {
		selectUser(WorkflowTask.PAYMENT, WorkFlowType.PERSON_TRAVEL);
	}

	public String editPersonTravel() {
		BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByPersonTravelproposalId(personTravelProposal.getId());
		outjectPersonTravelProposal(personTravelProposal);
		putParam("bancaassuranceProposal", bancaassuranceProposal);
		return "personTravelProposal";
	}

	public String denyPersonTravel() {
		String result = null;
		try {
			if (responsiblePerson == null) {
				responsiblePerson = user;
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(personTravelProposal.getId(), remark, WorkflowTask.PROPOSAL_REJECT, WorkflowReferenceType.PERSON_TRAVEL_PROPOSAL, user,
					responsiblePerson);
			personTravelProposalService.rejectPersonTravelProposal(personTravelProposal, workFlowDTO);
			outjectPersonTravelProposal(personTravelProposal);
			outjectWorkFlowDTO(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, personTravelProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	/**
	 * @return the personTravelProposal
	 */
	public PersonTravelProposal getPersonTravelProposal() {
		return personTravelProposal;
	}

	/**
	 * @param personTravelProposal
	 *            the personTravelProposal to set
	 */
	public void setPersonTravelProposal(PersonTravelProposal personTravelProposal) {
		this.personTravelProposal = personTravelProposal;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the responsiblePerson
	 */
	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	/**
	 * @param responsiblePerson
	 *            the responsiblePerson to set
	 */
	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
	}

}
