package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.company.Company;
import org.ace.insurance.system.common.company.service.interfaces.ICompanyService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.CompanyFactory;
import org.ace.ws.cargo.model.hirePurchase.CompanyDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CompanyController extends BaseController {
	private static final Logger logger = Logger.getLogger(CompanyController.class);
	@Resource(name = "CompanyService")
	private ICompanyService companyService;

	@RequestMapping(value = URIConstants.GET_COMPANY_LIST, method = RequestMethod.POST)
	public @ResponseBody String getCompanyList() {
		String response;
		List<CompanyDTO> companyDTOList = new ArrayList<CompanyDTO>();
		List<Company> companyList = companyService.findAllCompany();
		companyDTOList = CompanyFactory.convertCompanyDTOList(companyList);
		response = responseManager.getResponseString(companyDTOList);
		return response;
	}
}
