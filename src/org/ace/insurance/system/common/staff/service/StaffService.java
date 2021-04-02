package org.ace.insurance.system.common.staff.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.persistence.interfaces.IStaffDAO;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("StaffService")
public class StaffService implements IStaffService{

	@Resource(name = "StaffDAO")
	private IStaffDAO staffDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(Staff staff) {
		staffDAO.save(staff);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Staff staff) {
		staffDAO.delete(staff);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Staff findById(String id) {
		return staffDAO.findById(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Staff> findAll() {
		return staffDAO.findAll();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Staff staff) {
		staffDAO.save(staff);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Staff> findStaffWithGGIOrganization(String id) {
		if (id != null) {
			return staffDAO.findStaffWithGGIOrganization(id);
		}
		return null;
	}
}
