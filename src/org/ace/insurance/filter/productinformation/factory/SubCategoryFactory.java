package org.ace.insurance.filter.productinformation.factory;

import org.ace.insurance.filter.productinformation.dto.NameTextDTO;
import org.ace.insurance.filter.productinformation.dto.SubCategoryDTO;
import org.ace.insurance.system.productinformation.subcategory.SubCategory;
import org.ace.insurance.system.productinformation.text.NameText;

public class SubCategoryFactory {

	public static SubCategory createSubCategory(SubCategoryDTO subCategoryDTO) {
		SubCategory subCategory = new SubCategory();
		if (subCategoryDTO != null) {
			subCategory.setId(subCategoryDTO.getId());
			subCategory.setOrder(subCategoryDTO.getOrder());
			if (subCategoryDTO.getPhotoImageDTO() != null) {
				subCategory.setImage(PhotoImageFactory.createPhotoImage(subCategoryDTO.getPhotoImageDTO()));
			}
			if (subCategoryDTO.getNameTextDTOList() != null && subCategoryDTO.getNameTextDTOList().size() != 0) {
				for (NameTextDTO name : subCategoryDTO.getNameTextDTOList()) {
					subCategory.addNameAndText(NameTextFactory.createNameText(name));
				}
			}
			subCategory.setVersion(subCategoryDTO.getVersion());
		}
		return subCategory;
	}

	public static SubCategoryDTO createSubCategoryDTO(SubCategory subCategory) {
		SubCategoryDTO subCategoryDTO = new SubCategoryDTO();
		if (subCategory != null) {
			subCategoryDTO.setId(subCategory.getId());
			subCategoryDTO.setOrder(subCategory.getOrder());
			if (subCategory.getTextList() != null && subCategory.getTextList().size() != 0) {
				for (NameText name : subCategory.getTextList()) {
					subCategoryDTO.addNameTextDTO(NameTextFactory.createNameTextDTO(name));
				}
			}
			if (subCategory.getImage() != null) {
				subCategoryDTO.setPhotoImageDTO(PhotoImageFactory.createPhotoImageDTO(subCategory.getImage()));
			}
			subCategoryDTO.setVersion(subCategory.getVersion());

		}
		return subCategoryDTO;
	}
}
