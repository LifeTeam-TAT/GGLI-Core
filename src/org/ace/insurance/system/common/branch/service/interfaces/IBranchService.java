/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.branch.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.branch.BRA001;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.java.component.SystemException;

public interface IBranchService {
	public void addNewBranch(Branch Branch);

	public void updateBranch(Branch Branch);

	public void deleteBranch(Branch Branch);

	public Branch findBranchById(String id);

	public List<BRA001> findAll_BRA001();

	public List<BRA001> findAll_BRA001ByStatus();

	public List<Branch> findAllBranch();

	public Branch findByBranchCode(String branchCode);

	public Branch findByCSCBranchCode(String cscBranchCode) throws SystemException;

	public boolean isAlreadyExitBranch(Branch branch);

	public boolean isAlreadyExistBranchCode(String branchName);

	public List<BRA001> findBranchByEntityIdAndBranchId(String entityId, String branchId);
}
