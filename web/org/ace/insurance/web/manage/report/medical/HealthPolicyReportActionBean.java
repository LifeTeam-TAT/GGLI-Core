package org.ace.insurance.web.manage.report.medical;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.report.medical.HealthPolicyReportDTO;
import org.ace.insurance.report.medical.service.interfaces.IHealthPolicyReportService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "HealthPolicyReportActionBean")
public class HealthPolicyReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{HealthPolicyReportService}")
	private IHealthPolicyReportService healthPolicyReportService;

	public void setHealthPolicyReportService(IHealthPolicyReportService healthPolicyReportService) {
		this.healthPolicyReportService = healthPolicyReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private HealthPolicyReportCriteria criteria;
	private List<HealthPolicyReportDTO> healthPolicyReportList;
	private List<Branch> branchList;
	private boolean accessBranches;
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		if (user.isAccessAllBranch()) {
			accessBranches = true;
			// branchList = branchService.findAllBranch();
		}
		branchList = branchService.findAllBranch();
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new HealthPolicyReportCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		healthPolicyReportList = healthPolicyReportService.findHealthPolicyReportDTO(criteria);
	}

	public void filter() {
		healthPolicyReportList = healthPolicyReportService.findHealthPolicyReportDTO(criteria);
	}

	public void exportHealthPolicyReport() {

		if (null != healthPolicyReportList && healthPolicyReportList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = "HealthPolicyReport.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				HealthPolicyReportExcel exportExcel = new HealthPolicyReportExcel();
				exportExcel.generate(op, healthPolicyReportList);
				getFacesContext().responseComplete();

			} catch (IOException e) {
				throw new SystemException(null, "Failed to export HealthPolicyReport.xlsx", e);
			}
		} else {
			addErrorMessage("healthProposalListForm:healthPolicyListTable", MessageId.THERE_IS_NO_DATA);
		}
	}

	public void returnCustomer(SelectEvent event) {
		criteria.setCustomer((Customer) event.getObject());
	}

	public void returnAgent(SelectEvent event) {
		criteria.setAgent((Agent) event.getObject());
	}

	public HealthPolicyReportCriteria getCriteria() {
		return criteria;
	}

	public List<HealthPolicyReportDTO> getHealthPolicyReportList() {
		return healthPolicyReportList;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public List<Branch> getBranchList() {
		return branchList;
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

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		criteria.setSalePoint(salePoint);
	}

	public void removeBranch() {
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		criteria.setEntity(entity);
		criteria.setBranch(null);
		criteria.setSalePoint(null);
	}

	public void removeEntity() {
		criteria.setEntity(null);
		criteria.setBranch(null);
		criteria.setSalePoint(null);

	}
}
