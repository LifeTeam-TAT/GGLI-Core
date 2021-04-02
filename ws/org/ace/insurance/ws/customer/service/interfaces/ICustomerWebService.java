package org.ace.insurance.ws.customer.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.ws.common.exceptions.WSClientFault;
import org.ace.insurance.ws.common.model.versionindex.VersionIndexDto;
import org.ace.insurance.ws.customer.messages.BankBranchUpdateResponse;
import org.ace.insurance.ws.customer.messages.BranchUpdateResponse;
import org.ace.insurance.ws.customer.messages.CountryUpdateResponse;
import org.ace.insurance.ws.customer.messages.IndustryUpdateResponse;
import org.ace.insurance.ws.customer.messages.OccupationUpdateResponse;
import org.ace.insurance.ws.customer.messages.QualificationUpdateResponse;
import org.ace.insurance.ws.customer.messages.RelationshipUpdateResponse;
import org.ace.insurance.ws.customer.messages.ReligionUpdateResponse;
import org.ace.insurance.ws.customer.messages.TownshipUpdateResponse;
import org.ace.insurance.ws.customer.model.BankBranchUpdate;
import org.ace.insurance.ws.customer.model.BranchUpdate;
import org.ace.insurance.ws.customer.model.CountryUpdate;
import org.ace.insurance.ws.customer.model.CustomerDto;
import org.ace.insurance.ws.customer.model.CustomerRequirements;
import org.ace.insurance.ws.customer.model.IndustryUpdate;
import org.ace.insurance.ws.customer.model.OccupationUpdate;
import org.ace.insurance.ws.customer.model.QualificationUpdate;
import org.ace.insurance.ws.customer.model.RelationshipUpdate;
import org.ace.insurance.ws.customer.model.ReligionUpdate;
import org.ace.insurance.ws.customer.model.TownshipUpdate;

public interface ICustomerWebService {	
	
	public void addNewCustomer(CustomerDto customer);
	public CustomerRequirements getCustomerRequirements();
	
	/*
	public List<BankBranchUpdate> getBankBranchUpdateList(int versionNo);
	public List<BranchUpdate> getBranchUpdateList(int versionNo);
	public List<CountryUpdate> getCountryUpdateList(int versionNo);
	public List<IndustryUpdate> getIndustryUpdateList(int versionNo);
	public List<OccupationUpdate> getOccupationUpdateList(int versionNo);
	public List<QualificationUpdate> getQualificationUpdateList(int versionNo);
	public List<RelationshipUpdate> getRelationshipUpdateList(int versionNo);
	public List<ReligionUpdate> getReligionUpdateList(int versionNo);
	public List<TownshipUpdate> getTownshipUpdateList(int versionNo);
	*/
	public BankBranchUpdateResponse getBankBranchUpdateList(int versionNo);
	public BranchUpdateResponse getBranchUpdateList(int versionNo);
	public CountryUpdateResponse getCountryUpdateList(int versionNo);
	public IndustryUpdateResponse getIndustryUpdateList(int versionNo);
	public OccupationUpdateResponse getOccupationUpdateList(int versionNo);
	public QualificationUpdateResponse getQualificationUpdateList(int versionNo);
	public RelationshipUpdateResponse getRelationshipUpdateList(int versionNo);
	public ReligionUpdateResponse getReligionUpdateList(int versionNo);
	public TownshipUpdateResponse getTownshipUpdateList(int versionNo);
}
