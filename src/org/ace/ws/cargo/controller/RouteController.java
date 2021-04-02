package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.route.Route;
import org.ace.insurance.system.common.route.service.interfaces.IRouteService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.RouteFactory;
import org.ace.ws.cargo.model.route.RouteDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RouteController extends BaseController {
	private static final Logger logger = Logger.getLogger(RouteController.class);
	@Resource(name = "RouteService")
	private IRouteService routeService;

	@RequestMapping(value = URIConstants.GET_ROUTE_LIST, method = RequestMethod.POST)
	public @ResponseBody String getRoute() {
		String response;
		List<RouteDTO> routeDTOList = new ArrayList<RouteDTO>();
		List<Route> routeList = routeService.findAllRoute();
		routeDTOList = RouteFactory.convertRouteDTOList(routeList);
		response = responseManager.getResponseString(routeDTOList);
		return response;
	}
}
