package org.ace.insurance.filter.productinformation.factory;

import org.ace.insurance.filter.productinformation.dto.PhotoImageDTO;
import org.ace.insurance.system.productinformation.photoimage.PhotoImage;

public class PhotoImageFactory {

	public static PhotoImage createPhotoImage(PhotoImageDTO photoImageDTO) {
		PhotoImage photoImage = new PhotoImage();
		if (photoImageDTO != null) {
			photoImage.setId(photoImageDTO.getId());
			photoImage.setName(photoImageDTO.getName());
			photoImage.setImage(photoImageDTO.getImage());
			photoImage.setFilepath(photoImageDTO.getImagePath());
			photoImage.setOrder(photoImageDTO.getOrder());
			photoImage.setVersion(photoImageDTO.getVersion());
		}
		return photoImage;
	}

	public static PhotoImageDTO createPhotoImageDTO(PhotoImage photoImage) {
		PhotoImageDTO photoImageDTO = new PhotoImageDTO();
		if (photoImage != null) {
			photoImageDTO.setId(photoImage.getId());
			photoImageDTO.setName(photoImage.getName());
			photoImageDTO.setImage(photoImage.getImage());
			photoImageDTO.setImagePath(photoImage.getFilepath());
			photoImageDTO.setOrder(photoImage.getOrder());
			photoImageDTO.setVersion(photoImage.getVersion());
		}
		return photoImageDTO;
	}
}
