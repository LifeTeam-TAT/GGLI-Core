package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.filter.agent.STAFF001;
import org.ace.insurance.filter.agent.interfaces.ISTAFF_Filter;
import org.ace.insurance.filter.cirteria.CRISTAFF001;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "StaffDialogActionBean")
@ViewScoped
public class StaffDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{StaffService}")
	private IStaffService staffService;

	
	@ManagedProperty(value = "#{STAFF_Filter}")
	protected ISTAFF_Filter filter;

	private CRISTAFF001 staffCriteria;
	private String criteriaValue;
	private List<STAFF001> staffList;

	@PostConstruct
	public void init() {
		criteriaValue = "";
		staffList = filter.find(30);
	}

	public void search() {
		staffList = filter.find(staffCriteria, criteriaValue);
	}

	public void selectStaff(STAFF001 staff001) {
		Staff staff = staffService.findById(staff001.getId());
		PrimeFaces.current().dialog().closeDynamic(staff);
	}

	
	public CRISTAFF001[] getCriteriaItems() {
		return CRISTAFF001.values();
	}

	public IStaffService getStaffService() {
		return staffService;
	}

	public void setStaffService(IStaffService staffService) {
		this.staffService = staffService;
	}

	public ISTAFF_Filter getFilter() {
		return filter;
	}

	public void setFilter(ISTAFF_Filter filter) {
		this.filter = filter;
	}

	public CRISTAFF001 getStaffCriteria() {
		return staffCriteria;
	}

	public void setStaffCriteria(CRISTAFF001 staffCriteria) {
		this.staffCriteria = staffCriteria;
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public List<STAFF001> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<STAFF001> staffList) {
		this.staffList = staffList;
	}

	
}
