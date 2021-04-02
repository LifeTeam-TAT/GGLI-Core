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
import org.ace.insurance.report.agent.AgentComissionInfo;
import org.ace.insurance.report.agent.AgentSanctionCriteria;
import org.ace.insurance.report.agent.service.interfaces.IAgentSanctionService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AgentSanctionReportActionBean")
public class AgentSanctionReportActionBean<T extends IDataModel> extends BaseBean {
	@ManagedProperty(value = "#{AgentSanctionService}")
	private IAgentSanctionService agentSanctionService;

	public void setAgentSanctionService(IAgentSanctionService agentSanctionService) {
		this.agentSanctionService = agentSanctionService;
	}

	@ManagedProperty(value = "#{CurrencyService}")
	private ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	private boolean sanction = true;
	private AgentSanctionCriteria criteria;
	private List<AgentComissionInfo> agentSanctionList;
	private List<AgentComissionInfo> selectedList;
	private Map<String, List<AgentComissionInfo>> agentComissionMap;

	private final String reportName = "AgentSanctionReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	@PostConstruct
	public void init() {
		reset();
	}

	public void reset() {
		criteria = new AgentSanctionCriteria();
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
			agentComissionMap = new HashMap<String, List<AgentComissionInfo>>();
			List<AgentComissionInfo> commissionInfoList = null;
			for (AgentComissionInfo agent : selectedList) {
				key = agent.getAgentCode() + agent.getCurrencyCode();
				if (!agentComissionMap.containsKey(key)) {
					commissionInfoList = new ArrayList<AgentComissionInfo>();
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

	public AgentSanctionCriteria getCriteria() {
		return criteria;
	}

	public List<AgentComissionInfo> getAgentSanctionList() {
		return agentSanctionList;
	}

	public String getReportName() {
		return reportName;
	}

	public void setCriteria(AgentSanctionCriteria criteria) {
		this.criteria = criteria;
	}

	public InsuranceType[] getInsuranceTypes() {

		return new InsuranceType[] { InsuranceType.LIFE, InsuranceType.MEDICAL, InsuranceType.HEALTH, InsuranceType.MICRO_HEALTH, InsuranceType.CRITICAL_ILLNESS,
				InsuranceType.PERSONAL_ACCIDENT, InsuranceType.FARMER, InsuranceType.PERSON_TRAVEL, InsuranceType.SHORT_ENDOWMENT_LIFE, InsuranceType.GROUP_MICRO_HEALTH,
				InsuranceType.GROUP_FARMER, InsuranceType.STUDENT_LIFE ,InsuranceType.PUBLIC_TERM_LIFE,
				InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE, InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE, 
				InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE};
	}

	public boolean isSanction() {
		return sanction;
	}

	public List<AgentComissionInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<AgentComissionInfo> selectedList) {
		this.selectedList = selectedList;
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

}
