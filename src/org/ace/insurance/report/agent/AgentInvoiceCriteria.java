package org.ace.insurance.report.agent;

/**
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/18
 */
import java.util.Date;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;

public class AgentInvoiceCriteria {
	private Agent agent;
	private InsuranceType insuranceType;
	private Date startDate;
	private Date endDate;
	private Branch branch;
	private String invoiceNo;

	public AgentInvoiceCriteria() {
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

}
