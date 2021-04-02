package org.ace.insurance.payment.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.payment.AC001;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.persistence.interfacs.IAgentCommissionDAO;
import org.ace.insurance.payment.persistence.interfacs.IStaffAgentCommissionDAO;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.web.manage.agent.AgentEnquiryCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("StaffAgentCommissionDAO")
public class StaffAgentCommissionDAO extends BasicDAO implements IStaffAgentCommissionDAO {

	@Override
	public List<StaffAgentCommission> findStaffAgentByCommissionCriteria(AgentEnquiryCriteria agentEnquiryCriteria)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AC001> findStaffAgentCommissionByAgent(AgentEnquiryCriteria angenAgentEnquiryCriteria)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaffAgentCommission findStaffAgentCommissionByChalanNo(String challanNo) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaffAgentCommission findStaffAgentCommissionByReferenceNo(String referenceNo) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaffAgentCommission findStaffAgentCommissionByPolicyId(String policyId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeStaffAgentCommissionByReceiptNo(String receiptNo) throws DAOException {
		// TODO Auto-generated method stub
		
	}


}
