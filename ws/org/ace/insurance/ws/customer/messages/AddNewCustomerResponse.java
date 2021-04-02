package org.ace.insurance.ws.customer.messages;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.ace.insurance.ws.customer.model.CountryDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "result",
    "message"
})
@XmlRootElement(name = "AddNewCustomerResponse")
public class AddNewCustomerResponse implements Serializable {

	@XmlElement(required = true)
	private int result;
	@XmlElement(required = true)
	private String message;
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	
}
