package org.ace.insurance.life.bancassurance.proposalhistory.persistence.interfaces;

import org.ace.insurance.life.bancassurance.proposalhistory.BancaassuranceProposalHistory;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaassuranceProposalHistoryDAO {

	public BancaassuranceProposalHistory insert(BancaassuranceProposalHistory bancaassuranceProposalHistory) throws DAOException;

	public void update(BancaassuranceProposalHistory bancaassuranceProposalHistory) throws DAOException;

	public void delete(BancaassuranceProposalHistory bancaassuranceProposalHistory) throws DAOException;

	public BancaassuranceProposalHistory findBancaassuranceProposalByLifeproposalId(String lifeProposalId) throws DAOException;;

	public BancaassuranceProposalHistory findBancaassuranceProposalByMedicalproposalId(String medicalProposalId) throws DAOException;;

	public BancaassuranceProposalHistory findBancaassuranceProposalByPersonTravelproposalId(String personTravelProposalId) throws DAOException;;

	public BancaassuranceProposalHistory findBancaassuranceProposalByTravelproposalId(String travelProposalId) throws DAOException;;
}
