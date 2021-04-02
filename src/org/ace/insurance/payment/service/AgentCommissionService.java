package org.ace.insurance.payment.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.payment.AC001;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.persistence.interfacs.IAgentCommissionDAO;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IAgentCommissionService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.web.manage.agent.AgentEnquiryCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "AgentCommissionService")
public class AgentCommissionService extends BaseService implements IAgentCommissionService {

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "AgentCommissionDAO")
	private IAgentCommissionDAO agentCommissionDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewAgentCommisssion(MedicalPolicy medicalPolicy, String chalanNo) {
		try {
			float commissionPercent = medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct().getFirstCommission();
			double premium = 0.0;
			if (medicalPolicy.getTotalPremium() == 0) {
				premium = medicalPolicy.getTotalPremium();
			}

			double firstAgentCommission = 0.0;
			if (medicalPolicy.getAgentCommission() == 0) {
				firstAgentCommission = medicalPolicy.getAgentCommission();
			}
			// paymentDAO.insertAgentCommission(new
			// AgentCommission(medicalPolicy.getId(),
			// PolicyReferenceType.MEDICAL_POLICY,
			// medicalPolicy.getAgent(), firstAgentCommission, null,
			// premium, commissionPercent,
			// AgentCommissionEntryType.UNDERWRITING, 0.0, 0.0,
			// CurrencyUtils.getCurrencyCode(null), premium, chalanNo));

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add agentCommission", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAgentCommisssion(MedicalPolicy medicalPolicy, AgentCommission agentComission) {
		try {
			double premium = 0.0;
			if (medicalPolicy.getTotalPremium() == 0) {
				premium = medicalPolicy.getTotalPremium();
			}

			double firstAgentCommission = 0.0;
			if (medicalPolicy.getAgentCommission() == 0) {
				firstAgentCommission = medicalPolicy.getAgentCommission();
			}
			agentComission.setHomePremium(premium);
			agentComission.setCommission(firstAgentCommission);
			agentComission.setCommissionStartDate(new Date());
			// paymentDAO.updateAgentCommission(agentComission);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update agentCommission", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Agent> findAgentByCommissionCriteria(AgentEnquiryCriteria agentEnquiryCriteria) throws SystemException {
		List<Agent> agentList = null;
		try {
			agentList = agentCommissionDAO.findAgentByCommissionCriteria(agentEnquiryCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Agent By AgentCommission)", e);
		}
		return agentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AC001> findAgentCommissionByAgent(AgentEnquiryCriteria agentEnquiryCriteria) throws SystemException {
		List<AC001> result = null;
		try {
			result = agentCommissionDAO.findAgentCommissionByAgent(agentEnquiryCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of AgentCommission By Agent)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AgentCommission findAgentCommissionByChallanNo(String challanNo) throws SystemException {
		AgentCommission agentCommission = null;
		try {
			agentCommission = agentCommissionDAO.findAgentCommissionByChalanNo(challanNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of AgentCommission By ChallanNo)", e);
		}
		return agentCommission;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AgentCommission findAgentCommissionByPolicyId(String policyId) throws SystemException {
		AgentCommission agentCommission = null;
		try {
			agentCommission = agentCommissionDAO.findAgentCommissionByPolicyId(policyId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of AgentCommission By ChallanNo)", e);
		}
		return agentCommission;
	}

}
