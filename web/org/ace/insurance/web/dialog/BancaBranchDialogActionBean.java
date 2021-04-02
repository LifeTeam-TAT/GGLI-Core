package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaBranch.service.interfaces.IBancaBranchService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "BancaBranchDialogActionBean")
@ViewScoped
public class BancaBranchDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{BancaBranchService}")
	private IBancaBranchService bancaBranchService;

	public void setBancaBranchService(IBancaBranchService bancaBranchService) {
		this.bancaBranchService = bancaBranchService;
	}

	private List<BancaBranch> bancaBranchList;

	@PostConstruct
	public void init() {
		if (isExistParam("CHANNELID")) {
			String channelId = (String) getParam("CHANNELID");
			bancaBranchList = bancaBranchService.findAllBancaBranchByChannel(channelId);
		} else {
			bancaBranchList = bancaBranchService.findAllBancaBranch();
		}

	}

	public List<BancaBranch> getBancaBranchList() {
		return bancaBranchList;
	}

	public void selectBancaBranch(BancaBranch bancaBranch) {
		BancaBranch selectedBancaBranch = bancaBranchService.findByBancaBranchId(bancaBranch.getId());
		PrimeFaces.current().dialog().closeDynamic(selectedBancaBranch);

	}
}
