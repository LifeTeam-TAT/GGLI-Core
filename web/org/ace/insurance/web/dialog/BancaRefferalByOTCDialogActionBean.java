package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bancaBranch.service.interfaces.IBancaBranchService;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.bancaRefferal.service.interfaces.IBancaRefferalService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "BancaRefferalByOTCDialogActionBean")
@ViewScoped
public class BancaRefferalByOTCDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BancaRefferalService}")
	private IBancaRefferalService bancaRefferalService;

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaMethodService;

	@ManagedProperty(value = "#{BancaBranchService}")
	private IBancaBranchService bancaBranchService;

	private List<BancaRefferal> bancaRefferalByOTCList;

	public void setBancaRefferalService(IBancaRefferalService bancaRefferalService) {
		this.bancaRefferalService = bancaRefferalService;
	}

	@PostConstruct
	public void init() {
//		bancaRefferalByOTCList = new ArrayList<BancaRefferal>();
//
//		if (isExistParam("BANCABRANCHID")) {
//			String branchId = (String) getParam("BANCABRANCHID");
//			bancaRefferalByOTCList = bancaRefferalService.findAllBancaRefferalByOTC(bancaBranchService.findByBancaBranchId(branchId).getId());
//		}
		
		bancaRefferalByOTCList = bancaRefferalService.findAllBancaRefferalByAgentLicenseNo();
	}

	public void selectBancaRefferalByOTC(BancaRefferal referral) {
		BancaRefferal selectBancaRefferalByOTC = bancaRefferalService.findByBancaId(referral.getId());
		PrimeFaces.current().dialog().closeDynamic(selectBancaRefferalByOTC);
	}

	public IBancaMethodService getBancaMethodService() {
		return bancaMethodService;
	}

	public void setBancaMethodService(IBancaMethodService bancaMethodService) {
		this.bancaMethodService = bancaMethodService;
	}

	public IBancaBranchService getBancaBranchService() {
		return bancaBranchService;
	}

	public void setBancaBranchService(IBancaBranchService bancaBranchService) {
		this.bancaBranchService = bancaBranchService;
	}

	public List<BancaRefferal> getBancaRefferalByOTCList() {
		return bancaRefferalByOTCList;
	}

	public void setBancaRefferalByOTCList(List<BancaRefferal> bancaRefferalByOTCList) {
		this.bancaRefferalByOTCList = bancaRefferalByOTCList;
	}
}
