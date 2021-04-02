/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.agent.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.AgentCriteria;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.agent.AGT001;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.history.AgentHistory;
import org.ace.insurance.system.common.agent.persistence.interfaces.IAgentDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("AgentDAO")
public class AgentDAO extends BasicDAO implements IAgentDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Agent agent) throws DAOException {
		try {
			em.persist(agent);
			insertProcessLog(TableName.AGENT, agent.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Agent", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Agent update(Agent agent) throws DAOException {
		try {
			agent = em.merge(agent);
			updateProcessLog(TableName.AGENT, agent.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Agent", pe);
		}
		return agent;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Agent agent) throws DAOException {
		try {
			agent = em.merge(agent);
			em.remove(agent);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Agent", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Agent findById(String id) throws DAOException {
		Agent result = null;
		try {
			result = em.find(Agent.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Agent", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Agent> findAll() throws DAOException {
		List<Agent> result = null;
		try {
			Query q = em.createNamedQuery("Agent.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Agent", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<AGT001> findByCriteria(AgentCriteria criteria, int max) throws DAOException {
		List<AGT001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT New " + AGT001.class.getName());
			buffer.append("(a.id, a.codeNo, a.residentAddress.residentAddress, a.liscenseNo, a.initialId, a.name, a.idNo) FROM Agent a ");
			if (criteria.getAgentCriteriaItems() != null) {
				switch (criteria.getAgentCriteriaItems()) {
					case FULLNAME: {

						buffer.append(
								" WHERE FUNCTION( 'REPLACE', CONCAT(CONCAT(a.initialId, ''), CONCAT(a.name.firstName,''), CONCAT(a.name.middleName, ''), CONCAT(a.name.lastName , '') ), ' ', '')");
						buffer.append(" LIKE :value");
						break;
					}
					case AGENT_CODE: {
						buffer.append(" WHERE a.codeNo = :value");
						break;
					}
					case LISCENSENO: {
						buffer.append(" WHERE a.liscenseNo = :value");
						break;
					}
					case NRCNO:
					case FRCNO:
					case PASSPORTNO: {
						buffer.append("WHERE a.idNo = :value AND a.idType = :type");
						break;
					}
				}
			}

			buffer.append(" ORDER BY a.liscenseNo DESC");
			TypedQuery<AGT001> query = em.createQuery(buffer.toString(), AGT001.class);
			query.setMaxResults(max);

			if (criteria.getAgentCriteriaItems() != null) {
				switch (criteria.getAgentCriteriaItems()) {
					case PASSPORTNO:
						query.setParameter("type", IdType.PASSPORTNO);
						query.setParameter("value", criteria.getCriteriaValue());
						break;
					case NRCNO:
						query.setParameter("type", IdType.NRCNO);
						query.setParameter("value", criteria.getCriteriaValue());
						break;
					case FRCNO:
						query.setParameter("type", IdType.FRCNO);
						query.setParameter("value", criteria.getCriteriaValue());
						break;
					case FULLNAME:
						query.setParameter("value", "%" + StringUtils.replace(criteria.getCriteriaValue(), " ", "") + "%");
						break;
					default:
						query.setParameter("value", criteria.getCriteriaValue());
						break;
				}

			}

			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Agent", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isExitingAgent(Agent agent) throws DAOException {
		boolean exist = false;
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			if (!agent.getIdType().equals(IdType.STILL_APPLYING)) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.idNo) > 0) THEN TRUE ELSE FALSE END FROM Agent c");
				buffer.append(" WHERE c.idNo = :idNo ");
				buffer.append(agent.getId() != null ? "AND c.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (agent.getId() != null)
					query.setParameter("id", agent.getId());
				query.setParameter("idNo", agent.getFullIdNo());
				exist = (Boolean) query.getSingleResult();
			}

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.id) > 0) THEN TRUE ELSE FALSE END FROM Agent c");
				buffer.append(" WHERE c.liscenseNo = :liscenseNo ");
				buffer.append(agent.getId() != null ? "AND c.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (agent.getId() != null)
					query.setParameter("id", agent.getId());
				query.setParameter("liscenseNo", agent.getLiscenseNo());
				exist = (Boolean) query.getSingleResult();
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing NRC No.", pe);
		}

		return exist;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void inserthistory(AgentHistory Agenthistory) throws DAOException {
		try {
			em.persist(Agenthistory);
			insertProcessLog(TableName.AGENTHISTORY, Agenthistory.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Agenthistory", pe);
		}

	}
}
