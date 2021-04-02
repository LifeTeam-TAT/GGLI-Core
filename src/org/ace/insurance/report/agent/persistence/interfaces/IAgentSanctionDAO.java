package org.ace.insurance.report.agent.persistence.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.report.agent.AgentComissionInfo;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.AgentSanctionCriteria;
import org.ace.insurance.report.agent.AgentSanctionReport;
import org.ace.insurance.report.agent.persistence.AgentSanctionDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IAgentSanctionDAO {
	public List<AgentComissionInfo> findAgents(AgentSanctionCriteria criteria) throws DAOException;

	public List<AgentSanctionReport> findIndividual(AgentSanctionCriteria criteria, AgentComissionInfo a, boolean isEnquiry) throws DAOException;

	public List<AgentCommission> findIndividualAgentComission(AgentSanctionCriteria criteria, AgentComissionInfo a) throws DAOException;

	public void updateSanction(AgentCommission agentCommission) throws DAOException;

	public AgentSanctionReport findIndividualAgentCommission(AgentCommissionDetailCriteria criteria, AgentCommission agentCom);

	public void updateAgentCommissionStaus(String agentCommissionId, String sanctionNo, Date sanctionDate, double withHoldingTax, double homeWithHoldingTax) throws DAOException;

	public List<AgentSanctionDTO> findAllAgentCommissionInfoByEnquiry(AgentSanctionCriteria criteria) throws DAOException;

	public List<AgentComissionInfo> findAgentCommissionInfoBySanction(AgentSanctionDTO agentSanctionDTO);

	public AgentSanctionReport findLifeIndividualForSanction(AgentSanctionCriteria criteria, AgentComissionInfo a) throws DAOException;

	public AgentSanctionReport findLifeIndividualForSanctionEnquiry(AgentSanctionCriteria criteria, AgentComissionInfo a) throws DAOException;

	public AgentCommission findAgentCommissionById(String id) throws DAOException;
}
