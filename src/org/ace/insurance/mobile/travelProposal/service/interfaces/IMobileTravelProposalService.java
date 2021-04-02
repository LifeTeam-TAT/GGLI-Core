package org.ace.insurance.mobile.travelProposal.service.interfaces;

import java.util.List;

import org.ace.insurance.mobile.travelProposal.MTP002;
import org.ace.insurance.mobile.travelProposal.MobileTravelProposal;
import org.ace.insurance.mobile.travelProposal.TravelProposalCriteria;

public interface IMobileTravelProposalService {

	public List<MTP002> findMobileTravelProposalByCriteria(TravelProposalCriteria tProposalCriteria);

	public MobileTravelProposal findMobileProposalByPolicyNo(String policyNo);

	public MobileTravelProposal findMobileProposalById(String id);

}
