//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.13 at 03:53:24 PM MMT 
//


package org.ace.insurance.ws.customer.messages;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.ace.insurance.ws.common.model.versionindex.VersionIndexDto;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versionIndex" type="{http://common.model.web.insurance.ace.org}VersionIndex"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "versionNo"
})
@XmlRootElement(name = "QualificationUpdateRequest")
public class QualificationUpdateRequest implements Serializable {

	@XmlElement(required = true)
    protected int versionNo;

    /**
     * Gets the value of the versionIndex property.
     * 
     * @return
     *     possible object is
     *     {@link VersionIndex }
     *     
     */
    public int getVersionNo() {
        return versionNo;
    }

    /**
     * Sets the value of the versionIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionIndex }
     *     
     */
    public void setVersionNo(int value) {
        this.versionNo = value;
    }

}
