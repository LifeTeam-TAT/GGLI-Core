package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaLIC.service.intefaces.IBancaLICService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "BancaLICDialogActionBean")
@ViewScoped
public class BancaLICDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BancaLICService}")
	private IBancaLICService bancaLICService;

	public void setBancaLICService(IBancaLICService bancaLICService) {
		this.bancaLICService = bancaLICService;
	}

	private List<BancaLIC> bancaCLIList;

	@PostConstruct
	public void init() {
		bancaCLIList = bancaLICService.findAllBancaLICActive();

	}

	public List<BancaLIC> getBancaCLIList() {
		return bancaCLIList;
	}

	public void selectBancaCLI(BancaLIC bancaLIC) {
		BancaLIC selectBancaCLI = bancaLICService.findByBancaLICId(bancaLIC.getId());
		PrimeFaces.current().dialog().closeDynamic(selectBancaCLI);

	}
}
