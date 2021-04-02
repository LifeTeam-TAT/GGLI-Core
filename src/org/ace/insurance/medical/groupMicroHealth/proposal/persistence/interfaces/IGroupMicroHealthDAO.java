package org.ace.insurance.medical.groupMicroHealth.proposal.persistence.interfaces;

import java.util.List;

import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.enquires.medical.groupMicroHealth.GroupMicroHealthCriteria;
import org.ace.insurance.web.manage.medical.groupMicroHealth.policy.GroupMicroHealthDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IGroupMicroHealthDAO {

	GroupMicroHealth create(GroupMicroHealth groupMicroHealth);

	void update(GroupMicroHealth groupMicroHealth);

	void delete(GroupMicroHealth groupMicroHealth);

	List<GroupMicroHealth> findAll();

	GroupMicroHealth findById(String id) throws DAOException;

	List<GroupMicroHealthDTO> findAllPaymentCompleteDTO(GroupMicroHealthCriteria criteria) throws DAOException;

	List<GroupMicroHealthDTO> findALLProcessCompleteDTO(EnquiryCriteria criteria) throws DAOException;

}