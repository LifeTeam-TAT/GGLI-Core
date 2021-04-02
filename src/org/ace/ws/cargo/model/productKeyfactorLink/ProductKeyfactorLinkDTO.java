package org.ace.ws.cargo.model.productKeyfactorLink;

import java.io.Serializable;

/**
 * @author Hlaing Win Tunn
 *
 */
public class ProductKeyfactorLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String productId;
	private String keyfactorId;

	public ProductKeyfactorLinkDTO() {

	}

	public ProductKeyfactorLinkDTO(String productId, String keyfactorId) {
		super();
		this.productId = productId;
		this.keyfactorId = keyfactorId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getKeyfactorId() {
		return keyfactorId;
	}

	public void setKeyfactorId(String keyfactorId) {
		this.keyfactorId = keyfactorId;
	}

}
