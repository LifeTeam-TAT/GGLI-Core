package org.ace.insurance.web.manage.system;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.organization.ORG001;
import org.ace.insurance.system.common.township.Township;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewGGIOrganizationActionBean")
public class AddNewGGIOrganizationActionBean extends BaseBean {

	@ManagedProperty(value = "#{GGIOrganizationService}")
	private IGGIOrganizationService organizationService;

	public IGGIOrganizationService getOrganizationService() {
		return organizationService;
	}

	public void setOrganizationService(IGGIOrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	private GGIOrganization ggiorganization;
	private boolean createNew;
	private List<ORG001> organizationInfoList;
	private List<GGIOrganization> filterorganizationInfoList;

	@PostConstruct
	public void init() {
		createNewGGIorganizationInfo();
		loadGGIorganizationInfo();
	}

	public void loadGGIorganizationInfo() {
		organizationInfoList = organizationService.findAll_ORG001();
	}

	public void createNewGGIorganizationInfo() {
		createNew = true;
		ggiorganization = new GGIOrganization();
	}

	public void prepareUpdateGGIOrganization(ORG001 org001) {
		createNew = false;
		GGIOrganization ggiOrg = organizationService.findOrganizationById(org001.getId());
		String email = ggiOrg.getContentInfo().getEmail().toLowerCase();
		ggiOrg.getContentInfo().setEmail(email);
		this.ggiorganization = ggiOrg;
	}

	public void addNewGGIOrganization() {
		try {
			organizationService.insert(ggiorganization);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, ggiorganization.getName());
			createNewGGIorganizationInfo();
			loadGGIorganizationInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadGGIorganizationInfo();
	}

	public void updateGGIOrganization() {
		try {
			organizationService.update(ggiorganization);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, ggiorganization.getName());
			createNewGGIorganizationInfo();
			loadGGIorganizationInfo();

		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadGGIorganizationInfo();
	}

	public void deleteGGIOrganization(ORG001 org001) {
		try {
			organizationService.delete(organizationService.findOrganizationById(org001.getId()));
			addInfoMessage(null, MessageId.DELETE_SUCCESS, ggiorganization.getName());
			createNewGGIorganizationInfo();
			loadGGIorganizationInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadGGIorganizationInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public GGIOrganization getGgiorganization() {
		return ggiorganization;
	}

	public void setGgiorganization(GGIOrganization ggiorganization) {
		this.ggiorganization = ggiorganization;
	}

	public List<ORG001> getOrganizationInfoList() {
		return organizationInfoList;
	}

	public void setOrganizationInfoList(List<ORG001> organizationInfoList) {
		this.organizationInfoList = organizationInfoList;
	}

	public List<GGIOrganization> getFilterorganizationInfoList() {
		return filterorganizationInfoList;
	}

	public void setFilterorganizationInfoList(List<GGIOrganization> filterorganizationInfoList) {
		this.filterorganizationInfoList = filterorganizationInfoList;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		ggiorganization.getAddress().setTownship(township);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		ggiorganization.setBranch(branch);
	}
}
