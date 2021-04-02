package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.service.interfaces.IPaymentTypeService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.PaymentTypeFactory;
import org.ace.ws.cargo.model.paymentType.PaymentTypeDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PaymentTypeController extends BaseController {
	@Resource(name = "PaymentTypeService")
	private IPaymentTypeService paymentTypeService;

	@RequestMapping(value = URIConstants.GET_PAYMENTTYPE_LIST, method = RequestMethod.POST)
	public @ResponseBody String getPaymentType() {
		String response;
		List<PaymentTypeDTO> paymentTypeDTOList = new ArrayList<PaymentTypeDTO>();
		List<PaymentType> paymentTypeList = paymentTypeService.findAllPaymentType();
		paymentTypeDTOList = PaymentTypeFactory.convertPaymentTypeDTOList(paymentTypeList);
		response = responseManager.getResponseString(paymentTypeDTOList);
		return response;
	}

}
