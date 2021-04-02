/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.township;

import java.io.Serializable;

public class Township001 implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String province;

	public Township001(String id, String name, String province) {
		this.id = id;
		this.name = name;
		this.province = province;
	}

	public Township001(Township township) {
		this.id = township.getId();
		this.name = township.getName();
		this.province = township.getProvince().getName();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getProvince() {
		return province;
	}

}