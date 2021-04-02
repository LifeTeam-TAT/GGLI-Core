package org.ace.ws.cargo.model.productPremiumRateLink;

import java.io.Serializable;

/**
 * @author Hlaing Win Tunn
 *
 */
public class ProductPremiumRateLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private double premiumRate;
	private String productId;
	private int version;

	public ProductPremiumRateLinkDTO(String id, double premiumRate, String productId, int version) {
		super();
		this.id = id;
		this.premiumRate = premiumRate;
		this.productId = productId;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getPremiumRate() {
		return premiumRate;
	}

	public void setPremiumRate(double premiumRate) {
		this.premiumRate = premiumRate;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
