package org.ace.insurance.system.common.bancaMethod.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaMethodDAO {

	public void insert(BancaMethod banca) throws DAOException;

	public void delete(BancaMethod banca) throws DAOException;

	public void update(BancaMethod banca) throws DAOException;

	public BancaMethod findById(String BancaId) throws DAOException;

	public List<BancaMethod> findAllBanca();

	public boolean checkExistingBanca(BancaMethod banca) throws DAOException;
}
