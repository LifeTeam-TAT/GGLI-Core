package org.ace.insurance.system.productinformation.maincategory.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.productinformation.maincategory.MainCategory;
import org.ace.java.component.persistence.exception.DAOException;

public interface IMainCategoryDAO {
	
	public void insert(MainCategory mainCategory) throws DAOException;
	
	public MainCategory update(MainCategory mainCategory) throws DAOException;
	
	public void delete(MainCategory mainCategory) throws DAOException;
	
	public MainCategory findById(String id) throws DAOException;
	
	public List<MainCategory> findAll() throws DAOException;
	
}
