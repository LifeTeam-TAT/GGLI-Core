package org.ace.insurance.proxy.persistence.interfaces;

import java.util.List;

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDTO;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.proxy.CGO001;
import org.ace.insurance.proxy.CIN001;
import org.ace.insurance.proxy.CIN002;
import org.ace.insurance.proxy.GF001;
import org.ace.insurance.proxy.LCB001;
import org.ace.insurance.proxy.LCLD001;
import org.ace.insurance.proxy.LCP001;
import org.ace.insurance.proxy.LIF001;
import org.ace.insurance.proxy.LPP001;
import org.ace.insurance.proxy.LSP001;
import org.ace.insurance.proxy.MCL001;
import org.ace.insurance.proxy.MED001;
import org.ace.insurance.proxy.MEDCLM002;
import org.ace.insurance.proxy.MEDICAL002;
import org.ace.insurance.proxy.TRA001;
import org.ace.insurance.proxy.WorkflowCriteria;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.insurance.web.manage.agent.payment.StaffCommissionDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IProxyDAO {

	public List<CGO001> find_CGO001(WorkflowCriteria wfc) throws DAOException;

	public List<LIF001> find_LIF001(WorkflowCriteria wfc) throws DAOException;

	public List<MCL001> findDamagedVehicleClaim(WorkflowCriteria wfc) throws DAOException;

	public List<MCL001> findWindScreenClaim(WorkflowCriteria wfc) throws DAOException;

	public List<MCL001> findTowingClaim(WorkflowCriteria wfc) throws DAOException;

	public List<MCL001> findThirdPartyPropertyClaim(WorkflowCriteria wfc) throws DAOException;

	public List<MCL001> findPersonCasualtyClaim(WorkflowCriteria wfc) throws DAOException;

	public List<MCL001> findMedicalExpenseClaim(WorkflowCriteria wfc) throws DAOException;

	public List<LCLD001> find_LCLD001(WorkflowCriteria wfc) throws DAOException;

	public List<LCB001> find_LCB001(WorkflowCriteria wfc) throws DAOException;

	public List<CIN001> find_CIN001(WorkflowReferenceType referenceType, CoinsuranceType type, WorkflowTask workFlowTask, String userId) throws DAOException;

	public List<CIN002> find_CIN002(WorkflowReferenceType referenceType, CoinsuranceType type, WorkflowTask workFlowTask, String userId) throws DAOException;

	public List<SnakeBitePolicyDTO> findSnakeBitePolicyForDashboard(WorkflowCriteria wfc) throws DAOException;

	public List<AgentCommissionDTO> findAgentCommissionForDashboard(WorkflowCriteria wfc) throws DAOException;

	public long findAgentCommissionCount(WorkflowCriteria wfc) throws DAOException;

	public List<TRA001> findTravelProposalForDashboard(WorkflowCriteria wfc) throws DAOException;

	public List<MED001> find_MED001(WorkflowCriteria wfc) throws DAOException;

	public List<MEDCLM002> find_MEDCLM002(WorkflowCriteria wfc) throws DAOException;

	public List<LSP001> find_LSP001(WorkflowCriteria wfc) throws DAOException;

	public List<LPP001> find_LPP001(WorkflowCriteria wfc) throws DAOException;

	public List<TRA001> find_TRA001(WorkflowCriteria wfc) throws DAOException;

	List<MEDICAL002> find_MEDICAL002(WorkflowCriteria wfc, List<String> productIdList) throws DAOException;

	List<GroupMicroHealth> findGroupMicroHealth(WorkflowCriteria wfc);

	public List<GF001> find_GF001(WorkflowCriteria wfc) throws DAOException;

	/** Life Claim **/
	public List<LCP001> find_LCP001(WorkflowCriteria wfc) throws DAOException;

	List<StaffCommissionDTO> findStaffCommissionForDashboard(WorkflowCriteria wfc) throws DAOException;

	long findStaffCommissionCount(WorkflowCriteria wfc) throws DAOException;
}
