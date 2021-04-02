package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.PortCriteria;
import org.ace.insurance.common.PortCriteriaItems;
import org.ace.insurance.system.common.port.Port;
import org.ace.insurance.system.common.port.service.interfaces.IPortService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "PortDialogActionBean")
@ViewScoped
public class PortDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PortService}")
	private IPortService portService;

	public void setPortService(IPortService portService) {
		this.portService = portService;
	}

	private List<Port> portList;
	private String selectedCriteria;
	private List<SelectItem> portCriteriaItemList;
	private PortCriteria portCriteria;

	@PostConstruct
	public void init() {
		portCriteria = new PortCriteria();
		portCriteriaItemList = new ArrayList<SelectItem>();
		for (PortCriteriaItems criteriaItem : PortCriteriaItems.values()) {
			portCriteriaItemList.add(new SelectItem(criteriaItem.getLabel(), criteriaItem.getLabel()));
		}
		portList = portService.findPortByCriteria(portCriteria, 50);
	}

	public List<Port> getPortList() {
		return portList;
	}

	public void selectPort(Port port) {
		PrimeFaces.current().dialog().closeDynamic(port);
	}

	public void search() {
		portCriteria.setPortCriteria(null);
		for (PortCriteriaItems criteriaItem : PortCriteriaItems.values()) {
			if (criteriaItem.toString().equals(selectedCriteria)) {
				portCriteria.setPortCriteria(criteriaItem);
			}
		}
		portList = portService.findPortByCriteria(portCriteria, 50);
	}

	public String getSelectedCriteria() {
		return selectedCriteria;
	}

	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}

	public List<SelectItem> getPortCriteriaItemList() {
		return portCriteriaItemList;
	}

	public void setPortCriteriaItemList(List<SelectItem> portCriteriaItemList) {
		this.portCriteriaItemList = portCriteriaItemList;
	}

	public PortCriteria getPortCriteria() {
		return portCriteria;
	}

	public void setPortCriteria(PortCriteria portCriteria) {
		this.portCriteria = portCriteria;
	}

	public void setPortList(List<Port> portList) {
		this.portList = portList;
	}

	public void reset() {
		portCriteria.setPortCriteria(null);
		portList = portService.findPortByCriteria(portCriteria, 50);
		setSelectedCriteria(null);
		portCriteria.setCriteriaValue(null);
	}
}
