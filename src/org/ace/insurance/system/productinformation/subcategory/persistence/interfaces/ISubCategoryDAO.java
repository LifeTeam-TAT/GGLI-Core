package org.ace.insurance.system.productinformation.subcategory.persistence.interfaces;
import java.util.List;

import org.ace.insurance.system.productinformation.subcategory.SubCategory;
import org.ace.java.component.persistence.exception.DAOException;

public interface ISubCategoryDAO {
	
	public void insert(SubCategory subCategory) throws DAOException;
	
	public SubCategory update(SubCategory subCategory) throws DAOException;
	
	public void delete(SubCategory subCategory) throws DAOException;
	
	public SubCategory findById(String id) throws DAOException;
	
	public List<SubCategory> findAll() throws DAOException;
}