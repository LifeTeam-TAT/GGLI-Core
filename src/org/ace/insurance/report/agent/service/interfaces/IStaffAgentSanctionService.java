package org.ace.insurance.report.agent.service.interfaces;

import java.util.List;
import java.util.Map;

import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.report.agent.view.StaffAgentCommissionInfo;
import org.ace.insurance.system.common.currency.Currency;

public interface IStaffAgentSanctionService {
	public List<StaffAgentCommissionInfo> findAgents(StaffSanctionCriteria criteria);

	public void sanctionAgent(Map<String, List<StaffAgentCommissionInfo>> agentComissionMap, Currency currency);

	void generateReport(Map<String, List<StaffAgentCommissionInfo>> agentComissionMap, StaffSanctionCriteria criteria, boolean isEnquiry, String filePath, String fileName)
			throws Exception;
	public void generatestaffAgentInvoice(List<StaffAgentCommission> agentCommissions, boolean isEnquiry, String dirPath, String fileName) throws Exception;
	
	
}
