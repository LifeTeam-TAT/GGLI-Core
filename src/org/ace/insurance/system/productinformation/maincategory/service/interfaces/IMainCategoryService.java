package org.ace.insurance.system.productinformation.maincategory.service.interfaces;

import java.util.List;

import org.ace.insurance.system.productinformation.maincategory.MainCategory;

public interface IMainCategoryService {

	public MainCategory addNewMainCategory(MainCategory mainCategory);

	public MainCategory updateMainCategory(MainCategory mainCategory);

	public void deleteMainCategory(MainCategory mainCategory);

	public MainCategory findMainCategoryById(String id);

	public List<MainCategory> findAllMainCategory();

}