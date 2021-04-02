package org.ace.insurance.web.common;

import org.ace.insurance.system.common.deno.Deno;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DenoDTO {
	private String tempId;
	private int count;
	private Deno deno;

	public DenoDTO() {
		tempId = System.nanoTime() + "";
	}

	public DenoDTO(int count, Deno deno) {
		tempId = System.nanoTime() + "";
		this.count = count;
		this.deno = deno;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Deno getDeno() {
		return deno;
	}

	public void setDeno(Deno deno) {
		this.deno = deno;
	}

	@Override
	public boolean equals(Object object) {
		if (tempId.equals(((DenoDTO) object).getTempId())) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
