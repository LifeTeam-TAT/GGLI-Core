package org.ace.insurance.system.common.bancaLIC.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaLICDAO {

	public void insert(BancaLIC bancaLIC) throws DAOException;

	public void delete(BancaLIC bancaLIC) throws DAOException;

	public void update(BancaLIC bancaLIC) throws DAOException;

	public BancaLIC findById(String BancaLICId) throws DAOException;

	public List<BancaLIC> findAllBancaLIC() throws DAOException;

	public List<BancaLIC> findAllBancaLICActive();

	public boolean checkExistingBancaLIC(BancaLIC bancaLIC) throws DAOException;

}
