/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.branch.BRA001;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.township.Township;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageBranchActionBean")
public class ManageBranchActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	@ManagedProperty(value = "#{BankService}")
	private IBankService bankService;

	public void setBankService(IBankService bankService) {
		this.bankService = bankService;
	}

	private List<BRA001> branchList;
	private boolean createNew;
	private Branch branch;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@PostConstruct
	public void init() {

		createNew = true;
		branch = new Branch();
		loadBranch();
	}

	public void loadBranch() {
		branchList = branchService.findAll_BRA001();
	}

	public void createNewBranch() {
		createNew = true;
		branch = new Branch();
		PrimeFaces.current().resetInputs(":branchEntryForm");
	}

	public void prepareUpdateBranch(BRA001 bra001) {
		this.branch = branchService.findBranchById(bra001.getId());
		createNew = false;
	}

	public void addNewBranch() {
		try {
			if (!isAlreadyExistBranch()) {
				branchService.addNewBranch(branch);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, branch.getName());
				branchList.add(new BRA001(branch));
				createNewBranch();
			} else {
				addWranningMessage(null, MessageId.ALREADY_ADD_BRANCH, "This branch");
			}
			loadBranch();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateBranch() {
		try {
			if (!isAlreadyExistBranch()) {
				branchService.updateBranch(branch);
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, branch.getName());
				createNewBranch();
				loadBranch();
			} else {
				addWranningMessage(null, MessageId.ALREADY_ADD_BRANCH, "This branch");
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean isAlreadyExistBranch() {
		return branchService.isAlreadyExitBranch(branch);
	}

	public String deleteBranch(BRA001 bra001) {
		try {
			this.branch = branchService.findBranchById(bra001.getId());
			branchService.deleteBranch(branch);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, branch.getName());
			branchList.remove(bra001);
			createNewBranch();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<BRA001> getBranchList() {
		return branchList;
	}

	public Branch getBranch() {
		return branch;
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		branch.setTownship(township);
	}

	public void returnEntity(SelectEvent event) {
		Entitys entitys = (Entitys) event.getObject();
		branch.setEntity(entitys);
	}

}
