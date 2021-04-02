package org.ace.insurance.system.productinformation.photoimage.persistence;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.productinformation.photoimage.PhotoImage;
import org.ace.insurance.system.productinformation.photoimage.persistence.interfaces.IPhotoImageDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("PhotoImageDAO")
public class PhotoImageDAO extends BasicDAO implements IPhotoImageDAO{

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(PhotoImage photoImage) throws DAOException {
		try {
			em.persist(photoImage);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert PhotoImage", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PhotoImage update(PhotoImage photoImage) throws DAOException {
		try {
			photoImage = em.merge(photoImage);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update PhotoImage", pe);
		}
		return photoImage;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(PhotoImage photoImage) throws DAOException {
		try {
			photoImage = em.merge(photoImage);
			em.remove(photoImage);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update PhotoImage", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PhotoImage findById(String id) throws DAOException {
		PhotoImage result = null;
		try {
			result = em.find(PhotoImage.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PhotoImage", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PhotoImage> findAll() throws DAOException {
		List<PhotoImage> result = null;
		try {
			Query q = em.createNamedQuery("PhotoAttach.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of PhotoImage", pe);
		}
		return result;
	}

	@Override
	public PhotoImage updatePhotoImage(PhotoImage photoImage)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}


}