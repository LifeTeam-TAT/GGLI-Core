package org.ace.insurance.life.bancassurance.policy.persistence.interfaces;

import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaassurancePolicyDAO {

	public void insert(BancaassurancePolicy bancaassurancePolicy);

	public void delete(BancaassurancePolicy bancaassurancePolicy);

	public void update(BancaassurancePolicy bancaassurancePolicy);

	public BancaassurancePolicy findById(String id) throws DAOException;

	public BancaassurancePolicy findBancaassurancePolicyByLifepolicyId(String lifePolicyId);

	public BancaassurancePolicy findBancaassurancePolicyByMedicalpolicyId(String medicalPolicyId);

	public BancaassurancePolicy findBancaassurancePolicyByPersonTravelpolicyId(String personTravelPolicyId);

	public BancaassurancePolicy findBancaassurancePolicyByTravelproposalId(String travelProposalId);

}
