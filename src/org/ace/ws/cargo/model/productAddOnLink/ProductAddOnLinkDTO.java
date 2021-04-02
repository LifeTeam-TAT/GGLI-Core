package org.ace.ws.cargo.model.productAddOnLink;

import java.io.Serializable;

/**
 * @author Hlaing Win Tunn
 *
 */
public class ProductAddOnLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String productId;
	private String addOnId;
	
	public ProductAddOnLinkDTO(){
		
	}

	public ProductAddOnLinkDTO(String productId, String addOnId) {
		super();
		this.productId = productId;
		this.addOnId = addOnId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getAddOnId() {
		return addOnId;
	}

	public void setAddOnId(String addOnId) {
		this.addOnId = addOnId;
	}
	
	
	
	
  
}
