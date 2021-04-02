package org.ace.insurance.medical.numberReference.service.interfaces;

import org.ace.java.component.SystemException;

public interface INumberReferenceService {

	String findNewNumberReferenceByOldNumber(String oldNumber) throws SystemException;

	String findNewNumberReferenceByNewNumber(String newNumber) throws SystemException;

}