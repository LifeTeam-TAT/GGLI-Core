package org.ace.insurance.mobile.travelProposal.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.mobile.travelProposal.MTP002;
import org.ace.insurance.mobile.travelProposal.MobileTravelProposal;
import org.ace.insurance.mobile.travelProposal.TravelProposalCriteria;
import org.ace.insurance.mobile.travelProposal.persistence.interfaces.IMobileTravelProposalDAO;
import org.ace.insurance.mobile.travelProposal.service.interfaces.IMobileTravelProposalService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "MobileTravelProposalService")
public class MobileTravelProposalService extends BaseService implements IMobileTravelProposalService {

	@Resource(name = "MobileTravelProposalDAO")
	private IMobileTravelProposalDAO mobileTravelDAO;

	public List<MTP002> findMobileTravelProposalByCriteria(TravelProposalCriteria tProposalCriteria) {
		List<MTP002> result = null;
		try {
			result = mobileTravelDAO.findByCriteria(tProposalCriteria);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find Mobile Travel Proposal", e);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MobileTravelProposal findMobileProposalByPolicyNo(String policyNo) {
		MobileTravelProposal result = null;
		try {
			result = mobileTravelDAO.findByPolicyNo(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find Mobile Travel Proposal By PolicyNo", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MobileTravelProposal findMobileProposalById(String id) {
		MobileTravelProposal result = null;
		try {
			result = mobileTravelDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find Mobile Travel Proposal By ProposalId", e);
		}
		return result;
	}

}
