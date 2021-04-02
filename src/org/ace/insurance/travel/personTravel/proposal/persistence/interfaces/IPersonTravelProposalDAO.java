package org.ace.insurance.travel.personTravel.proposal.persistence.interfaces;

import java.util.List;

import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IPersonTravelProposalDAO {
	public void insert(PersonTravelProposal personTravelProposal) throws DAOException;

	public void update(PersonTravelProposal personTravelProposal) throws DAOException;

	public void delete(PersonTravelProposal personTravelProposal) throws DAOException;

	public List<PersonTravelProposal> findAll() throws DAOException;

	public PersonTravelProposal findById(String id) throws DAOException;

	public void updateStatus(String status, String id) throws DAOException;

	public List<PTPL001> findByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException;

	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException;
}
