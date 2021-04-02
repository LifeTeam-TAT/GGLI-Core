package org.ace.insurance.system.productinformation.text.persistence.interfaces;
import java.util.List;

import org.ace.insurance.system.productinformation.text.NameText;
import org.ace.java.component.persistence.exception.DAOException;

public interface INameTextDAO {
	
	public void insert(NameText nameText) throws DAOException;
	
	public NameText update(NameText nameText) throws DAOException;
	
	public void delete(NameText nameText) throws DAOException;
	
	public NameText findById(String id) throws DAOException;
	
	public List<NameText> findAll() throws DAOException;
}