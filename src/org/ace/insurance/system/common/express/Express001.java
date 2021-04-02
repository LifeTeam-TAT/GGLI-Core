/***************************************************************************************
 * @author <<Myo Thiha Kyaw>>
 * @Date 2014-06-18
 * @Version 1.0
 * @Purpose <<For Travel Insurance>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.express;

import java.io.Serializable;

public class Express001 implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String codeNo;
	private String regNo;

	public Express001(String id, String name, String codeNo, String regNo) {
		super();
		this.id = id;
		this.name = name;
		this.codeNo = codeNo;
		this.regNo = regNo;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public String getCodeNo() {
		return codeNo;
	}

	public String getRegNo() {
		return regNo;
	}

}