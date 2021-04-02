package org.ace.insurance.payment.persistence.interfacs;

import java.util.List;

import org.ace.insurance.payment.AC001;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.web.manage.agent.AgentEnquiryCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IAgentCommissionDAO {

	public List<Agent> findAgentByCommissionCriteria(AgentEnquiryCriteria agentEnquiryCriteria) throws DAOException;

	public List<AC001> findAgentCommissionByAgent(AgentEnquiryCriteria angenAgentEnquiryCriteria) throws DAOException;

	public AgentCommission findAgentCommissionByChalanNo(String challanNo) throws DAOException;

	public AgentCommission findAgentCommissionByReferenceNo(String referenceNo) throws DAOException;

	public AgentCommission findAgentCommissionByPolicyId(String policyId) throws DAOException;

	void removeAgentcomissionByReceiptNo(String receiptNo) throws DAOException;

}
