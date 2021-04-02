package org.ace.insurance.life.claim.persistence.interfaces;

import java.util.List;

import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeClaimSurvey;
import org.ace.insurance.web.manage.life.claim.LifeDisabilityPaymentCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface ILifeClaimProposalDAO {

	public void insert(LifeClaimProposal claimProposal) throws DAOException;

	public void update(LifeClaimProposal claimProposal) throws DAOException;

	public void delete(LifeClaimProposal claimProposal) throws DAOException;

	public LifeClaimProposal findById(String id) throws DAOException;

	public void insert(LifeClaimSurvey lifeClaimSurvey) throws DAOException;

	public List<DisabilityLifeClaim> findDisabilityLifeClaimByLifeClaimProposalNo(LifeDisabilityPaymentCriteria criteria) throws DAOException;

	public void update(DisabilityLifeClaim disabilityLifeClaim) throws DAOException;

}
