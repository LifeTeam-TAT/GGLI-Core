package org.ace.insurance.report.agent.service.interfaces;

import java.util.List;
import java.util.Map;

import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.report.agent.AgentComissionInfo;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.AgentSanctionCriteria;
import org.ace.insurance.report.agent.AgentSanctionReport;
import org.ace.insurance.report.agent.persistence.AgentSanctionDTO;
import org.ace.insurance.system.common.currency.Currency;

public interface IAgentSanctionService {
	public List<AgentComissionInfo> findAgents(AgentSanctionCriteria criteria);

	public List<AgentSanctionReport> findAgentCommissionIndividual(AgentSanctionCriteria criteria, AgentComissionInfo a);

	public void sanctionAgent(Map<String, List<AgentComissionInfo>> agentComissionMap, Currency currency);

	public void generateReport(Map<String, List<AgentComissionInfo>> agentComissionMap, AgentSanctionCriteria criteria, boolean isEnquiry, String filePath, String fileName)
			throws Exception;

	public AgentSanctionReport findIndividualAgentCommission(AgentCommissionDetailCriteria criteria, AgentCommission a);

	public List<AgentSanctionDTO> findAgentCommissionByEnquiry(AgentSanctionCriteria criteria);

	public List<AgentComissionInfo> findBySanctionNo(AgentSanctionDTO agentSanctionDTO);

}
