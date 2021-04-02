package org.ace.insurance.filter.life;

public class LifeFilterCriteria {
	public String value;
	public String productId;
	public LifeFilterItem item;

	public LifeFilterCriteria() {
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public LifeFilterItem getItem() {
		return item;
	}

	public void setItem(LifeFilterItem item) {
		this.item = item;
	}

	public enum LifeFilterItem {
		POLICY_NO("Policy No"), CUSTOMER_NAME("Customer"), ORGANIZATION_NAME("Organization");
		private String label;

		private LifeFilterItem(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}
}
