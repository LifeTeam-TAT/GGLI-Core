package org.ace.insurance.coinsurance.persistence.interfaces;

import java.util.List;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.CoinsuranceToOtherCompanyCriteria;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.java.component.persistence.exception.DAOException;

/**
 * This interface serves as the DAO to manipulate the Co-insurance object.
 * 
 * @author ACN
 * @version 1.0.0
 * @Date 2013/05/07
 */
public interface ICoinsuranceDAO {
	public List<Coinsurance> findCargoCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) throws DAOException;

	/**
	 * This method is used to add a new Co-insurance object to the database.
	 * 
	 * @param coinsurance
	 *            An instance of the newly created Co-insurance object which is
	 *            to be persisted.
	 * @param policy
	 *            An instance of the {@link IPolicy} which is to be updated as a
	 *            result of the Co-insurance creation.
	 * @throws DAOException
	 *             An exception occurs during the DB operation
	 */

	public List<Coinsurance> findByCargoPolicyNo(String policyNo) throws DAOException;

	public Coinsurance insert(Coinsurance coinsurance, IPolicy policy) throws DAOException;

	/**
	 * This method is used to update the existing Co-insurance object in the
	 * database.
	 * 
	 * @param coinsurance
	 *            An instance of the exiting Co-insurance object with the
	 *            updated data amended.
	 * @throws DAOException
	 *             An exception occurs during the DB operation
	 */
	public void update(Coinsurance coinsurance) throws DAOException;

	/**
	 * This method is used to remove the existing Co-insurance object in the
	 * database.
	 * 
	 * @param coinsurance
	 *            An instance of the exiting Co-insurance object which is to be
	 *            removed from the database.
	 * @throws DAOException
	 *             An exception occurs during the DB operation
	 */
	public void delete(Coinsurance coinsurance) throws DAOException;

	/**
	 * This method is used to retrieve by the given <code>ID</code> the existing
	 * Co-insurance object from the database.
	 * 
	 * @param id
	 *            The unique ID of the Co-insurance object
	 * @return An instance of the {@link Coinsurance}
	 * @throws DAOException
	 *             An exception occurs during the DB operation
	 */
	public Coinsurance findById(String id) throws DAOException;

	/**
	 * This method is used to retrieve all existing Co-insurance objects from
	 * the database.
	 * 
	 * @return A {@link List} of {@link Coinsurance} instances
	 * @throws DAOException
	 *             An exception occurs during the DB operation
	 */
	public List<Coinsurance> findAll() throws DAOException;

	/**
	 * This method is used to retrieve all existing Co-insurance objects for
	 * Invoice from the database.
	 * 
	 * @returnA {@link List} of {@link Coinsurance} instances
	 * @throws DAOException
	 *             An exception occurs during the DB operation
	 */
	public List<Coinsurance> findByPolicyNO(String policyNo) throws DAOException;

	/**
	 * This method is used to add Co-insurance Attachment of the existing
	 * Co-insurance object in the database.
	 * 
	 * @param coinsurance
	 *            An instance of the exiting Co-insurance object with the
	 *            attachemnt data amended.
	 * @throws DAOException
	 *             An exception occurs during the DB operation
	 */
	public void addAttachment(Coinsurance coinsurance) throws DAOException;

	public List<Coinsurance> findFireCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) throws DAOException;

	public List<Coinsurance> findMotorCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) throws DAOException;

	public Coinsurance findByPolicyByOutPolicyNo(String policyNo) throws DAOException;

	public Coinsurance findINPolicyNo(String policyNo) throws DAOException;
}
