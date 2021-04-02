package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.keyfactor.service.interfaces.IKeyFactorService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.KeyFactorFactory;
import org.ace.ws.cargo.model.keyFactor.KeyFactorDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class KeyFactorController extends BaseController {
	private static final Logger logger = Logger.getLogger(KeyFactorController.class);
	@Resource(name = "KeyFactorService")
	private IKeyFactorService keyFactorService;

	@RequestMapping(value = URIConstants.GET_KEYFACTOR_LIST, method = RequestMethod.POST)
	public @ResponseBody String getKeyFactorList() {
		String response;
		List<KeyFactorDTO> keyFactorDTOList = new ArrayList<KeyFactorDTO>();
		List<KeyFactor> keyFactorList = keyFactorService.findAllKeyFactor();
		keyFactorDTOList = KeyFactorFactory.convertKeyFactorDTOList(keyFactorList);
		response = responseManager.getResponseString(keyFactorDTOList);
		return response;
	}
}
