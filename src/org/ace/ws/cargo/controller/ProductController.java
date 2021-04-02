package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.ProductFactory;
import org.ace.ws.cargo.model.product.ProductDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductController extends BaseController {
	private static final Logger logger = Logger.getLogger(ProductController.class);
	@Resource(name = "ProductService")
	private IProductService productService;

	@RequestMapping(value = URIConstants.GET_PRODUCT_LIST, method = RequestMethod.POST)
	public @ResponseBody String getProductList() {
		String response;
		List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
		List<Product> productList = productService.findAllProduct();
		productDTOList = ProductFactory.convertProdcutDTOList(productList);
		response = responseManager.getResponseString(productDTOList);
		return response;
	}

	@RequestMapping(value = URIConstants.GET_PRODUCTLIST_LIST_BY_INSURANCETYPE, method = RequestMethod.POST)
	public @ResponseBody String getProductListByProductType(@RequestBody Product product) {
		String response;

		List<Product> productList = productService.findProductByInsuranceType(product.getInsuranceType());
		response = responseManager.getResponseString(productList);
		return response;

	}

	@RequestMapping(value = URIConstants.GET_PRODUCTLIST_BY_CURRENCYTYPE, method = RequestMethod.POST)
	public @ResponseBody String getProductListByCurrencyType(@RequestBody Product product) {
		String response;

		List<Product> productList = productService.findProductByCurrencyType(product.getInsuranceType(), product.getCurrency());
		response = responseManager.getResponseString(productList);
		return response;

	}
}
