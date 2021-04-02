package org.ace.insurance.coinsurance.service.interfaces;

import java.util.List;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.CoinsuranceToOtherCompanyCriteria;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.system.common.branch.Branch;

/**
 * This interface serves as the service layer to manipulate the Co-insurance
 * object.
 * 
 * @author ACN
 * @version 1.0.0
 * @Date 2013/05/07
 */
public interface ICoinsuranceService {
	public List<Coinsurance> findCargoCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria);

	public List<Coinsurance> findByCargoPolicyNo(String policyNo);

	/**
	 * This method adds the given {@link Policy} to the repository;
	 * 
	 * @param policy
	 *            An instance of the {@link IPolicy} which is to be updated as a
	 *            result of the Co-insurance creation.
	 * @return A newly created {@link Coinsurances} instance with the generated
	 *         unique ID populated
	 */

	public List<Coinsurance> addNewCargoCoinsurances(IPolicy policy, double agentCommission);

	// For InCoinsurance
	public Coinsurance addNewCoinsurance(Coinsurance coinsurance, IPolicy policy, WorkFlowDTO workFlowDTO);

	// For OutCoinsurance
	public List<Coinsurance> addNewCoinsurances(IPolicy policy, double agentCommission);

	public void confirmCoinsurance(Coinsurance coinsurance);

	/**
	 * This method updates the given {@link Coinsurance} in the repository;
	 * 
	 * @param coinsurance
	 *            An instance of the Co-insurance object
	 */
	public void update(Coinsurance coinsurance);

	/**
	 * This method removes the given {@link Coinsurance} from the repository;
	 * 
	 * @param coinsurance
	 *            An instance of the Co-insurance object
	 */
	public void delete(Coinsurance coinsurance);

	/**
	 * This method is used to retrieve by the given <code>ID</code> the existing
	 * Co-insurance object from the repository .
	 * 
	 * @param id
	 *            The unique ID of the Co-insurance object
	 * @return An instance of the {@link Coinsurance}
	 */
	public Coinsurance findById(String id);

	/**
	 * This method is used to retrieve all existing Co-insurance objects from
	 * the repository.
	 * 
	 * @return A {@link List} of {@link Coinsurance} instances
	 */
	public List<Coinsurance> findAll();

	/**
	 * This method is used to retrieve all existing Co-insurance objects by the
	 * given <code>PolicyID</code> from the repository.
	 * 
	 * @return A {@link List} of {@link Coinsurance} instances
	 */
	public List<Coinsurance> findByPolicyNo(String policyNo);

	/**
	 * This method add the attachment of given {@link Coinsurance} in the
	 * repository;
	 * 
	 * @param coinsurance
	 *            An instance of the Co-insurance object
	 */
	public void addCoinsuranceAttachment(Coinsurance coinsurance);

	public void generateOutCoinsuranceCommission(List<Coinsurance> coinsuranceList, String dirPath, String fileName, String premiumtype, String insurerName, Branch branch)
			throws Exception;

	public List<Coinsurance> findFireCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria);

	public List<Coinsurance> findMotorCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria);

	public Coinsurance findByPolicyByOutPolicyNo(String policyNo);

	public Coinsurance findINPolicyNo(String policyNo);

}
