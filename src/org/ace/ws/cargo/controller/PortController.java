package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.port.Port;
import org.ace.insurance.system.common.port.service.interfaces.IPortService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.PortFactory;
import org.ace.ws.cargo.model.port.PortDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PortController extends BaseController {
	private static final Logger logger = Logger.getLogger(RouteController.class);
	@Resource(name = "PortService")
	private IPortService portService;

	@RequestMapping(value = URIConstants.GET_PORT_LIST, method = RequestMethod.POST)
	public @ResponseBody String getPort() {
		String response;
		List<PortDTO> portDTOList = new ArrayList<PortDTO>();
		List<Port> portList = portService.findAllPort();
		portDTOList = PortFactory.convertPortDTOList(portList);
		response = responseManager.getResponseString(portDTOList);
		return response;
	}
}
