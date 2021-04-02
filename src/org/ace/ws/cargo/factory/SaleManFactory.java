package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.ws.cargo.model.saleMan.SaleManDTO;

public class SaleManFactory {
	public static SaleManDTO convertSaleManDTO(SaleMan saleMan) {
		SaleManDTO saleManDTO = new SaleManDTO();
		saleManDTO.setId(saleMan.getId());
		saleManDTO.setInitialId(saleMan.getInitialId());
		saleManDTO.setIdNo(saleMan.getIdNo());
		saleManDTO.setIdType(saleMan.getIdType().getLabel().toString());
		saleManDTO.setCodeNo(saleMan.getCodeNo());
		saleManDTO.setDateOfBirth(saleMan.getDateOfBirth() == null ? "" : saleMan.getDateOfBirth().toString());
		saleManDTO.setBranch(saleMan.getBranch().getName());
		saleManDTO.setAddress(saleMan.getAddress().getFullAddress());
		saleManDTO.setEmail(saleMan.getContentInfo().getMobile());
		saleManDTO.setMobile(saleMan.getContentInfo().getMobile());
		saleManDTO.setFax(saleMan.getContentInfo().getFax());
		saleManDTO.setLicenseNo(saleMan.getLicenseNo());
		saleManDTO.setName(saleMan.getName().getFullName());
		saleManDTO.setVersion(saleMan.getVersion());
		return saleManDTO;

	}

	public static List<SaleManDTO> convertSaleManDTOList(List<SaleMan> saleManList) {
		List<SaleManDTO> saleManDTOList = new ArrayList<SaleManDTO>();

		for (SaleMan saleMan : saleManList) {
			SaleManDTO saleManDTO = convertSaleManDTO(saleMan);
			saleManDTOList.add(saleManDTO);
		}
		return saleManDTOList;

	}
}
