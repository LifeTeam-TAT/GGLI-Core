package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.web.common.Param;
import org.ace.java.component.service.interfaces.IDataRepService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "KeyFactorDialogActionBean")
@ViewScoped
public class KeyFactorDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{DataRepService}")
	private IDataRepService<KeyFactor> keyFactorRepService;

	public void setKeyFactorRepService(IDataRepService<KeyFactor> keyFactorRepService) {
		this.keyFactorRepService = keyFactorRepService;
	}

	private List<KeyFactor> keyFactorList;
	private List<KeyFactor> selectedKeyFactorList;
	private String dialogType = "";

	@PostConstruct
	public void init() {
		dialogType = (String) getParam("DIALOGTYPE");
		keyFactorList = keyFactorRepService.findAll(KeyFactor.class);
		if (dialogType.equals("MULTIPLE")) {
			selectedKeyFactorList = (List<KeyFactor>) getParam(Param.KEYFACTOR_LIST);
			if (selectedKeyFactorList == null) {
				selectedKeyFactorList = new ArrayList<KeyFactor>();
			}
		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("DIALOGTYPE");
		removeParam(Param.KEYFACTOR_LIST);
	}

	public List<KeyFactor> getKeyFactorList() {
		return keyFactorList;
	}

	public List<KeyFactor> getSelectedKeyFactorList() {
		return selectedKeyFactorList;
	}

	public void setSelectedKeyFactorList(List<KeyFactor> selectedKeyFactorList) {
		this.selectedKeyFactorList = selectedKeyFactorList;
	}

	public void saveKeyFactor() {
		PrimeFaces.current().dialog().closeDynamic(selectedKeyFactorList);
	}

	public void selectKeyFactor(KeyFactor keyFactor) {
		PrimeFaces.current().dialog().closeDynamic(keyFactor);
	}

	public String getDialogType() {
		return dialogType;
	}

	public void setDialogType(String dialogType) {
		this.dialogType = dialogType;
	}

}
