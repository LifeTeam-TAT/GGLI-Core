package org.ace.java.component.idgen.service.interfaces;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.user.User;
import org.ace.java.component.idgen.IDGen;
import org.ace.java.component.idgen.exception.CustomIDGeneratorException;

public interface ICustomIDGenerator {

	public IDGen updateIDGen(IDGen idGen) throws CustomIDGeneratorException;

	public String getNextIdForAutoRenewal(String key);

	public String getPrefixForAutoRenewal(Class cla);

	String getNextId(String key, String productCode, boolean isIgnoreBranch) throws CustomIDGeneratorException;

	String getNextId(String key, String productCode, Branch branch, boolean isIgnoreBranch) throws CustomIDGeneratorException;

	String getPrefix(Class cla, boolean isIgnoreBranch);

	String getPrefix(Class cla, User user, boolean isIgnoreBranch);

	IDGen getIDGen(String key, boolean isIgnoreBranch) throws CustomIDGeneratorException;

	String getNextIdWithBranchCode(String key, String productCode, Branch branch, boolean isIgnoreBranch) throws CustomIDGeneratorException;
}
