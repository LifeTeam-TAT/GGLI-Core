package org.ace.insurance.life.snakebite.persistence.interfaces;

import java.util.List;

import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDTO;
import org.ace.insurance.life.snakebite.SnakeBitePolicyNoCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicySearch;
import org.ace.java.component.persistence.exception.DAOException;

public interface ISnakeBitePolicyDAO {
	public SnakeBitePolicy insert(SnakeBitePolicy snakeBitePolicy) throws DAOException;

	public SnakeBitePolicy update(SnakeBitePolicy snakeBitePolicy) throws DAOException;

	public void delete(SnakeBitePolicy snakeBitePolicy) throws DAOException;

	public SnakeBitePolicy findById(String id) throws DAOException;

	public List<SnakeBitePolicy> findAll() throws DAOException;

	public List<SnakeBitePolicy> findByIdList(List<String> proposalIdList) throws DAOException;

	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException;

	public List<SnakeBitePolicy> findSnakeBitePolicyByCriteria(SnakeBitePolicyCriteria snakeBitePolicyCriteria) throws DAOException;

	public List<SnakeBitePolicy> findSnakeBitePolicyByReceiptNo(String receiptNo) throws DAOException;

	public SnakeBitePolicyDTO findSnakeBitePolicyForPaymentDashboard(String referenceNo) throws DAOException;

	public List<String> findBookNo() throws DAOException;

	public List<SnakeBitePolicySearch> findSnakeBitePolicyNoByCriteria(SnakeBitePolicyNoCriteria criteria) throws DAOException;

	public SnakeBitePolicy findSnakeBitePolicyByPolicyNo(String snakeBitePolicyNo) throws DAOException;
}
