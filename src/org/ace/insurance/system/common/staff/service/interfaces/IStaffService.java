package org.ace.insurance.system.common.staff.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.staff.Staff;

public interface IStaffService {

	void save(Staff staff);

	void update(Staff staff);

	void delete(Staff staff);

	Staff findById(String id);

	List<Staff> findAll();
	
	List<Staff> findStaffWithGGIOrganization(String id);
}
