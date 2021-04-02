package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.product.Product;
import org.ace.ws.cargo.model.product.ProductDTO;

public class ProductFactory {
	public static ProductDTO convertProductDTO(Product product) {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setAutoCalculate(product.getAutoCalculate());
		productDTO.setFixedValue(product.getFixedValue());
		productDTO.setStandardExcess(product.getStandardExcess());
		productDTO.setFirstCommission(product.getFirstCommission());
		productDTO.setRenewalCommission(product.getRenewalCommission());
		productDTO.setMaxSumInsured(product.getMaxSumInsured());
		productDTO.setMinSumInsured(product.getMinSumInsured());
		productDTO.setMaxTerm(product.getMaxTerm());
		productDTO.setMinTerm(product.getMinTerm());
		productDTO.setMultiCurPrefix(product.getMultiCurPrefix());
		productDTO.setMaxAge(product.getMaxAge());
		productDTO.setMinAge(product.getMinAge());
		productDTO.setMaxHospDays(product.getMaxHospDays());
		productDTO.setMaxUnit(product.getMaxUnit());
		productDTO.setBaseSumInsured(product.getBaseSumInsured());
		productDTO.setPremiumRateType(product.getPremiumRateType() == null ? "" : product.getPremiumRateType().getLabel().toString());
		productDTO.setInsuranceType(product.getInsuranceType().getLabel() == null ? "" : product.getInsuranceType().getLabel().toString());
		productDTO.setProductGroupId(product.getProductGroup().getId());
		productDTO.setCurrencyId(product.getCurrency().getId());
		productDTO.setVersion(product.getVersion());
		return productDTO;
	}

	public static List<ProductDTO> convertProdcutDTOList(List<Product> productList) {
		List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
		for (Product product : productList) {
			ProductDTO proDTO = convertProductDTO(product);
			productDTOList.add(proDTO);
		}
		return productDTOList;

	}
}
