package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.model.productPremiumRateKeyfactor.ProductPremiumRateKeyfactorDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductPremiumRateKeyfactorController extends BaseController {
	private static final Logger logger = Logger.getLogger(ProductPremiumRateKeyfactorController.class);
	@Resource(name = "ProductService")
	private IProductService productService;

	@RequestMapping(value = URIConstants.GET_PRODUCTPREMIUMRATEKEYFACTOR_LIST, method = RequestMethod.POST)
	public @ResponseBody String getProductPremiumRateKeyfactorList() {
		String response;
		List<ProductPremiumRateKeyfactorDTO> productPremiumRateKeyfactorDTOList = new ArrayList<ProductPremiumRateKeyfactorDTO>();
		productPremiumRateKeyfactorDTOList = productService.findAllProductPremiumRateKeyfactorList();
		response = responseManager.getResponseString(productPremiumRateKeyfactorDTOList);
		return response;
	}
}
