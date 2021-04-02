package org.ace.insurance.ws.customer.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ace.insurance.aspects.versionref.UpdateOperationType;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.country.COUNTRY001;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.country.service.interfaces.ICountryService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.industry.service.interfaces.IIndustryService;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.qualification.service.interfaces.IQualificationService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.religion.service.interfaces.IReligionService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.versionref.VersionRef;
import org.ace.insurance.versionref.VersionRefDTO;
import org.ace.insurance.versionref.service.interfaces.IVersionRefService;
import org.ace.insurance.ws.common.exceptions.WSClientFault;
import org.ace.insurance.ws.common.exceptions.WSServerFault;
import org.ace.insurance.ws.common.model.VersionRefDomainName;
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
import org.ace.insurance.ws.customer.model.BankBranchDto;
import org.ace.insurance.ws.customer.model.BankBranchUpdate;
import org.ace.insurance.ws.customer.model.BranchDto;
import org.ace.insurance.ws.customer.model.BranchUpdate;
import org.ace.insurance.ws.customer.model.CountryDto;
import org.ace.insurance.ws.customer.model.CountryUpdate;
import org.ace.insurance.ws.customer.model.CustomerDto;
import org.ace.insurance.ws.customer.model.CustomerRequirements;
import org.ace.insurance.ws.customer.model.FamilyInfoDto;
import org.ace.insurance.ws.customer.model.IndustryDto;
import org.ace.insurance.ws.customer.model.IndustryUpdate;
import org.ace.insurance.ws.customer.model.OccupationDto;
import org.ace.insurance.ws.customer.model.OccupationUpdate;
import org.ace.insurance.ws.customer.model.QualificationDto;
import org.ace.insurance.ws.customer.model.QualificationUpdate;
import org.ace.insurance.ws.customer.model.RelationshipDto;
import org.ace.insurance.ws.customer.model.RelationshipUpdate;
import org.ace.insurance.ws.customer.model.ReligionDto;
import org.ace.insurance.ws.customer.model.ReligionUpdate;
import org.ace.insurance.ws.customer.model.TownshipDto;
import org.ace.insurance.ws.customer.model.TownshipUpdate;
import org.ace.insurance.ws.customer.service.interfaces.ICustomerWebService;
import org.ace.java.component.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Service("ICustomerWebService")
public class CustomerWebService implements ICustomerWebService {
	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private ICustomerService customerService;
	@Autowired
	private ICountryService countryService;
	@Autowired
	private ITownshipService townshipService;
	@Autowired
	private IReligionService religionService;
	@Autowired
	private IBranchService branchService;
	@Autowired
	private IBankBranchService bankBranchService;
	@Autowired
	private IIndustryService industryService;
	@Autowired
	private IQualificationService qualificationService;
	@Autowired
	private IOccupationService occupationService;
	@Autowired
	private IRelationShipService relationshipService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IVersionRefService versionRefService;

	private static String CUSTOMER_VERSION_REF = VersionRefDomainName.Customer.class.getSimpleName();

	// ---------------------------------------
	// -----BEGIN web service methods
	// ---------------------------------------
	public Customer findCustomer(String id) {
		// TODO auto-generated method stub
		return customerService.findCustomerById(id);
	}

	public void addNewCustomer(CustomerDto customer) {
		logger.debug("BEGIN - addNewCustomer() with customer name :" + customer.getName());
		String validationRes = validateCustomerInfo(customer);
		if (!validationRes.equals("OK")) {
			validationRes = validationRes.substring(0, validationRes.lastIndexOf(",")) + "]";
			throw new WSClientFault("Required Values: [" + validationRes);
		}
		String checkRet = checkCustomerDataConsistence(customer);
		if (!checkRet.equals("OK")) {
			checkRet = checkRet.substring(0, checkRet.lastIndexOf(",")) + "]";
			throw new WSServerFault("Not found: [" + checkRet);
		}
		try {
			if (userService.authenticate("wsuser", "wsuser")) {
				User user = userService.findUser("wsuser");
				customerService.addNewCustomer(customer, user);
			}
		} catch (SystemException e) {
			logger.error("Webservice Client - Failed to create customer.", e);
			throw new WSServerFault("Failed to create customer.");
		}
		logger.debug("END - addNewCustomer() with customer name :" + customer.getName());
	}

	public CustomerRequirements getCustomerRequirements() {
		logger.debug("BEGIN - getCustomerRequirements()");
		CustomerRequirements ret = null;
		try {
			List<BranchDto> branch = new ArrayList<BranchDto>();
			for (Branch br : branchService.findAllBranch()) {
				branch.add(new BranchDto(br));
			}

			List<BankBranchDto> bankBranch = new ArrayList<BankBranchDto>();
			for (BankBranch bb : bankBranchService.findAllBankBranch()) {
				bankBranch.add(new BankBranchDto(bb));
			}

			List<QualificationDto> qualification = new ArrayList<QualificationDto>();
			for (Qualification qul : qualificationService.findAllQualification()) {
				qualification.add(new QualificationDto(qul));
			}

			List<ReligionDto> religion = new ArrayList<ReligionDto>();
			for (Religion rel : religionService.findAllReligion()) {
				religion.add(new ReligionDto(rel));
			}

			List<OccupationDto> occupation = new ArrayList<OccupationDto>();
			for (Occupation occu : occupationService.findAllOccupation()) {
				occupation.add(new OccupationDto(occu));
			}

			List<IndustryDto> industry = new ArrayList<IndustryDto>();
			for (Industry ind : industryService.findAllIndustry()) {
				industry.add(new IndustryDto(ind));
			}

			List<CountryDto> country = new ArrayList<CountryDto>();
			for (COUNTRY001 coun : countryService.findCountry()) {
				country.add(new CountryDto(coun));
			}

			List<TownshipDto> township = new ArrayList<TownshipDto>();
			for (Township town : townshipService.findAllTownship()) {
				township.add(new TownshipDto(town));
			}

			List<RelationshipDto> relationship = new ArrayList<RelationshipDto>();
			for (RelationShip rela : relationshipService.findAllRelationShip()) {
				relationship.add(new RelationshipDto(rela));
			}

			List<VersionIndexDto> versionIndexes = new ArrayList<VersionIndexDto>();
			for (VersionRefDTO verRef : versionRefService.findVersionRefIndexesByGroup(CUSTOMER_VERSION_REF)) {
				versionIndexes.add(new VersionIndexDto(verRef.getEntityName(), verRef.getVersionNo()));
			}
			versionIndexes = checkAndPrepareRequiredVersionIndexes(versionIndexes);

			// create customer requirements information
			ret = new CustomerRequirements(branch, bankBranch, qualification, religion, occupation, industry, country, township, relationship, toStringList(Gender.class),
					toStringList(IdType.class), toStringList(MaritalStatus.class), versionIndexes);

		} catch (IllegalArgumentException iae) {
			logger.error("Webservice Client - Failed to prepare version indexes for depended entity names of Customer ", iae);
			throw new WSServerFault("Failed to prepare version indexes.");
		} catch (IllegalAccessException ie) {
			logger.error("Webservice Client - Failed to prepare version indexes for depended entity names of Customer ", ie);
			throw new WSServerFault("Failed to prepare version indexes.");
		} catch (Exception e) {
			logger.error("Webservice Client - Failed to get customer requirement information.", e);
			throw new WSServerFault("Failed to get customer requirement information.");
		}
		logger.debug("END - getCustomerRequirements()");
		return ret;
	}

	@Override
	public BankBranchUpdateResponse getBankBranchUpdateList(int versionNo) {
		logger.debug("BEGIN - getBankBranchUpdateList() with versionNo :" + versionNo);
		List<BankBranchUpdate> bankBranchUpdateList = new ArrayList<BankBranchUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(BankBranch.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			BankBranchUpdate bankBranchUpdate = new BankBranchUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				bankBranchUpdate.setBankBranch(new BankBranchDto(versionRef.getEntityId()));
			} else {
				BankBranch bankBranch = bankBranchService.findBankBranchById(versionRef.getEntityId());
				bankBranchUpdate.setBankBranch(new BankBranchDto(bankBranch));
			}
			bankBranchUpdate.setOperation(versionRef.getOperation());
			bankBranchUpdateList.add(bankBranchUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		BankBranchUpdateResponse response = new BankBranchUpdateResponse();
		response.setBankBranchUpdateList(bankBranchUpdateList);
		System.out.println("bankBranchUpdateList:" + bankBranchUpdateList.size());
		response.setLatestVersionNo(maxVersionNo);
		System.out.println("maxVersionNo" + maxVersionNo);

		logger.debug("END - getBankBranchUpdateList() with versionNo :" + versionNo);
		return response;
	}

	@Override
	public BranchUpdateResponse getBranchUpdateList(int versionNo) {
		logger.debug("BEGIN - getBranchUpdateList() with versionNo :" + versionNo);
		List<BranchUpdate> branchUpdateList = new ArrayList<BranchUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(Branch.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			BranchUpdate branchUpdate = new BranchUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				branchUpdate.setBranch(new BranchDto(versionRef.getEntityId()));
			} else {
				Branch branch = branchService.findBranchById(versionRef.getEntityId());
				branchUpdate.setBranch(new BranchDto(branch));
			}

			branchUpdate.setOperation(versionRef.getOperation());
			branchUpdateList.add(branchUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		BranchUpdateResponse response = new BranchUpdateResponse();
		response.setBranchUpdateList(branchUpdateList);
		response.setLatestVersionNo(maxVersionNo);

		logger.debug("END - getBranchUpdateList() with versionNo :" + versionNo);
		return response;
	}

	@Override
	public CountryUpdateResponse getCountryUpdateList(int versionNo) {
		logger.debug("BEGIN - getCountryUpdateList() with versionNo :" + versionNo);
		List<CountryUpdate> countryUpdateList = new ArrayList<CountryUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(Country.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			CountryUpdate countryUpdate = new CountryUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				countryUpdate.setCountry(new CountryDto(versionRef.getId()));
			} else {
				Country country = countryService.findCountryById(versionRef.getEntityId());
				countryUpdate.setCountry(new CountryDto(country));
			}
			countryUpdate.setOperation(versionRef.getOperation());
			countryUpdateList.add(countryUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		CountryUpdateResponse response = new CountryUpdateResponse();
		response.setCountryUpdateList(countryUpdateList);
		response.setLatestVersionNo(maxVersionNo);

		logger.debug("END - getCountryUpdateList() with versionNo :" + versionNo);
		return response;
	}

	@Override
	public IndustryUpdateResponse getIndustryUpdateList(int versionNo) {
		logger.debug("BEGIN - getIndustryUpdateList() with versionNo :" + versionNo);
		List<IndustryUpdate> industryUpdateList = new ArrayList<IndustryUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(Industry.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			IndustryUpdate retUpdate = new IndustryUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				retUpdate.setIndustry(new IndustryDto(versionRef.getEntityId()));
			} else {
				Industry industry = industryService.findIndustryById(versionRef.getEntityId());
				retUpdate.setIndustry(new IndustryDto(industry));
			}
			retUpdate.setOperation(versionRef.getOperation());
			industryUpdateList.add(retUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		IndustryUpdateResponse response = new IndustryUpdateResponse();
		response.setIndustryUpdateList(industryUpdateList);
		response.setLatestVersionNo(maxVersionNo);

		logger.debug("END - getIndustryUpdateList() with versionNo :" + versionNo);
		return response;
	}

	@Override
	public OccupationUpdateResponse getOccupationUpdateList(int versionNo) {
		logger.debug("BEGIN - getOccupationUpdateList() with versionNo :" + versionNo);
		List<OccupationUpdate> occupationUpdateList = new ArrayList<OccupationUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(Occupation.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			OccupationUpdate retUpdate = new OccupationUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				retUpdate.setOccupation(new OccupationDto(versionRef.getEntityId()));
			} else {
				Occupation occupation = occupationService.findOccupationById(versionRef.getEntityId());
				retUpdate.setOccupation(new OccupationDto(occupation));
			}
			retUpdate.setOperation(versionRef.getOperation());
			occupationUpdateList.add(retUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		OccupationUpdateResponse response = new OccupationUpdateResponse();
		response.setOccupationUpdateList(occupationUpdateList);
		response.setLatestVersionNo(maxVersionNo);

		logger.debug("END - getOccupationUpdateList() with versionNo :" + versionNo);
		return response;
	}

	@Override
	public QualificationUpdateResponse getQualificationUpdateList(int versionNo) {
		logger.debug("BEGIN - getQualificationUpdateList() with versionNo :" + versionNo);
		List<QualificationUpdate> qualificationUpdateList = new ArrayList<QualificationUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(Qualification.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			QualificationUpdate retUpdate = new QualificationUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				retUpdate.setQualification(new QualificationDto(versionRef.getEntityId()));
			} else {
				Qualification qualification = qualificationService.findQualificationById(versionRef.getEntityId());
				retUpdate.setQualification(new QualificationDto(qualification));
			}
			retUpdate.setOperation(versionRef.getOperation());
			qualificationUpdateList.add(retUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		QualificationUpdateResponse response = new QualificationUpdateResponse();
		response.setQualificationUpdateList(qualificationUpdateList);
		response.setLatestVersionNo(maxVersionNo);

		logger.debug("END - getQualificationUpdateList() with versionNo :" + versionNo);
		return response;
	}

	@Override
	public RelationshipUpdateResponse getRelationshipUpdateList(int versionNo) {
		logger.debug("BEGIN - getRelationshipUpdateList() with versionNo :" + versionNo);
		List<RelationshipUpdate> relationshipUpdateList = new ArrayList<RelationshipUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(RelationShip.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			RelationshipUpdate retUpdate = new RelationshipUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				retUpdate.setRelationship(new RelationshipDto(versionRef.getEntityId()));
			} else {
				RelationShip relationship = relationshipService.findRelationShipById(versionRef.getEntityId());
				retUpdate.setRelationship(new RelationshipDto(relationship));
			}

			retUpdate.setOperation(versionRef.getOperation());
			relationshipUpdateList.add(retUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		RelationshipUpdateResponse response = new RelationshipUpdateResponse();
		response.setRelationshipUpdateList(relationshipUpdateList);
		response.setLatestVersionNo(maxVersionNo);

		logger.debug("END - getRelationshipUpdateList() with versionNo :" + versionNo);
		return response;
	}

	@Override
	public ReligionUpdateResponse getReligionUpdateList(int versionNo) {
		logger.debug("BEGIN - getReligionUpdateList() with versionNo :" + versionNo);
		List<ReligionUpdate> religionUpdateList = new ArrayList<ReligionUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(Religion.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			ReligionUpdate retUpdate = new ReligionUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				retUpdate.setReligion(new ReligionDto(versionRef.getEntityId()));
			} else {
				Religion religion = religionService.findReligionById(versionRef.getEntityId());
				retUpdate.setReligion(new ReligionDto(religion));
			}

			retUpdate.setOperation(versionRef.getOperation());
			religionUpdateList.add(retUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		ReligionUpdateResponse response = new ReligionUpdateResponse();
		response.setReligionUpdateList(religionUpdateList);
		response.setLatestVersionNo(maxVersionNo);

		logger.debug("END - getReligionUpdateList() with versionNo :" + versionNo);
		return response;
	}

	@Override
	public TownshipUpdateResponse getTownshipUpdateList(int versionNo) {
		logger.debug("BEGIN - getTownshipUpdateList() with versionNo :" + versionNo);
		List<TownshipUpdate> townshipUpdateList = new ArrayList<TownshipUpdate>();
		List<VersionRef> ret = versionRefService.findVersionRefUpdateByGroupAndEntity(Township.class.getSimpleName(), CUSTOMER_VERSION_REF, versionNo);
		int maxVersionNo = 0;
		for (VersionRef versionRef : ret) {

			TownshipUpdate retUpdate = new TownshipUpdate();
			if (versionRef.getOperation().equals(UpdateOperationType.DELETE.getLabel())) {
				retUpdate.setTownship(new TownshipDto(versionRef.getEntityId()));
			} else {
				Township township = townshipService.findTownshipById(versionRef.getEntityId());
				retUpdate.setTownship(new TownshipDto(township));
			}

			retUpdate.setOperation(versionRef.getOperation());
			townshipUpdateList.add(retUpdate);

			if (versionRef.getVersionNo() > maxVersionNo)
				maxVersionNo = versionRef.getVersionNo();
		}

		TownshipUpdateResponse response = new TownshipUpdateResponse();
		response.setTownshipUpdateList(townshipUpdateList);
		response.setLatestVersionNo(maxVersionNo);

		logger.debug("END - getTownshipUpdateList() with versionNo :" + versionNo);
		return response;
	}
	// ----------------------------------------
	// --------END web service methods---------
	// ----------------------------------------

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
	}

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	public void setReligionService(IReligionService religionService) {
		this.religionService = religionService;
	}

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	public void setBankBranchService(IBankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	public void setIndustryService(IIndustryService industryService) {
		this.industryService = industryService;
	}

	public void setQualificationService(IQualificationService qualificationService) {
		this.qualificationService = qualificationService;
	}

	public void setOccupationService(IOccupationService occupationService) {
		this.occupationService = occupationService;
	}

	public void setRelationshipService(IRelationShipService relationshipService) {
		this.relationshipService = relationshipService;
	}

	// ---------------------------------------------------
	// ---------------Private Methods---------------------
	// ---------------------------------------------------
	private List<VersionIndexDto> checkAndPrepareRequiredVersionIndexes(List<VersionIndexDto> versionIndexes) throws IllegalArgumentException, IllegalAccessException {
		List<String> entityNames = new ArrayList<String>();
		for (VersionIndexDto versionIndexDto : versionIndexes) {
			entityNames.add(versionIndexDto.getEntityName());
		}

		Field[] dependFields = VersionRefDomainName.Customer.class.getFields();
		for (Field field : dependFields) {
			String dependEntityName = (String) field.get(field.getName());
			if (!entityNames.contains(dependEntityName))
				versionIndexes.add(new VersionIndexDto(dependEntityName, 0));
		}
		return versionIndexes;
	}

	private static <T extends Enum<T>> List<String> toStringList(Class<T> clz) {
		try {
			List<String> res = new LinkedList<String>();
			Method getDisplayValue = clz.getMethod("name");

			for (Object e : clz.getEnumConstants()) {
				res.add((String) getDisplayValue.invoke(e));
			}
			return res;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private boolean findCountryById(String id) {
		return countryService.findCountryById(id) == null ? false : true;
	}

	private boolean findTownshipById(String id) {
		return townshipService.findTownshipById(id) == null ? false : true;
	}

	private boolean findReligionById(String id) {
		return religionService.findReligionById(id) == null ? false : true;
	}

	private boolean findBranchById(String id) {
		return branchService.findBranchById(id) == null ? false : true;
	}

	private boolean findBankBranchById(String id) {
		return bankBranchService.findBankBranchById(id) == null ? false : true;
	}

	private boolean findIndustryById(String id) {
		return industryService.findIndustryById(id) == null ? false : true;
	}

	private boolean findQualificationById(String id) {
		return qualificationService.findQualificationById(id) == null ? false : true;
	}

	private boolean findOccupationById(String id) {
		return occupationService.findOccupationById(id) == null ? false : true;
	}

	private boolean findRelationshipById(String id) {
		return relationshipService.findRelationShipById(id) == null ? false : true;
	}

	private String checkCustomerDataConsistence(CustomerDto customer) {
		StringBuffer buf = new StringBuffer();
		if (!Strings.isNullOrEmpty(customer.getCountryId()) && !findCountryById(customer.getCountryId()))
			buf.append("Country, ");

		if (!Strings.isNullOrEmpty(customer.getReligionId()) && !findReligionById(customer.getReligionId()))
			buf.append("Religion, ");

		if (!Strings.isNullOrEmpty(customer.getBankBranchId()) && !findBankBranchById(customer.getBankBranchId()))
			buf.append("Bank Branch, ");

		if (!Strings.isNullOrEmpty(customer.getIndustryId()) && !findIndustryById(customer.getIndustryId()))
			buf.append("Industry, ");

		if (!Strings.isNullOrEmpty(customer.getBranchId()) && !findBranchById(customer.getBranchId()))
			buf.append("Branch, ");

		if (!Strings.isNullOrEmpty(customer.getQualificationId()) && !findQualificationById(customer.getQualificationId()))
			buf.append("Qualification, ");

		if (!Strings.isNullOrEmpty(customer.getOccupationId()) && !findOccupationById(customer.getOccupationId()))
			buf.append("Occupation, ");

		if (customer.getResidentAddress() != null && !findTownshipById(customer.getResidentAddress().getTownshipId()))
			buf.append("Resident address's township, ");

		if (customer.getPermanentAddress() != null && !findTownshipById(customer.getPermanentAddress().getTownshipId()))
			buf.append("Permanent address's township, ");

		return buf.toString().length() > 0 ? buf.toString() : "OK";
	}

	private String validateCustomerInfo(CustomerDto customer) {
		StringBuffer buf = new StringBuffer();

		if (StringUtils.isBlank(customer.getInitialId()))
			buf.append("Initial Id, ");
		if (customer.getDateOfBirth() == null)
			buf.append("Date of Birth, ");
		if (customer.getName() == null) {
			buf.append("Name, ");
		} else {
			if (StringUtils.isBlank(customer.getName().getFirstName()))
				buf.append("First Name, ");
		}
		if (customer.getGender() == null)
			buf.append("Gender, ");
		if (customer.getIdType() == null)
			buf.append("Id Type, ");
		if (customer.getResidentAddress() == null) {
			buf.append("Resident Address, ");
		} else {
			if (StringUtils.isBlank(customer.getResidentAddress().getResidentAddress()))
				buf.append("Resident Address, ");
			if (StringUtils.isBlank(customer.getResidentAddress().getTownshipId()))
				buf.append("Resident Address's Township, ");
		}
		if (customer.getPermanentAddress() != null) {
			if (StringUtils.isBlank(customer.getPermanentAddress().getPermanentAddress()))
				buf.append("Permanent Address, ");
			if (StringUtils.isBlank(customer.getPermanentAddress().getTownshipId()))
				buf.append("Permanent Address's Township, ");
		}

		for (FamilyInfoDto family : customer.getFamilyInfo()) {
			if (StringUtils.isBlank(family.getInitialId())) {
				buf.append("Family Info's Initial Id, ");
				break;
			}
			if (family.getName() == null) {
				buf.append("Family Name, ");
				break;
			} else {
				if (StringUtils.isBlank(family.getName().getFirstName())) {
					buf.append("Family First Name, ");
					break;
				}
			}
		}
		if (StringUtils.isBlank(customer.getBranchId()))
			buf.append("Branch Id, ");
		if (StringUtils.isBlank(customer.getCountryId()))
			buf.append("Country Id, ");

		return buf.toString().length() > 0 ? buf.toString() : "OK";
	}
}
