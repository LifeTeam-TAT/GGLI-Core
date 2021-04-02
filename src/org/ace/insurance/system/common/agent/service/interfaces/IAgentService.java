/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.agent.service.interfaces;

import java.util.List;

import org.ace.insurance.common.AgentCriteria;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.system.common.agent.AGT001;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.history.AgentHistory;
import org.ace.java.component.SystemException;

public interface IAgentService {
	public void addNewAgent(Agent Agent);

	public void addNewAgentHistory(AgentHistory agentHistory);

	public Agent updateAgent(Agent Agent);

	public void deleteAgent(Agent Agent);

	public Agent findAgentById(String id);

	public List<Agent> findAllAgent();

	public List<AGT001> findAgentByCriteria(AgentCriteria criteria, int max);

	public void generateAgentInvoice(List<AgentCommission> agentCommissions, boolean isEnquiry, String dirPath, String fileName);

	public boolean isExitingAgent(Agent agent);

	void CreateHistoryAndRemoveAgentById(String agentId) throws SystemException;
}
