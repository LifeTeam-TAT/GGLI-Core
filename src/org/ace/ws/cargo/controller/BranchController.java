package org.ace.ws.cargo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.ws.cargo.client.URIConstants;
import org.ace.ws.cargo.factory.BranchFactory;
import org.ace.ws.cargo.model.branch.BranchDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BranchController extends BaseController {
	@Resource(name = "BranchService")
	private IBranchService branchService;

	@RequestMapping(value = URIConstants.GET_BRANCH_LIST, method = RequestMethod.POST)
	public @ResponseBody String getCargoName() {
		String response;
		List<BranchDTO> branchDTOList = new ArrayList<BranchDTO>();
		List<Branch> branchList = branchService.findAllBranch();
		branchDTOList = BranchFactory.convertBranchDTOList(branchList);
		response = responseManager.getResponseString(branchDTOList);
		return response;
	}
}
