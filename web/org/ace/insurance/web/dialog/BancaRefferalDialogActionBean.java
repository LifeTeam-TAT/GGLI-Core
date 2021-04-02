package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.bancaRefferal.service.interfaces.IBancaRefferalService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "BancaRefferalDialogActionBean")
@ViewScoped
public class BancaRefferalDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BancaRefferalService}")
	private IBancaRefferalService bancaRefferalService;

	public void setBancaRefferalService(IBancaRefferalService bancaRefferalService) {
		this.bancaRefferalService = bancaRefferalService;
	}

	private List<BancaRefferal> bancaRefferalList;

	@PostConstruct
	public void init() {
//		if (isExistParam("BANCABRANCHID")) {
//			String branchId = (String) getParam("BANCABRANCHID");
//			bancaRefferalList = bancaRefferalService.findAllBancaRefferalActive(branchId);
//		}
		
		bancaRefferalList = bancaRefferalService.findAllBancaRefferalByAgentLicenseNo();
	}

	public List<BancaRefferal> getBancaRefferalList() {
		return bancaRefferalList;
	}

	public void selectBancaRefferal(BancaRefferal bancaRefferal) {
		BancaRefferal selectBancaRefferal = bancaRefferalService.findByBancaId(bancaRefferal.getId());
		PrimeFaces.current().dialog().closeDynamic(selectBancaRefferal);

	}

}
