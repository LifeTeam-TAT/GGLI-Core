/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.bankBranch.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bankBranch.BBRANCH001;
import org.ace.insurance.system.common.bankBranch.BankBranch;

public interface IBankBranchService {
	public void addNewBankBranch(BankBranch bankBranch) ;
	public void updateBankBranch(BankBranch bankBranch) ;
	public void deleteBankBranch(BankBranch bankBranch) ;
	public BankBranch findBankBranchById(String id);
	public List<BankBranch> findAllBankBranch();
	public List<BBRANCH001> findAll() ;
}
