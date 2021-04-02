package org.ace.insurance.web.manage.report.agent;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.AgentStatus;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.AgentCommissionDetailReport;
import org.ace.insurance.report.agent.service.interfaces.IAgentCommissionDetailService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AgentCommissionDetailReportActionBean")
public class AgentCommissionDetailReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{AgentCommissionDetailReportService}")
	private IAgentCommissionDetailService agentCommissionDetailService;

	public void setAgentCommissionDetailService(IAgentCommissionDetailService agentCommissionDetailService) {
		this.agentCommissionDetailService = agentCommissionDetailService;
	}

	private AgentCommissionDetailCriteria criteria;
	private List<AgentCommissionDetailReport> agentCommissionList;
	private final String reportName = "AgentCommissionDetailedReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		criteria = new AgentCommissionDetailCriteria();
		if (criteria.getStartDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			criteria.setStartDate(cal.getTime());
		}
		if (criteria.getEndDate() == null) {
			Date endDate = new Date();
			criteria.setEndDate(endDate);
		}
		// agentCommissionList =
		// agentCommissionDetailService.findAgentCommissionDetail(criteria);
	}

	public InsuranceType[] getInsuranceTypes() {

		InsuranceType[] insuranceType = { InsuranceType.LIFE, InsuranceType.PERSONAL_ACCIDENT, InsuranceType.PERSON_TRAVEL, InsuranceType.FARMER,
				InsuranceType.SHORT_ENDOWMENT_LIFE, InsuranceType.MICRO_HEALTH, InsuranceType.TRAVEL_INSURANCE, InsuranceType.CRITICAL_ILLNESS, InsuranceType.HEALTH };
		return insuranceType;
	}

	public AgentCommissionDetailCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(AgentCommissionDetailCriteria criteria) {
		this.criteria = criteria;
	}

	public List<AgentCommissionDetailReport> getAgentCommissionList() {
		return agentCommissionList;
	}

	public String reset() {
		return "agentCommissionDetailReport";
	}

	public void filter() {
		agentCommissionList = agentCommissionDetailService.findAgentCommissionDetail(criteria);
	}

	public void setAgentCommissionList(List<AgentCommissionDetailReport> agentCommissionList) {
		this.agentCommissionList = agentCommissionList;
	}

	public AgentStatus[] getAgentStatues() {
		return AgentStatus.values();
	}

	public double totalCommission() {
		double totalComm = 0.0;
		for (AgentCommissionDetailReport a : agentCommissionList) {
			totalComm += a.getCommission();
		}
		return totalComm;
	}

	public String getStream() {
		String fileFullPath = pdfDirPath + fileName;
		return fileFullPath;
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			List<AgentCommissionDetailReport> reports = getAgentCommissionList();
			agentCommissionDetailService.generateReport(reports, dirPath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		criteria.setEntity(entity);
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
		criteria.setSalePoint(null);
	}

	public void selectSalePoint() {

		selectSalePointByBranch(criteria.getBranch() == null ? "" : criteria.getBranch().getId());
	}

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(criteria.getEntity() == null ? "" : criteria.getEntity().getId(), user.getBranch().getId());

	}

	public void removeBranch() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void removeEntity() {
		criteria.setEntity(null);
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
	}

}
