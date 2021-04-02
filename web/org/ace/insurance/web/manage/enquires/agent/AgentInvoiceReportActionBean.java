package org.ace.insurance.web.manage.enquires.agent;

import java.io.IOException;
import java.io.Serializable;
/**
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/11
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.report.agent.AgentInvoiceCriteria;
import org.ace.insurance.report.agent.AgentInvoiceReport;
import org.ace.insurance.report.agent.service.interfaces.IAgentInvoiceReportService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.service.interfaces.IAgentService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.user.User;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AgentInvoiceReportActionBean")
public class AgentInvoiceReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{AgentInvoiceReportService}")
	private IAgentInvoiceReportService agentInvoiceReportService;

	public void setAgentInvoiceReportService(IAgentInvoiceReportService agentInvoiceReportService) {
		this.agentInvoiceReportService = agentInvoiceReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@ManagedProperty(value = "#{AgentService}")
	private IAgentService agentService;

	public void setAgentService(IAgentService agentService) {
		this.agentService = agentService;
	}

	@ManagedProperty(value = "#{IDConfigLoader}")
	protected IDConfigLoader configLoader;

	public void setConfigLoader(IDConfigLoader configLoader) {
		this.configLoader = configLoader;
	}

	@ManagedProperty(value = "#{UserProcessService}")
	private IUserProcessService userProcessService;

	public void setUserProcessService(IUserProcessService userProcessService) {
		this.userProcessService = userProcessService;
	}

	private AgentInvoiceCriteria criteria;
	private AgentInvoiceReport agentIndi;
	private List<AgentCommission> agentCommissions;
	private List<AgentInvoiceReport> agentInvoiceReportList;
	private Branch branch;
	private User user;
	private boolean accessBranch;
	private Map<String, List<AgentCommission>> agentCommissionMap;

	@PostConstruct
	public void init() {
		agentCommissionMap = new HashMap<String, List<AgentCommission>>();
		user = userProcessService.getLoginUser();
		if (user.isAccessAllBranch()) {
			accessBranch = true;
		}
		resetCriteria();

	}

	private void resetCriteria() {
		criteria = new AgentInvoiceCriteria();
		if (configLoader.isCentralizedSystem()) {
			criteria.setBranch(user.getBranch());
		} else {
			Branch branch = branchService.findByBranchCode(configLoader.getBranchCode());
			criteria.setBranch(branch);
		}
		agentCommissions = new ArrayList<AgentCommission>();
		agentInvoiceReportList = new ArrayList<AgentInvoiceReport>();
		agentCommissionMap = new HashMap<String, List<AgentCommission>>();
	}

	public void reset() {
		resetCriteria();
	}

	public void filter() {
		agentCommissions = agentInvoiceReportService.findAgentInvoiceReports(criteria);
		populateAgentInvoiceReport();
	}

	public void populateAgentInvoiceReport() {
		agentInvoiceReportList = new ArrayList<AgentInvoiceReport>();
		agentCommissionMap = new HashMap<String, List<AgentCommission>>();
		String key = null;
		for (AgentCommission commission : agentCommissions) {
			key = commission.getAgent().getCodeNo() + commission.getInvoiceNo();
			if (agentCommissionMap.containsKey(key)) {
				agentCommissionMap.get(key).add(commission);
			} else {
				List<AgentCommission> commissionList = new ArrayList<AgentCommission>();
				commissionList.add(commission);
				agentCommissionMap.put(key, commissionList);
			}
		}
		for (Map.Entry<String, List<AgentCommission>> entry : agentCommissionMap.entrySet()) {
			AgentInvoiceReport report = new AgentInvoiceReport(entry.getValue());
			agentInvoiceReportList.add(report);
		}
	}

	public Map<String, List<AgentCommission>> getAgentCommissionMap() {
		return agentCommissionMap;
	}

	public void setAgentCommissionMap(Map<String, List<AgentCommission>> agentCommissionMap) {
		this.agentCommissionMap = agentCommissionMap;
	}

	public AgentInvoiceCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(AgentInvoiceCriteria criteria) {
		this.criteria = criteria;
	}

	public InsuranceType[] getInsuranceTypeList() {

		InsuranceType[] insuranceTypes = { InsuranceType.LIFE, InsuranceType.PERSONAL_ACCIDENT, InsuranceType.MEDICAL, InsuranceType.HEALTH, InsuranceType.MICRO_HEALTH,
				InsuranceType.CRITICAL_ILLNESS, InsuranceType.SHORT_ENDOWMENT_LIFE, InsuranceType.GROUP_FARMER, InsuranceType.FARMER, InsuranceType.PERSON_TRAVEL,
				InsuranceType.STUDENT_LIFE,InsuranceType.PUBLIC_TERM_LIFE,InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE, InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE, InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE };
		return insuranceTypes;
	}

	public List<AgentInvoiceReport> getAgentInvoiceReportList() {
		return agentInvoiceReportList;
	}

	public void setAgentInvoiceReportList(List<AgentInvoiceReport> agentInvoiceReportList) {
		this.agentInvoiceReportList = agentInvoiceReportList;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public AgentInvoiceReport getAgentIndi() {
		return agentIndi;
	}

	public void setAgentIndi(AgentInvoiceReport agentIndi) {
		this.agentIndi = agentIndi;
	}

	public boolean isAccessBranch() {
		return accessBranch;
	}

	private final String agentReportName = "AgentInvoiceReport";
	private final String agentPdfDirPath = "/pdf-report/" + agentReportName + "/" + System.currentTimeMillis() + "/";
	private final String agentDirPath = getSystemPath() + agentPdfDirPath;
	private final String agentFileName = agentReportName + ".pdf";

	public void generateAgentDetails(AgentInvoiceReport agentReport) {
		String key = agentReport.getAgentCodeNo() + agentReport.getInvoiceNo();
		agentService.generateAgentInvoice(agentCommissionMap.get(key), true, agentDirPath, agentFileName);
	}

	public String getAgentStream() {
		String fileFullPath = agentPdfDirPath + agentFileName;
		return fileFullPath;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(agentDirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		this.criteria.setAgent(agent);
	}

}
