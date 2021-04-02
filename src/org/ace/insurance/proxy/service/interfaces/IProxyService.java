package org.ace.insurance.proxy.service.interfaces;

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
import org.ace.java.component.SystemException;

public interface IProxyService {
	public List<CGO001> find_CGO001(WorkflowCriteria wfc);

	public List<MED001> find_MED001(WorkflowCriteria wfc);

	public List<LIF001> find_LIF001(WorkflowCriteria wfc);

	public List<MCL001> findDamagedVehicleClaim(WorkflowCriteria wfc);

	public List<MCL001> findWindScreenClaim(WorkflowCriteria wfc);

	public List<MCL001> findTowingClaim(WorkflowCriteria wfc);

	public List<MCL001> findThirdPartyPropertyClaim(WorkflowCriteria wfc);

	public List<MCL001> findPersonCasualtyClaim(WorkflowCriteria wfc);

	public List<MCL001> findMedicalExpenseClaim(WorkflowCriteria wfc);

	public List<LCLD001> find_LCLD001(WorkflowCriteria wfc);

	public List<LCB001> find_LCB001(WorkflowCriteria wfc);

	public List<CIN001> find_CIN001(WorkflowReferenceType referenceType, CoinsuranceType type, WorkflowTask workFlowTask, String userId);

	public List<CIN002> find_CIN002(WorkflowReferenceType referenceType, CoinsuranceType type, WorkflowTask workFlowTask, String userId);

	public List<SnakeBitePolicyDTO> findSnakeBitePolicyForDashboard(WorkflowCriteria wfc);

	public List<AgentCommissionDTO> findAgentCommissionForDashboard(WorkflowCriteria wfc);

	public long findAgentCommissionCount(WorkflowCriteria wfc);

	public List<TRA001> findTravelProposalForDashboard(WorkflowCriteria wfc);

	public List<MEDCLM002> find_MEDCLM002(WorkflowCriteria wfc);

	public List<LSP001> find_LSP001(WorkflowCriteria wfc);

	public List<LPP001> find_LPP001(WorkflowCriteria wfc);

	public List<TRA001> find_TRA001(WorkflowCriteria wfc);

	public List<MEDICAL002> findMEDICAL002ByProduct(WorkflowCriteria wfc, List<String> productIdList);

	public List<GroupMicroHealth> findGroupMicroHealth(WorkflowCriteria wfc) throws SystemException;

	public List<GF001> find_GF001(WorkflowCriteria wfc);

	/** Life Claim **/
	public List<LCP001> find_LCP001(WorkflowCriteria wfc);

	List<StaffCommissionDTO> findStaffCommissionForDashboard(WorkflowCriteria wfc);

	long findStaffCommissionCount(WorkflowCriteria wfc);
}
