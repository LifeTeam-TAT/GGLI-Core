//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.13 at 03:53:24 PM MMT 
//


package org.ace.insurance.ws.customer.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.ace.insurance.ws.customer.model.QualificationUpdate;


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
 *         &lt;element name="qualificationUpdateList" type="{http://customer.model.web.insurance.ace.org}QualificationUpdate" maxOccurs="unbounded"/>
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
    "qualificationUpdateList",
    "latestVersionNo"
    
})
@XmlRootElement(name = "QualificationUpdateResponse")
public class QualificationUpdateResponse implements Serializable {

    @XmlElement(required = true)
    protected List<QualificationUpdate> qualificationUpdateList;
    @XmlElement(required = true)
    protected int latestVersionNo;
    
    
    public int getLatestVersionNo() {
		return latestVersionNo;
	}

	public void setLatestVersionNo(int latestVersionNo) {
		this.latestVersionNo = latestVersionNo;
	}
    /**
     * Gets the value of the qualificationUpdateList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qualificationUpdateList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQualificationUpdateList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QualificationUpdate }
     * 
     * 
     */
    public List<QualificationUpdate> getQualificationUpdateList() {
        if (qualificationUpdateList == null) {
            qualificationUpdateList = new ArrayList<QualificationUpdate>();
        }
        return this.qualificationUpdateList;
    }

	public void setQualificationUpdateList(
			List<QualificationUpdate> qualificationUpdateList) {
		this.qualificationUpdateList = qualificationUpdateList;
	}
    
}