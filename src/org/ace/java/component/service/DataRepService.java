package org.ace.java.component.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.persistence.interfaces.IDataRepository;
import org.ace.java.component.service.interfaces.IDataRepService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "DataRepService")
public class DataRepService<T> extends BaseService implements IDataRepService<T> {

	@Resource(name = "DataRepository")
	private IDataRepository<T> dataRepository;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public T insert(T param) throws SystemException {
		try {
			dataRepository.insert(param);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new " + param.getClass().getName(), e);
		}

		return param;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public T update(T param) throws SystemException {
		try {
			dataRepository.update(param);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update a " + param.getClass().getName(), e);
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(Object object) throws SystemException {
		try {
			dataRepository.delete(object);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete a " + object.getClass().getName(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public T findById(Class<T> paramClass, Object paramObject) throws SystemException {
		T result = null;
		try {
			result = dataRepository.findById(paramClass, paramObject);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a " + paramClass.getName() + "(ID : " + paramObject.toString() + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<T> findByParentId(Class<T> paramClass, String parentId) throws SystemException {
		List<T> result = null;
		try {
			result = dataRepository.findByParentId(paramClass, parentId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find by parent id " + parentId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public boolean checkCodeNo(Class<T> paramClass, String code) throws SystemException {
		boolean valid;
		try {
			valid = dataRepository.checkCodeNo(paramClass, code);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to check " + paramClass.getName() + "Code.", e);
		}
		return valid;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<T> findAll(Class<T> paramClass) throws SystemException {
		List<T> result = null;
		try {
			result = dataRepository.findAll(paramClass);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of " + paramClass.getSimpleName(), e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<T> findAllParamDTO(Class<T> paramClass) throws SystemException {
		List<T> result = null;
		try {
			result = dataRepository.findAllParamDTO(paramClass);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of " + paramClass.getSimpleName(), e);
		}
		return result;
	}

	public List<T> findLimitedRange(Class<T> paramClass, int start, int limit) throws SystemException {
		List<T> result = null;
		try {
			result = dataRepository.findLimitedRange(paramClass, start, limit);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find " + paramClass.getSimpleName() + " from Row " + start + " To Row" + (start + limit), e);
		}
		return result;
	}

	public Object findByCode(Class<T> paramClass, String code) throws SystemException {
		Object result = null;
		try {
			result = dataRepository.findByCode(paramClass, code);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find " + paramClass.getSimpleName() + " by code. ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int findDuplicateByMonth(Class<T> paramClass, Integer month, Integer year, String companyID) throws SystemException {
		int result;
		try {
			year = (int) year;
			month = (int) month;
			result = dataRepository.findDuplicateByMonth(paramClass, year, month, companyID);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find Duplicate Data by Month)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addList(List<T> objectList) throws SystemException {
		for (T param : objectList) {
			this.insert(param);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<T> findByType(Class<T> paramClass, Enum<?> type) throws SystemException {
		List<T> result = null;
		try {
			result = dataRepository.findByType(paramClass, type);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Object by Type)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String retrieveCustomNo(String systemConstant) throws SystemException {
		String codeNo = null;
		try {
			codeNo = customIDGenerator.getNextId(systemConstant, null, true);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to retrieve Custom Code No", e);
		}
		return codeNo;
	}

}
