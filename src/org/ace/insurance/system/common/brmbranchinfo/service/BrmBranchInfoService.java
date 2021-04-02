package org.ace.insurance.system.common.brmbranchinfo.service;

import javax.annotation.Resource;

import org.ace.insurance.system.common.brmbranchinfo.BrmBranchInfo;
import org.ace.insurance.system.common.brmbranchinfo.persistence.BrmBranchInfoDAO;
import org.ace.insurance.system.common.brmbranchinfo.service.interfaces.IBrmBranchInfoService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("BrmBranchInfoService")
public class BrmBranchInfoService implements IBrmBranchInfoService {

	@Resource(name = "BrmBranchInfoDAO")
	private BrmBranchInfoDAO brmBranchInfoDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BrmBranchInfo brmBranchInfo) {
		try {
			brmBranchInfoDAO.insert(brmBranchInfo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a brmBranchInfo", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BrmBranchInfo brmBranchInfo) {
		try {
			brmBranchInfoDAO.delete(brmBranchInfo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a brmBranchInfo", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BrmBranchInfo brmBranchInfo) {
		try {
			brmBranchInfoDAO.update(brmBranchInfo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a brmBranchInfo", e);
		}
	}
}
