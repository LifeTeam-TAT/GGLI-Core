package org.ace.insurance.life.bancassurance.policy.service.interfaces;

import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;

public interface IBancaassurancePolicyService {

	public void insert(BancaassurancePolicy bancaassurancePolicy);

	public void delete(BancaassurancePolicy bancaassurancePolicy);

	public void update(BancaassurancePolicy bancaassurancePolicy);

	public BancaassurancePolicy findBancaassurancePolicyByLifepolicyId(String lifePolicyId);

	public BancaassurancePolicy findBancaassurancePolicyByMedicalpolicyId(String medicalPolicyId);

	public BancaassurancePolicy findBancaassurancePolicyByPersonTravelpolicyId(String personTravelPolicyId);

	public BancaassurancePolicy findBancaassurancePolicyByTravelproposalId(String TravelProposalId);

}
