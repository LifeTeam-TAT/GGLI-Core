package org.ace.insurance.renewal;

import javax.annotation.Resource;

import org.ace.insurance.autoRenewal.service.interfaces.IAutoRenewalService;

public class Scheduler {
	@Resource(name = "AutoRenewalService")
	private IAutoRenewalService autoRenewalService;

	public void run() {
		autoRenewalService.scheduler();
	}
}
