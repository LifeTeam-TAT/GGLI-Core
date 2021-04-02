package org.ace.insurance.ws.customer.endpoint;

import java.util.List;

import org.ace.insurance.ws.common.exceptions.WSClientFault;
import org.ace.insurance.ws.common.model.versionindex.VersionIndexDto;
import org.ace.insurance.ws.customer.messages.AddNewCustomerRequest;
import org.ace.insurance.ws.customer.messages.AddNewCustomerResponse;
import org.ace.insurance.ws.customer.messages.BankBranchUpdateRequest;
import org.ace.insurance.ws.customer.messages.BankBranchUpdateResponse;
import org.ace.insurance.ws.customer.messages.BranchUpdateRequest;
import org.ace.insurance.ws.customer.messages.BranchUpdateResponse;
import org.ace.insurance.ws.customer.messages.CountryUpdateRequest;
import org.ace.insurance.ws.customer.messages.CountryUpdateResponse;
import org.ace.insurance.ws.customer.messages.GetCustomerRequirementsRequest;
import org.ace.insurance.ws.customer.messages.GetCustomerRequirementsResponse;
import org.ace.insurance.ws.customer.messages.IndustryUpdateRequest;
import org.ace.insurance.ws.customer.messages.IndustryUpdateResponse;
import org.ace.insurance.ws.customer.messages.OccupationUpdateRequest;
import org.ace.insurance.ws.customer.messages.OccupationUpdateResponse;
import org.ace.insurance.ws.customer.messages.QualificationUpdateRequest;
import org.ace.insurance.ws.customer.messages.QualificationUpdateResponse;
import org.ace.insurance.ws.customer.messages.RelationshipUpdateRequest;
import org.ace.insurance.ws.customer.messages.RelationshipUpdateResponse;
import org.ace.insurance.ws.customer.messages.ReligionUpdateRequest;
import org.ace.insurance.ws.customer.messages.ReligionUpdateResponse;
import org.ace.insurance.ws.customer.messages.TownshipUpdateRequest;
import org.ace.insurance.ws.customer.messages.TownshipUpdateResponse;
import org.ace.insurance.ws.customer.model.BankBranchUpdate;
import org.ace.insurance.ws.customer.model.BranchUpdate;
import org.ace.insurance.ws.customer.model.CountryUpdate;
import org.ace.insurance.ws.customer.model.CustomerDto;
import org.ace.insurance.ws.customer.model.IndustryUpdate;
import org.ace.insurance.ws.customer.model.OccupationUpdate;
import org.ace.insurance.ws.customer.model.QualificationUpdate;
import org.ace.insurance.ws.customer.model.RelationshipUpdate;
import org.ace.insurance.ws.customer.model.ReligionUpdate;
import org.ace.insurance.ws.customer.model.TownshipUpdate;
import org.ace.insurance.ws.customer.service.interfaces.ICustomerWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CustomerEndPoint {
	private static final String CUSTOMER_SERVICE_NAMESPACE = "http://www.ace.org/ggip/customerService";	
	
	@Autowired
	private ICustomerWebService customerWebService;
	
	@PayloadRoot(localPart = "AddNewCustomerRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public AddNewCustomerResponse addNewCustomer(@RequestPayload AddNewCustomerRequest request) {		
		AddNewCustomerResponse response = new AddNewCustomerResponse();
//		try {
			CustomerDto customer = request.getCustomer();
			customerWebService.addNewCustomer(customer);
			response.setResult(1);
//		} catch (CustomerWSException ce) {
//			response.setResult(0);
//			response.setMessage(ce.getMessage());
//		}
		return response;
	}
	
	@PayloadRoot(localPart = "GetCustomerRequirementsRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public GetCustomerRequirementsResponse getCustomerRequirements(@RequestPayload GetCustomerRequirementsRequest request) {	
		GetCustomerRequirementsResponse response = new GetCustomerRequirementsResponse();		
		response.setCustomerRequirements(customerWebService.getCustomerRequirements());
		return response;
	}
	
	@PayloadRoot(localPart = "BankBranchUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public BankBranchUpdateResponse bankBranchUpdate(@RequestPayload BankBranchUpdateRequest request) {		
		BankBranchUpdateResponse response = customerWebService.getBankBranchUpdateList(request.getVersionNo());
//		BankBranchUpdateResponse response = new BankBranchUpdateResponse();
//		response.setBankBranchUpdateList(result);		
		return response;
	}
	
	@PayloadRoot(localPart = "BranchUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public BranchUpdateResponse branchUpdate(@RequestPayload BranchUpdateRequest request) {	
		BranchUpdateResponse response = customerWebService.getBranchUpdateList(request.getVersionNo());
//		BranchUpdateResponse response = new BranchUpdateResponse();
//		response.setBranchUpdateList(result);		
		return response;
	}
	
	@PayloadRoot(localPart = "CountryUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public CountryUpdateResponse countryUpdate(@RequestPayload CountryUpdateRequest request) {	
		CountryUpdateResponse response = customerWebService.getCountryUpdateList(request.getVersionNo());
//		CountryUpdateResponse response = new CountryUpdateResponse();
//		response.setCountryUpdateList(result);
		return response;
	}
	
	@PayloadRoot(localPart = "IndustryUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public IndustryUpdateResponse industryUpdate(@RequestPayload IndustryUpdateRequest request) {	
		IndustryUpdateResponse response = customerWebService.getIndustryUpdateList(request.getVersionNo());
//		IndustryUpdateResponse response = new IndustryUpdateResponse();		
//		response.setIndustryUpdateList(result);
		return response;
	}
	
	@PayloadRoot(localPart = "OccupationUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public OccupationUpdateResponse occupationUpdate(@RequestPayload OccupationUpdateRequest request) {	
		OccupationUpdateResponse response = customerWebService.getOccupationUpdateList(request.getVersionNo());
//		OccupationUpdateResponse response = new OccupationUpdateResponse();		
//		response.setOccupationUpdateList(result);
		return response;
	}
	
	@PayloadRoot(localPart = "QualificationUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public QualificationUpdateResponse qualificationUpdate(@RequestPayload QualificationUpdateRequest request) {	
		QualificationUpdateResponse response = customerWebService.getQualificationUpdateList(request.getVersionNo());
//		QualificationUpdateResponse response = new QualificationUpdateResponse();		
//		response.setQualificationUpdateList(result);
		return response;
	}
	
	@PayloadRoot(localPart = "RelationshipUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public RelationshipUpdateResponse relationshipUpdate(@RequestPayload RelationshipUpdateRequest request) {	
		RelationshipUpdateResponse response = customerWebService.getRelationshipUpdateList(request.getVersionNo());
//		RelationshipUpdateResponse response = new RelationshipUpdateResponse();		
//		response.setRelationshipUpdateList(result);
		return response;
	}
	
	@PayloadRoot(localPart = "ReligionUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public ReligionUpdateResponse religionUpdate(@RequestPayload ReligionUpdateRequest request) {	
		ReligionUpdateResponse response = customerWebService.getReligionUpdateList(request.getVersionNo());
//		ReligionUpdateResponse response = new ReligionUpdateResponse();
//		response.setReligionUpdateList(result);
		return response;
	}
	
	@PayloadRoot(localPart = "TownshipUpdateRequest", namespace = CUSTOMER_SERVICE_NAMESPACE)
	@ResponsePayload
	public TownshipUpdateResponse townshipUpdate(@RequestPayload TownshipUpdateRequest request) {	
		TownshipUpdateResponse response = customerWebService.getTownshipUpdateList(request.getVersionNo());
//		TownshipUpdateResponse response = new TownshipUpdateResponse();
//		response.setTownshipUpdateList(result);
		return response;
	}
	
	//===================set DI
	public void setCustomerService(ICustomerWebService customerWebService) {
		this.customerWebService = customerWebService;
	}
		
}
