package org.ace.insurance.system.common.relationshiptype.services;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.dao.interfaces.IRelationShipTypeDAO;
import org.ace.insurance.system.common.relationshiptype.service.interfaces.IRelationShipTypeService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("RelationShipTypeService")
public class RelationShipTypeService implements IRelationShipTypeService {

	@Resource(name = "RelationShipTypeDAO")
	IRelationShipTypeDAO relationshipTypeDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(RelationShipType relationshiptype) {
		try {
			relationshipTypeDAO.insert(relationshiptype);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a relationshiptype", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(RelationShipType relationshiptype) {
		try {
			relationshipTypeDAO.delete(relationshiptype);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a relationshiptype", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(RelationShipType relationshiptype) {
		try {
			relationshipTypeDAO.update(relationshiptype);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a entity", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<RelationShipType> findAllRelationShipType() {
		List<RelationShipType> result = null;
		try {
			result = relationshipTypeDAO.findAllRelationShipType();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a entity", e);
		}
		return result;
	}

}
