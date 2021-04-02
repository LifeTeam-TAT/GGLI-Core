package org.ace.insurance.web.manage.life.studentLife;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.java.web.common.BaseBean;

@RequestScoped
@ManagedBean(name = "StudentLifeProposalInfoTemplateActionBean")
public class StudentLifeProposalInfoTemplateActionBean extends BaseBean {

	private LifeProposal lifeProposal;
	private List<WorkFlowHistory> workFlowList;
	private boolean isStudentLife;

	private void initializeInjection() {
		lifeProposal = (LifeProposal) getParam("lifeProposal");
		isStudentLife = KeyFactorChecker.isStudentLife(lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getId());
		workFlowList = (List<WorkFlowHistory>) getParam("workFlowList");
	}

	@PreDestroy
	public void destroy() {
		// removeParam("lifeProposal");
		// removeParam("workFlowList");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public boolean isStudentLife() {
		return isStudentLife;
	}

}
