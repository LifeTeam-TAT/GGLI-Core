package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.rpc.ServiceException;

import org.ace.insurance.user.MobileUser;
import org.ace.insurance.user.service.interfaces.IMobileUserService;
import org.ace.java.component.SystemException;
import org.ace.java.component.common.StatusType;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.SystemFactory;
import org.ace.ws.cargo.model.Status;
import org.ace.ws.cargo.model.user.MobileUserDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MobileUserController extends BaseController {

	@Resource(name = "MobileUserService")
	private IMobileUserService mobileUserService;

	private static final Logger logger = Logger.getLogger(MobileUserController.class);

	@RequestMapping(value = URIConstants.LOGIN, method = RequestMethod.POST)
	public @ResponseBody String Login(@RequestBody MobileUserDTO mobileUserDTO) throws ServiceException {
		logger.info("Start Login.");
		String response;
		try {
			MobileUser mobileUser = mobileUserService.authenticate(mobileUserDTO.getUserCode(), mobileUserDTO.getPassword());
			if (mobileUser == null) {
				response = responseManager.getResponseString(new Status(StatusType.FAIL));
			} else {
				if (!mobileUser.isAccessSync()) {
					response = responseManager.getResponseString(new Status(StatusType.ACCESSSYNC));
				}
				if (mobileUser.isAccountDisable()) {
					response = responseManager.getResponseString(new Status(StatusType.DISABLED));
				} else {
					response = responseManager.getResponseString(SystemFactory.convertMobileUserDTO(mobileUser));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(StatusType.SQL_Exception);
		}
		logger.info("End Login.");
		return response;
	}

	@RequestMapping(value = URIConstants.CREATE_MOBILE_USER, method = RequestMethod.POST)
	@ResponseBody
	public String addMobileUser(@RequestBody MobileUserDTO mobileUserDTO) throws ServiceException {
		logger.info("Start Create Mobile User.");
		String response = null;
		try {
			mobileUserService.addNewMobileUser(SystemFactory.convertMobileUser(mobileUserDTO));

			response = responseManager.getResponseString(new Status(StatusType.SUCCESS));
		} catch (SystemException e) {
			e.printStackTrace();
			response = responseManager.getResponseString(new Status(StatusType.FAIL));
			throw new ServiceException(StatusType.SQL_Exception);
		}
		logger.info("End Create Mobile User.");
		return response;
	}

	@RequestMapping(value = URIConstants.GET_MOBILEUSER_LIST, method = RequestMethod.POST)
	public @ResponseBody String findAllMobileUser() {
		String response;
		List<MobileUserDTO> mobileUserDTOList = new ArrayList<MobileUserDTO>();
		List<MobileUser> mobileUserlist = mobileUserService.findAllMobileUser();
		mobileUserDTOList = SystemFactory.convertMobileUserDTOList(mobileUserlist);
		response = responseManager.getResponseString(mobileUserDTOList);
		return response;
	}

	@RequestMapping(value = URIConstants.UPDATE_MOBILE_USER, method = RequestMethod.POST)
	public @ResponseBody String updateMobileUser(@RequestBody MobileUserDTO mobileUserDTO) throws ServiceException {
		logger.info("Start Update Mobile User.");
		String response = null;
		try {
			MobileUser mobileUser = mobileUserService.findMobileUserById(mobileUserDTO.getId());
			if (mobileUser == null) {
				response = responseManager.getResponseString(new Status(StatusType.FAIL));
			} else {
				mobileUserService.updateMobileUser(SystemFactory.convertMobileUser(mobileUserDTO, mobileUser));
				response = responseManager.getResponseString(new Status(StatusType.SUCCESS));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = responseManager.getResponseString(new Status(StatusType.FAIL));
			throw new ServiceException(StatusType.SQL_Exception);
		}
		logger.info("End Update Mobile User.");
		return response;
	}

}
