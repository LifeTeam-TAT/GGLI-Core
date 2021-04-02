package org.ace.insurance.groupfarmer.proposal.persistance.interfaces;

import java.util.List;

import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposalDTO;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IGroupfarmerProposalDAO {
	public GroupFarmerProposal insert(GroupFarmerProposal groupFarmerProposal) throws DAOException;

	public void update(GroupFarmerProposal groupFarmerProposal) throws DAOException;

	public void delete(GroupFarmerProposal groupFarmerProposal) throws DAOException;

	public GroupFarmerProposal findById(String id) throws DAOException;

	public List<GroupFarmerProposalDTO> findAllGroupFarmerProposalByisPaymentComplete(EnquiryCriteria criteria) throws DAOException;

	public List<GroupFarmerProposalDTO> findGroupFarmerProposalByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException;

	public List<GroupFarmerProposal> finGroupFarmerProposalByPOByReceiptNo(String receiptNo, String bpmsReceiptNo) throws DAOException;

}
