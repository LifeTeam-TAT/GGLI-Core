package org.ace.ws.cargo.model.product;

import java.io.Serializable;

public class ProductDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String productGroupId;
	private boolean autoCalculate;
	private float fixedValue;
	private String premiumRateType;
	private double baseSumInsured;
	private int standardExcess;
	private float firstCommission;
	private float renewalCommission;
	private double maxSumInsured;
	private double minSumInsured;
	private int maxTerm;
	private int minTerm;
	private String insuranceType;
	private String currencyId;
	private String multiCurPrefix;
	private int maxAge;
	private int minAge;
	private int maxHospDays;
	private int maxUnit;
	private int version;

	public ProductDTO() {
	}

	public ProductDTO(String id, String name, String productGroupId, boolean autoCalculate, int fixedValue, String premiumRateType, double baseSumInsured, int standardExcess,
			float firstCommission, float renewalCommission, double maxSumInsured, double minSumInsured, int maxTerm, int minTerm, String insuranceType, String currencyId,
			String multiCurPrefix, int maxAge, int minAge, int maxHospDays, int maxUnit, int version) {
		super();
		this.id = id;
		this.name = name;
		this.productGroupId = productGroupId;
		this.autoCalculate = autoCalculate;
		this.fixedValue = fixedValue;
		this.premiumRateType = premiumRateType;
		this.baseSumInsured = baseSumInsured;
		this.standardExcess = standardExcess;
		this.firstCommission = firstCommission;
		this.renewalCommission = renewalCommission;
		this.maxSumInsured = maxSumInsured;
		this.minSumInsured = minSumInsured;
		this.maxTerm = maxTerm;
		this.minTerm = minTerm;
		this.insuranceType = insuranceType;
		this.currencyId = currencyId;
		this.multiCurPrefix = multiCurPrefix;
		this.maxAge = maxAge;
		this.minAge = minAge;
		this.maxHospDays = maxHospDays;
		this.maxUnit = maxUnit;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAutoCalculate() {
		return autoCalculate;
	}

	public void setAutoCalculate(boolean autoCalculate) {
		this.autoCalculate = autoCalculate;
	}

	public float getFixedValue() {
		return fixedValue;
	}

	public void setFixedValue(float fixedValue) {
		this.fixedValue = fixedValue;
	}

	public int getStandardExcess() {
		return standardExcess;
	}

	public void setStandardExcess(int standardExcess) {
		this.standardExcess = standardExcess;
	}

	public float getFirstCommission() {
		return firstCommission;
	}

	public void setFirstCommission(float firstCommission) {
		this.firstCommission = firstCommission;
	}

	public float getRenewalCommission() {
		return renewalCommission;
	}

	public void setRenewalCommission(float renewalCommission) {
		this.renewalCommission = renewalCommission;
	}

	public double getMaxSumInsured() {
		return maxSumInsured;
	}

	public void setMaxSumInsured(double maxSumInsured) {
		this.maxSumInsured = maxSumInsured;
	}

	public double getMinSumInsured() {
		return minSumInsured;
	}

	public void setMinSumInsured(double minSumInsured) {
		this.minSumInsured = minSumInsured;
	}

	public int getMaxTerm() {
		return maxTerm;
	}

	public void setMaxTerm(int maxTerm) {
		this.maxTerm = maxTerm;
	}

	public int getMinTerm() {
		return minTerm;
	}

	public void setMinTerm(int minTerm) {
		this.minTerm = minTerm;
	}

	public String getMultiCurPrefix() {
		return multiCurPrefix;
	}

	public void setMultiCurPrefix(String multiCurPrefix) {
		this.multiCurPrefix = multiCurPrefix;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public int getMaxHospDays() {
		return maxHospDays;
	}

	public void setMaxHospDays(int maxHospDays) {
		this.maxHospDays = maxHospDays;
	}

	public int getMaxUnit() {
		return maxUnit;
	}

	public void setMaxUnit(int maxUnit) {
		this.maxUnit = maxUnit;
	}

	public String getPremiumRateType() {
		return premiumRateType;
	}

	public void setPremiumRateType(String premiumRateType) {
		this.premiumRateType = premiumRateType;
	}

	public double getBaseSumInsured() {
		return baseSumInsured;
	}

	public void setBaseSumInsured(double baseSumInsured) {
		this.baseSumInsured = baseSumInsured;
	}

	public String getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public String getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(String productGroupId) {
		this.productGroupId = productGroupId;
	}

	public String getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
