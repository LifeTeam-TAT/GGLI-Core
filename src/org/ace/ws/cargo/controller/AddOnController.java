package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.addon.service.interfaces.IAddOnService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.AddOnFactory;
import org.ace.ws.cargo.model.addOn.AddOnDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AddOnController extends BaseController {
	private static final Logger logger = Logger.getLogger(AddOnController.class);
	@Resource(name = "AddOnService")
	private IAddOnService addOnService;

	@RequestMapping(value = URIConstants.GET_ADDON_LIST, method = RequestMethod.POST)
	public @ResponseBody String getAddOn() {
		String response;
		List<AddOnDTO> addOnDTOList = new ArrayList<AddOnDTO>();
		List<AddOn> addOnList = addOnService.findAllAddOn();
		addOnDTOList = AddOnFactory.convertAddOnDTOList(addOnList);
		response = responseManager.getResponseString(addOnDTOList);
		return response;
	}

	@RequestMapping(value = URIConstants.GET_ADDONLIST_BY_PRODUCTTYPE, method = RequestMethod.POST)
	public @ResponseBody String getAddOnListByProductType(@RequestBody Product product) {
		String response;
		List<AddOn> addOnList = product.getAddOnList();
		response = responseManager.getResponseString(addOnList);
		return response;
	}
}
