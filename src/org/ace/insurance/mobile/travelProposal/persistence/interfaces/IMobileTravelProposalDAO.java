package org.ace.insurance.mobile.travelProposal.persistence.interfaces;

import java.util.List;

import org.ace.insurance.mobile.travelProposal.MTP002;
import org.ace.insurance.mobile.travelProposal.MobileTravelProposal;
import org.ace.insurance.mobile.travelProposal.TravelProposalCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IMobileTravelProposalDAO {

	public MobileTravelProposal update(MobileTravelProposal mobileTravelProposal) throws DAOException;

	public List<MTP002> findByCriteria(TravelProposalCriteria travelProposalCriteria) throws DAOException;

	public MobileTravelProposal findByPolicyNo(String policyNo) throws DAOException;

	public MobileTravelProposal findById(String id) throws DAOException;

}
