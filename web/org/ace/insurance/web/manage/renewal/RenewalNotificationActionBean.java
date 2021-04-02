package org.ace.insurance.web.manage.renewal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.GenericDataModel;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.renewal.RenewalNotification;
import org.ace.insurance.renewal.RenewalNotificationCriteria;
import org.ace.insurance.renewal.service.interfaces.IRenewalNotificationService;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.CloseEvent;

@ViewScoped
@ManagedBean(name = "RenewalNotificationActionBean")
public class RenewalNotificationActionBean<T extends IDataModel> extends BaseBean {

	@ManagedProperty(value = "#{RenewalNotificationService}")
	private IRenewalNotificationService renewalNotificationService;

	public void setRenewalNotificationService(IRenewalNotificationService renewalNotificationService) {
		this.renewalNotificationService = renewalNotificationService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	private List<RenewalNotification> renewalNotificationList;
	private RenewalNotification[] selectedNotis;
	private GenericDataModel<RenewalNotification> renewalDataModel;
	private RenewalNotificationCriteria criteria;
	private String motorPolicyId;
	private boolean groupLife;
	private String firePolicyId;
	private Map<String, LifePolicy> lifeMap;

	@PostConstruct
	public void init() {
		criteria = new RenewalNotificationCriteria();
		lifeMap = new HashMap<String, LifePolicy>();
		resetDate();
	}

	private void resetDate() {
		criteria.setStartDate(new Date());
		criteria.setEndDate(Utils.addMonth(new Date(), 1));
	}

	public List<RenewalNotification> getRenewalNotificationList() {
		return renewalNotificationList;
	}

	public void setRenewalNotificationList(List<RenewalNotification> renewalNotificationList) {
		this.renewalNotificationList = renewalNotificationList;
	}

	// vehicle dialog save event

	// config icon click event

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void search() {
		renewalNotificationList = new ArrayList<RenewalNotification>();
//		if (RenewalNotificationCriteria.RenewalInsuType.MOTOR.equals(criteria.getInsuranceType())) {
//			groupLife = false;
//			renewalNotificationList = renewalNotificationService.findMotorPolicies(criteria);
//		} else if (RenewalNotificationCriteria.RenewalInsuType.FIRE.equals(criteria.getInsuranceType())) {
//			groupLife = false;
//			renewalNotificationList = renewalNotificationService.findFirePolicies(criteria);
//		} else {
//			
			groupLife = true;
			renewalNotificationList = renewalNotificationService.findGroupLifePolicies(KeyFactorChecker.getGroupLifeID(), criteria);
//		}
		renewalDataModel = new GenericDataModel(renewalNotificationList);

	}

	public void reset() {
		renewalDataModel = new GenericDataModel<RenewalNotification>();
		resetDate();
	}

	public String regisNoOrNot() {
		/*if (RenewalNotificationCriteria.RenewalInsuType.MOTOR.equals(criteria.getInsuranceType())) {
			return "Registration No.";
		} else if (RenewalNotificationCriteria.RenewalInsuType.FIRE.equals(criteria.getInsuranceType())) {
			return "Building Name";
		} else {
			return "Id No";
		}*/
		return "Id No";
	}

	@SuppressWarnings("rawtypes")
	public GenericDataModel getRenewalDataModel() {
		return renewalDataModel;
	}

	private final String reportName = "RenewalNoticeLetter";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateReport() {
		if (selectedNotis.length > 0) {
//			if (RenewalNotificationCriteria.RenewalInsuType.MOTOR.equals(criteria.getInsuranceType())) {

				if (RenewalNotificationCriteria.RenewalInsuType.GROUPLIFE.equals(criteria.getInsuranceType())) {
					LifePolicy lifePolicy = null;
					List<LifePolicy> lifePolicyLists = new ArrayList<LifePolicy>();
					for (RenewalNotification renewal : selectedNotis) {
						if (lifeMap.get(renewal.getPolicyId()) == null) {
							lifePolicy = lifePolicyService.findLifePolicyById(renewal.getPolicyId());
							lifePolicyLists.add(lifePolicy);
						} else {
							lifePolicy = lifeMap.get(renewal.getPolicyId());
							lifePolicyLists.add(lifePolicy);
						}
					}
					DocumentBuilder.genetateGroupLifeRenewalNotificationLetter(lifePolicyLists, dirPath, fileName);
				}

				selectedNotis = null;
			}
//		}

	}

	public RenewalNotification[] getSelectedNotis() {
		return selectedNotis;
	}

	public void setSelectedNotis(RenewalNotification[] selectedNotis) {
		this.selectedNotis = selectedNotis;
	}

	public RenewalNotificationCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(RenewalNotificationCriteria criteria) {
		this.criteria = criteria;
	}

	public String getMotorPolicyId() {
		return motorPolicyId;
	}

	public void setMotorPolicyId(String motorPolicyId) {
		this.motorPolicyId = motorPolicyId;
	}

	public boolean isGroupLife() {
		return groupLife;
	}

	public String getFirePolicyId() {
		return firePolicyId;
	}

	public void setFirePolicyId(String firePolicyId) {
		this.firePolicyId = firePolicyId;
	}

}
