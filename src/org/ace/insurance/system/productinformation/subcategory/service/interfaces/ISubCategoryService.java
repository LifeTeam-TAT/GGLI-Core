package org.ace.insurance.system.productinformation.subcategory.service.interfaces;
import java.util.List;

import org.ace.insurance.system.productinformation.subcategory.SubCategory;

public interface ISubCategoryService {
	
	public void addNewSubCategory(SubCategory subCategory) ;

	public void updateSubCategory(SubCategory subCategory) ;

	public void deleteSubCategory(SubCategory subCategory) ;

	public SubCategory findSubCategoryById(String id);

	public List<SubCategory> findAllSubCategory();
}