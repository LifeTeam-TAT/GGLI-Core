package org.ace.insurance.life.bancassurance.proposal.persistence.interfaces;

import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaassuranceProposalDAO {

	public void insert(BancaassuranceProposal bancaassuranceProposal);

	public void delete(BancaassuranceProposal bancaassuranceProposal);

	public void update(BancaassuranceProposal bancaassuranceProposal);

	public BancaassuranceProposal findById(String id) throws DAOException;

	public BancaassuranceProposal findBancaassuranceProposalByLifeproposalId(String lifeProposalId);

	public BancaassuranceProposal findBancaassuranceProposalByMedicalproposalId(String medicalProposalId);

	public BancaassuranceProposal findBancaassuranceProposalByPersonTravelproposalId(String personTravelProposalId);

	public BancaassuranceProposal findBancaassuranceProposalByTravelproposalId(String travelProposalId);

	BancaassuranceProposal findBancaassuranceProposalByPersonTravelProposalId(String personTravelId);

}
