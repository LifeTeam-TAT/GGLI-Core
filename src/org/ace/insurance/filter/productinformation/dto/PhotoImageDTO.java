package org.ace.insurance.filter.productinformation.dto;

import java.util.Arrays;

import org.ace.insurance.web.common.CommonDTO;

public class PhotoImageDTO extends CommonDTO implements Comparable<PhotoImageDTO> {

	private String tempId;
	private String name;
	private byte[] image;
	private String imagePath;
	private int order;

	public PhotoImageDTO() {
		super();
		// this.tempId = "PHOTO" + System.nanoTime();
	}

	public PhotoImageDTO(String name, String imagePath, byte[] image) {
		super();
		this.name = name;
		this.image = image;
		this.imagePath = imagePath;
	}

	public PhotoImageDTO(String name, String imagePath, int order) {
		super();
		this.name = name;
		this.imagePath = imagePath;
		this.order = order;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(image);
		result = prime * result + ((imagePath == null) ? 0 : imagePath.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + order;
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhotoImageDTO other = (PhotoImageDTO) obj;
		if (!Arrays.equals(image, other.image))
			return false;
		if (imagePath == null) {
			if (other.imagePath != null)
				return false;
		} else if (!imagePath.equals(other.imagePath))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (order != other.order)
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
			return false;
		return true;
	}

	@Override
	public int compareTo(PhotoImageDTO o) {
		// TODO Auto-generated method stub
		return new Integer(order).compareTo(o.getOrder());
	}

}
