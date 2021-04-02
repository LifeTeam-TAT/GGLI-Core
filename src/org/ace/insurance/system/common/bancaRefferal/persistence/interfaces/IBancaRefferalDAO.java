package org.ace.insurance.system.common.bancaRefferal.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaRefferalDAO {

	public void insert(BancaRefferal bancarefferal) throws DAOException;

	public void delete(BancaRefferal bancarefferal) throws DAOException;

	public void update(BancaRefferal bancarefferal) throws DAOException;

	public BancaRefferal findById(String BancarefferalId) throws DAOException;

	public List<BancaRefferal> findAllBancarefferal() throws DAOException;
	
	public List<BancaRefferal> findAllBancaRefferalByAgentLicenseNo() throws DAOException;

	public List<BancaRefferal> findAllBancarefferalActive(String branchId) throws DAOException;

	public List<BancaRefferal> findAllBancarefferalByOTC(String branchId) throws DAOException;

	public boolean checkExistingBancaRefferal(BancaRefferal bancarefferal) throws DAOException;
}
