package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.city.City;
import org.ace.insurance.system.common.city.service.interfaces.ICityService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.CityFactory;
import org.ace.ws.cargo.model.city.CityDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CityController extends BaseController {
	private static final Logger logger = Logger.getLogger(CityController.class);
	@Resource(name = "CityService")
	private ICityService cityService;

	@RequestMapping(value = URIConstants.GET_CITY_LIST, method = RequestMethod.POST)
	public @ResponseBody String getCityList() {
		String response;
		List<CityDTO> cityDTOList = new ArrayList<CityDTO>();
		List<City> cityList = cityService.findAllCity();
		cityDTOList = CityFactory.convertCityDTOList(cityList);
		response = responseManager.getResponseString(cityDTOList);
		return response;
	}
}
