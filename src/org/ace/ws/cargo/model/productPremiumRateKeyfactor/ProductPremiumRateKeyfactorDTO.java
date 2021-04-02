package org.ace.ws.cargo.model.productPremiumRateKeyfactor;

import java.io.Serializable;

/**
 * @author Hlaing Win Tunn
 *
 */
public class ProductPremiumRateKeyfactorDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String productPremiumRateId;
	private String keyfactorId;
	private double fromValue;
	private double toValue;
	private String value;
	private String referenceName;
	private int version;

	public ProductPremiumRateKeyfactorDTO() {

	}

	public ProductPremiumRateKeyfactorDTO(String id, String productPremiumRateId, String keyfactorId, double fromValue, double toValue, String value, String referenceName,
			int version) {
		super();
		this.id = id;
		this.productPremiumRateId = productPremiumRateId;
		this.keyfactorId = keyfactorId;
		this.fromValue = fromValue;
		this.toValue = toValue;
		this.value = value;
		this.referenceName = referenceName;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductPremiumRateId() {
		return productPremiumRateId;
	}

	public void setProductPremiumRateId(String productPremiumRateId) {
		this.productPremiumRateId = productPremiumRateId;
	}

	public String getKeyfactorId() {
		return keyfactorId;
	}

	public void setKeyfactorId(String keyfactorId) {
		this.keyfactorId = keyfactorId;
	}

	public double getFromValue() {
		return fromValue;
	}

	public void setFromValue(double fromValue) {
		this.fromValue = fromValue;
	}

	public double getToValue() {
		return toValue;
	}

	public void setToValue(double toValue) {
		this.toValue = toValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
