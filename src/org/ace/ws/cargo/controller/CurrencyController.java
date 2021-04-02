package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.CurrencyFactory;
import org.ace.ws.cargo.model.currency.CurrencyDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CurrencyController extends BaseController {
	private static final Logger logger = Logger.getLogger(CurrencyController.class);
	@Resource(name = "CurrencyService")
	private ICurrencyService currencyService;

	@RequestMapping(value = URIConstants.GET_CURRENCY_LIST, method = RequestMethod.POST)
	public @ResponseBody String getCurrency() {
		String response;
		List<CurrencyDTO> currencyDTOList = new ArrayList<CurrencyDTO>();
		List<Currency> currList = currencyService.findAllCurrency();
		currencyDTOList = CurrencyFactory.convertCurrencyDTOList(currList);
		response = responseManager.getResponseString(currencyDTOList);
		return response;
	}
}
