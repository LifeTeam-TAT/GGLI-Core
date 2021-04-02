package org.ace.insurance.ws.customer.messages;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.ws.customer.model.CustomerDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AddNewCustomerRequest")
public class AddNewCustomerRequest implements Serializable {

	@XmlElement(required = true)
	private CustomerDto customer;

	public CustomerDto getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDto customer) {
		this.customer = customer;
	}
}
