package org.ace.insurance.proxy.service;

import java.util.List;

import javax.annotation.Resource;

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
import org.ace.insurance.proxy.persistence.interfaces.IProxyDAO;
import org.ace.insurance.proxy.service.interfaces.IProxyService;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.insurance.web.manage.agent.payment.StaffCommissionDTO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "ProxyService")
public class ProxyService implements IProxyService {
	@Resource(name = "ProxyDAO")
	private IProxyDAO proxyDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CGO001> find_CGO001(WorkflowCriteria wfc) {
		List<CGO001> result = null;
		try {
			result = proxyDAO.find_CGO001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find CGO001 By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MED001> find_MED001(WorkflowCriteria wfc) {
		List<MED001> result = null;
		try {
			result = proxyDAO.find_MED001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find MED001 By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LIF001> find_LIF001(WorkflowCriteria wfc) {
		List<LIF001> result = null;
		try {
			result = proxyDAO.find_LIF001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LIF001 By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findDamagedVehicleClaim(WorkflowCriteria wfc) {
		List<MCL001> result = null;
		try {
			result = proxyDAO.findDamagedVehicleClaim(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find find DamagedVehicle By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findWindScreenClaim(WorkflowCriteria wfc) {
		List<MCL001> result = null;
		try {
			result = proxyDAO.findWindScreenClaim(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  WindScreenClaim By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findTowingClaim(WorkflowCriteria wfc) {
		List<MCL001> result = null;
		try {
			result = proxyDAO.findTowingClaim(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find TowingClaim By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findThirdPartyPropertyClaim(WorkflowCriteria wfc) {
		List<MCL001> result = null;
		try {
			result = proxyDAO.findThirdPartyPropertyClaim(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find ThirdPartyProperty Claim By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findPersonCasualtyClaim(WorkflowCriteria wfc) {
		List<MCL001> result = null;
		try {
			result = proxyDAO.findPersonCasualtyClaim(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonCasualty Claim By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findMedicalExpenseClaim(WorkflowCriteria wfc) {
		List<MCL001> result = null;
		try {
			result = proxyDAO.findMedicalExpenseClaim(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find MedicalExpense Claim By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LCLD001> find_LCLD001(WorkflowCriteria wfc) {
		List<LCLD001> result = null;
		try {
			result = proxyDAO.find_LCLD001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LCLD001 for DashBoar By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LCB001> find_LCB001(WorkflowCriteria wfc) {
		List<LCB001> result = null;
		try {
			result = proxyDAO.find_LCB001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LCB001 for DashBoar By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CIN001> find_CIN001(WorkflowReferenceType referenceType, CoinsuranceType type, WorkflowTask workFlowTask, String userId) {
		List<CIN001> result = null;
		try {
			result = proxyDAO.find_CIN001(referenceType, type, workFlowTask, userId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find CIN001 for DashBoar By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CIN002> find_CIN002(WorkflowReferenceType referenceType, CoinsuranceType type, WorkflowTask workFlowTask, String userId) {
		List<CIN002> result = null;
		try {
			result = proxyDAO.find_CIN002(referenceType, type, workFlowTask, userId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find CIN002 for DashBoar By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<SnakeBitePolicyDTO> findSnakeBitePolicyForDashboard(WorkflowCriteria wfc) {
		List<SnakeBitePolicyDTO> result = null;
		try {
			result = proxyDAO.findSnakeBitePolicyForDashboard(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find SnakeBitePolicyDTO for DashBoar By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<AgentCommissionDTO> findAgentCommissionForDashboard(WorkflowCriteria wfc) {
		List<AgentCommissionDTO> result = null;
		try {
			result = proxyDAO.findAgentCommissionForDashboard(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommissionDTO for DashBoar By WorkflowCriteria", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<StaffCommissionDTO> findStaffCommissionForDashboard(WorkflowCriteria wfc) {
		List<StaffCommissionDTO> result = null;
		try {
			result = proxyDAO.findStaffCommissionForDashboard(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommissionDTO for DashBoar By WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public long findAgentCommissionCount(WorkflowCriteria wfc) {
		long count = 0;
		try {
			count = proxyDAO.findAgentCommissionCount(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommissionCount By WorkflowCriteria", e);
		}
		return count;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public long findStaffCommissionCount(WorkflowCriteria wfc) {
		long count = 0;
		try {
			count = proxyDAO.findStaffCommissionCount(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommissionCount By WorkflowCriteria", e);
		}
		return count;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TRA001> findTravelProposalForDashboard(WorkflowCriteria wfc) {
		List<TRA001> result = null;
		try {
			result = proxyDAO.findTravelProposalForDashboard(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find TravelProposal for dashboard by WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MEDCLM002> find_MEDCLM002(WorkflowCriteria wfc) {
		List<MEDCLM002> result = null;
		try {
			result = proxyDAO.find_MEDCLM002(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find MED002 By WorkflowCriteria", e);
		}
		return result;
	}

	/* Life Surrender Proposal */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LSP001> find_LSP001(WorkflowCriteria wfc) {
		List<LSP001> result = null;
		try {
			result = proxyDAO.find_LSP001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find LSP001 By WorkflowCriteria", e);
		}
		return result;

	}

	/* Life PaidUp */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LPP001> find_LPP001(WorkflowCriteria wfc) {
		List<LPP001> result = null;
		try {
			result = proxyDAO.find_LPP001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find LPP001 By WorkflowCriteria", e);
		}
		return result;

	}

	/* Person Travel */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TRA001> find_TRA001(WorkflowCriteria wfc) {
		List<TRA001> result = null;
		try {
			result = proxyDAO.find_TRA001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find TRA001 By WorkflowCriteria", e);
		}
		return result;
	}

	/* MEDICAL INSURANCE */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MEDICAL002> findMEDICAL002ByProduct(WorkflowCriteria wfc, List<String> productIdList) {
		List<MEDICAL002> result = null;
		try {
			result = proxyDAO.find_MEDICAL002(wfc, productIdList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find TRA001 By WorkflowCriteria", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<GroupMicroHealth> findGroupMicroHealth(WorkflowCriteria wfc) throws SystemException {
		List<GroupMicroHealth> result = null;
		try {
			result = proxyDAO.findGroupMicroHealth(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find TRA001 By WorkflowCriteria", e);
		}
		return result;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<GF001> find_GF001(WorkflowCriteria wfc) {
		List<GF001> result = null;
		try {
			result = proxyDAO.find_GF001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find groupFarmer for dashboard by WorkflowCriteria", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LCP001> find_LCP001(WorkflowCriteria wfc) {
		List<LCP001> result = null;
		try {
			result = proxyDAO.find_LCP001(wfc);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find groupFarmer for dashboard by WorkflowCriteria", e);
		}
		return result;
	}

}
