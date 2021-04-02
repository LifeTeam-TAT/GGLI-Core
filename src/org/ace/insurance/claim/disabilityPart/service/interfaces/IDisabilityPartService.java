package org.ace.insurance.claim.disabilityPart.service.interfaces;

import java.util.List;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;


public interface IDisabilityPartService {
	public void addNewDisabilityPart(DisabilityPart disabilityPart);

	public void updateDisabilityPart(DisabilityPart disabilityPart);

	public void deleteDisabilityPart(DisabilityPart disabilityPart);

	public List<DisabilityPart> findAllDisabilityPart();

	public DisabilityPart findDisabilityPartById(String id);
}
