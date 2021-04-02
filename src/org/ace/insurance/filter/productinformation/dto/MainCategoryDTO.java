package org.ace.insurance.filter.productinformation.dto;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.product.Product;
import org.ace.insurance.web.common.CommonDTO;

public class MainCategoryDTO extends CommonDTO {

	private boolean updateFlag;
	private String tempId;
	private int order;
	private Product product;
	private PhotoImageDTO photoDTO;
	private List<NameTextDTO> nameTextDTOList;
	private List<SubCategoryDTO> subCategoryDTOList;

	public MainCategoryDTO() {
		tempId = "MAI-" + System.nanoTime();
	}

	public boolean isUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(boolean updateFlag) {
		this.updateFlag = updateFlag;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
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

	public List<SubCategoryDTO> getSubCategoryDTOList() {
		if (subCategoryDTOList == null) {
			subCategoryDTOList = new ArrayList<SubCategoryDTO>();
		}
		return subCategoryDTOList;
	}

	public void setSubCategoryDTOList(List<SubCategoryDTO> subCategoryDTOList) {
		this.subCategoryDTOList = subCategoryDTOList;
	}

	public PhotoImageDTO getPhotoDTO() {
		return photoDTO;
	}

	public void setPhotoDTO(PhotoImageDTO photoDTO) {
		this.photoDTO = photoDTO;
	}

	public void addNameTextDTO(NameTextDTO nameText) {
		if (!getNameTextDTOList().contains(nameText)) {
			nameTextDTOList.add(nameText);
		}
	}

	public void addSubCategoryDTO(SubCategoryDTO subCategory) {
		if (!getSubCategoryDTOList().contains(subCategory)) {
			subCategoryDTOList.add(subCategory);
		}
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
