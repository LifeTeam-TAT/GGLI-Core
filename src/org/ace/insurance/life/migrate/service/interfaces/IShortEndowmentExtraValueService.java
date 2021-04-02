package org.ace.insurance.life.migrate.service.interfaces;

import org.ace.insurance.life.migrate.ShortEndowmentExtraValue;

public interface IShortEndowmentExtraValueService {
	
	public void addNewShortEndowmentExtraValue(ShortEndowmentExtraValue shortEndowmentExtraValue);
	
	public double findExtraAmount(String id);
	
	public ShortEndowmentExtraValue findShortEndowmentExtraValueByPolicyNo(String shortTermPolicyNo);
	
	public void updateShortEndowmentExtraValue(ShortEndowmentExtraValue extraValue);
	
	public ShortEndowmentExtraValue findShortEndowmentExtraValue(String referenceNo);
}
