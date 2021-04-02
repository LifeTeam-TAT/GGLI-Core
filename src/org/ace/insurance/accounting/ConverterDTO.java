package org.ace.insurance.accounting;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.payment.TLF;

public class ConverterDTO {
	private List<TLF> tlfList;
	private List<InterfaceFile> interfaceFileList;

	public ConverterDTO() {
	}

	public List<TLF> getTlfList() {
		return tlfList;
	}

	public void addTLF(TLF tlf) {
		if (getTlfList() == null) {
			this.tlfList = new ArrayList<>();
		}
		this.tlfList.add(tlf);
	}

	public void setTlfList(List<TLF> tlfList) {
		this.tlfList = tlfList;
	}

	public List<InterfaceFile> getInterfaceFileList() {
		return interfaceFileList;
	}

	public void addInterfaceFile(InterfaceFile interfaceFile) {
		if (getInterfaceFileList() == null) {
			this.interfaceFileList = new ArrayList<>();
		}
		if (!this.interfaceFileList.contains(interfaceFile))
			this.interfaceFileList.add(interfaceFile);
	}

	public void setInterfaceFileList(List<InterfaceFile> interfaceFileList) {
		this.interfaceFileList = interfaceFileList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((interfaceFileList == null) ? 0 : interfaceFileList.hashCode());
		result = prime * result + ((tlfList == null) ? 0 : tlfList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConverterDTO other = (ConverterDTO) obj;
		if (interfaceFileList == null) {
			if (other.interfaceFileList != null)
				return false;
		} else if (!interfaceFileList.equals(other.interfaceFileList))
			return false;
		if (tlfList == null) {
			if (other.tlfList != null)
				return false;
		} else if (!tlfList.equals(other.tlfList))
			return false;
		return true;
	}

}
