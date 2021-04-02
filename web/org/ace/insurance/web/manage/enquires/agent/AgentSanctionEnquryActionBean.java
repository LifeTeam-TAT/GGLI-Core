package org.ace.insurance.web.manage.enquires.agent;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.report.agent.AgentComissionInfo;
import org.ace.insurance.report.agent.AgentSanctionCriteria;
import org.ace.insurance.report.agent.persistence.AgentSanctionDTO;
import org.ace.insurance.report.agent.service.interfaces.IAgentSanctionService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.user.User;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AgentSanctionEnquryActionBean")
public class AgentSanctionEnquryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{AgentSanctionService}")
	private IAgentSanctionService agentSanctionService;

	public void setAgentSanctionService(IAgentSanctionService agentSanctionService) {
		this.agentSanctionService = agentSanctionService;
	}

	@ManagedProperty(value = "#{UserProcessService}")
	private IUserProcessService userProcessService;

	public void setUserProcessService(IUserProcessService userProcessService) {
		this.userProcessService = userProcessService;
	}

	@ManagedProperty(value = "#{IDConfigLoader}")
	protected IDConfigLoader configLoader;

	public void setConfigLoader(IDConfigLoader configLoader) {
		this.configLoader = configLoader;
	}

	private User user;
	private boolean accessBranch;
	private List<AgentSanctionDTO> agentSanctionDTOs;
	private AgentSanctionCriteria criteria;

	@PostConstruct
	public void init() {
		user = userProcessService.getLoginUser();
		if (user.isAccessAllBranch()) {
			accessBranch = true;
		}
		reset();
	}

	public void reset() {
		criteria = new AgentSanctionCriteria();
		criteria.setBranch(user.getBranch());
		criteria.setSanctionNo(null);
		agentSanctionDTOs = new ArrayList<AgentSanctionDTO>();
	}

	public void filter() {
		agentSanctionDTOs = agentSanctionService.findAgentCommissionByEnquiry(criteria);
	}

	private final String reportName = "AgentSanctionEnquiryReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}

	public void generateReport(AgentSanctionDTO agentSanctionDTO) {
		try {
			Map<String, List<AgentComissionInfo>> agentComissionMap = new HashMap<String, List<AgentComissionInfo>>();
			List<AgentComissionInfo> agentComissionInfoList = agentSanctionService.findBySanctionNo(agentSanctionDTO);
			agentComissionMap.put("SANCTION", agentComissionInfoList);
			agentSanctionService.generateReport(agentComissionMap, criteria, true, dirPath, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public InsuranceType[] getInsuranceTypes() {
		InsuranceType[] insuranceTypes = { InsuranceType.LIFE, InsuranceType.MEDICAL, InsuranceType.GROUP_FARMER, InsuranceType.HEALTH, InsuranceType.MICRO_HEALTH,
				InsuranceType.CRITICAL_ILLNESS, InsuranceType.PERSON_TRAVEL};
		return insuranceTypes;
	}

	public List<AgentSanctionDTO> getAgentSanctionDTOs() {
		RegNoSorter<AgentSanctionDTO> regNoSorter = new RegNoSorter<AgentSanctionDTO>(agentSanctionDTOs);
		return regNoSorter.getSortedList();
	}

	public void setAgentSanctionDTOs(List<AgentSanctionDTO> agentSanctionDTOs) {
		this.agentSanctionDTOs = agentSanctionDTOs;
	}

	public AgentSanctionCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(AgentSanctionCriteria criteria) {
		this.criteria = criteria;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public boolean isAccessBranch() {
		return accessBranch;
	}

}
