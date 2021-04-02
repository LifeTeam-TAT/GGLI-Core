/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.agent.persistence.interfaces;

import java.util.List;

import org.ace.insurance.common.AgentCriteria;
import org.ace.insurance.system.common.agent.AGT001;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.history.AgentHistory;
import org.ace.java.component.persistence.exception.DAOException;

public interface IAgentDAO {
	public void insert(Agent Agent) throws DAOException;

	public void inserthistory(AgentHistory Agenthistory) throws DAOException;

	public Agent update(Agent Agent) throws DAOException;

	public void delete(Agent Agent) throws DAOException;

	public Agent findById(String id) throws DAOException;

	public List<Agent> findAll() throws DAOException;

	public List<AGT001> findByCriteria(AgentCriteria criteria, int max) throws DAOException;

	public boolean isExitingAgent(Agent agent) throws DAOException;
}
