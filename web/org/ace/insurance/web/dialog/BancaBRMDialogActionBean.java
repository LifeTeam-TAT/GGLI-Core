package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bancaBRM.BRM001;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBRM.service.interfaces.IBancaBRMService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "BancaBRMDialogActionBean")
@ViewScoped
public class BancaBRMDialogActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BancaBRMService}")
	private IBancaBRMService bancaBrmService;

	public void setBancaBrmService(IBancaBRMService bancaBrmService) {
		this.bancaBrmService = bancaBrmService;
	}

	private List<BRM001> bancaBrmList;

	@PostConstruct
	public void init() {
//		if (isExistParam("BANCABRANCHID")) {
//			String branchId = (String) getParam("BANCABRANCHID");
//			bancaBrmList = bancaBrmService.findByBranchId(branchId);
//		} else {
			bancaBrmList = bancaBrmService.findAll_BRM001();
//		}
	}

	public List<BRM001> getBancaBrmList() {
		return bancaBrmList;
	}

	public void selectBancaBRM(BRM001 brm001) {
		BancaBRM bancaBrm = bancaBrmService.findByBancaBRMId(brm001.getId());
		PrimeFaces.current().dialog().closeDynamic(bancaBrm);
	}
}
