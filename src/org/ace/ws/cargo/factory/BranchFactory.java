package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.ws.cargo.model.branch.BranchDTO;

public class BranchFactory {

	public static List<BranchDTO> convertBranchDTOList(List<Branch> branchList) {
		List<BranchDTO> branchDTOList = new ArrayList<BranchDTO>();
		for (Branch branch : branchList) {
			BranchDTO branchDTO = convertBranchDTO(branch);
			branchDTOList.add(branchDTO);
		}
		return branchDTOList;
	}

	private static BranchDTO convertBranchDTO(Branch branch) {
		BranchDTO branchDTO = new BranchDTO();
		branchDTO.setId(branch.getId());
		branchDTO.setName(branch.getName());
		branchDTO.setBranchCode(branch.getBranchCode());
		branchDTO.setAddress(branch.getAddress());
		branchDTO.setCoInsuAccess(branch.isCoInsuAccess());
		branchDTO.setDescription(branch.getDescription());
		branchDTO.setTownship(branch.getTownship().getFullTownShip());
		branchDTO.setVersion(branch.getVersion());
		return branchDTO;
	}
}
