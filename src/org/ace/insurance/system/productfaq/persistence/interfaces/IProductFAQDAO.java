package org.ace.insurance.system.productfaq.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.productfaq.ProductFAQ;

public interface IProductFAQDAO {

	public void insert(ProductFAQ productFAQ);

	public ProductFAQ update(ProductFAQ productFAQ);

	public ProductFAQ findById(String id);

	public List<ProductFAQ> findAllProductFAQ();
}
