package org.ace.insurance.system.productinformation.photoimage.persistence.interfaces;
import java.util.List;

import org.ace.insurance.system.productinformation.photoimage.PhotoImage;
import org.ace.java.component.persistence.exception.DAOException;

public interface IPhotoImageDAO {
	
	public void insert(PhotoImage photoImage) throws DAOException;
	
	public PhotoImage update(PhotoImage photoImage) throws DAOException;
	
	public void delete(PhotoImage photoImage) throws DAOException;
	
	public PhotoImage findById(String id) throws DAOException;
	
	public List<PhotoImage> findAll() throws DAOException;
	
	public PhotoImage updatePhotoImage(PhotoImage photoImage)throws DAOException;
}
