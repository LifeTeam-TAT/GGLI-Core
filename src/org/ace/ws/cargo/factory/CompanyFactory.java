package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.company.Company;
import org.ace.ws.cargo.model.hirePurchase.CompanyDTO;

public class CompanyFactory {
	public static CompanyDTO convertCompanyDTO(Company company) {
		CompanyDTO companyDTO = new CompanyDTO();
		companyDTO.setId(company.getId());
		companyDTO.setName(company.getName());
		companyDTO.setDescription(company.getDescription());
		companyDTO.setAddress(company.getAddress().getPermanentAddress());
		companyDTO.setMobile(company.getContentInfo().getMobile());
		companyDTO.setEmail(company.getContentInfo().getEmail());
		companyDTO.setFax(company.getContentInfo().getFax());
		companyDTO.setVersion(company.getVersion());
		return companyDTO;

	}

	public static List<CompanyDTO> convertCompanyDTOList(List<Company> companyList) {
		List<CompanyDTO> compDTOList = new ArrayList<CompanyDTO>();
		for (Company company : companyList) {
			CompanyDTO comDTO = convertCompanyDTO(company);
			compDTOList.add(comDTO);
		}
		return compDTOList;
	}

}
