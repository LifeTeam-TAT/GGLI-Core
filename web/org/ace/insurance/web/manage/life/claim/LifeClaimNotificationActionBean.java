package org.ace.insurance.web.manage.life.claim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.life.claim.LifeClaimNotification;
import org.ace.insurance.life.claim.LifePolicySearch;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimNotificationService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.web.common.WebUtils;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifeClaimNotificationActionBean")
public class LifeClaimNotificationActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeClaimNotificationService}")
	private ILifeClaimNotificationService notificationService;

	public void setNotificationService(ILifeClaimNotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
	}

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
	}

	private boolean isNrcReporter;
	private boolean isStillApplyReporter;
	private boolean createNew;
	private LifeClaimNotification notification;
	private List<Product> productsList;
	private List<PolicyInsuredPerson> insuredPersonList;
	private List<LifeClaimNotification> notificationList;
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();

	@PostConstruct
	public void init() {
		initialization();
		// productsList =
		// productService.findProductByInsuranceType(InsuranceType.LIFE);
		insuredPersonList = new ArrayList<PolicyInsuredPerson>();
		loadLifeClaimNotification();
		// notificationList = notificationService.findLifeClaimNotification();
		stateCodeList = stateCodeService.findAllStateCode();
	}

	@PreDestroy
	private void destroy() {
		removeParam("notification");
	}

	private void initialization() {
		notification = (LifeClaimNotification) getParam("notification");
	}

	public void loadLifeClaimNotification() {
		if (notification != null) {
			createNew = false;
			this.notification.setClaimStatus(ClaimStatus.INITIAL_INFORM);
			LifePolicy lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(notification.getLifePolicyNo());
			insuredPersonList = lifePolicy.getPolicyInsuredPersonList();
		} else {
			createNewLifeClaimNotification();
		}
	}

	public void createNewLifeClaimNotification() {
		createNew = true;
		notification = new LifeClaimNotification();
		isNrcReporter = true;
		isStillApplyReporter = true;
	}

	public void selectPolicyNo() {
		selectLifePolicyNo();
	}

	public void changeProduct(AjaxBehaviorEvent event) {
		notification.setLifePolicyNo(null);
	}

	public String addNotification() {
		String result = null;
		try {
			if (!validReportInfo()) {
				return result;
			}
			notification.setClaimStatus(ClaimStatus.INITIAL_INFORM);
			notificationService.addNewLifeClaimNotification(notification);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_NOTIFICATION_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, notification.getNotificationNo());
			result = "managelifeClaimNotification";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String updateNotification() {
		String result = null;
		try {
			if (!validReportInfo()) {
				return result;
			}
			notification.setClaimStatus(ClaimStatus.INITIAL_INFORM);
			notificationService.updateLifeClaimNotification(notification);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_NOTIFICATION_PROCESS_UPDATE);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, notification.getNotificationNo());
			result = "managelifeClaimNotification";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private boolean validReportInfo() {
		boolean valid = true;

		String formID = "lifeClaimNotificationForm";
		if (isEmpty(notification.getClaimInitialReporter().getIdNo()) && !notification.getClaimInitialReporter().getIdType().equals(IdType.STILL_APPLYING)) {
			addErrorMessage(formID + ":reporterRegidNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (notification.getClaimInitialReporter().getIdType().equals(IdType.NRCNO)) {
			if ((notification.getClaimInitialReporter().getStateCode() == null && notification.getClaimInitialReporter().getTownshipCode() != null)
					|| (notification.getClaimInitialReporter().getStateCode() != null && notification.getClaimInitialReporter().getTownshipCode() == null)) {
				addErrorMessage(formID + ":reporterRegidNo", MessageId.NRC_STATE_AND_TOWNSHIP_ERROR);
				valid = false;
			} else if (notification.getClaimInitialReporter().getIdNo().length() != 6 && notification.getClaimInitialReporter().getStateCode() != null
					&& notification.getClaimInitialReporter().getTownshipCode() != null) {
				addErrorMessage(formID + ":reporterRegidNo", MessageId.NRC_FORMAT_INCORRECT);
				valid = false;
			}
		}
		return valid;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public void returnLifePolicyNo(SelectEvent event) {
		LifePolicySearch lifePolicySearch = (LifePolicySearch) event.getObject();
		notification.setLifePolicyNo(lifePolicySearch.getPolicyNo());
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(lifePolicySearch.getPolicyNo());
		insuredPersonList = lifePolicy.getPolicyInsuredPersonList();
		notification.setProduct(insuredPersonList.get(0).getProduct());
	}

	public void changeStateCode(AjaxBehaviorEvent e) {
		StateCode stateCode = (StateCode) ((UIOutput) e.getSource()).getValue();
		townshipCodeList = townshipCodeService.findByStateCode(stateCode);
	}

	public void changeIdType(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		if (idType.equals(IdType.NRCNO)) {
			isNrcReporter = true;
			isStillApplyReporter = true;
		} else if (idType.equals(IdType.STILL_APPLYING)) {
			isNrcReporter = false;
			isStillApplyReporter = false;
		} else {
			isNrcReporter = false;
			isStillApplyReporter = true;
		}
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public IdConditionType[] getIdConditionTypeSelectItemList() {
		return IdConditionType.values();
	}

	public void returnResidentTownship(SelectEvent event) {
		Township residentTownship = (Township) event.getObject();
		notification.getClaimInitialReporter().setTownship((residentTownship));
	}

	public void selectSalePoint() {
		PrimeFaces.current().dialog().openDynamic("salePointDialog", WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		notification.setSalePoint(salePoint);
	}

	public LifeClaimNotification getNotification() {
		return notification;
	}

	public void setNotification(LifeClaimNotification notification) {
		this.notification = notification;
	}

	public List<Product> getProductsList() {
		return productsList;
	}

	public List<PolicyInsuredPerson> getInsuredPersonList() {
		return insuredPersonList;
	}

	public List<LifeClaimNotification> getNotificationList() {
		return notificationList;
	}

	public boolean isNrcReporter() {
		return isNrcReporter;
	}

	public void setNrcReporter(boolean isNrcReporter) {
		this.isNrcReporter = isNrcReporter;
	}

	public boolean isStillApplyReporter() {
		return isStillApplyReporter;
	}

	public void setStillApplyReporter(boolean isStillApplyReporter) {
		this.isStillApplyReporter = isStillApplyReporter;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

}
