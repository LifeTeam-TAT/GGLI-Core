package org.ace.insurance.claim.disabilityPart.service.interfaces;

import java.util.List;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.claim.disabilityPart.LifeClaimKeyFactorInfoMultiValue;
import org.ace.insurance.claim.disabilityPart.ProductDisability;
import org.ace.insurance.claim.disabilityPart.ProductDisabilityRate;


public interface IProductDisabilityService {

	public void addNewProductDisability(ProductDisability productDisability);

	public ProductDisability updateProductDisability(ProductDisability productDisability);

	public void deleteProductDisability(ProductDisability productDisability);

	public void addNewProductDisabilityRate(ProductDisabilityRate productDisabilityRate);

	public void deleteProductDisabilityRate(ProductDisabilityRate productDisabilityRate);

	public List<ProductDisability> findAllDisabilityPart();

	public List<DisabilityPart> findAllDisabilityPartByProduct(String productId);

	public List<ProductDisabilityRate> findAllDisabilityRateByProduct(String productId);

	public double findAllDisabilityRateById(List<LifeClaimKeyFactorInfoMultiValue> claimMultiValueList);

	ProductDisability findProductDisbilityById(String id);
}
