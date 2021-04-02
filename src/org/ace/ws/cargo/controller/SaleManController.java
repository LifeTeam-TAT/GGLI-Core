package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.saleman.service.interfaces.ISaleManService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.SaleManFactory;
import org.ace.ws.cargo.model.saleMan.SaleManDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SaleManController extends BaseController {
	private static final Logger logger = Logger.getLogger(SaleManController.class);
	@Resource(name = "SaleManService")
	private ISaleManService saleManService;

	@RequestMapping(value = URIConstants.GET_SALEMAN_LIST, method = RequestMethod.POST)
	public @ResponseBody String getSaleMan() {
		String response;
		List<SaleManDTO> saleManDTOList = new ArrayList<SaleManDTO>();
		List<SaleMan> saleManList = saleManService.findAllSaleMan();
		saleManDTOList = SaleManFactory.convertSaleManDTOList(saleManList);
		response = responseManager.getResponseString(saleManDTOList);
		return response;
	}
}
