package org.ace.insurance.system.common.brmbranchinfo.persistence.interfaces;

import org.ace.insurance.system.common.brmbranchinfo.BrmBranchInfo;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBrmBranchInfoDAO {

	public void insert(BrmBranchInfo brmBranchInfo) throws DAOException;

	public void delete(BrmBranchInfo brmBranchInfo) throws DAOException;

	public void update(BrmBranchInfo brmBranchInfo) throws DAOException;

	// public BrmBranchInfo findById(String brmBranchInfo) throws DAOException;
	//
	// public List<BrmBranchInfo> findAllBancaBranch();
	//
	// List<BrmBranchInfo> findAllBancaBranchByChannel(String channelId) throws
	// DAOException;
	//
	// public boolean checkExistingBrmBranchInfo(BrmBranchInfo bancaBranch)
	// throws DAOException;
}
