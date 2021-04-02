package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.ProductGroupFactory;
import org.ace.ws.cargo.model.produtGroup.ProductGroupDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductGroupController extends BaseController {
	private static final Logger logger = Logger.getLogger(ProductGroupController.class);
	@Resource(name = "ProductService")
	private IProductService productService;

	@RequestMapping(value = URIConstants.GET_PRODUCTGROUP_LIST, method = RequestMethod.POST)
	public @ResponseBody String getProdcutGroup() {
		String response;
		List<ProductGroupDTO> productGroupDTOList = new ArrayList<ProductGroupDTO>();
		List<ProductGroup> productGroupList = productService.findAllProductGroup();
		productGroupDTOList = ProductGroupFactory.convertProductGroupDTOList(productGroupList);
		response = responseManager.getResponseString(productGroupDTOList);
		return response;
	}

	@RequestMapping(value = URIConstants.GET_PRODUCTGROUP_BY_PRODUCTGROUPTYPE, method = RequestMethod.POST)
	public @ResponseBody String getProductGroupList(@RequestBody ProductGroup productGp) {
		String response;

		List<ProductGroup> productGroupList = productService.findProductGroupByProductGroupType(productGp.getGroupType());
		response = responseManager.getResponseString(productGroupList);
		return response;

	}
}
