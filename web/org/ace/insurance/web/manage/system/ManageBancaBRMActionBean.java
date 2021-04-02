/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.util.ArrayList;
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
import org.ace.insurance.system.common.bancaBRM.service.interfaces.IBancaBRMService;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.brmbranchinfo.BrmBranchInfo;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageBancaBRMActionBean")
public class ManageBancaBRMActionBean extends BaseBean {

	@ManagedProperty(value = "#{BancaBRMService}")
	private IBancaBRMService bancaBRMService;

	public void setBancaBRMService(IBancaBRMService bancaBRMService) {
		this.bancaBRMService = bancaBRMService;
	}

	private boolean createNew;
	private boolean editBancaInfo;
	private BancaBRM bancaBRM;
	private List<BancaBRM> bancaBRMInfoList;
	private List<BancaBRM> filterBancaBRMInfoList;
	private List<BrmBranchInfo> brmbranchInfoList;
	private Map<String, BrmBranchInfo> brmBranchInfoDTOMap;
	private BrmBranchInfo brmBranchInfo;
	private BrmBranchInfo oldBrmBranchInfo;

	@PostConstruct
	public void init() {
		createNewBancaBRMInfo();
		loadBancaBRMInfo();
		crateBrmBranchInfo();
		editBancaInfo = false;
		brmBranchInfoDTOMap = new HashMap<String, BrmBranchInfo>();
	}

	public void crateBrmBranchInfo() {
		brmBranchInfo = new BrmBranchInfo();
	}

	public void saveBrmBranchInfo() {
		if (brmBranchInfo.getStatus().equals(BancaStatus.INACTIVE)) {
			brmBranchInfo.setEndDate(new Date());
		}
		if ((editBancaInfo && brmBranchInfo.getStatus().equals(BancaStatus.ACTIVE)) && oldBrmBranchInfo.getStatus().equals(BancaStatus.INACTIVE)) {
			brmBranchInfoDTOMap.put(oldBrmBranchInfo.getTempId(), oldBrmBranchInfo);
			brmBranchInfo.setEndDate(null);
			brmBranchInfo.setTempId(System.nanoTime() + "");
			brmBranchInfo.setId(null);
		}
		brmBranchInfoDTOMap.put(brmBranchInfo.getTempId(), brmBranchInfo);
		editBancaInfo = false;
		crateBrmBranchInfo();
	}

	public void removeBancaBranchInfo(BrmBranchInfo brmBranchInfo) {
		brmBranchInfoDTOMap.remove(brmBranchInfo.getTempId());
	}

	public void prepareEditBrmBranchInfo(BrmBranchInfo brmBranchInfo) {
		this.brmBranchInfo = brmBranchInfo;
		this.oldBrmBranchInfo = new BrmBranchInfo(brmBranchInfo);
		editBancaInfo = true;
	}

	public void resetBacaBranchInfo() {
		brmBranchInfo = new BrmBranchInfo();
		editBancaInfo = false;
	}

	public void loadBancaBRMInfo() {
		bancaBRMInfoList = bancaBRMService.findAllBancaBRM();
	}

	public void createNewBancaBRMInfo() {
		createNew = true;
		bancaBRM = new BancaBRM();
		brmBranchInfoDTOMap = new HashMap<String, BrmBranchInfo>();

		// PrimeFaces.current().resetInputs(":branchEntryForm");
	}

	public void prepareUpdateBancaBRM(BancaBRM bancabrm) {
		createNew = false;
		this.bancaBRM = bancabrm;
		this.brmbranchInfoList = bancaBRM.getBrmBranchInfoList();
		brmBranchInfoDTOMap.clear();
		for (BrmBranchInfo info : brmbranchInfoList) {
			brmBranchInfoDTOMap.put(info.getTempId(), info);
		}
	}

	public boolean validBrmBranchInfo() {
		boolean result = true;
		if (brmBranchInfoDTOMap.isEmpty()) {
			result = false;
			addErrorMessage("bancaBRMEntryForm:bancaInfoRecord", MessageId.BRANCHBRMINFO_REQUIRED);
		}
		return result;
	}

	public void addNewBancaBRM() {
		try {
			if (validBrmBranchInfo()) {
				bancaBRM.setBrmBranchInfoList(getBancaBranchInfoList());
				bancaBRMService.insert(bancaBRM);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, bancaBRM.getName());
				createNewBancaBRMInfo();
				loadBancaBRMInfo();
				brmBranchInfoDTOMap = new HashMap<String, BrmBranchInfo>();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaBRMInfo();
	}

	public void updateBancaBRM() {
		try {
			bancaBRM.setBrmBranchInfoList(getBancaBranchInfoList());
			bancaBRMService.update(bancaBRM);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, bancaBRM.getName());
			createNewBancaBRMInfo();
			loadBancaBRMInfo();
			brmBranchInfoDTOMap = new HashMap<String, BrmBranchInfo>();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaBRMInfo();
	}

	public void deleteBancaBRM(BancaBRM bancaBRM) {
		try {
			bancaBRMService.delete(bancaBRM);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, bancaBRM.getName());
			createNewBancaBRMInfo();
			loadBancaBRMInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaBRMInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public BancaBRM getBancaBRM() {
		return bancaBRM;
	}

	public void setBancaBRM(BancaBRM bancaBRM) {
		this.bancaBRM = bancaBRM;
	}

	public List<BancaBRM> getBancaBRMInfoList() {
		return bancaBRMInfoList;
	}

	public void setBancaBRMInfoList(List<BancaBRM> bancaBRMInfoList) {
		this.bancaBRMInfoList = bancaBRMInfoList;
	}

	public List<BancaBRM> getFilterBancaBRMInfoList() {
		return filterBancaBRMInfoList;
	}

	public void setFilterBancaBRMInfoList(List<BancaBRM> filterBancaBRMInfoList) {
		this.filterBancaBRMInfoList = filterBancaBRMInfoList;
	}

	public List<BrmBranchInfo> getBrmbranchInfoList() {
		return brmbranchInfoList;
	}

	public void setBrmbranchInfoList(List<BrmBranchInfo> brmbranchInfoList) {
		this.brmbranchInfoList = brmbranchInfoList;
	}

	public Gender[] getGenderSelectItemList() {
		return Gender.values();
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		brmBranchInfo.setBancaBranch(bancaBranch);
	}

	public BrmBranchInfo getBrmBranchInfo() {
		return brmBranchInfo;
	}

	public void setBrmBranchInfo(BrmBranchInfo brmBranchInfo) {
		this.brmBranchInfo = brmBranchInfo;
	}

	public List<BrmBranchInfo> getBancaBranchInfoList() {
		List<BrmBranchInfo> brmBranchInfoList = new ArrayList<BrmBranchInfo>(brmBranchInfoDTOMap.values());
		brmBranchInfoList.sort(Comparator.comparing(BrmBranchInfo::getStartDate).reversed());
		brmBranchInfoList.forEach(branchInfo -> {
			if (branchInfo.getStatus().equals(BancaStatus.INACTIVE)) {
				brmBranchInfoList.forEach(branchInfo2 -> {
					if (branchInfo2.getBancaBranch().getId().equalsIgnoreCase(branchInfo.getBancaBranch().getId()) && branchInfo2.getStatus().equals(BancaStatus.ACTIVE)) {
						branchInfo.setDisable(true);
					}
				});
			}
		});
		return new ArrayList<BrmBranchInfo>(brmBranchInfoDTOMap.values());
	}

	public Map<String, BrmBranchInfo> getBrmBranchInfoDTOMap() {
		return brmBranchInfoDTOMap;
	}

	public void setBrmBranchInfoDTOMap(Map<String, BrmBranchInfo> brmBranchInfoDTOMap) {
		this.brmBranchInfoDTOMap = brmBranchInfoDTOMap;
	}

	public boolean isEditBancaInfo() {
		return editBancaInfo;
	}

	public void setEditBancaInfo(boolean editBancaInfo) {
		this.editBancaInfo = editBancaInfo;
	}

	public BancaStatus[] getBancaStatus() {
		return BancaStatus.values();
	}

}
