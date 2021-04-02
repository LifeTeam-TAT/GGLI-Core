package org.ace.java.component.persistence.interfaces;

import java.util.List;

import org.ace.java.component.persistence.exception.DAOException;

public interface IDataRepository<T> {

	public void insert(Object object) throws DAOException;

	public T update(T param) throws DAOException;

	public void delete(Object object) throws DAOException;

	public void deleteById(Class<T> paramClass, String id) throws DAOException;

	public T findById(Class<T> paramClass, Object paramObject) throws DAOException;

	public boolean checkCodeNo(Class<T> paramClass, String codeNo) throws DAOException;

	public List<T> findAll(Class<T> paramClass) throws DAOException;

	public List<T> findAllParamDTO(Class<T> paramClass) throws DAOException;

	public List<T> findLimitedRange(Class<T> paramClass, int start, int limit) throws DAOException;

	public Object findByCode(Class<T> paramClass, String code) throws DAOException;

	public List<T> findByParentId(Class<T> paramClass, String parentId) throws DAOException;

	public int findDuplicateByMonth(Class<T> paramClass, int year, int month, String companyID) throws DAOException;

	public void deleteDuplicatesByMonth(Class<T> paramClass, int year, int month, String companyID) throws DAOException;

	public List<T> findByType(Class<T> paramClass, Enum<?> type) throws DAOException;
}
