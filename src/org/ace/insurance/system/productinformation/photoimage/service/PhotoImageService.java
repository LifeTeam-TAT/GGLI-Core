package org.ace.insurance.system.productinformation.photoimage.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.productinformation.photoimage.PhotoImage;
import org.ace.insurance.system.productinformation.photoimage.persistence.interfaces.IPhotoImageDAO;
import org.ace.insurance.system.productinformation.photoimage.service.interfaces.IPhotoImageService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "PhotoImageService")
public class PhotoImageService extends BaseService implements IPhotoImageService {

	@Resource(name = "PhotoImageDAO")
	private IPhotoImageDAO photoImageDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewPhotoAttach(PhotoImage photoImage) {
		try {
			photoImageDAO.insert(photoImage);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new PhotoImage", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updatePhotoAttach(PhotoImage photoImage) {
		try {
			photoImageDAO.update(photoImage);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update PhotoImage", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deletePhotoAttach(PhotoImage photoImage) {
		try {
			photoImageDAO.delete(photoImage);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a PhotoImage", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public PhotoImage findPhotoAttachById(String id) {
		PhotoImage result = null;
		try {
			result = photoImageDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a PhotoImage (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<PhotoImage> findAllPhotoAttach() {
		List<PhotoImage> result = null;
		try {
			result = photoImageDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of PhotoImage)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewAgentWithAttachment(String filePath, PhotoImage photoImage) {
		// TODO Auto-generated method stub

	}

}
