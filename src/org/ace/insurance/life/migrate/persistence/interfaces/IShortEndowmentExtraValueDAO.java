package org.ace.insurance.life.migrate.persistence.interfaces;

import org.ace.insurance.life.migrate.ShortEndowmentExtraValue;
import org.ace.java.component.persistence.exception.DAOException;

public interface IShortEndowmentExtraValueDAO {

	void insert(ShortEndowmentExtraValue shortEndowmentExtraValue);

	void updateShortTermPolicyNo(String id, String policyNo);
	
	public double getExtraAmountByReferencenNo(String referenceNo);
	
	public ShortEndowmentExtraValue getShortEndowmentExtraValueByPolicyNo(String shortTermPolicyNo);
	
	public void updateShortEndowmentExtraValue(ShortEndowmentExtraValue extraValue) throws DAOException;
	
	public ShortEndowmentExtraValue findShortEndowmentExtraValue(String referenceNo) throws DAOException;


}
