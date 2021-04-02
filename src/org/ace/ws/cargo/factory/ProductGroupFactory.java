package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.product.ProductGroup;
import org.ace.ws.cargo.model.produtGroup.ProductGroupDTO;

public class ProductGroupFactory {
	public static ProductGroupDTO convertProductGroupDTO(ProductGroup productGroup) {
		ProductGroupDTO productGpDTO = new ProductGroupDTO();
		productGpDTO.setId(productGroup.getId());
		productGpDTO.setAccountCode(productGroup.getAccountCode());
		productGpDTO.setDescription(productGroup.getDescription());
		productGpDTO.setGroupType(productGroup.getGroupType().getLabel().toString());
		productGpDTO.setMaxSumInsured(productGroup.getMaxSumInsured());
		productGpDTO.setName(productGroup.getName());
		productGpDTO.setPolicyNoPrefix(productGroup.getPolicyNoPrefix());
		productGpDTO.setProposalNoPrefix(productGroup.getProposalNoPrefix());
		productGpDTO.setUnderSession13(productGroup.getUnderSession13());
		productGpDTO.setUnderSession25(productGroup.getUnderSession25());
		productGpDTO.setVersion(productGroup.getVersion());
		return productGpDTO;
	}

	public static List<ProductGroupDTO> convertProductGroupDTOList(List<ProductGroup> productGroupList) {
		List<ProductGroupDTO> productGpDTOList = new ArrayList<ProductGroupDTO>();

		for (ProductGroup PrductGp : productGroupList) {
			ProductGroupDTO productGpDto = convertProductGroupDTO(PrductGp);
			productGpDTOList.add(productGpDto);
		}
		return productGpDTOList;

	}

}
