package org.ace.java.component.service.interfaces;

import java.util.List;

import org.ace.java.component.SystemException;

public interface IDataRepService<T> {

	public T insert(T param) throws SystemException;

	public T update(T param) throws SystemException;

	public void delete(Object object) throws SystemException;

	public T findById(Class<T> paramClass, Object paramObject) throws SystemException;

	public List<T> findAll(Class<T> paramClass) throws SystemException;

	public List<T> findAllParamDTO(Class<T> paramClass) throws SystemException;

	public List<T> findLimitedRange(Class<T> paramClass, int start, int limit) throws SystemException;

	public boolean checkCodeNo(Class<T> paramClass, String code) throws SystemException;

	public Object findByCode(Class<T> paramClass, String code) throws SystemException;

	public List<T> findByParentId(Class<T> paramClass, String parentId) throws SystemException;

	public int findDuplicateByMonth(Class<T> paramClass, Integer month, Integer year, String companyID) throws SystemException;

	public void addList(List<T> objectList) throws SystemException;

	public List<T> findByType(Class<T> paramClass, Enum<?> type) throws SystemException;

	public String retrieveCustomNo(String systemConstant) throws SystemException;

}
