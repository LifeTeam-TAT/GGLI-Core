package org.ace.insurance.web.manage.report.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.report.agent.service.interfaces.IStaffAgentSanctionService;
import org.ace.insurance.report.agent.view.StaffAgentCommissionInfo;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "StaffAgentSanctionReportActionBean")
public class StaffAgentSanctionReportActionBean<T extends IDataModel> extends BaseBean {
	@ManagedProperty(value = "#{StaffAgentSanctionService}")
	private IStaffAgentSanctionService agentSanctionService;

	public void setAgentSanctionService(IStaffAgentSanctionService agentSanctionService) {
		this.agentSanctionService = agentSanctionService;
	}

	@ManagedProperty(value = "#{CurrencyService}")
	private ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	private boolean sanction = true;
	private StaffSanctionCriteria criteria;
	private List<StaffAgentCommissionInfo> agentSanctionList;
	private List<StaffAgentCommissionInfo> selectedList;
	private Map<String, List<StaffAgentCommissionInfo>> agentComissionMap;

	private final String reportName = "StaffAgentSanctionReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	@PostConstruct
	public void init() {
		reset();
	}

	public void reset() {
		criteria = new StaffSanctionCriteria();
		if (criteria.getStartDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			criteria.setStartDate(cal.getTime());
		}
		if (criteria.getEndDate() == null) {
			Date endDate = new Date();
			criteria.setEndDate(endDate);
		}
		criteria.setInsuranceType(InsuranceType.LIFE);
		criteria.setCurrency(currencyService.findHomeCurrency());
		agentSanctionList = agentSanctionService.findAgents(criteria);
	}

	public void filter() {
		if (selectedList != null) {
			selectedList.clear();
		}
		agentSanctionList = agentSanctionService.findAgents(criteria);
		sanction = true;
	}

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}

	private void removeSelectedListAfterSanction() {
		agentSanctionList.removeAll(selectedList);
		selectedList.clear();
	}

	public void sanctionAgent() {
		if (selectedList.size() != 0) {
			String key = "";
			agentComissionMap = new HashMap<String, List<StaffAgentCommissionInfo>>();
			List<StaffAgentCommissionInfo> commissionInfoList = null;
			for (StaffAgentCommissionInfo agent : selectedList) {
				key = agent.getAgentCode() + agent.getCurrencyCode();
				if (!agentComissionMap.containsKey(key)) {
					commissionInfoList = new ArrayList<StaffAgentCommissionInfo>();
					commissionInfoList.add(agent);
					agentComissionMap.put(key, commissionInfoList);
				} else {
					agentComissionMap.get(key).add(agent);
				}
			}
			agentSanctionService.sanctionAgent(agentComissionMap, criteria.getCurrency());
			removeSelectedListAfterSanction();
			sanction = false;
			addInfoMessage(null, MessageId.AGENT_SANCTION);
		} else {
			addInfoMessage(null, MessageId.ATLEAST_ONE_AGENTCOMMISSION);
		}

	}

	public void generateAgentComission() throws Exception {
		try {
			agentSanctionService.generateReport(agentComissionMap, criteria, false, dirPath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Currency> getCurrencyList() {
		return currencyService.findAllCurrency();
	}

	public StaffSanctionCriteria getCriteria() {
		return criteria;
	}

	public List<StaffAgentCommissionInfo> getAgentSanctionList() {
		return agentSanctionList;
	}

	public void setAgentSanctionList(List<StaffAgentCommissionInfo> agentSanctionList) {
		this.agentSanctionList = agentSanctionList;
	}

	public String getReportName() {
		return reportName;
	}

	public void setCriteria(StaffSanctionCriteria criteria) {
		this.criteria = criteria;
	}

	public InsuranceType[] getInsuranceTypes() {

		return new InsuranceType[] { InsuranceType.LIFE, InsuranceType.MEDICAL, InsuranceType.HEALTH, InsuranceType.MICRO_HEALTH, InsuranceType.CRITICAL_ILLNESS,
				InsuranceType.PERSONAL_ACCIDENT, InsuranceType.FARMER, InsuranceType.PERSON_TRAVEL, InsuranceType.SHORT_ENDOWMENT_LIFE, InsuranceType.GROUP_MICRO_HEALTH,
				InsuranceType.GROUP_FARMER, InsuranceType.STUDENT_LIFE, InsuranceType.PUBLIC_TERM_LIFE, InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE,
				InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE, InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE };
	}

	public boolean isSanction() {
		return sanction;
	}

	public List<StaffAgentCommissionInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<StaffAgentCommissionInfo> selectedList) {
		this.selectedList = selectedList;
	}

	public void returnAgent(SelectEvent event) {
		Staff staff = (Staff) event.getObject();
		criteria.setStaff(staff);
	}

}
