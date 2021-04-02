package org.ace.insurance.renewal;

import java.util.Date;

public class RenewalNotificationCriteria {

	private Date startDate;
	private Date endDate;
	private RenewalInsuType insuranceType;

	public RenewalNotificationCriteria() {
		insuranceType = RenewalInsuType.GROUPLIFE;
	}

	public enum RenewalInsuType {
		GROUPLIFE("GROUPLIFE");

		private String label;

		private RenewalInsuType(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}

	public RenewalInsuType[] getInsuTypes() {
		return RenewalInsuType.values();
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

	public RenewalInsuType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(RenewalInsuType insuranceType) {
		this.insuranceType = insuranceType;
	}

}
