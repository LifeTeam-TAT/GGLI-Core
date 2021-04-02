package org.ace.java.component.service;

import java.util.Properties;

import javax.annotation.Resource;

import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.PeriodType;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;

public class BaseService {
	protected final String PROPOSAL = "PROPOSAL";
	protected final String INFORM = "INFORM";
	protected final String CONFIRMATION = "CONFIRMATION";
	protected final String PAYMENT = "PAYMENT";

	@Resource(name = "CustomIDGenerator")
	protected ICustomIDGenerator customIDGenerator;

	@Resource(name = "PREMIUM_CONFIG")
	private Properties premiumConfig;

	@SuppressWarnings("rawtypes")
	protected String getPrefix(Class cla) {
		return customIDGenerator.getPrefix(cla, true);
	}

	@SuppressWarnings("rawtypes")
	protected String getPrefix(Class cla, User user) {
		return customIDGenerator.getPrefix(cla, user, true);
	}

	@Resource(name = "UserProcessService")
	protected IUserProcessService userProcessService;

	public User getLoginUser() {
		return userProcessService.getLoginUser();
	}

	// TODO FIXME PSH
	protected boolean recalculatePremium(String workflow) {
		return Boolean.getBoolean(premiumConfig.getProperty(workflow));
	}

	public double getPremiumByPeriod(PeriodType periodType, int period, double prmAmt) {
		if (PeriodType.DAY.equals(periodType) && (period >= 1 && period <= 10)) {
			prmAmt = prmAmt / 8;
		} else if (PeriodType.DAY.equals(periodType) && (period >= 11 && period <= 15)) {
			prmAmt = prmAmt / 6;
		} else if ((PeriodType.MONTH.equals(periodType) && (period == 1)) || (PeriodType.DAY.equals(periodType) && (period >= 16 && period <= 31))) {
			prmAmt = prmAmt / 4;
		} else if (PeriodType.MONTH.equals(periodType) && (period == 2)) {
			prmAmt = (prmAmt / 8) * 3;
		} else if (PeriodType.MONTH.equals(periodType) && (period == 3)) {
			prmAmt = prmAmt / 2;
		} else if (PeriodType.MONTH.equals(periodType) && (period == 4)) {
			prmAmt = (prmAmt / 8) * 5;
		} else if (PeriodType.MONTH.equals(periodType) && (period == 5 || period == 6)) {
			prmAmt = (prmAmt / 4) * 3;
		} else {
			// do Nothing yet
		}
		return prmAmt;
	}
}
