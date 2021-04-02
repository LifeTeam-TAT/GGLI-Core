package org.ace.insurance.system.common.entitys.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.entitys.persistence.interfaces.IEntityDAO;
import org.ace.insurance.system.common.entitys.service.interfaces.IEntityService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("EnityService")
public class EnityService implements IEntityService {

	@Resource(name = "EntityDAO")
	IEntityDAO entityDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Entitys entitys) {
		try {
			entityDAO.insert(entitys);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a entity", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Entitys entitys) {
		try {
			entityDAO.delete(entitys);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a entity", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Entitys entitys) {
		try {
			entityDAO.update(entitys);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a entity", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Entitys> findAllEntitys() {
		List<Entitys> result = null;
		try {
			result = entityDAO.findAllEntitys();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a entity", e);
		}
		return result;
	}

}
