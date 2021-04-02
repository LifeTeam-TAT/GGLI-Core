package org.ace.insurance.system.productfaq.service.interfaces;

import java.util.List;

import org.ace.insurance.system.productfaq.ProductFAQ;

public interface IProductFAQService {

	public void insertProductFAQ(ProductFAQ productFAQ);

	public ProductFAQ updateProductFAQ(ProductFAQ productFAQ);

	public ProductFAQ findById(String id);

	public List<ProductFAQ> findAllProductFAQ();
}
