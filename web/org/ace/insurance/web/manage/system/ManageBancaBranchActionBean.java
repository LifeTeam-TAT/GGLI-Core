/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaBranch.service.interfaces.IBancaBranchService;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.township.Township;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageBancaBranchActionBean")
public class ManageBancaBranchActionBean extends BaseBean {

	@ManagedProperty(value = "#{BancaBranchService}")
	private IBancaBranchService bancaBranchService;

	public void setBancaBranchService(IBancaBranchService bancaBranchService) {
		this.bancaBranchService = bancaBranchService;
	}

	private boolean createNew;
	private BancaBranch bancaBranch;
	private List<BancaBranch> bancaBranchInfoList;
	private List<BancaBranch> filterBancaBranchInfoList;

	@PostConstruct
	public void init() {
		createNewBancaBranchInfo();
		loadBancaBranchInfo();
	}

	public void loadBancaBranchInfo() {
		bancaBranchInfoList = bancaBranchService.findAllBancaBranch();
	}

	public void createNewBancaBranchInfo() {
		createNew = true;
		bancaBranch = new BancaBranch();
		// PrimeFaces.current().resetInputs(":branchEntryForm");
	}

	public void prepareUpdateBancaBranch(BancaBranch bancaBranch) {
		createNew = false;
		this.bancaBranch = bancaBranch;
	}

	public void addNewBancaBranch() {
		try {
			bancaBranchService.insert(bancaBranch);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, bancaBranch.getName());
			createNewBancaBranchInfo();
			loadBancaBranchInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaBranchInfo();
	}

	public void updateBancaBranch() {
		try {
			bancaBranchService.update(bancaBranch);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, bancaBranch.getName());
			createNewBancaBranchInfo();
			loadBancaBranchInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaBranchInfo();
	}

	public void deleteBancaBranch(BancaBranch bancaBranch) {
		try {
			bancaBranchService.delete(bancaBranch);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, bancaBranch.getName());
			createNewBancaBranchInfo();
			loadBancaBranchInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaBranchInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public BancaBranch getBancaBranch() {
		return bancaBranch;
	}

	public void setBancaBranch(BancaBranch bancaBranch) {
		this.bancaBranch = bancaBranch;
	}

	public List<BancaBranch> getBancaBranchInfoList() {
		return bancaBranchInfoList;
	}

	public void setBancaBranchInfoList(List<BancaBranch> bancaBranchInfoList) {
		this.bancaBranchInfoList = bancaBranchInfoList;
	}

	public List<BancaBranch> getFilterBancaBranchInfoList() {
		return filterBancaBranchInfoList;
	}

	public void setFilterBancaBranchInfoList(List<BancaBranch> filterBancaBranchInfoList) {
		this.filterBancaBranchInfoList = filterBancaBranchInfoList;
	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		bancaBranch.setChannel(channel);
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		bancaBranch.setTownship(township);
	}

	public void removeChannel() {
		bancaBranch.setChannel(null);
	}

}
