package org.ace.insurance.filter.productinformation.dto;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.web.common.CommonDTO;

public class SubCategoryDTO extends CommonDTO {

	private boolean updateSub;
	private boolean updateFlag;
	private int order;
	private String tempId;
	private PhotoImageDTO photoImageDTO;
	private List<NameTextDTO> nameTextDTOList;

	public SubCategoryDTO() {
		super();
		tempId = "SUB-" + System.nanoTime();
	}

	public boolean isUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(boolean updateFlag) {
		this.updateFlag = updateFlag;
	}

	public boolean isUpdateSub() {
		return updateSub;
	}

	public void setUpdateSub(boolean updateSub) {
		this.updateSub = updateSub;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public List<NameTextDTO> getNameTextDTOList() {
		if (nameTextDTOList == null) {
			nameTextDTOList = new ArrayList<NameTextDTO>();
		}
		return nameTextDTOList;
	}

	public void setNameTextDTOList(List<NameTextDTO> nameTextDTOList) {
		this.nameTextDTOList = nameTextDTOList;
	}

	public void addNameTextDTO(NameTextDTO nameText) {
		if (!getNameTextDTOList().contains(nameText)) {
			nameTextDTOList.add(nameText);
		}
	}

	public void setPhotoImageDTO(PhotoImageDTO photoImageDTO) {
		this.photoImageDTO = photoImageDTO;
	}

	public PhotoImageDTO getPhotoImageDTO() {
		return photoImageDTO;
	}

}
