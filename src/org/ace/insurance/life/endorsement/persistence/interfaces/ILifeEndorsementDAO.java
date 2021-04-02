package org.ace.insurance.life.endorsement.persistence.interfaces;

import java.util.List;

import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.java.component.persistence.exception.DAOException;

public interface ILifeEndorsementDAO {
	public LifeEndorseInfo insert(LifeEndorseInfo lifeEndorseInfo) throws DAOException;
	
	public void deleteLifeEndorseInfo(LifeEndorseInfo lifeEndorseInfo) throws DAOException;
	
	public void updateEndorsePolicyReferenceNo(String oldPolicyId, String newPolicyId) throws DAOException;
	
	public LifeEndorseInfo findLastLifeEndorseInfoByProposalNo(String policyNo) throws DAOException;
	
	public LifeEndorseInfo findBySourcePolicyReferenceNo(String policyId) throws DAOException;
	
	public LifeEndorseInfo findByEndorsePolicyReferenceNo(String policyId) throws DAOException;
	
	public LifeProposal insert(LifeProposal lifeProposal) throws DAOException;

	public void update(LifeProposal lifeProposal) throws DAOException;

	public void delete(LifeProposal lifeProposal) throws DAOException;

	public void addAttachment(LifeProposal lifeProposal) throws DAOException;

	public LifeProposal findById(String id) throws DAOException;
	
	public void updateInsuredPersonApprovalInfo(List<ProposalInsuredPerson> proposalInsuredPersonList) throws DAOException;
	
	public void insertSurvey(LifeSurvey lifeSurvey) throws DAOException;
	
	public void updateInsuPersonMedicalStatus(List<ProposalInsuredPerson> proposalInsuredPersonList) throws DAOException;
	
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException;
	
	public void updateStatus(String status, String id) throws DAOException;

}
