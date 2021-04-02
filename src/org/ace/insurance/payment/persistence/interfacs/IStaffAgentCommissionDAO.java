package org.ace.insurance.payment.persistence.interfacs;

import java.util.List;

import org.ace.insurance.payment.AC001;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.web.manage.agent.AgentEnquiryCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IStaffAgentCommissionDAO {

	public List<StaffAgentCommission> findStaffAgentByCommissionCriteria(AgentEnquiryCriteria agentEnquiryCriteria) throws DAOException;

	public List<AC001> findStaffAgentCommissionByAgent(AgentEnquiryCriteria angenAgentEnquiryCriteria) throws DAOException;

	public StaffAgentCommission findStaffAgentCommissionByChalanNo(String challanNo) throws DAOException;

	public StaffAgentCommission findStaffAgentCommissionByReferenceNo(String referenceNo) throws DAOException;

	public StaffAgentCommission findStaffAgentCommissionByPolicyId(String policyId) throws DAOException;

	void removeStaffAgentCommissionByReceiptNo(String receiptNo) throws DAOException;

}
