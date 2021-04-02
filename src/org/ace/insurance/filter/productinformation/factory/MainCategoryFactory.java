package org.ace.insurance.filter.productinformation.factory;

import org.ace.insurance.filter.productinformation.dto.MainCategoryDTO;
import org.ace.insurance.filter.productinformation.dto.NameTextDTO;
import org.ace.insurance.filter.productinformation.dto.SubCategoryDTO;
import org.ace.insurance.system.productinformation.maincategory.MainCategory;
import org.ace.insurance.system.productinformation.subcategory.SubCategory;
import org.ace.insurance.system.productinformation.text.NameText;

public class MainCategoryFactory {

	public static MainCategory createMainCategory(MainCategoryDTO mainCategoryDTO) {
		MainCategory mainCategory = new MainCategory();
		if (mainCategoryDTO != null) {
			mainCategory.setId(mainCategoryDTO.getId());
			mainCategory.setOrder(mainCategoryDTO.getOrder());
			mainCategory.setImage(PhotoImageFactory.createPhotoImage(mainCategoryDTO.getPhotoDTO()));
			mainCategory.setProduct(mainCategoryDTO.getProduct());
			for (NameTextDTO name : mainCategoryDTO.getNameTextDTOList()) {
				mainCategory.addNameAndText(NameTextFactory.createNameText(name));
			}
			for (SubCategoryDTO sub : mainCategoryDTO.getSubCategoryDTOList()) {
				mainCategory.addSubCategory(SubCategoryFactory.createSubCategory(sub));
			}
			mainCategory.setVersion(mainCategoryDTO.getVersion());
		}
		return mainCategory;
	}

	public static MainCategoryDTO createMainCategoryDTO(MainCategory mainCategory) {

		MainCategoryDTO mainCategoryDTO = new MainCategoryDTO();
		if (mainCategory != null) {
			mainCategoryDTO.setId(mainCategory.getId());
			mainCategoryDTO.setOrder(mainCategory.getOrder());
			mainCategoryDTO.setPhotoDTO(PhotoImageFactory.createPhotoImageDTO(mainCategory.getImage()));
			mainCategoryDTO.setProduct(mainCategory.getProduct());
			for (NameText name : mainCategory.getTextList()) {
				mainCategoryDTO.addNameTextDTO(NameTextFactory.createNameTextDTO(name));
			}
			for (SubCategory sub : mainCategory.getSubCategoryList()) {
				mainCategoryDTO.addSubCategoryDTO(SubCategoryFactory.createSubCategoryDTO(sub));
			}
			mainCategoryDTO.setVersion(mainCategory.getVersion());
		}
		return mainCategoryDTO;

	}
}
