package org.ace.insurance.web.manage.life.snakebite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.life.snakebite.SnakeBiteBeneficiary;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyKeyFactorValue;
import org.ace.insurance.life.snakebite.SnakeBitePolicySearch;
import org.ace.insurance.life.snakebite.service.interfaces.ISnakeBitePolicyService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.common.WebUtils;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewSnakeBiteActionBean")
public class AddNewSnakeBiteActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SnakeBitePolicyService}")
	private ISnakeBitePolicyService snakeBitePolicyService;

	public void setSnakeBitePolicyService(ISnakeBitePolicyService snakeBitePolicyService) {
		this.snakeBitePolicyService = snakeBitePolicyService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{SalePointService}")
	private ISalePointService salePointService;

	public void setSalePointService(ISalePointService salePointService) {
		this.salePointService = salePointService;
	}

	private User user;
	private SnakeBitePolicy snakeBitePolicy;
	List<SnakeBiteBeneficiary> snakeBiteBeneficiaryList;
	private EnquiryCriteria criteria;
	private String userType;
	private List<SnakeBitePolicy> policyList;
	private String policyNo;
	private boolean createNew;
	private Product product;
	private Map<String, RelationShip> relationShipMap;
	private List<SelectItem> relationShipItemList;
	private String selectedProductId;
	private Map<String, Product> productMap;
	private List<SelectItem> productSelectItems;
	private List<Product> productList;

	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		relationShipMap = new HashMap<String, RelationShip>();
		criteria = new EnquiryCriteria();
		createNewSnakeBitePolicy();
		productMap = new HashMap<String, Product>();
		beneficiariesInfoDTOList = new ArrayList<SnakeBiteBeneficiariesInfoDTO>();
		createNewBeneficiariesInfoDTOMap();
		createNewBeneficiariesInfo();
		List<RelationShip> relationShipList = relationShipService.findAllRelationShip();
		relationShipItemList = new ArrayList<SelectItem>();
		for (RelationShip relationShip : relationShipList) {
			relationShipItemList.add(new SelectItem(relationShip.getName(), relationShip.getName()));
			relationShipMap.put(relationShip.getName(), relationShip);
		}
		productSelectItems = new ArrayList<SelectItem>();
		if (productSelectItems == null || productSelectItems.isEmpty()) {
			// productList =
			// productService.findProductByInsuranceType(InsuranceType.LIFE);
			// for(Product product : productList) {
			Product product = productService.findProductById("ISPRD0030001000000002231032013");
			productList = new ArrayList<Product>();
			productList.add(product);
			productSelectItems.add(new SelectItem(product.getId(), product.getName()));
			productMap.put(product.getId(), product);
			// }
			selectedProductId = productList.get(0).getId();
		}
		// List<SalePoint> salePointList = salePointService.findAllSalePoint();
		// if (salePointList.size() > 0) {
		// snakeBitePolicy.setSalePoint(salePointList.get(0));
		// }
		if (null != user.getSalePoint()) {
			snakeBitePolicy.setSalePoint(user.getSalePoint());
		}
	}

	public List<SelectItem> getProductSelectItems() {
		return productSelectItems;
	}

	public String getSelectedProductId() {
		return selectedProductId;
	}

	public void setSelectedProductId(String selectedProductId) {
		this.selectedProductId = selectedProductId;
	}

	public List<SelectItem> getRelationShipItemList() {
		return relationShipItemList;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, SnakeBiteBeneficiariesInfoDTO>();
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	/*************************************************
	 * Start Beneficiary Manage
	 *********************************************************/
	private boolean createNewBeneficiariesInfo;
	private SnakeBiteBeneficiariesInfoDTO beneficiariesInfoDTO;
	private Map<String, SnakeBiteBeneficiariesInfoDTO> beneficiariesInfoDTOMap;
	private String beneficiaryRelationShipId;
	private List<SnakeBiteBeneficiariesInfoDTO> beneficiariesInfoDTOList;

	public List<SnakeBiteBeneficiariesInfoDTO> getBeneficiariesInfoDTOList() {
		return beneficiariesInfoDTOList;
	}

	public void setBeneficiariesInfoDTOList(List<SnakeBiteBeneficiariesInfoDTO> beneficiariesInfoDTOList) {
		this.beneficiariesInfoDTOList = beneficiariesInfoDTOList;
	}

	public String getBeneficiaryRelationShipId() {
		return beneficiaryRelationShipId;
	}

	public void setBeneficiaryRelationShipId(String beneficiaryRelationShipId) {
		this.beneficiaryRelationShipId = beneficiaryRelationShipId;
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	private void createNewBeneficiariesInfo() {
		createNewBeneficiariesInfo = true;
		beneficiariesInfoDTO = new SnakeBiteBeneficiariesInfoDTO();
	}

	public SnakeBiteBeneficiariesInfoDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public void setBeneficiariesInfoDTO(SnakeBiteBeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
	}

	public void prepareAddNewBeneficiariesInfo() {
		createNewBeneficiariesInfo();
	}

	public void prepareEditBeneficiariesInfo(SnakeBiteBeneficiariesInfoDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
		this.createNewBeneficiariesInfo = false;
		if (beneficiariesInfoDTO.getRelationship() != null) {
			this.beneficiaryRelationShipId = beneficiariesInfoDTO.getRelationship().getName();
		} else {
			this.beneficiaryRelationShipId = null;
		}
	}

	public Map<String, SnakeBiteBeneficiariesInfoDTO> sortByValue(Map<String, SnakeBiteBeneficiariesInfoDTO> map) {
		List<Map.Entry<String, SnakeBiteBeneficiariesInfoDTO>> list = new LinkedList<Map.Entry<String, SnakeBiteBeneficiariesInfoDTO>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, SnakeBiteBeneficiariesInfoDTO>>() {
			public int compare(Map.Entry<String, SnakeBiteBeneficiariesInfoDTO> o1, Map.Entry<String, SnakeBiteBeneficiariesInfoDTO> o2) {
				Long l1 = Long.parseLong(o1.getKey());
				Long l2 = Long.parseLong(o2.getKey());
				if (l1 > l2) {
					return 1;
				} else if (l1 < l2) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		Map<String, SnakeBiteBeneficiariesInfoDTO> result = new LinkedHashMap<String, SnakeBiteBeneficiariesInfoDTO>();
		for (Map.Entry<String, SnakeBiteBeneficiariesInfoDTO> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public void saveBeneficiariesInfo() {
		if (validBeneficiary(beneficiariesInfoDTO)) {
			RelationShip selectedRelationShip = relationShipMap.get(beneficiaryRelationShipId);
			beneficiariesInfoDTO.setRelationship(selectedRelationShip);
			beneficiaryRelationShipId = null;
			beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
			setBeneficiariesInfoDTOList((new ArrayList<SnakeBiteBeneficiariesInfoDTO>(sortByValue(beneficiariesInfoDTOMap).values())));
			createNewBeneficiariesInfo();
			PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
		}
	}

	public void removeBeneficiariesInfo(SnakeBiteBeneficiariesInfoDTO beneficiariesInfoDTO) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfoDTO.getTempId());
		beneficiariesInfoDTOList.remove(beneficiariesInfoDTO);
		createNewBeneficiariesInfo();
	}

	/*************************************************
	 * End Beneficiary Manage
	 *********************************************************/

	/*************************************************
	 * Proposal Manage
	 ********************************************/
	private boolean organization;

	public boolean isOrganization() {
		return organization;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			snakeBitePolicy.setSaleMan(null);
			snakeBitePolicy.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			snakeBitePolicy.setAgent(null);
			snakeBitePolicy.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			snakeBitePolicy.setSaleMan(null);
			snakeBitePolicy.setAgent(null);
		}
	}

	public List<SnakeBitePolicy> getPolicyList() {
		return policyList;
	}

	public void setPolicyList(List<SnakeBitePolicy> policyList) {
		this.policyList = policyList;
	}

	public void loadPolicyList() {
		if (policyList == null) {
			policyList = snakeBitePolicyService.findAllSnakeBitePolicy();
		}
	}

	/*************************************************
	 * Proposal Manage
	 **********************************************/

	public void createNewSnakeBitePolicy() {
		createNew = true;
		snakeBitePolicy = new SnakeBitePolicy();
		// snakeBitePolicy.setPolicyStartDate(new Date());
		policyNo = null;
	}

	public void calculateEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(snakeBitePolicy.getPolicyStartDate());
		cal.add(Calendar.YEAR, snakeBitePolicy.getPeriodOfYear());
		snakeBitePolicy.setPolicyEndDate(cal.getTime());
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public SnakeBitePolicy getSnakeBitePolicy() {
		if (snakeBitePolicy != null) {
			if (snakeBitePolicy.getSnakeBiteBeneficiaryList().size() != 0) {

			}
		}
		return snakeBitePolicy;
	}

	public void setSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy) {
		this.snakeBitePolicy = snakeBitePolicy;
	}

	public String addNewSnakeBitePolicy() {
		String result = null;
		try {
			if (!requireBeneficiary(beneficiariesInfoDTOList)) {
				return null;
			}
			Product product = productMap.get(selectedProductId);
			snakeBitePolicy.setProduct(product);
			List<SnakeBitePolicyKeyFactorValue> snakeBitePolicyKeyFactorValueList = new ArrayList<SnakeBitePolicyKeyFactorValue>();
			for (KeyFactor kf : snakeBitePolicy.getProduct().getKeyFactorList()) {
				if (KeyFactorChecker.isSumInsured(kf)) {
					SnakeBitePolicyKeyFactorValue snakeBitePolicyKeyFactorValue1 = new SnakeBitePolicyKeyFactorValue(String.valueOf(snakeBitePolicy.getSumInsured()), kf,
							snakeBitePolicy);
					snakeBitePolicyKeyFactorValueList.add(snakeBitePolicyKeyFactorValue1);
				}
				if (KeyFactorChecker.isTerm(kf)) {
					SnakeBitePolicyKeyFactorValue snakeBitePolicyKeyFactorValue2 = new SnakeBitePolicyKeyFactorValue(String.valueOf(snakeBitePolicy.getPeriodOfYear()), kf,
							snakeBitePolicy);
					snakeBitePolicyKeyFactorValueList.add(snakeBitePolicyKeyFactorValue2);
				}

			}
			calculateEndDate();
			snakeBitePolicy.setSnakeBitePolicyKeyFactorValueList(snakeBitePolicyKeyFactorValueList);
			snakeBitePolicy.setSnakeBiteBeneficiaryList(getBeneficiariesInfoList());
			snakeBitePolicy = snakeBitePolicyService.addNewSnakeBitePolicy(snakeBitePolicy);
			createNewSnakeBitePolicy();
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public List<SnakeBiteBeneficiary> getBeneficiariesInfoList() {
		List<SnakeBiteBeneficiary> result = null;
		if (beneficiariesInfoDTOList != null && !beneficiariesInfoDTOList.isEmpty()) {
			result = new ArrayList<SnakeBiteBeneficiary>();
			for (SnakeBiteBeneficiariesInfoDTO beneficiairesInfoDTO : beneficiariesInfoDTOList) {
				SnakeBiteBeneficiary insPesBenf = new SnakeBiteBeneficiary(beneficiairesInfoDTO.getRelationship(), null, beneficiairesInfoDTO.getAge(),
						beneficiairesInfoDTO.getPercentage(), beneficiairesInfoDTO.getInitialId(), beneficiairesInfoDTO.getIdNo(), beneficiairesInfoDTO.getName(),
						beneficiairesInfoDTO.getIdType(), beneficiairesInfoDTO.getGender(), beneficiairesInfoDTO.getResidentAddress(), beneficiairesInfoDTO.getPrefix());
				insPesBenf.setSnakeBitePolicy(snakeBitePolicy);
				result.add(insPesBenf);
			}
		}
		return result;

	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "snakeBiteEntryForm";
		if ("proposalInfo".equals(event.getOldStep())) {
			if (snakeBitePolicy.getPolicyStartDate() == null) {
				addErrorMessage(formID + ":submittedDate", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (snakeBitePolicy.getCustomer() == null) {
				addErrorMessage(formID + ":customer", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (snakeBitePolicy.getPaymentType() == null) {
				addErrorMessage(formID + ":paymentType", MessageId.REQUIRED_PAYMENTTYPE);
				valid = false;
			}
			if (snakeBitePolicy.getBranch() == null) {
				addErrorMessage(formID + ":branch", MessageId.REQUIRED_BRANCH);
				valid = false;
			}
			if (isEmpty(snakeBitePolicy.getSalePersonName())) {
				addErrorMessage(formID + ":userType", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (isEmpty(snakeBitePolicy.getBookNo())) {
				addErrorMessage(formID + ":bookNo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (isEmpty(snakeBitePolicy.getSnakeBitePolicyNo())) {
				addErrorMessage(formID + ":poliyNo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (snakeBitePolicy.getSumInsured() <= 0.0) {
				addErrorMessage(formID + ":sumInsuredInfo", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if (snakeBitePolicy.getPeriodOfYear() != 1) {
				addErrorMessage(formID + ":periodOfInsurance", MessageId.AVAILABLE_INSURED_PERIOD, 1);
				valid = false;
			}
		}

		return valid ? event.getNewStep() : event.getOldStep();
	}

	public Map<String, SnakeBiteBeneficiariesInfoDTO> getBeneficiariesInfoDTOMap() {
		return beneficiariesInfoDTOMap;
	}

	public void setBeneficiariesInfoDTOMap(Map<String, SnakeBiteBeneficiariesInfoDTO> beneficiariesInfoDTOMap) {
		this.beneficiariesInfoDTOMap = beneficiariesInfoDTOMap;
	}

	public String getUserType() {
		if (userType == null) {
			userType = UserType.AGENT.toString();
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	private boolean validBeneficiary(SnakeBiteBeneficiariesInfoDTO beneficiariesInfoDTO) {
		boolean valid = true;
		String formID = "beneficiaryInfoEntryForm";
		if (isEmpty(beneficiariesInfoDTO.getName().getFirstName())) {
			addErrorMessage(formID + ":firstName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!beneficiariesInfoDTO.getIdType().equals(IdType.STILL_APPLYING) && isEmpty(beneficiariesInfoDTO.getIdNo())) {
			addErrorMessage(formID + ":idNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (isEmpty(beneficiariesInfoDTO.getResidentAddress().getResidentAddress())) {
			addErrorMessage(formID + ":address", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (beneficiariesInfoDTO.getResidentAddress().getTownship() == null) {
			addErrorMessage(formID + ":townShip", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (beneficiariesInfoDTO.getPercentage() < 1 || beneficiariesInfoDTO.getPercentage() > 100) {
			addErrorMessage(formID + ":percentage", MessageId.BENEFICIARY_PERCENTAGE);
			valid = false;
		}
		return valid;
	}

	private boolean requireBeneficiary(List<SnakeBiteBeneficiariesInfoDTO> beneficiariesInfoDTOList) {
		boolean valid = true;
		String formID = "snakeBiteEntryForm";
		if (beneficiariesInfoDTOList != null && beneficiariesInfoDTOList.size() != 0) {
			float totalPercent = 0.0f;
			for (SnakeBiteBeneficiariesInfoDTO sbDTO : beneficiariesInfoDTOList) {
				totalPercent = totalPercent + sbDTO.getPercentage();
			}
			if (totalPercent > 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.OVER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
			if (totalPercent < 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.LOWER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
			for (SnakeBiteBeneficiariesInfoDTO bfDTO : beneficiariesInfoDTOList) {
				if (!bfDTO.isValidBeneficiaries()) {
					addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.INVALID_BENEFICIARY_PERSON);
					valid = false;
					break;
				}
			}
		} else {
			addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.REQUIRED_BENEFICIARY_PERSON);
			valid = false;

		}
		return valid;
	}

	public void copySnakeBitePolicy(SnakeBitePolicy policy) {
		beneficiariesInfoDTOList.clear();
		beneficiariesInfoDTOMap.clear();
		policyNo = policy.getSnakeBitePolicyNo();
		snakeBitePolicy = new SnakeBitePolicy(policy);
		if (snakeBitePolicy.getSnakeBiteBeneficiaryList().size() != 0) {
			for (SnakeBiteBeneficiary snakeBiteBeneficiary : snakeBitePolicy.getSnakeBiteBeneficiaryList()) {
				SnakeBiteBeneficiariesInfoDTO snakeBiteBeneficiariesInfoDTO = new SnakeBiteBeneficiariesInfoDTO(snakeBiteBeneficiary);
				beneficiariesInfoDTOList.add(snakeBiteBeneficiariesInfoDTO);
				beneficiariesInfoDTOMap.put(snakeBiteBeneficiariesInfoDTO.getTempId(), snakeBiteBeneficiariesInfoDTO);
			}
		}
		if (snakeBitePolicy.getAgent() != null) {
			userType = UserType.AGENT.toString();
		} else if (snakeBitePolicy.getSaleMan() != null) {
			userType = UserType.SALEMAN.toString();
		} else if (snakeBitePolicy.getReferral() != null) {
			userType = UserType.REFERRAL.toString();
		}

	}

	public void reset() {
		beneficiariesInfoDTOList.clear();
		createNewSnakeBitePolicy();
		createNewBeneficiariesInfoDTOMap();
		createNewBeneficiariesInfo();
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void returnSnakeBitePolicyNo(SelectEvent event) {
		SnakeBitePolicySearch snakeBitePolicySearch = (SnakeBitePolicySearch) event.getObject();
		String formID = "snakeBiteEntryForm";
		SnakeBitePolicy s = new SnakeBitePolicy();
		s = snakeBitePolicyService.findSnakeBitePolicyByPolicyNo(snakeBitePolicySearch.getSnakeBitePolicyNo());
		snakeBitePolicy = new SnakeBitePolicy(s);
		policyNo = snakeBitePolicy.getSnakeBitePolicyNo();
		for (SnakeBiteBeneficiary snakeBiteBeneficiary : snakeBitePolicy.getSnakeBiteBeneficiaryList()) {
			beneficiariesInfoDTO.setAge(snakeBiteBeneficiary.getAge());
			beneficiariesInfoDTO.setPercentage(snakeBiteBeneficiary.getPercentage());
			beneficiariesInfoDTO.setInitialId(snakeBiteBeneficiary.getInitialId());
			beneficiariesInfoDTO.setGender(snakeBiteBeneficiary.getGender());
			beneficiariesInfoDTO.setIdType(snakeBiteBeneficiary.getIdType());
			beneficiariesInfoDTO.setResidentAddress(snakeBiteBeneficiary.getResidentAddress());
			beneficiariesInfoDTO.setName(snakeBiteBeneficiary.getName());
			beneficiariesInfoDTO.setIdNo(snakeBiteBeneficiary.getIdNo());
			beneficiariesInfoDTOList.add(beneficiariesInfoDTO);
		}
	}

	public void selectSalePoint() {
		PrimeFaces.current().dialog().openDynamic("salePointDialog", WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		snakeBitePolicy.setSalePoint(salePoint);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		snakeBitePolicy.setCustomer(customer);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		snakeBitePolicy.setPaymentType(paymentType);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		snakeBitePolicy.setBranch(branch);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		snakeBitePolicy.setAgent(agent);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		snakeBitePolicy.setSaleMan(saleMan);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		snakeBitePolicy.setReferral(referral);
	}

	public void returnTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void selectBeneficiariesTownship() {
		selectTownship();
	}
}
