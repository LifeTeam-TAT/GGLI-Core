package org.ace.insurance.system.productinformation.photoimage.service.interfaces;
import java.util.List;

import org.ace.insurance.system.productinformation.photoimage.PhotoImage;

public interface IPhotoImageService {
	
	public void addNewPhotoAttach(PhotoImage attach) ;

	public void updatePhotoAttach(PhotoImage PhotoAttach) ;

	public void deletePhotoAttach(PhotoImage PhotoAttach) ;

	public PhotoImage findPhotoAttachById(String id);

	public List<PhotoImage> findAllPhotoAttach();
	
	public void addNewAgentWithAttachment(String filePath, PhotoImage photoAttach);

}