package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.port.Port;
import org.ace.insurance.system.common.port.service.interfaces.IPortService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

@ViewScoped
@ManagedBean(name = "ManagePortActionBean")
public class ManagePortActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PortService}")
	private IPortService portService;

	public void setPortService(IPortService portService) {
		this.portService = portService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	private boolean createNew;
	private Port port;
	private LazyDataModelUtil<Port> lazyModel;
	private List<Port> portList;
	private String criteria;

	@PostConstruct
	public void init() {
		createNewPort();
		loadPort();
	}

	public void loadPort() {
		portList = portService.findAllPort();
		lazyModel = new LazyDataModelUtil<Port>(portList);
	}

	public void createNewPort() {
		createNew = true;
		port = new Port();
	}

	public void prepareUpdatePort(Port port) {
		createNew = false;
		this.port = port;
	}

	public void addNewPort() {
		try {
			Township township = port.getAddress().getTownship();
			if (township == null) {
				addInfoMessage(null, MessageId.REQUIRED_TOWNSHIP);
			} else {
				portService.addNewPort(port);
				loadPort();
				addInfoMessage(null, MessageId.INSERT_SUCCESS, port.getName());
				loadPort();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updatePort() {
		try {
			portService.updatePort(port);
			loadPort();
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, port.getName());
			createNewPort();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deletePort() {
		try {
			portService.deletePort(port);
			loadPort();
			addInfoMessage(null, MessageId.DELETE_SUCCESS, port.getName());
			createNewPort();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<Port> getPortList() {
		return portList;
	}

	public LazyDataModel<Port> getLazyModel() {
		return lazyModel;
	}

	public Port getPort() {
		return port;
	}

	public void setPort(Port port) {
		this.port = port;
	}

	public void setOrganizationList(List<Port> portList) {
		this.portList = portList;
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		port.getAddress().setTownship(township);
	}

	public void searchPort() {
		portList = portService.findByCriteria(criteria);
		lazyModel = new LazyDataModelUtil<Port>(portList);
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

}
