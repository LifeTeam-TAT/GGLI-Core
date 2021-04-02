package org.ace.insurance.ws.customer.messages;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.ace.insurance.ws.customer.model.CustomerRequirements;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GetCustomerRequirementsResponse")
public class GetCustomerRequirementsResponse implements Serializable{
	@XmlElement(required = true)
    protected CustomerRequirements customerRequirements;

    /**
     * Gets the value of the customerRequirements property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerRequirements }
     *     
     */
    public CustomerRequirements getCustomerRequirements() {
        return customerRequirements;
    }

    /**
     * Sets the value of the customerRequirements property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerRequirements }
     *     
     */
    public void setCustomerRequirements(CustomerRequirements value) {
        this.customerRequirements = value;
    }
}
