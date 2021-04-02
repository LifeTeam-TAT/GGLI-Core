package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.CustomerType;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.service.interfaces.IPaymentTypeService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "PaymentTypeDialogActionBean")
@ViewScoped
public class PaymentTypeDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PaymentTypeService}")
	private IPaymentTypeService paymentTypeService;

	public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
		this.paymentTypeService = paymentTypeService;
	}

	private List<PaymentType> paymentTypeList;

	@PostConstruct
	public void init() {
		CustomerType customerType = (CustomerType) getParam("CUSTOMERTYPE");
		Product product = (Product) getParam("PRODUCT");
		List<PaymentType> tempList = paymentTypeService.findAllPaymentType();
		paymentTypeList = new ArrayList<PaymentType>();
		if (customerType != null && customerType.equals(CustomerType.INDIVIDUALCUSTOMER)) {
			for (PaymentType p : tempList) {
				if (p.getMonth() == 6 || p.getMonth() == 0) {
					paymentTypeList.add(p);
				}
			}
		} else if (product != null) {
			paymentTypeList.addAll(product.getPaymentTypeList());
		} else {
			paymentTypeList.addAll(tempList);
		}
	}

	public List<PaymentType> getPaymentTypeList() {
		return paymentTypeList;
	}

	public void selectPaymentType(PaymentType paymentType) {
		PrimeFaces.current().dialog().closeDynamic(paymentType);
	}
}
