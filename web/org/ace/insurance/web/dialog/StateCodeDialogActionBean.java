package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "StateCodeDialogActionBean")
@ViewScoped
public class StateCodeDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
	}

	private String criteria;
	private List<StateCode> stateCodeList;

	@PostConstruct
	public void init() {
		stateCodeList = stateCodeService.findAllStateCode();
	}

	public void selectStateCode(StateCode stateCode) {
		PrimeFaces.current().dialog().closeDynamic(stateCode);
	}

	public void search() {
		stateCodeList = stateCodeService.findByCriteria(criteria);
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public void setStateCodeList(List<StateCode> stateCodeList) {
		this.stateCodeList = stateCodeList;
	}

}
