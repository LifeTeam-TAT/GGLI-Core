package org.ace.insurance.claim.disabilityPart.persistence.interfaces;

import java.util.List;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.java.component.persistence.exception.DAOException;

public interface IDisabilityPartDAO {

	public void insert(DisabilityPart disabilityPart) throws DAOException;

	public void update(DisabilityPart disabilityPart) throws DAOException;

	public void delete(DisabilityPart disabilityPart) throws DAOException;

	public DisabilityPart findById(String id) throws DAOException;

	public List<DisabilityPart> findAll() throws DAOException;

	boolean isAlreadyExist(DisabilityPart disabilityPart) throws DAOException;
}
