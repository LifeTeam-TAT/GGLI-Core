package org.ace.ws.cargo.model.productPaymentTypeLink;

import java.io.Serializable;

/**
 * @author Hlaing Win Tunn
 *
 */
public class ProductPaymentTypeLinkDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String productId;
	private String paymentTypeId;
	
	public ProductPaymentTypeLinkDTO(){
		
	}

	public ProductPaymentTypeLinkDTO(String productId, String paymentTypeId) {
		super();
		this.productId = productId;
		this.paymentTypeId = paymentTypeId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}
	
	

}
