package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.medical.process.Process;
import org.ace.insurance.medical.process.Process001;
import org.ace.insurance.medical.process.service.interfaces.IProcessService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "ProcessDialogActionBean")
@ViewScoped
public class ProcessDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProcessService}")
	private IProcessService processService;

	public void setProcessService(IProcessService processService) {
		this.processService = processService;
	}

	private List<Process001> processList;

	@PostConstruct
	public void init() {
		processList = processService.findAll();
	}

	public List<Process001> getProcessList() {
		return processList;
	}

	public void selectProcess(Process001 process001) {
		Process process = processService.findProcessById(process001.getId());
		PrimeFaces.current().dialog().closeDynamic(process);
	}
}
