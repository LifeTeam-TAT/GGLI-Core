package org.ace.insurance.report.agent.persistence.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.report.agent.view.StaffAgentCommissionInfo;
import org.ace.java.component.persistence.exception.DAOException;

public interface IStaffAgentSactionDAO {
	public List<StaffAgentCommissionInfo> findStaffAgents(StaffSanctionCriteria criteria) throws DAOException;

	public void updateAgentCommissionStaus(String agentCommissionId, String sanctionNo, Date sanctionDate, double withHoldingTax, double homeWithHoldingTax) throws DAOException;
	
	public StaffAgentCommission findAgentCommissionById(String id) throws DAOException;

}
