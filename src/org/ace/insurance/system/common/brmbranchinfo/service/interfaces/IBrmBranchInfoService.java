package org.ace.insurance.system.common.brmbranchinfo.service.interfaces;

import org.ace.insurance.system.common.brmbranchinfo.BrmBranchInfo;

public interface IBrmBranchInfoService {

	public void insert(BrmBranchInfo brmBranchInfo);

	public void delete(BrmBranchInfo brmBranchInfo);

	public void update(BrmBranchInfo brmBranchInfo);

	// public BrmBranchInfo findByBancaBranchId(String bancaBranchId);
	//
	// public List<BrmBranchInfo> findAllBancaBranch();
	//
	// public boolean checkExistingBancaBranch(BrmBranchInfo bancaBranch);
	//
	// List<BrmBranchInfo> findAll() throws SystemException;
	//
	// public List<BrmBranchInfo> findAllBancaBranchByChannel(String channelId);
}
