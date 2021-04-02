/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.currency;

import java.io.Serializable;

public class Currency001 implements Serializable {
	private static final long serialVersionUID = -6992572646153666363L;

	private String id;
	private String prefix;
	private String currencyCode;

	public Currency001(String id, String prefix, String currencyCode) {
		this.id = id;
		this.prefix = prefix;
		this.currencyCode = currencyCode;
	}

	public String getId() {
		return id;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

}
