package org.ace.insurance.system.common.bank.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bank.Coa;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICoaDAO {

	public List<Coa> findAll() throws DAOException;
}
