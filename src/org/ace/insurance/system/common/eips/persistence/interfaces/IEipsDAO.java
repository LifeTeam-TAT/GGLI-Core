package org.ace.insurance.system.common.eips.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.eips.Eips;
import org.ace.java.component.persistence.exception.DAOException;

public interface IEipsDAO {

	void save(Eips eips) throws DAOException;

	void delete(Eips eips) throws DAOException;

	Eips findById(String id) throws DAOException;

	List<Eips> findAll() throws DAOException;
}
