package org.ace.insurance.system.common.coinsurancecompany;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.PermanentAddress;

public class COINCOMPANY001 {

	private String id;
	private String name;
	private String description;
	private PermanentAddress address;
	private ContentInfo contentInfo;

	public COINCOMPANY001(String id, String name, String description, PermanentAddress address,
			ContentInfo contentInfo) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.address = address;
		this.contentInfo = contentInfo;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public PermanentAddress getAddress() {
		return address;
	}

	public ContentInfo getContentInfo() {
		return contentInfo;
	}

}
