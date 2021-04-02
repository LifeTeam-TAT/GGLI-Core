package org.ace.insurance.web.manage.system;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageBancaActionBean")
public class ManageBancaActionBean extends BaseBean {

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaService;

	public void setBancaService(IBancaMethodService bancaService) {
		this.bancaService = bancaService;
	}

	private boolean createNew;
	private BancaMethod banca;
	private List<BancaMethod> bancaInfoList;
	private List<BancaMethod> filterBancaInfoList;

	@PostConstruct
	public void init() {
		createNewBancaInfo();
		loadBancaInfo();
	}

	public void loadBancaInfo() {
		bancaInfoList = bancaService.findAllBanca();
	}

	public void createNewBancaInfo() {
		createNew = true;
		banca = new BancaMethod();
		// PrimeFaces.current().resetInputs(":branchEntryForm");
	}

	public void prepareUpdateBanca(BancaMethod banca) {
		createNew = false;
		this.banca = banca;
	}

	public void addNewBanca() {
		try {
			bancaService.insert(banca);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, banca.getName());
			createNewBancaInfo();
			loadBancaInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaInfo();
	}

	public void updateBanca() {
		try {
			bancaService.update(banca);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, banca.getName());
			createNewBancaInfo();
			loadBancaInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaInfo();
	}

	public void deleteBanca(BancaMethod banca) {
		try {
			bancaService.delete(banca);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, banca.getName());
			createNewBancaInfo();
			loadBancaInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadBancaInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public BancaMethod getBanca() {
		return banca;
	}

	public void setBanca(BancaMethod banca) {
		this.banca = banca;
	}

	public List<BancaMethod> getBancaInfoList() {
		return bancaInfoList;
	}

	public void setBancaInfoList(List<BancaMethod> bancaInfoList) {
		this.bancaInfoList = bancaInfoList;
	}

	public List<BancaMethod> getFilterBancaInfoList() {
		return filterBancaInfoList;
	}

	public void setFilterBancaInfoList(List<BancaMethod> filterBancaInfoList) {
		this.filterBancaInfoList = filterBancaInfoList;
	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		banca.setChannel(channel);
	}

	public void removeChannel() {
		banca.setChannel(null);
	}

}
