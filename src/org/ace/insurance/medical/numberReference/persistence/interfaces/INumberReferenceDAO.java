package org.ace.insurance.medical.numberReference.persistence.interfaces;

import org.ace.java.component.persistence.exception.DAOException;

public interface INumberReferenceDAO {

	String findNewNumberReferenceByOldNumber(String oldNumber) throws DAOException;

	String findNewNumberReferenceByNewNumber(String newNumber) throws DAOException;

}