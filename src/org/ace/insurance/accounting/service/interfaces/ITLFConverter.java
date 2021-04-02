package org.ace.insurance.accounting.service.interfaces;

import org.ace.insurance.accounting.InterfaceFile;
import org.ace.java.component.SystemException;

public interface ITLFConverter {
	public void convertToTLF(InterfaceFile interfaceFile, boolean ignoreSkip, String eNo) throws SystemException, Exception;
}
