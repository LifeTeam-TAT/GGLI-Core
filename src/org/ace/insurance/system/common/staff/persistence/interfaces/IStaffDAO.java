package org.ace.insurance.system.common.staff.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.staff.Staff;
import org.ace.java.component.persistence.exception.DAOException;

public interface IStaffDAO {

	void save(Staff staff) throws DAOException;

	void delete(Staff staff) throws DAOException;

	Staff findById(String id) throws DAOException;

	List<Staff> findAll() throws DAOException;

	List<Staff> findStaffWithGGIOrganization(String id) throws DAOException;
}
