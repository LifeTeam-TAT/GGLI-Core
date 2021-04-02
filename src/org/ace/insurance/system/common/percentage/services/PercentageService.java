package org.ace.insurance.system.common.percentage.services;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.percentage.Percentage;
import org.ace.insurance.system.common.percentage.dao.interfaces.IPercentageDAO;
import org.ace.insurance.system.common.percentage.service.interfaces.IPercentageService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("PercentageService")
public class PercentageService implements IPercentageService{

	@Resource(name = "PercentageDAO")
	private IPercentageDAO percentageDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Percentage percentage) {
		try {
			percentageDAO.insert(percentage);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a entity", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Percentage percentage) {
		try {
			percentageDAO.delete(percentage);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a entity", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Percentage percentage) {
		try {
			percentageDAO.update(percentage);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a entity", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Percentage> findAllPercentage() {
		List<Percentage> result = null;
		try {
			result = percentageDAO.findAllPercentage();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Percentage", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Percentage findPercentageWithRelationShip(String typeId, String productId) {
		try {
			return percentageDAO.findPercentageWithRelationShip(typeId,productId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Percentage", e);
		}
	}
}
