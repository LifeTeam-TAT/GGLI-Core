package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.utils.BancaStatus;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaLIC.service.intefaces.IBancaLICService;
import org.ace.insurance.system.common.licBrmfo.LicBrmInfo;
import org.ace.insurance.system.common.licbranchinfo.LicBranchInfo;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageBancaLICActionBean")
public class ManageBancaLICActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BancaLICService}")
	private IBancaLICService bancaLICService;

	public void setBancaLICService(IBancaLICService bancaLICService) {
		this.bancaLICService = bancaLICService;
	}

	private boolean createNew;
	private BancaLIC bancaLIC;
	private boolean isBRMInfo;
	private List<BancaLIC> bancaLICInfoList;
	private List<BancaLIC> filterbancaLICInfoList;

	private Map<String, LicBrmInfo> licBrmInfoMap;
	private Map<String, LicBranchInfo> licBranchInfoMap;
	private LicBrmInfo licBrmInfo;
	private LicBrmInfo oldLicBrmInfo;
	private LicBranchInfo licBranchInfo;
	private LicBranchInfo oldLicBranchInfo;
	private boolean editBrmInfo;
	private boolean editlicBranchInfo;
	private boolean createNewLicBrmInfo;

	/*
	 * private List<LICBrmfo> licBrmfoList; private List<LICBranchfo>
	 * licBranchfoList;
	 */

	@PostConstruct
	public void init() {
		createNewBancaLICInfo();
		loadBancaLICInfo();
		crateLicBrmInfo();
		createLicBranchInfo();
		licBrmInfoMap = new HashMap<String, LicBrmInfo>();
		licBranchInfoMap = new HashMap<String, LicBranchInfo>();
		editBrmInfo = false;
		editlicBranchInfo = false;
	}

	public void loadBancaLICInfo() {
		bancaLICInfoList = bancaLICService.findAllBancaLIC();
	}

	public void createNewBancaLICInfo() {
		createNew = true;
		bancaLIC = new BancaLIC();
		licBrmInfoMap = new HashMap<String, LicBrmInfo>();
		licBranchInfoMap = new HashMap<String, LicBranchInfo>();
		// PrimeFaces.current().resetInputs(":branchEntryForm");
	}

	public void prepareUpdateBancaLIC(BancaLIC bancaLIC) {
		createNew = false;
		this.bancaLIC = bancaLIC;
		licBrmInfoMap = new HashMap<String, LicBrmInfo>();
		licBranchInfoMap = new HashMap<String, LicBranchInfo>();
		for (LicBrmInfo brminfo : bancaLIC.getLicBrmInfoList()) {
			licBrmInfoMap.put(brminfo.getTempId(), brminfo);
		}
		for (LicBranchInfo brmBranchinfo : bancaLIC.getLicBranchInfoList()) {
			licBranchInfoMap.put(brmBranchinfo.getTempId(), brmBranchinfo);
		}
	}

	public boolean valid() {
		boolean result = true;
		if (licBrmInfoMap.isEmpty()) {
			result = false;
			addErrorMessage("bancaLICEntryForm:licBrmInfoRecord", MessageId.LIC_BRM_INFO_REQUIRED);
		}
		if (licBranchInfoMap.isEmpty()) {
			result = false;
			addErrorMessage("bancaLICEntryForm:licBranchInfoRecord", MessageId.LIC_BRANCHINFO_REQUIRED);
		}
		return result;
	}

	public void addNewBancaLIC() {
		try {
			if (valid()) {
				bancaLIC.setLicBranchInfoList(getLicBranchInfoList());
				bancaLIC.setLicBrmInfoList(getLicBrmInfoList());
				Calendar calendar = Calendar.getInstance();
				Date today = calendar.getTime();
				if (bancaLIC.getLicenseExpireDate().compareTo(today) < 0) {
					bancaLIC.setStatus(bancaLIC.getStatus().INACTIVE);
				}
				bancaLICService.insert(bancaLIC);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, bancaLIC.getName());
				createNewBancaLICInfo();
				loadBancaLICInfo();
				licBrmInfoMap = new HashMap<String, LicBrmInfo>();
				licBranchInfoMap = new HashMap<String, LicBranchInfo>();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaLICInfo();
	}

	public void updateBancaLIC() {
		try {
			bancaLIC.setLicBranchInfoList(getLicBranchInfoList());
			bancaLIC.setLicBrmInfoList(getLicBrmInfoList());
			bancaLICService.update(bancaLIC);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, bancaLIC.getName());
			createNewBancaLICInfo();
			loadBancaLICInfo();
			licBrmInfoMap = new HashMap<String, LicBrmInfo>();
			licBranchInfoMap = new HashMap<String, LicBranchInfo>();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaLICInfo();
	}

	public void deleteBancaLIC(BancaLIC bancaLIC) {
		try {
			bancaLICService.delete(bancaLIC);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, bancaLIC.getName());
			createNewBancaLICInfo();
			loadBancaLICInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaLICInfo();
	}

	// *********** LicBrmInfo **********
	public void crateLicBrmInfo() {
		licBrmInfo = new LicBrmInfo();
	}

	public void saveLicBrmInfo() {
		if (licBrmInfo.getStatus().equals(BancaStatus.INACTIVE)) {
			licBrmInfo.setEndDate(new Date());
		}
		if ((editBrmInfo && licBrmInfo.getStatus().equals(BancaStatus.ACTIVE)) && oldLicBrmInfo.getStatus().equals(BancaStatus.INACTIVE)) {
			licBrmInfoMap.put(oldLicBrmInfo.getTempId(), oldLicBrmInfo);
			licBrmInfo.setEndDate(null);
			licBrmInfo.setTempId(System.nanoTime() + "");
			licBrmInfo.setId(null);
		}
		licBrmInfoMap.put(licBrmInfo.getTempId(), licBrmInfo);
		editBrmInfo = false;
		crateLicBrmInfo();
	}

	public boolean isCreateNewLicBrmInfo() {
		return createNewLicBrmInfo;
	}

	public void removeLicBrmInfo(LicBrmInfo licBrminfo) {
		licBrmInfoMap.remove(licBrmInfo.getTempId());
		// licBrmInfoMap.clear();
	}

	public void prepareEditLicBrmInfo(LicBrmInfo licBrmInfo) {
		this.licBrmInfo = licBrmInfo;
		this.oldLicBrmInfo = new LicBrmInfo(licBrmInfo);
		editBrmInfo = true;
	}

	public void resetlicBrmInfo() {
		licBrmInfo = new LicBrmInfo();
		editBrmInfo = false;
	}

	// ********* LicBranchInfo *******
	public void createLicBranchInfo() {
		licBranchInfo = new LicBranchInfo();
	}

	public void saveLicBranchInfo() {
		if (licBranchInfo.getStatus().equals(BancaStatus.INACTIVE)) {
			licBranchInfo.setEndDate(new Date());
		}
		if ((editlicBranchInfo && licBranchInfo.getStatus().equals(BancaStatus.ACTIVE)) && oldLicBranchInfo.getStatus().equals(BancaStatus.INACTIVE)) {
			licBranchInfoMap.put(oldLicBranchInfo.getTempId(), oldLicBranchInfo);
			licBranchInfo.setEndDate(null);
			licBranchInfo.setTempId(System.nanoTime() + "");
			licBranchInfo.setId(null);
		}
		licBranchInfoMap.put(licBranchInfo.getTempId(), licBranchInfo);
		editlicBranchInfo = false;
		createLicBranchInfo();
	}

	public void removeLicBranchInfo(LicBranchInfo licBranchInfo) {
		licBranchInfoMap.remove(licBranchInfo.getTempId());
		// licBranchInfoMap.clear();
	}

	public void prepareEditLicBranchInfo(LicBranchInfo licBranchInfo) {
		this.licBranchInfo = licBranchInfo;
		this.oldLicBranchInfo = new LicBranchInfo(licBranchInfo);
		editlicBranchInfo = true;
	}

	public void resetLicBranchInfo() {
		licBranchInfo = new LicBranchInfo();
		editlicBranchInfo = false;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public BancaLIC getBancaLIC() {
		return bancaLIC;
	}

	public void setBancaLIC(BancaLIC bancaLIC) {
		this.bancaLIC = bancaLIC;
	}

	public List<BancaLIC> getBancaLICInfoList() {
		return bancaLICInfoList;
	}

	public void setBancaLICInfoList(List<BancaLIC> bancaLICInfoList) {
		this.bancaLICInfoList = bancaLICInfoList;
	}

	public List<BancaLIC> getFilterbancaLICInfoList() {
		return filterbancaLICInfoList;
	}

	public void setFilterbancaLICInfoList(List<BancaLIC> filterbancaLICInfoList) {
		this.filterbancaLICInfoList = filterbancaLICInfoList;
	}

	public IBancaLICService getBancaLICService() {
		return bancaLICService;
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public BancaStatus[] getBancaStatus() {
		return BancaStatus.values();
	}

	public Map<String, LicBrmInfo> getLicBrmInfoMap() {
		return licBrmInfoMap;
	}

	public void setLicBrmInfoMap(Map<String, LicBrmInfo> licBrmInfoMap) {
		this.licBrmInfoMap = licBrmInfoMap;
	}

	public Map<String, LicBranchInfo> getLicBranchInfoMap() {
		return licBranchInfoMap;
	}

	public void setLicBranchInfoMap(Map<String, LicBranchInfo> licBranchInfoMap) {
		this.licBranchInfoMap = licBranchInfoMap;
	}

	public LicBrmInfo getLicBrmInfo() {
		return licBrmInfo;
	}

	public void setLicBrmInfo(LicBrmInfo licBrmInfo) {
		this.licBrmInfo = licBrmInfo;
	}

	public LicBranchInfo getLicBranchInfo() {
		return licBranchInfo;
	}

	public void setLicBranchInfo(LicBranchInfo licBranchInfo) {
		this.licBranchInfo = licBranchInfo;
	}

	public List<LicBrmInfo> getLicBrmInfoList() {
		List<LicBrmInfo> licBrmInfoList = new ArrayList<LicBrmInfo>(licBrmInfoMap.values());
		licBrmInfoList.sort(Comparator.comparing(LicBrmInfo::getStartDate).reversed());
		licBrmInfoList.forEach(brmInfo -> {
			if (brmInfo.getStatus().equals(BancaStatus.INACTIVE)) {
				licBrmInfoList.forEach(brmInfo2 -> {
					if (brmInfo2.getBancaBrm().getId().equalsIgnoreCase(brmInfo.getBancaBrm().getId()) && brmInfo2.getStatus().equals(BancaStatus.ACTIVE)) {
						brmInfo.setDisable(true);
					}
				});
			}
		});
		return licBrmInfoList;
	}

	public List<LicBranchInfo> getLicBranchInfoList() {
		List<LicBranchInfo> licBranchInfoList = new ArrayList<LicBranchInfo>(licBranchInfoMap.values());
		licBranchInfoList.sort(Comparator.comparing(LicBranchInfo::getStartDate).reversed());
		licBranchInfoList.forEach(licBranch -> {
			if (licBranch.getStatus().equals(BancaStatus.INACTIVE)) {
				licBranchInfoList.forEach(licBranch2 -> {
					if (licBranch2.getBancaBranch().getId().equalsIgnoreCase(licBranch.getBancaBranch().getId()) && licBranch2.getStatus().equals(BancaStatus.ACTIVE)) {
						licBranch.setDisable(true);
					}
				});
			}
		});
		return licBranchInfoList;
	}

	public void returnBancaBrm(SelectEvent event) {
		BancaBRM bancaBRM = (BancaBRM) event.getObject();
		licBrmInfo.setBancaBrm(bancaBRM);
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		licBranchInfo.setBancaBranch(bancaBranch);

	}

	public boolean isEditBrmInfo() {
		return editBrmInfo;
	}

	public void setEditBrmInfo(boolean editBrmInfo) {
		this.editBrmInfo = editBrmInfo;
	}

	public boolean isEditlicBranchInfo() {
		return editlicBranchInfo;
	}

	public void setEditlicBranchInfo(boolean editlicBranchInfo) {
		this.editlicBranchInfo = editlicBranchInfo;
	}

	/*
	 * public void returnBancaBRM(SelectEvent event) { BancaBRM bancaBRM =
	 * (BancaBRM) event.getObject(); bancaLIC.setBancaBRM(bancaBRM); }
	 */

}
